package com.eaton.platform.core.services.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eaton.platform.core.services.FeatureEnablementService;
import com.eaton.platform.core.services.config.FeatureEnablementConfiguration;
import com.eaton.platform.core.services.impl.FeatureEnablementServiceImpl;

@ExtendWith(MockitoExtension.class)
public class FeatureEnablementServiceImplTest {
    
    @InjectMocks
    FeatureEnablementServiceImpl featureEnablementServiceImpl = new FeatureEnablementServiceImpl();
    @Mock
    FeatureEnablementService featureEnablementService;
    @Mock
    FeatureEnablementConfiguration featureEnablementConfiguration;
    private boolean isFreeSampleButtonEnabled;

    @Test
    public void testAactivate() {    
        when(featureEnablementConfiguration.isFreeSampleButtonEnabled()).thenReturn(isFreeSampleButtonEnabled);
        assertEquals(featureEnablementConfiguration.isFreeSampleButtonEnabled(),false);
        featureEnablementServiceImpl.activate(featureEnablementConfiguration);
     }
    
    @Test
    public void testIsFreeSampleButtonEnabled() {
       assertEquals(featureEnablementServiceImpl.isFreeSampleButtonEnabled(),false);
       featureEnablementService.isFreeSampleButtonEnabled();
    }
}
