package com.eaton.platform.core.servlets.secure;

import com.eaton.platform.core.bean.secure.SecureAssetsBean;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.services.secure.WhatsNewService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONArray;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * ICF
 */
@Component(service = Servlet.class,
        immediate = true,
        property = {
                ServletConstants.SLING_SERVLET_METHODS_GET,
                ServletConstants.SLING_SERVLET_RESOURCE_TYPES + "eaton/components/secure/whatsnew",
                ServletConstants.SLING_SERVLET_EXTENSION_JSON
        })
public class WhatsNewServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(WhatsNewServlet.class);

    @Reference
    private WhatsNewService whatsNewService;

    //Local Constants specific to WhatsNewServlet
    private static final String DATE_FORMAT_PUBLISH = "dd/MM/yyyy HH:mm:ss";
    private static final String LAST_SEVEN_DAYS = "last7days";
    private static final String LAST_FOURTEEN_DAYS = "last14days";
    private static final String LAST_TWENTY_ONE_DAYS = "last21days";
    private static final String LAST_THIRTY_DAYS = "last30days";
    private static final Integer LAST_SEVEN_DAYS_RANGE = -7;
    private static final Integer LAST_FOURTEEN_DAYS_RANGE = -14;
    private static final Integer LAST_TWENTY_ONE_DAYS_RANGE = -21;
    private static final Integer LAST_THIRTY_DAYS_RANGE = -30;
    private static final String USER_ID_FOR_TEST = "userId";

    final DateFormat simpleDate = new SimpleDateFormat(DATE_FORMAT_PUBLISH, Locale.ENGLISH);

    @Override
    public void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
            throws ServletException, IOException {
        LOG.debug("******** WhatsNewServlet execution started ***********");
        final JsonObject secureJsonObject = new JsonObject();
        secureJsonObject.add(LAST_SEVEN_DAYS, new Gson().toJsonTree(
                getSecureMarkedAssets(request,DateUtils.addDays(new Date(), LAST_SEVEN_DAYS_RANGE),new Date())));
        secureJsonObject.add(LAST_FOURTEEN_DAYS, new Gson().toJsonTree(
                getSecureMarkedAssets(request, DateUtils.addDays(new Date(), LAST_FOURTEEN_DAYS_RANGE), DateUtils.addDays(new Date(), LAST_SEVEN_DAYS_RANGE))));
        secureJsonObject.add(LAST_TWENTY_ONE_DAYS, new Gson().toJsonTree(
                getSecureMarkedAssets(request, DateUtils.addDays(new Date(), LAST_TWENTY_ONE_DAYS_RANGE), DateUtils.addDays(new Date(), LAST_FOURTEEN_DAYS_RANGE))));
        secureJsonObject.add(LAST_THIRTY_DAYS, new Gson().toJsonTree(
                getSecureMarkedAssets(request, DateUtils.addDays(new Date(), LAST_THIRTY_DAYS_RANGE), DateUtils.addDays(new Date(), LAST_TWENTY_ONE_DAYS_RANGE))));
        response.setContentType(CommonConstants.APPLICATION_JSON);
        response.getWriter().print(secureJsonObject.toString());
        LOG.debug("******** WhatsNewServlet execution completed ***********");

    }

    private final List<SecureAssetsBean> getSecureMarkedAssets(final SlingHttpServletRequest req, Date startDate, Date endDate){
        final String userId = req.getParameter(USER_ID_FOR_TEST);
        final List<SecureAssetsBean> secureSelectedAssets = new ArrayList<>();
        JSONArray results = whatsNewService.getLatestAssetsFromEndecaByRange(req, userId,startDate, endDate);
        whatsNewService.getAssetsFromHits(results, secureSelectedAssets);
        return secureSelectedAssets;
    }
}
