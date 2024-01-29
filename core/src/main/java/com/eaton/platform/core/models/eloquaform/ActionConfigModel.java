package com.eaton.platform.core.models.eloquaform;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import javax.inject.Inject;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ActionConfigModel {

    @Inject
    private String actionType;

    @Inject
    private String targetField;

    public final String getActionType() {
        return actionType;
    }

    public final String getTargetField() {
        return targetField;
    }

    public boolean isValid() {
        return getActionType() != null && getTargetField() != null;
    }
}
