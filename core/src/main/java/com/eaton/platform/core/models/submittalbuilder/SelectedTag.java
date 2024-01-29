package com.eaton.platform.core.models.submittalbuilder;

import com.day.cq.tagging.TagManager;
import com.day.cq.tagging.Tag;
import com.eaton.platform.core.models.EndecaFacetTag;
import com.eaton.platform.core.services.AdminService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import com.day.cq.wcm.api.Page;
import org.apache.sling.models.annotations.Model;
import org.codehaus.jackson.annotate.JsonProperty;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * This model represents a node with a "cq:tags" field that contains a single tag. This
 * is used in the submittal scope and submittal attributes orderable multifields fields
 * of the submittal builder.
 */
@Model(adaptables = { SlingHttpServletRequest.class, Resource.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SelectedTag {
    @Inject @Named("cq:tags")
    private String[] tags;

    @Inject
    private AdminService adminService;

    @Inject
    private Page resourcePage;

    private Tag tag;


    @JsonProperty(value="id")
    public String getId() {
        String tagId = StringUtils.EMPTY;
        try (ResourceResolver adminServiceReadService = adminService.getReadService()) {
            // Each node should contain exactly one tag.
            final Tag tag =  adminServiceReadService.adaptTo(TagManager.class).resolve(tags[0]);
            if (null != tag) {
                tagId = tag.getTagID();
            }
        }
        return tagId;
    }

    @JsonProperty(value="name")
    public String getName() {
        String facetID = StringUtils.EMPTY;
        try (ResourceResolver adminServiceReadService = adminService.getReadService()) {
            final Tag tag =  adminServiceReadService.adaptTo(TagManager.class).resolve(tags[0]);
            if (null != tag) {
                facetID = tag.adaptTo(EndecaFacetTag.class).getFacetId();
            }
        }
        return facetID;
    }

    @JsonProperty(value="title")
    public String getTitle() {
        String title = StringUtils.EMPTY;
        try (ResourceResolver adminServiceReadService = adminService.getReadService()) {
            final Tag tag =  adminServiceReadService.adaptTo(TagManager.class).resolve(tags[0]);
            if (null != tag) {
                title = tag.getTitle(resourcePage.getLanguage(false));
            }
        }
        return title;
    }
}
