package com.eaton.platform.integration.qr.constants;

/**
 * The Class QRConstants.
 */
public final class QRConstants {

    //QR code constant
    public static final String ERROR_CODE = "errorCode";
    public static final String ERRCD00 = "ERRCD00";
    public static final String SERIAL_ID_CHARATER = "S";
    public static final String SERIAL_NUMBER = "serialNumber";
    public static final String CATALOG_NUMBER = "catalogNumber";
    public static final String AUTH_CODE = "authCode";
	public static final String REPISSUE_FULL_NAME = "fullName";
    public static final String REPISSUE_EMAIL = "email";
    public static final String REPISSUE_COMMENTS = "comments";
    public static final String REPISSUE_EVENTID = "eventId";
    public static final String REPISSUE_AUTHCODE = "authcode";
    public static final String REPISSUE_REPEMAIL = "repIssueEmail";
    public static final String VALIDATE_SERIAL_NUMBER_MANUAL_FLOW = "validateSerialManualFlow";
    public static final String VALIDATE_AUTH_CODE = "validateAuthCodeManualFlow";
	public static final String REPORT_ISSUE = "reportIssue";
    public static final String ACTION = "action";
    public static final String COUNTRY_NAME = "Country Name";
    public static final String X_FORWARDED_FOR = "X-Forwarded-For";
    public static final String EVENT_ID_CHARACTER = "E";
    public static final String SERIAL_NUM_AUTH_MSG = "serialNumAuthMsg";
    public static final String API_KEY= "?apiKey=";
    public static final String API_SECRET="&apiSecret=";
	public static final String AUTHENTICATE_LABEL_DEFAULT = "Authenticate Product";
	public static final String AUTHENTICATE_PRODUCT_ICON = "auth-icon";

    //Ip Address Constant
    public static final String IP_ADDRESS_URL = "https://api.ipify.org/?format=json";
    public static final String IP_ADDRESS_PARAMETER = "ip";
    public static final String GOOGLE_API_URL_FOR_LAT_LNG = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";
    public static final String GOOGLE_API_URL_FOR_IP= "https://www.googleapis.com/geolocation/v1/geolocate?key=";
	
	  //QR code email functionality constants
    public static final String EMAIL_TEMPLATE_PAH="/etc/notification/email/html/com.eaton.qr.email/emailTemplate.txt";
    public static final String REPORTER_EMAIL_TEMPLATE_PAH="/etc/notification/email/html/com.eaton.qr.email/reporterEmailTemplate.txt";
}
