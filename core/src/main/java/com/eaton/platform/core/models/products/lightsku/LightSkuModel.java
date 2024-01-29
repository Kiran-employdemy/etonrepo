package com.eaton.platform.core.models.products.lightsku;

import javax.annotation.PostConstruct;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaton.platform.core.models.products.skudetails.SkuBasicDetails;

/**
 * LightSkuModel component is designed to provide basic SKU details from ENDECA
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class LightSkuModel {

    private static final Logger LOG = LoggerFactory.getLogger(LightSkuModel.class);


    private SkuBasicDetails skuDetails;

    @SlingObject
    private SlingHttpServletRequest slingRequest;

    /**
     * Get SKU details from ENDECA
     */
    @PostConstruct
	public void init() {
        LOG.debug("Start of init() : LightSkuModel");
        if(null != slingRequest){
            skuDetails = slingRequest.adaptTo(SkuBasicDetails.class);
        }
        LOG.debug("END of init() : LightSkuModel");
    }

    public SkuBasicDetails getSkuDetails() {
        return skuDetails;
    } 
    
}
