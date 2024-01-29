package com.eaton.platform.core.models;

import javax.inject.Inject;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

/**
 * <html> Description: This class is used to inject the dialog properties for Divider component.</html> .
 * 
 * @author E0465079
 *
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class DividerModel {
	
	/** Variable The componentType. */
    @Inject @Default(values="divider")
	private String componentType;

    /**
     * Gets the component Type.
     * 
     * @return componentType
     */
	public String getComponentType() {
		return componentType;
	}
}
