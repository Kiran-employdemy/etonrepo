package com.eaton.platform.core.models.verticallinklist;

import java.text.SimpleDateFormat;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.util.CommonUtil;

/**
 * <html> Description: This Sling Model used in CardListModel class
 *  to load Fixed links multifield for Generic views</html> .
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class VerticalLinkFixedLinksModel {

	/** The page link. */
	@Inject @Named("fixedLinkPath")
	private String pagePath;

	/** The page link. */
	@Inject @Named("fixedLinkPath")
	private String fixedPagePath;

	/** The new window. */
	@Inject @Named("fixedLinkOpenNewWindow")
	private String newWindow;
	
    /** The resource resolver. */
    @Inject @Source("sling-object")
    private ResourceResolver resourceResolver;

    /** The enable teaser content. */
    private String enableTeaserContent;

	private boolean secure;

	@Inject
	private Resource res;

	@PostConstruct
	protected void init() {
		if(res!=null) {
			if(res.getParent()!=null) {
				if(res.getParent().getParent()!=null) {
					ValueMap properties = res.getParent().getParent().getValueMap();
					String enableSourceTracking = CommonUtil.getStringProperty(properties, CommonConstants.ENABLE_SOURCE_TRACKING);
					if ((enableSourceTracking.contains( CommonConstants.TRUE))) {
						newWindow = CommonConstants.TARGET_SELF;
					} else if (!(enableSourceTracking.contains("true")) && (StringUtils.equals(CommonConstants.TRUE, this.newWindow) || StringUtils.equals(CommonConstants.TRUE, getIsExternal()))) {
						newWindow = CommonConstants.TARGET_BLANK;
					}
				}
			}
		}
	}

	/**
	 * Gets the headline.
	 *
	 * @return headline
	 */
	public String getHeadline() {
		String teaserTitle;
		// check if enable teaser content is checked, if checked then get value from teaser tab > teaser title
		if(StringUtils.equals(CommonConstants.TRUE, getEnableTeaserContent())) {
			teaserTitle = getPageProperty(CommonConstants.TEASER_TITLE);
			if(StringUtils.isBlank(teaserTitle)) {
				teaserTitle = CommonUtil.getLinkTitle(null, fixedPagePath, resourceResolver);
			}
		} else {
			teaserTitle = CommonUtil.getLinkTitle(null, fixedPagePath, this.resourceResolver);
		}
		return teaserTitle;
		
	}
	
	/**
	 * Gets the page link.
	 * 
	 * @return the page link
	 */
	public String getPagePath() {
		return CommonUtil.dotHtmlLink(this.pagePath);
	}

	public void setPagePath(String pagePath) {
		this.pagePath = pagePath;
	}

	/**
	 * Gets the new window.
	 *
	 * @return the new window
	 */
	public String getNewWindow() {
		return newWindow;
	}
	
	/**
	 * Gets the checks if is external.
	 *
	 * @return the checks if is external
	 */
	public String getIsExternal() {
		String isExternal = CommonConstants.FALSE;
		if(null != this.fixedPagePath && StringUtils.startsWith(this.fixedPagePath, CommonConstants.HTTP) || StringUtils.startsWith(this.fixedPagePath, CommonConstants.HTTPS) || StringUtils.startsWith(this.fixedPagePath, CommonConstants.WWW)) {
			isExternal = CommonConstants.TRUE;
		}
		return isExternal;
	}

	/**
	 * Gets the publication date.
	 *
	 * @return the publication date
	 */
	public String getPublicationDate() {
		String publicationDate = StringUtils.EMPTY;
		Resource linkPathResource = this.resourceResolver.getResource(this.fixedPagePath);
		if(null != linkPathResource) {
			Resource jcrResource = linkPathResource.getChild(CommonConstants.JCR_CONTENT_STR);
			if(null != jcrResource) {
				ValueMap pageProperties = jcrResource.getValueMap();
				SimpleDateFormat publicationDateFormat = new SimpleDateFormat(CommonConstants.DATE_FORMAT_PUBLISH);
				publicationDate = CommonUtil.getDateProperty(pageProperties, CommonConstants.PUBLICATION_DATE, publicationDateFormat);
			}
		}
		return publicationDate;
	}
	
	/**
	 * Gets the page property.
	 *
	 * @param propertyName the property name
	 * @return the page property
	 */
	private String getPageProperty(String propertyName) {
		String eyebrow = StringUtils.EMPTY;
		Resource linkPathResource = this.resourceResolver.getResource(this.fixedPagePath);
		if(null != linkPathResource) {
			Resource jcrResource = linkPathResource.getChild(CommonConstants.JCR_CONTENT_STR);
			if(null != jcrResource) {
				ValueMap pageProperties = jcrResource.getValueMap();
				eyebrow = CommonUtil.getStringProperty(pageProperties, propertyName);
			}
		}
		return eyebrow;
	}

	/**
	 * Sets the enable teaser content.
	 *
	 * @param enableTeaserContent the new enable teaser content
	 */
	public void setEnableTeaserContent(String enableTeaserContent) {
		this.enableTeaserContent = enableTeaserContent;
	}

	/**
	 * Gets the enable teaser content.
	 *
	 * @return the enable teaser content
	 */
	public String getEnableTeaserContent() {
		return enableTeaserContent;
	}

	public boolean isSecure() {
		return secure;
	}

	public void setSecure(boolean secure) {
		this.secure = secure;
	}
	
	public String getFixedPagePath() {
		return fixedPagePath;
	}
}
