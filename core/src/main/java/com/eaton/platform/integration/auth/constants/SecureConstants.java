package com.eaton.platform.integration.auth.constants;

/**
 * Secure Constants Class
 */
public final class SecureConstants {

    //properties
    public static final String COOKIE_NAME = "etn_country_selector_redirect_url";
    public static final String USER_ID = "userId";

    // SECURE REGEX Constants
    public static final String GENERIC_CONTENT_SECURE_REGEX = "(^/content/eaton/.*/secure/.*|^/content/dam/eaton/.*/secure/.*|^/content/dam/eaton/secure/.*|^/content/eaton/.*/digital/secure/.*)";
    public static final String SECURE_DIGITAL_CONTENT_PATH_SUFFIX = "/digital/secure";
    public static final String SECURE_USER_IDENTIFICATION_FILTER_REGEX = "(^/eaton/content/search/predective-search|^/content/eaton/.*/site-search)";
    public static final String SECURE_ENDECA_REDIRECT_REQUEST_FILTER_REGEX = "(^/content/eaton/.*|^/content/dam/.*/secure/.*)";

    public static final String VALUE = "value";
    public static final String FALSE = "false";

    public static final String JCR_CONTENT_METADATA = "jcr:content/metadata";
    public static final String JCR_CONTENT_PAGE = "/jcr:content";

    public static final String YES = "YES";
    public static final String TRUE = "true";
    public static final String NOT = "not";

    public static final String NO = "NO";

    public static final String SECURE_REDIRECT_URL = "/content/eaton/us/en-us/401.html";
    public static final String SECURE_LOGIN_PROFILE = "eaton_profile";
    public static final String ROLLMAPPER_JSON = "/content/dam/eaton/resources/secure/role-mapping.json";
    public static final String ACTUAL_KEY_VALUE = "tagpath";

    //Constants for Secure Link CTA
    public static final String SECURE_ATTRIBUTES_TEST_JSON = "SecurePageMock.json";
    public static final String PROP_PRODUCT_CATEGORIES_TAG = "productCategories";
    public static final String PROP_ACCOUNT_TYPE_TAG = "accountType";
    public static final String PROP_APPLICATION_ACCESS_TAG = "applicationAccess";
    public static final String PROP_TIER_LEVEL_TAG = "tierLevel";
    public static final String PROP_PARTNER_PROGRAMME_TYPE_TAG = "partnerProgrammeType";
    public static final String XMP_EATON_PROP_TIER_LEVEL_TAG = "xmp:eaton-tier-level";
    public static final String XMP_EATON_PROP_PARTNER_PROGRAMME_TYPE_TAG = "xmp:eaton-partner-program-type";
    public static final String XMP_EATON_PROP_PARTNER_PROGRAMME_AND_TIER_LEVEL_TYPE_TAG = "xmp:eaton-partner-program-and-tier-level";
    public static final String PROP_PARTNER_PROGRAMME_AND_TIER_LEVEL_TYPE_TAG = "partnerProgramAndTierLevel";
    public static final String SECURE_PARTNER_PROGRAMME_TYPE = "eaton-secure:partner-programme-type";

    public static final String ROOT_CONTENT_DAM_EATON = "/content/dam/eaton";
    public static final String METADATA_SECURE_ASSET = "secureAsset";
    public static final String METADATA_ASSET_PUB_DATE = "assetPublicationDate";
    public static final String METADATA_ASSET_LAST_MODIFIED_DATE = "jcr:lastModified";
    public static final String PROP_PATH = "path";
    public static final String PROPERTY_UNDERSCORE = "_property";
    public static final String PROPERTY_OPERATION = ".operation";
    public static final String DOT_VALUE = ".value";
    public static final String RELATIVE_DATE_RANGE = "_relativedaterange";
    public static final String PROPERTY_DOT = "_property.";
    public static final String LOWER_BOUND = ".lowerBound";
    public static final String UPPER_BOUND = ".upperBound";
    public static final String UPPER_OPERATION_VALUE = "<=";

    public static final String THIRTY_DAYS = "-30d";
    public static final String ONE_SECOND = "-1s";
    public static final String QUERY_GROUP = "group.";
    public static final String QUERY_UNDERSCORE_GROUP = "_group.";
    public static final String QUERY_PROPERTY = "property";
    public static final String QUERY_UNDERSCORE_VALUE = "_value";
    public static final String QUERY_AND = ".and";
    public static final String QUERY_OR = ".or";
    public static final String QUERY_OPERATION = "operation";
    public static final String QUERY_RELATIVEDATERANGE = "_relativedaterange.";
    public static final String QUERY_P = "p";
    public static final String RESOURCES_TAG_VALUE = "eaton:search-tabs/content-type/resources";
    public static final String QUERY_PROPERTY_WITH_DOT = "property.";

    public static final String WHATS_NEW_TEST_JSON = "WhatsNewResponse.json";
    public static final String REQUEST_PARAM_CONFIG_URL = "configuredUrl";

    public static final String IS_EXTERNAL = "isExternal";
    public static final String LINK_PROPERTY = "link";
    // Resource list component constants
    public static final String CONTENT_ROOT_SECURE_IS_ASSET_SECURE = "secureAsset";
    public static final String JCR_CONTENT_SECURE_METADATA = "jcr:content/metadata/secureAsset";
    public static final String JCR_CONTENT_ACCOUNTTYPE_METADATA = "jcr:content/metadata/accountType";
    public static final String JCR_CONTENT_PRODCAT_METADATA = "jcr:content/metadata/productCategories";
    public static final String JCR_CONTENT_APPACC_METADATA = "jcr:content/metadata/applicationAccess";
    public static final String JCR_CONTENT_COUNTRIES_METADATA = "jcr:content/metadata/countries";
    public static final String GROUP1_PROPERTY0 = "group.1_group.0_property";
    public static final String GROUP1_PROPERTY0_OPERATION = "group.1_group.0_property.operation";
    public static final String GROUP1_PROPERTY0_VALUE = "group.1_group.0_property.value";
    public static final String GROUP1_PROPERTY1 = "group.1_group.1_property";
    public static final String GROUP1_PROPERTY1_VALUE = "group.1_group.1_property.value";
    public static final String GROUP2_PROPERTY0 = "group.2_group.0_property";
    public static final String GROUP2_PROPERTY0_VALUE = "group.2_group.0_property.value";
    public static final String GROUP2_PROPERTY1 = "group.2_group.1_property";
    public static final String GROUP2_PROPERTY1_VALUE = "group.2_group.1_property.value";
    public static final String GROUP2_PROPERTY2 = "group.2_group.2_property";
    public static final String GROUP2_PROPERTY3 = "group.2_group.3_property";
    public static final String GROUP2_PROPERTY4 = "group.2_group.4_property";
    public static final String GROUP3_PROPERTY0 = "group.3_group.0_property";
    public static final String GROUP3_PROPERTY0_VALUE = "group.3_group.0_property.value";
    public static final String GROUP3_PROPERTY1 = "group.3_group.1_property";
    public static final String GROUP3_PROPERTY1_OPERATION = "group.3_group.1_property.operation";
    public static final String GROUP3_PROPERTY1_VALUE = "group.3_group.1_property.value";
    public static final String GROUP3_PROPERTY2 = "group.3_group.2_property";
    public static final String GROUP3_PROPERTY2_OPERATION = "group.3_group.2_property.operation";
    public static final String GROUP3_PROPERTY2_VALUE = "group.3_group.2_property.value";
    public static final String GROUP3_PROPERTY3 = "group.3_group.3_property";
    public static final String GROUP3_PROPERTY3_OPERATION = "group.3_group.3_property.operation";
    public static final String GROUP3_PROPERTY3_VALUE = "group.3_group.3_property.value";
    public static final String GROUP3_PROPERTY4 = "group.3_group.4_property";
    public static final String GROUP3_PROPERTY4_OPERATION = "group.3_group.4_property.operation";
    public static final String GROUP3_PROPERTY4_VALUE = "group.3_group.4_property.value";
    public static final String GROUP_OR = "group.p.or";
    public static final String GROUP1_OR = "group.1_group.p.or";
    public static final String EXISTS = "exists";
    public static final String SECUREPAGE = "securePage";

    //Constants for Secure Link CTA
    public static final String SLASH = "/";
    public static final String ROLE_MAPPER_ACCOUNT_KEY = "accounttype";
    public static final String ROLE_MAPPER_APP_KEY = "applicationaccess";
    public static final String ROLE_MAPPER_OKTAGROUP_KEY="oktagroups";
    public static final String ROLE_MAPPER_PROD_KEY = "productcategory";
    public static final String ROLE_MAPPER_TIER_KEY = "tierlevel";
    public static final String ROLE_MAPPER_PROGRAMME_TYPE_KEY = "partnerProgrammeType";
    public static final String ROLE_MAPPER_PROGRAM_TYPE_AND_TIERS_KEY = "partnerProgramTypeAndTier";

    public static final String ACCOUNT_TAG = "accountTypeTags";
    public static final String APP_TAG = "appAccessTags";
    public static final String BUSS_TAG = "businessUnitTags";
    public static final String PROD_TAG = "productCategoriesTags";
    public static final String LOCALE_TAG = "localeTags";
    public static final String TIER_TAG="tierLevelTags";
    public static final String PROG_TAG="partnerProgrammeTypeTags";

    public static final String PROG_AND_TIER_TAG="partnerProgramAndTierLevelTags";
    public static final String SECURE_FOLDER = "/secure";

    /***********  Search Constants *********/
    public static final String SECURE_FILTER_GROUP_LABEL = "MyEaton";
    public static final String SECURE_FACET_ID = "secure-content";
    public static final String SECURE_FILTER_LABEL = "Secure Only";

    public static final String SECURE_GLOBAL_ATTRIB = "secure-global-all-auth-users";

    // custom metadata predicates
    public static final String SINGLE_OPEN_BRACKET= "(";
    public static final String DOUBLE_OPEN_BRACKET= "((";
    public static final String SINGLE_CLOSE_BRACKET= ")";
    public static final String DOUBLE_CLOSE_BRACKET= "))";
    public static final String EQUAL_SINGLE_QUOTE= "='";
    public static final String GREATER_THAN= " > ";
    public static final String LESSER_THAN= " < ";
    public static final String SINGLE_QUOTE= "'";
    public static final String SINGLE_QUOTE_CLOSE_BRACKET= "')";
    public static final String SECURE_NOT = "not(";
    public static final String AND = " and ";
    public static final String OR = " or ";
    public static final String SECURE_OR = "or";
    public static final Integer NUMBER_THREE = 3;
    public static final Integer NUMBER_TWO = 2;
    public static final String ID = "id";
    public static final String SECURE_NON_SECURE = "secure.nonSecure";
    public static final String SECURE_BYPASS_FLAG = "secure.bypassFlag";
    public static final String SECURE_USERPROFILE = "secure.userProfile";
    public static final String SECURE_ASSET_METADATA = "jcr:content/metadata/@secureAsset";
    public static final String ACCOUNTTYPE_METADATA = "jcr:content/metadata/@accountType";
    public static final String PRODUCT_CATEGORY_METADATA = "jcr:content/metadata/@productCategories";
    public static final String APPLICATION_ACCESS_METADATA = "jcr:content/metadata/@applicationAccess";
    public static final String COUNTRIES_METADATA = "jcr:content/metadata/@countries";
    public static final String PROGRAM_TYPE_AND_TIER_LEVEL_METADATA = "jcr:content/metadata/@xmp:eaton-partner-program-and-tier-level";
    public static final String JCR_CONTENT_CQ_TAGS_METADATA = "jcr:content/metadata/@cq:tags";



    /***********  WhatsNew Constants *********/
    public static final String ASSET_PUBLICATION_FLAG = "assetPublicationDateFlag";
    public static final String USER_PROFILE = "userProfile";
    public static final String SECURE_WHATSNEWONJECT = "secure.whatsnewObj";
    public static final String ASSET_PUBLICATION_DATE_METADATA = "jcr:content/metadata/@assetPublicationDate";
    public static final String DATE_RANGE_STRING = "dateRange";
    public static final String XS_DATE_TIME = "xs:dateTime('";
    public static final String FILTER_BY_RES_TYPE_FLAG = "filterByResourceTypeFlag";


    public static final String BYPASS_FLAG = "byPassFlag";
    public static final String MEGA_MENU_PRIFIX = "mega-menu-";
    public static final String MEGA_MENU = "megaMenu";

    public static final String SECURE_LINKED_LIST_RESOURCE_TYPE  = "eaton/components/secure/secure-linked-list";


    /********** User API Profile Constants ************/
    public static final String ATTR_UPDATER_INDENTIFER  = "updatorIdentifier";
    public static final String ATTR_UPDATER_FIRST_NAME  = "updatorFirstName";
    public static final String ATTR_UPDATER_LAST_NAME  = "updatorLastName";
    public static final String ATTR_CURRENT_EMAIL_ADDR  = "currentEmailAddress";
    public static final String ATTR_EMAIL_ADDRESS  = "emailAddress";
    public static final String ATTR_CURRENT_PASSWORD = "currentUserPassword";
    public static final String ATTR_PASSWORD  = "userPassword";
    public static final String ATTR_UPDATE_LAST_LOGIN = "updateLastLogin";

    /********* Track Download API Constants **********/
    public static final String ATTR_USER_INDENTIFER  = "userIdentifier";
    public static final String ATTR_FIRST_NAME  = "firstName";
    public static final String ATTR_LAST_NAME  = "lastName";
    public static final String ATTR_EMAIL  = "emailAddress";
    public static final String ATTR_ADDRESS1  = "address1";
    public static final String ATTR_ADDRESS2  = "address2";
    public static final String ATTR_STREET  = "street";
    public static final String ATTR_CITY  = "city";
    public static final String ATTR_STATE  = "state";
    public static final String ATTR_COUNTRY  = "country";
    public static final String ATTR_POSTAL_CODE  = "postalCode";
    public static final String ATTR_USER_TYPE  = "userType";
    public static final String ATTR_ECCN  = "eccn";
    public static final String ATTR_ASSET_NAME  = "assetName";
    public static final String ATTR_ASSET_TYPE  = "assetType";
    public static final String ATTR_ASSET_ACCOUNT_TYPE  = "assetUserTypes";
    public static final String ATTR_ASSET_PROD_CAT  = "assetProductCategory";
    public static final String ATTR_ASSET_PART_DESCRIPTION  = "assetPartDescription";
    public static final String ATTR_ASSET_PART_NUMEBR  = "assetPartNumber";
    public static final String ATTR_ASSET_PUB_DATE  = "assetPublicationDate";
    public static final String ATTR_ASSET_PROD_LINE  = "assetProductLine";
    public static final String ATTR_ASSET_IDENTIFIER = "assetIdentifier";

    /********** Agent Reports API Constants ************/
    public static final String AGENT_REPORTS_MAPPING_JSON = "/content/dam/eaton/resources/secure/AgentReportsMapping.json";
    public static final String ORG_UNIT1 = "orgUnit1";
	public static final String ORG_UNIT3 = "orgUnit3";
	public static final String ORG_UNIT2 = "orgUnit2";
	public static final String PARTNER = "partner";
	public static final String ORGANIZATION = "organization";
	public static final String UNDER_SCORE = "_";
	public static final String MAPPING_CODES = "mappingCodes";
	public static final String API_KEYS = "apiKeys";
	public static final String DIVISIONS_DISPLAY_TEXT = "divisionsDisplayText";
	public static final String DISPLAY = "DISPLAY";
    public static final String AGENT_ID = "agentId";
    public static final String DIVISION = "division";
    public static final String AR_ACCESS_TOKEN_MAP = "arAccessTokenMap";
    public static final String DOC_NAME = "docName";
    public static final String CONTENT_TYPE_TEXT_XML_CHARSET = "text/xml; charset=utf-8";
    public static final String DATE_SORT = "DESC";
	public static final String CONTENT_LENGTH = "Content-Length";
	public static final String CONTENT_TYPE = "Content-Type";
	public static final String SOAP_ACTION = "SOAPAction";
	public static final String POST = "POST";
	public static final Boolean BOOLEAN_TRUE = true;
	public static final String BASIC = "Basic ";
	public static final String GRANT_TYPE_KEY = "grant_type";
	public static final String AUTHORIZATION = "Authorization";
	public static final String ADVANCED_SEARCH_RESULT = "idc:AdvancedSearchResult";
	public static final String DOWNLOAD_FILE = "idc:downloadFile";
	public static final String FILE_NAME = "idc:fileName";
	public static final String FILE_CONTENT = "idc:fileContent";
	public static final String IDC_SEARCHRESULT = "idc:SearchResults";
	public static final String IDC_DID = "idc:dID";
	public static final String IDC_DREVISIONID = "idc:dRevisionID";
	public static final String IDC_DDOCNAME = "idc:dDocName";
	public static final String IDC_DDOCTITLE = "idc:dDocTitle";
	public static final String IDC_DDOC_TYPE = "idc:dDocType";
	public static final String IDC_DDOC_AUTHOR = "idc:dDocAuthor";
	public static final String IDC_DSECURITY_GRP = "idc:dSecurityGroup";
	public static final String IDC_DDOC_ACCNT = "idc:dDocAccount";
	public static final String IDC_DEXTN = "idc:dExtension";
	public static final String IDC_DWEB_EXTN = "idc:dWebExtension";
	public static final String IDC_D_REV_LABEL = "idc:dRevLabel";
	public static final String IDC_D_IN_DATE = "idc:dInDate";
	public static final String IDC_D_OUT_DATE = "idc:dOutDate";
	public static final String IDC_D_FORMAT = "idc:dFormat";
	public static final String IDC_D_ORIGINAL_NAME = "idc:dOriginalName";
	public static final String IDC_URL = "idc:url";
	public static final String IDC_D_GIF = "idc:dGif";
	public static final String IDC_WEB_FILE_SIZE = "idc:webFileSize";
	public static final String IDC_VAULT_FILE_SIZE = "idc:vaultFileSize";
	public static final String IDC_ALTERNATE_FILE_SIZE = "idc:alternateFileSize";
	public static final String IDC_ALTERNATE_FORMAT = "idc:alternateFormat";
	public static final String IDC_D_PUBLISH_TYPE = "idc:dPublishType";
	public static final String IDC_D_RENDITION_1 = "idc:dRendition1";
	public static final String IDC_D_RENDITION_2 = "idc:dRendition2";
	public static final String LOGIN = "login";
	public static final String PASSWORD = "password";
	public static final String SET_COOKIE = "Set-Cookie";
	public static final String COOKIE = "Cookie";
	public static final String JSESSIONID = "JSESSIONID";
	public static final String ACCESS_TOKEN = "accessToken";
	public static final String DIVISION_STATIC_VALUE = "Cooper/Stellent/C3/";

	public static final String TAGS_PATH = "tagpath";

	public static final String REVENERA_PQ_PRODUCT_MANAGER = "Revenera_PQ_Product Manager";
	public static final String REVENERA_PQ_SYSTEM_ADMINISTRATOR = "Revenera_PQ_System Administrator";
    public static final String SOFTWARE_DELIVERY_TEMPLATE_PATH = "/conf/eaton/settings/wcm/templates/software-delivery";
    public static final String BUSINESS_GROUP_METADATA = "businessGroup" ;

    private SecureConstants(){}

}
