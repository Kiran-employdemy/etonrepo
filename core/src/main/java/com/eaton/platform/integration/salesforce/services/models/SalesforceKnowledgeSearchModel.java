package com.eaton.platform.integration.salesforce.services.models;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.ProductFamilyDetails;
import com.eaton.platform.core.bean.ProductFamilyPIMDetails;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.EndecaRequestService;
import com.eaton.platform.core.services.ProductFamilyDetailService;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsBean;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsResponseBean;
import com.eaton.platform.integration.endeca.services.EndecaService;
import com.eaton.platform.integration.salesforce.services.SalesforceKnowledgeSearchService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Salesforce Knowledge Search Model
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SalesforceKnowledgeSearchModel {

    private static final Logger LOG = LoggerFactory.getLogger(SalesforceKnowledgeSearchModel.class);
    private static final String TAG_PREFIX = StringUtils.join(CommonConstants.STARTS_WITH_CONTENT, CommonConstants.SLASH_STRING, CommonConstants.CQ_TAGS, CommonConstants.SLASH_STRING);
    private String pageType;
    private String pagePath;

    private SKUDetailsBean skuDetailsBean;

    @OSGiService
    private SalesforceKnowledgeSearchService salesforceKnowledgeSearchService;

    @OSGiService
    private ProductFamilyDetails productFamilyDetails;
    @OSGiService
    private ProductFamilyPIMDetails productFamilyPIMDetails;
    @Inject
    protected ProductFamilyDetailService productFamilyDetailService;

    @Inject
    private Page currentPage;
    @Inject @Source("sling-object")
    private SlingHttpServletRequest slingHttpServletRequest;

    @Inject
    protected EndecaService endecaService;
    @Inject
    protected EndecaRequestService endecaRequestService;

    @PostConstruct
    protected void init() {

        LOG.debug("init: Start");

        if ( !salesforceKnowledgeSearchService.isGlobalEnabled() ) {

            LOG.debug("Salesforce knowledge search is disabled globally in the salesforce configuration");

        } else {

            LOG.debug("Salesforce knowledge search is enabled globally in the salesforce configuration");

            pagePath = currentPage.getPath();
            LOG.debug("pagePath: {}", pagePath);
            if (pagePath.isEmpty()) {
                LOG.error("Page path not found");
            }

            pageType = currentPage.getProperties().get(CommonConstants.PAGE_TYPE).toString();
            LOG.debug("pageType: {}", pageType);

            if ( !StringUtils.isNotBlank(pageType) ) {

                LOG.error("Page type not found for {}", pagePath);

            } else {

                if (pageType.equals(CommonConstants.PAGE_TYPE_PRODUCT_FAMILY_PAGE)) {

                    productFamilyDetails = productFamilyDetailService.getProductFamilyDetailsBean(currentPage);

                } else if (pageType.equals(CommonConstants.PAGE_TYPE_PRODUCT_SKU_PAGE)) {

                    final String[] selectors = slingHttpServletRequest.getRequestPathInfo().getSelectors();
                    final EndecaServiceRequestBean endecaServiceRequestBean = endecaRequestService.getEndecaRequestBean(currentPage, selectors, StringUtils.EMPTY);
                    final SKUDetailsResponseBean skuDetails = endecaService.getSKUDetails(endecaServiceRequestBean);
                    skuDetailsBean = skuDetails.getSkuResponse().getSkuDetails();
                    productFamilyPIMDetails = productFamilyDetailService.getProductFamilyPIMDetailsBean(skuDetailsBean, currentPage);

                } else {
                    LOG.debug("Page is not product family or product sku page so no product details were set for {}. Will try to match targetTags with cq:tags from page properties", pagePath);
                }

            }

        }

        LOG.debug("init: End");
    }


    /**
     * Get the knowledge search url
     * with search term when on product family or product sku page (search term being product family)
     * when on page other than product family or product sku, link to site homepage is returned
     *
     * @return knowledge search url
     */
    public String getKnowledgeSearchUrl() {
        LOG.debug("getKnowledgeSearchUrl: Start");

        String knowledgeSearchUrl = StringUtils.EMPTY;


        if ( !salesforceKnowledgeSearchService.isGlobalEnabled() ) {

            LOG.debug("Salesforce knowledge search is disabled globally in the salesforce configuration");

        } else {

            knowledgeSearchUrl = StringUtils.join(salesforceKnowledgeSearchService.getSalesforceSiteUrl());
            String searchTerm = getKnowledgeSearchTerm();

            if (StringUtils.isNotBlank(searchTerm)) {

                String encodedSearchTerm = URLEncoder.encode(searchTerm, StandardCharsets.UTF_8);

                if (StringUtils.isNotBlank(encodedSearchTerm)) {
                    knowledgeSearchUrl = StringUtils.join(knowledgeSearchUrl, salesforceKnowledgeSearchService.getSalesforceSearchPath(), CommonConstants.SLASH_STRING, encodedSearchTerm);
                }

            }

        }

        LOG.debug("getKnowledgeSearchUrl: End");
        return knowledgeSearchUrl;

    }

    /**
     * Get the knowledge search term
     * when on product family or product sku page, the product family name is encoded and returned
     * when on page other than product family or product sku, an empty search term is returned
     * @return knowledge search term for current page
     */
    private String getKnowledgeSearchTerm() {
        LOG.debug("getKnowledgeSearchTerm: Start");

        String searchTerm = StringUtils.EMPTY;

        if ( StringUtils.isNotBlank(pageType) ) {

            if (pageType.equals(CommonConstants.PAGE_TYPE_PRODUCT_FAMILY_PAGE)
                    && productFamilyDetails != null
                    && StringUtils.isNotBlank(productFamilyDetails.getProductFamilyName())) {

                LOG.debug("product family name found: {} for {}", productFamilyDetails.getProductFamilyName(), pagePath);
                searchTerm = productFamilyDetails.getProductFamilyName();

            } else if (pageType.equals(CommonConstants.PAGE_TYPE_PRODUCT_SKU_PAGE)
                    && skuDetailsBean != null
                    && StringUtils.isNotBlank(skuDetailsBean.getFamilyName())) {

                LOG.debug("product family name found: {} for {}", skuDetailsBean.getFamilyName(), pagePath);
                searchTerm = skuDetailsBean.getFamilyName();

            } else {
                LOG.debug("page is not a product family or product sku page so no knowledge search term set for {}", pagePath);
            }

        }

        LOG.debug("getKnowledgeSearchTerm: End");
        return searchTerm;
    }


    /**
     * Check if any of the target tags (from salesforce configuration) are found in the source tags (from page/pim)
     * @return true if a target tag is found in the source tags, false if a target tag is not found in the source tags
     */
    public boolean isLocalEnabled() {

        if ( !salesforceKnowledgeSearchService.isGlobalEnabled() ) {

            LOG.debug("Salesforce knowledge search is disabled globally in the salesforce configuration");

        } else {

            LOG.debug("isLocalEnabled: Start");
            List<String> sourceTags = getSourceTags();
            List<String> targetTags = salesforceKnowledgeSearchService.getTargetTags();

            if (sourceTags.isEmpty()) {

                LOG.warn("no target tags ({}) were found in source tags ({})", targetTags, sourceTags);

            } else {

                for (String targetTag : targetTags) {
                    String targetTagWithPrefix;

                    if (pageType.equals(CommonConstants.PAGE_TYPE_PRODUCT_SKU_PAGE) || pageType.equals(CommonConstants.PAGE_TYPE_PRODUCT_FAMILY_PAGE)) {
                        targetTagWithPrefix = StringUtils.join(TAG_PREFIX, targetTag);
                    } else {
                        targetTagWithPrefix = StringUtils.replaceFirst(targetTag, CommonConstants.SLASH_STRING, CommonConstants.COLON);
                    }

                    if (StringUtils.isNotBlank(targetTagWithPrefix) && sourceTags.contains(targetTagWithPrefix)) {
                        LOG.debug("target tag ({}) found in source tags ({})", targetTagWithPrefix, sourceTags);
                        LOG.debug("isLocalEnabled : End");
                        return true;
                    } else {
                        LOG.debug("target tag ({}) not found in source tags ({})", targetTagWithPrefix, sourceTags);
                    }
                }

            }


        }
        LOG.debug("no target tags match the source tags so salesforce knowledge search is disabled.");
        LOG.debug("isLocalEnabled: End");
        return false;

    }

    /**
     * Get the (source) tags that will be compared to the (target) tags found in the salesforce configuration
     * When pf or sku page, tags come from pim
     * When non-pf or sku page, tags come from page properties cq:tags
     * @return list of source tags
     */
    private List<String> getSourceTags() {

        LOG.debug("getSourceTags: Start");
        List<String> sourceTags = new ArrayList<>();

        if ( StringUtils.isNotBlank(pageType) && pageType.equals(CommonConstants.PAGE_TYPE_PRODUCT_FAMILY_PAGE) ) {

            if ( productFamilyDetails != null &&
                    ( productFamilyDetails.getPimTags() != null && ( !productFamilyDetails.getPimTags().isEmpty() ) ) ) {
                sourceTags = productFamilyDetails.getPimTags();
            } else {
                LOG.warn("product family page pim tags not found for {}", currentPage.getPath());
            }

        } else if ( StringUtils.isNotBlank(pageType) && pageType.equals(CommonConstants.PAGE_TYPE_PRODUCT_SKU_PAGE) ) {


            if ( productFamilyPIMDetails != null &&
                    ( productFamilyPIMDetails.getPimTags() != null && ( !productFamilyPIMDetails.getPimTags().isEmpty() ) ) ) {
                sourceTags = productFamilyPIMDetails.getPimTags();
            } else {
                LOG.warn("product sku page pim tags not found for {}", currentPage.getPath());
            }

        } else {

            String[] pageTags = currentPage.getProperties().get(CommonConstants.CQ_TAGS, String[].class);
            if (pageTags != null) {
                sourceTags = Arrays.asList(pageTags);
            } else {
                LOG.warn("page tags not found for {}", currentPage.getPath());
            }

        }

        if (sourceTags.isEmpty()) {
            LOG.warn("cannot get source tags for knowledge search at {}", currentPage.getPath());
        }

        LOG.debug("sourceTags = {}", sourceTags);
        LOG.debug("getSourceTags: End");
        return sourceTags;

    }

    public String getImagePath() {
        return salesforceKnowledgeSearchService.getImagePath();
    }

}
