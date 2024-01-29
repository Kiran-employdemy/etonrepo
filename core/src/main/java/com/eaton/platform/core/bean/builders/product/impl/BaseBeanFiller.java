package com.eaton.platform.core.bean.builders.product.impl;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.ProductFamilyPIMDetails;
import com.eaton.platform.core.bean.ProductSupportBean;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.HowToBuyOptionsModel;
import com.eaton.platform.core.models.PIMResourceSlingModel;
import com.eaton.platform.core.models.SecondaryLinksModel;
import com.eaton.platform.core.models.SupportInfoModel;
import com.eaton.platform.core.models.TopicLinksModel;
import com.eaton.platform.core.util.CommonUtil;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.query.Query;
import java.util.Iterator;
import java.util.Locale;
import java.util.Optional;

public class BaseBeanFiller {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseBeanFiller.class);

    public static final String HTB_TITLE_LIST = "howtoBuyTitleList";
    private static final String INVENTORY_ID = "inventoryID";


    protected Page currentPage;

    public BaseBeanFiller(Page currentPage) {
        this.currentPage = currentPage;
    }

    protected void populateSecondaryLink(ProductFamilyPIMDetails productFamilyPIMDetails, String country, PIMResourceSlingModel pimResourceSlingModel) {
        SecondaryLinksModel secondaryLinks = pimResourceSlingModel.getSecondaryLinks(currentPage);
        if (null != secondaryLinks) {
            productFamilyPIMDetails.setSecondaryLinksList(secondaryLinks.forCountry(country));
        }
    }

    protected void populateTopicLink(ProductFamilyPIMDetails productFamilyPIMDetails, String country, PIMResourceSlingModel pimResourceSlingModel) {
        TopicLinksModel topicLinks = pimResourceSlingModel.getResourceSection(currentPage);
        if (topicLinks != null) {
            productFamilyPIMDetails.setTopicLinkWithIconList(topicLinks.forCountry(country));
        }
    }

    protected void populateSupportInfo(ProductFamilyPIMDetails productFamilyPIMDetails, String country, Boolean overridePDHData, Resource pdhRecordResource, PIMResourceSlingModel pimResourceSlingModel) {
        SupportInfoModel supportInfo = pimResourceSlingModel.getSupportInfo(currentPage);
        if (supportInfo != null) {
            ProductSupportBean supportInfoBean = supportInfo.forCountry(pdhRecordResource, overridePDHData, country);
            if (supportInfoBean != null) {
                productFamilyPIMDetails.setProductSupportBean(supportInfoBean);
            }
        }
    }

    protected void populateBuyOptions(ProductFamilyPIMDetails productFamilyPIMDetails, ResourceResolver adminResourceResolver, PIMResourceSlingModel pimResourceSlingModel, Optional<Resource> siteConfigResource) {
        HowToBuyOptionsModel howToBuyOptions = pimResourceSlingModel.getHowToBuy(currentPage);
        if (howToBuyOptions != null && siteConfigResource.isPresent()) {
            final String[] howTobuySiteConfig = siteConfigResource.get().
                    getValueMap().get(HTB_TITLE_LIST, String[].class);
            String primaryProductTaxonomy = pimResourceSlingModel.getPrimaryProductTaxonomy();
            productFamilyPIMDetails.setHowToBuyList(howToBuyOptions.forCountry(CommonUtil.getCountryFromPagePath(currentPage), howTobuySiteConfig, currentPage, adminResourceResolver, primaryProductTaxonomy));
        }
    }

    protected Resource getPimResource(ResourceResolver adminResourceResolver, String extensionId, String inventoryId, ProductFamilyPIMDetails bean) {
        // shouldn't run retrieve pim node query multiple times
        if (null != bean.getPimResource()) {
            return bean.getPimResource();
        }
        LOGGER.debug("BaseBeanFiller :: getPimResource :: start to retrieve pim resource");
        Locale language = currentPage.getLanguage(false);
        String currentLanguage = language.getLanguage();
        String country = language.getCountry();
        Resource pimResource = null;
        if (!Strings.isNullOrEmpty(extensionId) && !Strings.isNullOrEmpty(currentLanguage)) {
            String pimBasePath = new StringBuilder().append(CommonConstants.PIM_PATH)
                    .append(currentLanguage).append("_")
                    .append(country.toLowerCase(Locale.getDefault()))
                    .toString();
            String pimPath = new StringBuilder(pimBasePath).append("/_").append(extensionId).toString();
            pimResource = adminResourceResolver.getResource(pimPath);
            if (pimResource == null && StringUtils.isNotEmpty(inventoryId)) {
                String statement = String.format("SELECT * FROM [%s] AS n WHERE n.[%s] = \"%s\" AND ISDESCENDANTNODE(n,\"%s\")", JcrConstants.NT_BASE, INVENTORY_ID, inventoryId, pimBasePath);
                LOGGER.debug("BaseBeanFiller :: getPimResource :: SQL2 statement {}", statement);
                Iterator<Resource> result = adminResourceResolver.findResources(statement, Query.JCR_SQL2);
                if (result.hasNext()) {
                    pimResource = result.next();
                    LOGGER.debug("Found Pim resource: {}", pimResource.getPath());
                } else {
                    LOGGER.error("There is no pim with inventoryId ::{}", inventoryId);
                }
            }
        }
        LOGGER.debug("BaseBeanFiller :: getPimResource :: end to retrieve pim resource");
        bean.setPimResource(pimResource);
        return pimResource;
    }
}
