package com.eaton.platform.core.services;

import com.github.benmanes.caffeine.cache.LoadingCache;

import java.util.List;
import java.util.Map;

/**
 * The Interface EatonConfigService.
 */
public interface CountryLangCodeConfigService {
    /**
     * @param domainName from current request
     * @return the list of country language codes
     */
	List<String> getCountryLangCodesList(String domainName);

    /**
     * @param domainName from current request
     * @return the Map of country language codes
     */
    Map<String, List<String>> getCountryLangCodesMap(String domainName);

    LoadingCache<String, Map<String, List<String>>> getCountryLangCodeCache();

    void setCountryLangCodeCache(LoadingCache<String, Map<String, List<String>>> countryLangCodeCache);

    int getMaxCacheDuration();

    int getMaxCacheSize();

    String getDomainXDefaultHrefLangCode(final String domainName);
    
    String[] getExcludeCountryList();

}

