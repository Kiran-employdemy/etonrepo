package com.eaton.platform.integration.endeca.services.impl;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestPathInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EndecaAdvancedSearchLoadMoreOffsetProviderTest {

    @Mock
    SlingHttpServletRequest slingHttpServletRequest;
    @Mock
    RequestPathInfo requestPathInfo;

    @ParameterizedTest
    @CsvSource({
            "null, 0",
            "loadMoreOffset$, 0",
            "loadMoreOffset$60, 60",
            "loadMoreOffset$120, 120",
    })
    void testGetLoadMoreOffset(String loadMoreOffsetSelector, Long expectedResult){
        when(slingHttpServletRequest.getRequestPathInfo()).thenReturn(requestPathInfo);
        if(loadMoreOffsetSelector == null) {
            when(requestPathInfo.getSelectors()).thenReturn(new String[]{"searchTerm$", "SortBy$", "pub_date_desc", "Facets$", "startDate$", "endDate$", "nocache"});
        } else {
            when(requestPathInfo.getSelectors()).thenReturn(new String[]{"searchTerm$", "SortBy$", "pub_date_desc", "Facets$", "startDate$", "endDate$", loadMoreOffsetSelector, "nocache"});
        }
        assertEquals(expectedResult, new EndecaAdvancedSearchLoadMoreOffsetProvider(slingHttpServletRequest).getLoadMoreOffset(), "should be " + expectedResult);
    }

}