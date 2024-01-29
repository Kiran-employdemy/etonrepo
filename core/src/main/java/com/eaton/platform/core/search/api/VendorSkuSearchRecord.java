package com.eaton.platform.core.search.api;

import com.eaton.platform.core.bean.AttributeListDetail;
import com.eaton.platform.core.bean.attributetable.AttributeTable;
import com.eaton.platform.core.bean.GlobalAttrGroupBean;
import com.eaton.platform.core.bean.TaxynomyAttributeGroupBean;

import java.util.List;
import java.util.Set;

/**
 * The interface for the Vendor implementation of a record from the SKU Search result
 */
public interface VendorSkuSearchRecord extends VendorSearchRecord {
    String getInventoryId();

    String getMarketingDescription();

    String getImages();

    String getDocuments();

    String getUpSell();

    String getCrossSell();

    String getReplacement();

    String getCatalogNumber();

    String getExtensionId();

    String getBrand();

    String getStatus();

    String getSubBrand();

    String getTradeName();

    String getFamilyName();

    /**
     * Returns a list of all global Attributes groups with their attributes
     * @param globalAttributeGroupName to specify the name used for naming the global attributes
     * @param mappingContext context that has all what is needed for the vendor implementation to use
     * @return the list of all global attributes groups
     */
    List<AttributeListDetail> getAllGlobalAttributes(String globalAttributeGroupName, SkuSearchRecordMappingContext<? extends VendorSkuSearchRecord> mappingContext);

    /**
     * Returns a list of global attributes for the given list of Global Attributes.
     * Specifying this list means that the attributes in the attribute groups will be in order of appearance of this list, and the result will only contain the attributes of that list
     * @param globalAttributeGroupName to specify the name used for naming the global attributes
     * @param globalAttributeList limits the list of attributes and specifies the order
     * @param mappingContext context that has all what is needed for the vendor implementation to use
     * @return the list of filtered and ordered global attributes groups
     */
    List<AttributeListDetail> getFilteredGlobalAttributes(String globalAttributeGroupName, List<GlobalAttrGroupBean> globalAttributeList
            , SkuSearchRecordMappingContext<? extends VendorSkuSearchRecord> mappingContext);

    /**
     * Returns a list of all taxonomy attributes in the order of appearance it comes from the result of the SKU search of the vendor
     * @param mappingContext context that has all what is needed for the vendor implementation to use
     * @return the list of all taxonomy attributes
     */
    List<AttributeListDetail> getTaxonomyAttributesFromSku(SkuSearchRecordMappingContext<? extends VendorSkuSearchRecord> mappingContext);

    /**
     * Returns a list of taxonomy attributes for the given list of taxonomy attributes.
     * Specifying this list means that the attributes in the attribute groups will be in order of appearance of this list, and the result will only contain the attributes of that list
     * @param taxonomyAttributeGroupList limits the list of attributes and specifies the order
     * @param mappingContext context that has all what is needed for the vendor implementation to use
     * @return the list of filtered and ordered taxonomy attribute groups
     */
    List<AttributeListDetail> getTaxonomyAttributesForAGivenList(List<TaxynomyAttributeGroupBean> taxonomyAttributeGroupList
            , SkuSearchRecordMappingContext<? extends VendorSkuSearchRecord> mappingContext);

    Set<AttributeTable> getTableAttributes();
}
