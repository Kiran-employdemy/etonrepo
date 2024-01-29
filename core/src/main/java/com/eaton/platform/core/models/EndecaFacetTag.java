package com.eaton.platform.core.models;

import com.day.cq.tagging.Tag;
import com.eaton.platform.core.constants.CommonConstants;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.eaton.platform.core.constants.CommonConstants.*;

@Model(adaptables = { Tag.class })
public class EndecaFacetTag {
    public static final String AMPERSAND = "&";
    public static final String AMPERSAND_ENCODE = "&amp;";
    private static final String ID_ENCODE = "&#95;";
    public static final String ID_SEPARATOR = "_";

    @Self
    private Tag tag;

    private String facetId;

    @PostConstruct
    protected void init() {
        facetId = createId(tag);
    }

    /**
     * If this tag is a namespace then the facet id will be just the namespace name.
     * If this tag is not a namespace then the facet id will be in this format:
     *         parent-tag-name_child-tag-name_grandchild-tag-name
     * This method will never return null.
     * @return The facet id.
     */
    public String getFacetId() {
        return facetId == null ? "" : facetId;
    }

    /**
     * @return True if there is a valid facet id for this tag. False otherwise.
     */
    public boolean hasFacetId() {
        return facetId != null && ! facetId.isEmpty();
    }

    /**
     * @param locale The locale to find a localized facet label for.
     * @return The localized facet label based on the provided locale.
     */
    public String getLocalizedFacetLabel(Locale locale) {
        final String localizedTitle = tag.getLocalizedTitle(locale);

        return localizedTitle != null ? localizedTitle : tag.getTitle();
    }

    /**
     * @param endecaFacetId An Endeca facet id that can be converted to a tag based on the format defined by the getFacetId method.
     * @return The normal tag id represented by the endecaFacet id.
     */
    public static String convertId(String endecaFacetId) {
    	if (StringUtils.equalsIgnoreCase(endecaFacetId, CommonConstants.TAG_NEWS_RELEASES_TOPIC)) {
    		endecaFacetId = CommonConstants.TAG_NEWS_RELEASES_TOPIC;
    	}
        return  TAG_NAMESPACE_EATON +
                TAG_NAMESPACE_SEPARATOR +
                Arrays.stream(endecaFacetId.replaceAll(AMPERSAND_ENCODE, AMPERSAND).split(ID_SEPARATOR))
                    .map(tagName -> tagName.replaceAll(ID_ENCODE, ID_SEPARATOR))
                    .collect(Collectors.joining(TAG_HIERARCHY_SEPARATOR));
    }

    /**
     * @param endecaFacetId An Endeca facet id that can be converted to a tag based on the format defined by the getFacetId method.
     * @return The normal tag id represented by the endecaFacet id.
     * This method removes hard coded RootNameSpace of cq:tags(eaton:).
     */
    public static String convertToTagId(String endecaFacetId) {
    	if (StringUtils.equalsIgnoreCase(endecaFacetId, CommonConstants.TAG_NEWS_RELEASES_TOPIC)) {
    		endecaFacetId = CommonConstants.TAG_NEWS_RELEASES_TOPIC;
    	}
        return Arrays.stream(endecaFacetId.replace(AMPERSAND_ENCODE, AMPERSAND).split(ID_SEPARATOR))
                        .map(tagName -> tagName.replace(ID_ENCODE, ID_SEPARATOR))
                        .collect(Collectors.joining(TAG_HIERARCHY_SEPARATOR));
    }

    /**
     * Given a tag this generates the facet id that Endeca expects according to the
     * format explained in the getFacetId method.
     * @param tag The tag to use in order to generate the Endeca facet id. This cannot be a namespace.
     * @return The Endeca facet id for the provided tag.
     */
    private static String createId(Tag tag) {
    	String tagId = tag.getLocalTagID()
                .replaceAll(AMPERSAND, AMPERSAND_ENCODE)
                .replaceAll(ID_SEPARATOR, ID_ENCODE)
                .replaceAll(TAG_HIERARCHY_SEPARATOR, ID_SEPARATOR);
    	
    	if (tagId.equals(CommonConstants.TAG_NEWS_RELEASES_TOPIC)) {
    		tagId = tagId.toLowerCase();
    	}
    	
        return tagId;
    }
}
