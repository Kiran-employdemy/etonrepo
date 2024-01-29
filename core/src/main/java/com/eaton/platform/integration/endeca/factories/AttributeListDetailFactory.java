package com.eaton.platform.integration.endeca.factories;

import com.eaton.platform.core.bean.Attribute;
import com.eaton.platform.core.bean.AttributeListDetail;
import com.eaton.platform.core.bean.attributetable.AttributeTable;
import com.eaton.platform.core.bean.MultiAttributes;
import com.eaton.platform.core.models.ATRGRPTYPE;
import com.eaton.platform.integration.endeca.pojo.sku.SkuCIDContent;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AttributeListDetailFactory {

    private final AttributeFactory attributeFactory;

    public AttributeListDetailFactory(boolean withTableAttributes, Map<Long, SkuCIDContent> cidContentMap, String country) {
        attributeFactory = new AttributeFactory(withTableAttributes, cidContentMap, country);
    }

    public AttributeListDetail createFrom(ATRGRPTYPE xmlAttributeGroups) {
        AttributeListDetail attributeListDetail = new AttributeListDetail();
        attributeListDetail.setAttributeGroupName(xmlAttributeGroups.getLabel());
        attributeListDetail.setAttributeList(new LinkedList<>());
        xmlAttributeGroups.getATRGRP().forEach((ATRGRPTYPE.ATRGRP xmlAttributeGroup) -> {
            if (!xmlAttributeGroup.getATRNM().isEmpty()) {
                handleSingleRowAttribute(xmlAttributeGroup.getATRNM(), attributeListDetail);
            }
            if (!xmlAttributeGroup.getATRMULTIROW().isEmpty()) {
                handleMultiRowAttributes(xmlAttributeGroup.getATRMULTIROW(), attributeListDetail, xmlAttributeGroup.getLabel(), xmlAttributeGroup.getId(), null);
            }
        });
        return attributeListDetail;
    }

    private void handleSingleRowAttribute(List<ATRGRPTYPE.ATRGRP.ATRNM> xmlAttributes, AttributeListDetail attributeListDetail) {
        xmlAttributes.forEach((ATRGRPTYPE.ATRGRP.ATRNM xmlAttribute) -> attributeListDetail.getAttributeList().add(attributeFactory.createFrom(xmlAttribute)));
    }

    private void handleMultiRowAttributes(List<ATRGRPTYPE.ATRGRP.ATRMULTIROW> xmlMultiRows, AttributeListDetail attributeListDetail, String groupLabel, String groupId, List<String> groupsToRetain) {
        Attribute attribute = attributeFactory.createFrom(xmlMultiRows, groupsToRetain);
        if (attribute != null) {
            if (attribute instanceof MultiAttributes) {
                attributeListDetail.getAttributeList().addAll(((MultiAttributes) attribute).getAttributes());
            } else if (attribute instanceof AttributeTable) {
                AttributeTable attributeTable = (AttributeTable) attribute;
                attributeTable.setHeadline(groupLabel);
                attributeTable.setId(groupId);
                attributeListDetail.getAttributeList().add(attributeTable);
            } else {
                attributeListDetail.getAttributeList().add(attribute);
            }
        }
    }

    public AttributeListDetail createInOrderAndFiltered(List<String> groupNamesToRetain, ATRGRPTYPE xmlAttributeGroups) {
        AttributeListDetail attributeListDetail = new AttributeListDetail();
        attributeListDetail.setAttributeGroupName(xmlAttributeGroups.getLabel());
        attributeListDetail.setAttributeList(new LinkedList<>());
        groupNamesToRetain.forEach((String groupToRetain) -> xmlAttributeGroups.getATRGRP()
                .forEach((ATRGRPTYPE.ATRGRP xmlAttributeGroup) -> handleFiltered(groupToRetain, xmlAttributeGroup, attributeListDetail, groupNamesToRetain)));
        return attributeListDetail;
    }

    private void handleFiltered(String groupToRetain, ATRGRPTYPE.ATRGRP xmlAttributeGroup, AttributeListDetail attributeListDetail, List<String> groupsToRetain) {
        if (!xmlAttributeGroup.getATRNM().isEmpty()) {
            handleSingleRowAttribute(xmlAttributeGroup.getATRNM().stream()
                    .filter(xmlAttribute -> groupToRetain.equals(xmlAttribute.getId())).collect(Collectors.toList()), attributeListDetail);
        }
        if (!xmlAttributeGroup.getATRMULTIROW().isEmpty()){
            List<ATRGRPTYPE.ATRGRP.ATRMULTIROW> xmlGroupToRetainFilteredOut = xmlAttributeGroup.getATRMULTIROW().stream().filter(xmlMultiRow -> xmlMultiRow.getATRNM().stream()
                    .anyMatch(xmlAttribute -> groupToRetain.equals(xmlAttribute.getId()))).collect(Collectors.toList());
            if (!xmlGroupToRetainFilteredOut.isEmpty()) {
                handleMultiRowAttributes(
                        xmlGroupToRetainFilteredOut
                        , attributeListDetail, xmlAttributeGroup.getLabel(), xmlAttributeGroup.getId(), groupsToRetain);
            }
        }
    }
}
