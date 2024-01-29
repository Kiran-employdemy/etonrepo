package com.eaton.platform.core.models.pim;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Model(adaptables = Resource.class,defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AttributeList {

    @Self
    private Resource resource;

    private List<String> attributes;

    @PostConstruct
    protected void initModel(){
        this.attributes = new ArrayList<>();
        resource.listChildren().forEachRemaining(child -> attributes.add(child.getValueMap().get("taxonomyAttribute", StringUtils.EMPTY)));
    }

    public List<String> getAttributes() {
        return attributes;
    }
}
