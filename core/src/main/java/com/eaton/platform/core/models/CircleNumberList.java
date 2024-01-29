package com.eaton.platform.core.models;


import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import javax.inject.Inject;
import java.util.List;

@Model(adaptables = Resource.class,defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CircleNumberList {
    @Inject
    private List<CircleNumber> items;

    public List<CircleNumber> getItems() {
        return items;
    }
}
