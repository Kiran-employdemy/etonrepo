package com.eaton.platform.integration.endeca.bean.factories;

import com.eaton.platform.core.constants.CommonConstants;
import org.apache.commons.lang3.StringUtils;

/**
 * Abstract implementation of the SecureFilterBeanCreationStrategy to house common methods used in other implementations.
 */
public abstract class AbstractSecureFilterBeanCreationStrategy implements SecureFilterBeanCreationStrategy {
    /**
     * Extracts the tag value by removing rootTag + / from tag and returning the remainder
     * @return the extracted value
     */
    protected String extractSimpleTagValue(String tag, String rootTag) {
        return tag.replace(rootTag + CommonConstants.SLASH_STRING, StringUtils.EMPTY);
    }

    /**
     * extracts last level of the tag, it will extract after the last /
     * @return the extracted tag name
     */
    protected String extractLastLevel(String tag, String rootTag) {
        String extractedTagName = extractSimpleTagValue(tag, rootTag);
        return extractedTagName.substring(extractedTagName.indexOf(CommonConstants.SLASH_STRING) + 1);
    }

    /**
     * extracts the 2 levels but puts an _ between the 2
     * @return the extracted tag name
     */
    protected String extractReplacingUnderscoreBetweenLevels(String tag, String rootTag) {
        String extractedTagName = extractSimpleTagValue(tag, rootTag);
        return extractedTagName.replace(CommonConstants.SLASH_STRING, CommonConstants.UNDERSCORE);
    }
}
