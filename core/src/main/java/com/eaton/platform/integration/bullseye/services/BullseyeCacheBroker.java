package com.eaton.platform.integration.bullseye.services;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import java.util.ArrayList;
import java.util.List;

@Component(name = "Bullseye Cache broker",immediate = true, service = BullseyeCacheBroker.class)
public class BullseyeCacheBroker {

    List<BullseyeCache> cacheServices;

    @Reference(service = BullseyeCache.class,cardinality = ReferenceCardinality.MULTIPLE,policy = ReferencePolicy.DYNAMIC)
    protected void bind(BullseyeCache cacheService){
        if(null == cacheServices){
            cacheServices = new ArrayList<>();
        }
        cacheServices.add(cacheService);
    }

    protected void unbind(BullseyeCache cacheService) {
        cacheServices.remove(cacheService);
    }

    public BullseyeCache getBullseyeCache(String name) {
        for (BullseyeCache cacheService : cacheServices) {
            if(cacheService.isCacheService(name)) {
                return cacheService;
            }
        }
        return null;
    }

}
