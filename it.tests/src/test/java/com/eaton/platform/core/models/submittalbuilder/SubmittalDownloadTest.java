package com.eaton.platform.core.models.submittalbuilder;

import io.wcm.testing.mock.aem.junit.AemContext;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;

import static com.eaton.platform.TestConstants.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.script.*","com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*"})
public class SubmittalDownloadTest {
    @Rule
    public final AemContext context = new AemContext(ResourceResolverType.JCR_MOCK);

    @Before
    public void setup() throws Exception {
        context.load().json(EXAMPLE_COMPONENTS, EATON_CONTENT_PATH);
        context.addModelsForPackage(SUBMITTAL_BUILDER_MODELS_PACACKAGE);
    }

    @Test
    public void notAuthoredTest() {
        SubmittalDownload intro = context.resourceResolver()
                .getResource(SUBMITTAL_DOWNLOAD_1)
                .adaptTo(SubmittalDownload.class);

        assertNotNull(intro);
        assertEquals(intro.getTitle(), null);
        assertEquals(intro.getPreferredOptionText(), null);
        assertEquals(intro.getSendEmailText(), null);
        assertEquals(intro.getExpirationText(), null);
        assertEquals(intro.getFileSizeLimitText(), null);
        assertEquals(intro.getThankYouDownloadMessage(), null);
        assertEquals(intro.getThankYouEmailMessage(), null);
        assertEquals(intro.getInvalidEmailMessage(), null);
        assertEquals(intro.getEatonCommunicationsMessage(), null);
        assertEquals(intro.getEatonCommunicationsPage(), null);
        assertEquals(intro.getAssetRequiredMessage(), null);
        assertEquals(intro.getMaxSizeExceededMessage(), null);
        assertEquals(intro.getFileNamePrefix(), null);
        assertEquals(intro.getEmailSubject(), null);
        assertEquals(intro.getEmailTemplatePath(), null);
        assertEquals(intro.getSenderEmailAddress(), null);
        assertEquals(intro.getSenderName(), null);
        assertEquals(intro.getEmailErrorPage(), null);
        assertEquals(intro.getEmailNewsLetterLink(), null);
        assertEquals(intro.getEmailSubmittalBuilderToolLink(), null);
        assertEquals(intro.getEmailContactUsLink(), null);
        assertEquals(intro.getEmailWhereToBuyLink(), null);
    }

    @Test
    public void emptyPropertiesTest() {
        SubmittalDownload intro = context.resourceResolver()
                .getResource(SUBMITTAL_DOWNLOAD_2)
                .adaptTo(SubmittalDownload.class);

        assertNotNull(intro);
        assertEquals(intro.getTitle(), "");
        assertEquals(intro.getPreferredOptionText(), "");
        assertEquals(intro.getSendEmailText(), "");
        assertEquals(intro.getExpirationText(), "");
        assertEquals(intro.getFileSizeLimitText(), "");
        assertEquals(intro.getThankYouDownloadMessage(), "");
        assertEquals(intro.getThankYouEmailMessage(), "");
        assertEquals(intro.getInvalidEmailMessage(), "");
        assertEquals(intro.getEatonCommunicationsMessage(), "");
        assertEquals(intro.getEatonCommunicationsPage(), "");
        assertEquals(intro.getAssetRequiredMessage(), "");
        assertEquals(intro.getMaxSizeExceededMessage(), "");
        assertEquals(intro.getFileNamePrefix(), "");
        assertEquals(intro.getEmailSubject(), "");
        assertEquals(intro.getEmailTemplatePath(), "");
        assertEquals(intro.getSenderEmailAddress(), "");
        assertEquals(intro.getSenderName(), "");
        assertEquals(intro.getEmailErrorPage(), "");
        assertEquals(intro.getEmailNewsLetterLink(), "");
        assertEquals(intro.getEmailSubmittalBuilderToolLink(), "");
        assertEquals(intro.getEmailContactUsLink(), "");
        assertEquals(intro.getEmailWhereToBuyLink(), "");
    }

    @Test
    public void fullyAuthoredTest() {
        SubmittalDownload intro = context.resourceResolver()
                .getResource(SUBMITTAL_DOWNLOAD_3)
                .adaptTo(SubmittalDownload.class);

        assertNotNull(intro);
        assertEquals(intro.getTitle(), "Example");
        assertEquals(intro.getPreferredOptionText(), "Test description.");
        assertEquals(intro.getSendEmailText(), "Send Email");
        assertEquals(intro.getExpirationText(), "Link expires 24 hours after creation.");
        assertEquals(intro.getFileSizeLimitText(), "100");
        assertEquals(intro.getThankYouDownloadMessage(), "Thank you, your download has started.");
        assertEquals(intro.getThankYouEmailMessage(), "Thank you, your email has been sent.");
        assertEquals(intro.getInvalidEmailMessage(), "Please enter a valid email.");
        assertEquals(intro.getEatonCommunicationsMessage(), "Sample Communication Message");
        assertEquals(intro.getEatonCommunicationsPage(), "Sample Communication Page");
        assertEquals(intro.getAssetRequiredMessage(), "Invalid number of assets. Minimum one valid asset is required.");
        assertEquals(intro.getMaxSizeExceededMessage(), "Max size exceeded.");
        assertEquals(intro.getFileNamePrefix(), "SubmittalBuilder");
        assertEquals(intro.getEmailSubject(), "Eaton Asset Download");
        assertEquals(intro.getEmailTemplatePath(), "samplepath");
        assertEquals(intro.getSenderEmailAddress(), "xyz@sample.com");
        assertEquals(intro.getSenderName(), "Unknown");
        assertEquals(intro.getEmailErrorPage(), "/content/eaton/language-masters/en-us/404");
        assertEquals(intro.getEmailNewsLetterLink(), "http://www.google.com");
        assertEquals(intro.getEmailSubmittalBuilderToolLink(), "http://www.eaton.com");
        assertEquals(intro.getEmailContactUsLink(), "http://www.google.com");
        assertEquals(intro.getEmailWhereToBuyLink(), "http://www.google.com");
    }

    @Test
    public void partiallyAuthoredTest() {
        SubmittalDownload intro = context.resourceResolver()
                .getResource(SUBMITTAL_DOWNLOAD_4)
                .adaptTo(SubmittalDownload.class);

        assertNotNull(intro);
        assertEquals(intro.getTitle(), "Example");
        assertEquals(intro.getPreferredOptionText(), "");
        assertEquals(intro.getSendEmailText(), null);
        assertEquals(intro.getExpirationText(), null);
        assertEquals(intro.getFileSizeLimitText(), "");
        assertEquals(intro.getThankYouDownloadMessage(), null);
        assertEquals(intro.getThankYouEmailMessage(), null);
        assertEquals(intro.getInvalidEmailMessage(), null);
        assertEquals(intro.getEatonCommunicationsMessage(), "");
        assertEquals(intro.getEatonCommunicationsPage(), "");
        assertEquals(intro.getAssetRequiredMessage(), null);
        assertEquals(intro.getMaxSizeExceededMessage(), null);
        assertEquals(intro.getFileNamePrefix(), "");
        assertEquals(intro.getEmailSubject(), null);
        assertEquals(intro.getEmailTemplatePath(), null);
        assertEquals(intro.getSenderEmailAddress(), null);
        assertEquals(intro.getSenderName(), null);
        assertEquals(intro.getEmailErrorPage(), "");
        assertEquals(intro.getEmailNewsLetterLink(), "");
        assertEquals(intro.getEmailSubmittalBuilderToolLink(), "");
        assertEquals(intro.getEmailContactUsLink(), "");
        assertEquals(intro.getEmailWhereToBuyLink(), "");
    }
}
