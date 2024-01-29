package com.eaton.platform.integration.apigee.servlets;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.integration.apigee.constants.ApigeeConstants;
import com.eaton.platform.integration.apigee.services.ApigeeService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
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
import java.util.ArrayList;
import java.util.List;

@Component(service = Servlet.class,
        immediate = true,
        property = {
                ServletConstants.SLING_SERVLET_METHODS_GET,
                ServletConstants.SLING_SERVLET_PATHS + ApigeeConstants.APIGEE_SERVLET_PATH,
        })
public class ApigeeServlet extends SlingSafeMethodsServlet { 
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(ApigeeServlet.class);

    @Reference
    private transient ApigeeService apigeeService;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) 
            throws ServletException, IOException {
        LOGGER.debug("ApigeeServlet :: doGet() :: Started");
        
        String endpoint = request.getParameter(CommonConstants.ENDPOINT);
        String processFlow = request.getParameter(ApigeeConstants.X_PROCESS_FLOW);
        String countryID = request.getParameter(ApigeeConstants.COUNTRY_ID);
        String latitude = request.getParameter(ApigeeConstants.LATITUDE);
        String longitude= request.getParameter(ApigeeConstants.LONGITUDE);
        List<NameValuePair> params = new ArrayList<>();
        // it checks for souriau product country id
        if(countryID != null) {
            params.add( new BasicNameValuePair(ApigeeConstants.COUNTRY_ID, countryID));
        }
        if(latitude != null) {
            params.add( new BasicNameValuePair(ApigeeConstants.LATITUDE, latitude));
        }
        if(longitude != null) {
            params.add( new BasicNameValuePair(ApigeeConstants.LONGITUDE, longitude));
        }
        params.add( new BasicNameValuePair(ApigeeConstants.RADIUS, request.getParameter(ApigeeConstants.RADIUS)));
        params.add( new BasicNameValuePair(ApigeeConstants.START_INDEX, request.getParameter(ApigeeConstants.START_INDEX)));
        params.add( new BasicNameValuePair(ApigeeConstants.PAGE_SIZE, request.getParameter(ApigeeConstants.PAGE_SIZE)));

        String apiResponse = apigeeService.getData(endpoint, params, processFlow);

        if (StringUtils.isNotEmpty(apiResponse)) {
            try (Writer writer = response.getWriter()){
                response.setContentType(CommonConstants.APPLICATION_JSON);
                response.setCharacterEncoding(CommonConstants.UTF_8);
                writer.write(apiResponse);
                  
                
            } catch (IOException e) {
                LOGGER.error("Exception writing to sling servlet response: ", e);
            } finally {
                response.getWriter().flush();
            }
        }
        LOGGER.debug("ApigeeServlet :: doGet() :: Ended");
    }
}
