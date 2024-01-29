package com.eaton.platform.core.models.digital.v1;

import javax.inject.Inject;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;

/**
 * <html> Description: This class is used in ContentTileMultifieldModel class to inject the dialog properties. </html> .

 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ContentTileMultifieldModel extends CTALink{

	//Since we are extending from CTALink we can use the inherited properties instead of re-define them
	
	/** The link color. */
	@Inject
	private String color;
	private String cssClass;

    public String getCssClass() {
        return cssClass;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    /**
	 * Gets the link color.
	 *
	 * @return the link color
	 */
	public String getColor() {
		return color;
	}
}
