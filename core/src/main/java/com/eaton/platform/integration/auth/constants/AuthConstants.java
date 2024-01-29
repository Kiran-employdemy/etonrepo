package com.eaton.platform.integration.auth.constants;

public final class AuthConstants {

    private AuthConstants(){
        throw new UnsupportedOperationException();
    }

    public static final String AUTHORIZATION_CODE_PARAM = "code";
    public static final String REDIRECT_URL_PARAM = "state";
    public static final String ETN_LOGIN_COOKIE_NAME = "etn-login";
    public static final String EATON_COOKIES = "eatoncookies";
    public static final String LOGIN_LANGUAGE_COOKIE = "country_language";
    public static final String ACCESS_TOKEN_KEY = "access_token";
    public static final String BEARER_SPACE = "Bearer ";

    public static final String GRANT_TYPE_KEY = "grant_type";
    public static final String GRANT_TYPE_VALUE = "authorization_code";
    public static final String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";

    public static final String HEADERS = "headers";
    public static final String PARAMS = "params";
    public static final String AUTHORIZATION_HEADER_KEY = "Authorization";
    public static final String REDIRECT_URI_KEY = "redirect_uri";
    public static final String AUTHORIZATION_CODE_KEY = "code";

    public static final String SIG_PUBLIC_KEY_USE = "sig";
    public static final String RSA_KEY_TYPE = "RSA";

    public static final String CONTENT_LOGIN_ROOT_FOLDER = "/content/login/";

    public static final String ATTR_RESPONSE_STATUS = "status";
    public static final String ATTR_RESPONSE_MESSAGE = "message";

	public static final String ISSUER_PARAM = "iss";
    public static final String EQUAL_CONST = "=";
    public static final String AMP_CONST = "&";
    public static final String SCOPE_CONST = "&scope=openid%20email";
    public static final String RESP_CODE_CONST = "&response_type=code";
    public static final String AUTH_CLIENTID_CONST = "/v1/authorize?client_id=";
    public static final String DEFAULT_STATE_CONST = "eatonDashboard";
    public static final String ETN_REDIRECT_COOKIE = "etn_redirect_cookie";
    public static final String ETN_CART_COUNT_COOKIE = "etn-cart-count";


}