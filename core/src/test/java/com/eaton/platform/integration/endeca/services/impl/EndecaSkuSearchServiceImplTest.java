package com.eaton.platform.integration.endeca.services.impl;

import com.eaton.platform.core.services.EndecaRequestService;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.pojo.asset.EndecaAssetResponse;
import com.eaton.platform.integration.endeca.services.EndecaService;
import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EndecaSkuSearchServiceImplTest {

    @InjectMocks
    EndecaSkuSearchServiceImpl endecaSkuSearchService = new EndecaSkuSearchServiceImpl();
    @Mock
    EndecaRequestService endecaRequestService;
    @Mock
    EndecaService endecaService;
    @Mock
    EndecaServiceRequestBean endecaServiceRequestBean;

    @Test
    @DisplayName("searchInventoryIdsPerSkuIdsForLocale gives back a map of skuIds and their inventoryId")
    void testSearchInventoryIdsPerSkuIdsForLocaleAllOk() throws IOException {
        when(endecaRequestService.getDataSheetEndecaRequestBean("en-us", List.of("br120","ch120"))).thenReturn(endecaServiceRequestBean);
        when(endecaService.getSearchResultJson(endecaServiceRequestBean)).thenReturn(new Gson().fromJson(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("sku-details-response-from-endeca.json"))), EndecaAssetResponse.class).toString());
        Map<String, String> actual = endecaSkuSearchService.searchInventoryIdsPerSkuIdsForLocale("en-us", List.of("br120","ch120"));
        Map<String, String> expected = new HashMap<>();
        expected.put("BR120", "6380105");
        expected.put("CH120", "6380104");
        Assertions.assertEquals(expected, actual, "should be the expected");
    }
    @Test
    @DisplayName("searchInventoryIdsPerSkuIdsForLocale gives back an empty map if no response")
    void testSearchInventoryIdsPerSkuIdsForLocaleResponseNull() throws IOException {
        when(endecaRequestService.getDataSheetEndecaRequestBean("en-us", List.of("br120","ch120"))).thenReturn(endecaServiceRequestBean);
        when(endecaService.getSearchResultJson(endecaServiceRequestBean)).thenReturn(null);
        Map<String, String> actual = endecaSkuSearchService.searchInventoryIdsPerSkuIdsForLocale("en-us", List.of("br120","ch120"));
        Map<String, String> expected = new HashMap<>();
        Assertions.assertEquals(expected, actual, "should be an empty map");
    }
    @Test
    @DisplayName("searchInventoryIdsPerSkuIdsForLocale gives back an empty map if empty response")
    void testSearchInventoryIdsPerSkuIdsForLocaleResponseEmpty() throws IOException {
        when(endecaRequestService.getDataSheetEndecaRequestBean("en-us", List.of("br120","ch120"))).thenReturn(endecaServiceRequestBean);
        when(endecaService.getSearchResultJson(endecaServiceRequestBean)).thenReturn("{}");
        Map<String, String> actual = endecaSkuSearchService.searchInventoryIdsPerSkuIdsForLocale("en-us", List.of("br120","ch120"));
        Map<String, String> expected = new HashMap<>();
        Assertions.assertEquals(expected, actual, "should be an empty map");
    }
    @Test
    @DisplayName("searchInventoryIdsPerSkuIdsForLocale gives back an empty map if empty response")
    void testSearchInventoryIdsPerSkuIdsForLocaleSearchResultsEmpty() throws IOException {
        when(endecaRequestService.getDataSheetEndecaRequestBean("en-us", List.of("br120","ch120"))).thenReturn(endecaServiceRequestBean);
        when(endecaService.getSearchResultJson(endecaServiceRequestBean)).thenReturn("{\"response\":{\"searchResults\":{}}}");
        Map<String, String> actual = endecaSkuSearchService.searchInventoryIdsPerSkuIdsForLocale("en-us", List.of("br120","ch120"));
        Map<String, String> expected = new HashMap<>();
        Assertions.assertEquals(expected, actual, "should be an empty map");
    }
    @Test
    @DisplayName("searchInventoryIdsPerSkuIdsForLocale gives back an empty map if response with null documents")
    void testSearchInventoryIdsPerSkuIdsForLocaleSearchResultsNullDocuments() throws IOException {
        when(endecaRequestService.getDataSheetEndecaRequestBean("en-us", List.of("br120","ch120"))).thenReturn(endecaServiceRequestBean);
        when(endecaService.getSearchResultJson(endecaServiceRequestBean)).thenReturn("{\n" +
                "  \"response\" : {\n" +
                "    \"status\" : \"Success\",\n" +
                "    \"statusDetails\" : {\n" +
                "      \"messages\" : [ \"Query=enePort=16300&amp;Nr=AND%28Language%3aen_US%2cOR%28Country%3aDE%2cCountry%3aGLOBAL%29%2cNOT%28P_Restricted_Country%3aDE%29%2cOR%28Catalog_number%3aBR120%2cCatalog_number%3aCH120%29%29&amp;eneHost=endeca-dev.tcc.etn.com&amp;N=0\" ]\n" +
                "    },\n" +
                "    \"didYouMean\" : null,\n" +
                "    \"binning\" : null,\n" +
                "    \"searchResults\" : {\n" +
                "      \"totalCount\" : 0,\n" +
                "      \"document\" : [ null, null ]\n" +
                "    },\n" +
                "    \"request\" : {\n" +
                "      \"searchApplication\" : \"eatonpdhcomparision\",\n" +
                "      \"searchApplicationKey\" : \"abc123\",\n" +
                "      \"function\" : \"search\",\n" +
                "      \"searchTerms\" : \"ignore\",\n" +
                "      \"language\" : \"de_DE\",\n" +
                "      \"startingRecordNumber\" : 0,\n" +
                "      \"numberOfRecordsToReturn\" : 30,\n" +
                "      \"filters\" : [ {\n" +
                "        \"filterName\" : \"Country\",\n" +
                "        \"filterValue\" : [ \"DE\" ],\n" +
                "        \"preferenceValue\" : null\n" +
                "      }, {\n" +
                "        \"filterName\" : \"Catalog_Nos\",\n" +
                "        \"filterValue\" : [ \"BR120\", \"CH120\" ],\n" +
                "        \"preferenceValue\" : null\n" +
                "      } ],\n" +
                "      \"userName\" : null\n" +
                "    }\n" +
                "  }\n" +
                "}");
        Map<String, String> actual = endecaSkuSearchService.searchInventoryIdsPerSkuIdsForLocale("en-us", List.of("br120","ch120"));
        Map<String, String> expected = new HashMap<>();
        Assertions.assertEquals(expected, actual, "should be an empty map");
    }
}