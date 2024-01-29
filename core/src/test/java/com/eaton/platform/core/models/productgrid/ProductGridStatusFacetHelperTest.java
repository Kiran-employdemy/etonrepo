package com.eaton.platform.core.models.productgrid;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.integration.endeca.bean.FacetGroupBean;
import com.eaton.platform.integration.endeca.bean.FacetValueBean;
import com.eaton.platform.integration.endeca.bean.FacetsBean;
import com.eaton.platform.integration.endeca.bean.familymodule.SKUListResponseBean;
import com.eaton.platform.integration.endeca.services.EndecaService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBeanFixtures.eatonPdh2SearchRequestWithFilters;
import static com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBeanFixtures.eatonPdh2SearchRequestWithFiltersAndStatusFilter;
import static com.eaton.platform.integration.endeca.bean.FacetGroupBeanFixtures.createFacetGroupBeansWithStatus;
import static com.eaton.platform.integration.endeca.bean.FacetGroupBeanFixtures.statusFacetGroupWithGroupId;
import static com.eaton.platform.integration.endeca.bean.FacetsBeanFixtures.facetsBeanWithSpecificStatusFacetsGroupId;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductGridStatusFacetHelperTest {

    public static final String MODELS = "models";
    public static final String STATUS_GROUP_ID_FOR_PRODUCT_FAMILY = "Status";
    public static final String STATUS_GROUP_ID_FOR_SUB_CATEGORY = "product-attributes_product-status";
    public static final String ACTIVE = "Active";
    public static final String RADIOS = "radios";
    public static final String CHECKED = "checked";
    public static final String NOCACHE = "nocache";
    public static final String FACETS = "facets$1689422387";
    public static final String SORT_DESC = "sort$desc";
    public static final String ANON = "anon";
    @Mock
    Resource resource;
    @Mock
    SlingHttpServletRequest slingHttpServletRequest;
    @Mock
    EndecaService endecaService;
    @Mock
    RequestPathInfo requestPathInfo;

    @Test
    @DisplayName("When pageType is product-family if the facetGroup with label Status exists on the facetGroupList, it is removed")
    void testRemovalOfStatusFacetFromListWhenExistsProductFamily() {
        when(slingHttpServletRequest.getRequestPathInfo()).thenReturn(requestPathInfo);
        when(requestPathInfo.getSelectors()).thenReturn(new String[]{MODELS});
        String statusGroupId = STATUS_GROUP_ID_FOR_PRODUCT_FAMILY;
        ProductStatusFacetHelper productStatusFacetHelper = new ProductStatusFacetHelper(eatonPdh2SearchRequestWithFilters(), slingHttpServletRequest, resource, endecaService, facetsBeanWithSpecificStatusFacetsGroupId(statusGroupId));
        List<FacetGroupBean> facetGroupBeansWithStatus = createFacetGroupBeansWithStatus(statusGroupId);
        productStatusFacetHelper.callEndecaWithStatusFilterWhenModelsInSelectors(facetGroupBeansWithStatus, CommonConstants.PAGE_TYPE_PRODUCT_FAMILY_PAGE);
        Assertions.assertFalse(facetGroupBeansWithStatus.contains(statusFacetGroupWithGroupId(statusGroupId)), "should be false");
    }

    @Test
    @DisplayName("When pageType is sub-category if the facetGroup with label product-attributes_product-status exists on the facetGroupList, it is removed")
    void testRemovalOfStatusFacetFromListWhenExistsSubCategory() {
        when(slingHttpServletRequest.getRequestPathInfo()).thenReturn(requestPathInfo);
        when(requestPathInfo.getSelectors()).thenReturn(new String[]{MODELS});
        String statusGroupId = STATUS_GROUP_ID_FOR_SUB_CATEGORY;
        ProductStatusFacetHelper productStatusFacetHelper = new ProductStatusFacetHelper(eatonPdh2SearchRequestWithFilters(), slingHttpServletRequest, resource, endecaService, facetsBeanWithSpecificStatusFacetsGroupId(statusGroupId));
        List<FacetGroupBean> facetGroupBeansWithStatus = createFacetGroupBeansWithStatus(statusGroupId);
        productStatusFacetHelper.callEndecaWithStatusFilterWhenModelsInSelectors(facetGroupBeansWithStatus, CommonConstants.PAGE_TYPE_PRODUCT_SUB_CATEGORY_PAGE);
        Assertions.assertFalse(facetGroupBeansWithStatus.contains(statusFacetGroupWithGroupId(statusGroupId)), "should be false");
    }

    @Test
    @DisplayName("When page type sub-category, on facetsBean the statusFacet in the list is updated with type radios and the value active is marked as checked")
    void testStatusActiveIsCheckedAndTypeIsRadiosSubCategory() {
        when(slingHttpServletRequest.getRequestPathInfo()).thenReturn(requestPathInfo);
        when(requestPathInfo.getSelectors()).thenReturn(new String[]{MODELS});
        String statusGroupId = STATUS_GROUP_ID_FOR_SUB_CATEGORY;
        FacetsBean facetsBeanToUpdate = facetsBeanWithSpecificStatusFacetsGroupId(statusGroupId);
        ProductStatusFacetHelper productStatusFacetHelper = new ProductStatusFacetHelper(eatonPdh2SearchRequestWithFilters(), slingHttpServletRequest, resource, endecaService, facetsBeanToUpdate);
        productStatusFacetHelper.callEndecaWithStatusFilterWhenModelsInSelectors(createFacetGroupBeansWithStatus(statusGroupId), CommonConstants.PAGE_TYPE_PRODUCT_SUB_CATEGORY_PAGE);
        FacetGroupBean statusGroup = facetsBeanToUpdate.getFacetGroupList().stream().filter(facetGroupBean -> facetGroupBean.getFacetGroupId().equals(statusGroupId)).findAny().orElse(new FacetGroupBean());
        FacetValueBean activeValue = statusGroup.getFacetValueList().stream().filter(valueBean -> valueBean.getFacetValueLabel().equals(ACTIVE)).findAny().orElse(new FacetValueBean());
        Assertions.assertAll(() -> Assertions.assertEquals(RADIOS, statusGroup.getInputType(), "should be radios"),
                () -> Assertions.assertEquals(CHECKED, activeValue.getActiveRadioButton(), "should be checked"));
    }

    @Test
    @DisplayName("When page type product-family, on facetsBean the statusFacet in the list is updated with type radios and the value active is marked as checked")
    void testStatusActiveIsCheckedAndTypeIsRadiosProductFamily() {
        when(slingHttpServletRequest.getRequestPathInfo()).thenReturn(requestPathInfo);
        when(requestPathInfo.getSelectors()).thenReturn(new String[]{MODELS});
        String statusGroupId = STATUS_GROUP_ID_FOR_PRODUCT_FAMILY;
        FacetsBean facetsBeanToUpdate = facetsBeanWithSpecificStatusFacetsGroupId(statusGroupId);
        ProductStatusFacetHelper productStatusFacetHelper = new ProductStatusFacetHelper(eatonPdh2SearchRequestWithFilters(), slingHttpServletRequest, resource, endecaService, facetsBeanToUpdate);
        productStatusFacetHelper.callEndecaWithStatusFilterWhenModelsInSelectors(createFacetGroupBeansWithStatus(statusGroupId), CommonConstants.PAGE_TYPE_PRODUCT_FAMILY_PAGE);
        FacetGroupBean statusGroup = facetsBeanToUpdate.getFacetGroupList().stream().filter(facetGroupBean -> facetGroupBean.getFacetGroupId().equals(statusGroupId)).findAny().orElse(new FacetGroupBean());
        FacetValueBean activeValue = statusGroup.getFacetValueList().stream().filter(valueBean -> valueBean.getFacetValueLabel().equals(ACTIVE)).findAny().orElse(new FacetValueBean());
        Assertions.assertAll(() -> Assertions.assertEquals(RADIOS, statusGroup.getInputType(), "should be radios"),
                () -> Assertions.assertEquals(CHECKED, activeValue.getActiveRadioButton(), "should be checked"));
    }

    @Test
    @DisplayName("When page type product-family, new call is made to endeca with filter the status active")
    void testCorrectNewCallIsMadeToEndecaProductFamily() {
        when(slingHttpServletRequest.getRequestPathInfo()).thenReturn(requestPathInfo);
        when(requestPathInfo.getSelectors()).thenReturn(new String[]{MODELS});
        String statusGroupId = STATUS_GROUP_ID_FOR_PRODUCT_FAMILY;
        ProductStatusFacetHelper productStatusFacetHelper = new ProductStatusFacetHelper(eatonPdh2SearchRequestWithFilters(), slingHttpServletRequest, resource, endecaService, facetsBeanWithSpecificStatusFacetsGroupId(statusGroupId));
        SKUListResponseBean skuListResponseBean = new SKUListResponseBean();
        when(endecaService.getSKUList(eatonPdh2SearchRequestWithFiltersAndStatusFilter(), resource)).thenReturn(skuListResponseBean);
        SKUListResponseBean responseBean = productStatusFacetHelper.callEndecaWithStatusFilterWhenModelsInSelectors(createFacetGroupBeansWithStatus(statusGroupId), CommonConstants.PAGE_TYPE_PRODUCT_FAMILY_PAGE);
        Assertions.assertSame(skuListResponseBean, responseBean, "must be same object returned from endeca");
    }

    @Test
    @DisplayName("When page type sub-category, new call is made to endeca with filter the status active")
    void testCorrectNewCallIsMadeToEndecaSubCategoryWithModels() {
        when(slingHttpServletRequest.getRequestPathInfo()).thenReturn(requestPathInfo);
        when(requestPathInfo.getSelectors()).thenReturn(new String[]{MODELS});
        String statusLabel = STATUS_GROUP_ID_FOR_SUB_CATEGORY;
        ProductStatusFacetHelper productStatusFacetHelper = new ProductStatusFacetHelper(eatonPdh2SearchRequestWithFilters(), slingHttpServletRequest, resource, endecaService, facetsBeanWithSpecificStatusFacetsGroupId(statusLabel));
        SKUListResponseBean skuListResponseBean = new SKUListResponseBean();
        when(endecaService.getSKUList(eatonPdh2SearchRequestWithFiltersAndStatusFilter(), resource)).thenReturn(skuListResponseBean);
        SKUListResponseBean responseBean = productStatusFacetHelper.callEndecaWithStatusFilterWhenModelsInSelectors(createFacetGroupBeansWithStatus(statusLabel), CommonConstants.PAGE_TYPE_PRODUCT_SUB_CATEGORY_PAGE);
        Assertions.assertSame(skuListResponseBean, responseBean, "must be same object returned from endeca");
    }

    @Test
    @DisplayName("When page type sub-category and nocache in the selectors, new call is made to endeca with filter the status active")
    void testCorrectNewCallIsMadeToEndecaSubCategoryWithModelsAndNocache() {
        when(slingHttpServletRequest.getRequestPathInfo()).thenReturn(requestPathInfo);
        when(requestPathInfo.getSelectors()).thenReturn(new String[]{MODELS, NOCACHE});
        String statusLabel = STATUS_GROUP_ID_FOR_SUB_CATEGORY;
        ProductStatusFacetHelper productStatusFacetHelper = new ProductStatusFacetHelper(eatonPdh2SearchRequestWithFilters(), slingHttpServletRequest, resource, endecaService, facetsBeanWithSpecificStatusFacetsGroupId(statusLabel));
        SKUListResponseBean skuListResponseBean = new SKUListResponseBean();
        when(endecaService.getSKUList(eatonPdh2SearchRequestWithFiltersAndStatusFilter(), resource)).thenReturn(skuListResponseBean);
        SKUListResponseBean responseBean = productStatusFacetHelper.callEndecaWithStatusFilterWhenModelsInSelectors(createFacetGroupBeansWithStatus(statusLabel), CommonConstants.PAGE_TYPE_PRODUCT_SUB_CATEGORY_PAGE);
        Assertions.assertSame(skuListResponseBean, responseBean, "must be same object returned from endeca");
    }

    @Test
    @DisplayName("When page type sub-category and anon in the selectors, new call is made to endeca with filter the status active")
    void testCorrectNewCallIsMadeToEndecaSubCategoryWithModelsAndAnon() {
        when(slingHttpServletRequest.getRequestPathInfo()).thenReturn(requestPathInfo);
        when(requestPathInfo.getSelectors()).thenReturn(new String[]{MODELS, ANON});
        String statusLabel = STATUS_GROUP_ID_FOR_SUB_CATEGORY;
        ProductStatusFacetHelper productStatusFacetHelper = new ProductStatusFacetHelper(eatonPdh2SearchRequestWithFilters(), slingHttpServletRequest, resource, endecaService, facetsBeanWithSpecificStatusFacetsGroupId(statusLabel));
        SKUListResponseBean skuListResponseBean = new SKUListResponseBean();
        when(endecaService.getSKUList(eatonPdh2SearchRequestWithFiltersAndStatusFilter(), resource)).thenReturn(skuListResponseBean);
        SKUListResponseBean responseBean = productStatusFacetHelper.callEndecaWithStatusFilterWhenModelsInSelectors(createFacetGroupBeansWithStatus(statusLabel), CommonConstants.PAGE_TYPE_PRODUCT_SUB_CATEGORY_PAGE);
        Assertions.assertSame(skuListResponseBean, responseBean, "must be same object returned from endeca");
    }

    @Test
    @DisplayName("When page type sub-category models and other selector in the selectors, new call is made to endeca with filter the status active")
    void testCorrectNewCallIsMadeToEndecaSubCategoryWithModelsAndOtherSelector() {
        when(slingHttpServletRequest.getRequestPathInfo()).thenReturn(requestPathInfo);
        when(requestPathInfo.getSelectors()).thenReturn(new String[]{MODELS, SORT_DESC});
        String statusLabel = STATUS_GROUP_ID_FOR_SUB_CATEGORY;
        ProductStatusFacetHelper productStatusFacetHelper = new ProductStatusFacetHelper(eatonPdh2SearchRequestWithFilters(), slingHttpServletRequest, resource, endecaService, facetsBeanWithSpecificStatusFacetsGroupId(statusLabel));
        SKUListResponseBean skuListResponseBean = new SKUListResponseBean();
        when(endecaService.getSKUList(eatonPdh2SearchRequestWithFiltersAndStatusFilter(), resource)).thenReturn(skuListResponseBean);
        SKUListResponseBean responseBean = productStatusFacetHelper.callEndecaWithStatusFilterWhenModelsInSelectors(createFacetGroupBeansWithStatus(statusLabel), CommonConstants.PAGE_TYPE_PRODUCT_SUB_CATEGORY_PAGE);
        Assertions.assertSame(skuListResponseBean, responseBean, "must be same object returned from endeca");
    }

    @Test
    @DisplayName("When page type sub-category models, nocache and other selector in the selectors, new call is made to endeca with filter the status active")
    void testCorrectNewCallIsMadeToEndecaSubCategoryWithModelsAndNocachAndOtherSelector() {
        when(slingHttpServletRequest.getRequestPathInfo()).thenReturn(requestPathInfo);
        when(requestPathInfo.getSelectors()).thenReturn(new String[]{MODELS, SORT_DESC, NOCACHE});
        String statusLabel = STATUS_GROUP_ID_FOR_SUB_CATEGORY;
        ProductStatusFacetHelper productStatusFacetHelper = new ProductStatusFacetHelper(eatonPdh2SearchRequestWithFilters(), slingHttpServletRequest, resource, endecaService, facetsBeanWithSpecificStatusFacetsGroupId(statusLabel));
        SKUListResponseBean skuListResponseBean = new SKUListResponseBean();
        when(endecaService.getSKUList(eatonPdh2SearchRequestWithFiltersAndStatusFilter(), resource)).thenReturn(skuListResponseBean);
        SKUListResponseBean responseBean = productStatusFacetHelper.callEndecaWithStatusFilterWhenModelsInSelectors(createFacetGroupBeansWithStatus(statusLabel), CommonConstants.PAGE_TYPE_PRODUCT_SUB_CATEGORY_PAGE);
        Assertions.assertSame(skuListResponseBean, responseBean, "must be same object returned from endeca");
    }

    @Test
    @DisplayName("When page type sub-category models, nocache and other selector in the selectors, new call is made to endeca with filter the status active")
    void testCorrectNewCallIsMadeToEndecaSubCategoryWithModelsAndAnonAndOtherSelector() {
        when(slingHttpServletRequest.getRequestPathInfo()).thenReturn(requestPathInfo);
        when(requestPathInfo.getSelectors()).thenReturn(new String[]{MODELS, SORT_DESC, ANON});
        String statusLabel = STATUS_GROUP_ID_FOR_SUB_CATEGORY;
        ProductStatusFacetHelper productStatusFacetHelper = new ProductStatusFacetHelper(eatonPdh2SearchRequestWithFilters(), slingHttpServletRequest, resource, endecaService, facetsBeanWithSpecificStatusFacetsGroupId(statusLabel));
        SKUListResponseBean skuListResponseBean = new SKUListResponseBean();
        when(endecaService.getSKUList(eatonPdh2SearchRequestWithFiltersAndStatusFilter(), resource)).thenReturn(skuListResponseBean);
        SKUListResponseBean responseBean = productStatusFacetHelper.callEndecaWithStatusFilterWhenModelsInSelectors(createFacetGroupBeansWithStatus(statusLabel), CommonConstants.PAGE_TYPE_PRODUCT_SUB_CATEGORY_PAGE);
        Assertions.assertSame(skuListResponseBean, responseBean, "must be same object returned from endeca");
    }

    @Test
    @DisplayName("When page type sub-category and facets in the selectors, NO new call is made to endeca with filter the status active")
    void testNoNewCallIsMadeToEndecaSubCategoryWithFacets() {
        when(slingHttpServletRequest.getRequestPathInfo()).thenReturn(requestPathInfo);
        when(requestPathInfo.getSelectors()).thenReturn(new String[]{MODELS, FACETS});
        String statusLabel = STATUS_GROUP_ID_FOR_SUB_CATEGORY;
        ProductStatusFacetHelper productStatusFacetHelper = new ProductStatusFacetHelper(eatonPdh2SearchRequestWithFilters(), slingHttpServletRequest, resource, endecaService, facetsBeanWithSpecificStatusFacetsGroupId(statusLabel));
        SKUListResponseBean responseBean = productStatusFacetHelper.callEndecaWithStatusFilterWhenModelsInSelectors(createFacetGroupBeansWithStatus(statusLabel), CommonConstants.PAGE_TYPE_PRODUCT_SUB_CATEGORY_PAGE);
        Assertions.assertNull(responseBean, "must be null");
        verify(endecaService, times(0)).getSKUList(eatonPdh2SearchRequestWithFiltersAndStatusFilter(), resource);
    }

    @Test
    @DisplayName("When page type sub-category and selectors empty, NO new call is made to endeca with filter the status active")
    void testNoNewCallIsMadeToEndecaSubCategoryEmptySelectors() {
        when(slingHttpServletRequest.getRequestPathInfo()).thenReturn(requestPathInfo);
        when(requestPathInfo.getSelectors()).thenReturn(new String[]{});
        String statusLabel = STATUS_GROUP_ID_FOR_SUB_CATEGORY;
        ProductStatusFacetHelper productStatusFacetHelper = new ProductStatusFacetHelper(eatonPdh2SearchRequestWithFilters(), slingHttpServletRequest, resource, endecaService, facetsBeanWithSpecificStatusFacetsGroupId(statusLabel));
        SKUListResponseBean responseBean = productStatusFacetHelper.callEndecaWithStatusFilterWhenModelsInSelectors(createFacetGroupBeansWithStatus(statusLabel), CommonConstants.PAGE_TYPE_PRODUCT_SUB_CATEGORY_PAGE);
        Assertions.assertNull(responseBean, "must be null");
        verify(endecaService, times(0)).getSKUList(eatonPdh2SearchRequestWithFiltersAndStatusFilter(), resource);
    }

}