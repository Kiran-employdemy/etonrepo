package com.eaton.platform.core.models.articlefeature;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.Via;

import com.day.cq.dam.api.DamConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.injectors.annotations.EatonSiteConfigInjector;
import com.eaton.platform.core.models.SiteResourceSlingModel;
import com.eaton.platform.core.util.CommonUtil;

/**
 * <html> Description: This Sling Model used in ArticleFeatureHelper class. </html>
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ArticleFeatureModel {

	/** The article type. */
	@Inject @Via("resource") 
	private String articleType;
	
	/** The article image. */
    @Inject @Via("resource") 
	private String articleImage;
	
	/** The article image alt text. */
    @Inject @Via("resource") 
	private String articleImageAltText;
	
	/** The article eyebrow. */
    @Inject @Via("resource") 
	private String articleEyebrow;
	
	/** The article title. */
    @Inject @Via("resource") 
	private String articleTitle;
	
	/** The article byline. */
    @Inject @Via("resource") 
	private String articleByline;
	
	/** The article link path. */
    @Inject @Via("resource") 
	private String articleLinkPath;
	
	/** The article link open new window. */
    @Inject @Via("resource") 
	private String articleLinkOpenNewWindow;
                   
	/** The article desc. */
    @Inject @Via("resource") 
	private String articleDesc;
	
	/** The article pub date. */
    @Inject @Via("resource") 
	private Calendar articlePubDate;
	
	/** The Mobile Transforms. */
    @Inject @Via("resource") 
	private String mobileTrans;
	
	/** The Desktop Transforms. */
    @Inject @Via("resource") 
	private String desktopTrans;

    /** The resource resolver. */
    @Inject @Source("sling-object")
    private ResourceResolver resourceResolver;
    
    @EatonSiteConfigInjector
	private Optional<SiteResourceSlingModel> siteResourceSlingModel;
    
	/**
	 * Gets the article type.
	 *
	 * @return the article type
	 */
	public String getArticleType() {
		String articleTypeValue = this.articleType;
		if(null == articleTypeValue) {
			articleTypeValue = StringUtils.EMPTY;
		}
		return articleTypeValue;
	}

    /**
     * Gets the link image.
     *
     * @return the link image
     */
    public String getArticleImage() {
    	String articleImagePath = StringUtils.EMPTY;
		if(StringUtils.equals(CommonConstants.FIXED_LIST, articleType)) {
			articleImagePath = getPageProperty(CommonConstants.TEASER_IMAGE_PATH);
		} else if(StringUtils.equals(CommonConstants.MANUAL_LIST, articleType)) {
			articleImagePath = articleImage;
		}
    	return articleImagePath;
    }
    
    /**
     * Gets the alt text.
     *
     * @return the alt text
     */
    public String getAltText() {
    	String imageAltText = StringUtils.EMPTY;
		if(StringUtils.equals(CommonConstants.FIXED_LIST, articleType)) {
			imageAltText = CommonUtil.getAssetAltText(this.resourceResolver, getArticleImage());
		} else if(StringUtils.equals(CommonConstants.MANUAL_LIST, articleType)) {
			imageAltText = articleImageAltText;
			if(null ==  articleImageAltText) {
				imageAltText = CommonUtil.getAssetAltText(resourceResolver, articleImage);
			}
		}
    	return imageAltText;
    }
    
	/**
	 * Gets the link title.
	 *
	 * @return the link title
	 */
	public String getArticleTitle() {
		String title = StringUtils.EMPTY;
		if(StringUtils.equals(CommonConstants.FIXED_LIST, articleType)) {
			title = getPageProperty(CommonConstants.TEASER_TITLE);
			if(StringUtils.isBlank(title)) {
				title = CommonUtil.getLinkTitle(null, this.articleLinkPath, this.resourceResolver);
			}
		} else if(StringUtils.equals(CommonConstants.MANUAL_LIST, articleType)) {
			title = articleTitle;
			// get the link title from page if link starts within the list of site root path configs.
			if(null == title && CommonUtil.startsWithAnySiteContentRootPath(articleLinkPath)) {
				title = CommonUtil.getLinkTitle(articleTitle, articleLinkPath, resourceResolver);
			}
		}
    	return title;
	}
	
	/**
	 * Gets the page link.
	 *
	 * @return the page link
	 */
	public String getArticleLinkPath() {
		return CommonUtil.dotHtmlLink(this.articleLinkPath, this.resourceResolver);
	}
	
	/**
	 * Gets the article byline.
	 *
	 * @return the article byline
	 */
	public String getArticleByline() {
    	String byline = StringUtils.EMPTY;
		if(StringUtils.equals(CommonConstants.FIXED_LIST, articleType)) {
			byline = getPageProperty(CommonConstants.TEASER_BYLINE);
		} else if(StringUtils.equals(CommonConstants.MANUAL_LIST, articleType)) {
			byline = articleByline;
		}
    	return byline;
	}
	
	/**
	 * Gets the new window.
	 *
	 * @return the new window
	 */
	public String getArticleLinkOpenNewWindow() {
		String openNewWindow = StringUtils.EMPTY;
		if(StringUtils.equals(CommonConstants.TRUE,articleLinkOpenNewWindow)) {
			openNewWindow = CommonConstants.TARGET_BLANK;
		}
		return openNewWindow;
	}
	
	/**
	 * Gets the link eyebrow.
	 *
	 * @return the link eyebrow
	 */
	public String getArticleEyebrow() {
		String eyebrow = StringUtils.EMPTY;
		if(StringUtils.equals(CommonConstants.FIXED_LIST, articleType)) {
			eyebrow = getPageProperty(CommonConstants.EYEBROW_TITLE);
		} else if(StringUtils.equals(CommonConstants.MANUAL_LIST, articleType)) {
			eyebrow = articleEyebrow;
		}
    	return eyebrow;
    	
	}
	
	/**
	 * Gets the article desc.
	 *
	 * @return the article desc
	 */
	public String getArticleDesc() {
		String description = StringUtils.EMPTY;
		if(StringUtils.equals(CommonConstants.FIXED_LIST, articleType)) {
			description = getPageProperty(CommonConstants.TEASER_DESCRIPTION);
		} else if(StringUtils.equals(CommonConstants.MANUAL_LIST, articleType)) {
			description = articleDesc;
		}
    	return description;
	}
	
	/**
	 * Gets the publication date.
	 *
	 * @return the publication date
	 */
	public String getArticlePubDate() {
		String publicationDate = StringUtils.EMPTY;
		SimpleDateFormat publicationDateFormat  = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT_PUBLISH);
		if (siteResourceSlingModel.isPresent()) {
			boolean isUnitedStatesDateFormat = Boolean.parseBoolean(siteResourceSlingModel.get().getUnitedStatesDateFormat());
			if(isUnitedStatesDateFormat) {
				publicationDateFormat = new SimpleDateFormat(CommonConstants.UNITED_STATES_DATE_FORMAT_PUBLISH);
			}
		}
		if(StringUtils.equals(CommonConstants.FIXED_LIST, articleType)) {
			ValueMap pageProperties = getArticleValueMap();
			if(null != pageProperties) {
				publicationDate = CommonUtil.getDateProperty(pageProperties, CommonConstants.PUBLICATION_DATE, publicationDateFormat);
				if(StringUtils.isBlank(publicationDate)) {
					publicationDate = CommonUtil.getDateProperty(pageProperties, DamConstants.PN_PAGE_LAST_REPLICATED, publicationDateFormat);
				}
			}
		} else if(StringUtils.equals(CommonConstants.MANUAL_LIST, articleType) && null != articlePubDate) {
				publicationDateFormat.setCalendar(articlePubDate);
				publicationDate = publicationDateFormat.format(articlePubDate.getTime());
		}
		return publicationDate;
	}

	/**
	 * Gets the article value map.
	 *
	 * @return the article value map
	 */
	private ValueMap getArticleValueMap() {
		ValueMap pageProperties = null;
		Resource linkPathResource = resourceResolver.getResource(articleLinkPath);
		if(null != linkPathResource) {
			Resource jcrResource = linkPathResource.getChild(CommonConstants.JCR_CONTENT_STR);
			if(null != jcrResource) {
				pageProperties = jcrResource.getValueMap();
				
			}
		}
		return pageProperties;
	}
	
	/**
	 * Gets the page property.
	 *
	 * @param propertyName the property name
	 * @return the page property
	 */
	private String getPageProperty(String propertyName) {
		String propertyValue = StringUtils.EMPTY;
		Resource linkPathResource = this.resourceResolver.getResource(this.articleLinkPath);
		if(null != linkPathResource) {
			Resource jcrResource = linkPathResource.getChild(CommonConstants.JCR_CONTENT_STR);
			if(null != jcrResource) {
				ValueMap pageProperties = jcrResource.getValueMap();
				propertyValue = CommonUtil.getStringProperty(pageProperties, propertyName);
			}
		}
		return propertyValue;
	}
	
	/**
	 * Gets the desktop trans.
	 *
	 * @return the desktop trans
	 */
	public String getDesktopTrans() {
		return desktopTrans;
	}

	/**
	 * Gets the mobile trans.
	 *
	 * @return the mobile trans
	 */
	public String getMobileTrans() {
		return mobileTrans;
	}
	
	/**
	 * Sets the desktop transformed url.
	 *
	 * @return the desktop transformed url
	 */
    public String getDesktopTransformedUrl() {
    	String desktopTransformedUrl =null;
    	if(null != desktopTrans && !StringUtils.contains(desktopTrans, CommonConstants.DEFALUT_DESKTOP)){
        	desktopTransformedUrl = getArticleImage().trim().concat(CommonConstants.TRANSTORM).trim().concat(getDesktopTrans().trim()).concat(CommonConstants.IMAGE_JPG);
        } else {
        	desktopTransformedUrl = getArticleImage().trim();
        }
    	return desktopTransformedUrl;
    }
    
    /**
     * Sets the mobile transformed url.
     *
     * @return the mobile transformed url
     */
    public String getMobileTransformedUrl() {
        String mobileTransformedUrl =null;
		if(null != getArticleImage()) {
			if (null != mobileTrans && !StringUtils.contains(mobileTrans, CommonConstants.DEFAULT_MOBILE)) {
				mobileTransformedUrl = getArticleImage().trim().concat(CommonConstants.TRANSTORM).trim().concat(getMobileTrans().trim()).concat(CommonConstants.IMAGE_JPG);
			} else {
				mobileTransformedUrl = getArticleImage().trim();
			}
		}
        return mobileTransformedUrl;
    }

}
