package com.eaton.platform.core.models;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;

import com.eaton.platform.core.services.EatonConfigService;

/**
 * <html> Description: This class is used to inject the dialog properties.</html> .
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ProductHighlightsListModel {
	
	
	/** The highlightsTitle. */
	@Inject
	private String highlightsTitle;
	
	/** The highlightsDesc. */
	@Inject
	private String highlightsDesc;

	/** The config service. */
	@Inject @Source("osgi-services") 
	EatonConfigService configService;

	/**
	 * Gets the highlights title.
	 *
	 * @return the highlights title
	 */
	public String getHighlightsTitle() {
		return highlightsTitle;
	}

	/**
	 * Gets the highlights desc.
	 *
	 * @return the highlights desc
	 */
	public String getHighlightsDesc() {
		return highlightsDesc;
	}

	/**
	 * Sets the highlights title.
	 *
	 * @param highlightsTitle the new highlights title
	 */
	public void setHighlightsTitle(String highlightsTitle) {
		this.highlightsTitle = highlightsTitle;
	}

	/**
	 * Sets the highlights desc.
	 *
	 * @param highlightsDesc the new highlights desc
	 */
	public void setHighlightsDesc(String highlightsDesc) {
		this.highlightsDesc = highlightsDesc;
	}
	
	/**
	 * Inits
	 */
	@PostConstruct
	protected void init() {
	
		List<String> currencySymbols = configService.getConfigServiceBean().getProofPointSymbols();
		for (String currencySymbol : currencySymbols) {
			 String currencySymbolHTML = "";
			 /*if(getHighlightsTitle() != null && getHighlightsTitle().contains(currencySymbol)){
				 currencySymbolHTML = "<div class='icon-list-proof-points__symbol'>" + currencySymbol + "</div>";
				 setHighlightsTitle(getHighlightsTitle().replaceAll(currencySymbol, currencySymbolHTML));
			 }*/
			 if(getHighlightsDesc() != null && getHighlightsDesc().contains(currencySymbol)){
				 currencySymbolHTML = "<span class='icon-list-no-image-highlights__symbol'>"  + currencySymbol + "</span>";
				 setHighlightsDesc(getHighlightsDesc().replaceAll(currencySymbol, currencySymbolHTML));
			 }
			
		}
	
	}

}
