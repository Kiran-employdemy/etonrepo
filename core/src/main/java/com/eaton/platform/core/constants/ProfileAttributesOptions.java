package com.eaton.platform.core.constants;

/**    
 * Vanity URL Status    
 **/
public enum ProfileAttributesOptions {
    GIVEN_NAME("givenName"),
    FIRST_NAME("firstName"),
    EMAIL("emailAddress"),
    CURRENT_EMAIL("currentEmailAddress"),
    LAST_NAME("lastName"),
    COUNTY_CODE("country"),
    PRODUCT_CATEGORY_TAGS("productCategories"),
    APP_ACCESS_TAGS("applicationAccess"),
    BUSINESS_UNIT_TAGS("businessUnit"),
    USER_TYPE_TAGS("userTypes"),
    USER_TYPE("userType"),
    COMPANY_NAME("companyName"),
    COMPANY_ADDRESS("companyAddress"),
    COMPANY_CITY("city"),
    COMPANY_STATE("companyState"),
    COMPANY_ZIP_CODE("companyZipCode"),
    POSTAL_CODE("postalCode"),
    COMPANY_BUSINESS_PHONE("businessPhone"),
    COMPANY_BUSINESS_FAX("businessFax"),
    COMPANY_MOBILE_NUMBER("mobilePhone"),
    SUPPLIER_COMPANY_ID("eatonSupplierCompanyId");

    private String value;

    ProfileAttributesOptions(final String profileAttributes)	{
        this.value = profileAttributes;
    }

    public String getValue() {
        return value;
    }
}
