package com.eaton.platform.integration.endeca.enums;

/**
 * Enumeration for keeping the different secure filter types in one place and providing the mapping between
 * filter name and the root tag name
 */
public enum SecureFilter {
    ACCOUNT_TYPE("SEC-Account-Type", "eaton-secure:accounttype"),
    APPLICATION_ACCESS("SEC-Application-Access", "eaton-secure:application-access"),
    BUSINESS_UNIT("SEC-Company", "eaton-secure:business-group"),
    PRODUCT_CATEGORIES("SEC-Product-Categories", "eaton-secure:product-category"),

    COUNTRY("SEC-country", "eaton:country"),
    PARTNER_PROGRAMME_TYPE_AND_TIER_LEVEL("SEC-Partner-Program-And-Tier-Level", "eaton-secure:partner-programme-type");

    private final String value;
    private final String mappedRootTag;

    SecureFilter(final String value, String mappedRootTag) {
        this.value = value;
        this.mappedRootTag = mappedRootTag;
    }

    public String getValue() {
        return value;
    }

    public String getMappedRootTag() {
        return mappedRootTag;
    }


}
