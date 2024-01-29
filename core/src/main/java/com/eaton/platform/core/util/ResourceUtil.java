package com.eaton.platform.core.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ResourceUtil class contains all the utility methods to manipulate resource object.
 */
public class ResourceUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceUtil.class);

    /**
     * getPropertyValue utility method gives back the value of JCR property under a resource.
     * @param resource current resource object
     * @param propertyKey JCR Property Key
     * @return Property Value
     */
    public static String getPropertyValue(Resource resource, String propertyKey) {
        if(resource != null){
            ValueMap valueMap = org.apache.sling.api.resource.ResourceUtil.getValueMap(resource);
            if(valueMap.get(propertyKey) != null){
                return valueMap.get(propertyKey, StringUtils.EMPTY);
            }
        }
        return StringUtils.EMPTY;
    }
}
