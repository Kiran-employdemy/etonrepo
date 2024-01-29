package com.eaton.platform.core.search.api;

/**
 * The context for translation of values used while mapping
 */
public interface TranslationMappingContext {
    /**
     * Locates the Tag for given tagId
     *
     * @param tagId to use for locating the tag, make sure that the tag is in format eaton:parent_Tag/child-tag, etc...
     * @param defaultValue fallback value if the tag is not found
     * @return tagTitle or the defaultValue
     */
    String locateTagAndRetrieveTitle(String tagId, String defaultValue);

    /**
     * Translates the given String
     * @param stringToTranslate to use for translation
     * @return the translated value
     */
    String retrieveFromI18n(String stringToTranslate);
}
