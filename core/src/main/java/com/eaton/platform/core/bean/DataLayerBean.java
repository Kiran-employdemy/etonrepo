package com.eaton.platform.core.bean;

import com.eaton.platform.core.constants.CommonConstants;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * <html> Description: This bean class used in DataLayerModel class to store content </html>
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
public class DataLayerBean implements Serializable
{
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2156149953659295832L;

	/** The country. */
	private String country;

	/** The login state. */
	private String loginState;

	/** The language. */
	private String language;

	/** The channel. */
	private String channel;

	/** The page type. */
	private String pageType;

	/** The search query. */
	private String searchQuery;

	/** The search results count. */
	private Integer searchResultsCount;

	/** The site search facets. */
	private String siteSearchFacets;

	/** The product family. */
	private String productFamily;

	/** The product category. */
	private String productCategory;

	/** The product sku. */
	private String productSku;

	/** The product name. */
	private String productName;

	/** The login id. */
	private String loginId;

	/** The product facets applied. */
	private String productFacetsApplied;

    /** The selector business units. */
    private String sectorBusiness;

    /** The domain applied. */
	private String domain;

	/** The video component usage. */
    @SerializedName(value = "video")
	private String videoComponent = CommonConstants.NO;

    /** The resource list component usage. */
    @SerializedName(value = "resources")
    private String resourceListComponent = CommonConstants.NO;

    /** The eloqua form component usage. */
    @SerializedName(value = "form")
    private String eloquaFormComponent = CommonConstants.NO;

    /** Percolate ID */
    private String percolateContentId;

	/** Page Intent */
    private String pageIntent;

    /** Topic Cluster */
    private String topicCluster;

	/**Visitor Account Type*/
    @SerializedName(value = "accountType")
	private String visitorAccountType;

	/**Visitor Product Categories*/
    @SerializedName(value = "productCategories")
	private String visitorProductCategories;

	/**Visitor Application Access */
    @SerializedName(value = "applicationAccess")
	private String visitorApplicationAccess;

	/**Visitor Approved Countries*/
    @SerializedName(value = "approvedCountries")
	private String visitorApprovedCountries;

	/**Visitor Partner program type*/
    @SerializedName(value = "partnerprogram")
	private String visitorPartnerprogram;

	/**Visitor Tier level*/
    @SerializedName(value = "tierlevel")
	private String visitorTierlevel;



	public String getVisitorAccountType() {
		return visitorAccountType;
	}

	public void setVisitorAccountType(String visitorAccountType) {
		this.visitorAccountType = visitorAccountType;
	}

	public String getVisitorProductCategories() {
		return visitorProductCategories;
	}

	public void setVisitorProductCategories(String visitorProductCategories) {
		this.visitorProductCategories = visitorProductCategories;
	}

	public String getVisitorApplicationAccess() {
		return visitorApplicationAccess;
	}

	public void setVisitorApplicationAccess(String visitorApplicationAccess) {
		this.visitorApplicationAccess = visitorApplicationAccess;
	}

	public String getVisitorApprovedCountries() {
		return visitorApprovedCountries;
	}

	public void setVisitorApprovedCountries(String visitorApprovedCountries) {
		this.visitorApprovedCountries = visitorApprovedCountries;
	}

	public String getVisitorPartnerprogram() {
		return visitorPartnerprogram;
	}

	public void setVisitorPartnerprogram(String visitorPartnerprogram) {
		this.visitorPartnerprogram = visitorPartnerprogram;
	}

	public String getVisitorTierlevel() {
		return visitorTierlevel;
	}

	public void setVisitorTierlevel(String visitorTierlevel) {
		this.visitorTierlevel = visitorTierlevel;
	}

	/**
	 * Gets the country.
	 *
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * Sets the country.
	 *
	 * @param country the new country
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * Gets the login state.
	 *
	 * @return the login state
	 */
	public String getLoginState() {
		return loginState;
	}

	/**
	 * Sets the login state.
	 *
	 * @param loginState the new login state
	 */
	public void setLoginState(String loginState) {
		this.loginState = loginState;
	}

	/**
	 * Gets the language.
	 *
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * Sets the language.
	 *
	 * @param language the new  language
	 */
	public void setLanguage(String language) {

		this.language = language;
	}

	/**
	 * Gets the channel.
	 *
	 * @return the channel
	 */
	public String getChannel() {
		return channel;
	}

	/**
	 * Sets the channel.
	 *
	 * @param channel the new channel
	 */
	public void setChannel(String channel) {
		this.channel = channel;
	}

	/**
	 * Gets the page type.
	 *
	 * @return the page type
	 */
	public String getPageType() {
		return pageType;
	}

	/**
	 * Sets the page type.
	 *
	 * @param pageType the new page type
	 */
	public void setPageType(String pageType) {
		this.pageType = pageType;
	}

	/**
	 * Gets the search query.
	 *
	 * @return the search query
	 */
	public String getSearchQuery() {
		return searchQuery;
	}

	/**
	 * Sets the search query.
	 *
	 * @param searchQuery the new search query
	 */
	public void setSearchQuery(String searchQuery) {
		this.searchQuery = searchQuery;
	}

	/**
	 * Gets the search results count.
	 *
	 * @return the search results count
	 */
	public Integer getSearchResultsCount() {
		return searchResultsCount;
	}

	/**
	 * Sets the search results count.
	 *
	 * @param searchResultsCount the new search results count
	 */
	public void setSearchResultsCount(Integer searchResultsCount) {
		this.searchResultsCount = searchResultsCount;
	}

	/**
	 * Gets the site search facets.
	 *
	 * @return the site search facets
	 */
	public String getSiteSearchFacets() {
		return siteSearchFacets;
	}

	/**
	 * Sets the site search facets.
	 *
	 * @param siteSearchFacets the new site search facets
	 */
	public void setSiteSearchFacets(String siteSearchFacets) {
		this.siteSearchFacets = siteSearchFacets;
	}

	/**
	 * Gets the product family.
	 *
	 * @return the product family
	 */
	public String getProductFamily() {
		return productFamily;
	}

	/**
	 * Sets the product family.
	 *
	 * @param productFamily the new product family
	 */
	public void setProductFamily(String productFamily) {
		this.productFamily = productFamily;
	}

	/**
	 * Gets the product category.
	 *
	 * @return the product category
	 */
	public String getProductCategory() {
		return productCategory;
	}

	/**
	 * Sets the product category.
	 *
	 * @param productCategory the new product category
	 */
	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}

	/**
	 * Gets the product sku.
	 *
	 * @return the product sku
	 */
	public String getProductSku() {
		return productSku;
	}

	/**
	 * Sets the product sku.
	 *
	 * @param productSku the new product sku
	 */
	public void setProductSku(String productSku) {
		this.productSku = productSku;
	}

	/**
	 * Gets the product name.
	 *
	 * @return the product name
	 */
	public String getProductName() {
		return productName;
	}

	/**
	 * Sets the product name.
	 *
	 * @param productName the new product name
	 */
	public void setProductName(String productName) {
		this.productName = productName;
	}

	/**
	 * Gets the login id.
	 *
	 * @return the login id
	 */
	public String getLoginId() {
		return loginId;
	}

	/**
	 * Sets the login id.
	 *
	 * @param loginId the new login id
	 */
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	/**
	 * Gets the product facets applied.
	 *
	 * @return the product facets applied
	 */
	public String getProductFacetsApplied() {
		return productFacetsApplied;
	}

	/**
	 * Sets the product facets applied.
	 *
	 * @param productFacetsApplied the new product facets applied
	 */
	public void setProductFacetsApplied(String productFacetsApplied) {
		this.productFacetsApplied = productFacetsApplied;
	}

    /**
     * Gets the sector business units
     * @return sector business units
     */
    public String getSectorBusiness() {
        return sectorBusiness;
    }

    /**
     * Sets the sector business units
     * @param sectorBusiness the sector business units
     */
    public void setSectorBusiness(final String sectorBusiness) {
        this.sectorBusiness = sectorBusiness;
    }

    /**
     * Gets the video component usage info as yes or no
     * @return the video component usage info as yes or no
     */
    public String getVideoComponent() {
        return videoComponent;
    }

    /**
     * Sets the video component usage info as yes or no
     * @param videoComponent the video component usage info as yes or no
     */
    public void setVideoComponent(final String videoComponent) {
        this.videoComponent = videoComponent;
    }

    /**
     * Gets the resource list components usage info as yes or no
     * @return the resource list components usage info as yes or no
     */
    public String getResourceListComponent() {
        return resourceListComponent;
    }

    /**
     * Sets the resource list components usage info as yes or no
     * @param resourceListComponent the resource list usage info as yes or no
     */
    public void setResourceListComponent(final String resourceListComponent) {
        this.resourceListComponent = resourceListComponent;
    }

    /**
     * Gets the eloqua form component usage info as yes or no
     * @return the eloqua form component usage info as yes or no
     */
    public String getEloquaFormComponent() {
        return eloquaFormComponent;
    }

    /**
     * Sets the eloqua form component usage info as yes or no
     * @param eloquaFormComponent the eloqua form component usage info as yes or no
     */
    public void setEloquaFormComponent(final String eloquaFormComponent) {
        this.eloquaFormComponent = eloquaFormComponent;
    }
    /** get Percolate Content ID*/
    public String getPercolateContentId() {
		return percolateContentId;
	}
    /** Set Percolate Content ID*/
	public void setPercolateContentId(String percolateContentId) {
		this.percolateContentId = percolateContentId;
	}

	/**
	 * Gets the page intent.
	 *
	 * @return the page intent
	 */
	public String getPageIntent() {
		return pageIntent;
	}

	/**
	 * Sets the page intent.
	 *
	 * @param pageIntent the new page intent
	 */
	public void setPageIntent(String pageIntent) {
		this.pageIntent = pageIntent;
	}

	/**
	 * Gets the topic Cluster.
	 *
	 * @return the topic Cluster
	 */
	public String getTopicCluster() {
		return topicCluster;
	}

	/**
	 * Sets the topic Cluster.
	 *
	 * @param topicCluster the new topic Cluster
	 */
	public void setTopicCluster(String topicCluster) {
		this.topicCluster = topicCluster;
	}

	/**
	 * Sets the domain.
	 *
	 * @param domain the new domain
	 */
    public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}
}
