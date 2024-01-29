package com.eaton.platform.core.bean.builders.product.impl;

import com.adobe.cq.commerce.common.ValueMapDecorator;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.ProductFamilyPIMDetails;
import com.eaton.platform.core.bean.builders.product.exception.MissingFillingBeanException;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsBean;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.StringUtils;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Locale;

import static com.eaton.platform.core.constants.CommonConstants.PIM_PRIMARY_SUB_CATEGORY;
import static javax.jcr.query.Query.JCR_SQL2;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class ProductFamilyPageFillerTest {

    final AemContext context = new AemContext();

    @Mock
    Page mockPage;

    @Mock
    ResourceResolver mockResourceResolver;

    @Mock
    SKUDetailsBean mockSkuData;

    @Mock
    Resource pimResource;

    @Test
    @DisplayName("ensure filler does fill product family path and sub-category")
    void ensureProductFamilyPathAndSubCategoryIsNotEmpty() throws MissingFillingBeanException {
        Page languagePage = context.create().page("/content/eaton/en");
        Page productFamilyPage = context.create().page("/content/eaton/en/onefamilypage",
                "/sometemplate",
                Collections.singletonMap(PIM_PRIMARY_SUB_CATEGORY,"sub-cat"));
        Resource resource = context.create().resource("/var/commerce/someresource");
        ProductFamilyPIMDetails details = new ProductFamilyPIMDetails();
        when(mockPage.getLanguage(anyBoolean())).thenReturn(Locale.ENGLISH);
        when(mockPage.getAbsoluteParent(anyInt())).thenReturn(languagePage);
        when(mockSkuData.getExtensionId()).thenReturn("any-ext");
        when(mockResourceResolver.getResource(contains("any-ext"))).thenReturn(pimResource);
        when(pimResource.getValueMap()).thenReturn(new ValueMapDecorator(Collections.singletonMap("productFamilyPage","/content/familypage")));
        when(mockResourceResolver.findResources(contains("SELECT * FROM [cq:Page] AS S WHERE NAME()"),matches(JCR_SQL2))).thenReturn(Collections.singletonList(resource).iterator());
        when(mockResourceResolver.resolve(contains("familypage"))).thenReturn(productFamilyPage.adaptTo(Resource.class));
        ProductFamilyPageFiller filler = new ProductFamilyPageFiller(mockPage,mockResourceResolver,mockSkuData);
        filler.fill(details);
        Assertions.assertTrue(StringUtils.isNotBlank(details.getProductFamilyAEMPath()));
        Assertions.assertTrue(StringUtils.isNotBlank(details.getPrimarySubCategory()));
        Assertions.assertTrue(details.getPrimarySubCategory().equals("sub-cat"));
        verify(mockResourceResolver,times(1)).findResources(anyString(),anyString());
    }

    @Test
    @DisplayName("If Filling bean is null exception should be thrown")
    void ensureIfBeanIsNullExceptionIsThrown(){

        ProductFamilyPageFiller filler = new ProductFamilyPageFiller(mockPage,mockResourceResolver,mockSkuData);
        Exception exception = Assertions.assertThrows(MissingFillingBeanException.class,()->filler.fill(null));
        Assertions.assertNotNull(exception);
        Assertions.assertTrue(exception instanceof MissingFillingBeanException);
        Assertions.assertTrue(exception.getMessage().contains("Filling bean cannot be null"));
    }

    @Test
    @DisplayName("Ensure Bean Filler with bad/null SKUData Detail will return an empty bean instead an exception thrown")
    void testToEnsureIfBadSkuDataIsPassWillThrowException() throws MissingFillingBeanException {
        when(mockPage.getLanguage(anyBoolean())).thenReturn(Locale.ENGLISH);
        ProductFamilyPageFiller filler = new ProductFamilyPageFiller(mockPage,mockResourceResolver,null);
        ProductFamilyPIMDetails bean = new ProductFamilyPIMDetails();
        filler.fill(bean);
        Assertions.assertNotNull(bean);
        Assertions.assertNull(bean.getProductName());
        Assertions.assertNull(bean.getSecondaryLinksList());
        Assertions.assertNull(bean.getTaxonomyAttributeGroupList());
    }

}