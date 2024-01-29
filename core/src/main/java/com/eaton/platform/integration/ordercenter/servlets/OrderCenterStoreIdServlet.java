package com.eaton.platform.integration.ordercenter.servlets;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.integration.ordercenter.services.OrderCenterStoreIdService;
import com.google.gson.JsonElement;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;

@Component(service = Servlet.class,
        immediate = true,
        property = {
                ServletConstants.SLING_SERVLET_METHODS_GET,
                ServletConstants.SLING_SERVLET_PATHS + "/eaton/ordercenter/storeid",
                ServletConstants.SLING_SERVLET_EXTENSION_JSON
        })
public class OrderCenterStoreIdServlet  extends SlingSafeMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(OrderCenterStoreIdServlet.class);

    @Reference
    private transient OrderCenterStoreIdService orderCenterStoreIdService;


    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 7886330212310203900L;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {

        LOG.debug("In Order Center Store ID Servlet");

        JsonElement orderCenterResponse = orderCenterStoreIdService.getStoreIds();
        response.setContentType(CommonConstants.APPLICATION_JSON);
        response.setCharacterEncoding(CommonConstants.UTF_8);
        response.getWriter().print(orderCenterResponse);
        response.getWriter().flush();
    }


}
