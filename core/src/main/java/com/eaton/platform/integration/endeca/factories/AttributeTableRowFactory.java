package com.eaton.platform.integration.endeca.factories;

import com.eaton.platform.core.bean.Attribute;
import com.eaton.platform.core.bean.attributetable.AttributeTableRow;
import org.apache.commons.collections4.CollectionUtils;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AttributeTableRowFactory {

    private List<String> groupsToRetain;

    public List<AttributeTableRow> createFrom(Map<String, List<Attribute>> mapOfAttributesPerId, List<String> groupsToRetain) {
        LinkedList<AttributeTableRow> attributeTableRows = new LinkedList<>();
        this.groupsToRetain = groupsToRetain;
        mapOfAttributesPerId.entrySet().stream().findFirst().ifPresent((Map.Entry<String, List<Attribute>> first) -> {
            final List<Attribute> values = first.getValue();
            if (CollectionUtils.isNotEmpty(groupsToRetain)) {
                attributeTableRows.addAll(addRowsOrderedByGroupsToRetain(mapOfAttributesPerId, values));
            } else {
                attributeTableRows.addAll(addRowsToList(mapOfAttributesPerId, values));
            }
        });
        return attributeTableRows;
    }

    private List<AttributeTableRow> addRowsOrderedByGroupsToRetain(Map<String, List<Attribute>> mapOfAttributesPerId, List<Attribute> values) {
        Map<String, List<Attribute>> orderedMapOfAttributesPerId = new LinkedHashMap<>();
        groupsToRetain.forEach(groupToRetain -> orderedMapOfAttributesPerId.put(groupToRetain, mapOfAttributesPerId.get(groupToRetain)));
        return new LinkedList<>(addRowsToList(orderedMapOfAttributesPerId, values));
    }

    private static List<AttributeTableRow> addRowsToList(Map<String, List<Attribute>> mapOfAttributesPerId, List<Attribute> values) {
        LinkedList<AttributeTableRow> attributeTableRows = new LinkedList<>() ;
        for (int i = 0; i < values.size(); i++) {
            Map<String, String> rowCells = new LinkedHashMap<>();
            for (Map.Entry<String, List<Attribute>> entry : mapOfAttributesPerId.entrySet()) {
                rowCells.put(entry.getValue().get(i).getAttributeLabel(), entry.getValue().get(i).getAttributeValue().getCdata());
            }
            AttributeTableRow attributeTableRow = new AttributeTableRow();
            attributeTableRow.setId(values.get(i).getRowId());
            attributeTableRow.setCells(rowCells);
            attributeTableRows.add(attributeTableRow);
        }
        return attributeTableRows;
    }
}
