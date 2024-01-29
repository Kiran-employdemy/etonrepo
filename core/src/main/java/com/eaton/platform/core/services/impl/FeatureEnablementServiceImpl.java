package com.eaton.platform.core.services.impl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;

import com.eaton.platform.core.services.FeatureEnablementService;
import com.eaton.platform.core.services.config.FeatureEnablementConfiguration;

@Component(service = FeatureEnablementService.class,immediate = true)
@Designate(ocd=FeatureEnablementConfiguration.class)
public class FeatureEnablementServiceImpl implements FeatureEnablementService {
    private boolean isFreeSampleButtonEnabled;

    @Activate
    @Modified
    protected final void activate(final FeatureEnablementConfiguration config) {
        isFreeSampleButtonEnabled = config.isFreeSampleButtonEnabled();
    }
    @Override
    public boolean isFreeSampleButtonEnabled() {
        return isFreeSampleButtonEnabled;
    }
    
}
