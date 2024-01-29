package com.eaton.platform.core.bean;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.sling.api.resource.Resource;

import java.util.List;

public class ProductFamilyPIMDetails {

	private  String productName;
	private  String productFamilyAEMPath;
	private  Boolean showLongDescription;
	private  List<SecondaryLinksBean> secondaryLinksList;
	private  List<TopicLinkWithIconBean> topicLinkWithIconList;
	private  List<HowToBuyBean> howToBuyList;
	private  ProductSupportBean productSupportBean;

	private  String primarySubCategory;
	private  List<TaxynomyAttributeGroupBean> taxonomyAttributeGroupList;

	private  String  primaryCTALabel;
	private  String  primaryCTAURL;
	private  String	primaryCTANewWindow;
	private String primaryCTAEnableSourceTracking;
	private  List<String> pimTags;
	private String isSuffixDisabled;

	private String middleTabTitle;
	private  String primaryProductTaxonomy;

	private  Resource pimResource;

	/**
	 * @return the primaryProductTaxonomy
	 */
	public String getPrimaryProductTaxonomy() {
		return primaryProductTaxonomy;
	}
	/**
	 * @param primaryProductTaxonomy the primaryProductTaxonomy to set
	 */
	public void setPrimaryProductTaxonomy(String primaryProductTaxonomy) {
		this.primaryProductTaxonomy = primaryProductTaxonomy;
	}

	/**
	 * @return the showLongDescription
	 */
	public Boolean getShowLongDescription() {
		return showLongDescription;
	}
	/**
	 * @param showLongDescription the showLongDescription to set
	 */
	public void setShowLongDescription(Boolean showLongDescription) {
		this.showLongDescription = showLongDescription;
	}
	/**
	 * @return the pimTags
	 */
	public List<String> getPimTags() {
		return pimTags;
	}
	/**
	 * @param pimTags the pimTags to set
	 */
	public void setPimTags(List<String> pimTags) {
		this.pimTags = pimTags;
	}
	/**
	 * @return the productName
	 */
	public String getProductName() {
		return productName;
	}
	/**
	 * @param productName the productName to set
	 */
	public void setProductName(String productName) {
		this.productName = productName;
	}
	/**
	 * @return the productFamilyAEMPath
	 */
	public String getProductFamilyAEMPath() {
		return productFamilyAEMPath;
	}
	/**
	 * @param productFamilyAEMPath the productFamilyAEMPath to set
	 */
	public void setProductFamilyAEMPath(String productFamilyAEMPath) {
		this.productFamilyAEMPath = productFamilyAEMPath;
	}
	/**
	 * @return the secondaryLinksList
	 */
	public List<SecondaryLinksBean> getSecondaryLinksList() {
		return secondaryLinksList;
	}
	/**
	 * @param secondaryLinksList the secondaryLinksList to set
	 */
	public void setSecondaryLinksList(List<SecondaryLinksBean> secondaryLinksList) {
		this.secondaryLinksList = secondaryLinksList;
	}
	/**
	 * @return the topicLinkWithIconList
	 */
	public List<TopicLinkWithIconBean> getTopicLinkWithIconList() {
		return topicLinkWithIconList;
	}
	/**
	 * @param topicLinkWithIconList the topicLinkWithIconList to set
	 */
	public void setTopicLinkWithIconList(List<TopicLinkWithIconBean> topicLinkWithIconList) {
		this.topicLinkWithIconList = topicLinkWithIconList;
	}
	/**
	 * @return the howToBuyList
	 */
	public List<HowToBuyBean> getHowToBuyList() {
		return howToBuyList;
	}
	/**
	 * @param howToBuyList the howToBuyList to set
	 */
	public void setHowToBuyList(List<HowToBuyBean> howToBuyList) {
		this.howToBuyList = howToBuyList;
	}

	/**
	 * @return the productSupportBean
	 */
	public ProductSupportBean getProductSupportBean() {
		return productSupportBean;
	}
	/**
	 * @param productSupportBean the productSupportBean to set
	 */
	public void setProductSupportBean(ProductSupportBean productSupportBean) {
		this.productSupportBean = productSupportBean;
	}
	/**
	 * @return the primarySubCategory
	 */
	public String getPrimarySubCategory() {
		return primarySubCategory;
	}
	/**
	 * @param primarySubCategory the primarySubCategory to set
	 */
	public void setPrimarySubCategory(String primarySubCategory) {
		this.primarySubCategory = primarySubCategory;
	}
	/**
	 * @return the taxynomyAttributeGroupList
	 */
	public List<TaxynomyAttributeGroupBean> getTaxonomyAttributeGroupList() {
		return taxonomyAttributeGroupList;
	}
	/**
	 * @param taxonomyAttributeGroupList the taxynomyAttributeGroupList to set
	 */
	public void setTaxonomyAttributeGroupList(List<TaxynomyAttributeGroupBean> taxonomyAttributeGroupList) {
		this.taxonomyAttributeGroupList = taxonomyAttributeGroupList;
	}
	/**
	 * @return the primaryCTALabel
	 */
	public String getPrimaryCTALabel() {
		return primaryCTALabel;
	}
	/**
	 * @param primaryCTALabel the primaryCTALabel to set
	 */
	public void setPrimaryCTALabel(String primaryCTALabel) {
		this.primaryCTALabel = primaryCTALabel;
	}
	/**
	 * @return the primaryCTAURL
	 */
	public String getPrimaryCTAURL() {
		return primaryCTAURL;
	}
	/**
	 * @param primaryCTAURL the primaryCTAURL to set
	 */
	public void setPrimaryCTAURL(String primaryCTAURL) {
		this.primaryCTAURL = primaryCTAURL;
	}
	/**
	 * @return the primaryCTANewWindow
	 */
	public String getPrimaryCTANewWindow() {
		return primaryCTANewWindow;
	}
	/**
	 * @param primaryCTANewWindow the primaryCTANewWindow to set
	 */
	public void setPrimaryCTANewWindow(String primaryCTANewWindow) {
		this.primaryCTANewWindow = primaryCTANewWindow;
	}

	public String getPrimaryCTAEnableSourceTracking() {
		return primaryCTAEnableSourceTracking;
	}

	public void setPrimaryCTAEnableSourceTracking(String primaryCTAEnableSourceTracking) {
		this.primaryCTAEnableSourceTracking = primaryCTAEnableSourceTracking;
	}

	@Override
	 public String toString() {
		Gson gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
			@Override
			public boolean shouldSkipField(FieldAttributes fieldAttributes) {
				return false;
			}

			@Override
			public boolean shouldSkipClass(Class<?> aClass) {
				return ( aClass.equals(Resource.class));
			}
		}).create();
		return gson.toJson(this);
     }

	public String getIsSuffixDisabled() {
		return this.isSuffixDisabled;
	}

	public void setIsSuffixDisabled(String isSuffixDisabled) {
		this.isSuffixDisabled = isSuffixDisabled;
	}

	public String getMiddleTabTitle() {
		return middleTabTitle;
	}

	public void setMiddleTabTitle(String middleTabTitle) {
		this.middleTabTitle = middleTabTitle;
	}

	public Resource getPimResource() {
		return pimResource;
	}

	public void setPimResource(Resource pimResource) {
		this.pimResource = pimResource;
	}
}
