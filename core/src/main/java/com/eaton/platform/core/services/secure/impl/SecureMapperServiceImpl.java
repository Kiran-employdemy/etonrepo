package com.eaton.platform.core.services.secure.impl;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.secure.SecureMapperService;
import com.eaton.platform.integration.auth.constants.SecureConstants;
import com.eaton.platform.integration.auth.models.UserProfile;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

import static com.eaton.platform.integration.auth.constants.SecureConstants.TAGS_PATH;


/**
 *  The class SecureMapperServiceImpl : provides the actual tag based on key
 */
@Component(service = SecureMapperService.class, immediate = true)
public class SecureMapperServiceImpl implements SecureMapperService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecureMapperServiceImpl.class);

    @Reference
    private AdminService adminService;

    private JSONObject countryJson;

    private boolean isSecureMappingFileLoaded;

    private RoleMappingObject roleMappingObject;
    private final Map<String, Function<String, String>> tagMap = new HashMap<>();

    @Activate
    protected void activate(){
        LOGGER.debug("Activation Method Start :: SecureMapperServiceImpl");
        tagMap.put(SecureConstants.ROLE_MAPPER_ACCOUNT_KEY, role -> {
            if (roleMappingObject.getAccounttype().containsKey(role)){
                return roleMappingObject.getAccounttype().get(role).get(TAGS_PATH);
            };
            return StringUtils.EMPTY;
        });
        tagMap.put(SecureConstants.ROLE_MAPPER_APP_KEY, role -> {
            if (roleMappingObject.getApplicationaccess().containsKey(role)){
                return roleMappingObject.getApplicationaccess().get(role).get(TAGS_PATH);
            };
            return StringUtils.EMPTY;
        });
        tagMap.put(SecureConstants.ROLE_MAPPER_PROD_KEY, role -> {
            if (roleMappingObject.getProductcategory().containsKey(role)){
                return roleMappingObject.getProductcategory().get(role).get(TAGS_PATH);
            };
            return StringUtils.EMPTY;
        });
        tagMap.put(SecureConstants.ROLE_MAPPER_PROGRAM_TYPE_AND_TIERS_KEY, role -> {
            if (roleMappingObject.getPartnerProgramTypeAndTier().containsKey(role)){
                return roleMappingObject.getPartnerProgramTypeAndTier().get(role).get(TAGS_PATH);
            };
            return StringUtils.EMPTY;
        });
        tagMap.put(SecureConstants.ROLE_MAPPER_OKTAGROUP_KEY, role->{
           if(roleMappingObject.getOktagroups().containsKey(role)){
               return roleMappingObject.getOktagroups().get(role).get(TAGS_PATH);
           }
           return StringUtils.EMPTY;
        });
        loadFile();
        LOGGER.debug("Activation Method END :: SecureMapperServiceImpl");
    }

    /**
     *
     */
    private void loadFile(){
        LOGGER.debug("loadFile() Method Start :: SecureMapperServiceImpl");
        try(ResourceResolver resourceResolver = adminService.getReadService()) {
            final Resource resource = resourceResolver.getResource(SecureConstants.ROLLMAPPER_JSON);
            if (null != resource) {
                final Asset rolesMapper = resource.adaptTo(Asset.class);
                if (null != rolesMapper && rolesMapper.getRendition(DamConstants.ORIGINAL_FILE) != null) {
                    createRoleMapperJsonBuilder(rolesMapper);
                }else {
                    // Secure Mapper File - Failed to load
                    isSecureMappingFileLoaded = false;
                    LOGGER.error("SecureMapperServiceImpl::ALERT - loadFile() Method Rolemapper Asset resolved to NULL!!.");
                }
            }else {
                // Secure Mapper File - Failed to load
                isSecureMappingFileLoaded = false;
                LOGGER.error("SecureMapperServiceImpl::ALERT - loadFile() RoleMapper JSON Resource not found!");
            }
            createLocaleTagsJson(resourceResolver);
        } catch (Exception ex) {
            LOGGER.error(" Exception in SecureMapperServiceImpl  activate method :::{}", ex.getMessage(),ex);
        }
        LOGGER.debug("loadFile() Method END :: SecureMapperServiceImpl");
    }

    /**
     * This Method provides actual key value
     * @param role
     * @param secureCategory
     * @return actualTagValue
     */
    @Override
    public String getRoleBasedKeys(String role, String secureCategory)  {
        if(tagMap.containsKey(secureCategory)){
            return tagMap.get(secureCategory).apply(role);
        }
        return StringUtils.EMPTY;
    }


    /**
     * Return Mapped Profile
     * @param userAuthProfile
     * @return
     */
    public UserProfile mapSecureTags(UserProfile userAuthProfile) {
        if(!isSecureMappingFileLoaded){
            loadFile();
        }
        userAuthProfile.setAccountTypeTags(getMappedTags(userAuthProfile.getAccountTypes(), SecureConstants.ROLE_MAPPER_ACCOUNT_KEY));
        userAuthProfile.setAppAccessTags(getMappedTags(userAuthProfile.getApplicationAccessTags(), SecureConstants.ROLE_MAPPER_APP_KEY));
        userAuthProfile.setProductCategoriesTags(getMappedTags(userAuthProfile.getProductCategories(), SecureConstants.ROLE_MAPPER_PROD_KEY));
        userAuthProfile.setPartnerProgramTypeAndTierLevelTags(getMappedTags(userAuthProfile.getPartnerProgramTypeAndTierLevels(), SecureConstants.ROLE_MAPPER_PROGRAM_TYPE_AND_TIERS_KEY));
        userAuthProfile.setUserOktaGroups(getMappedTags(userAuthProfile.getUserOktaGroups(),SecureConstants.ROLE_MAPPER_OKTAGROUP_KEY));
        userAuthProfile.setLocaleTags(getMappedLocaleTag(userAuthProfile.getCountryCode()));
        return userAuthProfile;
    }

    /**
     * Return Mapped Tags
     * @param secureAttributes
     * @return
     */
    private List<String> getMappedTags(Iterable<String> secureAttributes, String secureCategory){
        List<String> list = new ArrayList<>();
        if(secureAttributes != null) {
            for (String property : secureAttributes) {
                LOGGER.debug("Input Key ::::: {}", property);
                if (property != null && !property.isEmpty()) {
                    String value = getRoleBasedKeys(property, secureCategory);
                    LOGGER.debug("Tag Found ::::: {}", value);
                    if(!value.isEmpty()){
                        list.add(value);
                    }
                }
            }
        }
        LOGGER.debug("Final List - Mapped  ::: {}", list);
        return list;
    }

    /**
     * This method returns the country tag that matches the given country code and also region tag of that particular country
     * @param countryCode ex:US
     * @return countryTag ex: ["eaton:country/north-america/us", "eaton:country/north-america"]
     */
    private List<String> getMappedLocaleTag(String countryCode){
        LOGGER.debug("getMappedLocaleTag  Start  ::: {}", countryCode);
        final List<String> list = new ArrayList<>();
        try {
            if (StringUtils.isNotBlank(countryCode) && this.countryJson != null) {
                countryCode = countryCode.toLowerCase(Locale.ENGLISH);
                if (this.countryJson.has(countryCode)) {
                    final String countryTagId = this.countryJson.getString(countryCode);
                    list.add(countryTagId);
                    final String[] countryRegionCode = getCountryRegionCode(countryTagId);
                    final String regionCode = getRegionCode(countryRegionCode);
                    if (this.countryJson.has(regionCode)) {
                        list.add(this.countryJson.getString(regionCode));
                    }
                }
            }
        } catch (JSONException e) {
            LOGGER.error("JSONException in SecureMapperServiceImpl  getMappedLocaleTag() method :: {}", e.getMessage());
        }
        LOGGER.debug("Final country List - Mapped  ::: {}", list);
        return list;
    }

    private static String getRegionCode(String[] countryRegionCode) {
        if (countryRegionCode.length >= SecureConstants.NUMBER_THREE) {
            return countryRegionCode[countryRegionCode.length - SecureConstants.NUMBER_TWO];
        }
        return StringUtils.EMPTY;
    }

    private static String[] getCountryRegionCode(String countryTagId) {
        if (StringUtils.isNotBlank(countryTagId)) {
            return countryTagId.split(CommonConstants.SLASH_STRING);
        }
        return new String[0];
    }

    /**
     * Method to build the  Json Object of country&Region tags available under - eaton:country
     * TagName is used as json key and TagId is set as value
     * @param resourceResolver
     */
    private void createLocaleTagsJson(ResourceResolver resourceResolver){
        LOGGER.debug("createLocaleTagsJson Start :: SecureMapperServiceImpl");
        countryJson = new JSONObject();
        if (null != resourceResolver) {
            TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
            if(null != tagManager) {
                final Iterator<Tag> tagIterator = tagManager.resolve(CommonConstants.COUNTRY_TAG_ROOT).listAllSubTags();
                tagIterator.forEachRemaining(tag -> {
                    if (StringUtils.isNotEmpty(tag.getName())) {
                        try {
                            countryJson.put(tag.getName(), tag.getTagID());
                        } catch (JSONException e) {
                            LOGGER.error("JSONException :: createLocaleTagsJson() in SecureMapperServiceImpl : {}", e.getMessage());
                        }
                    }
                });
            }
        }
        LOGGER.debug("createLocaleTagsJson End :: SecureMapperServiceImpl ::{}", countryJson.length());
    }

    /**
     * Method to read the data from roleMapperJson file and construct the jsonBuilder object
     * @param rolesMapper roleMapperJson file
     */
    private void createRoleMapperJsonBuilder(Asset rolesMapper){
        LOGGER.debug("createRoleMapperJsonBuilder Start :: SecureMapperServiceImpl");
        roleMappingObject = new RoleMappingObject();
        try(InputStream is = rolesMapper.getRendition(DamConstants.ORIGINAL_FILE).getStream()){
            roleMappingObject = new Gson().fromJson(new InputStreamReader(is, Charset.defaultCharset()), RoleMappingObject.class);
        } catch (IOException io) {
            LOGGER.error("Failed to Read the Mapping File :: IOException in createRoleMapperJsonBuilder method in SecureMapperServiceImpl {}", io.getMessage());
        }
        // Secure Mapper File loaded successful
        isSecureMappingFileLoaded = true;
        LOGGER.debug("createRoleMapperJsonBuilder End :: SecureMapperServiceImpl");
    }

    /**
     * Load updated Secure Mapper File from DAM
     */
    public void loadUpdatedSecureMapperFile(){
        this.loadFile();
    }
}
