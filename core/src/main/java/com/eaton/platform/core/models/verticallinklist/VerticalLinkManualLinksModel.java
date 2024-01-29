package com.eaton.platform.core.models.verticallinklist;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.util.CommonUtil;

/**
 * <html> Description: This Sling Model used in VerticalListModel class
 *  to load Manual links multifield</html> .
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class VerticalLinkManualLinksModel {

	/**  The Manual block link title. */
	@Inject @Named("manualLinkTitle")
	private String headline;	
	
	/**  The Manual link path. */
	@Inject @Named("manualLinkPath")
	private String pagePath;
	
	/** The Manual link open new window. */
	@Inject @Named("manualLinkOpenNewWindow")
	private String newWindow ;
	
    /** The resource resolver. */
    @Inject @Source("sling-object")
    private ResourceResolver resourceResolver;
	
    /** The resource page. */
    @Inject
    private Page resourcePage;

	private boolean secure;

	/**
	 * Gets the headline.
	 *
	 * @return the headline
	 */
	public String getHeadline() {
		String manualLinkTitle = headline;
		Resource assetResource = getAssetResource();
        // get the link title from page if link starts within the list of site root path configs.
		if(null == manualLinkTitle) {
			if(CommonUtil.startsWithAnySiteContentRootPath(pagePath)) {
				manualLinkTitle = CommonUtil.getLinkTitle(null, pagePath, resourceResolver);
			} else if(StringUtils.startsWith(pagePath, CommonConstants.CONTENT_DAM) && null != assetResource) {
				manualLinkTitle = CommonUtil.getAssetTitle(pagePath, assetResource);
			}
		}
		return manualLinkTitle ;
	}

	

	/**
	 * Gets the asset title DTM.
	 *
	 * @return the asset title DTM
	 */
	public String getAssetTitleDTM() {
		return CommonUtil.getAssetTitle(pagePath, getAssetResource());
	}
	
	/**
	 * Gets the asset format name DTM.
	 *
	 * @return the asset format name DTM
	 */
	public String getAssetFormatNameDTM() {
		String dcFormat = StringUtils.EMPTY;
		if (null != getAsset()) { 
			dcFormat = CommonUtil.getAssetMetadataValue(DamConstants.DC_FORMAT, getAssetResource());
		}
		return dcFormat;
	}
	
	/**
	 * Gets the analytics event.
	 *
	 * @return the analytics event
	 */
	public String getAnalyticsEventDTM() {
		String eventName = CommonConstants.DTM_DOWNLOAD_ASSETS;
		if(CommonUtil.getIsInvestorPage(resourcePage)) {
			eventName = CommonConstants.DTM_DOWNLOAD_INVESTOR;
		}
		return eventName;
	}

	/**
	 * Gets the asset UUID.
	 *
	 * @return the asset UUID
	 */
	public String getAssetUUID() {
		String uniqueDocId = StringUtils.EMPTY;
		if ((StringUtils.startsWith(pagePath, CommonConstants.CONTENT_DAM)) && (null != getAsset())) {
			uniqueDocId = getAsset().getID();
		}
		return uniqueDocId;
	}
	
	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		String description = StringUtils.EMPTY;
		if ((StringUtils.startsWith(pagePath, CommonConstants.CONTENT_DAM)) && (null != getAsset())) {
			description = getAsset().getMetadataValue(DamConstants.DC_DESCRIPTION);
		}
		return description;
	}

	/**
	 * Gets the page path.
	 *
	 * @return the manualLinkPath
	 */
	public String getPagePath() {
		String path;
		if(StringUtils.startsWith(pagePath, CommonConstants.CONTENT_DAM)) {
			path = pagePath;
		} else {
			path = CommonUtil.dotHtmlLink(pagePath);
		}
		return path;
	}

	public void setPagePath(String pagePath) {
		this.pagePath = pagePath;
	}

	/**
	 * Gets the new window.
	 *
	 * @return the manualLinkOpenNewWindow
	 */
	public String getNewWindow() {
		String manualLinkOpenNewWindow = StringUtils.EMPTY;
		if(StringUtils.equals(CommonConstants.TRUE, newWindow)) {
			manualLinkOpenNewWindow = CommonConstants.TARGET_BLANK;
		}
		return manualLinkOpenNewWindow;
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
	 * Checks if is asset path.
	 *
	 * @return true, if is asset path
	 */
	public boolean getIsAssetLink() {
		boolean isAssetPath = false;
		if (StringUtils.startsWith(pagePath, CommonConstants.CONTENT_DAM)) {
			isAssetPath = true;
		}
		return isAssetPath;
	}

	/**
	 * Gets the asset size.
	 *
	 * @return the asset size
	 */
	public String getAssetSize() {
		return CommonUtil.getAssetSize(getAsset());
	}

	/**
	 * Gets the asset.
	 *
	 * @return the asset
	 */
	private Asset getAsset() {
		return CommonUtil.getAsset(getAssetResource());
	}

	/**
	 * Gets the asset resource.
	 *
	 * @return the asset resource
	 */
	private Resource getAssetResource() {
		return resourceResolver.getResource(pagePath);
	}


	public boolean isSecure() {
		return secure;
	}

	public void setSecure(boolean secure) {
		this.secure = secure;
	}
}
