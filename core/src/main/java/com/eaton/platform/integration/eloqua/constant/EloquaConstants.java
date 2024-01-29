package com.eaton.platform.integration.eloqua.constant;

public final class EloquaConstants {
    /*** Eloqua Constant ***/
    public static final String LCRESOURCE = "lcResource";
    public static final String TYPE = "type";
    public static final String DISPLAY_NAME ="displayName";
    public static final String ELEMENTS = "elements";
    public static final String JSON_ASSET_PATH = "/content/dam/formsanddocuments/eaton/";
    public static final String FORM = "form";
    public static final String OPTION_LIST = "optionList";
    public static final String APPLICATION_SCHEMA_JSON = "application/schema+json";
    public static final String JCR_CONTENT = "/jcr:content";
    public static final String ELOQUA_FORM_RESOURCE_TYPE = "eaton/components/content/eloqua-form";
    public static final String SCHEMA_JSON =".schema.json";
    public static final String NAME = "name";
    public static final String HTML_NAME = "htmlName";
    public static final String DATA_TYPE = "dataType";
    public static final String OPTION_LIST_ID = "optionListId";
    public static final String VALIDATIONS = "validations";
    public static final String CONDITION = "condition";
    public static final String DISPLAY_TYPE = "displayType";
    public static final String HTML = "html";
    public static final String NUMERIC = "numeric";
    public static final String EMAIL_ID = "email";
    public static final String PAGE_URL = "pageUrl";
    public static final String NEVER = "never";
    public static final String EXECUTE = "execute";
    public static final String CONSTANT_VALUE = "constantValue";
    public static final String PROCESSING_STEPS = "processingSteps";
    public static final String CHECKBOX = "checkbox";
    public static final String RADIO = "radio";
    public static final String SUBMIT = "submit";
    public static final String COUNTRY = "Country";
    public static final String OPTION_NAME = "OptionName";
    public static final String SUBMIT_FORM_FRAGMENT_PATH = "/content/dam/formsanddocuments/eaton/formfragment";
    public static final String RECAPTCHA_FORM_FRAGMENT_PATH = "/content/dam/formsanddocuments/eaton/recaptcha";
    public static final String DISCLAIMER_FORM_FRAGMENT_PATH = "/content/dam/formsanddocuments/eaton/disclaimerfragment";
    public static final String SHEMA_JSON_FOLDER = "eloqua-json-schema/";
    public static final String HIDDEN = "hidden";
    public static final String PROGRESSIVE_PROFILE = "ProgressiveProfile";
    public static final String FIELDS = "fields";
    public static final String HASH_CONSTANT ="###";
    public static final String MAXIMUM = "maximum";
    public static final String MINIMUM ="minimum";
    public static final String AUTHORIZATION = "Authorization";
    /** The Constant CONST_BACKWARD_SLASH. */
    public static final String CONST_BACKWARD_SLASH = "\\";
    /** The Constant CONST_COLON. */
    public static final String CONST_COLON = ":";
    /** The Constant CONST_BASIC. */
    public static final String CONST_BASIC = "Basic ";
    public static final String DEPTH_COMPLETE ="?depth=complete";
    public static final String JCR_DATA = "jcr:data";
    public static final String FINAL_FIELD_PROPERTIES = "finalFieldProperties";
    public static final String REQUIRED_FIELD = "requiredField";
    public static final String JCRCONTENT_RENDITIONS_ORIGINAL_JCRCONTENT ="/jcr:content/renditions/original/jcr:content";
    public static final String ELOQUA_CUSTM_JSON_DAM_PATH ="/content/dam/eaton/resources/EloquaCustmAEMForm.json";
    public static final String COUNTRY_STATE_JSON_DAM_PATH ="/content/dam/eaton/resources/Country_state_Json_for_Eloqua_form.json";

    public static final String ELQ_SITE_ID ="input[name=elqSiteId]";
    public static final String ELQ_FORM_NAME = "input[name=elqFormName]";
    public static final String ELQ_CAMPAIGN_ID ="input[name=elqCampaignId]";
    public static final String ACTION = "action";

    public static final String SUBMISSION_RESPONSE_REDIRECT_REGEX = "document\\.location\\.href = '(.*?)'";

    public static final String IS_REQUIRED_CONDITION= "{\"type\":\"IsRequiredCondition\"}";
    public static final String IS_EMAIL_ADDRESS_CONDITION = "{\"type\":\"IsEmailAddressCondition\"}";
    public static final String EMAIL_FORMAT = "                     \"format\": \"email\"\n";
    public static final String OBJECT_TYPE ="\"type\": \"object\"";
    public static final String SUBMIT_="submit_";
    public static final String JCR_TITLE="                     \"jcr:title\": \"";
    public static final String FRAGREF ="                     \"fragRef\": \"";
    public static final String MAX_CHARS= "                    \"maxChars\": \"";
    public static final String MIN_LENGTH = "                    \"minLength\": \"";
    public static final String ELOQUA_CODE_WITH_COAT ="\"eloquaCode\"";
    public static final String ELOQUA_CODE="eloquaCode";
    public static final String ELQSITEID_VAL ="\"elqSiteId_val\"";
    public static final String ELQFORMNAME_VAL = "\"elqFormName_val\"";
    public static final String ELQCAMPAIGNID_VAL = "\"elqCampaignId_val\"";
    public static final String FORMACTIONURL_VAL = "\"formActionUrl_val\"";
    public static final String FORM_ACTION_URL_CONTENT_ROOT_REPLACE_REGEX = "\\/content\\/.*?\\/";
    public static final String FINAL_FIELD_PROPERTIES_BRACKETS = "\"finalFieldProperties\":{}";
    public static final String REQUIRED_FIELD_BRACKETS = "\"requiredField\":{}";
    public static final String CHECK_BOX_CLASS="                     \"guideNodeClass\" : \"guideCheckBox\",\n";
    public static final String CHECK_BOX_RESOURCE_TYPE="                     \"sling:resourceType\": \"fd/af/components/guidecheckbox\",\n";
    public static final String RADIO_BUTTON_CLASS = "                     \"guideNodeClass\" : \"guideRadioButton\",\n";
    public static final String RADIO_BUTTON_RESOURCE_TYPE="                     \"sling:resourceType\": \"fd/af/components/guideradiobutton\",\n";
    public static final String HIDDEN_FIELD_CSS= "                     \"css\" : \"hidden\",\n";
    public static final String NUMERIC_DATA_TYPE = "\"type\": \"number\"";
    public static final String STRING_DATA_TYPE_EMAIL_FORMAT="\"type\": \"string\",                     \n \"format\": \"email\"";
    public static final String STRING_DATA_TYPE = "\"type\": \"string\"";
    public static final String CONCAT_RECAPTCHA_AND_DISCLAIMER = "\"recaptcha\": {\n" +
            "                            \"type\": \"object\",\n"+
            "                            \"aem:afProperties\": {\n"+
            "                        \"fragRef\": \""+RECAPTCHA_FORM_FRAGMENT_PATH+"\"\n"+
            "                        }\n"+
            "                    },"+
            "\"disclaimer\": {\n" +
            "                            \"type\": \"object\",\n"+
            "                            \"aem:afProperties\": {\n"+
            "                        \"fragRef\": \""+DISCLAIMER_FORM_FRAGMENT_PATH+"\"\n"+
            "                    }\n" +
            "                },";
}
