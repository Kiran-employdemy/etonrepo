package com.eaton.platform.core.constants;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
/**
 * Tag.java
 * --------------------------------------
 * This class contains the Map that maps the new metadata property to eaton tag names
 * --------------------------------------
 * Author: Ceirra Schuette (ceirra@freedomdam.com)
 */
public class TagMap {
    private static final Map<String, String> tagSyncList;

    static {
        Map<String, String> tempList = new HashMap<>();
        tempList.put("eaton:language", "xmp:eaton-language");
        tempList.put("eaton:eaton-geography", "xmp:eaton-geography");
        tempList.put("eaton:news-releases/Topic", "xmp:eaton-topic");
        tempList.put("eaton:eaton-persona", "xmp:eaton-persona");
        tempList.put("eaton:eaton-audience", "xmp:eaton-audience");
        tempList.put("eaton:eaton-customer-journey-stage", "xmp:eaton-customer-journey-stage");
        tempList.put("eaton:resources", "xmp:eaton-content-type");
        tempList.put("eaton:b-line-submittal-builder", "xmp:eaton-b-line-submittal-builder");
        tempList.put("eaton:eaton-brand", "xmp:eaton-eaton-brand");
        tempList.put("eaton:search-tabs", "xmp:eaton-search-tabs");
        tempList.put("eaton:news-releases", "xmp:eaton-news");
        tempList.put("eaton:product-taxonomy", "xmp:eaton-product-taxonomy");
        tempList.put("eaton:services", "xmp:eaton-services-taxonomy");
        tempList.put("eaton:support-taxonomy", "xmp:eaton-support-taxonomy");
        tempList.put("eaton:product-attributes", "xmp:eaton-product-attributes");
        tempList.put("eaton:supermarkets", "xmp:eaton-segment");
        tempList.put("eaton-secure:product-category", "productCategories");
        tempList.put("eaton-secure:accounttype", "accountType");
        tempList.put("eaton-secure:application-access", "applicationAccess");
        tempList.put("eaton:country", "xmp:eaton-country");
        tempList.put("eaton:eaton-business-unit-function-division", "xmp:eaton-business-unit-function-division");
        tempList.put("eaton-secure:partner-programme-type", "xmp:eaton-partner-program-and-tier-level");
        tempList.put("eaton:myeaton-taxonomy", "xmp:eaton-myeaton-taxonomy");
        tempList.put("eaton:company-taxonomy", "xmp:eaton-company-taxonomy");
        tagSyncList =  Collections.unmodifiableMap(tempList);
    }
    /**
     * Getter for tagSyncList
     * @return tagSyncList
     */
    public Map<String, String> getTagSyncList() {
        return tagSyncList;
    }
}

