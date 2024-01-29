package com.eaton.platform.core.models;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.FragmentTemplate;
import com.day.cq.tagging.Tag;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.adobe.cq.dam.cfm.ContentElement;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Model(adaptables = { Resource.class, ContentFragment.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ContentFragmentPerson {
	private static final Logger LOG = LoggerFactory.getLogger(ContentFragmentPerson.class);
	private static final String PROP_NAME = "name";
	private static final String PROP_ROLE = "role";
	private static final String PROP_EMAIL = "email";
	private static final String PROP_PHONE_NUMBER = "phoneNumber";

	public static final String MODEL_TITLE = "Person";

	@Inject
	private Resource resource;

	@Inject
	private List<Tag> cqTags;

	private Optional<ContentFragment> contentFragment;

	@PostConstruct
	public void init() {
		if (resource != null) {
		    contentFragment = Optional.ofNullable(resource.adaptTo(ContentFragment.class));

		    if (! contentFragment.isPresent()) {
				LOG.error("ContentFragmentPerson could not be adapted from provided resource: " + resource.getPath());
			}
		} else {
			LOG.error("ContentFragmentPerson: resource was null");
		}
    }

    public String getModel() {
		return contentFragment
				.map(ContentFragment::getTemplate)
				.map(FragmentTemplate::getTitle)
				.orElse(StringUtils.EMPTY);
	}

	public String getName() {
	    return contentFragment
				.map(cf -> cf.getElement(PROP_NAME))
				.map(ContentElement::getContent)
				.orElse(StringUtils.EMPTY);
	}

    public String getRole() {
	    return contentFragment
				.map(cf -> cf.getElement(PROP_ROLE))
				.map(ContentElement::getContent)
				.orElse(StringUtils.EMPTY);
	}

    public String getEmail() {
	    return contentFragment
				.map(cf -> cf.getElement(PROP_EMAIL))
				.map(ContentElement::getContent)
				.orElse(StringUtils.EMPTY);
	}

    public String getPhoneNumber() {
	    return contentFragment
				.map(cf -> cf.getElement(PROP_PHONE_NUMBER))
				.map(ContentElement::getContent)
				.orElse(StringUtils.EMPTY);
	}

	public List<Tag> getTags() {
		return cqTags != null ? cqTags : new ArrayList<>();
	}
}
