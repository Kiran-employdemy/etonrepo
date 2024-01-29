package com.eaton.platform.core.services;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.ProductFamilyDetails;
import com.eaton.platform.core.bean.ProductFamilyPIMDetails;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsBean;

public interface ProductFamilyDetailService {

    ProductFamilyDetails getProductFamilyDetailsBean(Page currentPage);
    ProductFamilyPIMDetails getProductFamilyPIMDetailsBean(SKUDetailsBean skuData, Page containingPage);
    ProductFamilyPIMDetails getProductFamilyPIMDetailsBean(String extensionId, String inventoryId, Page containingPage);

}
