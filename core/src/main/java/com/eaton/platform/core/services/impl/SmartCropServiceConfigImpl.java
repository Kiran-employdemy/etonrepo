package com.eaton.platform.core.services.impl;

import com.eaton.platform.core.services.SmartCropConfigService;
import com.eaton.platform.core.services.config.SmartCropServiceConfig;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class SmartCropServiceConfigImpl.
 */
@Component(service = SmartCropConfigService.class, immediate = true)
@Designate(ocd = SmartCropServiceConfig.class, factory = true)
public class SmartCropServiceConfigImpl implements SmartCropConfigService {

    /** The Constant LOGGER. */
    private static final Logger LOG = LoggerFactory.getLogger(SmartCropServiceConfigImpl.class);

    /** The Smart Crop Title. */
    private String smartCropTitle;

    /** The Smart Crop Url Suffix. */
    private String smartCropUrlSuffix;

    /**
     * Activate.
     *
     * @param smartCropServiceConfig The smartCropServiceConfig.
     * @throws Exception The exception.
     */
    @Activate
    @Modified
    protected final void activate(final SmartCropServiceConfig smartCropServiceConfig) {
        LOG.debug("SmartCropServiceConfigImpl :: activate()");
        this.smartCropTitle = smartCropServiceConfig.smart_crop_title();
        this.smartCropUrlSuffix = smartCropServiceConfig.smart_crop_url_suffix();
    }

    /**
     * Retrieve the smart crop title.
     *
     * @return The smart crop title.
     */
    @Override
    public String getSmartCropTitle() {
        return this.smartCropTitle;
    }

    /**
     * Retrieve the smart crop url suffix.
     *
     * @return The smart crop url suffix.
     */
    @Override
    public String getSmartCropUrlSuffix() {
        return this.smartCropUrlSuffix;
    }

    /**
     * Deactivate.
     */
    @Deactivate
    protected void deactivate() {
        LOG.debug("SmartCropServiceConfigImpl :: deactivate()");
    }
}
