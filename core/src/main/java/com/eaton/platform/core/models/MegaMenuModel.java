package com.eaton.platform.core.models;

import javax.inject.Inject;
import javax.annotation.PostConstruct;

import com.eaton.platform.core.constants.CommonConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.Via;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaton.platform.core.util.CommonUtil;

/**
 * <html> Description: This bean class used in MegaMenuModel class to store content </html> .
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class MegaMenuModel {
	
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(MegaMenuModel.class);
	
	/** The mega menu title path. */
	@Inject  @Via("resource")
	private String megaMenuTitlePath;
	
	/** The merge column. */
	@Inject  @Via("resource")
	private String mergeColumn;

	/** The merge column. */
	@Inject  @Via("resource")
	private String secureMenu;

    /** The resource resolver. */
    @Inject @Source("sling-object")
    private ResourceResolver resourceResolver;
    
    @Inject @Source("sling-object")
    private SlingHttpServletRequest slingRequest;
    
    @Inject @Via("resource")  
    private Resource res;	 
	
	private static final String MEGA_MENU_COL_4 ="mega-menu__col--4";
	private static final String MEGA_MENU_COL_6 ="mega-menu__col--6";
	
	private boolean hideSecondCol;
	private boolean hideThirdCol;
	private boolean hideFourthCol;
	
	private String secondColCSS;
	private String thridColCSS;
	private String fourthColCSS;
	
    /**
	 * Inits the.
	 */
	@PostConstruct
	protected void init() {
		LOG.debug("MegaMenuModel :: Init() :: Started");
		int listColumnCount = 1;
		if(slingRequest.getAttribute("listColumnCount") != null) {
			listColumnCount = (int) slingRequest.getAttribute("listColumnCount");
		}
		String mergeCol = CommonUtil.getStringProperty(res.getValueMap(), "mergeColumn");	
		if(listColumnCount == 2 && StringUtils.isBlank(mergeCol)) {
			hideFourthCol = true;
			secondColCSS = MEGA_MENU_COL_4;
			thridColCSS = MEGA_MENU_COL_4;
		} else if(listColumnCount == 3 && (StringUtils.isBlank(mergeCol) || StringUtils.isNotBlank(mergeCol))) {
			hideFourthCol = true;
			hideThirdCol = true;
			secondColCSS = MEGA_MENU_COL_4;
		} else if(listColumnCount == 4 && (StringUtils.isBlank(mergeCol) || StringUtils.isNotBlank(mergeCol))) {
			hideFourthCol = true;
			hideThirdCol = true;
			hideSecondCol = true;			
		} else if(listColumnCount == 1 && StringUtils.isNotBlank(mergeCol)) {
			hideFourthCol = true;
			secondColCSS = MEGA_MENU_COL_4;
			thridColCSS = MEGA_MENU_COL_6;
		} else if(listColumnCount == 2 && StringUtils.isNotBlank(mergeCol)) {
			hideFourthCol = true;
			hideThirdCol = true;
			secondColCSS = MEGA_MENU_COL_6;
		} else{
			secondColCSS = MEGA_MENU_COL_4;
			thridColCSS = MEGA_MENU_COL_4;
			fourthColCSS = MEGA_MENU_COL_4;
		}
		LOG.debug("MegaMenuModel :: Init() :: Exit");
	}


	/**
	 * Gets the mega menu title path.
	 *
	 * @return the mega menu title path
	 */
	public String getMegaMenuTitlePath() {
		return CommonUtil.dotHtmlLink(this.megaMenuTitlePath,this.resourceResolver);
	}
	
	/**
	 * Gets the mega menu title text.
	 *
	 * @return the mega menu title text
	 */
	public String getMegaMenuTitleText() {
		return CommonUtil.getLinkTitle(null, this.megaMenuTitlePath, this.resourceResolver);
	}

	/**
	 * Gets the merge column.
	 *
	 * @return the merge column
	 */
	public String getMergeColumn() {
		return mergeColumn;
	}

	/**
	 * Gets the secureMegaMenu.
	 *
	 * @return the secure Mega Menu
	 */
	public String getSecureMenu() {
		return secureMenu;
	}

	/**
	 * @return the menuName
	 */
	public String getMenuName() {
		String[] parts = megaMenuTitlePath.split("/");
		return parts[parts.length-1];
	}

	public boolean isHideSecondCol() {
		return hideSecondCol;
	}
	public void setHideSecondCol(boolean hideSecondCol) {
		this.hideSecondCol = hideSecondCol;
	}
	public boolean isHideThirdCol() {
		return hideThirdCol;
	}
	public void setHideThirdCol(boolean hideThirdCol) {
		this.hideThirdCol = hideThirdCol;
	}
	public boolean isHideFourthCol() {
		return hideFourthCol;
	}
	public void setHideFourthCol(boolean hideFourthCol) {
		this.hideFourthCol = hideFourthCol;
	}
	public String getSecondColCSS() {
		return secondColCSS;
	}
	public void setSecondColCSS(String secondColCSS) {
		this.secondColCSS = secondColCSS;
	}
	public String getThridColCSS() {
		return thridColCSS;
	}
	public void setThridColCSS(String thridColCSS) {
		this.thridColCSS = thridColCSS;
	}
	public String getFourthColCSS() {
		return fourthColCSS;
	}
	public void setFourthColCSS(String fourthColCSS) {
		this.fourthColCSS = fourthColCSS;
	}
}
