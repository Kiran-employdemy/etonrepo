package com.eaton.platform.integration.auth.filters;

import com.day.cq.dam.commons.util.DamUtil;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.integration.auth.filters.deciders.AbstractPageLoginUrlDecider;
import com.eaton.platform.integration.auth.filters.deciders.PageDeciderFactory;
import com.eaton.platform.integration.auth.services.AuthenticationServiceConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;

import javax.servlet.http.Cookie;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static com.eaton.platform.core.constants.CommonConstants.*;

public class LoginUrlFactory {
    private final AuthenticationServiceConfiguration authenticationServiceConfig;

    public LoginUrlFactory(AuthenticationServiceConfiguration authenticationServiceConfig) {
        this.authenticationServiceConfig = authenticationServiceConfig;
    }

    public String generateRedirectToLoginPage(AuthorizationFilterActions filterActions) throws MalformedURLException {
        Resource resource = filterActions.getMainResourceIfNeeded();
        if (DamUtil.isAsset(resource)) {
            return returnLoginUrlForAsset(resource, filterActions);
        }
        Page page = filterActions.getPage();
        if (page == null) {
            return StringUtils.EMPTY;
        }
        return returnLoginUrlForPage(page);
    }

    private String returnLoginUrlForAsset(Resource resource, AuthorizationFilterActions filterActions) throws MalformedURLException {
        Resource assetMetadata = resource.getChild(JCR_CONTENT_METADATA);
        if (assetMetadata == null) {
            return StringUtils.EMPTY;
        }
        Cookie cookie = filterActions.getRedirectCookie();
        String oktaLoginURI = authenticationServiceConfig.getOktaLoginURI();
        if (cookie == null) {
            return oktaLoginURI;
        }
        String decodedLocale = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
        return constructLoginUrl(oktaLoginURI, decodedLocale);

    }

    private String returnLoginUrlForPage(Page page) throws MalformedURLException {
        PageDeciderFactory deciderFactory = new PageDeciderFactory(page,this.authenticationServiceConfig);
        for(AbstractPageLoginUrlDecider decider : deciderFactory.getAllDeciders()){
            if(decider.conditionMatched()){
                return decider.redirectTo();
            }
        }
        String loginPageURL = authenticationServiceConfig.getOktaLoginURI();
        Locale locale = page.getLanguage();
        String country = determineCountry(locale, page);
        String countryLanguage = String.format("/%s/%s", country, locale.toLanguageTag().toLowerCase(Locale.US));
        return constructLoginUrl(loginPageURL, countryLanguage);
    }

    private static String determineCountry(Locale locale, Page page) {
        String countryFromLocale = locale.getCountry().toLowerCase(Locale.US);
        String withoutContentEatonPart = page.getPath().substring(CONTENT_ROOT_FOLDER.length());
        String countryFromPath = withoutContentEatonPart.substring(0, withoutContentEatonPart.indexOf('/'));
        if (!countryFromPath.equals(countryFromLocale)) {
            return countryFromPath;
        }
        return countryFromLocale;
    }

    private static String constructLoginUrl(String loginPageURL, String countryLanguage) throws MalformedURLException {
        URL url = new URL(loginPageURL);
        return new URL(url.getProtocol(), url.getHost(), countryLanguage + SLASH_STRING + loginPageURL.substring(loginPageURL.lastIndexOf('/') + 1)).toString();
    }
}
