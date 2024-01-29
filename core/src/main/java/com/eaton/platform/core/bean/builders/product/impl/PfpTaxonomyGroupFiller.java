package com.eaton.platform.core.bean.builders.product.impl;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.ProductFamilyPIMDetails;
import com.eaton.platform.core.bean.TaxynomyAttributeGroupBean;
import com.eaton.platform.core.bean.builders.product.BeanFiller;
import com.eaton.platform.core.bean.builders.product.exception.MissingFillingBeanException;
import com.eaton.platform.core.models.pim.AttributeGroup;
import com.eaton.platform.core.models.pim.AttributeList;
import com.eaton.platform.core.models.pim.TaxonomyAttributes;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PfpTaxonomyGroupFiller extends BaseBeanFiller implements BeanFiller<ProductFamilyPIMDetails> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PfpTaxonomyGroupFiller.class);

    private ResourceResolver adminResourceResolver;
    private SKUDetailsBean skuData;
    private String extensionId;
    private String inventoryId;


    public PfpTaxonomyGroupFiller(Page currentPage,ResourceResolver adminResourceResolver, SKUDetailsBean skuData){
        this(currentPage, adminResourceResolver, skuData.getInventoryId(), skuData.getExtensionId());
    }

    public PfpTaxonomyGroupFiller(Page containingPage, ResourceResolver readResolver, String inventoryId, String extensionId) {
        super(containingPage);
        this.adminResourceResolver = readResolver;
        this.inventoryId = inventoryId;
        this.extensionId = extensionId;
    }

    @Override
    public void fill(ProductFamilyPIMDetails bean) throws MissingFillingBeanException {
        if(null == bean){
            throw new MissingFillingBeanException("Filling bean cannot be null");
        }
        LOGGER.debug("PfpTaxonomyGroupFiller :: populateTaxonomyAttrGroups :: start to populate taxonomy attribute groups");
        String productFamilyAEMPath = bean.getProductFamilyAEMPath();
        LOGGER.debug("PfpTaxonomyGroupFiller :: populateTaxonomyAttrGroups :: product family aem path: {}",productFamilyAEMPath);
        if (StringUtils.isNotBlank(productFamilyAEMPath) ) {
            Resource familyPageResource = adminResourceResolver.getResource(productFamilyAEMPath);
            Resource pimResource = getPimResource(adminResourceResolver,extensionId, inventoryId,bean);
            if (null != familyPageResource && null != familyPageResource.getChild(JcrConstants.JCR_CONTENT) && pimResource != null) {
                generateAttributeGroupList(bean, pimResource);
            }
        }
        LOGGER.debug("PfpTaxonomyGroupFiller :: populateTaxonomyAttrGroups :: end to populate taxonomy attribute groups");
    }

    private void generateAttributeGroupList(ProductFamilyPIMDetails bean, Resource pimResource) {
        List<TaxynomyAttributeGroupBean> taxonomyAttributeGroupList = new ArrayList<>();

        Resource taxonomyAttributeResource = pimResource.getChild("taxonomyAttributes");
        if(null != taxonomyAttributeResource) {
            TaxonomyAttributes taxonomyAttributes = taxonomyAttributeResource.adaptTo(TaxonomyAttributes.class);
            for (AttributeGroup attrGroup : taxonomyAttributes.getAttributeGroups()) {
                String attrGroupName = attrGroup.getGroupName();
                AttributeList attributeList = attrGroup.getAttributeList();
                List<String> attributes = attributeList != null ? attributeList.getAttributes() : Collections.emptyList();
                if (StringUtils.isNotBlank(attrGroupName) && !attributes.isEmpty()) {
                    TaxynomyAttributeGroupBean taGroupBean = new TaxynomyAttributeGroupBean();
                    taGroupBean.setGroupName(attrGroupName);
                    taGroupBean.setAttributeList(attributes);
                    taxonomyAttributeGroupList.add(taGroupBean);
                }
            }
            if (!taxonomyAttributeGroupList.isEmpty()) {
                bean.setTaxonomyAttributeGroupList(taxonomyAttributeGroupList);
            }
        }
    }
}
