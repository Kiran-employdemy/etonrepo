package com.eaton.platform.core.models;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.injectors.annotations.CurrentPage;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import javax.inject.Inject;
import javax.inject.Named;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class},defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public abstract class EatonBaseSlingModel {

    @CurrentPage
    public Page currentPage;

    @Inject
    public SlingHttpServletRequest slingRequest;

    @Inject
    public Resource resource;

    @SlingObject
    public ResourceResolver resourceResolver;

    @CurrentPage @Named(CommonConstants.PAGE_TYPE)
    public String pageType;


    public Page getCurrentPage() {
        return currentPage;
    }

    public Resource getResource() {
        return resource;
    }

    public ResourceResolver getResourceResolver() {
        return resourceResolver;
    }

    public SlingHttpServletRequest getSlingRequest() {
        return slingRequest;
    }

    public String getPageType() {
        return pageType;
    }
}
