package com.eaton.platform.core.servlets.assets;

import com.adobe.fd.assembler.client.OperationException;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.model.WorkflowModel;
import com.eaton.platform.core.constants.AssetDownloadConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.models.asset.AssetDownloadPackageDataModel;
import com.eaton.platform.core.models.submittalbuilder.SubmittalDownload;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.AssetDownloadPackageDataService;
import com.eaton.platform.core.services.AssetDownloadService;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceNotFoundException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import com.google.gson.JsonObject;
import com.google.gson.Gson;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(service = Servlet.class,
        immediate = true,
        property = {
                ServletConstants.SLING_SERVLET_METHODS_GET,
                ServletConstants.SLING_SERVLET_METHODS_POST,
                ServletConstants.SLING_SERVLET_RESOURCE_TYPES + "eaton/components/content/submittalbuilder",
                ServletConstants.SLING_SERVLET_SELECTORS +AssetDownloadConstants.SUBMITTAL_BUILDER_DOWNLOAD,
                ServletConstants.SLING_SERVLET_EXTENSION_JSON
        })
public class AssetDownloadPackageServlet extends SlingAllMethodsServlet {

    /**
	 * 	Serial Version ID
	 */
	private static final long serialVersionUID = 7050638381991415292L;

	private static final Logger LOG = LoggerFactory.getLogger(AssetDownloadPackageServlet.class);

    @Reference
    private AssetDownloadPackageDataService assetDownloadPackageDataService;

    @Reference
    private AssetDownloadService assetDownloadService;

    @Reference
    private AdminService adminService;

    /**
     * Either downloads the asset from ASRP (if the 'etnPackId' parameter is provided) or creates and downloads the package directly otherwise.
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws IOException, ServletException {
        final String downloadPackageIdentifier = request.getParameter(AssetDownloadConstants.PARAMETER_KEY_DOWNLOAD_PACKAGE_ID);
        if(request.getRequestParameterMap().containsKey(AssetDownloadConstants.PARAMETER_KEY_DOWNLOAD_PACKAGE_ID)) {
            if(StringUtils.isNotEmpty(downloadPackageIdentifier)) {
                final String resourcePath = request.getResource().getPath();
                final Resource submittalDownloadResource =
                        request.getResource().getChild(AssetDownloadConstants.SUBMITTAL_DOWNLOAD);
                String redirectionPath = StringUtils.EMPTY;
                if (null != submittalDownloadResource) {
                    final SubmittalDownload submittalDownload = submittalDownloadResource.adaptTo(SubmittalDownload.class);
                    redirectionPath = submittalDownload.getEmailErrorPage();
                }
                try {
                    final AssetDownloadPackageDataModel assetDownloadPackageDataModel = assetDownloadPackageDataService.getAssetDownloadPackageData(resourcePath, downloadPackageIdentifier);
                    initResponse(response, assetDownloadPackageDataModel.getDownloadFileName());
                    writeZipBytesToResponse(response, assetDownloadPackageDataModel.getDownloadAssetPaths(),
                            assetDownloadPackageDataModel.getMergeAssetsFileName(), assetDownloadPackageDataModel.isMergeAssetsToSinglePDF(), Boolean.FALSE);
                } catch (ResourceNotFoundException rnfe) {
                    LOG.error(AssetDownloadConstants.RESOURCE_NOT_FOUND, rnfe);
                    response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
                    final String redirectLocation = CommonUtil.dotHtmlLink(redirectionPath, request.getResourceResolver());
                    response.setHeader(AssetDownloadConstants.LOCATION, redirectLocation);
                    response.setHeader(AssetDownloadConstants.CONNECTION, AssetDownloadConstants.CLOSE);
                }
            } else {
                LOG.error(AssetDownloadConstants.ERROR_MESSAGE_UNIQUE_ID_MISSING);
                createErrorResponse(AssetDownloadConstants.ERROR_MESSAGE_UNIQUE_ID_MISSING,
                        AssetDownloadConstants.UNIQUE_ID, response);
            }

        }
    }

    /**
     * Saves the asset in ASRP and starts the email workflow.
     * @param request
     * @param response
     * @throws IOException
     * @throws IllegalArgumentException
     */
    @Override
    protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
            throws IOException, ServletException, IllegalArgumentException {
        try (ResourceResolver adminResourceResolver = adminService.getWriteService()){
            final String[] selectors = request.getRequestPathInfo().getSelectors();
            if (selectors.length > 0) {
                final String selectorValue = selectors[0];
                if (selectorValue.equals(AssetDownloadConstants.SUBMITTAL_BUILDER_DOWNLOAD)) {
                    final String action = selectors[1];
                    switch (action) {
                        case AssetDownloadConstants.DOWNLOAD:
                        	assetDownloadAction(request, response);
                            break;
                        case AssetDownloadConstants.EMAIL:
                        	assetDownloadEmailAction(request, response, adminResourceResolver);
                            break;
                        default:
                            LOG.error(AssetDownloadConstants.ERROR_INVALID_SELECTOR_PASSED);
                            createErrorResponse(AssetDownloadConstants.ERROR_INVALID_SELECTOR_PASSED,
                                    AssetDownloadConstants.SELECTOR_ERROR, response);
                            break;
                    }
                } else {
                    LOG.error(AssetDownloadConstants.ERROR_INVALID_SELECTOR_PASSED);
                    createErrorResponse(AssetDownloadConstants.ERROR_INVALID_SELECTOR_PASSED,
                            AssetDownloadConstants.SELECTOR_ERROR, response);
                }
            } else {
                LOG.error(AssetDownloadConstants.ERROR_INVALID_SELECTOR_PASSED);
                createErrorResponse(AssetDownloadConstants.ERROR_INVALID_SELECTOR_PASSED,
                        AssetDownloadConstants.SELECTOR_ERROR, response);
            }
        }
    }

	private void assetDownloadEmailAction(final SlingHttpServletRequest request,
			final SlingHttpServletResponse response, ResourceResolver adminResourceResolver) {
		final WorkflowSession wfSession = adminResourceResolver.adaptTo(WorkflowSession.class);
		try {
		    final AssetDownloadParameters assetDownloadParams =
		            AssetDownloadParameters.fromRequest(request, AssetDownloadConstants.EMAIL);
		    final String resourcePath = request.getResource().getPath();
		    final Resource submittaldownload =
		            request.getResource().getChild(AssetDownloadConstants.SUBMITTAL_DOWNLOAD);
		    if (null != submittaldownload) {
		        final SubmittalDownload submittalDownload = submittaldownload.adaptTo(SubmittalDownload.class);
		        this.startWorkflow(AssetDownloadConstants.EMAIL_DOWNLOAD_ASSET_PACKAGE_WF_MODEL_ID, wfSession,
		                resourcePath, getWorkflowMetadata(assetDownloadParams, resourcePath,
		                        submittalDownload));
		    } else {
		        LOG.error(AssetDownloadConstants.ERROR_SENDING_EMAIL);
		        createErrorResponse(AssetDownloadConstants.ERROR_SENDING_EMAIL,
		                AssetDownloadConstants.EMAIL_ERROR, response);
		    }
		} catch (WorkflowException we) {
		    LOG.warn(AssetDownloadConstants.ERROR_STARTING_WORKFLOW, we.getMessage());
		    createErrorResponse(we.getLocalizedMessage(), AssetDownloadConstants.WORKFLOW_ERROR,
		            response);
		} catch (Exception e) {
		    LOG.error(AssetDownloadConstants.ERROR_INVALID_REQUEST_PARAMETERS, e);
		    createErrorResponse(AssetDownloadConstants.ERROR_INVALID_REQUEST_PARAMETERS,
		            AssetDownloadConstants.INVALID_REQUEST_ERROR, response);
		}
	}

	private void assetDownloadAction(final SlingHttpServletRequest request, final SlingHttpServletResponse response) {
		try {
		    final AssetDownloadParameters assetDownloadParameters =
		            AssetDownloadParameters.fromRequest(request, AssetDownloadConstants.DOWNLOAD);
		    initResponse(response, assetDownloadParameters.getDownloadFileName());
			List<String> downloadAssetPaths =  assetDownloadParameters.getDownloadAssetPaths();
		    writeZipBytesToResponse(response, downloadAssetPaths, assetDownloadParameters.getMergeAssetsFileName(), assetDownloadParameters.isMergeAssetsToSinglePDF(), Boolean.TRUE);
		} catch (Exception e) {
		    LOG.error(AssetDownloadConstants.ERROR_INVALID_REQUEST_PARAMETERS, e);
		    createErrorResponse(AssetDownloadConstants.ERROR_INVALID_REQUEST_PARAMETERS,
		            AssetDownloadConstants.INVALID_REQUEST_ERROR, response);
		}
	}

    private static void initResponse(final SlingHttpServletResponse response, final String fileName) {
        response.setContentType(AssetDownloadConstants.ZIP_CONTENT_TYPE);
        response.setHeader(AssetDownloadConstants.CONTENT_DISPOSITION_KEY,
                String.format(AssetDownloadConstants.CONTENT_DISPOSITION_VALUE, fileName));
    }

    private void writeZipBytesToResponse(final SlingHttpServletResponse response, final List<String> downloadAssetPaths,
                                         final String mergeAssetsFileName, final boolean mergeAssetsToSinglePDF,
                                         final boolean encodeZipBytes) throws IOException, ServletException {
        try {
            byte[] zipBytes = assetDownloadService.createZipArchiveFromAssetPaths(downloadAssetPaths, mergeAssetsFileName, mergeAssetsToSinglePDF);

            if (encodeZipBytes) {
                zipBytes = Base64.getEncoder().encode(zipBytes);
            }
            final ServletOutputStream servletOutputStream = response.getOutputStream();
            servletOutputStream.write(zipBytes);
            servletOutputStream.flush();
            servletOutputStream.close();
        } catch (ParserConfigurationException | TransformerException | OperationException ptoe) {
            throw new ServletException(ptoe);
        }
    }

    private static Map<String, Object> getWorkflowMetadata(final AssetDownloadParameters assetDownloadParameters, final String resourcePath, final SubmittalDownload submittalDownload) {
        final Map<String, Object> wfMetaData = new HashMap();
        wfMetaData.put(AssetDownloadConstants.PARAMETER_KEY_DOWNLOAD_FILE_NAME, assetDownloadParameters.getDownloadFileName());
        wfMetaData.put(AssetDownloadConstants.PARAMETER_KEY_EMAIL_TO_RECIPIENTS, assetDownloadParameters.getEmailToRecipients().toArray());
        wfMetaData.put(AssetDownloadConstants.PARAMETER_KEY_DOWNLOAD_ASSET_PATHS, assetDownloadParameters.getDownloadAssetPaths().toArray());
        wfMetaData.put(AssetDownloadConstants.PARAMETER_KEY_MERGE_ASSETS_TO_SINGLE_PDF, assetDownloadParameters.isMergeAssetsToSinglePDF());
        wfMetaData.put(AssetDownloadConstants.PARAMETER_KEY_MERGE_ASSETS_FILE_NAME, assetDownloadParameters.getMergeAssetsFileName());
        wfMetaData.put(AssetDownloadConstants.PARAMETER_KEY_RESOURCE_PATH, resourcePath);
        wfMetaData.put(AssetDownloadConstants.EMAIL_TEMPLATE, submittalDownload.getEmailTemplatePath());
        wfMetaData.put(AssetDownloadConstants.EMAIL_SENDER_NAME, submittalDownload.getSenderName());
        wfMetaData.put(AssetDownloadConstants.EMAIL_SUBJECT, submittalDownload.getEmailSubject());
        wfMetaData.put(AssetDownloadConstants.EMAIL_SENDER_ADDRESS, submittalDownload.getSenderEmailAddress());
        wfMetaData.put(AssetDownloadConstants.PARAMETER_KEY_EMAIL_LINK_PATH, resourcePath.concat(AssetDownloadConstants.EMAIL_LINK_PATH_SELECTOR));
        wfMetaData.put(AssetDownloadConstants.EMAIL_NEWS_LETTER_LINK, submittalDownload.getEmailNewsLetterLink());
        wfMetaData.put(AssetDownloadConstants.EMAIL_SUBMITTAL_TOOL_LINK, submittalDownload.getEmailSubmittalBuilderToolLink());
        wfMetaData.put(AssetDownloadConstants.EMAIL_CONTACT_US_LINK, submittalDownload.getEmailContactUsLink());
        wfMetaData.put(AssetDownloadConstants.EMAIL_WHERE_TO_BUY_LINK, submittalDownload.getEmailWhereToBuyLink());
        return wfMetaData;
    }

    private static void startWorkflow(final String wfModelId, final WorkflowSession wfSession, final String payload, final Map<String, Object> metaData) throws WorkflowException {
        final WorkflowModel wfModel = wfSession.getModel(wfModelId);
        final WorkflowData wfData = wfSession.newWorkflowData(AssetDownloadConstants.JCR_PATH, payload);
        if(wfModel == null || wfData == null) {
            throw new WorkflowException("Error getting workflow");
        } else {
            wfSession.startWorkflow(wfModel, wfData, metaData);
        }
    }

    private static void createErrorResponse(final String errorMessage, final String errorCode,
                                     final SlingHttpServletResponse response) {
        final JsonObject errorJson = new JsonObject();
        try {
            errorJson.add(AssetDownloadConstants.STATUS, new Gson().toJsonTree(AssetDownloadConstants.FAIL));
            errorJson.add(AssetDownloadConstants.MESSAGE, new Gson().toJsonTree(errorMessage));
            errorJson.add(AssetDownloadConstants.ERROR_CODE, new Gson().toJsonTree(errorCode));
            response.setContentType(AssetDownloadConstants.APPLICATION_JSON);
            response.getWriter().write(errorJson.toString());
            response.getWriter().flush();
        } catch( Exception exc ) {
            LOG.error("Error constructing JSON response ", exc);
        }
    }
}