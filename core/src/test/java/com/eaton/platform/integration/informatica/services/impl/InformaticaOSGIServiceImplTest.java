package com.eaton.platform.integration.informatica.services.impl;

import com.eaton.platform.integration.informatica.bean.InformaticaConfigServiceBean;
import com.eaton.platform.integration.informatica.services.config.InformaticaOSGIServiceConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.eaton.platform.integration.informatica.bean.InformaticaConfigServiceBeanFixtures.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InformaticaOSGIServiceImplTest {

    @Mock
    InformaticaOSGIServiceConfig informaticaOSGIServiceConfig;
    InformaticaOSGIServiceImpl informaticaOSGIService = new InformaticaOSGIServiceImpl();

    @Test
    @DisplayName("When activate is called, the correct file paths from config are written to a InformaticaConfigServiceBean")
    void testActivateCorrectConfigPathsArePutInConfigBean() {
        when(informaticaOSGIServiceConfig.archive_attribute_file_path()).thenReturn(ARCHIVE_ATTRIBUTE_FILE_PATH);
        when(informaticaOSGIServiceConfig.failed_attribute_file_path()).thenReturn(FAILED_ATTRIBUTE_FILE_PATH);

        informaticaOSGIService.activate(informaticaOSGIServiceConfig);
        InformaticaConfigServiceBean informaticaConfigServiceBean = informaticaOSGIService.getConfigServiceBean();
        InformaticaConfigServiceBean expectedInformaticaConfigServiceBean = createConfigBean();
        Assertions.assertEquals(expectedInformaticaConfigServiceBean, informaticaConfigServiceBean, "should be equal");

    }
}