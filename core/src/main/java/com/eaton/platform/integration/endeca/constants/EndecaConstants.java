package com.eaton.platform.integration.endeca.constants;

public final class EndecaConstants {
	//Private Constructor
	private EndecaConstants() {

	}

	// endeca configuration property keys
	public static final String RUNMODE_ENDECA = "endeca";
	public static final String ESP_SERVICE_URL = "esp.service.url";
	public static final String SKU_DETAILS_APP_NAME = "sku.application.name";
	public static final String PRODUCT_COMPATIBILITY_APP_NAME = "productcompatibility.application.name";
	public static final String ESP_APP_KEY = "esp.service.application.key";
	public static final String ENDECAUSERAGENTVALUE = "endecaUserAgentValue";
	public static final String WEB_SERVICE_STUB_RESPONSE_DIR = "webservice.stub.dir";
	public static final String ENABLE_WEB_SERVICE_STUB_RESPONSE = "webservice.stub.enabled";
	public static final String PRODUCTS_TAB_ID = "products.tab.id";
	public static final String NEWS_TAB_ID = "news.tab.id";
	public static final String RESOURCES_TAB_ID = "resources.tab.id";
	public static final String SERVICES_TAB_ID = "services.tab.id";
	public static final String PRODUCT_FAMILY_APP_NAME = "productfamily.application.name";
	public static final String SUBCATEGORY_APP_NAME = "subcategory.application.name";
	public static final String SITESEARCH_APP_NAME = "sitesearch.application.name";
	public static final String ENDECA_URL_FOR_PDH1_PDH2 = "endeca.url.pdh1andpdh2";
	public static final String SUBMITTAL_BUILDER_APP_NAME = "submitbuilder.application.name";
	//VG Selector
	public static final String CLUTCHSELECTOR_APP_NAME = "clutchselector.application.name";
	public static final String TORQUESELECTOR_APP_NAME = "torqueselector.application.name";
	public static final String VG_SELECTOR_ENDECA_URL = "vgselector.endeca.url";
	public static final String TORQUE_APPLICATION_VALUE = "etnvgseltorque";
	public static final String CLUTCH_APPLICATION_VALUE = "etnvgselclutch";
	public static final String SECURE_STRING = "Secure";
	public static final String SECURE_STRING_LOWER_CASE = "secure";
	public static final String SECURE_FILE_TYPE_AND_SIZE = "fileTypeAndSize";

	public static final String SEARCH_STRING = "search";

	public static final String SUCCESS_STRING = "success";
	public static final String FAIL_STRING = "fail";

	public static final String DOCUMENT_STRING = "document";
	public static final String CONTENT_STRING = "content";
	public static final String RESPONSE_STRING = "response";
	public static final String SEARCH_RESULTS_STRING = "searchResults";
	public static final String SITE_SEARCH_RESULTS_STRING = "siteSearchResults";
	public static final String REQUEST_STRING = "request";
	public static final String FILTERS_STRING = "filters";
	public static final String SEARCH_TERMS_STRING = "searchTerms";
	public static final String RETURN_FACETS_FOR = "ReturnFacetsFor";
	public static final String FILTER_FACETS = "Facets";
	public static final String SORT_FACET = "SortFacet";
	public static final String GET_ALT_SKU = "Get_Alt_SKUs";
	public static final String SORT_BY = "SortBy";
	public static final String SUBCATEGORY_STRING = "SubCategory";
	public static final String FAMILY_STRING = "Family";
	public static final String SKU_STRING = "SKU";
	public static final String TYPE_AHEAD_STRING = "TypeAhead-SiteSearch";
	public static final String SITE_SEARCH_STRING = "SiteSearch";
	public static final String SYS_ERR_STRING = "SysErr";
	public static final String CONTENTHUB_STRING = "ContentHub";
	public static final String AUTOCORRECT = "autoCorrect";

	public static final String TOTAL_COUNT_STRING = "totalCount";
	public static final String PRODUCTS_STRING = "product";
	public static final String NEWS_STRING = "news-and-insights";
	public static final String RESOURCES_STRING = "resources";
	public static final String SERVICES_STRING = "services";
	public static final String ARTICLE_STRING = "article";
	public static final String NAME_STRING = "name";
	public static final String VALUE_STRING = "value";
	public static final String ID_STRING = "id";
	public static final String FILTER_NAME_STRING = "filterName";
	public static final String FILTER_VALUE_STRING = "filterValue";
	public static final String EATON_SECURE_FACET_GROUP_LABEL = "eaton-secure_attributes";
	public static final String SECURE_ONLY_FILTER_VALUE = "secure-only";

	public static final String HYPHEN = "-";


	//SubCategory
	public static final String PRODUCT_NAME_STRING = "product_name";
	public static final String PRODUCT_IMAGE_STRING = "product_image";
	public static final String SUB_CATEGORY_NAME_STRING = "sub-category_name";
	public static final String URL_STRING = "URL";
	public static final String PRODUCT_STATUS = "Status";

	//LearnPage
	public static final String DEFAULT_COMPANY_NAME = "Eaton";
	public static final String EATON_PAGE_TYPE_FILTER = "PageType_Filter";
	/*Added to extract Product Type from Tags in Page properties for Digital Catalogue*/
	public static final String PRODUCT_TYPE = "product-type";
	/**
	 * The Constant partnerBadge for Digital Product attributes
	 */
	public static final String PARTNER_BADGE = "partner-badge";
	/**
	 * The Constant companyName  for Digital Product attributes
	 */
	public static final String COMPANY_NAME = "company-name";

	//Product Family
	public static final String CATALOG_NUMBER_STRING = "Catalog_number";
	public static final String FAMILY_NAME_STRING = "Family_Name";
	public static final String MKTG_DESC_STRING = "Mktg_Desc";
	public static final String DISPLAY_IMG_STRING = "IMGS";
	public static final String LONG_DESC_STRING = "Long_Desc";
	public static final String MODEL_CODE_STRING = "Model_Code";
	public static final String PRIMARY_IMAGE_STRING = "Primary_Image";
	public static final String SKU_CARD_PARAMETERS_STRING = "SKUCardParameters";
	public static final String SKU_CARD_PARAMETER_1_STRING = "SKU_Attribute_1";
	public static final String SKU_CARD_PARAMETER_2_STRING = "SKU_Attribute_2";
	public static final String SKU_CARD_PARAMETER_3_STRING = "SKU_Attribute_3";
	public static final String FILTER_FAMILY_IDS = "Family_Ids";
	public static final String SKUS = "skus";
	public static final String PIPE_STRING = "\\|\\|";
	public static final String RUNTIME_GRAPH_URL = "http://eg.eaton.com/ups-battery-runtime/";
	public static final String RUNTIME_GRAPH = "Runtime_Graph";
	public static final String LANGUAGE = "language";

	//SKU Details
	public static final String INVENTORY_ID_STRING = "Inventory_id";
	public static final String EXTENSION_ID_STRING = "Extension_id";
	public static final String BRAND_STRING = "Brand";
	public static final String SUBBRAND_STRING = "SubBrand";
	public static final String TRADE_NAME_STRING = "Trade_Name";
	public static final String GLOBAL_ATTRS_STRING = "Global_Attrs";
	public static final String TAXONOMY_ATTRS_STRING = "Taxonomy_Attrs";
	public static final String SKU_IMGS_STRING = "Images";
	public static final String DOCUMENTS_STRING = "Documents";
	public static final String UPSELL_STRING = "UpSell";
	public static final String CROSS_SELL_STRING = "CrossSell";
	public static final String CID_STRING = "CID";
	public static final String STATUS_STRING = "Status";
	public static final String REPLACEMENT_STRING = "Replacement";
	public static final String IGNORE = "ignore";

	//Site Search
	public static final String TITLE_STRING = "Title";
	public static final String DESCRIPTION_STRING = "Description";
	public static final String DESCRIPTION_STRING_LOWER = "description";
	public static final String IMAGE_STRING = "image";
	public static final String PUBLISH_DATE_STRING = "Publish_Date";
	public static final String PUBLISH_DATE_CAMEL_STRING = "publishDate";
	public static final String EPOCH_PUBLISH_DATE_STRING = "EpochPubDate";
	public static final String FILE_TYPE_STRING = "File_Type";
	public static final String FILE_SIZE_STRING = "File_Size";
	public static final String MIDDLE_TAB_NAME_STRING = "Middle_Tab_Name";
	public static final String MIDDLE_TAB_URL_STRING = "Middle_Tab_URL";
	public static final String CONTENT_TYPE_STRING = "Content_Type";
	public static final String CONTENT_TYPE_FACET = "content-type";
	public static final String DOLLAR_SYMBOL = "$";
	public static final String SEARCH_PRODUCT_STATUS = "product-attributes_status";
	public static final String LANGUAGE_STRING = "Language";
	public static final String ECOMMERCE_PAGE_TYPE = "page_Type";
	public static final String BUY_PAGE_REFERENCE = "BuyPageReference";
	public static final String DC_FORMAT_STRING = "dc_format";

	public static final String EATON_SHA = "eaton-sha";
	public static final String EATON_ECCN = "eaton-eccn";

	public static final String RENDITION_ONE_STRING = "Rendition1";
	public static final String RENDITION_TWO_STRING = "Rendition2";
	public static final String ALT_SKUS = "Alt_SKU";

	//JSON File Names
	public static final String SUBCATEGORY_RESPONSE_FILENAME = "/SubCategoryResponse.json";
	public static final String PRODUCTFAMILY_RESPONSE_FILENAME = "/ProductFamilyResponse.json";
	public static final String SKU_RESPONSE_FILENAME = "/SKUResponse.json";
	public static final String TYPE_AHEAD_RESPONSE_FILENAME = "/TypeAheadResponse.json";
	public static final String SITE_SEARCH_RESPONSE_FILENAME = "/SiteSearchResponse.json";

	//VG Selector file names
	public static final String CLUTCH_SELECTOR_RESPONSE_FILENAME = "/ClutchSelectorResponse.json";
	public static final String TORQUE_SELECTOR_RESPONSE_FILENAME = "/TorqueSelectorResponse.json";

	//Submittal Builder file name
	public static final String SUBMITTAL_BUILDER_RESPONSE_FILENAME = "/SubmittalBuilderEndecaResponse.json";

	//Content Hub file name
	public static final String CONTENT_HUB_RESPONSE_FILENAME = "/ContentHubResponse.json";


	//Error codes & Corresponding Error Messages

	public static final String ERR_ENDECA_FAIL_STRING = "ERR_ENDECA_FAIL";

	public static final String ERR_ENDECA_001_STRING = "ERR_ENDECA_001";
	public static final String ERR_ENDECA_006_STRING = "ERR_ENDECA_006";
	public static final String ERR_ENDECA_011_STRING = "ERR_ENDECA_011";
	public static final String ERR_ENDECA_023_STRING = "ERR_ENDECA_023";
	public static final String ERR_ENDECA_026_STRING = "ERR_ENDECA_026";
	public static final String INVALID_SEARCH_APPLICATION_MSG = "INVALID SEARCH APPLICATION";

	public static final String ERR_ENDECA_002_STRING = "ERR_ENDECA_002";
	public static final String ERR_ENDECA_007_STRING = "ERR_ENDECA_007";
	public static final String ERR_ENDECA_012_STRING = "ERR_ENDECA_012";
	public static final String ERR_ENDECA_024_STRING = "ERR_ENDECA_024";
	public static final String ERR_ENDECA_027_STRING = "ERR_ENDECA_027";
	public static final String NO_SEARCH_APPLICATIONKEY_MSG = "SEARCH APPLICATION KEY IS MISSING";
	public static final String INVALID_SEARCH_APPLICATIONKEY_MSG = "INVALID SEARCH APPLICATION KEY";

	public static final String ERR_ENDECA_003_STRING = "ERR_ENDECA_003";
	public static final String ERR_ENDECA_008_STRING = "ERR_ENDECA_008";
	public static final String ERR_ENDECA_013_STRING = "ERR_ENDECA_013";
	public static final String ERR_ENDECA_025_STRING = "ERR_ENDECA_025";
	public static final String ERR_ENDECA_028_STRING = "ERR_ENDECA_028";
	public static final String NO_FUNCTION_MSG = "FUNCTION IS REQUIRED";
	public static final String INVALID_FUNCTION_MSG = "INVALID FUNCTION";

	public static final String ERR_ENDECA_004_STRING = "ERR_ENDECA_004";
	public static final String ERR_ENDECA_009_STRING = "ERR_ENDECA_009";
	public static final String ERR_ENDECA_014_STRING = "ERR_ENDECA_014";
	public static final String ERR_ENDECA_039_STRING = "ERR_ENDECA_039";
	public static final String ERR_ENDECA_043_STRING = "ERR_ENDECA_043";
	public static final String NO_SEARCH_TERMS_MSG = "SEARCHTERMS IS REQUIRED";

	public static final String ERR_ENDECA_033_STRING = "ERR_ENDECA_033";
	public static final String ERR_ENDECA_035_STRING = "ERR_ENDECA_035";
	public static final String ERR_ENDECA_037_STRING = "ERR_ENDECA_037";
	public static final String ERR_ENDECA_040_STRING = "ERR_ENDECA_040";
	public static final String ERR_ENDECA_044_STRING = "ERR_ENDECA_044";
	public static final String NO_LANGUAGE_VALUE = "LANGUAGE IS MISSING";

	public static final String ERR_ENDECA_005_STRING = "ERR_ENDECA_005";
	public static final String ERR_ENDECA_010_STRING = "ERR_ENDECA_010";
	public static final String ERR_ENDECA_015_STRING = "ERR_ENDECA_015";
	public static final String ERR_ENDECA_041_STRING = "ERR_ENDECA_041";
	public static final String ERR_ENDECA_045_STRING = "ERR_ENDECA_045";
	public static final String NO_COUNTRY_MSG = "COUNTRY IS MISSING";
	public static final String NO_MAXRELATIONSKU_MSG = "MAXRELATIONSKU IS MISSING";
	public static final String NO_RETURNFACETS_MSG = "RETURNFACETSFOR IS MISSING";
	public static final String NO_SKUCARDPARAMETERS_MSG = "SKUCARDPARAMETERS IS MISSING";
	public static final String NO_FACETS_MSG = "FACETS IS MISSING";
	public static final String NO_SORTBY_MSG = "SORTBY IS MISSING";
	public static final String NO_AUTOCORRECT_MSG = "AUTOCORRECT IS MISSING";
	public static final String INVALID_FILTER_VALUE_MSG = "INVALID FILTER VALUE";

	public static final String ERR_ENDECA_034_STRING = "ERR_ENDECA_034";
	public static final String ERR_ENDECA_036_STRING = "ERR_ENDECA_036";
	public static final String ERR_ENDECA_038_STRING = "ERR_ENDECA_038";
	public static final String ERR_ENDECA_042_STRING = "ERR_ENDECA_042";
	public static final String ERR_ENDECA_046_STRING = "ERR_ENDECA_046";
	public static final String NO_SEARCH_APPLICATION_MSG = "SEARCH APPLICATION IS MISSING";

	public static final String ERR_ENDECA_016_STRING = "ERR_ENDECA_016";
	public static final String ERR_ENDECA_017_STRING = "ERR_ENDECA_017";
	public static final String ERR_ENDECA_018_STRING = "ERR_ENDECA_018";
	public static final String ERR_ENDECA_029_STRING = "ERR_ENDECA_029";
	public static final String ERR_ENDECA_030_STRING = "ERR_ENDECA_030";
	public static final String CONNECTIVITY_ERROR_MSG = "Connectivity issue with ESP service";

	public static final String ERR_ENDECA_019_STRING = "ERR_ENDECA_019";
	public static final String ERR_ENDECA_020_STRING = "ERR_ENDECA_020";
	public static final String ERR_ENDECA_021_STRING = "ERR_ENDECA_021";
	public static final String ERR_ENDECA_031_STRING = "ERR_ENDECA_031";
	public static final String ERR_ENDECA_032_STRING = "ERR_ENDECA_032";
	public static final String INVALID_ENTRY_MSG = "Invalid entry or parsing issue within JSON response";


	public static final String ERR_ENDECA_022_STRING = "ERR_ENDECA_022";
	public static final String TECHNICAL_ISSUE_ERROR_MSG = "Due to any technical issues or runtime errors";

	public static final String FILE_NOT_FOUND_ERROR_MSG = "File Has not been found";

	public static final String NO_MATCHING_SEARCH_RESULTS = "No matching search results found for the search terms:";

	//Facet Validation messages
	public static final String FACET_VALUE_NULL_MSG = "Facet Value label cannot be null for the facet group:";
	public static final String FACET_GROUP_NULL_MSG = "FacetGroup or List cannot be null";
	public static final String FACET_GROUP_LABEL_NULL_MSG = "FacetGroupLabel cannot be null for the ID:";
	public static final String FACET_VALUE_ID_NULL_MSG = "FacetValueID cannot be null for the label:";

	//ESP Service SOAP Body
	public static final String ESP_SERVICE_DEV_URL = "http://searchv1-dev.tcc.etn.com:8080/EatonSearchApp/EatonSearchWS?WSDL";
	public static final String SOAP_ENVELOPE_PART_START = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.generic.eaton.com/\"> <soapenv:Header/>";
	public static final String SOAP_BODY_PART_START = "<soapenv:Body> <ws:doProcess> <arg0>";
	public static final String SOAP_BODY_PART_END = "</arg0> </ws:doProcess> </soapenv:Body>";
	public static final String SOAP_ENVELOPE_PART_END = "</soapenv:Envelope>";
	public static final String BLANK_STRING = "_blank";

	//Clutch Selector
	public static final String NO_RESULTS = "No results are returned";
	public static final String CLUTCH_STRING = "CLUTCH";

	public static final String PRODUCT_NAME = "product_name";
	public static final String CLUTCH_SIZE = "Clutch Size";
	public static final String DAMPER_SPRING_COUNT = "Damper Spring Count";
	public static final String TORQUE_CAPACITY = "Torque  Capacity";
	public static final String INPUT_SHAFT_SIZE = "Input Shaft Type";
	public static final String PRE_DAMPER = "Pre Damper";
	public static final String QLTY_DESIGNATION = "Quality_Designation";

	public static final String IMAGE_ONE = "Image1";
	public static final String IMAGE_TWO = "Image2";

	//Clutch Error codes
	public static final String ERR_ENDECA_059_STRING = "ERR_ENDECA_059";
	public static final String ERR_ENDECA_047_STRING = "ERR_ENDECA_047";
	public static final String ERR_ENDECA_048_STRING = "ERR_ENDECA_048";
	public static final String ERR_ENDECA_049_STRING = "ERR_ENDECA_049";
	public static final String ERR_ENDECA_050_STRING = "ERR_ENDECA_050";
	public static final String ERR_ENDECA_051_STRING = "ERR_ENDECA_051";
	public static final String ERR_ENDECA_057_STRING = "ERR_ENDECA_057";

	//Torque Selector
	public static final String AXLE_STRING = "Axle Description";
	public static final String SPLINE_COUNT_STRING = "Spline Count";
	public static final String TORQUE_STRING = "TORQUE";

	//Torque Error codes
	public static final String ERR_ENDECA_052_STRING = "ERR_ENDECA_052";
	public static final String ERR_ENDECA_053_STRING = "ERR_ENDECA_053";
	public static final String ERR_ENDECA_054_STRING = "ERR_ENDECA_054";
	public static final String ERR_ENDECA_055_STRING = "ERR_ENDECA_055";
	public static final String ERR_ENDECA_056_STRING = "ERR_ENDECA_056";
	public static final String ERR_ENDECA_058_STRING = "ERR_ENDECA_058";
	public static final String ERR_ENDECA_060_STRING = "ERR_ENDECA_060";

	public static final String ERR_ENDECA_061_STRING = "ERR_ENDECA_061";
	public static final String ERR_ENDECA_062_STRING = "ERR_ENDECA_062";
	//Submittal builder constants
	public static final String STATUS = "status";
	public static final String RESULTS = "results";
	public static final String BINNING = "binning";
	public static final String BINNING_SET = "binningSet";
	public static final String TITLE = "title";
	public static final String LABEL = "label";
	public static final String BIN = "bin";
	public static final String VALUES = "values";
	public static final String BSID = "bsId";
	public static final String COUNT = "count";
	public static final String NDOCS = "ndocs";
	public static final String URL = "url";
	public static final String PROPERTIES = "properties";
	public static final String SIZE = "size";
	public static final String DAM_SIZE = "dam_size";
	public static final String APPLICATION_ID = "ApplicationId";

	//Flags for the QR E-pass.
	public static final String SERIAL_FLAG = "SerialFlag";
	public static final String SERIAL_AUTH_FLAG = "SerialAuthFlag";

	//Flags for the report issue email 
	public static final String REP_EMAIL = "RepEmail";

	public static final String I18N_LABEL_PREFIX = "EndecaLabel.";

	//Cross reference constants
	public static final String BEST_MATCH_RESULTS = "bestMatchResults";
	public static final String RESULT_ID = "resultID";
	public static final String PARTIAL_MATCH_RESULTS = "partialMatchResults";
	public static final String FACETS_STRING = "facets";
	public static final String ACTIVE_STRING = "active";
	public static final String FACETS_STRING_WITH_F = "Facets";
	public static final String ASCENDING_ASC = "ASC";

	// Enedea Search Secure Attribute Constants

	public static final String SECURE_APP_ID = "sec-eatoncomtypeahead";
	public static final String NON_SECURE_APP_ID = "eatoncomtypeahead";
	public static final String PRODUCT_COUNTRY_LIST = "eatonpdhlstcountries";
	public static final String PRODUCT_GRID_APP_ID = "eatonpdh3";
	public static final String PRODUCT_GRID_AUTH_APP_ID = "eatonauthpdh3";
	public static final String SECURE_EATON_SEARCH = "sec-eatonsitesearch";
	public static final String SECURE_ASSET_EATON_SEARCH = "sec-asseteatonsitesearch";
	public static final String SEARCH_TYPE_TYPEAHEAD = "typeahead";
	public static final String SEARCH_TYPE_RESULTS = "search-results";
	public static final String SEARCH_TYPE_ASSET = "search-results-asset";

	public static final String SECURE_ATTR_SECURE_ONLY = "SEC-SecureOnly";
	public static final String SECURE_BY_PASS = "SEC-Bypass-Flag";

	public static final String COOKIE_NAME_SECRET_KEY = "aem-secret-key";

	public static final String COUNTRY_CONSTANT = "Country";
	public static final String FILE_TYPES = "file_Types";
	public static final String SEARCH_TERM = "searchTerm";
	public static final String LOAD_MORE_OFFSET = "loadMoreOffset";
	public static final String DOWNLOAD_ENABLED = "downloadEnabled";
	public static final String SECURE_ONLY = "secureOnly";
	public static final String END_DATE = "endDate";
	public static final String START_DATE = "startDate";
	public static final String PUBLICATION_DATE = "PublicationDate";
	public static final String EPOCH_PUBLICATION_DATE = "epochPublishDate";
	public static final String DEFAULT_IMAGE_DAM_PATH_ROOT = "/content/dam/eaton/resources/advanced-search/";
	public static final String DATE_FORMAT = "dd-MM-yyyy";
	public static final String FILE_TYPE = "fileType";
	public static final String FILE_SIZE = "fileSize";
	public static final String FILE_TYPE_AND_SIZE = "fileTypeAndSize";
	public static final String JCRCONTENT_RENDITIONS_ORIGINAL = "/jcr:content/renditions/original";
	public static final String SEARCH_TERM_IGNORE = "##IGNORE##";
	public static final String EMPTY_FILTER_VALUE = "##EMPTY##";
	public static final int TEN = 10;
	public static final int SIX = 6;
	public static final int THREE = 3;
	public static final String ZERO = "0";
	public static final String LIMIT_COUNT_TWO_THOUSAND = "2000";
	public static final String PUBLICATION_DATE_DESCENDING = "pub_date_desc";
	public static final String DC_FORMAT = "dcFormat";

	/**
	 * Category Tags filter Constants
	 */
	public static final String ETN_RESOURCE_TAG = "eaton:resources";
	public static final String ETN_PRODUCT_TAG = "eaton:product-taxonomy";
	public static final String CATEGORY_TAGS_FILTER = "categoryTags";
	public static final String PRODUCT_GRID_DESCRIPTION = "product-grid-description";

	public static final String FACET_GROUP_LIST = "facetGroupList";
	public static final String FACET_GROUP_VALUE_LIST = "facetValueList";
	public static final String FACET_VALUE_DOCS = "facetValueDocs";
	public static final String FACET_SELECTED = "facetSelected";
	public static final String FACET_VALUE_CHECKED = "checked";

	/**
	 * ENDECA Adv Search JSON Constants
	 */
	public static final String ENDECA_ADV_FACET_GROUP_ID = "facetGroupId";
	public static final String ENDECA_ADV_FACET_GRID_FACET = "gridFacet";
	public static final String ENDECA_ADV_FACET_SINGLE_FACET = "singleFacetEnabled";
	public static final String ENDECA_ADV_FACET_SEARCH_ENABLED = "facetSearchEnabled";
	public static final String SINGLE_FACET_ENABLED = "singleFacetEnabled";
	public static final String SHOW_AS_GRID = "showAsGrid";
	public static final String FACET_SEARCH_ENABLED = "facetSearchEnabled";
	public static final String HIDE = "hide";
	public static final String CHECKBOX = "checkbox";


	/* DT project changes */
	public static final String CATALOG_NO = "Catalog_Nos";
	public static final String RUNTIME_GRAPH_ID = "Runtime_graph";
	public static final String RUNTIME_GRAPH_URL_LABEL = "Runtime graph URL";

	/* Adv Constants */
	public static final String COLON = ":";

	public static final String PUBLISH_DATE = "publish-date";

	public static final String PRODUCT_TYPE_VALUE = "dim-product-attributes_product-type-values";

}
