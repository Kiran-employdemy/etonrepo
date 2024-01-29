package com.eaton.platform.core.models;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;

@Model(
    adaptables = { Resource.class,
        SlingHttpServletRequest.class },
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CCMSMetaTagModel {
    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(CCMSMetaTagModel.class);

    @Inject
    protected Page currentPage;

    protected ValueMap currentPageProperties;

    protected TagManager tagManager;

    /** The Constant DOUBLE_PIPE. */
    public static final String DOUBLE_PIPE = "||";

    /** The Constant SLASH. */
    public static final String SLASH = "/";
    private String categoryTag;
    /** The resolver. */
    @SlingObject
    private ResourceResolver resolver;
    private String tagSpace;

    public static final String TAG_NAMESPACE_PRODUCT_TAXONOMY = "eaton:product-taxonomy";
    public static final String TAG_NAMESPACE_SUPPORT_TAXONOMY = "eaton:support-taxonomy";
    public static final String TAG_NAMESPACE_SERVICES_TAXONOMY = "eaton:services";

    @PostConstruct
    protected void init() {

        if (Objects.isNull(currentPage) || !currentPage.getProperties().containsKey(NameConstants.PN_TAGS)) {
            return;
        }

        String[] tags = currentPage.getProperties().get(NameConstants.PN_TAGS, String[].class);
        StringBuilder categoryTags = new StringBuilder(StringUtils.EMPTY);
        tagManager = resolver.adaptTo(TagManager.class);
        for (String tagValue : tags) {
            if (tagManager != null) {
                Tag tag = tagManager.resolve(tagValue);
                if (Objects.nonNull(tag)) {
                    String id = tag.getTagID();
                    String[] tagSpaces = id.split(SLASH);
                    if (tagSpaces.length > 0) {
                        tagSpace = tagSpaces[0];
                    }
                    boolean isValidTag = isValidTagName(tagSpace);
                    if (isValidTag) {
                        categoryTags.append(tag.getName());
                        categoryTags.append(DOUBLE_PIPE);
                    }
                }
            }

        }
        if (categoryTags.length() > 1) {
            categoryTag = categoryTags.substring(0, categoryTags.length() - 2);
        }
    }

    public static boolean isValidTagName(String tagName) {
        if (StringUtils.isNotBlank(tagName)) {
            for (String tagNameSpace : ccmsTagNameSpace) {
                if (tagNameSpace.equals(tagName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getCategoryTag() {

        return categoryTag;
    }

    protected static final List<String> ccmsTagNameSpace = Arrays.asList(TAG_NAMESPACE_PRODUCT_TAXONOMY,
        TAG_NAMESPACE_SERVICES_TAXONOMY, TAG_NAMESPACE_SUPPORT_TAXONOMY);
}
