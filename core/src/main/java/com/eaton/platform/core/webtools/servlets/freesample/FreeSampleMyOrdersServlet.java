package com.eaton.platform.core.webtools.servlets.freesample;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.webtools.constants.WebtoolsConstants;
import com.eaton.platform.integration.apigee.services.ApigeeService;
import com.eaton.platform.integration.auth.services.AuthorizationService;

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

@Component(service = Servlet.class, immediate = true, property = {
                ServletConstants.SLING_SERVLET_METHODS_GET,
                ServletConstants.SLING_SERVLET_PATHS +
                                WebtoolsConstants.GET_SAMPLE_ORDERS_SERVLET_PATH,
})
public class FreeSampleMyOrdersServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(FreeSampleMyOrdersServlet.class);
    @Reference
    private transient ApigeeService apigeeService;
    @Reference
    private transient AuthorizationService authorizationService;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
                        throws ServletException, IOException {
        LOGGER.debug("ApigeeServlet :: doGet() :: Started");
        String endpoint = request.getParameter(CommonConstants.ENDPOINT);
        LOGGER.debug("endpoint {}",endpoint);
        String orderId = request.getParameter(WebtoolsConstants.FREESAMPLE_SERVLET_ORDERID);
        LOGGER.debug("orderId {}",orderId);
        // Retrieving email from param is only used for testing and to retrieve data in
        // author related to test
        // user. Retrieve email from profile of the logged-in user
        String email = null;
        // Retrieve user from logged in profile
        email = getUserEmail(request);
        LOGGER.debug("email from logged in user {}",email);
              
        if (endpoint !=null) {
            String apiResponse = apigeeService.getFreeSampleOrders(endpoint, getParamsFromRequest( request, orderId, email));
            LOGGER.debug("apiResponse {}",apiResponse);
            if (StringUtils.isNotEmpty(apiResponse)) {
                try(Writer writer = response.getWriter()) {
                    response.setContentType(CommonConstants.APPLICATION_JSON);
                    response.setCharacterEncoding(CommonConstants.UTF_8);
                    writer.write(apiResponse);
                                } catch (IOException e) {
                    LOGGER.error("Exception writing to sling servlet response: ", e);
                                } finally {
                    response.getWriter().flush();
                                }
                        }
                }
              
        LOGGER.debug("MyFreeSampleServlet :: doGet() :: Ended");
        }
    private List<NameValuePair> getParamsFromRequest(SlingHttpServletRequest request,String orderId,String email){
        List<NameValuePair> params = new ArrayList<>();
        if (request.getParameter(WebtoolsConstants.FREESAMPLE_SERVLET_ORDERSTATUS) != null) {
            params.add(new BasicNameValuePair(WebtoolsConstants.FREESAMPLE_SERVLET_ORDERSTATUS,
                                        request.getParameter(
                                                        WebtoolsConstants.FREESAMPLE_SERVLET_ORDERSTATUS)));
                }
        if (request.getParameter(WebtoolsConstants.FREESAMPLE_SERVLET_ORDERTYPE) != null) {
            params.add(new BasicNameValuePair(WebtoolsConstants.FREESAMPLE_SERVLET_ORDERTYPE,
                                        request.getParameter(
                                                        WebtoolsConstants.FREESAMPLE_SERVLET_ORDERTYPE)));
                }
        if (request.getParameter(WebtoolsConstants.FREESAMPLE_SERVLET_OFFSET) != null) {
            params.add(new BasicNameValuePair(WebtoolsConstants.FREESAMPLE_SERVLET_OFFSET,
                                        request.getParameter(
                                                        WebtoolsConstants.FREESAMPLE_SERVLET_OFFSET)));
                }
        if (request.getParameter(WebtoolsConstants.FREESAMPLE_SERVLET_LIMIT) != null) {
            params.add(new BasicNameValuePair(WebtoolsConstants.FREESAMPLE_SERVLET_LIMIT,
                                        request.getParameter(WebtoolsConstants.FREESAMPLE_SERVLET_LIMIT)));
                }
        if (orderId == null && email != null) {
            params.add(new BasicNameValuePair(WebtoolsConstants.FREESAMPLE_SERVLET_EMAIL,
                                        email));
                }
        if (orderId != null) {
            params.add(new BasicNameValuePair(WebtoolsConstants.FREESAMPLE_SERVLET_ORDERID,
                                        request.getParameter(
                                                        WebtoolsConstants.FREESAMPLE_SERVLET_ORDERID)));
                }
        return params;
        }
    private String getUserEmail(SlingHttpServletRequest request) {
        if (null != authorizationService && authorizationService.isAuthenticated(request)){
            return authorizationService.getTokenFromSlingRequest(request).getUserProfile().getEmail();
                                
                }
        return StringUtils.EMPTY;
        }
        
}
