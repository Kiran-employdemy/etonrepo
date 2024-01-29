package com.eaton.platform.core.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eaton.platform.core.services.BussmannPriceFileService;
import com.eaton.platform.core.services.config.BussmannPriceFileServiceConfig;

@ExtendWith(MockitoExtension.class)
public class BussmannPriceFileServiceImplTest {

    @InjectMocks
    BussmannPriceFileServiceImpl bussmannPriceFileServiceImpl = new BussmannPriceFileServiceImpl();
    @Mock
    BussmannPriceFileService bussmannPriceFileService;
    @Mock
    BussmannPriceFileServiceConfig bussmannPriceFileServiceConfig;
    private String  bussmannCANfileURL="";

    

     @Test
    void testAactivate() {
        when(bussmannPriceFileServiceConfig.bussmannCANfileURL()).thenReturn(bussmannCANfileURL);
        when(bussmannPriceFileServiceConfig.bussmannEDICANfileURL()).thenReturn(toString());
        when(bussmannPriceFileServiceConfig.bussmannEDIUSfileURL()).thenReturn("");
        when(bussmannPriceFileServiceConfig.bussmannUSfileURL()).thenReturn("");
        assertEquals(bussmannPriceFileServiceConfig.bussmannCANfileURL(),"");
        assertEquals(bussmannPriceFileServiceConfig.bussmannCANfileURL(),"");
        assertEquals(bussmannPriceFileServiceConfig.bussmannCANfileURL(),"");
        assertEquals(bussmannPriceFileServiceConfig.bussmannCANfileURL(),"");

        bussmannPriceFileServiceImpl.activate(bussmannPriceFileServiceConfig);
     }
    
    @Test
    void testGetBussmannCANfileURL() {
       assertEquals(bussmannPriceFileServiceImpl.getBussmannCANfileURL(),null);
       bussmannPriceFileServiceImpl.getBussmannCANfileURL();
    }
    @Test
    void testGetBussmannEDICANfileURL() {
       assertEquals(bussmannPriceFileServiceImpl.getBussmannEDICANfileURL(),null);
       bussmannPriceFileServiceImpl.getBussmannEDICANfileURL();
    }
    @Test
    void testGetBussmannEDIUSfileURL() {
       assertEquals(bussmannPriceFileServiceImpl.getBussmannEDIUSfileURL(),null);
       bussmannPriceFileServiceImpl.getBussmannEDIUSfileURL();
    }
    @Test
    void testGetBussmannUSfileURL() {
       assertEquals(bussmannPriceFileServiceImpl.getBussmannUSfileURL(),null);
       bussmannPriceFileServiceImpl.getBussmannUSfileURL();
    }
}
