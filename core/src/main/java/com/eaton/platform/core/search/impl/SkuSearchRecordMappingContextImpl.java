package com.eaton.platform.core.search.impl;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.AttributeListDetail;
import com.eaton.platform.core.bean.attributetable.AttributeTable;
import com.eaton.platform.core.bean.GlobalAttrGroupBean;
import com.eaton.platform.core.bean.ProductFamilyPIMDetails;
import com.eaton.platform.core.bean.TaxynomyAttributeGroupBean;
import com.eaton.platform.core.models.SiteResourceSlingModel;
import com.eaton.platform.core.search.api.SkuSearchRecordMappingContext;
import com.eaton.platform.core.search.api.VendorSkuSearchRecord;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.ProductFamilyDetailService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static com.eaton.platform.core.constants.CommonConstants.CONTENT_ROOT_FOLDER;

public class SkuSearchRecordMappingContextImpl<T extends VendorSkuSearchRecord> implements SkuSearchRecordMappingContext<T> {
    private final AdminService adminService;
    private final SiteResourceSlingModel siteResourceSlingModel;
    private final ProductFamilyDetailService productFamilyDetailService;
    private final Page currentPage;
    private final SlingHttpServletRequest request;

    public SkuSearchRecordMappingContextImpl(AdminService adminService, SiteResourceSlingModel siteResourceSlingModel, ProductFamilyDetailService productFamilyDetailService
            , Page currentPage, SlingHttpServletRequest request) {
        this.adminService = adminService;
        this.siteResourceSlingModel = siteResourceSlingModel;
        this.productFamilyDetailService = productFamilyDetailService;
        this.currentPage = currentPage;
        this.request = request;
    }

    @Override
    public List<AttributeListDetail> constructAttributeGroups(T skuSearchRecord) {
        List<AttributeListDetail> attributeListDetails = new LinkedList<>(constructGlobalAttributes(skuSearchRecord));
        ProductFamilyPIMDetails productFamilyPIMDetails = productFamilyDetailService.getProductFamilyPIMDetailsBean(skuSearchRecord.getExtensionId(), skuSearchRecord.getInventoryId(), currentPage);
        attributeListDetails.addAll(constructTaxonomyAttributes(skuSearchRecord, productFamilyPIMDetails));
        return attributeListDetails;
    }

    @Override
    public Set<AttributeTable> constructTableAttributes(T skuSearchRecord) {
        return skuSearchRecord.getTableAttributes();
    }

    private List<AttributeListDetail> constructGlobalAttributes(T skuData) {
        if (siteResourceSlingModel == null) {
            return new ArrayList<>();
        }
        String groupName = siteResourceSlingModel.getGlobalAttributeGroupName();
        List<GlobalAttrGroupBean> globalAttributeList = siteResourceSlingModel.getGlobalAttributeList();
        if (CollectionUtils.isEmpty(globalAttributeList)) {
            return skuData.getAllGlobalAttributes(groupName, this);
        }
        return skuData.getFilteredGlobalAttributes(siteResourceSlingModel.getGlobalAttributeGroupName(), globalAttributeList, this);
    }

    private List<AttributeListDetail> constructTaxonomyAttributes(VendorSkuSearchRecord skuData, ProductFamilyPIMDetails productFamilyPIMDetails) {
        if ((productFamilyPIMDetails.getTaxonomyAttributeGroupList() != null) && !(productFamilyPIMDetails.getTaxonomyAttributeGroupList().isEmpty())) {
            List<TaxynomyAttributeGroupBean> taxynomyAttributeGroupList = productFamilyPIMDetails.getTaxonomyAttributeGroupList();
            return skuData.getTaxonomyAttributesForAGivenList(taxynomyAttributeGroupList, this);
        }
            return skuData.getTaxonomyAttributesFromSku(this);
    }

    public AdminService getAdminService() {
        return adminService;
    }

    public Page getCurrentPage() {
        return currentPage;
    }

    @Override
    public String getCountry() {
        if (currentPage == null) {
            return Locale.getDefault().getCountry();
        }
        Locale locale = currentPage.getLanguage();
        String path = currentPage.getPath();
        if (path.toLowerCase(Locale.getDefault()).contains("/" + locale.getCountry().toLowerCase(Locale.getDefault()) + "/")) {
            return locale.getCountry();
        }
        if (path.contains(CONTENT_ROOT_FOLDER)) {
            path = path.substring(CONTENT_ROOT_FOLDER.length());
        }
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return path.substring(0, path.indexOf('/')).toUpperCase(Locale.getDefault());
    }

    public SlingHttpServletRequest getRequest() {
        return request;
    }
}
