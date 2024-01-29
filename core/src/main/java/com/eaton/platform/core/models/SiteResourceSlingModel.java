package com.eaton.platform.core.models;

import com.eaton.platform.core.bean.DocumentGroupBean;
import com.eaton.platform.core.bean.GlobalAttrGroupBean;
import com.eaton.platform.core.bean.ResourceCategory;
import com.eaton.platform.core.bean.SearchSortingOptionsBean;
import com.eaton.platform.core.bean.SupportCountryBean;
import com.eaton.platform.core.helpers.FacetedNavigationHelperV2;
import com.eaton.platform.integration.endeca.bean.FacetGroupBean;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SiteResourceSlingModel {

	private static final Logger LOGGER = LoggerFactory.getLogger(SiteResourceSlingModel.class);
	public static final String AEM_TAGS = "aemtags";
	public static final String ANCHOR_ID = "anchorId";
	public static final String GROUP_NAME = "groupName";
	public static final String GROUP_DESCRIPTION = "groupDescription";

	@Inject
	private int pageSize;

	@Inject
	private int facetCount;

	@Inject
	private int noOfDays;

	@Inject
	private int expandedFacetCount;

	@Inject
	private int facetValueCount;

	@Inject
	private String defaultSiteSearchSortOrder;

	@Inject
	private String defaultProductFamilySortOrder;

	@Inject
	private String defaultCategoryWithCardsSortOrder;

	@Inject
	private String globalAttributeGroupName;

	// @Inject
	private List<GlobalAttrGroupBean> globalAttributeArrayList = new ArrayList<GlobalAttrGroupBean>();

	@Inject
	private String[] globalAttributeList;

	@Inject
	private ArrayList<ResourceCategory> resourceCategoryList = new ArrayList<ResourceCategory>();

	@Inject
	private int countUpSell;

	@Inject
	private int countImages;

	@Inject
	private int countCorouselItem;

	@Inject
	private String skuFallBackImage;

	@Inject
	private String defaultLinkCTA;

	@Inject
	private String defaultLinkHTB;

	@Inject
	private String pfOverviewTitle;

	@Inject
	private String pfOverviewDesc;

	@Inject
	private String pfModelsTitle;

	@Inject
	private String pfModelsDesc;

	@Inject
	private String pfResourcesTitle;

	@Inject
	private String skuOverviewTitle;

	@Inject
	private String skuOverviewDesc;

	@Inject
	private String skuSpecificationsTitle;

	@Inject
	private String skuSpecificationsDesc;

	@Inject
	private String skuResourcesTitle;

	// @Inject
	private List<SupportCountryBean> suppCountryList = new ArrayList<>();

	private List<FacetGroupBean> facetGroupsList = new ArrayList<>();

	@Inject
	private String overridePDHData;

	private List<DocumentGroupBean> documentGroup = new ArrayList<>();

	@Inject
	private String skuPageURL;

	@Inject
	private String searchResultsURL;

	@Inject
	private String crossRefSearchResultsURL;

	@Inject
	private String[] resourceGroup;
	
	@Inject @Default (values = "false")
	private String  disableDatasheet;

	@Inject
	private String enableProductsTab;

	@Inject
	private String enableNewsTab;

	@Inject
	private String enableResourcesTab;

	@Inject
	private String enableServicesTab;
	
	@Inject @Default (values = "false")
	private String unitedStatesDateFormat;

	@Inject
	@Named("documentGroup")
	private String[] documentGroupArr;

	@Inject
	@Named("suppCountryList")
	private String[] suppCountryResource;

	private List<String> siteSearchFacetGroups = new ArrayList<String>();

	@Inject
	@Named("siteSearchFacetGroup")
	private String[] siteSearchFacetGroup;

	@Inject
	private String defaultSortOrder;

	@Inject
	private String dirType;

	private List<SearchSortingOptionsBean> searchSortingOptionList = new ArrayList<>();

	private FacetedNavigationHelperV2 facetedNavigationHelperV2 = new FacetedNavigationHelperV2();

	public String getDirType() {
		return dirType;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * @return the noOfDays
	 */
	public int getNoOfDays() {
		return noOfDays;
	}

	/**
	 * @param noOfDays the noOfDays to set
	 */
	public void setNoOfDays(int noOfDays) {
		this.noOfDays = noOfDays;
	}

	public int getFacetCount() {
		return facetCount;
	}

	public void setFacetCount(int facetCount) {
		this.facetCount = facetCount;
	}

	public int getExpandedFacetCount() {
		return expandedFacetCount;
	}

	public void setExpandedFacetCount(int expandedFacetCount) {
		this.expandedFacetCount = expandedFacetCount;
	}

	public int getFacetValueCount() {
		return facetValueCount;
	}

	public void setFacetValueCount(int facetValueCount) {
		this.facetValueCount = facetValueCount;
	}

	public String getDefaultSiteSearchSortOrder() {
		return defaultSiteSearchSortOrder;
	}

	public void setDefaultSiteSearchSortOrder(String defaultSiteSearchSortOrder) {
		this.defaultSiteSearchSortOrder = defaultSiteSearchSortOrder;
	}

	public String getDefaultProductFamilySortOrder() {
		return defaultProductFamilySortOrder;
	}

	public void setDefaultProductFamilySortOrder(String defaultProductFamilySortOrder) {
		this.defaultProductFamilySortOrder = defaultProductFamilySortOrder;
	}

	public List<FacetGroupBean> getFacetGroupsList() {
		return facetGroupsList;
	}

	public void setFacetGroupsList(List<FacetGroupBean> facetGroupsList) {
		this.facetGroupsList = facetGroupsList;
	}

	/**
	 * @return the globalAttributeList
	 */
	public List<GlobalAttrGroupBean> getGlobalAttributeList() {
		return globalAttributeArrayList;
	}

	/**
	 * @param globalAttributeList the globalAttributeList to set
	 */
	public void setGlobalAttributeList(List<GlobalAttrGroupBean> globalAttributeList) {
		this.globalAttributeArrayList = globalAttributeList;
	}

	public ArrayList<ResourceCategory> getResourceCategoryList() {
		return resourceCategoryList;
	}

	public void setResourceCategoryList(ArrayList<ResourceCategory> resourceCategoryList) {
		this.resourceCategoryList = resourceCategoryList;
	}

	public int getCountUpSell() {
		return countUpSell;
	}

	public void setCountUpSell(int countUpSell) {
		this.countUpSell = countUpSell;
	}

	public int getCountImages() {
		return countImages;
	}

	public void setCountImages(int countImages) {
		this.countImages = countImages;
	}

	public int getCountCorouselItem() {
		return countCorouselItem;
	}

	public void setCountCorouselItem(int countCorouselItem) {
		this.countCorouselItem = countCorouselItem;
	}

	public String getSkuFallBackImage() {
		return skuFallBackImage;
	}

	public void setSkuFallBackImage(String skuFallBackImage) {
		this.skuFallBackImage = skuFallBackImage;
	}

	public String getDefaultLinkCTA() {
		return defaultLinkCTA;
	}

	public void setDefaultLinkCTA(String defaultLinkCTA) {
		this.defaultLinkCTA = defaultLinkCTA;
	}

	public String getDefaultLinkHTB() {
		return defaultLinkHTB;
	}

	public void setDefaultLinkHTB(String defaultLinkHTB) {
		this.defaultLinkHTB = defaultLinkHTB;
	}

	public String getPfOverviewTitle() {
		return pfOverviewTitle;
	}

	public void setPfOverviewTitle(String pfOverviewTitle) {
		this.pfOverviewTitle = pfOverviewTitle;
	}

	public String getPfOverviewDesc() {
		return pfOverviewDesc;
	}

	public void setPfOverviewDesc(String pfOverviewDesc) {
		this.pfOverviewDesc = pfOverviewDesc;
	}

	public String getPfModelsTitle() {
		return pfModelsTitle;
	}

	public void setPfModelsTitle(String pfModelsTitle) {
		this.pfModelsTitle = pfModelsTitle;
	}

	public String getPfModelsDesc() {
		return pfModelsDesc;
	}

	public void setPfModelsDesc(String pfModelsDesc) {
		this.pfModelsDesc = pfModelsDesc;
	}

	public String getPfResourcesTitle() {
		return pfResourcesTitle;
	}

	public void setPfResourcesTitle(String pfResourcesTitle) {
		this.pfResourcesTitle = pfResourcesTitle;
	}

	public String getSkuOverviewTitle() {
		return skuOverviewTitle;
	}

	public void setSkuOverviewTitle(String skuOverviewTitle) {
		this.skuOverviewTitle = skuOverviewTitle;
	}

	public String getSkuOverviewDesc() {
		return skuOverviewDesc;
	}

	public void setSkuOverviewDesc(String skuOverviewDesc) {
		this.skuOverviewDesc = skuOverviewDesc;
	}

	public String getSkuSpecificationsTitle() {
		return skuSpecificationsTitle;
	}

	public void setSkuSpecificationsTitle(String skuSpecificationsTitle) {
		this.skuSpecificationsTitle = skuSpecificationsTitle;
	}

	public String getSkuSpecificationsDesc() {
		return skuSpecificationsDesc;
	}

	public void setSkuSpecificationsDesc(String skuSpecificationsDesc) {
		this.skuSpecificationsDesc = skuSpecificationsDesc;
	}

	public String getSkuResourcesTitle() {
		return skuResourcesTitle;
	}

	public void setSkuResourcesTitle(String skuResourcesTitle) {
		this.skuResourcesTitle = skuResourcesTitle;
	}

	public String getOverridePDHData() {
		return overridePDHData;
	}

	public void setOverridePDHData(String overridePDHData) {
		this.overridePDHData = overridePDHData;
	}

	/**
	 * @return the suppCountryList
	 */
	public List<SupportCountryBean> getSuppCountryList() {
		return suppCountryList;
	}

	/**
	 * @param suppCountryList the suppCountryList to set
	 */
	public void setSuppCountryList(List<SupportCountryBean> suppCountryList) {
		this.suppCountryList = suppCountryList;
	}

	/**
	 * @return the documentGroup
	 */
	public List<DocumentGroupBean> getDocumentGroup() {
		return documentGroup;
	}

	/**
	 * @param documentGroup the documentGroup to set
	 */
	public void setDocumentGroup(List<DocumentGroupBean> documentGroup) {
		this.documentGroup = documentGroup;
	}

	public String[] getResourceGroup() {
		return resourceGroup;
	}

	public void setResourceGroup(String[] resourceGroup) {
		this.resourceGroup = resourceGroup;
	}

	public String getGlobalAttributeGroupName() {
		return globalAttributeGroupName;
	}

	public void setGlobalAttributeGroupName(String globalAttributeGroupName) {
		this.globalAttributeGroupName = globalAttributeGroupName;
	}

	public String getSkuPageURL() {
		return skuPageURL;
	}

	public void setSkuPageURL(String skuPageURL) {
		this.skuPageURL = skuPageURL;
	}

	public String getSearchResultsURL() {
		return searchResultsURL;
	}

	public void setSearchResultsURL(String searchResultsURL) {
		this.searchResultsURL = searchResultsURL;
	}

	public String getCrossRefSearchResultsURL() {
		return crossRefSearchResultsURL;
	}

	public void setCrossRefSearchResultsURL(String crossRefSearchResultsURL) {
		this.crossRefSearchResultsURL = crossRefSearchResultsURL;
	}

	public List<String> getSiteSearchFacetGroups() {
		return siteSearchFacetGroups;
	}

	public void setSiteSearchFacetGroups(List<String> siteSearchFacetGroups) {
		this.siteSearchFacetGroups = siteSearchFacetGroups;
	}
	
	public String getDisableDatasheet() {
		return disableDatasheet;
	}

	public void setDisableDatasheet(String disableDatasheet) {
		this.disableDatasheet = disableDatasheet;
	}

	public String getEnableProductsTab() {
		return enableProductsTab;
	}

	public void setEnableProductsTab(String enableProductsTab) {
		this.enableProductsTab = enableProductsTab;
	}

	public String getEnableNewsTab() {
		return enableNewsTab;
	}

	public void setEnableNewsTab(String enableNewsTab) {
		this.enableNewsTab = enableNewsTab;
	}

	public String getEnableResourcesTab() {
		return enableResourcesTab;
	}

	public void setEnableResourcesTab(String enableResourcesTab) {
		this.enableResourcesTab = enableResourcesTab;
	}

	public String getEnableServicesTab() {
		return enableServicesTab;
	}

	public void setEnableServicesTab(String enableServicesTab) {
		this.enableServicesTab = enableServicesTab;
	}
	
	public String getUnitedStatesDateFormat() {
		return unitedStatesDateFormat;
	}

	public void setUnitedStatesDateFormat(String unitedStatesDateFormat) {
		this.unitedStatesDateFormat = unitedStatesDateFormat;
	}

	public String getDefaultCategoryWithCardsSortOrder() {
		return defaultCategoryWithCardsSortOrder;
	}

	public void setDefaultCategorywithCardsSortOrder(String defaultCategoryWithCardsSortOrder) {
		this.defaultCategoryWithCardsSortOrder = defaultCategoryWithCardsSortOrder;
	}

	public List<SearchSortingOptionsBean> getSearchSortingOptionList() {
		return searchSortingOptionList;
	}

	public void setSearchSortingOptionList(List<SearchSortingOptionsBean> searchSortingOptionList) {
		this.searchSortingOptionList = searchSortingOptionList;
	}

	public String getDefaultSortOrder() {
		return defaultSortOrder;
	}

	@PostConstruct
	private void init() {
		updateDocumentGroupWithAEMTag();
		updateSupportCountryList();
		updateResourceCategoryList();
		updateGlobalAttributeList();
		updateSiteSearchFacetGroup();
		updateDocumentGroupBeanList();
		updateFacetGroupBeanList();

	}

	private void updateFacetGroupBeanList() {
		if (null != siteSearchFacetGroup) {
			for (int i = 0; i < siteSearchFacetGroup.length; i++) {
				JSONParser parser = new JSONParser();
				try {
					org.json.simple.JSONObject JsonObject = (org.json.simple.JSONObject) parser
							.parse(siteSearchFacetGroup[i]);
					if (null != JsonObject) {
						final String facetGroup = JsonObject.getOrDefault(
								SearchResultsRenderModel.FACET_SITE_SEARCH_FACET_GROUPS, StringUtils.EMPTY).toString();
						final FacetGroupBean facetGroupBean = new FacetGroupBean();
						facetGroupBean.setFacetGroupId(facetGroup);
						facetGroupBean.setGridFacet((Boolean) JsonObject
								.getOrDefault(facetedNavigationHelperV2.FACET_GROUP_SHOW_AS_GRID, false));
						facetGroupBean.setFacetSearchEnabled((Boolean) JsonObject
								.getOrDefault(facetedNavigationHelperV2.FACET_GROUP_FACET_SEARCH_ENABLED, false));
						facetGroupBean.setSingleFacetEnabled((Boolean) JsonObject
								.getOrDefault(facetedNavigationHelperV2.SINGLE_FACET_ENABLED, false));
						facetGroupsList.add(facetGroupBean);
					}
				} catch (ParseException e) {
					LOGGER.error("Exception occured during paring json string to json object: {} ", e);
				}
			}
		}
	}

	private void updateDocumentGroupBeanList() {
		final List<String> documentGroupStringArray = Arrays.asList(documentGroupArr);
		documentGroupStringArray.forEach(resourceGroupString -> {
			JSONParser parser = new JSONParser();
			try {
				org.json.simple.JSONObject JsonObject = (org.json.simple.JSONObject) parser.parse(resourceGroupString);
				DocumentGroupBean documentGroupBean = new DocumentGroupBean();
				documentGroupBean.setGroupName(JsonObject.get(GROUP_NAME).toString());
				documentGroupBean.setAnchorId(JsonObject.get(ANCHOR_ID).toString());
				org.json.simple.JSONArray aemTags = (org.json.simple.JSONArray) JsonObject.get(AEM_TAGS);
				if (!aemTags.isEmpty()) {
					String[] tagsArray = new String[aemTags.size()];
					for (int j = 0; j < aemTags.size(); j++) {
						tagsArray[j] = aemTags.get(j).toString();
					}

					documentGroupBean.setAemtags(tagsArray);
				}
				documentGroup.add(documentGroupBean);
			} catch (ParseException e) {
				LOGGER.error("Exception occured during paring json string to json object: {} ", e);
			}
		});
	}

	private void updateSiteSearchFacetGroup() {
		final List<String> siteSearchFacetGroupArray = Arrays.asList(siteSearchFacetGroup);
		final JSONParser parser = new JSONParser();
		siteSearchFacetGroupArray.forEach(siteSearchFacetGroup -> {
			try {
				org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) parser.parse(siteSearchFacetGroup);
				String facetGroup = jsonObject.get("siteSearchFacetGroups").toString();
				siteSearchFacetGroups.add(facetGroup);
			} catch (ParseException e) {
				LOGGER.error("Parse Exception while updating site search facet group", e);
			}
		});
	}

	private void updateGlobalAttributeList() {
		if (null != globalAttributeList) {
			final List<String> globalAttributeStringArray = Arrays.asList(globalAttributeList);
			globalAttributeStringArray.forEach(globalAttribute -> {
				final JSONParser parser = new JSONParser();
				org.json.simple.JSONObject JsonObject = null;
				try {
					JsonObject = (org.json.simple.JSONObject) parser.parse(globalAttribute);
					final GlobalAttrGroupBean globalAttrGroupBean = new GlobalAttrGroupBean();
					globalAttrGroupBean.setAttrValue(JsonObject.get("attributevalue").toString());
					globalAttributeArrayList.add(globalAttrGroupBean);
				} catch (ParseException e) {
					LOGGER.error("Parse Exception while updating global attribute list", e);
				}
			});
		}
	}

	private void updateResourceCategoryList() {
		if (null != resourceGroup) {
			final List<String> resourceGroupStringArray = Arrays.asList(resourceGroup);
			resourceGroupStringArray.forEach(resourceGroupString -> {
				String[] resourceGroupEntryArray = resourceGroupString.split("\\|");
				ResourceCategory resourceCategory = new ResourceCategory();
				if ((resourceGroupEntryArray[0] != null) && (!resourceGroupEntryArray[0].equals(""))) {
					resourceCategory.setResourceCategoryID(resourceGroupEntryArray[0]);
					if ((resourceGroupEntryArray[1] != null) && (!resourceGroupEntryArray[1].equals(""))) {
						String pdhGroudIDs = resourceGroupEntryArray[1];
						if (pdhGroudIDs != null) {
							String[] pdhGroudIDArray = pdhGroudIDs.split(",");
							if (pdhGroudIDArray != null) {
								ArrayList<String> PDHGroupIDs = new ArrayList<String>();
								for (int h = 0; h < pdhGroudIDArray.length; h++) {
									PDHGroupIDs.add(pdhGroudIDArray[h]);
								}
								resourceCategory.setPDHGroupIDs(PDHGroupIDs);
							}
						}
					}
				}
				resourceCategoryList.add(resourceCategory);
			});
		}

	}

	private void updateDocumentGroupWithAEMTag() {
		try {
			final List<DocumentGroupWithAemTagsModel> documentGroupWithAemTagsModelsList = new ArrayList<>();
			for (String jsonStr : documentGroupArr) {
				DocumentGroupWithAemTagsModel documentGroupWithAemTagsModel = new DocumentGroupWithAemTagsModel();
				JSONObject jsonObject = new JSONObject(jsonStr);
				if (jsonObject.has(AEM_TAGS) && jsonObject.getJSONArray(AEM_TAGS).length() != 0) {
					String aemtags = new JSONArray(jsonObject.get(AEM_TAGS).toString()).getString(0);
					documentGroupWithAemTagsModel.setAemtags(aemtags);
				}
				documentGroupWithAemTagsModel.setAnchorId(
						jsonObject.has(ANCHOR_ID) ? String.valueOf(jsonObject.get(ANCHOR_ID)) : StringUtils.EMPTY);
				documentGroupWithAemTagsModel.setGroupName(
						jsonObject.has(GROUP_NAME) ? String.valueOf(jsonObject.get(GROUP_NAME)) : StringUtils.EMPTY);
				documentGroupWithAemTagsModel.setGroupDescription(
						jsonObject.has(GROUP_DESCRIPTION) ? String.valueOf(jsonObject.get(GROUP_DESCRIPTION))
								: StringUtils.EMPTY);
				documentGroupWithAemTagsModelsList.add(documentGroupWithAemTagsModel);
			}
		} catch (JSONException e) {
			LOGGER.error("JSON exception while update Document Group with Tags", e);
		}
	}

	private void updateSupportCountryList() {
		final List<String> suppCountryResourceArray = Arrays.asList(suppCountryResource);
		suppCountryResourceArray.forEach(suppCountryResourceItem -> {
			try {
				final JSONObject suppCountryJSON = new JSONObject(suppCountryResourceItem);
				final SupportCountryBean supportCountryBean = new SupportCountryBean();
				supportCountryBean.setText(suppCountryJSON.get("text").toString());
				supportCountryBean.setValue(suppCountryJSON.get("value").toString());
				suppCountryList.add(supportCountryBean);
			} catch (JSONException e) {
				LOGGER.error("JSONException constructing supportCountryList {}",e);
			}
		});
	}

}
