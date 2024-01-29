package com.eaton.platform.core.webtools.services.impl;

import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.webtools.services.WebtoolsServiceConfiguration;
import com.eaton.platform.core.webtools.services.config.WebtoolsServiceConfig;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * <html> Description: Implementation for Configuration for  Webtools Services.
 *
 * @author ICF
 * @version 1.0
 * @since 2022
 */
@Component(
        service = WebtoolsServiceConfiguration.class,
        immediate = true,
        property = {
                AEMConstants.SERVICE_VENDOR_EATON,
                AEMConstants.SERVICE_DESCRIPTION + "Webtools Service Configuration",
                AEMConstants.PROCESS_LABEL + "WebtoolsServiceConfiguration"
        })
@Designate(ocd = WebtoolsServiceConfig.class)
public class WebtoolsServiceConfigurationImpl implements WebtoolsServiceConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(WebtoolsServiceConfigurationImpl.class);

    @Reference
    private HttpClientBuilderFactory httpFactory;

    private PoolingHttpClientConnectionManager conMgr;

    private int maxConnections;
    private int maxHostConnections;
    private String webtoolsBaseEndpointRoot;
    private String arincComponentEndpoint;
    private String arincPackagesEndpoint;
    private String arincPackage;
    private String freeSampleStockEndpoint;

    private String backshellMetadataEndpoint;
    private String backshellPartbuildEndpoint;

    private String webtoolsUsername;
    private String webtoolsPassword;

    @Activate
    @Modified
    protected final void activate(final WebtoolsServiceConfig webtoolsConfig) {
        if (webtoolsConfig != null) {
            maxConnections = webtoolsConfig.maxConnections();
            maxHostConnections = webtoolsConfig.maxHostConnections();

            webtoolsBaseEndpointRoot = webtoolsConfig.webtoolsBaseEndpointRoot();
            arincComponentEndpoint = webtoolsConfig.arincComponentEndpoint();
            arincPackagesEndpoint = webtoolsConfig.arincPackagesEndpoint();
            backshellMetadataEndpoint = webtoolsConfig.backshellMetadataEndpoint();
            backshellPartbuildEndpoint = webtoolsConfig.backshellPartbuildEndpoint();
            arincPackage = webtoolsConfig.arincPackages();
            freeSampleStockEndpoint = webtoolsConfig.freeSampleStockAvailabilityEndpoint();
            webtoolsUsername = webtoolsConfig.webtoolsUsername();
            webtoolsPassword = webtoolsConfig.webtoolsPassword();
        }
    }

    @Override
    public int getMaxConnections() {
        return maxConnections;
    }

    @Override
    public int getMaxHostConnections() {
        return maxHostConnections;
    }

    @Override
    public String getWebtoolsBaseEndpointRoot() {
        return webtoolsBaseEndpointRoot;
    }

    @Override
    public String getArincComponentEndpoint() {
        return arincComponentEndpoint;
    }

    @Override
    public String getArincPackagesEndpoint() {
        return arincPackagesEndpoint;
    }

    @Override
    public String getBackshellMetadataEndpoint() {
        return backshellMetadataEndpoint;
    }

    @Override
    public String getBackshellPartbuildEndpoint() {
        return backshellPartbuildEndpoint;
    }

    @Override
    public String getWebtoolsUsername() {
        return webtoolsUsername;
    }

    @Override
    public String getWebtoolsPassword() {
        return webtoolsPassword;
    }

    @Override
    public JsonObject getArincComponents() {
        LOG.debug("Arinc Component URI:: " + webtoolsBaseEndpointRoot + arincComponentEndpoint);
        JsonObject arincComponentJson = executeGetAPI(webtoolsBaseEndpointRoot + arincComponentEndpoint);
        LOG.debug("Arinc Component JSON : " + arincComponentJson);
        return arincComponentJson;
    }

    @Override
    public JsonObject getArincPackage(JsonObject packageParamJson) {
        LOG.debug("Arinc Packaging URL:: " + webtoolsBaseEndpointRoot + arincPackage);
        JsonObject arincPackageJson = executePostAPI(webtoolsBaseEndpointRoot + arincPackage, packageParamJson);
        LOG.debug("Arinc JSON : " + arincPackageJson);
        return arincPackageJson;
    }

    @Override
    public JsonObject getArincPackagingOptions(JsonObject packageParamJson) {
        LOG.debug("Arinc Packaging URL:: " + webtoolsBaseEndpointRoot + arincPackagesEndpoint);
        JsonObject arincPackagingOptionsJson = executePostAPI(webtoolsBaseEndpointRoot + arincPackagesEndpoint, packageParamJson);
        LOG.debug("Arinc JSON : " + arincPackagingOptionsJson);
        return arincPackagingOptionsJson;
    }

    @Override
    public JsonObject getBackshellMetadataResults(){
        LOG.debug("Backshell Metadata URI:: " + webtoolsBaseEndpointRoot + backshellMetadataEndpoint);
        JsonObject backshellMetadataJson = executeGetAPI(webtoolsBaseEndpointRoot + backshellMetadataEndpoint);
        LOG.debug("Backshell Metadata JSON : " + backshellMetadataJson);
        return backshellMetadataJson;
    }

    @Override
    public JsonObject getBackshellPartBuildResults(int partId){
        String uri = webtoolsBaseEndpointRoot + backshellPartbuildEndpoint + "?partId=" + partId;
        LOG.debug("Backshell Part Build URI:: " + uri);
        JsonObject backshellPartBuildJson = executeGetAPI(uri);
        LOG.debug("Backshell Part Build JSON : " + backshellPartBuildJson);
        return backshellPartBuildJson;
    }

    private JsonObject executeGetAPI(final String uri) {
        JsonObject responseJsonArray = new JsonObject();
        LOG.info("Starting from Webtools executeAPI() method");
        try {
            final HttpClient client = httpFactory.newBuilder().setConnectionManager(getMultiThreadedConf()).setConnectionTimeToLive(90, TimeUnit.SECONDS).build();
            final URIBuilder uriBuilder = new URIBuilder(uri);
            final URI build = uriBuilder.build();

            HttpGet httprequest = new HttpGet(build);
            LOG.debug("httpRequest :: " + httprequest.getURI().toString());
            httprequest.addHeader(CommonConstants.ACCEPT, CommonConstants.APPLICATION_JSON);

            final HttpResponse httpResponse = client.execute(httprequest);
            final String reasonPhrase = httpResponse.getStatusLine().getReasonPhrase();

            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                responseJsonArray = (JsonObject) new JsonParser().parse(new InputStreamReader(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8));
            } else {
                LOG.debug("The Webtools API request returned the following status: {}", reasonPhrase);
            }
            LOG.debug("Webtools response :: {}", responseJsonArray.toString());
        } catch (URISyntaxException e) {
            LOG.error("URISyntaxException while calling the Webtools api: {}", e.getMessage());
        } catch (ClientProtocolException e) {
            LOG.error("ClientProtocolException while calling the Webtools api: {}", e.getMessage());
        } catch (IOException e) {
            LOG.error("IOException while calling the Webtools api: {}", e.getMessage());
        }

        LOG.info("exited from Webtools executeGetAPI() method");
        return responseJsonArray;

    }

    private JsonObject executePostAPI(final String uri, JsonObject packageParamsJson) {
        JsonObject responseJson = new JsonObject();
        LOG.info("Starting from Webtools executePostAPI() method");
        try {
            final HttpClient client = httpFactory.newBuilder().setConnectionManager(getMultiThreadedConf()).setConnectionTimeToLive(90, TimeUnit.SECONDS).build();
            final URIBuilder uriBuilder = new URIBuilder(uri);
            final URI build = uriBuilder.build();

            HttpPost httpPost = new HttpPost(build);
            httpPost.setHeader(CommonConstants.CONTENT_TYPE, CommonConstants.APPLICATION_JSON);
            StringEntity requestEntity = new StringEntity(String.valueOf(packageParamsJson), ContentType.APPLICATION_JSON);
            httpPost.setEntity(requestEntity);

            LOG.debug("package parameter json: {}", packageParamsJson.toString());

            final HttpResponse response = client.execute(httpPost);
            final String reasonPhrase = response.getStatusLine().getReasonPhrase();

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                responseJson = (JsonObject) new JsonParser().parse(new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8));
            } else {
                LOG.debug("Http post Status: {}", response.getStatusLine().getStatusCode());
                LOG.debug("The Webtools Post API request returned the following status: {}", reasonPhrase);
            }
            LOG.debug("Webtools Post response :: {}", responseJson.toString());
        } catch (URISyntaxException e) {
            LOG.error("URISyntaxException while calling the Webtools Post api: {}", e.getMessage());
        } catch (ClientProtocolException e) {
            LOG.error("ClientProtocolException while calling the Webtools Post api: {}", e.getMessage());
        } catch (IOException e) {
            LOG.error("IOException while calling the Webtools Post api: {}", e.getMessage());
        }

        LOG.info("exited from Webtools executePostAPI() method");
        return responseJson;

    }

    private PoolingHttpClientConnectionManager getMultiThreadedConf() {
        if (conMgr == null) {
            conMgr = new PoolingHttpClientConnectionManager();
            conMgr.setMaxTotal(maxConnections);
            conMgr.setDefaultMaxPerRoute(maxHostConnections);
        }
        return conMgr;
    }

    @Override
    public JsonObject getFreeSampleStockAvailability(String catalogId) {
        StringBuilder uriBuilder=new StringBuilder();
        uriBuilder.append(webtoolsBaseEndpointRoot).append(freeSampleStockEndpoint).append("?catalogId=").append(catalogId);       
        String uri =uriBuilder.toString();
        LOG.debug("Free sample URI: {}" , uri);
        JsonObject freeSampleStockAvailaibilityJson = executeGetAPI(uri);
        LOG.debug("Free sample JSON: {}" , freeSampleStockAvailaibilityJson);
        return freeSampleStockAvailaibilityJson;
    }
}
