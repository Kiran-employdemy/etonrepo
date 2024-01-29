package com.eaton.platform.core.search.pojo;

import com.eaton.platform.core.bean.AttributeListDetail;
import com.eaton.platform.core.bean.attributetable.AttributeTable;
import com.eaton.platform.core.search.api.SkuSearchRecordMappingContext;
import com.eaton.platform.core.search.api.VendorSkuResponse;
import com.eaton.platform.core.search.api.VendorSkuSearchRecord;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SkuSearchResponse {
    private Set<SkuSearchRecord> skuSearchRecords;

    public static <R extends VendorSkuSearchRecord, V extends VendorSkuResponse<R>, M extends SkuSearchRecordMappingContext<R>> SkuSearchResponse of(V vendorResponse
            , M mappingContext) {
        SkuSearchResponse skuSearchResponse = new SkuSearchResponse();
        skuSearchResponse.skuSearchRecords = new LinkedHashSet<>();
        vendorResponse.getDocuments().forEach((R vendorSearchRecord) ->
                skuSearchResponse.skuSearchRecords.add(SkuSearchRecord.of(vendorSearchRecord, mappingContext)));
        return skuSearchResponse;
    }

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }

    public List<AttributeListDetail> getAttributeGroups() {
        if (skuSearchRecords.isEmpty()) {
            return new ArrayList<>();
        }
        return skuSearchRecords.stream().findFirst().orElse(new SkuSearchRecord()).getAttributeGroups();
    }

    public Set<AttributeTable> getAttributeTableList() {
        if (skuSearchRecords.isEmpty()) {
            return new LinkedHashSet<>();
        }
        return skuSearchRecords.stream().findFirst().orElse(new SkuSearchRecord()).getAttributeTables();
    }
}
