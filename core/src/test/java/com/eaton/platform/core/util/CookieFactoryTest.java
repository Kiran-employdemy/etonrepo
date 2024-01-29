package com.eaton.platform.core.util;

import org.apache.sling.api.SlingHttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.Cookie;
import java.net.MalformedURLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CookieFactoryTest {

    static final String TEST_PATH = "/path/to/resource";
    static final String MUST_BE_EQUAL = "must be equal";
    static final String LOCAL_EATON_DASHBOARD = "https://www-local.eaton.com/fr/fr-fr/secure/dashboard.html";
    static final String EATON_COM = ".eaton.com";
    static final String DEEP_LINK_REDIRECT = "deep-link-redirect";
    static final String LOGIN_PAGE_REDIRECT = "login-page-redirect";
    static final String CHINESE_DASHBOARD = "https://www-local.eaton.com.cn/cn/zh-cn/secure/dashboard.html";
    static final String ETN_DOM_FRENCH_DASHBOARD = "https://eaton-dev.tcc.etn.com/fr/fr-fr/secure/dashboard.html";
    @Mock
    SlingHttpServletRequest slingHttpServletRequest;

    @Test
    @DisplayName("Login redirect cookie must be set with correct domain (.eaton.com), path and value")
    void testLoginRedirectCookieEatoDotCom() throws MalformedURLException {
        when(slingHttpServletRequest.getRequestURL()).thenReturn(new StringBuffer(LOCAL_EATON_DASHBOARD));
        Cookie actualCookie = new CookieFactory().loginRedirectCookie(slingHttpServletRequest, TEST_PATH);
        assertCookie(actualCookie, -1, EATON_COM, LOGIN_PAGE_REDIRECT);

    }

    @Test
    @DisplayName("Login redirect cookie must be set with correct domain (.eaton.com), path and value and max age 0")
    void testLoginRedirectCookieEatonDotComForDeletion() throws MalformedURLException {
        when(slingHttpServletRequest.getRequestURL()).thenReturn(new StringBuffer(LOCAL_EATON_DASHBOARD));
        Cookie actualCookie = new CookieFactory().loginRedirectCookieForDeletion(slingHttpServletRequest, TEST_PATH);
        assertCookie(actualCookie, 0, EATON_COM, LOGIN_PAGE_REDIRECT);
    }
    @Test
    @DisplayName("Login redirect cookie must be set with correct domain (.etn.com), path and value")
    void testLoginRedirectCookieEtnDotCom() throws MalformedURLException {
        when(slingHttpServletRequest.getRequestURL()).thenReturn(new StringBuffer(ETN_DOM_FRENCH_DASHBOARD));
        Cookie actualCookie = new CookieFactory().loginRedirectCookie(slingHttpServletRequest, TEST_PATH);
        assertCookie(actualCookie, -1, ".etn.com", LOGIN_PAGE_REDIRECT);

    }

    @Test
    @DisplayName("Login redirect cookie must be set with correct domain (.etn.com), path and value and max age 0")
    void testLoginRedirectCookieEtnDotComForDeletion() throws MalformedURLException {
        when(slingHttpServletRequest.getRequestURL()).thenReturn(new StringBuffer(ETN_DOM_FRENCH_DASHBOARD));
        Cookie actualCookie = new CookieFactory().loginRedirectCookieForDeletion(slingHttpServletRequest, TEST_PATH);
        assertCookie(actualCookie, 0, ".etn.com", LOGIN_PAGE_REDIRECT);
    }
    @Test
    @DisplayName("Login redirect cookie must be set with correct domain (.eaton.com.cn), path and value")
    void testLoginRedirectCookieEatoDotComChina() throws MalformedURLException {
        when(slingHttpServletRequest.getRequestURL()).thenReturn(new StringBuffer(CHINESE_DASHBOARD));
        Cookie actualCookie = new CookieFactory().loginRedirectCookie(slingHttpServletRequest, TEST_PATH);
        assertCookie(actualCookie, -1, ".eaton.com.cn", LOGIN_PAGE_REDIRECT);

    }

    @Test
    @DisplayName("Login redirect cookie must be set with correct domain (.eaton.com.cn), path and value and max age 0")
    void testLoginRedirectCookieEatonDotComChinaForDeletion() throws MalformedURLException {
        when(slingHttpServletRequest.getRequestURL()).thenReturn(new StringBuffer(CHINESE_DASHBOARD));
        Cookie actualCookie = new CookieFactory().loginRedirectCookieForDeletion(slingHttpServletRequest, TEST_PATH);
        assertCookie(actualCookie, 0, ".eaton.com.cn", LOGIN_PAGE_REDIRECT);
    }
    @Test
    @DisplayName("Deep link redirect cookie must be set with correct domain (.eaton.com), path and value")
    void testDeepLinkRedirectCookieEatonDotCom() throws MalformedURLException {
        when(slingHttpServletRequest.getRequestURL()).thenReturn(new StringBuffer(LOCAL_EATON_DASHBOARD));
        Cookie actualCookie = new CookieFactory().deepLinkRedirectCookie(slingHttpServletRequest, TEST_PATH);
        assertCookie(actualCookie, -1, EATON_COM, DEEP_LINK_REDIRECT);

    }

    @Test
    @DisplayName("Deep link redirect cookie must be set with correct domain (.eaton.com), path and value and max age 0")
    void testDeepLinkRedirectCookieEatonDotComForDeletion() throws MalformedURLException {
        when(slingHttpServletRequest.getRequestURL()).thenReturn(new StringBuffer(LOCAL_EATON_DASHBOARD));
        Cookie actualCookie = new CookieFactory().deepLinkRedirectCookieForDeletion(slingHttpServletRequest, TEST_PATH);
        assertCookie(actualCookie, 0, EATON_COM, DEEP_LINK_REDIRECT);
    }

    private static void assertCookie(Cookie actualCookie, int expected, String expectedDomain, String expectedName) {
        assertAll(() -> assertEquals(expectedDomain, actualCookie.getDomain(), MUST_BE_EQUAL),
                () -> assertEquals("/", actualCookie.getPath(), MUST_BE_EQUAL),
                () -> assertFalse(actualCookie.getSecure(), MUST_BE_EQUAL),
                () -> assertFalse(actualCookie.isHttpOnly(), MUST_BE_EQUAL),
                () -> assertEquals(expected, actualCookie.getMaxAge(), MUST_BE_EQUAL),
                () -> assertEquals(TEST_PATH, actualCookie.getValue(), MUST_BE_EQUAL),
                () -> assertEquals(expectedName, actualCookie.getName(), MUST_BE_EQUAL)
        );
    }

}