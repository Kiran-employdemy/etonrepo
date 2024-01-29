/**
 *
 * @author ICF
 * On 19th Jan 2021
 * EAT- 4495
 *
 * */
package com.eaton.platform.core.util;

import com.eaton.platform.core.constants.CommonConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.regex.Pattern;

/**    
 * RenderConditionLanguagePageUtil class to be used in eatonBasePage dialog
 **/
public class RenderConditionLanguagePageUtil {

    private static final String PAGE_PROPERTIES = "wcm/core/content/sites/properties";
    private static final String REFERER = "Referer";
    private static final Pattern pattern = Pattern.compile(".html");
    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(RenderConditionLanguagePageUtil.class);

    /**    
     * isHREFLangVisible method
     * @param slingHttpServletRequest object
     * @param httpServletRequest object
     * @returns boolean
     **/
    public static boolean isHREFLangVisible(
            final SlingHttpServletRequest slingHttpServletRequest,
            final HttpServletRequest httpServletRequest) {

        LOGGER.debug("RenderConditionLanguagePageUtil: Enters the isHREFLangVisible() method");

        if (slingHttpServletRequest == null || httpServletRequest == null) {
            throw new IllegalArgumentException("One of the passed parameters is null.");
        }

        boolean isLanguagePage = false;
        String pagePath;
        final Map<String, String> tagPathConfigs = CommonConstants.siteRootPathConfigForExternalizer;

        /* the dialog is a page properties dialog */
        if (StringUtils.contains(httpServletRequest.getPathInfo(), PAGE_PROPERTIES)) {
            pagePath = httpServletRequest.getParameter(CommonConstants.ITEM_PARAM);
            if(StringUtils.isNotBlank(pagePath)) {
                isLanguagePage = checkLanguagePagePath(pagePath, tagPathConfigs, 5);
            }
        } else {
            String refererStr = slingHttpServletRequest.getHeader(REFERER);
            if(StringUtils.isNotBlank(refererStr)) {
                String[] refererArr = pattern.split(refererStr);
                if (refererArr.length > 1) {
                    pagePath = refererArr[1];
                    if(StringUtils.isNotBlank(pagePath)) {
                        isLanguagePage = checkLanguagePagePath(pagePath, tagPathConfigs, 4);
                    }
                }
            }
        }

        LOGGER.debug("RenderConditionLanguagePageUtil: Exits the isHREFLangVisible() method");
        return isLanguagePage;
    }

    private static boolean checkLanguagePagePath(String pagePath, Map<String, String> tagPathConfigs, int pageLength) {
        boolean isLanguagePage = false;
        String[] pagePathArr = pagePath.split(CommonConstants.SLASH_STRING);
        if (pagePathArr.length > 3) {
        int pageNameLength = pagePathArr[3].length();
        for (Map.Entry<String, String> marketTagEntry : tagPathConfigs.entrySet()) {
            if (pagePath.startsWith(marketTagEntry.getKey()) && pagePathArr.length == pageLength && pageNameLength == 2) {
                isLanguagePage = true;
            }
        }
    }
        return isLanguagePage;
    }
}
