package com.eaton.platform.core.models;


import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import com.eaton.platform.core.models.eatonsiteconfig.EatonSiteConfigModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaton.platform.core.bean.ResourceCategory;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.search.SearchFacetGroupModel;
import com.google.common.base.Strings;

/**
 * The Class SiteConfigModel.
 */
@Model(adaptables =  Resource.class,defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SiteConfigModel {

	private static final Logger LOGGER = LoggerFactory.getLogger(SiteConfigModel.class);

	@Inject @Named(CommonConstants.JCR_TITLE)
	private String title;
	
	/** The global attribute group name. */
	@Inject
	private String globalAttributeGroupName;
	
	/** The resource group. */
	@Inject
	private String[] resourceGroup;
	
	/** The disableDatasheet. */
	@Inject @Default (values = "false")
	private String  disableDatasheet;
	
	/** The override PDH data. */
	@Inject
	private String  overridePDHData;
	
	/** The enable products tab. */
	@Inject
	private String enableProductsTab;
	
	/** The enable news tab. */
	@Inject
	private String enableNewsTab;
	
	/** The enable resources tab. */
	@Inject
	private String enableResourcesTab;
	
	/** The enable services tab. */
	@Inject
	private String enableServicesTab;
	
	/**
	 * The internationalStandardDate
	 */
	@Inject @Default (values = "false")
	private String unitedStatesDateFormat;
	
	/** The count corousel item. */
	@Inject
	private int  countCorouselItem;
	
	/** The facet value count. */
	@Inject
	private int  facetValueCount;
	
	/** The page size. */
	@Inject
	private int  pageSize;
	
	/** The no of days. */
	@Inject
	private int  noOfDays;
	
	/** The expanded facet count. */
	@Inject
	private int  expandedFacetCount;
	
	/** The facet count. */
	@Inject
	private int  facetCount;
	
	/** The count images. */
	@Inject
	private int  countImages;
	
	/** The count up sell. */
	@Inject
	private int  countUpSell;
	
	/** The default sort order. */
	@Inject
	private String  defaultSortOrder;
	
	/** The global attribute list. */
	@Inject
	private Resource globalAttributeList;	
	
	/** The supp country list. */
	@Inject
	private Resource suppCountryList;
	
	/** The document group. */
	@Inject
	private Resource documentGroup;
	
	/** The sku fall back image. */
	@Inject
	private String skuFallBackImage;
	
	/** The default link CTA. */
	@Inject
	private String defaultLinkCTA;
	
	/** The default link HTB. */
	@Inject
	private String defaultLinkHTB;
			
	/** The pf overview title. */
	@Inject
	private String pfOverviewTitle;
	
	/** The pf overview desc. */
	@Inject
	private String pfOverviewDesc;
	
	/** The pf models title. */
	@Inject
	private String pfModelsTitle;
	
	/** The pf resources title. */
	@Inject
	private String pfResourcesTitle;
	
	/** The sku overview title. */
	@Inject
	private String skuOverviewTitle;
	
	/** The sku overview desc. */
	@Inject
	private String skuOverviewDesc;
	
	/** The sku specifications title. */
	@Inject
	private String skuSpecificationsTitle;
	
	/** The sku resources title. */
	@Inject
	private String skuResourcesTitle;
	
	/** The sku page URL. */
	@Inject
	private String skuPageURL;

	/** The shopping cart URL. */
	@Inject
	private String cartUrl;
	
	/** The search results URL. */
	@Inject
	private String searchResultsURL;
	
	/** The cross reference search results URL. */
	@Inject
	private String crossRefSearchResultsURL;
	
	/** The site search facet group. */
	@Inject
	private Resource siteSearchFacetGroup;

	@Inject
	private String dirType;

	/** The default icon for content hub **/
	@Inject
	@Default(values=CommonConstants.BLANK_SPACE)
	private String contenthubDefaultIcon;
	
	@Inject @Default(values="/apps/eaton/settings/wcm/designs/favicon.ico")
	private String faviconIcon;

	/** The bulk download Package size. */
	@Inject@Default(intValues=10485760)
	private String  bulkDownloadPackageSize;

	/** The bulk downloaded cache duration. */
	@Inject@Default(intValues=120)
	private int  bulkDownloadCacheDuration;

	/** The bulk downloaded cache duration. */
	@Inject@Default(values ="Eaton")
	private String companyName;
	
	/** The path to the seo script. */
	@Inject @Default(values= StringUtils.EMPTY)
	private String seoScriptPath;
	
	/** The site verification code list */
	@Inject
	private String[] siteVerificationCodeList;

	/** The global attribute list size. */
	private int globalAttributeListSize;
	
	/** The resource category list size. */
	private int resourceCategoryListSize;
	
	/** The document type list size. */
	private int documentTypeListSize;
	
	/** The supprot info country list size. */
	private int supprotInfoCountryListSize;
	
	/** The document group with aem tags list size. */
	private int documentGroupWithAemTagsListSize;
	
	/** The site search facet groups list size. */
	private int siteSearchFacetGroupsListSize;

	/** The site verification code list size */
	private int siteVerificationCodeModelListSize;
	
	/** The global attributes list. */
	private List <GlobalAttributeModel> globalAttributesList;	
	
	/** The support info contries list. */
	private List <SupportInfoCountryModel> supportInfoContriesList;
	
	/** The document group with aem tags list. */
	private List<DocumentGroupWithAemTagsModel> documentGroupWithAemTagsList;
	
	/** The resource category list. */
	private List<ResourceCategory> resourceCategoryList;
	
	/** The site search facet groups. */
	private List<SearchFacetGroupModel> siteSearchFacetGroups;

	/** The site verification code list */
	private List<SiteVerificationCodeModel> siteVerificationCodeModelList;

	@Inject
	private List<String> whiteListForNsRoles;




	@PostConstruct
	private void init() {
		LOGGER.debug("Start of SiteConfigModel - init() .");

	}

	public List<String> getWhiteListForNsRoles() {
		return whiteListForNsRoles;
	}



	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Gets the global attribute list size.
	 *
	 * @return the global attribute list size
	 */
	public int getGlobalAttributeListSize() {
		return globalAttributeListSize;
	}

	/**
	 * Sets the global attribute list size.
	 *
	 * @param globalAttributeListSize the new global attribute list size
	 */
	public void setGlobalAttributeListSize(int globalAttributeListSize) {
		this.globalAttributeListSize = globalAttributeListSize;
	}

	/**
	 * Gets the resource category list size.
	 *
	 * @return the resource category list size
	 */
	public int getResourceCategoryListSize() {
		return resourceCategoryListSize;
	}

	/**
	 * Sets the resource category list size.
	 *
	 * @param resourceCategoryListSize the new resource category list size
	 */
	public void setResourceCategoryListSize(int resourceCategoryListSize) {
		this.resourceCategoryListSize = resourceCategoryListSize;
	}

	/**
	 * Gets the document type list size.
	 *
	 * @return the document type list size
	 */
	public int getDocumentTypeListSize() {
		return documentTypeListSize;
	}

	/**
	 * Sets the document type list size.
	 *
	 * @param documentTypeListSize the new document type list size
	 */
	public void setDocumentTypeListSize(int documentTypeListSize) {
		this.documentTypeListSize = documentTypeListSize;
	}

	/**
	 * Gets the supprot info country list size.
	 *
	 * @return the supprot info country list size
	 */
	public int getSupprotInfoCountryListSize() {
		return supprotInfoCountryListSize;
	}

	/**
	 * Sets the supprot info country list size.
	 *
	 * @param supprotInfoCountryListSize the new supprot info country list size
	 */
	public void setSupprotInfoCountryListSize(int supprotInfoCountryListSize) {
		this.supprotInfoCountryListSize = supprotInfoCountryListSize;
	}

	

	/**
	 * Gets the site search facet groups list size.
	 *
	 * @return the site search facet groups list size
	 */
	public int getSiteSearchFacetGroupsListSize() {
		return siteSearchFacetGroupsListSize;
	}

	/**
	 * Sets the site search facet groups list size.
	 *
	 * @param siteSearchFacetGroupsListSize the new site search facet groups list size
	 */
	public void setSiteSearchFacetGroupsListSize(int siteSearchFacetGroupsListSize) {
		this.siteSearchFacetGroupsListSize = siteSearchFacetGroupsListSize;
	}

	/**
	 * Gets the support info contries list.
	 *
	 * @return the support info contries list
	 */
	public List<SupportInfoCountryModel> getSupportInfoContriesList() {
		return supportInfoContriesList;
	}

	/**
	 * Sets the support info contries list.
	 *
	 * @param supportInfoContriesList the new support info contries list
	 */
	public void setSupportInfoContriesList(List<SupportInfoCountryModel> supportInfoContriesList) {
		this.supportInfoContriesList = supportInfoContriesList;
	}

	

	/**
	 * Gets the global attributes list.
	 *
	 * @return the global attributes list
	 */
	public List<GlobalAttributeModel> getGlobalAttributesList() {
		return globalAttributesList;
	}

	/**
	 * Sets the global attributes list.
	 *
	 * @param globalAttributesList the new global attributes list
	 */
	public void setGlobalAttributesList(List<GlobalAttributeModel> globalAttributesList) {
		this.globalAttributesList = globalAttributesList;
	}

	

	/**
	 * Gets the override PDH data.
	 *
	 * @return the override PDH data
	 */
	public String getOverridePDHData() {
		return overridePDHData;
	}

	/**
	 * Sets the override PDH data.
	 *
	 * @param overridePDHData the new override PDH data
	 */
	public void setOverridePDHData(String overridePDHData) {
		this.overridePDHData = overridePDHData;
	}

	/**
	 * Gets the count corousel item.
	 *
	 * @return the count corousel item
	 */
	public int getCountCorouselItem() {
		return countCorouselItem;
	}

	/**
	 * Sets the count corousel item.
	 *
	 * @param countCorouselItem the new count corousel item
	 */
	public void setCountCorouselItem(int countCorouselItem) {
		this.countCorouselItem = countCorouselItem;
	}

	/**
	 * Gets the facet value count.
	 *
	 * @return the facet value count
	 */
	public int getFacetValueCount() {
		return facetValueCount;
	}

	/**
	 * Sets the facet value count.
	 *
	 * @param facetValueCount the new facet value count
	 */
	public void setFacetValueCount(int facetValueCount) {
		this.facetValueCount = facetValueCount;
	}

	/**
	 * Gets the no of days.
	 *
	 * @return the noOfDays
	 */
	public int getNoOfDays() {
		return noOfDays;
	}

	/**
	 * Sets the no of days.
	 *
	 * @param noOfDays the noOfDays to set
	 */
	public void setNoOfDays(int noOfDays) {
		this.noOfDays = noOfDays;
	}

	/**
	 * Gets the page size.
	 *
	 * @return the page size
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * Sets the page size.
	 *
	 * @param pageSize the new page size
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * Gets the expanded facet count.
	 *
	 * @return the expanded facet count
	 */
	public int getExpandedFacetCount() {
		return expandedFacetCount;
	}

	/**
	 * Sets the expanded facet count.
	 *
	 * @param expandedFacetCount the new expanded facet count
	 */
	public void setExpandedFacetCount(int expandedFacetCount) {
		this.expandedFacetCount = expandedFacetCount;
	}

	/**
	 * Gets the facet count.
	 *
	 * @return the facet count
	 */
	public int getFacetCount() {
		return facetCount;
	}

	/**
	 * Sets the facet count.
	 *
	 * @param facetCount the new facet count
	 */
	public void setFacetCount(int facetCount) {
		this.facetCount = facetCount;
	}

	/**
	 * Gets the count images.
	 *
	 * @return the count images
	 */
	public int getCountImages() {
		return countImages;
	}

	/**
	 * Sets the count images.
	 *
	 * @param countImages the new count images
	 */
	public void setCountImages(int countImages) {
		this.countImages = countImages;
	}

	/**
	 * Gets the count up sell.
	 *
	 * @return the count up sell
	 */
	public int getCountUpSell() {
		return countUpSell;
	}

	/**
	 * Sets the count up sell.
	 *
	 * @param countUpSell the new count up sell
	 */
	public void setCountUpSell(int countUpSell) {
		this.countUpSell = countUpSell;
	}

	/**
	 * Gets the default sort order.
	 *
	 * @return the default sort order
	 */
	public String getDefaultSortOrder() {
		return defaultSortOrder;
	}

	/**
	 * Sets the default sort order.
	 *
	 * @param defaultSortOrder the new default sort order
	 */
	public void setDefaultSortOrder(String defaultSortOrder) {
		this.defaultSortOrder = defaultSortOrder;
	}

	/**
	 * Gets the global attribute list.
	 *
	 * @return the global attribute list
	 */
	public Resource getGlobalAttributeList() {
		
		return globalAttributeList;
	}

	/**
	 * Sets the global attribute list.
	 *
	 * @param globalAttributeList the new global attribute list
	 */
	public void setGlobalAttributeList(Resource globalAttributeList) {
		this.globalAttributeList = globalAttributeList;
	}

	
	/**
	 * Gets the supp country list.
	 *
	 * @return the supp country list
	 */
	public Resource getSuppCountryList() {
		return suppCountryList;
	}

	/**
	 * Sets the supp country list.
	 *
	 * @param suppCountryList the new supp country list
	 */
	public void setSuppCountryList(Resource suppCountryList) {
		this.suppCountryList = suppCountryList;
	}

	/**
	 * Gets the sku fall back image.
	 *
	 * @return the sku fall back image
	 */
	public String getSkuFallBackImage() {
		return skuFallBackImage;
	}

	/**
	 * Sets the sku fall back image.
	 *
	 * @param skuFallBackImage the new sku fall back image
	 */
	public void setSkuFallBackImage(String skuFallBackImage) {
		this.skuFallBackImage = skuFallBackImage;
	}

	/**
	 * Gets the default link CTA.
	 *
	 * @return the default link CTA
	 */
	public String getDefaultLinkCTA() {
		return defaultLinkCTA;
	}

	/**
	 * Sets the default link CTA.
	 *
	 * @param defaultLinkCTA the new default link CTA
	 */
	public void setDefaultLinkCTA(String defaultLinkCTA) {
		this.defaultLinkCTA = defaultLinkCTA;
	}

	/**
	 * Gets the default link HTB.
	 *
	 * @return the default link HTB
	 */
	public String getDefaultLinkHTB() {
		return defaultLinkHTB;
	}

	/**
	 * Sets the default link HTB.
	 *
	 * @param defaultLinkHTB the new default link HTB
	 */
	public void setDefaultLinkHTB(String defaultLinkHTB) {
		this.defaultLinkHTB = defaultLinkHTB;
	}

	/**
	 * Gets the pf overview title.
	 *
	 * @return the pf overview title
	 */
	public String getPfOverviewTitle() {
		return pfOverviewTitle;
	}

	/**
	 * Sets the pf overview title.
	 *
	 * @param pfOverviewTitle the new pf overview title
	 */
	public void setPfOverviewTitle(String pfOverviewTitle) {
		this.pfOverviewTitle = pfOverviewTitle;
	}

	/**
	 * Gets the pf overview desc.
	 *
	 * @return the pf overview desc
	 */
	public String getPfOverviewDesc() {
		return pfOverviewDesc;
	}

	/**
	 * Sets the pf overview desc.
	 *
	 * @param pfOverviewDesc the new pf overview desc
	 */
	public void setPfOverviewDesc(String pfOverviewDesc) {
		this.pfOverviewDesc = pfOverviewDesc;
	}

	/**
	 * Gets the pf models title.
	 *
	 * @return the pf models title
	 */
	public String getPfModelsTitle() {
		return pfModelsTitle;
	}

	/**
	 * Sets the pf models title.
	 *
	 * @param pfModelsTitle the new pf models title
	 */
	public void setPfModelsTitle(String pfModelsTitle) {
		this.pfModelsTitle = pfModelsTitle;
	}

	/**
	 * Gets the pf resources title.
	 *
	 * @return the pf resources title
	 */
	public String getPfResourcesTitle() {
		return pfResourcesTitle;
	}

	/**
	 * Sets the pf resources title.
	 *
	 * @param pfResourcesTitle the new pf resources title
	 */
	public void setPfResourcesTitle(String pfResourcesTitle) {
		this.pfResourcesTitle = pfResourcesTitle;
	}

	/**
	 * Gets the sku overview title.
	 *
	 * @return the sku overview title
	 */
	public String getSkuOverviewTitle() {
		return skuOverviewTitle;
	}

	/**
	 * Sets the sku overview title.
	 *
	 * @param skuOverviewTitle the new sku overview title
	 */
	public void setSkuOverviewTitle(String skuOverviewTitle) {
		this.skuOverviewTitle = skuOverviewTitle;
	}

	/**
	 * Gets the sku overview desc.
	 *
	 * @return the sku overview desc
	 */
	public String getSkuOverviewDesc() {
		return skuOverviewDesc;
	}

	/**
	 * Sets the sku overview desc.
	 *
	 * @param skuOverviewDesc the new sku overview desc
	 */
	public void setSkuOverviewDesc(String skuOverviewDesc) {
		this.skuOverviewDesc = skuOverviewDesc;
	}

	/**
	 * Gets the sku specifications title.
	 *
	 * @return the sku specifications title
	 */
	public String getSkuSpecificationsTitle() {
		return skuSpecificationsTitle;
	}

	/**
	 * Sets the sku specifications title.
	 *
	 * @param skuSpecificationsTitle the new sku specifications title
	 */
	public void setSkuSpecificationsTitle(String skuSpecificationsTitle) {
		this.skuSpecificationsTitle = skuSpecificationsTitle;
	}

	/**
	 * Gets the sku resources title.
	 *
	 * @return the sku resources title
	 */
	public String getSkuResourcesTitle() {
		return skuResourcesTitle;
	}

	/**
	 * Sets the sku resources title.
	 *
	 * @param skuResourcesTitle the new sku resources title
	 */
	public void setSkuResourcesTitle(String skuResourcesTitle) {
		this.skuResourcesTitle = skuResourcesTitle;
	}

	/**
	 * Gets the document group.
	 *
	 * @return the document group
	 */
	public Resource getDocumentGroup() {
		return documentGroup;
	}

	/**
	 * Sets the document group.
	 *
	 * @param documentGroup the new document group
	 */
	public void setDocumentGroup(Resource documentGroup) {
		this.documentGroup = documentGroup;
	}

	/**
	 * Gets the document group with aem tags list size.
	 *
	 * @return the document group with aem tags list size
	 */
	public int getDocumentGroupWithAemTagsListSize() {
		return documentGroupWithAemTagsListSize;
	}

	/**
	 * Sets the document group with aem tags list size.
	 *
	 * @param documentGroupWithAemTagsListSize the new document group with aem tags list size
	 */
	public void setDocumentGroupWithAemTagsListSize(int documentGroupWithAemTagsListSize) {
		this.documentGroupWithAemTagsListSize = documentGroupWithAemTagsListSize;
	}

	/**
	 * Gets the document group with aem tags list.
	 *
	 * @return the document group with aem tags list
	 */
	public List<DocumentGroupWithAemTagsModel> getDocumentGroupWithAemTagsList() {
		return documentGroupWithAemTagsList;
	}

	/**
	 * Sets the document group with aem tags list.
	 *
	 * @param documentGroupWithAemTagsList the new document group with aem tags list
	 */
	public void setDocumentGroupWithAemTagsList(List<DocumentGroupWithAemTagsModel> documentGroupWithAemTagsList) {
		this.documentGroupWithAemTagsList = documentGroupWithAemTagsList;
	}

	/**
	 * Gets the sku page URL.
	 *
	 * @return the skuPageURL
	 */
	public String getSkuPageURL() {
		return skuPageURL;
	}

	/**
	 * Sets the sku page URL.
	 *
	 * @param skuPageURL the skuPageURL to set
	 */
	public void setSkuPageURL(String skuPageURL) {
		this.skuPageURL = skuPageURL;
	}

	/**
	 * Gets the cartUrl page URL.
	 *
	 * @return the cartUrl
	 */
	public String getCartUrl() {
		return cartUrl;
	}

	/**
	 * Sets the cartUrl page URL.
	 *
	 * @param cartUrl the cartUrl to set
	 */
	public void setCartUrl(String cartUrl) {
		this.cartUrl = cartUrl;
	}

	/**
	 * Gets the search results URL.
	 *
	 * @return the searchResultsURL
	 */
	public String getSearchResultsURL() {
		return searchResultsURL;
	}

	/**
	 * Sets the search results URL.
	 *
	 * @param searchResultsURL the searchResultsURL to set
	 */
	public void setSearchResultsURL(String searchResultsURL) {
		this.searchResultsURL = searchResultsURL;
	}

	/**
	 * Gets the cross reference search results URL.
	 *
	 * @return the crossRefSearchResultsURL
	 */
	public String getCrossRefSearchResultsURL() {
		return crossRefSearchResultsURL;
	}

	/**
	 * Sets the cross reference search results URL.
	 *
	 * @param crossRefSearchResultsURL the crossRefSearchResultsURL to set
	 */
	public void setCrossRefSearchResultsURL(String crossRefSearchResultsURL) {
		this.crossRefSearchResultsURL = crossRefSearchResultsURL;
	}

	/**
	 * Gets the global attribute group name.
	 *
	 * @return the globalAttributeGroupName
	 */
	public String getGlobalAttributeGroupName() {
		return globalAttributeGroupName;
	}

	/**
	 * Sets the global attribute group name.
	 *
	 * @param globalAttributeGroupName the globalAttributeGroupName to set
	 */
	public void setGlobalAttributeGroupName(String globalAttributeGroupName) {
		this.globalAttributeGroupName = globalAttributeGroupName;
	}

	/**
	 * Gets the resource group.
	 *
	 * @return the resourceGroup
	 */
	public String[] getResourceGroup() {
		return resourceGroup;
	}

	/**
	 * Sets the resource group.
	 *
	 * @param resourceGroup the resourceGroup to set
	 */
	public void setResourceGroup(String[] resourceGroup) {
		this.resourceGroup = resourceGroup;
	}

	/**
	 * Gets the site search facet groups.
	 *
	 * @return the siteSearchFacetGroups
	 */
	public List<SearchFacetGroupModel> getSiteSearchFacetGroups() {
		return siteSearchFacetGroups;
	}

	/**
	 * Sets the site search facet groups.
	 *
	 * @param siteSearchFacetGroups the siteSearchFacetGroups to set
	 */
	public void setSiteSearchFacetGroups(List<SearchFacetGroupModel> siteSearchFacetGroups) {
		this.siteSearchFacetGroups = siteSearchFacetGroups;
	}
	
	/**
	 * Gets the disableDatasheet.
	 *
	 * @return the disableDatasheet
	 */
	public String getDisableDatasheet() {
		return disableDatasheet;
	}

	/**
	 * Sets the disableDatasheet.
	 *
	 * @param disableDatasheet to set
	 */
	public void setDisableDatasheet(String disableDatasheet) {
		this.disableDatasheet = disableDatasheet;
	}

	/**
	 * Gets the enable products tab.
	 *
	 * @return the enableProductsTab
	 */
	public String getEnableProductsTab() {
		return enableProductsTab;
	}

	/**
	 * Sets the enable products tab.
	 *
	 * @param enableProductsTab the enableProductsTab to set
	 */
	public void setEnableProductsTab(String enableProductsTab) {
		this.enableProductsTab = enableProductsTab;
	}

	/**
	 * Gets the enable news tab.
	 *
	 * @return the enableNewsTab
	 */
	public String getEnableNewsTab() {
		return enableNewsTab;
	}

	/**
	 * Sets the enable news tab.
	 *
	 * @param enableNewsTab the enableNewsTab to set
	 */
	public void setEnableNewsTab(String enableNewsTab) {
		this.enableNewsTab = enableNewsTab;
	}

	/**
	 * Gets the enable resources tab.
	 *
	 * @return the enableResourcesTab
	 */
	public String getEnableResourcesTab() {
		return enableResourcesTab;
	}

	/**
	 * Sets the enable resources tab.
	 *
	 * @param enableResourcesTab the enableResourcesTab to set
	 */
	public void setEnableResourcesTab(String enableResourcesTab) {
		this.enableResourcesTab = enableResourcesTab;
	}

	/**
	 * Gets the enable services tab.
	 *
	 * @return the enableServicesTab
	 */
	public String getEnableServicesTab() {
		return enableServicesTab;
	}

	/**
	 * Sets the enable services tab.
	 *
	 * @param enableServicesTab the enableServicesTab to set
	 */
	public void setEnableServicesTab(String enableServicesTab) {
		this.enableServicesTab = enableServicesTab;
	}
	
	/**
	 * @return unitedStatesDateFormat
	 */
	public String getUnitedStatesDateFormat() {
		return unitedStatesDateFormat;
	}

	/**
	 * @param unitedStatesDateFormat
	 */
	public void setUnitedStatesDateFormat(String unitedStatesDateFormat) {
		this.unitedStatesDateFormat = unitedStatesDateFormat;
	}

	/**
	 * Gets the resource category list.
	 *
	 * @return the resource category list
	 */
	public List<ResourceCategory> getResourceCategoryList() {
		return resourceCategoryList;
	}

	/**
	 * Sets the resource category list.
	 *
	 * @param resourceCategoryList the new resource category list
	 */
	public void setResourceCategoryList(List<ResourceCategory> resourceCategoryList) {
		this.resourceCategoryList = resourceCategoryList;
	}

	/**
	 * Gets the site search facet group.
	 *
	 * @return the siteSearchFacetGroup
	 */
	public Resource getSiteSearchFacetGroup() {
		return siteSearchFacetGroup;
	}

	/**
	 * Sets the site search facet group.
	 *
	 * @param siteSearchFacetGroup the siteSearchFacetGroup to set
	 */
	public void setSiteSearchFacetGroup(Resource siteSearchFacetGroup) {
		this.siteSearchFacetGroup = siteSearchFacetGroup;
	}

	public String getDirType() {
		return dirType;
	}

	public String getContenthubDefaultIcon() {
		return contenthubDefaultIcon;
	}
	
	public String getFavionIcon() {
		return faviconIcon; 
	}

	public long getBulkDownloadPackageSize() {
		long result = 0;
		if (!StringUtils.isBlank(bulkDownloadPackageSize)){
			result = Long.parseLong(bulkDownloadPackageSize);
		}
		return result;
	}

	public int getBulkDownloadCacheDuration() {
		return bulkDownloadCacheDuration;
	}

	public String getCompanyName() {
		return companyName;
	}
	
	public String getSeoScriptPath() {
		return seoScriptPath;
	}

	public String[] getSiteVerificationCodeList() {
		return siteVerificationCodeList;
	}

	public void setSiteVerificationCodeList(String[] siteVerificationCodeList) {
		this.siteVerificationCodeList = siteVerificationCodeList;
	}

	public int getSiteVerificationCodeModelListSize() {
		return siteVerificationCodeModelListSize;
	}

	public void setSiteVerificationCodeModelListSize(int siteVerificationCodeModelListSize) {
		this.siteVerificationCodeModelListSize = siteVerificationCodeModelListSize;
	}

	public List<SiteVerificationCodeModel> getSiteVerificationCodeModelList() {
		return siteVerificationCodeModelList;
	}

	public void setSiteVerificationCodeModelList(List<SiteVerificationCodeModel> siteVerificationCodeModelList) {
		this.siteVerificationCodeModelList = siteVerificationCodeModelList;
	}
}
