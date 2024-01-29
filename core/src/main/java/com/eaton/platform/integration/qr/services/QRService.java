package com.eaton.platform.integration.qr.services;

import com.google.gson.JsonObject;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.Map;

public interface QRService {

    /**    
     * This is to validate Serial Number
     * @param serialNumber from request
     * @param eventID from request
     * @param catalogNum from request
     * @param headersMap from request
     * @return JSONObject   
     **/
    JsonObject validateSerialNumber(final String serialNumber, final String eventID, final String catalogNum, final Map<String,String> headersMap);

    /**    
     * This is to report issue with product
     * @param fullname from request
     * @param contactemail from request
     * @param comments from request
     * @param serialNumber from request
     * @param catalogNumber from request
     * @param eventID from request
     * @param authCode from request
     * @param repIssueEmail from request
     * @return JSONObject   
     **/
	JsonObject reportIssue(final String fullname, final String contactemail,final String comments,final String serialNumber, final String catalogNumber,final String eventID,final String authCode,final String repIssueEmail);

    /**    
     * This is to validate Authentication code   
     * @return JSONObject  
     * @param authCode from request
     * @param serialNum from request
     * @param catalogNumber from request
     * @param headerMap from request 
     **/
	JsonObject validateAuthCode(final String authCode, final String serialNum, String catalogNumber, final Map<String,String> headerMap);
}
