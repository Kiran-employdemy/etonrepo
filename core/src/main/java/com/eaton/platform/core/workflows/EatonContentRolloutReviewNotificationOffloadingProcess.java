package com.eaton.platform.core.workflows;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.adobe.granite.workflow.model.WorkflowModel;
import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import com.day.cq.wcm.msm.api.RolloutManager;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.impl.EatonNotifyServiceFactoryConfig;
import com.eaton.platform.core.workflows.config.EatonContentRolloutReviewNotificationOffloadingProcessConfig;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RangeIterator;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Map;

@Component(service = WorkflowProcess.class,immediate = true,
        property = {
                AEMConstants.SERVICE_DESCRIPTION + "Eaton Content Rollout Review Notification Offloading process workflow",
                AEMConstants.SERVICE_VENDOR_EATON,
        })
@Designate(ocd = EatonContentRolloutReviewNotificationOffloadingProcessConfig.class)
public class EatonContentRolloutReviewNotificationOffloadingProcess implements WorkflowProcess {

    private static final Logger LOG = LoggerFactory.getLogger(EatonContentRolloutReviewNotificationOffloadingProcess.class);
    private String externalizerDomain;

    @Reference
    private ConfigurationAdmin configAdmin;

    @Reference
    private AdminService adminService;

    @Reference
    private LiveRelationshipManager liveRelationshipManager;

    @Reference
    private Externalizer externalizer;

    @Activate
    protected void activate(final EatonContentRolloutReviewNotificationOffloadingProcessConfig config) {
        this.externalizerDomain =  config.externalizer_domain();
    }

    @Override
    public void execute(final WorkItem item, final WorkflowSession workflowSession, final MetaDataMap args) throws WorkflowException {
        try(ResourceResolver adminServiceWriteService = adminService.getWriteService()){
            final WorkflowData workflowData = item.getWorkflowData();
            final MetaDataMap metaDataMap = item.getWorkflowData().getMetaDataMap();
            final String pagePath = workflowData.getPayload().toString();
            final Resource resource = adminServiceWriteService.getResource(pagePath);
            final RangeIterator liveRelationships = liveRelationshipManager.getLiveRelationships(resource, null, RolloutManager.Trigger.ROLLOUT);
            final Configuration[] configurations = configAdmin.listConfigurations(CommonConstants.SERVICE_PID);
            startWorkflowForEachCountryGroup(configurations,liveRelationships, workflowSession,metaDataMap,adminServiceWriteService);

        } catch (InvalidSyntaxException | IOException | WCMException e) {
            throw new WorkflowException("Unable to process further workflow from EatonGroupChooserWorkflow in WorkflowException::: " + e.getMessage());
        }
    }

    private void startWorkflowForEachCountryGroup(final Configuration[] configurations, final RangeIterator liveRelationships,
                                                  final WorkflowSession wfSession,
                                                  final MetaDataMap metaDataMap, final ResourceResolver adminServiceWriteService) throws WorkflowException {
        try {
            LOG.debug("Inside startWorkflowForEachCountryGroup");
            while (liveRelationships.hasNext()) {
                boolean flgForDefaultCountry = Boolean.TRUE;
                final LiveRelationship liveCopyPath = (LiveRelationship) liveRelationships.next();
                final String targetPath = liveCopyPath.getTargetPath();
                for (final Configuration config : configurations) {
                    final Dictionary<String, Object> properties = config.getProperties();
                    if (targetPath.contains(properties.get(EatonNotifyServiceFactoryConfig.LANGUAGE_WITH_COUNTRY).toString())) {
                        populationgMetaDataCode(metaDataMap, properties);
                        final String countryName = properties.get(EatonNotifyServiceFactoryConfig.ROLLOUT_NOTIFICATION_GROUP).toString();
                        callWorkflowForEachAndDefaultCountry(metaDataMap, countryName, adminServiceWriteService, targetPath, wfSession);
                        flgForDefaultCountry = Boolean.FALSE;
                    }
                }
                if (flgForDefaultCountry) {
                    final Configuration config = configurations[0];
                    final Dictionary<String, Object> prop = config.getProperties();
                    populationgMetaDataCode(metaDataMap, config.getProperties());
                    final String countryName = prop.get(EatonNotifyServiceFactoryConfig.ROLLOUT_NOTIFICATION_GROUP).toString();
                    callWorkflowForEachAndDefaultCountry(metaDataMap, countryName, adminServiceWriteService, targetPath, wfSession);
                }
                final Resource resource = adminServiceWriteService.getResource(targetPath);
                final RangeIterator childLiveRelationships = liveRelationshipManager.getLiveRelationships(resource, null, RolloutManager.Trigger.ROLLOUT);
                startWorkflowForEachCountryGroup(configurations, childLiveRelationships, wfSession, metaDataMap, adminServiceWriteService);
            }
        } catch (WCMException e) {
            throw new WorkflowException("Unable to process further workflow from WCMException Process::: " + e.getMessage());
        }
    }

    private static MetaDataMap populationgMetaDataCode(MetaDataMap metaDataMap, Dictionary<String, Object> properties) {
        metaDataMap.put(EatonNotifyServiceFactoryConfig.LANGUAGE_WITH_COUNTRY,properties.get(EatonNotifyServiceFactoryConfig.LANGUAGE_WITH_COUNTRY));
        metaDataMap.put(CommonConstants.EMAIL_TEMPLATE_FOLDER_PATH,properties.get(EatonNotifyServiceFactoryConfig.EMAIL_TEMPLATE_ROLL_OUT));
        metaDataMap.put(EatonNotifyServiceFactoryConfig.EMAIL_TEMPLATE_PUBLISH,properties.get(EatonNotifyServiceFactoryConfig.EMAIL_TEMPLATE_PUBLISH));
        metaDataMap.put(EatonNotifyServiceFactoryConfig.ROLLOUT_NOTIFICATION_GROUP,properties.get(EatonNotifyServiceFactoryConfig.ROLLOUT_NOTIFICATION_GROUP));
        metaDataMap.put(EatonNotifyServiceFactoryConfig.PUBLISH_NOTIFICATION_GROUP,properties.get(EatonNotifyServiceFactoryConfig.PUBLISH_NOTIFICATION_GROUP));
        return metaDataMap;
    }

    private void callWorkflowForEachAndDefaultCountry(MetaDataMap metaDataMap, String countyGroupName, ResourceResolver writeService,
                                                      String targetPath, WorkflowSession wfSession) throws WorkflowException {
        LOG.debug("inside callWorkflowForEachAndDefaultCountry targetPath::::{}",targetPath);
        metaDataMap.put(CommonConstants.EMAIL_GROUP_FOR_NOTIFICATION, countyGroupName);
        final String pageLink = externalizer.externalLink(writeService, externalizerDomain, targetPath);
        final String notificationLink = externalizer.externalLink(writeService, externalizerDomain, CommonConstants.NOTIFICATION_INBOX_LINK);
        metaDataMap.put(CommonConstants.PAGE_LINK, pageLink);
        metaDataMap.put(CommonConstants.NOTIFICATION_LINK, notificationLink);
        metaDataMap.put(CommonConstants.SEND_EMAIL, CommonConstants.SEND_EMAIL_FOR_ROLL_OUT);
        startNewWorkflow(CommonConstants.EATON_AUTO_PUBLISH_ROLLOUT_FOR_EACH_COUNTRY,
                wfSession, targetPath, metaDataMap);
    }

    private static void startNewWorkflow(final String wfModelId, final WorkflowSession wfSession, final String payload, final Map<String, Object> metaData) throws WorkflowException {
        final WorkflowModel wfModel = wfSession.getModel(wfModelId);
        final WorkflowData wfData = wfSession.newWorkflowData(CommonConstants.JCR_PATH, payload);
        if(wfModel == null || wfData == null) {
            throw new WorkflowException("Error getting workflow");
        } else {
            wfSession.startWorkflow(wfModel, wfData, metaData);
        }
    }


}
