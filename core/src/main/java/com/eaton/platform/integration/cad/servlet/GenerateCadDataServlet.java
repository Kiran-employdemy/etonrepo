package com.eaton.platform.integration.cad.servlet;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.exception.EatonApplicationException;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.vgselector.constants.VGCommonConstants;
import com.eaton.platform.integration.cad.services.CadenasUrlService;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a Javadoc comment
 * GenerateCadDataServlet class
 */
@Component(service = Servlet.class,
        immediate = true,
        property = {
                ServletConstants.SLING_SERVLET_METHODS_POST,
                ServletConstants.SLING_SERVLET_PATHS + "/eaton/generateCadDataServlet",
        })
public class GenerateCadDataServlet extends SlingAllMethodsServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateCadDataServlet.class);
    private static final String CAD_GLOBAL_TABLE_CSV_LOCATION = "/content/dam/eaton/resources/cad-global-table/";
    private static final String CGIACTION = "cgiaction=download";
    private static final String GLOBAL_TABLE_CSV_FILE = "globaltable.csv";
    private static final String PART = "part=";
    private static final String PLID = "PLID=";
    private static final String FORMAT_EQUAL = "format=";
    private static final String CADENAS_REQUEST_ATTRIBUTES = "firm=eaton_cad&server_type=SUPPLIER_EXTERNAL_eaton_cad&downloadFlags=zip&ok_url_type=text&ok_url=";
    private static final String API_KEY = "APIKEY=f853d29f8f064dd3ab38c11b1c14b63d";
    private static final String NEW_LINE = "\n";
    private static final String EMPTY_STRING = "";
    private static final String REQUEST_PARAMETER_VALUE = "value";
    private static final int FOUR = 4;
    private static final int SIX = 6;
    private static final String HTML_TAG = "HTML";
    private static final String CAD_XML_URL = "cadXMLURL";
    private static final String INVALID_PLID = "invalidPlid";
    private static final String DOWNLOAD_XML = "download_xml";
    /**
     * This is a Javadoc comment
     * HttpClientBuilderFactory referenced
     */
    @Reference
    public transient HttpClientBuilderFactory httpFactory;
    /**
     * This is a Javadoc comment
     * adminService referenced
     */
    @Reference
    public transient AdminService adminService;
    /**
     * This is a Javadoc comment
     * CadenasUrlService referenced
     */
    @Reference
    public transient CadenasUrlService cadUrlService;
    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        LOGGER.info("GenerateCadDataServlet :: doPost() :: Started");
        String filePath = CAD_GLOBAL_TABLE_CSV_LOCATION
                .concat(GLOBAL_TABLE_CSV_FILE);
        List<String> plidList = new ArrayList<>();
        String requestData = request.getParameter(REQUEST_PARAMETER_VALUE);
        String cadXMLResponse;
        BufferedReader reader = new BufferedReader(new StringReader(requestData));
        StringBuilder sb = new StringBuilder();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } finally {
            reader.close();
        }
        final PrintWriter responseWriter = response.getWriter();
        response.setContentType(VGCommonConstants.APPLICATION_JSON);

        String inputStr = sb.toString();
        int plidFirstIndex = inputStr.indexOf(CommonConstants.OPEN_SQUARE_BRACKET);
        int plidLastIndex = inputStr.indexOf(CommonConstants.CLOSE_SQUARE_BRACKET);

        String plidStr = inputStr.substring(plidFirstIndex + 1, plidLastIndex).replace(CommonConstants.DOUBLE_QUOTE, EMPTY_STRING);
        String[] plidArray = plidStr.split(CommonConstants.COMMA);
        for (int i = 0; i < plidArray.length; i++) {
            plidList.add(plidArray[i]);
        }
        int formatFirstIndex = inputStr.lastIndexOf(CommonConstants.OPEN_SQUARE_BRACKET);
        int formatLastIndex = inputStr.lastIndexOf(CommonConstants.CLOSE_SQUARE_BRACKET);
        String formatStr = inputStr.substring(formatFirstIndex + 1, formatLastIndex).replace(CommonConstants.DOUBLE_QUOTE, EMPTY_STRING);

        try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
            Map<String, String> mp = new HashMap<>();
            File csvFile = cadUrlService.getCSVfilefromDAM(adminResourceResolver, filePath, GLOBAL_TABLE_CSV_FILE);
            byte[] bytes = FileUtils.readFileToByteArray(csvFile);
            String data = new String(bytes);
            data = StringUtils.replaceAll(data, "\r", EMPTY_STRING);
            String[] dataArray = data.split(NEW_LINE);

            for (int i = FOUR; i < dataArray.length; i++) {
                List<String> rows = new ArrayList<>();
                String[] rowArray = dataArray[i].split(CommonConstants.SEMI_COLON);
                rows.addAll(Arrays.asList(rowArray));
                String plid = rows.get(SIX);
                String partPath = rows.get(FOUR);
                mp.put(plid, partPath);
            }
            String pathPlid = StringUtils.EMPTY;
            String invalidPlid = StringUtils.EMPTY;
            JsonObject jsonObject = new JsonObject();
            String finalResponse;
            String openBracket = URLEncoder.encode(CommonConstants.OPEN_CURLY_BRACKET, CommonConstants.UTF_8);
            String closeBracket = URLEncoder.encode(CommonConstants.CLOSE_CURLY_BRACKET, CommonConstants.UTF_8);
            String angleLTPrcntBracket = URLEncoder.encode(CommonConstants.LT_PERCENT_BRACKET, CommonConstants.UTF_8);
            String angleGTPrcntBracket = URLEncoder.encode(CommonConstants.GT_PERCENT_BRACKET, CommonConstants.UTF_8);
            String requestParameters = new StringBuilder().append(CADENAS_REQUEST_ATTRIBUTES).append(angleLTPrcntBracket).append(DOWNLOAD_XML).append(angleGTPrcntBracket).append(CommonConstants.AMPERSAND).append(API_KEY).toString();

            for (String s : plidList) {
                if (mp.containsKey(s)) {
                    pathPlid = new StringBuilder().append(pathPlid).append(PART).
                            append(openBracket).append(mp.get(s)).
                            append(closeBracket).append(CommonConstants.COMMA).append(openBracket).
                            append(PLID).append(s).
                            append(closeBracket).append(CommonConstants.AMPERSAND).toString();
                } else
                    invalidPlid = new StringBuilder().append(invalidPlid).append(s).append(CommonConstants.COMMA).toString();
            }
            String cadRequest = new StringBuilder().append(cadUrlService.getPartCommunityUrl()).append(CommonConstants.QUESTION_MARK_CHAR).
                    append(CGIACTION).append(CommonConstants.AMPERSAND).append(pathPlid).append(FORMAT_EQUAL).
                    append(formatStr).append(CommonConstants.AMPERSAND).append(requestParameters).toString();
            LOGGER.error("cadRequest::{}", cadRequest);
            cadXMLResponse = cadUrlService.getResponseFromCadenas(cadRequest);

            if(cadXMLResponse != null) {
                if (cadXMLResponse.contains(HTML_TAG)) {
                    cadXMLResponse = StringUtils.EMPTY;
                    jsonObject.addProperty(CAD_XML_URL,cadXMLResponse);
                    jsonObject.addProperty(INVALID_PLID, invalidPlid);
                } else {
                    jsonObject.addProperty(CAD_XML_URL,cadXMLResponse);
                    jsonObject.addProperty(INVALID_PLID, invalidPlid);
                }
            }
            finalResponse = jsonObject.toString();
            responseWriter.write(finalResponse);
            responseWriter.flush();
        } catch (EatonApplicationException e) {
            LOGGER.error("GenerateCadDataServlet::Error while processing CAD request::EatonApplicationException:{}", e.getMessage());
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("GenerateCadDataServlet::Error while processing CAD request::UnsupportedEncodingException:{}", e.getMessage());
        } catch (IOException e) {
            LOGGER.error("GenerateCadDataServlet::Error while processing CAD request::IOException:{}", e.getMessage());
        }
        LOGGER.info("GenerateCadDataServlet :: doPost() :: Ended");
    }

}