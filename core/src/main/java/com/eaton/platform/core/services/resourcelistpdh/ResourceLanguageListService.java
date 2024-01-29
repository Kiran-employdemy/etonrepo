package com.eaton.platform.core.services.resourcelistpdh;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Service for giving access to the ResourceLanguage json
 */
public interface ResourceLanguageListService {
    /**
     * @param key    to map to the resource group name
     * @param locale to use with mapping
     * @return the found group name
     */
    String resourceGroupName(String key, Locale locale);


    /**
     * @param locale to get the group name and the appropriate key(s)
     * @return map of resource names to keys for a given locale
     */
    Map<String, List<String>> getResourceLanguageList(Locale locale);
}
