package com.eaton.platform.core.servlets.secure;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.integration.auth.models.AuthenticationToken;
import com.eaton.platform.integration.auth.services.AuthorizationService;
import com.eaton.platform.integration.auth.services.UserProfileService;
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
import java.util.Map;

/**
 * This servlet is responsible to track the user profile and assets metadata details and submits to backend API.
 */

@Component(service = Servlet.class,
        immediate = true,
        property = {
                ServletConstants.SLING_SERVLET_METHODS_GET,
                ServletConstants.SLING_SERVLET_PATHS + "/eaton/secure/track-download",
                ServletConstants.SLING_SERVLET_SELECTORS + "{nocache}",
                ServletConstants.SLING_SERVLET_EXTENSIONS + ServletConstants.SLING_SERVLET_EXTENSION_JSON
        })
public class TrackDownloadServlet  extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(TrackDownloadServlet.class);

    @Reference
    private AuthorizationService authorizationService;

    @Reference
    private UserProfileService profileService;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException {
        LOG.debug("****** Start :  TrackDownloadServlet : doPost() ");
        response.setContentType("application/json");
        JSONObject results = new JSONObject();
        try {
            final Map<String,String[]> filesList = request.getParameterMap();
            if(authorizationService.getTokenFromSlingRequest(request) != null && !filesList.isEmpty()) {
                AuthenticationToken authenticationToken = authorizationService.getTokenFromSlingRequest(request);
                if(authenticationToken !=null && authenticationToken.getUserProfile()!=null && profileService!=null) {
                    results = profileService.trackUserProfileForDownload(authenticationToken.getUserProfile(),request,filesList);
                    response.getWriter().print(results);
                }else{
                    response.getWriter().print(CommonConstants.IVALID_INPUT_RECEIVED);
                }
            }
            response.getWriter().flush();
        }
        catch(Exception e) {
            LOG.error("Error while tracking the data out to PrintWriter.", e);
        }
        LOG.debug("****** End :  TrackDownloadServlet doPost " + results);
    }
}
