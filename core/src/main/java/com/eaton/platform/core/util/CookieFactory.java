package com.eaton.platform.core.util;

import org.apache.sling.api.SlingHttpServletRequest;

import javax.servlet.http.Cookie;
import java.net.MalformedURLException;
import java.net.URL;

public class CookieFactory {


    public static final int MAX_AGE_FOR_DELETION = 0;
    public static final int START_INDEX_IN_STRING = 0;
    public static final String DOT_COM = ".com";
    public static final String DOT_COM_CHINA = ".com.cn";
    public static final String LOGIN_PAGE_REDIRECT_COOKIE_NAME = "login-page-redirect";
    public static final char DOT = '.';
    public static final String DEEP_LINK_REDIRECT_COOKIE_NAME = "deep-link-redirect";

    public Cookie loginRedirectCookie(SlingHttpServletRequest slingHttpServletRequest, String value) throws MalformedURLException {
        return baseCookie(value, slingHttpServletRequest, LOGIN_PAGE_REDIRECT_COOKIE_NAME);
    }

    private static Cookie baseCookie(String value, SlingHttpServletRequest slingHttpServletRequest, String cookieName) throws MalformedURLException {
        URL url = new URL(slingHttpServletRequest.getRequestURL().toString());
        Cookie cookie = new Cookie(cookieName, value);
        cookie.setPath("/");
        String host = url.getHost();
        if (host.contains(DOT_COM_CHINA)) {
            String withoutDotCom = host.substring(START_INDEX_IN_STRING, host.indexOf(DOT_COM_CHINA));
            cookie.setDomain(withoutDotCom.substring(withoutDotCom.lastIndexOf(DOT)) + DOT_COM_CHINA);
            return cookie;
        }
        if (host.contains(DOT_COM)) {
            String withoutDotCom = host.substring(START_INDEX_IN_STRING, host.indexOf(DOT_COM));
            cookie.setDomain(withoutDotCom.substring(withoutDotCom.lastIndexOf(DOT)) + DOT_COM);
        }
        return cookie;
    }

    public Cookie loginRedirectCookieForDeletion(SlingHttpServletRequest slingHttpServletRequest, String value) throws MalformedURLException {
        Cookie cookie = loginRedirectCookie(slingHttpServletRequest, value);
        cookie.setMaxAge(MAX_AGE_FOR_DELETION);
        return cookie;
    }

    public Cookie deepLinkRedirectCookie(SlingHttpServletRequest slingHttpServletRequest, String value) throws MalformedURLException {
        return baseCookie(value, slingHttpServletRequest, DEEP_LINK_REDIRECT_COOKIE_NAME);
    }

    public Cookie deepLinkRedirectCookieForDeletion(SlingHttpServletRequest slingHttpServletRequest, String value) throws MalformedURLException {
        Cookie cookie = deepLinkRedirectCookie(slingHttpServletRequest, value);
        cookie.setMaxAge(MAX_AGE_FOR_DELETION);
        return cookie;
    }
}
