package com.eaton.platform.core.models.drilldownselectiontool;

import com.day.cq.wcm.api.Page;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Locale;

import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class DropdownOptionModelTest {

    private static final String DROPDOWN_OPTION_TAG_PATH = "/content/cq:tags/eaton/support-taxonomy/category/technical-support";
    private static final String DROPDOWN_OPTION_TITLE_EN_US = "Contact technical support";
    private static final String DROPDOWN_OPTION_TITLE_DE_DE = "Contact technical support German";

    @InjectMocks
    DropdownOptionModel dropdownOptionModel = new DropdownOptionModel();

    @Mock
    private Page currentPage;

    @Mock
    private Resource resource;

    @Mock
    private ValueMap valueMap;


    @BeforeEach
    void setup(){
        when(resource.getPath()).thenReturn(DROPDOWN_OPTION_TAG_PATH);
        when(resource.getValueMap()).thenReturn(valueMap);
    }

    @Test
    @DisplayName("Test populating new DropdownOptionModel with all valid values and default language")
    void testDropdownOptionModelOfWithValidDataAndDefaultLanguage() {
        when(currentPage.getLanguage()).thenReturn(Locale.getDefault());
        when(valueMap.get("jcr:title.en_us")).thenReturn(null);
        when(valueMap.get("jcr:title")).thenReturn(DROPDOWN_OPTION_TITLE_EN_US);
        dropdownOptionModel = DropdownOptionModel.of(resource, currentPage);

        Assertions.assertEquals(DROPDOWN_OPTION_TAG_PATH, dropdownOptionModel.getTagPath(), "should equal");
        Assertions.assertEquals(DROPDOWN_OPTION_TITLE_EN_US, dropdownOptionModel.getTitle(), "should equal");

    }

    @Test
    @DisplayName("Test populating new DropdownOptionModel with all valid values and non-default language")
    void testDropdownOptionModelOfWithValidDataAndNonDefaultLanguage() {
        when(currentPage.getLanguage()).thenReturn(Locale.GERMANY);
        when(valueMap.get("jcr:title.de_de")).thenReturn(DROPDOWN_OPTION_TITLE_DE_DE);

        dropdownOptionModel = DropdownOptionModel.of(resource, currentPage);

        Assertions.assertEquals(DROPDOWN_OPTION_TAG_PATH, dropdownOptionModel.getTagPath(), "should equal");
        Assertions.assertEquals(DROPDOWN_OPTION_TITLE_DE_DE, dropdownOptionModel.getTitle(), "should equal");


    }


}
