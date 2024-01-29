package com.eaton.platform.integration.informatica.services;

import com.eaton.platform.integration.informatica.bean.InformaticaConfigServiceBean;

public interface InformaticaOSGIService {

	// informatica configuration properties - product family
	public static final String PRODUCT_FAMILY_FILE_NAME = "family.file.name";
	public static final String PRODUCT_FAMILY_FILE_PATH = "input.family.file.path";
	public static final String PRODUCT_FAMILY_ARCHIVE_FILE_PATH = "archive.family.file.path";
	public static final String PRODUCT_FAMILY_FAILED_FILE_PATH = "failed.family.file.path";
	public static final String PRODUCT_FAMILY_LANGUAGES = "family.languages";
	public static final String PRODUCT_FAMILY_NOTIFICATION_EMAIL_IDS = "family.notification.emailIds";
	// informatica configuration properties - global & taxonomy attribues
	public static final String TAXONOMY_ATTRIBUTE_FILE_NAME = "taxattr.file.name";
	public static final String GLOBAL_ATTRIBUTE_FILE_NAME = "globalattr.file.name";
	public static final String ATTRIBUTE_INPUT_FILE_PATH = "input.attribute.file.path";
	public static final String ATTRIBUTE_ARCHIVE_FILE_PATH = "archive.attribute.file.path";
	public static final String ATTRIBUTE_FAILED_FILE_PATH = "failed.attribute.file.path";
	public static final String ATTRIBUTES_NOTIFICATION_EMAIL_IDS = "attribute.notification.emailIds";

	/**
	 * Gets the config service bean.
	 * 
	 * @return the config service bean
	 */
	public InformaticaConfigServiceBean getConfigServiceBean();
}
