package com.eaton.platform.core.util;

import com.adobe.acs.commons.util.CookieUtil;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.eaton.platform.core.constants.AEMFormConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ProfileAttributesOptions;
import com.eaton.platform.core.models.secure.SecureAttributesModel;
import com.eaton.platform.integration.auth.constants.SecureConstants;
import com.eaton.platform.integration.auth.models.AuthenticationToken;
import com.eaton.platform.integration.auth.models.UserProfile;
import com.eaton.platform.integration.auth.services.AuthorizationService;
import com.eaton.platform.integration.auth.services.EndecaSecretKeyConfiguration;
import com.eaton.platform.integration.endeca.constants.EndecaConstants;
import com.eaton.platform.integration.endeca.services.EndecaConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;


/**
 * The Class SecureUtil.
 */
public final class SecureUtil {


    private static final Logger LOGGER = LoggerFactory.getLogger(SecureUtil.class);

    /**
     * Instantiates a new SecureUtil .
     */
    private SecureUtil() {
        LOGGER.debug("Inside SecureUtil constructor");
    }

    /**
     * Get SecureTagNames
     * this method can be used for getting the secure Tag Names from Secure Tab
     */
    public static String[] getSecureTagNames(String[] tagIds, ResourceResolver resourceResolver, boolean titlePathRequired) {
        LOGGER.debug("SecureUtil :: getSecureTagNames() :: Start");
        List<String> tagNames = new ArrayList<>();
        if (null != tagIds && null != resourceResolver) {
            final TagManager tagMgr = resourceResolver.adaptTo(TagManager.class);
            if (null != tagMgr) {
                for (String tagId : tagIds) {
                    Tag secureTag = tagMgr.resolve(tagId);
                    if (null != secureTag && titlePathRequired) {
                        tagNames.add(secureTag.getTitlePath());
                    } else if (null != secureTag) {
                        tagNames.add(secureTag.getName());
                    }
                }
            }
        } else {
            return new String[0];
        }
        LOGGER.debug("SecureUtil :: getSecureTagNames() :: Exit");
        return tagNames.toArray(new String[tagNames.size()]);
    }

    /**
     * Get SecureTagNamesPartnerProgrammeAndTierLevel
     * this method can be used for getting the secure Tag Names from Secure Tab
     */
    public static String[] getSecureTagNamesPartnerProgrammeAndTierLevel(String[] tagIds, ResourceResolver resourceResolver) {
        LOGGER.debug("SecureUtil :: getSecureTagNamesPartnerProgramme() :: Start");
        List<String> tagNames = new ArrayList<>();
        if (null != tagIds && null != resourceResolver) {
            final TagManager tagMgr = resourceResolver.adaptTo(TagManager.class);
            if (null != tagMgr) {
                for (String tagId : tagIds) {
                    String[] parts = tagId.split(CommonConstants.SLASH_STRING);
                    if (parts.length>2) {
                        tagNames.add(parts[1] + CommonConstants.UNDERSCORE + parts[2]);
                    } else {
                        tagNames.add(parts[1]);
                    }
                }
            }
        }
        LOGGER.debug("SecureUtil :: getSecureTagNamesPartnerProgramme() :: Exit");
        return tagNames.toArray(new String[tagNames.size()]);
    }

    /**
     * getTagTitles :  this method can be used for getting the secure Tag Titles from Secure Tab
     *
     * @param tagIds           - List of tag Id's
     * @param resourceResolver - Resource Resolver
     * @return List Of Tag Titles
     */
    public static List<String> getTagTitles(List<String> tagIds, ResourceResolver resourceResolver) {
        LOGGER.debug("SecureUtil :: getSecureTagNames() :: Start");
        List<String> tagTitles = new ArrayList<>();
        if (null != tagIds && null != resourceResolver) {
            final TagManager tagMgr = resourceResolver.adaptTo(TagManager.class);
            if (null != tagMgr) {
                for (String tagId : tagIds) {
                    Tag secureTag = tagMgr.resolve(tagId);
                    if (null != secureTag) {
                        tagTitles.add(secureTag.getTitle());
                    }
                }
            }
        }
        LOGGER.debug("SecureUtil :: getSecureTagNames() :: Exit");
        return tagTitles;
    }

    /**
     * Get getSecurePipeSeperatedTagNames
     * this method can be used for getting the pipe seperated Tag Names
     */

    public static String getPipeSeparatedSecuredTagNames(String[] tags) {
        return String.join("||", tags);
    }


    /**
     * This method returns Secure Attribute Model based on the resource Asset/Page path
     *
     * @param resource
     * @return
     */
    public static SecureAttributesModel getSecureModelByResource(Resource resource) {
        if (resource != null) {
            if (resource.getPath().contains(DamConstants.MOUNTPOINT_ASSETS)) {
                LOGGER.debug("******* Resource Type : Assets --- > {}", resource.getPath());
                Resource metaData = resource.getChild(SecureConstants.JCR_CONTENT_METADATA);
                if (metaData != null) {
                    return metaData.adaptTo(SecureAttributesModel.class);
                }
            } else if (resource.getChild(JcrConstants.JCR_CONTENT) != null) {
                LOGGER.debug("******* Resource Type : Page --- > {}", resource.getPath());
                Resource pageContent = resource.getChild(JcrConstants.JCR_CONTENT);
                if (pageContent != null) {
                    return pageContent.adaptTo(SecureAttributesModel.class);
                }
            } else {
                LOGGER.debug("******* Resource Type : Component --- > {}", resource.getPath());
                return resource.adaptTo(SecureAttributesModel.class);
            }
        }
        return new SecureAttributesModel();
    }

    /**
     * @param headerName
     * @param request
     * @return
     */
    public static String getHeaderValue(String headerName, ServletRequest request) {
        LOGGER.debug("******* Requested Header Name ---> {}", headerName);
        SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;
        return slingRequest.getHeader(headerName) != null ? slingRequest.getHeader(headerName) : StringUtils.EMPTY;
    }

    /**
     * /**
     * This Methods checks if the request is originated from Endeca by checking the cookie (aem-secret-key) if present.
     * If Presents, it cross verify with  Configured key in the OSGI. If both matches returns true else false.
     * True : Allow the page/asset to serve.
     * False : Deny the page/asset request.
     */
    public static boolean isValidSecretKey(SlingHttpServletRequest request, String aemSecretKey) {
        boolean isValid = false;
        Cookie secretKeyCookie = CookieUtil.getCookie(request, EndecaConstants.COOKIE_NAME_SECRET_KEY);
        if (secretKeyCookie != null) {
            if (secretKeyCookie.getValue() != null && StringUtils.isNotEmpty(aemSecretKey)
                    && aemSecretKey.equalsIgnoreCase(secretKeyCookie.getValue())) {
                isValid = true;
                LOGGER.debug("************* ENDECA : Both the Keys got Matched ********** {} - {}", secretKeyCookie.getValue(), aemSecretKey);
            } else {
                LOGGER.debug("************* Failed ENDECA Request: Secret Key did not match info ********** {}", secretKeyCookie.getValue());
            }
        } else {
            LOGGER.debug("Cookie doesn't exist for the Endeca request");
        }

        return isValid;
    }

    /**
     * @param request
     * @return
     */
    public static boolean isRequestOriginatedFromEndeca(SlingHttpServletRequest request, EndecaConfig endecaConfig) {
        boolean isEndecaRequest = false;
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);

            LOGGER.debug("Header info {} {}", headerName, headerValue);
        }
        if (SecureUtil.getHeaderValue(CommonConstants.USER_AGENT, request).equalsIgnoreCase(endecaConfig.getConfigServiceBean().getEndecaUserAgentValue())) {
            isEndecaRequest = true;
        } else {
            LOGGER.debug("User agent of current request {}", request.getHeader(CommonConstants.USER_AGENT));

        }
        return isEndecaRequest;
    }

    /**
     * This Methods checks if the request is originated from Endeca by checking the cookie (aem-secret-key) if present.
     * If Presents, it cross verify with  Configured key in the OSGI. If both matches returns true else false.
     * True : Allow the page/asset to serve.
     * False : Deny the page/asset request.
     *
     * @param request
     * @param endecaSecretKeyConfiguration
     * @return True : Allow the page/asset to serve.
     * False : Deny the page/asset request.
     */
    public static boolean isValidEndecaRequest(SlingHttpServletRequest request, EndecaSecretKeyConfiguration endecaSecretKeyConfiguration, EndecaConfig endecaConfig) {
        return SecureUtil.isRequestOriginatedFromEndeca(request, endecaConfig) && SecureUtil.isValidSecretKey(request,
                java.util.Optional.ofNullable(endecaSecretKeyConfiguration.getAemSecretKey()).orElseGet(() -> StringUtils.EMPTY));
    }

    /**
     * Method to check if  resource is marked as secure
     *
     * @param resource resource
     * @return True : resource(asset/page) is marked as secure
     * False : resource(asset/page) is Not  marked as secure
     */
    public static boolean isSecureResource(Resource resource) {
        boolean isSecure = false;
        final SecureAttributesModel secureModel = SecureUtil.getSecureModelByResource(resource);
        if (null != secureModel) {
            isSecure = (secureModel.getSecurePage() != null && secureModel.getSecurePage().equals(SecureConstants.TRUE)) ||
                    (secureModel.getSecureAsset() != null && secureModel.getSecureAsset().equals(SecureConstants.YES));
        }
        return isSecure;
    }

    /**
     * This Method takes input as key and profile object. Return the matched value based on the
     * key passed.
     *
     * @param key        -  Key
     * @param adDataJson - User Profile XML
     * @return value from the profile by Key.
     */
    public static String getProfileValueByKey(String key, String adDataJson) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(adDataJson);
            JSONObject afData = (JSONObject) json.get("afData");
            JSONObject afUnboundData = (JSONObject) afData.get("afBoundData");
            JSONObject data = (JSONObject) afUnboundData.get("data");
            JSONObject profileObject = (JSONObject) data.get("UpdateRequest");
            if (profileObject != null && profileObject.get(key) != null) {
                return profileObject.get(key).toString();
            }
        } catch (org.json.simple.parser.ParseException e) {
            LOGGER.error("getProfileValueByKey : Exception while Parsing XML ", e);
        }
        return StringUtils.EMPTY;
    }

    /**
     * This Method constructs the JSON from the userProfile Object which helps to auto-populate(Prefill) the AEM FORM fields.
     * key passed.
     * Json Structure - example
     * {"afData":{"afBoundData":{"data":{"UpdateRequest":{"lastName":"thomas"}}},"afUnboundData":{"data":{"lastName":"thomas"}}}}
     *
     * @param authenticationToken - Current Profile Authentication Token
     * @param resourceResolver    - Resource Resolver
     * @return value from the profile by Key.
     */
    public static String constructPrefillBoundJSON(AuthenticationToken authenticationToken, ResourceResolver resourceResolver) {
        UserProfile userProfile = authenticationToken.getUserProfile();
        if (userProfile != null) {
            JSONObject afDataObject = new JSONObject();
            JSONObject afBoundData = new JSONObject();
            JSONObject afData = new JSONObject();
            JSONObject updateRequest = new JSONObject();
            JSONObject basicProfileData = new JSONObject();
            JSONObject userRegistrationData = new JSONObject();
            JSONObject unboundAfData = new JSONObject();

            updateRequest.put(AEMFormConstants.FORM_UPDATE_REQUEST_ATTR, getProfileUserRecord(userProfile, resourceResolver, basicProfileData));
            updateRequest.put(AEMFormConstants.FORM_REGISTRATION_REQUEST_ATTR, getRegistrationRequestData(userProfile, resourceResolver, userRegistrationData));
            afData.put(AEMFormConstants.FORM_DATA_ATTR, updateRequest);
            afBoundData.put(AEMFormConstants.FORM_AF_BOUND_ATTR, afData);

            unboundAfData.put(AEMFormConstants.FORM_DATA_ATTR, getProfileUserRecord(userProfile, resourceResolver, basicProfileData));
            afBoundData.put(AEMFormConstants.FORM_AF_UNBOUND_ATTR, unboundAfData);
            afDataObject.put(AEMFormConstants.FORM_AF_DATA_ATTR, afBoundData);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("afData Json {}", afDataObject.toJSONString());
            }
            return afDataObject.toJSONString();
        }

        return StringUtils.EMPTY;
    }


    /**
     * @param userProfile
     * @param resourceResolver
     * @return JSON Object holds profile Data.
     */
    private static JSONObject getProfileUserRecord(UserProfile userProfile, ResourceResolver resourceResolver, JSONObject profileData) {
        addItem(ProfileAttributesOptions.GIVEN_NAME.getValue(), userProfile.getGivenName(), profileData);
        addItem(ProfileAttributesOptions.FIRST_NAME.getValue(), userProfile.getGivenName(), profileData);
        addItem(ProfileAttributesOptions.LAST_NAME.getValue(), userProfile.getLastName(), profileData);
        addItem(ProfileAttributesOptions.EMAIL.getValue(), userProfile.getEmail(), profileData);
        addItem(ProfileAttributesOptions.CURRENT_EMAIL.getValue(), userProfile.getEmail(), profileData);
        addItem(ProfileAttributesOptions.COUNTY_CODE.getValue(), userProfile.getCountryCode(), profileData);
        addItem(ProfileAttributesOptions.PRODUCT_CATEGORY_TAGS.getValue(),
                String.join(",", getTagTitles(userProfile.getProductCategoriesTags(), resourceResolver)), profileData);
        addItem(ProfileAttributesOptions.APP_ACCESS_TAGS.getValue(),
                String.join(",", getTagTitles(userProfile.getAppAccessTags(), resourceResolver)), profileData);
        addItem(ProfileAttributesOptions.USER_TYPE_TAGS.getValue(),
                String.join(",", getTagTitles(userProfile.getAccountTypeTags(), resourceResolver)), profileData);
        if (null != userProfile.getAccountTypes() && userProfile.getAccountTypes().iterator().hasNext()) {
            addItem(ProfileAttributesOptions.USER_TYPE.getValue(), userProfile.getAccountTypes().iterator().next(), profileData);
        } else {
            addItem(ProfileAttributesOptions.USER_TYPE.getValue(), StringUtils.EMPTY, profileData);
        }
        addItem(ProfileAttributesOptions.COMPANY_NAME.getValue(), userProfile.getCompanyName(), profileData);
        addItem(ProfileAttributesOptions.COMPANY_ADDRESS.getValue(), userProfile.getCompanyAddress(), profileData);
        addItem(ProfileAttributesOptions.COMPANY_CITY.getValue(), userProfile.getCompanyCity(), profileData);
        addItem(ProfileAttributesOptions.COMPANY_STATE.getValue(), userProfile.getCompanyState(), profileData);
        addItem(ProfileAttributesOptions.COMPANY_ZIP_CODE.getValue(), userProfile.getCompanyZipCode(), profileData);
        addItem(ProfileAttributesOptions.POSTAL_CODE.getValue(), userProfile.getCompanyZipCode(), profileData);
        addItem(ProfileAttributesOptions.COMPANY_BUSINESS_PHONE.getValue(), userProfile.getCompanyBusinessPhone(), profileData);
        addItem(ProfileAttributesOptions.COMPANY_BUSINESS_FAX.getValue(), userProfile.getCompanyBusinessFax(), profileData);
        addItem(ProfileAttributesOptions.COMPANY_MOBILE_NUMBER.getValue(), userProfile.getCompanyMobileNumber(), profileData);
        addItem(ProfileAttributesOptions.SUPPLIER_COMPANY_ID.getValue(), userProfile.getEatonSupplierCompanyId(), profileData);
        return profileData;
    }

    /**
     * @param userProfile
     * @param resourceResolver
     * @return JSON Object holds profile Data.
     */
    private static JSONObject getRegistrationRequestData(UserProfile userProfile, ResourceResolver resourceResolver, JSONObject userRegistrationData) {
        addItem(ProfileAttributesOptions.FIRST_NAME.getValue(), userProfile.getGivenName(), userRegistrationData);
        addItem(ProfileAttributesOptions.LAST_NAME.getValue(), userProfile.getLastName(), userRegistrationData);
        addItem(ProfileAttributesOptions.EMAIL.getValue(), userProfile.getEmail(), userRegistrationData);
        addItem(ProfileAttributesOptions.COUNTY_CODE.getValue(), userProfile.getCountryCode(), userRegistrationData);
        if (null != userProfile.getAccountTypes() && userProfile.getAccountTypes().iterator().hasNext()) {
            addItem(AEMFormConstants.SELECTED_USER_TYPE, userProfile.getAccountTypes().iterator().next(), userRegistrationData);
        } else {
            addItem(AEMFormConstants.SELECTED_USER_TYPE, StringUtils.EMPTY, userRegistrationData);
        }

        return userRegistrationData;
    }

    /**
     * @param elementName
     * @param elementValue
     * @return
     */
    private static void addItem(String elementName, String elementValue, JSONObject profileObject) {
        if (elementName != null && elementValue != null) {
            profileObject.put(elementName, elementValue);
        }
    }

    /**
     * This method checks if the link configured has required privileges or not.
     *
     * @param resource                resource
     * @param link                    link
     * @param authorizationService    authorizationService
     * @param slingHttpServletRequest slingHttpServletRequest
     *                                Returns True : If all the Secure Attributes Matches with current logged-in user.
     *                                Returns False : If Matching criteria fails.
     * @return isAuthorized
     */
    public static boolean isAuthorisedLink(Resource resource, String link, AuthorizationService authorizationService, SlingHttpServletRequest slingHttpServletRequest) {
        Resource secureResource = null;
        boolean isAuthorized = false;
        if (checkIfExternal(link, resource)) {
            secureResource = resource;
        } else if (null != link) {
            secureResource = resource.getResourceResolver().resolve(link);
        } else {
            LOGGER.debug("Inside isAuthorized flow");
        }
        if (null != secureResource) {
            AuthenticationToken authenticationToken = authorizationService.getTokenFromSlingRequest(slingHttpServletRequest);
            if (null != authenticationToken) {
                isAuthorized = authorizationService.isAuthorized(authenticationToken, secureResource);
            }
        }
        return isAuthorized;
    }

    /**
     * This method Returns True : If the link is External. False : Not an external Link
     *
     * @param resourceLink
     * @param resource
     * @return
     */
    private static boolean checkIfExternal(final String resourceLink, Resource resource) {
        Boolean isSecureExternal = false;
        final boolean isFQDNUrl = (null != resourceLink && CommonUtil.getIsExternal(resourceLink));
        if (isFQDNUrl && resource.getValueMap().get(SecureConstants.IS_EXTERNAL, org.apache.commons.lang.StringUtils.EMPTY).equals(SecureConstants.TRUE)) {
            isSecureExternal = true;
        }
        return isSecureExternal;
    }

    /**
     * This method authorized the link/resource by checking all the secure attributes
     *
     * @param resource             current resource
     * @param authorizationService authorisation service from current request.
     * @param slingRequest         current request.
     * @return true - matched with current logged-in user or if the link is non-secure
     * false - If current resource doesn't have right privileges to view.
     */
    public static boolean isAuthorisedLink(Resource resource, AuthorizationService authorizationService, SlingHttpServletRequest slingRequest) {
        boolean isAuthorized = true;
        AuthenticationToken authenticationToken = authorizationService.getTokenFromSlingRequest(slingRequest);
        if (authenticationToken != null && authenticationToken.getUserProfile() != null) {
            isAuthorized = authorizationService.isAuthorized(authenticationToken, resource);
        }
        if (SecureUtil.isSecureResource(resource) && authenticationToken == null) {
            isAuthorized = false;
        }
        return isAuthorized;
    }
}

