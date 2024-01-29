package com.eaton.platform.core.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMException;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class LinkListWithIconModelTest {

    @Mock
    private LinkListWithIconModel linkListWithIconModel;

    AemContext aemContext = new AemContext();
    private static final String ROOT_PATH= "/content";
    private static final String RESOURCE_PATH = ROOT_PATH + "/resource/jcr:content/root/responsivegrid/link_list_with_icons";
    private static final String PAGE_PATH= ROOT_PATH + "/page";

    @BeforeEach
    void setUp(){
        aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("linklistwithiconmodel-parent.json")), PAGE_PATH);
        Resource pageResource = aemContext.resourceResolver().getResource(PAGE_PATH);
        Page page = pageResource.adaptTo(Page.class);
        aemContext.currentPage(page);
        aemContext.registerService(Page.class, page);
        aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("linklistwithiconmodel.json")), RESOURCE_PATH);
        aemContext.currentResource(RESOURCE_PATH);
        linkListWithIconModel = Objects.requireNonNull(aemContext.request().adaptTo(LinkListWithIconModel.class));
    }
    @Test
    @DisplayName("test when links are set in linklist")
    void testGetLinks() {
        assertEquals(3, linkListWithIconModel.getLinks().size(),"getLinks should return 3 links");
    }
    @Test
    @DisplayName("test when isSuffixEnabled is enabled")
    void testIsSuffixEnabled() {
        assertTrue(linkListWithIconModel.getLinks().get(0).isSuffixEnabled(),"isSuffixEnabled should return true");
    }
    @Test
    @DisplayName("test when isSuffixEnabled is disabled")
    void testIsSuffixDisabled() {
        assertFalse(linkListWithIconModel.getLinks().get(1).isSuffixEnabled(),"isSuffixEnabled should return false");
    }

}
