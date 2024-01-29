package com.eaton.platform.core.constants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Class CommmonConstants.
 */
public final class CommonConstants {

    /* **************** Symbol Constants - Starts Here **************** */
    /** The Constant Fallback Locale. */
    public static final String FALLBACK_LOCALE = "en_US";
    /** The Constant SLASH_STRING. */
    public static final String SLASH_STRING = "/";
    /** The Constant DOUBLE_QUOTE. */
    public static final String DOUBLE_QUOTE = "\"";
    /** The Constant FORWARD_SLASH. */
    public static final String FORWARD_SLASH = "\\/";
    /** The Constant SEMI_COLON. */
    public static final String SEMI_COLON = ";";
    /** The Constant PIPE. */
    public static final String PIPE = "|";
    /** The Constant ESCAPED_PIPE. */
    public static final String ESCAPED_PIPE = "\\|";
    /** The Constant DOUBLE_PIPE. */
    public static final String DOUBLE_PIPE = "||";
    /** The Constant DOUBLE_COLON. */
    public static final String DOUBLE_COLON = "::";
    /** The Constant DOUBLE_SLASH. */
    public static final String DOUBLE_SLASH = "//";
    /** The Constant DOUBLE_QUOTE_WITH_SPACE. */
    public static final String DOUBLE_QUOTE_WITH_SPACE = "\" ";
    /** The Constant UNDERSCORE. */
    public static final String UNDERSCORE = "_";
    /** The Constant HYPHEN. */
    public static final String HYPHEN = "-";
    /** The Constant COMMA. */
    public static final String COMMA = ",";
    /** The Constant PERIOD. */
    public static final String PERIOD = ".";
    /** The Constant used in the sort selector to deliminate the sort attribute from the asc / desc */
    public static final String SORT_DIRECTION_DELIMITER = "*";
    public static final String ASCENDING_SORT = "asc";
    /** The Constant SPACE_STRING. */
    public static final String SPACE_STRING = " ";
    /** The Constant BACKSLASH_SINGLE_QUOTE. */
    public static final String BACKSLASH_SINGLE_QUOTE = "\\'";
    /** The Constant BACKSLASH_PERIOD. */
    public static final String BACKSLASH_PERIOD = "\\.";
    /** The Constant SPACE_HYPEHN_SPACE. */
    public static final String SPACE_HYPEHN_SPACE = " - ";
    /** The Constant COLON. */
    public static final String COLON = ":";
    /** The Constant SINGLE_QUOTE. */
    public static final String SINGLE_QUOTE = "'";
    /** The Constant DOLLAR_CHAR. */
    public static final String DOLLAR_CHAR = "$";
    /** The Constant COLON_SLASH. */
    public static final String COLON_SLASH = ":/";
    /** The Constant QUESTION_MARK_CHAR. */
    public static final String QUESTION_MARK_CHAR = "?";
    /** The Constant COLON_DOUBLE_SLASH. */
    public static final String COLON_DOUBLE_SLASH = "://";
    /** The Constant ATRATE_CHAR. */
    public static final String ATRATE_CHAR = "@";
    /** The Constant WCM_MODE. */
    public static final String WCM_MODE = "wcmmode";
    /** The Constant CONTENT_PAGE_DEPTH. */
    public static final int CONTENT_PAGE_DEPTH = 1;
    /** The Constant BRAND_PAGE_DEPTH. */
    public static final int BRAND_PAGE_DEPTH = 1;
    /** The Constant COUNTRY_PAGE_DEPTH. */
    public static final int COUNTRY_PAGE_DEPTH = 3;
    /** The Constant LANGUAGE_PAGE_DEPTH. */
    public static final int LANGUAGE_PAGE_DEPTH = 4;
    /* **************** Symbol Constants - Ends Here **************** */

    /* **************** Property Constants - Starts Here **************** */

    /** The Constant TEMPLATE_PROP_KEY. */
    public static final String TEMPLATE_PROP_KEY = "cq:template";
    /** The Constant HTML_EXTN. */
    public static final String HTML_EXTN = ".html";
    /** The Constant LOCATION. */
    public static final String LOCATION = "Location";
    /** The Constant CONNECTION. */
    public static final String CONNECTION = "Connection";
    /** The Constant USER_AGENT. */
    public static final String USER_AGENT = "user-agent";
    /** The Constant CLOSETEXT. */
    public static final String CLOSETEXT = "close";
    /** The Constant HTTP. */
    public static final String HTTP = "http";
    /** The Constant HTTPS. */
    public static final String HTTPS = "https";
    /** The Constant CACHE_CONTROL. */
    public static final String CACHE_CONTROL = "Cache-Control";
    /** The Constant NO_CACHE. */
    public static final String NO_CACHE = "no-cache";
    /** The Constant GET_METHOD. */
    public static final String GET_METHOD = "GET";
    /** The Constant GET_METHOD. */
    public static final String ACCEPT = "Accept";
    /** The Constant POST_METHOD. */
    public static final String POST_METHOD= "POST";
    /** The Constant OAUTH_TOKEN_RESOURCE_PATH. */
    public static final String OAUTH_TOKEN_RESOURCE_PATH = "/oauth/accesstoken";
    /** The Constant ACCESS_TOKEN_RESOURCE_PATH. */
    public static final String ACCESS_TOKEN_RESOURCE_PATH = "/accesstoken";

    /** The Constant CONTENT_TYPE. */
    public static final String CONTENT_TYPE = "Content-Type";
    /** The Constant AUTHORIZATION. */
    public static final String AUTHORIZATION = "Authorization";
    /** The Constant ENDPOINT. */
    public static final String ENDPOINT = "endpoint";
    /** The Constant KEYWORDS. */
    public static final String KEYWORDS = "keywords";
    /** The Constant KEY. */
    public static final String KEY = "key";
    /** The Constant ADDRESS. */
    public static final String ADDRESS = "address";
    /** The Constant PARAMS. */
    public static final String PARAMS = "params";
    /** The Constant PAGE_SIZE. */
    public static final String PAGE_SIZE = "pageSize";
    /** The Constant UNAUTHENTICATED. */
    public static final String UNAUTHENTICATED = "Unauthenticated";
    /** The Constant AUTHENTICATED. */
    public static final String AUTHENTICATED = "Authenticated";
    /** The Constant REDIRECT_URL. */
    public static final String CQ_REDIRECT_URL = "cq:redirectTarget";
    public static final String REDIRECT_URL = "redirectTarget";
    /** The Constant JCR_TITLE. */
    public static final String JCR_TITLE = "jcr:title";
    public static final String JCR_TITLE_HE_IL = "jcr:title.he_il";
    /** The Constant JCR_MODIFIED. */
    public static final String JCR_MODIFIED = "jcr:lastModified";
    /** The Constant HASH_VALUE. */
    public static final String HASH_VALUE = "hashValue";
    /** The Constant JCR_CREATED. */
    public static final String JCR_CREATED = "jcr:created";

    /** The Constant RESOURCE_RESOLVER_READ_SERVICE. */
    public static final String RESOURCE_RESOLVER_READ_SERVICE = "readService";

    /** The Constant RESOURCE_RESOLVER_WRITE_SERVICE. */
    public static final String RESOURCE_RESOLVER_WRITE_SERVICE = "writeService";
    /** The Constant NODE_TYPE. */
    public static final String NODE_TYPE = "cq:Page";
    /** The Constant JCR_PRIMARY_TYPE. */
    public static final String JCR_PRIMARY_TYPE = "jcr:primaryType";
    /** The Constant CQ_PAGE_CONTENT. */
    public static final String CQ_PAGE_CONTENT = "cq:PageContent";

    /** The Constant CQ_LAST_REPLICATED. */
    public static final String CQ_LAST_REPLICATED = "cq:lastReplicated";

    /** The Constant ASSET_PUBLICATION_DATE */
    public static final String ASSET_PUBLICATION_DATE = "assetPublicationDate";

    /** The Constant ECCN */
    public static final String ECCN = "xmp:eaton-eccn";

    /** The Constant ASSET_PUBLICATION_DATE */
    public static final String ASSET_PRODUCT_LINE = "xmp:eaton-product-line";
    /** The Constant ASSET_PART_NUMBER */
    public static final String ASSET_PART_NUMBER = "xmp:eaton-part-number";
    /** The Constant ASSET_PART_DESC */
    public static final String ASSET_PART_DESC = "xmp:eaton-part-description";
    /** The Constant ASSET_BORN_ON_DATE */
    public static final String ASSET_BORN_ON_DATE = "bornOnDate";
    /** The Constant ASSET_DC_PERCOLATE_CONTENT_ID */
    public static final String DC_PERCOLATE_CONTENT_ID = "dc:percolate-content-id";
    /** The Constant ASSET_CONTENT_ID_PARAM */
    public static final String ASSET_CONTENT_ID_PARAM = "assetID";
    /** The Constant SLING_VANITY. */
    public static final String SLING_VANITY = "sling:vanityPath";
    /** The Constant BASE_PAGE_PATH. */
    public static final String BASE_PAGE_PATH = "base.page.path";
    /* **************** Property Constants - Ends Here **************** */

    /* **************** Tempalte Paths Constants - Starts Here **************** */
    /** The Constant ARTICLE_PAGE_TEMPLATE_PATH. */
    public static final String ARTICLE_PAGE_TEMPLATE_PATH = "";
    /* **************** Tempalte Paths Constants - Ends Here **************** */

    /* **************** Common Constants - Starts Here **************** */
    /** The Constant ARTICLE_PAGE_TEMPLATE_PATH. */
    /** The Constant CONTENT_ROOT_FOLDER. */
    public static final String CONTENT_ROOT_FOLDER = "/content/eaton/";

    public static final String CONTENT_ROOT_FOLDER_LOGIN = "/content/login/";

    public static final String CONTENT_ROOT_FOLDER_CUMMINS = "/content/eaton-cummins/";

    public static final String CONTENT_ROOT_FOLDER_PHOENIXTEC = "/content/phoenixtec/";

    public static final String CONTENT_ROOT_FOLDER_GREENSWITCHING = "/content/greenswitching/";

    public static final String CONTENT_ROOT_FOLDER_REFERENCE_MICROSITE = "/content/reference-microsite/";

    public static final String CONTENT_ROOT_FOLDER_DEMO_MICROSITE = "/content/demo-microsite/";

    public static final String CONTENT_ROOT_FOLDER_SWITCHON = "/content/switchon/";

    public static final String RUNMODE_PUBLISH = "publish";
    public static final String RUNMODE_AUTHOR = "author";

    public static final String CONTENT_ROOT_FOLDER_NO_EXTRA_SLASH = "/content/eaton";
    public static final String SECURE_DASHBOARD = "/secure/dashboard";

    /** The Constant CONTENT_ROOT_FOLDER_REGEX. */
    public static final String CONTENT_ROOT_FOLDER_REGEX = "/content/eaton/.*";
    /* **************** Common Constants - Ends Here **************** */

    /** The Constant WEBSERVICE_SERVER_URL. */
    /* **************** Data Interface Constants - Starts Here **************** */
    public static final  String WEBSERVICE_SERVER_URL="webservice.server.url";

    /** The Constant WEBSERVICE_STUB_ENABLED. */
    public static final  String WEBSERVICE_STUB_ENABLED="webservice.stub.enabled";

    /** The Constant WEBSERVICE_STUB_DIR. */
    public static final  String WEBSERVICE_STUB_DIR="webservice.stub.dir";
    /* **************** Data Interface Constants - Ends Here **************** */

    /**  TTIL Graphic Drop down constants. */
    public static final  String ICON_IMAGE_ROOT = "/content/dam/eaton/images/icons";

    /**  Socail Links Drop down constants. */
    public static final  String SOCIAL_ICON_ROOT_PATH = "/content/dam/eaton/images/sociallinks";

    /** The Constant VALUE. */
    public static final  String VALUE = "value";

    /** The Constant TEXT. */
    public static final  String TEXT = "text";

    /**  TTIL List Constants. */
    public static final  String CHILD_PAGES = "child-pages";

    /** The Constant TAGS. */
    public static final  String TAGS = "tags";

    /** The Constant TAGS. */
    public static final  String CQ_TAGS = "cq:tags";

    /** The Dam Asset Title. */
    public static final  String DC_TITLE = "dc:title";

    /** The Dam Asset Format. */
    public static final String DC_FORMAT = "dc:format";

    /** The Dam Asset Format Type. */
    public static final String APPLICATION = "application";

    /** The Dam Asset Format Audio. */
    public static final String AUDIO = "audio";

    /** The Dam Asset Format Video. */
    public static final String VIDEO = "video";

    /** The Constant YES. */
    public static final String YES = "yes";

    /** The Constant NO. */
    public static final String NO = "no";

    /** The Constant FROM. */
    public static final String FROM = "from";

    public static final  String TAG_NAMESPACE_EATON = "eaton";
    public static final  String TAG_NAMESPACE_PHOENIXTEC = "phoenixtec";
    public static final  String TAG_NAMESPACE_EATON_CUMMINS = "eaton-cummins";
    public static final  String TAG_NAMESPACE_RESOURCES = "resources";
    public static final  String TAG_NAMESPACE_PRODUCT_TAXONOMY = "product-taxonomy";

	public static final  String TAG_NAMESPACE_PRODUCT_TAXONOMY_EXTENDED = "product-taxonomy-extended";
    public static final  String TAG_NAMESPACE_MYEATON_TAXONOMY = "myeaton-taxonomy";
    public static final  String TAG_NAMESPACE_SERVICES_TAXONOMY = "services";
    public static final  String TAG_NAMESPACE_SUPERMARKETS = "supermarkets";
    public static final  String TAG_NAMESPACE_EATON_BRAND = "eaton-brand";
    public static final  String TAG_NAMESPACE_SUPPORT_TAXONOMY = "support-taxonomy";
    public static final String TAG_HIERARCHY_SEPARATOR = "/";
    public static final String TAG_NAMESPACE_SEPARATOR = ":";
    public static final String SKU_PAGE = "skuPage";
    public static final String TAG_NEWS_RELEASES_TOPIC = "news-releases_Topic";


    /** The Constant CQ_NAME. */
    public static final  String CQ_NAME = "cq:name";

    /** The Constant PUBLICATION_DATE. */
    public static final  String PUBLICATION_DATE = "publication-date";

    /** The Constant Replication_DATE. */
    public static final  String REPLICATION_DATE = "cq:lastReplicated";

    /** The Constant TEASER_IMAGE_PATH. */
    public static final  String TEASER_IMAGE_PATH = "teaser-image-path";

    /** The Constant FIXED_LIST. */
    public static final  String FIXED_LIST = "fixed-list";

    /** The Constant LINK_PATH. */
    public static final  String LINK_PATH = "linkPath";

    /** The Constant TRANS_HEADlINE. */
    public static final  String TRANS_HEADlINE = "transHeadline";

    /** The Constant MANUAL_LIST. */
    public static final  String MANUAL_LIST = "manual-list";

    /** The Constant PUB_DATE. */
    public static final  String PUB_DATE = "publicationDate";

    /** The Constant LINK_DESTINATION. */
    public static final  String LINK_DESTINATION = "transLinkDestination";

    /** The Constant NEW_WINDOW_MANUAL. */
    public static final  String NEW_WINDOW_MANUAL = "newWindowManual";

    /** The Constant EYEBROW. */
    public static final  String EYEBROW = "eyebrow";

    /** The Constant FEATURE_IMAGE. */
    public static final  String FEATURE_IMAGE = "featureImage";

    /** The Constant TITLE. */
    public static final  String TITLE = "title";

    /** The Constant PUBLISH_DATE. */
    public static final  String PUBLISH_DATE = "pub-date";

    /** The Constant CREATED_DT. */
    public static final  String CREATED_DT = "created-date";

    /** The Constant LAST_MOD_DT. */
    public static final  String LAST_MOD_DT = "last-modified-date";

    /** The Constant TEMPLATE. */
    public static final  String TEMPLATE = "template";

    /** The Constant NEW_WINDOW_FIXED. */
    public static final  String NEW_WINDOW_FIXED = "newWindowFixed";

    /** The Constant DESCRIPTION. */
    public static final  String DESCRIPTION = "transDescription";

    /** The Constant TEASER_DESCRIPTION. */
    public static final  String TEASER_DESCRIPTION = "teaser-description";

    public static final  String PRODUCT_GRID_DESCRIPTION = "product-grid-description";

    /** The Constant DATE_FORMAT_PUBLISH. */
    public static final  String DATE_FORMAT_PUBLISH = "MMMM d, yyyy";

    /** The Constant DEFAULT_DATE_FORMAT_PUBLISH. */
    public static final  String DEFAULT_DATE_FORMAT_PUBLISH = "dd/MM/yyyy";

    /** The Constant UNITED_STATES_DATE_FORMAT_PUBLISH. */
    public static final  String UNITED_STATES_DATE_FORMAT_PUBLISH = "MM/dd/yyyy";

    /** The Constant TEASER_TITLE. */
    public static final  String TEASER_TITLE = "teaser-title";

    /** The Constant TEASER_TAG. */
    public static final  String TEASER_TAG = "primary-sub-category-tag";

    /** The Constant EYEBROW_TITLE. */
    public static final  String EYEBROW_TITLE = "eyebrow-title";

	 /** The Constant PRIMARY_SUB_CATEGORY. */
    public static final  String PRIMARY_SUB_CATEGORY = "primarySubCategory";

    /** The Constant TEASER_BYLINE. */
    public static final  String TEASER_BYLINE = "teaserByline";

    /** The Constant ALT_TXT. */
    public static final  String ALT_TXT = "transAltTxt";

    /** The Constant MANUAL_PUB_DATE. */
    public static final  String MANUAL_PUB_DATE ="yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

    /** The Constant MANUAL_PUB_DATE_FORMAT. */
    public static final  String MANUAL_PUB_DATE_FORMAT ="MMMM dd, yyyy";

    /** The Constant UTF_8. */
    public static final  String UTF_8 ="UTF-8";

    /** The Constant TARGET_BLANK. */
    public static final  String TARGET_BLANK ="_blank";


    /** The Constant TARGET_BLANK. */
    public static final  String TARGET_SELF ="_self";

    /** The Constant TRUE. */
    public static final  String TRUE ="true";

    /** The Constant FALSE. */
    public static final  String FALSE ="false";

    /** The Constant HOME_LEVEL. */
    /* ***************************************/
    public static final  int HOME_LEVEL = 3;

    /** The Constant STARTS_WITH_CONTENT. */
    public static final String STARTS_WITH_CONTENT = "/content";

    public static final String STARTS_WITH_CONTENT_WITH_SLASH = "/content/";

    public static final String LANGUAGE_MASTERS_NODE_NAME = "language-masters";

    /** The Constant REFERER_URL. */
    public static final String REFERER_URL = "referer";
    /** The Constant EMPTY_ARRAY. */
    public static final String[] EMPTY_ARRAY = new String[0];

    /**  Brightcove Component Constants. */
    public static final String BC_ERROR_MSG_FIELD_NAME = "errorMsg";

    /** The Constant BC_ACCOUNTS_FIELD_NAME. */
    public static final String BC_ACCOUNTS_FIELD_NAME = "bcAccounts";

    /** The Constant BC_ACC_NAME_FIELD_NAME. */
    public static final String BC_ACC_NAME_FIELD_NAME = "accName";

    /** The Constant BC_ACC_NUMBER_FIELD_NAME. */
    public static final String BC_ACC_NUMBER_FIELD_NAME = "accNumber";

    /** The Constant BC_PLAYERS_FIELD_NAME. */
    public static final String BC_PLAYERS_FIELD_NAME = "players";

    /** The Constant BC_PLAYER_ID_FIELD_NAME. */
    public static final String BC_PLAYER_ID_FIELD_NAME = "playerID";

    /** The Constant BC_PLAYER_NAME_FIELD_NAME. */
    public static final String BC_PLAYER_NAME_FIELD_NAME = "playerName";

    /** The Constant BC_CLOUD_CONFIG_NODE_NAME. */
    public static final String BC_CLOUD_CONFIG_NODE_NAME = "brightcoveconfig";


    /** The Constant C_CLOUD_CONFIG_NODE_NAME. */
    public static final String C_CLOUD_CONFIG_NODE_NAME = "/chatconfig";

    /** The Constant CC_CLOUD_CONFIG_NODE_NAME. */
    public static final String NR_CLOUD_CONFIG_NODE_NAME = "nanorepchatconfig";

    /** The Constant BRAND_COLOR_SELECTION_CLOUD_CONF_NODE_NAME. */
    public static final String BRAND_COLOR_SELECTION_CLOUD_CONF_NODE_NAME = "brandcolorselection";

    /** The Constant JCR_CONTENT_STR. */
    public static final String JCR_CONTENT_STR = "jcr:content";

    /** The Constant CLOUD_SERVICES. */
    public static final String CLOUD_SERVICES = "cq:cloudserviceconfigs";

    /**  Social Share Component Constants. */
    public static final String SOCIAL_SHARE_CLOUD_CONFIG = "socialshare";

    /** The Constant SOCIAL_SHARE_PUB_ID. */
    public static final String SOCIAL_SHARE_PUB_ID = "pubID";

    /** The Constant CQ_TEMPLATE_PROPERTY. */
    public static final String CQ_TEMPLATE_PROPERTY = "cq:template";

    /** The Constant ALLOWED_TEMPLATE_PROPERTY. */
    public static final String ALLOWED_TEMPLATE_PROPERTY = "allowedTemplates";

    /** The Constant SEARCH_TERM. */
    public static final String SEARCH_TERM = "searchTerm";

    /** The Constant META_ROBOT_TAGS. */
    public static final String META_ROBOT_TAGS = "meta-robot-tags";
    public static final String INDEX_FOLLOW = "index,follow";

    /** The Constant PAGE_TYPE. */
    public static final String PAGE_TYPE = "page-type";

	 /** The Constant PAGE_INTENT. */
    public static final String PAGE_INTENT = "page-intent";

    /** The Constant OPEN GRAPH OR TWITTER. */
    public static final String PRODUCT = "product";
    public static final String EATON = "Eaton";
    public static final String EATON_CORP = "@eatoncorp";
    public static final String WEBSITE = "website";
    public static final String CONTENT_TYPE_LOWER_CASE = "content-type";

    /** The Constant FILTER_PAGE_TYPE. */
    public static final String FILTER_PAGE_TYPE = "PageType";

    /** The Constant PAGE_TYPE_PRIMARY_PAGE. */
    public static final String PAGE_TYPE_PRIMARY_PAGE = "primary";

    /** The Constant PAGE_TYPE_HOME_PAGE. */
    public static final String PAGE_TYPE_HOME_PAGE = "home-page";

    /** The Constant PAGE_TYPE_GENERIC_PAGE. */
    public static final String PAGE_TYPE_GENERIC_PAGE = "generic-page";

    /** The Constant PAGE_TYPE_ARTICLE_PAGE. */
    public static final String PAGE_TYPE_ARTICLE_PAGE = "article-page";

    /** The Constant PAGE_TYPE_SITEMAP_PAGE. */
    public static final String PAGE_TYPE_SITEMAP_PAGE = "sitemap-page";

    /** The Constant PAGE_TYPE_SKU_PAGE. */
    public static final String PAGE_TYPE_SKU_PAGE = "product-sku";

    /** The Constant PAGE_TYPE_PRODUCTFAMILY_PAGE. */
    public static final String PAGE_TYPE_PRODUCTFAMILY_PAGE = "product-family";

    /** The Constant DATA_LAYER_PRODUCT_FAMILY. */
    public static final String DATA_LAYER_PRODUCT_FAMILY = "product family";

    /** The Constant DATA_LAYER_PRODUCT_SKU. */
    public static final String DATA_LAYER_PRODUCT_SKU = "product sku";

    /** The Constant HOME_PAGE_LABEL. */
    public static final String HOME_PAGE_LABEL = "Home page";

    /** The Constant GENERIC_PAGE_LABEL. */
    public static final String GENERIC_PAGE_LABEL = "Generic Page";

    /** The Constant ARTICLE_PAGE_LABEL. */
    public static final String ARTICLE_PAGE_LABEL = "Article Page";

    /** The Constant SITEMAP_PAGE_LABEL. */
    public static final String SITEMAP_PAGE_LABEL = "Sitemap Page";


    /** The Constant SKU_PAGE_LABEL. */
    public static final String SKU_PAGE_LABEL = "SKU Page";

    /** The Constant PRODUCTFAMIL_PAGE_LABEL. */
    public static final String PRODUCTFAMILY_PAGE_LABEL = "Product family Page";

    /** The Constant PRODUCT_REPLACEMENT_TEXT. */
    public static final String PRODUCT_REPLACEMENT_TEXT = "replacement";

    /** The Constant PRODUCT_DETAILS_CARD_REPLACEMENT. */
    public static final String PRODUCT_DETAILS_CARD_REPLACEMENT_OPTION_TEXT = "ProductDetailsCard.ReplacementOptionText";

    /** The Constant PRODUCT_DETAILS_CARD_REPLACEMENT_DEFAULT_TEXT. */
    public static final String PRODUCT_DETAILS_CARD_REPLACEMENT_DEFAULT_TEXT = "Replacement option:";

    /** The Constant PRODUCT_DETAILS_CARD_REPLACEMENTS. */
    public static final String PRODUCT_DETAILS_CARD_REPLACEMENT_OPTIONS_TEXT = "ProductDetailsCard.ReplacementOptionsText";

    /** The Constant PRODUCT_DETAILS_CARD_REPLACEMENTS_DEFAULT_TEXT. */
    public static final String PRODUCT_DETAILS_CARD_REPLACEMENTS_DEFAULT_TEXT = "Replacement options:";


    /**  MegaMenu Component Constants. */

    public static final String PRIMARY_NAV_PATH = "/jcr:content/root/header/primary-nav";

    /** The Constant OVERLAY_NAV_PATH. */
    public static final String OVERLAY_NAV_PATH = "/jcr:content/overlay-responsive-grid/responsive-grid/menu_overlay";

    /** The Constant QUERY_BASE. */
    public static final String QUERY_BASE = "SELECT * FROM [nt:base] AS nodes WHERE ISDESCENDANTNODE ([";

    /** The Constant QUERY_HIDEIN. */
    public static final String QUERY_HIDEIN = "]) and nodes.hideInNav='true' ";

    public static final String QUERY_SLINGRESOURCETYPE = "]) and [sling:resourceType]='";

    /** The Constant CQ_TEMPLATE_PROPERTY_INITIAL. */
    public static final String CQ_TEMPLATE_PROPERTY_INITIAL = "initial";

    /** The Constant CQ_TEMPLATE_PROPERTY_STRUCRTURE. */
    public static final String CQ_TEMPLATE_PROPERTY_STRUCRTURE = "structure";

    /**  CommonUtil Constants. */

    public static final String WWW = "www.";

    /** The Constant HTTP_SLASH. */
    public static final String HTTP_SLASH = "http://";

    /** The Constant HTTPS_SLASH. */
    public static final String HTTPS_SLASH = "https://";


    /**  FullPageDrawer Constants. */

    public static final String ICONS = "icons";

    /** The Constant LINKPATH. */
    public static final String LINKPATH = "linkPath";

    /** The Constant TRANSLINKTITLE. */
    public static final String TRANSLINKTITLE = "transLinkTitle";

    /** The Constant NEW_WINDOW. */
    public static final String NEW_WINDOW = "newWindow";

    /** Nofollow;Noindex constant*/
    public static final String NOFOLLOWNOINDEX="noindex,nofollow";

    /** The Constant ESCAPE_S. */
    public static final String ESCAPE_S = "\\s";

    /** The Constant ANIMATEDGIF */
    public static final String ANIMATEDGIF = "ANIMATEDGIF";

    /** The Constant PDF. */
    public static final String PDF = "PDF";

    /** The Constant JPEG. */
    public static final String JPEG = "JPEG";

    /** The Constant PNG. */
    public static final String PNG = "PNG";

    /** The Constant LINK_TITLE. */
    public static final String LINK_TITLE = "transLinkTitle";

    /** The Constant FIXED_LINK_TITLE. */
    public static final String FIXED_LINK_TITLE = "fixedLinkTitle";

    /** The Constant ASSET_LINK_TITLE. */
    public static final String ASSET_LINK_TITLE = "assetLinkTitle";

    /** The Constant NEWLINE_CHARACTER. */
    public static final String NEWLINE_CHARACTER = "\n";

    /** The Constant CONTENT_DAM. */
    public static final String CONTENT_DAM_NO_EXTRA_SLASH = "/content/dam";

    public static final String CONTENT_DAM_IMAGE_PATH = "payload";

    public static final String DAM_NOTIFY_RECIPIENTS = "recipients:";

    public static final String CONTENT_DAM_EMAIL_TEMPLATE = "emailTemplate:";
    public static final String CONTENT_DAM = CONTENT_DAM_NO_EXTRA_SLASH  + "/";

    /** Icon List Constants */

    public static final String TOPIC_LINK_WITH_ICON = "topic-link-with-icon";
    public static final String NO_IMAGE_HIGHLIGHTS = "no-image-highlights";
    public static final String SUPERSCRIPT_SPAN_PART1 = "<span class=\"icon-list-no-image-highlights__symbol\">";
    public static final String SUPERSCRIPT_SPAN_PART2 = "</span>";
    public static final String ICON = "icon";
    /** The Constant MB. */
    public static final String MB = "MB";

    /** The Constant KB. */
    public static final String KB = "KB";

    /** The Constant B. */
    public static final String B = "B";

    /** The Constant BLANK_SPACE. */
    public static final String BLANK_SPACE = " ";

    /** The Constant EDIT. */
    public static final String EDIT = "edit";
    /** The Constant DISABLED. */
    public static final String DISABLED = "disabled";

    /** The Constant INACTIVE. */
    public static final String INACTIVE = "Inactive";

    /** The Constant DISCONTINUED. */
    public static final String DISCONTINUED = "Discontinued";

    /** The Constant PRIMARY_NAVIGATION. */
    public static final String PRIMARY_NAVIGATION = "primary-navigation";
    public static final String AEM_FORMS_ACTUAL_BASE_PATH = "/content/forms/af";
    public static final String AEM_FORMS_MIRROR_BASE_PATH = "/content/dam/formsanddocuments";
    public static final String RESOURCE_TYPE_AEM_FORMS_CAPTCHA = "fd/af/components/guideCaptcha";
    public static final String PROP_FORM_REF = "formRef";
    public static final String PROP_CAPTCHA_SERVICE = "captchaService";
    public static final String VAL_CAPTCHA_SERVICE_RECAPTCHA = "recaptcha";
    public static final String GLOBAL_ATTRIBUTE_VALUE = "globalAttributeValue";
    public static final String BULK_DOWNLOAD_TOOL_TIP_KEY = "bulkDownloadToolTip";
    public static final String BULK_DOWNLOAD_TOOL_TIP_DEFAULT_VALUE = "Select to enable bulk downloads";
    public static final String BULK_DOWNLOAD_KEY = "bulkdownload";
    public static final String BULK_DOWNLOAD_DEFAULT_VALUE = "Bulk Download";
    public static final String DOWNLOAD_KEY = "download";
    public static final String DOWNLOAD_DEFAULT_VALUE = "Download";

    public static final String DOWNLOAD_LIMIT_EXCEEDED_KEY = "downloadLimitExceeded";

    public static final String DOWNLOAD_LIMIT_EXCEEDED_DEFAULT_VALUE = "You have exceeded the download limit";

    /** The facets key low to high sort. */
    public static String FACETS_KEY_LOW2HIGH_SORT= "ProductGrid.lowToHigh";

    /** The facets key high to low sort. */
    public static String FACETS_KEY_HIGH2LOW_SORT = "ProductGrid.highToLow";

    /** The Constant SECTION_NAVIGATION. */
    public static final String SECTION_NAVIGATION = "section-navigation";
    public static final String FACET_SEPERATOR = "$";

    // Dropdown default option
    public static final String DROPDOWN_DEFAULT_OPTION = "Please Select";
    public static final String DATASOURCE_ADD_NONE_PROP= "addNone";
    public static final String DATASOURCE_ADD_NONE_TITLE_PROP = "noneOptionTitle";
    public static final String DATASOURCE_NODE_NAME = "datasource";

    /** Products module constants */
    //public static final String PRODUCT_GRID_OBJECT = "productGridObject";CommentedCode
    public static final String FAMILY_LIST_RESPONSE_BEAN = "familyListResponseBean";
    public static final String PRODUCT_TYPE_LIST_RESPONSE_BEAN = "productTypeListResponseBean";
    public static final String SKU_LIST_RESPONSE_BEAN = "skuListResponseBean";
    public static final String SKU_DETAILS_BEAN = "skuDetailsObject";
    public static final String SITE_SEARCH_RESULTS_BEAN = "siteSearchResultsObject";
    public static final String PRODUCT_TYPE_FACET_NAME = "Product_Type";

    public static final String PIM_BASE_PATH = "/var/commerce/products/eaton";
    public static final String PAGE_TYPE_PRODUCT_FAMILY_PAGE = "product-family";
    public static final String PAGE_TYPE_PRODUCT_SUB_CATEGORY_PAGE = "sub-category";
    /*Added for Page type -  Learn Page */
    public static final String PAGE_TYPE_LEARN_PAGE = "learn-page";
    public static final String PAGE_TYPE_PRODUCT_SKU_PAGE = "product-sku";
    public static final String PAGE_TYPE_GLOBAL_PRODUCT_SKU_PAGE = "global-product-sku";
    public static final String PAGE_TYPE_SITE_SEARCH_PAGE = "site-search";
    public static final String SUBCATEGORY_PRODUCT_GRID_RESOURCE_PATH = "/root/responsivegrid/product_grid";
    public static final String PAGE_PIM_PATH = "pimPagePath";
    public static final String PERCOLATE_ID = "percolate-content-id";
    public static final String PAGE_PIM_PATHS = "pimPagePaths";
    public static final String PRODUCT_FAMILY_PIM_DETAIL_OBJECT = "productFamilyPIMDetail";
    public static final String SITE_CONFIGURATION_OBJECT = "siteConfiguration";
    public static final String RESOURCE_LIST_DETAIL_OBJECT = "resourceListDetail";
    public static final String PRODUCT_FAMILY_DETAIL_OBJECT = "productFamilyDetail";
    public static final String ENDECA_STATUS_SUCCESS = "Success";
    public static final String SITE_CLOUD_CONFIG_NODE_NAME = "siteconfig";
    public static final String CATALOG_NO = "catalog_no";
    public static final String OVERVIEW_TAB = "Overview";
    public static final String OVERVIEW_TAB_ICON = "doc-text";
    public static final String SPECIFICATIONS_TAB = "Specifications";
    public static final String SPECIFICATIONS_TAB_ICON = "list-items";
    public static final String SPECIFICATIONS_TAB_SELECTOR = "specifications";
    public static final String RESOURCES_TAB = "Resources";
    public static final String VIEW_ADDITIONAL_RESOURCES_DEFAULT = "View additional resources";
    public static final String VIEW_ADDITIONAL_RESOURCES = "viewAdditionalResources";
    public static final String RESOURCES_TAB_ICON = "doc-download";
    public static final String RESOURCES_TAB_SELECTOR = "resources";
    public static final String MODEL_TAB_ICON = "tiles";
    public static final String HOW_TO_BUY_LABEL = "HowtoBuy";
    public static final String HOW_TO_BUY_LABEL_DEFAULT = "How to buy";
    public static final String DISCONTINUED_PRODUCT_HOW_TO_BUY_HEADLINE = "HowToBuy.SupportFromEaton";
    public static final String HOW_TO_BUY_HEADLINE_DEFAULT_VALUE = "How to buy from Eaton";
    public static final String DISCONTINUED_PRODUCT_HOW_TO_BUY_HEADLINE_DEFAULT_VALUE = "Support from Eaton";
    public static final String SKU_STATUS_Retired = "Retired";
    public static final String SUPPORT_LABEL = "Support";
    public static final String CONTACT_ME = "contactMe";
    public static final String CONTACT_ME_DEFAULT_VALUE = "CONTACT ME";
    public static final String MODULES_TAB_SELECTOR = "models";
    public static final String MODULES_TAB = "Models";
    public static final String PIM_PRIMARY_IMAGE = "primaryImage";
    public static final String SPIN_IMG = "/imgs/360_image/360_image_doc_links";
    public static final String PIM_PRIMARY_SUB_CATEGORY = "primarySubCategory";
    public static final String PIM_PRODUCT_NAME = "productName";
    public static final String PIM_MARKETING_DESCRIPTION = "marketingDesc";
    public static final String PIM_PDH_DESCRIPTION = "pdhProdDesc";
    public static final String PIM_CORE_FEATURES = "coreFeatures";
    public static final String PDH_PRODUCT_NAME = "pdhProdName";
    public static final String SPINNER_IMAGE = "spinnerImage";
    public static final String PRIMARY_SUB_CATEGORY_TAG = "primary-sub-category-tag";
    public static final String HOW_TO_BUY_HEADLINE = "headline";
    public static final String MORE = "more";
    public static final String MODELS = "models";
    public static final String PRODUCT_SUPPORT_TEXT = "productSupportText";
    public static final String PRODUCT_SUPPORT_DESCRIPTION = "productSupportDescription";
    public static final String ALL = "All";
    public static final String ATTRIBUTE_LIST_DETAIL_OBJECT = "attributeListDetail";
    public static final String DEFAULT_TAXONOMY_ATTRIBUTE_LABEL = "defaultTaxonomyAttributeLabel";
    public static final String DEFAULT_TAXONOMY_ATTRIBUTE_LABEL_DEFAULT = "defaultTaxonomyAttributeLabel";

    public static final int DEFAULT_STARTING_RECORD = 0;
    public static final int DEFAULT_PAGE_SIZE = 10;

	public static final String STATUS_DISCONTINUED = "Discontinued";
	public static final String STATUS_ACTIVE = "Active";
	public static final String PRODUCT_STATUS = "Status";
	public static final String STATUS_LABEL = "Product Status";
	public static final String CWC_PRODUCT_STATUS = "product-attributes_product-status";

    //SKU XML parsing Constants
    public static final String SKUCARD_XSD_FILENAME = "endecaSKUResponseSkucard.xsd";
    public static final String IMG_XSD_FILENAME = "endecaSKUResponseImg.xsd";
    public static final String CID_XSD_FILENAME = "CID.xsd";
    public static final String DAM_XSD_FILEPATH ="/content/dam/eaton/resources/endeca-stub-response/";
    public static final String UPSELL = "upsell";

    /** ChatConfig Component constants */

    public static final String BUSINESS_DIVISION = "businessdivision";
    public static final String URL_TO_CHAT = "urltochat";
    public static final String URL_TO_AGENT = "urltoagent";
    public static final String ONLINE_CONTENT = "onlinecontent";
    public static final String OFFLINE_CONTENT = "offlinecontent";
    public static final String ENTRY_POINT_ID = "entrypointid";
    public static final String AEM_TAGS = "aemtags";
    public static final String ERR_MSG = "errorMsg";
    public static final String CHAT_VIEWS = "chatviews";

    /** NanoRep- ChatConfig Component constants */

    public static final String NANOREP_BUSINESS_DIVISION = "nrbusinessdivision";
    public static final String NANOREP_KB_NUMBER = "nrentrypointid";
    public static final String NANOREP_AEM_TAGS = "nraemtags";
    public static final String NANOREP_ERR_MSG = "nrerrorMsg";
    public static final String NANOREP_CHAT_VIEWS = "chatviews";

    /** PIM NODE Properties **/
    public static final String COUNTRIES_FIELD = "countries";
    /** country Page  constants **/
    public static final String COUNTRY = "/country";

    /** HowToBuy properties **/
    /** ImageTransformation  constants */

    public static final String DESKTOP = "desktop";
    public static final String MOBILE = "mobile";
    public static final String TABLET = "tablet";
    public static final String SELECTOR = "selector";
    public static final String TRANSTORM = ".transform/";
    public static final String IMAGE_JPG = "/image.jpg";
    //the default values removed because default image transformation url implemented
    public static final String DEFALUT_DESKTOP = "No Image Transformation - desktop";
    public static final String DEFAULT_MOBILE = "No Image Transformation - mobile";
    public static final String DEFAULT_TABLET = "No Image Transformation - tablet";

    public static final String DEFAULT_DESKTOP_TRANSFORMATION = "default-desktop";
    public static final String DEFAULT_MOBILE_TRANSFORMATION = "default-mobile";
    public static final String DEFAULT_TABLET_TRANSFORMATION = "default-tablet";

    public static final String DEFAULT_DESKTOP_DYNAMIC_RENDITION = "default-desktop";
    public static final String DEFAULT_MOBILE_DYNAMIC_RENDITION = "default-mobile";
    public static final String DEFAULT_TABLET_DYNAMIC_RENDITION = "default-tablet";

    public static final String HOW_TO_BUY_TITLE = "howtoBuyTitle";
    public static final String HOW_TO_BUY_DESCRIPTION = "howtoBuyDesc";
    public static final String HOW_TO_BUY_ICON = "howtoBuyIcon";
    public static final String HOW_TO_BUY_DROPDOWN_ICON = "howtoBuyDropdownIcon";
    public static final String HOW_TO_BUY_LINK = "howtoBuyLink";
    public static final String HOW_TO_BUY_NEW_WINDOW = "howtoBuyNewWindow";
    public static final String ENABLE_SOURCE_TRACKING = "enableSourceTracking";

    /** SecondaryLinks properties **/

    public static final String SECONDARY_LINK_TEXT = "secLinkText";
    public static final String SECONDARY_LINK_URL = "secLinkURL";
    public static final String SECONDARY_LINK_NEW_WINDOW = "secLinkNewWindow";
    public static final String SECONDARY_LINK_SKU_ONLY = "secLinkSkuOnly";

    /** TopicLinkWithIcon properties **/

    public static final String TOPIC_LIST_WITH_ICON_TITLE = "resourceTitle";
    public static final String TOPIC_LIST_WITH_ICON_DESCRIPTION = "resourceDesc";
    public static final String TOPIC_LIST_WITH_ICON_ICON = "resourceIcon";
    public static final String TOPIC_LIST_WITH_ICON_LINK = "resourceLink";
    public static final String TOPIC_LIST_WITH_ICON_NEW_WINDOW = "resourceNewWindow";

    /** SecondaryLinks properties **/

    public static final String SUPPORT_INFO_TEXT = "supportText";
    public static final String SUPPORT_INFO_LABEL = "supportLabel";
    public static final String SUPPORT_INFO_COUNTRY = "supportCountry";

    public static final String SKU_CARD_ATTRIBUTE_NAME = "skuCardAttrValueDiff";

    /** PDH Node properties **/
    public static final  String PROPERTY_PDH_VALUE_CQ_DATA = "ValueCQDATA";
    public static final  String PROPERTY_PDH_REPFL = "REPFL";
    public static final  String SEQUENCE = "Sequence";
    public static final  String PROPERTY_PDH_LABEL = "LABEL";
    public static final  String NODE_PRODUCT_NAME = "product_name";
    public static final  String NODE_PDH_MKTG_DESC = "xxpdh_prd_fm_mar_ag";
    public static final  String NODE_CORE_FEATURES = "xxpdh_prd_fm_feature_ag";
    public static final  String NODE_PROD_FEATURE = "product_feature";
    public static final  String NODE_TECH_SUPPORT = "xxpdh_prd_fm_tech_support";
    public static final  String IMG_RENDITION = "/imgs/image_primary/image_primary_500x500_72dpi";
    public static final  String NODE_MKTG_DESC = "product_marketing_description";
    public static final  String NODE_TXNMY_ATTRS = "txnmy_atrbs";
    public static final String GLOBAL_ATTR_IMPORT_PATH = "/var/commerce/pdh/global-attributes";
    public static final String GLOBAL_ATTR_NAME = "attributeName";
    public static final String GLOBAL_ATTR_DISPLAY_NAME = "attributeDisplayName";

    /** Constants for new MetaTag values **/
    public static final  String APPLICATION_ID_META_TAG_NAME = "application-id";
    public static final  String APPLICATION_ID_ECJV = "ecjv";
    public static final String APPLICATION_ID_EATON = "eaton";
    public static final String APPLICATION_ID_LOGIN = "login";
    public static final String APPLICATION_ID_PHOENIXTEC ="phoenixtec";
    public static final String APPLICATION_ID_GREENSWITCHING ="greenswitching";
    public static final String APPLICATION_ID_REFERENCE_MICROSITE = "referenceMicrosite";
    public static final String APPLICATION_ID_DEMO_MICROSITE = "demoMicrosite";
    public static final String APPLICATION_ID_MISSING = "application-id-missing";
    public static final String DISCARD_APPLICATION_ID = "DiscardApplicationId";
    public static final String GREEN_SWITCHING = "greenswitching";
    public static final String X_DEFAULT = "x-default";
    public static final String APPLICATION_ID_SWITCHON = "switchon";

    /** DTM **/
    public static final  String DTM_DOWNLOAD_INVESTOR = "download-investors";
    public static final  String DTM_DOWNLOAD_ASSETS = "download-assets";
    public static final  String DTM_SEARCH_FACET = "search-facet";
    public static final  String DTM_MODEL_FACET = "model-facet";

    /** Cache **/
    public static final  String DISPATCHER = "Dispatcher";

    /**Externalizer**/
    public static final  String EXTERNALIZER_DOMAIN_EATON = "eaton";
    public static final  String EXTERNALIZER_DOMAIN_LOGIN = "login";
    public final static String EXTERNALIZER_DOMAIN_EATON_CUMMINS = "eaton-cummins";
    public final static String EXTERNALIZER_DOMAIN_PHOENIXTEC = "phoenixtec";
    public final static String EXTERNALIZER_DOMAIN_GREENSWITCHING = "greenswitching";
    public static final  String SHORT_URL_REMOVAL_PART = "/content/eaton/";
    public static final  String EXTERNALIZER_DOMAIN_SWITCHON = "switchon";

    /**Search Results Constants **/
    public static final  String SEARCH_DEFAULT_VIEW = "default";
    public static final  String SEARCH_SIMPLE_VIEW = "simple";
    public static final  String SEARCH_ADVANCED_VIEW = "advanced";
    public static final String SEARCH_RESULTS_PATH_FROM_JCR_CONTENT = "/root/responsivegrid/search_results";
    public static final String SEARCH_BOX_TEXT = "search_box";
    public static final  String SEARCH_CONTENT_TYPE_PRODUCT = "enableProductsTab";
    public static final  String SEARCH_CONTENT_TYPE_NEWS = "enableNewsTab";
    public static final  String SEARCH_CONTENT_TYPE_RESOURCES = "enableResourcesTab";
    public static final  String SEARCH_CONTENT_TYPE_SERVICES = "enableServicesTab";

    /**Content Hub Assets Constants **/
    public static final String DAM_MIME_TYPE_PROPERTY = "dam:MIMEtype";
    public static final String DAM_APPLICATION_PROPERTY = "dam:Application";
    public static final String DAM_THUMBNAIL_WEB = "cq5dam.web.1280.1280.jpeg";
    public static final String CONTENT_HUB_SHOW_FILTERS = "showFilters";
    public static final String CONTENT_HUB_SHOW_FILTERS_TAXONOMY = "showFiltersTaxonomy";
    public static final String CONTENT_HUB_DEFAULT_IMAGE_PATH = "defaultImagePath";
    public static final String CONTENT_HUB_ENABLE_PUBLICATION_DATE = "enablePublicationDate";
    public static final String CONTENT_HUB_SORT_BY = "sortBy";

    /**Custom Vanity implementation constants **/
    public static final String DEFAULT_PAGE_PATH = "defaultPage";
    public static final String ADDITIONAL_PAGE_PATH = "additionalPage";
    public static final String DEFAULT_PAGE_URL_PARAM = "?defaultPage=";
    public static final String HOME_PAGE_URL_PARAM = "&homePage=";
    public static final String EATON_DOMAIN= "eaton.com";
    public static final String LOGIN_DOMAIN= "login.eaton.com";
    public static final String EATON_CUMMINS_DOMAIN= "eaton-cummins.com";
    public static final String GREEN_SWITCHING_DOMAIN= "greenswitching.com";
    public static final String PHOENIX_TEC_POWER_DOMAIN= "phoenixtecpower.com";
    public static final String JSON_EXTN= ".json";
    public static final String PRIMARY_VANITY= "primaryVanity";
    public static final String CREATED_DATE= "createdDate";
    public static final String LAST_MODIFIED_DATE= "lastModifiedDate";
    public static final String DATA= "data";
    public static final String VANITY_URL= "vanity_url";
    public static final String VANITY_URL_LIST = "vanityUrlList";
    public static final String DEF_PUBLISH_PAGE = "defPublishPage";
    public static final String ADD_PUBLISH_PAGE= "addPublishPage";
    public static final String PUBLISHING_DATE= "publishDate";
    public static final String PUBLISHING_DATE_FORMAT= "yyyy-MM-dd hh:mm:ss:SSS";
    public static final String GROUP_ID = "groupId";
    public static final String DOMAIN_NAME = "domainName";
    public static final String DOMAIN = "domain";
    public static final String OPERATION = "operation";
    public static final String STATUS= "status";
    public static final String  NOTES= "notes";
    public static final String LOGIN_DOMAIN_TEXT= "login";



    /** The Constant ELOQUA_CLOUD_CONFIG_NODE_NAME. */
    public static final String ELOQUA_CLOUD_CONFIG_NODE_NAME = "eloquaconfig";

    /** The Constant PIM_PATH. */
    public final static String PIM_PATH = "/var/commerce/products/eaton/";

    /** The Constant PIM_PATH PHOENIXTEC */
    public final static String PIM_PATH_PHOENIXTEC = "/var/commerce/products/phoenixtec/";

    /** The Constant PIM_PATH_CUMMINS. */
    public final static String PIM_PATH_CUMMINS = "/var/commerce/products/eaton_cummins";

    /** The Constant PIM_PATH_REFERENCE_MICROSITE. */
    public final static String PIM_PATH_REFERENCE_MICROSITE = "/var/commerce/products/microsites/reference-microsite";

    /** The Constant PIM_PATH_DEMO_MICROSITE. */
    public final static String PIM_PATH_DEMO_MICROSITE = "/var/commerce/products/microsites/demo-microsite";

    /** The Constant ENGLISH_LANGUAGE. */
    public final static String ENGLISH_LANGUAGE = "en_us";

    public static final String CONTENT_DAM_EATON = "/content/dam/eaton";
    public static final String CONTENT_DAM_EATON_CUMMINS = "/content/dam/eaton-cummins";
    public static final String CONTENT_DAM_PHOENIXTEC = "/content/dam/phoenixtec";

    public static final String EN_LANG = "en";

    public static final String GLOBAL = "global";

    public static final String UNDER_SCORE = "_";

    public static final String EQUALS_SYMBOL = "=";

    public static final String ITEM_PARAM = "item";

    public static final String ITEM_PARAM_NAME_EQUALS = "item=";

    public static final String EN_US = "en-us";

    public static final String PRODUCT_FAMILY_PAGE = "productFamilyPage";


    public static final String US_EN_US ="us/en-us";

    public static final String US_STRING ="us";

    public static final String PRIMARY_SUB_CATEGORY_MULTI = "primarySubCategoryMulti";

    public final static String SKUID_URL_TOKEN = "{SKUID}";

    public final static String FACET_GROUP = "facetGroup";

    public final static String FACET_VALUE = "facetValue";

    public static final String CONTACT_DISPLAY_LIST_METHOD_ANY = "any";
    public static final String CONTACT_DISPLAY_LIST_METHOD_ALL = "all";

    public static final String ANY = "any";
    public static final String DEFAULT = "default";
    public static final String BLANK_CONSTANT = "_blank";


    /** Email notification constant**/
    public final static String EMAIL = "./profile/email";
    public final static String PAGE_LINK="pageLink";
    public final static String EMAIL_TEMPLATE_FOLDER_PATH = "emailTemplate";
    public final static String EMAIL_TEMPLATE_SUBJECT_ROLLOUT = "emailSubjectRollout";
    public final static String EMAIL_TEMPLATE_SUBJECT_PUBLISH = "emailSubjectPublish";
    public final static String SEND_EMAIL ="sendEmail";
    public final static String EMAIL_TEMPLATE_CONTENT_ROLLOUT = "emailContentRollout";
    public final static String EMAIL_TEMPLATE_CONTENT_PUBLISH = "emailContentPublish";
    public final static String CQ_LAST_MODIFIED = "cq:lastModified";
    public final static String CQ_LAST_ROLLED_OUT = "cq:lastRolledout";
    public final static String PARTICIPANT_TIME_OUT = "absoluteTime";
    public final static String AUTHOR ="author";
    public final static String NOTIFICATION_LINK ="notificationLink";
    public final static String SEND_EMAIL_FOR_ROLL_OUT = "sendEmailForRollout";
    public final static String SEND_EMAIL_FOR_PUBLISH = "sendEmailForPublish";
    public final static String TIME_STARTED = "timeStarted";
    public final static String JCR_PATH = "JCR_PATH";
    public final static String EMAIL_GROUP_FOR_NOTIFICATION = "emailGroupForNotification";
    public final static String CONTENT_ROOT_FOLDER_PATH_PROPERTY_NAME="contentRootFolderPath";
	public final static String LANGUAGE_WITH_COUNTRY="languageWithCountry";
    public final static String STRING_TO = "to";
    public final static String STRING_SUBJECT = "subject";
    public final static String LIST_OF_LINKS = "listOfLinks";
    public static final String ASSET_EMAIL_TEMPLATE_PATH = "/etc/notification/email/html/com.eaton.links.email/assetLinksTemplate.html";

    /** Eaton Topic Market */
    private static final String EATON_MARKET_TAG_PATH =  "/content/cq:tags/eaton/supermarkets/markets";
    /** Eaton Cummins Topic Market */
    private static final String CUMMINS_MARKET_TAG_PATH =  "/content/cq:tags/eaton-cummins/supermarkets/markets";

    /** Eaton global attr import path . */
    private static final  String TAG_ATTR_IMPORT_PATH_EATON = "/content/cq:tags/eaton/product-attributes";

    /** Eaton Cummins global attr import path. */
    private static final  String TAG_ATTR_IMPORT_PATH_EATON_CUMMINS = "/content/cq:tags/eaton-cummins/product-attributes";

    /** Eaton sector business unit tag path. */
    public static final String SECTOR_BUSINESS_UNIT_TAG_PATH="/content/cq:tags/eaton/eaton-brand/industry";

    /** Eaton country tag path. */
    public static final String COUNTRY_TAG_PATH="/content/cq:tags/eaton/country";

    public static final String ACTIVE_TAG_PATH = "/content/cq:tags/eaton/product-attributes/product-status/active";
    public static final String DISCONTINUED_TAG_PATH = "/content/cq:tags/eaton/product-attributes/product-status/discontinued";

    public static final String GUIDE_CONTAINER = "fd/af/components/guideContainerWrapper";
    public static final String REDIRECT = "redirect";/** The ConstantPIM_PATH. */

    public final static String PRODUCT_FAMILY_BASE_PATH = "/var/commerce/pdh/product-family/";
    public static final String SERVICE_PID ="(service.factoryPid=com.eaton.platform.core.services.impl.EatonNotifyServiceFactoryConfig)";
    public static final String EATON_AUTO_PUBLISH_ROLLOUT_FOR_EACH_COUNTRY="/var/workflow/models/eaton-workflows/Eaton-Auto-Publish-rollout-for-each-country";
    public static final String NOTIFICATION_INBOX_LINK = "/aem/inbox";
    public static final String PROP_EXTERNALIZER_DOMAIN = "externalizer.domain";
    public static final String DEFAULT_EXTERNALIZER_DOMAIN = CommonConstants.EXTERNALIZER_DOMAIN_EATON;

    public static final String SUBCATEGORY = "subCategory";
    public static final String PRODUCT_FAMILY = "productFamily";
    public static final String JCR_CONTENT_METADATA = "jcr:content/metadata";
    public static final String JCR_CONTENT_DATA_MODEL = "jcr:content/data/cq:model";
    public static final String JCR_CONTENT_CONTENT_FRAGMENT = "jcr:content/contentFragment";
    public static final String JCR_CONTENT_DATA_MASTER = "jcr:content/data/master/";
    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_JSON_UTF_8 = "application/json;charset=UTF-8";
    public static final String APPLICATION_FORM_URLENCODED ="application/x-www-form-urlencoded";
    public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
    public static final String FILE_TYPE_IMAGE_JPEG = "image/jpeg";
    public static final String FILE_TYPE_IMAGE_JPEG_VALUE = "jpg";
    public static final String FILE_TYPE_APPLICATION_JAVASCRIPTS = "application/javascript";
    public static final String FILE_TYPE_APPLICATION_JAVASCRIPTS_VALUE = "javascript";
    public static final String FILE_TYPE_TEXT_HTML = "text/html";
    public static final String FILE_TYPE_TEXT_HTML_VALUE = ".html";
    public static final String FILE_TYPE_HTML = "html";
    public static final String FILE_TYPE_IMAGE_GIF = "image/gif";
    public static final String FILE_TYPE_IMAGE_GIF_VALUE ="gif";
    public static final String FILE_TYPE_APPLICATION_XML = "application/xml";
    public static final String FILE_TYPE_APPLICATION_XML_VALUE = "xml";
    public static final String FILE_TYPE_APPLICATION_MS_POWERPOINT = "application/vnd.ms-powerpoint";
    public static final String FILE_TYPE_APPLICATION_MS_POWERPOINT_VALUE = "ppt";
    public static final String FILE_TYPE_APPLICATION_MS_EXCEL ="application/vnd.ms-excel";
    public static final String FILE_TYPE_APPLICATION_MS_EXCEL_VALUE ="xls";
    public static final String FILE_TYPE_APPLICATION_PDF = "application/pdf";
    public static final String FILE_TYPE_APPLICATION_PDF_VALUE = "pdf";
    public static final String FILE_TYPE_APPLICATION_POSTSCRIPT = "application/postscript";
    public static final String FILE_TYPE_APPLICATION_EPS_VALUE = "eps";
    public static final String FILE_TYPE_EPS = "/eps";
    public static final String FILE_TYPE_X_EPS = "/x-eps";
    public static final String CONTENT_DISPOSITION = "Content-disposition";
    public static final String EXTENSION_JSON = "json";
    public static final String AMPERSAND = "&";
    public static final String ARRAY_EMPTY = "[]";

    // TODO: micro site home page template path need to be updated
    /*Micro site template constants Start*/
    private static final String MICROSITE_HOME_PAGE_TEMPLATE_PATH = "/conf/eaton/settings/wcm/templates/eaton-microsite-homepage";
    private static final String MICROSITE_HOME_PAGE_TEMPLATE = "microsite-homepage-template";
    /*Micro site template constants End*/

    public static final List<String> siteRootPathConfig = Arrays.asList(
            CONTENT_ROOT_FOLDER_CUMMINS,
            CONTENT_ROOT_FOLDER,
            CONTENT_ROOT_FOLDER_REFERENCE_MICROSITE,
            CONTENT_ROOT_FOLDER_DEMO_MICROSITE,
            CONTENT_ROOT_FOLDER_GREENSWITCHING,
            CONTENT_ROOT_FOLDER_PHOENIXTEC,
            CONTENT_ROOT_FOLDER_LOGIN,
            CONTENT_ROOT_FOLDER_SWITCHON);
    public static final List<String> damRootPathConfig = Arrays.asList(CONTENT_DAM_EATON_CUMMINS, CONTENT_DAM_EATON,CONTENT_DAM_PHOENIXTEC);
    public static final Map<String, String> siteRootPathConfigForExternalizer;

    static {
        siteRootPathConfigForExternalizer = new HashMap<>();
        siteRootPathConfigForExternalizer.put(CONTENT_ROOT_FOLDER,EXTERNALIZER_DOMAIN_EATON);
        siteRootPathConfigForExternalizer.put(CONTENT_ROOT_FOLDER_LOGIN,EXTERNALIZER_DOMAIN_LOGIN);
        siteRootPathConfigForExternalizer.put(CONTENT_ROOT_FOLDER_CUMMINS,EXTERNALIZER_DOMAIN_EATON_CUMMINS);
        siteRootPathConfigForExternalizer.put(CONTENT_ROOT_FOLDER_PHOENIXTEC,EXTERNALIZER_DOMAIN_PHOENIXTEC);
        siteRootPathConfigForExternalizer.put(CONTENT_ROOT_FOLDER_GREENSWITCHING,EXTERNALIZER_DOMAIN_GREENSWITCHING);
        siteRootPathConfigForExternalizer.put(CONTENT_ROOT_FOLDER_SWITCHON,EXTERNALIZER_DOMAIN_SWITCHON);
    }

    public static final Map<String, String> siteMarketTagPathConfig;

    static {
        siteMarketTagPathConfig = new HashMap<>();
        siteMarketTagPathConfig.put(CONTENT_ROOT_FOLDER, EATON_MARKET_TAG_PATH);
        siteMarketTagPathConfig.put(CONTENT_ROOT_FOLDER_CUMMINS, CUMMINS_MARKET_TAG_PATH);
    }

    public static final Map<String, String> rootPathApplicationIdConfig;
    static {
        rootPathApplicationIdConfig = new HashMap<String, String>();
        rootPathApplicationIdConfig.put(CONTENT_ROOT_FOLDER, APPLICATION_ID_EATON);
        rootPathApplicationIdConfig.put(CONTENT_ROOT_FOLDER_LOGIN, APPLICATION_ID_LOGIN);
        rootPathApplicationIdConfig.put(CONTENT_ROOT_FOLDER_CUMMINS, APPLICATION_ID_ECJV);
        rootPathApplicationIdConfig.put(CONTENT_ROOT_FOLDER_REFERENCE_MICROSITE, APPLICATION_ID_REFERENCE_MICROSITE);
        rootPathApplicationIdConfig.put(CONTENT_ROOT_FOLDER_DEMO_MICROSITE, APPLICATION_ID_DEMO_MICROSITE);
        rootPathApplicationIdConfig.put(CONTENT_ROOT_FOLDER_PHOENIXTEC, APPLICATION_ID_PHOENIXTEC);
        rootPathApplicationIdConfig.put(CONTENT_DAM_PHOENIXTEC, APPLICATION_ID_PHOENIXTEC);
        rootPathApplicationIdConfig.put(CONTENT_DAM_EATON, APPLICATION_ID_EATON);
        rootPathApplicationIdConfig.put(CONTENT_DAM_EATON_CUMMINS, APPLICATION_ID_ECJV);
        rootPathApplicationIdConfig.put(CONTENT_ROOT_FOLDER_SWITCHON, APPLICATION_ID_SWITCHON);
    }

    public static final Map<String, String> globalAttributeImportPathConfig;

    static {
        globalAttributeImportPathConfig = new HashMap<>();
        globalAttributeImportPathConfig.put(CONTENT_ROOT_FOLDER, TAG_ATTR_IMPORT_PATH_EATON);
        globalAttributeImportPathConfig.put(CONTENT_ROOT_FOLDER_CUMMINS, TAG_ATTR_IMPORT_PATH_EATON_CUMMINS);
    }

    public static final List<String> productRootPathConfigList = Arrays.asList(PIM_PATH,
            PIM_PATH_CUMMINS,PIM_PATH_REFERENCE_MICROSITE,PIM_PATH_DEMO_MICROSITE,PIM_PATH_PHOENIXTEC);

    public static final Map<String, String> productRootPathConfigMap;

    static {
        productRootPathConfigMap = new HashMap<>();
        productRootPathConfigMap.put(CONTENT_ROOT_FOLDER,PIM_PATH);
        productRootPathConfigMap.put(CONTENT_ROOT_FOLDER_CUMMINS,PIM_PATH_CUMMINS);
        productRootPathConfigMap.put(CONTENT_ROOT_FOLDER_PHOENIXTEC,PIM_PATH_PHOENIXTEC);
        productRootPathConfigMap.put(CONTENT_ROOT_FOLDER_REFERENCE_MICROSITE,PIM_PATH_REFERENCE_MICROSITE);
        productRootPathConfigMap.put(CONTENT_ROOT_FOLDER_DEMO_MICROSITE,PIM_PATH_DEMO_MICROSITE);
    }

    public static final Map<String, String> microSiteHomePageTemplatePathConfig;

    static {
        microSiteHomePageTemplatePathConfig = new HashMap<>();
        microSiteHomePageTemplatePathConfig.put(MICROSITE_HOME_PAGE_TEMPLATE, MICROSITE_HOME_PAGE_TEMPLATE_PATH);
    }

    public static final List<String> tagNameSpaceConfig=Arrays.asList(TAG_NAMESPACE_EATON,TAG_NAMESPACE_EATON_CUMMINS,TAG_NAMESPACE_PHOENIXTEC);
    public static final List<String> categoryIdTagNameSpace=Arrays.asList(TAG_NAMESPACE_RESOURCES,TAG_NAMESPACE_PRODUCT_TAXONOMY,TAG_NAMESPACE_MYEATON_TAXONOMY,
            TAG_NAMESPACE_SERVICES_TAXONOMY,TAG_NAMESPACE_SERVICES_TAXONOMY,TAG_NAMESPACE_SUPERMARKETS,
            TAG_NAMESPACE_EATON_BRAND,TAG_NAMESPACE_SUPPORT_TAXONOMY);

    public static final List<String> vanityDomain=Arrays.asList(EXTERNALIZER_DOMAIN_EATON, EXTERNALIZER_DOMAIN_EATON_CUMMINS,
            EXTERNALIZER_DOMAIN_GREENSWITCHING,EXTERNALIZER_DOMAIN_PHOENIXTEC);

    /*
     *categoryIdMetaTagExcludedPageTypes will contain the list of page types which should not display category id in meta tag view source.
     */
    public static final List<String> categoryIdMetaTagExcludedPageTypes = Arrays.asList(PAGE_TYPE_HOME_PAGE,PAGE_TYPE_PRIMARY_PAGE,PAGE_TYPE_SITEMAP_PAGE,PAGE_TYPE_SITE_SEARCH_PAGE);
    public static final String PRODUCT_GRID_RESOURCE_TYPE = "eaton/components/product/product-grid";
    public static final String PRODUCT_SELECTOR_FORM_RESOURCE_TYPE = "eaton/components/form/product-selector-form";
    public static final String PRODUCT_SELECTOR_FORM_DROPDOWN_RESOURCE_TYPE = "eaton/components/form/productselectordropdown";
    public static final String GUIDED_PRODUCT_SELECTOR_FORM_DROPDOWN_RESOURCE_TYPE = "eaton/components/form/guidedproductselection";
    public static final String PRODUCT_TABS_RESOURCE_TYPE = "eaton/components/product/product-tabs";
    public static final String GUIDE_CONTAINER_RESOURCE_TYPE = "fd/af/components/guideContainerWrapper";

    public static final String PROPERTY_NAME = "name";
    public static final String PROPERTY_VALUE_CQ_COMMERCE_TYPE = "product";
    public static final String PIM_CONTENT = "pimcontent";
    public static final String PIM_BASE = "/var/commerce/products";
    public static final String CURRENT_PATH = "currentPath";
    public static final String DESTINATION_PATH = "destinationPath";
    public static final String REFERENCE_UPDATE_JOB_TOPIC = "eaton/sling/job/referenceUpdate";
    public static final String COUNTRY_SITE_CREATION_JOB_TOPIC = "eaton/sling/job/countrySiteCreation";
    public static final String ROLLOUT_CONFLICT_REPORT_JOB_TOPIC = "eaton/sling/job/rolloutConflict";
    public static final String PRAGMA = "Pragma";
    public static final String EXPIRES = "Expires";

    public static final String BRIGHTCOVE_VIDEO_RESOURCE_TYPE = "eaton/components/content/brightcoveplayer";
    public static final String RESOURCE_LIST_RESOURCE_TYPE = "eaton/components/product/resource-list";
    public static final String ELOQUA_FROM_RESOURCE_TYPE = "eaton/components/content/eloqua-form";

    //SiteMap Constant//
    public static final String SITEMAP_ROOT_PATH = "sitemapRootPath";
    public static final String PROP_NAME = "1_property";
    public static final String PROP_VALUE = "1_property.value";
    public static final String RSRC_TYPE_CONTENT_SITEMAP = "eaton/components/content/sitemap";
    public static final String JCR_CONTENT_PAGE_TYPE = "jcr:content/page-type";

    public static String PRICE_DISCLAIMER_I18N_KEY = "priceDisclaimer";
    public static String PRICE_DISCLAIMER_NO_ASTERISK_I18N_KEY = "priceDisclaimerNoAsterisk";
    public static String I18N_SPECIFICATION_TAB = "specification";
    public static String I18N_RESOURCE_TAB = "resource";

    //Reference Constant//
    public static final String SECOND_PROP_NAME = "2_property";
    public static final String SECOND_PROP_VALUE = "2_property.value";

    public static final String THIRD_PROP_NAME = "3_property";
    public static final String THIRD_PROP_OPERATION = "3_property.operation";

    public static final String NOT = "not";
    public static final String SLING_RESOURCETYPE = "sling:resourceType";

    public static final int HTTP_SUCCESS_MIN = 200;
    public static final int HTTP_SUCCESS_MAX = 299;

    //Content hub constant
    public static final String CONTENT_HUB_RESOURCE_TYPE  = "eaton/components/content/content-hub";
    public static final String CONTENT_HUB_TAG_PATH  = "eaton:search-tabs/page-type/content-hub";

	public static final String DISCONTINUE_TAG  = "/product-attributes/product-status/discontinued";
    public static final String DISCONTINUE_TAG_PATH  = "eaton:product-attributes/product-status/discontinued";

    /** The Constant DATE_TIME_FORMAT_PUBLISH. */
    public static final  String DATE_TIME_FORMAT_PUBLISH = "MMMM d, yyyy HH:mm:ss.SSSXXX";

    /** The Constant DEFAULT_DATE. */
    public static final  String DEFAULT_DATE_TIME = "January 01, 0000 00:00:00.000+00:01";

    public static final String CROSS_REFERENCE_RESOURCE_TYPE = "eaton/components/product/cross-reference";

    /** The Constant Percolate Content ID. */
    public static final String PERCOLATE_CON_ID = "percolate-content-id";

	public static final String ACCOUNT_TYPE = "accountType";
	public static final String PRODUCT_CATEGORIES = "productCategories";
	public static final String APPLICATION_ACCESS = "applicationAccess";
	public static final String COUNTRIES = "countries";
	public static final String PARTNER_PROGRAMME_TYPE = "partnerProgrammeType";
	public static final String TIER_LEVEL = "tierLevel";


    /** host string added : The Constant Percolate Content ID */
    public static final String PERCOLATE_POST_NAME = "post:";
    public static final int LIST_TYPE_SIZE =3;
    public static final String SEARCH_RESULT_PATH="/root/responsivegrid/search_results";

    /** i18 Key Name for Disclaimer Heading */
    public static final String SIGN_UP_NO_ASTERISK_I18N_KEY="Disclaimer.heading";

    /** i18 Key Name for Disclaimer Heading default value */
    public static final String SIGN_UP_NO_ASTERISK_I18N_DEFAULTVALUE="Sign up to recieve updates";

    /** i18 Key Name for Disclaimer Message */
    public static final String DISCLAIMER_MESSAGE_I18N_KEY="Disclaimer.message";

    /** Key Name for Israel old language locale */
    public static final String ISRAEL_OLD_LOCALE="iw";

    /** Key Name for Israel new language locale */
    public static final String ISRAEL_NEW_LOCALE="he";

    /** Key Name for Israel country locale */
    public static final String ISRAEL_LOCALE="IL";

    public static final String INDONESIA_OLD_LOCALE="in";

    public static final String INDONESIA_NEW_LOCALE="ID";

    /** SECURE PROP NAMES */
    public final static String SECURE_GENERIC_TEMPLATE = "secure-generic-page";
    public final static String SECURE_CATEGORY_TEMPLATE_NAME = "category-page";
    public final static String SECURE_CATEGORY_WITH_CARDS_TEMPLATE_NAME = "category-with-cards";
    public final static String SECURE_PRODUCT_FAMILY_TEMPLATE_NAME = "product-family-page";
    public final static String SECURE_TEMPLATE_NAME = "secure";
    public final static String SECURE_PROP_NAME_SECURE_PAGE = "securePage";
    public final static String SECURE_PROP_NAME_ACCOUNT_TYPE = "accountType";
    public final static String SECURE_PROP_NAME_PRODUCT_CATEGORIES = "productCategories";
    public final static String SECURE_PROP_NAME_APPLICATION_ACCESS = "applicationAccess";
    public final static String LOGIN_PAGE_REDIRECT_COOKIE_ID = "login-page-redirect";
    public final static String SECURE_PROP_NAME_TIER_LEVEL = "tierLevel";
    public final static String SECURE_PROP_NAME_PARTNER_PROGRAMME_TYPE = "partnerProgrammeType";
    public final static String SECURE_PROP_NAME_PARTNER_PROGRAMME_AND_TIER_LEVEL_TYPE = "partnerProgramAndTierLevel";

    /** Uncached Seclector Constant */
    public static final String UN_CACHED_SELECTOR = ".uncached";

    // Sitemap Constants
    public static final String SITEMAP_FULL_PAGE = "sitemap-full";

    public static final String PIM_PDH_PRIMARY_IMAGE = "pdhPrimaryImg";

    public static final String EATON_NEWS_TOPIC_TAG_PATH = "/content/cq:tags/eaton/news-releases/Topic";

    public static final String COUNTRY_TAG_ROOT = "eaton:country";

    public static final String DAM_1280_RENDITION = ".1280";
    public static final String DAM_THUMBNAIL_KEY = "thumbnail";
    public static final String DAM_ASSET_PUBLICATION_DATE ="assetPublicationDate";

    public static final String DAM_PATH = "path";
    public static final String METADATA_CQ_TAGS = "jcr:content/metadata/cq:tags";
    public static final String PROPERTY = "0_property";
    public static final String LIKE = "like";
    public static final String PROPERTY_OPERATION = "0_property.operation";
    public static final String UNDERSCORE_PROPERTY = "_property";
    public static final String PROPERTY_VALUE = "_property.value";
    public static final String SEARCH_VIEW_TYPE = "viewType";
    public static final String CZCS_DASHBOARD ="/cz/cs-cz/secure/dashboard.html";
    /**
     * Sitemap Query Builder Constants
     */
    public static final String PARAM_LOCALE="locale";
    public static final String PARAM_RANGE="range";
    public static final String QUERY_PATH="path";
    public static final String QUERY_TYPE="type";
    public static final String QUERY_PROPERTY_1_KEY="1_property";
    public static final String QUERY_PROPERTY_1_VALUE="1_property.value";
    public static final String QUERY_PROPERTY_KEY="property";
    public static final String QUERY_PROPERTY_2_KEY="2_property";
    public static final String QUERY_PROPERTY_VALUE_1="property.1_value";
    public static final String QUERY_PROPERTY_VALUE_2="property.2_value";
    public static final String QUERY_PROPERTY_2_VALUE="2_property.value";
    public static final String QUERY_PROPERTY_3_KEY="3_relativedaterange.property";
    public static final String QUERY_DATE_RAGE_UPPERBOUND="3_relativedaterange.upperBound";
    public static final String QUERY_DATE_RAGE_LOWERBOUND="3_relativedaterange.lowerBound";
    public static final String QUERY_PROPERTY_JCR_LASTMODIFED="jcr:content/jcr:lastModified";
    public static final String QUERY_PROPERTY_LANGUAGE="jcr:content/metadata/cq:tags";
    public static final String QUERY_LIMIT="p.limit";
	public static final String EXCLUDE_COUNTRY_LIST = "excludeCountryList";

	public static final String EXCLUDE_COUNTRY_PROPERTY = "excludeCountries";

    /** Image Serving Constants  */
    public static final String IS_IMAGE = "is/image";
    public static final String IS_CONTENT = "is/content";

    /** The Constant PUBLISH_STATUS. */
    public static final String PUBLISH_STATUS = "publishStatus";

	/** Product Comparison. */
	public static final String PRODUCT_NAME="Product Name";
    public static final String PRODUCT_ID="Product_Name";
    public static final String CATALOG_NUMBER="Catalog Number";
    public static final String CATALOG_ID="Catalog_Number";
    public static final String JSON_KEY="ProductComp";
    public static final String PROD_COMP_KEY="_Pr@ductC@mpT@R_";

    public static final String PRODUCT_SPEC="Product specifications";
    public static final String PRODUCT_PARAM_AJAX="value1";
	public static final int THREE = 3;
	public static final int FIFTY = 50;
	public static final String PRODUCT_PARAM_DIFF="_different";

	public static final String EATON_EDIT_TEMPLATE_PAGE = "eaton/components/structure/eaton-edit-template-page";
	public static final String EATON_REGISTRATION_TEMPLATE_PAGE = "eaton/components/structure/eaton-registration-template-page";
	public static final String EATON_EDIT_TEMPLATE_LOGIN_PAGE = "eaton/components/structure/eaton-edit-template-login-page";
	public static final String EATON_CUMMINS_EDIT_TEMPLATE_PAGE = "eaton/components/structure/eaton-cummins-edit-template-page";

    public static final String PRIMARY_PRODUCT_TAXONOMY = "primaryProductTaxonomy";

    /**View Type Constants */
    public static final String VIEW_TYPE_GRID="grid";
    public static final String DISPLAY_SHOW="show";
    public static final String FIELD_TYPE_RADIO="radio";

    /** The Constant CATEGORIES_. */
    public static final String CATEGORIES_ = "Categories_";
    public static final String CATEGORIES_GENERAL_SPEC = "General specifications_Pr@ductC@mpT@R_";

    /** Asset Reditions */
    public static final String ASSET_1280_1280_THUMBNAIL = ".thumb.1280.1280.png";
    public static final String ASSET_319_319_THUMBNAIL = ".thumb.319.319.png";
    public static final String ASSET_140_100_THUMBNAIL = ".thumb.140.100.png";
    public static final String ASSET_48_48_THUMBNAIL = ".thumb.48.48.png";
    public static final String ASSET_1280_VIEW_PORT = ".1280";
    public static final String ASSET_319_VIEW_PORT  = ".319";
    public static final String ASSET_140_VIEW_PORT  = ".140";
    public static final String ASSET_48_VIEW_PORT  = ".48";
    public static final  String SHA = "xmp:eaton-sha";

    /*
    * Track Download Constants
    */
    public static final  String ASSET_TRACK_DOWNLOAD = "trackDownload";
    public static final  String IVALID_INPUT_RECEIVED = "Invalid Input Received";

    /**
     * Product compatibility constants
     */
    public static final String TST_EXT_LAMP_MODEL_NUMBER =  "TST_Ext_Lamp_Model_Number";
    public static final String TST_EXT_TYPE =  "TST_Ext_Type";
    public static final String TST_CONTROL_FAMILY =  "TST_Control_Family";
    public static final String TST_LOWES_ITEMS_NUMBER =  "TST_Lowes_Item_Number";
    public static final String TST_LOWES_MODEL_NUMBER =  "TST_Lowes_Model_Number";
    public static final String TST_LOW_END_TRIM =  "TST_Low_end_trim";
    public static final String TST_EXT_LAMP_VOLTAGE =  "TST_EXT_Lamp_Voltage";
    public static final String TST_EXT_COLOR_TEMPERATURE =  "TST_EXT_Color_Temperature";
    public static final String TST_LED_COMPATIBLE_SCORE =  "TST_LED_Compatible_Score";
    public static final String TST_EXT_BASE =  "TST_EXT_Base";
    public static final String TST_EXT_DATE_CODE =  "TST_EXT_Date_Code";
    public static final String TST_EXT_LAMP_LUMEN_OUTPUT =  "TST_EXT_Lamp_Lumen_Output";
    public static final String TST_FLICKER =  "TST_Flicker";
    public static final String TST_LED_RATING =  "TST_LED_Rating";
    public static final String TST_LOW_END =  "TST_Low_End";
    public static final String TST_RELATIVE_LOW_END =  "TST_Relative_Low_End";
    public static final String TST_PERCEIVED_DIMMING_RANGE =  "TST_Perceived_Dimming_range";
    public static final String TST_PERCEIVED_LOW_END =  "TST_Perceived_Low_end";
    public static final String TST_TEST_NOTES =  "TST_Test_Notes";
    public static final String TST_MAX_OF_LOADS = "TST_Max_of_loads";
    public static final String TST_EXT_IMAGE_URL = "TST_EXT_Image_URL";
    public static final String TST_FAMILY_ID = "TST_family_id";
    public static final String ATTRIBUTELIST  ="attributeList";
    public static final String CURRENT_LANGUAGE = "currentLanguage";
    public static final String CURRENT_COUNTRY = "currentCountry";
    public static final String PRIMARYSUBCATEGORYTAG = "primarySubCategoryTag";
    public static final String PAGESIZE = "pageSize";
    public static final String LANGUGAGEMASTERS = "language-masters";
    public static final String CURRENTCOUNTRY = "currentCountry";
    public static final String CURRENTLOADMORE = "currentLoadMore";
    public static final String CURRENTRESOURCEPATH = "currentResourcePath";
    public static final String PIMPRIMARYIMAGE = "pimPrimaryImage";
    public static final String SELECTORS = "selectors";
    public static final String REQUESTURI = "requestUri";
    public static final String PRODUCTTYPE = "productType";
    public static final String CURRENTPAGEPATH = "currentPagePath";
    public static final String FALLBACKIMAGE = "fallBackImage";
    public static final String DATAATTRIBUTE = "dataAttribute";
    public static final String COMPONENTVIEW = "componentView";
    public static final String COMPONENT = "component";
    public static final String PRODUCTTYPES = "productTypes";
    public static final String PRODUCT_TYPES = "Product Types";
    public static final String EMAIL_TEMPLATE_PAH="/etc/notification/email/html/com.eaton.generic.email/dataSheetsTemplate.html";
    public static final String JSON_KEY_EMAILSTATUS="EmailStatus";
    public static final String EMAIL_STATUS_SUCCESS = "success";
    public static final String EMAIL_STATUS_FAIL = "fail";
    public static final String JSON_KEY_DATASHEET="DatasheetGen";
    public static final String PDF_EXTN = ".pdf";
    public static final String DOT = ".";
    public static final String OPEN_SQUARE_BRACKET = "[";
    public static final String CLOSE_SQUARE_BRACKET = "]";
    public static final String EMPTY_STR = " ";
    public static final String ASSET_ORIGINAL_RENDITION_PATH = "/jcr:content/renditions/original/jcr:content";
    public static final String JCR_DATA = "jcr:data";
    public static final String OPEN_CURLY_BRACKET = "{";
    public static final String CLOSE_CURLY_BRACKET = "}";
    public static final String LT_PERCENT_BRACKET = "<%";
    public static final String GT_PERCENT_BRACKET = "%>";
    /*
    Replication queue alert constants
    */
    public static final String REPLICATION_EMAIL_TEMPLATE_PATH="/etc/notification/email/html/com.eaton.generic.email/replicationQueueAlertTemplate.html";
    public static final String EMAIL_SUBJECT="Replication queue is blocked for ";
    public static final String EMAIL_BODY="Replication queue is blocked.";
    public static final String NO_REPLY="This is an automatic generated message. Please do not reply.";
    public static final String BEST_REGARDS="Thanks & Regards";
    public static final String TEAM_NAME="Eaton.com";
    /*
    Tabbed Menu List constants
     */
    public static final String DEFAULT_PARENT_PAGE_TABBED_MENU_LIST = "/content/eaton";
    public static final String TAG_PATH = "/content/cq:tags/eaton/language";
    public static final String ERROR_PAGE_404 = "/404.html";
    public static final String DEACTIVATE = "Deactivate";
    public static final String JCR_UUID = "jcr:uuid";
    public static final String UUID = "uuid";
	/*
	 * Rollout conflict
	 */
	public static final String MSM_MOVED_POSTFIX = "_msm_moved";
    
    /** The Constant CONFIG_PATH. */
    public static final String CONFIG_PATH = "/apps/eaton/runmodes/config";

}
