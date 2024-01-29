package com.eaton.platform.core.webtools.servlets.freesample;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;

import org.apache.commons.io.IOUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.ResourceResolver;
import org.json.JSONException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.webtools.services.WebtoolsServiceConfiguration;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@ExtendWith({ MockitoExtension.class })
public class FreeSampleStockAvailabilityServletTest {

   private static final String CATALOG_ID = "1234";

   @Mock
   AdminService adminService;

   @Mock
   SlingHttpServletRequest request;

   @Mock
   SlingHttpServletResponse response;

   @Mock
   PrintWriter writer;

   @Mock
   ResourceResolver resourceResolver;

   @Mock
   WebtoolsServiceConfiguration webtoolsService;

   @Mock
   RequestPathInfo requestPathInfo;

   @InjectMocks
   FreeSampleStockAvailabilityServlet freeSampleStockAvailabilityServlet = new FreeSampleStockAvailabilityServlet();

   @BeforeEach
    void setup() throws IOException{
    	when(request.getRequestPathInfo()).thenReturn(requestPathInfo);
    	when(response.getWriter()).thenReturn(writer);

    }

   @Test
   public void testPathInfoSelectorsNotEmpty() throws JsonIOException, ServletException, IOException, JSONException {
      InputStream productStream = this.getClass().getResourceAsStream("MyFreeSampleStockAvailability.json");
      JsonObject expectedResponseFromApigee = new JsonParser()
            .parse(IOUtils.toString(productStream, StandardCharsets.UTF_8)).getAsJsonObject();
      String[] selectors = { "catalogId$" + CATALOG_ID };
      when(requestPathInfo.getSelectors()).thenReturn(selectors);

      when(webtoolsService.getFreeSampleStockAvailability(CATALOG_ID)).thenReturn(expectedResponseFromApigee);
      freeSampleStockAvailabilityServlet.doGet(request, response);
      Mockito.verify(writer).write(expectedResponseFromApigee.toString());

   }

   @Test
   public void testPathInfoSelectorsEmpty() throws JsonIOException, ServletException, IOException, JSONException {

      String[] selectors = {};
      when(requestPathInfo.getSelectors()).thenReturn(selectors);
      freeSampleStockAvailabilityServlet.doGet(request, response);
      Mockito.verify(writer).write("{}");

   }

}
