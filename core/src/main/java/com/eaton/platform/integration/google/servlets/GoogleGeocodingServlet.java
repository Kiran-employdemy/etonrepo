package com.eaton.platform.integration.google.servlets;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.integration.apigee.services.ApigeeService;
import com.eaton.platform.integration.google.constants.GoogleConstants;
import com.eaton.platform.integration.google.services.GoogleService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component(service = Servlet.class,
        immediate = true,
        property = {
                ServletConstants.SLING_SERVLET_METHODS_GET,
                ServletConstants.SLING_SERVLET_PATHS + GoogleConstants.GEOCODING_SERVLET_PATH
        })
public class GoogleGeocodingServlet extends SlingSafeMethodsServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleGeocodingServlet.class);
    
    @Reference
    private transient GoogleService googleService;

    @Reference
    private transient ApigeeService apigeeService;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws IOException {
        LOGGER.debug("GoogleGeocodingServlet :: doGet() :: Started");
        
        final List<NameValuePair> params = new ArrayList<>();
        params.add( new BasicNameValuePair( CommonConstants.ADDRESS, request.getParameter(CommonConstants.ADDRESS) ));

        final List<NameValuePair> headers = new ArrayList<>();

        String coordinates = null;

        try {
            
            String jsonResponse = googleService.getResponse(GoogleConstants.MAPS_ENDPOINT, headers, params);
            if (StringUtils.isEmpty(jsonResponse)) {
                throw new IOException("json response from google maps api is empty");
            }
            
            JSONArray jsonResultsArr = new JSONObject(jsonResponse).getJSONArray("results");
            if (jsonResultsArr == null) {
                throw new JSONException("results key");
            }

            JSONObject jsonResultsObj = jsonResultsArr.getJSONObject(0);
            if (jsonResultsObj == null) {
                throw new JSONException("results index 0");
            }
            
            coordinates = jsonResultsObj.getJSONObject("geometry").get("location").toString();
            LOGGER.debug("coordinates: {}", coordinates);

            response.setCharacterEncoding(CommonConstants.UTF_8);
            response.getWriter().write(coordinates);

        } catch (IOException e) {
            LOGGER.error("Unable to get coordinates from Google Maps Api. IOException: ", e);
        } catch (JSONException je) {
            LOGGER.error("Cannot parse Google maps geocoding api response: cannot find ", je);
        } finally {
            response.getWriter().flush();
        }

        LOGGER.debug("GoogleGeocodingServlet :: doGet() :: Ended");
    }
}
