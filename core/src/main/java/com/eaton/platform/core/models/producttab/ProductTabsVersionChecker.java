package com.eaton.platform.core.models.producttab;

import com.day.cq.wcm.api.Page;
import org.apache.sling.api.resource.Resource;

/**
 * Checker to check if version 2 of product tabs is used
 */
public class ProductTabsVersionChecker {

    public static final String PATH_TO_COMPONENT = "root/responsivegrid/product_tabs";
    public static final String PRODUCT_TABS_V2_RESOURCE_TYPE = "eaton/components/product/product-tabs-v2";

    /**
     * Checks.... wait for it... if the product tabs version is v2
     * @param pageToCheck do I really have to state the obvious ?
     * @return true if v2
     */
    public boolean isVersion2(Page pageToCheck) {
        Resource productTabs = pageToCheck.getContentResource().getChild(PATH_TO_COMPONENT);
        if (productTabs == null) {
            return false;
        }
        return productTabs.isResourceType(PRODUCT_TABS_V2_RESOURCE_TYPE);
    }
}
