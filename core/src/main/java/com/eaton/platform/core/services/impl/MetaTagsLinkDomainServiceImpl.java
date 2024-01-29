package com.eaton.platform.core.services.impl;

import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.MetaTagsLinkDomainService;
import com.eaton.platform.core.services.config.MetaTagsLinkDomainServiceConfig;
import org.apache.commons.lang.StringUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Designate(ocd = MetaTagsLinkDomainServiceConfig.class)
@Component(service = MetaTagsLinkDomainService.class, immediate = true,
        property = {
                AEMConstants.SERVICE_VENDOR_EATON,
                AEMConstants.SERVICE_DESCRIPTION + "MetaTagsLinkDomainServiceImpl",
                AEMConstants.PROCESS_LABEL + "MetaTagsLinkDomainServiceImpl"
        })
public class MetaTagsLinkDomainServiceImpl implements MetaTagsLinkDomainService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetaTagsLinkDomainServiceImpl.class);
    
    private String[] excludedCountryDomainKeyList;
    private String fallbackDomainKey;


    @Activate
    @Modified
    protected void activate(final MetaTagsLinkDomainServiceConfig config) {
        LOGGER.debug("MetaTagsLinkDomainServiceImpl :: activate :: Start");
        
        this.excludedCountryDomainKeyList = config.excludedCountryDomainKeyList();
        this.fallbackDomainKey = config.fallbackDomainKey();
        
        LOGGER.debug("MetaTagsLinkDomainServiceImpl :: activate :: End");
    }
    
    @Override
    public boolean isCountryExcluded(String countryCode) {
        
        LOGGER.debug("MetaTagsLinkDomainServiceImpl :: isCountryExcluded :: Start");
        boolean isExcluded = false;
        
        if (this.excludedCountryDomainKeyList.length == 0) {
            LOGGER.debug("excludedCountryDomainKeyList is empty. {} not excluded.", countryCode);
        } else {

            for (String countryDomainKeyString : this.excludedCountryDomainKeyList) {
                String[] countryDomainKeyArray = countryDomainKeyString.split(CommonConstants.COLON);
                if (StringUtils.equalsIgnoreCase(countryDomainKeyArray[0], countryCode)) {
                    isExcluded = true;
                    LOGGER.debug("{} found in excludedCountryDomainKeyList", countryCode);
                }
            }
        }
        LOGGER.debug("MetaTagsLinkDomainServiceImpl :: isCountryExcluded :: End");
        return isExcluded;
    }
    
    @Override
    public String getCustomDomainKey(String countryCode) {
        
        LOGGER.debug("MetaTagsLinkDomainServiceImpl :: getCustomDomainKey :: Start");

        String domainKey = StringUtils.EMPTY;
        
        try {

            if (!isCountryExcluded(countryCode)) {
                throw new NullPointerException("Country code not found in domain mapping.");
            }

            for (String countryDomainKeyString : this.excludedCountryDomainKeyList) {
                String[] countryDomainKeyArray = countryDomainKeyString.split(CommonConstants.COLON);
                if (StringUtils.equals(countryDomainKeyArray[0], countryCode)) {
                    domainKey = countryDomainKeyArray[1];
                    LOGGER.debug("domain key {} set for country {}", domainKey, countryCode);
                }
            }
        } catch (NullPointerException ne) {
            LOGGER.debug("NullPointerException :: {} :: {}", ne.getMessage(), ne.getStackTrace());
            domainKey = fallbackDomainKey;
        }
        
        LOGGER.debug("MetaTagsLinkDomainServiceImpl :: getCustomDomainKey :: End");
        return domainKey;
        
    }
    
    

}
