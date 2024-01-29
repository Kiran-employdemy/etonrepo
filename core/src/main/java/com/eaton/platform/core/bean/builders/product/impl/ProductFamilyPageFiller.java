package com.eaton.platform.core.bean.builders.product.impl;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.ProductFamilyPIMDetails;
import com.eaton.platform.core.bean.builders.product.BeanFiller;
import com.eaton.platform.core.bean.builders.product.exception.MissingFillingBeanException;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.query.Query;
import java.util.Iterator;

public class ProductFamilyPageFiller extends BaseBeanFiller implements BeanFiller<ProductFamilyPIMDetails> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductFamilyPageFiller.class);


    private ResourceResolver adminResourceResolver;
    private SKUDetailsBean skuData;
    private String inventoryId;
    private String extensionId;


    public ProductFamilyPageFiller(Page currentPage,ResourceResolver adminResourceResolver, SKUDetailsBean skuData){
        this(currentPage, adminResourceResolver, skuData != null ? skuData.getInventoryId() : null, skuData != null ? skuData.getExtensionId() : null);
    }

    public ProductFamilyPageFiller(Page containingPage, ResourceResolver readResolver, String inventoryId, String extensionId) {
        super(containingPage);
        this.adminResourceResolver = readResolver;
        this.inventoryId = inventoryId;
        this.extensionId = extensionId;
    }


    @Override
    public void fill(final ProductFamilyPIMDetails bean) throws MissingFillingBeanException {
        if(null == bean){
            throw new MissingFillingBeanException("Filling bean cannot be null");
        }
        Resource pimResource = getPimResource(adminResourceResolver,extensionId, inventoryId,bean);
        if(null != pimResource) {
            String productFamilyPage = pimResource.getValueMap().get("productFamilyPage", StringUtils.EMPTY);
            if (StringUtils.isNotBlank(productFamilyPage)) {
                String familyPageName = StringUtils.substringAfterLast(productFamilyPage, "/");
                Page languagePage = currentPage.getAbsoluteParent(CommonConstants.COUNTRY_PAGE_DEPTH);
                if (languagePage != null && !languagePage.getPath().contains(CommonConstants.LANGUAGE_MASTERS_NODE_NAME)) {
                    String catalogPage = languagePage.getPath().concat("/catalog");
                    String sqlStatement = String.format("SELECT * FROM [cq:Page] AS S WHERE NAME() = \"%s\" AND ISDESCENDANTNODE(S,\"%s\")", familyPageName, catalogPage);
                    LOGGER.debug("sql statement :: {}", sqlStatement);
                    Iterator<Resource> result = adminResourceResolver.findResources(sqlStatement, Query.JCR_SQL2);
                    result.forEachRemaining(resource -> bean.setProductFamilyAEMPath(resource.getPath()));
                } else {
                    bean.setProductFamilyAEMPath(productFamilyPage);
                }
                Resource pfResource = adminResourceResolver.resolve(productFamilyPage);
                Page pfPage;
                if (!pfResource.isResourceType(Resource.RESOURCE_TYPE_NON_EXISTING) &&
                        null != (pfPage = pfResource.adaptTo(Page.class)) &&
                        null != pfPage.getContentResource()) {
                    String primarySubCategory = pfPage.getProperties().get(CommonConstants.PIM_PRIMARY_SUB_CATEGORY, StringUtils.EMPTY);
                    if (StringUtils.isNotBlank(primarySubCategory)) {
                        bean.setPrimarySubCategory(primarySubCategory);
                    }
                }
            }
        }
    }
}
