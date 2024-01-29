package com.eaton.platform.core.webtools.servlets;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.core.webtools.constants.WebtoolsConstants;
import com.eaton.platform.core.webtools.services.WebtoolsServiceConfiguration;
import com.eaton.platform.core.webtools.util.WebtoolsUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.Map;

/**
 * <html> Description: Backshell resource based servlet.
 *
 * @author ICF
 * @version 1.0
 * @since 2022
 *
 */
@Component(service = Servlet.class,
        immediate = true,
        property = {
                ServletConstants.SLING_SERVLET_METHODS_GET,
                ServletConstants.SLING_SERVLET_RESOURCE_TYPES + "eaton/components/web-tools/backshell",
                ServletConstants.SLING_SERVLET_EXTENSION_JSON
        })
public class BackshellServlet extends SlingSafeMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(BackshellServlet.class);
    private static final long serialVersionUID = 1L;

    @Reference
    private transient WebtoolsServiceConfiguration webtoolsService;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
            throws IOException {
        JsonObject jsonObject = new JsonObject();

        try {
            // Flow for First JSON which holds the Product, Basic Series and Basic Part Number details
            if(request.getRequestPathInfo().getSelectors().length == 0){
                LOG.info("Inside Build Backshell Flow where selector length is zero");
                jsonObject = webtoolsService.getBackshellMetadataResults();
                LOG.info("Build Backshell Metadata JSON:: Exited successfully");
            }
            // Flow for Components and Metadata section
            if(request.getRequestPathInfo().getSelectors().length>0){
                jsonObject = getIntegratedJsonFromAPI(request);
                LOG.info("Components and Part Build JSON:: Exited successfully");
            }
            if(jsonObject != null){
                response.setContentType("application/json");
                response.getWriter().write(jsonObject.toString());
            }
        } catch (Exception e) {
            LOG.error("Exception in BackshellServlet class doGet method {}",e);
        }
    }

    /**
     * Method to get the Part Id from the http request selector
     * @param slingRequest
     * @return partId int
     */
    private static int getPartIdApiJsonPath(SlingHttpServletRequest slingRequest){
        LOG.debug("Inside Get Part ID from Request Flow");

        String[] selectors = selectors = slingRequest.getRequestPathInfo().getSelectors();
        final Map<String, String> requestParameterMap = WebtoolsUtil.getParamsFromSelectors(selectors);
        final int partId = Integer.parseInt(requestParameterMap.get(WebtoolsConstants.PART_ID));

        return partId;
    }

    /**
     * Method to get the complete integrated JSON array from API and AEM internal mapping
     * @param slingHttpServletRequest
     * @return JsonObject
     */
    private JsonObject getIntegratedJsonFromAPI(SlingHttpServletRequest slingHttpServletRequest){
        LOG.debug("Inside the Components and Metadata Json Flow");
        final ResourceResolver resourceResolver = slingHttpServletRequest.getResourceResolver();
        final String backshellMappingJsonPath = WebtoolsConstants.BACKSHELL_MAPPING_JSON_PATH;
        LOG.debug("Internal Json Path {}", backshellMappingJsonPath);
        final Resource resource = resourceResolver.getResource(backshellMappingJsonPath);

        final String refererURL = CommonUtil.getRefererURL(slingHttpServletRequest);
        final String currentPagePath = CommonUtil.getContentPath(resourceResolver, refererURL);
        final Page currentPage = resourceResolver.resolve(currentPagePath).adaptTo(Page.class);

        JsonObject backshellInternalJsonMapping = new JsonObject();
        if (null != resource) {
            final Asset assetMapper = resource.adaptTo(Asset.class);
            if (null != assetMapper && assetMapper.getRendition(DamConstants.ORIGINAL_FILE) != null) {
                backshellInternalJsonMapping = WebtoolsUtil.createComponentsJsonBuilder(assetMapper);
            }
        }
        return getAllIntegratedJson(slingHttpServletRequest, currentPage, backshellInternalJsonMapping);
    }

    /**
     * Method to get the generate the each integrated JSON array sections from API and AEM internal mapping
     * @param httpServletRequest
     * @param page
     * @param internalJsonObject
     * @return JsonObject apiJsonObject
     */
    private JsonObject getAllIntegratedJson(SlingHttpServletRequest httpServletRequest, Page page, JsonObject internalJsonObject){
        int partId = getPartIdApiJsonPath(httpServletRequest);
        JsonObject apiJsonObject = webtoolsService.getBackshellPartBuildResults(partId);

        if(internalJsonObject != null && apiJsonObject != null){

            //Integration of Component-Definitions array
            apiJsonObject.add(WebtoolsConstants.COMPONENT_DEFINITIONS, getIntegratedJsonArray(internalJsonObject, apiJsonObject, httpServletRequest, page,
                    WebtoolsConstants.COMPONENT_DEFINITIONS, WebtoolsConstants.COMPONENT_DEFINITIONS, WebtoolsConstants.COMPONENT_TYPE_ID, WebtoolsConstants.COMPONENT_DISPLAY_NAME));

            //Integration of Part-notes array
            apiJsonObject.add(WebtoolsConstants.PART_NOTES, getIntegratedJsonArray(internalJsonObject, apiJsonObject, httpServletRequest, page,
                    WebtoolsConstants.PART_NOTES, WebtoolsConstants.PART_NOTES, WebtoolsConstants.MESSAGE_CODE, WebtoolsConstants.MESSAGE));

            //Integration of RHS-Metadata array
            apiJsonObject.add(WebtoolsConstants.RIGHT_HAND_METADATA_DEFINITIONS, getIntegratedJsonArray(internalJsonObject, apiJsonObject, httpServletRequest, page,
                    WebtoolsConstants.RIGHT_HAND_METADATA_DEFINITIONS, WebtoolsConstants.COMPONENT_DEFINITIONS, WebtoolsConstants.COMPONENT_TYPE_ID, WebtoolsConstants.LABEL));

            //Integration of LHS-Metadata array
            apiJsonObject.add(WebtoolsConstants.LEFT_HAND_METADATA_DEFINITIONS, getIntegratedJsonArray(internalJsonObject, apiJsonObject, httpServletRequest, page,
                    WebtoolsConstants.LEFT_HAND_METADATA_DEFINITIONS, WebtoolsConstants.COMPONENT_DEFINITIONS, WebtoolsConstants.COMPONENT_TYPE_ID, WebtoolsConstants.LABEL));
        }
        return apiJsonObject;
    }

    private static JsonArray getIntegratedJsonArray(JsonObject internalComponentDefObject, JsonObject apiComponentDefObject, SlingHttpServletRequest httpServletRequest, Page page,
                                                                   String apiJsonObjectName, String internalJsonObjectName, String comparisionPropertyName, String jsonPropertyName){
        JsonArray integratedComponentDefinitionArray = new JsonArray();
        JsonArray internalDefinitionArray = internalComponentDefObject.getAsJsonArray(internalJsonObjectName);
        JsonArray apiDefinitionArray = apiComponentDefObject.getAsJsonArray(apiJsonObjectName);

        if(!internalDefinitionArray.isJsonNull() && !apiDefinitionArray.isJsonNull()){
            for (JsonElement apiJsonElement : apiDefinitionArray){
                Boolean componentFlag = false;
                for (JsonElement componentElement : internalDefinitionArray){
                    if(!apiJsonElement.isJsonNull() && !componentElement.isJsonNull() &&
                            (apiJsonElement.getAsJsonObject().get(comparisionPropertyName).getAsInt() == componentElement.getAsJsonObject().get(comparisionPropertyName).getAsInt())){
                        JsonElement jsonElement = apiJsonElement;
                        LOG.debug("Current page Locale: {}", page.getLanguage(false));
                        jsonElement.getAsJsonObject().addProperty(jsonPropertyName,CommonUtil.getI18NFromLocale(httpServletRequest,
                                componentElement.getAsJsonObject().get(WebtoolsConstants.I18N_KEY_NAME).getAsString(), page.getLanguage(false)));
                        integratedComponentDefinitionArray.add(jsonElement);
                        componentFlag = true;
                        LOG.debug("Integrated JSON Element: {}", jsonElement.toString());
                        break;
                    }
                }
                if (!componentFlag){
                    LOG.debug("JSON Element is added to the object");
                    integratedComponentDefinitionArray.add(apiJsonElement);
                }
            }
        }
        return integratedComponentDefinitionArray;
    }
}
