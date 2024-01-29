package com.eaton.platform.integration.auth.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eaton.platform.core.util.EncryptionUtil;
import com.eaton.platform.integration.auth.services.EatonVirtualAssistantConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.acs.commons.util.CookieUtil;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.integration.auth.models.AuthenticationToken;
import com.eaton.platform.integration.auth.models.UserProfile;
import com.eaton.platform.integration.auth.services.AuthenticationServiceConfiguration;

import java.util.List;

public class AuthCookieUtil {
    private static final Logger LOG = LoggerFactory.getLogger(AuthCookieUtil.class);
    private static final String SSO_COOKIE_NAME = "iPlanetDirectoryPro";
    private static final String PUBLISH_SERVER_NAME_COOKIE_ID = "publishServerName";

    /**
     * Instantiates a new AuthCookieUtil
     */
    private AuthCookieUtil() {
        LOG.debug("Inside AuthCookieUtil constructor");
    }
    
    public static void setJWTonAuthCookie(String rawJWT,
    									  HttpServletRequest request,
    									  HttpServletResponse response,
                                          AuthenticationServiceConfiguration config){

        LOG.debug("Attempting to create cookie: {} ", config.getSecurityCookieId());
        Cookie securityCookie = new Cookie(config.getSecurityCookieId(), rawJWT);

        securityCookie.setMaxAge(config.getSecurityCookieTTL());
        securityCookie.setPath(CommonConstants.SLASH_STRING);
        securityCookie.setHttpOnly(true);
        securityCookie.setSecure(request.isSecure());
        securityCookie.setDomain(config.getSecurityCookieDomain());

        CookieUtil.addCookie(securityCookie, response);
        LOG.debug("Cookie successfully created.");
    }
    
	public static void setSSOUtilsCookie(AuthenticationToken authenticationToken, HttpServletResponse response,
			AuthenticationServiceConfiguration config) {

		LOG.debug("Attempting to create iPlanetDirectoryPro Cookie");
		UserProfile userProfile = authenticationToken.getUserProfile();

		EatonSsoToken token;
		try {
			token = EatonSsoToken.createTokenWithUid(authenticationToken.getUserLDAPId(),
					userProfile.getEatonPersonType(), userProfile.getGivenName(), userProfile.getLastName(),
					userProfile.getEmail());

			Cookie ssoCookie = new Cookie(SSO_COOKIE_NAME, token.getToken());
			ssoCookie.setMaxAge(config.getSecurityCookieTTL());
			ssoCookie.setPath(CommonConstants.SLASH_STRING);
			ssoCookie.setHttpOnly(true);
			ssoCookie.setSecure(true);
			ssoCookie.setDomain(config.getSsoCookieDomain());

			CookieUtil.addCookie(ssoCookie, response);
			LOG.debug("SSOUtilsCookie successfully created.");
		} catch (Exception e) {
			LOG.warn("Error generating SSO Cookie", e);
		}
	}

    /*
     * Set cookies to personalize eaton virtual assistant (eva)
     */
    public static void setEatonVirtualAssistantCookies(AuthenticationToken authenticationToken,
                                                       SlingHttpServletResponse response,
                                                        EatonVirtualAssistantConfiguration config) {

        LOG.debug("AuthCookieUtil.setEatonVirtualAssistantCookies START");

        UserProfile userProfile = authenticationToken.getUserProfile();

        try {
            
            // USER ID COOKIE
            String userIdCookieValue = StringUtils.isBlank(userProfile.getUid()) ? StringUtils.EMPTY : userProfile.getUid();
            LOG.debug("userIdCookieValue: {}", userIdCookieValue);
            List<String> userIdRandomIvAndCipherText = EncryptionUtil.encrypt(userIdCookieValue, config.getEncryptionAlgorithm(), config.getEncryptionMode(), config.getEncryptionKey());
            String userIdRandomIvAndCipherTextCombined = StringUtils.join(userIdRandomIvAndCipherText.get(0), userIdRandomIvAndCipherText.get(1));
            LOG.debug("userIdRandomIvAndCipherTextCombined : {}", userIdRandomIvAndCipherTextCombined);
            setCookie(config.getUserIdCookieName(), userIdRandomIvAndCipherTextCombined, config.getUserIdCookieDomain(), true, false, config.getUserIdCookieTTL(), response);
            
            // VISTA ID / DRC ID COOKIE
            JSONObject vistaIdCookieJsonObject = new JSONObject();
            String vistaId = StringUtils.isBlank(userProfile.getEatonIccVistaUid()) ? StringUtils.EMPTY : userProfile.getEatonIccVistaUid();
            vistaIdCookieJsonObject.put(config.getVistaIdJsonName(), vistaId);
            LOG.debug("vistaId: {}", vistaId);
            String drcId = StringUtils.isBlank(userProfile.getEatonDrcId()) ? StringUtils.EMPTY : userProfile.getEatonDrcId();
            LOG.debug("drcId: {}", drcId);
            vistaIdCookieJsonObject.put(config.getDrcIdJsonName(), drcId);
            String vistaIdDrcIdJsonStr = vistaIdCookieJsonObject.toString();
            LOG.debug("vistaId and DrcId Json String: {}", vistaIdDrcIdJsonStr);
            List<String> vistaIdDrcIdJsonStrEncryptionDetails = EncryptionUtil.encrypt(vistaIdDrcIdJsonStr, config.getEncryptionAlgorithm(), config.getEncryptionMode(), config.getEncryptionKey());
            String vistaIdDrcIdCookieValue = StringUtils.join(vistaIdDrcIdJsonStrEncryptionDetails.get(0), vistaIdDrcIdJsonStrEncryptionDetails.get(1));
            setCookie(config.getVistaIdCookieName(), vistaIdDrcIdCookieValue, config.getVistaIdCookieDomain(), true, false, config.getVistaIdCookieTTL(), response);

            // BOOLEAN IV SECURITY COOKIE
            if (config.getIsIvSecurityCookieSet()) {
                setCookie(config.getIvSecurityCookieName(), config.getIvSecurityCookieValue(), config.getIvSecurityCookieDomain(),true,false, config.getIvSecurityCookieTTL(), response);
            }
            
                
        } catch (IllegalArgumentException e){
            LOG.error("Error setting eaton virtual assistant cookies.", e);
        } catch (JSONException je){
            LOG.error("Error formatting json string before encryption.", je);
        }

        LOG.debug("AuthCookieUtil.setEatonVirtualAssistantCookies END");
    }
    
    public static String getJWTFromAuthCookie(HttpServletRequest request,
                                            AuthenticationServiceConfiguration config){
        LOG.debug("Attempting to get cookie: {}", config.getSecurityCookieId());
        Cookie securityCookie = CookieUtil.getCookie(request,config.getSecurityCookieId());

        if(securityCookie != null) {
            LOG.debug("Cookie found.  Returning value: {}", securityCookie.getValue());
            return securityCookie.getValue();
        }

        LOG.debug("Cookie not found.  Exiting method.");
        return null;
    }
    
    public static void deleteCookie(SlingHttpServletRequest request,
                                    SlingHttpServletResponse response,
                                    String cookieName,
                                    String cookieDomain) {
        LOG.debug("AuthCookieUtil.deleteCookie START");
        LOG.debug("Attempting to delete {} cookie.", cookieName);
        Cookie cookie = CookieUtil.getCookie(request, cookieName);

        if(cookie != null) {
            LOG.debug("{} cookie found. Deleting {} cookie at path: {}", cookie.getName(), cookie.getName(), cookie.getPath());
            cookie.setMaxAge(0);
            cookie.setValue("");
            cookie.setPath(CommonConstants.SLASH_STRING);
            cookie.setDomain(cookieDomain);
            CookieUtil.addCookie(cookie, response);
        } else {
            LOG.warn("{} cookie not found. Exiting method.", cookieName);
        }

        LOG.debug("AuthCookieUtil.deleteCookie END");
    }

    public static void deleteSsoUtilsCookie(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationServiceConfiguration config){
    	LOG.debug("Attempting to delete iPlanetDirectoryPro Cookie");
        Cookie ssoCookie = CookieUtil.getCookie(request,SSO_COOKIE_NAME);

        if(ssoCookie != null) {
            LOG.debug("Cookie found.  Deleting cookie at path: {}", ssoCookie.getName());

            ssoCookie.setMaxAge(0);
            ssoCookie.setValue("");
            ssoCookie.setPath(CommonConstants.SLASH_STRING);
            ssoCookie.setDomain(config.getSsoCookieDomain());
            CookieUtil.addCookie(ssoCookie, response);
            return;
        }

        LOG.debug("Cookie not found.  Exiting method.");
    }
    
	public static void deleteAuthCookie(HttpServletRequest request, HttpServletResponse response,
			AuthenticationServiceConfiguration config) {
		LOG.debug("Attempting to delete cookie: {}", config.getSecurityCookieId());
		Cookie securityCookie = CookieUtil.getCookie(request, config.getSecurityCookieId());

		if (securityCookie != null) {
			LOG.debug("Cookie found.  Deleting cookie at path: {}", securityCookie.getName());

			securityCookie.setMaxAge(0);
			securityCookie.setValue("");
			securityCookie.setPath(CommonConstants.SLASH_STRING);
			securityCookie.setDomain(config.getSecurityCookieDomain());
			CookieUtil.addCookie(securityCookie, response);
			return;
		}

		LOG.debug("Cookie not found.  Exiting method.");
	}
    
    /**
     *
     * Method to delete publish server name cookie
     * @param request
     *            the request
     * @param response
     *            the response
     */
    public static void deletePublishServerNameCookie(HttpServletRequest request, HttpServletResponse response) {
		LOG.debug("Attempting to delete cookie: " + PUBLISH_SERVER_NAME_COOKIE_ID);
		Cookie publishServerNameCookie = CookieUtil.getCookie(request, PUBLISH_SERVER_NAME_COOKIE_ID);

		if (publishServerNameCookie != null) {
			LOG.debug("Cookie found.  Deleting cookie at path: {}", publishServerNameCookie.getName());

			publishServerNameCookie.setMaxAge(0);
			publishServerNameCookie.setValue("");
			publishServerNameCookie.setPath(CommonConstants.SLASH_STRING);
			CookieUtil.addCookie(publishServerNameCookie, response);
			return;
		}

		LOG.debug("Cookie not found.  Exiting method.");
	}
    
    /**
     *
     * Method to set cookie in the response
     * @param key
     *            the cookie name
     * @param value
     *            the cookie value
     * @param response
     *            the response
     */
    public static void setCookie(String key, String value, SlingHttpServletResponse response){
        final Cookie cookie = new Cookie(key, value);
        cookie.setPath(CommonConstants.SLASH_STRING);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        CookieUtil.addCookie(cookie, response);
        LOG.debug("{} cookie set to {}", key, value);
    }

    public static void setCookie(String key, String value, String domain, SlingHttpServletResponse response){
        final Cookie cookie = new Cookie(key, value);
        cookie.setPath(CommonConstants.SLASH_STRING);
        cookie.setDomain(domain);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        CookieUtil.addCookie(cookie, response);
        LOG.debug("{} cookie set to {}", key, value);
    }

    public static void setCookie(String key, String value, String domain, boolean isSecure, boolean isHttpOnly, int maxAge, SlingHttpServletResponse response){
        final Cookie cookie = new Cookie(key, value);
        cookie.setPath(CommonConstants.SLASH_STRING);
        cookie.setDomain(domain);
        cookie.setSecure(isSecure);
        cookie.setHttpOnly(isHttpOnly);
        cookie.setMaxAge(maxAge);
        CookieUtil.addCookie(cookie, response);
        LOG.debug("{} cookie set to {}", key, value);
    }
    /**
     *
     * Method to read cookie
     * @param cookieId
     *            the cookie id
     * @param request
     *            the request
     * @return cookie
     */
    public static String getCookie(HttpServletRequest request,
                                              String cookieId){
        LOG.debug("Attempting to get cookie: {} ", cookieId);
        Cookie cookie = CookieUtil.getCookie(request,cookieId);

        if(cookie != null) {
            LOG.debug("Cookie found.  Returning value: {}", cookie.getValue());
            return cookie.getValue();
        }

        LOG.debug("Cookie not found.  Exiting method.");
        return null;
    }
}
