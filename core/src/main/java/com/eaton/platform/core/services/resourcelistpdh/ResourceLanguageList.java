package com.eaton.platform.core.services.resourcelistpdh;

import org.apache.commons.lang3.StringUtils;

import java.text.Collator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * POJO used to parse json and providing convenient methods to the parsed data
 */
public class ResourceLanguageList {
    private List<ResourceLanguage> resourceLanguages;

    /**
     * @param key to check existence for
     * @return true if it does
     */
    public boolean hasKey(String key) {
        return resourceLanguages.stream().anyMatch(resourceLanguage -> resourceLanguage.getResourceKeys().contains(key));
    }

    public List<ResourceLanguage> getResourceLanguages() {
        return resourceLanguages;
    }

    /**
     * @param key to use
     * @param locale to use
     * @return the resource group name for given key and locale
     */
    public String getResourceGroupName(String key, Locale locale) {
        Optional<ResourceLanguage> presentOrNot = resourceLanguages.stream().filter(resource -> resource.getResourceKeys().contains(key)).findFirst();
        if(presentOrNot.isPresent()) {
            ResourceLanguage resourceLanguage = presentOrNot.get();
            return resourceLanguage.nameFor(locale);
        }
        return StringUtils.EMPTY;
    }

    /**
     * @param locale to get the Map of resource name and appropriate keys
     * @return map
     */
    public Map<String, List<String>> getResourceLanguagesFor(Locale locale) {
        Collator collator = Collator.getInstance(locale);
        collator.setStrength(Collator.PRIMARY);
        Map<String, List<String>> resourceLanguageMap = new TreeMap<>(collator);
        for(ResourceLanguage resourceLanguage : resourceLanguages ) {
            resourceLanguageMap.put(resourceLanguage.nameFor(locale), resourceLanguage.getResourceKeys());
        }
        return resourceLanguageMap;
    }
}
