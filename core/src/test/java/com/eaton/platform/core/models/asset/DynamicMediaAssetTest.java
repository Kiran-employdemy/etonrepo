package com.eaton.platform.core.models.asset;

import com.day.cq.commons.Externalizer;
import com.day.cq.dam.api.Asset;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextBuilder;
import io.wcm.testing.mock.aem.junit5.AemContextCallback;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.models.factory.ModelFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.StringUtils;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static com.day.cq.dam.api.DamConstants.DC_FORMAT;
import static com.day.cq.dam.api.s7dam.constants.S7damConstants.*;
import static com.day.cq.dam.scene7.api.constants.Scene7Constants.PN_S7_FILE_STATUS;
import static com.day.cq.dam.scene7.api.constants.Scene7Constants.PV_S7_PUBLISH_COMPLETE;
import static com.eaton.platform.core.constants.AssetConstants.RENDITION_319BY319;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class DynamicMediaAssetTest {

    private final AemContext context = new AemContextBuilder().afterSetUp(
            (AemContextCallback) context -> {
                // custom project initialization code for every unit test
                context.addModelsForClasses(DynamicMediaAsset.class);
            }
    ).build();
    String assetPath = "/content/eaton/dam/someasset.jpg";
    ModelFactory modelFactory;

    @BeforeEach
    void setup(){
        modelFactory = context.getService(ModelFactory.class);
    }

    @Test
    @DisplayName("Ensure if smart crop is available it should return with smart crop path")
    void testToEnsureSmartCropImageExist() {
        context.runMode(Externalizer.PUBLISH);
        Asset asset = createFixture(true);
        DynamicMediaAsset model =  modelFactory.createModel(asset, DynamicMediaAsset.class);
        String path = model.getSCImageOrDefault(":someprofile", RENDITION_319BY319);
        Assertions.assertNotNull(path);
        Assertions.assertFalse(StringUtils.isBlank(path));
        Assertions.assertTrue(path.contains(":someprofile"));
    }

    @Test
    @DisplayName("Ensure if no smart crop is available it should return rendition")
    void testToEsnureIfNoSmartCropAvailableItShouldReturnRendition(){
        Asset asset = createFixture(false);
        DynamicMediaAsset model =  context.getService(ModelFactory.class).createModel(asset, DynamicMediaAsset.class);
        String path = model.getSCImageOrDefault(":someprofile", RENDITION_319BY319);
        Assertions.assertNotNull(path);
        Assertions.assertFalse(StringUtils.isBlank(path));
        Assertions.assertFalse(path.contains(":someprofile"));
        Assertions.assertTrue(path.contains(RENDITION_319BY319));
    }

    private Asset createFixture(boolean withMetadata) {
        Map<String, Object> assetMetadata = new HashMap<>();
        if (withMetadata){
            assetMetadata.put(PN_S7_DOMAIN, "http://domain.com");
            assetMetadata.put(PN_S7_FILE, "somefile");
            assetMetadata.put(PN_S7_FILE_STATUS,PV_S7_PUBLISH_COMPLETE);
            assetMetadata.put(DC_FORMAT,IMAGE);
        }
        Asset asset = context.create().asset(assetPath,1000,1000,"image/jpeg",assetMetadata);
        context.create().assetRendition(asset, RENDITION_319BY319,319,319,"image/jpeg");
        return asset;
    }


}
