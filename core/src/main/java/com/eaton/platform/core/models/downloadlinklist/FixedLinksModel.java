package com.eaton.platform.core.models.downloadlinklist;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Optional;

import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.util.CommonUtil;

/**
 * <html> Description: This Sling Model used in DownloadLinkListModel class to load
 * Manual links multifield for Download with Description view</html> .
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FixedLinksModel {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(FixedLinksModel.class);

	/** The download link title. */
	@Inject
	private String downloadLinkTitle;

	/** The download link description. */
	@Inject
	private String downloadLinkDescription;

	/** SHA. */
	@Inject
	private String SHA;

	/** ECCN */
	@Inject
	private String ECCN;

	/** The download link path. */
	@Inject
	private String downloadLinkPath;

	/** The download link new window. */
	@Inject
	private String downloadLinkNewWindow;

	/** The is asset path. */
	@Inject
	private Boolean isAssetPath;

	/** The resource resolver. */
	@Inject
	@Source("sling-object")
	private ResourceResolver resourceResolver;
	
    /** The resource page. */
    @Inject
    private Page resourcePage;

	private boolean secure = false;


	private String trackDownload;


	/**
	 * Gets the asset link title.
	 *
	 * @return the asset link title
	 */
	public String getDownloadLinkTitle() {
		String linkTitle = downloadLinkTitle;
		if (null == linkTitle) {
			linkTitle = CommonUtil.getAssetTitle(downloadLinkPath, getAssetResource());
		}
		return linkTitle;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDownloadLinkDescription() {
		String assetDescription = downloadLinkDescription;
		if (null == assetDescription) {
			if (null != getAsset()) {
				assetDescription = getAsset().getMetadataValue(DamConstants.DC_DESCRIPTION);
			} else {
				assetDescription = StringUtils.EMPTY;
			}
		}
		return assetDescription;
	}

	/**
	 * Gets the asset path.
	 *
	 * @return the asset path
	 */
	public String getDownloadLinkPath() {
	        String assetLinkPath = StringUtils.EMPTY;
	        Resource res = getAssetResource();
	        LOGGER.debug("DonwloadLinkModel :: getAssetPath() :: Start");
	            if ((null != res) && (StringUtils.equals(CommonUtil.getStringProperty(res.getValueMap(), CommonConstants.JCR_PRIMARY_TYPE),
	                    DamConstants.NT_DAM_ASSET))) {
	                assetLinkPath = downloadLinkPath;
	                final String assetContentId = Optional.ofNullable(CommonUtil.getAssetMetadataValue(CommonConstants.DC_PERCOLATE_CONTENT_ID, res)).orElse(StringUtils.EMPTY);
	    	        if(StringUtils.isNotEmpty(assetContentId)) {
	    	            assetLinkPath = assetLinkPath+"?"+CommonConstants.ASSET_CONTENT_ID_PARAM+"="+assetContentId;
	    	        }
	            }
	       
	        LOGGER.debug("DonwloadLinkModel :: getAssetPath() :: End");
	        return assetLinkPath;
	    }
		
	/**
	 * Gets the asset title DTM.
	 *
	 * @return the asset title DTM
	 */
	public String getAssetTitleDTM() {
		return CommonUtil.getAssetTitle(downloadLinkPath, getAssetResource());
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
	 * Gets the new window.
	 *
	 * @return the new window
	 */
	public String getDownloadLinkNewWindow() {
		return downloadLinkNewWindow;
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
	 * Gets the asset type.
	 *
	 * @return the asset type
	 */
	public String getType() {
		return CommonUtil.getType(getAsset());
	}

	/**
	 * Gets the asset resource.
	 *
	 * @return the asset resource
	 */
	public Resource getAssetResource() {
		Resource assetResource = null;
		try {
			assetResource = this.resourceResolver
					.getResource(URLDecoder.decode(downloadLinkPath, CommonConstants.UTF_8));
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("DownloadLinkModel | Exception occured due to improper image name - ", e);
		}
		return assetResource;
	}

	/**
	 * Checks if is asset path.
	 *
	 * @return true, if is asset path
	 */
	public boolean getIsAssetLink() {
		isAssetPath = false;
		if (StringUtils.startsWith(downloadLinkPath, CommonConstants.CONTENT_DAM)) {
			isAssetPath = true;
		}
		return isAssetPath;
	}
	
	/**
	 * Gets the asset UUID.
	 *
	 * @return the asset UUID
	 */
	public String getAssetUUID() {
		String uniqueDocId = StringUtils.EMPTY;
		if ((StringUtils.startsWith(downloadLinkPath, CommonConstants.CONTENT_DAM)) && (null != getAsset())) {
			uniqueDocId = getAsset().getID();
		}
		return uniqueDocId;
	}

	public boolean isSecure() {
		return secure;
	}

	public void setSecure(boolean secure) {
		this.secure = secure;
	}


	public String getSHA() {
		String SHAValue = SHA;
		if (null == SHAValue) {
			if (null != getAsset()) {
				SHAValue = getAsset().getMetadataValue(CommonConstants.SHA);
			} else {
				SHAValue = StringUtils.EMPTY;
			}
		}
		return SHAValue;
	}

	public String getECCN() {
		String ECCNValue = ECCN;
		if (null == ECCNValue) {
			if (null != getAsset()) {
				ECCNValue = getAsset().getMetadataValue(CommonConstants.ECCN);
			} else {
				ECCNValue = StringUtils.EMPTY;
			}
		}
		return ECCNValue;
	}

	public String getTrackDownload() {
		return trackDownload;
	}

	public void setTrackDownload(String trackDownload) {
		this.trackDownload = trackDownload;
	}
}
