package com.eaton.platform.core.models;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.ProductFamilyPIMDetails;
import com.eaton.platform.core.services.EndecaRequestService;
import com.eaton.platform.core.services.ProductFamilyDetailService;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsBean;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsResponseBean;
import com.eaton.platform.integration.endeca.services.EndecaService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class FamilyInheritedParsysModelTest {

    public static final String[] SELECTORS = {"N272-F03-BK"};
    public static final String PRODUCT_FAMILY_PAGE_PATH_WITH_PAR = "/content/eaton/language-masters/en-us/catalog/tripp-lite/cat8-network-cable";
    public static final String PRODUCT_FAMILY_PAGE_PATH_WITH_PARS = "/content/eaton/language-masters/en-us/catalog/tripp-lite/cat8-network-cable-with-pars";
    public static final String PRODUCT_FAMILY_PAGE_PATH_WITHOUT_FAMILY_INHERITED = "/content/eaton/language-masters/en-us/catalog/tripp-lite/cat8-network-cable-without-familyInheritted";
    public static final String INHRITED_PARSYS_PATH_WITH_PAR = "/jcr:content/root/responsivegrid/product_tabs/content-tab-1/family_inherited_par/familyInherited";
    public static final String INHERITED_PARSYS_PATH_WITH_PARS = "/jcr:content/root/responsivegrid/product_tabs/content-tab-1/family_inherited_pars/familyInherited";
    @InjectMocks
    FamilyInheritedParsysModel familyInheritedParsysModel = new FamilyInheritedParsysModel();
    @Mock
    EndecaRequestService endecaRequestService;
    @Mock
    EndecaService endecaService;
    @Mock
    ProductFamilyDetailService productFamilyDetailService;
    @Mock
    SlingHttpServletRequest request;
    @Mock
    EndecaServiceRequestBean endecaServiceRequestBean;
    @Mock
    SKUDetailsResponseBean skuDetailsResponseBean;
    @Mock
    RequestPathInfo requestPathInfo;
    SKUDetailsBean skuDetailsBean = new SKUDetailsBean();
    @Mock
    ProductFamilyPIMDetails productFamilyDetails;
    @Mock
    Page currentPage;
    Resource skuPageResource;
    Resource familyPageResource;
    private ResourceResolver resourceResolver;

    @BeforeEach
    void setUp(AemContext aemContext) {
        aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("inheritedParsys-family-page-with-par.json"))
                , PRODUCT_FAMILY_PAGE_PATH_WITH_PAR);
        aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("inheritedParsys-family-page-with-pars.json"))
                , PRODUCT_FAMILY_PAGE_PATH_WITH_PARS);
        aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("inheritedParsys-family-page-without-inherited-part.json"))
                , PRODUCT_FAMILY_PAGE_PATH_WITHOUT_FAMILY_INHERITED);
        aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("inheritedParsys-sku-page.json"))
                , "/content/eaton/language-masters/en-us/skuPage");
        when(request.getRequestPathInfo()).thenReturn(requestPathInfo);
        when(requestPathInfo.getSelectors()).thenReturn(SELECTORS);
        resourceResolver = aemContext.resourceResolver();
        skuPageResource = resourceResolver.getResource("/content/eaton/language-masters/en-us/skuPage/jcr:content");
        familyPageResource = resourceResolver.getResource("/content/eaton/language-masters/en-us/catalog/tripp-lite/cat8-network-cable/jcr:content");
    }

    @Test
    void testThatInitPreparesTheProductFamilyDetailsCorrectlyIfSkuPage() {
        when(currentPage.getContentResource()).thenReturn(skuPageResource);
        when(endecaRequestService.getEndecaRequestBean(currentPage, SELECTORS, null)).thenReturn(endecaServiceRequestBean);
        when(endecaService.getSKUDetails(endecaServiceRequestBean)).thenReturn(skuDetailsResponseBean);
        when(skuDetailsResponseBean.getSkuDetails()).thenReturn(skuDetailsBean);
        when(productFamilyDetailService.getProductFamilyPIMDetailsBean(skuDetailsBean, currentPage)).thenReturn(productFamilyDetails);

        familyInheritedParsysModel.init();

        verify(productFamilyDetailService).getProductFamilyPIMDetailsBean(skuDetailsBean, currentPage);

    }

    @Test
    void testThatInitDoesntPreparePimDetailsIfFamilyPage() {
        reset(request, requestPathInfo);
        when(currentPage.getContentResource()).thenReturn(familyPageResource);

        familyInheritedParsysModel.init();

        verify(productFamilyDetailService, times(0)).getProductFamilyPIMDetailsBean(skuDetailsBean, currentPage);

    }

    @Test
    void testGetPathReturnsFamilyForFamilyPage() {
        reset(request, requestPathInfo);
        when(currentPage.getContentResource()).thenReturn(familyPageResource);

        assertEquals("family", familyInheritedParsysModel.getPath(), "should be family");

    }
    @Test
    void testGetPathReturnsFamilyForSkuPageWithPar() {
        reset(request, requestPathInfo);
        when(currentPage.getContentResource()).thenReturn(skuPageResource);
        when(productFamilyDetails.getProductFamilyAEMPath()).thenReturn(PRODUCT_FAMILY_PAGE_PATH_WITH_PAR);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        assertEquals(PRODUCT_FAMILY_PAGE_PATH_WITH_PAR.concat(INHRITED_PARSYS_PATH_WITH_PAR), familyInheritedParsysModel.getPath(), "should be the path from pimfamily appended with parsys path");
    }

    @Test
    void testGetPathReturnsFamilyForSkuPageWithPars() {
        reset(request, requestPathInfo);
        when(currentPage.getContentResource()).thenReturn(skuPageResource);
        when(productFamilyDetails.getProductFamilyAEMPath()).thenReturn(PRODUCT_FAMILY_PAGE_PATH_WITH_PARS);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        assertEquals(PRODUCT_FAMILY_PAGE_PATH_WITH_PARS.concat(INHERITED_PARSYS_PATH_WITH_PARS), familyInheritedParsysModel.getPath(), "should be the path from pimfamily appended with parsys path");
    }

    @Test
    void testLazyLoadOfPath() {
        reset(request, requestPathInfo);
        when(currentPage.getContentResource()).thenReturn(familyPageResource);

        for (int i = 0; i < 5; i++) {
            familyInheritedParsysModel.getPath();
        }

        verify(currentPage, times(1)).getContentResource();

    }

    @Test
    void testThatPathIsReturningNullAfterInitWherePimDetailsAreNotFound() {
        when(currentPage.getContentResource()).thenReturn(skuPageResource);
        when(endecaRequestService.getEndecaRequestBean(currentPage, SELECTORS, null)).thenReturn(endecaServiceRequestBean);
        when(endecaService.getSKUDetails(endecaServiceRequestBean)).thenReturn(skuDetailsResponseBean);
        when(skuDetailsResponseBean.getSkuDetails()).thenReturn(skuDetailsBean);
        when(productFamilyDetailService.getProductFamilyPIMDetailsBean(skuDetailsBean, currentPage)).thenReturn(null);

        familyInheritedParsysModel.init();

        Assertions.assertNull(familyInheritedParsysModel.getPath(), "should be null");

    }

    @Test
    void testGetPathWhenFamilyPathOnDetailsNull() {
        reset(request, requestPathInfo);
        when(currentPage.getContentResource()).thenReturn(skuPageResource);
        when(productFamilyDetails.getProductFamilyAEMPath()).thenReturn(null);
        assertEquals("family", familyInheritedParsysModel.getPath(), "should be family");

        for (int i = 0; i < 5; i++) {
            familyInheritedParsysModel.getPath();
        }

        verify(currentPage, times(1)).getContentResource();
    }
    @Test
    void testGetPathWhenFamilyPathYieldsNullResource() {
        reset(request, requestPathInfo);
        when(currentPage.getContentResource()).thenReturn(skuPageResource);
        when(productFamilyDetails.getProductFamilyAEMPath()).thenReturn("not-existing-resource");
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        assertEquals("family", familyInheritedParsysModel.getPath(), "should be family");

        for (int i = 0; i < 5; i++) {
            familyInheritedParsysModel.getPath();
        }

        verify(currentPage, times(1)).getContentResource();
    }

    @Test
    void testGetPathWhenFamilyPathYieldsResourceWithoutFamilyInheritedPart() {
        reset(request, requestPathInfo);
        when(currentPage.getContentResource()).thenReturn(skuPageResource);
        when(productFamilyDetails.getProductFamilyAEMPath()).thenReturn(PRODUCT_FAMILY_PAGE_PATH_WITHOUT_FAMILY_INHERITED);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        assertEquals("family", familyInheritedParsysModel.getPath(), "should be family");

        for (int i = 0; i < 5; i++) {
            familyInheritedParsysModel.getPath();
        }

        verify(currentPage, times(1)).getContentResource();
    }
}