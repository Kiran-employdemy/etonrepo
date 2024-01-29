package com.eaton.platform.integration.endeca.servlets;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.search.pojo.AssetSearchResponse;
import com.eaton.platform.core.search.service.AssetSearchService;
import com.eaton.platform.integration.auth.models.AuthenticationToken;
import com.eaton.platform.integration.auth.services.AuthorizationService;
import com.eaton.platform.integration.auth.services.UserProfileService;
import com.eaton.platform.integration.endeca.constants.EndecaConstants;
import com.eaton.platform.integration.endeca.services.impl.NewEndecaAdvancedSearchServiceImpl;
import com.google.gson.Gson;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;


/**
 * <html> Description: AdvanceD search resource based servlet.
 *
 * @author ICF
 * @version 1.0
 * @since 2021
 */
@Component(service = Servlet.class,
        immediate = true,
        property = {
                ServletConstants.SLING_SERVLET_METHODS_GET,
                ServletConstants.SLING_SERVLET_METHODS_POST,
                ServletConstants.SLING_SERVLET_RESOURCE_TYPES + "eaton/components/secure/advanced-search",
                ServletConstants.SLING_SERVLET_EXTENSION_JSON
        })
public class EndecaAdvancedSearchServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(EndecaAdvancedSearchServlet.class);

    @Reference
    private AuthorizationService authorizationService;

    @Reference
    private UserProfileService profileService;
    @Reference(target = "(serviceName=" + NewEndecaAdvancedSearchServiceImpl.SERVICE_NAME + ")")
    private AssetSearchService searchService;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
            throws IOException {
        try {
            response.setContentType(CommonConstants.APPLICATION_JSON);
            LocalDateTime just = LocalDateTime.now();
            AssetSearchResponse searchResponse = searchService.search(request);
            if (null != searchResponse) {
                searchResponse.setExecutionTime(just.toLocalTime().until(LocalDateTime.now(), ChronoUnit.MILLIS));
                response.getWriter().write(new Gson().toJson(searchResponse));
            } else {
                response.getWriter().write(new Gson().toJson(AssetSearchResponse.fail(EndecaConstants.FAIL_STRING)));
            }
        } catch (Exception e) {
            LOG.error("Exception in EndecaAdvancedSearchServlet class doGet method {} ", e.getMessage(), e);
        }
        response.getWriter().flush();
    }

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException {
        LOG.debug("****** Start :  TrackDownloadServlet ");
        response.setContentType("application/json");
        JSONObject results = null;
        try {
            final Map<String, String[]> filesList = request.getParameterMap();
            if (authorizationService.getTokenFromSlingRequest(request) != null) {
                AuthenticationToken authenticationToken = authorizationService.getTokenFromSlingRequest(request);
                if (authenticationToken != null && authenticationToken.getUserProfile() != null && profileService != null) {
                    results = profileService.trackUserProfileForDownload(authenticationToken.getUserProfile(), request, filesList);
                }
            }
        } catch (Exception e) {
            LOG.error("Error while tracking the data out to PrintWriter.", e);
        }
        LOG.debug("****** End :  doPost {}", results);
    }

}

