package com.eaton.platform.core.models.submittalbuilder;

import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * This model represents the scope of the submittal builder. This is defined by
 * a list of product families that define what PDF's should be available for the
 * user in the submittal builder.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SubmittalScope {
    @Inject @Named(".")
    private List<SelectedTag> families;

    @JsonProperty(value="families")
    public List<SelectedTag> getFamilies() {
        return families;
    }
}
