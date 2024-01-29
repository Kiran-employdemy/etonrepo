package com.eaton.platform.integration.myeaton.services.impl;

import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.integration.myeaton.dto.UserProfilePayload;
import com.eaton.platform.integration.myeaton.services.MyEatonServiceConfiguration;
import com.eaton.platform.integration.myeaton.services.UpdateUserService;
import com.eaton.platform.integration.myeaton.services.exception.UpdateUserInformationException;
import com.eaton.platform.integration.myeaton.util.MyEatonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import static com.eaton.platform.core.constants.CommonConstants.APPLICATION_JSON;
import static com.eaton.platform.core.constants.CommonConstants.CONTENT_TYPE;

@Component(
        service = UpdateUserService.class,
        immediate = true,
        property = {
                AEMConstants.SERVICE_VENDOR_EATON,
                AEMConstants.SERVICE_DESCRIPTION + "My Eaton - Update User Information Service",
                AEMConstants.PROCESS_LABEL + "UpdateUserServiceImpl"
        })
public class UpdateUserServiceImpl implements UpdateUserService {

    private static final Logger LOG = LoggerFactory.getLogger(UpdateUserServiceImpl.class);


    @Reference
    private MyEatonServiceConfiguration myEatonServiceConfiguration;

    @Reference
    private HttpClientBuilderFactory httpFactory;

    private PoolingHttpClientConnectionManager conMgr;

    private static final String RESP_SUCCESS = "SUCCESS";


    @Override
    public boolean updateUserInformation(UserProfilePayload userProfilePayload) throws UpdateUserInformationException {
        if(null != userProfilePayload && userProfilePayload.isValidPayload()) {
            String response = executeApi(userProfilePayload);
            LOG.error("Response String: {}",response);
            if(StringUtils.isNotBlank(response) && response.equalsIgnoreCase(RESP_SUCCESS)) {
                return true;
            }
        } else {
            LOG.error("******* User Profile payload object is either null or invalid *******");
        }
        return false;
    }

    protected String executeApi(UserProfilePayload userProfilePayload) throws UpdateUserInformationException {
        try{
            conMgr = MyEatonUtil.getMultiThreadedConf(conMgr, myEatonServiceConfiguration);
            String endpoint = myEatonServiceConfiguration.getEndpointRoot() + myEatonServiceConfiguration.getUpdateUserUrl();
            LOG.error("Update User Information URL: {}",endpoint);
            final CloseableHttpClient client = httpFactory.newBuilder()
                    .setConnectionManager(MyEatonUtil.getMultiThreadedConf(conMgr, myEatonServiceConfiguration))
                    .build();

            final URIBuilder uriBuilder = new URIBuilder(endpoint);
            final URI build = uriBuilder.build();
            final HttpPut httpPut = new HttpPut(build);
            httpPut.setHeader("Authorization", MyEatonUtil.encodeBasicAuthorization(
                    myEatonServiceConfiguration.getUsername(), myEatonServiceConfiguration.getPassword()));
            httpPut.setHeader(CONTENT_TYPE,APPLICATION_JSON);
            String payload = new ObjectMapper().writeValueAsString(userProfilePayload);

            LOG.error("Payload: {}",payload);

            HttpEntity httpEntity = new StringEntity(payload);
            httpPut.setEntity(httpEntity);

            try(CloseableHttpResponse response = client.execute(httpPut)) {
                if (null != response ) {
                    // result return just fine
                    String resp = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
                    LOG.error("Response status code: {} - Response String: {}",response.getStatusLine().getStatusCode(),resp);
                    switch (response.getStatusLine().getStatusCode()) {
                        case HttpStatus.SC_OK:
                            return resp;
                        case HttpStatus.SC_BAD_REQUEST:
                            String[] errors = new ObjectMapper().readValue(resp,String[].class);
                            throw new UpdateUserInformationException(String.join(",",errors));
                        default:
                            throw new UpdateUserInformationException(resp);
                    }
                }
            }
        } catch (URISyntaxException e ) {
            LOG.error("URISyntaxException: {}",e.getMessage(),e);
        } catch (ClientProtocolException e) {
            LOG.error("ClientProtocolException: {}",e.getMessage(),e);
        } catch (IOException e) {
            LOG.error("IOException: {}",e.getMessage(),e);
        }
        return StringUtils.EMPTY;
    }

}
