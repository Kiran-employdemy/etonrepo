package com.eaton.platform.core.models;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.commons.util.DamUtil;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.bean.HowToBuyBean;
import com.eaton.platform.core.bean.ProductCardBean;
import com.eaton.platform.core.bean.ProductFamilyDetails;
import com.eaton.platform.core.bean.SecondaryLinksBean;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.ProductFamilyDetailService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.eloqua.util.EloquaUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.NonExistingResource;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;

@Model(adaptables = {Resource.class,
        SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ProductFamilyCardModel {
    @Inject
    @ScriptVariable
    private Page currentPage;
    @Self
    private SlingHttpServletRequest slingHttpServletRequest;
    @Inject
    @Source("sling-object")
    private ResourceResolver resourceResolver;

    @Inject
    protected AdminService adminService;

    @Inject
    /** The page manager. */
    protected PageManager pageManager;

    @Inject
    protected ProductFamilyDetailService productFamilyDetailService;

    public static final int LIMIT = 5;

    private ProductCardBean productCardBean = new ProductCardBean();
    private List<SecondaryLinksBean> secondaryLinksBeanList = new ArrayList<SecondaryLinksBean>();
    private SecondaryLinksBean secondaryLinksBean;
    private static final String PIM_SITE_CONFIG_PAGE_FOLDER_PATH = "/etc/cloudservices/siteconfig/";
    private static final String PROPERTY_DEFAULT_LINK_CTA = "defaultLinkCTA";
    private static final String REPFL_YES = "Y";
    private Locale pageLang;
    private static final Logger LOG = LoggerFactory
            .getLogger(ProductFamilyCardModel.class);
    private ProductFamilyDetails productFamilyDetails;

    private String eyebrowTitle;
    private String howToBuyLabel;

    private String defaultHowtoBuyCTALink = StringUtils.EMPTY;
    private static final String DEFAULT_HOWTOBUY = "defaultLinkHTB";
    @PostConstruct
    protected void init() {
        LOG.debug("ProductFamilyCardModel :: init() :: Started");
        if (currentPage != null) {
            getPageLang();
        }
        productFamilyDetails = productFamilyDetailService
                .getProductFamilyDetailsBean(currentPage);
        String defaultCTALink = StringUtils.EMPTY;
        if (null != pageLang && null != adminService) {
            try (ResourceResolver adminResourceResolver = adminService
                    .getReadService()) {
                final String locale = pageLang.toString().toLowerCase();
                Page siteConfigPage = adminResourceResolver.resolve(PIM_SITE_CONFIG_PAGE_FOLDER_PATH.concat(locale))
                        .adaptTo(Page.class);
                if (null != currentPage) {
                    String mappedPagePath = adminResourceResolver.map(currentPage.getPath());
                    if (StringUtils.isNotEmpty(mappedPagePath)) {
                        mappedPagePath = CommonUtil.removeSiteContentRootPathPrefix(mappedPagePath);
                        productCardBean.setExternalMappedCurrentPagePath(mappedPagePath);
                        populateNewProductBadge(adminResourceResolver, currentPage.getPath());
                    }
                }
                if (null != siteConfigPage) {
                    defaultCTALink = siteConfigPage.getProperties().get(
                            PROPERTY_DEFAULT_LINK_CTA, String.class);
                    defaultHowtoBuyCTALink = siteConfigPage.getProperties().get(
                            DEFAULT_HOWTOBUY, String.class);
                } else {
                    LOG.error("Unable to locate the siteconfigPage for this local");
                }
            }
        }
        if (productFamilyDetails != null && currentPage != null) {
            Boolean isProductDiscontinuedForthisCountry = false;
            if (null != currentPage) {
                isProductDiscontinuedForthisCountry = checkForDiscountinuedTagOnPage(currentPage);
            }
            populateProductCard(productFamilyDetails, defaultCTALink, isProductDiscontinuedForthisCountry);
        } else {
            LOG.debug("Product Family object is null");
        }
        LOG.debug("ProductFamilyCardModel :: init() :: END");
    }

    private static Boolean checkForDiscountinuedTagOnPage(Page currentPage) {
        ValueMap properties = currentPage.getProperties();
        String[] cqTags = properties.get(CommonConstants.CQ_TAGS, String[].class);
        if (cqTags != null && cqTags.length > 0) {
            for (String tag : cqTags) {
                if (tag.contains(CommonConstants.DISCONTINUE_TAG_PATH)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void populateProductCard(ProductFamilyDetails pfDetails, String defaultCTALink,
                                     Boolean isProductDiscontinuedForthisCountry) {
        final String productFamilyName = pfDetails.getProductFamilyName() != null ? pfDetails
                .getProductFamilyName() : CommonConstants.BLANK_SPACE;
        productCardBean.setProductFamilyName(productFamilyName);
        if ((null != pfDetails.getPimTags()
                && String.join(CommonConstants.COMMA,
                pfDetails.getPimTags()).contains(
                CommonConstants.DISCONTINUE_TAG))
                || isProductDiscontinuedForthisCountry) {
            final TagManager tagManager = resourceResolver
                    .adaptTo(TagManager.class);
            final Tag cqTag = tagManager
                    .resolve(CommonConstants.DISCONTINUE_TAG_PATH);
            if (null != cqTag) {
                final Locale locale = CommonUtil
                        .getLocaleFromPagePath(currentPage);
                productCardBean
                        .setStatus(StringUtils.equals(locale.toString(),
                                CommonConstants.FALLBACK_LOCALE)
                                ? cqTag
                                .getTitle()
                                : cqTag.getTitle(locale));
            }
        }
        productCardBean
                .setMarketingDesc(pfDetails.getMarketingDesc() != null ? pfDetails
                        .getMarketingDesc() : CommonConstants.BLANK_SPACE);
        populateCTADetails(pfDetails, defaultCTALink);

        productCardBean.setAltPrimaryImage(
                pfDetails.getAltPrimaryImage() != null ? pfDetails.getAltPrimaryImage() : CommonConstants.BLANK_SPACE);

        String spinImageSet = pfDetails.getPdhSpinnerImage() != null ? pfDetails.getPdhSpinnerImage() : "";
        if (StringUtils.isNotEmpty(spinImageSet)) {
            productCardBean.setPrimaryOrSpinImage(spinImageSet);
        } else {
            productCardBean.setPrimaryOrSpinImage(
                    pfDetails.getPrimaryImage() != null ? pfDetails.getPrimaryImage() : CommonConstants.BLANK_SPACE);
        }
        if (pfDetails.getSpinImages() != null) {
            productCardBean.setSpinImages(pfDetails.getSpinImages());
        }
        if ((pfDetails.getRepfl() != null) && (pfDetails.getRepfl().equals(REPFL_YES))) {
            productCardBean.setPrimaryImageIsRepresentative(Boolean.TRUE);
        } else {
            productCardBean.setPrimaryImageIsRepresentative(Boolean.FALSE);
        }

        List<String> imagesList = new ArrayList<String>();
        if (StringUtils.isNotEmpty(productCardBean.getPrimaryOrSpinImage())) {
            imagesList.add(productCardBean.getPrimaryOrSpinImage());
        } else if (StringUtils.isNotEmpty(productCardBean.getSpinImages())) {
            imagesList.add(productCardBean.getSpinImages());
        }
        if (pfDetails.getSecondaryImage() != null) {
            getAssetsFromDamPath(imagesList, pfDetails.getSecondaryImage());
        }
        productCardBean.setPrimaryAndSecondaryImagesList(imagesList);

        if (pfDetails.getSecondaryLinksList() != null) {

            for (SecondaryLinksBean items : pfDetails
                    .getSecondaryLinksList()) {
                secondaryLinksBean = new SecondaryLinksBean();
                secondaryLinksBean.setText(items.getText());
                String path = items.getPath();
                secondaryLinksBean.setNewWindow(items.getNewWindow());
                secondaryLinksBean.setPath(CommonUtil.dotHtmlLink(path, resourceResolver));
                secondaryLinksBean.setIsExternal(CommonUtil
                        .getIsExternal(path));
                secondaryLinksBean.setSecLinkSkuOnly(items.isSecLinkSkuOnly());
                secondaryLinksBeanList.add(secondaryLinksBean);

            }
            productCardBean
                    .setSecondaryLinksList(CommonUtil.filterSkuSecondaryLinkOptions(secondaryLinksBeanList, false));
        } else {
            LOG.debug("No Secondary links available");
        }

        if (pfDetails.getPrimaryCTAEnableSourceTracking() != null) {
            productCardBean.setPrimaryCTAEnableSourceTracking(pfDetails.getPrimaryCTAEnableSourceTracking());
        } else {
            LOG.warn("Cannot find primary cta source tracking enabled value");
        }
    }

    private void getAssetsFromDamPath(List<String> secondaryImagesList, String secondaryImage) {
        LOG.debug("ProductFamilyCardModel :: getAssetsFromDamPath() :: Started");
        try {
            Resource resource = resourceResolver.getResource(secondaryImage);
            if (resource != null) {
                Asset asset = resource.adaptTo(Asset.class);
                if (asset != null && DamUtil.isImage(asset)) {
                    secondaryImagesList.add(asset.getPath());
                } else {
                    List<Asset> secondaryImageAssetList = new ArrayList<>();
                    for (Resource childResource : resource.getChildren()) {
                        Asset childAsset = childResource.adaptTo(Asset.class);
                        if (childAsset != null && DamUtil.isImage(childAsset)) {
                            secondaryImageAssetList.add(childAsset);
                        }
                    }
                    if (!secondaryImageAssetList.isEmpty()) {
                        getSortedAssetList(secondaryImageAssetList);
                        for (int i = 0; i < secondaryImageAssetList.size(); i++) {
                            if (i < LIMIT) {
                                Asset imageAsset = secondaryImageAssetList.get(i);
                                secondaryImagesList.add(imageAsset.getPath());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Exception while fetching assets from dam path : {} " + e.getMessage());
        }
        LOG.debug("ProductFamilyCardModel :: getAssetsFromDamPath() :: END");
    }

    private void getSortedAssetList(List<Asset> list) {
        LOG.debug("ProductFamilyCardModel :: getSortedAssetList() :: Started");
        Collections.sort(list, new Comparator<Asset>() {
            public int compare(Asset asset1, Asset asset2) {
                int comparisonValue = 0;
                comparisonValue = CommonUtil.compareDate(new Date(asset1.getLastModified()),
                        new Date(asset2.getLastModified()));
                return comparisonValue;
            }
        });
        LOG.debug("ProductFamilyCardModel :: getSortedAssetList() :: END");
    }

    private void populateCTADetails(ProductFamilyDetails pfDetails, String defaultCTALink) {
        productCardBean
                .setPrimaryCTALabel(pfDetails.getPrimaryCTALabel() != null ? pfDetails
                        .getPrimaryCTALabel() : CommonConstants.BLANK_SPACE);
        final String primaryCTAURL = StringUtils.isNotBlank(pfDetails.getPrimaryCTAURL())
                ? pfDetails.getPrimaryCTAURL() : defaultCTALink;
        String linkURL = CommonUtil.dotHtmlLink(primaryCTAURL, resourceResolver);
        if (linkURL.equalsIgnoreCase(CommonUtil.dotHtmlLink("", resourceResolver))) {
            linkURL = linkURL.concat(productCardBean.getExternalMappedCurrentPagePath()).concat(CommonConstants.HTML_EXTN);
        }

        String primaryProductTaxonomy = pfDetails.getPrimaryProductTaxonomy();
        linkURL = EloquaUtil.hasEloquaForm(primaryCTAURL, adminService) ? EloquaUtil.appendPrimaryProductTaxonomy(linkURL, primaryProductTaxonomy) : linkURL;

        productCardBean.setPrimaryCTAURL(linkURL);
        productCardBean
                .setIsSuffixDisabled(pfDetails.getIsSuffixDisabled());
        productCardBean.setPrimaryCTANewWindow(pfDetails
                .getPrimaryCTANewWindow());
        productCardBean.setPrimaryCTAEnableSourceTracking(pfDetails.getPrimaryCTAEnableSourceTracking());

    }

    /* Get the pageLang */
    private void getPageLang() {
        if (currentPage.getLanguage(Boolean.FALSE) != null) {
            pageLang = currentPage.getLanguage(Boolean.FALSE);
        }
    }

    public ProductCardBean getProductCardBean() {
        return productCardBean;

    }

    public String getEyebrowLink() {
        if(productFamilyDetails != null) {
            String primarySubCategory = productFamilyDetails.getPrimarySubCategory();
            if (primarySubCategory.contains(CommonConstants.HTML_EXTN)) {
                return primarySubCategory;
            }
            return primarySubCategory + CommonConstants.HTML_EXTN;
        }
        return null;
    }

    public String getEyebrowTitle() {
        if (eyebrowTitle != null) {
            return eyebrowTitle;
        }
        if (productFamilyDetails != null) {
            Resource resource = resourceResolver.getResource(Objects.requireNonNull(productFamilyDetails.getPrimarySubCategory()));
            if(resource == null || resource instanceof NonExistingResource) {
                return null;
            }
            Page primarySubCategoryPage = Objects.requireNonNull(resource).adaptTo(Page.class);
            Page page = Objects.requireNonNull(primarySubCategoryPage);
            eyebrowTitle = page.getNavigationTitle();
            if (eyebrowTitle == null) {
                eyebrowTitle = page.getPageTitle();
            }
            if (eyebrowTitle == null) {
                eyebrowTitle = page.getTitle();
            }
        }
        return eyebrowTitle;
    }
    public List<HowToBuyBean> getHowToBuyOptions() {
        if (productFamilyDetails == null) {
            return new ArrayList<>();
        }
        List<HowToBuyBean> howToBuyList = productFamilyDetails.getHowToBuyList();
        if(CollectionUtils.isEmpty(howToBuyList)) {
            return new ArrayList<>();
        }
        HowToBuyBean howToBuyBean = howToBuyList.get(0);
        if (StringUtils.isNotEmpty(howToBuyBean.getTitle())) {
            return howToBuyList;
        }
        howToBuyBean.setLink(defaultHowtoBuyCTALink);
        howToBuyBean.setTitle(null);
        return howToBuyList;
    }

    public String getHowToBuyLabel(){
        if (howToBuyLabel != null){
            return howToBuyLabel;
        }
        howToBuyLabel = CommonUtil.getI18NFromResourceBundle(slingHttpServletRequest, currentPage, CommonConstants.HOW_TO_BUY_LABEL, CommonConstants.HOW_TO_BUY_LABEL_DEFAULT);
        return howToBuyLabel;
    }

    public String getHowToBuyLink(){
        if (productFamilyDetails != null) {
            String howToBuyLink = productFamilyDetails.getHowToBuyLink();
            if (howToBuyLink == null) {
                return defaultHowtoBuyCTALink;
            }
            return howToBuyLink;
        }
        return defaultHowtoBuyCTALink;
    }

    private void populateNewProductBadge(ResourceResolver adminResourceResolver, String pagePathLink) {
        ValueMap pageProperties = null;
        String publicationDate = "";
        SimpleDateFormat publicationDateFormat = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT_PUBLISH);
        Resource linkPathResource = adminResourceResolver.getResource(pagePathLink);
        if (null != linkPathResource) {
            Resource jcrResource = linkPathResource.getChild(CommonConstants.JCR_CONTENT_STR);
            if (null != jcrResource) {
                pageProperties = jcrResource.getValueMap();
            }
            if (pageProperties != null) {
                publicationDate = CommonUtil.getDateProperty(pageProperties, CommonConstants.PUBLICATION_DATE,
                        publicationDateFormat);

                if (publicationDate != null && !publicationDate.isEmpty()) {
                    long noOfDays = CommonUtil.getDaysDifference(publicationDate);
                    if (noOfDays >= 0 && noOfDays <= 90)
                        productCardBean.setnewBadgeVisible(true);
                    else
                        productCardBean.setnewBadgeVisible(false);
                } else {
                    productCardBean.setnewBadgeVisible(false);
                    LOG.debug("populateNewProductBadge:publicationDate is null for Family Page ");
                }
            }
        }
    }

}