package com.eaton.platform.core.models.horizontalinklist;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.util.CommonUtil;

/**
 * The Class FullPageDrawerLinkListModel.
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL) 
public class HorizontalLinkListResourceModel {

	/** The horizontal link title. */
	@Inject @Named("horizontalLinkTitle")
	private String horizontalLinkTitle;

	/** The horizontal link path. */
	@Inject @Named("horizontalLinkPath")
	private String horizontalLinkPath;

	/** The horizontal link open new window. */
	@Inject @Named("horizontalLinkOpenNewWindow")
	private String horizontalLinkOpenNewWindow;
	
	 /** The resource resolver. */
    @Inject @Source("sling-object")
    private ResourceResolver resourceResolver;

	/**
	 * Gets the horizontal link title.
	 *
	 * @return the horizontal link title
	 */
	public String getHorizontalLinkTitle() {
		if(horizontalLinkTitle == null || horizontalLinkTitle.equals(StringUtils.EMPTY)) {
			return CommonUtil.getLinkTitle(null, horizontalLinkPath, resourceResolver);
		} else {
			return horizontalLinkTitle;
			
		}
	}
	

	/**
	 * Gets the horizontal link path.
	 *
	 * @return the horizontal link path
	 */
	public String getHorizontalLinkPath() {
		return CommonUtil.dotHtmlLink(horizontalLinkPath);
	}

	/**
	 * Gets the horizontal link open new window.
	 *
	 * @return the horizontal link open new window
	 */
	public String getHorizontalLinkOpenNewWindow() {
		String newWindow = StringUtils.EMPTY;
		if(StringUtils.equals(CommonConstants.TRUE, this.horizontalLinkOpenNewWindow) || StringUtils.equals(CommonConstants.TRUE, getIsExternal())) {
			newWindow = CommonConstants.TARGET_BLANK;
		}
		return newWindow;
		
	}
    
	/**
	 * Gets the checks if is external.
	 *
	 * @return the checks if is external
	 */
	public String getIsExternal() {
		String isExternal = CommonConstants.FALSE;
		if((null != horizontalLinkPath) && (StringUtils.startsWith(horizontalLinkPath, CommonConstants.HTTP) || StringUtils.startsWith(horizontalLinkPath, CommonConstants.HTTPS) || StringUtils.startsWith(horizontalLinkPath, CommonConstants.WWW))) {
			isExternal = CommonConstants.TRUE;
		}
		return isExternal;
	}	

}