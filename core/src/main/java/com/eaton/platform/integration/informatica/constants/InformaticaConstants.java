package com.eaton.platform.integration.informatica.constants;

/**
 * This class defines all the common constants used by the Informatica Service
 * 
 * @author TCS
 * 
 */
public final class InformaticaConstants {

	/*
	 * **************** Error codes & Error Message Constants - Starts Here
	 * ****************
	 */
	public static final String PATH_ERROR_CODE = "ERR_INFORMATICA_007 ";
	public static final String PATH_ERROR_MSG = "Invalid Input Path ";
	public static final String FILE_NOT_FOUND_ERROR_CODE = "ERR_INFORMATICA_001 ";
	public static final String FILE_NOT_FOUND_ERROR_MSG = "File not found";
	public static final String INVALID_FILE_FORMAT_ERROR_CODE = "ERR_INFORMATICA_002 ";
	public static final String INVALID_FILE_FORMAT_ERROR_MSG = "Invalid format of file";
	public static final String PARSING_ISSUE_ERROR_CODE = "ERR_INFORMATICA_003 ";
	public static final String PARSING_ISSUE_ERROR_MSG = "Invalid entry or parsing issue within File";
	public static final String IMPORT_FILE_ERROR_CODE = "ERR_INFORMATICA_004 ";
	public static final String IMPORT_FILE_ERROR_MSG = "Not able to import data into AEM";
	public static final String ARCHIVAL_PATH_ERROR_CODE = "ERR_INFORMATICA_005 ";
	public static final String ARCHIVAL_PATH_ERROR_MSG = "Not able to move the processed file into archival or backlog path";
	public static final String EMAIL_NOTIFICATION_ERROR_CODE = "ERR_INFORMATICA_006 ";
	public static final String EMAIL_NOTIFICATION_ERROR_MSG = " Not able to send Email Notification";
	public static final String TECHNICAL_ISSUE_ERROR_CODE = "ERR_INFORMATICA_SYS-ERR ";
	public static final String TECHNICAL_ISSUE_ERROR_MSG = "Due to any technical issues or runtime errors";
	public static final String NO_XML_ERROR_CODE = "ERR_INFORMATICA_009 ";
	public static final String NO_XML_ERROR_MSG = "No XML files found to process at given Path";
	public static final String EXTENSION_ID_ERROR_CODE = "ERR_INFORMATICA_008 ";
	public static final String EXTENSION_ID_ERROR_MSG = "ExtensionID doesnot match under existing Inventory ID";

	/*
	 * **************** Error codes & Error Message Constants -Ends Here
	 * ****************
	 */

	public static final String ERR_MSG_STATUS_FILE = "Error while Creating status.txt file";
	public static final String INPUT_FILE_EMPTY = "XML file {} is empty. ";
	public static final String INVENTORY_ITEM_ID_ERROR_MSG = "Invenotry item ID having invalid value";
	public static final String ERR_CODE_IO = "ERR_IO_Exception";
	public static final String TOTAL_ENTRIES_ADDEDTO_AEM = " Total entries added/updated to AEM is : {}";
	public static final String NO_LANGUAGE_ENTERED = "No Language Entered for SelectedTag by author in Config Service";
	public static final String IMPORT_STARTED = "Started";
	public static final String PROCESS_ENDED = "Ended";
	public static final String FINAL_IDENTIFIER = ".xml";
	public static final String FORWARD_SLASH = "\\";
	public static final String UNDERSCORE = "_";
	public static final String HYPHEN_STRING = "-";
	public static final String BACKWARD_SLASH = "/";
	public static final String COMMA_STRING = ",";
	public static final String PIPE_STRING = "\\|\\|";
	/** The Constant DOT STRING. */
	public static final String DOT = ".";
	public static final String OSGI_NODE_TYPE = "sling:OsgiConfig";
	public static final String UNSTRUCTURED_FOLDER_TYPE = "nt:folder";
	 /** The Constant JCR_TITLE. */
    public static final String JCR_TITLE = "jcr:title";
	public static final String STATUS_FILE_TAX = "Taxonomy_Attributes_Import_Status";
	public static final String STATUS_FILE_GLOBAL = "Global_Attributes_Import_Status";
	public static final String STATUS_FILE_PDH = "Product_Families_Import_Status";
	public static final String STATUS_FILE_PATH = "status.txt file created at path {} ";
	public static final String NONE_NODE = "none";
	public static final String GLOBAL_ATTRIBUTE_AEM_LOCATION = "/var/commerce/pdh/global-attributes/";
	public static final String PRODUCT_FAMILY_AEM_LOCATION = "/var/commerce/pdh/product-family/";
	public static final String TAXONOMY_ATTRIBUTE_AEM_LOCATION = "/var/commerce/pdh/product-family/en_us/";
	public static final String XXPDH_PRD_FM_AG_LOCATION = "/";

	// XSD Path and file names
	public static final String INFORMATICA_XSD_LOCATION = "/content/dam/eaton/resources/informatica-xsd/";
	public static final String GLOBAL_ATTR_XSD_FILE = "Global_Attr.xsd";
	public static final String PRODUCT_FAMILY_XSD_FILE = "Product_Family.xsd";
	public static final String TAX_ATTR_XSD_FILE = "Taxonomy_Attr.xsd";
	public static final String EMAIL_TEMPLATE_PATH = "/etc/notification/email/html/com.eaton.informatica.email/en-us.html";

	public static final String SENDER_EMAIL_ADDRESS = "Informatica@eaton.com";
	public static final String SENDER_NAME = "Informatica Team";
	public static final String RECIPIENT_NAME = "Eaton Team";
	public static final String MAIL_SUBJECT = " File import Status";
	public static final String NO_DATA_TO_CHANGE = "No incremental Data";
	public static final String EXCEPTION_OCCURED = "Exception Occured ";
	public static final String SUCCESS_MSG = "All Entries Of File imported Successfully";
	public static final String SUCCESS_CODE = "SUCCESS_INFORMATICA ";
	public static final String SUBJECT = "subject";

	// PRIVATE //
	private InformaticaConstants() {
		throw new AssertionError();
	}
}
