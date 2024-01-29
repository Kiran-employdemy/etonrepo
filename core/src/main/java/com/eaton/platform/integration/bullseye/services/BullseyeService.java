package com.eaton.platform.integration.bullseye.services;

import org.apache.commons.csv.CSVPrinter;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public interface BullseyeService {

    JSONObject getCategoryGroup(final Resource resource, final boolean returnAllCategories) throws Exception;

    JSONObject getDoSearchResults(final Resource resource, final LinkedHashMap<String, String> requestParamMap, final JSONArray categories) throws Exception;

    JSONObject getCategory(final Resource resource, final String categoryGroupID, Locale language) throws Exception;

    JSONObject getLatLongService(final Resource resource, final String keyword) throws Exception;

    JSONObject getLatLongGoogleService(final Resource resource, final String keyword) throws Exception;

    JSONObject getLatLongMapBoxService(final Resource resource, final String keyword) throws Exception;

    CSVPrinter getStoreCSV(final JSONObject bullEyeSearchResponse, final PrintWriter outputStream, String distanceUnit, final ArrayList<String> csvHeaders, final String classLabel);

    String getCategoryDropdownRes(Map<String, String> dropdownMap);

    int getCountryId(final Resource resource, String resourceCountryCode);

}
