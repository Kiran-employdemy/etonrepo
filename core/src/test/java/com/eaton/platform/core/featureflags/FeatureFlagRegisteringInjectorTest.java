package com.eaton.platform.core.featureflags;

import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Inject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeatureFlagRegisteringInjectorTest {

    @InjectMocks
    FeatureFlagRegisteringInjector featureFlagRegisteringInjector = new FeatureFlagRegisteringInjector();
    @Mock
    FeatureFlagRegistryService featureFlagRegistryService;
    @Mock
    FeatureFlag featureFlag;
    @Mock
    Inject inject;
    @Mock
    AnnotatedElement annotatedElement;
    @Mock
    DisposalCallbackRegistry disposalCallbackRegistry;
    @Mock
    Type type;

    @Test
    void testGetName() {
        Assertions.assertEquals("featureFlagged", featureFlagRegisteringInjector.getName(), "should be featureFlagged");
    }

    @Test
    void testGetValueNotFeatureFlagAnnotation() {
        when(annotatedElement.getDeclaredAnnotation(FeatureFlag.class)).thenReturn(null);
        Assertions.assertNull(featureFlagRegisteringInjector.getValue("adaptable", "aFieldName"
                , type, annotatedElement, disposalCallbackRegistry), "should be null");
    }

    @Test
    void testGetValueFeatureFlagAnnotation() {
        when(annotatedElement.getDeclaredAnnotation(FeatureFlag.class)).thenReturn(featureFlag);
        when(annotatedElement.getAnnotation(FeatureFlag.class)).thenReturn(featureFlag);
        Assertions.assertSame("adaptable", featureFlagRegisteringInjector.getValue("adaptable", "aFieldName"
                , type, annotatedElement, disposalCallbackRegistry), "should be same adaptable object from method");
        verify(featureFlagRegistryService).registerFeatureFlag("aFieldName", featureFlag);
    }


}