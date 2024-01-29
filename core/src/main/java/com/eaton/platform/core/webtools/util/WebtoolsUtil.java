package com.eaton.platform.core.webtools.util;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <html> Description: Webtools Util.
 *
 * @author ICF
 * @version 1.0
 * @since 2022
 *
 */
public final class WebtoolsUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebtoolsUtil.class);

    public WebtoolsUtil() {
    }

    /**
     * Method to get the resource file within the package
     * @param filePath String
     * @return json String
     */
    public static String getResourceFile(String filePath) {
        StringBuilder json = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(Objects.requireNonNull(WebtoolsUtil.class.getClassLoader().getResourceAsStream(filePath)),
                            StandardCharsets.UTF_8));
            String str;
            while ((str = in.readLine()) != null) {
                json.append(str);
            }
            in.close();
        } catch (IOException e) {
            LOGGER.error("Caught exception while reading resources file " + filePath, e);
        }
        return json.toString();
    }

    /**
     * Method to get Parameters from  selectors array
     * @param selectors String[]
     * @return requestParameterMap Map<String, String>
     */
    public static Map<String, String> getParamsFromSelectors(String[] selectors){
        final Map<String, String> requestParameterMap = new HashMap<>();
        if (null != selectors && selectors.length > 0) {
            Arrays.asList(selectors).stream().forEach(selector -> {
                final String[] selectorArray = selector.split("\\$");
                String value = selectorArray[1];
                if(value.isEmpty()){
                    value = StringUtils.EMPTY;
                }
                requestParameterMap.put(selectorArray[0], value);
            });
        }
        return requestParameterMap;
    }

    /**
     * Method to read the data from AEM DAM Asset Json file and construct the jsonBuilder object
     * @param mapper Asset Json file
     * @return JsonObject mappingJson
     */
    public static JsonObject createComponentsJsonBuilder(Asset mapper){
        LOGGER.debug("createComponentsJsonBuilder Start");
        BufferedReader br = null;
        JsonObject mappingJson = null;
        try(InputStream is = mapper.getRendition(DamConstants.ORIGINAL_FILE).getStream()){
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
            }
            LOGGER.debug("\n ***** Loaded JSON File ******* \n {}", stringBuilder.toString());

            mappingJson = new JsonParser().parse(stringBuilder.toString()).getAsJsonObject();

        } catch (IOException io) {
            LOGGER.error("Failed to Read the Mapping File :: IOException in createComponentsJsonBuilder method {}", io);
        }finally {
            try {
                if(br != null) {
                    br.close();
                }
            } catch (IOException e) {
                LOGGER.error("Error while closing the Buffer Reader : ", e);
            }
        }
        LOGGER.debug("createComponentsJsonBuilder End");
        return mappingJson;
    }
}
