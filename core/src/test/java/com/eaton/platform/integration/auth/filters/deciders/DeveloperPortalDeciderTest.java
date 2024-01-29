package com.eaton.platform.integration.auth.filters.deciders;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextBuilder;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static com.eaton.platform.integration.auth.filters.deciders.DeveloperPortalDecider.DEVELOPER_PORTAL_SLING_RES_TYPE;

@ExtendWith({MockitoExtension.class, AemContextExtension.class})
public class DeveloperPortalDeciderTest {
    AemContext context = new AemContextBuilder().build();

    @Test
    @DisplayName(value = "Test to ensure when condition matched it should return true")
    void testToEnsureConditionMatchedShouldReturnTrue(){
        String redirectTo = "/content/to/redirect";
        Resource resource = context.create().resource("/content/eaton/page/jcr:content",Collections.singletonMap(ResourceResolver.PROPERTY_RESOURCE_TYPE,DEVELOPER_PORTAL_SLING_RES_TYPE));
        DeveloperPortalDecider testable = new DeveloperPortalDecider(resource,redirectTo);
        Assertions.assertTrue(testable.conditionMatched());
        Assertions.assertEquals(testable.redirectTo(),redirectTo);
    }

    @Test
    @DisplayName(value = "Test to ensure when condition does not match it should return false")
    void testToEnsureConditionDoesNotMatchReturnFalse(){
        Resource resource = context.create().resource("/content/eaton/page/jcr:content",Collections.singletonMap(ResourceResolver.PROPERTY_RESOURCE_TYPE,"some/resourceType"));
        DeveloperPortalDecider testable = new DeveloperPortalDecider(resource,"");
        Assertions.assertFalse(testable.conditionMatched());
    }
}
