package com.eaton.platform.core.models;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.experiencefragment.ExperienceFragmentLinkTransformerModel;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.core.util.XFUtil;

/**
 * <html> Description: This class is used in LinkListWithIconModel class to inject the dialog properties. </html> .
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class LinkListWithIconMultifieldModel extends ExperienceFragmentLinkTransformerModel {

	/** The link icon. */
	@Inject @Named("linkIcon")
	private String linkIcon;


	/** The link title. */
	private Page currentPage;

	/** The link title. */
	@Inject @Named("linkTitle")
	private String linkTitle;
	
	/** The link path. */
	@Inject @Named("linkPath")
	private String linkPath;
	
	/** The link open new window. */
	@Inject @Named("linkOpenNewWindow")
	private String linkOpenNewWindow;
	
	/** The link desc. */
	@Inject @Named("linkDesc")
	private String linkDesc;

	@Inject
	private boolean isSuffixEnabled;

	/** The resource resolver. */
	@Inject
	@Source("sling-object")
	private ResourceResolver resourceResolver;
	
	/**
	 * Gets the link icon.
	 *
	 * @return the link icon
	 */
	public String getLinkIcon() {
		return linkIcon;
	}
	
	/**
	 * Gets the image alt text.
	 *
	 * @return the image alt text
	 */
	public String getImageAltText() {
		return CommonUtil.getAssetAltText(resourceResolver, getLinkIcon());
	}
	
	/**
	 * Gets the link title.
	 *
	 * @return the link title
	 */
	public String getLinkTitle() {
		return linkTitle;
	}



	/**
	 * Gets the link path.
	 *
	 * @return the link path
	 */
	public String getLinkPath() {
		String localeSpecificLink = null;
		if (Boolean.TRUE.equals(this.getIsComponentInXS())) {
			localeSpecificLink = XFUtil.updateHTMLLinkInXF(this.getCurrentPage(), resourceResolver, linkPath);
		}
		return StringUtils.isNotEmpty(localeSpecificLink) ? CommonUtil.dotHtmlLink(localeSpecificLink)
				: CommonUtil.dotHtmlLink(linkPath);
	}

	/**
	 * Gets the link open new window.
	 *
	 * @return the link open new window
	 */
	public String getLinkOpenNewWindow() {
		String newWindow = StringUtils.EMPTY;
		if(StringUtils.equals(CommonConstants.TRUE, this.linkOpenNewWindow) || StringUtils.equals(CommonConstants.TRUE, getIsExternal())) {
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
		if((null != linkPath) && (StringUtils.startsWith(linkPath, CommonConstants.HTTP) || StringUtils.startsWith(linkPath, CommonConstants.HTTPS) || StringUtils.startsWith(linkPath, CommonConstants.WWW))) {
			isExternal = CommonConstants.TRUE;
		}
		return isExternal;
	}
	/**
	 * Gets the link desc.
	 *
	 * @return the link desc
	 */
	public String getLinkDesc() {
		return linkDesc;
	}


    /**
     * @return Page return the currentPage
     */
    public Page getCurrentPage() {
        return currentPage;
    }

    /**
     * @param currentPage the currentPage to set
     */
    public void setCurrentPage(Page currentPage) {
        this.currentPage = currentPage;
    }

    public boolean isSuffixEnabled() {
        return isSuffixEnabled;
    }

    public void setSuffixEnabled(boolean suffixEnabled) {
        isSuffixEnabled = suffixEnabled;
    }
}
