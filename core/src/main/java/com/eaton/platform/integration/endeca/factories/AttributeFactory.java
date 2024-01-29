package com.eaton.platform.integration.endeca.factories;

import com.eaton.platform.core.bean.Attribute;
import com.eaton.platform.core.bean.attributetable.AttributeTable;
import com.eaton.platform.core.bean.MultiAttributes;
import com.eaton.platform.core.models.ATRGRPTYPE;
import com.eaton.platform.integration.endeca.pojo.sku.SkuCIDContent;
import org.apache.commons.collections4.CollectionUtils;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AttributeFactory {

    private final boolean withTableAttributes;
    private final AttributeValueFactory attributeValueFactory;
    private final AttributeTableRowFactory attributeTableRowFactory = new AttributeTableRowFactory();
    private final String country;

    public AttributeFactory(boolean withTableAttributes, Map<Long, SkuCIDContent> cidContentMap, String country) {
        this.withTableAttributes = withTableAttributes;
        this.attributeValueFactory = new AttributeValueFactory(cidContentMap);
        this.country = country;
    }

    public Attribute createFrom(ATRGRPTYPE.ATRGRP.ATRNM xmlSingleAttribute) {
        Attribute attribute = new Attribute();
        attribute.setAttributeLabel(xmlSingleAttribute.getLabel());
        attribute.setAttributeValue(attributeValueFactory.createFrom(xmlSingleAttribute));
        return attribute;
    }

    public Attribute createFrom(List<ATRGRPTYPE.ATRGRP.ATRMULTIROW> xmlMultiRowAttributes, List<String> groupsToRetain) {
        Map<String, List<Attribute>> attributeMapPerId = new LinkedHashMap<>();
        xmlMultiRowAttributes.forEach((ATRGRPTYPE.ATRGRP.ATRMULTIROW xmlMultiRow) -> xmlMultiRow.getATRNM().forEach((ATRGRPTYPE.ATRGRP.ATRMULTIROW.ATRNM xmlAttribute) ->{
            String id = xmlAttribute.getId();
            if (!attributeMapPerId.containsKey(id)) {
                attributeMapPerId.put(id, new LinkedList<>());
            }
            attributeMapPerId.get(id).add(createFrom(xmlAttribute, xmlMultiRow.getROWID()));
        }));
        return createAttribute(xmlMultiRowAttributes, attributeMapPerId, groupsToRetain);
    }

    private Attribute createAttribute(List<ATRGRPTYPE.ATRGRP.ATRMULTIROW> xmlMultiRowAttributes, Map<String, List<Attribute>> attributeMapPerId, List<String> groupsToRetain) {
        if (attributeMapPerId.size() == 1) {
            return attributeMapPerId.values().stream().findFirst().map(this::createFrom).orElse(null);
        }
        if (attributeMapPerId.values().stream().anyMatch(this::doesIdContainRegionOrCountry)) {
            return createAttributeForCountry(xmlMultiRowAttributes, country);
        }
        return createMultiOrTableAttribute(attributeMapPerId, groupsToRetain);
    }

    private Attribute createMultiOrTableAttribute(Map<String, List<Attribute>> attributeMapPerId, List<String> groupsToRetain) {
        if (withTableAttributes) {
            return createTableAttributes(attributeMapPerId, groupsToRetain);
        }
        return createAListOfAttributes(attributeMapPerId);
    }

    private Attribute createAListOfAttributes(Map<String, List<Attribute>> attributeMapPerId) {
        List<Attribute> attributes = attributeMapPerId.keySet().stream().map(key -> createFrom(attributeMapPerId.get(key))).collect(Collectors.toCollection(LinkedList::new));
        return new MultiAttributes(attributes);
    }

    private Attribute createFrom(List<Attribute> attributeList) {
        Attribute attribute = new Attribute();
        Attribute firstAttribute = attributeList.get(0);
        attribute.setAttributeId(firstAttribute.getAttributeId());
        attribute.setAttributeLabel(firstAttribute.getAttributeLabel());
        attribute.setAttributeValue(attributeValueFactory.createFrom(attributeList));
        return attribute;
    }

    private Attribute createAttributeForCountry(List<ATRGRPTYPE.ATRGRP.ATRMULTIROW> xmlMultiRowAttributes, String country) {
        List<Attribute> matchingAttributes = new LinkedList<>();
        xmlMultiRowAttributes.forEach((ATRGRPTYPE.ATRGRP.ATRMULTIROW multiRow) -> {
            List<String> countries = multiRow.getATRNM().stream().filter(atrnm -> "Country".equals(atrnm.getLabel())).map(ATRGRPTYPE.ATRGRP.ATRMULTIROW.ATRNM::getValue)
                    .collect(Collectors.toCollection(LinkedList::new));
            if (countries.contains(country) || countries.contains("GLOBAL")) {
                matchingAttributes.add(createFrom(multiRow.getATRNM().get(0), multiRow.getROWID()));
            }
        });
        if (matchingAttributes.isEmpty()) {
            return null;
        }
        return createFrom(matchingAttributes);
    }

    private boolean doesIdContainRegionOrCountry(List<Attribute> attributesToCheck) {
        return attributesToCheck.stream().anyMatch(attribute -> "Region".equals(attribute.getAttributeLabel()) || "Country".equals(attribute.getAttributeLabel()));
    }

    private Attribute createTableAttributes(Map<String, List<Attribute>> attributeMapPerId, List<String> groupsToRetain) {
        AttributeTable attributeTable = new AttributeTable();
        if (CollectionUtils.isNotEmpty(groupsToRetain)) {
            attributeTable.setHeaders(groupsToRetain.stream().map(attributeMapPerId::get).map((List<Attribute> attributes) -> attributes.get(0).getAttributeLabel())
                    .collect(Collectors.toCollection(LinkedHashSet::new)));
        } else {
            attributeTable.setHeaders(attributeMapPerId.values().stream().map(attributes -> attributes.get(0).getAttributeLabel()).collect(Collectors.toCollection(LinkedHashSet::new)));
        }
        attributeTable.setRows(attributeTableRowFactory.createFrom(attributeMapPerId, groupsToRetain));
        return attributeTable;
    }

    private Attribute createFrom(ATRGRPTYPE.ATRGRP.ATRMULTIROW.ATRNM xmlAttributeFromMultiRow, Integer rowId) {
        Attribute attribute = new Attribute();
        attribute.setRowId(rowId);
        attribute.setAttributeLabel(xmlAttributeFromMultiRow.getLabel());
        attribute.setAttributeValue(attributeValueFactory.createFrom(xmlAttributeFromMultiRow));
        return attribute;
    }
}
