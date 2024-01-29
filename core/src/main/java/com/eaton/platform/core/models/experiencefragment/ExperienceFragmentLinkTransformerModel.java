package com.eaton.platform.core.models.experiencefragment;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.sling.models.annotations.DefaultInjectionStrategy;

/**
 * This link transformer factory is used to rewrite master links to live copy
 * links
 */
@Model(adaptables = { Resource.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ExperienceFragmentLinkTransformerModel {
    
    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ExperienceFragmentLinkTransformerModel.class);

    private static final String EXFRRAGMENT_PATH = "experience-fragments/";

    private Boolean isComponentInXS = false;

    @Inject
    @Self
    private Resource currentResource;

    @Inject
    @Via("resource")
    private Resource childResource;

    /** The resource resolver. */
    @Inject
    @Source("sling-object")
    private ResourceResolver resourceResolver;

    @PostConstruct
    protected void initMethod() {
    	LOGGER.debug("ExperienceFragmentLinkTransformerModel :: init() :: Started");
        if (childResource != null || currentResource != null) {
            PageManager pm = resourceResolver.adaptTo(PageManager.class);
            LOGGER.debug("Page Manager:{}", pm);
            if(null != pm) {
                Page xfPage = null;
                if (childResource !=null) {
                    xfPage = pm.getContainingPage(childResource);
                } else {
                    xfPage = pm.getContainingPage(currentResource);
                }
                if (xfPage != null && xfPage.getPath().contains(EXFRRAGMENT_PATH)) {
                    isComponentInXS = true;
                }
            }
        }
        LOGGER.debug("ExperienceFragmentLinkTransformerModel :: init() :: Ended");
    }

    public Boolean getIsComponentInXS() {
        return isComponentInXS;
    }

}
