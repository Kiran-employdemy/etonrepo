package com.eaton.platform.core.bean.builders.product.impl;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.ProductFamilyPIMDetails;
import com.eaton.platform.core.bean.builders.product.exception.MissingFillingBeanException;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsBean;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.StringUtils;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Locale;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class PfpTaxonomyGroupFillerTest {
    @Mock
    Page mockPage;

    @Mock
    SKUDetailsBean mockSkuData;

    @Mock
    ResourceResolver mockResourceResolver;

    @Mock
    Resource mockPimResource;

    @Mock
    Resource mockFamilyPageResource;

    final AemContext context = new AemContext();

    @BeforeEach
    void setUp(){
        context.addModelsForPackage("com.eaton.platform.core.models.pim");
    }

    private String familyAEMPath="/content/eaton/some/path";

    @Test
    @DisplayName("Ensure Bean Filler fill should contain taxonomy group must have group name and taxonomy list")
    void ensureBeanAreFillWithCorrectInformation() throws MissingFillingBeanException {

        Resource taxonomyResource = context.load().json("/pim/sampletaxonomyattr.json","/var/commerce/products/eaton/en_us/_66803179/taxonomyAttributes");
        ProductFamilyPIMDetails details = new ProductFamilyPIMDetails();
        details.setProductFamilyAEMPath(familyAEMPath);
        when(mockResourceResolver.getResource(ArgumentMatchers.contains("/some/path"))).thenReturn(mockFamilyPageResource);
        when(mockFamilyPageResource.getChild(anyString())).thenReturn(mockPimResource);
        when(mockResourceResolver.getResource(ArgumentMatchers.contains("some-extension-id"))).thenReturn(mockPimResource);
        when(mockPimResource.getChild(anyString())).thenReturn(taxonomyResource);

        when(mockPage.getLanguage(anyBoolean())).thenReturn(Locale.ENGLISH);
        when(mockSkuData.getExtensionId()).thenReturn("some-extension-id");
        PfpTaxonomyGroupFiller filler = new PfpTaxonomyGroupFiller(mockPage,mockResourceResolver,mockSkuData);
        filler.fill(details);
        Assertions.assertNotNull(details.getTaxonomyAttributeGroupList());
        Assertions.assertFalse(details.getTaxonomyAttributeGroupList().isEmpty());
        details.getTaxonomyAttributeGroupList().forEach(groupBean ->{
            Assertions.assertNotNull(groupBean);
            Assertions.assertFalse(StringUtils.isBlank(groupBean.getGroupName()));
            Assertions.assertFalse(groupBean.getAttributeList().isEmpty());
        });
    }

    @Test
    @DisplayName("If Filling bean is null exception should be thrown")
    void ensureIfBeanIsNullExceptionIsThrown(){

        PfpTaxonomyGroupFiller filler = new PfpTaxonomyGroupFiller(mockPage,mockResourceResolver,mockSkuData);
        Exception exception = Assertions.assertThrows(MissingFillingBeanException.class,()->filler.fill(null));
        Assertions.assertNotNull(exception);
        Assertions.assertTrue(exception instanceof MissingFillingBeanException);
        Assertions.assertTrue(exception.getMessage().contains("Filling bean cannot be null"));
    }
}
