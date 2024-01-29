package com.eaton.platform.integration.akamai.services;

import com.akamai.edgegrid.signer.ClientCredential;
import com.akamai.edgegrid.signer.apachehttpclient.ApacheHttpClientEdgeGridInterceptor;
import com.akamai.edgegrid.signer.exceptions.RequestSigningException;
import com.day.cq.commons.Externalizer;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.AgentConfig;
import com.day.cq.replication.TransportContext;
import com.day.cq.replication.ReplicationTransaction;
import com.day.cq.replication.ReplicationResult;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.ReplicationLog;
import com.day.cq.replication.TransportHandler;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.bean.ProductFamilyDetails;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.replication.FlushServiceConfiguration;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.ProductFamilyDetailService;
import com.eaton.platform.integration.akamai.config.AkamaiConfig;
import com.eaton.platform.integration.akamai.constants.AkamaiConstants;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;


/**
 * Transport handler to send test and purge requests to Akamai and handle
 * responses. The handler sets up basic authentication with the user/pass from
 * the replication agent's transport config and sends a GET request as a test
 * and POST as purge request. A valid test response is 200 while a valid purge
 * response is 201.
 *
 * The transport handler is triggered by setting your replication agent's
 * transport URL's protocol to "akamai://".
 *
 * The transport handler builds the POST request body in accordance with
 * Akamai's Fast Purge REST API {@link https://developer.akamai.com/api/core_features/fast_purge/v3.html}
 * using the replication agent properties.
 */
@Component(service = TransportHandler.class, immediate = true)
@Designate(ocd = AkamaiConfig.class)
public class AkamaiTransportHandler implements TransportHandler {


    /**Logger Instantiation for Akamai Transport Handler*/
    private static final Logger LOGGER = LoggerFactory.getLogger(AkamaiTransportHandler.class);
    private static final String NO_PAGE_TYPE = "NoPageType";
    
    @Reference
    private HttpClientBuilderFactory httpFactory;

    @Reference
    private Externalizer externalizer;

    @Reference
    private ProductFamilyDetailService productFamilyDetailService;


    @Reference
    private AdminService adminService;

    @Reference
    private FlushServiceConfiguration flushServiceConfiguration;

    private String baseURL = StringUtils.EMPTY;
    private String akamaiAccessToken = StringUtils.EMPTY;
    private String akamaiClientToken =  StringUtils.EMPTY;
    private String akamaiClientSecret = StringUtils.EMPTY;
    private String akamaiHost =  StringUtils.EMPTY;
    private int transportDelay = 2;
	private static final String VAR_STR = "/var";
    /** The current page. */
	@Inject
	protected Page currentPage;
    @Activate
    @Modified
    protected void activate(final AkamaiConfig config){
        baseURL = config.base_url();
        akamaiHost = config.akamai_host();
        akamaiAccessToken = config.access_token();
        akamaiClientToken = config.client_token();
        akamaiClientSecret = config.client_secret();
        transportDelay = config.transport_delay();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canHandle(AgentConfig config) {
        final String transportURI = config.getTransportURI();


        return (transportURI != null) && (transportURI.toLowerCase().startsWith(AkamaiConstants.AKAMAI_PROTOCOL));
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ReplicationResult deliver(TransportContext ctx, ReplicationTransaction tx)
            throws ReplicationException {


        final ReplicationActionType replicationType = tx.getAction().getType();


        if (replicationType == ReplicationActionType.TEST) {
            return ReplicationResult.OK;
        } else if (replicationType == ReplicationActionType.ACTIVATE ||
                replicationType == ReplicationActionType.DEACTIVATE ||
                replicationType == ReplicationActionType.DELETE) {
            LOGGER.info("Replication  Type in Akamai Handler: {}", replicationType);
            String resourcePath = tx.getAction().getPath();
            if (flushServiceConfiguration.skipContentFlushing(resourcePath)) {
                LOGGER.debug("Content is skipped from flushing due to configuration: {}", resourcePath);
                return ReplicationResult.OK;
            }
            if (StringUtils.startsWith(resourcePath, CommonConstants.CONTENT_ROOT_FOLDER)
                    || StringUtils.startsWith(resourcePath, CommonConstants.CONTENT_DAM_EATON)) {
                LOGGER.info("Calling activate in Akamai for path: {}", resourcePath);
                try {
                    return doActivate(ctx, tx);
                } catch (IOException e) {
                    LOGGER.error("IO Exception in deliver \n");
                    throw new ReplicationException("IO Exception in deliver: {}", e);
                }
            }
            return ReplicationResult.OK;
        } else {
            throw new ReplicationException("Replication action type " + replicationType + " not supported.");
        }
    }

    private String getTransportURI(TransportContext ctx) throws IOException {
        LOGGER.info("Entering getTransportURI method.");
        final ValueMap properties = ctx.getConfig().getProperties();
        final String domain = PropertiesUtil.toString(properties.get(AkamaiConstants.PROPERTY_AKAMAI_DOMAIN), AkamaiConstants.PROPERTY_AKAMAI_DOMAIN_DEFAULT);
        final String action = PropertiesUtil.toString(properties.get(AkamaiConstants.PROPERTY_AKAMAI_ACTION), AkamaiConstants.PROPERTY_AKAMAI_ACTION_DEFAULT);
        final String type = PropertiesUtil.toString(properties.get(AkamaiConstants.PROPERTY_AKAMAI_TYPE), AkamaiConstants.PROPERTY_AKAMAI_TYPE_DEFAULT);
        return AkamaiConstants.HTTPS_PROTOCOL + akamaiHost + "/ccu/v3/"
                + action + AkamaiConstants.BACK_SLASH + type + AkamaiConstants.BACK_SLASH + domain;
    }


    /**
     * Send purge request to Akamai via a POST request
     *
     * Akamai will respond with a 201 HTTP status code if the purge request was
     * successfully submitted.
     *
     * @param ctx Transport Context
     * @param tx Replication Transaction
     * @return ReplicationResult OK if 201 response from Akamai
     * @throws ReplicationException
     * @throws RequestSigningException
     * @throws IOException
     * @throws JSONException
     */
    protected ReplicationResult doActivate(TransportContext ctx, ReplicationTransaction tx)
            throws ReplicationException, IOException {
        LOGGER.info("Inside doActivate of Akamai");

        final ReplicationLog log = tx.getLog();

        String jsonObject = "";
        try {
            jsonObject = createPostBody(ctx, tx).toString();
        } catch(IllegalArgumentException e){
            LOGGER.error("Illegal Characters or Invalid Path submitted", e);
            log.error("Illegal Characters or Invalid Path submitted");
            return ReplicationResult.OK;
        } catch(ReplicationException e){
            throw new ReplicationException(e);
        }

        log.info("Post body: " + jsonObject);
        String uri = URI.create(getTransportURI(ctx)).toString();

        ClientCredential clientCredential = ClientCredential.builder().accessToken(akamaiAccessToken).
                clientToken(akamaiClientToken).clientSecret(akamaiClientSecret).host(akamaiHost).build();
        final HttpResponse response = sendRequest(uri, jsonObject, clientCredential, tx);


        if (response != null) {
            final int statusCode = response.getStatusLine().getStatusCode();
            LOGGER.info("Response code recieved: {}", statusCode);
            log.info(response.toString());
            log.info("---------------------------------------");
            if (statusCode == HttpStatus.SC_CREATED) {
                return ReplicationResult.OK;
            }
        }
        return new ReplicationResult(false, 0, "Replication failed");
    }

    private HttpResponse sendRequest(String uri, String jsonObject, ClientCredential clientCredential, ReplicationTransaction tx)
            throws ReplicationException {
        LOGGER.info("Inside Send Request method of Akamai");

        final ReplicationLog log = tx.getLog();

        HttpClient client = httpFactory.newBuilder().addInterceptorFirst(new ApacheHttpClientEdgeGridInterceptor(clientCredential))
                //.setRoutePlanner(new ApacheHttpClientEdgeGridRoutePlanner(clientCredential)) //This overrides the vpn setting passed from httpFactory.
                .build();

        HttpResponse response = null;
        HttpPost postConnection = new HttpPost(uri);
        postConnection.setHeader(CommonConstants.CONTENT_TYPE, CommonConstants.APPLICATION_JSON);
        StringEntity requestEntity = new StringEntity(jsonObject, ContentType.APPLICATION_JSON);
        postConnection.setEntity(requestEntity);
        try {
            TimeUnit.SECONDS.sleep(transportDelay);
            response = client.execute(postConnection);
            if (null != response) {
                log.info(response.toString());
                log.info(AkamaiConstants.END_LINE);
                String inputLine ;
                BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                while ((inputLine = br.readLine()) != null) {
                    log.info(AkamaiConstants.AKAMAI_RESPONSE_DESC.concat(inputLine));
                    String inputStr = AkamaiConstants.AKAMAI_RESPONSE_DESC.concat(inputLine);
                    LOGGER.info("{} is ready, inputLine expected ", inputStr);
                }
                br.close();
                log.info(AkamaiConstants.END_LINE);
            }
        } catch(InterruptedException ie){
            LOGGER.error("Pause for Akamai Transport Handler failed: ", ie);
            Thread.currentThread().interrupt();
        }catch (IOException e) {
            LOGGER.error("IO Exception in sendRequest");
            throw new ReplicationException("Could not send replication request.", e);
        }
        LOGGER.info("Sucessfully executed Send Request for Akamai");
        return response;
    }

    /**
     * Build the Akamai purge request body based on the replication agent
     * settings and append it to the POST request.
     *
     * @param request The HTTP POST request to append the request body
     * @param ctx TransportContext
     * @param tx ReplicationTransactionz
     * @throws ReplicationException if errors building the request body
     */
    private JSONObject createPostBody(final TransportContext ctx,
                                      final ReplicationTransaction tx) throws ReplicationException, IllegalArgumentException {
  	    final ValueMap properties = ctx.getConfig().getProperties();
        final String type = PropertiesUtil.toString(properties.get(AkamaiConstants.PROPERTY_AKAMAI_TYPE), AkamaiConstants.PROPERTY_AKAMAI_TYPE_DEFAULT);
        JSONObject json = new JSONObject();
        JSONArray purgeObjects = new JSONArray();

        if (type.equals(AkamaiConstants.PROPERTY_AKAMAI_TYPE_DEFAULT)) {
            purgeObjects = purgeURLs(tx);
        } else{
            String[] cpCodes = PropertiesUtil.toStringArray(properties.get(AkamaiConstants.CPCODES));
            for(String cpCode:cpCodes){
                purgeObjects.put(cpCode);
            }
        }
        if (purgeObjects.length() > 0) {
            try {
                json.put("objects", purgeObjects);
            } catch (JSONException e) {
                throw new ReplicationException("Could not build purge request content", e);
            }
        } else {
            throw new IllegalArgumentException("No CP codes or pages to purge");
        }
        return json;
    }

    private JSONArray purgeURLs(final ReplicationTransaction tx) throws ReplicationException{
        JSONArray purgeURLs = new JSONArray();
        try {
            String content = IOUtils.toString(tx.getContent().getInputStream(), StandardCharsets.UTF_8);

            if (StringUtils.isNotBlank(content)) {
                LOGGER.info("Content of Akamai is:\n {}", content);
                JSONArray urls = new JSONArray(content);
                purgeURLs=createValideURLArray(purgeURLs, urls);
            }
        } catch (JSONException | IOException e) {
            throw new ReplicationException("Could not retrieve content from content builder", e);
        } catch (Exception e){
            LOGGER.error("Failed to get content urls due to characterset processing", e);
        }
        return purgeURLs;
    }

	private JSONArray createValideURLArray(JSONArray purgeURLs, JSONArray urls)
			throws JSONException {
        try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
            PageManager pageManager = adminResourceResolver.adaptTo(PageManager.class);
            for (int j = 0; j < urls.length(); j++) {
                String data = urls.get(j).toString();
                boolean isURLValid = validateURL(data);
                if (isURLValid) {
                    Page page = pageManager.getPage(data);
                    if (page != null && page.getProperties() != null) {
                        ValueMap properties = page.getProperties();
                        String pageType = null != properties.get(CommonConstants.PAGE_TYPE)
                                ? properties.get(CommonConstants.PAGE_TYPE).toString() : NO_PAGE_TYPE;

                        if (pageType != null && StringUtils.equals(pageType, CommonConstants.PAGE_TYPE_PRODUCT_FAMILY_PAGE)) {
                            purgeURLs.put(baseURL + data + CommonConstants.FILE_TYPE_TEXT_HTML_VALUE);
                            purgeURLs.put(baseURL + data + CommonConstants.PERIOD + getMiddleTabPath(page) + CommonConstants.FILE_TYPE_TEXT_HTML_VALUE);
                            purgeURLs.put(baseURL + data + CommonConstants.PERIOD + CommonConstants.TAG_NAMESPACE_RESOURCES + CommonConstants.FILE_TYPE_TEXT_HTML_VALUE);
                        } else {
                            purgeURLs.put(baseURL + data + CommonConstants.FILE_TYPE_TEXT_HTML_VALUE);
                        }
                        addEatonShortenedUrlsAndExtension(data, purgeURLs, page, pageType);
                    } else {
                        purgeURLs.put(baseURL + data);
                    }
                }
            }
        }

	    return purgeURLs;
	}
	
    private static boolean validateURL(String url) {
        if (StringUtils.isAsciiPrintable(url)){ 
        	if(!url.contains(VAR_STR)){
        		return true;
        	}
        }else{
        	return false;
        }
        return false;
    }

    private String getMiddleTabPath(Page page){
        String tabPath = CommonConstants.MODULES_TAB_SELECTOR;

        ProductFamilyDetails productFamilyDetails = productFamilyDetailService.getProductFamilyDetailsBean(page);
        if(productFamilyDetails != null && (StringUtils.isNotBlank(productFamilyDetails.getModelTabName()))){
            String tabName = productFamilyDetails.getModelTabName();
            tabPath = StringUtils.lowerCase(tabName);
        }

        return tabPath;
    }

    /**
     *  This method checks if the URL contains /content/eaton.
     *  If yes, replace the /content/eaton with empty and append the .html extension.
     *  Else, bypass.
     *  TODO - Replace this logic to handle via ResourceResolver.resolve(). Right now, etc/map or resource resolver
     *  configs are not present in Author Instance. Due to which, resolve method won't resolve to absolute url.
     * @param url
     * @param purgeURLs
     */
    private void addEatonShortenedUrlsAndExtension(String url,JSONArray purgeURLs, Page page, String pageType){

        if(url != null && url.contains(CommonConstants.SHORT_URL_REMOVAL_PART)) {
            String afterReplacedURL =  url.replace(CommonConstants.SHORT_URL_REMOVAL_PART, "/");
            if (StringUtils.equals(pageType, CommonConstants.PAGE_TYPE_PRODUCT_FAMILY_PAGE)) {
                purgeURLs.put(baseURL + afterReplacedURL + CommonConstants.FILE_TYPE_TEXT_HTML_VALUE);
                purgeURLs.put(baseURL + afterReplacedURL + CommonConstants.PERIOD + getMiddleTabPath(page) + CommonConstants.FILE_TYPE_TEXT_HTML_VALUE);
                purgeURLs.put(baseURL + afterReplacedURL + CommonConstants.PERIOD + CommonConstants.TAG_NAMESPACE_RESOURCES + CommonConstants.FILE_TYPE_TEXT_HTML_VALUE);
            } else {
                purgeURLs.put(baseURL + afterReplacedURL + CommonConstants.FILE_TYPE_TEXT_HTML_VALUE);
            }
        }
    }
}
