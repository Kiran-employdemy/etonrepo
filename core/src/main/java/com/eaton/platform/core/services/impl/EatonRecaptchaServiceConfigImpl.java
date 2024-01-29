package com.eaton.platform.core.services.impl;

import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.services.EatonRecaptchaConfigService;
import com.eaton.platform.core.services.config.EatonRecaptchaServiceConfig;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class EatonRecaptchaServiceConfigImpl.
 */
@Designate(ocd = EatonRecaptchaServiceConfig.class)
@Component(service = EatonRecaptchaConfigService.class ,immediate = true,
        property = {
                AEMConstants.SERVICE_VENDOR_EATON,
                AEMConstants.SERVICE_DESCRIPTION + "EatonRecaptchaServiceConfigImpl",
                AEMConstants.PROCESS_LABEL + "EatonRecaptchaServiceConfigImpl"
        })
public class EatonRecaptchaServiceConfigImpl implements EatonRecaptchaConfigService {
	
	/** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EatonRecaptchaServiceConfigImpl.class);
    private  String recaptchaSiteKey;

    /**
     * Activate.
     *
     * @param recaptchaConfig the recaptchaConfig
     * @throws Exception the exception
     */
    @Activate
    @Modified
    protected final void activate(final EatonRecaptchaServiceConfig recaptchaConfig) throws Exception {
        LOGGER.debug("EatonRecaptchaServiceConfigImpl :: activate()");
        this.recaptchaSiteKey=recaptchaConfig.recaptcha_site_key();
    }

     @Override
     public String getRecaptchaSiteKey(){
        return this.recaptchaSiteKey;
     }

	/**
     * Deactivate.
     */
    @Deactivate
    protected void deactivate() {
        LOGGER.debug("EatonRecaptchaServiceConfigImpl :: deactivate()");
    }
}
