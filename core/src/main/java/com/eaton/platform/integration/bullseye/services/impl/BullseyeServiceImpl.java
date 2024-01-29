package com.eaton.platform.integration.bullseye.services.impl;

import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.CloudConfigService;
import com.eaton.platform.integration.bullseye.constants.BullseyeConstant;
import com.eaton.platform.integration.bullseye.models.BullseyeConfigModel;
import com.eaton.platform.integration.bullseye.services.BullseyeCache;
import com.eaton.platform.integration.bullseye.services.BullseyeCacheBroker;
import com.eaton.platform.integration.bullseye.services.BullseyeService;
import com.eaton.platform.integration.bullseye.services.config.BullseyeServiceConfig;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.apache.sling.api.resource.Resource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component(service = BullseyeService.class, immediate = true, property = {
        AEMConstants.SERVICE_DESCRIPTION + "Bullseye Service",
        AEMConstants.SERVICE_VENDOR_EATON,
        AEMConstants.PROCESS_LABEL + "BullseyeServiceImpl"
})
@Designate(ocd = BullseyeServiceConfig.class)
public class BullseyeServiceImpl implements BullseyeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BullseyeServiceImpl.class);

    @Reference
    private CloudConfigService configService;

    private static final String JSON_EXCEPTION ="JSONException while creating the csv";

    @Reference
    private HttpClientBuilderFactory httpFactory;

    @Reference
    BullseyeCacheBroker broker;

    private PoolingHttpClientConnectionManager conMgr;
    private int maxConnections;
    private int maxHostConnections;

    // TODO:: need to confirm if googleplace and mapBoxApi is ever used

    private String googlePlaceURL;
    private LoadingCache<List<NameValuePair>, String> googlePlaceCache;

    private String mapBoxAPIURL;
    private String mapBoxAPIRequestURL;
    private LoadingCache<List<NameValuePair>, String> mapBoxAPICache;

    private JSONArray categoryHeaders;

    @Activate
    @Modified
    protected final void activate(final BullseyeServiceConfig config) {
        if (null != config) {
            googlePlaceURL = config.googlePlaceURL();
            mapBoxAPIURL = config.mapBoxAPIURL();
            maxConnections = config.maxConnections();
            maxHostConnections = config.maxHostConnections();
            mapBoxAPIRequestURL = "";
            // TODO:: need to confirm if googleplace and mapBoxApi is ever used
            googlePlaceCache = Caffeine.newBuilder()
                    .maximumSize(config.googlePlaceCacheSize())
                    .expireAfterWrite(config.googlePlaceCacheDuration(), TimeUnit.SECONDS)
                    .build(params -> executeAPI(googlePlaceURL, params));

            mapBoxAPICache = Caffeine.newBuilder()
                    .maximumSize(config.mapBoxAPICacheSize())
                    .expireAfterWrite(config.mapBoxAPICacheDuration(), TimeUnit.SECONDS)
                    .build(params -> executeAPI(mapBoxAPIRequestURL, params));
        }
    }

    @Override
    public JSONObject getCategoryGroup(final Resource resource, final boolean returnAllCategories) throws Exception {
        LOGGER.debug("Start with getCategoryGroup method ::");
        JSONObject responseJson = new JSONObject();
        try {
            final Optional<BullseyeConfigModel> bullsEyeCloudConfig = configService.getBullsEyeCloudConfig(resource);

            final List<NameValuePair> params = new ArrayList<>();
            if (returnAllCategories) {
                params.add(new BasicNameValuePair(BullseyeConstant.RETURN_ALL_CATEGORIES, "true"));
            }
            if (bullsEyeCloudConfig.isPresent()) {
                setBasicNameValuePairParam(params, bullsEyeCloudConfig.get().getClientId(),
                        bullsEyeCloudConfig.get().getApiKey());
            } else {
                BullseyeConfigModel bullseyeConfigAdaptModel = resource.getChild(BullseyeConstant.JCR_CONTENT)
                        .adaptTo(BullseyeConfigModel.class);
                if (bullseyeConfigAdaptModel != null) {
                    setBasicNameValuePairParam(params, bullseyeConfigAdaptModel.getClientId(),
                            bullseyeConfigAdaptModel.getApiKey());
                }
            }

            final String response;
            String cacheKey = returnAllCategories ? CategoryCacheImpl.NAME : CategoryGroupCacheImpl.NAME;
            BullseyeCache cacheService = broker.getBullseyeCache(cacheKey);
            response = (null != cacheService) ? cacheService.getCache(params) : StringUtils.EMPTY;
            if (null != response) {
                final JSONArray jsonArray = new JSONArray(response);
                responseJson.put(BullseyeConstant.RESPONSE, jsonArray);
            }
        } catch (JSONException e) {
            LOGGER.error(BullseyeConstant.JSON_ERROR_CALLING_BULLSEYE_API, e);
            throw new JSONException(BullseyeConstant.JSON_ERROR_CALLING_BULLSEYE_API);
        }
        LOGGER.debug("End with getCategoryGroup method ::");
        return responseJson;
    }

    @Override
    public JSONObject getDoSearchResults(final Resource resource, final LinkedHashMap<String, String> requestParamMap,
            final JSONArray categories) throws Exception {
        LOGGER.debug("Start with getDoSearchResults method ::");
        JSONObject responseJson = new JSONObject();
        categoryHeaders = new JSONArray();
        try {
            final Optional<BullseyeConfigModel> bullsEyeCloudConfig = configService.getBullsEyeCloudConfig(resource);
            if (bullsEyeCloudConfig.isPresent()) {
                final List<NameValuePair> params = new ArrayList<>();
                setBasicNameValuePairParam(params, bullsEyeCloudConfig.get().getClientId(), bullsEyeCloudConfig.get().getApiKey());
                params.add(new BasicNameValuePair(BullseyeConstant.START_INDEX, "0"));
                if (!requestParamMap.containsKey(BullseyeConstant.PAGE_SIZE)) {
                    params.add(new BasicNameValuePair(BullseyeConstant.PAGE_SIZE, bullsEyeCloudConfig.get().getPageSize()));
                }
                params.add(new BasicNameValuePair(BullseyeConstant.MATCH_ALL_CATEGORIES, "true"));
                requestParamMap.forEach((key,value) -> {
                    if (StringUtils.isNotBlank(value) && !key.equals(BullseyeConstant.RADIUS)) params.add(new BasicNameValuePair(key, value));
                });
                String defaultRadius;
                String defaultDistanceUnit;
                if (!requestParamMap.containsKey(BullseyeConstant.RADIUS) && !requestParamMap.containsKey(BullseyeConstant.DISTANCE_UNIT)) {
                    defaultDistanceUnit = bullsEyeCloudConfig.get().getDefaultDistanceUnit();
                    defaultRadius = bullsEyeCloudConfig.get().getDefaultRadius();
                    getRadius(params, defaultRadius, defaultDistanceUnit);
                } else {
                    defaultDistanceUnit = requestParamMap.get(BullseyeConstant.DISTANCE_UNIT);
                    defaultRadius = requestParamMap.get(BullseyeConstant.RADIUS);
                    getRadius(params, defaultRadius, defaultDistanceUnit);
                }
                BullseyeCache cacheService = broker.getBullseyeCache(SearchCacheImpl.NAME);
                final String response = (null != cacheService) ? cacheService.getCache(params) : StringUtils.EMPTY;
                if ((null != response) && (!("").equals(response))) {
                    responseJson = new JSONObject(response);
                    if (responseJson.has(BullseyeConstant.RESULT_LIST)) {
                        final JSONArray resultList = responseJson.getJSONArray(BullseyeConstant.RESULT_LIST);
                        categoryHeaders = createCategoryLists(resultList, categories.toString(), defaultDistanceUnit);
                        LOGGER.info("facetGroupListJson :: {}" , categories);
                        responseJson.put("categoryHeaders", categoryHeaders);
                        if (resultList.length() > 0) {
                            final JSONObject firstLocation = resultList.getJSONObject(0);
                            if (firstLocation.has(BullseyeConstant.POST_CODE)) {
                                responseJson.put(BullseyeConstant.SEARCH_TERM, firstLocation.getString(BullseyeConstant.POST_CODE));
                            }
                        }
                        setCategoryIcon(responseJson, bullsEyeCloudConfig);
                    }
                    responseJson.put(BullseyeConstant.RADIUS_CONST, defaultRadius);
                    responseJson.put(BullseyeConstant.DISTANCE_UNIT, defaultDistanceUnit);
                }else{
                    responseJson.put(BullseyeConstant.RESULT_LIST, new JSONArray());
                    responseJson.put(BullseyeConstant.CATEGORY_HEADERS, new JSONArray());
                    responseJson.put(BullseyeConstant.TOTAL_RESULTS, 0);
                    responseJson.put(BullseyeConstant.RADIUS_CONST, defaultRadius);
                    responseJson.put(BullseyeConstant.DISTANCE_UNIT, defaultDistanceUnit);
                }

            }
        } catch (JSONException e) {
            LOGGER.error(BullseyeConstant.JSON_ERROR_CALLING_BULLSEYE_API, e);
        }
        LOGGER.debug("End with getDoSearchResults method ::");
        return responseJson;
    }

    private void processResultListJsonArray(JSONObject responseJson, String defaultDistanceUnit, JSONArray categories,
            Optional<BullseyeConfigModel> bullsEyeCloudConfig) {
                LOGGER.debug("Start with processResultListJsonArray method ::");
        try {
            if (responseJson.has(BullseyeConstant.RESULT_LIST)) {
                final JSONArray resultList = responseJson.getJSONArray(BullseyeConstant.RESULT_LIST);
                categoryHeaders = createCategoryLists(resultList, categories.toString(), defaultDistanceUnit);
                LOGGER.info("facetGroupListJson :: {}", categories);
                responseJson.put("categoryHeaders", categoryHeaders);
                processFirstLocation(resultList, responseJson);
                setCategoryIcon(responseJson, bullsEyeCloudConfig);
            }
        } catch (JSONException e) {
            LOGGER.error(BullseyeConstant.JSON_ERROR_CALLING_BULLSEYE_API, e);
        }
        LOGGER.debug("End with processResultListJsonArray method ::");
    }

    private void processFirstLocation(JSONArray resultList, JSONObject responseJson) {
        LOGGER.debug("Start with processFirstLocation method ::");
        try {
            if (resultList.length() > 0) {
                final JSONObject firstLocation = resultList.getJSONObject(0);
                if (firstLocation.has(BullseyeConstant.POST_CODE)) {
                    responseJson.put(BullseyeConstant.SEARCH_TERM,
                            firstLocation.getString(BullseyeConstant.POST_CODE));
                }
            }
        } catch (JSONException e) {
            LOGGER.error(BullseyeConstant.JSON_ERROR_CALLING_BULLSEYE_API, e);
        }
        LOGGER.debug("End with processFirstLocation method ::");
    }

    private void setBasicNameValuePairParam(List<NameValuePair> params, String client_id, String API_key) {
        params.add(new BasicNameValuePair(BullseyeConstant.CLIENT_ID, client_id));
        params.add(new BasicNameValuePair(BullseyeConstant.API_KEY, API_key));
    }

    private void getRadius(List<NameValuePair> params, String defaultRadius, String defaultDistanceUnit) {
        LOGGER.debug("Start with getRadius method ::");
        if (defaultDistanceUnit.equals(BullseyeConstant.KILOMETERS)) {
            Long radius = Math.round(Integer.parseInt(defaultRadius) / 1.609);
            params.add(new BasicNameValuePair(BullseyeConstant.RADIUS, radius.toString()));
        } else {
            params.add(new BasicNameValuePair(BullseyeConstant.RADIUS, defaultRadius));
        }
        LOGGER.debug("End with getRadius method ::");
    }

    private JSONArray createCategoryLists(final JSONArray resultList, final String facetGroupList,
            final String distanceUnit) {
                LOGGER.debug("Start createCategoryLists method ::");
        try {
            if (StringUtils.isNotEmpty(facetGroupList) && resultList.length() > 0) {
                final JSONArray facetGroupListJson = new JSONArray(facetGroupList);
                LOGGER.info("facetGroupListJson :: {} ", facetGroupListJson);
                final JSONObject categoryJson = new JSONObject();
                for (int resultCount = 0; resultCount < resultList.length(); resultCount++) {
                    final JSONObject categoryArrayJson = new JSONObject();
                    final JSONObject result = resultList.getJSONObject(resultCount);
                    
                    if (result.has(BullseyeConstant.DISTANCE) && distanceUnit.equals(BullseyeConstant.KILOMETERS)
                            && null != result.get(BullseyeConstant.DISTANCE)) {
                        final String distance = convertMilesToKilometer(result.getDouble(BullseyeConstant.DISTANCE));
                        result.put(BullseyeConstant.DISTANCE, distance);
                    }
                    
                    if (result.has(BullseyeConstant.CATEGORY_IDS)) {
                        final String categoryIds = result.getString(BullseyeConstant.CATEGORY_IDS);
                        final List<String> categoryList = Arrays.asList(categoryIds.split(","));
                        categoryList.forEach(category -> {
                            for (int filterCount = 0; filterCount < facetGroupListJson.length(); filterCount++) {

                                try {
                                    final JSONObject facet = facetGroupListJson.getJSONObject(filterCount);
                                    final String facetTitle = facet.getString(BullseyeConstant.TITLE);
                                    final JSONArray values = facet.getJSONArray(BullseyeConstant.VALUES);
                                    for (int facetCount = 0; facetCount < values.length(); facetCount++) {
                                        final JSONObject facetNode = values.getJSONObject(facetCount);
                                        final String facetID = facetNode.getString(BullseyeConstant.ID_CONST);
                                        final String title = facetNode.getString(BullseyeConstant.TITLE);
                                        if (facetID.equals(category)) {
                                            if (categoryArrayJson.has(facetTitle) && categoryArrayJson.length() > 0) {
                                                categoryArrayJson.getJSONArray(facetTitle).put(title);
                                                categoryJson.getJSONArray(facetTitle).put(title);
                                            } else {
                                                categoryJson.put(facetTitle, new JSONArray().put(title));
                                                categoryArrayJson.put(facetTitle, new JSONArray().put(title));
                                                categoryHeaders.put(facetTitle);
                                            }
                                        }
                                    }
                                } catch (JSONException e) {
                                    LOGGER.error("JSON exception", e);
                                }
                                
                            }
                        });
                    }
                    result.put(BullseyeConstant.CATEGORY_GROUPS_ARRAY, categoryArrayJson);
                }
            }
        } catch (JSONException e) {
            LOGGER.error("Exception while constructing the product and services", e);
        }
        LOGGER.debug("End with createCategoryLists method ::");
        return categoryHeaders;
        
    }

    /**
     * Calls Bullseye API getCategories endpoint which returns full list of
     * categories for a given client
     * Ex. categoryGroupID of 54 will return list of all Product Line categories
     * (Airflex, Char-Lynn.. etc)
     * 
     * @param resource
     * @param categoryGroupID category group to return categories for
     * @param language        Bullseye languageID of the language that results
     *                        should be returned in
     * @return JSON object containing categories within given category group
     * @throws Exception
     */
    @Override
    public JSONObject getCategory(final Resource resource, final String categoryGroupID, Locale language)
            throws Exception {
        LOGGER.debug("Start with getCategory method ::");
        JSONObject responseJson = new JSONObject();
        try {
            final Optional<BullseyeConfigModel> bullsEyeCloudConfig = configService.getBullsEyeCloudConfig(resource);
            final List<NameValuePair> params = new ArrayList<>();
            if (bullsEyeCloudConfig.isPresent()) {
                setBasicNameValuePairParam(params, bullsEyeCloudConfig.get().getClientId(),
                        bullsEyeCloudConfig.get().getApiKey());
            } else {
                BullseyeConfigModel bullseyeConfigAdaptModel = resource.getChild(BullseyeConstant.JCR_CONTENT)
                        .adaptTo(BullseyeConfigModel.class);
                if (bullseyeConfigAdaptModel != null) {
                    setBasicNameValuePairParam(params, bullseyeConfigAdaptModel.getClientId(),
                            bullseyeConfigAdaptModel.getApiKey());
                }
            }

            params.add(new BasicNameValuePair(BullseyeConstant.CATEGORY_GROUP_ID, categoryGroupID));
            if (null != language && StringUtils.isNotBlank(language.getLanguage())) {
                params.add(new BasicNameValuePair(BullseyeConstant.LANGUAGE_CODE, language.getLanguage()));
            }
            BullseyeCache cacheService = broker.getBullseyeCache(CategoryCacheImpl.NAME);
            final String response = (null != cacheService) ? cacheService.getCache(params) : StringUtils.EMPTY;
            if (StringUtils.isNotBlank(response)) {
                final JSONArray jsonArray = new JSONArray(response);
                responseJson.put(BullseyeConstant.RESPONSE, jsonArray);
            }
        } catch (JSONException e) {
            LOGGER.error(BullseyeConstant.JSON_ERROR_CALLING_BULLSEYE_API, e);
            throw new JSONException(BullseyeConstant.JSON_ERROR_CALLING_BULLSEYE_API);
        }
        LOGGER.debug("End with getCategory method ::");
        return responseJson;
    }

    @Override
    public JSONObject getLatLongService(final Resource resource, final String keyword) throws Exception {
        LOGGER.debug("Start with getLatLongService method ::");
        JSONObject responseJson = new JSONObject();
        try {
            final Optional<BullseyeConfigModel> bullsEyeCloudConfig = configService.getBullsEyeCloudConfig(resource);
            if (bullsEyeCloudConfig.isPresent()) {
                if (bullsEyeCloudConfig.get().getMappingVendor().equals(BullseyeConstant.MAPBOX)) {
                    responseJson = getLatLongMapBoxService(resource, keyword);
                } else {
                    responseJson = getLatLongGoogleService(resource, keyword);
                }
            }
        } catch (JSONException e) {
            LOGGER.error("Exception while calling the api", e);
            throw new JSONException("Exception while calling the api");
        }
        LOGGER.debug("End with getLatLongService method ::");
        return responseJson;
    }

    @Override
    public JSONObject getLatLongGoogleService(final Resource resource, final String keyword) throws Exception {
        LOGGER.debug("Start with getLatLongGoogleService method ::");
        JSONObject responseJson = new JSONObject();
        try {
            final Optional<BullseyeConfigModel> bullsEyeCloudConfig = configService.getBullsEyeCloudConfig(resource);
            if (bullsEyeCloudConfig.isPresent()) {
                final List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair(BullseyeConstant.INPUT_TYPE, BullseyeConstant.INPUT_TYPE_VALUE));
                params.add(new BasicNameValuePair(BullseyeConstant.FIELDS, BullseyeConstant.FIELDS_VALUE));
                params.add(new BasicNameValuePair(BullseyeConstant.GOOGLE_API_KEY,
                        bullsEyeCloudConfig.get().getMappingApiKey()));
                params.add(new BasicNameValuePair(BullseyeConstant.INPUT, keyword));
                if (StringUtils.isNotBlank(googlePlaceURL)) {
                    final String response = googlePlaceCache.get(params);
                    if (null != response) {
                        final JSONObject responseData = new JSONObject(response);
                        responseJson = fetchLocation(responseData);
                    }
                } else {
                    LOGGER.error(
                            "Please configure the service url in bullseye cloudconfig for getLatLongGoogleService.");
                }
            }
        } catch (JSONException e) {
            LOGGER.error(BullseyeConstant.JSON_ERROR_CALLING_BULLSEYE_API, e);
            throw new JSONException(BullseyeConstant.JSON_ERROR_CALLING_BULLSEYE_API);
        }
        LOGGER.debug("End with getLatLongGoogleService method ::");
        return responseJson;
    }

    @Override
    public JSONObject getLatLongMapBoxService(final Resource resource, final String keyword) throws Exception {
        LOGGER.debug("Start with getLatLongMapBoxService method ::");
        JSONObject responseJson = new JSONObject();
        try {
            final Optional<BullseyeConfigModel> bullsEyeCloudConfig = configService.getBullsEyeCloudConfig(resource);
            if (bullsEyeCloudConfig.isPresent()) {
                final List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair(BullseyeConstant.MAPBOX_API_KEY,
                        bullsEyeCloudConfig.get().getMappingApiKey()));
                if (StringUtils.isNotBlank(mapBoxAPIURL)) {
                    mapBoxAPIRequestURL = mapBoxAPIURL + URLEncoder.encode(keyword, StandardCharsets.UTF_8.toString())
                            + BullseyeConstant.JSON_EXTENSION;
                    final String response = mapBoxAPICache.get(params);
                    if (null != response) {
                        final JSONObject responseData = new JSONObject(response);
                        responseJson = fetchMapBoxLocation(responseData);
                    }
                } else {
                    LOGGER.error(
                            "Please configure the service url in bullseye cloudconfig for getLatLongMapBoxService.");
                }
            }
        } catch (JSONException e) {
            LOGGER.error("JSONException while calling the api", e);
            throw new JSONException("JSONException while calling the api");
        }
        LOGGER.debug("End with getLatLongMapBoxService method ::");
        return responseJson;
    }

    @Override
    public CSVPrinter getStoreCSV(final JSONObject bullEyeSearchResponse, final PrintWriter outputStream,
                                  final String distanceUnit, final ArrayList<String> csvHeaderLabel, final String classLabel) {
        CSVPrinter csvPrinter = null;
        try {
            String []csvHeaders = csvHeaderLabel.toArray(new String[0]);
            List<String> columnName = new ArrayList<String>();
            if (bullEyeSearchResponse.has(BullseyeConstant.CATEGORY_HEADERS)) {
                categoryHeaders = bullEyeSearchResponse.getJSONArray(BullseyeConstant.CATEGORY_HEADERS);
                for (int categoryHeaderCount = 0; categoryHeaderCount < categoryHeaders.length(); categoryHeaderCount++) {
                    if (! columnName.contains(categoryHeaders.getString(categoryHeaderCount))) {
                        columnName.add(categoryHeaders.getString(categoryHeaderCount));
                    }
                }
            }
            if(columnName.size() != 0) {
                csvHeaders = ArrayUtils.addAll(csvHeaders, columnName.toArray(new String[0]));
            }
            csvHeaders = ArrayUtils.add(csvHeaders, classLabel);
            csvPrinter = new CSVPrinter(outputStream, CSVFormat.DEFAULT
                    .withHeader(csvHeaders));
            if(null != bullEyeSearchResponse && bullEyeSearchResponse.has(BullseyeConstant.RESULT_LIST)) {
                final JSONArray resultList = bullEyeSearchResponse.getJSONArray(BullseyeConstant.RESULT_LIST);
                if (resultList.length() > 0) {
                    for (int i = 0; i < resultList.length(); i++) {
                        final JSONObject resultJSONObject = resultList.getJSONObject(i);
                        List<String> rowDataList = new ArrayList<>();
                        rowDataList.add(getNodeValue(BullseyeConstant.NAME, resultJSONObject));
                        rowDataList.add(getNodeValue(BullseyeConstant.ADDRESS_1, resultJSONObject));
                        rowDataList.add(getNodeValue(BullseyeConstant.ADDRESS_2, resultJSONObject));
                        rowDataList.add(getNodeValue(BullseyeConstant.ADDRESS_3, resultJSONObject));
                        rowDataList.add(getNodeValue(BullseyeConstant.CITY, resultJSONObject));
                        rowDataList.add(getNodeValue(BullseyeConstant.STATE, resultJSONObject));
                        rowDataList.add(getNodeValue(BullseyeConstant.POST_CODE, resultJSONObject));
                        rowDataList.add(getNodeValue(BullseyeConstant.COUNTRY_CODE, resultJSONObject));
                        rowDataList.add(getNodeValue(BullseyeConstant.PHONE_NUMBER, resultJSONObject));
                        rowDataList.add(getNodeValue(BullseyeConstant.FAX_NUMBER, resultJSONObject));
                        rowDataList.add(StringUtils.EMPTY);
                        rowDataList.add(getNodeValue(BullseyeConstant.DISTANCE, resultJSONObject)
                                .concat(" ")
                                .concat(distanceUnit));
                        rowDataList.add(getNodeValue(BullseyeConstant.EMAIL_ADDRESS, resultJSONObject));
                        rowDataList.add(getNodeValue(BullseyeConstant.URL, resultJSONObject));
                        for (String categoryGroupHeader : columnName) {
                            if (resultJSONObject.has(BullseyeConstant.CATEGORY_GROUPS_ARRAY) && resultJSONObject.getJSONObject(BullseyeConstant.CATEGORY_GROUPS_ARRAY).has(categoryGroupHeader)) {
                                rowDataList.add(resultJSONObject.getJSONObject(BullseyeConstant.CATEGORY_GROUPS_ARRAY).getJSONArray(categoryGroupHeader).join(", "));
                            } else {
                                rowDataList.add(StringUtils.EMPTY);
                            }
                        }
                        rowDataList.add(getNodeValue(BullseyeConstant.LOCATION_TYPE_NAME, resultJSONObject));
                        csvPrinter.printRecord(rowDataList);

                    }
                }
            }
        } catch (JSONException e) {
            LOGGER.error("JSONException while creating the csv", e);
        } catch (IOException e) {
            LOGGER.error("IO Exception while creating the csv", e);
        }
        return csvPrinter;
    }

    private void processCategoryGroupHeader(List<String> columnName, JSONObject resultJSONObject,
            List<String> rowDataList) {
                LOGGER.debug("Start with processCategoryGroupHeader method ::");
        try {
            for (String categoryGroupHeader : columnName) {
                if (resultJSONObject.has(BullseyeConstant.CATEGORY_GROUPS_ARRAY) && resultJSONObject
                        .getJSONObject(BullseyeConstant.CATEGORY_GROUPS_ARRAY).has(categoryGroupHeader)) {
                    rowDataList.add(resultJSONObject.getJSONObject(BullseyeConstant.CATEGORY_GROUPS_ARRAY)
                            .getJSONArray(categoryGroupHeader).join(CommonConstants.COMMA));
                } else {
                    rowDataList.add(StringUtils.EMPTY);
                }
            }
        } catch (JSONException e) {
            LOGGER.error(JSON_EXCEPTION, e);
        }
        LOGGER.debug("End with processCategoryGroupHeader method ::");
    }

    private String getNodeValue(final String key, final JSONObject resultObject) {
        String nodeValue = StringUtils.EMPTY;
        try {
            if (resultObject.has(key) && null != resultObject.get(key)) {
                nodeValue = !(resultObject.get(key) == null) ? resultObject.get(key).toString() : StringUtils.EMPTY;
            }
        } catch (JSONException e) {
            LOGGER.error("Exception while getting the node value", e);
        }
        return nodeValue;
    }

    private String executeAPI(final String serviceUrl, final List<NameValuePair> params)
            throws JSONException, URISyntaxException, IOException {
        LOGGER.debug("Start with executeAPI method ::");
        final StringBuilder content = new StringBuilder();
        try {
            final HttpClient client = httpFactory.newBuilder().setConnectionManager(getMultiThreadedConf()).build();
            final URIBuilder uriBuilder = new URIBuilder(serviceUrl);
            uriBuilder.addParameters(params);
            final URI build = uriBuilder.build();
            final HttpGet httpGet = new HttpGet(build);
            final HttpResponse execute = client.execute(httpGet);
            final String reasonPhrase = execute.getStatusLine().getReasonPhrase();
            if (execute.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                final BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(execute.getEntity().getContent()));
                String line;
                while (null != (line = bufferedReader.readLine())) {
                    content.append(line);
                }
                bufferedReader.close();
            }
            LOGGER.info("reasonPhrase :: {}", reasonPhrase);
        } catch (URISyntaxException e) {
            LOGGER.error("URISyntaxException while calling the api", e);
            throw new URISyntaxException("URISyntaxException while calling the api", serviceUrl);
        } catch (ClientProtocolException e) {
            LOGGER.error("ClientProtocolException while calling the api", e);
            throw new ClientProtocolException("ClientProtocolException while calling the api");
        } catch (IOException e) {
            LOGGER.error("IOException while calling the api", e);
            throw new IOException("IOException while calling the api");
        }
        LOGGER.debug("End with executeAPI method ::");
        return content.toString();
    }

    private PoolingHttpClientConnectionManager getMultiThreadedConf() {
        if (conMgr == null) {
            conMgr = new PoolingHttpClientConnectionManager();
            conMgr.setMaxTotal(maxConnections);
            conMgr.setDefaultMaxPerRoute(maxHostConnections);
        }
        return conMgr;
    }

    final JSONObject fetchLocation(final JSONObject latLongResponse) {
        LOGGER.debug("Start with fetchLocation method ::");
        JSONObject locationJson = new JSONObject();
        try {
            if (latLongResponse.has(BullseyeConstant.STATUS)
                    && latLongResponse.getString(BullseyeConstant.STATUS).equals(BullseyeConstant.OK)
                    && latLongResponse.has(BullseyeConstant.CANDIDATES)) {
                final JSONArray candidateArray = latLongResponse.getJSONArray(BullseyeConstant.CANDIDATES);
                if (candidateArray.length() > 0) {
                    final JSONObject candidateJson = candidateArray.getJSONObject(0);
                    if (null != candidateJson && candidateJson.has(BullseyeConstant.GEOMETRY)) {
                        final JSONObject geometryJson = candidateJson.getJSONObject(BullseyeConstant.GEOMETRY);
                        if (geometryJson.has(BullseyeConstant.LOCATION)) {
                            locationJson = geometryJson.getJSONObject(BullseyeConstant.LOCATION);
                        }
                    }
                }
            } else {
                locationJson.put("error", BullseyeConstant.ERROR_CALLING_GOOGLE_SERVICE);
            }
        } catch (JSONException e) {
            LOGGER.error("JSONException while calling fetchLocation method.");
        }
        LOGGER.debug("End with fetchLocation method ::");
        return locationJson;
    }

    final JSONObject fetchMapBoxLocation(final JSONObject latLongResponse) {
        LOGGER.debug("Start with fetchMapBoxLocation method ::");
        JSONObject locationJson = new JSONObject();
        JSONArray locationArray;
        try {
            if (latLongResponse.has(BullseyeConstant.FEATURES)) {
                final JSONArray featuresArray = latLongResponse.getJSONArray(BullseyeConstant.FEATURES);
                if (featuresArray.length() > 0) {
                    final JSONObject candidateJson = featuresArray.getJSONObject(0);
                    if (null != candidateJson && candidateJson.has(BullseyeConstant.GEOMETRY)) {
                        final JSONObject geometryJson = candidateJson.getJSONObject(BullseyeConstant.GEOMETRY);
                        if (geometryJson.has(BullseyeConstant.COORDINATES)) {
                            locationArray = geometryJson.getJSONArray(BullseyeConstant.COORDINATES);
                            if(locationArray.length()==BullseyeConstant.COORDINATES_LENGTH) {
                                locationJson.put(BullseyeConstant.LONGITUDE_KEY, locationArray.get(BullseyeConstant.LONGITUDE_INDEX));
                                locationJson.put(BullseyeConstant.LATITUDE_KEY, locationArray.get(BullseyeConstant.LATITUDE_INDEX));
                            }else {
                                locationJson.put(BullseyeConstant.ERROR, BullseyeConstant.INVALID_RESPONSE_MAPBOX_SERVICE);
                            }
                        }
                    }
                }
            } else {
                locationJson.put(BullseyeConstant.ERROR, BullseyeConstant.ERROR_CALLING_MAPBOX_SERVICE);
            }
        } catch (JSONException e) {
            LOGGER.error("JSONException while calling fetchMapBoxLocation method.");
        }
        LOGGER.debug("End with fetchMapBoxLocation method ::");
        return locationJson;
    }

    private String convertMilesToKilometer(double distanceInMiles) {
        final DecimalFormat decimalFormat = new DecimalFormat("#.##");
        final String distance = decimalFormat.format(distanceInMiles * 1.609);
        return distance;
    }

    private void setCategoryIcon(JSONObject searchJsonResponse, Optional<BullseyeConfigModel> bullsEyeCloudConfig ) throws JSONException {
        LOGGER.debug("Start with setCategoryIcon method ::");
        if ((bullsEyeCloudConfig.isPresent()) && (null != bullsEyeCloudConfig.get().getMarkers())) {
            final String[] markersConfig = bullsEyeCloudConfig.get().getMarkers();
            final JSONArray searchResponseArray = searchJsonResponse.getJSONArray(BullseyeConstant.RESULT_LIST);
            for (int arrayLength = 0; arrayLength < searchResponseArray.length(); arrayLength++) {
                final JSONObject locationJson = searchResponseArray.getJSONObject(arrayLength);
                if (locationJson.has(BullseyeConstant.CATEGORY_IDS)) {
                    final String[] CategoryIds = locationJson.getString(BullseyeConstant.CATEGORY_IDS).split(",");
                    final List<String> categoryList = Arrays.asList(CategoryIds);
                    for (String category : categoryList) {
                        final List<String> markerList = Arrays.asList(markersConfig);
                        final Optional<String> matchedMarker = markerList.stream()
                                .filter(marker -> {
                                    try {
                                        final JSONObject markerJson = new JSONObject(marker);
                                        if (markerJson.has(BullseyeConstant.CATEGORY_ID_CONST)) {
                                            final String categoryId = markerJson.getString(BullseyeConstant.CATEGORY_ID_CONST);
                                            if (category.equals(categoryId)) {
                                                return true;
                                            }
                                        }
                                    } catch(JSONException jse) {
                                        LOGGER.error("JSONException while calling setCategoryIcon method.");
                                    }
                                    return false;
                                }).findFirst();
                        if (matchedMarker.isPresent()) {
                            final JSONObject markerJson = new JSONObject(matchedMarker.get());
                            final String iconPath = markerJson.getString(BullseyeConstant.ICON);
                            if (StringUtils.isNotBlank(iconPath)) {
                                locationJson.put(BullseyeConstant.ICON, iconPath);
                            } else {
                                locationJson.put(BullseyeConstant.ICON, bullsEyeCloudConfig.get().getDefaultMapMarker());
                            }
                            break;
                        } else {
                            locationJson.put(BullseyeConstant.ICON, bullsEyeCloudConfig.get().getDefaultMapMarker());
                        }

                    }
                }
            }
        }
        LOGGER.debug("End with setCategoryIcon method ::");
    }

    private boolean markerJson(String category, String marker) {
        LOGGER.debug("Start with markerJson method ::");
        try {
            final JSONObject markerJson = new JSONObject(marker);
            if (markerJson.has(BullseyeConstant.CATEGORY_ID_CONST)) {
                final String categoryId = markerJson
                        .getString(BullseyeConstant.CATEGORY_ID_CONST);
                if (category.equals(categoryId)) {
                    return true;
                }
            }
        } catch (JSONException jse) {
            LOGGER.error("JSONException while calling setCategoryIcon method.");
        }
        LOGGER.debug("End with markerJson method ::");
        return false;
        
    }

    @Override
    public String getCategoryDropdownRes(Map<String, String> dropDownMap) {
        JsonArray dropdownJsonArray = dropDownMap.entrySet().stream().map(r -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(BullseyeConstant.TEXT, r.getKey());
            jsonObject.addProperty(BullseyeConstant.VALUE, r.getValue());
            return jsonObject;
        }).reduce(new JsonArray(), (jsonArray, jsonObject) -> {
            jsonArray.add(jsonObject);
            return jsonArray;
        }, (jsonArray, otherJsonArray) -> {
            jsonArray.addAll(otherJsonArray);
            return jsonArray;
        });
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(dropdownJsonArray);
    }

    @Override
    public int getCountryId(final Resource resource, String resourceCountryCode) {
        LOGGER.debug("Start with getCountryId method ::");
        int countryId = 0;
        try {
            final Optional<BullseyeConfigModel> bullsEyeCloudConfig = configService.getBullsEyeCloudConfig(resource);

            final List<NameValuePair> params = new ArrayList<>();
            if (bullsEyeCloudConfig.isPresent()) {
                setBasicNameValuePairParam(params, bullsEyeCloudConfig.get().getClientId(),
                        bullsEyeCloudConfig.get().getApiKey());
            } else {
                BullseyeConfigModel bullseyeConfigAdaptModel = resource.getChild(BullseyeConstant.JCR_CONTENT)
                        .adaptTo(BullseyeConfigModel.class);
                if (bullseyeConfigAdaptModel != null) {
                    setBasicNameValuePairParam(params, bullseyeConfigAdaptModel.getClientId(),
                            bullseyeConfigAdaptModel.getApiKey());
                }
            }

            BullseyeCache cacheService = broker.getBullseyeCache(CountryListCacheImpl.NAME);
            final String response = (null != cacheService) ? cacheService.getCache(params) : StringUtils.EMPTY;
            if (null != response) {
                final JSONArray responseData = new JSONArray(response);
                countryId = parseCountryId(responseData, resourceCountryCode);
            }
        } catch (JSONException e) {
            LOGGER.error(BullseyeConstant.JSON_ERROR_CALLING_BULLSEYE_API, e);
        } catch (IOException e) {
            LOGGER.error("IOException :{} ", e.getMessage(), e);
        } catch (URISyntaxException e) {
            LOGGER.error("URISyntaxException :{}", e.getMessage(), e);
        }
        LOGGER.debug("End with getCountryId method ::");
        return countryId;
    }

    final int parseCountryId(final JSONArray countryListArray, String resourceCountryCode) {
        LOGGER.debug("Start with parseCountryId method ::");
        int countryId = 0;
        try {
            for (int count = 0; count < countryListArray.length(); count++) {
                JSONObject candidateJson = countryListArray.getJSONObject(count);
                if (null != candidateJson && candidateJson.has(BullseyeConstant.CODE)) {
                    String countryCode = candidateJson.getString(BullseyeConstant.CODE);
                    if (countryCode.equalsIgnoreCase(resourceCountryCode)) {
                        countryId = candidateJson.getInt(BullseyeConstant.BULLSEYE_COUNTRY_ID);
                        break;
                    }
                }
            }
        } catch (JSONException e) {
            LOGGER.error("JSONException while calling parseCountryId method.");
        }
        LOGGER.debug("End with parseCountryId method ::");
        return countryId;
    }
}
