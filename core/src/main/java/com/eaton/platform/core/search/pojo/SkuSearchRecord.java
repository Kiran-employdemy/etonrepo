package com.eaton.platform.core.search.pojo;

import com.eaton.platform.core.bean.AttributeListDetail;
import com.eaton.platform.core.bean.attributetable.AttributeTable;
import com.eaton.platform.core.search.api.SkuSearchRecordMappingContext;
import com.eaton.platform.core.search.api.VendorSkuSearchRecord;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class SkuSearchRecord {

    private List<AttributeListDetail> attributeGroups = new LinkedList<>();

    private Set<AttributeTable> attributeTables = new LinkedHashSet<>();

    public static <R extends VendorSkuSearchRecord, M extends SkuSearchRecordMappingContext<R>> SkuSearchRecord of(R vendorSearchRecord
            , M mappingContext) {
        SkuSearchRecord skuSearchRecord = new SkuSearchRecord();
        skuSearchRecord.attributeGroups = mappingContext.constructAttributeGroups(vendorSearchRecord);
        skuSearchRecord.attributeTables = mappingContext.constructTableAttributes(vendorSearchRecord);
        return skuSearchRecord;
    }

    public List<AttributeListDetail> getAttributeGroups() {
        return attributeGroups;
    }

    public Set<AttributeTable> getAttributeTables() {
        return attributeTables;
    }
}
