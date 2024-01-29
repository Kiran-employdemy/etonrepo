package com.eaton.platform.core.models;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.EatonConfigService;

/**
 * <html> Description: This class is used to inject the dialog properties.</html> .
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ProofPointsListModel {
	
	
	/** The proof point icon. */
	@Inject
	private String proofPointIcon;
	
	/** The proof point title. */
	@Inject
	private String proofPointTitle;
	
	/** The proof point desc. */
	@Inject
	private String proofPointDesc;

	/** The config service. */
	@Inject @Source("osgi-services") 
	EatonConfigService configService;

	@Inject
	private String mobileTransformedUrl;
	
	/** The desktop transformed url. */
	@Inject
	private String desktopTransformedUrl;
	
	/**
	 * Gets the proof point title.
	 *
	 * @return the proof point title
	 */
	public String getProofPointTitle() {
		return proofPointTitle;
	}



	/**
	 * Sets the proof point title.
	 *
	 * @param proofPointTitle the new proof point title
	 */
	public void setProofPointTitle(String proofPointTitle) {
		this.proofPointTitle = proofPointTitle;
	}



	/**
	 * Gets the proof point desc.
	 *
	 * @return the proof point desc
	 */
	public String getProofPointDesc() {
		return proofPointDesc;
	}



	/**
	 * Sets the proof point desc.
	 *
	 * @param proofPointDesc the new proof point desc
	 */
	public void setProofPointDesc(String proofPointDesc) {
		this.proofPointDesc = proofPointDesc;
	}



	/**
	 * Gets the proof point icon.
	 *
	 * @return the proof point icon
	 */
	public String getProofPointIcon() {
		return proofPointIcon;
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
	 * Inits
	 */
	@PostConstruct
	protected void init() {
	
		List<String> currencySymbols = configService.getConfigServiceBean().getProofPointSymbols();
		for (String currencySymbol : currencySymbols) {
			 String currencySymbolHTML = "";
			 if(getProofPointTitle() != null && getProofPointTitle().contains(currencySymbol)){
				 currencySymbolHTML = "<div class='icon-list-proof-points__symbol'>" + currencySymbol + "</div>";
				 setProofPointTitle(getProofPointTitle().replaceAll(currencySymbol, currencySymbolHTML));
			 }
		}
	
	}
	
	/**
     * Sets the mobile transformed url.
     *
     * @param mobileTrans the new mobile transformed url
     */
    public void setMobileTransformedUrl(String mobileTrans) {
    	 if(null!=getProofPointIcon()){
	    	  if(null != mobileTrans && !StringUtils.contains(mobileTrans, CommonConstants.DEFAULT_MOBILE)){
	            mobileTransformedUrl = getProofPointIcon().trim().concat(CommonConstants.TRANSTORM).trim().concat(mobileTrans.trim()).concat(CommonConstants.IMAGE_JPG);
	        } else{
	        	mobileTransformedUrl = getProofPointIcon().trim();
	        }
    	 }
    }
    /**
     * Sets the desktop transformed url.
     *
     * @param desktopTrans the new desktop transformed url
     */
    public void setDesktopTransformedUrl(String desktopTrans) {
    	 if(null!=getProofPointIcon()){
	    	  if(null != desktopTrans && !StringUtils.contains(desktopTrans, CommonConstants.DEFALUT_DESKTOP)){
	        	desktopTransformedUrl = getProofPointIcon().trim().concat(CommonConstants.TRANSTORM).trim().concat(desktopTrans.trim()).concat(CommonConstants.IMAGE_JPG);
	        } else{
	        	desktopTransformedUrl = getProofPointIcon().trim();
	        }
    	 }
    }
	/**
	 * @return the mobileTrans
	 */
	

}
