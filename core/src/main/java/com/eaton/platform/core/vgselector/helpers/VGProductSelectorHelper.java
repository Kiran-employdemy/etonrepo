/**
 * 
 */
package com.eaton.platform.core.vgselector.helpers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.webservicesupport.ConfigurationManagerFactory;
import com.eaton.platform.core.models.vgSelector.ConfigModel;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.core.vgselector.constants.VGCommonConstants;
import com.eaton.platform.core.vgselector.utils.VGSelectorUtil;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.FacetGroupBean;
import com.eaton.platform.integration.endeca.bean.FacetValueBean;
import com.eaton.platform.integration.endeca.bean.vgproductselector.clutchselector.ClutchSelectorResponse;
import com.eaton.platform.integration.endeca.bean.vgproductselector.torqueselector.TorqueSelectorResponse;
import com.eaton.platform.integration.endeca.services.EndecaConfig;
import com.eaton.platform.integration.endeca.services.EndecaService;

/**
 * <html> Description: This class is invoked at "/apps/eaton/components/structure/eaton-product-selector-form/body.jsp"
 * for Page rendering component to load the dropdown options from endeca</html> .
 *
 * @author TCS
 * @version 1.0
 * @since 2018
 */
public class VGProductSelectorHelper {

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(VGProductSelectorHelper.class);
	
	/** The country. */
	private String country;
	
	/** The facet group list. */
	private List<FacetGroupBean> facetGroupList;
	
	/** The endeca service request bean. */
	private EndecaServiceRequestBean endecaServiceRequestBean = null;
	
	/**
	 * Sets the options on page load. This method is invoked in the body.jsp of page rendering component
	 *
	 * @param currentRes the current res
	 * @param resourceResolver the resource resolver
	 * @param sling the sling
	 */
	public void setOptionsOnPageLoad(Resource currentRes, ResourceResolver resourceResolver, SlingScriptHelper sling){
		LOG.debug("VGProductSelectorHelper :: setOptionsOnPageLoad() :: Start");
		Resource formContainerRes = null;
		String selectorToolType = StringUtils.EMPTY;
		int pageSize = 0;
		// get the form resource
		formContainerRes = resourceResolver.getResource(currentRes.getPath().concat(VGCommonConstants.FORM_ELEMENTS_PATH));
		if(null != formContainerRes) {
			AdminService adminService = sling.getService(AdminService.class);
			ConfigurationManagerFactory configManagerFctry = sling.getService(ConfigurationManagerFactory.class);
			if (null != adminService && null != configManagerFctry) {
				try (ResourceResolver adminResourceResolver = adminService.getReadService()){
					String resourcePath = StringUtils.substringBefore(formContainerRes.getPath(), VGCommonConstants.JCR_CONTENT);
					Resource currentPageRes = adminResourceResolver.resolve(resourcePath);


					// get the configuration set in the Cloud Config Tab of the form page
					ConfigModel cloudConfig = VGSelectorUtil.populateSiteConfiguration(configManagerFctry, resourceResolver, currentPageRes);
					if(null != cloudConfig) {
                        selectorToolType = cloudConfig.getSelectorToolType();
                        pageSize = cloudConfig.getPageSize();
                    }
					String searchTerm = VGCommonConstants.ENDECA_NO_RESULT;

					// get Data from Endeca
					populateEndecaData(currentRes, resourceResolver, sling, searchTerm, selectorToolType, pageSize);

					// set the dynamic options in the dropdown from Endeca
					setDynamicDropdownOptions(formContainerRes);
				}
			}
		}
		LOG.debug("VGProductSelectorHelper :: setOptionsOnPageLoad() :: End");
	}
	
	/**
	 * Populate endeca data.
	 *
	 * @param currentRes the current res
	 * @param resourceResolver the resource resolver
	 * @param slingHelper the sling helper
	 * @param searchTerm the search term
	 * @param selectorToolType the selector tool type
	 */
	public void populateEndecaData(Resource currentRes, ResourceResolver resourceResolver, SlingScriptHelper slingHelper, String searchTerm, String selectorToolType, int PageSize) {
		
		LOG.debug("VGProductSelectorHelper :: populateEndecaData() :: Start");
		EndecaService endecaService = null;
		// get Endeca service instance
		endecaService = slingHelper.getService(EndecaService.class);
		
		if (null != endecaService) {
			try {
				
				endecaServiceRequestBean = new EndecaServiceRequestBean();
				EndecaConfig endecaConfigService = slingHelper.getService(EndecaConfig.class);
				
				// set the language in the endeca request
				setLanguageRequestBean(currentRes);
				
				// set Endeca request Bean and for page rendering component set search term
				endecaServiceRequestBean.setSearchTerms(searchTerm);
				
				List<String> facetValues = new ArrayList<>();
				if(facetValues.isEmpty()){
					facetValues.add(StringUtils.EMPTY);
				}
				String defaultSort = VGCommonConstants.ASCENDING_SORT;
				
				List<String> returnFacetsFor = VGSelectorUtil.getFormElementNames(currentRes, resourceResolver, searchTerm, selectorToolType);

				endecaServiceRequestBean = VGSelectorUtil.populateEndecaServiceRequestBean(currentRes, resourceResolver, endecaConfigService, endecaServiceRequestBean, selectorToolType, searchTerm, returnFacetsFor, facetValues, facetValues, country, 0, PageSize, defaultSort);
	
				LOG.debug("Endeca Request : {}", endecaServiceRequestBean);
				// Populate Clutch/Torque Tool Response
				if (StringUtils.equals(VGCommonConstants.CLUTCH_PAGE, selectorToolType)) {
					ClutchSelectorResponse clutchSelectorResponseBean = null;
					clutchSelectorResponseBean = endecaService.getClutchToolDetails(endecaServiceRequestBean);
					LOG.debug(" Clutch Details Response from Endeca : {}", clutchSelectorResponseBean);
					if ((null != clutchSelectorResponseBean) && (null != clutchSelectorResponseBean.getClutchToolResponse())
							&& (null != clutchSelectorResponseBean.getClutchToolResponse().getStatus()) && (clutchSelectorResponseBean.getClutchToolResponse().getStatus()
								.equalsIgnoreCase(VGCommonConstants.ENDECA_STATUS_SUCCESS)) && (null != clutchSelectorResponseBean.getClutchToolResponse().getFacets()) ) {
							facetGroupList = clutchSelectorResponseBean.getClutchToolResponse().getFacets().getFacetGroupList();
					}
				} else if(StringUtils.equals(VGCommonConstants.TORQUE_PAGE, selectorToolType)) {
					TorqueSelectorResponse torqueSelectorResponseBean = null;
					torqueSelectorResponseBean = endecaService.getTorqueToolDetails(endecaServiceRequestBean);
					LOG.debug(" Torque Details Response from Endeca : {}", torqueSelectorResponseBean);
					if ((null != torqueSelectorResponseBean) && (null != torqueSelectorResponseBean.getTorqueToolResponse())
							&& (null != torqueSelectorResponseBean.getTorqueToolResponse().getStatus()) && (torqueSelectorResponseBean.getTorqueToolResponse().getStatus()
								.equalsIgnoreCase(VGCommonConstants.ENDECA_STATUS_SUCCESS)) && (null != torqueSelectorResponseBean.getTorqueToolResponse().getFacets()) ) {
							facetGroupList = torqueSelectorResponseBean.getTorqueToolResponse().getFacets().getFacetGroupList();
						}
					}
				} catch(Exception e){
					LOG.error("Exception while fetching response from endeca"+e.getMessage());
			}
		} else {
			LOG.error("Issue in getting Endeca Service instance");
		}
		
		LOG.debug("VGProductSelectorHelper :: populateEndecaData() :: End");
	}

	/**
	 * Sets the dynamic dropdown options.
	 *
	 * @param formContainerRes the new dynamic dropdown options
	 */
	private void setDynamicDropdownOptions(Resource formContainerRes) {
		LOG.debug("VGProductSelectorHelper :: setDynamicDropdownOptions() :: Start");
		String elementName;
		
		// get all the form dropdown elements
		Iterator<Resource> formCompIt = formContainerRes.listChildren();
		    
		while(formCompIt.hasNext()) {
        Resource formPanelElements = formCompIt.next();
        Iterator<Resource> panelElement = formPanelElements.listChildren();
        while(panelElement.hasNext()) {
        	Resource panelChildren = panelElement.next();
        	if(null != panelChildren && StringUtils.equals(VGCommonConstants.ITEMS, panelChildren.getName())) {
        		Iterator<Resource> elements = panelChildren.listChildren();
    		    while(elements.hasNext()){
    		        Resource formCompRes = elements.next();
    		        ValueMap dropdownCompPropMap = formCompRes.getValueMap();
    		        if(null != dropdownCompPropMap && StringUtils.equals(VGCommonConstants.FORM_DROPDOWN_ELEMENT, dropdownCompPropMap.get(VGCommonConstants.SLING_RESOURCE_TYPE_PROPERTY).toString())) {
    		        	elementName = CommonUtil.getStringProperty(dropdownCompPropMap, VGCommonConstants.PROPERTY_NAME_FIELD);
    			        
			        	if(null != facetGroupList){
			        		Node dropdownCompNode = formCompRes.adaptTo(Node.class);
			    			for(int i = 0 ; i < facetGroupList.size(); i++){
			    				
			    				FacetGroupBean facetGroupBean = facetGroupList.get(i);
			    				String facetGroupID = facetGroupBean.getFacetGroupId();
						        if(StringUtils.equalsIgnoreCase(facetGroupID, elementName) && !StringUtils.equals(VGCommonConstants.PORTFOLIO_RATING, facetGroupID)){
						        	String[] endecaResOptions = new String[facetGroupBean.getFacetValueList().size()];
						        	int index = 0;
						        	for (FacetValueBean value : facetGroupBean.getFacetValueList()) {
						        		if(StringUtils.equals(VGCommonConstants.YEAR, facetGroupID)) {
						        			endecaResOptions[index] = value.getFacetValueLabel()+ VGCommonConstants.YEAR + VGCommonConstants.EQUAL_ASSIGNMENT + value.getFacetValueLabel();
						        		}else if(StringUtils.equals(VGCommonConstants.MAKE, facetGroupID)) {
						        			endecaResOptions[index] = value.getFacetValueLabel() + VGCommonConstants.MAKE + VGCommonConstants.EQUAL_ASSIGNMENT + value.getFacetValueLabel();
						        		}else if(StringUtils.equals(VGCommonConstants.MODEL, facetGroupID)) {
						        			endecaResOptions[index] = value.getFacetValueLabel() + VGCommonConstants.MODEL +VGCommonConstants.EQUAL_ASSIGNMENT + value.getFacetValueLabel();
						        		} else {
						        			endecaResOptions[index] = value.getFacetValueId() + VGCommonConstants.EQUAL_ASSIGNMENT + value.getFacetValueLabel();
						        		}
						        	  index++;
						        	}
						        	setOptionsProperty(dropdownCompNode, endecaResOptions);
						        } 
			    			}
			    		}
    		        }
    		    }
        	}
        }
    }
		LOG.debug("VGProductSelectorHelper :: setDynamicDropdownOptions() :: End");
	}
	
	/**
	 * Sets the language request bean.
	 *
	 * @param currentRes the new language request bean
	 */
	private void setLanguageRequestBean(Resource currentRes) {
		LOG.debug("VGProductSelectorHelper :: setLanguageRequestBean() :: Start");
		country = StringUtils.EMPTY;
		String language = StringUtils.EMPTY;
		String formPath = currentRes.getPath();
		if(StringUtils.startsWith(formPath, VGCommonConstants.CONTENT_FORM_AF_PATH)) {
			String[] splitPath = StringUtils.split(formPath, VGCommonConstants.SLASH);
			country = splitPath[3].toUpperCase();
			language = splitPath[4];
			
			String[] lang = null;
			if(StringUtils.contains(language, VGCommonConstants.HYPHEN)) {
				lang = StringUtils.split(language, VGCommonConstants.HYPHEN);
			}else if(StringUtils.contains(language, VGCommonConstants.UNDERSCORE)) {
				lang = StringUtils.split(language, VGCommonConstants.UNDERSCORE);
			}
			if(null != lang) {
				language = lang[0]+ VGCommonConstants.UNDERSCORE +lang[1].toUpperCase();
			}
			
		}
		endecaServiceRequestBean.setLanguage(language);
		LOG.debug("VGProductSelectorHelper :: setLanguageRequestBean() :: End");
	}
	
	/**
	 * Sets the options property.
	 *
	 * @param dropdownCompNode the dropdown comp node
	 * @param options the options
	 */
	private void setOptionsProperty(Node dropdownCompNode, String[] options) {
		LOG.debug("VGProductSelectorHelper :: setOptionsProperty() :: Start");
		if(null != dropdownCompNode) {
			try {
				dropdownCompNode.setProperty(VGCommonConstants.PROPERTY_OPTIONS_FIELD, options);
				dropdownCompNode.getSession().save();
			} catch (ValueFormatException e) {
				LOG.error("Value Format Exception - "+e);
			} catch (VersionException e) {
				LOG.error("Version Exception - "+e);
			} catch (LockException e) {
				LOG.error("Lock Exception - "+e);
			} catch (ConstraintViolationException e) {
				LOG.error("Constraint Violation Exception - "+e);
			} catch (RepositoryException e) {
				LOG.error("Repository Exception - "+e);
			}
		}
		LOG.debug("VGProductSelectorHelper :: setOptionsProperty() :: End");
	}

}
