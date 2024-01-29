package com.eaton.platform.integration.varnish.servlets;

import com.day.cq.replication.Agent;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.integration.varnish.constants.VarnishConstants;
import com.eaton.platform.integration.varnish.services.VarnishCacheClearService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import com.google.gson.JsonObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component(service = Servlet.class,
        immediate = true,
        property = {
                ServletConstants.SLING_SERVLET_METHODS_POST,
                ServletConstants.SLING_SERVLET_PATHS + VarnishConstants.VARNISH_SERVICE_URL,
                ServletConstants.SLING_SERVLET_EXTENSION_HTML
        })
public class VarnishCacheClearServlet extends SlingAllMethodsServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(VarnishCacheClearServlet.class);

    @Reference
    private VarnishCacheClearService varnishCacheClearService;

    @Override
    protected final void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader(VarnishConstants.CACHE_CONTROL, VarnishConstants.CACHE_CONTROL_VALUE);
        response.setHeader(VarnishConstants.PRAGMA, VarnishConstants.PRAGMA_VALUE);
        response.setHeader(VarnishConstants.EXPIRES, "0");
        response.setContentType(VarnishConstants.APPLICATION_JSON);
        response.setCharacterEncoding(VarnishConstants.UTF_CONSTANT);
        String redirectPath = request.getParameter(VarnishConstants.REDIRECT_PATH);
        final LinkedHashMap<Agent, ArrayList<String>> results = varnishCacheClearService.clearVarnishCache(request);
        final JsonObject redirectPathJson = getRedirectPath(results, redirectPath);
        final PrintWriter writer = response.getWriter();
        writer.print(redirectPathJson);
        writer.flush();

    }

    private static JsonObject getRedirectPath(final LinkedHashMap<Agent, ArrayList<String>> varnishResult,
                                              String redirectPath) {
        List<VarnishCacheClearServlet.FlushResult> overallResults = new ArrayList();
        for(Map.Entry<Agent, ArrayList<String>> entry : varnishResult.entrySet()) {
            Agent agent = entry.getKey();
            ArrayList<String> varnishResponse = entry.getValue();
            overallResults.add(new VarnishCacheClearServlet.FlushResult(agent, varnishResponse));
        }
        String suffix = StringUtils.EMPTY;
            suffix = StringUtils.join(overallResults, '/');
        final JsonObject redirectPathJson = new JsonObject();
        try {
            if (StringUtils.isNotBlank(redirectPath)) {
                if (redirectPath.contains(VarnishConstants.HTML_EXT)) {
                    redirectPath = redirectPath.substring( 0, redirectPath.indexOf(VarnishConstants.HTML_EXT) + 5);
                }
                redirectPathJson.addProperty(VarnishConstants.REDIRECT_PATH, redirectPath.concat(VarnishConstants.SLASH).concat(suffix));
            }
        } catch (Exception e) {
            LOGGER.error("JSON Exception while generating redirect path" + e.getMessage());
        }
        return redirectPathJson;
    }

    private static final class FlushResult {
        private final String agentId;
        private boolean success;

        private FlushResult(Agent agent, ArrayList<String> varnishClearList) {
            this.agentId = agent.getId();
            for (String varnishStatus : varnishClearList) {
                if (!varnishStatus.equals(VarnishConstants.HTTP_SUCCESS_CODE)) {
                    this.success = Boolean.FALSE;
                    break;
                } else {
                     this.success = Boolean.TRUE;
                }
            }
        }
        public String toString() {
            return this.agentId + VarnishConstants.SLASH + this.success;
        }
    }
}

