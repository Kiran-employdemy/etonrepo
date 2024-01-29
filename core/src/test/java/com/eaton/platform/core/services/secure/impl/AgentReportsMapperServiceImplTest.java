package com.eaton.platform.core.services.secure.impl;

import com.eaton.platform.core.services.AdminService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class AgentReportsMapperServiceImplTest {

    private AemContext context = new AemContext();

    @Mock
    private AdminService adminService;
    private AgentReportsMapperServiceImpl agentReportsMapperService;

    @BeforeEach
    public void beforeEach() {
        when(adminService.getReadService()).thenReturn(context.resourceResolver());
        context.registerService(AdminService.class, adminService);
        agentReportsMapperService = context.registerInjectActivateService(new AgentReportsMapperServiceImpl());
    }

    @Test
    void testGetDivisionByMappingCodeWhenJsonFileIsNotAvailableDuringActivate() {
        context.create().asset("/content/dam/eaton/resources/secure/AgentReportsMapping.json",
                "/com/eaton/platform/core/services/secure/impl/AgentReportsMapping.json",
                "application/json");

        Map<String, String> divisionByMappingCode = agentReportsMapperService.getDivisionByMappingCode("1216_10_10");

        assertEquals("Wiring Devices", divisionByMappingCode.get("WDevices"),
                "Should return mapping by code");
    }

    @Test
    void testReturnEmptyMapWhenDivisionIsNotFound(){
        context.create().asset("/content/dam/eaton/resources/secure/AgentReportsMapping.json",
                "/com/eaton/platform/core/services/secure/impl/AgentReportsMapping.json",
                "application/json");

        Map<String, String> divisionByMappingCode = agentReportsMapperService.getDivisionByMappingCode("0");

        assertTrue( divisionByMappingCode.isEmpty(),"Should return empty map when mapping not exist");
    }
}