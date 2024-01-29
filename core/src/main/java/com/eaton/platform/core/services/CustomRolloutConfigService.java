package com.eaton.platform.core.services;

import java.util.List;
import java.util.Map;

/**
 * The Interface EatonConfigService.
 */
public interface CustomRolloutConfigService {
    /**
     * @return the Map of page properties to be rolled out
     */
	Map<String,String> getRolloutPageProperties();

}
