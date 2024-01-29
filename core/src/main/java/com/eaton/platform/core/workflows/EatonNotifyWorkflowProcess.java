package com.eaton.platform.core.workflows;

import com.adobe.acs.commons.email.EmailService;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component(service = com.adobe.granite.workflow.exec.WorkflowProcess.class,immediate = true,
		property = {
				AEMConstants.SERVICE_DESCRIPTION + "Notify country owners of content update",
				AEMConstants.SERVICE_VENDOR_EATON,
		})
public class EatonNotifyWorkflowProcess implements WorkflowProcess{
	private static final Logger log = LoggerFactory.getLogger(EatonNotifyWorkflowProcess.class);
	
	//Email Service Reference
	@Reference
	private EmailService emailService;

	/** The target lang. */
	private static String targetLang;
	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap args) throws WorkflowException {
			log.info("Here in execute method of EatonNotifyWorkflowProcess");    //ensure that the execute method is invoked
			final WorkflowData workflowData = workItem.getWorkflowData();
	        final String type = workflowData.getPayloadType();
	        String mailRecipients = null;
	        String emailTemplatePath = null;

	        // Check if the payload is a path in the JCR; The other (less common) type is JCR_UUID
	        if (!StringUtils.equals(type, "JCR_PATH")) {
	            return;
	        }
	        
	        // Get the path to the JCR resource from the payload
	        final String path = workflowData.getPayload().toString();	        

	        log.info("workflow metadata for key PROCESS_ARGS and path {}",path);	
	        log.info("workflow metadata for key PROCESS_ARGS and type {}",type);
	        
	        if(null != path){
	        	if(path.contains("fr-fr")){
	        		targetLang = "French";
	        	} else if(path.contains("nl-nl")){
	        		targetLang = "Dutch";
	        	}
	        	
	        }
	        
	        String processArgs = args.get("PROCESS_ARGS", "string");
	        
	        log.info("workflow metadata for key PROCESS_ARGS and string {}",args.get("PROCESS_ARGS","string"));	        
			String[] processArgsParam = processArgs.split("],");
			String recipent =processArgsParam[0];
			String template = processArgsParam[1];
			mailRecipients =recipent.replace("sendTo:[", "");
			emailTemplatePath=template.replace("emailTemplate:", "");
			
	        try {
	        	
				// Array of email recipients
				String[] recipients =  mailRecipients.split(",");
	            sendNotification(recipients,emailTemplatePath);

	         
	        } catch (Exception e) {
	            // If an error occurs that prevents the Workflow from completing/continuing - Throw a WorkflowException
	            // and the WF engine will retry the Workflow later (based on the AEM Workflow Engine configuration).

	            log.error("Unable to complete processing the Workflow Process step", e);

	            throw new WorkflowException("Unable to complete processing the Workflow Process step", e);
	        }
		
	}
	
	/**
	 * This method is to send Notification to recipients 
	 * when Input Path or Input file does not exists.
	 * @param mailRecipients - List of Recipients
	 * @param fileSourcepath - Input Path String
	 * @param emailParams 
	 */
	private void sendNotification(String[] recipients, String emailTemplatePath) {
						
		// emailService.sendEmail(..) returns a list of all the recipients that
		// could not be sent the email
		// An empty list indicates 100% success
		 Map<String, String> emailParams=getEmailParams();
		 
		 log.info("workflow metadata for key PROCESS_ARGS and value {}",emailTemplatePath);
		List<String> participantList = emailService.sendEmail(emailTemplatePath, emailParams, recipients);
		
		if (participantList.isEmpty()) {
			log.info("Email sent successfully to the recipients");
		} else {
			log.info("Email sent failed to Recipients ");
			for (String itr : participantList) 
				log.info(itr);
			}		
	}
	
    /**
     * This method is to return email parameters
     * @return Map of email parameters
     */
    public static   Map<String, String> getEmailParams(){
    	
    	// Set the dynamic variables of email template
 		Map<String, String> emailParams = new HashMap<>();
 		// Customize the sender email address - if required
 		emailParams.put("senderEmailAddress","translationteam@eaton.com");
 		emailParams.put("senderName", "Eaton Translation Team");
 		emailParams.put("recipientName", "Eaton Team");	
 		emailParams.put("subject", "Eaton Content Translation");
 		String messageStr = null;
 		messageStr = "The Requested Page or Tag has been Translated.";
 		if(null!=targetLang){
 			messageStr = messageStr.concat("Target Language is "+targetLang);
 		}
 		emailParams.put("message", messageStr);
 		
 		return emailParams;
    	
    }



}
