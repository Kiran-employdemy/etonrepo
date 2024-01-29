package com.eaton.platform.integration.auth.filters.deciders;

import org.apache.sling.api.resource.Resource;

public class DeveloperPortalDecider extends AbstractPageLoginUrlDecider{
    public static final String DEVELOPER_PORTAL_SLING_RES_TYPE = "developer_portal/components/structure/developer-portal-page";
    private String redirectTo;
    private Resource contentResource;
    public DeveloperPortalDecider(Resource contentResource, String redirectTo){
        super.orderRanking = 1;
        this.contentResource = contentResource;
        this.redirectTo = redirectTo;
    }

    @Override
    public boolean conditionMatched() {
        return contentResource.isResourceType(DEVELOPER_PORTAL_SLING_RES_TYPE);
    }

    @Override
    public String redirectTo() {
        return this.redirectTo;
    }
}
