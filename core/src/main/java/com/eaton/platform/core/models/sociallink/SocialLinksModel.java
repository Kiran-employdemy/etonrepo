package com.eaton.platform.core.models.sociallink;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.util.CommonUtil;

/**
 * The Class SocialLinksModel.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SocialLinksModel {
	
	/** The social sites. */
	@Inject
	private String imagePath;
	
	/** The social links. */
	@Inject
	private String path;
	
	/** The Resource Resolver */
	@Inject
	@Source("sling-object")
	private ResourceResolver resourceResolver;

	/**
	 * Gets the image path.
	 *
	 * @return the image path
	 */
	public String getImagePath() {
		return imagePath;
	}

	/**
	 * Gets the social links.
	 *
	 * @return socialLinks
	 */
	public String getPath() {
		return CommonUtil.dotHtmlLink(path, resourceResolver);
	}

	/**
	 * Gets the target.
	 *
	 * @return the target
	 */
	public String getTarget() {
		return CommonConstants.TARGET_BLANK;
	}
	
}
