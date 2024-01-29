package com.eaton.platform.core.util;

import com.day.cq.dam.api.Asset;
import com.eaton.platform.core.constants.CommonConstants;

import java.util.Locale;

/**
 * MultiLingualUtil.java
 * About : Having utility methods to get multi lingual content from Assets.
 * Author: Satya
 */
public class MultiLingualUtil {

    /**
      Get MultiLingual property key from current language code. If not exist,return the default key.
     * @param propertyKey JCR Property Name
     * @param locale current page locale
     * @param asset current asset
     * @return locale specific key
     *
     */
    public static String getMultiLingualPropertyKeyByLocale(String propertyKey, Locale locale, Asset asset){
        if(asset!= null && locale!= null){
            String multiLingualKey = propertyKey + CommonConstants.HYPHEN + locale.getLanguage() +
                    CommonConstants.HYPHEN + locale.getCountry().toLowerCase(locale);
            if(null != asset.getMetadataValue(multiLingualKey)){
                return multiLingualKey;
            }
        }
        // Return default key
        return propertyKey;
    }
}
