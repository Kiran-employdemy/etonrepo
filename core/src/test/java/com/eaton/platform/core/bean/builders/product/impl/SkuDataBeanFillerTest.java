package com.eaton.platform.core.bean.builders.product.impl;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.ProductFamilyPIMDetails;
import com.eaton.platform.core.bean.builders.product.BeanFiller;
import com.eaton.platform.core.bean.builders.product.exception.MissingFillingBeanException;
import com.eaton.platform.core.models.SiteResourceSlingModel;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.EatonSiteConfigService;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsBean;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsResponseBean;
import com.eaton.platform.integration.endeca.helpers.EndecaServiceHelper;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class SkuDataBeanFillerTest {

    @Mock
    AdminService mockAdminService;

    @Mock
    EatonSiteConfigService mockEatonSiteConfig;

    @Mock
    SiteResourceSlingModel mockSiteResourceSlingModel;

    private String currentPagePath = "/content/eaton/us/en-us/skuPage";
    private String pimPath ="/var/commerce/products/eaton/en_us/_124206398";
    private String pdhParentPath = "/var/commerce/pdh/product-family/en_us/eaton/crouse-hinds-series/none/6417832";
    private String pdhNodeName="124206398";

    private Resource skuPageResource;

    private AemContext aemContext = new AemContext();

    @BeforeEach
    void setup(){
        aemContext.addModelsForPackage("com.eaton.platform.core.models");
        aemContext.registerService(AdminService.class,mockAdminService);
        Page countryPage = aemContext.create().page("/content/eaton/us");
        Page countryLangPage = aemContext.create().page(countryPage,"en-us");
        skuPageResource = aemContext.load().json("/page/skupage.json",countryLangPage.adaptTo(Resource.class),currentPagePath);
        aemContext.load().json("/pim/124206398.json",pimPath);
        Resource pdhParent =  aemContext.load().json("/pdh/6417832.json",pdhParentPath);
        aemContext.load().json("/pdh/124206398.json",pdhParent,pdhNodeName);
    }

    @Test
    @DisplayName("Ensure Bean Filler with SKUData Contains Secondary links and product name")
    void testToEnsureFillBeanReturnWithSecondaryLinks() throws IOException, MissingFillingBeanException {
            when(mockAdminService.getReadService()).thenReturn(aemContext.resourceResolver());
            when(mockSiteResourceSlingModel.getOverridePDHData()).thenReturn("true");
            when(mockEatonSiteConfig.getSiteConfig(any(Page.class))).thenReturn(Optional.of(mockSiteResourceSlingModel));
            ProductFamilyPIMDetails productFamilyPIMDetails = new ProductFamilyPIMDetails();
            BeanFiller beanFiller = getFixture();
            // ensure beanFiller is not null
            Assertions.assertNotNull(beanFiller);
            beanFiller.fill(productFamilyPIMDetails);
            Assertions.assertNotNull(productFamilyPIMDetails);
            Assertions.assertFalse(productFamilyPIMDetails.getSecondaryLinksList().isEmpty());
            Assertions.assertEquals("Champ VMVL LED Hazardous Area Light Fixtures", productFamilyPIMDetails.getProductName());
    }

    @Test
    @DisplayName("Ensure Bean Filler with bad/null SKUData Detail will return an empty Bean instead of null object or exception thrown")
    void testToEnsureIfBadSkuDataIsPassWillThrowException() throws MissingFillingBeanException {
        BeanFiller filler = new SkuDataBeanFiller(null,skuPageResource.adaptTo(Page.class),aemContext.resourceResolver(),mockEatonSiteConfig);
        ProductFamilyPIMDetails bean = new ProductFamilyPIMDetails();
        filler.fill(bean);
        Assertions.assertNotNull(bean);
        Assertions.assertNull(bean.getProductName());
        Assertions.assertNull(bean.getSecondaryLinksList());
    }

    @Test
    @DisplayName("Exception should be thrown when no filling bean has been pass")
    void testToEnsureExceptionIsThrownWhenNoFillingBeanHasBeenPassed(){
        BeanFiller filler = new SkuDataBeanFiller(new SKUDetailsBean(),skuPageResource.adaptTo(Page.class),aemContext.resourceResolver(),mockEatonSiteConfig);
        Exception exception = Assertions.assertThrows(MissingFillingBeanException.class,()->{
            // an exception should have been thrown because sku data is null
            filler.fill(null);
        });
        Assertions.assertNotNull(exception);
        Assertions.assertTrue(exception instanceof MissingFillingBeanException);
        Assertions.assertTrue(exception.getMessage().contains("Filling bean cannot be null"));

    }


    private BeanFiller getFixture() throws IOException {
      InputStream stream = getClass().getResourceAsStream("/skudetailendecaresponse.json");
      String skuDataValue = IOUtils.toString(stream, StandardCharsets.UTF_8);
        SKUDetailsResponseBean resp = new EndecaServiceHelper().convertSKUDetailsJSONTOBeanList(skuDataValue);
        if(null != resp){
            return new SkuDataBeanFiller(resp.getSkuResponse().getSkuDetails(), skuPageResource.adaptTo(Page.class),aemContext.resourceResolver(),mockEatonSiteConfig);
        }
        return null;
    }


}
