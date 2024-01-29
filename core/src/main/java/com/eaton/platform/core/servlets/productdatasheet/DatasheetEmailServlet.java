package com.eaton.platform.core.servlets.productdatasheet;

import com.adobe.acs.commons.i18n.I18nProvider;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.services.EatonEmailService;
import com.eaton.platform.core.vgselector.constants.VGCommonConstants;
import com.google.gson.Gson;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Component(service = Servlet.class,
        immediate = true,
        property = {
                ServletConstants.SLING_SERVLET_METHODS_POST,
                ServletConstants.SLING_SERVLET_PATHS + "/eaton/datasheets/sendEmail",
        })
public class DatasheetEmailServlet extends SlingAllMethodsServlet {

    private static final String EMAIL_BODY_TEXT = "emailBody";

    private static final Logger LOGGER = LoggerFactory.getLogger(DatasheetEmailServlet.class);
    private static final String EMAIl_SUBJECT = "emailSubject";
    private static final String DEAR_CUSTOMER = "dearCustomer";
    private static final String EMAIl_START = "emailStart";
    private static final String NO_REPLY = "noReply";
    private static final String BEST_REGARDS = "bestRegards";
    private static final String TEAM_EATON = "teamEaton";
    private static final String DEAR_CUSTOMER_I18N_KEY = "Datasheet.dearCust";
    private static final String EMAIL_START_I18N_KEY = "Datasheet.emailStart";
    private static final String NO_REPLY_I18N_KEY = "Datasheet.noReply";
    private static final String BEST_REGARDS_I18N_KEY = "Datasheet.bestRegards";
    private static final String TEAM_EATON_I18N_KEY = "Datasheet.teamEaton";
    private static final String SUBJECT_I18N_KEY = "Datasheet.subject";
    @Reference
    private transient EatonEmailService eatonEmailService;
    @Reference
    private transient I18nProvider i18nProvider;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        LOGGER.info("DatasheetEmailServlet :: goPost() :: Started");
        if (eatonEmailService == null) {
            LOGGER.error("email service is not properly injected, should not happen, please check!");
            return;
        }
        DatasheetEmailServletInput datasheetEmailServletInput = new Gson().fromJson(request.getReader(), DatasheetEmailServletInput.class);
        response.setContentType(VGCommonConstants.APPLICATION_JSON);
        String eMailAddress = datasheetEmailServletInput.getEmail();
        String eMailBody = datasheetEmailServletInput.getSkuData();
        if (eMailAddress != null && eMailBody != null) {
            String status = sendEmail(eMailAddress, datasheetEmailServletInput.getSkuData(), datasheetEmailServletInput.getPagePath());
            try (PrintWriter responseWriter = response.getWriter()) {
                responseWriter.write(status);
            }
        }
        LOGGER.info("DatasheetEmailServlet :: doPost() :: Exit");
    }

    private String sendEmail(String emailAddress, String body, String currentLocal) {
        LOGGER.info("DatasheetEmailServlet :: datasheetSendEmail() :: Started");
        Map<String, String> emailParams = new HashMap<>();
        Locale forLangLocale = Locale.forLanguageTag(currentLocal);
        emailParams.put(EMAIl_SUBJECT, i18nProvider.translate(SUBJECT_I18N_KEY, forLangLocale));
        emailParams.put(DEAR_CUSTOMER, i18nProvider.translate(DEAR_CUSTOMER_I18N_KEY, forLangLocale));
        emailParams.put(EMAIl_START, i18nProvider.translate(EMAIL_START_I18N_KEY, forLangLocale));
        emailParams.put(EMAIL_BODY_TEXT, body);
        emailParams.put(NO_REPLY, i18nProvider.translate(NO_REPLY_I18N_KEY, forLangLocale));
        emailParams.put(BEST_REGARDS, i18nProvider.translate(BEST_REGARDS_I18N_KEY, forLangLocale));
        emailParams.put(TEAM_EATON, i18nProvider.translate(TEAM_EATON_I18N_KEY, forLangLocale));
        return eatonEmailService.sendEmail(emailAddress, emailParams, CommonConstants.EMAIL_TEMPLATE_PAH);
    }

}

