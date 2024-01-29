package com.eaton.platform.core.models.cardlist;

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
 * <html> Description: This Sling Model used in CardListModel class
 *  to load Manual links multifield for Generic views</html> .
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CardListManualLinksModel {

	/** The Manual link image. */
	@Inject @Named("manualLinkImage")
	private String teaserImage;
	
	/** The Manual link image altText. */
	@Inject @Named("manualLinkImageAltText")
	private String altTxt;
	
	/**  The Manual block link title. */
	@Inject @Named("manualLinkTitle")
	private String headline;	
	
	/**  The Manual link path. */
	@Inject @Named("manualLinkPath")
	private String pagePath;
	
	/** The Manual link open new window. */
	@Inject @Named("manualLinkOpenNewWindow")
	private String newWindow ;
	
	/** The Manual link /. */
	@Inject @Named("manualLinkEyebrow")
	private String eyebrow;	
	
	/** The alignment desktop. */
	@Inject @Named("cardListAlignmentDesktop")
	private String alignmentDesktop;

	/** The alignment mobile. */
	@Inject @Named("cardListAlignmentMobile")
	private String alignmentMobile;
	
	/** The mobile transformed url. */
	@Inject
	private String mobileTransformedUrl;
	
	/** The desktop transformed url. */
	@Inject
	private String desktopTransformedUrl;

    /** The resource resolver. */
    @Inject @Source("sling-object")
    private ResourceResolver resourceResolver;
    
    /** The isUnitedStatesDateFormat. */
	private boolean isUnitedStatesDateFormat;
	
    /**
     * @param isUnitedStatesDateFormat
     */
	public void setUnitedStatesDateFormat(boolean isUnitedStatesDateFormat) {
		this.isUnitedStatesDateFormat = isUnitedStatesDateFormat;
	}
    
	 /**
 	 * Get the manualLinkImage.
 	 *
 	 * @return the manualLinkImage
 	 */
	
	public String getTeaserImage() {
		return teaserImage;
	}

	/**
	 * Gets the alt txt.
	 *
	 * @return the manualLinkImageAltText
	 */
	
	public String getAltTxt() {
		return altTxt;
	}

	/**
	 * Gets the headline.
	 *
	 * @return the manualLinkTitle
	 */
	public String getHeadline() {
		String manualLinkTitle = this.headline;
        // get the link title from page if link starts within the list of site root path configs.
		if(null == manualLinkTitle && CommonUtil.startsWithAnySiteContentRootPath(pagePath)) {
			String teaserTitle = getPageProperty(CommonConstants.TEASER_TITLE);			
			manualLinkTitle = StringUtils.isNotBlank(teaserTitle) ? teaserTitle : CommonUtil.getLinkTitle(null, pagePath, resourceResolver);
		}
		return manualLinkTitle ;
	}

	/**
	 * Gets the page property.
	 *
	 * @param propertyName the property name
	 * @return the page property
	 */
	private String getPageProperty(String propertyName) {
		String eyebrowValue = StringUtils.EMPTY;
		Resource linkPathResource = this.resourceResolver.getResource(pagePath);
		if(null != linkPathResource) {
			Resource jcrResource = linkPathResource.getChild(CommonConstants.JCR_CONTENT_STR);
			if(null != jcrResource) {
				ValueMap pageProperties = jcrResource.getValueMap();
				eyebrowValue = CommonUtil.getStringProperty(pageProperties, propertyName);
			}
		}
		return eyebrowValue;
	}

	/**
	 * Gets the page path.
	 *
	 * @return the manualLinkPath
	 */
	public String getPagePath() {
		return CommonUtil.dotHtmlLink(pagePath);
	}

	/**
	 * Gets the new window.
	 *
	 * @return the manualLinkOpenNewWindow
	 */
	public String getNewWindow() {
		String manualLinkOpenNewWindow = StringUtils.EMPTY;
		if(StringUtils.equals(CommonConstants.TRUE, newWindow) || StringUtils.equals(CommonConstants.TRUE, getIsExternal())) {
			manualLinkOpenNewWindow = CommonConstants.TARGET_BLANK;
		}
		return manualLinkOpenNewWindow;
	}

	/**
	 * Gets the eyebrow.
	 *
	 * @return the manualLinkEyebrow
	 */
	public String getEyebrow() {
		return eyebrow;
	}

	/**
	 * Gets the checks if is external.
	 *
	 * @return the checks if is external
	 */
	public String getIsExternal() {
		String isExternal = CommonConstants.FALSE;
		if(null != pagePath && (StringUtils.startsWith(pagePath, CommonConstants.HTTP) || StringUtils.startsWith(pagePath, CommonConstants.HTTPS) || StringUtils.startsWith(pagePath, CommonConstants.WWW))) {
			isExternal = CommonConstants.TRUE;
		}
		return isExternal;
	}
	

	/**
	 * Gets the publication date.
	 *
	 * @return the publication date
	 */
	public String getPublicationDateDisplay() {
		String publicationDate = StringUtils.EMPTY;
		Resource linkPathResource = resourceResolver.getResource(pagePath);
		if(null != linkPathResource) {
			Resource jcrResource = linkPathResource.getChild(CommonConstants.JCR_CONTENT_STR);
			if(null != jcrResource) {
				ValueMap pageProperties = jcrResource.getValueMap();
				SimpleDateFormat publicationDateFormat  = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT_PUBLISH);
				if(isUnitedStatesDateFormat) {
					publicationDateFormat = new SimpleDateFormat(CommonConstants.UNITED_STATES_DATE_FORMAT_PUBLISH);
				}
				publicationDate = CommonUtil.getDateProperty(pageProperties, CommonConstants.PUBLICATION_DATE, publicationDateFormat);
				if(StringUtils.isEmpty(publicationDate)) {
					publicationDate = CommonUtil.getDateProperty(pageProperties,NameConstants.PN_PAGE_LAST_REPLICATED, publicationDateFormat);
				}
			}
		}
		return publicationDate;
	}

	 /**
 	 * Gets the alignment desktop.
 	 *
 	 * @return the alignment desktop
 	 */
 	public String getAlignmentDesktop() {
		return alignmentDesktop;
	}

	/**
	 * Sets the alignment desktop.
	 *
	 * @param alignmentDesktop the new alignment desktop
	 */
	public void setAlignmentDesktop(String alignmentDesktop) {
		this.alignmentDesktop = alignmentDesktop;
	}

	/**
	 * Gets the alignment mobile.
	 *
	 * @return the alignment mobile
	 */
	public String getAlignmentMobile() {
		return alignmentMobile;
	}

	/**
	 * Sets the alignment mobile.
	 *
	 * @param alignmentMobile the new alignment mobile
	 */
	public void setAlignmentMobile(String alignmentMobile) {
		this.alignmentMobile = alignmentMobile;
	}

	/**
      * Gets the desktop transformed url.
      *
      * @return the desktop transformed url
      */
      public String getDesktopTransformedUrl() {
             return desktopTransformedUrl;
      }
  
      /**
       * Sets the desktop transformed url.
       *
       * @param desktopTrans the new desktop transformed url
       */
      public void setDesktopTransformedUrl(String desktopTrans) {
    	  if(null!=teaserImage){
	    	  if(null != desktopTrans && !StringUtils.contains(desktopTrans, CommonConstants.DEFALUT_DESKTOP)){
	          	desktopTransformedUrl = teaserImage.trim().concat(CommonConstants.TRANSTORM).trim().concat(desktopTrans.trim()).concat(CommonConstants.IMAGE_JPG);
	          } else {
	          	desktopTransformedUrl = teaserImage.trim();
	          }
    	  }
      }
      /**
      * Gets the mobile transformed url.
      *
      * @return the mobile transformed url
      */
      public String getMobileTransformedUrl() {
             return mobileTransformedUrl;
      }
      
      /**
       * Sets the mobile transformed url.
       *
       * @param mobileTrans the new mobile transformed url
       */
      public void setMobileTransformedUrl(String mobileTrans) {
    	  if(null!=teaserImage){
	    	  if(null != mobileTrans && !StringUtils.contains(mobileTrans, CommonConstants.DEFAULT_MOBILE)){
	              mobileTransformedUrl = teaserImage.trim().concat(CommonConstants.TRANSTORM).trim().concat(mobileTrans.trim()).concat(CommonConstants.IMAGE_JPG);
	          } else {
	          	mobileTransformedUrl = teaserImage.trim();
	          }
    	  }
      }

}
