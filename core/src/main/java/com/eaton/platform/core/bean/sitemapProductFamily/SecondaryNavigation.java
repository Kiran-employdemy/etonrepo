package com.eaton.platform.core.bean.sitemapProductFamily;

import java.io.Serializable;
import java.util.List;

/**
 * This class is for Level 3 Sitemap Pojo.
 * @author TCS
 *
 */
public class SecondaryNavigation implements Serializable {
	
	private static final long serialVersionUID = -5701714204351754518L;

	/** The secondary nav. */
	private SitemapBean secondaryNav;
	
	/** The ternary nav list. */
	private List<TernaryNavigation> ternaryNavList;
	
	/**
	 * Gets the secondary nav.
	 *
	 * @return the secondary nav
	 */
	public SitemapBean getSecondaryNav() {
		return secondaryNav;
	}
	
	/**
	 * Sets the secondary nav.
	 *
	 * @param secondaryNav the new secondary nav
	 */
	public void setSecondaryNav(SitemapBean secondaryNav) {
		this.secondaryNav = secondaryNav;
	}
	
	/**
	 * Gets the ternary nav list.
	 *
	 * @return the ternary nav list
	 */
	public List<TernaryNavigation> getTernaryNavList() {
		return ternaryNavList;
	}
	
	/**
	 * Sets the ternary nav list.
	 *
	 * @param ternaryNavList the new ternary nav list
	 */
	public void setTernaryNavList(List<TernaryNavigation> ternaryNavList) {
		this.ternaryNavList = ternaryNavList;
	}

}
