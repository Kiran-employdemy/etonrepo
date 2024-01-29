package com.eaton.platform.integration.auth.services.impl;


import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.integration.auth.services.EndecaSecretKeyConfiguration;
import com.eaton.platform.integration.auth.services.config.EndecaSecretKeyConfig;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
        service = EndecaSecretKeyConfiguration.class,
        immediate = true,
        property = {
                AEMConstants.SERVICE_VENDOR_EATON,
                AEMConstants.SERVICE_DESCRIPTION + "Endeca - Secret Key Configuration Service",
                AEMConstants.PROCESS_LABEL + "EndecaSecretKeyConfig"
        })
@Designate(ocd = EndecaSecretKeyConfig.class)
public class EndecaSecretKeyConfigurationImpl implements EndecaSecretKeyConfiguration {

    private String aemSecretKey;

    private static final Logger LOG = LoggerFactory.getLogger(EndecaSecretKeyConfigurationImpl.class);


    @Reference
    private SlingSettingsService slingSettingsService;


    @Override
    public String getAemSecretKey() {
        return aemSecretKey;
    }

    @Activate
    @Modified
    protected  final void activate(final EndecaSecretKeyConfig config) {
        if (config != null) {
             aemSecretKey = config.secret_key();
        }
    }

}
