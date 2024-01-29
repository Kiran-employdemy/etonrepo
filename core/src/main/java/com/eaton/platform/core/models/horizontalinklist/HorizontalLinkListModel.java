package com.eaton.platform.core.models.horizontalinklist;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.Via;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <html> Description: This Sling Model used to inject dialog values. </html>
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HorizontalLinkListModel {
	
	@Inject
	private Page currentPage;	

	@Inject @Source("sling-object")
	private SlingHttpServletRequest slingRequest;
	
	@Inject @Source("sling-object") 
	private ResourceResolver resourceResolver;
	
	/** The links. */
	@Inject @Via("resource")
	public List<HorizontalLinkListResourceModel> links;
	 
	/** The link list. */
	@Inject @Via("resource")
    private Resource horizontalLinkList;
	
	@Inject @Via("resource")
	private Resource res;
	
	/** The Constant FOOTER_HORIZONTAL_LINK_LIST_SELECTOR. */
	private static final String FOOTER_HORIZONTAL_LINK_LIST_SELECTOR = "footer-primary-link-list";
    private static final String FOOTER ="/jcr:content/root/footer";
	private static final String LINK_LIST ="/jcr:content/footerReference/footer-primary-link-list";
	private static final String REFERENCE ="reference";
	private String resourceReference;
	
	private static final Logger LOG = LoggerFactory.getLogger(HorizontalLinkListModel.class);
	
   	/**
	 * Inits the.
	 */
	@PostConstruct
	protected void init() {
		LOG.debug("HorizontalLinkListModel :: init() :: Started");
		links = new ArrayList<HorizontalLinkListResourceModel>();
		
		String selector = null;
		Resource linkListRes = null;
		String homePagePath = CommonUtil.getHomePagePath(currentPage);
		String path = null;
		
		// get selector from footer passed while including horizontal link list component
		selector = slingRequest.getRequestPathInfo().getSelectorString();
		
		/*if selector is available, component is statically included in footer of home page template,
		 in pages other than homepage, horizontal listlink component needs to be derived programmatically because horizontal linklist 
		resources are not present under page resources but inherited from home page*/
		if(null != selector && (StringUtils.equals(FOOTER_HORIZONTAL_LINK_LIST_SELECTOR, selector))) {
			path = homePagePath.concat(FOOTER);		
			Resource resource = resourceResolver.getResource(path);
			if(null != resource) {
				resourceReference= resource.getValueMap().get(REFERENCE).toString();
				linkListRes= resourceResolver.getResource(resourceReference+LINK_LIST);
			}
		} 
		else {
			linkListRes = res;
		}
		
		if (horizontalLinkList != null) {
			populateModel(links, horizontalLinkList);
		}
		LOG.debug("HorizontalLinkListModel :: init() :: Ended");
	}

	/**
	 * Populate model.
	 *
	 * @param array the array
	 * @param resource the resource
	 * @return the list
	 */
	public  List<HorizontalLinkListResourceModel> populateModel(List<HorizontalLinkListResourceModel> array, Resource resource) {
		if (resource != null) {
			Iterator<Resource> linkResources = resource.listChildren();
			while (linkResources.hasNext()) {
				HorizontalLinkListResourceModel link = linkResources.next().adaptTo(HorizontalLinkListResourceModel.class);
				if (link != null){
					array.add(link);
				}
			}
		}
		return array;
	}

	/**
	 * Gets the links.
	 *
	 * @return the links
	 */
	public List<HorizontalLinkListResourceModel> getLinks() {
		return links;
	}
	
}
