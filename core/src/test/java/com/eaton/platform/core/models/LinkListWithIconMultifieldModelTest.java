package com.eaton.platform.core.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Objects;
import static junitx.framework.Assert.assertEquals;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class LinkListWithIconMultifieldModelTest {
    private static final String RESOURCE_PATH = "/content";
    AemContext aemContext = new AemContext();

    private LinkListWithIconMultifieldModel linkListWithIconMultifieldModel;

    @BeforeEach
    void setUp() {
        aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("linklistwithicon-multifieldmodel.json")), RESOURCE_PATH);
        aemContext.currentResource(RESOURCE_PATH);
        linkListWithIconMultifieldModel = aemContext.currentResource().adaptTo(LinkListWithIconMultifieldModel.class);

    }

    @Test
    @DisplayName("Test Link Icon is generated")
    void testLinkIcon() {
        assertEquals("Link Icon should return 'support-email.png'",linkListWithIconMultifieldModel.getLinkIcon(),"/content/dam/eaton/resources/icons/support-icons/support-email.png");
    }

    @Test
    @DisplayName("Test Link Path is generated")
    void testLinkPath() {
        assertEquals("Link Path should return 'mailto:test@email.com'", linkListWithIconMultifieldModel.getLinkPath(),"mailto:test@email.com");
    }

    @Test
    @DisplayName("Test Link Title is generated")
    void testLinkTitle() {
        assertEquals("Link Title should return 'Email link'",linkListWithIconMultifieldModel.getLinkTitle(),"Email link");
    }

    @Test
    @DisplayName("Test Link opens in new window")
    void testLinkOpenNewWindow() {
        assertEquals("Link opens in new window if true then return '_blank'",linkListWithIconMultifieldModel.getLinkOpenNewWindow(),"_blank");
    }

    @Test
    @DisplayName("Test Link Desc is generated")
    void testLinkDesc() {
        assertEquals("Link Desc should return 'Email Desc'",linkListWithIconMultifieldModel.getLinkDesc(),"Email Desc");
    }
}
