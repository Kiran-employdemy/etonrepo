package com.eaton.platform.core.bean;

import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsBean;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;



/**
 * <html> Description: This bean class used in ProductDetailsCardBean class to store content </html> .
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
public class ProductDetailsCardBean implements Serializable {

	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = -7130265800501204353L;

	/**
	 * The page path.
	 */
	private String primaryCTAURL;

	/**
	 * The page path.
	 */
	private String primaryCTALabel;

	/**
	 * The page path.
	 */
	private String primaryCTANewWindow;

	private String primaryCTAEnableSourceTracking;

	private transient List<SecondaryLinksBean> secondaryLinksList;//SonarQube private or transient issue


	private String title;
	private String imageMobile;
	private String imageTablet;
	private String imageDesktop;
	private String sirvImage;
	private String altText;
	private String description;
	private String price;
	private String priceLabel;
	private String crossSellTitle;
	private String crossSellDescription;
	private int length;
	private List<SkuCardBean> skuSellList;
	private List<SkuCardBean> skuReplacementOptionList;

	//media gallery
	private String galleryHeadline = StringUtils.EMPTY;
	private String galleryDesc = StringUtils.EMPTY;
	private int galleryLength;

	private String category;
	private String isSuffixDisabled;
	private String productFamilyAEMPath;
	private String status;
	private String replacementOptionText;
	private boolean displayReplacementOptions = Boolean.FALSE;
	private String priceDisclaimer;
	private boolean isPriceDisclaimerEnabled = false;
	private boolean isFreeSampleButtonEnabled = false;


	private Boolean primaryImageIsRepresentative;
	private Boolean sirvImageIsRepresentative;
	private Boolean hasSirvImage;

	private transient List<MediagalleryBean> listTypeArray;

	private String viewType;

	private String view;
	private String modelCode;

	public static ProductDetailsCardBean of(SKUDetailsBean skuDetailsBean) {
		ProductDetailsCardBean productDetailsCardBean = new ProductDetailsCardBean();
		productDetailsCardBean.modelCode = skuDetailsBean.getModelCode();
		return productDetailsCardBean;
	}

	/**
	 * @return the galleryHeadline
	 */
	public String getGalleryHeadline() {
		return galleryHeadline;
	}

	/**
	 * @return the listTypeArray
	 */
	public List<MediagalleryBean> getListTypeArray() {
		return listTypeArray;
	}

	/**
	 * @param listTypeArray the listTypeArray to set
	 */
	public void setListTypeArray(List<MediagalleryBean> listTypeArray) {
		this.listTypeArray = listTypeArray;
	}

	/**
	 * @param galleryHeadline the galleryHeadline to set
	 */
	public void setGalleryHeadline(String galleryHeadline) {
		this.galleryHeadline = galleryHeadline;
	}

	/**
	 * @return the galleryDesc
	 */
	public String getGalleryDesc() {
		return galleryDesc;
	}

	/**
	 * @param galleryDesc the galleryDesc to set
	 */
	public void setGalleryDesc(String galleryDesc) {
		this.galleryDesc = galleryDesc;
	}

	/**
	 * @return the galleryLength
	 */
	public int getGalleryLength() {
		return galleryLength;
	}

	/**
	 * @param galleryLength the galleryLength to set
	 */
	public void setGalleryLength(int galleryLength) {
		this.galleryLength = galleryLength;
	}

	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @param length the length to set
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * @return the crossSellTitle
	 */
	public String getCrossSellTitle() {
		return crossSellTitle;
	}

	/**
	 * @param crossSellTitle the crossSellTitle to set
	 */
	public void setCrossSellTitle(String crossSellTitle) {
		this.crossSellTitle = crossSellTitle;
	}

	/**
	 * @return the crossSellDescription
	 */
	public String getCrossSellDescription() {
		return crossSellDescription;
	}

	/**
	 * @param crossSellDescription the crossSellDescription to set
	 */
	public void setCrossSellDescription(String crossSellDescription) {
		this.crossSellDescription = crossSellDescription;
	}

	/**
	 * @return the skuSellList
	 */
	public List<SkuCardBean> getSkuSellList() {
		return skuSellList;
	}

	/**
	 * @param skuSellList the skuSellList to set
	 */
	public void setSkuSellList(List<SkuCardBean> skuSellList) {
		this.skuSellList = skuSellList;
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

	/**
	 * @return the imageMobile
	 */
	public String getImageMobile() {
		return imageMobile;
	}

	/**
	 * @param imageMobile the imageMobile to set
	 */
	public void setImageMobile(String imageMobile) {
		this.imageMobile = imageMobile;
	}

	/**
	 * @return the imageTablet
	 */
	public String getImageTablet() {
		return imageTablet;
	}

	/**
	 * @param imageTablet the imageTablet to set
	 */
	public void setImageTablet(String imageTablet) {
		this.imageTablet = imageTablet;
	}

	/**
	 * @return the imageDesktop
	 */
	public String getImageDesktop() {
		return imageDesktop;
	}

	/**
	 * @param imageDesktop the imageDesktop to set
	 */
	public void setImageDesktop(String imageDesktop) {
		this.imageDesktop = imageDesktop;
	}

	public void setSirvImage(String sirvImage) {
		this.sirvImage = sirvImage;
	}

	public String getSirvImage() {
		return sirvImage;
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
	 * @return the price
	 */
	public String getPrice() {
		return price;
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(String price) {
		this.price = price;
	}

	/**
	 * @return the priceLabel
	 */
	public String getPriceLabel() {
		return priceLabel;
	}

	/**
	 * @param priceLabel the priceLabel to set
	 */
	public void setPriceLabel(String priceLabel) {
		this.priceLabel = priceLabel;
	}

	public List<SecondaryLinksBean> getSecondaryLinksList() {
		return secondaryLinksList;
	}

	public void setSecondaryLinksList(List<SecondaryLinksBean> secondaryLinksList) {
		this.secondaryLinksList = secondaryLinksList;
	}

	public String getPrimaryCTAURL() {
		return primaryCTAURL;
	}

	public void setPrimaryCTAURL(String primaryCTAURL) {
		this.primaryCTAURL = primaryCTAURL;
	}

	public String getPrimaryCTALabel() {
		return primaryCTALabel;
	}

	public void setPrimaryCTALabel(String primaryCTALabel) {
		this.primaryCTALabel = primaryCTALabel;
	}

	public String getPrimaryCTANewWindow() {
		return primaryCTANewWindow;
	}

	public void setPrimaryCTANewWindow(String primaryCTANewWindow) {
		this.primaryCTANewWindow = primaryCTANewWindow;
	}

	public String getPrimaryCTAEnableSourceTracking() {
		return primaryCTAEnableSourceTracking;
	}

	public void setPrimaryCTAEnableSourceTracking(String primaryCTAEnableSourceTracking) {
		this.primaryCTAEnableSourceTracking = primaryCTAEnableSourceTracking;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getIsSuffixDisabled() {
		return this.isSuffixDisabled;
	}

	public void setIsSuffixDisabled(String isSuffixDisabled) {
		this.isSuffixDisabled = isSuffixDisabled;
	}



	public String getProductFamilyAEMPath() {
		return this.productFamilyAEMPath;
	}

	public void setProductFamilyAEMPath(String productFamilyAEMPath) {
		this.productFamilyAEMPath = productFamilyAEMPath;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getReplacementOptionText() {
		return replacementOptionText;
	}

	public void setReplacementOptionText(String replacementOptionText) {
		this.replacementOptionText = replacementOptionText;
	}

	public List<SkuCardBean> getSkuReplacementOptionList() {
		return skuReplacementOptionList;
	}

	public void setSkuReplacementOptionList(List<SkuCardBean> skuReplacementOptionList) {
		this.skuReplacementOptionList = skuReplacementOptionList;
	}

	public boolean isDisplayReplacementOptions() {
		return displayReplacementOptions;
	}

	public void setDisplayReplacementOptions(boolean displayReplacementOptions) {
		this.displayReplacementOptions = displayReplacementOptions;
	}

	public boolean isPriceDisclaimerEnabled() {
		return this.isPriceDisclaimerEnabled;
	}

	public void setIsPriceDisclaimerEnabled(boolean enabled) {
		this.isPriceDisclaimerEnabled = enabled;
	}

	public String getPriceDisclaimer() {
		return this.priceDisclaimer;
	}

	public void setPriceDisclaimer(String priceDisclaimer) {
		this.priceDisclaimer = priceDisclaimer;
	}

	/**
	 * @return if the primary image is representative
	 */
	public Boolean getPrimaryImageIsRepresentative() {
		return primaryImageIsRepresentative;
	}

	/**
	 * @param primaryImageIsRepresentative if the primary image is representative
	 */
	public void setPrimaryImageIsRepresentative(Boolean primaryImageIsRepresentative) {
		this.primaryImageIsRepresentative = primaryImageIsRepresentative;
	}

	/**
	 * @param sirvImageIsRepresentative if the sirv image is representative
	 */
	public void setSirvImageIsRepresentative(Boolean sirvImageIsRepresentative){
		this.sirvImageIsRepresentative = sirvImageIsRepresentative;
	}

	/**
	 * @param hasSirvImage if the sirv image is present
	 */
	public void setHasSirvImage(Boolean hasSirvImage){
		this.hasSirvImage = hasSirvImage;
	}

	public Boolean getSirvImageIsRepresentative(){
		return sirvImageIsRepresentative;
	}

	public Boolean getHasSirvImage(){
		return hasSirvImage;
	}

	public String getViewType() {
		return viewType;
	}

	public void setViewType(String viewType) {
		this.viewType = viewType;
	}

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}

	public boolean isFreeSampleButtonEnabled() {
		return isFreeSampleButtonEnabled;
	}

	public void setFreeSampleButtonEnabled(boolean isFreeSampleButtonEnabled) {
		this.isFreeSampleButtonEnabled = isFreeSampleButtonEnabled;
	}

	public String getModelCode() {
		return modelCode;
	}

}
