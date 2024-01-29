package com.eaton.platform.integration.eloqua.services.impl;


import com.eaton.platform.core.models.EloquaCloudConfigModel;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.integration.eloqua.constant.EloquaConstants;
import com.eaton.platform.integration.eloqua.services.EloquaOptionListService;
import com.eaton.platform.integration.eloqua.services.EloquaService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.InputStream;
import java.io.InputStreamReader;

@Component(service = EloquaOptionListService.class, immediate = true)
public class EloquaOptionListServiceImpl implements EloquaOptionListService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EloquaOptionListServiceImpl.class);

    @Reference
    private EloquaService eloquaService;

    @Reference
    AdminService adminService;

    /**
     * This method returns the drop down list of eloqua optionId.
     *
     * @param eloquaCloudConfigModel
     * @param optionId
     * @return drop down options.
     */
    @Override
    public String getOptionList(EloquaCloudConfigModel eloquaCloudConfigModel,String optionId) {
        LOGGER.debug("Start getOptionList method to get the drop down val from eloqua");
        String options = StringUtils.EMPTY;
        try {
            String optionList = eloquaService.getEloquaOptionList(eloquaCloudConfigModel,optionId);
            final JsonParser parser = new JsonParser();
            final JsonObject jsonResult = (JsonObject) parser.parse(optionList);
                if (jsonResult != null && jsonResult.has(EloquaConstants.ELEMENTS)) {
                    final JsonArray jsonArray = jsonResult.getAsJsonArray(EloquaConstants.ELEMENTS);
                    for (int i = 0; i < jsonArray.size(); i++) {
                        final JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
                        if (jsonObject.has(EloquaConstants.DISPLAY_NAME)) {
                            String option = jsonObject.get(EloquaConstants.DISPLAY_NAME).getAsString();
                            options+= addOptionsInEnum(option,jsonArray,i);
                        }
                    }
                    if (!options.isEmpty()) {
                        options = ("                     \"enum\":").concat("[" + options + "],\n");
                    }
                }
            LOGGER.debug("End getOptionList method to get the drop down val from eloqua:::: Option List "+options);
        } catch (Exception e) {
            LOGGER.error("Exception in optionList method "+e.getMessage());
        }
        return options;
    }

    @Override
    public String getCountryFromDAMOptionList() {
        LOGGER.debug("Start getCountryFromDAMOptionList method to get the drop down for country and state");
        String countryOptions = StringUtils.EMPTY;
        JsonObject responseJson;
        try {
            final Resource resource = adminService.getReadService().getResource(EloquaConstants.COUNTRY_STATE_JSON_DAM_PATH.
                    concat(EloquaConstants.JCRCONTENT_RENDITIONS_ORIGINAL_JCRCONTENT));
            if(null != resource) {
                final ValueMap valMap = resource.getValueMap();
                if (valMap.containsKey(EloquaConstants.JCR_DATA)) {
                    final JsonElement element = new JsonParser().parse(
                            new InputStreamReader((InputStream) valMap.get(EloquaConstants.JCR_DATA)));
                    responseJson = element.getAsJsonObject();
                    if (responseJson.has(EloquaConstants.COUNTRY)) {
                        final JsonArray jsonArray = responseJson.getAsJsonArray(EloquaConstants.COUNTRY);
                        for (int i = 0; i < jsonArray.size(); i++) {
                            final JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
                            if (jsonObject.has(EloquaConstants.OPTION_NAME)) {
                                String option = jsonObject.get(EloquaConstants.OPTION_NAME).getAsString();
                                countryOptions += addOptionsInEnum(option, jsonArray, i);
                            }
                        }
                        if (!countryOptions.isEmpty()) {
                            countryOptions = ("                     \"enum\":").concat("[" + countryOptions + "],\n");
                        }
                    }
                }
            }
            LOGGER.debug("End getCountryFromDAMOptionList method to get the drop down for country and state ::: countryOptions "+countryOptions);
        }catch (Exception e){
            LOGGER.error("IOException in getCountryFromDAMOptionList "+e.getMessage());
        }
        return countryOptions;
    }

    private String addOptionsInEnum(String option, final JsonArray jsonArray, final int flag) {
        String options;
        if (jsonArray.size() - 1 == flag) {
            option = "\"" + option + "\"";
        } else {
            option = "\"" + option + "\",";
        }
        options = option;
        return options;
    }
}

