package com.eaton.platform.core.workflows;

import com.adobe.acs.commons.email.EmailService;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.commons.Externalizer;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.AssetDownloadConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.workflows.config.EmailLinkProcessConfig;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(service = com.adobe.granite.workflow.exec.WorkflowProcess.class,immediate = true,
        property = {
                AEMConstants.SERVICE_DESCRIPTION + "Eaton asset download email link process workflow",
                AEMConstants.SERVICE_DESCRIPTION + "Eaton asset download email link process workflow",
                AEMConstants.SERVICE_VENDOR_EATON,
        })
@Designate(ocd = EmailLinkProcessConfig.class)
public class EmailLinkProcess implements WorkflowProcess {

    private static final Logger LOG = LoggerFactory.getLogger(EmailLinkProcess.class);

    private String externalizerDomain;

    @Reference
    private AdminService adminService;

    @Reference
    private EmailService emailService;

    @Reference
    private Externalizer externalizer;

    @Activate
    protected void activate(final EmailLinkProcessConfig config) {
        this.externalizerDomain = config.externalizer_domain();
    }
    @Override
    public void execute(final WorkItem item, final WorkflowSession workflowSession, final MetaDataMap args) throws WorkflowException {
        LOG.info("Enter in email process workflow");
        final MetaDataMap wfMetaData = item.getWorkflowData().getMetaDataMap();

        try(ResourceResolver resourceResolver = adminService.getWriteService()) {
            final String downloadPackageIdentifier = wfMetaData.get(AssetDownloadConstants.PARAMETER_KEY_DOWNLOAD_PACKAGE_IDENTIFIER, String.class);
            final String[] mailRecipientsInArray = wfMetaData.get(AssetDownloadConstants.PARAMETER_KEY_EMAIL_TO_RECIPIENTS, new String[0]);
            final String emailTemplatePath = wfMetaData.get(AssetDownloadConstants.EMAIL_TEMPLATE).toString();
            final String emailLinkPath = wfMetaData.get(AssetDownloadConstants.PARAMETER_KEY_EMAIL_LINK_PATH).toString();

            final String downloadLink = externalizer.externalLink(resourceResolver, externalizerDomain, getEmailDownloadLink(emailLinkPath, downloadPackageIdentifier));
            final Map<String, String> emailParams = getEmailParameters(wfMetaData, downloadLink);

            LOG.info("Initiate the email");
            final List<String> participantList = emailService.sendEmail(emailTemplatePath, emailParams, mailRecipientsInArray);

            if (!participantList.isEmpty() && participantList.size() > 0) {
                throw new WorkflowException("Unable to process because email is not shout out ");
            }

            wfMetaData.remove(AssetDownloadConstants.PARAMETER_KEY_EMAIL_TO_RECIPIENTS);

            LOG.info("End the Email: " + participantList);
        }
    }

    private Map<String, String> getEmailParameters(final MetaDataMap emailDataMap, final String emailDownloadLink) {
        Map<String, String> emailParams = new HashMap<>();
        if (emailDataMap.containsKey(AssetDownloadConstants.EMAIL_SENDER_ADDRESS)) {
            emailParams.put(AssetDownloadConstants.SENDER_EMAIL_ADDRESS,
                    emailDataMap.get(AssetDownloadConstants.EMAIL_SENDER_ADDRESS).toString());
        }
        if (emailDataMap.containsKey(AssetDownloadConstants.EMAIL_SENDER_NAME)) {
            emailParams.put(AssetDownloadConstants.SENDER_NAME,
                    emailDataMap.get(AssetDownloadConstants.EMAIL_SENDER_NAME).toString());
        }
        if (emailDataMap.containsKey(AssetDownloadConstants.EMAIL_SUBJECT)) {
            emailParams.put(AssetDownloadConstants.SUBJECT,
                    emailDataMap.get(AssetDownloadConstants.EMAIL_SUBJECT).toString());
        }
        emailParams = setEmailParameters(emailDataMap,AssetDownloadConstants.EMAIL_NEWS_LETTER_LINK,emailParams);
        emailParams = setEmailParameters(emailDataMap,AssetDownloadConstants.EMAIL_CONTACT_US_LINK,emailParams);
        emailParams = setEmailParameters(emailDataMap,AssetDownloadConstants.EMAIL_SUBMITTAL_TOOL_LINK,emailParams);
        emailParams = setEmailParameters(emailDataMap,AssetDownloadConstants.EMAIL_WHERE_TO_BUY_LINK,emailParams);
        emailParams.put(AssetDownloadConstants.MAIL_LINK, emailDownloadLink);
        return emailParams;
    }

    private String getEmailDownloadLink(final String emailLinkPath, final String downloadPackageIdentifier) {
        final StringBuilder emailDownloadLinkBuilder = new StringBuilder();
        emailDownloadLinkBuilder.append(emailLinkPath).append(AssetDownloadConstants.QUESTION_MARK).
                append(AssetDownloadConstants.PARAMETER_KEY_DOWNLOAD_PACKAGE_ID).
                append(AssetDownloadConstants.EQUALS).append(downloadPackageIdentifier);
        return emailDownloadLinkBuilder.toString();
    }

    private Map<String, String> setEmailParameters(final MetaDataMap emailDataMap, final String emailParamKey,
                                                   final Map<String, String> emailParams) {
        if (emailDataMap.containsKey(emailParamKey)) {
            emailParams.put(emailParamKey,
                    emailDataMap.get(emailParamKey).toString());
        }
        return emailParams;
    }
}