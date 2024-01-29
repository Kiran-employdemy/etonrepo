package com.eaton.platform.core.models;

import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.Asset;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.bean.AttrNmBean;
import com.eaton.platform.core.bean.CountryLanguageCodeBean;
import com.eaton.platform.core.bean.Dimensions;
import com.eaton.platform.core.bean.ImageGroupBean;
import com.eaton.platform.core.bean.ProductFamilyDetails;
import com.eaton.platform.core.constants.AssetConstants;
import com.eaton.platform.core.constants.AssetDownloadConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.asset.DynamicMediaAsset;
import com.eaton.platform.core.models.brandingselectortool.BrandColorSelectionConfigModel;
import com.eaton.platform.core.models.eatonsiteconfig.EatonSiteConfigModel;
import com.eaton.platform.core.models.producttab.ProductTabsVersionChecker;
import com.eaton.platform.core.services.*;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.core.util.SecureUtil;
import com.eaton.platform.core.util.SiteConfigUtil;
import com.eaton.platform.core.util.SkuImageParser;
import com.eaton.platform.integration.auth.services.AuthenticationServiceConfiguration;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsBean;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsResponseBean;
import com.eaton.platform.integration.endeca.services.EndecaService;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceNotFoundException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.query.SlingQuery;
import org.apache.sling.xss.XSSAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.eaton.platform.core.constants.AssetConstants.DM_PROFILE_MEGA_MENU_FEATURED_LIST_DESK;
import static org.apache.sling.query.SlingQuery.$;

@Model(adaptables = { Resource.class,
        SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class MetaTagsModel {

    public static final String MIDDLE_TAB_HASH = "#tab-2";
    private String pageType;
    private List<Dimensions> dimensionTagValues;
    private MetaTagValuesModel metaTagGenericValuesModel;
    private List<CountryLanguageCodeBean> hreflangValues;
    private List<SiteVerificationCodeModel> siteVerificationCodeModelList;

    /** The site resource sling model. */
    private SiteResourceSlingModel siteResourceSlingModel;

    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(MetaTagsModel.class);

    /** The Constant SIMPLE_DATE_FORMAT. */
    private static final String SIMPLE_DATE_FORMAT = "MMMM d, yyyy";

    /** The Constant KEYWORD_PRODUCT_FAMILY_NAME. */
    private static final String KEYWORD_PRODUCT_FAMILY_NAME = "{PRODUCT_FAMILY_NAME}";

    /** The Constant KEYWORD_PRODUCT_FAMILY_MKTG_DESC. */
    private static final String KEYWORD_PRODUCT_MKTG_DESC = "{PRODUCT_MKTG_DESC}";

    /** The Constant X_DEFAULT. */
    private static final String X_DEFAULT = "x-default";

    /** The Constant HREF_LANG_CODE. */
    private static final String HREF_LANG_CODE = "hrefLangCode";

    /** The Constant KEYWORD_CATALOG_NUMBER. */
    private static final String KEYWORD_CATALOG_NUMBER = "{CATALOG_NUMBER}";

    /** The sku details response bean. */
    private SKUDetailsBean skuDetailsBean;

    /** The Constant ZOOM. */
    private static final String ZOOM = "1000x1000";

    private static boolean isSubmittalBuilder;

    private String currentPageURL;

    @Inject
    @Source("sling-object")
    protected SlingHttpServletRequest slingRequest;

    @Inject
    protected Page currentPage;

    protected ValueMap currentPageProperties;

    @Inject
    protected ProductFamilyDetailService productFamilyDetailService;

    @Inject
    protected EatonSiteConfigService eatonSiteConfigService;

    @Inject
    protected EndecaRequestService endecaRequestService;

    @Inject
    protected EndecaService endecaService;

    protected TagManager tagManager;

    protected InheritanceValueMap currentPageInheritedProp;

    @Inject
    protected PageManager pageManager;

    @OSGiService
    protected AdminService adminService;

    @Inject
    @Source("osgi-services")
    protected EatonConfigService eatonConfigService;

    @Inject
    @Source("osgi-services")
    protected CountryLangCodeConfigService countryLangCodeConfigService;

    @Inject
    @Source("osgi-services")
    protected MetaTagsLinkDomainService metaTagsLinkDomainService;

    @Inject
    protected XSSAPI xssapi;

    @Inject
    protected AuthenticationServiceConfiguration authenticationServiceConfiguration;

    @Inject
    protected CloudConfigService cloudConfigService;

    @Self
    @Via("resource")
    private Resource resource;

    private String excludeCountryList;

    @Inject
    @Via("resource")
    private String canonicalPages;

    @PostConstruct
    protected void init() {
        LOG.debug("MetaTagsModel :: init() :: Started");
        if (null != adminService) {
            try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
                String currentPagePath = null;
                currentPageInheritedProp = new HierarchyNodeInheritanceValueMap(resource);
                if (adminResourceResolver != null) {
                    tagManager = adminResourceResolver.adaptTo(TagManager.class);
                }
                dimensionTagValues = new ArrayList<>();
                Map<String, Dimensions> dimensionTagMap = new HashMap<>();
                final Locale locale = CommonUtil.getLocaleFromPagePath(currentPage);
                String country = CommonUtil.getCountryFromPagePath(currentPage);
                currentPageProperties = currentPage.getProperties();
                currentPageURL = getCurrentPageURL();

                metaTagGenericValuesModel = new MetaTagValuesModel();
                String[] enabledTemplateNames = { CommonConstants.SECURE_GENERIC_TEMPLATE,
                        CommonConstants.SECURE_CATEGORY_TEMPLATE_NAME,
                        CommonConstants.SECURE_CATEGORY_WITH_CARDS_TEMPLATE_NAME,
                        CommonConstants.SECURE_PRODUCT_FAMILY_TEMPLATE_NAME,
                        CommonConstants.SECURE_TEMPLATE_NAME };
                if (null != currentPageProperties
                        && null != currentPageProperties.get(CommonConstants.TEMPLATE_PROP_KEY) &&
                        Arrays.stream(enabledTemplateNames).anyMatch(
                                currentPageProperties.get(CommonConstants.TEMPLATE_PROP_KEY).toString()::contains)) {
                    Boolean securePageTemplate = Arrays.stream(enabledTemplateNames).anyMatch(
                            currentPageProperties.get(CommonConstants.TEMPLATE_PROP_KEY).toString()::contains);
                    String page = (String) currentPageProperties.get(CommonConstants.SECURE_PROP_NAME_SECURE_PAGE);
                    String[] accountType = (String[]) currentPageProperties
                            .get(CommonConstants.SECURE_PROP_NAME_ACCOUNT_TYPE);
                    String[] productCategories = (String[]) currentPageProperties
                            .get(CommonConstants.SECURE_PROP_NAME_PRODUCT_CATEGORIES);
                    String[] applicationAccess = (String[]) currentPageProperties
                            .get(CommonConstants.SECURE_PROP_NAME_APPLICATION_ACCESS);
                    String[] countries = (String[]) currentPageProperties.get(CommonConstants.COUNTRIES_FIELD);
                    String[] partnerProgrammeAndTierLevelType = (String[]) currentPageProperties
                            .get(CommonConstants.SECURE_PROP_NAME_PARTNER_PROGRAMME_AND_TIER_LEVEL_TYPE);
                    metaTagGenericValuesModel = new MetaTagValuesModel();
                    // Secure Page Template
                    metaTagGenericValuesModel.setSecurePageTemplate(securePageTemplate);

                    // SecurePage
                    if (null != page) {
                        metaTagGenericValuesModel.setSecurePage(page);
                    }

                    // AccountType
                    if (null != accountType && accountType.length > 0) {
                        accountType = SecureUtil.getSecureTagNames(accountType, adminResourceResolver, false);
                        metaTagGenericValuesModel
                                .setAccountTypeTagNames(SecureUtil.getPipeSeparatedSecuredTagNames(accountType));
                    }

                    // Product Category
                    if (null != productCategories && productCategories.length > 0) {
                        productCategories = SecureUtil.getSecureTagNames(productCategories, adminResourceResolver,
                                false);
                        metaTagGenericValuesModel.setProductCategoriesTagNames(
                                SecureUtil.getPipeSeparatedSecuredTagNames(productCategories));
                    }

                    // Application Access
                    if (applicationAccess != null && applicationAccess.length > 0) {
                        applicationAccess = SecureUtil.getSecureTagNames(applicationAccess, adminResourceResolver,
                                false);
                        metaTagGenericValuesModel.setApplicationAccessTagNames(
                                SecureUtil.getPipeSeparatedSecuredTagNames(applicationAccess));
                    }

                    // Countries
                    if (countries != null && countries.length > 0) {
                        countries = SecureUtil.getSecureTagNames(countries, adminResourceResolver, false);
                        metaTagGenericValuesModel
                                .setCountryTagNames(SecureUtil.getPipeSeparatedSecuredTagNames(countries));
                    }

                    // Partner Programme and Tier Level Type
                    if (partnerProgrammeAndTierLevelType != null && partnerProgrammeAndTierLevelType.length > 0) {
                        partnerProgrammeAndTierLevelType = SecureUtil.getSecureTagNamesPartnerProgrammeAndTierLevel(partnerProgrammeAndTierLevelType, adminResourceResolver);
                        metaTagGenericValuesModel.setPartnerProgrammeAndTierLevelTypeTagNames(
                                SecureUtil.getPipeSeparatedSecuredTagNames(partnerProgrammeAndTierLevelType));
                    }
                }
                // getting PageType
                if (currentPageProperties != null && currentPageProperties.get(CommonConstants.PAGE_TYPE) != null) {
                    pageType = currentPageProperties.get(CommonConstants.PAGE_TYPE).toString();
                }

                if ((currentPage.getPath() != null) && (!currentPage.getPath().equals(StringUtils.EMPTY))) {
                    currentPagePath = CommonUtil.dotHtmlLink(currentPage.getPath(), adminResourceResolver);
                }
                SimpleDateFormat fmt = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
                String formattedReplicationDate = null;
                if (currentPage.getProperties().get(CommonConstants.REPLICATION_DATE) != null) {
                    formattedReplicationDate = CommonUtil.getDateProperty(currentPage.getProperties(),
                            CommonConstants.REPLICATION_DATE, fmt);
                }

                if (currentPage.getProperties().get(CommonConstants.PUBLICATION_DATE) != null) {
                    formattedReplicationDate = CommonUtil.getDateProperty(currentPage.getProperties(),
                            CommonConstants.PUBLICATION_DATE, fmt);
                }

                if (formattedReplicationDate != null) {
                    metaTagGenericValuesModel.setPagePublishDate(formattedReplicationDate);
                }

                metaTagGenericValuesModel.setCurrentPageLanguage(CommonUtil.getUpdatedLocaleFromPagePath(currentPage));
                metaTagGenericValuesModel.setCurrentPageCountry(country);

                if (StringUtils.isNotEmpty(currentPage.getPageTitle())) {
                    metaTagGenericValuesModel.setPageTitle(currentPage.getPageTitle());
                }
                if (StringUtils.isNotEmpty(currentPage.getNavigationTitle())) {
                    metaTagGenericValuesModel.setNavigationTitle(currentPage.getNavigationTitle());
                }

                if (pageType != null) {
                    metaTagGenericValuesModel.setAuthoredPageType(pageType);
                }

                ProductFamilyDetails productFamilyDetails = productFamilyDetailService
                        .getProductFamilyDetailsBean(currentPage);
                if (pageType != null && productFamilyDetails != null
                        && pageType.equals(CommonConstants.PAGE_TYPE_PRODUCT_FAMILY_PAGE)) {

                    if ((currentPagePath != null) && (!currentPagePath.equals(StringUtils.EMPTY))) {
                        metaTagGenericValuesModel.setFamilyPageUrl(currentPagePath);
                    }

                    if (productFamilyDetails.getProductFamilyName() != null) {
                        metaTagGenericValuesModel.setFamilyName(productFamilyDetails.getProductFamilyName());
                    }

                    if (productFamilyDetails.getPrimaryImage() != null) {
                        String primaryImage = generatePrimaryImage(productFamilyDetails.getPrimaryImage(),adminResourceResolver);
                        metaTagGenericValuesModel.setPrimaryImage(primaryImage);
                    }
                    if (productFamilyDetails.getMarketingDesc() != null) {
                        metaTagGenericValuesModel.setMaeketingDesc(productFamilyDetails.getMarketingDesc());
                    }
                    if (productFamilyDetails.getCoreFeatureDescription() != null) {
                        metaTagGenericValuesModel.setCoreFeature(productFamilyDetails.getCoreFeatureDescription());
                    }

                    metaTagGenericValuesModel.setMiddleTabName(productFamilyDetails.getModelTabName());
                    if ((currentPage.getPath() != null) && (!currentPage.getPath().equals(StringUtils.EMPTY))) {
                        metaTagGenericValuesModel = addMiddleTabUrlToMetaTagValueModel(adminResourceResolver);
                    }
                    tagManager = adminResourceResolver.adaptTo(TagManager.class);

                    currentPage = CommonUtil.getOriginalPage(currentPage, adminResourceResolver);

                    String[] tagsArray = (String[]) currentPage.getProperties().get(CommonConstants.CQ_TAGS);
                    List<Tag> tagsList = new ArrayList<>();
                    Boolean isProductDiscontinuedForthisCountry = false;
                    if ((tagsArray != null) && (tagsArray.length > 0)) {
                        String formatCQStr = "CQ Tags Length ";
                        String logCQLenght = String.format(formatCQStr, tagsArray.length);
                        LOG.debug(logCQLenght);
                        for (String tagItem : tagsArray) {
                            if (tagItem.contains(CommonConstants.DISCONTINUE_TAG_PATH)) {
                                isProductDiscontinuedForthisCountry = true;
                            }
                            if (tagManager != null) {
                                Tag cqTag = tagManager.resolve(tagItem);
                                if (cqTag != null) {
                                    tagsList.add(cqTag);
                                }
                            }
                        }
                    }
                    if (productFamilyDetails.getPimTags() != null) {
                        List<String> pimTags = productFamilyDetails.getPimTags();
                        String[] pimTagsArray = pimTags.toArray(new String[0]);
                        setCategoryAndDimensionalMapMetaData(pimTagsArray, tagManager, dimensionTagMap,
                                isProductDiscontinuedForthisCountry);
                    }
                    String formatTgStr = "TagList Length is ";
                    String logTagLenght = String.format(formatTgStr, tagsList.size());
                    LOG.debug(logTagLenght);

                    for (int i = 0; i < tagsList.size(); i++) {

                        Tag firstLevelTag = null;
                        Tag secondLevelTag = null;
                        Tag tag = tagsList.get(i);
                        if (tag != null) {
                            String dimValues = StringUtils.EMPTY;
                            String dimNames = StringUtils.EMPTY;

                            int count = 0;
                            while (!CommonUtil.isTagNameMatchesWithAnySiteTagNameSpace(tag.getName())) {
                                if (tag.getLocalizedTitle(locale) != null) {
                                    dimValues = dimValues + tag.getLocalizedTitle(locale) + CommonConstants.DOUBLE_PIPE;
                                }
                                if (tag.getTitle() != null) {
                                    dimValues = dimValues + tag.getTitle() + CommonConstants.DOUBLE_PIPE;
                                }
                                dimNames = dimNames + tag.getName() + CommonConstants.DOUBLE_PIPE;

                                if (count == 0) {
                                    secondLevelTag = tag;
                                } else if (count == 1) {
                                    firstLevelTag = tag;
                                } else {
                                    secondLevelTag = firstLevelTag;
                                    firstLevelTag = firstLevelTag.getParent();
                                }

                                tag = tag.getParent();
                                count = count + 1;
                                if (tag == null) {
                                    break;
                                }
                            }

                            if ((dimNames != null)
                                    && ((secondLevelTag != null) && (secondLevelTag.getName() != null))) {
                                int index = dimNames.lastIndexOf(secondLevelTag.getName());
                                if (index > 2) {
                                    if (index != dimNames.length()) {
                                        dimNames = dimNames.substring(0, index - 2);
                                    }
                                    if (index == 0) {
                                        dimNames = StringUtils.EMPTY;
                                    }
                                }
                            }

                            if ((dimValues != null) && ((secondLevelTag != null)
                                    && (secondLevelTag.getLocalizedTitle(locale) != null))) {
                                int index = dimValues.lastIndexOf(secondLevelTag.getLocalizedTitle(locale));
                                if (index > 2) {
                                    if (index != dimValues.length()) {
                                        dimValues = dimValues.substring(0, index - 2);
                                    }
                                }
                                if (index == 0) {
                                    dimValues = StringUtils.EMPTY;
                                }
                            }
                            if ((secondLevelTag != null) && (secondLevelTag.getTitle() != null)) {
                                LOG.info("secondLevelTag is not null then calculating the index");
                                if (dimValues != null) {
                                    int index = dimValues.lastIndexOf(secondLevelTag.getTitle());
                                    if (index > 2) {
                                        if (index != dimValues.length()) {
                                            dimValues = dimValues.substring(0, index - 2);
                                        }
                                        if (index == 0) {
                                            dimValues = StringUtils.EMPTY;
                                        }
                                    }
                                }
                            }
                            if (secondLevelTag != null && secondLevelTag.getName() != null) {
                                if (secondLevelTag.getName().equals(CommonConstants.CONTENT_TYPE_LOWER_CASE)) {
                                    metaTagGenericValuesModel.setContentType(dimNames);

                                }
                                if (secondLevelTag.getName().equals(CommonConstants.PAGE_TYPE)) {
                                    metaTagGenericValuesModel.setPageType(dimNames);
                                }
                            }
                        }
                    }
                } else if (pageType != null && !pageType.equalsIgnoreCase(CommonConstants.PAGE_TYPE_PRODUCT_SKU_PAGE)) {
                    /**
                     * This is to ensure that category id and categoryname is not being set for
                     * home,primary,site-map,site-search pages
                     */
                    if (!CommonConstants.categoryIdMetaTagExcludedPageTypes.contains(pageType)) {
                        String[] currentPageCqTags = (String[]) currentPage.getProperties()
                                .get(CommonConstants.CQ_TAGS);
                        if (null != currentPageCqTags && currentPageCqTags.length > 0) {
                            setCategoryAndDimensionalMapMetaData(currentPageCqTags, tagManager, dimensionTagMap, false);
                        }
                    }
                    if (currentPage.getNavigationTitle() != null) {
                        metaTagGenericValuesModel.setFamilyName(currentPage.getNavigationTitle());
                    } else if (currentPage.getPageTitle() != null) {
                        metaTagGenericValuesModel.setFamilyName(currentPage.getPageTitle());
                    } else {
                        metaTagGenericValuesModel.setFamilyName(currentPage.getTitle());
                    }

                    if (currentPage.getProperties().get(CommonConstants.TEASER_IMAGE_PATH) != null) {
                        String primaryImage = generatePrimaryImage(currentPage.getProperties().get(CommonConstants.TEASER_IMAGE_PATH).toString(),adminResourceResolver);
                        metaTagGenericValuesModel.setPrimaryImage(primaryImage);
                    }

                    final String primarySubCategory = currentPageProperties
                            .get(CommonConstants.PIM_PRIMARY_SUB_CATEGORY, StringUtils.EMPTY);
                    if ((!primarySubCategory.isEmpty())) {
                        metaTagGenericValuesModel.setCategoryName(
                                CommonUtil.getLinkTitle(null, primarySubCategory, adminResourceResolver));
                    }

                    if ((currentPagePath != null) && (!currentPagePath.equals(StringUtils.EMPTY))) {
                        metaTagGenericValuesModel.setFamilyPageUrl(currentPagePath);
                    }

                    if (currentPage.getProperties().get(CommonConstants.TEASER_DESCRIPTION) != null) {
                        metaTagGenericValuesModel.setMaeketingDesc(
                                currentPage.getProperties().get(CommonConstants.TEASER_DESCRIPTION).toString());
                    }
                    tagManager = adminResourceResolver.adaptTo(TagManager.class);
                    metaTagGenericValuesModel.setDescription(currentPage.getDescription());
                    currentPage = CommonUtil.getOriginalPage(currentPage, adminResourceResolver);
                    String[] tagsArray = (String[]) currentPage.getProperties().get(CommonConstants.CQ_TAGS);
                    List<Tag> tagsList = new ArrayList<>();

                    if ((tagsArray != null) && (tagsArray.length > 0)) {
                        String formatCQStr = "CQ Tags Length ";
                        String logCQLenght = String.format(formatCQStr, tagsArray.length);
                        LOG.debug(logCQLenght);
                        for (String tagItem : tagsArray) {
                            if (tagManager != null) {
                                Tag cqTag = tagManager.resolve(tagItem);
                                if (cqTag != null) {
                                    tagsList.add(cqTag);
                                }
                            }
                        }
                    }
                    String formatStr = "TagList Length is ";
                    String logTagLenght = String.format(formatStr, tagsList.size());
                    LOG.debug(logTagLenght);

                    for (int i = 0; i < tagsList.size(); i++) {

                        Tag firstLevelTag = null;
                        Tag secondLevelTag = null;
                        Dimensions dimensionTag = new Dimensions();
                        Tag tag = tagsList.get(i);
                        if (tag != null) {
                            String dimValues = StringUtils.EMPTY;
                            String dimNames = StringUtils.EMPTY;
                            int count = 0;
                            while (!CommonUtil.isTagNameMatchesWithAnySiteTagNameSpace(tag.getName())) {

                                if (tag.getLocalizedTitle(locale) != null) {
                                    dimValues = dimValues + tag.getLocalizedTitle(locale) + CommonConstants.DOUBLE_PIPE;
                                } else if (tag.getTitle() != null) {
                                    dimValues = dimValues + tag.getTitle() + CommonConstants.DOUBLE_PIPE;
                                }
                                dimNames = dimNames + tag.getName() + CommonConstants.DOUBLE_PIPE;

                                if (count == 0) {
                                    secondLevelTag = tag;
                                } else if (count == 1) {
                                    firstLevelTag = tag;
                                } else {
                                    secondLevelTag = firstLevelTag;
                                    firstLevelTag = firstLevelTag.getParent();
                                }

                                tag = tag.getParent();
                                count = count + 1;
                                if (tag == null) {
                                    break;
                                }
                            }
                            String dimNameStr = "dimNames" + i;
                            String logDimName = String.format(dimNameStr, dimNames);
                            LOG.debug(logDimName);
                            String dimValStr = "dimValues" + i;
                            String logDimVal = String.format(dimValStr, dimValues);
                            LOG.debug(logDimVal);
                            if ((dimNames != null)
                                    && ((secondLevelTag != null) && (secondLevelTag.getName() != null))) {
                                int index = dimNames.lastIndexOf(secondLevelTag.getName());
                                if (index > 2) {
                                    if (index != dimNames.length()) {
                                        dimNames = dimNames.substring(0, index - 2);
                                    }
                                } else if (index == 0) {
                                    dimNames = StringUtils.EMPTY;
                                }
                            }
                            if (dimValues != null) {
                                if ((secondLevelTag != null) && (secondLevelTag.getLocalizedTitle(locale) != null)) {
                                    int index = dimValues.lastIndexOf(secondLevelTag.getLocalizedTitle(locale));
                                    if (index > 2) {
                                        if (index != dimValues.length()) {
                                            dimValues = dimValues.substring(0, index - 2);
                                        }
                                    } else if (index == 0) {
                                        dimValues = StringUtils.EMPTY;
                                    }
                                } else if ((secondLevelTag != null) && (secondLevelTag.getTitle() != null)) {
                                    int index = dimValues.lastIndexOf(secondLevelTag.getTitle());
                                    if (index > 2) {
                                        if (index != dimValues.length()) {
                                            dimValues = dimValues.substring(0, index - 2);
                                        }
                                    } else if (index == 0) {
                                        dimValues = StringUtils.EMPTY;
                                    }
                                }
                            }
                            if (secondLevelTag != null && secondLevelTag.getName() != null) {
                                String formatStrTgName = "secondLevelTag Name";
                                String logTagName = String.format(formatStrTgName, secondLevelTag.getName());
                                LOG.debug(logTagName);
                                if (secondLevelTag.getName().equals(CommonConstants.CONTENT_TYPE_LOWER_CASE)) {
                                    metaTagGenericValuesModel.setContentType(dimNames);
                                } else if (secondLevelTag.getName().equals(CommonConstants.PAGE_TYPE)) {
                                    metaTagGenericValuesModel.setPageType(dimNames);
                                } else {
                                    LOG.debug(" Else Case");
                                    if (firstLevelTag != null && secondLevelTag != null) {
                                        if (dimensionTagMap.containsKey(
                                                firstLevelTag.getName() + "_" + secondLevelTag.getName())) {
                                            dimensionTag = dimensionTagMap
                                                    .get(firstLevelTag.getName() + "_" + secondLevelTag.getName());

                                            if (dimValues != null) {
                                                if ((dimensionTag.getValues() != null)
                                                        && (!dimensionTag.getValues().equals(StringUtils.EMPTY))) {
                                                    dimValues = dimensionTag.getValues() + CommonConstants.DOUBLE_PIPE
                                                            + dimValues;
                                                }
                                                dimensionTag.setValues(dimValues);
                                            }
                                            if (dimNames != null) {
                                                if ((dimensionTag.getNames() != null)
                                                        && (!dimensionTag.getNames().equals(StringUtils.EMPTY))) {
                                                    dimNames = dimensionTag.getNames() + CommonConstants.DOUBLE_PIPE
                                                            + dimNames;
                                                }
                                                dimensionTag.setNames(dimNames);
                                            }
                                        } else {
                                            LOG.debug("second case");
                                            dimensionTag
                                                    .setKey(firstLevelTag.getName() + "_" + secondLevelTag.getName());
                                            dimensionTag.setId(secondLevelTag.getName());
                                            if (secondLevelTag.getLocalizedTitle(locale) != null) {
                                                dimensionTag.setLabel(secondLevelTag.getLocalizedTitle(locale));
                                            } else if (secondLevelTag.getTitle() != null) {
                                                dimensionTag.setLabel(secondLevelTag.getTitle());
                                            }
                                            if (dimValues != null) {
                                                dimensionTag.setValues(dimValues);
                                            }
                                            if (dimNames != null) {
                                                dimensionTag.setNames(dimNames);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if ((dimensionTag.getKey() != null) && (!dimensionTag.getKey().equals(StringUtils.EMPTY))) {
                            if (!dimensionTagMap.containsKey(dimensionTag.getKey())) {
                                dimensionTagValues.add(dimensionTag);
                            }
                            dimensionTagMap.put(dimensionTag.getKey(), dimensionTag);
                        }
                    }
                }else {
                    if (currentPage.getProperties().get(CommonConstants.TEASER_IMAGE_PATH) != null) {
                        String primaryImage = generatePrimaryImage(currentPage.getProperties().get(CommonConstants.TEASER_IMAGE_PATH).toString(),adminResourceResolver);
                        metaTagGenericValuesModel.setPrimaryImage(primaryImage);
                    }
                }
                LOG.debug(" Outside For Loop ");
                // populate metadata:title,description,og:title and og:description
                populateSEOMetaData();
                populateHrefLangValues(adminResourceResolver);
                // population application id
                metaTagGenericValuesModel.setApplicationId(CommonUtil.getApplicationId(currentPage.getPath()));
                populateMetaRobotTags();
                if (currentPageProperties != null && countryLangCodeConfigService != null &&
                        currentPageProperties.get(CommonConstants.EXCLUDE_COUNTRY_PROPERTY) != null &&
                        currentPageProperties.get(CommonConstants.EXCLUDE_COUNTRY_PROPERTY)
                                .equals(CommonConstants.TRUE)) {
                    excludeCountryList = excludecountry();
                }
                if (authenticationServiceConfiguration != null) {
                    String ssoCookieDomain = authenticationServiceConfiguration.getSsoCookieDomain();
                    metaTagGenericValuesModel.setSsoCookieDomain(ssoCookieDomain);
                }
                // Adding product grid description to metadata
                String productGridDescription = currentPageProperties.get(CommonConstants.PRODUCT_GRID_DESCRIPTION,
                        StringUtils.EMPTY);
                if (StringUtils.isNotBlank(productGridDescription)) {
                    metaTagGenericValuesModel.setProductGridDescription(productGridDescription);
                }
                    if (currentPageProperties != null && currentPageProperties.get(CommonConstants.JCR_UUID) != null) {
                    String uuid = currentPageProperties.get(CommonConstants.JCR_UUID).toString();
                    metaTagGenericValuesModel.setUuid(uuid);
                }
                LOG.debug("End of TRY BLOCK :: init() ");
            } catch (Exception exception) {
                LOG.error("Exception occured while getting the reader service", exception);
            }

            setMetaTagsWithSiteConfig();

        }
        LOG.debug("MetaTagModel :: init() :: END");
    }

    private MetaTagValuesModel addMiddleTabUrlToMetaTagValueModel(ResourceResolver adminResourceResolver) {
        if (new ProductTabsVersionChecker().isVersion2(currentPage)){
            metaTagGenericValuesModel.setMiddleTabUrl(CommonUtil.dotHtmlLink(currentPage.getPath()) + MIDDLE_TAB_HASH);
            return metaTagGenericValuesModel;
        }
        metaTagGenericValuesModel.setMiddleTabUrl(CommonUtil.dotHtmlLink(
                currentPage.getPath() + CommonConstants.PERIOD +
                        metaTagGenericValuesModel.getMiddleTabName().toLowerCase(Locale.ENGLISH),
                adminResourceResolver));
        return metaTagGenericValuesModel;
    }

    /**
     * Populate Meta robot tags.
     */
    private void populateMetaRobotTags() {
        LOG.debug("MetaTagsModel : Entry into populateMetaRobotTags() method");
        ValueMap pageProperties = currentPage.getProperties();
        if (null != pageProperties) {
            String metaRobotTags = CommonUtil.getStringProperty(pageProperties, CommonConstants.META_ROBOT_TAGS);
            if (StringUtils.isEmpty(metaRobotTags)) {
                metaRobotTags = CommonConstants.INDEX_FOLLOW;
            }
            metaTagGenericValuesModel.setMetaRobotTags(metaRobotTags);
        }
        LOG.debug("MetaTagsModel : Exit from populateMetaRobotTags() method");
    }

    /**
     * Populate SEO meta data.
     */
    private void populateSEOMetaData() {
        LOG.debug("MetaTagsModel : Entered into populateSEOMetaData() method");
        final ValueMap pageProperties = currentPage.getProperties();
        if (null != pageProperties) {
            String pagetype = CommonUtil.getStringProperty(pageProperties, CommonConstants.PAGE_TYPE);
            LOG.debug("pageType : {}", pageType);
            if (pagetype.equalsIgnoreCase(CommonConstants.PAGE_TYPE_PRODUCT_FAMILY_PAGE)) {
                setProductFamilySEOMetaData();
            } else if (pagetype.equalsIgnoreCase(CommonConstants.PAGE_TYPE_PRODUCT_SKU_PAGE)) {
                setSKUSEOMetaData();
                // populate social metadata: og:title, og:description, og:url etc...
                populateSocialMetaData();
            } else {
                // Other pages
                populateSocialMetaDataForOtherPages();
            }
        }
        LOG.debug("MetaTagsModel : Exit from populateSEOMetaData() method");
    }

    /**
     * Sets the product family SEO meta data.
     */
    private void setProductFamilySEOMetaData() {
        LOG.debug("MetaTagsModel : Entered into setProductFamilySEOMetaData() method");
        // local varaibles
        String titlePattern = null;
        String descPattern = null;
        String seoPFTitle = null;
        String seoPFDesc = null;
        Boolean isMiddleTab = false;
        /** The eaton config service. */

        List<String> middleTabLablesList = eatonConfigService.getConfigServiceBean().getMiddleTabListPagePathList();
        // get product family details from request
        ProductFamilyDetails productfamilydetails = productFamilyDetailService.getProductFamilyDetailsBean(currentPage);
        // get site configuration details from request
        Optional<SiteResourceSlingModel> siteConfig = eatonSiteConfigService.getSiteConfig(currentPage);
        siteResourceSlingModel = siteConfig.isPresent() ? siteConfig.get() : null;

        String pfSeoMetaDescription = null;
        if (null != productfamilydetails && StringUtils.isNotBlank(productfamilydetails.getMetaDescOverviewTab())) {
            pfSeoMetaDescription = productfamilydetails.getMetaDescOverviewTab();
        } else {
            if (null != siteResourceSlingModel && StringUtils.isNotBlank(siteResourceSlingModel.getPfOverviewDesc())) {
                pfSeoMetaDescription = siteResourceSlingModel.getPfOverviewDesc();
                descPattern = siteResourceSlingModel.getPfOverviewDesc();
            }
        }

        if (null != productfamilydetails) {
            if (StringUtils.isNotBlank(productfamilydetails.getProductFamilyName())) {
                metaTagGenericValuesModel.setOgTitle(productfamilydetails.getProductFamilyName());
                metaTagGenericValuesModel.setTwitterTitle(productfamilydetails.getProductFamilyName());
            }
            if (StringUtils.isNotBlank(productfamilydetails.getMetaDescOverviewTab())) {
                metaTagGenericValuesModel.setOgDesc(productfamilydetails.getMetaDescOverviewTab());
                metaTagGenericValuesModel.setTwitterDesc(productfamilydetails.getMetaDescOverviewTab());
            }

            String productFamilyImagePath = StringUtils.isNotBlank(productfamilydetails.getPdhPrimaryImg()) ? productfamilydetails.getPdhPrimaryImg() : productfamilydetails.getPrimaryImage();
            LOG.debug("product family image path: {}", productFamilyImagePath);
            setImageTags(productFamilyImagePath);

        }
        metaTagGenericValuesModel.setOgUrl(currentPageURL);
        metaTagGenericValuesModel.setTwitterUrl(currentPageURL);
        metaTagGenericValuesModel.setOgType(CommonConstants.PRODUCT);
        String locale = CommonUtil.getStringInheritedProperty(currentPageInheritedProp, JcrConstants.JCR_LANGUAGE);
        metaTagGenericValuesModel.setOgLocale(locale);
        populateBrandingInformation();

        // get selectors from page url to determine tab name
        String[] selectors = slingRequest.getRequestPathInfo().getSelectors();
        if (null != selectors && selectors.length > 0) {
            Iterator<String> it = middleTabLablesList.iterator();
            while (it.hasNext()) {
                if (it.next().equalsIgnoreCase(selectors[0])) {
                    isMiddleTab = true;
                }
            }
            // populate metadata for middle tab
            if ((selectors[0] != null) && isMiddleTab) {
                if (null != productfamilydetails && null != productfamilydetails.getMetaTitleModelsTab() && !StringUtils
                        .equalsIgnoreCase(StringUtils.EMPTY, productfamilydetails.getMetaTitleModelsTab())) {
                    metaTagGenericValuesModel.setTitle(productfamilydetails.getMetaTitleModelsTab());
                } else {
                    if (null != siteResourceSlingModel) {
                        titlePattern = siteResourceSlingModel.getPfModelsTitle();
                        String prodFamilyTitle = productfamilydetails.getProductFamilyName();
                        if (StringUtils.isNotBlank(prodFamilyTitle)) {
                            seoPFTitle = StringUtils.replace(titlePattern, KEYWORD_PRODUCT_FAMILY_NAME,
                                    prodFamilyTitle);
                        } else {
                            String fallbackTitle = currentPage.getPageTitle() != null ? currentPage.getPageTitle()
                                    : currentPage.getNavigationTitle();
                            fallbackTitle = (fallbackTitle != null ? fallbackTitle : currentPage.getTitle());
                            seoPFTitle = StringUtils.replace(titlePattern, KEYWORD_PRODUCT_FAMILY_NAME, fallbackTitle);
                        }

                        metaTagGenericValuesModel.setTitle(seoPFTitle);
                    }
                }

                if (null != productfamilydetails && null != productfamilydetails.getMetaDescOverviewTab()
                        && !StringUtils.equalsIgnoreCase(StringUtils.EMPTY,
                                productfamilydetails.getMetaDescOverviewTab())) {
                    metaTagGenericValuesModel.setDescription(pfSeoMetaDescription);
                    metaTagGenericValuesModel.setOgDesc(pfSeoMetaDescription);
                    metaTagGenericValuesModel.setTwitterDesc(pfSeoMetaDescription);
                } else {
                    if (null != siteResourceSlingModel) {
                        String prodFamilyDesc = productfamilydetails.getMarketingDesc();
                        if (StringUtils.isNotBlank(prodFamilyDesc)) {
                            seoPFDesc = StringUtils.replace(descPattern, KEYWORD_PRODUCT_MKTG_DESC, prodFamilyDesc);
                        } else {
                            String fallbackDesc = currentPage.getDescription() != null ? currentPage.getDescription()
                                    : currentPage.getTitle();
                            seoPFDesc = StringUtils.replace(descPattern, KEYWORD_PRODUCT_MKTG_DESC, fallbackDesc);
                        }

                        metaTagGenericValuesModel.setDescription(seoPFDesc);
                        metaTagGenericValuesModel.setOgDesc(seoPFDesc);
                        metaTagGenericValuesModel.setTwitterDesc(seoPFDesc);
                    }
                }
                // populate metadata for Resources tab
            } else if ((selectors[0] != null) && (selectors[0].startsWith("resources"))) {
                if (null != productfamilydetails && null != productfamilydetails.getMetaTitleResourcesTab()
                        && !StringUtils.equalsIgnoreCase(StringUtils.EMPTY,
                                productfamilydetails.getMetaTitleResourcesTab())) {
                    metaTagGenericValuesModel.setTitle(productfamilydetails.getMetaTitleResourcesTab());
                } else {
                    if (null != siteResourceSlingModel) {
                        titlePattern = siteResourceSlingModel.getPfResourcesTitle();
                        String prodFamilyTitle = productfamilydetails.getProductFamilyName();
                        if (StringUtils.isNotBlank(prodFamilyTitle)) {
                            seoPFTitle = StringUtils.replace(titlePattern, KEYWORD_PRODUCT_FAMILY_NAME,
                                    prodFamilyTitle);
                        } else {
                            String fallbackTitle = currentPage.getPageTitle() != null ? currentPage.getPageTitle()
                                    : currentPage.getNavigationTitle();
                            fallbackTitle = (fallbackTitle != null ? fallbackTitle : currentPage.getTitle());
                            seoPFTitle = StringUtils.replace(titlePattern, KEYWORD_PRODUCT_FAMILY_NAME, fallbackTitle);
                        }

                        metaTagGenericValuesModel.setTitle(seoPFTitle);
                    }
                }

                if (null != productfamilydetails && null != productfamilydetails.getMetaDescOverviewTab()
                        && !StringUtils.equalsIgnoreCase(StringUtils.EMPTY,
                                productfamilydetails.getMetaDescOverviewTab())) {
                    metaTagGenericValuesModel.setDescription(pfSeoMetaDescription);
                } else {
                    if (null != siteResourceSlingModel) {
                        String prodFamilyDesc = productfamilydetails.getMarketingDesc();
                        if (StringUtils.isNotBlank(prodFamilyDesc)) {
                            seoPFDesc = StringUtils.replace(descPattern, KEYWORD_PRODUCT_MKTG_DESC, prodFamilyDesc);
                        } else {
                            String fallbackDesc = currentPage.getDescription() != null ? currentPage.getDescription()
                                    : currentPage.getTitle();
                            seoPFDesc = StringUtils.replace(descPattern, KEYWORD_PRODUCT_MKTG_DESC, fallbackDesc);
                        }

                        metaTagGenericValuesModel.setDescription(seoPFDesc);
                    }
                }
                // populate title for result pages with facets selector
            } else if ((selectors[0] != null) && (selectors[0].startsWith("facets"))) {
                String fallbackTitle = currentPage.getPageTitle() != null ? currentPage.getPageTitle()
                        : currentPage.getNavigationTitle();
                fallbackTitle = (fallbackTitle != null ? fallbackTitle : currentPage.getTitle());
                metaTagGenericValuesModel.setTitle(fallbackTitle);
            }
            // populate metadata for overview tab
        } else {
            if (null != productfamilydetails && null != productfamilydetails.getMetaTitleOverviewTab() && !StringUtils
                    .equalsIgnoreCase(StringUtils.EMPTY, productfamilydetails.getMetaTitleOverviewTab())) {
                metaTagGenericValuesModel.setTitle(productfamilydetails.getMetaTitleOverviewTab());
            } else {
                if (null != siteResourceSlingModel && null != siteResourceSlingModel.getPfOverviewTitle() &&
                        !StringUtils.equalsIgnoreCase(StringUtils.EMPTY, siteResourceSlingModel.getPfOverviewTitle())) {
                    titlePattern = siteResourceSlingModel.getPfOverviewTitle();
                    String prodFamilyTitle = productfamilydetails.getProductFamilyName();
                    if (StringUtils.isNotBlank(prodFamilyTitle)) {
                        seoPFTitle = StringUtils.replace(titlePattern, KEYWORD_PRODUCT_FAMILY_NAME, prodFamilyTitle);
                    } else {
                        String fallbackTitle = currentPage.getPageTitle() != null ? currentPage.getPageTitle()
                                : currentPage.getNavigationTitle();
                        fallbackTitle = (fallbackTitle != null ? fallbackTitle : currentPage.getTitle());
                        seoPFTitle = StringUtils.replace(titlePattern, KEYWORD_PRODUCT_FAMILY_NAME, fallbackTitle);
                    }

                    metaTagGenericValuesModel.setTitle(seoPFTitle);
                }
            }

            if (null != productfamilydetails && null != productfamilydetails.getMetaDescOverviewTab() && !StringUtils
                    .equalsIgnoreCase(StringUtils.EMPTY, productfamilydetails.getMetaDescOverviewTab())) {
                metaTagGenericValuesModel.setDescription(pfSeoMetaDescription);
            } else {
                if (null != siteResourceSlingModel && null != siteResourceSlingModel.getPfOverviewDesc() &&
                        !StringUtils.equalsIgnoreCase(StringUtils.EMPTY, siteResourceSlingModel.getPfOverviewDesc())) {
                    String prodFamilyDesc = productfamilydetails.getMarketingDesc();
                    if (StringUtils.isNotBlank(prodFamilyDesc)) {
                        seoPFDesc = StringUtils.replace(descPattern, KEYWORD_PRODUCT_MKTG_DESC, prodFamilyDesc);
                    } else {
                        String fallbackDesc = currentPage.getDescription() != null ? currentPage.getDescription()
                                : currentPage.getTitle();
                        seoPFDesc = StringUtils.replace(descPattern, KEYWORD_PRODUCT_MKTG_DESC, fallbackDesc);
                    }

                    metaTagGenericValuesModel.setDescription(seoPFDesc);
                }
            }
        }
        LOG.debug("MetaTagsModel : Exit from setProductFamilySEOMetaData() method");
    }

    /**
     * Sets the SKUSEO meta data.
     */
    private void setSKUSEOMetaData() {
        LOG.debug("MetaTagsModel : Entry into setSKUSEOMetaData() method");
        // local varaibles
        String titlePattern = null;
        String descPattern = null;
        String seoSKUTitle = null;
        String seoSKUDesc = null;
        String catalogNumber = null;

        // get site configuration details from request
        Optional<SiteResourceSlingModel> siteConfig = eatonSiteConfigService.getSiteConfig(currentPage);
        siteResourceSlingModel = siteConfig.isPresent() ? siteConfig.get() : null;
        // get selectors from page url to determine tab name
        String[] selectors = slingRequest.getRequestPathInfo().getSelectors();
        final EndecaServiceRequestBean endecaServiceRequestBean = endecaRequestService
                .getEndecaRequestBean(currentPage, selectors, StringUtils.EMPTY);
        final SKUDetailsResponseBean skuDetailsResponseBean = endecaService.getSKUDetails(endecaServiceRequestBean);
        skuDetailsBean = skuDetailsResponseBean.getSkuResponse().getSkuDetails();
        if (selectors.length > 0) {
            catalogNumber = CommonUtil.decodeSearchTermString(selectors[0]);
        }
        if (null != siteResourceSlingModel && StringUtils.isNotBlank(siteResourceSlingModel.getSkuOverviewDesc())) {
            descPattern = siteResourceSlingModel.getSkuOverviewDesc();
        }
        // populate metadata for specifications tab
        if ((selectors.length > 1) && StringUtils.equalsIgnoreCase(selectors[1], "specifications")) {
            if (null != siteResourceSlingModel
                    && StringUtils.isNotBlank(siteResourceSlingModel.getSkuSpecificationsTitle())) {
                titlePattern = siteResourceSlingModel.getSkuSpecificationsTitle();
                if (null != skuDetailsBean) {
                    seoSKUTitle = StringUtils.replace(titlePattern, KEYWORD_PRODUCT_FAMILY_NAME,
                            skuDetailsBean.getFamilyName());
                }
                seoSKUTitle = StringUtils.replace(seoSKUTitle, KEYWORD_CATALOG_NUMBER, catalogNumber);
                metaTagGenericValuesModel.setOgTitle(seoSKUTitle);
                metaTagGenericValuesModel.setTwitterTitle(seoSKUTitle);
                metaTagGenericValuesModel.setFamilyName(seoSKUTitle);
            }
            if (StringUtils.isNotBlank(descPattern)) {
                seoSKUDesc = StringUtils.replace(descPattern, KEYWORD_CATALOG_NUMBER, catalogNumber);
                if (null != skuDetailsBean) {
                    seoSKUDesc = StringUtils.replace(seoSKUDesc, KEYWORD_PRODUCT_MKTG_DESC,
                            skuDetailsBean.getMktgDesc());
                }

                metaTagGenericValuesModel.setOgDesc(seoSKUDesc);
                metaTagGenericValuesModel.setTwitterDesc(seoSKUDesc);
                metaTagGenericValuesModel.setMaeketingDesc(seoSKUDesc);
            }

        } else if ((selectors.length > 1) && (selectors[1].startsWith("resources"))) {
            if (null != siteResourceSlingModel
                    && StringUtils.isNotBlank(siteResourceSlingModel.getSkuResourcesTitle())) {
                titlePattern = siteResourceSlingModel.getSkuResourcesTitle();
                if (null != skuDetailsBean) {
                    seoSKUTitle = StringUtils.replace(titlePattern, KEYWORD_PRODUCT_FAMILY_NAME,
                            skuDetailsBean.getFamilyName());
                }
                seoSKUTitle = StringUtils.replace(seoSKUTitle, KEYWORD_CATALOG_NUMBER, catalogNumber);
                metaTagGenericValuesModel.setOgTitle(seoSKUTitle);
                metaTagGenericValuesModel.setTwitterTitle(seoSKUTitle);
                metaTagGenericValuesModel.setFamilyName(seoSKUTitle);
            }
            if (StringUtils.isNotBlank(descPattern)) {
                seoSKUDesc = StringUtils.replace(descPattern, KEYWORD_CATALOG_NUMBER, catalogNumber);
                if (null != skuDetailsBean) {
                    seoSKUDesc = StringUtils.replace(seoSKUDesc, KEYWORD_PRODUCT_MKTG_DESC,
                            skuDetailsBean.getMktgDesc());
                }

                metaTagGenericValuesModel.setOgDesc(seoSKUDesc);
                metaTagGenericValuesModel.setTwitterDesc(seoSKUDesc);
                metaTagGenericValuesModel.setMaeketingDesc(seoSKUDesc);
            }
        } else {
            if (null != siteResourceSlingModel && null != siteResourceSlingModel.getSkuOverviewTitle()
                    && !StringUtils.equalsIgnoreCase(StringUtils.EMPTY, siteResourceSlingModel.getSkuOverviewTitle())) {
                titlePattern = siteResourceSlingModel.getSkuOverviewTitle();
                if (null != skuDetailsBean) {
                    seoSKUTitle = StringUtils.replace(titlePattern, KEYWORD_PRODUCT_FAMILY_NAME,
                            skuDetailsBean.getFamilyName());
                }
                seoSKUTitle = StringUtils.replace(seoSKUTitle, KEYWORD_CATALOG_NUMBER, catalogNumber);
                metaTagGenericValuesModel.setOgTitle(seoSKUTitle);
                metaTagGenericValuesModel.setTwitterTitle(seoSKUTitle);
                metaTagGenericValuesModel.setFamilyName(seoSKUTitle);
            }

            if (StringUtils.isNotBlank(descPattern)) {
                seoSKUDesc = StringUtils.replace(descPattern, KEYWORD_CATALOG_NUMBER, catalogNumber);
                if (null != skuDetailsBean) {
                    seoSKUDesc = StringUtils.replace(seoSKUDesc, KEYWORD_PRODUCT_MKTG_DESC,
                            skuDetailsBean.getMktgDesc());
                }
                metaTagGenericValuesModel.setOgDesc(seoSKUDesc);
                metaTagGenericValuesModel.setTwitterDesc(seoSKUDesc);
                metaTagGenericValuesModel.setMaeketingDesc(seoSKUDesc);
            }
        }

        LOG.debug("MetaTagsModel : Exit from setSKUSEOMetaData() method");
    }

    /**
     * Populate social meta data.
     */
    private void populateSocialMetaData() {
        LOG.debug("MetaTagsModel : Entry into populateSocialMetaData() method");
        // set current page url in og:url and twitter:url
        metaTagGenericValuesModel.setOgUrl(currentPageURL);
        metaTagGenericValuesModel.setTwitterUrl(currentPageURL);
        // set og:type as product
        metaTagGenericValuesModel.setOgType(CommonConstants.PRODUCT);

        String locale = CommonUtil.getStringInheritedProperty(currentPageInheritedProp, JcrConstants.JCR_LANGUAGE);
        metaTagGenericValuesModel.setOgLocale(locale);

        populateBrandingInformation();

        if (null != skuDetailsBean) {
            LOG.debug("sku image path: {}", getPrimaryImage());
            setImageTags(getPrimaryImage());
        }
        LOG.debug("MetaTagsModel : Exit from populateSocialMetaData() method");
    }

    private void populateSocialMetaDataForOtherPages() {
        LOG.debug("MetaTagsModel : Entry into populateSocialMetaDataForOtherPages() method");
        metaTagGenericValuesModel.setOgTitle(currentPage.getTitle());
        metaTagGenericValuesModel.setTwitterTitle(currentPage.getTitle());

        metaTagGenericValuesModel.setOgDesc(currentPage.getDescription());
        metaTagGenericValuesModel.setTwitterDesc(currentPage.getDescription());

        // set current page url in og:url and twitter:url
        metaTagGenericValuesModel.setOgUrl(currentPageURL);
        metaTagGenericValuesModel.setTwitterUrl(currentPageURL);
        // set og:type as product
        metaTagGenericValuesModel.setOgType(CommonConstants.WEBSITE);

        String locale = CommonUtil.getStringInheritedProperty(currentPageInheritedProp, JcrConstants.JCR_LANGUAGE);
        metaTagGenericValuesModel.setOgLocale(locale);

        populateBrandingInformation();

        String teaserImagePath = CommonUtil.getStringProperty(currentPage.getProperties(),
                CommonConstants.TEASER_IMAGE_PATH);
        LOG.debug("teaser image path: {}", teaserImagePath);
        setImageTags(teaserImagePath);

        LOG.debug("MetaTagsModel : Exit from populateSocialMetaDataForOtherPages() method");
    }

    private void populateBrandingInformation() {
        LOG.debug("MetaTagsModel : Entry into populateBrandingInformation() method");

        Optional<BrandColorSelectionConfigModel> brandColorSelectionCloudConfigObj = cloudConfigService != null
                ? cloudConfigService.getBrandColorSelectionConfig(resource)
                : java.util.Optional.empty();
        if (brandColorSelectionCloudConfigObj.isPresent()) {
            metaTagGenericValuesModel.setOgSitename(brandColorSelectionCloudConfigObj.get().getSiteName());
            metaTagGenericValuesModel.setTwitterSitename(brandColorSelectionCloudConfigObj.get().getTwitterSite());
        } else {
            metaTagGenericValuesModel.setOgSitename(CommonConstants.EATON);
            metaTagGenericValuesModel.setTwitterSitename(CommonConstants.EATON_CORP);
        }

        LOG.debug("MetaTagsModel : Exit from populateBrandingInformation() method");
    }

    private String getPrimaryImage() {
        LOG.debug("MetaTagsModel : Entry into getPrimaryImage() method");
        String xmlString = null;
        String primaryImage = null;

        if (skuDetailsBean.getSkuImgs() != null && !(skuDetailsBean.getSkuImgs().isEmpty())) {
            xmlString = skuDetailsBean.getSkuImgs();
            SkuImageParser skuImageParser = new SkuImageParser();
            Map<String, ImageGroupBean> attrGrpList = skuImageParser.getPDHImageList(xmlString, adminService);
            if (attrGrpList != null) {
                for (Map.Entry<String, ImageGroupBean> entry1 : attrGrpList.entrySet()) {
                    if (entry1 != null && entry1.getKey() != null && !(entry1.getKey().isEmpty())
                            && entry1.getValue() != null) {
                        String title = entry1.getKey();
                        ImageGroupBean imageGroupBean = entry1.getValue();
                        if (imageGroupBean.getImageRenditionMap() != null
                                && !(imageGroupBean.getImageRenditionMap().isEmpty())) {
                            Map<String, AttrNmBean> renditionMap = imageGroupBean.getImageRenditionMap();
                            for (Map.Entry<String, AttrNmBean> entry2 : renditionMap.entrySet()) {
                                String temp = entry2.getKey();
                                AttrNmBean attrNmBean = entry2.getValue();

                                if (StringUtils.containsIgnoreCase(temp, ZOOM)) {
                                    LOG.info("Get the Primary image value");
                                    if (attrNmBean.getCdata() != null && !(attrNmBean.getCdata().isEmpty())) {
                                        LOG.info("attrNmBean cData is not null");
                                        if ("IMAGE_PRIMARY".equals(title)) {
                                            primaryImage = attrNmBean.getCdata();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        LOG.debug("MetaTagsModel : Exit from getPrimaryImage() method");
        return primaryImage;
    }

    public void populateHrefLangValues(final ResourceResolver adminResourceResolver) {
        LOG.debug("MetaTagsModel : Entry into populateHrefLangValues() method");
        final String domainCurrentPagePath = currentPage.getPath();
        String domainName = StringUtils.EMPTY;
        hreflangValues = new ArrayList<>();
        Map<String, CountryLanguageCodeBean> countryLangCodeListMap = new HashMap<>();
        List<String> countryLangCodeList;
        String[] countryLanguageCodeArray = new String[0];

        if (domainCurrentPagePath != null) {
            domainName = CommonUtil.getExternalizerDomainNameBySiteRootPath(domainCurrentPagePath);
        }

        if (countryLangCodeConfigService != null) {
            countryLangCodeList = countryLangCodeConfigService.getCountryLangCodesList(domainName);
            countryLanguageCodeArray = countryLangCodeList.toArray(new String[0]);
        }

        if (countryLanguageCodeArray != null && countryLangCodeConfigService != null) {
            for (String countryLanguageCodeArrayElement : countryLanguageCodeArray) {
                CountryLanguageCodeBean countryLanguageCodeBean = CommonUtil.getCountryLangCodesMap(
                        countryLanguageCodeArrayElement,
                        countryLangCodeConfigService.getDomainXDefaultHrefLangCode(domainName));
                countryLangCodeListMap.put(countryLanguageCodeArrayElement, countryLanguageCodeBean);
                String countryCode = countryLanguageCodeBean.getCountryCode();
                String languageCode = countryLanguageCodeBean.getLanguageCode();
                String currentPagePath = currentPage.getPath();

                if (adminResourceResolver != null && !currentPagePath.contains(CommonConstants.LANGUAGE_MASTERS_NODE_NAME)) {

                    if (!CommonConstants.PAGE_TYPE_SKU_PAGE.equalsIgnoreCase(pageType)){
                        populateHrefLangList(adminResourceResolver, countryLanguageCodeBean, countryCode, languageCode);

                    } else if (CommonConstants.PAGE_TYPE_SKU_PAGE.equalsIgnoreCase(pageType) && isCountryInSkuCountryList(countryCode)) {
                        populateHrefLangList(adminResourceResolver, countryLanguageCodeBean, countryCode, languageCode);

                    } else {
                        LOG.debug("Alternate link for page ({}) not set for country code ({}) because country code not found in the country list sku sold in.", currentPagePath, countryCode);
                    }
                }

            }

        }
        LOG.debug("MetaTagsModel : Exit from populateHrefLangValues() method");
    }

    /*
    * Check if country exists in sku's country list
    * @param countryCode - country code to check if in sku's country list
    * @return true if country exists in sku's country list
     */
    private boolean isCountryInSkuCountryList(String countryCode) {
        LOG.debug("isCountryInSkuCountryList : START");

        final String inventoryId = skuDetailsBean.getInventoryId();
        final String[] skuCountryList = skuDetailsBean.getCountryList();
        LOG.debug("checking if country code ({}) is in the sku's ({}) country list ({})", countryCode, inventoryId, skuCountryList);

        if (null == inventoryId) {
            LOG.error("Inventory ID not found for sku");
        } else if (null == skuCountryList || skuCountryList.length == 0) {
            LOG.error("Country list not found for sku or is empty");
        } else {
            for (final String skuCountry : skuCountryList) {
                if (StringUtils.equalsIgnoreCase(skuCountry, countryCode) || StringUtils.equalsIgnoreCase(skuCountry, CommonConstants.GLOBAL)) {
                    LOG.debug("country ({}) found in sku's ({}) country list ({})", countryCode, inventoryId, skuCountryList);
                    LOG.debug("isCountryInSkuCountryList : END");
                    return true;
                }
            }
        }
        LOG.debug("isCountryInSkuCountryList : END");
        return false;
    }

    protected void populateHrefLangList(final ResourceResolver adminResourceResolver,
            CountryLanguageCodeBean countryLanguageCodeBean, String countryCode, String languageCode) {
        LOG.debug("MetaTagsModel : Entry into populateHrefLangList() method");
        String pagePath = StringUtils.EMPTY;
        final String currentPagePath = currentPage.getPath();
        final int currentPageDepth = currentPage.getDepth();
        String googleCode = countryLanguageCodeBean.getGoogleCode();
        if ((languageCode != null) && (currentPageDepth == 4)) {
            pagePath = CommonUtil.getSiteRootPrefixByPagePath(currentPage.getPath()) + countryCode
                    + CommonConstants.SLASH_STRING + languageCode;
            googleCode = googleCodeSetter(adminResourceResolver, googleCode, pagePath);
            Resource resource = adminResourceResolver.getResource(pagePath);
            if (resource != null && resource.getChild(JcrConstants.JCR_CONTENT) != null) {
                if (metaTagsLinkDomainService.isCountryExcluded(countryCode)) {
                    String domainKey = metaTagsLinkDomainService.getCustomDomainKey(countryCode);
                    pagePath = CommonUtil.dotHtmlLink(pagePath, adminResourceResolver, domainKey);
                } else {
                    pagePath = CommonUtil.dotHtmlLink(pagePath, adminResourceResolver);
                }
            } else {
                LOG.warn("Unable to find resource at {}. Alternate link for this page not set.", pagePath);
                pagePath = StringUtils.EMPTY;
            }
            countryLanguageCodeBean.setGoogleCode(googleCode);
        } else {
            if ((countryCode != null) && (languageCode != null) && (currentPageDepth > 4)) {
                pagePath = CommonUtil.getSiteRootPrefixByPagePath(currentPage.getPath()) + countryCode
                        + CommonConstants.SLASH_STRING + languageCode + CommonConstants.SLASH_STRING;
                String[] pagePathArr = currentPagePath.split(CommonConstants.SLASH_STRING);
                pagePathArr = Arrays.copyOfRange(pagePathArr, 5, pagePathArr.length);
                String pageName = StringUtils.join(pagePathArr, CommonConstants.SLASH_STRING);

                if (!pagePath.startsWith(CommonConstants.SLASH_STRING)) {
                    pagePath = CommonConstants.SLASH_STRING + pagePath;
                }
                googleCode = googleCodeSetter(adminResourceResolver, googleCode, pagePath);
                countryLanguageCodeBean.setGoogleCode(googleCode);

                Resource resource = adminResourceResolver.getResource(pagePath + pageName);
                if (resource != null && resource.getChild(JcrConstants.JCR_CONTENT) != null) {
                    if (CommonConstants.PAGE_TYPE_PRODUCT_SKU_PAGE.equalsIgnoreCase(pageType)) {
                        String pageSku = slingRequest.getRequestPathInfo().getSelectorString();
                        pageName = pageName.concat(CommonConstants.PERIOD).concat(pageSku);
                    }

                    if (metaTagsLinkDomainService.isCountryExcluded(countryCode)) {
                        String domainKey = metaTagsLinkDomainService.getCustomDomainKey(countryCode);
                        pagePath = CommonUtil.dotHtmlLink(pagePath + pageName, adminResourceResolver, domainKey);
                    } else {
                        pagePath = CommonUtil.dotHtmlLink(pagePath + pageName, adminResourceResolver);
                    }

                } else {
                    LOG.warn("Unable to find resource at {}. Alternate link for this page not set.", pagePath + pageName);
                    pagePath = StringUtils.EMPTY;
                }
            }
        }
        if (StringUtils.isNotBlank(pagePath) && StringUtils.isNotBlank(countryLanguageCodeBean.getGoogleCode())) {
            countryLanguageCodeBean.setPageUrl(pagePath);
            hreflangValues.add(countryLanguageCodeBean);
        }
        LOG.debug("MetaTagsModel : Exit the populateHrefLangList() method");
    }

    private static String googleCodeSetter(ResourceResolver adminResourceResolver, String googleCodeParam,
            String pagePath) {
        String googleCode = StringUtils.EMPTY;
        Page languageHomePage;
        final Resource pageResource = adminResourceResolver.getResource(pagePath);
        if (pageResource != null) {
            languageHomePage = pageResource.adaptTo(Page.class);
            if (X_DEFAULT.equals(googleCodeParam)) {
                googleCode = googleCodeParam;
                LOG.debug("Default hreflang google code received in googleCodeSetter. Setting hreflang code to {}", googleCode);
            } else {
                if (languageHomePage != null && languageHomePage.getProperties().size() > 0) {
                    if (languageHomePage.getProperties().get(HREF_LANG_CODE) != null) {
                        googleCode = languageHomePage.getProperties().get(HREF_LANG_CODE).toString();
                        LOG.debug("Google hreflang code found in country site ({}) homepage properties. Returning ({}) as the hreflang code.", pagePath, googleCode);
                    } else {
                        LOG.debug("Hreflang code is blank in country site ({}) homepage properties.", pagePath);
                    }
                } else {
                    LOG.warn("Unable to get country site's ({}) homepage properties.", pagePath);
                }
            }
        }
        return googleCode;
    }

    public MetaTagValuesModel getMetaTagGenericValuesModel() {
        return metaTagGenericValuesModel;
    }

    public List<Dimensions> getDimensionTagValues() {
        return dimensionTagValues;
    }

    public void setDimensionTagValues(List<Dimensions> dimensionTagValues) {
        this.dimensionTagValues = dimensionTagValues;
    }

    public List<CountryLanguageCodeBean> getHreflangValues() {
        return hreflangValues;
    }

    public void setHreflangValues(List<CountryLanguageCodeBean> hreflangValues) {
        this.hreflangValues = hreflangValues;
    }

    public String getCanonicalURL() throws ValueFormatException, PathNotFoundException {
        LOG.debug("MetaTagsModel : Entry into getCanonicalURL() method");
        String canonicalLink = StringUtils.EMPTY;
        String pagetype = CommonUtil.getStringProperty(currentPage.getProperties(), CommonConstants.PAGE_TYPE);
        String countryCode = CommonUtil.getCountryFromPagePath(currentPage).toLowerCase();
        if (null != adminService) {
            try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
                Resource currentPageResource = slingRequest.getResourceResolver().getResource(currentPage.getPath());
                if (null != currentPageResource) {
                    Node node = currentPageResource.adaptTo(Node.class);
                    if (node != null) {
                        final String jcrPrimaryType = node.getProperty(CommonConstants.JCR_PRIMARY_TYPE).getString();
                        isSubmittalBuilder = checkSubmittalBuilderPage();
                        final boolean isValidPages = StringUtils.equals(jcrPrimaryType, CommonConstants.NODE_TYPE)
                                || isSubmittalBuilder;
                        if (isValidPages) {
                            LOG.info("Inside isValidPages check");
                            if (StringUtils.isNotEmpty(currentPage.getPath()) && (null != xssapi)) {
                                if (canonicalPages != null) {
                                    canonicalLink = canonicalPages;
                                } else {

                                    if (metaTagsLinkDomainService.isCountryExcluded(countryCode)) {
                                        String domainKey = metaTagsLinkDomainService.getCustomDomainKey(countryCode);
                                        canonicalLink = CommonUtil.dotHtmlLink(currentPage.getPath(), adminResourceResolver, domainKey);
                                    } else {
                                        canonicalLink = CommonUtil.dotHtmlLink(currentPage.getPath(), adminResourceResolver);
                                    }

                                    if (pagetype.equalsIgnoreCase(CommonConstants.PAGE_TYPE_PRODUCT_SKU_PAGE)) {
                                        String skuId = slingRequest.getRequestPathInfo().getSelectorString();
                                        if (null != skuId) {
                                            String skuUrlEnding = CommonConstants.PERIOD + skuId + CommonConstants.HTML_EXTN;
                                            canonicalLink = canonicalLink.replace(CommonConstants.HTML_EXTN, skuUrlEnding);
                                        }
                                    }

                                }
                                canonicalLink = xssapi.getValidHref(canonicalLink);
                                LOG.info("canonicalLink is {}", canonicalLink);
                            }
                        }
                    }
                }
            } catch (RepositoryException e) {
                LOG.error("Repository Exception", e);
            }
        }
        LOG.debug("MetaTagsModel : Exit from getCanonicalURL() method");
        return canonicalLink;
    }

    private String getCurrentPageURL() {
        LOG.debug("MetaTagsModel : Entry into getCurrentPageURL() method");
        String pageURL = StringUtils.EMPTY;
        if (null != adminService) {
            try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
                if (StringUtils.isNotEmpty(currentPage.getPath()) && (null != xssapi)) {
                    if (StringUtils.isNotEmpty(slingRequest.getRequestPathInfo().getSelectorString())) {
                        pageURL = CommonUtil.dotHtmlLink(currentPage.getPath().concat(CommonConstants.PERIOD)
                                .concat(slingRequest.getRequestPathInfo().getSelectorString()), adminResourceResolver);
                    } else {
                        pageURL = CommonUtil.dotHtmlLink(currentPage.getPath(), adminResourceResolver);
                    }
                    pageURL = xssapi.getValidHref(pageURL);
                    LOG.info("currentPageurl is {}", pageURL);
                }
            } catch (Exception e) {
                LOG.error("Exception while constructing pageURL {}", e.getMessage());
            }
        }
        LOG.debug("MetaTagsModel : Exit from getCurrentPageURL() method");
        return pageURL;
    }

    private void setCategoryAndDimensionalMapMetaData(String[] tags, TagManager tagManager,
            Map<String, Dimensions> dimensionTagMap, Boolean isDiscontinuedProduct) {
        LOG.debug("MetaTagsModel : Entry into setCategoryAndDimensionalMapMetaData() method");
        if (Boolean.TRUE.equals(isDiscontinuedProduct)) {
            List<String> tagsList = new ArrayList<String>(Arrays.asList(tags));

            if (!tagsList.contains(CommonConstants.DISCONTINUE_TAG_PATH)) {
                tagsList.add(CommonConstants.DISCONTINUE_TAG_PATH);
                tags = Arrays.copyOf(
                        tagsList.toArray(), tagsList.size(), String[].class);
            }

        }
        Locale locale = CommonUtil.getLocaleFromPagePath(currentPage);
        for (String tagPath : tags) {
            Tag tag = null;
            Tag firstLevelTag = null;
            Tag secondLevelTag = null;
            Dimensions dimensionTag = new Dimensions();
            tag = tagManager.resolve(tagPath);
            if (tag != null) {
                String dimValues = StringUtils.EMPTY;
                String dimNames = StringUtils.EMPTY;
                int count = 0;
                while (!CommonUtil.isTagNameMatchesWithAnySiteTagNameSpace(tag.getName())) {
                    if (tag.getTagID().equals(CommonConstants.DISCONTINUE_TAG_PATH)) {
                        dimValues = dimValues + tag.getTitle() + CommonConstants.DOUBLE_PIPE;
                    } else {
                        if (tag.getLocalizedTitle(locale) != null) {
                            dimValues = dimValues + tag.getLocalizedTitle(locale) + CommonConstants.DOUBLE_PIPE;
                        } else if (tag.getTitle() != null) {
                            dimValues = dimValues + tag.getTitle() + CommonConstants.DOUBLE_PIPE;
                        }
                    }

                    dimNames = dimNames + tag.getName() + CommonConstants.DOUBLE_PIPE;

                    if (count == 0) {
                        secondLevelTag = tag;
                    } else if (count == 1) {
                        firstLevelTag = tag;
                    } else {
                        secondLevelTag = firstLevelTag;
                        firstLevelTag = firstLevelTag.getParent();
                    }

                    tag = tag.getParent();
                    count = count + 1;
                    if (tag == null) {
                        break;
                    }
                }

                if ((dimValues != null) && ((secondLevelTag != null) && (secondLevelTag.getName() != null))) {
                    int index = dimNames.lastIndexOf(secondLevelTag.getName());
                    if (index > 2) {
                        if (index != dimNames.length()) {
                            dimNames = dimNames.substring(0, index - 2);
                        }
                    } else if (index == 0 && firstLevelTag != null &&
                            firstLevelTag.getName().contains(CommonConstants.TAG_NAMESPACE_MYEATON_TAXONOMY)) {
                        dimNames = secondLevelTag.getName();
                    } else if (index == 0) {
                        dimNames = StringUtils.EMPTY;
                    }
                }
                if (dimValues != null) {
                    if ((secondLevelTag != null) && (secondLevelTag.getLocalizedTitle(locale) != null)) {
                        int index = dimValues.lastIndexOf(secondLevelTag.getLocalizedTitle(locale));
                        if (index > 2) {
                            if (index != dimValues.length()) {
                                dimValues = dimValues.substring(0, index - 2);
                            }
                        } else if (index == 0) {
                            dimValues = StringUtils.EMPTY;
                        }
                    } else if ((secondLevelTag != null) && (secondLevelTag.getTitle() != null)) {
                        int index = dimValues.lastIndexOf(secondLevelTag.getTitle());
                        if (index > 2) {
                            if (index != dimValues.length()) {
                                dimValues = dimValues.substring(0, index - 2);
                            }
                        } else if (index == 0) {
                            dimValues = StringUtils.EMPTY;
                        }
                    }
                }

                if ((firstLevelTag != null)
                        && (firstLevelTag.getName().equals(CommonConstants.TAG_NAMESPACE_PRODUCT_TAXONOMY)
                                || firstLevelTag.getName().equals(CommonConstants.TAG_NAMESPACE_MYEATON_TAXONOMY)
                                || CommonConstants.categoryIdTagNameSpace.contains(firstLevelTag.getName()))) {
                    if ((metaTagGenericValuesModel.getCategoryId() != null)
                            && (!metaTagGenericValuesModel.getCategoryId().equals(StringUtils.EMPTY))) {
                        dimNames = metaTagGenericValuesModel.getCategoryId() + CommonConstants.DOUBLE_PIPE + dimNames;

                    }
                    metaTagGenericValuesModel.setCategoryId(dimNames);
                    final String primarySubCategory = currentPageProperties
                            .get(CommonConstants.PIM_PRIMARY_SUB_CATEGORY, StringUtils.EMPTY);
                    try(ResourceResolver adminResourceResolver = adminService.getReadService()) {
                        metaTagGenericValuesModel.setCategoryName(
                                CommonUtil.getLinkTitle(null, primarySubCategory, adminResourceResolver));
                    }
                }

                if ((firstLevelTag != null && secondLevelTag != null) && (secondLevelTag.getName() != null)) {

                    if (dimensionTagMap.containsKey(firstLevelTag.getName() + "_" + secondLevelTag.getName())) {

                        dimensionTag = dimensionTagMap.get(firstLevelTag.getName() + "_" + secondLevelTag.getName());
                        if (dimValues != null) {

                            if ((dimensionTag.getValues() != null)
                                    && (!dimensionTag.getValues().equals(StringUtils.EMPTY))) {
                                dimValues = dimensionTag.getValues() + CommonConstants.DOUBLE_PIPE + dimValues;
                            }
                            dimensionTag.setValues(dimValues);
                        }
                        if (dimNames != null) {

                            if ((dimensionTag.getNames() != null)
                                    && (!dimensionTag.getNames().equals(StringUtils.EMPTY))) {
                                dimNames = dimensionTag.getNames() + CommonConstants.DOUBLE_PIPE + dimNames;
                            }
                            dimensionTag.setNames(dimNames);
                        }
                    } else {
                        dimensionTag.setKey(firstLevelTag.getName() + "_" + secondLevelTag.getName());
                        dimensionTag.setId(secondLevelTag.getName());
                        if (secondLevelTag.getLocalizedTitle(locale) != null) {
                            dimensionTag.setLabel(secondLevelTag.getLocalizedTitle(locale));
                        } else if (secondLevelTag.getTitle() != null) {
                            dimensionTag.setLabel(secondLevelTag.getTitle());
                        }
                        if (dimValues != null) {
                            dimensionTag.setValues(dimValues);
                        }
                        if (dimNames != null) {
                            dimensionTag.setNames(dimNames);
                        }
                    }
                }
            }

            if ((dimensionTag.getKey() != null) && (!dimensionTag.getKey().equals(StringUtils.EMPTY))) {
                if (!dimensionTagMap.containsKey(dimensionTag.getKey())) {
                    dimensionTagValues.add(dimensionTag);
                }
                dimensionTagMap.put(dimensionTag.getKey(), dimensionTag);
            }
        }
        LOG.debug("MetaTagsModel : Exit from setCategoryAndDimensionalMapMetaData() method");
    }

    private boolean checkSubmittalBuilderPage() {
        LOG.debug("MetaTagsModel : Entry into checkSubmittalBuilderPage() method");
        final SlingQuery resources = $(currentPage.getContentResource())
                .find(AssetDownloadConstants.SUBMITTAL_BUILDER_RESOURCE_PATH);
        final List<Resource> submittalBuilderResource = resources.asList();
        LOG.debug("MetaTagsModel : Exit from checkSubmittalBuilderPage() method");
        return !submittalBuilderResource.isEmpty();
    }

    public String excludecountry() {
        String[] excludeCountryArray = countryLangCodeConfigService.getExcludeCountryList();
        return String.join("||", excludeCountryArray);
    }

    public boolean isCqPage() {
        return (currentPageProperties!= null &&
                null != currentPageProperties.get(CommonConstants.JCR_PRIMARY_TYPE) && currentPageProperties
                .get(CommonConstants.JCR_PRIMARY_TYPE).toString().equalsIgnoreCase(CommonConstants.CQ_PAGE_CONTENT))
                        ? Boolean.TRUE
                        : Boolean.FALSE;
    }

    public static boolean isIsSubmittalBuilder() {
        return isSubmittalBuilder;
    }

    public String getExcludeCountryList() {
        return excludeCountryList;
    }

    /*
    * Set meta tags with the page's site configuration properties
     */
    private void setMetaTagsWithSiteConfig() {
        LOG.debug("MetaTagModel :: setMetaTagsWithSiteConfig() :: START");

        try {
            final EatonSiteConfigModel eatonSiteConfigModel = slingRequest.adaptTo(EatonSiteConfigModel.class);
            if (null == eatonSiteConfigModel || null == eatonSiteConfigModel.getSiteConfig()) {
                throw new ResourceNotFoundException("Cannot find site configuration for page.");
            }
            final SiteConfigModel siteConfiguration = eatonSiteConfigModel.getSiteConfig();
            setSeoScriptPath(siteConfiguration, eatonSiteConfigModel);
            setSiteVerificationCodeModelList(SiteConfigUtil.populateSiteVerificationCodeModelList(siteConfiguration));

        } catch (ResourceNotFoundException e) {
            LOG.error("{} {}", e.getMessage(), e.getCause(), e);
        }

        LOG.debug("MetaTagModel :: setMetaTagsWithSiteConfig() :: END");
    }

    /*
    * Get the SEO script path from the current site's configuration and set it in the MetaTagValuesModel
    * If SEO script path is blank or not found in a site configuration, the script is not added to the page (this logic is a htl expression in head.html)
     */
    private void setSeoScriptPath(SiteConfigModel siteConfiguration, EatonSiteConfigModel eatonSiteConfigModel){
        LOG.debug("MetaTagsModel : setSeoScriptPath : START");

        String seoScriptPath = siteConfiguration.getSeoScriptPath();
        if (StringUtils.isEmpty(seoScriptPath)) {
            LOG.warn("Seo js script not added to page. SeoScriptPath is blank in the {} site configuration.", eatonSiteConfigModel.getCurrentLanguage());
        } else {
            try (ResourceResolver resourceResolver = adminService.getReadService()) {
                Resource seoScriptResource = resourceResolver.resolve(seoScriptPath);
                if (ResourceUtil.isNonExistingResource(seoScriptResource)) {
                    throw new ResourceNotFoundException("Seo script resource not found at path: {}", seoScriptPath);
                }
                metaTagGenericValuesModel.setSeoScriptPath(seoScriptPath);
                LOG.debug("SeoScriptPath set in page's MetaTagsValueModel to {}", seoScriptPath);
            } catch (ResourceNotFoundException rnf) {
                LOG.error("SeoScriptPath not showing on page. SeoScriptPath is set in configuration but is not set in page's MetaTagsValueModel.");
                LOG.error("{}\n{}\n{}", rnf.getMessage(), rnf.getResource(), rnf.getCause());
            }
        }

        LOG.debug("MetaTagsModel : setSeoScriptPath : END");
    }

    /**
     * Externalize image path and set to image tags
     * @param imagePath
     */
    private void setImageTags(String imagePath) {
        LOG.trace("setImageTags : START");

        LOG.debug("image path: {}", imagePath);

        if (StringUtils.isNotBlank(imagePath)) {
            String imageExternalLink = CommonUtil.dotHtmlLink(imagePath, resource.getResourceResolver());
            LOG.debug("image external link: {}", imageExternalLink);
            String imageTagContent = StringUtils.isNotBlank(imageExternalLink) ? imageExternalLink : imagePath;
            LOG.debug("image tag content: {}", imageTagContent);
            metaTagGenericValuesModel.setOgImage(imageTagContent);
            LOG.debug("ogImage: {}", imageTagContent);
            metaTagGenericValuesModel.setTwitterImage(imageTagContent);
            LOG.debug("twitterImage: {}", imageTagContent);
        } else {
            LOG.warn("Image path is blank. Image tags not set.");
        }

        LOG.trace("setImageTags : END");
    }


    public List<SiteVerificationCodeModel> getSiteVerificationCodeModelList() {
        return siteVerificationCodeModelList;
    }

    public void setSiteVerificationCodeModelList(List<SiteVerificationCodeModel> siteVerificationCodeModelList) {
        this.siteVerificationCodeModelList = siteVerificationCodeModelList;
    }

    public String generatePrimaryImage(String primaryImage,ResourceResolver resourceResolver){
		LOG.debug("Inside generatePrimaryImage: {}", primaryImage);
        Resource assetResource = CommonUtil.getResourceFromLinkPath(primaryImage,resourceResolver);
        if(null == assetResource){
			LOG.debug("Asset resource is null so PrimaryImage is: {}", primaryImage);
            return primaryImage;
        }
        Asset asset = assetResource.adaptTo(Asset.class);
        if(null == asset){
			LOG.debug("Final PrimaryImage: {}", primaryImage);
            return primaryImage;
        }
        DynamicMediaAsset dynamicMediaAsset = asset.adaptTo(DynamicMediaAsset.class);
		primaryImage = dynamicMediaAsset.getSCImageOrDefault(DM_PROFILE_MEGA_MENU_FEATURED_LIST_DESK, AssetConstants.RENDITION_319BY319);
		LOG.debug("Final PrimaryImage: {}", primaryImage);
        return primaryImage;
    }

}
