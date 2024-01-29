package com.eaton.platform.core.models.developerportal.v1;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TesimonialListModel {
	
	@Inject
	private String image;

	@Inject
	private String companylogo;
	
	@Inject
	private String quote;
	
	@Inject
	private String quoteauthor;
	
	@Inject
	private String linkurl;

	@Inject
	private Boolean openLinksInANewTab;

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getCompanylogo() {
		return companylogo;
	}

	public void setCompanylogo(String companylogo) {
		this.companylogo = companylogo;
	}

	public String getQuote() {
		return quote;
	}

	public void setQuote(String quote) {
		this.quote = quote;
	}

	public String getQuoteauthor() {
		return quoteauthor;
	}

	public void setQuoteauthor(String quoteauthor) {
		this.quoteauthor = quoteauthor;
	}

	public String getLinkurl() {
		return linkurl;
	}

	public boolean getOpenLinksInANewTab() {return openLinksInANewTab;}

	public void setLinkurl(String linkurl) {
		this.linkurl = linkurl;
	}
}