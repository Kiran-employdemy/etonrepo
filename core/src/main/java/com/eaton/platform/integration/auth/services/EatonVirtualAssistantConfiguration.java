package com.eaton.platform.integration.auth.services;

public interface EatonVirtualAssistantConfiguration {

    String getUserIdCookieName();
    String getUserIdCookieDomain();
    int getUserIdCookieTTL();
    String getVistaIdCookieName();
    String getVistaIdCookieDomain();
    int getVistaIdCookieTTL();
    String getVistaIdJsonName();
    String getDrcIdJsonName();
    String getHistoryCookieName();
    String getHistoryCookieDomain();
    boolean getIsIvSecurityCookieSet();
    String getIvSecurityCookieName();
    String getIvSecurityCookieDomain();
    int getIvSecurityCookieTTL();
    String getIvSecurityCookieValue();
    String getEncryptionAlgorithm();
    String getEncryptionMode();
    String getEncryptionKey();
    
}
