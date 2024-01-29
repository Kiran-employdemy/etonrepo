package com.eaton.platform.core.services.secure.impl;

import com.eaton.platform.core.bean.secure.SecureAssetsBean;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.secure.WhatsNewService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.auth.constants.SecureConstants;
import com.eaton.platform.integration.auth.models.AuthenticationToken;
import com.eaton.platform.integration.auth.services.AuthenticationService;
import com.eaton.platform.integration.auth.services.AuthenticationServiceConfiguration;
import com.eaton.platform.integration.auth.services.AuthorizationService;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.constants.EndecaConstants;
import com.eaton.platform.integration.endeca.services.EndecaAdvancedSearchService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.Date;

import java.util.regex.Pattern;


/**
 * Class WhatsNewServiceImpl.
 *
 */
@Component(service = WhatsNewService.class, immediate = true)
public class WhatsNewServiceImpl implements WhatsNewService {

    private static final String PROP_PATH = "path";
    private static final String PROPERTY = "property";
    private static final String PROPERTY_VALUE = "_value";
    private static final String DATE_FORMAT_PUBLISH = "dd/MM/yyyy HH:mm:ss";
    private static final Pattern pattern = Pattern.compile(SecureConstants.JCR_CONTENT_PAGE);
    private static final Logger LOG = LoggerFactory.getLogger(WhatsNewServiceImpl.class);

    @Reference
    private AuthenticationService authenticationService;

    @Reference
    private AuthorizationService authorizationService;

    @Reference
    private EndecaAdvancedSearchService endecaAdvancedSearchService;

    @Reference
    private AuthenticationServiceConfiguration authenticationServiceConfig;


    @Override
    public JSONArray getLatestAssetsFromEndecaByRange(final SlingHttpServletRequest request, final String userId, Date startDate, Date endDate) {
        LOG.info("****** Inside getAssetsOnPublicationDate method of Whats new Service *********");
        AuthenticationToken authenticationToken = authorizationService.getTokenFromSlingRequest(request);
        LOG.debug("******** getAssetsOnPublicationDate : LDAP ID  ******* {}",authenticationToken.getUserLDAPId());
        EndecaServiceRequestBean endecaServiceRequestBean =  endecaAdvancedSearchService.constructWhatsNewEndecaRequestBean(request,  startDate.getTime(), endDate.getTime());
        JSONObject responseObject = endecaAdvancedSearchService.getAdvanceSearchResults(endecaServiceRequestBean,request);
        if(responseObject != null && responseObject.has(EndecaConstants.SITE_SEARCH_RESULTS_STRING)){

            try {
                return responseObject.getJSONArray(EndecaConstants.SITE_SEARCH_RESULTS_STRING);
            } catch (JSONException e) {
                LOG.error(" ******** Exception sWhile getting Search Results From ENDECA ********", e);
            }
        }
        return new JSONArray();
    }

    private static Map<String, String> setResourceTagsParams(ValueMap pagePropertiesValueMap, Map<String, String> queryParams){
        final String[] resourceTagsArray = CommonUtil.getStringArrayProperty(pagePropertiesValueMap, CommonConstants.PRIMARY_SUB_CATEGORY_TAG);
        if(resourceTagsArray.length > 0) {
            queryParams.put(PROPERTY, SecureConstants.JCR_CONTENT_CQ_TAGS_METADATA);
            for (int count = 0; count < resourceTagsArray.length; count++) {
                queryParams.put(PROPERTY + "." + (count + 1) + PROPERTY_VALUE, resourceTagsArray[count]);
            }
        }
        return queryParams;
    }


    @Override
    public void getAssetsFromHits(final JSONArray jsonArray, final List<SecureAssetsBean> secureSelectedAssets) {
        LOG.info("****** Inside getAssetsFromHits method of Whats new Service *********");
        for (int i=0 ; i<jsonArray.length() ; i++) {
            try {
                JSONObject item = jsonArray.getJSONObject(i);

                if (null != item && item.has(EndecaConstants.SECURE_STRING_LOWER_CASE) && item.has(EndecaConstants.TITLE)
                        && item.has(EndecaConstants.FILE_TYPE_AND_SIZE)) {
                    SecureAssetsBean secureSelectedSortedAssetModel = new SecureAssetsBean();
                    secureSelectedSortedAssetModel.setSecureAsset( item.getBoolean(EndecaConstants.SECURE_STRING_LOWER_CASE));
                    secureSelectedSortedAssetModel.setAssetTitle(item.getString(EndecaConstants.TITLE));
                    secureSelectedSortedAssetModel.setAssetSize(item.getString(EndecaConstants.FILE_TYPE_AND_SIZE));
                    secureSelectedSortedAssetModel.setAssetPublicationDate(item.getString(EndecaConstants.PUBLISH_DATE_CAMEL_STRING));
                    if(item.has(EndecaConstants.DESCRIPTION_STRING_LOWER)){
                        secureSelectedSortedAssetModel.setAssetDescription(item.getString(EndecaConstants.DESCRIPTION_STRING_LOWER));
                    }
                    secureSelectedSortedAssetModel.setAssetPath(item.getString(EndecaConstants.URL));
                    secureSelectedAssets.add(secureSelectedSortedAssetModel);
                }
            } catch (JSONException e) {
                LOG.error("Exception While Constructing What's new model object ", e);
            }
        }
    }

}
