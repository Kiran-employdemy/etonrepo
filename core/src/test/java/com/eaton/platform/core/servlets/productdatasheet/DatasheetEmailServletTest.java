package com.eaton.platform.core.servlets.productdatasheet;

import com.adobe.acs.commons.i18n.I18nProvider;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.EatonEmailService;
import com.eaton.platform.core.services.impl.EmailParamFixtures;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Locale;

import static com.eaton.platform.core.services.impl.EmailParamFixtures.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DatasheetEmailServletTest {

    public static final String DATASHEET_I18N_NO_REPLY_KEY = "Datasheet.noReply";
    public static final String DATASHEET_I18N_DEAR_CUST_KEY = "Datasheet.dearCust";
    public static final String DATASHEET_I18N_EMAIL_START_KEY = "Datasheet.emailStart";
    public static final String DATASHEET_I18N_BEST_REGARDS_KEY = "Datasheet.bestRegards";
    public static final String DATASHEET_I18N_TEAM_EATON_KEY = "Datasheet.teamEaton";
    public static final String DATASHEET_I18N_SUBJECT_KEY = "Datasheet.subject";
    public static final String SKUDATA = "<span style='font-family:Arial, Helvetica, sans-serif; font-size:15px;'><br>English<br></span> Product specifications : <a href='https://www-local.eaton.com/us/en-us/skuPage.BR120.html#Specifications' target='_blank' style='font-family:Arial, Helvetica, sans-serif; font-size:14px;'>https://www-local.eaton.com/us/en-us/skuPage.BR120.html</a><br> Save product specifications as PDF : <a href='https://www-local.eaton.com/us/en-us/skuPage.BR120.pdf' target='_blank' style='font-family:Arial, Helvetica, sans-serif; font-size:14px;'>https://www-local.eaton.com/us/en-us/skuPage.BR120.pdf</a><br> Resources : <a href='https://www-local.eaton.com/us/en-us/skuPage.BR120.html#Resources' target='_blank' style='font-family:Arial, Helvetica, sans-serif; font-size:14px;'>https://www-local.eaton.com/us/en-us/skuPage.BR120.html</a><br> <br/><br/><b style='font-family:Arial, Helvetica, sans-serif; font-size:17px;'>BR120</b> <span style='font-family:Arial, Helvetica, sans-serif; font-size:15px;'><br>English<br></span> Product specifications : <span style='font-family:Arial, Helvetica, sans-serif; font-size:14px;'>No Data</span><br> Save product specifications as PDF : <span style='font-family:Arial, Helvetica, sans-serif; font-size:14px;'>No Data</span><br> Resources : <span style='font-family:Arial, Helvetica, sans-serif; font-size:14px;'>No Data</span><br> <br/><br/><b style='font-family:Arial, Helvetica, sans-serif; font-size:17px;'>BRADFAF</b>";
    public static final Locale EN_US_LOCALE = new Locale("en", "us");
    @InjectMocks
    DatasheetEmailServlet datasheetEmailServlet = new DatasheetEmailServlet();
    @Mock
    EatonEmailService emailService;
    @Mock
    I18nProvider i18nProvider;
    @Mock
    PrintWriter printWriter;
    @Mock
    SlingHttpServletRequest slingHttpServletRequest;
    @Mock
    SlingHttpServletResponse slingHttpServletResponse;

    @Test
    void testDoPostAllOK() throws IOException, ServletException {
        when(slingHttpServletRequest.getReader()).thenReturn(new BufferedReader(new StringReader("{\"email\":\"homer.simpsons@springfield.us\",\"skuData\":\"" + SKUDATA + "\",\"pagePath\":\"en-us\"}")));
        when(slingHttpServletResponse.getWriter()).thenReturn(printWriter);
        when(i18nProvider.translate(DATASHEET_I18N_NO_REPLY_KEY, EN_US_LOCALE)).thenReturn(NO_REPLY);
        when(i18nProvider.translate(DATASHEET_I18N_DEAR_CUST_KEY, EN_US_LOCALE)).thenReturn(DEAR_CUSTOMER);
        when(i18nProvider.translate(DATASHEET_I18N_EMAIL_START_KEY, EN_US_LOCALE)).thenReturn(HERE_ARE_THE_LINKS);
        when(i18nProvider.translate(DATASHEET_I18N_BEST_REGARDS_KEY, EN_US_LOCALE)).thenReturn(BEST_REGARDS);
        when(i18nProvider.translate(DATASHEET_I18N_TEAM_EATON_KEY, EN_US_LOCALE)).thenReturn(TEAM_EATON);
        when(i18nProvider.translate(DATASHEET_I18N_SUBJECT_KEY, EN_US_LOCALE)).thenReturn(EATON_DATASHEETS);
        when(emailService.sendEmail("homer.simpsons@springfield.us", EmailParamFixtures.forDataSheet(SKUDATA), CommonConstants.EMAIL_TEMPLATE_PAH)).thenReturn("success");
        datasheetEmailServlet.doPost(slingHttpServletRequest, slingHttpServletResponse);
        verify(printWriter).write("success");
    }
}