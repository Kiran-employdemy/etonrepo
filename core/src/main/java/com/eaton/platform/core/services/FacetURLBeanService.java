package com.eaton.platform.core.services;

import com.eaton.platform.core.bean.FacetURLBeanServiceResponse;
import org.apache.sling.api.resource.Resource;

public interface FacetURLBeanService {
    FacetURLBeanServiceResponse getFacetURLBeanResponse(String[] selectors, int configPageSize, String pageType, String contentResourcePath) ;

}
