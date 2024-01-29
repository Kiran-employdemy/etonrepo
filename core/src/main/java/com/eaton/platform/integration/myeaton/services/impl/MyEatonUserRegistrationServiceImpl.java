/*
 * Eaton
 * Copyright (C) 2020 Eaton. All Rights Reserved
 */

package com.eaton.platform.integration.myeaton.services.impl;

import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.integration.myeaton.bean.UserRegistrationResponseBean;
import com.eaton.platform.integration.myeaton.services.MyEatonServiceConfiguration;
import com.eaton.platform.integration.myeaton.services.MyEatonUserRegistrationService;
import com.eaton.platform.integration.myeaton.util.MyEatonUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static com.day.cq.commons.jcr.JcrConstants.JCR_DATA;

/** Service to handle submission of User Registration information into MyEaton */
@Component(
    service = MyEatonUserRegistrationService.class,
    immediate = true,
    property = {
      AEMConstants.SERVICE_VENDOR_EATON,
      AEMConstants.SERVICE_DESCRIPTION + "My Eaton - User Registration Service",
      AEMConstants.PROCESS_LABEL + "MyEatonUserRegistrationServiceImpl"
    })
public class MyEatonUserRegistrationServiceImpl implements MyEatonUserRegistrationService {
    private static final Logger LOG = LoggerFactory.getLogger(MyEatonUserRegistrationServiceImpl.class);
    private static final String JSON_CONTENT_TYPE = "application/json";

    @Reference
    private HttpClientBuilderFactory httpFactory;

    @Reference
    private MyEatonServiceConfiguration serviceConfiguration;

    private PoolingHttpClientConnectionManager conMgr;

    private String userRegistrationEndpoint;

    private Map<String, String> fieldTypes = new HashMap<>();

    @Activate
    @Modified
    protected  final void activate() {
        userRegistrationEndpoint = serviceConfiguration.getEndpointRoot()
            + serviceConfiguration.getUserRegistrationUrl();
    }

    @Override
    public UserRegistrationResponseBean handleFormData(SlingHttpServletRequest request, ResourceResolver resourceResolver) {
        JsonObject submittedJson = new JsonObject();

        try {
            LOG.debug("Entered into MyEatonUserRegistrationService.handleFormData()");

            String formDataJson = request.getParameter(JCR_DATA);
            JsonParser parser = new JsonParser();

            JsonElement dataRoot = parser.parse(formDataJson);

            LOG.trace("Output of raw JCR_DATA JSON:");
            LOG.trace(dataRoot.toString());

            if (!dataRoot.isJsonObject()) {
                LOG.error("jcr:data could not be interpreted as a JSON Object.");
                return new UserRegistrationResponseBean();
            }

            JsonObject dataRootObj = dataRoot.getAsJsonObject();
            JsonObject afData = dataRootObj.getAsJsonObject("afData");

            LOG.trace("Output of raw afData JSON from JCR_DATA:");
            LOG.trace(afData.toString());

            // Loop through AF JSON, and build a usable JSON object.
            for (Entry<String, JsonElement> field : extractFieldJson(afData, resourceResolver)) {
                String fieldName = field.getKey();
                JsonElement fieldValue = field.getValue();

                LOG.debug("fieldName: " + fieldName);
                LOG.debug("fieldValue: " + fieldValue);

                if(fieldValue.isJsonPrimitive() && !fieldValue.isJsonNull()) {
                    submittedJson.add(fieldName, processField(fieldName, fieldValue));

                    LOG.trace("Field (non-multi value) information added to API JSON payload:");
                    LOG.trace(submittedJson.toString());
                }
            }

            for (Map.Entry<String, JsonArray> field : extracMultifieldJson(afData).entrySet()) {
                submittedJson.add(field.getKey(), field.getValue());

                LOG.trace("Field (multi-value) information added to API JSON payload:");
                LOG.trace(submittedJson.toString());
            }

            LOG.trace("Final API JSON payload before excuteAPI() call:");
            LOG.trace(submittedJson.toString());
        }
        catch (Exception exception) {
            LOG.error("Exception triggered in MyEatonUserRegistrationServiceImpl.handleFormData()",exception);
        }

        return executeAPI(userRegistrationEndpoint, submittedJson);
    }

    /** Extract the field information from the af JSON data
     * @param jsonObject Parent JsonObject
     * @param resourceResolver Resource Resolver
     * @return Set of Entries for each form bound field submitted
     */
    private Set<Entry<String, JsonElement>> extractFieldJson(JsonObject jsonObject, ResourceResolver resourceResolver) {
        Set<Entry<String, JsonElement>> fieldSet = Collections.emptySet();

        try {
            LOG.debug("Entered into MyEatonUserRegistrationService.extractFieldJson()");

            JsonObject fieldData = jsonObject.getAsJsonObject("afBoundData").getAsJsonObject("data");

            LOG.trace("Output of raw afBoundData.data of JCR_DATA:");
            LOG.trace(fieldData.toString());

            Set<String> fdmObjectNames = fieldData.keySet();

            LOG.debug("Attribute keys found in afBoundData.data: " + fdmObjectNames);

            if (fdmObjectNames.size() > 1) {
                LOG.warn("The form seems to be tied to multiple data models. This may not be well supported.");
            }

            for (String fdmObjectName: fdmObjectNames) {
                JsonObject fieldBoundData = fieldData.getAsJsonObject(fdmObjectName);

                LOG.trace("JSON definition for field: " + fdmObjectName);
                LOG.trace(fieldBoundData.toString());

                processFieldTypes(extractFormPath(jsonObject), fdmObjectName, resourceResolver);

                fieldSet = fieldBoundData.entrySet();

                LOG.debug("Calculated the following fieldSet for this field:");
                LOG.debug(fieldSet.toString());
            }

            if (fieldSet.isEmpty()) {
                LOG.error("Could not find data model object associated with any fields.");
            }

            LOG.debug("Final calculated fieldSet:");
            LOG.debug(fieldSet.toString());
        }
        catch (Exception exception) {
            LOG.error("Exception triggered in MyEatonUserRegistrationServiceImpl.extractFieldJson()",exception);
        }

        return fieldSet;
    }

    private Map<String, JsonArray> extracMultifieldJson(JsonObject jsonObject) {
        Map<String, JsonArray> result = new HashMap<>();

        try {
            LOG.debug("Entered into MyEatonUserRegistrationService.extracMultifieldJson");

            String repeatableJsonString;

            if (jsonObject.getAsJsonObject("afUnboundData").getAsJsonObject("data").get("repeatabl").getAsString() != null) {
                repeatableJsonString = jsonObject.getAsJsonObject("afUnboundData")
                        .getAsJsonObject("data").get("repeatabl").getAsString();

                LOG.trace("Raw output of afUnboundData.data.repeatabl from JCR_DATA:");
                LOG.trace(repeatableJsonString);
            } else {
                LOG.debug("No multifield data found for user registration form.");
                return result;
            }

            JsonParser parser = new JsonParser();
            JsonObject repeatableJson = parser.parse(repeatableJsonString).getAsJsonObject();
            Set<String> panelKeys = repeatableJson.keySet();

            LOG.debug("Panel Keys found in afUnboundData.data.repeatabl: " + panelKeys);

            for (String panelKey: panelKeys) {
                JsonArray panelArray = repeatableJson.getAsJsonArray(panelKey);
                JsonArray formattedArray = new JsonArray();

                LOG.trace("JSON definition of ["+panelKey+"]");
                LOG.trace(panelArray.toString());

                for (JsonElement item: panelArray) {
                    JsonObject repeatableItem = item.getAsJsonObject();
                    JsonObject formattedItem = new JsonObject();
                    Set<String> paramKeys = repeatableItem.keySet();

                    LOG.debug("Parameter Keys found in ["+panelKey+"]: " + paramKeys.toString());

                    for (String paramKey : paramKeys) {
                        if (!repeatableItem.get(paramKey).isJsonNull()) {
                            formattedItem.add(paramKey, processField(paramKey, repeatableItem.get(paramKey)));
                        }
                    }

                    formattedArray.add(formattedItem);

                    LOG.trace("Final JSON array for ["+panelKey+"]:");
                    LOG.trace(formattedArray.toString());
                }

                result.put(panelKey, formattedArray);
            }

            LOG.trace("Final MAP of panelKey:JsonArray:");
            LOG.trace(result.toString());
        }
        catch (Exception exception) {
            LOG.error("Exception triggered in MyEatonUserRegistrationServiceImpl.extracMultifieldJson()",exception);
        }

        return result;
    }

    /**
     * Extracts the path to our adaptive form from the jcr:data JSON
     * @param jsonObject Parent JsonObject
     * @return Path to the adaptive form
     */
    private static String extractFormPath(JsonObject jsonObject) {
        String formPath = new String();

        try {
            JsonObject afSubmissionInfo = jsonObject.getAsJsonObject("afSubmissionInfo");

            LOG.trace("Raw output of JCR_DATA.afSubmissionInfo:");
            LOG.trace(afSubmissionInfo.toString());

            JsonPrimitive afPath = afSubmissionInfo.getAsJsonPrimitive("afPath");

            LOG.trace("Raw output of JCR_DATA.afSubmissionInfo.afPath:");
            LOG.trace(afPath.getAsString());

            formPath = afPath.getAsString();
        }
        catch (Exception exception) {
            LOG.error("Exception triggered in MyEatonUserRegistrationServiceImpl.extractFormPath()",exception);
        }

        return formPath;
    }

    private JsonElement processField(String fieldName, JsonElement fieldJson) {
        try {
            if (isFieldType(fieldName, "array")) {
                LOG.debug("Processing field as - ARRAY");
                LOG.debug("Original JSON:");
                LOG.debug(fieldJson.toString());

                String fieldValue = fieldJson.getAsString();
                String splitChar = ",";
                if (fieldValue.contains("\n")) {
                    splitChar = "\n";
                }
                String[] fieldArray = fieldValue.split(splitChar);

                LOG.debug("Split values: " + fieldArray);

                Gson gson = new Gson();
                return gson.toJsonTree(fieldArray);
            }

            if (isFieldType(fieldName, "boolean")) {
                LOG.debug("Processing field as - BOOLEAN");

                boolean fieldValue = !fieldJson.getAsString().isEmpty();

                LOG.debug("Value: " + fieldValue);

                return new JsonPrimitive(fieldValue);
            }

            if((!fieldJson.getAsString().isEmpty()) && (("Yes").equalsIgnoreCase(fieldJson.getAsString()))){
                LOG.debug("Processing field as - YES");

                boolean fieldValue = true;

                LOG.debug("Value: " + fieldValue);

                return new JsonPrimitive(fieldValue);
            }

            if((!fieldJson.getAsString().isEmpty()) && (("No").equalsIgnoreCase(fieldJson.getAsString()))) {
                LOG.debug("Processing field as - NO");

                boolean fieldValue = false;

                LOG.debug("Value: " + fieldValue);

                return new JsonPrimitive(fieldValue);
            }

            LOG.debug("Unhandled field type - returning original JSON");
        }
        catch (Exception exception) {
            LOG.error("Exception triggered in MyEatonUserRegistrationServiceImpl.processField()",exception);
        }

        return fieldJson;
    }

    /**
     * Builds map of FDM definition types, with the key being the bindRef and value being the type
     * @param formPath Path to the form which binds to the FDM
     * @param fdmName Name of the FDM
     * @param resourceResolver Resource Resolver
     */
    private void processFieldTypes(String formPath, String fdmName, ResourceResolver resourceResolver) {
        try {
            LOG.debug("Using Metadata Path: {}{}", formPath, serviceConfiguration.getMetadataPath());

            Resource formRes = resourceResolver.resolve(formPath
                    + serviceConfiguration.getMetadataPath());
            String schemaRef = "";

            if (!ResourceUtil.isNonExistingResource(formRes)) {
                ValueMap properties = formRes.adaptTo(ValueMap.class);

                if (properties != null) {
                    schemaRef = properties.get("schemaRef", "");
                }
            } else {
                LOG.error("Could not resolve the form path ({}{}) to a resource.", formPath,
                        serviceConfiguration.getMetadataPath());
            }

            LOG.debug("Using Definition Path: {}{}", schemaRef,
                    serviceConfiguration.getFdmDefinitionsPath());

            Resource definitionRes = resourceResolver.resolve(schemaRef
                    + serviceConfiguration.getFdmDefinitionsPath());
            Resource fdmDefinitionsRes = null;

            if (!ResourceUtil.isNonExistingResource(definitionRes)) {
                fdmDefinitionsRes = definitionRes.getChild(fdmName + "/properties");
            } else {
                LOG.error("Could not resolve FDM definition path ({}{}) to a resource.", schemaRef,
                        serviceConfiguration.getFdmDefinitionsPath());
            }

            if (fdmDefinitionsRes == null) {
                LOG.error("Could not resolve the specific FDM path ({}) to a resource.", fdmName);
            }

            buildTypeMap(fdmDefinitionsRes);
        }
        catch (Exception exception) {
            LOG.error("Exception triggered in MyEatonUserRegistrationServiceImpl.processFieldTypes()",exception);
        }
    }

    private void buildTypeMap(Resource resource) {
        try {
            if (resource != null) {
                for (Resource fdmDefinitionRes : resource.getChildren()) {
                    ValueMap properties = fdmDefinitionRes.adaptTo(ValueMap.class);

                    if (properties != null) {
                        String bindRef = properties.get("fdm:bindRef", "");
                        String type = properties.get("type", "");

                        this.fieldTypes.put(bindRef, type);

                        buildRefTypes(fdmDefinitionRes);
                    }
                }
            }
        }
        catch (Exception exception) {
            LOG.error("Exception triggered in MyEatonUserRegistrationServiceImpl.buildTypeMap()",exception);
        }
    }

    private void buildRefTypes(Resource resource) {
        try {
            Resource itemsRes = resource.getChild("items");

            if (itemsRes == null) {
                return;
            }
            ValueMap itemsProps = itemsRes.adaptTo(ValueMap.class);
            if (itemsProps == null) {
                return;
            }
            String refName = itemsProps.get("$ref", "");
            if (refName.isEmpty()) {
                return;
            }
            Resource fdmParent = getThirdParent(resource);
            if (fdmParent == null) {
                return;
            }
            Resource refRes = fdmParent.getChild(refName + "/properties");
            if (refRes == null) {
                return;
            }

            buildTypeMap(refRes);
        }
        catch (Exception exception) {
            LOG.error("Exception triggered in MyEatonUserRegistrationServiceImpl.buildRefTypes()",exception);
        }
    }

    private static Resource getThirdParent(Resource resource) {
        try {
            Resource firstParent = resource.getParent();
            if (firstParent == null) {
                return null;
            }

            Resource secondParent = firstParent.getParent();
            if (secondParent == null) {
                return null;
            }

            return secondParent.getParent();
        }
        catch (Exception exception) {
            LOG.error("Exception triggered in MyEatonUserRegistrationServiceImpl.getThirdParent()",exception);
        }

        LOG.error("Returning NULL from MyEatonUserRegistrationServiceImpl.getThirdParent()");
        return null;
    }

    private UserRegistrationResponseBean executeAPI(final String serviceUrl, final JsonObject jsonBody) {
        UserRegistrationResponseBean userRegistrationResponseBean = new UserRegistrationResponseBean();

        try {
            LOG.debug("Start with executeAPI method ::");
            final StringBuilder content = new StringBuilder();

            try {
                final HttpClient client = httpFactory.newBuilder()
                        .setConnectionManager(MyEatonUtil.getMultiThreadedConf(conMgr, serviceConfiguration))
                        .build();

                final URIBuilder uriBuilder = new URIBuilder(serviceUrl);
                final URI build = uriBuilder.build();
                final HttpPost httpPost = new HttpPost(build);

                LOG.debug("Requset JSON for body --- {}", jsonBody);

                StringEntity reqBody = new StringEntity(jsonBody.toString());
                reqBody.setContentType(JSON_CONTENT_TYPE);
                httpPost.setEntity(reqBody);
                httpPost.setHeader("Content-Type", JSON_CONTENT_TYPE);
                httpPost.setHeader("Authorization", MyEatonUtil.encodeBasicAuthorization(
                        serviceConfiguration.getUsername(), serviceConfiguration.getPassword()));

                final HttpResponse execute = client.execute(httpPost);
                final String reasonPhrase = execute.getStatusLine().getReasonPhrase();

                try (BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(execute.getEntity().getContent(), StandardCharsets.UTF_8))) {
                    String line;
                    while (null != (line = bufferedReader.readLine())) {
                        content.append(line);
                    }
                }

                if (execute.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
                    LOG.info("SUCCESS - valid response from Registration API");

                    if (content != null ) {
                        LOG.trace("Raw response payload from Registration API:");
                        LOG.trace(content.toString());
                    }
                }
                else {
                    LOG.error("Failed to create new account, reason from server: {}", execute.getEntity().getContent());

                    if (content != null ) {
                        LOG.trace("Raw response payload from Registration API:");
                        LOG.trace(content.toString());
                    }
                }
                LOG.info("reasonPhrase: {}", reasonPhrase);

                if (!content.toString().isEmpty()) {
                    userRegistrationResponseBean = new Gson().fromJson(content.toString(),
                            UserRegistrationResponseBean.class);

                }
            } catch (URISyntaxException e) {
                LOG.error("URISyntaxException while calling the api", e);
            } catch (ClientProtocolException e) {
                LOG.error("ClientProtocolException while calling the api", e);
            } catch (IOException e) {
                LOG.error("IOException while calling the api", e);
            }
            userRegistrationResponseBean.setRequestJson(jsonBody);

            LOG.trace("JSON set into userRegistrationResponseBean.requestJson:");
            LOG.trace(jsonBody.toString());

            LOG.info("End with executeAPI method Response :: {}", userRegistrationResponseBean);
        }
        catch (Exception exception) {
            LOG.error("Exception triggered in MyEatonUserRegistrationServiceImpl.executeAPI()",exception);
        }

        return userRegistrationResponseBean;
    }

    private boolean isFieldType(String key, String type) {
        return this.fieldTypes.containsKey(key) && type.equals(this.fieldTypes.get(key));
    }
}
