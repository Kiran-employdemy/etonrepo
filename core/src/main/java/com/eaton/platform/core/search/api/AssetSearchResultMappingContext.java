package com.eaton.platform.core.search.api;

import java.time.LocalDate;


/**
 * The context for mapping search result record data
 * @param <T> is the context used for translation
 */
public interface AssetSearchResultMappingContext<T extends TranslationMappingContext> {
    /**
     * determines the filetype using
     * @param dcFormat is the file type that comes from dam
     * @return the mapped fileType
     */
    String determineFileType(String dcFormat);

    /**
     * @return the image url factory for the default image, generally used when a fallback image url needs to be crafted
     */
    DefaultImageUrlFactory getDefaultImageUrlCreation();

    /**
     * To use when the language we receive back from the search engine is not localized
     * This method locates the Language tag in AEM and returns the localized title
     * @param language to localize
     * @return the localized language, or the input if it doesn't find the tag
     */
    String locateLanguageTagAndReturnLocalizedTitle(String language);

    /**
     * Converts a given file size in bites to human-readable format
     * @param fileSize in bites
     * @return the size in human readable form: 1 MB, etc...
     */
    String convertToHumanReadableFileSize(String fileSize);

    /**
     * @return indicates if bulk download is enabled
     */
    Boolean isBulkDownloadEnabled();

    /**
     * @return number of days to use for evaluating the new badge
     */
    Integer getNumberOfDays();

    /**
     * @return returns the date of today
     */
    LocalDate getNow();

    /**
     * To check if bronOnDay exists on a resource (used to determine the updated badge)
     * @param url the path to the resource
     * @return true if the property bornOnDay exists in the resource at the given url
     */
    boolean bornOnDayExists(String url);

    /**
     * @return the translation context
     */
    T getTranslationContext();

    /**
     * Formats the epochDate
     * @param epochDate to use to
     * @return the formatted publishDate
     */
    String getFormattedPublishDate(Long epochDate);


    /**
     * Evaluates if the
     * @param epochDate to evaluate
     * @return true if the epochDate is considered to indicate a document is new
     */
    boolean isNew(Long epochDate);
}
