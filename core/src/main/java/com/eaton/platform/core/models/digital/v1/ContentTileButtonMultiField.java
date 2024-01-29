package com.eaton.platform.core.models.digital.v1;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;


@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)

public class ContentTileButtonMultiField extends ContentTileMultifieldModel{

    public void setCtaLinkTitle(String ctaLinkTitle) {
        super.ctaLinkTitle = ctaLinkTitle;
    }

    public void setCtaLinkPath(String ctaLinkPath) {
        super.ctaLinkPath = ctaLinkPath;
    }

}
