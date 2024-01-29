package com.eaton.platform.core.models.search;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;

import com.eaton.platform.core.util.CommonUtil;

/**
 * <html> Description: This Sling Model used in SearchModel class
 *  to load Relevant links multifield for Search view</html> .
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SearchRelevantLinksModel {

	/** The link. */
	@Inject
	private String link;

	/** The link text. */
	@Inject @Named("transLinkText")
	private String linkText;
	
	/** The new window. */
	@Inject
	private String newWindow;
	
    /** The resource resolver. */
    @Inject @Source("sling-object")
    private ResourceResolver resourceResolver;

	/**
	 * Gets the link.
	 *
	 * @return the link
	 */
	public String getLink() {
		return CommonUtil.dotHtmlLink(this.link);
	}

	/**
	 * Gets the link text.
	 *
	 * @return the link text
	 */
	public String getLinkText() {
		return CommonUtil.getLinkTitle(this.linkText, this.link, this.resourceResolver);
	}

	/**
	 * Gets the new window.
	 *
	 * @return the new window
	 */
	public String getNewWindow() {
		return this.newWindow;
	}
}
