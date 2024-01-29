package com.eaton.platform.core.services.vanity.impl;

import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.vanity.EatonVanityConfigService;
import com.eaton.platform.core.services.vanity.LocaleMappingService;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * LocaleMappingServiceImpl
 **/
@Component(service = LocaleMappingService.class, immediate = true,
        property = {
                AEMConstants.SERVICE_VENDOR_EATON,
                AEMConstants.SERVICE_DESCRIPTION + "LocaleMappingServiceImpl",
                AEMConstants.PROCESS_LABEL + "LocaleMappingServiceImpl"
        })
public class LocaleMappingServiceImpl implements LocaleMappingService {

    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(LocaleMappingServiceImpl.class);

    public static final String REDIRECT_URL_COOKIE_NAME = "etn_country_selector_redirect_url";
    public static final String DOT_INSIDE_BRACKETS = "[.]" ;
    public static final Pattern URL_PATTERN = Pattern.compile(DOT_INSIDE_BRACKETS, 0);

    @Reference
    private EatonVanityConfigService eatonVanityConfigService;

    /**
     * Called when the the service is activated/updated.
     */
    @Activate
    protected final void activate(){
        LOG.debug("LocaleMappingServiceImpl :: activate()");
    }

    /**
     * method returns the language site root path based on the cookie set
     * @param request
     * @return rootPath
     */
    @Override
    public String getLanguageSiteRootPagePath(SlingHttpServletRequest request) {
        LOG.debug("LocaleMappingServiceImpl :: getLanguageSiteRootPagePath() :: Started");
        String rootPagePath = StringUtils.EMPTY;
        final String redirectionUrl = getCookieValue(request,REDIRECT_URL_COOKIE_NAME );
        final String requestDomain = request.getServerName();
        final String[] topLevelDomain = eatonVanityConfigService.getVanityConfig().getTopLevelDomainConfig();
        final String decodedUrl = CommonUtil.decodeURLString(redirectionUrl);
        if(StringUtils.isNotBlank(redirectionUrl)){
            final String[] urlParts = decodedUrl.split(CommonConstants.SLASH_STRING );
            if(urlParts.length >= 5){
                final String countryCode = urlParts[3];
                final String languageCode = urlParts[4].split(DOT_INSIDE_BRACKETS, 0)[0];
                rootPagePath = CommonConstants.CONTENT_ROOT_FOLDER +
                        countryCode + CommonConstants.SLASH_STRING + languageCode ;
            } else {
                rootPagePath =  Arrays.stream(topLevelDomain).parallel().filter(requestDomain::contains).findFirst().toString().split("|")[0];
            }
        }
        LOG.debug("LocaleMappingServiceImpl :: getLanguageSiteRootPagePath() :: Exit");
        return rootPagePath;
    }

    /**
     * method returns the language site root path based on the cookie set
     * @param request
     * @param countryLangCode
     * @return rootPath
     */
    @Override
    public String getLanguageSiteRootPagePath(SlingHttpServletRequest request, boolean countryLangCode) {
        LOG.debug("LocaleMappingServiceImpl :: getLanguageSiteRootPagePath() :: Started");
        String rootPagePath = StringUtils.EMPTY;
        final String redirectionUrl = getCookieValue(request,REDIRECT_URL_COOKIE_NAME );
        final String decodedUrl = CommonUtil.decodeURLString(redirectionUrl);
        if(StringUtils.isNotBlank(redirectionUrl)){
            final String[] urlParts = decodedUrl.split(CommonConstants.SLASH_STRING );
            if(urlParts.length >= 5){
                final String countryCode = urlParts[3];
                final String languageCode = URL_PATTERN.split(urlParts[4])[0];
                rootPagePath = countryCode + CommonConstants.SLASH_STRING + languageCode ;
            }
        }
        LOG.debug("LocaleMappingServiceImpl :: getLanguageSiteRootPagePath() :: Exit");
        return rootPagePath;
    }

    /**
     * Method to get cookie value from the request
     * @param request
     * @param cookieName
     * @return cookieValue
     */
    private String getCookieValue(SlingHttpServletRequest request, String cookieName) {
        LOG.debug("LocaleMappingServiceImpl :: getCookieValue() :: Started");
        if(null != request.getCookie(cookieName)) {
            LOG.debug("LocaleMappingServiceImpl :: getCookieValue() - Cookie value returned :: Exit");
            return Arrays.stream(request.getCookies())
                    .filter(c -> c.getName().equals(cookieName))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElse(StringUtils.EMPTY);
        }
        LOG.debug("LocaleMappingServiceImpl :: getCookieValue() - No Cookie found :: Exit");
        return StringUtils.EMPTY;
    }
}
