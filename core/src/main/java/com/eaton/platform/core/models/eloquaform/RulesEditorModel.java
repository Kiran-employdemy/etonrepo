package com.eaton.platform.core.models.eloquaform;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

import javax.inject.Inject;
import java.util.List;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class RulesEditorModel {

    private static final String IS_EMPTY = "IS_EMPTY";
    private static final String IS_NOT_EMPTY = "IS_NOT_EMPTY";
    private static final String ON_LOAD = "ON_LOAD";

    @Inject
    private String conditionField;

    @Inject
    private String conditionType;

    @Inject
    private String conditionText;

    @Inject
    @ChildResource
    List<ActionConfigModel> actionConfig;

    public final String getConditionField() {
        return conditionField;
    }

    public final String getConditionType() {
        return conditionType;
    }

    public final String getConditionText() {
        return conditionText;
    }

    public List<ActionConfigModel> getActionConfig() {
        return actionConfig;
    }

    public boolean isValid() {
        if(null != getConditionType() && ON_LOAD.equals(getConditionType())){
            return Boolean.TRUE;
        }else if (null != getConditionType() && null != getConditionField() && (IS_EMPTY.equals(getConditionType()) ||
                IS_NOT_EMPTY.equals(getConditionType()))){
            return Boolean.TRUE;
        }
        return getConditionField() != null && getConditionType() != null && getConditionText() != null;
    }
}
