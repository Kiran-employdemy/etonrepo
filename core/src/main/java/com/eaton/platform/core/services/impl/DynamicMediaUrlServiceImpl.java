package com.eaton.platform.core.services.impl;

import com.eaton.platform.core.services.DynamicMediaUrlService;
import com.eaton.platform.core.services.config.DynamicMediaUrlServiceConfig;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class DynamicMediaUrlServiceImpl.
 */
@Component(service = DynamicMediaUrlService.class, immediate = true)
public class DynamicMediaUrlServiceImpl implements DynamicMediaUrlService {

    /** The Constant LOGGER. */
    private static final Logger LOG = LoggerFactory.getLogger(DynamicMediaUrlServiceImpl.class);

    /** The Dynamic Media URL Base. */
    private String dmUrlBase;

    /**
     * Activate.
     *
     * @param dynamicMediaUrlServiceConfig The dynamicMediaUrlServiceConfig.
     * @throws Exception The exception.
     */
    @Activate
    @Modified
    protected final void activate(final DynamicMediaUrlServiceConfig dynamicMediaUrlServiceConfig) {
        LOG.debug("DynamicMediaUrlServiceImpl :: activate()");
        this.dmUrlBase = dynamicMediaUrlServiceConfig.dm_url_base();
    }

    /**
     * Retrieve the dynamic media url base.
     *
     * @return The dynamic media url base.
     */
    @Override
    public String getDynamicMediaUrlBase() {
        return this.dmUrlBase;
    }

    /**
     * Deactivate.
     */
    @Deactivate
    protected void deactivate() {
        LOG.debug("DynamicMediaUrlServiceImpl :: deactivate()");
    }
}
