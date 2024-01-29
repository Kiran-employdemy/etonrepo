package com.eaton.platform.integration.akamai.servlets;

import com.day.cq.replication.Agent;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.integration.akamai.constants.AkamaiConstants;
import com.eaton.platform.integration.akamai.services.AkamaiClear;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONException;
import org.json.JSONObject;
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
                ServletConstants.SLING_SERVLET_PATHS + AkamaiConstants.AKAMAI_SERVICE_URL,
                ServletConstants.SLING_SERVLET_EXTENSION_HTML
        })
public class AkamaiCacheClearServlet extends SlingAllMethodsServlet {
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8616330297777203241L;

    private static final Logger LOGGER = LoggerFactory.getLogger(AkamaiCacheClearServlet.class);

    @Reference
    private transient AkamaiClear akamaiClear;

    @Override
    protected final void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader(CommonConstants.CACHE_CONTROL, AkamaiConstants.CACHE_CONTROL_VALUE);
        response.setHeader(CommonConstants.PRAGMA, AkamaiConstants.PRAGMA_VALUE);
        response.setHeader(CommonConstants.EXPIRES, "0");
        response.setContentType(CommonConstants.APPLICATION_JSON);
        response.setCharacterEncoding(CommonConstants.UTF_8);
        String redirectPath = request.getParameter(AkamaiConstants.REDIRECT_PATH);
        final LinkedHashMap<Agent, ArrayList<String>> results = akamaiClear.clearAkamaiCache(request);
        final JSONObject redirectPathJson = getRedirectPath(results, redirectPath);
        final PrintWriter writer = response.getWriter();
        writer.print(redirectPathJson);
        writer.flush();

    }

    private static JSONObject getRedirectPath(final LinkedHashMap<Agent, ArrayList<String>> akamaiResult,
                                              String redirectPath) {
        List<AkamaiCacheClearServlet.FlushResult> overallResults = new ArrayList();
        for(Map.Entry<Agent, ArrayList<String>> entry : akamaiResult.entrySet()) {
            Agent agent = entry.getKey();
            ArrayList<String> akamaiResponse = entry.getValue();
            overallResults.add(new AkamaiCacheClearServlet.FlushResult(agent, akamaiResponse));
        }
        String suffix = StringUtils.join(overallResults, '/');

        final JSONObject redirectPathJson = new JSONObject();
        try {
            if (StringUtils.isNotBlank(redirectPath)) {
                if (redirectPath.contains(CommonConstants.HTML_EXTN)) {
                    redirectPath = redirectPath.substring( 0, redirectPath.indexOf(CommonConstants.HTML_EXTN) + 5);
                }
                redirectPathJson.put(AkamaiConstants.REDIRECT_PATH, redirectPath.concat(CommonConstants.SLASH_STRING).concat(suffix));
            }
        } catch (JSONException e) {
            LOGGER.error("JSON Exception while generating redirect path {}" , e.getMessage());
        }
        return redirectPathJson;
    }

    private static final class FlushResult {
        private final String agentId;
        private boolean success;

        private FlushResult(Agent agent, ArrayList<String> akamaiClearList) {
            this.agentId = agent.getId();
            for (String akamaiStatus : akamaiClearList) {
                if (!akamaiStatus.equals(AkamaiConstants.HTTP_SUCCESS_CODE)) {
                    this.success = Boolean.FALSE;
                    break;
                } else {
                     this.success = Boolean.TRUE;
                }
            }
        }
        public String toString() {
            return this.agentId + CommonConstants.SLASH_STRING + this.success;
        }
    }

}

