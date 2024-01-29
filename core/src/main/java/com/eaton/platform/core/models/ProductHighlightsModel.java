package com.eaton.platform.core.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;


/**
 * <html> Description: This class is used to inject the dialog properties.</html> .
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ProductHighlightsModel {
	
	/** The highlights list. */
	@Inject
    private Resource highlightsList;
	
	/** The toggle inner grid. */
	@Inject
    private String toggleInnerGrid;

	/** The links. */
	private List<ProductHighlightsListModel> links;
	
	/**
	 * Gets the links.
	 *
	 * @return the links
	 */
	public List<ProductHighlightsListModel> getLinks() {
		return links;
	}

	/**
	 * Inits the.
	 */
	@PostConstruct
	protected void init() {
		links = new ArrayList<>();
		if (highlightsList != null) {
			populateModel(links, highlightsList);
		}
	}
	
	/**
	 * Populate model.
	 *
	 * @param array the array
	 * @param resource the resource
	 * @return the list
	 */
	public  List<ProductHighlightsListModel> populateModel(List<ProductHighlightsListModel> array, Resource resource) {
		if (null != resource) {
			Iterator<Resource> linkResources = resource.listChildren();
			if(null != linkResources){
			while (linkResources.hasNext()) {
				ProductHighlightsListModel link = linkResources.next().adaptTo(ProductHighlightsListModel.class);
				if (link != null)
					array.add(link);
			}
			}
		}
		return array;
	}

	/**
	 * Gets the toggle inner grid.
	 *
	 * @return the toggle inner grid
	 */
	public String getToggleInnerGrid() {
		return toggleInnerGrid;
	}
}
