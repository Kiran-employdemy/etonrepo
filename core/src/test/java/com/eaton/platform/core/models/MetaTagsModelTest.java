package com.eaton.platform.core.models;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.CountryLanguageCodeBean;
import com.eaton.platform.core.services.MetaTagsLinkDomainService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.PersistenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class MetaTagsModelTest {
    private AemContext ctx = new AemContext();

    @Spy
    private List<CountryLanguageCodeBean> hreflangValues = new ArrayList<>();

    @Mock
    private Page currentPage;

    @Mock
    private MetaTagsLinkDomainService metaTagsLinkDomainService;

    @InjectMocks
    private MetaTagsModel model;
    private Page page;
    private Page countryPage;

    @BeforeEach
    public void setUp() {
        countryPage = ctx.create().page("/content/eaton/us/en-us", "/test", Map.of("hrefLangCode", "us-en"));
        page = ctx.create().page("/content/eaton/us/en-us/test");
    }

    @Test
    @DisplayName("test that hreflang is present in the case when page is valid")
    public void testCreateHreflangItemForPage() {
        when(currentPage.getDepth()).thenReturn(5);
        when(currentPage.getPath()).thenReturn("/content/eaton/us/en-us/test");
        model.populateHrefLangList(ctx.resourceResolver(), new CountryLanguageCodeBean(), "us", "en-us");

        assertEquals(1, hreflangValues.size(), "One item should be present in the list");
        assertEquals("/content/eaton/us/en-us/test", hreflangValues.get(0).getPageUrl(), "URL should be as expected");
    }

    @Test
    @DisplayName("test that hreflang is not created for the page without jcr:content node")
    public void testIgnoreCreateHreflangItemForPageWithoutContent() throws PersistenceException {
        ctx.resourceResolver().delete(page.getContentResource());
        ctx.resourceResolver().commit();
        when(currentPage.getDepth()).thenReturn(5);
        when(currentPage.getPath()).thenReturn("/content/eaton/us/en-us/test");
        model.populateHrefLangList(ctx.resourceResolver(), new CountryLanguageCodeBean(), "us", "en-us");

        assertEquals(0, hreflangValues.size(), "List of hreflang must be empty");
    }

    @Test
    @DisplayName("test that hreflang is present in the case when country page is valid")
    public void testCreateHreflangItemForCountryPage() {
        when(currentPage.getDepth()).thenReturn(4);
        when(currentPage.getPath()).thenReturn("/content/eaton/us/en-us");
        model.populateHrefLangList(ctx.resourceResolver(), new CountryLanguageCodeBean(), "us", "en-us");

        assertEquals(1, hreflangValues.size(), "One item should be present in the list");
        assertEquals("/content/eaton/us/en-us", hreflangValues.get(0).getPageUrl(), "URL should be as expected");
    }

    @Test
    @DisplayName("test that hreflang is not created for the country page without jcr:content node")
    public void testIgnoreCreateHreflangItemForCountryPageWithoutContent() throws PersistenceException {
        ctx.resourceResolver().delete(countryPage.getContentResource());
        ctx.resourceResolver().commit();
        when(currentPage.getDepth()).thenReturn(4);
        when(currentPage.getPath()).thenReturn("/content/eaton/us/en-us");
        model.populateHrefLangList(ctx.resourceResolver(), new CountryLanguageCodeBean(), "us", "en-us");

        assertEquals(0, hreflangValues.size(), "List of hreflang must be empty");
    }
}