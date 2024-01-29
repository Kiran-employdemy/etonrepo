package com.eaton.platform.core.search.api;

/**
 * Creation of the default image url
 * Implementation of this interface allows the caller
 * <ol>
 *     <li>to validate for the need of a default image url</li>
 *     <li>creates the image url using extension as identifier</li>
 * </ol>
 *
 */
public interface DefaultImageUrlFactory {
    /**
     * Validates for the need of a default image url
     *
     * @param imageUrl to validate against
     * @return true if the need is there
     */
    boolean needsDefaultImageUrl(String imageUrl);

    /**
     * Creates the default image url
     *
     * @param identifier to use for creation, you can use the extension like jpg, jpeg, pdf, etc...
     * @return the crafted default image url
     */
    String createImageUrl(String identifier);
}
