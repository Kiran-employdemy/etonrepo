package com.eaton.platform.core.models.pim;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Model(adaptables = Resource.class,defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TaxonomyAttributes {

    @Self
    private Resource resource;

    private List<AttributeGroup> attributeGroups;

    @PostConstruct
    protected void initModel(){
        attributeGroups = new ArrayList<>();
        resource.listChildren().forEachRemaining(child->attributeGroups.add(child.adaptTo(AttributeGroup.class)));
    }

    public List<AttributeGroup> getAttributeGroups() {
        return attributeGroups;
    }
}
