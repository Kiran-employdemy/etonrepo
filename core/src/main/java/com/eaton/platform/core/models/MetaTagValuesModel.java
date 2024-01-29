package com.eaton.platform.core.models;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class MetaTagValuesModel {

	private String dimId;
	private String dimLabel;
	private String dimValue;
	private String contentType;
	private String pageType;

	private String familyName;
	private String primaryImage;
	private String familyPageUrl;
	private String maeketingDesc;
	private String coreFeature;
	private String middleTabName;
	private String middleTabUrl;
	private String currentPageCountry;
	private String currentPageLanguage;
	private String pagePublishDate;
	private String authoredPageType;

	private String categoryId;
	private String categoryName;

	private String title;
	private String pageTitle;
	private String navigationTitle;
	private String ogTitle;
	private String twitterTitle;
	private String description;
	private String ogDesc;
	private String twitterDesc;
	private String ogUrl;
	private String twitterUrl;
	private String ogType;
	private String ogLocale;
	private String ogSitename;
	private String ogImage;
	private String twitterImage;
	private String twitterSitename;
	private String applicationId;
	private String metaRobotTags;
	private String seoScriptPath;

	private Boolean securePageTemplate;
	private String securePage;
	private String accountTypeTagNames;
	private String productCategoriesTagNames;
	private String applicationAccessTagNames;
	private String countryTagNames;
	private String ssoCookieDomain;
	private String productGridDescription;
	private String tierLevelTagNames;
	private String partnerProgrammeTypeTagNames;
	private String partnerProgrammeAndTierLevelTypeTagNames;
	private String uuid;
	public String getProductGridDescription() { return productGridDescription; }
	public void setProductGridDescription(String productGridDescription) { this.productGridDescription = productGridDescription; }

	public Boolean getSecurePageTemplate() {
		return securePageTemplate;
	}

	public void setSecurePageTemplate(Boolean securePageTemplate) {
		this.securePageTemplate = securePageTemplate;
	}

	public String getSecurePage() {
		return securePage;
	}

	public void setSecurePage(String securePage) {
		this.securePage = securePage;
	}

	public String getAccountTypeTagNames() {
		return accountTypeTagNames;
	}

	public void setAccountTypeTagNames(String accountTypeTagNames) {
		this.accountTypeTagNames = accountTypeTagNames;
	}

	public String getProductCategoriesTagNames() {
		return productCategoriesTagNames;
	}

	public void setProductCategoriesTagNames(String productCategoriesTagNames) {
		this.productCategoriesTagNames = productCategoriesTagNames;
	}

	public String getApplicationAccessTagNames() {
		return applicationAccessTagNames;
	}

	public void setApplicationAccessTagNames(String applicationAccessTagNames) {
		this.applicationAccessTagNames = applicationAccessTagNames;
	}

	public String getCountryTagNames() {
		return countryTagNames;
	}

	public void setCountryTagNames(String countryTagNames) {
		this.countryTagNames = countryTagNames;
	}

	public String getAuthoredPageType() {
		return authoredPageType;
	}
	public void setAuthoredPageType(String authoredPageType) {
		this.authoredPageType = authoredPageType;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getPageType() {
		return pageType;
	}
	public void setPageType(String pageType) {
		this.pageType = pageType;
	}
	public String getFamilyName() {
		return familyName;
	}
	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}
	public String getPrimaryImage() {
		return primaryImage;
	}
	public void setPrimaryImage(String primaryImage) {
		this.primaryImage = primaryImage;
	}
	public String getFamilyPageUrl() {
		return familyPageUrl;
	}
	public void setFamilyPageUrl(String familyPageUrl) {
		this.familyPageUrl = familyPageUrl;
	}
	public String getMaeketingDesc() {
		if((maeketingDesc!=null) && (StringUtils.isNotEmpty(maeketingDesc))){
			Document doc = Jsoup.parse(maeketingDesc);
			return doc.text();
		} else {
			return maeketingDesc ;
		}
	}
	public void setMaeketingDesc(String maeketingDesc) {
		this.maeketingDesc = maeketingDesc;
	}
	public String getCoreFeature() {
		return coreFeature;
	}
	public void setCoreFeature(String coreFeature) {
		this.coreFeature = coreFeature;
	}
	public String getMiddleTabName() {
		return middleTabName;
	}
	public void setMiddleTabName(String middleTabName) {
		this.middleTabName = middleTabName;
	}
	public String getMiddleTabUrl() {
		return middleTabUrl;
	}
	public void setMiddleTabUrl(String middleTabUrl) {
		this.middleTabUrl = middleTabUrl;
	}
	public String getCurrentPageCountry() {
		return currentPageCountry;
	}
	public void setCurrentPageCountry(String currentPageCountry) {
		this.currentPageCountry = currentPageCountry;
	}
	public String getCurrentPageLanguage() {
		return currentPageLanguage;
	}
	public void setCurrentPageLanguage(String currentPageLanguage) {
		this.currentPageLanguage = currentPageLanguage;
	}
	public String getPagePublishDate() {
		return pagePublishDate;
	}
	public void setPagePublishDate(String pagePublishDate) {
		this.pagePublishDate = pagePublishDate;
	}
	public String getDimId() {
		return dimId;
	}
	public void setDimId(String dimId) {
		this.dimId = dimId;
	}
	public String getDimLabel() {
		return dimLabel;
	}
	public void setDimLabel(String dimLabel) {
		this.dimLabel = dimLabel;
	}
	public String getDimValue() {
		return dimValue;
	}
	public void setDimValue(String dimValue) {
		this.dimValue = dimValue;
	}
	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	public String getPageTitle() {
		return pageTitle;
	}

	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}

	public String getNavigationTitle() {
		return navigationTitle;
	}

	public void setNavigationTitle(String navigationTitle) {
		this.navigationTitle = navigationTitle;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the ogTitle
	 */
	public String getOgTitle() {
		return ogTitle;
	}
	/**
	 * @param ogTitle the ogTitle to set
	 */
	public void setOgTitle(String ogTitle) {
		this.ogTitle = ogTitle;
	}
	/**
	 * @return the ogDesc
	 */
	public String getOgDesc() {
		return ogDesc;
	}
	/**
	 * @param ogDesc the ogDesc to set
	 */
	public void setOgDesc(String ogDesc) {
		this.ogDesc = ogDesc;
	}
	/**
	 * @return the twitterTitle
	 */
	public String getTwitterTitle() {
		return twitterTitle;
	}
	/**
	 * @param twitterTitle the twitterTitle to set
	 */
	public void setTwitterTitle(String twitterTitle) {
		this.twitterTitle = twitterTitle;
	}
	/**
	 * @return the twitterDesc
	 */
	public String getTwitterDesc() {
		return twitterDesc;
	}
	/**
	 * @param twitterDesc the twitterDesc to set
	 */
	public void setTwitterDesc(String twitterDesc) {
		this.twitterDesc = twitterDesc;
	}
	/**
	 * @return the ogUrl
	 */
	public String getOgUrl() {
		return ogUrl;
	}
	/**
	 * @param ogUrl the ogUrl to set
	 */
	public void setOgUrl(String ogUrl) {
		this.ogUrl = ogUrl;
	}
	/**
	 * @return the twitterUrl
	 */
	public String getTwitterUrl() {
		return twitterUrl;
	}
	/**
	 * @param twitterUrl the twitterUrl to set
	 */
	public void setTwitterUrl(String twitterUrl) {
		this.twitterUrl = twitterUrl;
	}
	/**
	 * @return the ogType
	 */
	public String getOgType() {
		return ogType;
	}
	/**
	 * @param ogType the ogType to set
	 */
	public void setOgType(String ogType) {
		this.ogType = ogType;
	}
	/**
	 * @return the ogLocale
	 */
	public String getOgLocale() {
		return ogLocale;
	}
	/**
	 * @param ogLocale the ogLocale to set
	 */
	public void setOgLocale(String ogLocale) {
		this.ogLocale = ogLocale;
	}
	/**
	 * @return the ogSitename
	 */
	public String getOgSitename() {
		return ogSitename;
	}
	/**
	 * @param ogSitename the ogSitename to set
	 */
	public void setOgSitename(String ogSitename) {
		this.ogSitename = ogSitename;
	}
	/**
	 * @return the ogImage
	 */
	public String getOgImage() {
		return ogImage;
	}
	/**
	 * @param ogImage the ogImage to set
	 */
	public void setOgImage(String ogImage) {
		this.ogImage = ogImage;
	}
	/**
	 * @return the twitterImage
	 */
	public String getTwitterImage() {
		return twitterImage;
	}
	/**
	 * @param twitterImage the twitterImage to set
	 */
	public void setTwitterImage(String twitterImage) {
		this.twitterImage = twitterImage;
	}
	/**
	 * @return the twitterSitename
	 */
	public String getTwitterSitename() {
		return twitterSitename;
	}
	/**
	 * @param twitterSitename the twitterSitename to set
	 */
	public void setTwitterSitename(String twitterSitename) {
		this.twitterSitename = twitterSitename;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getMetaRobotTags() {
		return metaRobotTags;
	}

	public void setMetaRobotTags(String metaRobotTags) {
		this.metaRobotTags = metaRobotTags;
	}

	public String getSeoScriptPath() {
		return seoScriptPath;
	}

	public void setSeoScriptPath(String seoScriptPath) {
		this.seoScriptPath = seoScriptPath;
	}

	public String getSsoCookieDomain() {
		return ssoCookieDomain;
	}
	public void setSsoCookieDomain(String ssoCookieDomain) {
		this.ssoCookieDomain = ssoCookieDomain;
	}

	public String getTierLevelTagNames() {
		return tierLevelTagNames;
	}

	public void setTierLevelTagNames(String tierLevelTagNames) {
		this.tierLevelTagNames = tierLevelTagNames;
	}

	public String getPartnerProgrammeTypeTagNames() {
		return partnerProgrammeTypeTagNames;
	}

	public void setPartnerProgrammeTypeTagNames(String partnerProgrammeTypeTagNames) {
		this.partnerProgrammeTypeTagNames = partnerProgrammeTypeTagNames;
	}

	public String getPartnerProgrammeAndTierLevelTypeTagNames() {
		return partnerProgrammeAndTierLevelTypeTagNames;
	}

	public void setPartnerProgrammeAndTierLevelTypeTagNames(String partnerProgrammeAndTierLevelTypeTagNames) {
		this.partnerProgrammeAndTierLevelTypeTagNames = partnerProgrammeAndTierLevelTypeTagNames;
	}
	public String getUuid() { return uuid; }
	public void setUuid(String uuid) { this.uuid = uuid; }

}
