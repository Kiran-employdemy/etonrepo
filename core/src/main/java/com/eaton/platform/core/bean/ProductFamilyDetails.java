package com.eaton.platform.core.bean;

import com.eaton.platform.core.bean.builders.product.BeanFiller;
import com.eaton.platform.core.bean.builders.product.ProductFamilyDetailBuilder;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.PIMResourceSlingModel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;

/**
 * The Class ProductFamilyDetails.
 *
 */
public class ProductFamilyDetails {

	private static final Logger LOG = LoggerFactory.getLogger(ProductFamilyDetails.class);

	private String inventoryID;
	private String extensionID;
	private String primarySubCategory;
	private String productFamilyName;
	private String modelTabName;
	private List<HowToBuyBean> howToBuyList;
	private String marketingDesc;
	private String primaryImage;
	private String secondaryImage;
	private String altPrimaryImage;
	private String primaryCTALabel;
	private String primaryCTAURL;
	private String primaryCTANewWindow;
	private String primaryCTAEnableSourceTracking;
	private List<SecondaryLinksBean> secondaryLinksList;
	private List<TopicLinkWithIconBean> topicLinkWithIconList;
	private Boolean showLongDescription;
	private List<String> skuCardAttributeList;
	private String metaTitleOverviewTab;
	private String metaDescOverviewTab;
	private String metaTitleResourcesTab;
	private String metaTitleModelsTab;
	private String metaDescriptionResourcesTab;
	private String metaDescriptionModelsTab;
	private ProductSupportBean productSupportBean;
	private String coreFeatureTitle;
	private String coreFeatureDescription;
	private String coreFeatureImage;
	private List<String> pimTags;
	private String primaryProductTaxonomy;
	private String altText;
	private String pdhRecordPath;
	private String pdhPrimaryImg;
	private String productType;
	private String productFamilyAEMPath;
	private String isSuffixDisabled;
	private List<PIMResourceSlingModel> multiPims;
	private String spinImages;
	private Boolean primaryImageIsRepresentative;
	private String repfl;

	public String getPrimaryProductTaxonomy() {
		return primaryProductTaxonomy;
	}

	public void setPrimaryProductTaxonomy(String primaryProductTaxonomy) {
		this.primaryProductTaxonomy = primaryProductTaxonomy;
	}

	public String getSpinImages() {
		return spinImages;
	}

	public void setSpinImages(String spinImages) {
		this.spinImages = spinImages;
	}
	private String pdhSpinnerImage;

	public List<PIMResourceSlingModel> getMultiPims() {
		return multiPims;
	}

	public void setMultiPims(List<PIMResourceSlingModel> multiPims) {
		this.multiPims = multiPims;
	}

	public String getPdhRecordPath() {
		return pdhRecordPath;
	}
	public void setPdhRecordPath(String pdhRecordPath) {
		this.pdhRecordPath = pdhRecordPath;
	}

	public String getPdhPrimaryImg() {
		return pdhPrimaryImg;
	}

	public void setPdhPrimaryImg(String pdhPrimaryImg) {
		this.pdhPrimaryImg = pdhPrimaryImg;
	}

	public String getInventoryID() {
		return inventoryID;
	}
	public void setInventoryID(String inventoryID) {
		this.inventoryID = inventoryID;
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
	 * @return the coreFeatureTitle
	 */
	public String getCoreFeatureTitle() {
		return coreFeatureTitle;
	}
	/**
	 * @param coreFeatureTitle the coreFeatureTitle to set
	 */
	public void setCoreFeatureTitle(String coreFeatureTitle) {
		this.coreFeatureTitle = coreFeatureTitle;
	}
	/**
	 * @return the coreFeatureDescription
	 */
	public String getCoreFeatureDescription() {
		return coreFeatureDescription;
	}
	/**
	 * @param coreFeatureDescription the coreFeatureDescription to set
	 */
	public void setCoreFeatureDescription(String coreFeatureDescription) {
		this.coreFeatureDescription = coreFeatureDescription;
	}
	/**
	 * @return the coreFeatureImage
	 */
	public String getCoreFeatureImage() {
		return coreFeatureImage;
	}
	/**
	 * @param coreFeatureImage the coreFeatureImage to set
	 */
	public void setCoreFeatureImage(String coreFeatureImage) {
		this.coreFeatureImage = coreFeatureImage;
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
	 * @return the productFamilyName
	 */
	public String getProductFamilyName() {
		return productFamilyName;
	}
	/**
	 * @param productFamilyName the productFamilyName to set
	 */
	public void setProductFamilyName(String productFamilyName) {
		this.productFamilyName = productFamilyName;
	}
	/**
	 * @return the modelTabName
	 */
	public String getModelTabName() {
		if(modelTabName == null) {
			modelTabName = CommonConstants.MODULES_TAB;
		}
		return modelTabName;
	}
	/**
	 * @param modelTabName the modelTabName to set
	 */
	public void setModelTabName(String modelTabName) {
		this.modelTabName = modelTabName;
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
	 * @return the marketingDesc
	 */
	public String getMarketingDesc() {
		return marketingDesc;
	}
	/**
	 * @param marketingDesc the marketingDesc to set
	 */
	public void setMarketingDesc(String marketingDesc) {
		this.marketingDesc = marketingDesc;
	}
	/**
	 * @return the primaryImage
	 */
	public String getPrimaryImage() {
		return primaryImage;
	}
	/**
	 * @param primaryImage the primaryImage to set
	 */
	public void setPrimaryImage(String primaryImage) {
		this.primaryImage = primaryImage;
	}
	/**
	 * @return the secondaryImage
	 */
	public String getSecondaryImage() {
		return secondaryImage;
	}
	/**
	 * @param secondaryImage the secondary Image to set or path of Dam
	 */
	public void setSecondaryImage(String secondaryImage) {
		this.secondaryImage = secondaryImage;
	}

	/**
	 * @return altPrimaryImage
	 */
	public String getAltPrimaryImage() {
		return altPrimaryImage;
	}

	/**
	 * @param altPrimaryImage
	 */
	public void setAltPrimaryImage(String altPrimaryImage) {
		this.altPrimaryImage = altPrimaryImage;
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
	 * @return the skuCardAttributeList
	 */
	public List<String> getSkuCardAttributeList() {
		return skuCardAttributeList;
	}
	/**
	 * @param skuCardAttributeList the skuCardAttributeList to set
	 */
	public void setSkuCardAttributeList(List<String> skuCardAttributeList) {
		this.skuCardAttributeList = skuCardAttributeList;
	}
	/**
	 * @return the metaTitleResourcesTab
	 */
	public String getMetaTitleResourcesTab() {
		return metaTitleResourcesTab;
	}
	/**
	 * @param metaTitleResourcesTab the metaTitleResourcesTab to set
	 */
	public void setMetaTitleResourcesTab(String metaTitleResourcesTab) {
		this.metaTitleResourcesTab = metaTitleResourcesTab;
	}
	/**
	 * @return the metaTitleModelsTab
	 */
	public String getMetaTitleModelsTab() {
		return metaTitleModelsTab;
	}
	/**
	 * @param metaTitleModelsTab the metaTitleModelsTab to set
	 */
	public void setMetaTitleModelsTab(String metaTitleModelsTab) {
		this.metaTitleModelsTab = metaTitleModelsTab;
	}
	/**
	 * @return the metaDescriptionResourcesTab
	 */
	public String getMetaDescriptionResourcesTab() {
		return metaDescriptionResourcesTab;
	}
	/**
	 * @param metaDescriptionResourcesTab the metaDescriptionResourcesTab to set
	 */
	public void setMetaDescriptionResourcesTab(String metaDescriptionResourcesTab) {
		this.metaDescriptionResourcesTab = metaDescriptionResourcesTab;
	}
	/**
	 * @return the metaDescriptionModelsTab
	 */
	public String getMetaDescriptionModelsTab() {
		return metaDescriptionModelsTab;
	}
	/**
	 * @param metaDescriptionModelsTab the metaDescriptionModelsTab to set
	 */
	public void setMetaDescriptionModelsTab(String metaDescriptionModelsTab) {
		this.metaDescriptionModelsTab = metaDescriptionModelsTab;
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
	 * @return the altText
	 */
	public String getAltText() {
		return altText;
	}
	/**
	 * @param altText the altText to set
	 */
	public void setAltText(String altText) {
		this.altText = altText;
	}
	public String getExtensionID() {
		return extensionID;
	}
	public void setExtensionID(String extensionID) {
		this.extensionID = extensionID;
	}
	public String getProductType() {
		if(StringUtils.isEmpty(productType)) {
			productType = StringUtils.EMPTY;
		}
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
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
	 * @return the metaTitleOverviewTab
	 */
	public String getMetaTitleOverviewTab() {
		return metaTitleOverviewTab;
	}
	/**
	 * @param metaTitleOverviewTab the metaTitleOverviewTab to set
	 */
	public void setMetaTitleOverviewTab(String metaTitleOverviewTab) {
		this.metaTitleOverviewTab = metaTitleOverviewTab;
	}
	/**
	 * @return the metaDescOverviewTab
	 */
	public String getMetaDescOverviewTab() {
		return metaDescOverviewTab;
	}
	/**
	 * @param metaDescOverviewTab the metaDescOverviewTab to set
	 */
	public void setMetaDescOverviewTab(String metaDescOverviewTab) {
		this.metaDescOverviewTab = metaDescOverviewTab;
	}
	@Override
	 public String toString() {
		StringBuilder result = new StringBuilder();
	    String newLine = System.getProperty("line.separator");

	    result.append(newLine);

	    //determine fields declared in this class only (no fields of superclass)
	    Field[] fields = this.getClass().getDeclaredFields();

	    //print field names paired with their values
		for (Field field : fields) {
			if (!"serialVersionUID".equalsIgnoreCase(field.getName())) {
				result.append("  ");
				try {
					result.append(field.getName());
					result.append(": ");
					// requires access to private field:
					result.append(field.get(this));
				} catch (IllegalAccessException ex) {
					LOG.debug(String.format("Error in toString of ProductFamilyDetails: %s", ex.getMessage()));
					return "Error in toString of ProductFamilyDetails" + ex.getMessage();
				}
				result.append(newLine);
			}
		}

		return result.toString();
     }

	public String getIsSuffixDisabled() {
		return this.isSuffixDisabled;
	}

	public void setIsSuffixDisabled(String isSuffixDisabled) {
		this.isSuffixDisabled = isSuffixDisabled;
	}

	public String getPdhSpinnerImage() {
		return pdhSpinnerImage;
	}
	public void setPdhSpinImage(String pdhSpinnerImage) {
		this.pdhSpinnerImage = pdhSpinnerImage;
	}


	public Boolean getPrimaryImageIsRepresentative() {
		return primaryImageIsRepresentative;
	}

	public void setPrimaryImageIsRepresentative(Boolean primaryImageIsRepresentative) {
		this.primaryImageIsRepresentative = primaryImageIsRepresentative;
	}

	public String getRepfl() {
		return repfl;
	}

	public void setRepfl(String repfl) {
		this.repfl = repfl;
	}
	public static ProductFamilyDetailBuilder custom(BeanFiller... filler){
		return new ProductFamilyDetailBuilder().prepare(filler);
	}

	public String getHowToBuyLink() {
		if (howToBuyList != null && howToBuyList.size() == 1) {
			return howToBuyList.get(0).getLink();
		}
		return null;
	}
}
