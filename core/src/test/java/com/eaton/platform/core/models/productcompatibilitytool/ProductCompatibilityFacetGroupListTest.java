package com.eaton.platform.core.models.productcompatibilitytool;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class ProductCompatibilityFacetGroupListTest {

    private List<Filter> createFilterList() {
        ArrayList<Filter> filters = new ArrayList<>();
        filters.add(createFilter("D_TST_LED_Compatible_Score", "LED compatible score"));
        filters.add(createFilter("D_TST_Ext_Type", "Type (bulb shape)"));
        filters.add(createFilter("D_TST_LED_Rating", "LED rating (max wattage)"));
        filters.add(createFilter("D_TST_EXT_Wattage", "LED wattage"));
        filters.add(createFilter("D_TST_EXT_EquivalentIncandescent_wattage", "Incandescent wattage"));
        filters.add(createFilter("Product_Type", "Product Type"));
        filters.add(createFilter("D_TST_EXT_Color_Temperature", "Color temperature"));
        filters.add(createFilter("Voltage_Rating", "Voltage rating"));
        filters.add(createFilter("D_TST_EXT_Base", "Base"));
        return filters;
    }

    private Filter createFilter(String attributeValue, String attributeLabel) {
        Filter filter = new Filter();
        filter.setFilterAttributeVaue(attributeValue);
        filter.setFilterAttributeLabel(attributeLabel);
        return filter;
    }

    private String prettyPrintJson(String json) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement actualJsonElement = new JsonParser().parse(json);
        return gson.toJson(actualJsonElement);
    }

    @Test
    void testOrderedList() throws IOException {
        List<Filter> filterList = createFilterList();
        String jsonFacetsString = IOUtils.toString(Objects.requireNonNull(this.getClass().getResourceAsStream("facetGroup.json")), Charset.defaultCharset());
        String expectedOrderedFacetsString = IOUtils.toString(Objects.requireNonNull(this.getClass().getResourceAsStream("facetGroupOrdered.json")), StandardCharsets.UTF_8);
        ProductCompatibilityFacetGroupList productCompatibilityFacetGroupList = new ProductCompatibilityFacetGroupList(jsonFacetsString, filterList);
        Assertions.assertEquals(prettyPrintJson(expectedOrderedFacetsString), prettyPrintJson(productCompatibilityFacetGroupList.orderedFacetList()), "should order the facets by the order of filterList");
    }

    @Test
    void testFilterListNotSet() throws IOException {
        String jsonFacetsString = IOUtils.toString(Objects.requireNonNull(this.getClass().getResourceAsStream("facetGroup.json")), Charset.defaultCharset());
        ProductCompatibilityFacetGroupList productCompatibilityFacetGroupList = new ProductCompatibilityFacetGroupList(jsonFacetsString, null);
        Assertions.assertEquals(prettyPrintJson(jsonFacetsString), prettyPrintJson(productCompatibilityFacetGroupList.orderedFacetList()), "if filterList (filter order) not specified, facet order should remain unchanged");
    }

}
