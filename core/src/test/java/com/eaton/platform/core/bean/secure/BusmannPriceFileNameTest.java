package com.eaton.platform.core.bean.secure;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class BusmannPriceFileNameTest {

    @InjectMocks
    BusmannPriceFileName busmannPriceFileName;

    
    @Test
    void testGetNameOfFile() {
         busmannPriceFileName = new BusmannPriceFileName();
        assertNull(busmannPriceFileName.getNameOfFile(), "should be null");
    }

    @Test
    void testGetUrlOfFile() {
       busmannPriceFileName = new BusmannPriceFileName();
        assertNull(busmannPriceFileName.getUrlOfFile(), "should be null");
    }

    @Test
    void testSetNameOfFile() {
         busmannPriceFileName = new BusmannPriceFileName();
         busmannPriceFileName.setNameOfFile("");
        assertEquals("", busmannPriceFileName.getNameOfFile(), "should be empty string");
    }

    @Test
    void testSetUrlOfFile() {
       busmannPriceFileName = new BusmannPriceFileName();
        busmannPriceFileName.setUrlOfFile("");
        assertEquals("", busmannPriceFileName.getUrlOfFile(), "should be empty string");
    }
    
}
