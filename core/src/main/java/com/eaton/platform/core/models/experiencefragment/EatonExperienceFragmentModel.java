package com.eaton.platform.core.models.experiencefragment;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.sling.models.annotations.DefaultInjectionStrategy;

/**
 * This link transformer factory is used to rewrite master links to live copy
 * links
 */
@Model(adaptables = { Resource.class,
        SlingHttpServletRequest.class,
        SlingHttpServletResponse.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)

 public class EatonExperienceFragmentModel {

    /** The Constant LOGGER. */
    private static final Logger LOG = LoggerFactory.getLogger(EatonExperienceFragmentModel.class);

    private static final int  ABSOLUTE_PARENT=3;

    private static final int  LOCALE_PATH=4;

    /** The page. */
    @Inject
    public Page page;

    /** The request. */
    @Inject
    public SlingHttpServletRequest request;

    /** The fragmentVariationPath. */
    @ValueMapValue
    public String fragmentVariationPath;

    /**
     * Admin Service.
     */
    @Inject
    public AdminService adminService;

    private String localizedFragmentVariationPath;

    @PostConstruct
    void init() {
        LOG.debug("EatonExperienceFragmentModel :: init() :: Started");
        Page localePage = page.getAbsoluteParent(ABSOLUTE_PARENT);
        String locale = "";
        locale = localePage.getName();
        String xfVariationPath = fragmentVariationPath;
        if (xfVariationPath != null) {
            String xfLocale = xfVariationPath.split(CommonConstants.SLASH_STRING)[LOCALE_PATH];
            ResourceResolver resourceResolver = adminService.getReadService();
            if (resourceResolver != null) {
                Resource localizedFragmentResource = resourceResolver
                        .getResource(xfVariationPath.replace(xfLocale, locale));
                if (localizedFragmentResource != null) {
                    localizedFragmentVariationPath = localizedFragmentResource.getPath();
                    fragmentVariationPath = localizedFragmentVariationPath;
                }
            }

            LOG.debug("EatonExperienceFragmentModel :: init() :: Ended");
        }
        
    }

    public String getFragmentVariationPath() {
    	if (localizedFragmentVariationPath != null) {
    		return localizedFragmentVariationPath;
    	} else {
    		return fragmentVariationPath;
    	}
       
    }

}
