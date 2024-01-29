package com.eaton.platform.core.search.api;

import com.eaton.platform.core.models.SiteResourceSlingModel;
import org.apache.sling.api.SlingHttpServletRequest;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Locale;

/**
 * The factory for creating the different mapping contexts.
 * Preparing this context with all the data the mapping needs so that it can be used in one go while iterating once over the facets and results.
 *
 * @param <F> The Facet group mapping context
 * @param <R> The Result mapping context
 * @param <T> The translation context
 * @param <V> The Facet value id provider (see note in {@link AssetSearchResponseMappingContext )}
 */
public interface AssetSearchResponseMappingContextFactory<F extends FacetGroupMappingContext<T>, R extends AssetSearchResultMappingContext<T>
        , T extends TranslationMappingContext, V extends FacetValueIdsProvider> {
    /**
     * Creates the context
     *
     * @param request    to use for extracting goodies like ResourceResolver, the ResourceBundle, etc...
     * @param siteConfig to use to get some global/local config
     * @param locale     to use for translating to the correct language
     * @return the context
     */
    AssetSearchResponseMappingContext<F, R, V> create(SlingHttpServletRequest request, SiteResourceSlingModel siteConfig, Locale locale);

    /**
     * Sets the clock used when logic requires to test against the current date
     *
     * @param now to use
     */
    void setNow(LocalDate now);

    void setZoneId(ZoneId zoneId);
}
