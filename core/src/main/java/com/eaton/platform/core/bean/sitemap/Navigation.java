package com.eaton.platform.core.bean.sitemap;

import java.io.Serializable;
import java.util.List;

/**
 * Page navigation DTO. Contains hierarchy of the pages and their base info
 */
public class Navigation implements Serializable  {

	private static final long serialVersionUID = 1;

	private SitemapBean nav;

	private List<Navigation> innerNavList;

	public SitemapBean getNav() {
		return nav;
	}

	public void setNav(SitemapBean nav) {
		this.nav = nav;
	}

	public List<Navigation> getInnerNavList() {
		return innerNavList;
	}

	public void setInnerNavList(List<Navigation> innerNavList) {
		this.innerNavList = innerNavList;
	}
}
