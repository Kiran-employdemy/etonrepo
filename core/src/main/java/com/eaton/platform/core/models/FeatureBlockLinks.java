package com.eaton.platform.core.models;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.experiencefragment.ExperienceFragmentLinkTransformerModel;
import com.eaton.platform.core.util.CommonUtil;
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
public class FeatureBlockLinks extends ExperienceFragmentLinkTransformerModel{


	private Page currentpage;

	/** The feature block link text. */
	@Inject 
	private String featureBlockLinkText;

	/** The resource resolver. */
	@Inject
	@Source("sling-object")
	private ResourceResolver resourceResolver;

	/** The feature block link path. */
	@Inject 
	private String featureBlockLinkPath;
	
	/** The feature block link open new window. */
	@Inject
	private String featureBlockLinkOpenNewWindow;

	/** The modal choice. */
	@Inject  @Default(values = "false")
	private String linkModal;

	/** The Source tracking javascript. */
	@Inject @Default(values = "false")
	private String featureEnableSourceTracking;

	public Page getCurrentpage() {
		return currentpage;
	}

	public void setCurrentpage(Page currentpage) {
		this.currentpage = currentpage;
	}

	
	/**
	 * Gets the feature block link text.
	 *
	 * @return the feature block link text
	 */
	public String getFeatureBlockLinkText() {
		return featureBlockLinkText;
	}

	/**
	 * Gets the feature block link path.
	 *
	 * @return the feature block link path
	 */
	public String getFeatureBlockLinkPath() {
		String localeSpecificLink = null;
		if (Boolean.TRUE.equals(this.getIsComponentInXS())) {
			localeSpecificLink = XFUtil.updateHTMLLinkInXF(this.getCurrentpage(), resourceResolver, featureBlockLinkPath);
		}
		return localeSpecificLink != null ? CommonUtil.dotHtmlLink(localeSpecificLink)
				: CommonUtil.dotHtmlLink(featureBlockLinkPath);

	}

	/**
	 * Gets the feature block link open new window.
	 *
	 * @return the feature block link open new window
	 */
	public String getFeatureBlockLinkOpenNewWindow() {
		return CommonConstants.TRUE.equalsIgnoreCase(featureBlockLinkOpenNewWindow) ? CommonConstants.TARGET_BLANK : CommonConstants.TARGET_SELF;
	}

	/**
	 * Gets the modal.
	 *
	 * @return modal
	 */
	public String getLinkModal(){
		return linkModal;
	}

	public String getFeatureEnableSourceTracking() {
		return featureEnableSourceTracking;
	}



/**
 * Gets the checks if is external.
 * 
 * @return the checks if is external
 */
public String getIsExternal() {
	String isExternal = CommonConstants.FALSE;
	if (null != this.featureBlockLinkPath
			&& StringUtils.startsWith(this.featureBlockLinkPath, CommonConstants.HTTP)
			|| StringUtils.startsWith(this.featureBlockLinkPath, CommonConstants.HTTPS)
			|| StringUtils.startsWith(this.featureBlockLinkPath, CommonConstants.WWW)) {
		isExternal = CommonConstants.TRUE;
	}
	return isExternal;
}
}