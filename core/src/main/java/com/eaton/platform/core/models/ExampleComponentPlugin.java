package com.eaton.platform.core.models;

import org.apache.sling.api.resource.Resource;

import com.citytechinc.cq.component.annotations.Component;
import com.citytechinc.cq.component.annotations.DialogField;
import com.citytechinc.cq.component.annotations.widgets.TextField;
import org.apache.sling.models.annotations.Model;
import javax.inject.Inject;

@Component(
    value = "Example Component Plugin",
    group = ".hidden")
@Model(adaptables = Resource.class)
public class ExampleComponentPlugin {
    @DialogField(
        fieldLabel = "Title")
    @TextField
    @Inject
    private String title;
}
