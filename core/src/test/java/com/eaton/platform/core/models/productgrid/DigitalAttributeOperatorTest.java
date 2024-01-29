package com.eaton.platform.core.models.productgrid;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.SiteConfigModel;
import com.eaton.platform.core.models.eatonsiteconfig.EatonSiteConfigModel;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.integration.endeca.bean.subcategory.ProductFamilyBean;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextBuilder;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class DigitalAttributeOperatorTest {

    @Mock
    AdminService mockAdminService;

    @Mock
    SlingHttpServletRequest mockSlingRequest;

    DigitalAttributeOperator fixtureToTest;

    AemContext context = new AemContextBuilder().build();
    Resource pageResource;

    static String resourceURL = "/content/eaton/us/en-us/dev_team_pages/test-digital-product-62";
    static String jsonFile = "/digitalProduct.json";

    static String referrerURL="http://localhost:4502"+ resourceURL +".html";

    @BeforeEach
    protected void setup(){
        pageResource = context.load().json(jsonFile,resourceURL);
        context.request().setResource(pageResource);
        context.request().setHeader(CommonConstants.REFERER_URL,referrerURL);
        fixtureToTest = new DigitalAttributeOperator(context.request(),mockAdminService);
    }

    @Test
    @DisplayName("Test to ensure all digital attributes are set")
    void testToEnsureProductFamilyBeanDigitalAttributesAreSet(){
        fixtureToTest = new DigitalAttributeOperator(mockSlingRequest,mockAdminService);
        EatonSiteConfigModel mockEatonSiteConfigModel = mock(EatonSiteConfigModel.class);
        SiteConfigModel mockConfigModel = mock(SiteConfigModel.class);
        when(mockSlingRequest.adaptTo(EatonSiteConfigModel.class)).thenReturn(mockEatonSiteConfigModel);
        when(mockEatonSiteConfigModel.getSiteConfig()).thenReturn(mockConfigModel);
        when(mockConfigModel.getNoOfDays()).thenReturn(60);
        when(mockAdminService.getReadService()).thenReturn(context.resourceResolver());
        ProductFamilyBean bean = getProductFamilyBean();
        bean.setCompanyName("eaton");
        bean.setPartnerBadgeVisible(true);
        fixtureToTest.addDigitalAttributes(bean);
        Assertions.assertNotNull(bean.getCompanyName());
        Assertions.assertTrue(bean.getPartnerBadgeVisible());
    }

    @Test
    @DisplayName("Test to ensure if resource url doesn't resolve to a resource no attributes are set")
    void testToEnsureAttributesAreNotSetIfUrlCannotResolveToResource(){
        when(mockAdminService.getReadService()).thenReturn(context.resourceResolver());
        ProductFamilyBean bean = getProductFamilyBean();
        bean.setUrl("/content/someresource/cannot/be/resolve");
        fixtureToTest.addDigitalAttributes(bean);
        Assertions.assertNull(bean.getCompanyName());
        Assertions.assertFalse(bean.getPartnerBadgeVisible());
    }

   private ProductFamilyBean getProductFamilyBean(){
        ProductFamilyBean productFamilyBean = new ProductFamilyBean();
        productFamilyBean.setUrl(resourceURL);
        productFamilyBean.setPublishDate("January 1, 2024");
        return productFamilyBean;
    }
}