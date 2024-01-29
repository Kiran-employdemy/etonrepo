package com.eaton.platform.integration.bullseye.servlets;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.bullseye.constants.BullseyeConstant;
import com.eaton.platform.integration.bullseye.models.MapModel;
import com.eaton.platform.integration.bullseye.services.BullseyeService;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component(service = Servlet.class,
        immediate = true,
        property = {
                ServletConstants.SLING_SERVLET_METHODS_GET,
                ServletConstants.SLING_SERVLET_RESOURCE_TYPES + "eaton/components/content/map",
                ServletConstants.SLING_SERVLET_EXTENSION_JSON,
                ServletConstants.SLING_SERVLET_EXTENSION_CSV
        })
public class BullseyeSearchServlet extends SlingAllMethodsServlet {
    private static final long serialVersionUID = 6986390122000208721L;
    private static final Logger LOGGER = LoggerFactory.getLogger(BullseyeSearchServlet.class);

    @Reference
    private BullseyeService bullseyeService;

    private String city;
    private String state;
    private String postalCode;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws IOException {
        LOGGER.debug("BullseyeSearchServlet :: doGet() :: Started");
        final Resource currentPageRes = request.getResource();
        final String refererURL = CommonUtil.getRefererURL(request);
        final ResourceResolver resourceResolver = request.getResourceResolver();
        final String pagePath = CommonUtil.getContentPath(resourceResolver, refererURL);
        final Page currentPage = resourceResolver.resolve(pagePath).adaptTo(Page.class);

        final MapModel mapModel = request.adaptTo(MapModel.class);
        final String keyword = request.getParameter(BullseyeConstant.KEYWORD);

        parseKeyword(keyword);
       

        final Map<String, String[]> requestParamMap = request.getParameterMap();
        final LinkedHashMap<String, String> paramMap = getRequestParamMap(requestParamMap);
        final String preSeltCategoryList=mapModel.preSeltCategoryList.trim();
       
        if(!(preSeltCategoryList.isEmpty()))
        {
           if(!(paramMap.containsKey(BullseyeConstant.CATEGORYIDS)))
           {
        	paramMap.put(BullseyeConstant.CATEGORYIDS, preSeltCategoryList);
           }else {
              for(Map.Entry<String,String>it:paramMap.entrySet()) {
        	     if(it.getKey().equals(BullseyeConstant.CATEGORYIDS))
        	     {
        		   paramMap.put(BullseyeConstant.CATEGORYIDS, it.getValue().concat(",").concat(preSeltCategoryList));
        		 }
        	  }
          }
          for(Map.Entry<String,String>iterator:paramMap.entrySet()) {
        	LOGGER.debug(iterator.getKey()+", "+iterator.getValue());
          }
        }  
        
        final String miles = CommonUtil.getI18NFromResourceBundle(request, currentPage,
                BullseyeConstant.MILES_I18N);
        final ArrayList<String> csvHeaderLabel = BullseyeConstant.getI18nValuesMap(request, currentPage);
        final String classLabel = CommonUtil.getI18NFromResourceBundle(request, currentPage,
                BullseyeConstant.CLASS_I18N);
        String latitude = request.getParameter(BullseyeConstant.LATITUDE);
        String longitude = request.getParameter(BullseyeConstant.LONGITUDE);

        populateParamMap(paramMap,keyword,currentPage,currentPageRes);

        try {
            final JSONObject bullEyeSearchResponse;
            if (request.getRequestPathInfo().getExtension().equals(CommonConstants.EXTENSION_JSON)) {
                bullEyeSearchResponse = bullseyeService.getDoSearchResults(currentPageRes, paramMap, mapModel.getFiltersArray());
            } else {
                bullEyeSearchResponse = bullseyeService.getDoSearchResults(currentPageRes, paramMap, mapModel.getCsvColumnsArray());
            }
            bullEyeSearchResponse.put(BullseyeConstant.LATITUDE, latitude);
            bullEyeSearchResponse.put(BullseyeConstant.LONGITUDE, longitude);
            if (StringUtils.isNotEmpty(keyword)) {
                bullEyeSearchResponse.put(BullseyeConstant.SEARCH_TERM, keyword);
            }
            if (request.getRequestPathInfo().getExtension().equals(CommonConstants.EXTENSION_JSON)) {
                response.setContentType(CommonConstants.APPLICATION_JSON);
                response.setCharacterEncoding(CommonConstants.UTF_8);
                response.getWriter().print(bullEyeSearchResponse);
                response.getWriter().flush();
            } else {
                response.setContentType(CommonConstants.APPLICATION_OCTET_STREAM);
                response.setHeader(CommonConstants.CONTENT_DISPOSITION,BullseyeConstant.FILE_NAME);
                CSVPrinter storeCSV = bullseyeService.getStoreCSV(bullEyeSearchResponse, response.getWriter(),
                        miles, csvHeaderLabel, classLabel);
                storeCSV.flush();
                storeCSV.close();
            }
        } catch (Exception e) {
            LOGGER.error("Exception while calling BullseyeSearchServlet method.");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{ \"error\": \"Error calling search service.\" }");
        }
          LOGGER.debug("BullseyeSearchServlet :: doGet() :: Ended");
    }

    private LinkedHashMap<String, String> getRequestParamMap(final Map<String, String[]> requestParamMap) {
        final LinkedHashMap<String, String> paramMap = new LinkedHashMap<>();
        if (null != requestParamMap) {
            requestParamMap.forEach((key, value) -> {
                if (null != value && StringUtils.isNotBlank(value[0])) {
                    paramMap.put(key, value[0]);
                }
            });
            paramMap.remove(BullseyeConstant.KEYWORD);
        }
        return paramMap;
    }

    private boolean checkForPostalCode(String input){
        LOGGER.debug("BullseyeSearchServlet :: checkForPostalCode() :: Started");
        String regex = "(.)*(\\d)(.)*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        boolean isInputPostalCode = matcher.matches();
        LOGGER.debug("BullseyeSearchServlet :: checkForPostalCode() :: Ended");
        return isInputPostalCode;
    }

    private void parseKeyword(String keyword){
        LOGGER.debug("BullseyeSearchServlet :: parseKeyword() :: Started");

        city = "";
        state = "";
        postalCode = "";

        if (StringUtils.isNotBlank(keyword)) {
            String [] keywordArray = keyword.split(",");
            if(keywordArray.length==BullseyeConstant.NUMBER_3){
                city = keywordArray[BullseyeConstant.NUMBER_0].trim();
                state = keywordArray[BullseyeConstant.NUMBER_1].trim();
                postalCode = keywordArray[BullseyeConstant.NUMBER_2].trim();
            }else if(keywordArray.length==BullseyeConstant.NUMBER_2){
                city = keywordArray[BullseyeConstant.NUMBER_0].trim();
                boolean isPostalCode = checkForPostalCode(keywordArray[BullseyeConstant.NUMBER_1].trim());
                if(isPostalCode) {
                    postalCode = keywordArray[BullseyeConstant.NUMBER_1].trim();
                }else{
                    state = keywordArray[BullseyeConstant.NUMBER_1].trim();
                }
            }else {
                boolean isPostalCode = checkForPostalCode(keywordArray[BullseyeConstant.NUMBER_0].trim());
                if(isPostalCode) {
                    postalCode = keywordArray[BullseyeConstant.NUMBER_0].trim();
                }else{
                    city = keywordArray[BullseyeConstant.NUMBER_0].trim();
                }
            }
        }
        LOGGER.debug("BullseyeSearchServlet :: parseKeyword() :: Ended");
    }

    private void populateParamMap(LinkedHashMap<String, String> paramMap,String keyword,Page currentPage,Resource currentPageRes){
        LOGGER.debug("BullseyeSearchServlet :: populateParamMap() :: Started");
        final Locale language = CommonUtil.getLocaleFromPagePath(currentPage);
        final String country = CommonUtil.getCountryFromPagePath(currentPage);

        if (null != language && StringUtils.isNotBlank(language.getLanguage())) {
            paramMap.put(BullseyeConstant.LANGUAGE_CODE, language.getLanguage());
        }

        if ((null != country) && StringUtils.isNotBlank(country)) {
            int countryId = bullseyeService.getCountryId(currentPageRes,country);
            if(countryId  > 0) {
                paramMap.put(BullseyeConstant.COUNTRY_ID, StringUtils.EMPTY+countryId);
            }
        }

        boolean searchTeamPresent = false;
        if ((StringUtils.isNotBlank(city)) && ((StringUtils.isNotBlank(state)) || (StringUtils.isNotBlank(postalCode)))) {
            paramMap.put(BullseyeConstant.CITY, city);
            searchTeamPresent = true;
        }
        if (StringUtils.isNotBlank(state)) {
            paramMap.put(BullseyeConstant.STATE, state);
            searchTeamPresent = true;
        }
        if (StringUtils.isNotBlank(postalCode)) {
            paramMap.put(BullseyeConstant.POSTAL_CODE, postalCode);
            searchTeamPresent = true;
        }
        if ((!searchTeamPresent) && (StringUtils.isNotBlank(keyword))){
            paramMap.remove(BullseyeConstant.LATITUDE);
            paramMap.remove(BullseyeConstant.LONGITUDE);
            paramMap.put(BullseyeConstant.KEYWORD, keyword);
        }else if(searchTeamPresent){
            paramMap.remove(BullseyeConstant.LATITUDE);
            paramMap.remove(BullseyeConstant.LONGITUDE);
        }else{
            LOGGER.debug("Using Longitude & Latitude for API call.");
        }
        paramMap.put(BullseyeConstant.FILL_ATTR, "true");

        LOGGER.debug("BullseyeSearchServlet :: populateParamMap() :: Ended");
    }

}
