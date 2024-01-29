package com.eaton.platform.core.webtools.servlets;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.core.webtools.constants.WebtoolsConstants;
import com.eaton.platform.core.webtools.services.WebtoolsServiceConfiguration;
import com.eaton.platform.core.webtools.util.WebtoolsUtil;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * <html> Description: Arinc600 resource based servlet.
 *
 * @author ICF
 * @version 1.0
 * @since 2022
 *
 */
@Component(service = Servlet.class, immediate = true, property = {
        ServletConstants.SLING_SERVLET_METHODS_GET,
        ServletConstants.SLING_SERVLET_RESOURCE_TYPES + "eaton/components/web-tools/arinc",
        ServletConstants.SLING_SERVLET_EXTENSION_JSON
})
public class Arinc600ConnectorServlet extends SlingSafeMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(Arinc600ConnectorServlet.class);
    private static final long serialVersionUID = 1L;

    @Reference
    private transient WebtoolsServiceConfiguration webtoolsService;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
            throws IOException {
        JsonObject jsonObject = new JsonObject();
        response.setContentType("application/json");
        try {
            // Flow for Packaging section
            if (request.getRequestPathInfo().getSelectors().length > 0) {
                String[] selectors = request.getRequestPathInfo().getSelectors();
                List<String> selectorsList = new ArrayList<>();
                for (String stringSelector : selectors) {
                    Integer index = stringSelector.indexOf('$');
                    String selector = stringSelector.substring(0, index);
                    selectorsList.add(selector);
                }
                if (selectorsList.contains(WebtoolsConstants.SHELL_SIZE)
                        && selectorsList.contains(WebtoolsConstants.MOUNTING_RELEASE_CODE)
                        && selectorsList.contains(WebtoolsConstants.PKG_FIRST_LETTER)
                        && selectorsList.contains(WebtoolsConstants.PKG_SECOND_LETTER)) {
                    jsonObject = getValidPackagePath(request);

                    LOG.info("Packaging JSON:: Exited successfully");
                } else {
                    jsonObject = getPackageJsonPath(request);
                    LOG.info("Packaging JSON:: Exited successfully");
                }
                response.getWriter().write(jsonObject.toString());
            }
            // Flow for First JSON which holds the shell, polarization and insert
            // configuration details
            if (request.getRequestPathInfo().getSelectors().length == 0) {
                jsonObject = getIntegratedJsonFromAPI(request);
                LOG.info("Metadata JSON:: Exited successfully");
                if (jsonObject != null) {
                    response.setContentType("application/json");
                    response.getWriter().write(jsonObject.toString());

                }
            }
        } catch (Exception e) {
            LOG.error("Exception in Arinc600ConnectorServlet class doGet method {}", e);
        }
    }


    /**
     * Method to get the Packing JSON file path the internal resources
     * 
     * @param slingRequest
     * @return packingApiJsonPath String
     */
    private JsonObject getPackageJsonPath(SlingHttpServletRequest slingRequest) {
        LOG.debug("Inside Packaging Json Flow");
        JsonObject responsePackagingJson = new JsonObject();

        String[] selectors = selectors = slingRequest.getRequestPathInfo().getSelectors();
        final Map<String, String> requestParameterMap = WebtoolsUtil.getParamsFromSelectors(selectors);

        JsonObject packageParamJson = new JsonObject();
        packageParamJson.addProperty(WebtoolsConstants.CAVITY_SIGNAL_CODE,
                requestParameterMap.get(WebtoolsConstants.SIGNAL_SOURCE_CODE).toUpperCase(Locale.ROOT));
        packageParamJson.addProperty(WebtoolsConstants.CAVITY_POWER_DIGIT,
                requestParameterMap.get(WebtoolsConstants.POWER_SOURCE_CODE).toUpperCase());
        packageParamJson.addProperty(WebtoolsConstants.SHELL_SIZE,
                Integer.parseInt(requestParameterMap.get(WebtoolsConstants.SHELL_SIZE)));
        packageParamJson.addProperty(WebtoolsConstants.MOUNTING_RELEASE,
                requestParameterMap.get(WebtoolsConstants.MOUNTING_RELEASE_CODE).toUpperCase());
        packageParamJson.addProperty(WebtoolsConstants.SHELL_TYPE,
                requestParameterMap.get(WebtoolsConstants.SHELL_TYPE).toUpperCase());
        packageParamJson.addProperty(WebtoolsConstants.CONNECTOR_TYPE,
                requestParameterMap.get(WebtoolsConstants.CONNECTOR_TYPE).toLowerCase(Locale.ROOT));

        responsePackagingJson = webtoolsService.getArincPackagingOptions(packageParamJson);

        return responsePackagingJson;
    }

    /**
     * Method to get the integrated Component JSON array
     * 
     * @param slingHttpServletRequest
     * @return JsonArray
     */
    private JsonObject getIntegratedJsonFromAPI(SlingHttpServletRequest slingHttpServletRequest) {
        LOG.debug("Inside Metadata Json Flow");
        final ResourceResolver resourceResolver = slingHttpServletRequest.getResourceResolver();
        final String mappingJsonPath = WebtoolsConstants.ARINC_MAPPING_JSON_PATH;
        final Resource resource = resourceResolver.getResource(mappingJsonPath);

        final String refererURL = CommonUtil.getRefererURL(slingHttpServletRequest);
        final String currentPagePath = CommonUtil.getContentPath(resourceResolver, refererURL);
        final Page currentPage = resourceResolver.resolve(currentPagePath).adaptTo(Page.class);

        JsonObject arincInternalJsonMapping = new JsonObject();
        if (null != resource) {
            final Asset arincMapper = resource.adaptTo(Asset.class);
            if (null != arincMapper && arincMapper.getRendition(DamConstants.ORIGINAL_FILE) != null) {
                arincInternalJsonMapping = WebtoolsUtil.createComponentsJsonBuilder(arincMapper);
            }
        }
        return getIntegratedComponentJsonArray(slingHttpServletRequest, currentPage, arincInternalJsonMapping);
    }

    /**
     * Method to get the integrated JSON array from API and AEM internal mapping
     * 
     * @param httpServletRequest
     * @param page
     * @param internalJsonObject
     * @return JsonArray integratedApiJsonComponentArray
     */
    private JsonObject getIntegratedComponentJsonArray(SlingHttpServletRequest httpServletRequest, Page page,
            JsonObject internalJsonObject) {
        JsonObject apiJsonObject = webtoolsService.getArincComponents();
        JsonArray integratedApiJsonComponentArray = new JsonArray();

        if (internalJsonObject != null && apiJsonObject != null) {
            JsonArray componentArray = internalJsonObject.getAsJsonArray(WebtoolsConstants.COMPONENTS);
            JsonArray apiJsonArray = apiJsonObject.getAsJsonArray(WebtoolsConstants.COMPONENTS);
            for (JsonElement apiJsonElement : apiJsonArray) {
                Boolean componentFlag = false;
                for (JsonElement componentElement : componentArray) {
                    if (apiJsonElement.getAsJsonObject().get(WebtoolsConstants.ID).getAsInt() == componentElement
                            .getAsJsonObject().get(WebtoolsConstants.ID).getAsInt()) {
                        JsonElement jsonElement = apiJsonElement;
                        LOG.debug("Current page Locale: {}", page.getLanguage(false));
                        jsonElement.getAsJsonObject().addProperty(WebtoolsConstants.IS_DEFAULT,
                                componentElement.getAsJsonObject().get(WebtoolsConstants.IS_DEFAULT).getAsBoolean());
                        jsonElement.getAsJsonObject().addProperty(WebtoolsConstants.COMPONENT_GROUP, componentElement
                                .getAsJsonObject().get(WebtoolsConstants.COMPONENT_GROUP).getAsString());
                        jsonElement.getAsJsonObject().addProperty(WebtoolsConstants.FORM_ELEMENT_TYPE, componentElement
                                .getAsJsonObject().get(WebtoolsConstants.FORM_ELEMENT_TYPE).getAsString());
                        jsonElement.getAsJsonObject().addProperty(WebtoolsConstants.COMPONENT_UNIQUE_ID,
                                componentElement.getAsJsonObject().get(WebtoolsConstants.COMPONENT_UNIQUE_ID)
                                        .getAsString());
                        jsonElement.getAsJsonObject().add(WebtoolsConstants.RESET_POSITION_IDS, componentElement
                                .getAsJsonObject().get(WebtoolsConstants.RESET_POSITION_IDS).getAsJsonArray());
                        jsonElement.getAsJsonObject().get(WebtoolsConstants.COMPONENT_TYPE).getAsJsonObject()
                                .addProperty(WebtoolsConstants.NAME, CommonUtil.getI18NFromLocale(httpServletRequest,
                                        componentElement.getAsJsonObject().get(WebtoolsConstants.I18N_KEY_NAME)
                                                .getAsString(),
                                        page.getLanguage(false)));
                        integratedApiJsonComponentArray.add(jsonElement);
                        componentFlag = true;
                        LOG.debug("Integrated JSON Element: {}", jsonElement.toString());
                        break;
                    }
                }
                if (!componentFlag) {
                    LOG.debug("JSON Element is added to the object");
                    integratedApiJsonComponentArray.add(apiJsonElement);
                }
            }
            apiJsonObject.add("components", integratedApiJsonComponentArray);
        }
        return apiJsonObject;
    }

    private JsonObject getValidPackagePath(SlingHttpServletRequest request) {
        LOG.debug("Inside Packaging Json validation Flow");
        JsonObject responsePackagingJson = new JsonObject();

        String[] selectors = request.getRequestPathInfo().getSelectors();
        final Map<String, String> requestParameterMap = WebtoolsUtil.getParamsFromSelectors(selectors);

        JsonObject packageParamJson = new JsonObject();
        packageParamJson.addProperty(WebtoolsConstants.SHELL_SIZE,
                Integer.parseInt(requestParameterMap.get(WebtoolsConstants.SHELL_SIZE)));
        packageParamJson.addProperty(WebtoolsConstants.MOUNTING_RELEASE,
                requestParameterMap.get(WebtoolsConstants.MOUNTING_RELEASE_CODE));
        packageParamJson.addProperty(WebtoolsConstants.PKG_FIRST_LETTER,
                requestParameterMap.get(WebtoolsConstants.PKG_FIRST_LETTER));
        packageParamJson.addProperty(WebtoolsConstants.PKG_SECOND_LETTER,
                requestParameterMap.get(WebtoolsConstants.PKG_SECOND_LETTER));

        responsePackagingJson = webtoolsService.getArincPackage(packageParamJson);

        return responsePackagingJson;
    }
}
