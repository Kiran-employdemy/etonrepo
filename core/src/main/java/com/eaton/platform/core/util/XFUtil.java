package com.eaton.platform.core.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import com.day.cq.wcm.api.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  Description: It is a Util used for xf method
 * @version 1.0
 * @since 2023
 *
 */
public class XFUtil {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(XFUtil.class);

    private static final int LOCALE_LEVEL=3;
    private static final int COUNTRY_LEVEL = 2;
    
    /**
   	 * Instantiates a new common util.
   	 */
    public XFUtil() {
        LOGGER.debug("Inside CommonUtil constructor");
   	}
    /**
     * This method updates links in componenst authors in XFs
     *
     * @param page page
     * @param resolver resolver
     * @param link link
     * @return the link
     */
    public static String updateHTMLLinkInXF(Page page, ResourceResolver resolver, String link) {
        Page localePage = page.getAbsoluteParent(LOCALE_LEVEL);
        Page countryPage = page.getAbsoluteParent(COUNTRY_LEVEL);
        String localeName = StringUtils.EMPTY;
        String countryName = StringUtils.EMPTY;
        String localeNameForAuthoredLink = StringUtils.EMPTY;
        String countryNameForAuthoredLink = StringUtils.EMPTY;
        String linkPath = "";
        Resource resourceAtConfiguredLInk = resolver.getResource(link);
        if (resourceAtConfiguredLInk != null) {
            Page pageAtConfiguredLInk = resourceAtConfiguredLInk.adaptTo(Page.class);
            if (pageAtConfiguredLInk != null) {
                LOGGER.debug("Inside IF condition : {}");
                Page countryPageForAuthoredLInk = pageAtConfiguredLInk.getAbsoluteParent(COUNTRY_LEVEL);
                countryNameForAuthoredLink = countryPageForAuthoredLInk.getName();
                Page localePageForAuthoredLink = pageAtConfiguredLInk.getAbsoluteParent(LOCALE_LEVEL);
                localeNameForAuthoredLink = localePageForAuthoredLink.getName();
                LOGGER.debug("Locale Name For AuthoredLink:{}", localeNameForAuthoredLink);
            }
        }
        if (localePage != null && countryPage != null) {
            localeName = localePage.getName();
            countryName = countryPage.getName();
        }
        linkPath = transformURL( resolver,localeName,countryName,link,localeNameForAuthoredLink,countryNameForAuthoredLink);
    
        return linkPath;
    }

    /**
     * This method updates links
     *
     * @param localeName localeName
     * @param resolver resolver
     * @param link link
     * @param countryName countryName
     * @param localeNameForAuthoredLink localeNameForAuthoredLink
     * @param countryNameForAuthoredLink countryNameForAuthoredLink
     * @return the link
     */
    public static String transformURL(ResourceResolver resolver, String localeName, String countryName, String link,
            String localeNameForAuthoredLink, String countryNameForAuthoredLink) {
        String originalLInkPath = StringUtils.EMPTY;
        if (!localeName.isEmpty() && !countryName.isEmpty()
                && link.contains(localeName) && link.contains(countryName)) {
            originalLInkPath = link;
            return originalLInkPath;
        } else if (!localeName.isEmpty() && !countryName.isEmpty()
                && !localeNameForAuthoredLink.isEmpty()) {
            String localeReplacedLink = link.replace(localeNameForAuthoredLink, localeName);
            if (localeReplacedLink != null) {
                String countryReplacedLink = localeReplacedLink.replaceFirst(countryNameForAuthoredLink, countryName);
                Resource modifiedResource = resolver.getResource(countryReplacedLink);
                if (modifiedResource != null) {
                    originalLInkPath = modifiedResource.getPath();
                    return originalLInkPath;
                }
            }

        } else {
            return originalLInkPath;
        }
        return StringUtils.EMPTY;
    }

}
