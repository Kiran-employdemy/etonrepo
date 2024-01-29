package com.eaton.platform.core.services.vanity;

import com.eaton.platform.core.bean.EatonVanityConfigBean;

public interface EatonVanityConfigService {
    EatonVanityConfigBean getVanityConfig();
    String getVanityJsonfilePath(String domainName);
}
