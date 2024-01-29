package com.eaton.platform.core.services.resourcelistpdh;

import java.util.List;
import java.util.Locale;
import java.util.Map;
/**
 * POJO used to parse json and providing convenient methods to the parsed data
 */
public class ResourceLanguage {
    private List<String> resourceKeys;
    private Map<String,String> languageMap;

    public List<String> getResourceKeys() {
        return resourceKeys;
    }

    /**
     * @param locale to get the resource name for
     * @return the one for locale, if not found the one for en_US
     */
    public String nameFor(Locale locale) {
        String localeKey = locale.toLanguageTag().toLowerCase(Locale.US);
        if (languageMap.containsKey(localeKey)) {
            return languageMap.get(localeKey);
        }
        return nameFor(Locale.US);
    }
}
