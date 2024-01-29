package com.eaton.platform.core.services.vanity.impl;

import com.eaton.platform.core.bean.EatonVanityConfigBean;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.vanity.EatonVanityConfigService;
import com.eaton.platform.core.services.config.EatonVanityConfig;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <html> Description: This class is used to get osgi configuration for custom vanity flow. </html>
 * @author ICF
 * @version 1.0
 * @since 2020
 *
 */
@Component(service = EatonVanityConfigService.class)
@Designate(ocd = EatonVanityConfig.class)
public class EatonVanityConfigImpl implements EatonVanityConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EatonVanityConfigImpl.class);
    private EatonVanityConfig config;
    private EatonVanityConfigBean eatonVanityConfigBean;

    /**
     * Activate.
     *
     * @param config the props
     */
    @Activate
    public void activate(final EatonVanityConfig config) {
        LOGGER.debug("EatonVanityConfigImpl :: activate()");
        this.config = config;
        this.eatonVanityConfigBean = new EatonVanityConfigBean();
        initializeConfigurations();

    }


    @Override
    public EatonVanityConfigBean getVanityConfig() {
        LOGGER.debug("EatonVanityConfigImpl :: getVanityConfig() :: Started");
        return this.eatonVanityConfigBean;
    }

    private void initializeConfigurations() {
        LOGGER.debug("EatonVanityConfigImpl :: getVanityConfig() :: Started");
        this.eatonVanityConfigBean.setEnableCustomVanityFlow(config.enable_vanity_flow());
        this.eatonVanityConfigBean.setIntermediatePageName(config.intermediate_page_name());
        this.eatonVanityConfigBean.setVanityDataStoreParent(config.vanity_data_store_location_path());
        String vanityJsonfilePath = new StringBuilder()
                .append(this.getVanityConfig().getVanityDataStoreParent())
                .append(CommonConstants.SLASH_STRING).toString();
        this.eatonVanityConfigBean.setEatonVanityDataStore(vanityJsonfilePath + config.eaton_com_data_store_filename());
        this.eatonVanityConfigBean.setEatonCumminsVanityDataStore(vanityJsonfilePath + config.eatoncummins_data_store_filename());
        this.eatonVanityConfigBean.setGreenSwitchingVanityDataStore(vanityJsonfilePath + config.greenswitching_data_store_filename());
        this.eatonVanityConfigBean.setPhoenixtecPowerVanityDataStore(vanityJsonfilePath + config.phoenixtecpower_data_store_filename());
        this.eatonVanityConfigBean.setTopLevelDomainConfig(config.top_level_domain_config());
        this.eatonVanityConfigBean.setLookupSkipPath(config.vanity_lookup_skip_paths());
    }

    @Override
    public String getVanityJsonfilePath(String domainName) {
        switch (domainName) {
            case CommonConstants.EATON_DOMAIN:
                return this.eatonVanityConfigBean.getEatonVanityDataStore();
            case CommonConstants.EATON_CUMMINS_DOMAIN:
                return this.eatonVanityConfigBean.getEatonCumminsVanityDataStore();
            case CommonConstants.PHOENIX_TEC_POWER_DOMAIN:
                return this.eatonVanityConfigBean.getPhoenixtecPowerVanityDataStore();
            case CommonConstants.GREEN_SWITCHING_DOMAIN:
                return this.eatonVanityConfigBean.getGreenSwitchingVanityDataStore();
            default:
                return StringUtils.EMPTY;
        }
    }
}
