package com.eaton.platform.integration.ordercenter.services.impl;

import com.day.cq.wcm.webservicesupport.ConfigurationManagerFactory;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.CloudConfigService;
import com.eaton.platform.integration.ordercenter.config.OrderCenterConfig;
import com.eaton.platform.integration.ordercenter.services.OrderCenterStoreIdService;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

@Component(service = OrderCenterStoreIdService.class,immediate = true,
        property = {
                AEMConstants.SERVICE_DESCRIPTION + "Order Center Service",
                AEMConstants.SERVICE_VENDOR_EATON,
                AEMConstants.PROCESS_LABEL + "OrderCenterStoreIdServiceImpl"
        })
@Designate(ocd = OrderCenterConfig.class)
public class OrderCenterStoreIdServiceImpl implements OrderCenterStoreIdService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderCenterStoreIdServiceImpl.class);

    private static final String STORE_ID_URL = "storeid";
    private static final String ORDER_CENTER_TAG_DEFAULT = "/content/cq:tags/order-center";

    @Reference
    private CloudConfigService configService; /** The config factory. */

    @Reference
    private transient ConfigurationManagerFactory configFactory;

    /** The admin services. */
    @Reference
    private transient AdminService adminService;

    @Reference
    private HttpClientBuilderFactory httpFactory;

    private PoolingHttpClientConnectionManager conMgr;

    private String orderCenterURL;
    private String orderCenterTagPath;
    private int maxConnections;
    private int maxHostConnections;
    private boolean enabled;

    @Activate
    @Modified
    protected final void activate(final OrderCenterConfig config){
        if (null != config) {
            maxConnections = config.maxConnections();
            maxHostConnections = config.maxHostConnections();
            orderCenterURL = config.orderCenterURL() + STORE_ID_URL;
            orderCenterTagPath = config.orderCenterTagPath();
            enabled = config.enabled();
        }
    }

    public String getOrderCenterTagPath(){
        return StringUtils.isNotBlank(orderCenterTagPath) ? orderCenterTagPath : ORDER_CENTER_TAG_DEFAULT;
    }

    public boolean isEnabled(){
        return enabled;
    }

    public JsonElement getStoreIds(){
        String result = executeAPI(orderCenterURL);
        JsonElement storeIds = new JsonPrimitive("");

        if (StringUtils.isNotEmpty(result)) {
            JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
            if(jsonObject.has("values")) {
                storeIds = jsonObject.get("values");
            }
        }

        return storeIds;
    }

    private String executeAPI(final String uri){
        final StringBuilder responseBody = new StringBuilder();
        LOG.info("Starting from OrderCenterStoreIdServiceImpl executeAPI() method");
        try{
            final HttpClient client = httpFactory.newBuilder().setConnectionManager(getMultiThreadedConf()).build();
            final URIBuilder uriBuilder = new URIBuilder(uri);
            final URI build = uriBuilder.build();

            HttpGet httprequest = new HttpGet(build);
            httprequest.addHeader(CommonConstants.ACCEPT, CommonConstants.APPLICATION_JSON);

            final HttpResponse httpResponse = client.execute(httprequest);
            final String reasonPhrase = httpResponse.getStatusLine().getReasonPhrase();

            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
                String line;
                while (null != (line = bufferedReader.readLine()) ) {
                    responseBody.append(line);
                }
                bufferedReader.close();

            } else {
                LOG.debug("Order Center API request returned the following status: {}", reasonPhrase);
            }
        }  catch (URISyntaxException e) {
            LOG.error("URISyntaxException while calling the Order Center api: {}", e.getMessage());
        } catch (ClientProtocolException e) {
            LOG.error("ClientProtocolException while calling the Order Center api: {}", e.getMessage());
        } catch (IOException e) {
            LOG.error("IOException while calling the ElOrder Centeroqua api: {}", e.getMessage());
        }

        LOG.info("exited from OrderCenterStoreIdServiceImpl executeAPI() method");
        return responseBody.toString();

    }

    private PoolingHttpClientConnectionManager getMultiThreadedConf() {
        if (conMgr == null) {
            conMgr = new PoolingHttpClientConnectionManager();
            conMgr.setMaxTotal(maxConnections);
            conMgr.setDefaultMaxPerRoute(maxHostConnections);
        }
        return conMgr;
    }


}
