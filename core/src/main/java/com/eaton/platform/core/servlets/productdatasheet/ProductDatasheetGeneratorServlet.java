package com.eaton.platform.core.servlets.productdatasheet;

import com.day.cq.commons.Externalizer;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.search.service.SkuSearchService;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


@Component(service = Servlet.class,
        immediate = true,
        property = {
                ServletConstants.SLING_SERVLET_METHODS_POST,
                ServletConstants.SLING_SERVLET_PATHS + "/eaton/productDatasheetGenerator",
        })
public class ProductDatasheetGeneratorServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 2598426539166789515L;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductDatasheetGeneratorServlet.class);

    @Reference
    private SkuSearchService skuSearchService;


    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        LOGGER.info("ProductDatasheetGeneratorServlet :: goPost() :: Started");
        ProductDatasheetGeneratorInput productDatasheetGeneratorInput = new Gson().fromJson(request.getReader(), ProductDatasheetGeneratorInput.class);
        String baseUrl = getBaseUrl(request);
        if (null != baseUrl) {
                List<String> skuIDs = productDatasheetGeneratorInput.getSkuIDs();
                List<Locale> locales = productDatasheetGeneratorInput.getLocales();
                Map<Locale, Map<String, Boolean>> validSkuIdsPerLocale = new HashMap<>();
                locales.forEach(locale -> validSkuIdsPerLocale.put(locale, isValidSKUForLocale(locale.toLanguageTag().toLowerCase(), skuIDs)));
                ProductDatasheetGeneratorOutput productDatasheetGeneratorOutput = new ProductDatasheetGeneratorOutput();
                skuIDs.forEach(skuId -> {
                    ProductDatasheetOutput productDatasheetOutput = createProductDatasheetOutput(baseUrl, locales, validSkuIdsPerLocale, skuId);
                    productDatasheetGeneratorOutput.addDataSheetOutput(productDatasheetOutput);
                });
            try (PrintWriter responseWriter = response.getWriter()) {
                responseWriter.write(new Gson().toJson(productDatasheetGeneratorOutput));
            }
        }
        LOGGER.info("ProductDatasheetGeneratorServlet :: doPost() :: Exit");
    }

    private static ProductDatasheetOutput createProductDatasheetOutput(String baseUrl, List<Locale> locales, Map<Locale, Map<String, Boolean>> validSkuIdsPerLocale, String skuId) {
        ProductDatasheetOutput productDatasheetOutput = new ProductDatasheetOutput(skuId);
        locales.forEach(locale -> {
            if (validSkuIdsPerLocale.containsKey(locale) && Boolean.TRUE.equals(validSkuIdsPerLocale.get(locale).get(skuId))) {
                productDatasheetOutput.addPresentLocale(locale, baseUrl);
            } else {
                productDatasheetOutput.addAbsentLocale(locale);
            }
        });
        return productDatasheetOutput;
    }

    private Map<String, Boolean> isValidSKUForLocale(String locale, List<String> skuIds) {
        Map<String, Boolean> skuStatusMap = new HashMap<>();
        try {
            Map<String, String> inventoryIdPerSkuId = skuSearchService.searchInventoryIdsPerSkuIdsForLocale(locale, skuIds);
            skuIds.forEach(skuId -> {
                skuStatusMap.put(skuId, inventoryIdPerSkuId.containsKey(skuId));
            });
            return skuStatusMap;
        } catch (IOException e) {
            LOGGER.error("Unexpected IOException in validation of skuIds {} for locale {}", skuIds, locale);
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private String getBaseUrl(SlingHttpServletRequest request) {
        ResourceResolver resourceResolver = request.getResourceResolver();
        Externalizer externalizer = resourceResolver.adaptTo(Externalizer.class);
        if (externalizer != null) {
            String pagePath = externalizer.externalLink(resourceResolver, CommonConstants.EXTERNALIZER_DOMAIN_EATON, CommonConstants.COUNTRY);
            return pagePath.replace(CommonConstants.COUNTRY, StringUtils.EMPTY);
        }
        return null;
    }
}
