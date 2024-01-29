package com.eaton.platform.core.services.secure.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

import com.eaton.platform.core.constants.CommonConstants;
import com.google.gson.Gson;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.secure.AgentReportsMapperService;
import com.eaton.platform.integration.auth.constants.SecureConstants;


/**
 * The class AgentReportsMapperServiceImpl : provides the actual divison value based on key
 */
@Component(service = AgentReportsMapperService.class, immediate = true)
public class AgentReportsMapperServiceImpl implements AgentReportsMapperService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgentReportsMapperServiceImpl.class);

    @Reference
    private AdminService adminService;

    private AgentReportMapping agentReportsMapping;

    @Activate
    protected void activate() {
        LOGGER.debug("Activation Method Start :: AgentReportsMapperServiceImpl");
        initJsonBuilder();
        LOGGER.debug("Activation Method END :: AgentReportsMapperServiceImpl");
    }

    private void initJsonBuilder() {
        try (ResourceResolver resourceResolver = adminService.getReadService()) {
            final Resource resource = resourceResolver.getResource(SecureConstants.AGENT_REPORTS_MAPPING_JSON);
            if (null != resource) {
                final Asset divisionMapper = resource.adaptTo(Asset.class);
                if (null != divisionMapper && divisionMapper.getRendition(DamConstants.ORIGINAL_FILE) != null) {
                    createDivisionMapperJsonBuilder(divisionMapper);
                }
            } else {
                LOGGER.warn("Agent reports mapping json is not available");
            }
        } catch (Exception ex) {
            LOGGER.error("Exception in AgentReportsMapperServiceImpl  activate method", ex);
        }
    }

    /**
     * This Method provides actual key value
     *
     * @param mappingCode
     * @return actualTagValue
     */
    @Override
    public Map<String, String> getDivisionByMappingCode(String mappingCode) {
        LOGGER.debug("getDivisionByMappingCode : START");
        LOGGER.debug("mappingCode : {}", mappingCode);
        Map<String, String> divisionsMap = Collections.emptyMap();
        if (agentReportsMapping == null) {
            LOGGER.warn("Json Object is null. Trying to re-init");
            initJsonBuilder();
        }
        LOGGER.debug("agentReportsMappingJO : {}", agentReportsMapping);
        if (ObjectUtils.allNotNull(agentReportsMapping, agentReportsMapping.getMappingCodes(),
                agentReportsMapping.getApiKeys(), agentReportsMapping.getDivisionsDisplayText())) {
            LOGGER.debug("mappingCodesJO : {}", agentReportsMapping.getMappingCodes());
            LOGGER.debug("apiKeysJO : {}", agentReportsMapping.getApiKeys());
            LOGGER.debug("divisionsDisplayTextJO : {}", agentReportsMapping.getDivisionsDisplayText());
            divisionsMap = getDivisionMap(mappingCode);
        }

        LOGGER.debug("getDivisionByMappingCode : END");
        return divisionsMap;
    }

    private Map<String, String> getDivisionMap(String mappingCode) {
        Map<String, String> divisionsMap = new HashedMap<>();
        if (agentReportsMapping.getMappingCodes().containsKey(mappingCode)) {
            String values = agentReportsMapping.getMappingCodes().get(mappingCode);
            LOGGER.debug("mappingCodesJO values : {}", values);
            String[] mappingCodevalues = values.split(CommonConstants.COMMA);
            for (String mappingCodevalue : mappingCodevalues) {
                if (agentReportsMapping.getApiKeys().containsKey(mappingCodevalue)) {
                    String apiKeyValue = agentReportsMapping.getApiKeys().get(mappingCodevalue);
                    LOGGER.debug("apiKeyValue : {}", apiKeyValue);
                    String key = mappingCodevalue + SecureConstants.UNDER_SCORE + SecureConstants.DISPLAY;
                    if (agentReportsMapping.getDivisionsDisplayText().containsKey(key)) {
                        String divisionsDisplayText = agentReportsMapping.getDivisionsDisplayText().get(key);
                        LOGGER.debug("divisionsDisplayText : {}", divisionsDisplayText);
                        divisionsMap.put(apiKeyValue, divisionsDisplayText);
                        LOGGER.debug("apiKeyValue : divisionsDisplayText added to divisionsMap");
                    }
                }
            }
        }
        return divisionsMap;
    }

    /**
     * Method to read the data from divisionMapper file and construct the jsonBuilder object
     *
     * @param divisionMapper divisionMapperJson file
     */
    private void createDivisionMapperJsonBuilder(Asset divisionMapper) {
        LOGGER.debug("createDivisionMapperJsonBuilder Start :: AgentReportsMapperServiceImpl");
        BufferedReader br;
        try (InputStream is = divisionMapper.getRendition(DamConstants.ORIGINAL_FILE).getStream()) {
            br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            Gson gson = new Gson();
            agentReportsMapping = gson.fromJson(br, AgentReportMapping.class);
        } catch (IOException io) {
            LOGGER.error("Failed to Read the Mapping File :: IOException in createDivisionMapperJsonBuilder method in AgentReportsMapperServiceImpl", io);
        }
        LOGGER.debug("createDivisionMapperJsonBuilder End :: AgentReportsMapperServiceImpl");
    }
}
