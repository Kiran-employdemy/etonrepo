package com.eaton.platform.core.services.vanity.impl;

import com.day.cq.commons.Externalizer;
import com.eaton.platform.core.bean.VanityUrlBean;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.VanityStatus;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.vanity.EatonVanityConfigService;
import com.eaton.platform.core.services.vanity.VanityDataStoreService;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;


/**
 * <html> Description: This class is used to fetch stored vanity json from DAM resource. </html>
 * @author ICF
 * @version 1.0
 * @since 2020
 *
 */
@Component(service = VanityDataStoreService.class,immediate = true,
        property = {
                AEMConstants.SERVICE_VENDOR_EATON,
                AEMConstants.SERVICE_DESCRIPTION + "VanityDataStoreImpl",
                AEMConstants.PROCESS_LABEL + "VanityDataStoreImpl"
        })
public class VanityDataStoreImpl implements VanityDataStoreService {

    @Reference
    private AdminService adminService;

    /**
     * The config service.
     */
    @Reference
    private EatonVanityConfigService eatonVanityConfigService;

    @Reference
    private Externalizer externalizer;

    private static final Logger LOGGER = LoggerFactory.getLogger(VanityDataStoreImpl.class);

    @Override
    public JSONArray getAllVanities() {
        LOGGER.debug(" VanityDataStoreImpl :: getAllVanity() :: Started");
        JSONArray allVanityURLJsonObject = new JSONArray();
        if (null != adminService) {
            final String vanityJsonFolderPath = eatonVanityConfigService.getVanityConfig().getVanityDataStoreParent();
            try (final ResourceResolver readServiceResourceResolver = adminService.getReadService()) {
                if (readServiceResourceResolver != null) {
                    final Resource vanityRes = readServiceResourceResolver
                            .getResource(vanityJsonFolderPath);
                    if (null != vanityRes) {
                       final Iterator<Resource> domainJsonList = vanityRes.listChildren();
                        allVanityURLJsonObject = getAllVanityURLJson(readServiceResourceResolver, domainJsonList);
                    }
                }
            } catch (IOException e) {
                LOGGER.error("IOException in  getAllVanities() {}", e.getMessage());
            } catch (RepositoryException e) {
                LOGGER.error("Repository Exception getAllVanities method {}", e.getMessage());
            } catch (JSONException e) {
                LOGGER.error("JSON Exception in getAllVanities method {}", e.getMessage());
            }
        }
        LOGGER.debug(" VanityDataStoreImpl :: getAllVanity() :: Exit");
        return allVanityURLJsonObject;
    }

    private static JSONArray getAllVanityURLJson(ResourceResolver readServiceResourceResolver, Iterator<Resource> domainJsonList) throws IOException, RepositoryException, JSONException {
        LOGGER.debug(" VanityDataStoreImpl :: getAllVanityURLJson() :: Started");
        final JSONArray allVanityURLJsonArr = new JSONArray();
        while (domainJsonList.hasNext()) {
            JSONObject vanityURLJsonObject = new JSONObject();
            final String nextDomainPath = domainJsonList.next().getPath();
            if (StringUtils.isNotBlank(nextDomainPath) && !nextDomainPath.contains(CommonConstants.JCR_CONTENT_STR)) {
                final String reqJsonString = CommonUtil.getResponseStringFromFile(readServiceResourceResolver, nextDomainPath);
                if (StringUtils.isNotBlank(reqJsonString)) {
                    vanityURLJsonObject = new JSONObject(reqJsonString);
                }
            }
            allVanityURLJsonArr.put(vanityURLJsonObject);
        }
        LOGGER.debug(" VanityDataStoreImpl :: getAllVanityURLJson() :: Exit");
        return allVanityURLJsonArr;
    }


    @Override
    public JSONObject getDomainSpecificVanities(String domainName) {
        LOGGER.debug("VanityDataStoreImpl :: getDomainSpecificVanities() :: Started");
        JSONObject vanityURLJsonObject = new JSONObject();
        if (null != adminService) {
            final String vanityJsonfilePath = eatonVanityConfigService.getVanityJsonfilePath(domainName);
            try (final ResourceResolver readServiceResourceResolver = adminService.getReadService()) {
                if (StringUtils.isNotBlank(vanityJsonfilePath)) {
                    final String reqJsonString = CommonUtil.getResponseStringFromFile(readServiceResourceResolver, vanityJsonfilePath);
                    if (StringUtils.isNotBlank(reqJsonString)) {
                        vanityURLJsonObject = new JSONObject(reqJsonString);
                    }
                }
            } catch (IOException e) {
                LOGGER.error("Error in reading file {}", e.getMessage());
            } catch (RepositoryException e) {
                LOGGER.error("Repository Exception {}", e.getMessage());
            } catch (JSONException e) {
                LOGGER.error("JSONException :{}", e.getMessage());
            }

            LOGGER.debug(" Exiting from getDomainSpecificVanities()");
            return vanityURLJsonObject;
        }
        LOGGER.debug("Exiting from getDomainSpecificVanities()");
        return vanityURLJsonObject;
    }



    @Override
    public JSONObject lookupAndFetchVanityEntry(SlingHttpServletRequest request) {
        LOGGER.debug("Start VanityDataStoreImpl  lookupAndFetchVanityEntry()");
        final String vanityRequestPath = request.getRequestPathInfo().getResourcePath().replace(CommonConstants.HTML_EXTN,"").toLowerCase(Locale.ENGLISH);
        final String candidateVanity = removeSiteRootPath(vanityRequestPath);
        final String domainName = CommonUtil.getDomainName(request, externalizer);
        final String vanityJsonfilePath = eatonVanityConfigService.getVanityJsonfilePath(domainName);
        if (null != adminService && StringUtils.isNotBlank(vanityJsonfilePath)){
            try (final ResourceResolver readServiceResourceResolver = adminService.getReadService()) {
                final String vanityDomainString = CommonUtil.getResponseStringFromFile(readServiceResourceResolver, vanityJsonfilePath);
                if (StringUtils.isNotBlank(vanityDomainString)) {
                    final JSONObject vanityDomainObject = new JSONObject(vanityDomainString).getJSONObject(domainName);
                    final JSONObject vanityObject = vanityDomainObject != null && vanityDomainObject.has(candidateVanity) ? vanityDomainObject.getJSONObject(candidateVanity): null;
                    if( vanityObject != null && !vanityObject.getBoolean(CommonConstants.DISABLED) ){
                        return vanityObject;
                    }
                }
            } catch (IOException e) {
                LOGGER.error("Error in reading file", e);
            } catch (RepositoryException e) {
                LOGGER.error("Repository Exception", e);
            } catch (JSONException e) {
                LOGGER.error("JSONException :", e);
            }

            LOGGER.debug(" VanityDataStoreImpl :: getDomainSpecificVanities() :: Exit");

        }
        LOGGER.debug(" VanityDataStoreImpl :: getDomainSpecificVanities() :: Exit");
        return null;
    }

    @Override
    public Set<String> getUniqueVanityUrlSetFromAllDomains() throws JSONException {
        LOGGER.debug(" VanityDataStoreImpl :: getUniqueVanityUrlSetFromAllDomains() :: Started");
        final Set<String> vanityUrlSet = new TreeSet<>();
        final JSONArray vanityObjArr = getAllVanities();
        for (int i = 0; i < vanityObjArr.length(); i++) {
            final JSONObject vanityJsonObject = vanityObjArr.getJSONObject(i);
            final Iterator<String> keyList = vanityJsonObject.keys();
            while (keyList.hasNext()) {
                final String keyValue = keyList.next();
                final JSONObject domainJsonObject = vanityJsonObject.getJSONObject(keyValue);
                final Iterator<String> childKeyList = domainJsonObject.keys();
                while (childKeyList.hasNext()) {
                    vanityUrlSet.add(childKeyList.next());
                }
            }
        }
        LOGGER.debug(" VanityDataStoreImpl :: getUniqueVanityUrlSetFromAllDomains() :: Exit");
        return vanityUrlSet;
    }

    @Override
    public JSONObject disableRequestedVanities(String[] vanityUrlList, String jsonFileName, Timestamp currentDate) {
        LOGGER.debug(" VanityDataStoreImpl :: disableRequestedVanities() :: Started");
        final JSONObject vanityJsonObject = getDomainSpecificVanities(jsonFileName);
        final List<String> vanityUrls = CommonUtil.getListFromStringArray(vanityUrlList);
        JSONObject resVanityJsonObj = new JSONObject();
        try {
            if(vanityJsonObject != null) {
                resVanityJsonObj = vanityJsonObject.getJSONObject(jsonFileName);
                for (String vanity : vanityUrls) {
                    final JSONObject reqVanityJsonObj = vanityJsonObject.getJSONObject(jsonFileName).getJSONObject(vanity);
                    if (reqVanityJsonObj != null) {
                        reqVanityJsonObj.put(CommonConstants.DISABLED, true);
                        reqVanityJsonObj.put(CommonConstants.LAST_MODIFIED_DATE, currentDate);
                        reqVanityJsonObj.put(CommonConstants.STATUS, VanityStatus.DISABLED_UNPUBLISHED_STATUS.getVanityStatus());
                        resVanityJsonObj.put(vanity, reqVanityJsonObj);
                    }
                }
            }
        } catch (JSONException e) {
            LOGGER.error("VanityDataStoreImpl :: disableRequestedVanities() :: Error - JSONException{}", e.getMessage());
        }
        LOGGER.debug(" VanityDataStoreImpl :: disableRequestedVanities() :: Exit");
        return resVanityJsonObj;
    }

    @Override
    public JSONObject deleteRequestedVanities(String[] vanityUrlList, String domainName, Timestamp currentDate) {
        LOGGER.debug(" VanityDataStoreImpl :: deleteRequestedVanities() :: Started");
        final JSONObject vanityJsonObject = getDomainSpecificVanities(domainName);
        final List<String> vanityUrls = CommonUtil.getListFromStringArray(vanityUrlList);
        JSONObject resJsonObject =  new JSONObject();
        try {
            if(vanityJsonObject != null) {
                final JSONObject parentJson = vanityJsonObject.getJSONObject(domainName);
                for (String vanity : vanityUrls) {
                    final JSONObject reqVanityJsonObj = vanityJsonObject.getJSONObject(domainName).getJSONObject(vanity);
                    if(reqVanityJsonObj != null) {
                        parentJson.remove(vanity);
                    }
                }
                resJsonObject = parentJson;
            }
        } catch (JSONException e) {
            LOGGER.error("VanityDataStoreImpl :: deleteRequestedVanities() :: Json exception{}", e.getMessage());
        }
        LOGGER.debug(" VanityDataStoreImpl :: deleteRequestedVanities() :: Exit");
        return resJsonObject;
    }


    /**
     * This method is used to create new vanity entry
     *
     */
    @Override
    public JSONObject createNewVanity(String jsonFileName, SlingHttpServletRequest request, String[] vanityList, JSONObject vanityJsonObject, String groupId) {
        LOGGER.debug(" VanityDataStoreImpl :: createNewVanity() :: Started");
        final String[] vanityUrlList = vanityList != null ? vanityList : request.getParameterValues(CommonConstants.VANITY_URL_LIST);
        final String defaultPublishPage = request.getParameter(CommonConstants.DEF_PUBLISH_PAGE);
        final String title = request.getParameter(CommonConstants.TITLE);
        final String notes = request.getParameter(CommonConstants.NOTES);
        final String[] addPublishPage = request.getParameterValues(CommonConstants.ADD_PUBLISH_PAGE);

        boolean duplicateFlag = false;
        try {
            final JSONObject jsonObject = getDomainSpecificVanities(jsonFileName);
            final JSONObject vanityJson = jsonObject.getJSONObject(jsonFileName);
            int objCount = 0;
            final String uuid = UUID.randomUUID().toString();
            String uniqueGroupId = StringUtils.isNotBlank(groupId) ? groupId : uuid;
            for (String vanityUrl : vanityUrlList) {
                String vanityKey;

                /* Empty Vanity Check */
                if(vanityUrl.isEmpty() || StringUtils.isBlank(vanityUrl)) {
                    continue;
                }
                /* Duplicate Vanity Check*/
                if(!vanityUrl.startsWith(CommonConstants.SLASH_STRING)) {
                    vanityKey = CommonConstants.SLASH_STRING.concat(vanityUrl.toLowerCase(Locale.ENGLISH));
                } else {
                    vanityKey = vanityUrl.toLowerCase(Locale.ENGLISH);
                }
                if (vanityJson != null) {
                    duplicateFlag = vanityJson.has(vanityKey);
                    final VanityUrlBean vanityUrlBean = new VanityUrlBean();
                    vanityUrlBean.setDefaultPage(defaultPublishPage);
                    vanityUrlBean.setAdditionalPage(addPublishPage);
                    vanityUrlBean.setTitle(title);
                    vanityUrlBean.setNotes(notes);
                    vanityUrlBean.setDisabled(false);
                    vanityUrlBean.setCreatedDate(CommonUtil.getCurrentTime());
                    vanityUrlBean.setLastModifiedDate(CommonUtil.getCurrentTime());
                    vanityUrlBean.setStatus(VanityStatus.NOT_ACTIVE_STATUS.getVanityStatus());
                    vanityUrlBean.setPrimaryVanity(false);
                    vanityUrlBean.setGroupId(uniqueGroupId);
                    if (objCount == 0 && StringUtils.isBlank(groupId)) {
                        vanityUrlBean.setPrimaryVanity(true);
                    }
                    final JSONObject beanJsonObject = new JSONObject(vanityUrlBean);
                    vanityJson.put(vanityKey,beanJsonObject);
                    objCount++;
                }
            }
            if(!duplicateFlag) {
                LOGGER.debug(" VanityDataStoreImpl :: createNewVanity() :: Exit");
                return vanityJson;
            }

        } catch (JSONException e) {
            LOGGER.error("VanityDataStoreImpl :: createNewVanity() :: Error occurred", e);
        }
        LOGGER.debug(" VanityDataStoreImpl :: createNewVanity() :: Exit");
        return null;
    }

    @Override
    public JSONObject updateRequestedVanity(SlingHttpServletRequest request, String jsonFileName, Timestamp currentTime, JSONObject vanityJsonObject) {
        LOGGER.debug(" VanityDataStoreImpl :: updateRequestedVanity() :: Started");
        final String[] vanityUrlList = request.getParameterValues(CommonConstants.VANITY_URL_LIST);
        final String defaultPublishPage = StringUtils.isNotBlank(request.getParameter(CommonConstants.DEF_PUBLISH_PAGE)) ? request.getParameter(CommonConstants.DEF_PUBLISH_PAGE): StringUtils.EMPTY ;
        final String title =  StringUtils.isNotBlank(request.getParameter(CommonConstants.TITLE)) ? request.getParameter(CommonConstants.TITLE): StringUtils.EMPTY ;
        final String notes =  StringUtils.isNotBlank(request.getParameter(CommonConstants.NOTES)) ? request.getParameter(CommonConstants.NOTES): StringUtils.EMPTY ;
        final String[] addPublishPage = request.getParameterValues(CommonConstants.ADD_PUBLISH_PAGE) != null ?
                request.getParameterValues(CommonConstants.ADD_PUBLISH_PAGE) :new String[0] ;
        final List<String> vanityUrls = CommonUtil.getListFromStringArray(vanityUrlList);
        final String currentGrpId = StringUtils.isNotBlank(request.getParameter(CommonConstants.GROUP_ID)) ? request.getParameter(CommonConstants.GROUP_ID) : StringUtils.EMPTY;
        JSONObject resVanityJsonObj = new JSONObject();
        List<String> existingVanity = new ArrayList<>();
        try {
            resVanityJsonObj = vanityJsonObject.getJSONObject(jsonFileName);
            Iterator<String> keys = resVanityJsonObj.keys();
            while(keys.hasNext()) {
                String key = keys.next();
                if (resVanityJsonObj.get(key) instanceof JSONObject
                        && resVanityJsonObj.getJSONObject(key).getString(CommonConstants.GROUP_ID).equals(currentGrpId)) {
                    existingVanity.add(key);
                }
            }
            List<String> deleteVanityList = new ArrayList<>(existingVanity);
            deleteVanityList.removeAll(vanityUrls);
            List<String> createNewVanityList = new ArrayList<>(vanityUrls);
            createNewVanityList.removeAll(existingVanity);

            String createNewVanityArr[]=createNewVanityList.toArray(new String[createNewVanityList.size()]);
            if(createNewVanityArr.length > 0 && null != createNewVanity(jsonFileName, request, createNewVanityArr, resVanityJsonObj, currentGrpId)) {
                resVanityJsonObj = createNewVanity(jsonFileName, request, createNewVanityArr, resVanityJsonObj, currentGrpId);
            }

            if(!deleteVanityList.isEmpty()) {
                for (String vanity : deleteVanityList) {
                    final JSONObject reqVanityJsonObj = resVanityJsonObj.getJSONObject(vanity);
                    if(reqVanityJsonObj != null) {
                        resVanityJsonObj.remove(vanity);
                    }
                }
            }

            int objCount = 0;
            for (String vanity : vanityUrls) {
                boolean primaryVanity = objCount == 0 ? true : false;
                if(vanityJsonObject.getJSONObject(jsonFileName).has(vanity)) {
                    final JSONObject reqVanityJsonObj = vanityJsonObject.getJSONObject(jsonFileName).getJSONObject(vanity);
                    if (reqVanityJsonObj != null) {
                        reqVanityJsonObj.put(CommonConstants.TITLE, title);
                        reqVanityJsonObj.put(CommonConstants.NOTES, notes);
                        reqVanityJsonObj.put(CommonConstants.ADDITIONAL_PAGE_PATH, addPublishPage);
                        reqVanityJsonObj.put(CommonConstants.DEFAULT_PAGE_PATH, defaultPublishPage);
                        reqVanityJsonObj.put(CommonConstants.LAST_MODIFIED_DATE, currentTime);
                        reqVanityJsonObj.put(CommonConstants.STATUS, setVanityStatus(reqVanityJsonObj.getString(CommonConstants.STATUS)));
                        reqVanityJsonObj.put(CommonConstants.DISABLED, false);
                        reqVanityJsonObj.put(CommonConstants.PRIMARY_VANITY, primaryVanity);
                        objCount++;
                        resVanityJsonObj.put(vanity, reqVanityJsonObj);
                    }
                }
                resVanityJsonObj.getJSONObject(vanity).put(CommonConstants.PRIMARY_VANITY, primaryVanity);
                objCount++;
            }

        } catch (JSONException e) {
            LOGGER.error("VanityDataStoreImpl :: updateRequestedVanity() :: Json exception{}", e.getMessage());
        }
        LOGGER.debug(" VanityDataStoreImpl :: updateRequestedVanity() :: Exit");
        return resVanityJsonObj;
    }


    private static String setVanityStatus(String currentStatus){
        String changedStatus;
        if(VanityStatus.DISABLED_STATUS.getVanityStatus().equals(currentStatus) || VanityStatus.DISABLED_UNPUBLISHED_STATUS.getVanityStatus().equals(currentStatus)){
            changedStatus = VanityStatus.NOT_ACTIVE_STATUS.getVanityStatus();
        } else if(VanityStatus.ACTIVE.getVanityStatus().equals(currentStatus)){
            changedStatus = VanityStatus.ACTIVE_UNPUBLISHED_STATUS.getVanityStatus();
        } else {
            changedStatus = currentStatus;
        }
        return changedStatus;
    }

    @Override
    public int getUnPublishChangesCount(String domainName , String operation){
        LOGGER.debug(" VanityDataStoreImpl :: getUnPublishChangesCount() :: Started");
        final JSONObject jsonObject = getDomainSpecificVanities(domainName);
        int unPublishCount = 0;
        if("DELETE".equalsIgnoreCase(operation)){
            unPublishCount = 1;
        }
        try {
            if (jsonObject != null) {
                final JSONObject vanityJson = jsonObject.getJSONObject(domainName);
                final Iterator<String> keys = vanityJson.keys();
                while (keys.hasNext()) {
                    final String key = keys.next();
                    final boolean isPrimaryVanity = vanityJson.get(key) instanceof JSONObject ? vanityJson.getJSONObject(key).getBoolean(CommonConstants.PRIMARY_VANITY) : false;
                    final String currentStatus = vanityJson.get(key) instanceof JSONObject ? vanityJson.getJSONObject(key).getString(CommonConstants.STATUS) : StringUtils.EMPTY;
                    if ((!VanityStatus.ACTIVE.getVanityStatus().equals(currentStatus)) && !(VanityStatus.DISABLED_STATUS.getVanityStatus().equals(currentStatus)) && isPrimaryVanity) {
                        unPublishCount++;
                    }
                }
            }
        } catch (JSONException e) {
            LOGGER.error("VanityDataStoreImpl :: getUnPublishChangesCount() :: Json exception{}", e.getMessage());
        }
        LOGGER.debug(" VanityDataStoreImpl :: getUnPublishChangesCount() :: Exit");
        return unPublishCount;
    }

    private static String removeSiteRootPath(String vanityRequestPath) {
        final String rootPath = CommonConstants.siteRootPathConfig.stream().filter(vanityRequestPath::startsWith).findFirst().map(Object::toString).orElse("");
        return vanityRequestPath.replace(StringUtils.chop(rootPath), "");
    }
}
