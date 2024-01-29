package com.eaton.platform.core.bean;

import java.io.Serializable;

public class EatonVanityConfigBean implements Serializable {
    private static final long serialVersionUID = 6856237390555375580L;

    private boolean enableCustomVanityFlow;
    private String intermediatePageName;
    private String vanityDataStoreParent;
    private String eatonVanityDataStore;
    private String eatonCumminsVanityDataStore;
    private String greenSwitchingVanityDataStore;
    private String phoenixtecPowerVanityDataStore;
    private String[] topLevelDomainConfig;
    private String[] lookupSkipPath;
    public boolean isEnableCustomVanityFlow() {
        return enableCustomVanityFlow;
    }

    public void setEnableCustomVanityFlow(boolean enableCustomVanityFlow) {
        this.enableCustomVanityFlow = enableCustomVanityFlow;
    }

    public String getIntermediatePageName() {
        return intermediatePageName;
    }

    public void setIntermediatePageName(String intermediatePageName) {
        this.intermediatePageName = intermediatePageName;
    }

    public String getVanityDataStoreParent() {
        return vanityDataStoreParent;
    }

    public void setVanityDataStoreParent(String vanityDataStoreParent) {
        this.vanityDataStoreParent = vanityDataStoreParent;
    }

    public String getEatonVanityDataStore() {
        return eatonVanityDataStore;
    }

    public void setEatonVanityDataStore(String eatonVanityDataStore) {
        this.eatonVanityDataStore = eatonVanityDataStore;
    }

    public String getEatonCumminsVanityDataStore() {
        return eatonCumminsVanityDataStore;
    }

    public void setEatonCumminsVanityDataStore(String eatonCumminsVanityDataStore) {
        this.eatonCumminsVanityDataStore = eatonCumminsVanityDataStore;
    }

    public String getGreenSwitchingVanityDataStore() {
        return greenSwitchingVanityDataStore;
    }

    public void setGreenSwitchingVanityDataStore(String greenSwitchingVanityDataStore) {
        this.greenSwitchingVanityDataStore = greenSwitchingVanityDataStore;
    }

    public String getPhoenixtecPowerVanityDataStore() {
        return phoenixtecPowerVanityDataStore;
    }

    public void setPhoenixtecPowerVanityDataStore(String phoenixtecPowerVanityDataStore) {
        this.phoenixtecPowerVanityDataStore = phoenixtecPowerVanityDataStore;
    }

    public String[] getTopLevelDomainConfig() {
        return topLevelDomainConfig;
    }

    public void setTopLevelDomainConfig(String[] topLevelDomainConfig) {
        this.topLevelDomainConfig = topLevelDomainConfig;
    }

    public String[] getLookupSkipPath() {
        return lookupSkipPath;
    }

    public void setLookupSkipPath(String[] lookupSkipPath) {
        this.lookupSkipPath = lookupSkipPath;
    }
}
