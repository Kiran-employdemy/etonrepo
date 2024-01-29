package com.eaton.platform.core.schedulers;

import com.adobe.acs.commons.email.EmailService;
import com.day.cq.replication.Agent;
import com.day.cq.replication.AgentManager;
import com.day.cq.replication.ReplicationQueue;
import com.day.cq.replication.Replicator;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.config.ReplicationQueueAlertServiceConfig;
import org.apache.sling.commons.scheduler.Scheduler;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This scheduler is send notification if the replication queue is blocked for all the instances
 * By default, will run every 30 minutes.  To adjust, use config.
 *
 * Config: apps/eaton/runmodes/config.author/com.eaton.platform.core.schedulers.ReplicationQueueAlerts.xml
 */
@Component(immediate = true, service = Runnable.class)
@Designate(ocd=ReplicationQueueAlertServiceConfig.class)
public class ReplicationQueueAlerts implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReplicationQueueAlerts.class);
    /**
     * Id of the scheduler based on its name
     */
    private static final String BEST_REGARDS="bestRegards";
    private static final String EMAIL_BODY_TEXT = "emailBody";
    private static final String EMAIL_SUBJECT="emailSubject";
    private static final String NO_REPLY="noReply";
    private static final String TEAM_NAME="teamEaton";

    private int schedulerId;
    @Reference
    private AgentManager agentMgr;

    private String templatePath =CommonConstants.REPLICATION_EMAIL_TEMPLATE_PATH;

    @Reference
    private Replicator replicator;

    @Reference
    private EmailService emailService;

    /**
     * Scheduler instance injected
     */
    @Reference
    private Scheduler scheduler;

    @Reference
    private SlingSettingsService slingSettingsService;

    private String[] agentId;
    private String emailDL;
    private String emailBody;
    private String noReply;
    private String bestRegards;
    private String teamName;
    private boolean enabled;

    private static final String RUNMODE_AUTHOR = "author";

    public boolean isAuthor() {
        return slingSettingsService.getRunModes().contains(RUNMODE_AUTHOR);
    }

    Map<String, String> emailParams = null;

    /**
     * Activate method to initialize stuff
     *
     * @param config
     */
    @Activate
    protected void activate(ReplicationQueueAlertServiceConfig config) {
        schedulerId = config.schdulerName().hashCode();
        this.agentId = config.agentIDs();
        this.emailDL = config.emailDL();
        this.emailBody = CommonConstants.EMAIL_BODY;
        this.noReply = CommonConstants.NO_REPLY;
        this.bestRegards = CommonConstants.BEST_REGARDS;
        this.teamName = CommonConstants.TEAM_NAME;
        this.enabled=config.enabled();
    }

    /**
     * This method adds the scheduler
     */
    private void addScheduler(){
        if(isAuthor() && enabled) {
            LOGGER.info(String.format("addScheduler initiated for ReplicationQueueAlert"));
            LOGGER.info(String.format("Number of Agent ID's : {}" ) , agentId.length);
            if (agentMgr != null) {
                for (String agentIdName : agentId) {
                    LOGGER.info(String.format("Name of Agent ID: {}" ) , agentIdName);
                    Agent agent = agentMgr.getAgents().get(agentIdName);
                    String emailSubject = CommonConstants.EMAIL_SUBJECT + agentIdName;
                    isQueueBlocked(agent,emailSubject);

                }
            }
        }else{
            LOGGER.info("Replication queue alert scheduler is disabled.");
        }
    }

    public void isQueueBlocked(Agent agent,String emailSubject){

        if (agent != null) {
            ReplicationQueue queue = agent.getQueue();
            if (queue != null) {
                @Deprecated
                Boolean isQueueBlocked = queue.isBlocked();
                if (Boolean.TRUE.equals(isQueueBlocked)) {
                    LOGGER.info(String.format("Queue is Blocked  : {}" ) , isQueueBlocked);
                    emailParams = new HashMap<>();
                    emailParams.put(EMAIL_SUBJECT, emailSubject);
                    emailParams.put(EMAIL_BODY_TEXT, emailBody);
                    emailParams.put(NO_REPLY, noReply);
                    emailParams.put(BEST_REGARDS, bestRegards);
                    emailParams.put(TEAM_NAME, teamName);
                    sendEmail(emailDL, emailParams, templatePath);
                }
            }
        }
    }
    /**
     * This method used to send email if the queue is blocked
     * @param emailIdList to Email List
     * @param emailParams Email Parameters
     * @param templatePath email template path
     * @return send status
     */
    public String sendEmail(String emailIdList, Map<String, String> emailParams, String templatePath){
        String status =null;
        // Array of email recipients
        String[] recipients = {emailIdList};
        if(null != templatePath) {
            List<String> emailResponse = emailService.sendEmail(templatePath, emailParams, recipients);
            if (emailResponse.isEmpty()) {
                status = CommonConstants.EMAIL_STATUS_SUCCESS;
                LOGGER.info("ReplicationQueueAlertImpl: sendEmail() :: Email sent successfully to the recipients");
            } else {
                status = CommonConstants.EMAIL_STATUS_FAIL;
                LOGGER.info("ReplicationQueueAlertImpl: sendEmail() :: Email sent failed");
            }
        }
        return status;
    }

    public int getSchedulerId() {
        return schedulerId;
    }


    @Override
    public void run(){
        LOGGER.info("************ReplicationQueueAlertImpl Run method executed for replication queue scheduler ***********");
        addScheduler();
    }

}

