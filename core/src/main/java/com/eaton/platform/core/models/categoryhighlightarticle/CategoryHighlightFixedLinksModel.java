package com.eaton.platform.core.models.categoryhighlightarticle;

import java.text.SimpleDateFormat;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;

import com.day.cq.wcm.api.NameConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.util.CommonUtil;

/**
 * <html> Description: This Sling Model used in CategoryHighlightArticleModel
 * class to load Fixed links multifield for Generic views</html> .
 * 
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CategoryHighlightFixedLinksModel implements CategoryHighLightArticleService {

	/** The page link. */
	@Inject
	@Named("cardFixedLinkPath")
	private String pagePath;

	/** The new window. */
	@Inject
	@Named("cardFixedLinkOpenNewWindow")
	private String newWindow;
	
	/** The alignment desktop. */
	@Inject @Named("cardAlignmentDesktop")
	private String alignmentDesktop;

	/** The alignment mobile. */
	@Inject @Named("cardAlignmentMobile")
	private String alignmentMobile;
	
	/** The mobile transformed url. */
	@Inject
	private String mobileTransformedUrl;

	/** The desktop transformed url. */
	@Inject
	private String desktopTransformedUrl;

	/** The tablet transformed url. */
	private String tabletTransformedUrl;

	/** The resource resolver. */
	@Inject
	@Source("sling-object")
	private ResourceResolver resourceResolver;
	
	/** The isUnitedStatesDateFormat. */
	private boolean isUnitedStatesDateFormat;
	
    /**
     * @param isUnitedStatesDateFormat
     */
	public void setUnitedStatesDateFormat(boolean isUnitedStatesDateFormat) {
		this.isUnitedStatesDateFormat = isUnitedStatesDateFormat;
	}

	/* 
	 * Gets the link image.
	 * @return the link imag
	 */
	@Override
	public String getTeaserImage() {
		return getPageProperty(CommonConstants.TEASER_IMAGE_PATH);
	}

	/* (non-Javadoc)
	 * @see com.eaton.platform.core.models.categoryhighlightarticle.CategoryHighLightArticleService#getAltTxt()
	 */
	@Override
	public String getAltTxt() {
		return CommonUtil.getAssetAltText(this.resourceResolver, getTeaserImage());
	}

	/* (non-Javadoc)
	 * @see com.eaton.platform.core.models.categoryhighlightarticle.CategoryHighLightArticleService#getHeadline()
	 */
	@Override
	public String getHeadline() {
		String teaserTitle = getPageProperty(CommonConstants.TEASER_TITLE);
		return (StringUtils.isNotBlank(teaserTitle) ? teaserTitle : CommonUtil
				.getLinkTitle(null, this.pagePath, this.resourceResolver));

	}

	/* (non-Javadoc)
	 * @see com.eaton.platform.core.models.categoryhighlightarticle.CategoryHighLightArticleService#getPagePath()
	 */
	@Override
	public String getPagePath() {
		return CommonUtil.dotHtmlLink(this.pagePath, this.resourceResolver);
	}

	/* (non-Javadoc)
	 * @see com.eaton.platform.core.models.categoryhighlightarticle.CategoryHighLightArticleService#getNewWindow()
	 */
	@Override
	public String getNewWindow() {
		String fixedLinkOpenNewWindow = StringUtils.EMPTY;
		if (StringUtils.equals(CommonConstants.TRUE, this.newWindow)
				|| StringUtils.equals(CommonConstants.TRUE, getIsExternal())) {
			fixedLinkOpenNewWindow = CommonConstants.TARGET_BLANK;
		}
		return fixedLinkOpenNewWindow;
	}

	/* (non-Javadoc)
	 * @see com.eaton.platform.core.models.categoryhighlightarticle.CategoryHighLightArticleService#getIsExternal()
	 */
	
	public String getIsExternal() {
		String isExternal = CommonConstants.FALSE;
		if ((null != this.pagePath)
				&& (StringUtils.startsWith(this.pagePath, CommonConstants.HTTP)
				|| StringUtils.startsWith(this.pagePath, CommonConstants.HTTPS)
				|| StringUtils.startsWith(this.pagePath, CommonConstants.WWW))) {
			isExternal = CommonConstants.TRUE;
		}
		return isExternal;
	}

	/* (non-Javadoc)
	 * @see com.eaton.platform.core.models.categoryhighlightarticle.CategoryHighLightArticleService#getPublicationDateDisplay()
	 */
	@Override
	public String getPublicationDateDisplay() {
		String publicationDate = StringUtils.EMPTY;
		Resource linkPathResource = resourceResolver.getResource(this.pagePath);
		if (null != linkPathResource) {
			Resource jcrResource = linkPathResource.getChild(CommonConstants.JCR_CONTENT_STR);
			if (null != jcrResource) {
				ValueMap pageProperties = jcrResource.getValueMap();
				SimpleDateFormat publicationDateFormat  = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT_PUBLISH);
				if(isUnitedStatesDateFormat) {
					publicationDateFormat = new SimpleDateFormat(CommonConstants.UNITED_STATES_DATE_FORMAT_PUBLISH);
				}
				publicationDate = CommonUtil.getDateProperty(pageProperties,CommonConstants.PUBLICATION_DATE,publicationDateFormat);
				if(StringUtils.isEmpty(publicationDate)) {
					publicationDate = CommonUtil.getDateProperty(pageProperties,NameConstants.PN_PAGE_LAST_REPLICATED, publicationDateFormat);
				}
			}
		}
		return publicationDate;
	}

	/* (non-Javadoc)
	 * @see com.eaton.platform.core.models.categoryhighlightarticle.CategoryHighLightArticleService#getEyebrow()
	 */
	@Override
	public String getEyebrow() {
		return getPageProperty(CommonConstants.EYEBROW_TITLE);
	}

	/**
	 * Gets the page property.
	 * 
	 * @param propertyName
	 *            the property name
	 * @return the page property
	 */
	private String getPageProperty(String propertyName) {
		String eyebrow = StringUtils.EMPTY;
		Resource linkPathResource = resourceResolver.getResource(this.pagePath);
		if (null != linkPathResource) {
			Resource jcrResource = linkPathResource	.getChild(CommonConstants.JCR_CONTENT_STR);
			if (null != jcrResource) {
				ValueMap pageProperties = jcrResource.getValueMap();
				eyebrow = CommonUtil.getStringProperty(pageProperties,	propertyName);
			}
		}
		return eyebrow;
	}

	/* (non-Javadoc)
	 * @see com.eaton.platform.core.models.categoryhighlightarticle.CategoryHighLightArticleService#getAlignmentDesktop()
	 */
	@Override
	public String getAlignmentDesktop() {
		return alignmentDesktop;
	}

	/* (non-Javadoc)
	 * @see com.eaton.platform.core.models.categoryhighlightarticle.CategoryHighLightArticleService#setAlignmentDesktop(java.lang.String)
	 */
	@Override
	public void setAlignmentDesktop(String alignmentDesktop) {
		this.alignmentDesktop = alignmentDesktop;
	}

	/* (non-Javadoc)
	 * @see com.eaton.platform.core.models.categoryhighlightarticle.CategoryHighLightArticleService#getAlignmentMobile()
	 */
	@Override
	public String getAlignmentMobile() {
		return alignmentMobile;
	}

	/* (non-Javadoc)
	 * @see com.eaton.platform.core.models.categoryhighlightarticle.CategoryHighLightArticleService#setAlignmentMobile(java.lang.String)
	 */
	@Override
	public void setAlignmentMobile(String alignmentMobile) {
		this.alignmentMobile = alignmentMobile;
	}

	/* (non-Javadoc)
	 * @see com.eaton.platform.core.models.categoryhighlightarticle.CategoryHighLightArticleService#getDesktopTransformedUrl()
	 */
	@Override
	public String getDesktopTransformedUrl() {
		return desktopTransformedUrl;
	}

	/* (non-Javadoc)
	 * @see com.eaton.platform.core.models.categoryhighlightarticle.CategoryHighLightArticleService#setDesktopTransformedUrl(java.lang.String)
	 */
	@Override
	public void setDesktopTransformedUrl(String desktopTrans) {
		if (StringUtils.isNotBlank(desktopTrans) && !StringUtils.contains(desktopTrans,	CommonConstants.DEFALUT_DESKTOP)) {
			desktopTransformedUrl = getTeaserImage().trim().concat(CommonConstants.TRANSTORM).trim().concat(desktopTrans.trim()).concat(CommonConstants.IMAGE_JPG);
		} else {
			desktopTransformedUrl = getTeaserImage().trim();
		}
	}

	/* (non-Javadoc)
	 * @see com.eaton.platform.core.models.categoryhighlightarticle.CategoryHighLightArticleService#getMobileTransformedUrl()
	 */
	@Override
	public String getMobileTransformedUrl() {
		return mobileTransformedUrl;
	}

	/* (non-Javadoc)
	 * @see com.eaton.platform.core.models.categoryhighlightarticle.CategoryHighLightArticleService#setMobileTransformedUrl(java.lang.String)
	 */
	@Override
	public void setMobileTransformedUrl(String mobileTrans) {
		if (StringUtils.isNotBlank(mobileTrans) && !StringUtils.contains(mobileTrans,	CommonConstants.DEFAULT_MOBILE)) {
			mobileTransformedUrl = getTeaserImage().trim().concat(CommonConstants.TRANSTORM).trim().concat(mobileTrans.trim()).concat(CommonConstants.IMAGE_JPG);
		} else {
			mobileTransformedUrl = getTeaserImage().trim();
		}
	}

	/* (non-Javadoc)
	 * @see com.eaton.platform.core.models.categoryhighlightarticle.CategoryHighLightArticleService#getTabletTransformedUrl()
	 */
	@Override
	public String getTabletTransformedUrl() {
		return tabletTransformedUrl;
	}

	/* (non-Javadoc)
	 * @see com.eaton.platform.core.models.categoryhighlightarticle.CategoryHighLightArticleService#setTabletTransformedUrl(java.lang.String)
	 */
	@Override
	public void setTabletTransformedUrl(String tabletTrans) {
		if (StringUtils.isNotBlank(tabletTrans) && !StringUtils.contains(tabletTrans,	CommonConstants.DEFAULT_TABLET)) {
			tabletTransformedUrl = getTeaserImage().trim().concat(CommonConstants.TRANSTORM).trim().concat(tabletTrans.trim()).concat(CommonConstants.IMAGE_JPG);
		} else {
			tabletTransformedUrl = getTeaserImage().trim();
		}
	}
}
