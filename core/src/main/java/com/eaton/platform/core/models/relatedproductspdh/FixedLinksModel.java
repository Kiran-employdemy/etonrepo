package com.eaton.platform.core.models.relatedproductspdh;

import javax.inject.Inject;
import javax.inject.Named;

import com.eaton.platform.core.models.PIMResourceSlingModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.RelatedProductsService;
import com.eaton.platform.core.util.CommonUtil;

import java.util.Objects;

/**
 * <html> Description: This Sling Model used in RelatedProductsPDHModel class
 *  to load Fixed links multifield values from PIM Record</html> .
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FixedLinksModel implements RelatedProductsService {

	/** The page link. */
	@Inject
	private String fixedLinkPath;
	
	/** The new window. */
	@Inject @Named("fixedLinkOpenNewWindow")
	private String newWindow;
	
	/** The mobile transformed url. */
	@Inject
	private String mobileTransformedUrl;
	
	/** The desktop transformed url. */
	@Inject
	private String desktopTransformedUrl;


	private String marketingDescription;

	private boolean showMarketingDescription;

	private boolean isSecure;

    /** The resource resolver. */
    @Inject @Source("sling-object")
    private ResourceResolver resourceResolver;

    /**
     * Gets the link image.
     *
     * @return the link image
     */
	@Override
    public String getLinkImage() {
    	return getPimPageProperties(CommonConstants.PIM_PRIMARY_IMAGE);
    }

    /**
     * Gets the link image alt text.
     *
     * @return the link image alt text
     */
	@Override
    public String getAltText() {
    	return CommonUtil.getAssetAltText(resourceResolver, getLinkImage());
    }
	/**
	 * Gets the link title.
	 *
	 * @return the link title
	 */
	@Override
	public String getLinkEyebrow() {
		String productTitle = StringUtils.EMPTY;
		String primarySubCategory = getPimPageProperties(CommonConstants.PIM_PRIMARY_SUB_CATEGORY);
		PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
		if(null != pageManager) {
			Page primarySubCategoryPage = pageManager.getPage(primarySubCategory);
			if(null != primarySubCategoryPage) {
				productTitle = CommonUtil.getLinkTitle(null, primarySubCategory, resourceResolver);
			}
		}
		return productTitle;
	}

	/**
	 * Gets the page link.
	 *
	 * @return the page link
	 */
	@Override
	public String getLinkPath() {
		return CommonUtil.dotHtmlLink(fixedLinkPath, resourceResolver);
	}

	/**
	 * Gets the new window.
	 *
	 * @return the new window
	 */
	@Override
	public String getNewWindow() {
		String fixedLinkOpenNewWindow = StringUtils.EMPTY;
		if(StringUtils.equals(CommonConstants.TRUE, this.newWindow)) {
			fixedLinkOpenNewWindow = CommonConstants.TARGET_BLANK;
		}
		return fixedLinkOpenNewWindow;
	}

	/**
	 * Gets the link eyebrow.
	 *
	 * @return the link eyebrow
	 */
	@Override
	public String getLinkTitle() {
		return getPimPageProperties(CommonConstants.PIM_PRODUCT_NAME);
	}

	 /**
    * Gets the desktop transformed url.
    *
    * @return the desktop transformed url
    */
	@Override
    public String getDesktopTransformedUrl() {
           return desktopTransformedUrl;
    }

    /**
     * Sets the desktop transformed url.
     *
     * @param desktopTrans the new desktop transformed url
     */
	@Override
    public void setDesktopTransformedUrl(String desktopTrans) {
    	 if(null!=getLinkImage()){
    	 	String trimmedImageLink = getLinkImage().trim();
			if(null != desktopTrans && !StringUtils.contains(desktopTrans, CommonConstants.DEFALUT_DESKTOP) && StringUtils.startsWith(trimmedImageLink,CommonConstants.CONTENT_DAM)){
				desktopTransformedUrl = trimmedImageLink.concat(CommonConstants.TRANSTORM).trim().concat(desktopTrans.trim()).concat(CommonConstants.IMAGE_JPG);
			} else {
				desktopTransformedUrl = trimmedImageLink;
			}
    	 }
    }

    /**
    * Gets the mobile transformed url.
    *
    * @return the mobile transformed url
    */
	@Override
    public String getMobileTransformedUrl() {
          return mobileTransformedUrl;
    }

    /**
     * Sets the mobile transformed url.
     *
     * @param mobileTrans the new mobile transformed url
     */
	@Override
    public void setMobileTransformedUrl(String mobileTrans) {
    	if(null!=getLinkImage()){
            String trimmedImageLink = getLinkImage().trim();
            if(null != mobileTrans && !StringUtils.contains(mobileTrans, CommonConstants.DEFAULT_MOBILE) && StringUtils.startsWith(trimmedImageLink,CommonConstants.CONTENT_DAM)){
	            mobileTransformedUrl = trimmedImageLink.concat(CommonConstants.TRANSTORM).trim().concat(mobileTrans.trim()).concat(CommonConstants.IMAGE_JPG);
	        } else {
            	mobileTransformedUrl = trimmedImageLink;
	        }
    	}
    }

    /**
     * Gets the pim page properties.
     *
     * @param pimPropertyName the pim property name
     * @return the pim page properties
     */
    public String getPimPageProperties(String pimPropertyName) {
    	String propertyValue = StringUtils.EMPTY;
		Resource fixedPageResource = resourceResolver.getResource(fixedLinkPath);
		if(null != fixedPageResource && StringUtils.equals(CommonUtil.getStringProperty(fixedPageResource.getValueMap(), JcrConstants.JCR_PRIMARYTYPE), NameConstants.NT_PAGE)){
			Resource jcrResource = fixedPageResource.getChild(JcrConstants.JCR_CONTENT);
			if(null != jcrResource) {
				ValueMap pageProperties = jcrResource.getValueMap();
				if(StringUtils.equals(StringUtils.EMPTY, CommonUtil.getStringProperty(pageProperties, NameConstants.PN_HIDE_IN_NAV)) && StringUtils.equals(CommonConstants.PAGE_TYPE_PRODUCT_FAMILY_PAGE, CommonUtil.getStringProperty(pageProperties, CommonConstants.PAGE_TYPE))) {
					String pimPagePath = CommonUtil.getStringProperty(pageProperties, CommonConstants.PAGE_PIM_PATH);
					if(StringUtils.isNotBlank(pimPagePath)) {
						propertyValue=setPIMProperties(propertyValue,pimPagePath,pimPropertyName);
					}
				}


			}
		}
		return propertyValue;
	}

    private String setPIMProperties(final String propertyValue,final String pimPagePath, final String pimPropertyName){
		String pdhPropertyValue = StringUtils.EMPTY;
    	final Resource pimPagePathResource = resourceResolver.getResource(pimPagePath);
    	if(null != pimPagePathResource) {
			final ValueMap pimPageProperties = pimPagePathResource.getValueMap();
			final PIMResourceSlingModel pimResourceSlingModel = pimPagePathResource.adaptTo(PIMResourceSlingModel.class);
			if(Objects.nonNull(pimResourceSlingModel)) {
				pdhPropertyValue = CommonUtil.getStringProperty(pimPageProperties, pimPropertyName);
				if ((pimPropertyName.equals(CommonConstants.PIM_PRIMARY_IMAGE)) && StringUtils.isBlank(propertyValue)) {
					pdhPropertyValue = pdhPropertyValue.isEmpty() ? pimResourceSlingModel.getPdhPrimaryImg() : pdhPropertyValue;
				} else if ((pimPropertyName.equals(CommonConstants.PIM_PRODUCT_NAME)) && StringUtils.isBlank(propertyValue)) {
					pdhPropertyValue = pdhPropertyValue.isEmpty() ? pimResourceSlingModel.getPdhProdName() : pdhPropertyValue;
				} else if ((pimPropertyName.equals(CommonConstants.PIM_MARKETING_DESCRIPTION)) && StringUtils.isBlank(propertyValue)) {
					pdhPropertyValue = pdhPropertyValue.isEmpty() ? pimResourceSlingModel.getPdhProdDesc() : pdhPropertyValue;
				} else if ((pimPropertyName.equals(CommonConstants.PIM_CORE_FEATURES)) && StringUtils.isBlank(propertyValue)) {
					pdhPropertyValue = pdhPropertyValue.isEmpty() ? pimResourceSlingModel.getPdhCoreFeatures() : pdhPropertyValue;
				}
			}
    	}
		return pdhPropertyValue;
    }


	/**
	 *
	 * @return marketingDescription
	 */
	public String getMarketingDescription(){
		return marketingDescription;
	}

	public void setMarketingDescription(){
		// EAT-5215
		if (showMarketingDescription) {
			if (StringUtils.isNoneEmpty(getPimPageProperties(CommonConstants.PIM_MARKETING_DESCRIPTION))) {
				marketingDescription = getPimPageProperties(CommonConstants.PIM_MARKETING_DESCRIPTION);
			} else {
				marketingDescription = getPimPageProperties(CommonConstants.PIM_PDH_DESCRIPTION);
			}
		}
	}



	public void setShowMarketingDescription(boolean showMarketingDescription) {
		this.showMarketingDescription = showMarketingDescription;
	}

	public boolean isSecure() {
		return isSecure;
	}

	public void setSecure(boolean secure) {
		isSecure = secure;
	}
}
