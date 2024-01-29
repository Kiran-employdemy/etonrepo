package com.eaton.platform.integration.endeca.pojo.sku;

import com.eaton.platform.core.bean.AttributeListDetail;
import com.eaton.platform.core.bean.GlobalAttrGroupBean;
import com.eaton.platform.core.bean.TaxynomyAttributeGroupBean;
import com.eaton.platform.core.bean.attributetable.AttributeTable;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.search.api.SkuSearchRecordMappingContext;
import com.eaton.platform.core.search.api.VendorSkuSearchRecord;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.endeca.pojo.base.BaseEndecaDocument;
import com.eaton.platform.integration.endeca.pojo.base.EndecaField;
import com.eaton.platform.integration.endeca.pojo.base.EndecaFieldEnum;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class EndecaSkuDocument extends BaseEndecaDocument implements VendorSkuSearchRecord {

    private Set<AttributeTable> attributeTables;
    private Map<Long, SkuCIDContent> cidContentMap;
    @Override
    public String getInventoryId() {
        return getValueFor(EndecaFieldEnum.INVENTORY_ID);
    }
    @Override
    public String getMarketingDescription() {
        return getValueFor(EndecaFieldEnum.MKTG_DESC);
    }
    @Override
    public String getImages() {
        return getValueFor(EndecaFieldEnum.IMAGES);
    }
    @Override
    public String getDocuments() {
        return getValueFor(EndecaFieldEnum.DOCUMENTS);
    }

    @Override
    public String getUpSell() {
        return getValueFor(EndecaFieldEnum.UPSELL);
    }
    @Override
    public String getCrossSell() {
        return getValueFor(EndecaFieldEnum.CROSSSELL);
    }
    @Override
    public String getReplacement() {
        return getValueFor(EndecaFieldEnum.REPLACEMENT);
    }
    @Override
    public String getCatalogNumber() {
        return getValueFor(EndecaFieldEnum.CATALOG_NUMBER);
    }
    @Override
    public String getExtensionId() {
        return getValueFor(EndecaFieldEnum.EXTENSION_ID);
    }
    @Override
    public String getBrand() {
        return getValueFor(EndecaFieldEnum.BRAND);
    }

    @Override
    public String getStatus() {
        return getValueFor(EndecaFieldEnum.STATUS);
    }
    @Override
    public String getSubBrand() {
        return getValueFor(EndecaFieldEnum.SUBBRAND);
    }
    @Override
    public String getTradeName() {
        return getValueFor(EndecaFieldEnum.TRADE_NAME);
    }
    @Override
    public String getFamilyName() {
        return getValueFor(EndecaFieldEnum.FAMILY_NAME);
    }

    private EndecaXMLAttributeField getGlobalAttributes(AdminService adminService, String country, Map<Long, SkuCIDContent> cidContentMap) {
        EndecaField endecaField = getEndecaField(EndecaFieldEnum.GLOBAL_ATTRS);
        return new EndecaXMLAttributeField(adminService, endecaField, country, false, cidContentMap);
    }

    private EndecaXMLAttributeField getTaxonomyAttributes(AdminService adminService, String country, Map<Long, SkuCIDContent> cidContentMap) {
        EndecaField endecaField = getEndecaField(EndecaFieldEnum.TAXONOMY_ATTRS);
        EndecaXMLAttributeField endecaXMLAttributeField = new EndecaXMLAttributeField(adminService, endecaField, country, true, cidContentMap);
        attributeTables = endecaXMLAttributeField.getAttributeTables();
        return endecaXMLAttributeField;
    }

    private Map<Long, SkuCIDContent> getCIDContent(SkuSearchRecordMappingContext<? extends VendorSkuSearchRecord> mappingContext) {
        if (cidContentMap == null) {
            EndecaField endecaField = getEndecaField(EndecaFieldEnum.CID);
            cidContentMap = new EndecaXMLCidField(mappingContext.getAdminService(), endecaField, mappingContext.getCurrentPage().getLanguage(), mappingContext.getCountry()).getSkuCidDocContentMap();
        }
        return cidContentMap;
    }

    @Override
    public List<AttributeListDetail> getAllGlobalAttributes(String groupName, SkuSearchRecordMappingContext<? extends VendorSkuSearchRecord> mappingContext) {
        return getGlobalAttributes(mappingContext.getAdminService(), mappingContext.getCountry(), getCIDContent(mappingContext)).withGroupName(groupName);
    }

    @Override
    public List<AttributeListDetail> getFilteredGlobalAttributes(String globalAttributeGroupName, List<GlobalAttrGroupBean> globalAttributeList
            , SkuSearchRecordMappingContext<? extends VendorSkuSearchRecord> mappingContext) {
        List<String> groupNamesToRetain = new LinkedList<>();
        globalAttributeList.forEach(globalAttrGroupBean -> groupNamesToRetain.add(globalAttrGroupBean.getAttrValue()));
        return getGlobalAttributes(mappingContext.getAdminService(), mappingContext.getCountry(), getCIDContent(mappingContext)).withGroupNameAndOnlyFor(globalAttributeGroupName, groupNamesToRetain);
    }

    @Override
    public List<AttributeListDetail> getTaxonomyAttributesFromSku(SkuSearchRecordMappingContext<? extends VendorSkuSearchRecord> mappingContext) {
        return getTaxonomyAttributes(mappingContext.getAdminService()
                , mappingContext.getCountry(), getCIDContent(mappingContext)).withGroupName(CommonUtil.getI18NFromResourceBundle(mappingContext.getRequest()
                , Objects.requireNonNull(mappingContext.getCurrentPage())
                , CommonConstants.DEFAULT_TAXONOMY_ATTRIBUTE_LABEL));
    }

    @Override
    public List<AttributeListDetail> getTaxonomyAttributesForAGivenList(List<TaxynomyAttributeGroupBean> taxonomyAttributeGroupList
            , SkuSearchRecordMappingContext<? extends VendorSkuSearchRecord> mappingContext) {
        List<AttributeListDetail> attributeListDetails = new LinkedList<>();
        taxonomyAttributeGroupList.forEach(taxonomyAttributeGroupBean -> attributeListDetails.addAll(getTaxonomyAttributes(mappingContext.getAdminService()
                , mappingContext.getCountry(), cidContentMap).withGroupNameAndOnlyFor(taxonomyAttributeGroupBean.getGroupName(), taxonomyAttributeGroupBean.getAttributeList())));
        return attributeListDetails;
    }

    @Override
    public Set<AttributeTable> getTableAttributes() {
       return attributeTables;
    }

}
