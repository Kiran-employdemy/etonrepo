package com.eaton.platform.integration.informatica.services;

import com.eaton.platform.core.exception.EatonApplicationException;
import com.eaton.platform.integration.informatica.bean.InformaticaConfigServiceBean;

import java.io.IOException;

/**
 * This is a Informatica Service to import Informatica XML data to AEM
 * Repository
 * 
 * @author TCS
 * 
 */
public interface InformaticaService {

	public String processInformaticaGlobalAttrData(
			InformaticaConfigServiceBean informaticaConfigServiceBean, String userID)
            throws EatonApplicationException, IOException;

	public String processInformaticaTaxAttrData(
			InformaticaConfigServiceBean informaticaConfigServiceBean, String userId)
			throws EatonApplicationException, IOException;

	public String processInformaticaProductFamilyData(
			InformaticaConfigServiceBean informaticaConfigServiceBean,String userId )
			throws EatonApplicationException, IOException;
}
