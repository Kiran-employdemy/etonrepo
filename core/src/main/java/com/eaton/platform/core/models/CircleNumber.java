package com.eaton.platform.core.models;


import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;


@Model(adaptables = Resource.class,defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CircleNumber {

    @ValueMapValue
    private String heading;

    @ValueMapValue
    private String body;



    public String getHeading() {
        return heading;
    }

    public String getBody() {
        return body;
    }
}
