package com.eaton.platform.core.models;

import com.day.cq.tagging.TagManager;
import com.eaton.platform.core.services.AdminService;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import static com.eaton.platform.core.constants.CommonConstants.CONTACT_DISPLAY_LIST_METHOD_ANY;
import static com.eaton.platform.core.constants.CommonConstants.CONTACT_DISPLAY_LIST_METHOD_ALL;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ContactList {
	private static final Logger LOG = LoggerFactory.getLogger(ContactList.class);

	@OSGiService
	private AdminService adminService;

	@Inject @Named("cq:tags")
	private String[] cqTags;

	@Inject @Default(values = CONTACT_DISPLAY_LIST_METHOD_ANY)
	private String listMethod;

	@Inject @Default(values = "false")
	private Boolean showEmail;

	@Inject @Default(values = "")
	private String heading;

	@Inject @Default(values = "/content/dam")
	private String parentPath;

	private final List<ContentFragmentPerson> people = new ArrayList<>();

	@PostConstruct
	public void init() {
		if (adminService != null && cqTags != null && cqTags.length > 0) {
			try (final ResourceResolver readService = adminService.getReadService()) {
				if (readService != null) {
					final TagManager tagManager = readService.adaptTo(TagManager.class);

					if (tagManager != null) {
						tagManager.find(parentPath, cqTags, !listMethod.equals(CONTACT_DISPLAY_LIST_METHOD_ALL)).forEachRemaining(resource -> {
							// The cq:tags property of the default ContentFragment tags field places the property on the
							// <content-fragment>/jcr:content/metadata node. And so to adapt the <content-fragment> you have to go to nodes upwards.
							final ContentFragmentPerson cfPerson = resource.getParent().getParent().adaptTo(ContentFragmentPerson.class);
							if (cfPerson != null && ContentFragmentPerson.MODEL_TITLE.equals(cfPerson.getModel())) {
								people.add(cfPerson);
							}
						});
					} else {
						LOG.error("ContactList: tagManager was null");
					}
				} else {
					LOG.error("ContactList: readService was null");
				}
			}
		} else {
			if (adminService == null) LOG.error("ContactList: adminService was null");
		}
	}

	public List<ContentFragmentPerson> getPeople() {
		return people;
	}

	public boolean isShowEmail() {
		return showEmail;
	}

	public boolean isHasHeading() {
		return ! StringUtils.EMPTY.equals(heading);
	}

	public String getHeading() {
		return heading;
	}
}
