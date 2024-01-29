package com.eaton.platform.core.replication;

import com.day.cq.replication.AgentConfig;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.ReplicationLog;
import com.day.cq.replication.ReplicationResult;
import com.day.cq.replication.ReplicationTransaction;
import com.day.cq.replication.TransportContext;
import com.day.cq.replication.TransportHandler;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.varnish.constants.VarnishConstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Transport handler to send test and purge requests to Vanish and handle
 * responses. The handler sets up basic authentication with the user/pass from
 * the replication agent's transport config and sends a GET request as a test
 * and POST as purge request. A valid test response is 200 while a valid purge
 * response is 201.
 *
 * The transport handler is triggered by setting your replication agent's
 * transport URL's protocol to "varnish://".
 *
 * The transport handler builds the POST request body in accordance with
 * Vanish's REST APIs {@link //https://docs.varnish-software.com/varnish-administration-console/api/introduction/}
 * using the replication agent properties.
 */
@Component(service = TransportHandler.class,immediate = true)
public class VarnishSixTransportHandler implements TransportHandler {

    /** To keep the context between multiple http post calls */
    HttpClientContext context = new HttpClientContext();
    public static final Logger LOGGER = LoggerFactory.getLogger(VarnishSixTransportHandler.class);

    /** Vanish REST API URL */
    private static final String VARNISH_REST_API_URL = "http://varnish-mgt-prod1.tcc.etn.com:8002";
    private static final String VARNISH_SIX_TRANSPORT_URI = "varnish://varnish-mgt-prod1.tcc.etn.com";

    /** Replication agent domain property name */
    private static final String PROPERTY_DOMAIN = "varnishSixDomain";

    /** Replication agent default domain ID value */
    private static final String PROPERTY_VCLGROUP_DEFAULT_ID = "9";

    @Reference
    private FlushServiceConfiguration flushServiceConfiguration;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canHandle(AgentConfig config) {
        final String transportURI = config.getTransportURI();

        return transportURI != null && transportURI.equalsIgnoreCase(VARNISH_SIX_TRANSPORT_URI);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public ReplicationResult deliver(TransportContext ctx, ReplicationTransaction tx)
            throws ReplicationException {
        String resourcePath = tx.getAction().getPath();
        if (flushServiceConfiguration.skipContentFlushing(resourcePath)) {
            LOGGER.debug("Content is skipped from flushing due to configuration: {}", resourcePath);
            return ReplicationResult.OK;
        }
        final ReplicationActionType replicationType = tx.getAction().getType();
        if (replicationType == ReplicationActionType.TEST) {
            try {
                return doTest(ctx, tx);
            } catch ( JSONException | AuthenticationException e) {
                throw new ReplicationException("replication exception", e);
            }
        } else if (replicationType == ReplicationActionType.ACTIVATE ||
                replicationType == ReplicationActionType.DEACTIVATE || replicationType == ReplicationActionType.DELETE ) {
            try {
                return doActivate(ctx, tx);
            } catch (JSONException | AuthenticationException e) {
                throw new ReplicationException("RuntimeException exception", e);
            }
        } else {
            throw new ReplicationException("Replication action type " + replicationType + " not supported.");
        }
    }


    /**
     * Send test request to Vanish via a POST request.
     *
     * Vanish will respond with a 200 HTTP status code if the request was
     * successfully submitted. The response will have information about the
     * queue length, but we're simply interested in the fact that the request
     * was authenticated.
     *
     * @param ctx Transport Context
     * @param tx Replication Transaction
     * @return ReplicationResult OK if 200 response from Vanish
     * @throws ReplicationException
     */
    private ReplicationResult doTest(TransportContext ctx, ReplicationTransaction tx)
            throws ReplicationException, JSONException, AuthenticationException {
        String orgValue = StringUtils.EMPTY;
        final ReplicationLog log = tx.getLog();
        final ValueMap properties = ctx.getConfig().getProperties();
        String domain = String.valueOf(properties.get(PROPERTY_DOMAIN));
        final String [] domainValues = domain.split(VarnishConstants.PIPE);
        if(domainValues.length > 1) {
            orgValue = domainValues[2].trim();
        }
        final HttpPost request = new HttpPost(VARNISH_REST_API_URL + "/api/v1/auth/login");
        final HttpResponse response = loginSendRequest(request, ctx, tx, orgValue);
           final int statusCode = response.getStatusLine().getStatusCode();

            log.info(response.toString());
            log.info("---------------------------------------");

            if (statusCode == HttpStatus.SC_OK) {
                return ReplicationResult.OK;
            }
        return new ReplicationResult(false, 0, "Replication test failed");
    }
    /**
     * Send purge request to Vanish via a POST request
     *
     * Vanish will respond with a 201 HTTP status code if the purge request was
     * successfully submitted.
     *
     * @param ctx Transport Context
     * @param tx Replication Transaction
     * @return ReplicationResult OK if 201 response from Vanish
     * @throws ReplicationException
     */
    protected ReplicationResult doActivate(TransportContext ctx, ReplicationTransaction tx)
            throws ReplicationException, JSONException, AuthenticationException {
        String orgValue = StringUtils.EMPTY;
        final ReplicationLog log = tx.getLog();

        final ValueMap properties = ctx.getConfig().getProperties();
        final String domain = String.valueOf(properties.get(PROPERTY_DOMAIN));
        final String [] domainValues = domain.split(VarnishConstants.PIPE);
        if(domainValues.length > 1) {
                    orgValue = domainValues[2].trim();
        }
        final HttpPost loginrequest = new HttpPost(VARNISH_REST_API_URL + "/api/v1/auth/login");
        final HttpPost apirequest = new HttpPost(VARNISH_REST_API_URL + "/api/v1/invalidations");

        final HttpResponse response = loginSendRequest(loginrequest, ctx, tx, orgValue);

            final int statusCode = response.getStatusLine().getStatusCode();

            log.info(response.toString());
            log.info("---------------------------------------");

            if (statusCode == HttpStatus.SC_OK) {
                String accessToken = bufferAccessToken(response);

                varnishBanClear(apirequest, ctx, tx,accessToken);
                return ReplicationResult.OK;
            }
        return new ReplicationResult(false, 0, "Replication failed");
    }
    /**
     * Build preemptive basic authentication headers and send request.
     *
     * @param request The request to send to Vanish
     * @param ctx The TransportContext containing the username and password
     * @return HttpResponse The HTTP response from Vanish
     * @throws ReplicationException if a request could not be sent
     */
    private HttpResponse loginSendRequest(HttpPost request, final TransportContext ctx, final ReplicationTransaction tx , String orgValue)
            throws ReplicationException, JSONException, AuthenticationException {
        final ReplicationLog log = tx.getLog();
        JSONObject jsonObj = new JSONObject();
        jsonObj.put(VarnishConstants.ORG, orgValue);
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(ctx.getConfig().getTransportUser(), ctx.getConfig().getTransportPassword());
        request.addHeader(new BasicScheme().authenticate(credentials, request, context));
        request.setHeader(VarnishConstants.CONTENT_TYPE, ContentType.DEFAULT_TEXT.toString());

        final StringEntity loginEntity = new StringEntity(jsonObj.toString(), ContentType.APPLICATION_JSON);
        request.setEntity(loginEntity);

        CloseableHttpClient client = HttpClientBuilder.create().build();
        CloseableHttpResponse response;
        try {
            response = client.execute(request,context);
            log.debug("Return Varnish Six Login result::{} ", response.getStatusLine().getStatusCode());

        } catch (IOException e) {
            throw new ReplicationException("VarnishSixTransportHandler:: Could not send replication request.", e);
        }
        return response;
    }
    /**
     * Build the Vanish purge request body based on the replication agent
     * settings and append it to the POST request.
     *
     * @param request The HTTP POST request to append the request body
     * @param ctx TransportContext
     * @param tx ReplicationTransaction
     * @throws ReplicationException if errors building the request body
     */
    private void varnishBanClear(final HttpPost request, final TransportContext ctx,
                                final ReplicationTransaction tx, String accessToken) {
        int vclGroupId = 0;
        String varnishDomain = StringUtils.EMPTY;
        final ReplicationLog log = tx.getLog();
        final ValueMap properties = ctx.getConfig().getProperties();
        final String[] excludePaths = CommonUtil.getExcludedPaths(ctx.getConfig().getProperties());
        final String domain = PropertiesUtil.toString(properties.get(PROPERTY_DOMAIN), PROPERTY_VCLGROUP_DEFAULT_ID);
        final String [] domainValues = domain.split(VarnishConstants.PIPE);
        if(domainValues.length > 1) {
            vclGroupId = Integer.parseInt(domainValues[0].trim());
            varnishDomain = domainValues[1];
        }
        JsonArray varnishFlushPaths = null;
        try {
            final String content = IOUtils.toString(tx.getContent().getInputStream());
            if (StringUtils.isNotBlank(content)){
                final JsonParser parser = new JsonParser();
                final JsonElement contentPaths = parser.parse(content);
                varnishFlushPaths = contentPaths.getAsJsonArray();
                String path = varnishFlushPaths.toString();
                LOGGER.debug("VarnishSixTransportHandler:: Length of Content Array to clear Varnish::{}", path);
            }
            final JsonArray purgeObjects = CommonUtil.getFilteredPurgePath(varnishFlushPaths, excludePaths, log);
            CommonUtil.clearVarnishSixCache(purgeObjects, request, context, log, vclGroupId, varnishDomain,accessToken);
        } catch (IOException e) {
            LOGGER.error(VarnishConstants.REQUEST_SEND_EXCEEPTION, e.getMessage());
        } catch(Exception e) {
            LOGGER.error(VarnishConstants.CONTENT_BUILDER_EXCEPTION, e.getMessage());
        }
    }
    private String bufferAccessToken(HttpResponse execute){

        final StringBuilder response = new StringBuilder();
        String accessToken = null;

        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(execute.getEntity().getContent(), StandardCharsets.UTF_8))) {
            String line;
            while (null != (line = bufferedReader.readLine())) {
                response.append(line);
            }
        } catch (IOException e) {
            LOGGER.error("VarnishSixTransportHandler:: Unable to read access token from api: ", e);
        }
        try {
            JSONObject responseJson = new JSONObject(response.toString());
            String accessTokenVal = responseJson.getString(VarnishConstants.ACCESS_TOKEN_KEY);
            if (accessTokenVal != null && StringUtils.isNotEmpty(accessTokenVal)) {
                accessToken = accessTokenVal;
                LOGGER.debug(VarnishConstants.MSG_DEBUG_ACCESS_TOKEN, accessToken);
            }
        } catch (JSONException e) {
            LOGGER.error("VarnishSixTransportHandler:: Unable to parse access token from api: ", e);
        }
        return accessToken;
    }
}
