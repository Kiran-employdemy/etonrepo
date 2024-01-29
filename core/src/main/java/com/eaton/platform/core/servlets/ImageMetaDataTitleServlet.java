package com.eaton.platform.core.servlets;

import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.json.simple.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

@Component(service = Servlet.class,
        immediate = true,
        property = {
                ServletConstants.SLING_SERVLET_METHODS_GET,
                ServletConstants.SLING_SERVLET_PATHS + "/eaton/getimagealttextservlet",
                ServletConstants.SLING_SERVLET_EXTENSION_JSON
        })
public class ImageMetaDataTitleServlet extends SlingSafeMethodsServlet {

    /** The admin service. */
    @Reference
    private transient AdminService adminService;

    /** The Constant REQUEST_PARAM_IMAGEPATH. */
    private static final String REQUEST_PARAM_IMAGEPATH = "imagePath";


    @Override
    protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp)
            throws ServletException, IOException {
        if (null != adminService) {
            try (ResourceResolver adminResourceResolver = adminService.getWriteService()){
                final JSONObject jsonResult = new JSONObject();
                final String imagePath = req.getParameter(REQUEST_PARAM_IMAGEPATH);
                String altText = StringUtils.EMPTY;
                if (null  != imagePath) {
                    altText = CommonUtil.getAssetAltText(adminResourceResolver, imagePath);
                }
                if (null != altText) {
                    jsonResult.put("altText",altText);
                }
                resp.setContentType("application/json");
                resp.getWriter().write(jsonResult.toJSONString());
            }
        }
    }
}
