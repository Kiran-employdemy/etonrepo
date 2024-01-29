package com.eaton.platform.integration.informatica.bean;

import java.util.Arrays;

public class InformaticaConfigServiceBeanFixtures {
    public static final String ARCHIVE_ATTRIBUTE_FILE_PATH = "/opt/aem/import/informatica/attributes/archive/";
    public static final String FAILED_ATTRIBUTE_FILE_PATH = "/opt/aem/import/informatica/attributes/failed/";

    public static InformaticaConfigServiceBean createConfigBean() {
        InformaticaConfigServiceBean informaticaConfigServiceBean = new InformaticaConfigServiceBean();
        informaticaConfigServiceBean.setAttributeArchiveFilePath(ARCHIVE_ATTRIBUTE_FILE_PATH);
        informaticaConfigServiceBean.setAttributeFailedFilePath(FAILED_ATTRIBUTE_FILE_PATH);
        return informaticaConfigServiceBean;
    }

}