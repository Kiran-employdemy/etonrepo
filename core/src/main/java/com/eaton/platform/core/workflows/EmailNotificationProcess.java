package com.eaton.platform.core.workflows;

import com.adobe.acs.commons.email.EmailService;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Value;
import java.util.*;


@Component(service = com.adobe.granite.workflow.exec.WorkflowProcess.class,immediate = true,
        property = {
                AEMConstants.SERVICE_DESCRIPTION + "Email notification process workflow",
                AEMConstants.SERVICE_VENDOR_EATON,
        })
public class EmailNotificationProcess implements WorkflowProcess {

    private static final Logger LOG = LoggerFactory.getLogger(EmailNotificationProcess.class);

    @Reference
    private transient AdminService adminService;

    @Reference
    private EmailService emailService;

    @Override
    public void execute(final WorkItem item, final WorkflowSession workflowSession, final MetaDataMap args) throws WorkflowException {
        try {

            String emailTemplatePath =StringUtils.EMPTY;
            String emailGroup =StringUtils.EMPTY;
            final Date timeStarted = item.getTimeStarted();
            final Map<String, String> emailParams = new HashMap<>();
            final MetaDataMap metaDataMap = item.getWorkflowData().getMetaDataMap();
            final String sendEmail = metaDataMap.get(CommonConstants.SEND_EMAIL).toString();
            metaDataMap.put(CommonConstants.TIME_STARTED,timeStarted);
            List<String> mailRecipientsInArray = new ArrayList<>();
            if(CommonConstants.SEND_EMAIL_FOR_ROLL_OUT.equals(sendEmail) || CommonConstants.SEND_EMAIL_FOR_PUBLISH.equals(sendEmail)) {
                if (null != metaDataMap) {
                    emailParams.put(CommonConstants.PAGE_LINK, metaDataMap.get(CommonConstants.PAGE_LINK).toString() + CommonConstants.HTML_EXTN);
                    emailParams.put(CommonConstants.NOTIFICATION_LINK, metaDataMap.get(CommonConstants.NOTIFICATION_LINK).toString());
					emailParams.put(CommonConstants.LANGUAGE_WITH_COUNTRY, metaDataMap.get(CommonConstants.LANGUAGE_WITH_COUNTRY).toString());
                    emailTemplatePath = metaDataMap.get(CommonConstants.EMAIL_TEMPLATE_FOLDER_PATH).toString();
                    emailGroup = metaDataMap.get(CommonConstants.EMAIL_GROUP_FOR_NOTIFICATION).toString();
                    mailRecipientsInArray = getEmailRecipientsFromGroup(emailGroup);
                }
                if (CollectionUtils.isNotEmpty(mailRecipientsInArray)) {
                    final String[] EmailArr = mailRecipientsInArray.toArray(new String[mailRecipientsInArray.size()]);
                    final List<String> participantList = emailService.sendEmail(emailTemplatePath, emailParams, EmailArr);
                    if (!participantList.isEmpty() && participantList.size() > 0) {
                        throw new WorkflowException("Unable to process because email is not shout out ");
                    }
                }
            }
        }catch (WorkflowException e){
            throw new WorkflowException("Unable to process further workflow from EmailNotificationProcess in WorkflowException::: "+e.getMessage());
        }
    }
    private List<String> getEmailRecipientsFromGroup(final String emailGroup) throws WorkflowException {
        final List<String> emailRecipientsFromGroupInArray = new ArrayList<>();
        try (ResourceResolver writeService = adminService.getWriteService()){
            LOG.info("Enter in email process workflow");
            final UserManager userManager = writeService.adaptTo(UserManager.class);
            final Group group = (Group) (userManager.getAuthorizable(emailGroup));
            final Iterator itr = group.getMembers();
            while (itr.hasNext()) {
                final Object obj = itr.next();
                if (obj instanceof User) {
                    final User user = (User) obj;
                    final Authorizable userAuthorization = userManager.getAuthorizable(user.getID());
                    if (userAuthorization.hasProperty(CommonConstants.EMAIL)) {
                        final Value[] emails = userAuthorization.getProperty(CommonConstants.EMAIL);
                        for (Value email : emails) {
                            emailRecipientsFromGroupInArray.add(email.getString());
                        }
                    }else {
                        LOG.error("This user does not have email-id:::: "+user.getID()+" in this Group #### "+group);
                    }
                }
            }
        }catch (RepositoryException e){
            throw new WorkflowException("Unable to process further workflow from getEmailProcess in RepositoryException::: "+e.getMessage());
        }
        return emailRecipientsFromGroupInArray;
    }
}
