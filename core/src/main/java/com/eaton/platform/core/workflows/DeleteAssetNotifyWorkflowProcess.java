package com.eaton.platform.core.workflows;

import com.adobe.acs.commons.email.EmailService;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.services.AdminService;
import org.apache.sling.api.resource.ResourceResolver;
import com.day.cq.commons.Externalizer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

import static com.eaton.platform.core.constants.CommonConstants.*;

@Component(service = WorkflowProcess.class,immediate = true,
		property = {
				AEMConstants.SERVICE_DESCRIPTION + "Notify deleted asset to PDH team",
				AEMConstants.SERVICE_VENDOR_EATON,
		})
public class DeleteAssetNotifyWorkflowProcess implements WorkflowProcess {

	private static final Logger log = LoggerFactory.getLogger(DeleteAssetNotifyWorkflowProcess.class);

	//Email Service Reference
	@Reference
	private EmailService emailService;

	@Reference
	private AdminService adminService;

	/** The target lang. */
	private static String targetLang;

	/**
	 * externalizer
	 */
	@Reference
	private Externalizer externalizer;
	@Override
	public void execute (WorkItem workItem, WorkflowSession workflowSession, MetaDataMap args) throws
			WorkflowException {
		try (ResourceResolver resourceResolver = adminService.getReadService()) {
			String mailRecipients = null;
			String emailTemplatePath = null;

			Map<String, String> emailParams = new HashMap<>();
			if (workItem.getContentPath().startsWith(CONTENT_DAM_NO_EXTRA_SLASH)) {
				emailParams.put(CONTENT_DAM_IMAGE_PATH,
						externalizer.externalLink(resourceResolver, Externalizer.AUTHOR,
								"/assetdetails.html" + workItem.getContentPath()));
			}

			log.info("workflow metadata for key PROCESS_ARGS and path {}", emailParams);
			String processArgs = args.get("PROCESS_ARGS", "string");
			log.info("workflow metadata for key PROCESS_ARGS and string {}", args.get("PROCESS_ARGS", "string"));

			String[] processArgsParam = processArgs.split("\r\n");
			String template = processArgsParam[0];
			String recipent = processArgsParam[1];

			mailRecipients = recipent.replace(DAM_NOTIFY_RECIPIENTS, "");
			emailTemplatePath = template.replace(CONTENT_DAM_EMAIL_TEMPLATE, "");

			try {
				// Array of email recipients
				String[] recipients = mailRecipients.split(",");
				emailService.sendEmail(emailTemplatePath, emailParams, recipients);
			} catch (Exception e) {
				// If an error occurs that prevents the Workflow from completing/continuing - Throw a WorkflowException
				// and the WF engine will retry the Workflow later (based on the AEM Workflow Engine configuration).
				log.error("Unable to complete processing the Workflow Process step", e);
				throw new WorkflowException("Unable to complete processing the Workflow Process step", e);
			}
		}
		catch (Exception e){
			log.error("Exception in getCategoryTagsFromResources() {} ",e.getMessage());
		}

	}
}