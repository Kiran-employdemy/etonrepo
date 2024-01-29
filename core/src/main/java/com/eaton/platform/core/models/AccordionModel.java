package com.eaton.platform.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import javax.inject.Inject;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AccordionModel {
    @Inject
    private Resource resource;

    @Inject
    private String[] accordionItemValue;

    @Inject String toggleInnerGrid;

    @Inject @Default(values = "false")
    private String defaultAccordionClosed;

    public String[] getAccordionItemValue() {
        return accordionItemValue;
    }

    public String getDefaultAccordionClosed() {
        return defaultAccordionClosed;
    }

    public String getUniqueId() {
        return Integer.toString(resource.getPath().hashCode());
    }

    public String getToggleInnerGrid() {
		return toggleInnerGrid;
	}
}
