package com.eaton.platform.core.services;

public interface MetaTagsLinkDomainService {
    boolean isCountryExcluded(String countryCode);
    String getCustomDomainKey(String countryCode);
}