package com.eaton.platform.integration.auth.filters;

import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.integration.auth.constants.SecureConstants;
import com.eaton.platform.integration.auth.models.AuthenticationToken;
import com.eaton.platform.integration.auth.services.AuthenticationService;
import com.eaton.platform.integration.auth.services.AuthenticationServiceConfiguration;
import com.eaton.platform.integration.auth.services.AuthorizationService;
import com.eaton.platform.integration.auth.util.AuthCookieUtil;
import com.eaton.platform.integration.endeca.constants.EndecaConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.engine.EngineConstants;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import java.io.IOException;
import java.util.Set;

import static com.eaton.platform.core.constants.CommonConstants.RUNMODE_PUBLISH;

/**
 * Filter used to intercept specific paths as per the regex defined . Validates the auth token and if valid
 * adds the user profile to the sling request. If the token is not present or is
 * not valid, the filter proceeds and does not redirect
 */

@Component(service = Filter.class,
property = {
        AEMConstants.SERVICE_DESCRIPTION + "Eaton User Identification Filter",
		EngineConstants.SLING_FILTER_SCOPE + "=" + EngineConstants.FILTER_SCOPE_REQUEST,
		EngineConstants.SLING_FILTER_METHODS + "=GET",
		EngineConstants.SLING_FILTER_PATTERN + "=" + SecureConstants.SECURE_USER_IDENTIFICATION_FILTER_REGEX,
		Constants.SERVICE_RANKING + "=" + Integer.MAX_VALUE
})

public class UserIdentificationFilter implements Filter {
	@Reference
	private AuthenticationService authenticationService;

	@Reference
	private AuthorizationService authorizationService;

	@Reference
	private SlingSettingsService slingSettingsService;

	@Reference
	private AuthenticationServiceConfiguration authenticationServiceConfig;
	
	private static final Logger LOG = LoggerFactory.getLogger(UserIdentificationFilter.class);

	@Activate
	public void activate(final ComponentContext context){
		LOG.debug("UserIdentificationFilter :: activate() :: Start, set");
		Set<String> runModes = slingSettingsService.getRunModes();
		if (!runModes.contains(RUNMODE_PUBLISH) || runModes.contains(EndecaConstants.RUNMODE_ENDECA)) {
			LOG.info("UserIdentificationFilter deactivating as runmode doesnt include publish");
			if (context != null) {
				final String componentName = (String) context.getProperties()
						.get(ComponentConstants.COMPONENT_NAME);
				context.disableComponent(componentName);
			}
		}
		LOG.debug("UserIdentificationFilter :: activate() :: End");
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// Empty Body
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("UserIdentificationFilter :: doFilter() :: Start");
		}
		SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;
		String rawJWT = AuthCookieUtil.getJWTFromAuthCookie((SlingHttpServletRequest) request,
				authenticationServiceConfig);
		if (LOG.isDebugEnabled()) {
			LOG.debug(String.format("UserIdentificationFilter :: Raw JWT from cookie: %s", rawJWT));
		}
		if (StringUtils.isNotEmpty(rawJWT)) {
			final AuthenticationToken authenticationToken = authenticationService.parseToken(rawJWT);
			if (authenticationToken != null) {
				authorizationService.setTokenOnSlingRequest(slingRequest, authenticationToken);
			} else {
				LOG.debug("UserIdentificationFilter :: authentication token is NULL!");
			}
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		// No-Op
	}

}
