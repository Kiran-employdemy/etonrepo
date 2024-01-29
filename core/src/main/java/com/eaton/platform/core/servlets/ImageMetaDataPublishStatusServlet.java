package com.eaton.platform.core.servlets;

import com.day.cq.dam.scene7.api.constants.Scene7Constants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.json.simple.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * This servlet checks if an asset is published to Dynamic Media.
 */
@Component(service = Servlet.class,
        immediate = true,
        property = {
                ServletConstants.SLING_SERVLET_METHODS_GET,
                ServletConstants.SLING_SERVLET_PATHS + "/eaton/getimagepublishstatusservlet",
                ServletConstants.SLING_SERVLET_EXTENSION_JSON
        })
public class ImageMetaDataPublishStatusServlet extends SlingSafeMethodsServlet {
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(DynamicRenditionDropDownServlet.class);

    /** The Constant REQUEST_PARAM_IMAGEPATH. */
    private static final String REQUEST_PARAM_IMAGEPATH = "imagePath";

    /** The admin service. */
    @Reference
    private transient AdminService adminService;

    @Override
    protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp)
            throws ServletException, IOException {
        LOG.debug("ImageMetaDataPublishStatusServlet :: doGet() :: Start");

        if (null != adminService) {
            try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
                final JSONObject jsonResult = new JSONObject();
                final String imagePath = req.getParameter(REQUEST_PARAM_IMAGEPATH);
                String publishStatus = StringUtils.EMPTY;
                if (null != imagePath) {
                    Resource imageResource = adminResourceResolver.resolve(imagePath);
                    if (null != imageResource) {
                        publishStatus = CommonUtil.getAssetMetadataValue(Scene7Constants.PN_S7_FILE_STATUS, imageResource);
                    }
                }
                if (null != publishStatus) {
                    jsonResult.put(CommonConstants.PUBLISH_STATUS, publishStatus);
                }
                resp.setContentType(CommonConstants.APPLICATION_JSON);
                resp.getWriter().write(jsonResult.toJSONString());
            }
        }

        LOG.debug("ImageMetaDataPublishStatusServlet :: doGet() :: Exit");
    }
}
