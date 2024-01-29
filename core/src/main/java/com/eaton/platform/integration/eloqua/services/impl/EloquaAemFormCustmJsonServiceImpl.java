package com.eaton.platform.integration.eloqua.services.impl;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;
import com.day.cq.wcm.webservicesupport.ConfigurationManagerFactory;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.EloquaCloudConfigModel;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.EloquaFormConfigService;
import com.eaton.platform.integration.eloqua.constant.EloquaConstants;
import com.eaton.platform.integration.eloqua.services.EloquaAemFormCustmJsonService;
import com.eaton.platform.integration.eloqua.services.EloquaOptionListService;
import com.eaton.platform.integration.eloqua.services.EloquaService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jcr.Node;
import javax.jcr.Session;
import java.io.*;
import java.nio.charset.StandardCharsets;

@Component(service = EloquaAemFormCustmJsonService.class, immediate = true)
public class EloquaAemFormCustmJsonServiceImpl implements EloquaAemFormCustmJsonService {

    @Reference
    EloquaOptionListService eloquaOptionListService;

    @Reference
    private EloquaFormConfigService eloquaFormConfigService;

    @Reference
    private ConfigurationManagerFactory configurationManagerFactory;

    @Reference
    AdminService adminService;


    @Reference
    private EloquaService eloquaService;

    private static final Logger LOGGER = LoggerFactory.getLogger(EloquaAemFormCustmJsonServiceImpl.class);
    /**
     * This method returns the AEM for Json schema file and placed in DAM.
     *
     * @param eloquaCode
     */
    @Override
    public String createAndGetEloquaFormSchemaPath(final String eloquaCode) {
        LOGGER.debug("Start createAndGetEloquaFormSchemaPath method to get the Eloqua connection ");
        String response = null;
        if(null != eloquaCode ){
            try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
                final EloquaCloudConfigModel eloquaCloudConfigModel = eloquaFormConfigService
                        .setUpEloquaServiceParameters(configurationManagerFactory, null,
                                adminResourceResolver);

                //Call this method to get Eloqua Json from Eloqua Id
                final String eloquaFormResponse = eloquaService.getEloquaForm(eloquaCloudConfigModel, eloquaCode);
                if (null != eloquaFormResponse) {
                    //Call this method to get Custom Aem form Json schema
                    final String aemFormJsonSchema = executeReq(eloquaCloudConfigModel, eloquaFormResponse, eloquaCode);
                    //Create Json file using custom json input
                    InputStream stream = null;
                    try (ResourceResolver writeServiceResourceResolver = adminService.getWriteService()) {
                        stream = new ByteArrayInputStream(aemFormJsonSchema.getBytes(StandardCharsets.UTF_8));
                        final Session session = writeServiceResourceResolver.adaptTo(Session.class);
                        final AssetManager assetMgr = writeServiceResourceResolver.adaptTo(AssetManager.class);
                        //Create new Json file and upload into DAM and extension should be always .schema.json
                        final String newFile = EloquaConstants.JSON_ASSET_PATH.concat(EloquaConstants.SHEMA_JSON_FOLDER.
                                concat(eloquaCode).concat(EloquaConstants.SCHEMA_JSON));
                        final Asset asset = assetMgr.createAsset(newFile, stream, EloquaConstants.APPLICATION_SCHEMA_JSON, Boolean.TRUE);
                        final Resource resolveDamPath = writeServiceResourceResolver.resolve(newFile + EloquaConstants.JCR_CONTENT);
                        if (null != resolveDamPath) {
                            final Node assetPathNode = resolveDamPath.adaptTo(Node.class);
                            assetPathNode.setProperty(EloquaConstants.LCRESOURCE, 1);
                            assetPathNode.setProperty(EloquaConstants.TYPE, EloquaConstants.LCRESOURCE);
                            session.save();
                        }
                        if (asset != null) {
                            response = newFile;
                        }
                    } catch (Exception e) {
                        LOGGER.error("Exception in eloquaCustmJson method : " + e.getMessage());
                    } finally {
                        if (stream != null) {
                            try {
                                stream.close();
                            } catch (IOException e) {
                                LOGGER.error("IOException for stream close : " + e.getMessage());
                            }
                        }
                    }
                }
            }
        }
        LOGGER.debug("End createAndGetEloquaFormSchemaPath method to get the Eloqua connection ");
        return response;
    }

    private String executeReq(EloquaCloudConfigModel eloquaCloudConfigModel, final String eloquaFormResponse, final String eloquaCode)
    {
        LOGGER.debug("Start calling executeReq method from createAndGetEloquaFormSchemaPath method to parse Eloqua json ");
        String finalFieldProperties = StringUtils.EMPTY;
        String requiredField = StringUtils.EMPTY;
        String elqSiteId = StringUtils.EMPTY;
        String elqFormName = StringUtils.EMPTY;
        String elqCampaignId = StringUtils.EMPTY;
        String formActionUrl = StringUtils.EMPTY;
        try{
            final JsonParser parser = new JsonParser();
            final JsonObject jsonResult =  (JsonObject) parser.parse(eloquaFormResponse);
            LOGGER.debug("JsonResult Result from Eloqua:::  "+jsonResult);
            final JsonArray jsonArray = jsonResult.getAsJsonArray(EloquaConstants.ELEMENTS);
            for (int i=0; i < jsonArray.size(); i++) {
                JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
                // Check Eloqua json have PROGRESSIVE_PROFILE field data
                if(jsonObject.has(EloquaConstants.NAME) && jsonObject.has(EloquaConstants.TYPE) && jsonObject.get(EloquaConstants.TYPE).
                        getAsString().equals(EloquaConstants.PROGRESSIVE_PROFILE)){
                    final JsonArray progressiveProfileFieldsJsonArr = jsonObject.getAsJsonArray(EloquaConstants.FIELDS);
                    for (int l=0; l < progressiveProfileFieldsJsonArr.size(); l++) {
                        final JsonObject progressiveProfileFieldsJsonObject = progressiveProfileFieldsJsonArr.get(l).getAsJsonObject();
                        final JsonObject ProgressiveProfileFieldJson = getProgressiveProfileField(progressiveProfileFieldsJsonArr,
                                eloquaCode,eloquaCloudConfigModel,progressiveProfileFieldsJsonObject);
                        finalFieldProperties +=ProgressiveProfileFieldJson.get(EloquaConstants.FINAL_FIELD_PROPERTIES).getAsString();
                        requiredField +=ProgressiveProfileFieldJson.get(EloquaConstants.REQUIRED_FIELD).getAsString();
                    }
                    // if Eloqua json does not have PROGRESSIVE_PROFILE field data then have the field from elements param
                }else {
                    final String fieldProperties =  getFormFields(jsonObject,eloquaCode,eloquaCloudConfigModel);
                    if(fieldProperties != null && fieldProperties.contains(EloquaConstants.HASH_CONSTANT)){
                        requiredField += fieldProperties.substring(fieldProperties.indexOf(EloquaConstants.HASH_CONSTANT),fieldProperties.length()).
                                replace(EloquaConstants.HASH_CONSTANT,StringUtils.EMPTY);;
                        finalFieldProperties += fieldProperties.substring(0,fieldProperties.indexOf(EloquaConstants.HASH_CONSTANT));
                    }else {
                        finalFieldProperties +=fieldProperties;
                    }
                }
            }
            //Add all required filed in string array
            if(requiredField.contains(CommonConstants.COMMA)) {
                requiredField = requiredField.substring(0, requiredField.lastIndexOf(CommonConstants.COMMA));
                requiredField = ("\"required\":").concat("[" + requiredField + "]\n");
            }
            final int lastComma = finalFieldProperties.lastIndexOf(CommonConstants.COMMA);
            finalFieldProperties = finalFieldProperties.substring(0,lastComma);

            if(jsonResult.has(EloquaConstants.HTML)) {
                final Document html = Jsoup.parse(jsonResult.get(EloquaConstants.HTML).getAsString());

                 elqSiteId = html.select(EloquaConstants.ELQ_SITE_ID).val();
                 elqFormName = html.select(EloquaConstants.ELQ_FORM_NAME).val();
                 elqCampaignId = html.select(EloquaConstants.ELQ_CAMPAIGN_ID).val();
                 formActionUrl = html.select(EloquaConstants.FORM).attr(EloquaConstants.ACTION);
            }
        }
        catch(Exception e){
            LOGGER.error("exception occured while sending http request : "+e.getMessage());
        }
        //Call getAemFormJsonSchema method to construct the Json using below param.
            String aemFormJsonSchema =  getAemFormJsonSchema(eloquaCode,elqSiteId,elqFormName,elqCampaignId,finalFieldProperties,requiredField,formActionUrl);
        LOGGER.debug("End executeReq method ");
        return aemFormJsonSchema;
    }

    private JsonObject  getProgressiveProfileField(final JsonArray progressiveProfileFieldsJsonArr, final String eloquaCode,
                                                   final EloquaCloudConfigModel eloquaCloudConfigModel,
                                                   final JsonObject progressiveProfileFieldsJsonObject) {
        LOGGER.debug("Start getProgressiveProfielField method ");
        JsonObject jsonData = new JsonObject();
        String finalFieldProperties=null;
        String requiredField =StringUtils.EMPTY;
        JsonObject jsonObject = progressiveProfileFieldsJsonObject;
        final String fieldProperties =  getFormFields(jsonObject,eloquaCode,eloquaCloudConfigModel);
        if(null != fieldProperties && fieldProperties.contains(EloquaConstants.HASH_CONSTANT)){
            requiredField = fieldProperties.substring(fieldProperties.indexOf(EloquaConstants.HASH_CONSTANT),fieldProperties.length()).
                    replace(EloquaConstants.HASH_CONSTANT,StringUtils.EMPTY);
            finalFieldProperties = fieldProperties.substring(0,fieldProperties.indexOf(EloquaConstants.HASH_CONSTANT));
        }else {
            finalFieldProperties =fieldProperties;
        }
        jsonData.addProperty(EloquaConstants.FINAL_FIELD_PROPERTIES,finalFieldProperties);
        jsonData.addProperty(EloquaConstants.REQUIRED_FIELD,requiredField);
        LOGGER.debug("End getProgressiveProfielField method ");
        return jsonData;
    }

    private String getFormFields(JsonObject jsonObject, String eloquaCode,
                                 EloquaCloudConfigModel eloquaCloudConfigModel){
        LOGGER.debug("Start getFormFields method ");
        String fieldProperties =StringUtils.EMPTY;
        String requiredField = null;
        if (jsonObject.has(EloquaConstants.NAME) && jsonObject.has(EloquaConstants.HTML_NAME)) {
            final String title = jsonObject.get(EloquaConstants.NAME).getAsString();
            String name = jsonObject.get(EloquaConstants.HTML_NAME).getAsString();
            final String type = jsonObject.get(EloquaConstants.DATA_TYPE).getAsString();

            String optionList = StringUtils.EMPTY;
            String format = null;
            String maxLength = null;
            String minLength = null;
            String dataType =  getFieldDataType(type,name);
            String afProperties = getAfProperties(jsonObject);

            if(jsonObject.has(EloquaConstants.OPTION_LIST_ID) && !name.toLowerCase().contains("country")){
                optionList = eloquaOptionListService.getOptionList(eloquaCloudConfigModel,jsonObject.get(EloquaConstants.OPTION_LIST_ID).getAsString());
            }else if(name.toLowerCase().contains("country")){
                optionList =  eloquaOptionListService.getCountryFromDAMOptionList();
            }
            if (jsonObject.has(EloquaConstants.VALIDATIONS) && jsonObject.getAsJsonArray(EloquaConstants.VALIDATIONS).size() != 0) {
                final JsonArray jsonArrayOfValidation =  jsonObject.getAsJsonArray(EloquaConstants.VALIDATIONS);
                for (int k = 0; k < jsonArrayOfValidation.size(); k++) {
                    final JsonObject jsonObjectOfValidation = jsonArrayOfValidation.get(k).getAsJsonObject();
                    if (jsonObjectOfValidation.has(EloquaConstants.CONDITION)) {
                        final String condition = jsonObjectOfValidation.get(EloquaConstants.CONDITION).toString();
                        if (condition.equals(EloquaConstants.IS_REQUIRED_CONDITION)) {
                            requiredField = "\"" + name + "\",";
                        }
                        if (condition.equals(EloquaConstants.IS_EMAIL_ADDRESS_CONDITION)) {
                            format = EloquaConstants.EMAIL_FORMAT;
                        }
                        if(condition.contains(EloquaConstants.MAXIMUM)){
                            final String maximum = jsonObjectOfValidation.get(EloquaConstants.CONDITION).
                                    getAsJsonObject().get(EloquaConstants.MAXIMUM).getAsString();
                            maxLength = EloquaConstants.MAX_CHARS.concat(maximum + "\",\n");
                        }
                        if(condition.contains(EloquaConstants.MINIMUM)){
                            final String minimum = jsonObjectOfValidation.get(EloquaConstants.CONDITION).
                                    getAsJsonObject().get(EloquaConstants.MINIMUM).getAsString();
                            if(!minimum.equals("0")) {
                                minLength = EloquaConstants.MIN_LENGTH.concat(minimum + "\",\n");
                            }
                        }
                    }
                }
            }
            if(name.equals(EloquaConstants.SUBMIT)){
                dataType =EloquaConstants.OBJECT_TYPE;
                name = EloquaConstants.SUBMIT_.concat(eloquaCode);
                format =EloquaConstants.FRAGREF.concat(EloquaConstants.SUBMIT_FORM_FRAGMENT_PATH+"\"\n");
            }
            if(null != maxLength){
                afProperties += maxLength;
            }
            if(null != minLength){
                afProperties += minLength;
            }
            if(null == format)  {
                afProperties += EloquaConstants.JCR_TITLE.concat(title + "\"\n");
            }else {
                afProperties += EloquaConstants.JCR_TITLE.concat( title + "\",\n").concat(format);
            }
            fieldProperties = "\"" + name + "\": {\n" +
                    "                    "+dataType+",\n"+optionList+
                    "                     \"aem:afProperties\": {\n" +afProperties+
                    "                    }\n" +
                    "                },";

            if(null != format && format.contains(EloquaConstants.SUBMIT_FORM_FRAGMENT_PATH)){
                fieldProperties = EloquaConstants.CONCAT_RECAPTCHA_AND_DISCLAIMER.concat(fieldProperties);
            }
        }
        if(null != requiredField ){
            requiredField = EloquaConstants.HASH_CONSTANT+requiredField;
            fieldProperties = fieldProperties.concat(requiredField);
        }
        LOGGER.debug("End getFormFields method ::: Custom form field:::: "+fieldProperties);
        return fieldProperties;
    }

    private String getAemFormJsonSchema(final String eloquaCode, final String elqSiteId, final String elqFormName,
                                        final String elqCampaignId, final String finalFieldProperties, final String requiredField, String formActionUrl) {
        LOGGER.debug("Start getAemFormJsonSchema method ::: Stored form path in DAM:::: ");
        String aemFormJsonSchema = null;
        try (ResourceResolver resourceResolver = adminService.getReadService()) {
            //Get file form DAM to construct the JSON
            Resource resource = resourceResolver.getResource(EloquaConstants.ELOQUA_CUSTM_JSON_DAM_PATH.
                    concat(EloquaConstants.JCRCONTENT_RENDITIONS_ORIGINAL_JCRCONTENT));
            if(null != resource){
            ValueMap valMap = resource.getValueMap();
            if (valMap.containsKey(EloquaConstants.JCR_DATA)) {
                JsonElement element = new JsonParser().parse(
                        new InputStreamReader((InputStream) valMap.get(EloquaConstants.JCR_DATA)));
                final JsonObject asJsonObject = element.getAsJsonObject();
                aemFormJsonSchema = asJsonObject.toString();
                aemFormJsonSchema = aemFormJsonSchema.replace(EloquaConstants.ELOQUA_CODE_WITH_COAT, "\"" + eloquaCode + "\"");
                aemFormJsonSchema = aemFormJsonSchema.replace(EloquaConstants.ELOQUA_CODE,eloquaCode);
                aemFormJsonSchema = aemFormJsonSchema.replace(EloquaConstants.ELQSITEID_VAL, "\"" + elqSiteId + "\"");
                aemFormJsonSchema = aemFormJsonSchema.replace(EloquaConstants.ELQFORMNAME_VAL, "\"" + elqFormName + "\"");
                aemFormJsonSchema = aemFormJsonSchema.replace(EloquaConstants.ELQCAMPAIGNID_VAL, "\"" + elqCampaignId + "\"");
                aemFormJsonSchema = aemFormJsonSchema.replace(EloquaConstants.FORMACTIONURL_VAL, "\"" + formActionUrl+ "\"");
                aemFormJsonSchema = aemFormJsonSchema.replace(EloquaConstants.FINAL_FIELD_PROPERTIES_BRACKETS, finalFieldProperties);
                if (requiredField != StringUtils.EMPTY) {
                    aemFormJsonSchema = aemFormJsonSchema.replace(EloquaConstants.REQUIRED_FIELD_BRACKETS, requiredField);
                }
              }
            }
        } catch (Exception e) {
           LOGGER.error("Exception in getAemFormJsonSchema "+e.getMessage());
        }
        LOGGER.debug("END getAemFormJsonSchema method ::: Final Json Data:::: "+aemFormJsonSchema);
        return aemFormJsonSchema;
    }
    //Call this method to return the Jcr property of field which need to add in custom json
    private String getAfProperties(final JsonObject jsonObject) {
        LOGGER.debug("Start getAfProperties method ");
        String afProperties = StringUtils.EMPTY;
        try {
            if (jsonObject.has(EloquaConstants.DISPLAY_TYPE) && jsonObject.get(EloquaConstants.DISPLAY_TYPE).getAsString().equals(EloquaConstants.CHECKBOX)) {
                afProperties = EloquaConstants.CHECK_BOX_CLASS.concat(EloquaConstants.CHECK_BOX_RESOURCE_TYPE);
            }
            if (jsonObject.has(EloquaConstants.DISPLAY_TYPE) && jsonObject.get(EloquaConstants.DISPLAY_TYPE).getAsString().equals(EloquaConstants.RADIO)) {
                afProperties = EloquaConstants.RADIO_BUTTON_CLASS.concat(EloquaConstants.RADIO_BUTTON_RESOURCE_TYPE);
            }
            if (jsonObject.has(EloquaConstants.DISPLAY_TYPE) && jsonObject.get(EloquaConstants.DISPLAY_TYPE).getAsString().equals(EloquaConstants.HIDDEN)) {
                afProperties =EloquaConstants.HIDDEN_FIELD_CSS;
            }
        }catch (Exception e){
            LOGGER.error("Exception Exception in getAfProperties "+e.getMessage());
        }
        LOGGER.debug("End getAfProperties method ");
        return afProperties;
    }
    //Call this method to return the field type which need to add in custom json
    private String getFieldDataType(final String type, final String name) {
        LOGGER.debug("Start getFieldDataType method ");
        String dataType;
        if(type.equals(EloquaConstants.NUMERIC)){
            dataType = EloquaConstants.NUMERIC_DATA_TYPE;
        }else {
            if(name.toLowerCase().contains(EloquaConstants.EMAIL_ID)) {
                dataType = EloquaConstants.STRING_DATA_TYPE_EMAIL_FORMAT;
            }else {
                dataType =EloquaConstants.STRING_DATA_TYPE;
            }
        }
        LOGGER.debug("End getFieldDataType method ");
        return dataType;
    }
}
