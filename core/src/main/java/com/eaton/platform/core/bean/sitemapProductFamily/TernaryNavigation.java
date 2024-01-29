package com.eaton.platform.core.bean.sitemapProductFamily;

import java.io.Serializable;
import java.util.List;

/**
 * This class is for Level 4 Sitemap Pojo.
 * @author TCS
 *
 */
public class TernaryNavigation implements Serializable  {
	
	private static final long serialVersionUID = 4558469932010519810L;

	/** The ternary nav. */
	private SitemapBean ternaryNav;
	
	/** The fourth nav list. */
	private List<FourthNavigation> fourthNavList;
	
	/**
	 * Gets the ternary nav.
	 *
	 * @return the ternary nav
	 */
	public SitemapBean getTernaryNav() {
		return ternaryNav;
	}
	
	/**
	 * Sets the ternary nav.
	 *
	 * @param ternaryNav the new ternary nav
	 */
	public void setTernaryNav(SitemapBean ternaryNav) {
		this.ternaryNav = ternaryNav;
	}
	
	/**
	 * Gets the fourth nav list.
	 *
	 * @return the fourth nav list
	 */
	public List<FourthNavigation> getFourthNavList() {
		return fourthNavList;
	}
	
	/**
	 * Sets the fourth nav list.
	 *
	 * @param fourthNavList the new fourth nav list
	 */
	public void setFourthNavList(List<FourthNavigation> fourthNavList) {
		this.fourthNavList = fourthNavList;
	}

}
