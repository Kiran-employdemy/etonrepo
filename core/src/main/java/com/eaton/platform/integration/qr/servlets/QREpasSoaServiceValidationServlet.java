package com.eaton.platform.integration.qr.servlets;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.qr.constants.QRConstants;
import com.eaton.platform.integration.qr.services.QRService;
import com.eaton.platform.integration.qr.services.AESService;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Map;

@Component(service = Servlet.class,
        immediate = true,
        property = {
                ServletConstants.SLING_SERVLET_METHODS_POST,
                ServletConstants.SLING_SERVLET_PATHS + "/eaton/qr/epasSoaServiceValidation",
                ServletConstants.SLING_SERVLET_EXTENSION_JSON
        })
public class QREpasSoaServiceValidationServlet extends SlingAllMethodsServlet {
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8616330297710203241L;

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(QREpasSoaServiceValidationServlet.class);

    /** The admin service. */
    @Reference
    private transient AdminService adminService;

    @Reference
    private transient QRService qrService;
	
	@OSGiService
    private transient AESService aESService;


   @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        LOGGER.debug("QREpasSoaServiceValidationServlet :: doPost() :: Started");
        try  {

            final String serialNumber = request.getParameter(QRConstants.SERIAL_NUMBER);
            final String catalogNumber = request.getParameter(QRConstants.CATALOG_NUMBER);
            //We can use this servlet for auth check and brand protection using action and writing condition accordingly
            
            final String action = request.getParameter(QRConstants.ACTION);
            JsonObject qrCodeJsonObject = null;
            final Map<String,String> headerMap = CommonUtil.getRequestHeadersMap(request);
            if( action.equals(QRConstants.VALIDATE_SERIAL_NUMBER_MANUAL_FLOW)){
                qrCodeJsonObject = qrService.validateSerialNumber( serialNumber, StringUtils.EMPTY,catalogNumber, headerMap );

            } else if(action.equals(QRConstants.VALIDATE_AUTH_CODE)) {
            	final String authCode = request.getParameter(QRConstants.AUTH_CODE);
            	qrCodeJsonObject = qrService.validateAuthCode( authCode, serialNumber, catalogNumber, headerMap );

            }else if( action.equals(QRConstants.REPORT_ISSUE)){
            		final String fullName = request.getParameter(QRConstants.REPISSUE_FULL_NAME);
                	final String email = request.getParameter(QRConstants.REPISSUE_EMAIL);
                	final String comments = request.getParameter(QRConstants.REPISSUE_COMMENTS);
                	final String eventID = request.getParameter(QRConstants.REPISSUE_EVENTID);
                	final String authCode = request.getParameter(QRConstants.REPISSUE_AUTHCODE);
                	final String repIssueEmail = request.getParameter(QRConstants.REPISSUE_REPEMAIL);
                	LOGGER.debug("QREpasSoaServiceValidationServlet :: doPost() :: repIssueEmail :: {}", repIssueEmail);
                
            		qrCodeJsonObject = qrService.reportIssue(fullName, email, comments, serialNumber, catalogNumber, eventID,authCode,repIssueEmail);
            	
            		
            }
            response.setContentType(CommonConstants.APPLICATION_JSON);
            response.getWriter().print(qrCodeJsonObject);
            response.getWriter().flush();
            LOGGER.debug("QREpasSoaServiceValidationServlet :: doPost() :: Ended");
        } catch (Exception exception){
            LOGGER.error("Exception in QREpasSoaServiceValidationServlet: {}", exception.getMessage());

        }
    }

}
