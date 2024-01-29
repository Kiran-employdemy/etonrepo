package com.eaton.platform.integration.priceSpider.models;

import com.day.cq.dam.api.Asset;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.CloudConfigService;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Model(adaptables = { Resource.class,
        SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class PriceSpiderModel {
    private static final Logger LOG = LoggerFactory.getLogger(PriceSpiderModel.class);

    private boolean priceSpider = false;


    @Inject
    private CloudConfigService cloudConfigService;


    @Inject
    @ScriptVariable
    private Page currentPage;

    /** The SlingHttpServletRequest */
    @Inject
    @Source("sling-object")
    private SlingHttpServletRequest slingRequest;


    /** The resource resolver. */
    @Inject
    @Source("sling-object")
    private ResourceResolver resourceResolver;

    private String universalScriptPath = "";

    private String productScriptPath = "";

    @PostConstruct
    public void init() {
        if(currentPage != null){
            ValueMap pageProperties = currentPage.getProperties();
            if (null != pageProperties) {
                final Optional<PriceSpiderConfigModel> priceSpiderCloudConfig = cloudConfigService
                        .getPriceSpiderCloudConfig(currentPage.getContentResource());
                if (priceSpiderCloudConfig.isPresent()) {
                    PriceSpiderConfigModel priceSpiderConfigModel = priceSpiderCloudConfig.get();

                    String fileLocation = priceSpiderConfigModel.getJsonLocation();
                    universalScriptPath = priceSpiderConfigModel.getUniversalScriptPath();
                    productScriptPath = priceSpiderConfigModel.getProductScriptPath();

                    String pagetype = CommonUtil.getStringProperty(pageProperties, CommonConstants.PAGE_TYPE).replace("-", CommonConstants.SPACE_STRING);
                    if (StringUtils.equalsIgnoreCase(pagetype, CommonConstants.DATA_LAYER_PRODUCT_SKU) &&
                            StringUtils.isNotEmpty(fileLocation) && resourceResolver != null) {
                        priceSpider = isSkuInPriceSpiderList(fileLocation);
                    }
                }
            }
        }
    }

    /**
     * Sets the product detail pages.
     */
    private String getProductSku() {
        String prodsku = "";

        LOG.debug("PriceSpiderModel : This is Entry of setProductDetailPages() method");
        if(slingRequest!=null){
            String[] selectors = slingRequest.getRequestPathInfo().getSelectors();
            if (selectors.length > 0) {
                String prodSkuPath = slingRequest.getRequestPathInfo().getSelectors()[0];
                prodSkuPath = CommonUtil.decodeSearchTermString(prodSkuPath);
                if (StringUtils.isNotEmpty(prodSkuPath)) {
                    prodsku = prodSkuPath;
                }
            }
        }

        return prodsku;
    }

    /**
     *  Checks if the current sku is in the priceSpider
     * @return
     */
    private boolean isSkuInPriceSpiderList(String fileLocation){
        LOG.debug("Start of isSkuInPriceSpiderList");
        String productSku = getProductSku();
        JSONObject jsonObj = new JSONObject();
        LOG.debug("Found JSON file : ", fileLocation);
        Resource resource = resourceResolver.getResource(fileLocation);
        if (resource != null) {

            Resource original = resource.adaptTo(Asset.class).getOriginal();
            InputStream content = original.adaptTo(InputStream.class);
            try {

                StringBuilder sb = new StringBuilder();
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        content, StandardCharsets.UTF_8));

                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                jsonObj = new JSONObject(sb.toString());
            } catch (IOException | JSONException e) {
                LOG.error("Failed to read priceSpider JSON file", e);
            } finally {
                try {
                    content.close();
                } catch(IOException e){
                    LOG.error("Unable to close input stream for Price Spider JSON File", e);
                }
            }

            if (jsonObj.has("skus")) {
                try {
                    JSONArray skus = jsonObj.getJSONArray("skus");
                    for (int i = 0; i < skus.length(); i++) {
                        if (StringUtils.equalsIgnoreCase(skus.getString(i), productSku)) {
                            return true;
                        }
                    }
                } catch (JSONException e) {
                    LOG.error("Failed to navigate returned JSON from Price Spider Config ", e);
                }
            }
        }


        LOG.debug("End of isSkuInPriceSpiderList");
        return false;
    }

    public boolean isPriceSpider() {
        return priceSpider;
    }

    public String getUniversalScriptPath() {
        return universalScriptPath;
    }

    public String getProductScriptPath() {
        return productScriptPath;
    }
}
