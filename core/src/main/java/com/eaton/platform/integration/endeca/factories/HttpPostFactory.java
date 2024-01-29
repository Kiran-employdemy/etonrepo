package com.eaton.platform.integration.endeca.factories;

import com.adobe.xfa.ut.StringUtils;
import com.eaton.platform.integration.endeca.constants.EndecaConstants;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <html> Description: This class is used to create an HttpPost for Endeca.
 *
 * @author Victoria Pershick
 * @version 1.0
 * @since 2023
 *
 */
public class HttpPostFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpPostFactory.class);
    public static final String ACCEPT = "Accept";
    public static final String CONTENT_TYPE_TEXT_XML_CHARSET = "text/xml; charset=utf-8";
    public static final String SOAP_ACTION = "SOAPAction";
    public static final String ILLEGAL_STATE_NULL_URL = "HttpPostFactory, IllegalStateException due to null or empty url param";
    public static final String ILLEGAL_STATE_NULL_REQUEST = "HttpPostFactory, IllegalStateException due to null or empty request param";

    /**
     * Create an HttpPost with headers and a StringEntity.
     *
     * @param url the url to post to
     * @param request the request to put in the SOAP envelope body
     * @return the HttpPost
     */
    public HttpPost createEndecaRequestHttpPost(String url, String request) {

        throwIllegalStateWhenInputNullOrEmpty(url, request);

        final String body = String.format("%s%s%s%s%s",
                EndecaConstants.SOAP_ENVELOPE_PART_START,
                EndecaConstants.SOAP_BODY_PART_START,
                request,
                EndecaConstants.SOAP_BODY_PART_END,
                EndecaConstants.SOAP_ENVELOPE_PART_END);
        final StringEntity stringEntity = new StringEntity(body, "UTF-8");
        stringEntity.setChunked(true);

        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader(ACCEPT, CONTENT_TYPE_TEXT_XML_CHARSET);
        httpPost.addHeader(SOAP_ACTION, "");
        httpPost.setEntity(stringEntity);
        return httpPost;

    }

    private static void throwIllegalStateWhenInputNullOrEmpty(String url, String request) {
        if(StringUtils.isEmpty(url)) {
            LOGGER.error(ILLEGAL_STATE_NULL_URL);
            throw new IllegalStateException(ILLEGAL_STATE_NULL_URL);
        }
        if(StringUtils.isEmpty(request)) {
            LOGGER.error(ILLEGAL_STATE_NULL_REQUEST);
            throw new IllegalStateException(ILLEGAL_STATE_NULL_REQUEST);
        }

    }
}
