package com.eaton.platform.core.bean;

import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import io.wcm.testing.mock.aem.junit.AemContext;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.script.*","com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*"})
public class EndecaServiceRequestBeanTest {

    @Mock
    EndecaServiceRequestBean endecaServiceRequestBean;

    @Rule
    public final AemContext context = new AemContext(ResourceResolverType.JCR_MOCK);

    @Test
    public void testEqualsAndHashCode() {
        EndecaServiceRequestBean endecaServiceRequestBean1 = new EndecaServiceRequestBean();
        endecaServiceRequestBean1 = new EndecaServiceRequestBean();
        endecaServiceRequestBean1.setSearchApplicationKey("abc123");
        endecaServiceRequestBean1.setFunction("search");
        endecaServiceRequestBean1.setLanguage("en_US");
        endecaServiceRequestBean1.setStartingRecordNumber("0");
        endecaServiceRequestBean1.setNumberOfRecordsToReturn("10");
        endecaServiceRequestBean1.setSearchTerms("RESULTS");

        EndecaServiceRequestBean endecaServiceRequestBean2 = new EndecaServiceRequestBean();
        endecaServiceRequestBean2 = new EndecaServiceRequestBean();
        endecaServiceRequestBean2.setSearchApplicationKey("abc123");
        endecaServiceRequestBean2.setFunction("search");
        endecaServiceRequestBean2.setLanguage("en_US");
        endecaServiceRequestBean2.setStartingRecordNumber("0");
        endecaServiceRequestBean2.setNumberOfRecordsToReturn("10");
        endecaServiceRequestBean2.setSearchTerms("RESULTS");

        assertEquals(endecaServiceRequestBean1, endecaServiceRequestBean2);
        assertTrue(endecaServiceRequestBean1.hashCode() == endecaServiceRequestBean2.hashCode());
        assertTrue(endecaServiceRequestBean1.equals(endecaServiceRequestBean2));
    }

    @Test
    public void testNotEquals() {
        EndecaServiceRequestBean endecaServiceRequestBean1 = new EndecaServiceRequestBean();
        endecaServiceRequestBean1 = new EndecaServiceRequestBean();
        endecaServiceRequestBean1.setSearchApplicationKey("abc123");
        endecaServiceRequestBean1.setFunction("search");
        endecaServiceRequestBean1.setLanguage("en_US");
        endecaServiceRequestBean1.setStartingRecordNumber("0");
        endecaServiceRequestBean1.setNumberOfRecordsToReturn("10");
        endecaServiceRequestBean1.setSearchTerms("RESULTS");

        EndecaServiceRequestBean endecaServiceRequestBean2 = new EndecaServiceRequestBean();
        endecaServiceRequestBean2 = new EndecaServiceRequestBean();
        endecaServiceRequestBean2.setSearchApplicationKey("abc123");
        endecaServiceRequestBean2.setFunction("search");
        endecaServiceRequestBean2.setLanguage("en_US");
        endecaServiceRequestBean2.setStartingRecordNumber("0");
        endecaServiceRequestBean2.setNumberOfRecordsToReturn("10");
        endecaServiceRequestBean2.setSearchTerms("ENDECA RESULTS");

        assertNotEquals(endecaServiceRequestBean1, endecaServiceRequestBean2);
        assertTrue(endecaServiceRequestBean1.hashCode() != endecaServiceRequestBean2.hashCode());
        assertTrue(!endecaServiceRequestBean1.equals(endecaServiceRequestBean2));
    }

}
