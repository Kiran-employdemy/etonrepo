package com.eaton.platform.core.models.submittalbuilder;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.sling.api.SlingHttpServletRequest;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.models.SiteResourceSlingModel;
import com.eaton.platform.core.services.EatonSiteConfigService;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SubmittalFilters {
    @Inject
    private String title;

    @Inject
    private String mobileDialogTitle;

    @Inject
    private boolean globalFacetSearchDisabled;
	
	 /**Facet value count from site config*/
   	private int facetValueCount;
   	
    @Inject
	protected EatonSiteConfigService eatonSiteConfigService;
    
    @Inject
    private Page currentPage;
       	
   	/** Log reference */
	private static final Logger LOG = LoggerFactory.getLogger(SubmittalFilters.class);

    @PostConstruct
    protected void init() {
		try {
				if(null != eatonSiteConfigService){	
					Optional<SiteResourceSlingModel> siteConfig = eatonSiteConfigService.getSiteConfig(currentPage);
						if (siteConfig.isPresent()) {
							 facetValueCount = siteConfig.get().getFacetValueCount();
						} else {
							LOG.error("Site config was not authored : siteResourceSlingModel is null");
						}
				}			
	    	}catch (Exception e) {
	    		LOG.error("catch block: exception", e);
	    	}
		
    }

    public String getTitle() {
        return title;
    }

    public String getMobileDialogTitle() {
        return mobileDialogTitle;
    }

    public boolean isFacetSearchDisabled() { return globalFacetSearchDisabled; }
	
	 
    /**
   	 * Gets the facet value count for siteconfig.
   	 *
   	 * @return the facetvaluecount
   	 */
   	
   	public int getFacetValueCount() {
   		return facetValueCount;
   	}
}
