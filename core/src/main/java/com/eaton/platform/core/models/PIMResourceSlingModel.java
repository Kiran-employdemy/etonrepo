package com.eaton.platform.core.models;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.ProductFamilyPDHDetails;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.CloudConfigService;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

import static org.apache.commons.lang3.StringUtils.EMPTY;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class PIMResourceSlingModel {
    private static final Logger LOG = LoggerFactory.getLogger(PIMResourceSlingModel.class);
    private static final String PRIMARY_CTA_LABEL_PROPERTY = "primaryCTALabel";
    private static final String PRIMARY_CTA_URL_PROPERTY = "primaryCTAURL";
    private static final String PRIMARY_CTA_ENABLE_SOURCE = "enableSourceTrackingPrimaryCTA";
    private static final String PRIMARY_CTA_NEW_WINDOW_PROPERTY = "primaryCTANewWindow";
    private static final String PRIMARY_CTA_COUNTRY_PROPERTY = "country";
    private static final String PROPERTY_ATTRIBUTE_DISPLAY_DATA = "attributeDisplayName";
    private static final String PROPERTY_ATTRIBUTE_LABEL = "attributeName";
    private static final String SUPPORT_INFO = "supportInfo";
    private static final String HOW_TO_BUY = "howToBuy";
    private static final String SECONDARY_LINKS = "secondaryLinks";
    private static final String RESOURCE_SECTION = "resourceSection";
    private static final String PRIMARY_CTAS = "primaryctas";

    @Self
    private Resource resource;
    @Inject
    @Optional
    @Named("jcr:title")
    private String extensionId;
    @Inject
    @Optional
    private String pdhRecordPath;
    @Inject
    @Optional
    private String identifier;
    @Inject
    @Optional
    private String productName;
    @Inject
    @Optional
    private String marketingDesc;
    @Inject
    @Optional
    @Default(values = "false")
    private String enableSourceTracking;
    @Inject
    @Optional
    @Default(values = "false")
    private String enableSourceTrackingPrimaryCTA;
    @Inject
    @Optional
    private String primaryImage;
    @Inject
    @Optional
    private String secondaryImage;
    @Inject
    @Optional
    private String altPrimaryImage;
    @Inject
    @Optional
    private String primarySubCategory;
    @Inject
    @Optional
    private String coreFeatures;
    @Inject
    @Optional
    private String featureImage;
    @Inject
    @Optional
    private String showLongDesc;
    @Inject
    @Optional
    private String productFamilyPage;

    @Inject
    @Optional
    private String seoTitleResourcesTab;
    @Inject
    @Optional
    private String seoTitleModelsTab;

    @Inject
    @Optional
    private String seoDescResourcesTab;
    @Inject
    @Optional
    private String seoDescModelsTab;

    @Inject
    @Optional
    private String spinImages;

    public String getSpinImages() {
        return spinImages;
    }


    public void setSpinImages(String spinImages) {
        this.spinImages = spinImages;
    }

    @Reference
    private CloudConfigService cloudConfigService;
    @Inject
    @Optional
    private String primaryCTALabel;
    @Inject
    @Optional
    private String primaryCTAURL;
    @Inject
    @Optional
    private String primaryCTAMakeGlobal;
    @Inject
    @Optional
    @Default(values = "false")
    private String primaryCTANewWindow;
    @Inject
    @Optional
    @Default(values = "false")
    private String isSuffixDisabled;


    @Inject
    @Optional
    private String MiddleTabTitle;

    @Inject
    @Optional
    @Named("cq:tags")
    private String[] tags;

    @Inject
    @Optional
    @Named("cq:primaryProductTaxonomy")
    private String primaryProductTaxonomy;

    @Inject
    private Resource skuCardAttributes;
    @Inject
    private Resource supportInfo;
    @Inject
    private HowToBuyOptionsModel howToBuy;
    @Inject
    private Resource secondaryLinks;
    @Inject
    private Resource resourceSection;

    @Inject
    @Optional
    private Resource taxonomyAttributes;

    @Inject
    @Optional
    private Resource primaryctas;

    @Inject
    @Optional
    private String dataFromSingleProductFamilyPage;

    @Inject
    @Optional
    private String seoTitleOverviewTab;

    @Inject
    @Optional
    private String seoDescOverviewTab;

    private String currentCountry;
    private Resource primaryCTAResource;
    private Resource selectedExtnIdRes;
    private String spinImage;
    private String resolvedPrimaryImage;
    private String commonPIMPrimaryCTAURL;
    private ProductFamilyPDHDetails productFamilyPDHDetails;
    private String repfl;

    // Injecting AdminService Reference
    @Inject
    private AdminService adminService;

    /**
     * @return the pdhProdName
     */
    public String getPdhProdName() {
        if (Objects.nonNull(productFamilyPDHDetails)) {
            return productFamilyPDHDetails.getProductName();
        } else {
            return StringUtils.EMPTY;
        }

    }

    /**
     * @return the pdhProdDesc
     */
    public String getPdhProdDesc() {
        if (Objects.nonNull(productFamilyPDHDetails)) {
            return productFamilyPDHDetails.getMarketingDescription();
        } else {
            return StringUtils.EMPTY;
        }
    }

    /**
     * @return the pdhCoreFeatures
     */
    public String getPdhCoreFeatures() {
        if (Objects.nonNull(productFamilyPDHDetails)) {
            return productFamilyPDHDetails.getCoreFeatures();
        } else {
            return StringUtils.EMPTY;
        }

    }


    /**
     * @return the pdhSupportInfo
     */
    public String getPdhSupportInfo() {
        if (Objects.nonNull(productFamilyPDHDetails)) {
            return productFamilyPDHDetails.getSupportInfo();
        } else {
            return StringUtils.EMPTY;
        }
    }


    /**
     * @return the dataFromSingleProductFamilyPage
     */
    public String getDataFromSingleProductFamilyPage() {
        return dataFromSingleProductFamilyPage;
    }


    /**
     * @param dataFromSingleProductFamilyPage the dataFromSingleProductFamilyPage to set
     */
    public void setDataFromSingleProductFamilyPage(String dataFromSingleProductFamilyPage) {
        this.dataFromSingleProductFamilyPage = dataFromSingleProductFamilyPage;
    }


    /**
     * @return the taxonomyAttributes
     */
    public Resource getTaxonomyAttributes() {
        return taxonomyAttributes;
    }

    public String getPdhRecordPath() {
        return pdhRecordPath;
    }

    public void setPdhRecordPath(String pdhRecordPath) {
        this.pdhRecordPath = pdhRecordPath;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getMarketingDesc() {
        return marketingDesc;
    }

    public void setMarketingDesc(String marketingDesc) {
        this.marketingDesc = marketingDesc;
    }

    public String getPrimaryImage() {
        return primaryImage;
    }

    public void setPrimaryImage(String primaryImage) {
        this.primaryImage = primaryImage;
    }

    public String getSecondaryImage() {
        return secondaryImage;
    }

    public void setSecondaryImage(String secondaryImage) {
        this.secondaryImage = secondaryImage;
    }

    public String getAltPrimaryImage() {
        return altPrimaryImage;
    }

    public void setAltPrimaryImage(String altPrimaryImage) {
        this.altPrimaryImage = altPrimaryImage;
    }

    public String getPrimarySubCategory() {
        return primarySubCategory;
    }

    public void setPrimarySubCategory(String primarySubCategory) {
        this.primarySubCategory = primarySubCategory;
    }

    public String getCoreFeatures() {
        return coreFeatures;
    }

    public void setCoreFeatures(String coreFeatures) {
        this.coreFeatures = coreFeatures;
    }

    public String getFeatureImage() {
        return featureImage;
    }

    public void setFeatureImage(String featureImage) {
        this.featureImage = featureImage;
    }

    public String getShowLongDesc() {
        return showLongDesc;
    }

    public void setShowLongDesc(String showLongDesc) {
        this.showLongDesc = showLongDesc;
    }

    public String getProductFamilyPage() {
        return productFamilyPage;
    }

    public void setProductFamilyPage(String productFamilyPage) {
        this.productFamilyPage = productFamilyPage;
    }

    public String getSeoTitleResourcesTab() {
        return seoTitleResourcesTab;
    }

    public void setSeoTitleResourcesTab(String seoTitleResourcessTab) {
        this.seoTitleResourcesTab = seoTitleResourcessTab;
    }

    public String getSeoTitleModelsTab() {
        return seoTitleModelsTab;
    }

    public void setSeoTitleModelsTab(String seoTitleModelsTab) {
        this.seoTitleModelsTab = seoTitleModelsTab;
    }

    public void setCurrentCountry(String country) {
        this.currentCountry = country;
    }

    public String getRepfl() {
        return repfl;
    }

    public void setRepfl(String repfl) {
        this.repfl = repfl;
    }


    /**
     * This provides the correct CTA Label to based on the country provided to the
     * setCurrentCountry method. If either no country has been provided or there is
     * no specific primary CTA for that country then the default primary CTA will be used.
     *
     * @param currentPage
     * @return The CTA Label authored under this PIMNode that is used for the provided country.
     */
    public String getPrimaryCTALabel(final Page currentPage) {
        findPrimaryCTAResource();
        String label = primaryCTALabel;

        if (primaryCTAResource != null) {
            label = primaryCTAResource.getValueMap().get(PRIMARY_CTA_LABEL_PROPERTY, EMPTY);
        }
		/*If the PFP or SKU PIM is null then get the
		 data from common PIM which is Configured in parent page of PFP or SKU*/
        final Resource commonPIMResource = getCommonPIMResource(currentPage);
        if (StringUtils.isEmpty(label) && null != commonPIMResource) {
            label = commonPIMResource.getValueMap().get(PRIMARY_CTA_LABEL_PROPERTY, EMPTY);
        }
        String CTALabel;
        if (!StringUtils.isEmpty(primaryCTALabel)) {
            CTALabel = label.equals(StringUtils.EMPTY) ? primaryCTALabel : label;
        } else {
            CTALabel = label;
        }
        return CTALabel;
    }

    /**
     * This provides the correct CTA URL to based on the country provided to the
     * setCurrentCountry method. If either no country has been provided or there is
     * no specific primary CTA for that country then the default primary CTA will be used.
     *
     * @param currentPage
     * @return The CTA URL authored under this PIMNode that is used for the provided country.
     */
    public String getPrimaryCTAURL(Page currentPage) {
        String url = primaryCTAURL;
        findPrimaryCTAResource();

        if (primaryCTAResource != null) {
            url = primaryCTAResource.getValueMap().get(PRIMARY_CTA_URL_PROPERTY, "");

        }
		/*If the PFP or SKU PIM is null then get the
		 data from common PIM which is Configured in parent page of PFP or SKU*/
        final Resource commonPIMResource = getCommonPIMResource(currentPage);
        if (StringUtils.isEmpty(url) && null != commonPIMResource) {
            url = commonPIMResource.getValueMap().get(PRIMARY_CTA_URL_PROPERTY, "");
            commonPIMPrimaryCTAURL = url;
        }
        String CTAurl = StringUtils.EMPTY;
        if (!StringUtils.isEmpty(primaryCTALabel)) {
            CTAurl = url.equals(StringUtils.EMPTY) ? primaryCTAURL : url;
        } else {
            CTAurl = url;
        }
        try (ResourceResolver adminServiceReadService = adminService.getReadService()) {
            String currentLink = CTAurl;
            if (primaryCTAMakeGlobal != null) {
                CTAurl = CommonUtil.updateCTAURLString(currentLink, currentPage, adminServiceReadService);
            } else {
                return currentLink;
            }
        }
        return CTAurl;
    }

    /**
     * This provides the correct CTA New Window value based on the country provided to the
     * setCurrentCountry method. If either no country has been provided or there is
     * no specific primary CTA for that country then the default primary CTA will be used.
     *
     * @param currentPage
     * @return Either CommonConstants.TARGET_BLANK or CommonConstants.TARGET_SELF
     * for the CTA used in the provided country.
     */
    public String getPrimaryCTANewWindow(Page currentPage) {
        String newWindow = primaryCTANewWindow;
        findPrimaryCTAResource();

        if (primaryCTAResource != null) {
            newWindow = primaryCTAResource.getValueMap().get(PRIMARY_CTA_NEW_WINDOW_PROPERTY, "");
        }
		/*If the PFP or SKU PIM is null then get the
		 data from common PIM which is Configured in parent page of PFP or SKU*/
        final Resource commonPIMResource = getCommonPIMResource(currentPage);
        if (StringUtils.isEmpty(newWindow) && StringUtils.isNotEmpty(commonPIMPrimaryCTAURL) && null != commonPIMResource) {
            if (null != commonPIMResource.getValueMap().get(PRIMARY_CTA_NEW_WINDOW_PROPERTY, StringUtils.EMPTY)) {
                newWindow = commonPIMResource.getValueMap().get(PRIMARY_CTA_NEW_WINDOW_PROPERTY, StringUtils.EMPTY);
            }
        }
        return CommonConstants.TRUE.equalsIgnoreCase(newWindow) ? CommonConstants.TARGET_BLANK : CommonConstants.TARGET_SELF;
    }

    public String getMiddleTabTitle() {
        return MiddleTabTitle;
    }

    public void setMiddleTabTitle(String middleTabTitle) {
        MiddleTabTitle = middleTabTitle;
    }

    public Resource getSkuCardAttributes() {
        return skuCardAttributes;
    }

    public void setSkuCardAttributes(Resource skuCardAttributes) {
        this.skuCardAttributes = skuCardAttributes;
    }

    public SupportInfoModel getSupportInfo(Page currentPage) {
        final Resource commonPIMResource = getCommonPIMResource(currentPage);
        if (supportInfo != null) {
            return supportInfo.adaptTo(SupportInfoModel.class);
        } else if (null != commonPIMResource && null != commonPIMResource.getChild(SUPPORT_INFO)) {
			/*If the PFP or SKU PIM is null then get the
		    data from common PIM which is Configured in parent page of PFP or SKU*/
            final SupportInfoModel supportInfoValue = commonPIMResource.getChild(SUPPORT_INFO).adaptTo(SupportInfoModel.class);
            return supportInfoValue;
        } else {
            return null;
        }
    }

    public HowToBuyOptionsModel getHowToBuy(Page currentPage) {
        if (howToBuy == null) {
            String pimPagePathFromCurrentPage = currentPage.getProperties().get(CommonConstants.PAGE_PIM_PATH, StringUtils.EMPTY);
            Resource commonPIMResource;
            if (!pimPagePathFromCurrentPage.equals(StringUtils.EMPTY)) {
                commonPIMResource = Objects.requireNonNull(resource).getResourceResolver().getResource(pimPagePathFromCurrentPage);
            } else {
                commonPIMResource = getCommonPIMResource(currentPage);
            }
            if (commonPIMResource == null || commonPIMResource.getChild(HOW_TO_BUY) == null) {
                howToBuy = new HowToBuyOptionsModel();
                return howToBuy;
            }
            howToBuy = commonPIMResource.getChild(HOW_TO_BUY).adaptTo(HowToBuyOptionsModel.class);
        }
        return howToBuy;
    }

    public SecondaryLinksModel getSecondaryLinks(Page currentPage) {
        final Resource commonPIMResource = getCommonPIMResource(currentPage);
        if (secondaryLinks != null) {
            return secondaryLinks.adaptTo(SecondaryLinksModel.class);
        } else if (null != commonPIMResource && null != commonPIMResource.getChild(SECONDARY_LINKS)) {
			/*If the PFP or SKU PIM is null then get the
		     data from common PIM which is Configured in parent page of PFP or SKU*/
            final SecondaryLinksModel secondaryLinksValue = commonPIMResource.getChild(SECONDARY_LINKS).adaptTo(SecondaryLinksModel.class);
            return secondaryLinksValue;
        } else {
            return null;
        }
    }

    public TopicLinksModel getResourceSection(Page currentPage) {
        final Resource commonPIMResource = getCommonPIMResource(currentPage);
        if (resourceSection != null) {
            return resourceSection.adaptTo(TopicLinksModel.class);
        } else if (null != commonPIMResource && null != commonPIMResource.getChild(RESOURCE_SECTION)) {
			/*If the PFP or SKU PIM is null then get the
		     data from common PIM which is Configured in parent page of PFP or SKU*/
            final TopicLinksModel resourceSectionValue = commonPIMResource.getChild(RESOURCE_SECTION).adaptTo(TopicLinksModel.class);
            return resourceSectionValue;
        } else {
            return null;
        }
    }

    public String getExtensionId() {
        return extensionId;
    }

    public void setExtensionId(String extensionId) {
        this.extensionId = extensionId;
    }


    /**
     * @return The inventory id from the PDH node. This is defined as the parent node name of the selected PDH resource.
     */
    public String getInventoryId() {
        final String inventoryId;
        try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
            final Resource pdhRecordResource = adminResourceResolver.getResource(pdhRecordPath);

            if (pdhRecordResource != null) {
                inventoryId = pdhRecordResource.getParent().getName();
            } else {
                inventoryId = "";
                LOG.warn(String.format("Could not retrieve resource at configured PDH path: ", pdhRecordPath));
            }
        }

        return inventoryId;
    }

    /**
     * @return the tags
     */
    public String[] getTags() {
        return tags;
    }

    /**
     * @param tags the tags to set
     */
    public void setTags(String[] tags) {
        this.tags = tags;
    }

    /**
     * @return the primaryProductTaxonomy
     */
    public String getPrimaryProductTaxonomy() {
        return primaryProductTaxonomy;
    }

    /**
     * @param primaryProductTaxonomy the tags to set
     */
    public void setPrimaryProductTaxonomy(String primaryProductTaxonomy) {
        this.primaryProductTaxonomy = primaryProductTaxonomy;
    }

    /**
     * @return the seoTitleOverviewTab
     */
    public String getSeoTitleOverviewTab() {
        return seoTitleOverviewTab;
    }

    /**
     * @return the seoDescOverviewTab
     */
    public String getSeoDescOverviewTab() {
        return seoDescOverviewTab;
    }

    public String getIsSuffixDisabled() {
        return this.isSuffixDisabled;
    }

    public String getSeoDescResourcesTab() {
        return seoDescResourcesTab;
    }

    public void setSeoDescResourcesTab(String seoDescResourcesTab) {
        this.seoDescResourcesTab = seoDescResourcesTab;
    }

    public String getPrimaryCTAEnableSourceTracking(Page currentPage) {
        String primaryCTAEnableSourceTracking = getEnableSourceTracking();
        findPrimaryCTAResource();

        if (primaryCTAResource != null) {
            primaryCTAEnableSourceTracking = primaryCTAResource.getValueMap().get(PRIMARY_CTA_ENABLE_SOURCE, StringUtils.EMPTY);
        }
		/*If the PFP or SKU PIM is null then get the
		 data from common PIM which is Configured in parent page of PFP or SKU*/
        final Resource commonPIMResource = getCommonPIMResource(currentPage);
        if (StringUtils.isEmpty(primaryCTAEnableSourceTracking) && StringUtils.isNotEmpty(commonPIMPrimaryCTAURL) && null != commonPIMResource) {
            if (null != commonPIMResource.getValueMap().get(PRIMARY_CTA_ENABLE_SOURCE, StringUtils.EMPTY)) {
                primaryCTAEnableSourceTracking = commonPIMResource.getValueMap().get(PRIMARY_CTA_ENABLE_SOURCE, StringUtils.EMPTY);
            }
        }
        return StringUtils.isEmpty(primaryCTAEnableSourceTracking) ? CommonConstants.FALSE : primaryCTAEnableSourceTracking;
    }

    public String getEnableSourceTracking() {
        return enableSourceTracking;
    }

    public void setEnableSourceTracking(String enableSourceTracking) {
        this.enableSourceTracking = enableSourceTracking;
    }

    public String getEnableSourceTrackingPrimaryCTA() {
        return enableSourceTrackingPrimaryCTA;
    }

    public void setEnableSourceTrackingPrimaryCTA(String enableSourceTrackingPrimaryCTA) {
        this.enableSourceTrackingPrimaryCTA = enableSourceTrackingPrimaryCTA;
    }

    public String getSeoDescModelsTab() {
        return seoDescModelsTab;
    }

    public void setSeoDescModelsTab(String seoDescModelsTab) {
        this.seoDescModelsTab = seoDescModelsTab;
    }

    public void findPrimaryCTAResource() {
        if (primaryCTAResource == null) {
            boolean foundExact = false;

            if (primaryctas != null && primaryctas.getName().equalsIgnoreCase(PRIMARY_CTAS)) {
                for (Resource ctaResource : primaryctas.getChildren()) {
                    final String[] ctaCountries = ctaResource.getValueMap().get(PRIMARY_CTA_COUNTRY_PROPERTY, EMPTY).split(",");

                    if (CommonUtil.countryMatches(currentCountry, ctaCountries) && !foundExact) {
                        if (CommonUtil.exactCountryMatch(currentCountry, ctaCountries)) {
                            foundExact = true;
                        }

                        primaryCTAResource = ctaResource;
                    }
                }
            }
        }
    }

    @PostConstruct
    protected void init() {
        // get resource resolver object from admin service
        try (ResourceResolver readServiceResourceResolver = adminService.getReadService()) {
            selectedExtnIdRes = StringUtils.isNotBlank(pdhRecordPath) ? readServiceResourceResolver.getResource(pdhRecordPath) : null;

            if (Objects.nonNull(selectedExtnIdRes)) {
                Resource imgPrimaryRes = readServiceResourceResolver
                        .resolve(selectedExtnIdRes.getParent().getPath().concat(CommonConstants.IMG_RENDITION));
                if (Objects.nonNull(imgPrimaryRes)) {
                    resolvedPrimaryImage = CommonUtil.getStringProperty(imgPrimaryRes.getValueMap(),
                            CommonConstants.PROPERTY_PDH_VALUE_CQ_DATA);
                    repfl = CommonUtil.getStringProperty(imgPrimaryRes.getValueMap(),
                            CommonConstants.PROPERTY_PDH_REPFL);
                }
                Resource spinimg = readServiceResourceResolver.resolve(selectedExtnIdRes.getParent().getPath().concat(CommonConstants.SPIN_IMG));
                if (Objects.nonNull(spinimg)) {
                    spinImage = CommonUtil.getStringProperty(spinimg.getValueMap(), CommonConstants.PROPERTY_PDH_VALUE_CQ_DATA);
                }

                productFamilyPDHDetails = CommonUtil.readPDHNodeData(readServiceResourceResolver, selectedExtnIdRes);
            }
            primaryCTAURL = CommonUtil.dotHtmlLink(primaryCTAURL);
        }
    }

    /**
     * @return the pdhPrimaryImg
     */
    public String getPdhPrimaryImg() {
        if (Objects.nonNull(productFamilyPDHDetails)) {
            return productFamilyPDHDetails.getPrimaryImgName();
        } else if (Objects.nonNull(resolvedPrimaryImage)) {
            return resolvedPrimaryImage;
        } else {
            return StringUtils.EMPTY;
        }
    }

    public String getSpinImage() {
        if (Objects.nonNull(productFamilyPDHDetails)) {
            return productFamilyPDHDetails.getPdhSpinImage();
        } else if (Objects.nonNull(spinImage)) {
            return spinImage;
        } else {
            return getPdhPrimaryImg();
        }
    }

    public HashMap<String, String> getTaxonomyAttributesLookup(final java.util.Optional<EndecaConfigModel> endecaConfigModel) {
        HashMap<String, String> unsortedMap = new HashMap<>();
        if (Objects.isNull(selectedExtnIdRes)) {
            return unsortedMap;
        }

        try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
            Resource prodFamilyMasterDetailsRes = adminResourceResolver.resolve(selectedExtnIdRes.getPath()).getParent();
            if (null != prodFamilyMasterDetailsRes) {
                for (Iterator<Resource> prodFamilyMasterDetailsItr = prodFamilyMasterDetailsRes.listChildren(); prodFamilyMasterDetailsItr.hasNext(); ) {
                    Resource extnIdResource = prodFamilyMasterDetailsItr.next();
                    for (Iterator<Resource> txnmyAttrResourceList = extnIdResource.listChildren(); txnmyAttrResourceList.hasNext(); ) {
                        Resource attrItem = txnmyAttrResourceList.next();
                        if (Objects.nonNull(attrItem)) {
                            // allocate memory to the Map instance
                            String optionText = CommonUtil.getStringProperty(attrItem.getValueMap(),
                                    PROPERTY_ATTRIBUTE_DISPLAY_DATA);
                            String optionValue = CommonUtil.getStringProperty(attrItem.getValueMap(),
                                    PROPERTY_ATTRIBUTE_LABEL);
                            if (StringUtils.isNotBlank(optionText) && StringUtils.isNotBlank(optionValue)) {
                                unsortedMap.put(optionText, optionValue);
                            } else {
                                optionText = StringUtils.isNotBlank(optionText) ? optionText : CommonUtil.getStringProperty(attrItem.getValueMap(), "LABEL");
                                final java.util.Optional<String> additionalTaxonomyAttributes = getAdditionalTaxonomyAttributes(endecaConfigModel, attrItem.getName());
                                if (additionalTaxonomyAttributes.isPresent()) {
                                    unsortedMap.put(optionText, additionalTaxonomyAttributes.get());
                                }
                            }
                        }
                    }
                }
            }
        }
        return unsortedMap;
    }

    /**
     * This returns the Resource form Product Family and SKU parent page PIM.
     *
     * @param currentPage
     * @return Resource to get the common data in get Method (Example:getPrimaryCTALabel())
     */
    private Resource getCommonPIMResource(final Page currentPage) {
        Resource commonPIMResource = null;
        try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
            Page productFamilyPagePath = null;
            Page currentProductFamilyPage = null;
            final Boolean currentPageAndDataFromSingleProductFamilyPage = currentPage != null &&
                    null != dataFromSingleProductFamilyPage && dataFromSingleProductFamilyPage.equals("true");
            final Boolean langMasterPageAndProductFamilyPage = currentPage != null && currentPage.getPath().contains(CommonConstants.LANGUAGE_MASTERS_NODE_NAME)
                    && null != productFamilyPage;

            if (currentPageAndDataFromSingleProductFamilyPage && langMasterPageAndProductFamilyPage) {
                currentProductFamilyPage = adminResourceResolver.resolve(productFamilyPage).adaptTo(Page.class);
                if (null != currentProductFamilyPage) {
                    productFamilyPagePath = currentProductFamilyPage.getParent();
                }

            } else if (currentPageAndDataFromSingleProductFamilyPage && null != productFamilyPage) {
                if (productFamilyPage.contains(CommonConstants.LANGUAGE_MASTERS_NODE_NAME) && null != currentCountry) {
                    productFamilyPage = productFamilyPage.replace(CommonConstants.LANGUAGE_MASTERS_NODE_NAME, currentCountry.toLowerCase());
                }
                currentProductFamilyPage = adminResourceResolver.resolve(productFamilyPage).adaptTo(Page.class);
                if (null != currentProductFamilyPage) {
                    productFamilyPagePath = currentProductFamilyPage.getParent();
                }

            }

            if (null != productFamilyPagePath && productFamilyPagePath.getProperties().containsKey(CommonConstants.PAGE_PIM_PATH)) {
                final String commonPIMPath = productFamilyPagePath.getProperties().get(CommonConstants.PAGE_PIM_PATH, StringUtils.EMPTY);
                commonPIMResource = adminResourceResolver.getResource(commonPIMPath);
            }
        }
        return commonPIMResource;
    }

    public String getPath() {
        return resource.getPath();
    }

    private static java.util.Optional<String> getAdditionalTaxonomyAttributes(java.util.Optional<EndecaConfigModel> endecaConfigModel, String taxonomyAttributeValue) {
        return (endecaConfigModel.isPresent() ? endecaConfigModel.get().getAdditionalTaxonomyAttributes() : new ArrayList<EndecaConfigModel.AdditionalTaxonomyAttribute>()).stream()
                .filter(mapping -> mapping.getTaxonomyAttributeName().equalsIgnoreCase(taxonomyAttributeValue))
                .map(mapping -> mapping.getTaxonomyAttributeName())
                .filter(Objects::nonNull).findFirst();
    }
}
