package com.eaton.platform.core.search.api;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.AttributeListDetail;
import com.eaton.platform.core.bean.attributetable.AttributeTable;
import com.eaton.platform.core.services.AdminService;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.List;
import java.util.Set;

/**
 * This is the mapping context for everything the Vendor implementation of SKU Search needs
 * @param <R> The record to use for conversion
 */
public interface SkuSearchRecordMappingContext<R extends VendorSkuSearchRecord> {
    /**
     * Constructs ths list of attribute groups
     * @param skuSearchRecord the record from the result of searching for a SKU
     * @return list of attribute groups with their attributes
     */
    List<AttributeListDetail> constructAttributeGroups(R skuSearchRecord);

    /**
     * Constructs the list of attribute groups of type table
     * @param skuSearchRecord the record from the result of searching for a SKU
     * @return list of attribute groups of type table
     */
    Set<AttributeTable> constructTableAttributes(R skuSearchRecord);

    AdminService getAdminService();

    SlingHttpServletRequest getRequest();

    Page getCurrentPage();

    String getCountry();
}
