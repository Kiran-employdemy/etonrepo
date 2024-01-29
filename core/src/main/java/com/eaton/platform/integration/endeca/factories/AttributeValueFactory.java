package com.eaton.platform.integration.endeca.factories;

import com.eaton.platform.core.bean.Attribute;
import com.eaton.platform.core.bean.AttributeValue;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.ATRGRPTYPE;
import com.eaton.platform.integration.endeca.enums.CIDDocType;
import com.eaton.platform.integration.endeca.pojo.sku.SkuCIDContent;
import com.eaton.platform.integration.endeca.pojo.sku.SkuCIDDoc;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AttributeValueFactory {
    private static final String LINE_BREAK = " <br> ";
    private final Map<Long, SkuCIDContent> cidContentMap;

    public AttributeValueFactory(Map<Long, SkuCIDContent> cidContentMap) {
        this.cidContentMap = cidContentMap;
    }

    public AttributeValue createFrom(ATRGRPTYPE.ATRGRP.ATRNM xmlAttribute) {
        if (xmlAttribute.getCID() != null) {
            return createCIDMappedAttributeValue(xmlAttribute);
        }
        return createAttributeValue(xmlAttribute.getValue().trim());
    }

    private AttributeValue createCIDMappedAttributeValue(ATRGRPTYPE.ATRGRP.ATRNM xmlAttribute) {
        AttributeValue attributeValue = new AttributeValue();
        Long cid = xmlAttribute.getCID();
        if (cidContentMap.containsKey(cid) && cidContentMap.get(cid).containsDocs()) {
            SkuCIDContent skuCIDContent = cidContentMap.get(cid);
            attributeValue.setCdata(xmlAttribute.getValue());
            SkuCIDDoc first = skuCIDContent.getSkuCIDDocList().get(0);
            attributeValue.setCountry(first.getCountries());
            attributeValue.setLanguage(first.getLanguage());
            attributeValue.setPriority(first.getPriority());
            attributeValue.setCidCdata(first.getValue());
            if (skuCIDContent.isSingle()) {
                attributeValue.setAttributeType(first.getType().getType());
            } else {
                attributeValue.setAttributeType(CIDDocType.MULTIPLE.getType());
            }
            skuCIDContent.getSkuCIDDocList().forEach((SkuCIDDoc skuCIDDoc) -> skuCIDDoc.getType().setCorrectValueOnAttributeValue(attributeValue, skuCIDDoc.getValue()));
        }
        return attributeValue;
    }

    public AttributeValue createFrom(ATRGRPTYPE.ATRGRP.ATRMULTIROW.ATRNM xmlAttributeFromMultiRow) {
        return createAttributeValue(xmlAttributeFromMultiRow.getValue());
    }

    public AttributeValue createFrom(List<Attribute> attributeList) {
        AttributeValue attributeValue = new AttributeValue();
        attributeValue.setAttributeType(CIDDocType.OTHER.getType());
        attributeValue.setCdata(Joiner.on(LINE_BREAK).join(attributeList.stream().map(attribute -> attribute.getAttributeValue().getCdata()).collect(Collectors.toList())));
        return attributeValue;
    }

    private AttributeValue createAttributeValue(String value) {
        AttributeValue attributeValue = new AttributeValue();
        attributeValue.setAttributeType(CIDDocType.OTHER.getType());
        attributeValue.setCdata(ifPipeTransformToBulletedList(value));
        return attributeValue;
    }

    private static String ifPipeTransformToBulletedList(String value) {
        if (!value.contains(CommonConstants.PIPE)) {
            return value;
        }
        return String.format("<ul><li>%s</li></ul>", String.join("</li><li>", Splitter.on(CommonConstants.PIPE).split(value)));
    }

}
