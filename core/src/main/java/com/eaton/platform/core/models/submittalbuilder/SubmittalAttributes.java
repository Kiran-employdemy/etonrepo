package com.eaton.platform.core.models.submittalbuilder;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.codehaus.jackson.annotate.JsonProperty;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

/**
 * This model represents the attributes selected for the submittal builder. This is defined by
 * a list of product attribute tags.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SubmittalAttributes {
    @Inject @Named(".")
    private List<SelectedFacetTag> attributes;

    @JsonProperty(value="attributes")
    public List<SelectedFacetTag> getAttributes() {
        return attributes;
    }
}
