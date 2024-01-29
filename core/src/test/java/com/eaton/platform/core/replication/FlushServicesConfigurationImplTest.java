package com.eaton.platform.core.replication;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@ExtendWith(AemContextExtension.class)
class FlushServicesConfigurationImplTest {
    private AemContext ctx = new AemContext();
    private FlushServicesConfigurationImpl flushServiceConfiguration;

    @BeforeEach
    public void init() {
        flushServiceConfiguration = ctx.registerInjectActivateService(new FlushServicesConfigurationImpl(),
                Map.of("skipPathsRegexp", new String[]{".*/secure($|/.*)"}));
    }

    @Test
    void testShouldReturnTrueWhenPathShouldBeSkipped(){
        assertTrue("Should return true for skipped path",
                flushServiceConfiguration.skipContentFlushing("/content/eaton/secure") );
        assertTrue("Should return true for skipped path",
                flushServiceConfiguration.skipContentFlushing("/content/eaton/secure/nested") );
    }

    @Test
    void testShouldReturnFalseWhenPathShouldNotBeSkipped(){
        assertFalse("Should return false for not skipped path",
                flushServiceConfiguration.skipContentFlushing("/content/eaton/secure2") );
        assertFalse("Should return false for not skipped path",
                flushServiceConfiguration.skipContentFlushing("/content/eaton/notSecure") );
        assertFalse("Should return false for not skipped path",
                flushServiceConfiguration.skipContentFlushing("/content/eaton/notSecure/nested") );
    }
}