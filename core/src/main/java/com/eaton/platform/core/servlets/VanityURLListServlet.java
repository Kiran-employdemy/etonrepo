package com.eaton.platform.core.servlets;

import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.services.vanity.VanityDataStoreService;
import com.google.common.net.MediaType;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.json.JSONException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author : icf
 * @since : 05/11/20, Thu
 * Servlet that writes all configured vanity urls to the response
 *
 */
@Component(service = Servlet.class,
        immediate = true,
        property = {
                ServletConstants.SLING_SERVLET_METHODS_GET,
                ServletConstants.SLING_SERVLET_PATHS + "/eaton/vanity/data",
                ServletConstants.SLING_SERVLET_EXTENSION_HTML
        })
public class VanityURLListServlet extends SlingSafeMethodsServlet {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(VanityURLListServlet.class);

    @Reference
    private transient VanityDataStoreService vanityDataStoreService;

    @Override
    protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp)
            throws ServletException, IOException {
        LOG.debug(" VanityURLListServlet :: doGet()");
        try {
            resp.setContentType(MediaType.PLAIN_TEXT_UTF_8.toString());
            resp.getOutputStream().write(getVanityList().getBytes(StandardCharsets.UTF_8));
            resp.setStatus(200);

        } catch (IOException io) {
            LOG.error(" VanityURLListServlet :: doGet() :: Exception",io);
            resp.setStatus(500);
        } catch (JSONException jsonEx) {
            LOG.error(" VanityURLListServlet :: doGet() :: Exception",jsonEx);
            resp.setStatus(500);
        }

    }

    private String getVanityList() throws JSONException {
        LOG.debug(" VanityURLListServlet :: getVanityList() :: Started");
        try {
            return StringUtils.join(
                    vanityDataStoreService.getUniqueVanityUrlSetFromAllDomains().toArray(new String[0]), "\n"
            );
        } catch (JSONException jsonException) {
            LOG.error("Exception in getVanityList() method - VanityURLListServlet", jsonException);
        }
        LOG.debug(" VanityURLListServlet :: getVanityList() :: Exit");
        return StringUtils.EMPTY;
    }

}
