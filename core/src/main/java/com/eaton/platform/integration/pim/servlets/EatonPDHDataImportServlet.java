/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.eaton.platform.integration.pim.servlets;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import com.eaton.platform.core.constants.ServletConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import com.google.gson.JsonObject;
import com.google.gson.Gson;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaton.platform.core.bean.ProductFamilyPDHDetails;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.util.CommonUtil;

/**
 * Servlet that writes some sample content into the response. It is mounted for
 * all resources of a specific Sling resource type. The
 * {@link SlingSafeMethodsServlet} shall be used for HTTP methods that are
 * idempotent. For write operations use the {@link SlingAllMethodsServlet}.
 */
@Component(service = Servlet.class,
		immediate = true,
		property = {
				ServletConstants.SLING_SERVLET_METHODS_GET,
				ServletConstants.SLING_SERVLET_PATHS + "/eaton/productfamilypagedata",
				ServletConstants.SLING_SERVLET_EXTENSION_JSON
		})
public class EatonPDHDataImportServlet extends SlingSafeMethodsServlet {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(EatonPDHDataImportServlet.class);

	/** The admin service. */
	@Reference
	private transient AdminService adminService;

	/** The Constant REQUEST_PARAM_PRODID. */
	private static final String REQUEST_PARAM_PRODID = "prodId";

	/** The Constant REQUEST_PARAM_OLDID. */
	private static final String REQUEST_PARAM_OLDID = "oldID";

	/** The Constant REQUEST_PARAM_PIM_NODE_PATH. */
	private static final String REQUEST_PARAM_PIM_NODE_PATH = "pimNode";

	private static final String ETC_PRODUCTS_PATH = "/var/commerce/products/eaton/";
	
	private static final String REQUEST_PARAM_ACTION = "action";
	
	private static final DateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	

	private String nodeRenameSuccess;
	
	private String language;
	
	String temp;

	@Override
	protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp)
			throws ServletException, IOException {
		LOG.info("******** EatonPDHDataImportServlet execution started ***********");
		try (ResourceResolver adminResourceResolver = adminService.getWriteService()){
			String inventoryID = StringUtils.EMPTY;
			ProductFamilyPDHDetails productFamilyPDHDetails = new ProductFamilyPDHDetails();
			JsonObject jsonObject = new JsonObject();
			Resource extnIdResource = null;
			Session adminSession = adminResourceResolver.adaptTo(Session.class);
			String action = req.getParameter(REQUEST_PARAM_ACTION);
			Resource currentPIMResource = null;
			String currentPagePath = req.getParameter("url");
			String currentPDHPath = req.getParameter("existingPDHPath");
			String currentExtensionId = req.getParameter(REQUEST_PARAM_OLDID);
			String updatedExtnId = null;

			if(null != currentPDHPath && StringUtils.isNotEmpty(currentPDHPath)){
				 currentExtensionId = StringUtils.substringAfterLast(currentPDHPath, "/");
			}

			String pdhRecordPath = req.getParameter(REQUEST_PARAM_PRODID);
			if(null != pdhRecordPath && !pdhRecordPath.isEmpty()){
				extnIdResource = adminResourceResolver.getResource(pdhRecordPath);
				updatedExtnId = StringUtils.substringAfterLast(pdhRecordPath, "/");
				if(null != extnIdResource){
					productFamilyPDHDetails = CommonUtil.readPDHNodeData(adminResourceResolver, extnIdResource);
					inventoryID  = extnIdResource.getParent().getName();
				}
			}
			
			if((StringUtils.equalsIgnoreCase(action, "onLoad")) 
					&& null != extnIdResource){
				populateJSONObj(productFamilyPDHDetails, jsonObject);

			}else if((StringUtils.equalsIgnoreCase(action, "onLoad")) && null == extnIdResource){
				resp.setStatus(500);
					}
			
			if((StringUtils.equalsIgnoreCase(action, "pathBrowserChange")) 
					&& null != extnIdResource){
				populateJSONObj(productFamilyPDHDetails, jsonObject);
			}
			
			if (StringUtils.equalsIgnoreCase(action, "Create")) {
				if (null != extnIdResource) {
					if (checkPIMNodeExists(currentPagePath, extnIdResource.getName(), adminResourceResolver)) {
						resp.setStatus(500);
					} else {
						populateJSONObj(productFamilyPDHDetails, jsonObject);
					}
				}

				else {
					Date date = new Date();
					String newExtensionID = "aem".concat(sdf.format(date));
					jsonObject.add("extnId", new Gson().toJsonTree(newExtensionID));
				}
			}

			jsonObject.add("inventoryID", new Gson().toJsonTree(inventoryID));
			if(StringUtils.equalsIgnoreCase(action, "Save & Close") && null != extnIdResource){
				if(!StringUtils.equalsIgnoreCase(currentExtensionId, updatedExtnId)){
					if(checkPIMNodeExists(currentPagePath, extnIdResource.getName(), adminResourceResolver)){
						resp.setStatus(500);
					} else {
						populateJSONObj(productFamilyPDHDetails, jsonObject);
						if(null != currentPDHPath) {
                            Resource currentPDHResource = adminResourceResolver.getResource(currentPDHPath);
                            String productBasePath = CommonUtil.getProductBasePathByProductPath(
                                CommonUtil.getProductsFolderPath(currentPagePath));
                            if(null != currentPDHResource){
                                currentPIMResource = adminResourceResolver.getResource(
                                    productBasePath.concat(language).concat("/_".concat(currentPDHResource.getName())));
                            }

                            if(null == currentPIMResource){
                                currentPIMResource = adminResourceResolver.getResource(
                                    productBasePath.concat(language).concat("/".concat(currentExtensionId)));
                            }
                            if (adminSession != null && null != currentPIMResource) {
                                Node currentPIMNode = currentPIMResource.adaptTo(Node.class);
                                if(null != currentPIMNode) {
                                    adminSession.move(currentPIMNode.getPath(), currentPIMNode.getParent().getPath().concat("/_").concat(extnIdResource.getName()));
                                    adminSession.save();
                                    jsonObject.add("action", new Gson().toJsonTree(currentPIMNode.getParent().getPath().concat("/_").concat(extnIdResource.getName())));
                                    jsonObject.add("nodeRenameSuccess", new Gson().toJsonTree("true"));
                                }
                            }
                        }
					}
				}
				
			}
			resp.setContentType("application/json");
			resp.getWriter().print(jsonObject.toString());
		} catch(Exception e){
			resp.setStatus(500);
		}
		

		LOG.info("******** EatonPDHDataImportServlet execution completed ***********");
	}
	
	/**
	 * Populate JSON obj.
	 *
	 * @param productFamilyPDHDetails the product family PDH details
	 * @param jsonObject the json object
	 */
	private void populateJSONObj(ProductFamilyPDHDetails productFamilyPDHDetails, JsonObject jsonObject){
		// form json response
		if(productFamilyPDHDetails != null) {
			try {
				jsonObject.add("extnId", new Gson().toJsonTree(productFamilyPDHDetails.getExtensionId()));
				jsonObject.add("pdhTitle", new Gson().toJsonTree(productFamilyPDHDetails.getProductName()));
				jsonObject.add("pdhMarDesc", new Gson().toJsonTree(productFamilyPDHDetails.getMarketingDescription()));
				jsonObject.add("primaryImgName", new Gson().toJsonTree(productFamilyPDHDetails.getPrimaryImgName()));
				jsonObject.add("supportInfo", new Gson().toJsonTree(productFamilyPDHDetails.getSupportInfo()));
				jsonObject.add("coreFeatures", new Gson().toJsonTree(productFamilyPDHDetails.getCoreFeatures()));
				jsonObject.add("spinImage",new Gson().toJsonTree(productFamilyPDHDetails.getPdhSpinImage()));
				jsonObject.add("nodeRenameSuccess", new Gson().toJsonTree(nodeRenameSuccess));
				
			} catch (Exception e) {
				LOG.error("Error in EatonPDHDataImportServlet, populateJSONObj() method "+ e.getMessage());
			}
			
		}
	}
	
	/**
	 * Check PIM node exists.
	 *
	 * @param pdhRecordPath the pdh record path
	 * @param extnId the extn id
	 * @param adminResourceResolver
	 * @return true, if successful
	 */
	private boolean checkPIMNodeExists(String currentPagePath, String extnId, ResourceResolver adminResourceResolver){
		
		boolean pimExists = false;
		String productsFolderPath = CommonUtil.getProductsFolderPath(currentPagePath);
		String[] productsFolderPathArr = StringUtils.split(productsFolderPath, "/");

		if (null != productsFolderPathArr && productsFolderPathArr.length > 4) {
			language = productsFolderPathArr[4];
		}
        String productBasePath = CommonUtil.getProductBasePathByProductPath(productsFolderPath);
        Resource productRes = adminResourceResolver
				.getResource(productBasePath.concat(language).concat("/_".concat(extnId)));
		
		if (null != productRes) {
			LOG.info("PIM Node " + extnId + " already exists", extnId);
			pimExists = true;
		}
		
		return pimExists;
	}
}
