package com.eaton.platform.integration.endeca.services;

import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.pojo.pdh.EndecaPdhResponse;

/**
 * The Interface EndecaService.
 */
public interface EndecaQRService {


    /**
     * Gets the Global Sku Page path - from EndecaQRServiceConfig.global_sku_page_path()
     *
     * @return the globalSkuPagePath
     */
    String getGlobalSkuPagePath();

    /**
     * Gets the Result as a EndecaPdhResponse
     *
     * @param endecaRequestBean request bean
     * @return the search result as EndecaPdhResponse
     */
    EndecaPdhResponse getEatonpdhlstcountries(EndecaServiceRequestBean endecaRequestBean);

    /**
     * Gets the Result as a EndecaPdhResponse
     *
     * @param catalogNumber string from selector in EndecaQRProcessingServlet
     * @return the constructed EndecaServiceRequestBean
     */
    EndecaServiceRequestBean createPdhEndecaRequestBean(String catalogNumber);
}
