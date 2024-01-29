package com.eaton.platform.core.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.sling.api.resource.ResourceResolver;

import org.apache.sling.models.annotations.Source;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.day.cq.wcm.api.Page;

/**
 * <html> Description: This class is used to inject the dialog properties. </html> .
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class LinkListWithIconModel {
	
	/** The link list. */
	@Inject
	@Via("resource")
	private Resource linkList;

	@Inject
	private Page page;

	@Inject
	@Source("sling-object")
	private ResourceResolver resourceResolver;
	/** The links. */
	@Inject @Via("resource")
	private List<LinkListWithIconMultifieldModel> links;

	private static final Logger LOG = LoggerFactory.getLogger(LinkListWithIconModel.class); 
	/**
	 * Inits the.
	 */
	@PostConstruct
	protected void init() {
		LOG.debug("LinkListWithIconModel :: init() :: Started");
		LOG.debug("Current Page path {}", page.getPath());
		links = new ArrayList<>();
		links = populateModel(links, linkList);
		LOG.debug("LinkListWithIconModel :: int() :: Exit");
	}
	/**
	 * Populate model.
	 *
	 * @param relevantLinksMultiField the relevant links multi field
	 * @param resource the resource
	 * @return the list
	 */
	public List<LinkListWithIconMultifieldModel> populateModel(List<LinkListWithIconMultifieldModel> iconLinksMultiField, Resource resource) {
		if (null != resource) {
			Iterator<Resource> linkResources = resource.listChildren();
			while (linkResources.hasNext()) {
				LinkListWithIconMultifieldModel iconListLinks = linkResources.next().adaptTo(LinkListWithIconMultifieldModel.class);
				iconListLinks.setCurrentPage(page);
				iconLinksMultiField.add(iconListLinks);
				
			}
		}
		return iconLinksMultiField;
	}
	/**
	 * Gets the links.
	 *
	 * @return the links
	 */
	public List<LinkListWithIconMultifieldModel> getLinks() {
		return links;

	}

}
