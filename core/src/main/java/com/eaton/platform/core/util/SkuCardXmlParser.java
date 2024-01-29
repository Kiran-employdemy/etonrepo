package com.eaton.platform.core.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.eaton.platform.core.bean.Association;
import com.eaton.platform.core.bean.Association.SKU;
import com.eaton.platform.core.bean.SkuCardBean;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsBean;

public class SkuCardXmlParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(SkuCardXmlParser.class);
    private static final String UPSELL = "upSell";
    private static final String CROSSSELL = "crossSell";
    private static final String REPLACEMENT = "replacement";

    /**
     * @param xmlName
     * @param skuDetailBean
     * @param adminService
     * @return SKU card List for corresponding xml
     */
    public List<SkuCardBean> getSkuCardList(String xmlName, SKUDetailsBean skuDetailBean, AdminService adminService) {
        final String xmlString = getXmlStringFromEndeca(xmlName, skuDetailBean);
        if (null != xmlString && !xmlString.isEmpty()) {
            final String convertedXmlString = CommonUtil.convertXMLString(xmlString);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("sku card xml unescaped data---- " + convertedXmlString);
            }


            final InputStream xsdInputStream = CommonUtil.getFileFromDAM(adminService, CommonConstants.DAM_XSD_FILEPATH, CommonConstants.SKUCARD_XSD_FILENAME);
            if (xsdInputStream != null) {
                final Association association = CommonUtil.getUnmarshaledClass(convertedXmlString, Association.class, xsdInputStream);
                return setSKUItems(null != association ? association.getSKU() : new ArrayList<>());
            }

        }
        return null;
    }

    /**
     * @param skuList
     * @return sku card list
     * Method contain the logic to iterate corresponding xml element and parse inside a bean
     */
    private List<SkuCardBean> setSKUItems(List<SKU> skuList) {
        final ArrayList<SkuCardBean> skuCardList = new ArrayList<>();
        for (SKU item : skuList) {
            final SkuCardBean skuCardBean = new SkuCardBean();
            skuCardBean.setImage(item.getImage());
            skuCardBean.setCatalog(item.getCatalog());
            skuCardBean.setPriority(item.getPriority());
            skuCardBean.setProductFamily(item.getProductFamily());
            skuCardBean.setProductName(item.getProductName());
            skuCardBean.setLongDesc(item.getLongDesc());
            skuCardList.add(skuCardBean);
        }
        return skuCardList;
    }

    /**
     * @param xmlName
     * @param skuDetailBean
     * @return xml string retrieved from Endeca
     * Method retrieve xml string for corresponding xml name.
     */
    private String getXmlStringFromEndeca(String xmlName, SKUDetailsBean skuDetailBean) {
        String xmlString = StringUtils.EMPTY;
        switch (xmlName) {
            case UPSELL:
                xmlString = skuDetailBean.getUpSell();
                break;
            case CROSSSELL:
                xmlString = skuDetailBean.getCrossSell();
                break;
            case REPLACEMENT:
                xmlString = skuDetailBean.getReplacement();
                break;
            default:
                LOGGER.debug("xml name is not matching for parsing in SkuCardXmlParser ");
        }
        return xmlString;
    }
}
