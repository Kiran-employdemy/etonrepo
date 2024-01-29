package com.eaton.platform.core.services.impl;

import com.adobe.acs.commons.email.EmailService;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.EatonEmailService;
import com.eaton.platform.core.services.config.EatonEmailServiceConfig;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@Component(service = EatonEmailService.class, immediate = true,
        property = {
                AEMConstants.SERVICE_VENDOR_EATON,
                AEMConstants.SERVICE_DESCRIPTION + "EatonEmailServiceImpl",
                AEMConstants.PROCESS_LABEL + "EatonEmailServiceImpl"
        })
public class EatonEmailServiceImpl implements EatonEmailService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EatonEmailServiceImpl.class);
    private static final String SENDER_EMAIL_ADD = "senderEmailAddress";
    private static final String SENDER_NAME = "senderName";
    private String fromAddress;
    private String senderPreName;


    @Reference
    private EmailService emailService;

    @Activate
    @Modified
    protected final void activate(final EatonEmailServiceConfig config) {
        fromAddress = config.fromAddress();
        senderPreName = config.senderPreName();
    }

    public String sendEmail(String emailAddress, Map<String, String> emailParams, String templatePath) {
        if (null != templatePath && null != emailAddress && null != emailParams) {
            emailParams.put(SENDER_EMAIL_ADD, fromAddress);
            emailParams.put(SENDER_NAME, senderPreName);
            List<String> emailResponse = emailService.sendEmail(templatePath, emailParams, emailAddress);
            if (emailResponse.isEmpty()) {
                LOGGER.info("EatonEmailServiceImpl: sendEmail() :: Email sent successfully to the recipients");
                return CommonConstants.EMAIL_STATUS_SUCCESS;
            }
            LOGGER.info("EatonEmailServiceImpl: sendEmail() :: Email sent failed");
            return CommonConstants.EMAIL_STATUS_FAIL;
        }
        return CommonConstants.EMAIL_STATUS_FAIL;
    }

    @Override
    public String getFromAddress() {
        return fromAddress;
    }

    @Override
    public String getSenderPreName() {
        return senderPreName;
    }
}
