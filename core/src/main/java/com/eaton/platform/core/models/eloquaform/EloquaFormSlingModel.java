package com.eaton.platform.core.models.eloquaform;

import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.webservicesupport.ConfigurationManagerFactory;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.DisclaimerModel;
import com.eaton.platform.core.models.EloquaCloudConfigModel;
import com.eaton.platform.core.models.EloquaFormModel;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.EloquaFormConfigService;
import com.eaton.platform.core.services.EloquaFormOverlayService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.eloqua.constant.EloquaConstants;
import com.eaton.platform.integration.eloqua.services.EloquaService;
import com.eaton.platform.integration.eloqua.servlets.EloquaSubmitProxyServlet;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.settings.SlingSettingsService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

@Model(adaptables = {
        Resource.class,
        SlingHttpServletRequest.class
}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class EloquaFormSlingModel {
    private static final Logger LOGGER = LoggerFactory.getLogger(EloquaFormSlingModel.class);
    private static final String TAG_SKU_VALUE_PLACEHOLDER = "name=\"tag_sku\" value=\"\"";
    private static final String PAGE_TITLE_PLACEHOLDER = "name=\"tag_pagetitle\" value=\"";
    private static final String CONDITION_FIELD = "conditionField";
    private static final String CONDITION_TEXT = "conditionText";
    private static final String CONDITION_TYPE = "conditionType";
    private static final String ACTIONS = "actions";
    private static final String FORM_RULE_TARGET_FIELD = "formRuleTargetField";
    private static final String FORM_RULE_ACTION_TYPE = "formRuleActionType";
    private static final String JCR_CONTENT_FORM_ROUTING_TAG="jcr:content/form_routing_tag";
    private static final String FORM_ROUTING_TAG="form_routing_tag";
    private static final String PRODUCT_PAGE_PATH_PARAM_NAME = "productPagePath";
       
    /** The form HTML. */
    private String formHTML;

    /** The re captcha site key. */
    private String reCaptchaSiteKey;

    /** The eloqua site id. */
    private String eloquaSiteId;

    /** The lookup id visitor. */
    private String lookupIdVisitor;

    /** The lookup id primary. */
    private String lookupIdPrimary;

    /** The visitor unique field. */
    private String visitorUniqueField;

    /** The primary unique field. */
    private String primaryUniqueField;

    /** The eloqua component model. */
    @Self @Via(value = "resource")
    private EloquaFormModel eloquaCompModel;

    /** The tag page. */
    private String tag_page;

    /** The tag page. */
    private String tag_percolateID;

    /** The tag page. */
    private String tag_page_query_parameters;

    /** The cookie tracking URL. */
    private String cookieTrackingURL;

    /** The re CAPTCHA err msg. */
    private String reCAPTCHAErrMsg;

    /** The invalid form id err msg. */
    private String invalidFormIdErrMsg;
    
    /** The unavailable form id err msg. */
    private String unavailableFormIdErrMsg;

	/** The prepopulation enabled. */
    private boolean prepopulationEnabled;

    /** The disclaimer msg. */
    private String disclaimerMsg;
  
    private String invalidHTML;

    private String validHTML;

    /** The re CAPTCHA lang. */
    private String reCAPTCHALang;
    
    /** The re CAPTCHA lang. */
    private String reCAPTCHACountry;

    private boolean eloquaServerStatus;
    
    /** The Constant FORM_INPUT_SUBMIT_HTML_TAG. */
    private static final String FORM_INPUT_SUBMIT_HTML_TAG = "<input type=\"submit\"";

    /** The Constant FORM_INPUT_CAPITAL_SUBMIT_HTML_TAG. */
    private static final String FORM_INPUT_CAPITAL_SUBMIT_HTML_TAG = "<input type=\"Submit\"";

    /** The Constant FORM_INPUT_SUBMIT_IMAGE_HTML_TAG. */
    private static final String FORM_INPUT_SUBMIT_IMAGE_HTML_TAG = "<input alt=\"Submit\" type=\"image\"";

    /** The Constant GOOGLE_RECAPTCHA_HTML_TAG_PART_1. */
    private static final String GOOGLE_RECAPTCHA_HTML_TAG_PART_1 = "<div id=\"";

    private static final String GOOGLE_RECAPTCHA_HTML_TAG_PART_2 = "\" class=\"g-recaptcha\" data-sitekey=\"";

    /** The Constant GOOGLE_RECAPTCHA_HTML_TAG_PART_3. */
    private static final String GOOGLE_RECAPTCHA_HTML_TAG_PART_3 = "\" data-callback=\"recaptchaSuccess\" data-expired-callback=\"recaptchaPreventSubmission\" data-error-callback=\"recaptchaPreventSubmission\"></div>";

    /** The Constant GOOGLE_RECAPTCHA_HTML_TAG_PART_4. */
    private static final String GOOGLE_RECAPTCHA_HTML_TAG_PART_4 = "<div id=\"recaptcha-err-msg-form";

    /** The Constant GOOGLE_RECAPTCHA_HTML_TAG_PART_5. */
    private static final String GOOGLE_RECAPTCHA_HTML_TAG_PART_5 = "\" class=\"exception_message\" style=\"display: none\">";
    private static final String DEFAULT_DISCLAIMER_HTML_TAG_PART_2 = "\" class=\"sign_up_header\">" ;
    private static final String DEFAULT_DISCLAIMER_HTML_TAG_PART_3 = "<h4 class=\"elq-heading form-element-form-text\">" ;
    private static final String DEFAULT_DISCLAIMER_HTML_TAG_PART_4 = "</h4>" ;

    /** The Constant DIV_TAG. */
    private static final String DIV_TAG = "</div>";

    /** The Constant INVALID_FORM_ID_HTML_CONTENT_PART_1. */
    private static final String INVALID_FORM_ID_HTML_CONTENT_PART_1 = "<div id=\"invalid-form-id\" class=\"exception_message\">";

    /** The Constant INVALID_FORM_ID_HTML_CONTENT_PART_1. */
    private static final String UNAVAILABLE_FORM_ID_HTML_CONTENT_PART_1 = "<div id=\"Unavailable-form-id\" class=\"server_exception_message\">";

    /** The Constant RECAPTCHA_ERR_MSG_I18N_KEY. */
    private static final String RECAPTCHA_ERR_MSG_I18N_KEY = "recaptchaErrMsg";

    /** The Constant INVALID_FORM_ID_ERR_MSG_I18N_KEY. */
    private static final String INVALID_FORM_ID_ERR_MSG_I18N_KEY = "invalidEloquaFormIdErrMsg";
    
    /** The Constant UNAVAILABLE_FORM_ID_ERR_MSG_I18N_KEY. */
    private static final String UNAVAILABLE_FORM_ID_ERR_MSG_I18N_KEY = "unavailableEloquaFormIdErrMsg";

    private static final String CONST_QUESTION_MARK = "?";
    private static final String CONST_AMPERSAND = "&";

    private static final String TAG_OPTIN_SOURCE_STRING = "name=\"tag_Optin\"";

    private static final String TAG_OPTIN_FORM_CLASS = "tag_Optin_form";

    private static final String TAG_OPTIN_MULTIPLE_DISCLAIMER_CLASS = "disclaimer-checkbox multiple_checkbox";

    private static final String TAG_OPTIN_TARGET_STRING = "name=\"tag_Optin\" class=\"tag_Optin_form";
    
    private static final String TRUE = "true";
    
    private static final String HTML = "html";
    
    /** The Constant DEFAULT_DISCLAIMER_CHECKBOX_HTML_TAG_PART_1,2,3,4,5. */
    private static final String DEFAULT_DISCLAIMER_CHECKBOX_HTML_TAG_PART_1 = "<div id=\"";
    private static final String DEFAULT_DISCLAIMER_CHECKBOX_HTML_TAG_PART_2 = "\" class=\"disclaimer-msg-policyurl\">";
    private static final String DEFAULT_DISCLAIMER_CHECKBOX_HTML_TAG_PART_3 = "<a href=";
    private static final String DEFAULT_DISCLAIMER_CHECKBOX_HTML_TAG_PART_4 = ".html target=\"_blank\">";
    private static final String DEFAULT_DISCLAIMER_CHECKBOX_HTML_TAG_PART_5 = "</a>";
    
    /** The form name. */
    private String formName;

    /** if the form is configured in SkuPage template extract skuId from url */
    private String skuId;

    @OSGiService
    private EloquaFormOverlayService eloquaFormOverlayService;

    @OSGiService
    private EloquaFormConfigService eloquaFormConfigService;

    @OSGiService
    private ConfigurationManagerFactory configManagerFctry;

    @OSGiService
    private AdminService adminService;

    @OSGiService
    private EloquaService eloquaService;

    @OSGiService
    private SlingSettingsService slingSettings;

    @Inject
    private SlingHttpServletRequest slingHttpServletRequest;

    @Inject
    private Page currentPage;

    @Inject
    @ChildResource
    @Via("resource")
    @org.apache.sling.models.annotations.Optional
    private List<RulesEditorModel> formRulesList;

    private String rulesConfigJSON;

    @Inject
    private PageManager pageManager;

    @Inject
    private Resource resource;

    @Inject
    @Source("sling-object")
    private ResourceResolver resourceResolver;

    @PostConstruct
    private void init() {
        try (ResourceResolver adminResourceResolver = adminService.getReadService()){
            Optional < EloquaCloudConfigModel > eloquaCloudConfigModelOptional = Optional.empty();
            rulesConfigJSON = buildFormRules(formRulesList);

            setProductName(adminResourceResolver);

            final Locale languageValue = currentPage.getLanguage(false);
            
            if (languageValue != null && ((languageValue.getLanguage() != null) && (!languageValue.getLanguage().isEmpty()))) {
                reCAPTCHALang = languageValue.getLanguage();
                reCAPTCHACountry=languageValue.getCountry();                
            } else {
                reCAPTCHALang = CommonConstants.EN_LANG;
            }
            // populate current page name
            populateCurrentPageName();

            // populate Percolate Content ID
            populatePercolateId();

            // get eloqua cloud configuration object
            final String currentPagePath = currentPage.getPath();
            if (StringUtils.isNotEmpty(currentPagePath) && eloquaFormConfigService != null && adminResourceResolver != null) {
                eloquaCloudConfigModelOptional = Optional.ofNullable(eloquaFormConfigService
                        .setUpEloquaServiceParameters(configManagerFctry, currentPagePath, adminResourceResolver));
            }

            final String prepopulateOption = eloquaCompModel.getPrepopulate();
            if (StringUtils.isNotBlank(prepopulateOption) && TRUE.equalsIgnoreCase(prepopulateOption)) {
                prepopulationEnabled = true;
            } else {
                prepopulationEnabled = false;
            }

            // get details from eloqua cloud configuration
            if (eloquaCloudConfigModelOptional.isPresent()) {
                initializeEloquaConfig(eloquaCloudConfigModelOptional.get());
            }

            String response = null;
            // invoke getEloquaForm() method from eloquaService
            if (null != eloquaService && eloquaCloudConfigModelOptional.isPresent()) {
                // SonarQube Null Pointer Issue fix
                response = eloquaService.getEloquaForm(eloquaCloudConfigModelOptional.get(), eloquaCompModel.getFormId());
            }

			if (!eloquaServerStatus) {
				LOGGER.debug("eloqua server running - ", eloquaServerStatus);
				validHTML = TRUE;
				if (null != unavailableFormIdErrMsg) {
					formHTML = UNAVAILABLE_FORM_ID_HTML_CONTENT_PART_1.concat(unavailableFormIdErrMsg).concat(DIV_TAG); // SonarQube
				}
			} else {
            if (StringUtils.isNotBlank(response)) {
                JSONParser parser = new JSONParser();
                JSONObject eloquoFormJsonObject = (JSONObject) parser.parse(response);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("eloquoFormJsonObject - ", eloquoFormJsonObject.toJSONString());
                }
                constructHTML(eloquoFormJsonObject);
                validHTML = TRUE;
            } else {
                invalidHTML = TRUE;
                if (null != invalidFormIdErrMsg) {
                    formHTML = INVALID_FORM_ID_HTML_CONTENT_PART_1.concat(invalidFormIdErrMsg).concat(DIV_TAG); // SonarQube
                }
            }
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("formHTML - ", formHTML);
                }
        } catch (Exception exception) {
            invalidHTML = TRUE;
            LOGGER.error("Exception occured while getting the Form HTML from Eloqua : ", exception);
            if (null != invalidFormIdErrMsg) {
                formHTML = INVALID_FORM_ID_HTML_CONTENT_PART_1.concat(invalidFormIdErrMsg).concat(DIV_TAG);
            }
        }
    }

    private static final String buildFormRules(final List<RulesEditorModel> formRulesList) {
        String rulesConfigJSON = StringUtils.EMPTY;
        if (null != formRulesList ) {
            final JsonArray rulesArray = new JsonArray();
            formRulesList.forEach(formRule -> {
                if (null != formRule && formRule.isValid()) {
                    final JsonObject ruleEditorJSON = new JsonObject();
                    ruleEditorJSON.add(CONDITION_FIELD, new Gson().toJsonTree(formRule.getConditionField()));
                    ruleEditorJSON.add(CONDITION_TEXT, new Gson().toJsonTree(formRule.getConditionText()));
                    ruleEditorJSON.add(CONDITION_TYPE, new Gson().toJsonTree(formRule.getConditionType()));
                    final List<ActionConfigModel> actionConfig = formRule.getActionConfig();
                    if (actionConfig != null && !actionConfig.isEmpty()) {
                        final JsonArray actionConfigArray = getActionConfig(actionConfig);
                        ruleEditorJSON.add(ACTIONS, new Gson().toJsonTree(actionConfigArray));
                    }
                    rulesArray.add(ruleEditorJSON);
                }
            });
            rulesConfigJSON = rulesArray.toString();
        }
        return rulesConfigJSON;
    }

    private static final JsonArray getActionConfig(final List<ActionConfigModel> actionConfigList) {
        final JsonArray actionArrayConfigs = new JsonArray();
        actionConfigList.forEach(actionConfig -> {
            try {
                if (null != actionConfig && actionConfig.isValid()) {
                	final JsonObject actionConfigNode = new JsonObject();
                    final String targetField = actionConfig.getTargetField();
                    final String formRuleActionType = actionConfig.getActionType();
                    actionConfigNode.add(FORM_RULE_TARGET_FIELD, new Gson().toJsonTree(targetField));
                    actionConfigNode.add(FORM_RULE_ACTION_TYPE, new Gson().toJsonTree(formRuleActionType));
                    actionArrayConfigs.add(actionConfigNode);
                }
            } catch (Exception e) {
                LOGGER.info("Exception while constructing Json for action types", e);
            }
        });
        return actionArrayConfigs;
    }

    /**
     * Set the tag_product field value by getting the product name or pdh product name from the product family's pim
     * @param adminResourceResolver - resource resolver used to get pim resource
     */
    private void setProductName(ResourceResolver adminResourceResolver) {
        LOGGER.trace("setProductName : START");

        String productPagePath = getProductPagePath();
        Page productFamilyPage = pageManager.getContainingPage(productPagePath);

        if (null != productFamilyPage)  {
            ValueMap pageProperties = productFamilyPage.getProperties();
            String pimPath = pageProperties.get(CommonConstants.PAGE_PIM_PATH, StringUtils.EMPTY);
            LOGGER.debug("Pim node path: {}", pimPath);
            if (StringUtils.isNotBlank(pimPath)) {
                Resource pimResource = adminResourceResolver.getResource(pimPath);
                if (null != pimResource){
                    final String productName = pimResource.getValueMap().get(CommonConstants.PIM_PRODUCT_NAME, StringUtils.EMPTY);
                    LOGGER.debug("product name: {}", productName);
                    final String pdhProductName = pimResource.getValueMap().get(CommonConstants.PDH_PRODUCT_NAME, StringUtils.EMPTY);
                    LOGGER.debug("pdh product name: {}", pdhProductName);
                    eloquaCompModel.setTag_product(StringUtils.isNotBlank(productName) ? productName : pdhProductName);
                }
            }
        }

        LOGGER.trace("setProductName : END");
    }

    /**
     * Return the product sku id if page path from productPagePath url parameter is sku page
     * @return sku id
     */
    public Optional<String> getSkuId() {
        LOGGER.trace("getSkuId : START");

        String productPagePath = getProductPagePath();

        if (StringUtils.isNotBlank(productPagePath)) {

            if (StringUtils.contains(productPagePath, CommonConstants.SKU_PAGE)) {
                String regex = StringUtils.join(CommonConstants.SKU_PAGE, "\\.(.*?)(\\.|$)");
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(productPagePath);
                if (matcher.find()) {
                    String productSkuId = matcher.group(1);
                    LOGGER.debug("product sku id set to {}", productSkuId);
                    LOGGER.trace("getSkuId : END");
                    return Optional.of(productSkuId);
                } else {
                    LOGGER.error("product sku id not found in {}", productPagePath);
                }

            } else {
                LOGGER.debug("{} not a sku page.", productPagePath);
            }

        }

        LOGGER.trace("getSkuId : END");
        return Optional.empty();
    }

    /**
     * Get the product page path from the productPagePath request parameter
     * @return product page path
     */
    private String getProductPagePath() {
        LOGGER.trace("productPagePath : START");

        String productPagePath = String.valueOf(slingHttpServletRequest.getRequestParameter(PRODUCT_PAGE_PATH_PARAM_NAME));
        if (!StringUtils.startsWith(productPagePath, CommonConstants.CONTENT_ROOT_FOLDER)) {
            productPagePath = StringUtils.join(CommonConstants.CONTENT_ROOT_FOLDER, productPagePath);
        }

        LOGGER.debug("Product page path: {}", productPagePath);

        LOGGER.trace("productPagePath : END");
        return productPagePath;
    }

    /**
     * construct the form's html
     * 
     * EAT-8168 - Form action changed 
     * 
     * @param eloquoFormJsonObject
     */
    
    private void constructHTML(JSONObject eloquoFormJsonObject) {
        
        LOGGER.debug("constructHTML : START");
        // get eloqua form html
        formHTML = eloquoFormJsonObject.get(HTML).toString();
        final String customCSS = (String) eloquoFormJsonObject.getOrDefault("customCSS",StringUtils.EMPTY);

        Document doc = Jsoup.parse(formHTML);
        Elements elements = doc.select("form");
        formName = elements.attr(CommonConstants.PROPERTY_NAME);
        LOGGER.debug("form name: {}", formName);

        String originalFormActionUrl = elements.attr("action");
        modifyFormActionUrl(originalFormActionUrl);
        
        LOGGER.debug(" Before : ", formHTML);
        formHTML = formHTML.replaceAll("value=\"<eloqua type=(.*?) syntax=(.*?) />\"", "");
        formHTML = formHTML.replaceAll("data-previous=\"<eloqua type=(.*?) fieldId=(.*?) />\"", "data-previous=\"\"");
        formHTML = formHTML.replaceAll("data-previous=\"~~(.*?)~~\"", "data-previous=\"\"");
        formHTML = formHTML.replaceAll("data-value=\"~~(.*?)~~\"", "data-value=\"\"");
        formHTML = formHTML.replaceAll("value=\"~~(.*?)~~\"", "");
        LOGGER.debug(" After : ", formHTML);

        String extraHTML = StringUtils.EMPTY;

        //Google captcha processing method
        extraHTML = googleCaptchaProcessor(extraHTML);
        //Standard Disclaimer Header added
 	
        extraHTML = extractDisclaimerHTML(extraHTML);
        extraHTML = extraHTML.concat(FORM_INPUT_SUBMIT_HTML_TAG);
        insertCustomHTML(extraHTML);
        if (StringUtils.isNotEmpty(customCSS)) {
        	formHTML = formHTML.concat(customCSS);
        }

        LOGGER.debug("constructHTML : END");
    }

    /**
     * Modify the form action url in the form's html based on whether proxy is on or off
     * 
     * EAT-8168 - Form action changed 
     * 
     *  @param originalFormActionUrl
     */
    private void modifyFormActionUrl(String originalFormActionUrl) {
        LOGGER.debug("modifyFormActionUrl : START");
        
        LOGGER.debug("original form action url: {}", originalFormActionUrl);
        if (StringUtils.isEmpty(originalFormActionUrl) || eloquaService.doProxy()) {
            String proxyFormActionUrl= getProxyFormActionUrl();
            if (StringUtils.isNotEmpty(proxyFormActionUrl)) {
                formHTML = formHTML.replace(originalFormActionUrl, proxyFormActionUrl);
                LOGGER.debug("form action url replaced in html from original url ({}) to proxy url ({})", originalFormActionUrl, proxyFormActionUrl);
            } else {
                LOGGER.debug("issue getting proxy url. form action url not replaced in html. original url used ({})", originalFormActionUrl);
            }
        } else {
            LOGGER.debug("form action url not replaced in html. original url used ({})", originalFormActionUrl);
        }
        
        LOGGER.debug("modifyFormActionUrl : END");
    }

    /**
     * Get the form action url used when proxy is on
     * 
     * EAT-8168 - Form action changed 
     * 
     * @return servletProxyUrl - form action url used when proxy is on
     */
    private String getProxyFormActionUrl() {
        LOGGER.debug("getProxyFormActionUrl : START");

        String servletProxyUrl = StringUtils.EMPTY;

        String eloquaResourcePath = resource.getPath();
        LOGGER.debug("eloqua form resource path: {}", eloquaResourcePath);
        
        if (StringUtils.isEmpty(eloquaResourcePath)) {
            LOGGER.error("unable to get eloqua form resource path");
        } else {

            if ( !slingSettings.getRunModes().contains(CommonConstants.AUTHOR)
                    && eloquaResourcePath.startsWith(CommonConstants.STARTS_WITH_CONTENT_WITH_SLASH) ) {
                
                LOGGER.debug("eloqua form not in author and resource path contains a content root that needs to be removed. Removing the content root from resource path ({}).", eloquaResourcePath);
                eloquaResourcePath = StringUtils.replaceFirst(eloquaResourcePath, EloquaConstants.FORM_ACTION_URL_CONTENT_ROOT_REPLACE_REGEX, CommonConstants.SLASH_STRING);
                LOGGER.debug("eloqua form resource path after content root removal: {}", eloquaResourcePath);
            }

            servletProxyUrl = eloquaResourcePath + CommonConstants.PERIOD + EloquaSubmitProxyServlet.SERVLET_SELECTOR + CommonConstants.HTML_EXTN;
            LOGGER.debug("servlet proxy url (before url parameters added): {}", servletProxyUrl);

            final String redirectUrl = eloquaCompModel.getRedirectUrl();
            if (null == redirectUrl) {
                LOGGER.debug("No redirect url found in eloqua form component.");
            } else {
                LOGGER.debug("Redirect url found in eloqua form component ({}). Appending to servlet proxy url.", redirectUrl);
                
                String encodedRedirectUrl = StringUtils.EMPTY;
                
                try {
                    encodedRedirectUrl = URLEncoder.encode(redirectUrl, CommonConstants.UTF_8);
                } catch (UnsupportedEncodingException e) {
                    LOGGER.error("Exception while encoding the redirect url ", e);
                }
                
                if (StringUtils.isNotEmpty(encodedRedirectUrl)) {
                    servletProxyUrl = StringUtils.join(servletProxyUrl, 
                            CommonConstants.QUESTION_MARK_CHAR, 
                            EloquaSubmitProxyServlet.PARAM_REDIRECT_URL, 
                            CommonConstants.EQUALS_SYMBOL, 
                            encodedRedirectUrl);
                }
            }

        }

        LOGGER.debug("final eloqua proxy servlet url: {}", servletProxyUrl);
        
        LOGGER.debug("getProxyFormActionUrl : END");
        
        return servletProxyUrl;
    }

    private String privacyPolicyPageLink(String extraHTML) {
    	try(ResourceResolver resourceResolver = adminService.getReadService()) {
    		extraHTML = formPrivacyPageHTML(extraHTML, pageManager);
    	}catch(Exception resEx)
    	{
    		LOGGER.error("privacyPolicyPageLink : Error in getting adminService.getReadService");
    	}

    	return extraHTML;
    }

    private String formPrivacyPageHTML(String extraHTML, final PageManager eloquaPageManager) {
    	if (null != eloquaPageManager && null != eloquaCompModel.getPrivacypageurl()) {
    		final Page policyPage = eloquaPageManager.getPage(eloquaCompModel.getPrivacypageurl());
    		if (null != policyPage){
    			String pagtitle=policyPage.getNavigationTitle()!=null? policyPage.getNavigationTitle(): policyPage.getPageTitle()!=null ? policyPage.getPageTitle():policyPage.getTitle();
    			if(pagtitle!=null && !pagtitle.isEmpty())
    			{
    				extraHTML = extraHTML.concat(DEFAULT_DISCLAIMER_CHECKBOX_HTML_TAG_PART_1)
    						.concat(eloquaCompModel.getFormId())
    						.concat(DEFAULT_DISCLAIMER_CHECKBOX_HTML_TAG_PART_2)
    						.concat(DEFAULT_DISCLAIMER_CHECKBOX_HTML_TAG_PART_3+eloquaCompModel.getPrivacypageurl()+DEFAULT_DISCLAIMER_CHECKBOX_HTML_TAG_PART_4+pagtitle+DEFAULT_DISCLAIMER_CHECKBOX_HTML_TAG_PART_5)
    						.concat(DIV_TAG);
    			}
    		}
    	}
    	return extraHTML;
    }

	private String disclaimerHeaderAdd(String extraHTML) {    	
             extraHTML = extraHTML.concat(DEFAULT_DISCLAIMER_CHECKBOX_HTML_TAG_PART_1)
                        .concat(eloquaCompModel.getFormId())
                        .concat(DEFAULT_DISCLAIMER_HTML_TAG_PART_2)
                        .concat(DEFAULT_DISCLAIMER_HTML_TAG_PART_3+CommonUtil.getI18NFromResourceBundle(slingHttpServletRequest, currentPage, CommonConstants.SIGN_UP_NO_ASTERISK_I18N_KEY,
                        		CommonConstants.SIGN_UP_NO_ASTERISK_I18N_DEFAULTVALUE)+DEFAULT_DISCLAIMER_HTML_TAG_PART_4)
                        .concat(DIV_TAG);
     		return extraHTML;
	}

	private void insertCustomHTML(String extraHTML) {
		String formInputSubmitHtmlTag = formHTML
				.contains(FORM_INPUT_SUBMIT_HTML_TAG) ? FORM_INPUT_SUBMIT_HTML_TAG
				: (formHTML.contains(FORM_INPUT_CAPITAL_SUBMIT_HTML_TAG) ? FORM_INPUT_CAPITAL_SUBMIT_HTML_TAG
						: StringUtils.EMPTY);
        if (StringUtils.isNotBlank(formInputSubmitHtmlTag)) {
            LOGGER.debug("Submit Button replaced with custom HTML ");
            formHTML = formHTML.replace(formInputSubmitHtmlTag, extraHTML);
        } else if (formHTML.indexOf(FORM_INPUT_SUBMIT_IMAGE_HTML_TAG) != -1) {
            LOGGER.debug("Submit Image replaced with custom HTML ");
            formHTML = formHTML.replace(FORM_INPUT_SUBMIT_IMAGE_HTML_TAG, extraHTML);
        }

        if (formHTML.indexOf(TAG_OPTIN_SOURCE_STRING) != -1) {
            LOGGER.debug("Tag Options Replace");
            formHTML = formHTML.replace(TAG_OPTIN_SOURCE_STRING, TAG_OPTIN_TARGET_STRING + eloquaCompModel.getFormId() + "\"");
        }

        final String tagOptinClass = "class=\"" + TAG_OPTIN_FORM_CLASS + eloquaCompModel.getFormId() + "\"";
        if (formHTML.indexOf(tagOptinClass) != -1) {
            LOGGER.debug("tag_Optin Class Addition");
            formHTML = formHTML.replace(tagOptinClass, "class=\"" + TAG_OPTIN_FORM_CLASS + eloquaCompModel.getFormId() + " " + TAG_OPTIN_MULTIPLE_DISCLAIMER_CLASS + "\"");
        }
        if (StringUtils.isNotEmpty(skuId) && formHTML.indexOf(TAG_SKU_VALUE_PLACEHOLDER) != -1) {
            LOGGER.debug("\n########  Eloqua form from SkuPage and SkuId=", skuId, " ##################\n");
            formHTML = formHTML.replace(TAG_SKU_VALUE_PLACEHOLDER, "name=\"tag_sku\" value=\"" + skuId + "\"");
        }
        if (currentPage != null && (StringUtils.isNotEmpty(currentPage.getTitle()) || StringUtils.isNotEmpty(currentPage.getPageTitle()))) {
            formHTML = formHTML.replace(PAGE_TITLE_PLACEHOLDER, PAGE_TITLE_PLACEHOLDER + (currentPage.getTitle() != null ? currentPage.getTitle() : currentPage.getPageTitle()));
        }
    }

	private String extractDisclaimerHTML(String extraHTML) {
		//Disclaimer Processing
		final Optional < Resource > disclaimerOptional = Optional.ofNullable(eloquaCompModel.getDisclaimerlist());
		String defaultDMsgPreapared= CommonUtil.getI18NFromResourceBundle(slingHttpServletRequest,currentPage,
				CommonConstants.DISCLAIMER_MESSAGE_I18N_KEY,disclaimerMsg);
		if(eloquaFormOverlayService !=null && StringUtils.equalsIgnoreCase(TRUE, eloquaCompModel.getDefaultmarketdisclaimer()))
		  {
				LOGGER.info("Entered DisclaimerMsg is {}", disclaimerMsg);
				if(defaultDMsgPreapared!=null && !defaultDMsgPreapared.isEmpty())
				{
					extraHTML=disclaimerHeaderAdd(extraHTML);
					extraHTML = eloquaFormOverlayService.generateDisclaimerDom(extraHTML,
							eloquaCompModel.getFormId(),defaultDMsgPreapared);
					extraHTML=privacyPolicyPageLink(extraHTML);
    		    }
			   
		    } else {
		    	if(disclaimerMsg!=null && StringUtils.isNotEmpty(disclaimerMsg))
				  {
		    			extraHTML=disclaimerHeaderAdd(extraHTML);
				    	extraHTML = eloquaFormOverlayService.generateDisclaimerDom(extraHTML,eloquaCompModel.getFormId(),disclaimerMsg);
				    	extraHTML=privacyPolicyPageLink(extraHTML);
				  }
		    	}
	
		if (disclaimerOptional.isPresent())
         {						
			final Resource disclaimerResource = disclaimerOptional.get();
			final Iterable < Resource > disclaimerResourceIterator = disclaimerResource.getChildren();
			final long count = StreamSupport.stream(disclaimerResourceIterator.spliterator(),
					Boolean.FALSE).count();
			if (count > 0) {
				if((StringUtils.isBlank(defaultDMsgPreapared)|| !StringUtils.equalsIgnoreCase(TRUE, eloquaCompModel.getDefaultmarketdisclaimer()))&& StringUtils.isBlank(disclaimerMsg))
				{
				    extraHTML=disclaimerHeaderAdd(extraHTML);
				}
				Document parsedHTML = Jsoup.parse(formHTML);
				if(null != eloquaFormOverlayService){
					final List < Element > hiddenFields = eloquaFormOverlayService
							.getFilteredElement("hidden", parsedHTML);
					extraHTML = processDisclaimer(hiddenFields, disclaimerResourceIterator, extraHTML);
					hiddenFields.stream().forEach(hiddenField -> {
						final String hiddenFieldID = hiddenField.attr("id");
						disclaimerResourceIterator.forEach(discResource -> {
							final Optional < DisclaimerModel > disclaimerModelOptional = Optional
									.ofNullable(discResource.adaptTo(DisclaimerModel.class));
							if (disclaimerModelOptional.isPresent() && eloquaFormOverlayService != null) {
								final DisclaimerModel disclaimerModel = disclaimerModelOptional.get();
								final String selectedField = disclaimerModel.getSelectedField();
								final String value = hiddenField.attr("name");
								if (StringUtils.isNotEmpty(selectedField) && selectedField.equals(value) && StringUtils.isNotEmpty(hiddenFieldID)) {
									parsedHTML.getElementById(hiddenFieldID).remove();
								}
							}
						});
					});
				}
				formHTML = parsedHTML.toString();
			}
		}
		return extraHTML;
	}

    private String googleCaptchaProcessor(String extraHTML) {
        if (StringUtils.equalsIgnoreCase(TRUE, eloquaCompModel.getAddCaptcha()) && null != reCaptchaSiteKey) {
            extraHTML = extraHTML.concat(GOOGLE_RECAPTCHA_HTML_TAG_PART_1)
                    .concat("g-recaptcha-form" + eloquaCompModel.getFormId())
                    .concat(GOOGLE_RECAPTCHA_HTML_TAG_PART_2)
                    .concat(reCaptchaSiteKey)
                    .concat(GOOGLE_RECAPTCHA_HTML_TAG_PART_3);
            if (reCAPTCHAErrMsg != null) {
                extraHTML = extraHTML.concat(GOOGLE_RECAPTCHA_HTML_TAG_PART_4)
                        .concat(GOOGLE_RECAPTCHA_HTML_TAG_PART_5)
                        .concat(reCAPTCHAErrMsg)
                        .concat(DIV_TAG);
            }
        }
        return extraHTML;
    }

    private void initializeEloquaConfig(final EloquaCloudConfigModel eloquaCloudConfigModel) {
        // set google reCAPTCHA field configured in eloqua cloud
        // configuration
        reCaptchaSiteKey = eloquaCloudConfigModel.getRecaptchasitekey();

        // set fields required for eloqua setup and pre-population
        eloquaSiteId = eloquaCloudConfigModel.getEloquaSiteId();
        lookupIdVisitor = eloquaCloudConfigModel.getLookupIdVisitor();
        lookupIdPrimary = eloquaCloudConfigModel.getLookupIdPrimary();
        visitorUniqueField = eloquaCloudConfigModel.getVisitorUniqueField();
        primaryUniqueField = eloquaCloudConfigModel.getPrimaryUniqueField();
        cookieTrackingURL = eloquaCloudConfigModel.getCookieTrackingUrl();
        // get error messages reCAPTCHA and invalid form id from cloud
        // configuration
        invalidFormIdErrMsg = CommonUtil.getI18NFromResourceBundle(slingHttpServletRequest, currentPage, INVALID_FORM_ID_ERR_MSG_I18N_KEY);
        reCAPTCHAErrMsg = CommonUtil.getI18NFromResourceBundle(slingHttpServletRequest, currentPage, RECAPTCHA_ERR_MSG_I18N_KEY);

        unavailableFormIdErrMsg =CommonUtil.getI18NFromResourceBundle(slingHttpServletRequest, currentPage, UNAVAILABLE_FORM_ID_ERR_MSG_I18N_KEY);
        
        
        // set Disclaimer Message
        disclaimerMsg = eloquaCompModel.getDisclaimer();
        eloquaServerStatus=eloquaCloudConfigModel.getEloquaServerStatus();
    }

    private void populateCurrentPageName() {
        if(eloquaCompModel != null) {
            if (eloquaCompModel.getPageReqd() != null && StringUtils.equalsIgnoreCase(TRUE, eloquaCompModel.getPageReqd())) {
                tag_page = CommonConstants.TRUE;
                final RequestParameterMap requestParameterMap = slingHttpServletRequest.getRequestParameterMap();
                final List<NameValuePair> queryParameter = new ArrayList<NameValuePair>();
                requestParameterMap.forEach((key, val) -> {
                    if (val.length > 0) {
                        BasicNameValuePair basicNameValuePair = new BasicNameValuePair(key, val[0].toString());
                        queryParameter.add(basicNameValuePair);
                    }
                });
                tag_page_query_parameters = queryParameter.toString().replaceAll("\\[|\\]|\\s+", "");

            } else {
                tag_page = CommonConstants.FALSE;
                LOGGER.debug("tag_page_query_parameters not populated because Page Value Required is not checked in Eloqua Form Component Configuration > Hidden Fields");
            }
        }
    }

    private void populatePercolateId() {
        if (null != currentPage) {
            ValueMap pageProperties = currentPage.getProperties();
            if (null != pageProperties){
                tag_percolateID = pageProperties.get(CommonConstants.PERCOLATE_ID, StringUtils.EMPTY);
                if (!StringUtils.isEmpty(tag_percolateID)) {
                    tag_percolateID = tag_percolateID.replace("-", CommonConstants.SPACE_STRING);
                    tag_percolateID = CommonConstants.PERCOLATE_POST_NAME + tag_percolateID.toLowerCase();
                }
            }
        }
    }

    private String processDisclaimer(final List < Element > hiddenElements, final Iterable < Resource > disclaimerResourceIterator,
                                     final String extraHTML) {

        StringBuilder extraHTMLBuilder = new StringBuilder();
        extraHTMLBuilder.append(extraHTML);
        disclaimerResourceIterator.forEach(disclaimerChildResource -> {
            final Optional < DisclaimerModel > disclaimerModelOptional = Optional.ofNullable(disclaimerChildResource
                    .adaptTo(DisclaimerModel.class));
            if (disclaimerModelOptional.isPresent() && eloquaFormOverlayService != null && !hiddenElements.isEmpty()) {
                final DisclaimerModel disclaimerModel = disclaimerModelOptional.get();
                final String selectedField = disclaimerModel.getSelectedField();
                hiddenElements.forEach(element -> {
                    final String value = element.attr("name");
                    if (StringUtils.isNotEmpty(selectedField) && selectedField.equals(value)) {
                        Element newInputElement = new Element(Tag.valueOf("input"), "");
                        Element div = new Element(Tag.valueOf("div"), "");
                        div.attr("id", "disclaimer-msg");
                        div.attr("class", "disclaimer-msg");
                        div.append(Jsoup.parse(disclaimerModel.getDisclaimersText()).outerHtml());
                        newInputElement.attr("name", value);
                        newInputElement.attr("value", "0");
                        newInputElement.attr("id", value.concat("_").concat(eloquaCompModel.getFormId()));
                        newInputElement.attr("type", "checkbox");
                        newInputElement.attr("class", TAG_OPTIN_MULTIPLE_DISCLAIMER_CLASS);
                        String outerHtml = newInputElement.outerHtml();
                        extraHTMLBuilder.append(outerHtml).append(div.outerHtml());
                    }
                });
            }
        });

        return extraHTMLBuilder.toString();
    }

    /**
     * @return the formHTML
     */
    public String getFormHTML() {
        return formHTML;
    }

    /**
     * @return the reCaptchaSiteKey
     */
    public String getReCaptchaSiteKey() {
        return reCaptchaSiteKey;
    }

    /**
     * @return the eloquaCompModel
     */
    public EloquaFormModel getEloquaCompModel() {
        return eloquaCompModel;
    }

    /**
     * @return the eloquaSiteId
     */
    public String getEloquaSiteId() {
        return eloquaSiteId;
    }

    /**
     * @return the lookupIdVisitor
     */
    public String getLookupIdVisitor() {
        return lookupIdVisitor;
    }

    /**
     * @return the lookupIdPrimary
     */
    public String getLookupIdPrimary() {
        return lookupIdPrimary;
    }

    /**
     * @return the visitorUniqueField
     */
    public String getVisitorUniqueField() {
        return visitorUniqueField;
    }

    /**
     * @return the primaryUniqueField
     */
    public String getPrimaryUniqueField() {
        return primaryUniqueField;
    }

    /**
     * @return the tag_page
     */
    public String getTag_page() {
        return tag_page;
    }

    /**
     * @return the cookieTrackingURL
     */
    public String getCookieTrackingURL() {
        return cookieTrackingURL;
    }

    /**
     * @return the reCAPTCHAErrMsg
     */
    public String getReCAPTCHAErrMsg() {
        return reCAPTCHAErrMsg;
    }

    /**
     * @return the invalidFormIdErrMsg
     */
    public String getInvalidFormIdErrMsg() {
        return invalidFormIdErrMsg;
    }

    /**
     * @return the prepopulationEnabled
     */
    public boolean isPrepopulationEnabled() {
        return prepopulationEnabled;
    }


    public String getInvalidHTML() {
        return invalidHTML;
    }

    public String getValidHTML() {
        return validHTML;
    }

    public String getTag_page_query_parameters() {
        return tag_page_query_parameters;
    }

    public String getTag_percolateID() {
        return tag_percolateID;
    }

    /**
     * @return the reCAPTCHALang
     */
    public String getReCAPTCHALang() {
        return reCAPTCHALang;
    }
    
    /**
     * @return the reCAPTCHALang
     */
    public String getReCAPTCHACountry() {
        return reCAPTCHACountry;
    }

    /**
     * @return the formName
     */
    public String getFormName() {
        return formName;
    }

    public final String getRulesConfigJSON() {
        return rulesConfigJSON;
    }

    /**
     * /
     * @return get formRoutingTag to set the hidden
     * field "tag_Routing" value for 3310 Eloqua form.
     * This tag values from page properties.
     */
    public String getFormRoutingTags() {
        String formRoutingTags = null;
        final String requestSuffix = slingHttpServletRequest.getRequestPathInfo().getSuffix();
        try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
            if (StringUtils.isNotEmpty(requestSuffix)) {
                String currentPageSuffix = CommonUtil.getSiteRootPrefixByPagePath(currentPage.getPath());
                if (StringUtils.isNotEmpty(currentPageSuffix) && currentPageSuffix.contains(CommonConstants.SLASH_STRING)) {
                    currentPageSuffix = currentPageSuffix.substring(0, currentPageSuffix.lastIndexOf(CommonConstants.SLASH_STRING));
                    final String productFamilyPage = currentPageSuffix.concat(requestSuffix);
                    Resource resolveProductFamilyPage = adminResourceResolver.resolve(productFamilyPage);
                    final Page familyPage = resolveProductFamilyPage.adaptTo(Page.class);
                    if (familyPage != null && familyPage.hasChild(JCR_CONTENT_FORM_ROUTING_TAG)
                                && familyPage.getContentResource(FORM_ROUTING_TAG).getValueMap().containsKey(CommonConstants.CQ_TAGS)) {
                    	formRoutingTags = getPropertiesFromPagePath(familyPage, adminResourceResolver);
                        } else {
                        formRoutingTags = getPropertiesFromPagePath(currentPage, adminResourceResolver);
                        }
                }
            } else {
                if (null != currentPage) {
                    formRoutingTags = getPropertiesFromPagePath(currentPage, adminResourceResolver);
                }
            }
        }
        return formRoutingTags;
    }

    private static String getPropertiesFromPagePath(final Page pagePath, ResourceResolver adminResourceResolver){
        String formRoutingTags = null;
        final TagManager tagManager = adminResourceResolver.adaptTo(TagManager.class);
        if(pagePath.hasChild(JCR_CONTENT_FORM_ROUTING_TAG)) {
            ValueMap valueMapFormRoutingTag = pagePath.getContentResource(FORM_ROUTING_TAG).getValueMap();
            if (valueMapFormRoutingTag.containsKey(CommonConstants.CQ_TAGS)) {
                final String[] formRoutingTagsArray = valueMapFormRoutingTag.get(CommonConstants.CQ_TAGS,String[].class);
                if (null != tagManager && null != formRoutingTagsArray && formRoutingTagsArray.length != 0) {
                    final List<String> tagIds = new ArrayList<>();
                    for (final String tags : formRoutingTagsArray) {
                        tagIds.add((tagManager.resolve(tags) == null) ? null : tagManager.resolve(tags).getTagID());
                    }
                    if (!tagIds.isEmpty()) {
                        formRoutingTags = String.join(CommonConstants.COMMA, tagIds);
                    }
                }
            }
        }
        return formRoutingTags;
    }
    
    public String getUnavailableFormIdErrMsg() {
		return unavailableFormIdErrMsg;
	}

}
