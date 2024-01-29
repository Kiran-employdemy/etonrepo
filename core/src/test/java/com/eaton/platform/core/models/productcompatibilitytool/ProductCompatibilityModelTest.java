package com.eaton.platform.core.models.productcompatibilitytool;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class ProductCompatibilityModelTest {

    @InjectMocks
    ProductCompatibilityModel productCompatibilityModel;

    AemContext aemContext = new AemContext();

    private static final String RESOURCE_PATH = "/content/eaton/us/en-us/products/wiring-devices-connectivity/lighting-controls-and-connected-systems/led-compatibility-selector-tools/compatible-bulbs-results/jcr:content/root/responsivegrid/compatible_product";

    @BeforeEach
    void setUp() {
        Resource resource= aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("product-compatibility-tool-model.json")), RESOURCE_PATH);
        aemContext.request().setResource(resource);
        productCompatibilityModel = aemContext.request().adaptTo(ProductCompatibilityModel.class);
    }

    @Test
    void testCreateProductCompatibilityModel() {
        assertNotNull(productCompatibilityModel, "example json resource should be converted to a ProductCompatibilityModel");
    }
}
