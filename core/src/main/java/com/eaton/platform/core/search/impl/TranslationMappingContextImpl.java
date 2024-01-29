package com.eaton.platform.core.search.impl;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.eaton.platform.core.search.api.TranslationMappingContext;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Default implementation for the Translation mapping context
 */
public class TranslationMappingContextImpl implements TranslationMappingContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(TranslationMappingContextImpl.class);
    private final TagManager tagManager;
    private final Locale locale;
    private final ResourceBundle i18nResourceBundle;

    /**
     * Constructor taking as arguments:
     * @param request to set
     * @param locale to set
     */
    public TranslationMappingContextImpl(SlingHttpServletRequest request, Locale locale) {
        this.tagManager = request.getResourceResolver().adaptTo(TagManager.class);
        this.locale = locale;
        this.i18nResourceBundle = request.getResourceBundle(locale);
    }
    @Override
    public String locateTagAndRetrieveTitle(String tagId, String defaultTitle) {
        String unescapedTagId = StringEscapeUtils.unescapeHtml(tagId);
        Tag resolvedTag = tagManager.resolve(unescapedTagId);
        if (resolvedTag == null) {
            return defaultTitle;
        }
        String localizedTitle = resolvedTag.getLocalizedTitle(locale);
        if (localizedTitle == null) {
            return resolvedTag.getTitle();
        }
        return localizedTitle;
    }

    @Override
    public String retrieveFromI18n(String label) {
        try {
            return i18nResourceBundle.getString(label);
        } catch (MissingResourceException missingResourceException) {
            LOGGER.error("The translation for key {} is not found in the system, returning it. exception was {}", label, missingResourceException.getMessage());
            return label;
        }
    }
}
