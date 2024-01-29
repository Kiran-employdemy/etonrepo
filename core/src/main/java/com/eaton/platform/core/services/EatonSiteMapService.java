package com.eaton.platform.core.services;

import com.eaton.platform.core.models.eatonsiteconfig.EatonSiteConfigModel;
import java.util.Optional;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;

import javax.xml.stream.XMLStreamWriter;

/**
 * This service is used for Eaton Site map generation
 */
public interface EatonSiteMapService {


    void getSubCategorySiteMap(ResourceResolver resourceResolver, XMLStreamWriter stream);

    void getProductSkuSiteMap(ResourceResolver resourceResolver, XMLStreamWriter stream,
                              Optional<EatonSiteConfigModel> eatonSiteConfigModel, String resourcePath, SlingHttpServletRequest request);

    void getProductFamilySiteMap(ResourceResolver resourceResolver, XMLStreamWriter xmlStream, String resourcePath,
                                 SlingHttpServletRequest request);
}
