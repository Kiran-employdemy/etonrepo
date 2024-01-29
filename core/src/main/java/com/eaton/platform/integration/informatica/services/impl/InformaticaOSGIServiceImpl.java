package com.eaton.platform.integration.informatica.services.impl;

import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.informatica.bean.InformaticaConfigServiceBean;
import com.eaton.platform.integration.informatica.services.InformaticaOSGIService;
import com.eaton.platform.integration.informatica.services.config.InformaticaOSGIServiceConfig;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * <html> Description: This class is used to get the Informatica OSGI Service
 * configuration on the basis of request Type.. </html>
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 *
 */

@Designate(ocd = InformaticaOSGIServiceConfig.class)
@Component(service = InformaticaOSGIService.class,immediate = true,
property = {
		AEMConstants.SERVICE_DESCRIPTION + "Informatica Configration Service",
		AEMConstants.PROCESS_LABEL + "InformaticaOSGIServiceImpl",
		AEMConstants.SERVICE_VENDOR_EATON,
})
public class InformaticaOSGIServiceImpl implements InformaticaOSGIService {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(InformaticaOSGIServiceImpl.class);

	/** The service reference. */
	InformaticaConfigServiceBean informaticaConfigServiceBean;

	@Activate
	@Modified
	protected final void activate(final InformaticaOSGIServiceConfig config) {
		this.informaticaConfigServiceBean = new InformaticaConfigServiceBean();
		initializeConfigurations(config);
	}

	/**
	 * Deactivate.
	 */
	@Deactivate
	protected void deactivate() {
		this.informaticaConfigServiceBean = null;
	}

	private void initializeConfigurations(InformaticaOSGIServiceConfig config) {
		LOGGER.info("Entered into initializeConfigurations method");

		this.informaticaConfigServiceBean.setProductfamilyFileName(config.family_file_name());
		if(LOGGER.isInfoEnabled())
			LOGGER.info("PRODUCT_FAMILY_FILE_NAME{}::", config.family_file_name());

		this.informaticaConfigServiceBean.setProductfamilyInputFilePath(config.input_family_file_path());
		if(LOGGER.isInfoEnabled())
			LOGGER.info("PRODUCT_FAMILY_FILE_PATH{}::", config.input_family_file_path());

		this.informaticaConfigServiceBean.setProductfamilyArchiveFilePath(config.archive_family_file_path());
		if(LOGGER.isInfoEnabled())
			LOGGER.info("PRODUCT_FAMILY_ARCHIVE_FILE_PATH{}::", config.archive_family_file_path());

		this.informaticaConfigServiceBean.setProductfamilyFailedFilePath(config.failed_family_file_path());
		if(LOGGER.isInfoEnabled())
			LOGGER.info("PRODUCT_FAMILY_FAILED_FILE_PATH::{}", config.failed_family_file_path());


		if (null != config.family_languages()) {
			String[] prodfamilyLanguageArray = config.family_languages();
			List<String> productfamilyLanguageList = Arrays.asList(prodfamilyLanguageArray);
			this.informaticaConfigServiceBean.setProductfamilyLanguages(productfamilyLanguageList);
			if(LOGGER.isInfoEnabled())
            	LOGGER.info("PRODUCT_FAMILY_LANGUAGES{}::", productfamilyLanguageList);
		}
		if (null != config.family_notification_emailIds()) {
			String[] prodfamilyEmailIdArray = config.family_notification_emailIds();
			List<String> productfamilyEmailIdList = CommonUtil.getListFromStringArray(prodfamilyEmailIdArray);
			this.informaticaConfigServiceBean.setProductfamilyEmailIds(productfamilyEmailIdList);
			if(LOGGER.isInfoEnabled())
            	LOGGER.info("PRODUCT_FAMILY_EMAIL_LIST{}::", productfamilyEmailIdList);
		}

		this.informaticaConfigServiceBean.setGlobalAttFileName(config.globalattr_file_name());
		if(LOGGER.isInfoEnabled())
			LOGGER.info("GLOBAL_ATTRIBUTE_FILE_NAME{}::", config.globalattr_file_name());


		this.informaticaConfigServiceBean.setTaxonomyAttFileName(config.taxattr_file_name());
		if(LOGGER.isInfoEnabled())
			LOGGER.info("TAXONOMY_ATTRIBUTE_FILE_NAME{}::", config.taxattr_file_name());

		this.informaticaConfigServiceBean.setAttributeInputFilePath(config.input_attribute_file_path());
		if(LOGGER.isInfoEnabled())
			LOGGER.info("ATTRIBUTE_INPUT_FILE_PATH{}::", config.input_attribute_file_path());

		this.informaticaConfigServiceBean.setAttributeArchiveFilePath(config.archive_attribute_file_path());
		if(LOGGER.isInfoEnabled())
			LOGGER.info("ATTRIBUTE_ARCHIVE_FILE_PATH{}::", config.archive_attribute_file_path());

		this.informaticaConfigServiceBean.setAttributeFailedFilePath(config.failed_attribute_file_path());
		if(LOGGER.isInfoEnabled())
			LOGGER.info("ATTRIBUTE_FAILED_FILE_PATH{}::", config.failed_attribute_file_path());

		if (null!= config.attribute_notification_emailIds()) {
			String[] attributesEmailIdArray = config.attribute_notification_emailIds();
			List<String> attributeEmailIdList = CommonUtil.getListFromStringArray(attributesEmailIdArray);
			this.informaticaConfigServiceBean.setAttributeEmailIds(attributeEmailIdList);
			if(LOGGER.isInfoEnabled())
            	LOGGER.info("ATTRIBUTES_NOTIFICATION_EMAIL_IDS{}::", attributeEmailIdList);
		}
		LOGGER.info("Exit from initializeConfigurations method");
	}

	public InformaticaConfigServiceBean getConfigServiceBean() {
		return informaticaConfigServiceBean;
	}

}
