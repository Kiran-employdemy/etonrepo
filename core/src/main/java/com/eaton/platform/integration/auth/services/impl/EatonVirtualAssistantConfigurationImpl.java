package com.eaton.platform.integration.auth.services.impl;


import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.integration.auth.services.EatonVirtualAssistantConfiguration;
import com.eaton.platform.integration.auth.services.config.EatonVirtualAssistantConfig;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
        service = EatonVirtualAssistantConfiguration.class,
        immediate = true,
        property = {
                AEMConstants.SERVICE_VENDOR_EATON,
                AEMConstants.SERVICE_DESCRIPTION + "Eaton Virtual Assistant Configuration",
                AEMConstants.PROCESS_LABEL + "Eaton Virtual Assistant Configuration"
        })
@Designate(ocd = EatonVirtualAssistantConfig.class)
public class EatonVirtualAssistantConfigurationImpl implements EatonVirtualAssistantConfiguration {

    private String userIdCookieName;
    private String userIdCookieDomain;
    private int userIdCookieTTL;
    private String vistaIdCookieName;
    private String vistaIdCookieDomain;
    private int vistaIdCookieTTL;
    private String vistaIdJsonName;
    private String drcIdJsonName;
    private String historyCookieName;
    private String historyCookieDomain;
    private boolean isIvSecurityCookieSet;
    private String ivSecurityCookieName;
    private String ivSecurityCookieDomain;
    private int ivSecurityCookieTTL;
    private String ivSecurityCookieValue;
    private String encryptionAlgorithm;
    private String encryptionMode;
    private String encryptionKey;

    private static final Logger LOG = LoggerFactory.getLogger(EatonVirtualAssistantConfigurationImpl.class);


    @Activate
    @Modified
    protected  final void activate(final EatonVirtualAssistantConfig config) {
        LOG.debug("EatonVirtualAssistantConfigurationImpl.activate :: START");
        if (config != null) {
            userIdCookieName = config.userIdCookieName();
            LOG.debug("userIdCookieName: {}", userIdCookieName);
            userIdCookieDomain = config.userIdCookieDomain();
            LOG.debug("userIdCookieDomain: {}", userIdCookieDomain);
            userIdCookieTTL = config.userIdCookieTTL();
            LOG.debug("userIdCookieTTL: {}", userIdCookieTTL);

            vistaIdCookieName = config.vistaIdCookieName();
            LOG.debug("vistaIdCookieName: {}", vistaIdCookieName);
            vistaIdCookieDomain = config.vistaIdCookieDomain();
            LOG.debug("vistaIdCookieDomain: {}", vistaIdCookieDomain);
            vistaIdCookieTTL = config.vistaIdCookieTTL();
            LOG.debug("vistaIdCookieTTL: {}", vistaIdCookieTTL);
            vistaIdJsonName = config.vistaIdJsonName();
            LOG.debug("vistaIdJsonName: {}", vistaIdJsonName);
            drcIdJsonName = config.drcIdJsonName();
            LOG.debug("drcIdJsonName: {}", drcIdJsonName);
            
            historyCookieName = config.historyCookieName();
            LOG.debug("historyCookieName: {}", historyCookieName);
            historyCookieDomain = config.historyCookieDomain();
            LOG.debug("historyCookieDomain: {}", historyCookieDomain);

            isIvSecurityCookieSet = config.isIvSecurityCookieSet();
            LOG.debug("isIvSecurityCookieSet: {}", isIvSecurityCookieSet);
            ivSecurityCookieName = config.ivSecurityCookieName();
            LOG.debug("ivSecurityCookieName: {}", ivSecurityCookieName);
            ivSecurityCookieDomain = config.ivSecurityCookieDomain();
            LOG.debug("ivSecurityCookieDomain: {}", ivSecurityCookieDomain);
            ivSecurityCookieTTL = config.ivSecurityCookieTTL();
            LOG.debug("ivSecurityCookieTTL: {}", ivSecurityCookieTTL);
            ivSecurityCookieValue = config.ivSecurityCookieValue();
            LOG.debug("ivSecurityCookieValue: {}", ivSecurityCookieValue);

            encryptionAlgorithm = config.encryptionAlgorithm();
            LOG.debug("encryptionAlgorithm: {}", encryptionAlgorithm);
            encryptionMode = config.encryptionMode();
            LOG.debug("encryptionMode: {}", encryptionMode);
            encryptionKey = config.encryptionKey();
            LOG.debug("encryptionKey: {}", encryptionKey);
        }
        LOG.debug("EatonVirtualAssistantConfigurationImpl.activate :: END");

    }

    @Override
    public String getUserIdCookieName() {
        return userIdCookieName;
    }

    @Override
    public String getUserIdCookieDomain() {
        return userIdCookieDomain;
    }

    @Override
    public int getUserIdCookieTTL() {
        return userIdCookieTTL;
    }

    @Override
    public String getVistaIdCookieName() {
        return vistaIdCookieName;
    }

    @Override
    public String getVistaIdCookieDomain() {
        return vistaIdCookieDomain;
    }

    @Override
    public int getVistaIdCookieTTL() {
        return vistaIdCookieTTL;
    }

    @Override
    public String getVistaIdJsonName() {
        return vistaIdJsonName;
    }

    @Override
    public String getDrcIdJsonName() {
        return drcIdJsonName;
    }
    
    @Override
    public String getHistoryCookieName() {
        return historyCookieName;
    }

    @Override
    public String getHistoryCookieDomain() {
        return historyCookieDomain;
    }
    
    @Override
    public boolean getIsIvSecurityCookieSet() {
        return isIvSecurityCookieSet;
    }

    @Override
    public String getIvSecurityCookieName() {
        return ivSecurityCookieName;
    }

    @Override
    public String getIvSecurityCookieDomain() {
        return ivSecurityCookieDomain;
    }

    @Override
    public int getIvSecurityCookieTTL() {
        return ivSecurityCookieTTL;
    }

    @Override
    public String getIvSecurityCookieValue() {
        return ivSecurityCookieValue;
    }

    @Override
    public String getEncryptionAlgorithm() {
        return encryptionAlgorithm;
    }

    @Override
    public String getEncryptionMode() {
        return encryptionMode;
    }

    @Override
    public String getEncryptionKey() {
        return encryptionKey;
    }

}
