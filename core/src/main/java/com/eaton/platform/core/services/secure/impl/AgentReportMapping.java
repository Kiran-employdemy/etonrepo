package com.eaton.platform.core.services.secure.impl;

import java.util.Map;

public class AgentReportMapping {
    private Map<String, String> apiKeys;
    private Map<String, String> divisionsDisplayText;
    private Map<String, String> mappingCodes;

    public Map<String, String> getApiKeys() {
        return apiKeys;
    }

    public Map<String, String> getDivisionsDisplayText() {
        return divisionsDisplayText;
    }

    public Map<String, String> getMappingCodes() {
        return mappingCodes;
    }
}
