package com.eaton.platform.core.models.pim;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.inject.Inject;
import javax.inject.Named;

@Model(adaptables = Resource.class,defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AttributeGroup {

    @Self
    private Resource resource;
    @Inject
    @Named("taxonomyAttributeGrpName")
    private String groupName;

    @Inject
    private AttributeList attributeList;

    public String getGroupName() {
        return groupName;
    }

    public AttributeList getAttributeList() {
        return attributeList;
    }
}
