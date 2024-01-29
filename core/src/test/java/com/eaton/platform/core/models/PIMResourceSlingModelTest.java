package com.eaton.platform.core.models;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Iterator;
import java.util.Locale;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.servlethelpers.MockSlingHttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.util.CommonUtil;
import junitx.util.PrivateAccessor;

@ExtendWith(value = { MockitoExtension.class })
public class PIMResourceSlingModelTest {
    @InjectMocks
    PIMResourceSlingModel pimResourceSlingModel;

    String primaryCTAURL = "/content/eaton/mx/es-mx/forms/crouse-hinds-contact-me-form.html";

    String UpdateCTAURL = "/content/eaton/mx/es-mx/forms/crouse-hinds-contact-me-form.html";
    String UpdateCTAURLToLocale = "/content/eaton/cr/es-mx/forms/crouse-hinds-contact-me-form.html";

    @Mock
    private Page currentPage;

    String primaryCTALabel = "Contácteme sobre este producto";
    @Mock
    Resource primaryctas;

    @Mock
    private Iterable<Resource> primaryctasIterable;
    
    @Mock
    private Iterator<Resource> primaryctasIterator;

    Resource primaryCTAResource;

    @Mock
    Resource commonPIMResource;

    @Mock
    ResourceResolver adminServiceReadService;

    @Mock
    AdminService adminService;

    @Mock
    ValueMap valueMap;

    Logger logger = LoggerFactory.getLogger(getClass());

    MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(adminServiceReadService);

    String primaryCTAMakeGlobal;
    String PRIMARY_CTA_LABEL_PROPERTY = "primaryCTALabel";
    String PRIMARY_CTA_URL_PROPERTY = "primaryCTAURL";
    String PRIMARY_CTAS = "primaryctas";
    String COUNTRY_MAXICO = "mx";
    String COUNTRY_SPAIN = "es";
    String COUNTRY_COSTA_RICA = "cr";

    @Test
    public void testGetPrimaryCTAURL(){
    	when(primaryctas.getChildren()).thenReturn(primaryctasIterable);    
        when(primaryctasIterable.iterator()).thenReturn(primaryctasIterator); 
    	when( primaryctasIterator.hasNext()).thenReturn(true).thenReturn(false);
        when( primaryctasIterator.next()).thenReturn(primaryctas);
        when(primaryctas.getName()).thenReturn(PRIMARY_CTAS);
    	when( primaryctas.getValueMap()).thenReturn(valueMap);
         try {
        PrivateAccessor.setField(pimResourceSlingModel, PRIMARY_CTA_URL_PROPERTY, primaryCTAURL);
    } catch (NoSuchFieldException  e) {
        logger.error("An error occurred while checking for primaryCTA: {}", e.getMessage());
        e.printStackTrace();
    }
    	String[] country = {COUNTRY_SPAIN,COUNTRY_MAXICO};
    	valueMap.put("country",country);
    	when(valueMap.get("country", EMPTY)).thenReturn(COUNTRY_SPAIN);
        pimResourceSlingModel.findPrimaryCTAResource();
        pimResourceSlingModel.init();
       try {
        PrivateAccessor.setField(pimResourceSlingModel, "primaryCTAMakeGlobal", primaryCTAMakeGlobal);
        PrivateAccessor.getField(pimResourceSlingModel,"primaryCTAMakeGlobal");
    } catch (NoSuchFieldException  e) {
        logger.error("An error occurred while checking for primaryCTAMakeGlobal checkbox is unchecked: {}", e.getMessage());
        e.printStackTrace();
    }
       when(currentPage.getPath()).thenReturn("language-masters");
       valueMap.put(PRIMARY_CTA_URL_PROPERTY, PRIMARY_CTA_URL_PROPERTY);
       Locale locale = new Locale("es_MX");
       when(currentPage.getLanguage(Boolean.TRUE)).thenReturn(locale);
       when(currentPage.getAbsoluteParent(2)).thenReturn(currentPage);
       when(currentPage.getName()).thenReturn(COUNTRY_COSTA_RICA);
       String url=CommonUtil.updateCTAURLString(primaryCTAURL, currentPage, adminServiceReadService);
       pimResourceSlingModel.getPrimaryCTAURL(currentPage);
       assertEquals(UpdateCTAURL,url);

    }

    @Test
    public void testprimaryCTAMakeGlobalIsNotNull() {
        String primaryCTAMakeGlobal="false";
        when(primaryctas.getChildren()).thenReturn(primaryctasIterable);    
        when(primaryctasIterable.iterator()).thenReturn(primaryctasIterator); 
    	when( primaryctasIterator.hasNext()).thenReturn(true).thenReturn(false);
        when( primaryctasIterator.next()).thenReturn(primaryctas);
        when(primaryctas.getName()).thenReturn(PRIMARY_CTAS);
    	when( primaryctas.getValueMap()).thenReturn(valueMap);
         try {
        PrivateAccessor.setField(pimResourceSlingModel, PRIMARY_CTA_URL_PROPERTY, primaryCTAURL);
    } catch (NoSuchFieldException  e) {
        logger.error("An error occurred while checking for primaryCTAMakeGlobal: {}", e.getMessage());
        e.printStackTrace();
    }
    	String[] country = {COUNTRY_SPAIN,COUNTRY_MAXICO};
    	valueMap.put("country",country);
    	when(valueMap.get("country", EMPTY)).thenReturn(COUNTRY_SPAIN);
        pimResourceSlingModel.findPrimaryCTAResource();
        pimResourceSlingModel.init();
       try {
        PrivateAccessor.setField(pimResourceSlingModel, "primaryCTAMakeGlobal", primaryCTAMakeGlobal);
        PrivateAccessor.getField(pimResourceSlingModel,"primaryCTAMakeGlobal");
    } catch (NoSuchFieldException  e) {
        logger.error("An error occurred while checking for primaryCTAMakeGlobal checkbox is checked: {}", e.getMessage());
        e.printStackTrace();
    }
       when(currentPage.getPath()).thenReturn("language-masters");
       valueMap.put(PRIMARY_CTA_URL_PROPERTY,PRIMARY_CTA_URL_PROPERTY);
       Locale locale = new Locale("es_MX");
       when(currentPage.getLanguage(Boolean.TRUE)).thenReturn(locale);
       when(currentPage.getAbsoluteParent(2)).thenReturn(currentPage);
       when(currentPage.getName()).thenReturn(COUNTRY_COSTA_RICA);
       String linkCountry= primaryCTAURL.substring(15, 17);
       primaryCTAURL = primaryCTAURL.replaceFirst(linkCountry, COUNTRY_COSTA_RICA);
       when(adminService.getReadService()).thenReturn(adminServiceReadService);
       String url=CommonUtil.updateCTAURLString(primaryCTAURL, currentPage, adminServiceReadService);
       pimResourceSlingModel.getPrimaryCTAURL(currentPage);
       assertEquals((currentPage.getName()),COUNTRY_COSTA_RICA);
       assertEquals(UpdateCTAURLToLocale,url);
    }

}
