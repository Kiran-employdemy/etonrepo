package com.eaton.platform.integration.auth.filters;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.commons.util.DamUtil;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.integration.auth.constants.AuthConstants;
import com.eaton.platform.integration.auth.models.AuthenticationToken;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.NonExistingResource;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthorizationFilterActions {
    private SlingHttpServletRequest slingHttpServletRequest;
    private SlingHttpServletResponse slingHttpServletResponse;
    private FilterChain chain;
    private AuthenticationToken authenticationToken;
    private Resource resource;
    private ResourceResolver resourceResolver;

    public AuthorizationFilterActions request(ServletRequest request) {
        this.slingHttpServletRequest = (SlingHttpServletRequest) request;
        this.resource = slingHttpServletRequest.getResource();
        this.resourceResolver = slingHttpServletRequest.getResourceResolver();
        return this;
    }

    public AuthorizationFilterActions response(ServletResponse response) {
        this.slingHttpServletResponse = (SlingHttpServletResponse) response;
        return this;
    }

    public AuthorizationFilterActions filterChain(FilterChain chain) {
        this.chain = chain;
        return this;
    }

    public boolean isPageOrAsset() {
        return getPage() != null || DamUtil.isAsset(resource);
    }

    public AuthorizationFilterActions authenticationToken(AuthenticationToken authenticationToken) {
        this.authenticationToken = authenticationToken;
        return this;
    }

    public SlingHttpServletRequest getSlingHttpServletRequest() {
        return slingHttpServletRequest;
    }

    public AuthenticationToken getAuthenticationToken() {
        return authenticationToken;
    }

    public Resource getResource() {
        return resource;
    }

    public ResourceResolver getResourceResolver() {
        return resourceResolver;
    }

    public SlingHttpServletResponse getSlingHttpServletResponse() {
        return slingHttpServletResponse;
    }

    public String getPathOfResource() {
        return resource.getPath();
    }

    public boolean isResourceExisting() {
        return ! ( resource instanceof NonExistingResource );
    }

    public FilterChain getChain() {
        return chain;
    }

    public void sendNotFound() throws IOException {
        slingHttpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    public String extractRawJWTFromRequest(String securityCookieId) {
        Cookie cookie = slingHttpServletRequest.getCookie(securityCookieId);
        if (cookie == null) {
            return StringUtils.EMPTY;
        }
        return cookie.getValue();
    }

    public void sendRedirect(String redirectPath) throws IOException {
        getSlingHttpServletResponse().sendRedirect(redirectPath);
    }

    public void continueChain() throws IOException {
        try {
            chain.doFilter(slingHttpServletRequest, slingHttpServletResponse);
        } catch (ServletException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public Resource getMainResourceIfNeeded() {
        String resourcePath = getPathOfResource();
        return resourcePath.contains(JcrConstants.JCR_CONTENT) ? getSlingHttpServletRequest().getResourceResolver()
                .getResource(resourcePath.substring(0, resourcePath.indexOf(JcrConstants.JCR_CONTENT) - 1)) : getResource();
    }

    public Cookie getRedirectCookie() {
        return getSlingHttpServletRequest().getCookie(AuthConstants.ETN_REDIRECT_COOKIE);
    }

    public Page getPage() {
        if (resource == null) {
            return null;
        }
        return resource.adaptTo(Page.class);
    }
}
