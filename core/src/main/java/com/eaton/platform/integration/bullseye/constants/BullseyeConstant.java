package com.eaton.platform.integration.bullseye.constants;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BullseyeConstant {

    private BullseyeConstant() {}

    public static final String RADIUS = "Radius";
    public static final String LATITUDE = "Latitude";
    public static final String LONGITUDE= "Longitude";
    public static final String KEYWORD = "Keyword";
    public static final String POSTAL_CODE = "PostalCode";
    public static final String SEARCH_TYPE_OVERRIDE = "SearchTypeOverride";
    public static final String CATEGORY_ID = "CategoryId";
    public static final String CATEGORY_IDS = "CategoryIds";
    public static final String CATEGORYIDS = "CategoryIDs";
    public static final String CATEGORY_NAME = "CategoryName";
    public static final String CLIENT_ID = "ClientId";
    public static final String RETURN_ALL_CATEGORIES = "ReturnAllCategories";
    public static final String API_KEY = "ApiKey";
    public static final String START_INDEX = "StartIndex";
    public static final String MATCH_ALL_CATEGORIES = "MatchAllCategories";
    public static final String PAGE_SIZE= "PageSize";
    public static final String CATEGORY_GROUP_ID= "CategoryGroupID";
    public static final String FILL_ATTR= "FillAttr";
    public static final String RESPONSE = "response";
    public static final String INPUT = "input";
    public static final String INPUT_TYPE = "inputtype";
    public static final String INPUT_TYPE_VALUE = "textquery";
    public static final String FIELDS = "fields";
    public static final String FIELDS_VALUE = "formatted_address,geometry";
    public static final String GOOGLE_API_KEY = "key";
    public static final String MAPBOX_API_KEY = "access_token";
    public static final String MAPBOX = "mapbox";
    public static final String STATUS = "status";
    public static final String OK = "OK";
    public static final String CANDIDATES = "candidates";
    public static final String FEATURES = "features";
    public static final String ERROR = "error";
    public static final String LNG = "lng";
    public static final String LAT = "lat";
    public static final String GEOMETRY = "geometry";
    public static final String LOCATION = "location";
    public static final String COORDINATES = "coordinates";
    public static final String LATITUDE_KEY = "lat";
    public static final int LATITUDE_INDEX = 1;
    public static final String LONGITUDE_KEY = "lng";
    public static final int LONGITUDE_INDEX = 0;
    public static final int COORDINATES_LENGTH = 2;
    public static final String ERROR_CALLING_GOOGLE_SERVICE = "Error calling google service to fetch latitude and longitude.";
    public static final String ERROR_CALLING_MAPBOX_SERVICE = "Error calling mapbox service to fetch latitude and longitude.";
    public static final String INVALID_RESPONSE_MAPBOX_SERVICE = "Invalid Response while calling mapbox service while fetching latitude and longitude.";
    public static final String SEARCH_TERM = "searchTerm";
    public static final String NAME = "Name";
    public static final String ID = "ID";
    public static final String RESULT_LIST = "ResultList";
    public static final String POST_CODE = "PostCode";
    public static final String KILOMETERS = "kilometers";
    public static final String DISTANCE_UNIT = "distanceUnit";
    public static final String ADDRESS_1 = "Address1";
    public static final String ADDRESS_2 ="Address2";
    public static final String ADDRESS_3 ="Address3";
    public static final String CITY ="City";
    public static final String STATE = "State";
    public static final String COUNTRY_CODE = "CountryCode";
    public static final String PHONE_NUMBER = "PhoneNumber";
    public static final String FAX_NUMBER = "FaxNumber";
    public static final String DISTANCE = "Distance";
    public static final String EMAIL_ADDRESS = "EmailAddress";
    public static final String URL = "URL";
    public static final String LOCATION_TYPE_NAME = "LocationTypeName";
    public static final String FILE_NAME = "attachment; filename=WhereToBuy.csv";
    public static final String CATEGORY_HEADERS = "categoryHeaders";
    public static final String MILES_I18N = "Map.Miles";
    public static final String KILOMETERS_I18N = "Map.Kilometers";
    public static final String NAME_I18N = "Map.Name";
    public static final String ADDRESS1_I18N = "Map.Address1";
    public static final String ADDRESS2_I18N = "Map.Address2";
    public static final String ADDRESS3_I18N = "Map.Address3";
    public static final String COUNTRY_I18N = "Map.Country";
    public static final String STATE_I18N = "Map.State";
    public static final String CITY_I18N = "Map.City";
    public static final String POSTAL_CODE_I18N = "Map.PostalCode";
    public static final String FAX_NUMBER_I18N = "Map.FaxNumber";
    public static final String PHONE_NUMBER_I18N = "Map.PhoneNumber";
    public static final String TOLL_FREE_NUMBER_I18N = "Map.TollFreeNumber";
    public static final String DISTANCE_I18N = "Map.Distance";
    public static final String URL_I18N = "Map.Url";
    public static final String EMAIL_I18N = "Map.Email";
    public static final String CLASS_I18N = "Map.Class";
    public static final String LANGUAGE_CODE = "LanguageCode";
    public static final String COUNTRY_ID = "CountryId";
    public static final String ICON = "icon";
    public static final String TEXT = "text";
    public static final String VALUE = "value";
    public static final String BULLSEYE_CONFIG_PAGE = "bullseyeconfig";
    public static final String RADIUS_CONST = "radius";
    public static final String ID_CONST = "id";
    public static final String VALUES = "values";
    public static final String TITLE = "title";
    public static final String CATEGORY_GROUPS_ARRAY = "categoryGroups";
    public static final String CATEGORY_ID_CONST = "categoryId";
    public static final String JSON_EXTENSION = ".json";
    public static final String TOTAL_RESULTS = "TotalResults";
    public static final int NUMBER_3 = 3;
    public static final int NUMBER_2 = 2;
    public static final int NUMBER_1 = 1;
    public static final int NUMBER_0 = 0;
    public static final String JSON_ERROR_CALLING_BULLSEYE_API = "JSONException while calling the api";
    public static final String CODE = "Code";
    public static final String BULLSEYE_COUNTRY_ID = "Id";
    public static final String JCR_CONTENT = "jcr:content";

    public static ArrayList<String> getI18nValuesMap(final SlingHttpServletRequest request, final Page currentPage) {
        final ArrayList<String> csvHeaderList = new ArrayList<>();
        final String name = CommonUtil.getI18NFromResourceBundle(request, currentPage,
                BullseyeConstant.NAME_I18N);
        final String address1 = CommonUtil.getI18NFromResourceBundle(request, currentPage,
                BullseyeConstant.ADDRESS1_I18N);
        final String address2 = CommonUtil.getI18NFromResourceBundle(request, currentPage,
                BullseyeConstant.ADDRESS2_I18N);
        final String address3 = CommonUtil.getI18NFromResourceBundle(request, currentPage,
                BullseyeConstant.ADDRESS3_I18N);
        final String city = CommonUtil.getI18NFromResourceBundle(request, currentPage,
                BullseyeConstant.CITY_I18N);
        final String state = CommonUtil.getI18NFromResourceBundle(request, currentPage,
                BullseyeConstant.STATE_I18N);
        final String postalCode = CommonUtil.getI18NFromResourceBundle(request, currentPage,
                BullseyeConstant.POSTAL_CODE_I18N);
        final String country = CommonUtil.getI18NFromResourceBundle(request, currentPage,
                BullseyeConstant.COUNTRY_I18N);
        final String phoneNumber = CommonUtil.getI18NFromResourceBundle(request, currentPage,
                BullseyeConstant.PHONE_NUMBER_I18N);
        final String faxNumber = CommonUtil.getI18NFromResourceBundle(request, currentPage,
                BullseyeConstant.FAX_NUMBER_I18N);
        final String tollFreeNumber = CommonUtil.getI18NFromResourceBundle(request, currentPage,
                BullseyeConstant.TOLL_FREE_NUMBER_I18N);
        final String distance = CommonUtil.getI18NFromResourceBundle(request, currentPage,
                BullseyeConstant.DISTANCE_I18N);
        final String email = CommonUtil.getI18NFromResourceBundle(request, currentPage,
                BullseyeConstant.EMAIL_I18N);
        final String url = CommonUtil.getI18NFromResourceBundle(request, currentPage,
                BullseyeConstant.URL_I18N);
        List<String> headerList = Arrays.asList(name, address1, address2, address3, city, state, postalCode, country, phoneNumber, faxNumber,
                tollFreeNumber, distance, email, url);
        csvHeaderList.addAll(headerList);
        return csvHeaderList;
    }

}
