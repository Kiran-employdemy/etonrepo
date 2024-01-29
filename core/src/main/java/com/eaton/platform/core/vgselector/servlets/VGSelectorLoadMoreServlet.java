package com.eaton.platform.core.vgselector.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.eaton.platform.core.constants.ServletConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.webservicesupport.ConfigurationManagerFactory;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.core.vgselector.bean.loadmore.ClutchListLoadMore;
import com.eaton.platform.core.vgselector.bean.loadmore.TorqueListLoadMore;
import com.eaton.platform.core.vgselector.constants.VGCommonConstants;
import com.eaton.platform.core.vgselector.helpers.VGSelectorLoadMoreHelper;
import com.eaton.platform.core.vgselector.utils.VGSelectorUtil;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.vgproductselector.clutchselector.ClutchSelectorResponse;
import com.eaton.platform.integration.endeca.bean.vgproductselector.torqueselector.TorqueSelectorResponse;
import com.eaton.platform.integration.endeca.services.EndecaConfig;
import com.eaton.platform.integration.endeca.services.EndecaService;

import javax.servlet.Servlet;

/**
 * The Class VGSelectorLoadMoreServlet.
 */
@Component(service = Servlet.class,
		immediate = true,
		property = {
				ServletConstants.SLING_SERVLET_METHODS_POST,
				ServletConstants.SLING_SERVLET_PATHS + VGCommonConstants.LOAD_MORE_SERVLET_PATH,
		})
public class VGSelectorLoadMoreServlet  extends SlingSafeMethodsServlet {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7405260992713487834L;

	private static final Logger LOGGER = LoggerFactory.getLogger(VGSelectorLoadMoreServlet.class);
	
	/** The config factory. */
	@Reference
	private transient ConfigurationManagerFactory configFactory;
	
	/** The admin service. */
	@Reference
	private transient AdminService adminService;
	
	/** The endeca service. */
	@Reference
	private transient EndecaService endecaService;
	
	/** The endeca service request bean. */
	private EndecaServiceRequestBean endecaServiceRequestBean;
	
	/** The endeca config service. */
	@Reference
	private transient EndecaConfig endecaConfigService;

	/** The pagemanager. */
	PageManager pagemanager;
	
	/** The country. */
	private String country;
	
	/** The language. */
	String language;
	
	/** The formtype. */
	String formtype;
	
	/** The page size. */
	String pageSize;
	
	/** The default sort order. */
	String defaultSortOrder;
	
	/** The current load more. */
	String currentLoadMore;
	
	/** The current sort by option. */
	String currentSortByOption;
	
	/** The current resource path. */
	String currentResourcePath;
	
	/** The request uri. */
	String requestUri;
	
	/** The current page path. */
	String currentPagePath;
	
	/** The total count. */
	private int totalCount;
	
	/** The json response. */
	private String jsonResponse;
	
	/** The sku page name. */
	String skuPageName= null;
	
	/** The fall back image. */
	String fallBackImage = null; 
	
	/** The long desc check. */
	String longDescCheck = "true";
	
	/** The facets values str. */
	String facetsValuesStr = null;
	
	/** The return facets for str. */
	String returnFacetsForStr = null;
	
	/** The sku card parameters str. */
	String skuCardParametersStr = null;
	
	/** The count string. */
	private String countString;
	
	/** The sort option string. */
	String Sortoption;
	
	/* (non-Javadoc)
	 * @see org.apache.sling.api.servlets.SlingSafeMethodsServlet#doGet(org.apache.sling.api.SlingHttpServletRequest, org.apache.sling.api.SlingHttpServletResponse)
	 */
	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
		try (ResourceResolver adminResourceResolver = adminService.getReadService();){
			String loadMoreJsonString = request.getParameter(VGCommonConstants.URL);
			loadMoreJsonString = loadMoreJsonString.replace(VGCommonConstants.AMPERSAND_STRING, VGCommonConstants.AMPERSAND_SIGN);
			countString = request.getParameter(VGCommonConstants.COUNT);
			JSONParser parser = new JSONParser();
			JSONObject paramJson = new JSONObject();
			String selectorToolType;
			Page currentPage = null;
			pagemanager = adminResourceResolver.adaptTo(PageManager.class);
			paramJson = (JSONObject) parser.parse(loadMoreJsonString);
			populateJSONValues(paramJson);
			currentPage = pagemanager.getPage(currentPagePath);
			// get refererURL from request since current page is not available in fixed path servelts
			String refererURL = CommonUtil.getRefererURL(request);
			//get content path
			String resourcePath = CommonUtil.getContentPath(adminResourceResolver, refererURL);
			resourcePath = StringUtils.substringBefore(resourcePath, VGCommonConstants.JCR_CONTENT);

			Resource currentPageRes = adminResourceResolver.resolve(resourcePath);
			selectorToolType = formtype;
			VGSelectorLoadMoreHelper loadMoreHelper = new VGSelectorLoadMoreHelper();
			endecaServiceRequestBean = new EndecaServiceRequestBean();

			// set the language in the endeca request
			setLanguageRequestBean(currentPageRes);
			List<String> skuCard = new ArrayList<>(Arrays.asList(skuCardParametersStr.split(VGCommonConstants.COMMA)));
			List<String> returnFacets = new ArrayList<>(Arrays.asList(returnFacetsForStr.split(VGCommonConstants.COMMA)));
			List<String> facetsValues = new ArrayList<>(Arrays.asList(facetsValuesStr.split(VGCommonConstants.COMMA)));
			if(currentSortByOption != null && !(currentSortByOption.isEmpty())){
                Sortoption = currentSortByOption;
            }else{
                Sortoption = VGCommonConstants.ASCENDING_SORT;
            }
			int countInt = Integer.parseInt(countString);
			int pageSizeInt = Integer.parseInt(pageSize);
			countInt = countInt + pageSizeInt;
			endecaServiceRequestBean = VGSelectorUtil.populateEndecaServiceRequestBean(currentPageRes, adminResourceResolver, endecaConfigService, endecaServiceRequestBean, selectorToolType, VGCommonConstants.ENDECA_RESULT, returnFacets, skuCard, facetsValues, country,countInt, pageSizeInt, Sortoption);
			if (StringUtils.equals(VGCommonConstants.CLUTCH_PAGE, selectorToolType)) {
                ClutchSelectorResponse clutchSelectorResponseBean = endecaService.getClutchToolDetails(endecaServiceRequestBean);
                totalCount = clutchSelectorResponseBean.getClutchToolResponse().getTotalCount();
                ClutchListLoadMore clutchListLoadMore = loadMoreHelper.setClutchListLoadMoreValues(clutchSelectorResponseBean.getClutchToolResponse().getClutchSearchResults(),VGCommonConstants.PAGE_NAME,currentPage,fallBackImage,longDescCheck,request.getResourceResolver());
                jsonResponse = loadMoreHelper.convertClutchToolBeanTOJSON(clutchListLoadMore);
            }else if(StringUtils.equals(VGCommonConstants.TORQUE_PAGE, selectorToolType)){
                TorqueSelectorResponse torqueSelectorResponseBean = endecaService.getTorqueToolDetails(endecaServiceRequestBean);
                totalCount = torqueSelectorResponseBean.getTorqueToolResponse().getTotalCount();

                TorqueListLoadMore torqueListLoadMore = loadMoreHelper.setTorqueListLoadMoreValues(torqueSelectorResponseBean.getTorqueToolResponse().getTorqueSearchResults(),VGCommonConstants.PAGE_NAME,currentPage,fallBackImage,longDescCheck,request.getResourceResolver());

                jsonResponse = loadMoreHelper.convertTorqueToolBeanTOJSON(torqueListLoadMore);
            }
			int indexLast = jsonResponse.lastIndexOf(VGCommonConstants.BRACKET);
			String subJson = jsonResponse.substring(0, indexLast);
			String buttonStatus = VGCommonConstants.ACTIVE;
			if(countInt+pageSizeInt >= totalCount){
                buttonStatus = VGCommonConstants.INACTIVE;
            }
			jsonResponse = subJson+",\"loadmoreButtonCount\":\""+countInt+"\",\"buttonStatus\":\""+buttonStatus+"\"}";

			response.setContentType(VGCommonConstants.TEXT_HTML);
			response.getWriter().println(jsonResponse);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Populate JSON values.
	 *
	 * @param paramJson the param json
	 */
	private void populateJSONValues(JSONObject paramJson) {
		JSONArray valuesJSONarray = new JSONArray();
		if(paramJson.containsKey(VGCommonConstants.DATA_ATTRIBUTE)){
			valuesJSONarray = (JSONArray) paramJson.get(VGCommonConstants.DATA_ATTRIBUTE);
			
			for(int i=0;i<valuesJSONarray.size();i++){
				JSONObject  innerJson = (JSONObject) valuesJSONarray.get(i);
				
				if(innerJson.containsKey(VGCommonConstants.CURRENT_LANG)  && innerJson.get(VGCommonConstants.CURRENT_LANG) != null){
					language = innerJson.get(VGCommonConstants.CURRENT_LANG).toString();
				}else if(innerJson.containsKey(VGCommonConstants.CURRENT_COUNTRY) && innerJson.get(VGCommonConstants.CURRENT_COUNTRY) != null){
					country = innerJson.get(VGCommonConstants.CURRENT_COUNTRY).toString();
				}else if(innerJson.containsKey(VGCommonConstants.FORM_TYPE) && innerJson.get(VGCommonConstants.FORM_TYPE) != null){
					formtype = innerJson.get(VGCommonConstants.FORM_TYPE).toString();
				}else if(innerJson.containsKey(VGCommonConstants.PAGE_SIZE) && innerJson.get(VGCommonConstants.PAGE_SIZE) != null){
					pageSize = innerJson.get(VGCommonConstants.PAGE_SIZE).toString();
				}else if(innerJson.containsKey(VGCommonConstants.DEFAULT_SORT_ORDER) && innerJson.get(VGCommonConstants.DEFAULT_SORT_ORDER) != null){
					defaultSortOrder = innerJson.get(VGCommonConstants.DEFAULT_SORT_ORDER).toString();
				}else if(innerJson.containsKey(VGCommonConstants.CURRENT_LOAD_MORE) && innerJson.get(VGCommonConstants.CURRENT_LOAD_MORE) != null){
					currentLoadMore = innerJson.get(VGCommonConstants.CURRENT_LOAD_MORE).toString();
				}else if(innerJson.containsKey(VGCommonConstants.CURRENT_SORT_OPTION) && innerJson.get(VGCommonConstants.CURRENT_SORT_OPTION) != null){
					currentSortByOption = innerJson.get(VGCommonConstants.CURRENT_SORT_OPTION).toString();
				}else if(innerJson.containsKey(VGCommonConstants.CURRENT_RESOURCE_PATH) && innerJson.get(VGCommonConstants.CURRENT_RESOURCE_PATH) != null){
					currentResourcePath = innerJson.get(VGCommonConstants.CURRENT_RESOURCE_PATH).toString();
				}else if(innerJson.containsKey(VGCommonConstants.REQUEST_URI) && innerJson.get(VGCommonConstants.REQUEST_URI) != null){
					requestUri = innerJson.get(VGCommonConstants.REQUEST_URI).toString();
				}else if(innerJson.containsKey(VGCommonConstants.CURRENT_PAGE_PATH) && innerJson.get(VGCommonConstants.CURRENT_PAGE_PATH) != null){
                	currentPagePath = innerJson.get(VGCommonConstants.CURRENT_PAGE_PATH).toString();
                }else if(innerJson.containsKey(VGCommonConstants.FALL_BACK_IMAGE) && innerJson.get(VGCommonConstants.FALL_BACK_IMAGE) != null){
                	fallBackImage = innerJson.get(VGCommonConstants.FALL_BACK_IMAGE).toString();
                }else if(innerJson.containsKey(VGCommonConstants.FACET_VALUES) && innerJson.get(VGCommonConstants.FACET_VALUES) != null){
                	facetsValuesStr = innerJson.get(VGCommonConstants.FACET_VALUES).toString();
                }else if(innerJson.containsKey(VGCommonConstants.RETURN_FACETS_FOR) && innerJson.get(VGCommonConstants.RETURN_FACETS_FOR) != null){
                	returnFacetsForStr = innerJson.get(VGCommonConstants.RETURN_FACETS_FOR).toString();
                }else if(innerJson.containsKey(VGCommonConstants.SKU_CARD_PARAMETERS_LOAD_MORE) && innerJson.get(VGCommonConstants.SKU_CARD_PARAMETERS_LOAD_MORE) != null){
                	skuCardParametersStr = innerJson.get(VGCommonConstants.SKU_CARD_PARAMETERS_LOAD_MORE).toString();
                }
				}
			}
	}

	/**
	 * Sets the language request bean.
	 *
	 * @param currentRes the new language request bean
	 */
	private void setLanguageRequestBean(Resource currentRes) {
		//		 Setting Language Locale & country
		
		Page currentPage = currentRes.adaptTo(Page.class);
		if(null != currentPage) {
			Locale languageValue = currentPage.getLanguage(false);
			if (languageValue != null && ((languageValue.getLanguage() != null) && (!languageValue.getLanguage().equals(StringUtils.EMPTY)))) {
				if ((languageValue.getCountry() != null) && (!languageValue.getCountry().equals(StringUtils.EMPTY))) {
					country = languageValue.getCountry();
					endecaServiceRequestBean.setLanguage(CommonUtil.getUpdatedLocaleFromPagePath(currentPage));
					Page countryPage = currentPage.getAbsoluteParent(2);
					String countryString = countryPage.getName();
					if((!country.equalsIgnoreCase(countryString)) && (!countryString.equals(VGCommonConstants.LANGUAGE_MASTERS))){
						country = countryString.toUpperCase();
					}
				}
			}
		}
	}

}
