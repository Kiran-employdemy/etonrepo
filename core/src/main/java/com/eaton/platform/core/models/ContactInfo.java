package com.eaton.platform.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ContactInfo {
	@Inject
	private String personPath;

	@Inject @Default(values = "false")
	private Boolean showEmail;

	@Inject
	private Resource resource;

	private ContentFragmentPerson cfPerson;

	@PostConstruct
	public void init() {
		if (personPath != null) {
			final ResourceResolver resourceResolver = resource.getResourceResolver();
			final Resource personResource = resourceResolver.resolve(personPath);

			if (personResource != null) {
			    cfPerson = personResource.adaptTo(ContentFragmentPerson.class);
			}
		}
    }

    public boolean isHasPerson() {
	    return cfPerson != null;
	}

	public ContentFragmentPerson getPerson() {
	    return cfPerson;
	}

	public boolean isShowEmail() {
		return showEmail;
	}
}
