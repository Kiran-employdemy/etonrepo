package com.eaton.platform.core.services.impl;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.TermsAndConditionsService;
import com.eaton.platform.core.services.config.TermsAndConditionsConfig;
import com.eaton.platform.integration.auth.constants.AuthConstants;
import com.eaton.platform.integration.auth.models.AuthenticationToken;
import com.eaton.platform.integration.auth.models.UserProfile;
import com.eaton.platform.integration.auth.util.AuthCookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Component(service = TermsAndConditionsService.class, immediate = true)
@Designate(ocd = TermsAndConditionsConfig.class)
public class TermsAndConditionsServiceImpl implements TermsAndConditionsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TermsAndConditionsServiceImpl.class);


    @Reference
    private AdminService adminService;


    private Map<String,String> languagePaths = new HashMap<>();
    private String defaultPath = StringUtils.EMPTY;
    private boolean enable = false;

    @Activate
    protected void activate(TermsAndConditionsConfig config){
        for(String langMapPath : config.languagePaths()) {
            String[] pathSplit = langMapPath.split("\\|");
            if(pathSplit.length == 2) {
                languagePaths.put(pathSplit[0],pathSplit[1]);
            }
        }
        defaultPath = config.defaultPath();
        enable = config.enable();
    }

    @Override
    public String getTermAndConditionPath(SlingHttpServletRequest request) {
        // if we are using the request we will get the language base on the language cookie
        String lang = AuthCookieUtil.getCookie(request, AuthConstants.LOGIN_LANGUAGE_COOKIE);
        return languagePaths.getOrDefault(lang,defaultPath);
    }

    @Override
    public String getTermAndConditionPath(Resource resource) {
        // if we are getting from resource we will get the language base on page
        try (ResourceResolver resourceResolver = adminService.getReadService()) {
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            Page page;
            if(null != resource && null != pageManager && null != (page = pageManager.getContainingPage(resource))) {
                LOGGER.info("******* Resource path: {} *********",resource.getPath());
                Locale locale = page.getLanguage();
                if (null != locale && null != locale.toLanguageTag()) {
                    String lang = locale.toLanguageTag();
                    LOGGER.info("******* language tag: {} *********",lang);
                    return languagePaths.getOrDefault(lang, defaultPath);
                }
            }
            return defaultPath;
        }
    }

    public boolean shouldRedirect(AuthenticationToken authToken) {
        if(!enable) {
            return false;
        }
         boolean shouldRedirect = false;
        // now we need to check
        UserProfile userProfile;
        if(null != authToken && null != ( userProfile = authToken.getUserProfile()) ){
            // token isn't empty and user profile isn't null
            shouldRedirect = checkRedirect(userProfile);
        }
        return shouldRedirect;
    }

    private boolean checkRedirect(UserProfile userProfile){
        boolean redirect = false;
        if(null == userProfile.getEatonEshopEulaAcceptDate()
                && null == userProfile.getEatonEulaAcceptDate()) {
            LOGGER.error("User need to be redirected to TC");
            redirect = true;
        } else {
            LOGGER.error("getEatonEshopEulaAcceptDate is not null and getEatonEulaAcceptDate is not null user should not be redirect it to TC");
        }
        return redirect;
    }

    public String getDefaultPath() {
        return this.defaultPath;
    }
}
