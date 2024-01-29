package com.eaton.platform.core.bean.sitemapProductFamily;

import java.io.Serializable;
import java.util.List;

/**
 * This class is for Level 2 Sitemap Pojo.
 * @author TCS
 *
 */
public class PrimaryNavigation implements Serializable  {
	
	private static final long serialVersionUID = -7327042530165774723L;

	/** The primary nav. */
	private SitemapBean primaryNav;
	
	/** The secondary nav list. */
	private List<SecondaryNavigation> secondaryNavList;
	
	/**
	 * Gets the primary nav.
	 *
	 * @return the primary nav
	 */
	public SitemapBean getPrimaryNav() {
		return primaryNav;
	}
	
	/**
	 * Sets the primary nav.
	 *
	 * @param primaryNav the new primary nav
	 */
	public void setPrimaryNav(SitemapBean primaryNav) {
		this.primaryNav = primaryNav;
	}
	
	/**
	 * Gets the secondary nav list.
	 *
	 * @return the secondary nav list
	 */
	public List<SecondaryNavigation> getSecondaryNavList() {
		return secondaryNavList;
	}
	
	/**
	 * Sets the secondary nav list.
	 *
	 * @param secondaryNavList the new secondary nav list
	 */
	public void setSecondaryNavList(List<SecondaryNavigation> secondaryNavList) {
		this.secondaryNavList = secondaryNavList;
	}
	
}
