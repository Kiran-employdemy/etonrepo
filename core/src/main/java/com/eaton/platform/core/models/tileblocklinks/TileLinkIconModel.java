package com.eaton.platform.core.models.tileblocklinks;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;

import javax.inject.Inject;


	/**
	 * <html> Description: This is a Sling Model for TileLinkBlock</html> .
	 *
	 * @author TCS
	 * @version 1.0
	 * @since 2017
	 */
	@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
	public class TileLinkIconModel {

		/** The tile link icon. */
		@Inject
		private String tileLinkIcon;
		
		/** The tile block link title */
		@Inject
		private String tileBlockLinkTitle;
		
		/** The tile link path */
		@Inject
		private String tileBlockLinkPath;
		
		/** The tile link open new window. */
		@Inject
		private String tileBlockLinkOpenNewWindow;		
		
	    /** The resource resolver. */
	    @Inject @Source("sling-object")
	    private ResourceResolver resourceResolver;

	    /** The mobile transformed url. */
		@Inject
		private String mobileTransformedUrl;
		
		/** The desktop transformed url. */
		@Inject
		private String desktopTransformedUrl;

		/**
		 * Get the tileLinkIcon
		 * @return the tileLinkIcon
		 */
		public String getTileLinkIcon() {
			String tileIcon = StringUtils.EMPTY;
	      if (tileLinkIcon != null) 		    
	    	  tileIcon = tileLinkIcon;	
		 return tileIcon;
		}

		/**
		 * Get the tileBlockLinkTitle
		 * @return the tileBlockLinkTitle
		 */
		public String getTileBlockLinkTitle() {
			return tileBlockLinkTitle.trim();
		}

		/**
		 * Gets tileBlockLinkPath
		 * @return the tileBlockLinkPath
		 */
		public String getTileBlockLinkPath() {
			return CommonUtil.dotHtmlLink(this.tileBlockLinkPath);
			
		}

		/**
		 * Get the tileBlockLinkOpenNewWindow
		 * @return the tileBlockLinkOpenNewWindow
		 */
		public String getTileBlockLinkOpenNewWindow() {
			String newWindow = StringUtils.EMPTY;
			if(StringUtils.equals(CommonConstants.TRUE, this.tileBlockLinkOpenNewWindow) || StringUtils.equals(CommonConstants.TRUE, getIsExternal())) {
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
			if((null != this.tileBlockLinkPath) && (StringUtils.startsWith(this.tileBlockLinkPath, CommonConstants.HTTP) || StringUtils.startsWith(this.tileBlockLinkPath, CommonConstants.HTTPS) || StringUtils.startsWith(this.tileBlockLinkPath, CommonConstants.WWW))) {
				isExternal = CommonConstants.TRUE;
			}
			return isExternal;
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
	    	 if(null!=getTileLinkIcon()){
		    	  if(null != mobileTrans && !StringUtils.contains(mobileTrans, CommonConstants.DEFAULT_MOBILE)){
		             mobileTransformedUrl = getTileLinkIcon().trim().concat(CommonConstants.TRANSTORM).trim().concat(mobileTrans.trim()).concat(CommonConstants.IMAGE_JPG);
		         } else{
		         	mobileTransformedUrl = getTileLinkIcon().trim();
		         }
	    	 }
	     }
	     /**
	      * Sets the desktop transformed url.
	      *
	      * @param desktopTrans the new desktop transformed url
	      */
	     public void setDesktopTransformedUrl(String desktopTrans) {
	    	 if(null!=getTileLinkIcon()){
		    	 if(null != desktopTrans && !StringUtils.contains(desktopTrans, CommonConstants.DEFALUT_DESKTOP)){
		         	desktopTransformedUrl = getTileLinkIcon().trim().concat(CommonConstants.TRANSTORM).trim().concat(desktopTrans.trim()).concat(CommonConstants.IMAGE_JPG);
		         } else {
		         	desktopTransformedUrl = getTileLinkIcon().trim();
		         }
	    	 }
	     }
}
