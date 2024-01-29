package com.eaton.platform.core.bean.builders.product.impl;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.webservicesupport.ConfigurationManager;
import com.eaton.platform.core.bean.ProductFamilyPIMDetails;
import com.eaton.platform.core.bean.builders.product.BeanFiller;
import com.eaton.platform.core.bean.builders.product.exception.MissingFillingBeanException;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.PIMResourceSlingModel;
import com.eaton.platform.core.models.SiteResourceSlingModel;
import com.eaton.platform.core.services.EatonSiteConfigService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class SkuDataBeanFiller extends BaseBeanFiller implements BeanFiller<ProductFamilyPIMDetails> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SkuDataBeanFiller.class);

    private SKUDetailsBean skuData;
    private ResourceResolver adminResourceResolver;

    private EatonSiteConfigService eatonSiteConfigService;
    private String inventoryId;
    private String extensionId;

    public SkuDataBeanFiller(SKUDetailsBean skuData, Page currentPage, ResourceResolver resourceResolver,
                             EatonSiteConfigService eatonSiteConfigService){
        this(skuData != null ? skuData.getInventoryId() : null, skuData != null ? skuData.getExtensionId() : null, currentPage, resourceResolver, eatonSiteConfigService);
    }

    public SkuDataBeanFiller(String inventoryId, String extensionId,Page currentPage, ResourceResolver resourceResolver,
                             EatonSiteConfigService eatonSiteConfigService) {
        super(currentPage);
        this.extensionId = extensionId;
        this.inventoryId = inventoryId;
        this.eatonSiteConfigService = eatonSiteConfigService;
        this.adminResourceResolver = resourceResolver;
    }


    @Override
    public void fill(ProductFamilyPIMDetails bean) throws MissingFillingBeanException {
        if(null == bean){
            throw new MissingFillingBeanException("Filling bean cannot be null");
        }
        LOGGER.debug("SkuDataBeanFiller :: ProductFamilyPIMDetails :: attempting to fill Product Family Detail Bean");
        LOGGER.debug("SkuDataBeanFiller :: ProductFamilyPIMDetails :: current page path: {}",currentPage.getPath());
        Locale language = currentPage.getLanguage(false);
        String country = language.getCountry();
        Optional<SiteResourceSlingModel> siteConfig = eatonSiteConfigService.getSiteConfig(currentPage);
        SiteResourceSlingModel siteResourceSlingModel;
        boolean overridePDHData = false;
        if(siteConfig.isPresent()) {
            siteResourceSlingModel = siteConfig.get();
            overridePDHData = Boolean.parseBoolean(siteResourceSlingModel.getOverridePDHData());
        }
        Resource pimResource = getPimResource(adminResourceResolver, extensionId, inventoryId,bean);
        if(null != pimResource) {
            populateWithPimResource(bean, adminResourceResolver, country, overridePDHData, pimResource);
            LOGGER.debug("SkuDataBeanFiller :: Exit from fill() method");
        }
    }



    protected void populateWithPimResource(ProductFamilyPIMDetails productFamilyPIMDetails,
                                         ResourceResolver adminResourceResolver, String country,
                                         Boolean overridePDHData, Resource pimResource) {
        LOGGER.debug("SkuDataBeanFiller :: populateWithPimResource :: start to populate ProductFamilyPIMDetail with pim resource");
        String pdhRecordPath;
        Resource pdhRecordResource;
        PIMResourceSlingModel pimResourceSlingModel;
        final Optional<ConfigurationManager> configurationManagerOptional = Optional
                .ofNullable(adminResourceResolver.adaptTo(ConfigurationManager.class));
        Optional<Resource> siteConfigResource = eatonSiteConfigService.getSiteConfigResource(currentPage, configurationManagerOptional);
        if (pimResource != null && null != (pimResourceSlingModel = pimResource.adaptTo(PIMResourceSlingModel.class))) {
            LOGGER.debug("SkuDataBeanFiller :: populateWithPimResource :: pim resource not null pim resource sling mode not null");
            pimResourceSlingModel.setCurrentCountry(CommonUtil.getCountryFromPagePath(currentPage));
            productFamilyPIMDetails.setPrimaryCTALabel(pimResourceSlingModel.getPrimaryCTALabel(currentPage));
            productFamilyPIMDetails.setPrimaryCTAURL(pimResourceSlingModel.getPrimaryCTAURL(currentPage));
            productFamilyPIMDetails.setPrimaryCTANewWindow(pimResourceSlingModel.getPrimaryCTANewWindow(currentPage));
            productFamilyPIMDetails.setPrimaryCTAEnableSourceTracking(pimResourceSlingModel.getPrimaryCTAEnableSourceTracking(currentPage));
            productFamilyPIMDetails.setIsSuffixDisabled(pimResourceSlingModel.getIsSuffixDisabled());
            productFamilyPIMDetails.setMiddleTabTitle(pimResourceSlingModel.getMiddleTabTitle());
            pdhRecordPath = pimResourceSlingModel.getPdhRecordPath();
            pdhRecordResource = adminResourceResolver.getResource(pdhRecordPath);
            if(StringUtils.isNotBlank(pimResourceSlingModel.getProductName())){
                productFamilyPIMDetails.setProductName(pimResourceSlingModel.getProductName());
            } else if(StringUtils.isNotBlank(pimResourceSlingModel.getPdhProdName())){
                productFamilyPIMDetails.setProductName(pimResourceSlingModel.getPdhProdName());
            }
            boolean showLongDescription = pimResourceSlingModel.getShowLongDesc() != null && CommonConstants.TRUE.equals(pimResourceSlingModel.getShowLongDesc());
            productFamilyPIMDetails.setShowLongDescription( showLongDescription);
            populateBuyOptions(productFamilyPIMDetails, adminResourceResolver, pimResourceSlingModel, siteConfigResource);

            // Set tags configured on PIM page into product family details.
            populatePimTags(productFamilyPIMDetails, adminResourceResolver, pimResourceSlingModel);
            if (null != pimResourceSlingModel.getPrimaryProductTaxonomy()) {
                String primaryProductTaxonomy = pimResourceSlingModel.getPrimaryProductTaxonomy();
                productFamilyPIMDetails.setPrimaryProductTaxonomy(primaryProductTaxonomy);
            }
            populateSecondaryLink(productFamilyPIMDetails, country, pimResourceSlingModel);
            populateTopicLink(productFamilyPIMDetails, country, pimResourceSlingModel);
            populateSupportInfo(productFamilyPIMDetails, country, overridePDHData, pdhRecordResource, pimResourceSlingModel);
        }
        LOGGER.debug("SkuDataBeanFiller :: populateWithPimResource :: end to populate ProductFamilyPIMDetail with pim resource");

    }

    protected void populatePimTags(ProductFamilyPIMDetails productFamilyPIMDetails, ResourceResolver adminResourceResolver, PIMResourceSlingModel pimResourceSlingModel) {
        LOGGER.debug("SkuDataBeanFiller :: populatePimTags :: start to populate pim tags");
        String[] pimTags = pimResourceSlingModel.getTags();
        LOGGER.debug("SkuDataBeanFiller :: populatePimTags :: pim tags: {}",pimTags);
        TagManager tagManager = adminResourceResolver.adaptTo(TagManager.class);
        if (null != pimTags && pimTags.length > 0 && null != tagManager) {
            List<String> tagList = new ArrayList<>();
            for (String tagItem : pimTags) {
                Tag pimTag = tagManager.resolve(tagItem);
                if(null != pimTag) {
                    tagList.add(pimTag.getPath());
                }
            }
            productFamilyPIMDetails.setPimTags(tagList);
        }
        LOGGER.debug("SkuDataBeanFiller :: populatePimTags :: end to populate pim tags");
    }


}
