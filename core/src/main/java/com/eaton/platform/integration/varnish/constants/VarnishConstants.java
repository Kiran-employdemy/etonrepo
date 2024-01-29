package com.eaton.platform.integration.varnish.constants;

public final class VarnishConstants {

    //Private Constructor
    private VarnishConstants(){

    }
    // varnish configuration
    public static final String VARNISH_SERVICE_URL = "/eaton/clearVarnishCache";

    public static final String POST_METHOD = "POST";

    public static final String APPLICATION_JSON = "application/json";
    public static final String CACHE_CONTROL = "Cache-Control";
    public static final String CACHE_CONTROL_VALUE = "no-cache, no-store, must-revalidate";
    public static final String PRAGMA = "Pragma";
    public static final String PRAGMA_VALUE = "no-cache";
    public static final String EXPIRES = "Expires";
    public static final String UTF_CONSTANT = "utf-8";
    public static final String IO_EXCEPTION = "IOException";
    public static final String VARNISH_DOMAIN="varnishDomain";
    public static final String DATE_FORMAT = "MM/dd/yyyy HH:mm:ss";
    public static final String US_DOMAIN = "_US";
    public static final String SG_DOMAIN = "_SG";
    public static final String NL_DOMAIN = "_NL";
    public static final String PURGE_DESCRIPTION = "No pages to purge";
    public static final String REDIRECT_PATH = "redirectPath";
    public static final String SLASH = "/";

    public static final String VARNISH_ERROR = "varnish-error";
    public static final String HTTP_SUCCESS_CODE = "200";

    public static final String HYPHEN = "-";
    public static final String PIPE = "\\|";
    public static final String USERNAME = "username=";

    public static final String PASSWORD = "&password=";
    public static final String VARNISH_RESPONSE_DESC = "Response from Varnish :";
    public static final String VARNISH_JSON_DESC = "JSON sent for Varnish clear ";
    public static final String REQUEST_URLCONSTANT = "req.url ~ ";
    public static final String EXPRESSION = "expression";
    public static final String TARGET = "target";
    public static final String GROUP_NAME = "groupName";
    public static final String USERNAME_CONSTANT = "username";

    public static final String TIMESTAMP = "timestamp";
    public static final String CREATED = "created";

    public static final String ID = "id";

    public static final String JSON_EXCEPTION_DESC = "Could not build purge request content";
    public static final String REQUEST_SEND_EXCEEPTION = "Could not send Varnish Ban Clear request";
    public static final String CONTENT_BUILDER_EXCEPTION = "Could not retrieve content from content builder";
    public static final String HTML_EXT = ".html";
    public static final String GROUP_NAME_US = "  (US)";
    public static final String GROUP_NAME_SG = "  (SG)";
    public static final String GROUP_NAME_NL = "  (NL)";
    public static final String END_LINE = "---------------------------------------";
    public static final String VARNISH_PROTOCOL = "varnish://";
    public static final String HTTP_PROTOCOL = "http://";
    public static final String EXCLUDE_PATHS = "excludePaths";
    public static final String EXCLUDE_PATH_LOG_MESSAGE = "  Path is excluded from varnish cache clearing.";
    public static final String PATHS = "paths";
    public static final String METHOD = "method";
    public static final String BAN = "BAN";
    public static final String VCLGROUP = "vclGroup";
    public static final String FQDN = "fqdn";
    public static final String DOMAINS = "domains";
    public static final String MSG_DEBUG_ACCESS_TOKEN = "Access Token: ";
    public static final String ACCESS_TOKEN_KEY = "accessToken";
    public static final String BEARER_SPACE = "Bearer ";
    public static final String ORG = "org";

    public static final String CONTENT_TYPE = "Content-Type";

    public static final String COMMA = ",";
    public static final String VARNISH_SIX_SERVICE_URL = "/eaton/clearVarnishSixCache";
    public static final String COLON_PORT = ":8002";
    public static final String PATH = "path";
    public static final String VARNISH_SIX_DOMAIN="varnishSixDomain";
    public static final String HTTPS_SUCCESS_CODE = "201";

}
