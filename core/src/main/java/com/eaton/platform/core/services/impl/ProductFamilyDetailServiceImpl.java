package com.eaton.platform.core.services.impl;

import com.day.cq.search.QueryBuilder;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.webservicesupport.ConfigurationManager;
import com.eaton.platform.core.bean.ProductFamilyDetails;
import com.eaton.platform.core.bean.ProductFamilyPIMDetails;
import com.eaton.platform.core.bean.ProductSupportBean;
import com.eaton.platform.core.bean.builders.product.BeanFiller;
import com.eaton.platform.core.bean.builders.product.exception.MissingFillingBeanException;
import com.eaton.platform.core.bean.builders.product.impl.PfpTaxonomyGroupFiller;
import com.eaton.platform.core.bean.builders.product.impl.ProductFamilyPageFiller;
import com.eaton.platform.core.bean.builders.product.impl.SkuDataBeanFiller;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.*;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.EatonSiteConfigService;
import com.eaton.platform.core.services.ProductFamilyDetailService;
import com.eaton.platform.core.threadlocal.ThreadGlobalState;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsBean;
import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.QueryManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;


@Component(service = ProductFamilyDetailService.class,immediate = true)
public class ProductFamilyDetailServiceImpl implements ProductFamilyDetailService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductFamilyDetailServiceImpl.class);
	public static final String PRODUCT_TYPE_VALUE_CQDATA = "product_type/ValueCQDATA";
	private static final String PRIMARY_SUB_CATEGORY = "primarySubCategory";
	private static final String HTB_TITLE_LIST = "howtoBuyTitleList";

	@Reference
	private AdminService adminService;

	@Reference
	private EatonSiteConfigService eatonSiteConfigService;

	@Reference
	private QueryBuilder queryBuilder;


	@Override
	public ProductFamilyDetails getProductFamilyDetailsBean(final Page currentPage) {

		LOGGER.debug("ProductFamilyDetailService :: getProductFamilyDetailsBean() :: Started");
		String pimPath = StringUtils.EMPTY;
		ProductFamilyDetails productFamilyDetails = null;
		final String country = CommonUtil.getCountryFromPagePath(currentPage);
		final Optional<SiteResourceSlingModel> siteConfig = eatonSiteConfigService.getSiteConfig(currentPage);

		if (null != adminService && siteConfig.isPresent()) {
			productFamilyDetails = new ProductFamilyDetails();
			try (ResourceResolver adminServiceReadService = adminService.getReadService()) {
				final ValueMap currentPageValueMap = currentPage.getContentResource().getValueMap();

				if (currentPageValueMap.containsKey(CommonConstants.PAGE_PIM_PATH)) {
					pimPath = currentPageValueMap.get(CommonConstants.PAGE_PIM_PATH, StringUtils.EMPTY);
					LOGGER.debug("ProductFamilyDetailService :: getProductFamilyDetailsBean() :: Found PIM in following page property" + CommonConstants.PAGE_PIM_PATH);
				} else if (currentPageValueMap.containsKey(CommonConstants.PAGE_PIM_PATHS)) {
					pimPath = currentPageValueMap.get(CommonConstants.PAGE_PIM_PATHS, StringUtils.EMPTY);
					LOGGER.debug("ProductFamilyDetailService :: getProductFamilyDetailsBean() :: Found PIM in following page property" + CommonConstants.PAGE_PIM_PATHS);
				}

				if (StringUtils.isNotEmpty(pimPath)) {
					final Resource pimResourcePimPath = adminServiceReadService.resolve(pimPath);

					if (null != pimResourcePimPath) {
						productFamilyDetails.setPrimarySubCategory(currentPageValueMap.containsKey(PRIMARY_SUB_CATEGORY)
								? currentPageValueMap.get(PRIMARY_SUB_CATEGORY, StringUtils.EMPTY)
								: StringUtils.EMPTY);
						final PIMResourceSlingModel pimResourceSlingModel = pimResourcePimPath
								.adaptTo(PIMResourceSlingModel.class);
						pimResourceSlingModel.setCurrentCountry(country);


						final String pdhRecordPath = pimResourceSlingModel.getPdhRecordPath();
						productFamilyDetails.setPdhRecordPath(pdhRecordPath);

						final String pdhPrimaryImg = pimResourceSlingModel.getPdhPrimaryImg();
						productFamilyDetails.setPdhPrimaryImg(pdhPrimaryImg);
						if (null != pimResourceSlingModel.getRepfl()){
							productFamilyDetails.setRepfl(pimResourceSlingModel.getRepfl());
						}

						final String spinnerImage = pimResourceSlingModel.getSpinImage();
						final String spinnerPDH = pimResourceSlingModel.getSpinImages();
						if(!StringUtils.isEmpty(spinnerImage)) {
							productFamilyDetails.setPdhSpinImage(spinnerImage);
							productFamilyDetails.setPdhPrimaryImg(spinnerImage);
						} else if(!StringUtils.isEmpty(spinnerPDH)) {
							productFamilyDetails.setPdhSpinImage(spinnerPDH);
							productFamilyDetails.setPdhPrimaryImg(spinnerPDH);
						}
						final Resource pdhResource = adminServiceReadService.getResource(pdhRecordPath);

						if (null != pdhResource) {
							final String inventoryId = getInventoryID(pdhResource);
							productFamilyDetails.setInventoryID(inventoryId);
							final ValueMap pdhRecordValueMap = pdhResource.getValueMap();
							if (null != pdhRecordValueMap) {
								final Optional<String> productTypeOptional = Optional.ofNullable(CommonUtil
										.getStringProperty(pdhRecordValueMap, PRODUCT_TYPE_VALUE_CQDATA));
								if (productTypeOptional.isPresent()) {
									productFamilyDetails.setProductType(productTypeOptional.get());
								}
							}
						}

						productFamilyDetails.setExtensionID(pimResourceSlingModel.getExtensionId());
						final SiteResourceSlingModel siteConfigModel = siteConfig.get();
						final boolean overRidePDH = Boolean.parseBoolean(siteConfigModel.getOverridePDHData());
						updateProductFamilyDetails(productFamilyDetails, pimResourceSlingModel, currentPage);

						final HowToBuyOptionsModel howToBuyOptions = pimResourceSlingModel.getHowToBuy(currentPage);
						final Optional<ConfigurationManager> configurationManagerOptional = Optional
								.ofNullable(adminServiceReadService.adaptTo(ConfigurationManager.class));
						final Optional<Resource> siteConfigResource = eatonSiteConfigService.getSiteConfigResource(currentPage, configurationManagerOptional);
						if (howToBuyOptions != null && siteConfigResource.isPresent()) {
							final String[] howTobuySiteConfig = siteConfigResource.get().
									getValueMap().get(HTB_TITLE_LIST, String[].class);
							String primaryProductTaxonomy = pimResourceSlingModel.getPrimaryProductTaxonomy();
							productFamilyDetails
									.setHowToBuyList(howToBuyOptions.forCountry(country, howTobuySiteConfig,currentPage,adminServiceReadService, primaryProductTaxonomy));
							LOGGER.debug("ProductFamilyDetailService :: getProductFamilyDetailsBean() :: HowToBuyOption Set");
						}

						final SecondaryLinksModel secondaryLinks = pimResourceSlingModel.getSecondaryLinks(currentPage);
						if (null != secondaryLinks) {
							productFamilyDetails.setSecondaryLinksList(secondaryLinks.forCountry(country));
							LOGGER.debug("ProductFamilyDetailService :: getProductFamilyDetailsBean() :: SecondaryLinksList Set");
						}

						final TopicLinksModel topicLinks = pimResourceSlingModel.getResourceSection(currentPage);
						if (null != topicLinks) {
							productFamilyDetails.setTopicLinkWithIconList(topicLinks.forCountry(country));
							LOGGER.debug("ProductFamilyDetailService :: getProductFamilyDetailsBean() :: TopicLink Set");
						}

						final SupportInfoModel supportInfo = pimResourceSlingModel.getSupportInfo(currentPage);

						if (supportInfo != null) {
							final ProductSupportBean supportInfoBean = supportInfo.forCountry(pdhResource, overRidePDH, country);
							if (supportInfoBean != null) {
								productFamilyDetails.setProductSupportBean(supportInfoBean);
								LOGGER.debug("ProductFamilyDetailService :: getProductFamilyDetailsBean() :: Product Support Set");
							}
						}

						final Resource skuCardAttributeListResource = pimResourceSlingModel.getSkuCardAttributes();
						if (null != skuCardAttributeListResource) {
							final Iterator<Resource> items = skuCardAttributeListResource.listChildren();
							final List<String> skuCardAttributeList = new ArrayList<>();
							while (items.hasNext()) {
								final Resource item = items.next();
								ValueMap properties = item.getValueMap();
								skuCardAttributeList.add(CommonUtil.getStringProperty(properties, CommonConstants.SKU_CARD_ATTRIBUTE_NAME));
							}
							productFamilyDetails.setSkuCardAttributeList(skuCardAttributeList);
							LOGGER.debug("ProductFamilyDetailService :: getProductFamilyDetailsBean() :: SKU Card Attribute List Set");
						}

						updateProductFamilyAEMPath(currentPage, adminServiceReadService,
								productFamilyDetails, pimResourceSlingModel);
						updateProductFamilyPageTags(adminServiceReadService, productFamilyDetails, pimResourceSlingModel, currentPage);

						if (overRidePDH) {
							setOverRidePDHDataValue(productFamilyDetails, pimResourceSlingModel);
						}

						productFamilyDetails.setAltPrimaryImage(pimResourceSlingModel.getAltPrimaryImage());
						productFamilyDetails.setSecondaryImage(pimResourceSlingModel.getSecondaryImage());
						productFamilyDetails.setSpinImages(pimResourceSlingModel.getSpinImages());

						if ((pimResourceSlingModel.getShowLongDesc() != null)
								&& ("true".equals(pimResourceSlingModel.getShowLongDesc()))) {
							productFamilyDetails.setShowLongDescription(true);
						} else {
							productFamilyDetails.setShowLongDescription(false);
						}
						if (null != pimResourceSlingModel.getPrimaryProductTaxonomy()) {
								String primaryProductTaxonomy = pimResourceSlingModel.getPrimaryProductTaxonomy();
								productFamilyDetails.setPrimaryProductTaxonomy(primaryProductTaxonomy);

						}
					}
				}
			}
		}
		LOGGER.debug("ProductFamilyDetailService :: getProductFamilyDetailsBean() :: Ended");
		return productFamilyDetails;

	}

	private void updateProductFamilyAEMPath(Page currentPage, ResourceResolver adminServiceReadService,
											ProductFamilyDetails productFamilyDetails,
											PIMResourceSlingModel pimResourceSlingModel) {
		LOGGER.debug("ProductFamilyDetailServiceImpl : Entered into updateProductFamilyAEMPath() method");
		String productFamilyPage;
		if (pimResourceSlingModel.getDataFromSingleProductFamilyPage() != null
				&& ("true").equals(pimResourceSlingModel.getDataFromSingleProductFamilyPage())) {
            productFamilyPage = pimResourceSlingModel.getProductFamilyPage();
            if (null != productFamilyPage) {
                try {
                    String familyPageName = org.apache.commons.lang3.StringUtils
                            .substringAfterLast(productFamilyPage, "/");
                    Page newPage = currentPage.getAbsoluteParent(CommonConstants.COUNTRY_PAGE_DEPTH + 1);
                    String currentPagePath = newPage.getPath();
                    if (!currentPagePath.contains("language-masters")) {
                        Session adminSession = adminServiceReadService.adaptTo(Session.class);
                        QueryManager queryManager = adminSession.getWorkspace().getQueryManager();
                        String sqlStatement = "SELECT * FROM [cq:Page] AS S WHERE NAME() = '"
                                + familyPageName + "' AND ISDESCENDANTNODE(S,'" + currentPagePath + "')";
                        javax.jcr.query.Query query = queryManager
                                .createQuery(sqlStatement, "JCR-SQL2");
                        javax.jcr.query.QueryResult result = query.execute();
                        NodeIterator nodeIter = result.getNodes();
                        nodeIter.getSize();
                        while (nodeIter.hasNext()) {
                            Node temp = (Node) nodeIter.next();
                            String queryPagePath = temp.getPath();
                            productFamilyDetails.setProductFamilyAEMPath(queryPagePath);
                        }
                    } else {
                        productFamilyDetails.setProductFamilyAEMPath(productFamilyPage);
                    }
                } catch (InvalidQueryException e) {
                    LOGGER.error("Invalid Query Exception in getProductFamilyDetailsBean method", e);
                } catch (RepositoryException e) {
                    LOGGER.error("RepositoryException in getProductFamilyDetailsBean method", e);
                }
            }
        }
		LOGGER.debug("ProductFamilyDetailServiceImpl : Exit from updateProductFamilyAEMPath() method");
	}

	private void updateProductFamilyPageTags(final ResourceResolver adminServiceReadService,
											 final ProductFamilyDetails productFamilyDetails,
											 final PIMResourceSlingModel pimResourceSlingModel, Page currentPage) {
		LOGGER.debug("ProductFamilyDetailServiceImpl : Entered into updateProductFamilyPageTags() method");
		// Set tags configured on PIM page into product family details.
		if (null != pimResourceSlingModel.getTags() && pimResourceSlingModel.getTags().length > 0) {
            final String[] pimTags = pimResourceSlingModel.getTags();
			final Optional<TagManager> tagManagerOptional = Optional.ofNullable(adminServiceReadService.adaptTo(TagManager.class));
			final List<String> tagStringArray = Arrays.asList(pimTags);
			List<String> tagList = new ArrayList<>();
			if (tagManagerOptional.isPresent()) {
				TagManager tagManager = tagManagerOptional.get();
				tagStringArray.forEach(tagString -> {
					Tag resolvedTag = tagManager.resolve(tagString);
					if (null != resolvedTag) {
						tagList.add(resolvedTag.getPath());
					}
				});
			}
            productFamilyDetails.setPimTags(tagList);
        }
		LOGGER.debug("ProductFamilyDetailServiceImpl : Exit from updateProductFamilyPageTags() method");
	}

	private void updateProductFamilyDetails(final ProductFamilyDetails productFamilyDetails, final PIMResourceSlingModel pimResourceSlingModel, Page currentPage) {
		LOGGER.debug("ProductFamilyDetailServiceImpl : Entered into updateProductFamilyDetails() method");
		productFamilyDetails
                .setModelTabName(Optional.ofNullable(pimResourceSlingModel.getMiddleTabTitle())
                        .orElse(StringUtils.EMPTY));
		productFamilyDetails.setPrimaryCTALabel(Optional
                .ofNullable(pimResourceSlingModel.getPrimaryCTALabel(currentPage))
                .orElse(StringUtils.EMPTY));
		productFamilyDetails.setPrimaryCTAURL(Optional
                .ofNullable(pimResourceSlingModel.getPrimaryCTAURL(currentPage))
                .orElse(StringUtils.EMPTY));
		productFamilyDetails.setPrimaryCTANewWindow(Optional
                .ofNullable(pimResourceSlingModel.getPrimaryCTANewWindow(currentPage))
                .orElse(StringUtils.EMPTY));
		productFamilyDetails.setPrimaryCTAEnableSourceTracking(Optional
				.ofNullable(pimResourceSlingModel.getPrimaryCTAEnableSourceTracking(currentPage))
				.orElse(StringUtils.EMPTY));
		productFamilyDetails.setIsSuffixDisabled(Optional
                .ofNullable(pimResourceSlingModel.getIsSuffixDisabled())
                .orElse(StringUtils.EMPTY));
		productFamilyDetails.setMetaTitleOverviewTab(Optional
                .ofNullable(pimResourceSlingModel.getSeoTitleOverviewTab())
                .orElse(StringUtils.EMPTY));
		productFamilyDetails.setMetaDescOverviewTab(Optional
                .ofNullable(pimResourceSlingModel.getSeoDescOverviewTab())
                .orElse(StringUtils.EMPTY));
		productFamilyDetails.setMetaTitleModelsTab(Optional
                .ofNullable(pimResourceSlingModel.getSeoTitleModelsTab())
                .orElse(StringUtils.EMPTY));
		productFamilyDetails.setMetaTitleResourcesTab(Optional
                .ofNullable(pimResourceSlingModel.getSeoTitleResourcesTab())
                .orElse(StringUtils.EMPTY));
		productFamilyDetails.setMetaDescriptionModelsTab(Optional
                .ofNullable(pimResourceSlingModel.getSeoDescModelsTab())
                .orElse(StringUtils.EMPTY));
		productFamilyDetails.setMetaDescriptionResourcesTab(Optional
                .ofNullable(pimResourceSlingModel.getSeoDescResourcesTab())
                .orElse(StringUtils.EMPTY));
		productFamilyDetails
                .setMetaDescriptionResourcesTab(Optional
                        .ofNullable(pimResourceSlingModel.getSeoDescResourcesTab())
                        .orElse(StringUtils.EMPTY));
		LOGGER.debug("ProductFamilyDetailServiceImpl : Exit from updateProductFamilyDetails() method");
	}

	private final void setOverRidePDHDataValue(final ProductFamilyDetails productFamilyDetails,
											   final PIMResourceSlingModel pimResourceSlingModel) {
		LOGGER.debug("ProductFamilyDetailServiceImpl : Entered into setOverRidePDHDataValue() method");
		productFamilyDetails.setProductFamilyName(Optional.ofNullable(pimResourceSlingModel.getProductName())
				.orElse(Optional.ofNullable(pimResourceSlingModel.getPdhProdName()).orElse(StringUtils.EMPTY)));
		productFamilyDetails.setMarketingDesc(Optional.ofNullable(pimResourceSlingModel.getMarketingDesc())
				.orElse(Optional.ofNullable(pimResourceSlingModel.getPdhProdDesc()).orElse(StringUtils.EMPTY)));
		productFamilyDetails.setCoreFeatureDescription(Optional.ofNullable(pimResourceSlingModel.getCoreFeatures())
				.orElse(Optional.ofNullable(pimResourceSlingModel.getPdhCoreFeatures()).orElse(StringUtils.EMPTY)));
		productFamilyDetails.setPrimaryImage(Optional.ofNullable(pimResourceSlingModel.getPrimaryImage())
				.orElse(Optional.ofNullable(pimResourceSlingModel.getPdhPrimaryImg()).orElse(StringUtils.EMPTY)));
		LOGGER.debug("ProductFamilyDetailServiceImpl : Exit from setOverRidePDHDataValue() method");

	}

	private final String getInventoryID(final Resource pdhResource) {
		LOGGER.debug("ProductFamilyDetailServiceImpl : Entered into getInventoryID() method");
		String inventoryId = StringUtils.EMPTY;
		if (null != pdhResource) {
            final Resource inventoryIdResource = pdhResource.getParent();
            if (null != inventoryIdResource) {
				inventoryId = inventoryIdResource.getName();
            }
        }
		LOGGER.debug("ProductFamilyDetailServiceImpl : Exit from getInventoryID() method");
        return inventoryId;
	}

    @Override
    public ProductFamilyPIMDetails getProductFamilyPIMDetailsBean(SKUDetailsBean skuData, Page currentPage) {
        LOGGER.debug("ProductFamilyDetailServiceImpl : Entered into getProductFamilyPIMDetailsBean() method");
        if (skuData == null) {
            return new ProductFamilyPIMDetails();
        }
        return getProductFamilyPIMDetailsBean(skuData.getExtensionId(), skuData.getInventoryId(), currentPage);
    }

    @Override
    public ProductFamilyPIMDetails getProductFamilyPIMDetailsBean(String extensionId, String inventoryId, Page containingPage) {
        if (Strings.isNullOrEmpty(extensionId) || Strings.isNullOrEmpty(inventoryId)) {
            return new ProductFamilyPIMDetails();
        }
        String threadLocalKey = inventoryId + extensionId;
        if (isOnThreadLocal(threadLocalKey)) {
            return ThreadGlobalState.getProductFamilyPIMDetails(threadLocalKey);
        }
		ProductFamilyPIMDetails productFamilyPIMDetails = new ProductFamilyPIMDetails();
        try (ResourceResolver readResolver = adminService.getReadService()) {
            BeanFiller<ProductFamilyPIMDetails> skuBeanFiller = new SkuDataBeanFiller(inventoryId, extensionId, containingPage, readResolver, eatonSiteConfigService);
			BeanFiller<ProductFamilyPIMDetails> productFamilyPageFiller = new ProductFamilyPageFiller(containingPage, readResolver, inventoryId, extensionId);
			BeanFiller<ProductFamilyPIMDetails> pfpTaxonomyGroupFiller = new PfpTaxonomyGroupFiller(containingPage, readResolver, inventoryId, extensionId);
            productFamilyPIMDetails = ProductFamilyDetails
                    .custom(skuBeanFiller, productFamilyPageFiller, pfpTaxonomyGroupFiller)
                    .build();
            if (productFamilyPIMDetails != null) {
                registerOnThreadLocal(productFamilyPIMDetails, threadLocalKey);
            }
            LOGGER.debug("ProductFamilyDetailServiceImpl : Exit from getProductFamilyPIMDetailsBean() method");
        } catch (MissingFillingBeanException e) {
            LOGGER.error("ProductFamilyDetailServiceImpl :: getProductFamilyPIMDetailsBean :: MissingFillingBeanException : {}", e.getMessage(), e);
        }
        return productFamilyPIMDetails;
    }

	private static boolean isOnThreadLocal(String threadLocalKey) {
		return ThreadGlobalState.getProductFamilyPIMDetails(threadLocalKey) != null;
	}

	private static void registerOnThreadLocal(ProductFamilyPIMDetails productFamilyPIMDetails, String threadLocalKey) {
		ThreadGlobalState.registerProductFamilyPIMDetails(threadLocalKey, productFamilyPIMDetails);
	}
}
