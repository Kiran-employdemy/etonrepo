package com.eaton.platform.integration.auth.filters;

import io.wcm.testing.mock.aem.junit5.AemContext;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.settings.SlingSettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.Rule;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import java.io.IOException;
import static org.junit.Assert.assertEquals;
import java.util.TreeSet;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class ACSCommonsReportsFilterTest {
    @Rule
    public final AemContext aemContext = new AemContext();
    @InjectMocks
    ACSCommonsReportsFilter aCSCommonsReportsFilter = new ACSCommonsReportsFilter();
    @Mock
    SlingHttpServletRequest slingHttpServletRequest;
    @Mock
    SlingHttpServletResponse slingHttpServletResponse;
    @Mock
    FilterChain filterChain;
    Resource resource;
    @Mock
    SlingSettingsService slingSettingsService;
    @Mock
    ComponentContext context;
    private static final String KEYWORD = "keyword";
    private static final String TYPE = "type";
    Set<String> mockRunModes = new TreeSet<String>();
    Resource componentReportResource;
    
    @BeforeEach
    public void setUp(){
        aemContext.load().json(Objects.requireNonNull(getClass().getResourceAsStream("assetReference.json"))
                , "/var/acs-commons/reports/reference-report-by-keyword-or-path");
        resource = aemContext.resourceResolver().getResource("/var/acs-commons/reports/reference-report-by-keyword-or-path");
        when(slingHttpServletRequest.getResource()).thenReturn(resource);
    }

    @Test
    public void testActivate() {
        Mockito.reset(slingHttpServletRequest);
        mockRunModes.add("author");
        when(slingSettingsService.getRunModes()).thenReturn(mockRunModes);
        aCSCommonsReportsFilter.activate(context);
    }

    @Test
    public void testDoFilterPassCorrectParam() throws IOException, ServletException {
        when(slingHttpServletRequest.getParameter(KEYWORD)).thenReturn(KEYWORD);
        when(slingHttpServletRequest.getParameter(TYPE)).thenReturn(TYPE);
        aCSCommonsReportsFilter.doFilter(slingHttpServletRequest, slingHttpServletResponse,filterChain);
        verify(slingHttpServletRequest, atLeast(1)).getParameter(KEYWORD);
        verify(slingHttpServletRequest, atLeast(1)).getParameter(TYPE);
    }

    @Test
    public void testDoFilterPassNullParam() throws IOException, ServletException {
        when(slingHttpServletRequest.getParameter(KEYWORD)).thenReturn(null);
        when(slingHttpServletRequest.getParameter(TYPE)).thenReturn(null);
        aCSCommonsReportsFilter.doFilter(slingHttpServletRequest, slingHttpServletResponse,filterChain);
    }

    @Test
    public void testDoFilter() throws IOException, ServletException {
        when(slingHttpServletRequest.getParameter(KEYWORD)).thenReturn(KEYWORD);
        when(slingHttpServletRequest.getParameter(TYPE)).thenReturn(TYPE);
        aCSCommonsReportsFilter.doFilter(slingHttpServletRequest, slingHttpServletResponse,filterChain);
        assertEquals(200, aemContext.response().getStatus());
    }
    @Test
    public void testWithDiffrentReport() throws IOException, ServletException {
        aemContext.load().json(Objects.requireNonNull(getClass().getResourceAsStream("componentReport.json"))
                , "/var/acs-commons/reports/sample-components-report");
        componentReportResource = aemContext.resourceResolver().getResource("/var/acs-commons/reports/sample-components-report");
        when(slingHttpServletRequest.getResource()).thenReturn(componentReportResource);
        when(slingHttpServletRequest.getParameter(KEYWORD)).thenReturn(KEYWORD);
        when(slingHttpServletRequest.getParameter(TYPE)).thenReturn(TYPE);
        aCSCommonsReportsFilter.doFilter(slingHttpServletRequest, slingHttpServletResponse,filterChain);
        assertEquals(200, aemContext.response().getStatus());
    }
}