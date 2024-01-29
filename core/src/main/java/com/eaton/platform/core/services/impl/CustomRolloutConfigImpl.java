package com.eaton.platform.core.services.impl;

import com.eaton.platform.core.services.CustomRolloutConfigService;
import com.eaton.platform.core.services.config.PagePropUpdateOnRolloutConfig;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.HashMap;
/**
 * <html> Description: This class is used to get osgi configuration for custom rollout configs. </html>
 * @author ICF
 * @version 1.0
 * @since 2021
 *
 */
@Component(service = CustomRolloutConfigService.class, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = PagePropUpdateOnRolloutConfig.class)
public class CustomRolloutConfigImpl implements CustomRolloutConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomRolloutConfigImpl.class);
    private Map<String,String> rolloutPagePropertyMap = new HashMap<>();

    /**
     * Activate.
     *
     * @param config the props
     */
    @Activate
    @Modified
    public void activate(final PagePropUpdateOnRolloutConfig config) {
        LOGGER.debug("CustomRolloutConfigImpl :: activate()");
        setRolloutPagePropertiesMap(config.ROLLOUT_PAGE_PROPERTIES_LIST());
    }

    @Override
    public Map<String, String> getRolloutPageProperties() {
        return rolloutPagePropertyMap;
    }

    private void setRolloutPagePropertiesMap(String[] rolloutPagePropList){
        LOGGER.debug("CustomRolloutConfigImpl :: setPagePropertiesToRollout()");
        for(String pageProp : rolloutPagePropList){
            String[] pagePropArr = pageProp.split("\\|");
            rolloutPagePropertyMap.put(pagePropArr[0],pagePropArr[1]);
        }
    }
}
