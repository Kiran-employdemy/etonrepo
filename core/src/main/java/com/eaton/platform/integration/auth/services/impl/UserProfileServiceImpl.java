package com.eaton.platform.integration.auth.services.impl;

import com.day.cq.dam.api.Asset;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.services.secure.SecureMapperService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.core.util.SecureUtil;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.secure.SecureAttributesModel;
import com.eaton.platform.integration.auth.constants.SecureConstants;
import com.eaton.platform.integration.auth.models.UserProfile;
import com.eaton.platform.integration.auth.services.AuthenticationServiceConfiguration;
import com.eaton.platform.integration.auth.services.UserProfileService;
import com.eaton.platform.integration.auth.util.EatonAuthUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.apache.sling.api.resource.ResourceUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Iterator;
import java.util.Arrays;
import java.util.Map;

import java.util.concurrent.TimeUnit;

/**
 * Service for handling all UserProfile related use cases
 */
@Component(service = UserProfileService.class, immediate = true,
        property = {
                AEMConstants.SERVICE_VENDOR_EATON,
                AEMConstants.SERVICE_DESCRIPTION + "User Profile Service for secure implementation",
                AEMConstants.PROCESS_LABEL + "UserProfileService"
        })
public class UserProfileServiceImpl implements UserProfileService {
    private static final String USER_PROFILE_IDENTIFIER = "MyEaton Profile Page";

    private static final Logger LOG = LoggerFactory.getLogger(UserProfileServiceImpl.class);

    @Reference
    private AuthenticationServiceConfiguration authenticationServiceConfiguration;

    @Reference
    private HttpClientBuilderFactory httpFactory;

    @Reference
    private SecureMapperService secureMapperService;

    private String userProfileEndpoint;

    private PoolingHttpClientConnectionManager conMgr;

    private Cache<String, UserProfile> userProfileCache;

    private String trackingLookupEndpoint;


    @Override
    public UserProfile getUserProfile(String id, Long uniqueCacheIdentifier) {
        LOG.debug("Start with getUserProfile method :: UserProfileServiceImpl");

        //Check for UserProfile in Cache
        UserProfile userProfile = userProfileCache.getIfPresent(createUniqueCacheIdentifier(id,uniqueCacheIdentifier));

        if(userProfile == null) {
            LOG.debug("Valid UserProfile not found in Cache");
            conMgr = EatonAuthUtil.getMultiThreadedConf(conMgr, authenticationServiceConfiguration);
            userProfile = EatonAuthUtil.callUserProfileAPI(userProfileEndpoint, conMgr, httpFactory, authenticationServiceConfiguration, id);

            //Setting UserProfile in Cache.
            if(userProfile!=null) {
                LOG.debug("Setting UserProfile in Cache");
                userProfileCache.put(createUniqueCacheIdentifier(id,uniqueCacheIdentifier), secureMapperService.mapSecureTags(userProfile));
            }else{
                LOG.error("UserProfile retrieved from API call is null");
            }
        }else{
            LOG.debug("Valid UserProfile found in Cache");
        }

        LOG.debug("End with getToken method :: UserProfileServiceImpl");
        return userProfile;
    }

    public boolean updateUserTermsAndConditionsTimestamp(String id, Long uniqueCacheIdentifier) {
        String cacheKey = createUniqueCacheIdentifier(id,uniqueCacheIdentifier);
        UserProfile userProfile = userProfileCache.getIfPresent(cacheKey);
        if(null != userProfile) {
            userProfile.setEatonEshopEulaAcceptDate(new Date());
            userProfile.setEatonEulaAcceptDate(new Date());
            // set the user back into the cache after timestamp has been set
            userProfileCache.put(cacheKey,userProfile);
            return true;
        } else {
            LOG.error("UserProfileServiceImpl :: updateUserTermsAndConditionsTimestamp :: Unable to update EULA timestamp with id");
        }
        return false;
    }

    @Override
	public JSONObject trackUserProfileForDownload(UserProfile userProfile,final SlingHttpServletRequest request, Map<String, String[]> links) {

		LOG.debug("Start with trackUserProfileForDownload method :: UserProfileServiceImpl");
		JSONObject trackUserData = new JSONObject();

        try {
            conMgr = EatonAuthUtil.getMultiThreadedConf(conMgr, authenticationServiceConfiguration);

            Iterator<String> itr = links.keySet().iterator();
            while(itr.hasNext())  {
                String linksArray= itr.next();
                JSONArray documentLinks = new JSONArray(linksArray);
                LOG.debug("******** Documents Links Received --> {}", documentLinks);
                if(documentLinks.length() > 0) {
                    return callTrackDownloadAPI(documentLinks, request, trackUserData, userProfile);
                }
            }

		}catch (JSONException e) {
            LOG.error("********* Error While Constructing track download user Request Input **********",e);
		}
		LOG.debug("End with trackUserProfileforDownload method :: UserProfileServiceImpl");
		return new JSONObject();
	}

	private JSONObject callTrackDownloadAPI( JSONArray documentLinks, SlingHttpServletRequest request, JSONObject trackUserData, UserProfile userProfile){
        String assetTitle= null;
        String publicationDate = null;
        String eccn=null;
        String assetProductLine=null;
        String assetPartNumber=null;
        String assetPartDescription=null;
        String mimeType=null;
        LOG.debug("Start with callTrackDownloadAPI method :: UserProfileServiceImpl");
        for (int i = 0; i < documentLinks.length(); i++) {
            try {
                Resource assetResource = request.getResourceResolver().resolve(request.getResourceResolver().map(documentLinks.getString(i)));
                if (!ResourceUtil.isNonExistingResource(assetResource)) {
                    LOG.debug("******** Resource is Not EMPTY --> {}", assetResource);
                    assetTitle = CommonUtil.getAssetTitle(documentLinks.getString(i), assetResource);
                    SecureAttributesModel secureModel = SecureUtil.getSecureModelByResource(assetResource);
                    Asset damAsset = CommonUtil.getAsset(assetResource);
                    if (damAsset != null) {
                        publicationDate = damAsset.getMetadataValue(CommonConstants.ASSET_PUBLICATION_DATE);
                        eccn = damAsset.getMetadataValue(CommonConstants.ECCN);
                        assetPartNumber = damAsset.getMetadataValue(CommonConstants.ASSET_PART_NUMBER);
                        assetPartDescription = damAsset.getMetadataValue(CommonConstants.ASSET_PART_DESC);
                        assetProductLine = damAsset.getMetadataValue(CommonConstants.ASSET_PRODUCT_LINE);
                        mimeType = damAsset.getMimeType();
                    }
                    // Set User Profile Data to trackUser data POJO
                    setUserProfileValuesToTrackDownloadPOJO(secureModel, trackUserData, userProfile);
                    //Asset Data
                    trackUserData.put(SecureConstants.ATTR_ASSET_NAME, assetTitle);
                    trackUserData.put(SecureConstants.ATTR_ASSET_TYPE, mimeType);
                    trackUserData.put(SecureConstants.ATTR_ASSET_IDENTIFIER, assetTitle);
                    trackUserData.put(SecureConstants.ATTR_ASSET_PUB_DATE, publicationDate);
                    trackUserData.put(SecureConstants.ATTR_ECCN, eccn);
                    trackUserData.put(SecureConstants.ATTR_ASSET_PART_DESCRIPTION, assetPartDescription);
                    trackUserData.put(SecureConstants.ATTR_ASSET_PART_NUMEBR, assetPartNumber);
                    trackUserData.put(SecureConstants.ATTR_ASSET_PROD_LINE, assetProductLine);

                    return EatonAuthUtil.trackDownload(trackingLookupEndpoint, conMgr, httpFactory, authenticationServiceConfiguration, trackUserData);
                }
            }catch(JSONException json){
                LOG.error("********** Exception While Setting Values to JSON Object ***************", json);
            }
        }
        LOG.debug("END with callTrackDownloadAPI method :: UserProfileServiceImpl");
        return new JSONObject();
    }

    /**
     *  This method sets User Profile attribute values to tackUser POJO
     * @param secureModel Resource Model
     * @param trackUserData trackUserData POJO
     * @param userProfile current user Profile
     */
	private static void setUserProfileValuesToTrackDownloadPOJO( SecureAttributesModel secureModel, JSONObject trackUserData, UserProfile userProfile  ){

        try {
            LOG.debug("************ setUserProfileValueToTrackDownloadPojo() : Start *********");
            if (secureModel.getProductCategories() != null && secureModel.getProductCategories().length > 0) {
                trackUserData.put(SecureConstants.ATTR_ASSET_PROD_CAT, String.join("||", secureModel.getProductCategories()));
            }
            if (secureModel.getAccountType() != null && secureModel.getAccountType().length > 0) {
                trackUserData.put(SecureConstants.ATTR_ASSET_ACCOUNT_TYPE, Arrays.asList(secureModel.getAccountType()));
            }
            //User Profile Data
            trackUserData.put(SecureConstants.ATTR_USER_INDENTIFER, userProfile.getId());
            trackUserData.put(SecureConstants.ATTR_FIRST_NAME, userProfile.getGivenName());
            trackUserData.put(SecureConstants.ATTR_LAST_NAME, userProfile.getLastName());
            trackUserData.put(SecureConstants.ATTR_EMAIL, userProfile.getEmail());
            trackUserData.put(SecureConstants.ATTR_ADDRESS1, userProfile.getCompanyAddress());
            trackUserData.put(SecureConstants.ATTR_CITY, userProfile.getCompanyState());
            trackUserData.put(SecureConstants.ATTR_STATE, userProfile.getCountryCode());
            trackUserData.put(SecureConstants.ATTR_COUNTRY, userProfile.getCountryCode());
            trackUserData.put(SecureConstants.ATTR_POSTAL_CODE, userProfile.getCompanyZipCode());
            trackUserData.put(SecureConstants.ATTR_USER_TYPE, userProfile.getEatonPersonType());
            trackUserData.put(SecureConstants.ATTR_ADDRESS2, userProfile.getCompanyAddress());
        } catch (JSONException e) {
            LOG.error("************ setUserProfileValueToTrackDownloadPOJO() :  Exception While setting profile Attributes *********", e);
        }
        LOG.debug("************ setUserProfileValueToTrackDownloadPOJO() : END *********");

    }





    @Override
    public JSONObject changePassword(String oldPassword, String newPassword,UserProfile userProfile) {
        LOG.debug("Start with changePassword method :: UserProfileServiceImpl");
        JSONObject response = new JSONObject();
        if(oldPassword != null && newPassword!=null) {
            LOG.debug("Valid UserProfile not found in Cache");
            conMgr = EatonAuthUtil.getMultiThreadedConf(conMgr, authenticationServiceConfiguration);
            JSONObject changePwdInputRequest = new JSONObject();
            try {
                changePwdInputRequest.put(SecureConstants.ATTR_UPDATER_INDENTIFER,USER_PROFILE_IDENTIFIER);
                changePwdInputRequest.put(SecureConstants.ATTR_UPDATER_FIRST_NAME,userProfile.getGivenName());
                changePwdInputRequest.put(SecureConstants.ATTR_UPDATER_LAST_NAME,userProfile.getLastName());
                changePwdInputRequest.put(SecureConstants.ATTR_CURRENT_EMAIL_ADDR,userProfile.getEmail());
                changePwdInputRequest.put(SecureConstants.ATTR_EMAIL_ADDRESS,userProfile.getEmail());
                changePwdInputRequest.put(SecureConstants.ATTR_CURRENT_PASSWORD,oldPassword);
                changePwdInputRequest.put(SecureConstants.ATTR_PASSWORD,newPassword);
            } catch (JSONException e) {
                LOG.error("********* Error While Constructing Change Password Request Input **********",e);
            }
            response = EatonAuthUtil.changePassword(userProfileEndpoint, conMgr, httpFactory, authenticationServiceConfiguration,changePwdInputRequest);
        }
        LOG.debug("End with changePassword method :: UserProfileServiceImpl");
        return response;
    }
    @Override
    public void removeUserCacheEntry(String id, Long uniqueCacheIdentifier){
        LOG.debug(String.format("Attempting to remove user %s entry from cache",
                createUniqueCacheIdentifier(id,uniqueCacheIdentifier)));
        userProfileCache.invalidate(createUniqueCacheIdentifier(id,uniqueCacheIdentifier));
        LOG.debug("Successfully removed cache entry ");
    }
    @Override
    public void updateLastLogin(UserProfile userProfile){
        LOG.debug("Start updateLastLogin method :: UserProfileServiceImpl");
        JSONObject userProfileInfo = new JSONObject();

        try {
            userProfileInfo.put(SecureConstants.ATTR_UPDATER_INDENTIFER,USER_PROFILE_IDENTIFIER);
            userProfileInfo.put(SecureConstants.ATTR_UPDATER_FIRST_NAME,userProfile.getGivenName());
            userProfileInfo.put(SecureConstants.ATTR_UPDATER_LAST_NAME,userProfile.getLastName());
            userProfileInfo.put(SecureConstants.ATTR_CURRENT_EMAIL_ADDR,userProfile.getUid());
            userProfileInfo.put(SecureConstants.ATTR_EMAIL_ADDRESS,userProfile.getUid());
            userProfileInfo.put(SecureConstants.ATTR_UPDATE_LAST_LOGIN,true);
        } catch (JSONException e) {
            LOG.error("********* Error While Constructing User Profile Information **********",e);
        }
        EatonAuthUtil.updateLastLogin(userProfileEndpoint,conMgr,httpFactory,authenticationServiceConfiguration,userProfileInfo);
        LOG.debug("End with updateLastLogin method :: UserProfileServiceImpl");
    }
    private String createUniqueCacheIdentifier(String id, Long uniqueIdentifier){
        if(uniqueIdentifier == null){
            return id;
        }
        return id + CommonConstants.HYPHEN + uniqueIdentifier.toString();
    }
    @Activate
    @Modified
    protected final void activate() {
    	trackingLookupEndpoint = authenticationServiceConfiguration.getTrackDownloadEndpointUrl();
        userProfileEndpoint = authenticationServiceConfiguration.getUserLookupUrl();
        userProfileCache = Caffeine.newBuilder()
                .maximumSize(authenticationServiceConfiguration.getMaxCacheSize())
                .expireAfterWrite(authenticationServiceConfiguration.getUserProfileCacheTTL(), TimeUnit.SECONDS)
                .build();

    }
}
