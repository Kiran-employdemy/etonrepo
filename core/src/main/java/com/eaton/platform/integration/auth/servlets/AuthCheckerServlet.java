package com.eaton.platform.integration.auth.servlets;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.integration.auth.constants.SecureConstants;
import com.eaton.platform.integration.auth.models.AuthenticationToken;
import com.eaton.platform.integration.auth.services.AuthenticationService;
import com.eaton.platform.integration.auth.services.AuthenticationServiceConfiguration;
import com.eaton.platform.integration.auth.services.AuthorizationService;
import com.eaton.platform.integration.auth.util.AuthCookieUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.mime.MimeTypeService;
import org.apache.sling.engine.EngineConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component(
        immediate = true,
        service = Servlet.class,
        property = {
                ServletConstants.SLING_SERVLET_PATHS + "/bin/permissioncheck",
                ServletConstants.SLING_SERVLET_METHODS_GET,
                ServletConstants.SLING_SERVLET_METHODS_HEAD,
                EngineConstants.SLING_FILTER_PATTERN + "=" + SecureConstants.GENERIC_CONTENT_SECURE_REGEX
        }
)
public class AuthCheckerServlet extends SlingAllMethodsServlet {
    private static final String PATH_PARAM = "uri";
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthCheckerServlet.class);
    private static final long serialVersionUID = 4842226015373251140L;

    @Reference
    private transient AuthenticationService authenticationService;

    @Reference
    private transient AuthorizationService authorizationService;
    @Reference
    private transient MimeTypeService mimeTypeService;
    @Reference
    private transient AuthenticationServiceConfiguration authenticationServiceConfig;

    @Override
    public void doHead(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
            throws IOException {

        LOGGER.debug("Start with doGet method :: AuthCheckerServlet");

        final String path = request.getParameter(PATH_PARAM);

        boolean grantAccess = false;
        if(StringUtils.isNotEmpty(path)) {
            Resource resourcePath = request.getResourceResolver().resolve(
                    path.contains(CommonConstants.HTML_EXTN) ? FilenameUtils.removeExtension(path): path);
            String rawJWT = AuthCookieUtil.getJWTFromAuthCookie(request, authenticationServiceConfig);
            if (StringUtils.isNotEmpty(rawJWT)) {
                final AuthenticationToken authenticationToken = authenticationService.parseToken(rawJWT);
                if (authenticationToken != null) {
                    grantAccess = authorizationService.isAuthorized(authenticationToken, resourcePath);
                }
            }
        }
        response.setContentType(getMimeType(path));
        //Send the response based on if access is granted
        response.setStatus(grantAccess ? HttpServletResponse.SC_OK : HttpServletResponse.SC_FORBIDDEN);
        LOGGER.debug("END with doGet method :: AuthCheckerServlet");
    }

    @Override
    public void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
            throws IOException {
        doHead(request, response);
    }
    /**
     * Gets the mimeType of the uri.
     * - The extension of the page/asset looked at first and used
     * - default to the resource's mimeType if the requested mimeType by extension is not supported.
     *
     * @param path  to get the mimeType for
     * @return the string representation of the page/asset mimeType
     */
    private String getMimeType(final String path) {
        final String lastSuffix = FilenameUtils.getExtension(path);
        return mimeTypeService.getMimeType(lastSuffix);
    }
}
