package com.eaton.platform.core.services.vanity;

import org.apache.sling.api.SlingHttpServletRequest;

/**
 * Interface LocaleMappingService
 **/
public interface LocaleMappingService {

    /**
     * This method returns the language site root path based on the cookie set on the given request
     *
     * @param request the request object
     * @return root path of the languagecountry site.
     *
     */
    String  getLanguageSiteRootPagePath(SlingHttpServletRequest request);

    /**
     * This method returns the language site root path based on the cookie set on the given request
     *
     * @param request the request object
     * @param countryLangCode to check whether to send full path or not
     * @return root path of the languagecountry site.
     *
     */
    String  getLanguageSiteRootPagePath(SlingHttpServletRequest request, boolean countryLangCode);
}
