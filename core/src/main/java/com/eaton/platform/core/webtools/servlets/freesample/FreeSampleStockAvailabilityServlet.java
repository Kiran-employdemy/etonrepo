package com.eaton.platform.core.webtools.servlets.freesample;

import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.webtools.constants.WebtoolsConstants;
import com.eaton.platform.core.webtools.services.WebtoolsServiceConfiguration;
import com.eaton.platform.core.webtools.util.WebtoolsUtil;
import com.google.gson.JsonObject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

@Component(service = Servlet.class, immediate = true, property = {
                ServletConstants.SLING_SERVLET_METHODS_GET,
                ServletConstants.SLING_SERVLET_PATHS +
                                WebtoolsConstants.FREE_SAMPLE_ORDERS_STOCK_PATH,
})
public class FreeSampleStockAvailabilityServlet extends SlingSafeMethodsServlet {

        private static final long serialVersionUID = 1L;
        private static final Logger LOGGER = LoggerFactory.getLogger(FreeSampleStockAvailabilityServlet.class);

        @Reference
        transient WebtoolsServiceConfiguration webtoolsService;

        @Override
        protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
                        throws ServletException, IOException {
                LOGGER.debug("FreeSample stock availability :: doGet() :: Started");
                // Add Code to check stock availability.
                JsonObject jsonObject = new JsonObject();
                
                try(Writer writer = response.getWriter()){
                        // Flow for First JSON which holds the Product, Basic Series and Basic Part
                        // Number details
                        if (request.getRequestPathInfo().getSelectors().length > 0) {
                                LOGGER.info("Inside Free sample stock availability servelt");
                                String catalogID = getCatalogIdApiJsonPath(request);
                                jsonObject = webtoolsService.getFreeSampleStockAvailability(catalogID);
                                LOGGER.info("Build Backshell Metadata JSON:: Exited successfully");
                        }

                        if (jsonObject != null) {
                                response.setContentType("application/json");
                                writer.write(jsonObject.toString());
                               
                        }
                } catch (Exception e) {
                        LOGGER.error("Exception in MyFreeSampleServlet class doGet method", e);
                }

                LOGGER.debug("MyFreeSampleServlet stock availability :: doGet() :: Ended");
        }

        /**
         * Method to get the Part Id from the http request selector
         * 
         * @param slingRequest
         * @return catalogID int
         */
        static String getCatalogIdApiJsonPath(SlingHttpServletRequest slingRequest) {
                LOGGER.debug("Inside Get Catalog ID from Request Flow");

                String[] selectors = slingRequest.getRequestPathInfo().getSelectors();
                final Map<String, String> requestParameterMap = WebtoolsUtil.getParamsFromSelectors(selectors);
                return requestParameterMap.get(WebtoolsConstants.CATALOG_ID);
        }

}
