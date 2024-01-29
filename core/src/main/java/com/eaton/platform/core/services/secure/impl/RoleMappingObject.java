package com.eaton.platform.core.services.secure.impl;

import java.util.HashMap;
import java.util.Map;

class RoleMappingObject {

    private Map<String, Map<String, String>> accounttype;
    private Map<String, Map<String, String>> productcategory;
    private Map<String, Map<String, String>> applicationaccess;
    private Map<String, Map<String, String>> partnerProgramTypeAndTier;

    private Map<String,Map<String,String>> oktagroups;

    public Map<String, Map<String, String>> getAccounttype() {
        if (accounttype == null) {
            return new HashMap<>();
        }
        return accounttype;
    }

    public Map<String, Map<String, String>> getProductcategory() {
        if (productcategory == null) {
            return new HashMap<>();
        }
        return productcategory;
    }

    public Map<String, Map<String, String>> getApplicationaccess() {
        if (applicationaccess == null) {
            return new HashMap<>();
        }
        return applicationaccess;
    }

    public Map<String, Map<String, String>> getPartnerProgramTypeAndTier() {
        if (partnerProgramTypeAndTier == null) {
            return new HashMap<>();
        }
        return partnerProgramTypeAndTier;
    }

    public Map<String, Map<String, String>> getOktagroups() {
        if(oktagroups == null){
            return new HashMap<>();
        }
        return oktagroups;
    }
}
