package com.eaton.platform.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.helpers.FacetedNavigationHelperV2;
import com.eaton.platform.core.models.*;
import com.eaton.platform.core.models.search.SearchFacetGroupModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaton.platform.core.bean.ResourceCategory;

/**
 * The Class SiteConfigUtil.
 */
public final class SiteConfigUtil {
	/** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SiteConfigUtil.class);
    
	/** The Constant SUPPORT_COUNTRY_LIST_NODE_NAME. */
	private static final String SUPPORT_COUNTRY_LIST_NODE_NAME = "suppCountryList";
	
	/** The Constant GLOBAL_ATTR_LIST_NODE_NAME. */
	private static final String GLOBAL_ATTR_LIST_NODE_NAME = "globalAttributeList";
	
	/** The Constant SITE_SEARCH_FACET_GROUP. */
	private static final String SITE_SEARCH_FACET_GROUP = "siteSearchFacetGroup";
	
	/** The Constant Document_Group_LIST_NODE_NAME. */
	private static final String DOCUMENT_GROUP_LIST_NODE_NAME = "documentGroup";
	
	/** The Constant SITE_VERIFICATION_CODE_LIST_NODE_NAME. */
	private static final String SITE_VERIFICATION_CODE_LIST_NODE_NAME = "siteVerificationCodeList";

    /**
     * Instantiates a new site config util.
     */
    private SiteConfigUtil() {
        LOGGER.info("Inside SiteConfigUtil constructor");
    }

    /**
     * Gets the site config details.
     *
     * @param siteConfigRes the site config res
     * @return the site config details
     */
    public static SiteConfigModel getSiteConfigDetails(Resource siteConfigRes){
    	LOGGER.info("Entered into SiteConfigUtil getSiteConfigDetails() method :::");
    	SiteConfigModel siteConfigModel = null;
    	if(null != siteConfigRes){
    		siteConfigModel = siteConfigRes.adaptTo(SiteConfigModel.class);
    		LOGGER.info(" SiteConfig Attributes:::");
    		LOGGER.info("Override PDH Data:" ,siteConfigModel.getOverridePDHData() );
    		LOGGER.info("EnableProductsTab:" ,siteConfigModel.getEnableProductsTab() );
    		LOGGER.info("EnableNewsTab:" ,siteConfigModel.getEnableNewsTab());
    		LOGGER.info("EnableServicesTab:" ,siteConfigModel.getEnableServicesTab());
    		ValueMap siteConfigVP = siteConfigRes.getValueMap();
    		
    		List <GlobalAttributeModel> globalAttributesList = populateGlobalAttributes(siteConfigVP);
    		siteConfigModel.setGlobalAttributeListSize(globalAttributesList.size());
    		siteConfigModel.setGlobalAttributesList(globalAttributesList);
    		
    		List <SupportInfoCountryModel> supportInfoCountryList = populateSupportInfoCountry(siteConfigVP);
    		siteConfigModel.setSupprotInfoCountryListSize(supportInfoCountryList.size());
    		siteConfigModel.setSupportInfoContriesList(supportInfoCountryList);
    		
    		List <DocumentGroupWithAemTagsModel> documentGroupsListList = populateDocumentGroupWithAemTags(siteConfigVP);    		
    		siteConfigModel.setDocumentGroupWithAemTagsListSize(documentGroupsListList.size());
    		siteConfigModel.setDocumentGroupWithAemTagsList(documentGroupsListList);
    		
    		List <ResourceCategory> resourceCategoryList = populateResourceListForSKU(siteConfigModel);    		
    		siteConfigModel.setResourceCategoryListSize(resourceCategoryList.size());
    		siteConfigModel.setResourceCategoryList(resourceCategoryList);
    		 
    		List<SearchFacetGroupModel> siteSearchFacetGroupsList = populateSiteSearchFacets(siteConfigVP);
    		siteConfigModel.setSiteSearchFacetGroupsListSize(siteSearchFacetGroupsList.size());
    		siteConfigModel.setSiteSearchFacetGroups(siteSearchFacetGroupsList);
			
			List<SiteVerificationCodeModel> siteVerificationCodeList = populateSiteVerificationCodeModelList(siteConfigModel);
			siteConfigModel.setSiteVerificationCodeModelListSize(siteVerificationCodeList.size());
			siteConfigModel.setSiteVerificationCodeModelList(siteVerificationCodeList);
    		
    	}
    	LOGGER.info("Exited from SiteConfigUtil getSiteConfigDetails() method :::");
    	return siteConfigModel;
    	
    }
    
    /**
     * Populate global attributes.
     *
     * @param siteConfigVP the site config VP
     * @return the list
     */
    public static List <GlobalAttributeModel> populateGlobalAttributes(ValueMap siteConfigVP) {
    	LOGGER.info("Entered into SiteConfigUtil populateGlobalAttributes() method :::");
    	List <GlobalAttributeModel> globalAttributesList = new ArrayList <GlobalAttributeModel>();
    	
    	if (null != siteConfigVP) {
			String[] jsonString =  (String[]) siteConfigVP.get(GLOBAL_ATTR_LIST_NODE_NAME,String[].class);
			if(jsonString != null && jsonString.length != 0){
			for (int i = 0; i <jsonString.length; i++){
				GlobalAttributeModel globalAttribute = new GlobalAttributeModel();
				JSONParser parser = new JSONParser();
				try {
					JSONObject jsonObject = (JSONObject) parser.parse(jsonString[i]);
					
					globalAttribute.setAttributevalue(jsonObject.get("attributevalue").toString());
					LOGGER.info("Global Attributes Values :");
					LOGGER.info(globalAttribute.getAttributevalue());
					globalAttributesList.add(globalAttribute);
					
				} catch (ParseException pe) {
					LOGGER.error("Exception Occured while parsing ", pe);
				}
			}
						
		}
    	}
    	LOGGER.info("Exit from SiteConfigUtil populateGlobalAttributes() method :::");
		return globalAttributesList;
		
	}
    
   
    /**
     * Populate support info country.
     *
     * @param siteConfigVP the site config VP
     * @return the list
     */
    public static List <SupportInfoCountryModel> populateSupportInfoCountry(ValueMap siteConfigVP) {
    	LOGGER.info("Entered into SiteConfigUtil populateSupportInfoCountry() method :::");
    List <SupportInfoCountryModel> supportInfoCountryList = new ArrayList <SupportInfoCountryModel>();
    	
		if (null != siteConfigVP) {
			String[] jsonString =  (String[]) siteConfigVP.get(SUPPORT_COUNTRY_LIST_NODE_NAME,String[].class);
			if(jsonString != null && jsonString.length != 0){
			for (int i = 0; i <jsonString.length; i++){
				SupportInfoCountryModel supportInfoCountry = new SupportInfoCountryModel();
				JSONParser parser = new JSONParser();
				try {
					JSONObject jsonObject = (JSONObject) parser.parse(jsonString[i]);
					
					supportInfoCountry.setCountryName(jsonObject.get("text").toString());
					supportInfoCountry.setCountryValue(jsonObject.get("value").toString());
					supportInfoCountryList.add(supportInfoCountry);
					LOGGER.info("supportInfoCountry Values :");
					LOGGER.info(supportInfoCountry.getCountryName()+ "" +supportInfoCountry.getCountryValue());
				} catch (ParseException e) {
					LOGGER.error("Exception Occured while parsing ", e);
				}
			}
						
		}
			}
		LOGGER.info("Exit fom SiteConfigUtil populateSupportInfoCountry() method :::");
		return supportInfoCountryList;
		
	}
    
    /**
     * Populate document group with aem tags.
     *
     * @param siteConfigVP the site config VP
     * @return the list
     */
    public static List <DocumentGroupWithAemTagsModel> populateDocumentGroupWithAemTags(ValueMap siteConfigVP) {
    	LOGGER.info("Entered into fom SiteConfigUtil populateDocumentGroupWithAemTags() method :::");
    	List <DocumentGroupWithAemTagsModel> documentTypeList = new ArrayList <DocumentGroupWithAemTagsModel>();
    	
    	if (null != siteConfigVP) {
			String[] jsonString =  (String[]) siteConfigVP.get(DOCUMENT_GROUP_LIST_NODE_NAME,String[].class);
			if(jsonString != null && jsonString.length != 0){
			for (int i = 0; i <jsonString.length; i++){
				DocumentGroupWithAemTagsModel documentGroupModel = new DocumentGroupWithAemTagsModel();
				JSONParser parser = new JSONParser();
				try {
					JSONObject JsonObject = (JSONObject) parser.parse(jsonString[i]);
					
					documentGroupModel.setGroupName(JsonObject.get("groupName").toString());
					documentGroupModel.setGroupDescription(JsonObject.get("groupDescription").toString());
					documentGroupModel.setAnchorId(JsonObject.get("anchorId").toString());
					LOGGER.info("Resource List for Family Page :");
					LOGGER.info(documentGroupModel.getGroupName()+ "" + documentGroupModel.getGroupDescription()+ "" +documentGroupModel.getAnchorId());
					JSONArray aemTags = (JSONArray) JsonObject.get("aemtags");
					if(aemTags != null && aemTags.size()!= 0){
					String[] tagsArray = new String[aemTags.size()];
					for(int j=0;j<aemTags.size();j++){
						 tagsArray[j] =  aemTags.get(j).toString();
					}
					 
					    String aemTagsString = Arrays.toString(tagsArray);
					    String aemTagsStringModified = aemTagsString.replace("[", "").replace("]", "");
					
					documentGroupModel.setAemtags(aemTagsStringModified);
					LOGGER.info(documentGroupModel.getAemtags());
					}
					documentTypeList.add(documentGroupModel);
					
				} catch (ParseException e) {
					LOGGER.error("Exception Occured while parsing ",e);
				}
			}
			}				
		}
    	LOGGER.info("Exit fom SiteConfigUtil populateDocumentGroupWithAemTags() method :::");
		return documentTypeList;
		
	}
    
    /**
     * Populate resource list for SKU.
     *
     * @param siteConfigModel the site config model
     * @return the list
     */
    public static List <ResourceCategory> populateResourceListForSKU(SiteConfigModel siteConfigModel) {
    	LOGGER.info("Entered into fom SiteConfigUtil populateResourceListForSKU() method :::");
    String[] resourceGroupArray = siteConfigModel.getResourceGroup();
	ArrayList<ResourceCategory> resourceCategoryList = new ArrayList<ResourceCategory>();
	
	if(resourceGroupArray!=null){
		for(int g=0;g<resourceGroupArray.length;g++){
			String resourceGroupEntry = resourceGroupArray[g];
			if(resourceGroupEntry!=null){
				String[] resourceGroupEntryArray = resourceGroupEntry.split("\\|");
				ResourceCategory resourceCategory = new ResourceCategory();
				if((resourceGroupEntryArray[0]!=null) && (!resourceGroupEntryArray[0].equals(""))){
					resourceCategory.setResourceCategoryID(resourceGroupEntryArray[0]);
					
					if((resourceGroupEntryArray[1]!=null) && (!resourceGroupEntryArray[1].equals(""))){
						String pdhGroudIDs = resourceGroupEntryArray[1];
						if(pdhGroudIDs!=null){
							String[] pdhGroudIDArray = pdhGroudIDs.split(",");
							if(pdhGroudIDArray!=null){
								ArrayList<String> PDHGroupIDs = new ArrayList<String>();
								for(int h=0;h<pdhGroudIDArray.length;h++){
									PDHGroupIDs.add(pdhGroudIDArray[h]);
								}
								resourceCategory.setPDHGroupIDs(PDHGroupIDs);
							}
						}
					}
					
					LOGGER.info("Resource List for SKU Page :");
					LOGGER.info(resourceCategory.getResourceCategoryID()+ "" + resourceCategory.getPDHGroupIDs());
				}
				resourceCategoryList.add(resourceCategory);
			}
		}
		
	}
	LOGGER.info("Exit fom SiteConfigUtil populateResourceListForSKU() method :::");
	return resourceCategoryList; 
       
}
    
    /**
     * Populate site search facets.
     *
     * @param siteConfigVP the site config VP
     * @return the list
     */
    public static List <SearchFacetGroupModel> populateSiteSearchFacets(ValueMap siteConfigVP) {
    	LOGGER.info("Entered into SiteConfigUtil populateSiteSearchFacets() method :::");
    	List<SearchFacetGroupModel> siteSearchFacetGroupList = new ArrayList<>();
		if	(siteConfigVP != null){
		final String[] jsonStringSite = siteConfigVP.get(SITE_SEARCH_FACET_GROUP,String[].class);

		if(jsonStringSite != null && jsonStringSite.length != 0){

			for (int i = 0; i <jsonStringSite.length; i++){
				JSONParser parser = new JSONParser();
				try {
					final JSONObject JsonObject = (JSONObject) parser.parse(jsonStringSite[i]);
					if (null != JsonObject){
						final SearchFacetGroupModel facetGroupBean = new SearchFacetGroupModel();
						facetGroupBean.setSiteSearchFacetGroup(String.valueOf(JsonObject.getOrDefault(SearchResultsRenderModel.FACET_SITE_SEARCH_FACET_GROUPS, StringUtils.EMPTY)));
						facetGroupBean.setGridFacet((boolean) JsonObject.getOrDefault(FacetedNavigationHelperV2.FACET_GROUP_SHOW_AS_GRID,false));
						facetGroupBean.setFacetSearchEnabled((boolean) JsonObject.getOrDefault(FacetedNavigationHelperV2.FACET_GROUP_FACET_SEARCH_ENABLED,false));
						facetGroupBean.setSingleFacetEnabled((boolean) JsonObject.getOrDefault(FacetedNavigationHelperV2.SINGLE_FACET_ENABLED,false));
						siteSearchFacetGroupList.add(facetGroupBean);
						LOGGER.info("Factes Group:{} ", facetGroupBean);
					}

				} catch (ParseException e) {
					LOGGER.info("Exception Occured",e);
				}
			   }
		  }
		}
    LOGGER.info("Exit fom SiteConfigUtil populateSiteSearchFacets() method :::");
    return siteSearchFacetGroupList;
    }

	/**
	 * Populate a list containing the meta tag names and values for site verification codes
	 *
	 * @param siteConfigModel - the site's configuration model used to get the list of site verification codes
	 * @return the list site verification codes
	 */
	public static List<SiteVerificationCodeModel> populateSiteVerificationCodeModelList(SiteConfigModel siteConfigModel) {
		LOGGER.debug("SiteConfigUtil : populateSiteVerificationCodeModelList : START");
		
		List <SiteVerificationCodeModel> siteVerificationCodeModelList = new ArrayList <>();

		if (null != siteConfigModel) {
			
			String siteConfigTitle = siteConfigModel.getTitle();
			String[] siteVerificationCodeList = siteConfigModel.getSiteVerificationCodeList();
			
			if(null == siteVerificationCodeList || siteVerificationCodeList.length == 0) {
				LOGGER.warn("Site verification code list is empty in {}", siteConfigTitle);
			} else {
				LOGGER.debug("Site verification code list found in {}. Setting {} site verification codes.", siteConfigTitle, siteVerificationCodeList.length);
				for (String siteVerificationCodeVP : siteVerificationCodeList) {
					
					SiteVerificationCodeModel siteVerificationCodeModel = new SiteVerificationCodeModel();
					JSONParser parser = new JSONParser();
					try {
						JSONObject jsonObject = (JSONObject) parser.parse(siteVerificationCodeVP);
						siteVerificationCodeModel.setName(jsonObject.get(CommonConstants.PROPERTY_NAME).toString());
						siteVerificationCodeModel.setValue(jsonObject.get(CommonConstants.VALUE).toString());
						siteVerificationCodeModelList.add(siteVerificationCodeModel);
						LOGGER.debug("Site verification code set with meta tag name ({}) and site verification code ({}).", siteVerificationCodeModel.getName(), siteVerificationCodeModel.getValue());
					} catch (ParseException e) {
						LOGGER.error("Exception occurred while parsing site verification code list for {}", siteConfigTitle, e);
					}
					
				}
			}
			
		}
		LOGGER.debug("SiteConfigUtil : populateSiteVerificationCodeModelList : END");
		return siteVerificationCodeModelList;

	}
    
}
