package com.eaton.platform.core.webtools.services;

import com.google.gson.JsonObject;

/**
 * <html> Description: Interface for Configuration for  Webtools Services.
 *
 * @author ICF
 * @version 1.0
 * @since 2022
 *
 */
public interface WebtoolsServiceConfiguration {

    int getMaxConnections();

    int getMaxHostConnections();

    String getWebtoolsBaseEndpointRoot();

    String getArincComponentEndpoint();

    String getArincPackagesEndpoint();

    String getBackshellMetadataEndpoint();

    String getBackshellPartbuildEndpoint();

    String getWebtoolsUsername();

    String getWebtoolsPassword();

    JsonObject getArincComponents();

    /**
     * gets Arinc Packaging options json
     * @param packageParamJson JsonObject
     * @return JsonObject
     * */
    JsonObject getArincPackagingOptions(JsonObject packageParamJson);

    JsonObject getBackshellMetadataResults();

    /**
     * gets backshell part build json
     * @param partId int
     * @return JsonObject
     * */
    JsonObject getBackshellPartBuildResults(int partId);

    JsonObject getArincPackage(JsonObject packageParamJson);

    /**
     * gets backshell part build json
     * 
     * @param partId int
     * @return JsonObject
     */
    JsonObject getFreeSampleStockAvailability(String catalogId);
}
