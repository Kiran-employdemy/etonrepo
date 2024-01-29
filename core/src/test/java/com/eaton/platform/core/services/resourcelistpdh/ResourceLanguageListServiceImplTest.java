package com.eaton.platform.core.services.resourcelistpdh;

import com.day.cq.dam.api.Asset;
import com.eaton.platform.core.services.AdminService;
import com.google.gson.Gson;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class ResourceLanguageListServiceImplTest {

    private static final String NON_EXISTING_KEY = "NON_EXISTING_KEY";
    private static final String CAD_DRAW_PACK = "3D_CAD_DRAW_PACK";
    private static final String CAD_DRAWING_PACKAGE_EN_US = "3D CAD drawing package";
    private static final String CAD_DRAWING_PACKAGE_ID_ID = "Paket menggambar CAD 3D";
    @InjectMocks
    ResourceLanguageListServiceImpl resourceLanguageListService = new ResourceLanguageListServiceImpl();
    @Mock
    AdminService adminService;

    Asset resourceLangageListAsset;
    ResourceLanguageList resourceLanguageList;
    ResourceResolver resourceResolver;

    @Mock
    ResourceResolver mockedResourceResolver;
    @Mock
    Resource mockedResource;
    @Mock
    Asset asset;

    @BeforeEach
    void setUp(AemContext aemContext) {
        resourceLangageListAsset = aemContext.create().asset(ResourceLanguageListServiceImpl.RESOURCE_LANGUAGE_LIST_JSON_LOCATION
                , Objects.requireNonNull(this.getClass().getResourceAsStream("resourceLanguageList.json"))
                , "application/json");
        resourceLanguageList = new Gson().fromJson(new InputStreamReader(resourceLangageListAsset.getOriginal().getStream(), StandardCharsets.UTF_8), ResourceLanguageList.class);
        resourceResolver = aemContext.resourceResolver();
    }

    @Test
    @DisplayName("Everything should be loaded correctly")
    void testThatEverythingIsLoadedCorrectly() {
        assertEquals(41, resourceLanguageList.getResourceLanguages().size(), "41 mappings should have been loaded");
    }

    @Test
    @DisplayName("When a key is used for which no key-value mapping exist, null is returned")
    void testThatNullIsReturnedWhenKeyIsNotExisting() {
        when(adminService.getReadService()).thenReturn(resourceResolver);
        assertNull(resourceLanguageListService.resourceGroupName(NON_EXISTING_KEY, Locale.US), "should return null");
    }

    @Test
    @DisplayName("When a key is used for which the mapping exist but the language is missing, en-us one is returned")
    void testThatEnUSIsUsedAsFallbackWhenLanguageMissing() {
        when(adminService.getReadService()).thenReturn(resourceResolver);
        assertEquals(CAD_DRAWING_PACKAGE_EN_US,resourceLanguageListService.resourceGroupName(CAD_DRAW_PACK, new Locale("mm", "my")), "should return en-us one");
    }
    @Test
    @DisplayName("When a key is used for which the mapping exist and the language is existing, that one is returned")
    void testThatCorrectOneIsReturnedWhenLanguageExists() {
        when(adminService.getReadService()).thenReturn(resourceResolver);
        assertEquals(CAD_DRAWING_PACKAGE_ID_ID,resourceLanguageListService.resourceGroupName(CAD_DRAW_PACK, new Locale("id", "id")), "should return id-id one");
    }

    @Test
    @DisplayName("When a key is used for which the mapping exist and the language is existing, that one is returned")
    void testThatSameNumberOfKeysExistFor2Locales() {
        when(adminService.getReadService()).thenReturn(resourceResolver);
        assertEquals(resourceLanguageListService.getResourceLanguageList(Locale.US).size(),resourceLanguageListService.getResourceLanguageList(Locale.FRANCE).size(), "should be the same number");
    }
}