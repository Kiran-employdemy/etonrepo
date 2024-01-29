package com.eaton.platform.core.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.bean.AttrNmBean;
import com.eaton.platform.core.bean.ImageGroupBean;
import com.eaton.platform.core.bean.MediagalleryBean;
import com.eaton.platform.core.bean.ProductDetailsCardBean;
import com.eaton.platform.core.bean.ProductFamilyPIMDetails;
import com.eaton.platform.core.bean.SecondaryLinksBean;
import com.eaton.platform.core.bean.SkuCardBean;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.EatonConfigService;
import com.eaton.platform.core.services.EndecaRequestService;
import com.eaton.platform.core.services.FeatureEnablementService;
import com.eaton.platform.core.services.ProductFamilyDetailService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.core.util.SkuCardXmlParser;
import com.eaton.platform.core.util.SkuImageParser;
import com.eaton.platform.integration.eloqua.util.EloquaUtil;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsBean;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsResponseBean;
import com.eaton.platform.integration.endeca.services.EndecaService;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

@Model(adaptables = { Resource.class,
		SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ProductDetailsCardModel {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductDetailsCardModel.class);
	private static final String BLANK_CONSTANT = "_blank";
	private static final String SELF_CONSTANT = "_self";
	private static final String PREVIEW = "500x500";
	private static final String THUMB = "90x90";
	private static final String DOWNLOAD = "Native%20File";
	private static final String ZOOM = "1000x1000";
	private static final String SIRV_IMAGE = "360_IMAGE";
	private static final String LANGUAGE_MASTERS = "language-masters";
	private static final String DEFAULT_JSON_FILE_UPLOAD_PATH = "/content/dam/eaton/resources/price/json/";
	private static final String DEFAULT_ROOT_ELEMENT = "catalogPriceList";
	private static final String PIM_SITE_CONFIG_PAGE_FOLDER_PATH = "/etc/cloudservices/siteconfig/";
	private static final String PROPERTY_DEFAULT_LINK_CTA = "defaultLinkCTA";
	private static final String GENERIC_IMAGE = "/content/dam/eaton/resources/default-sku-image-220.jpg";
	private static final String IMAGE_PRIMARY = "IMAGE_PRIMARY";
	private static final String GLOBAL = "Global";
	private static final String REPFL_YES = "Y";

	private String priceListPath;
	private String catalognumber;
	private String countryName;
	private Locale languageValue;
	private String rootElementName;
	private String filename;

	private ProductDetailsCardBean productDetailsCardBean;
	private SecondaryLinksBean secondaryLinksBean = new SecondaryLinksBean();
	private SkuCardXmlParser skuCardXmlParser = new SkuCardXmlParser();
	private List<MediagalleryBean> mediaGalleryList = new ArrayList<>();

	@Inject
	@Source("sling-object")
	private SlingHttpServletRequest slingRequest;

	@Inject
	protected EndecaRequestService endecaRequestService;

	@Inject
	@ScriptVariable
	private Page currentPage;

	@Inject
	protected EndecaService endecaService;

	@Inject
	protected ProductFamilyDetailService productFamilyDetailService;

	@Inject
	protected AdminService adminService;

	@Inject
	protected FeatureEnablementService enablementService;

	@Inject
	protected EatonConfigService configService;

	@Inject
	@Source("sling-object")
	private ResourceResolver resourceResolver;

	@Inject
	/** The page manager. */
	protected PageManager pageManager;

	@Inject
	private ProductFamilyPIMDetails productFamilyPIMDetails;

	private String isSkuPage;

	@ValueMapValue
	private String viewType;

	@ValueMapValue
	private String view;
	@PostConstruct
	protected void init() {

		LOGGER.debug("ProductDetailsCardModel :: init() :: Started");
		SKUDetailsBean skuDetailsBean;
		boolean serialAuthFlag;
		boolean serialFlag;
		if (slingRequest.getRequestPathInfo() != null) {
			final String[] selectors = slingRequest.getRequestPathInfo().getSelectors();
			final EndecaServiceRequestBean endecaRequestBean = endecaRequestService.getEndecaRequestBean(currentPage,
					selectors, StringUtils.EMPTY);

			SKUDetailsResponseBean skuDetails = endecaService.getSKUDetails(endecaRequestBean);
			if (skuDetails != null) {

				skuDetailsBean = skuDetails.getSkuResponse().getSkuDetails();
				productDetailsCardBean = ProductDetailsCardBean.of(skuDetailsBean);
				productDetailsCardBean.setFreeSampleButtonEnabled(enablementService.isFreeSampleButtonEnabled());
				LOGGER.debug("IS free sample enabled {}",enablementService.isFreeSampleButtonEnabled());
				productDetailsCardBean.setViewType(viewType);
				productDetailsCardBean.setView(view);
				serialAuthFlag = skuDetailsBean.getSerialAuthFlag();
				serialFlag = skuDetailsBean.getSerialFlag();
				LOGGER.debug("serialAuthFlag for the sku id {} is {}", endecaRequestBean.getSearchTerms(),
						serialAuthFlag);
				LOGGER.debug("serialFlag for the sku id {} is {}", endecaRequestBean.getSearchTerms(), serialFlag);
				productFamilyPIMDetails = productFamilyDetailService.getProductFamilyPIMDetailsBean(skuDetailsBean,
						currentPage);
				if ((configService != null) && (configService.getConfigServiceBean() != null)) {
					if (configService.getConfigServiceBean().getDamJsonUploadPath() != null
							&& !(configService.getConfigServiceBean().getDamJsonUploadPath().isEmpty())) {
						priceListPath = configService.getConfigServiceBean().getDamJsonUploadPath();
					} else {
						priceListPath = DEFAULT_JSON_FILE_UPLOAD_PATH;
					}
					if (configService.getConfigServiceBean().getJsonFileName() != null
							&& !(configService.getConfigServiceBean().getJsonFileName().isEmpty())) {
						filename = configService.getConfigServiceBean().getJsonFileName();
					}
					if (configService.getConfigServiceBean().getJsonRootElementName() != null
							&& !(configService.getConfigServiceBean().getJsonRootElementName().isEmpty())) {
						rootElementName = configService.getConfigServiceBean().getJsonRootElementName();
					} else {
						rootElementName = DEFAULT_ROOT_ELEMENT;
					}

				}
				if (currentPage != null && currentPage.getLanguage(false) != null) {
					languageValue = currentPage.getLanguage(false);
				}

				if (languageValue != null && languageValue.getCountry() != null) {
					countryName = languageValue.getCountry();
				}

				if (null != currentPage) {
					ValueMap pageProperties = currentPage.getProperties();
					isSkuPage = pageProperties.get(CommonConstants.PAGE_TYPE, StringUtils.EMPTY);
				}

				if (productFamilyPIMDetails != null) {
					productDetailsCardBean.setIsSuffixDisabled(productFamilyPIMDetails.getIsSuffixDisabled());
					productDetailsCardBean.setProductFamilyAEMPath(productFamilyPIMDetails.getProductFamilyAEMPath());

					if (productFamilyPIMDetails.getPrimaryCTALabel() != null) {
						productDetailsCardBean.setPrimaryCTALabel(productFamilyPIMDetails.getPrimaryCTALabel());
					}
					if (productFamilyPIMDetails.getPrimaryCTAURL() != null) {
						// without # -- the required CSS is not applied.
						// you can remove # if the URL is valid one
						String newPath = CommonUtil.dotHtmlLink(productFamilyPIMDetails.getPrimaryCTAURL(), resourceResolver);
						if (validLink(newPath)) {
							String linkPagePath = productFamilyPIMDetails.getPrimaryCTAURL();
							String linkURL = CommonUtil.dotHtmlLink(linkPagePath, resourceResolver);
							String primaryProductTaxonomy = productFamilyPIMDetails.getPrimaryProductTaxonomy();
							if(StringUtils.isNotBlank(linkURL)) {
								linkURL = EloquaUtil.hasEloquaForm(linkPagePath, adminService) ? EloquaUtil.appendPrimaryProductTaxonomy(linkURL, primaryProductTaxonomy) : linkURL;
								productDetailsCardBean.setPrimaryCTAURL(linkURL);
							}
						} else {
							productDetailsCardBean.setPrimaryCTAURL(StringUtils.EMPTY);
						}
					}

					if (productFamilyPIMDetails.getPrimaryCTAEnableSourceTracking() != null) {
						productDetailsCardBean.setPrimaryCTAEnableSourceTracking(productFamilyPIMDetails.getPrimaryCTAEnableSourceTracking());
					} else {
						LOGGER.warn("Cannot find the primary cta enable source tracking property value.");
					}

 					productDetailsCardBean.setPrimaryCTANewWindow(productFamilyPIMDetails.getPrimaryCTANewWindow());

					if (productFamilyPIMDetails.getSecondaryLinksList() != null) {
						productDetailsCardBean
								.setSecondaryLinksList(CommonUtil.replaceTokenForSecondaryLinkOptions(secondaryLinks(productFamilyPIMDetails.getSecondaryLinksList()), isSkuPage, selectors[0]) );
					}
				}

				if (selectors != null && selectors.length > 0) {
					catalognumber = URLDecoder.decode(selectors[0], StandardCharsets.UTF_8);
					productDetailsCardBean.setTitle(catalognumber);
				}

				if (skuDetailsBean != null) {
					/*
					 * if discontinued sku and have replacement catalogs then show catalog list else
					 * Set contact me CTA for orphan SKU page and point to default CTA link
					 */
					if (((CommonConstants.INACTIVE.equalsIgnoreCase(skuDetailsBean.getStatus()))
							|| CommonConstants.DISCONTINUED.equalsIgnoreCase(skuDetailsBean.getStatus()))
							&& null != skuDetailsBean.getReplacement()) {
						// Replacement options
						productDetailsCardBean.setSkuReplacementOptionList(getReplacementOptionsList(
								CommonConstants.PRODUCT_REPLACEMENT_TEXT, skuDetailsBean, adminService, currentPage));
						if (!productDetailsCardBean.getSkuReplacementOptionList().isEmpty()) {
							if (productDetailsCardBean.getSkuReplacementOptionList().size() == 1) {
								productDetailsCardBean.setReplacementOptionText(
										CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage,
												CommonConstants.PRODUCT_DETAILS_CARD_REPLACEMENT_OPTION_TEXT,
												CommonConstants.PRODUCT_DETAILS_CARD_REPLACEMENT_DEFAULT_TEXT));
							} else {
								productDetailsCardBean.setReplacementOptionText(
										CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage,
												CommonConstants.PRODUCT_DETAILS_CARD_REPLACEMENT_OPTIONS_TEXT,
												CommonConstants.PRODUCT_DETAILS_CARD_REPLACEMENTS_DEFAULT_TEXT));
							}
							productDetailsCardBean.setDisplayReplacementOptions(Boolean.TRUE);
						}
					} else if (null == skuDetailsBean.getExtensionId() || skuDetailsBean.getExtensionId().isEmpty()
							|| null == skuDetailsBean.getInventoryId() || skuDetailsBean.getInventoryId().isEmpty()) {
						productDetailsCardBean.setPrimaryCTALabel(CommonUtil.getI18NFromResourceBundle(slingRequest,
								currentPage, CommonConstants.CONTACT_ME, CommonConstants.CONTACT_ME_DEFAULT_VALUE));
						if (null != adminService) {
							try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
								String locale = languageValue.toString().toLowerCase();
								Page siteConfigPage = adminResourceResolver
										.resolve(PIM_SITE_CONFIG_PAGE_FOLDER_PATH.concat(locale)).adaptTo(Page.class);
								if (null != siteConfigPage) {
									productDetailsCardBean.setPrimaryCTAURL(siteConfigPage.getProperties()
											.get(PROPERTY_DEFAULT_LINK_CTA, String.class));
								}
							}
						}
					}
					if (skuDetailsBean.getMktgDesc() != null) {
						productDetailsCardBean.setDescription(skuDetailsBean.getMktgDesc());
					}
					// media gallery

					productDetailsCardBean.setListTypeArray(mediaGallery(skuDetailsBean, adminService));
					if (productDetailsCardBean.getListTypeArray() != null) {
						if (!productDetailsCardBean.getListTypeArray().isEmpty()) {
							productDetailsCardBean.setGalleryLength(productDetailsCardBean.getListTypeArray().size());
						} else {
							productDetailsCardBean.setGalleryLength(0);
						}
					} else {
						productDetailsCardBean.setGalleryLength(0);
					}

					// price
					String price = CommonUtil.priceItem(priceListPath, adminService, catalognumber, countryName,
							rootElementName, filename);
					if (price != null) {
						setPriceDisclaimer();
						productDetailsCardBean.setPrice(price);
						productDetailsCardBean.setPriceLabel(CommonUtil.getI18NFromResourceBundle(slingRequest,
								currentPage, "ListPrice", "LIST PRICE"));
					}

					// Status
					if (null != skuDetailsBean.getStatus()) {
						productDetailsCardBean.setStatus(skuDetailsBean.getStatus());
					}

					// cross-sell
					productDetailsCardBean.setSkuSellList(crossSell("crossSell", skuDetailsBean, adminService));

					if (productDetailsCardBean != null) {
						if (productDetailsCardBean.getSkuSellList() != null) {
							// cross-sell
							productDetailsCardBean.setCrossSellTitle(CommonUtil.getI18NFromResourceBundle(slingRequest,
									currentPage, "Cross Sell Title", "Cross Sell Title"));
							productDetailsCardBean.setCrossSellDescription(CommonUtil.getI18NFromResourceBundle(
									slingRequest, currentPage, "Cross Sell Description", "Cross Sell Description"));
						}
						productDetailsCardBean.setCategory(skuDetailsBean.getFamilyName());
					}
				}

			}
		}
		LOGGER.debug("ProductDetailsCardModel :: init() :: End");
	}

	private Optional<PageDecorator> getProductFamilyPage() {
		Resource familyResource = resourceResolver.getResource(productFamilyPIMDetails.getProductFamilyAEMPath());
		if (familyResource != null) {
			return Optional.ofNullable(familyResource.adaptTo(PageDecorator.class));
		}
		return Optional.empty();
	}

	private Optional<ProductGridSlingModel> getProductGrid() {
		Optional<PageDecorator> familyPage = getProductFamilyPage();
		if (familyPage.isPresent()) {
			Optional<Resource> productGrid = familyPage.get()
					.findByResourceType(CommonConstants.PRODUCT_GRID_RESOURCE_TYPE);
			if (productGrid.isPresent()) {
				return Optional.ofNullable(productGrid.get().adaptTo(ProductGridSlingModel.class));
			}
		}
		return Optional.empty();
	}

	private void setPriceDisclaimer() {
		Optional<ProductGridSlingModel> productGrid = getProductGrid();
		if (productGrid.isPresent()) {
			productDetailsCardBean.setIsPriceDisclaimerEnabled(!productGrid.get().getHidePriceDisc());
			productDetailsCardBean.setPriceDisclaimer(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage,
					CommonConstants.PRICE_DISCLAIMER_NO_ASTERISK_I18N_KEY,
					CommonConstants.PRICE_DISCLAIMER_NO_ASTERISK_I18N_KEY));
		} else {
			productDetailsCardBean.setIsPriceDisclaimerEnabled(false);
		}
	}

	private boolean validLink(final String primaryCTAURL) {
		boolean isValidLink = StringUtils.startsWith(primaryCTAURL, CommonConstants.HTTP)
				|| StringUtils.startsWith(primaryCTAURL, CommonConstants.HTTPS)
				|| StringUtils.startsWith(primaryCTAURL, CommonConstants.WWW);
		isValidLink = (!isValidLink && CommonUtil.startsWithAnySiteContentRootPath(primaryCTAURL))
				? (null != pageManager.getPage(primaryCTAURL))
				: isValidLink;
		LOGGER.debug("primaryCTAURL- {} isValidLink ::{}", primaryCTAURL, isValidLink);
		return isValidLink;
	}

	private List<SecondaryLinksBean> secondaryLinks(List<SecondaryLinksBean> secondaryLinksLink) {
		for (SecondaryLinksBean items : secondaryLinksLink) {
			if (items != null) {
				if (items.getText() != null && !(items.getText().isEmpty())) {
					secondaryLinksBean.setText(items.getText());
				}
				if (items.getPath() != null && !(items.getPath().isEmpty())) {
					String path = items.getPath();

					if (CommonUtil.getIsExternal(path)) {
						items.setNewWindow(BLANK_CONSTANT);
					} else {
						items.setNewWindow(SELF_CONSTANT);
						items.setPath(CommonUtil.dotHtmlLink(path));
					}
				}
			}
		}
		return secondaryLinksLink;
	}

	private List<SkuCardBean> crossSell(String string, SKUDetailsBean skuDetailsBean, AdminService adminService) {

		List<SkuCardBean> skuSellList;
		skuSellList = skuCardXmlParser.getSkuCardList(string, skuDetailsBean, adminService);
		if (null != skuSellList) {

			productDetailsCardBean.setLength(skuSellList.size());
			for (SkuCardBean item : skuSellList) {
				if (currentPage != null) {
					item.setLink(currentPage.getPath().concat(".").concat(item.getCatalog()).concat(".html"));
				}
				String imagePath = item.getImage();
				if (imagePath != null && !(imagePath.isEmpty())) {
					item.setImage(imagePath);
				} else {
					item.setImage(GENERIC_IMAGE);
				}
			}
			getSortedList(skuSellList);
		}
		return skuSellList;
	}

	private List<SkuCardBean> getReplacementOptionsList(final String xmlFileName, final SKUDetailsBean skuDetailsBean,
			final AdminService adminService, final Page currentPage) {
		final List<SkuCardBean> skuList = skuCardXmlParser.getSkuCardList(xmlFileName, skuDetailsBean, adminService);
		if (null != skuList) {
			for (SkuCardBean item : skuList) {
				if (currentPage != null) {
					item.setLink(currentPage.getPath().concat(".").concat(item.getCatalog())
							.concat(CommonConstants.HTML_EXTN));
				} else {
					LOGGER.error("Page does not exist. ");
				}
				item.setCatalog(item.getCatalog());
				item.setPriority(item.getPriority());
			}
			getSortedList(skuList);
		}
		return skuList;
	}

	private List<MediagalleryBean> mediaGallery(SKUDetailsBean skuDetailsBean2, AdminService adminService) {

		MediagalleryBean mediagalleryBean;
		MediagalleryBean intraMediaGalleryBean;
		Map<String, MediagalleryBean> intraMediaGalleryBeanMap;
		String xmlString = null;
		boolean hasSirvImage = false;
		productDetailsCardBean.setHasSirvImage(false);
		if ((skuDetailsBean2 != null)
				&& (skuDetailsBean2.getSkuImgs() != null && !(skuDetailsBean2.getSkuImgs().isEmpty()))) {
			xmlString = skuDetailsBean2.getSkuImgs();

			SkuImageParser skuImageParser = new SkuImageParser();

			Map<String, ImageGroupBean> attrGrpList = skuImageParser.getPDHImageList(xmlString, adminService);
			if (attrGrpList != null) {
				for (Entry<String, ImageGroupBean> entry1 : attrGrpList.entrySet()) {

					if ((entry1 != null)
							&& (entry1.getKey() != null && !(entry1.getKey().isEmpty()) && entry1.getValue() != null)) {
						String title = entry1.getKey();

						intraMediaGalleryBeanMap = new HashMap<>();
						ImageGroupBean imageGroupBean = entry1.getValue();
						if (imageGroupBean.getImageRenditionMap() != null
								&& !(imageGroupBean.getImageRenditionMap().isEmpty())) {
							Map<String, AttrNmBean> renditionMap = imageGroupBean.getImageRenditionMap();
							mediagalleryBean = new MediagalleryBean();
							for (Entry<String, AttrNmBean> entry2 : renditionMap.entrySet()) {

								if ((entry2 != null) && (entry2.getKey() != null && !(entry2.getKey().isEmpty())
										&& entry2.getValue() != null)) {

									String temp = entry2.getKey();

									AttrNmBean attrNmBean = entry2.getValue();

									int lastkeyPartNumber = 0;
									try {
										lastkeyPartNumber = Integer.parseInt(attrNmBean.getSeq());
									} catch (Exception e) {
										LOGGER.error("Exception", e);
									}

									if ((lastkeyPartNumber > 0) && (!title.equals(IMAGE_PRIMARY))) {

										if (intraMediaGalleryBeanMap.containsKey(title + "_" + lastkeyPartNumber)) {
											intraMediaGalleryBean = intraMediaGalleryBeanMap
													.get(title + "_" + lastkeyPartNumber);
										} else {
											intraMediaGalleryBean = new MediagalleryBean();
										}

										intraMediaGalleryBean.setVideo(false);
										intraMediaGalleryBean.setZoomEnabled(true);
										intraMediaGalleryBean.setTitle(StringUtils.EMPTY);
										intraMediaGalleryBean.setAltText(title);
										intraMediaGalleryBean.setText(StringUtils.EMPTY);
										intraMediaGalleryBean.setIsSpinImage(false);

										if (isImageEligible(attrNmBean) && attrNmBean.getCdata() != null
												&& !(attrNmBean.getCdata().isEmpty())) {

											if (StringUtils.containsIgnoreCase(temp, PREVIEW)) {
												intraMediaGalleryBean.setPreview(attrNmBean.getCdata());
												if (title.equals(IMAGE_PRIMARY)) {
													productDetailsCardBean.setImageDesktop(attrNmBean.getCdata());
												}

											} else if (StringUtils.containsIgnoreCase(temp, THUMB)) {
												intraMediaGalleryBean.setThumbnail(attrNmBean.getCdata());
												intraMediaGalleryBean.setSeq(attrNmBean.getSeq());

											} else if (StringUtils.containsIgnoreCase(temp, DOWNLOAD)) {
												intraMediaGalleryBean.setDownload(attrNmBean.getCdata());
												intraMediaGalleryBean.setDownloadEnabled(true);

											} else if (StringUtils.containsIgnoreCase(temp, ZOOM)) {
												intraMediaGalleryBean.setZoom(attrNmBean.getCdata());
												intraMediaGalleryBean.setZoomEnabled(true);

											} else if (StringUtils.containsIgnoreCase(temp, SIRV_IMAGE)) {
												productDetailsCardBean.setSirvImage(attrNmBean.getCdata());
												// if we only find Sirv image after primary has been defined, we need to
												// set it to null so that the Sirv image will be displayed
												productDetailsCardBean.setImageDesktop(null);
												hasSirvImage = true;
												productDetailsCardBean.setHasSirvImage(true);
												intraMediaGalleryBean.setDownload(attrNmBean.getCdata());
												intraMediaGalleryBean.setDownloadEnabled(true);
												intraMediaGalleryBean.setZoom(attrNmBean.getCdata());
												intraMediaGalleryBean.setZoomEnabled(true);
												if(!mediaGalleryList.isEmpty()) {
													intraMediaGalleryBean.setThumbnail(mediaGalleryList.get(0).getThumbnail());
												}
												intraMediaGalleryBean.setSeq(attrNmBean.getSeq());
												intraMediaGalleryBean.setPreview(attrNmBean.getCdata());
												intraMediaGalleryBean.setIsSpinImage(true);
												if((attrNmBean.getRepfl() != null) && (attrNmBean.getRepfl().equals(REPFL_YES))){
													productDetailsCardBean.setSirvImageIsRepresentative(Boolean.TRUE);
												} else{
													productDetailsCardBean.setSirvImageIsRepresentative(Boolean.FALSE);
												}

												LOGGER.debug(
														"ProductDetailsCardModel :: mediaGallery() :: Sirv image found");
											} else {
												LOGGER.debug(
														"ProductDetailsCardModel :: mediaGallery() :: entry key does not match any expected values");
											}
										}

										intraMediaGalleryBeanMap.put(title + "_" + lastkeyPartNumber,
												intraMediaGalleryBean);

									} else {
										attrNmBean = entry2.getValue();
										mediagalleryBean.setVideo(false);
										mediagalleryBean.setZoomEnabled(true);
										mediagalleryBean.setTitle(StringUtils.EMPTY);
										mediagalleryBean.setAltText(title);
										mediagalleryBean.setText(StringUtils.EMPTY);
										if (StringUtils.containsIgnoreCase(attrNmBean.getId(), SIRV_IMAGE)) {
											hasSirvImage = true;
										}
										if((attrNmBean.getRepfl() != null) && (attrNmBean.getRepfl().equals(REPFL_YES))){
											productDetailsCardBean.setPrimaryImageIsRepresentative(Boolean.TRUE);
										} else{
											productDetailsCardBean.setPrimaryImageIsRepresentative(Boolean.FALSE);
										}
										if (isImageEligible(attrNmBean)&& attrNmBean.getCdata() != null
												&& !(attrNmBean.getCdata().isEmpty())) {

											if (StringUtils.containsIgnoreCase(temp, PREVIEW)) {
												mediagalleryBean.setPreview(attrNmBean.getCdata());
												if ((title.equals(IMAGE_PRIMARY)) && (!hasSirvImage)) {
													productDetailsCardBean.setImageDesktop(attrNmBean.getCdata());
												}

											} else if (StringUtils.containsIgnoreCase(temp, THUMB)) {
												mediagalleryBean.setThumbnail(attrNmBean.getCdata());
												mediagalleryBean.setSeq(attrNmBean.getSeq());

											} else if (StringUtils.containsIgnoreCase(temp, DOWNLOAD)) {
												mediagalleryBean.setDownload(attrNmBean.getCdata());
												mediagalleryBean.setDownloadEnabled(true);

											} else if (StringUtils.containsIgnoreCase(temp, ZOOM)) {
												mediagalleryBean.setZoom(attrNmBean.getCdata());
												mediagalleryBean.setZoomEnabled(true);
											} else {
												LOGGER.debug(
														"ProductDetailsCardModel :: mediaGallery() :: entry key does not match any expected values");
											}
										}
									}
								}
							}

							if ((mediagalleryBean != null) && (StringUtils.isNotEmpty(mediagalleryBean.getThumbnail()))
									&& (mediagalleryBean.getPreview() != null)
									&& (!mediagalleryBean.getPreview().equals(StringUtils.EMPTY))) {
								mediaGalleryList.add(mediagalleryBean);
							}

							for (MediagalleryBean temp : intraMediaGalleryBeanMap.values()) {
								if ((temp != null) && (StringUtils.isNotEmpty(temp.getThumbnail()))
										&& (temp.getPreview() != null)
										&& (!temp.getPreview().equals(StringUtils.EMPTY))){
									mediaGalleryList.add(temp);
								}
							}

						}
					}
				}
				if (mediaGalleryList != null) {
					getImageSortedList(mediaGalleryList);
				}
				if(mediaGalleryList != null) {
					for (int i = 0; i < mediaGalleryList.size(); i++) {
						if (mediaGalleryList.get(i).getIsSpinImage()) {
							MediagalleryBean spinMediagalleryBean = mediaGalleryList.get(i);
							mediaGalleryList.remove(mediaGalleryList.get(i));
							mediaGalleryList.add(0, spinMediagalleryBean);
						}
					}
				}
			}
		}
		return mediaGalleryList;
	}

	private void getImageSortedList(List<MediagalleryBean> mediaGalleryList) {

		Collections.sort(mediaGalleryList, new Comparator<MediagalleryBean>() {
			@Override
			public int compare(MediagalleryBean mediagalleryBean1, MediagalleryBean mediagalleryBean2) {
				int soretedResult = 0;
				if (mediagalleryBean1.getSeq() != null && mediagalleryBean2.getSeq() != null) {
					soretedResult = mediagalleryBean1.getSeq().compareToIgnoreCase(mediagalleryBean2.getSeq());
				}
				return soretedResult;
			}
		});

	}

	private boolean isImageEligible(AttrNmBean attrNmBean) {

		String currentCountry = StringUtils.EMPTY;
		String[] imageCountry = null;
		boolean imageEligible = false;
		boolean countryEligible = false;

		if ((languageValue != null) && ((languageValue.getLanguage() != null)
				&& (!languageValue.getLanguage().equals(StringUtils.EMPTY)))) {

			if ((languageValue.getCountry() != null) && (!languageValue.getCountry().equals(StringUtils.EMPTY))) {
				currentCountry = languageValue.getCountry();
			}

			Page countryPage = currentPage.getAbsoluteParent(2);
			String countryString = countryPage.getName();
			if ((!currentCountry.equalsIgnoreCase(countryString)) && (!countryString.equals(LANGUAGE_MASTERS))) {
				currentCountry = countryString.toUpperCase();
			}
		}

		if (attrNmBean.getCountry() != null) {
			imageCountry = attrNmBean.getCountry().split(",");
		}

		if (((imageCountry != null) && (imageCountry[0].equals(GLOBAL)))
				|| ((imageCountry != null) && (ArrayUtils.contains(imageCountry, currentCountry)))) {
			countryEligible = true;
		}

		if (countryEligible) {
			imageEligible = true;
		}

		return imageEligible;
	}

	public void getSortedList(List<SkuCardBean> skuSellList) {

		Collections.sort(skuSellList, new Comparator<SkuCardBean>() {
			@Override
			public int compare(SkuCardBean skuCardBean1, SkuCardBean skuCardBean2) {
				int soretedResult = 0;
				if (skuCardBean1 != null && skuCardBean2 != null) {
					soretedResult = Byte.compare(skuCardBean1.getPriority(), skuCardBean2.getPriority());
				}
				return soretedResult;
			}
		});
	}

	/**
	 * @return the productDetailsCardBean
	 */
	public ProductDetailsCardBean getProductDetailsCardBean() {
		return productDetailsCardBean;
	}

	public String getIsSkuPage() {
		return isSkuPage;
	}

	public void setIsSkuPage(String isSkuPage) {
		this.isSkuPage = isSkuPage;
	}

	public String getViewType() {
		return viewType;
	}

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}

}
