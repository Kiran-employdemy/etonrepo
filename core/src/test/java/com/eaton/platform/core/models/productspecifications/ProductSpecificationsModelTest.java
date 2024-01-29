package com.eaton.platform.core.models.productspecifications;

import com.eaton.platform.core.bean.AttributeListDetail;
import com.eaton.platform.core.bean.attributetable.AttributeTable;
import com.eaton.platform.core.search.pojo.SkuSearchResponse;
import com.eaton.platform.core.search.service.SkuSearchService;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.SlingHttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, AemContextExtension.class})
class ProductSpecificationsModelTest {
    @InjectMocks
    ProductSpecificationsModel productSpecificationsModel = new ProductSpecificationsModel();
    @Mock
    SkuSearchService skuSearchService;
    @Mock
    SlingHttpServletRequest request;
    @Mock
    SkuSearchResponse skuSearchResponse;

    @Test
    void testThatInitCallSkuSearchServiceAndSetsTheSkuSearchResponseForAttributeGroups() throws IOException {
        when(skuSearchService.search(request)).thenReturn(skuSearchResponse);
        ArrayList<AttributeListDetail> expected = new ArrayList<>();
        when(skuSearchResponse.getAttributeGroups()).thenReturn(expected);
        productSpecificationsModel.init();
        List<AttributeListDetail> attributeGroupList = productSpecificationsModel.getAttributeGroupList();
        assertSame(expected, attributeGroupList, "should be same as from searched sku search response");
    }
    @Test
    void testThatInitCallSkuSearchServiceAndSetsTheSkuSearchResponseForAttributeTables() throws IOException {
        when(skuSearchService.search(request)).thenReturn(skuSearchResponse);
        Set<AttributeTable> expected = new LinkedHashSet<>();
        when(skuSearchResponse.getAttributeTableList()).thenReturn(expected);
        productSpecificationsModel.init();
        Set<AttributeTable> attributeTableList = productSpecificationsModel.getAttributeTableList();
        assertSame(expected, attributeTableList, "should be same as from searched sku search response");
    }


}