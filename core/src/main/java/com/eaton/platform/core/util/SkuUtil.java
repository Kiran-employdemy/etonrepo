package com.eaton.platform.core.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.eaton.platform.core.bean.AttrNmBean;
import com.eaton.platform.core.bean.ImageGroupBean;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsBean;

public class SkuUtil {

    private SkuUtil(){
        // To ristrict instantiation
    }

     /**
     * This method extracts SKU images from the endeca response.
     * @param skuDetailsBean
     * @param adminService
     * @return
     */
    public static Map<String,   Map<String, AttrNmBean>> getSkuImages(SKUDetailsBean skuDetailsBean, AdminService adminService) {
        String xmlString = skuDetailsBean.getSkuImgs();
        Map<String,  Map<String, AttrNmBean>> skuImages = new HashMap<>();
        if(null != xmlString) {
            SkuImageParser skuImageParser = new SkuImageParser();
            Map<String, ImageGroupBean> attrGrpList = skuImageParser.getPDHImageList(xmlString, adminService);
            if (attrGrpList != null) {
				for (Map.Entry<String, ImageGroupBean> entry : attrGrpList.entrySet()) {
                    if(StringUtils.isNoneEmpty(entry.getKey()) && entry.getValue() != null) {
                        String title = entry.getKey();
						ImageGroupBean imageGroupBean = entry.getValue();
                        skuImages.put(title, imageGroupBean.getImageRenditionMap());
                    }
                }
            }
        }
      return skuImages;
    }
}
