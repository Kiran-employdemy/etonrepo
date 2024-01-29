package com.eaton.platform.core.services.vanity;

import org.apache.sling.api.SlingHttpServletRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.Set;

/**
 * Interface VanityDataStoreService
 **/
public interface VanityDataStoreService {

    /**
     * This method returns the array of vanity urls across domains
     *
     * @return JSONArray with all vanity urls.
     *
     */
    JSONArray getAllVanities();

    /**
     * This method returns the set of vanity data objects across domains
     *
     * @return Set with all vanity Strings.
     *
     */
    Set<String> getUniqueVanityUrlSetFromAllDomains() throws JSONException;

    /**
     * This method returns the json object of domain specific vanity data
     * @param domain name
     * @return JSONObject with vanity data.
     *
     */
    JSONObject  getDomainSpecificVanities(String domain);
    /**
     * This method returns the json object of domain specific vanity data
     * @param request param
     * @return JSONObject with vanity data.
     *
     */
    JSONObject lookupAndFetchVanityEntry(SlingHttpServletRequest request);

    /**
     * This method updates the vanity data object with disable flag
     * @param vanityUrlList list of vanities
     * @param domainName name of the domain selected
     * @param currentDate to set modified date
     * @return JSONArray with vanity data.
     *
     */
    JSONObject disableRequestedVanities(String[] vanityUrlList, String domainName, Timestamp currentDate);

    /**
     * This method updates the vanity data object with deleted objects
     * @param vanityUrlList list of vanities from request
     * @param  jsonFileName name of the selected domain
     * @param currentDate to set modified date
     * @return JSONArray with vanity data.
     *
     */
    JSONObject deleteRequestedVanities(String[] vanityUrlList, String jsonFileName, Timestamp currentDate);

    /**
     * This method handles updates on existing vanity data object
     * @param request object
     * @param jsonFileName name of the selected domain
     * @param currentTime to set modified date
     * @param vanityJsonObject from request
     * @return JSONArray with vanity data.
     *
     */
    JSONObject updateRequestedVanity(SlingHttpServletRequest request, String jsonFileName, Timestamp currentTime, JSONObject vanityJsonObject);

    /**
     * This method handles updates on existing vanity data object
     * @param request object
     * @param jsonFileName name of the selected domain
     * @param vanityList to use at time of update
     * @param vanityJsonObject from request
     * @return JSONArray with vanity data.
     *
     */
    JSONObject createNewVanity(String jsonFileName, SlingHttpServletRequest request, String[] vanityList, JSONObject vanityJsonObject, String groupId);

    /**
     * This method returns count of unPublished changes
     * @param domainName name of the selected domain
     * @return int unPublishChangesCount
     *
     */
    int getUnPublishChangesCount(String domainName , String Operation);
}
