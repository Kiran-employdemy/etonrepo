package com.eaton.platform.core.models.topiclink;

import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.experiencefragment.ExperienceFragmentLinkTransformerModel;

import org.apache.sling.models.annotations.Via;
import com.eaton.platform.core.util.CommonUtil;
import com.day.cq.wcm.api.Page;

import com.eaton.platform.core.util.XFUtil;


/**
 * <html> Description: It is a Sling Model class which automatically maps from Sling objects
 * i.e. dialog fields are injected </html> 
 * @author TCS
 * @version 1.0
 * @since 2017
 *
 */
@Model(adaptables = { Resource.class,
		SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TopicLinkModel extends ExperienceFragmentLinkTransformerModel {
	
	@Inject
	Resource resource;

	/** The color. */
	@Inject
	@Via("resource")
	private String topicLinkColor;

	@Inject
	private Page page;

	/** The topicLinkHeader. */
	@Inject
	@Via("resource")
	private String topicLinkHeader;
	
	/** The topicLinkDesc. */
	@Inject
	@Via("resource")
	private String topicLinkDesc;
	
	/** The topicLinkIcon. */
	@Inject
	@Via("resource")
	private String topicLinkIcon;
	
	/** The topicLinkCTATitle */
	@Inject
	@Via("resource")
	private String topicLinkCTATitle;
	
	/** The topicLinkCTALink */
	@Inject
	@Via("resource")
	private String topicLinkCTALink;	

	/** The topicLinkCTAOpenNewWindow */
	@Inject
	@Via("resource")
	private String topicLinkCTAOpenNewWindow;

	/** The topicLinkCTAModal */
	@Inject
	@Via("resource")
	private String topicLinkCTAModal;

    /** The resource resolver. */
    @Inject @Source("sling-object")
    private ResourceResolver resourceResolver;

	/** The Source tracking javascript. */
	@Inject
	@Via("resource")
	@Default(values = "false")
	private String enableSourceTracking;


	private String localeSpecificLink = null;

	
	/**
	 * @return the topicLinkColor
	 */
	public String getTopicLinkColor() {
		return topicLinkColor;
	}
	/**
	 * @return the topicLinkHeader
	 */
	public String getTopicLinkHeader() {
		return topicLinkHeader;
	}

	/**
	 * @return the topicLinkDesc
	 */
	public String getTopicLinkDesc() {
		return topicLinkDesc;
	}

	/**
	 * @return the topicLinkIcon
	 */
	public String getTopicLinkIcon() {
		return topicLinkIcon;
	}
	
	/**
	 * if author has not entered the link title then
	 * return navigation title if it is not authored, then return the page title.
	 * @return the topicLinkCTATitle
	 */
	public String getTopicLinkCTATitle() {
		return  CommonUtil.getLinkTitle(this.topicLinkCTATitle, this.topicLinkCTALink, resourceResolver);
	
	}

	/**
	 * @return the topicLinkCTALink
	 */
	public String getTopicLinkCTALink() {
		if (Boolean.TRUE.equals(this.getIsComponentInXS())) {
			localeSpecificLink = XFUtil.updateHTMLLinkInXF(page, resourceResolver,
			topicLinkCTALink);
		}
		return localeSpecificLink != null ? CommonUtil.dotHtmlLink(localeSpecificLink)
				: CommonUtil.dotHtmlLink(topicLinkCTALink);
		
	}

	/**
	 * @return the topicLinkCTAOpenNewWindow
	 */
	public String getTopicLinkCTAOpenNewWindow() {
		return CommonConstants.TRUE.equalsIgnoreCase(this.topicLinkCTAOpenNewWindow) ? CommonConstants.TARGET_BLANK : CommonConstants.TARGET_SELF;
	}

	/**
	 * @return the topicLinkCTAModal
	 */
	public String getTopicLinkCTAModal() {
		return topicLinkCTAModal;
	}

	public String getEnableSourceTracking() {
		return enableSourceTracking;
	}
}
