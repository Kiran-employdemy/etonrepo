package com.eaton.platform.core.workflows;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.impl.EatonNotifyServiceFactoryConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component(service = WorkflowProcess.class,immediate = true,
        property = {
                AEMConstants.SERVICE_DESCRIPTION + "Eaton Auto Publish Workflow Process",
                AEMConstants.SERVICE_VENDOR_EATON,
        })
public class EatonAutoPublishWorkflowProcess implements WorkflowProcess {

    private static final Logger LOG = LoggerFactory.getLogger(EatonAutoPublishWorkflowProcess.class);

    @Reference
    private Replicator replicator;

    @Reference
    private LiveRelationshipManager liveRelationshipManager;

    @Reference
    private transient AdminService adminService;

    @Override
    public void execute(final WorkItem item, final WorkflowSession workflowSession, final MetaDataMap args) throws WorkflowException {
        try (ResourceResolver writeService = adminService.getWriteService()){
            LOG.info("############ Enter EatonAutoPublishWorkflowProcess ############");
            final WorkflowData workflowData = item.getWorkflowData();
            final MetaDataMap metaDataMap = workflowData.getMetaDataMap();
            String path = workflowData.getPayload().toString();
            LOG.info("############ EatonAutoPublishWorkflowProcess Workflow Payload Path ############ "+path);
            final Session session = writeService.adaptTo(Session.class);
            final PageManager pageManager = writeService.adaptTo(PageManager.class);
            final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
            final Date currentDate = new Date();
            final Date parseCurrentDate = dateFormat.parse(dateFormat.format(currentDate));
            final long milSecCurrentDateDiff = parseCurrentDate.getTime();
            metaDataMap.put(CommonConstants.EMAIL_TEMPLATE_FOLDER_PATH,metaDataMap.get(EatonNotifyServiceFactoryConfig.EMAIL_TEMPLATE_PUBLISH));
            metaDataMap.put(CommonConstants.EMAIL_GROUP_FOR_NOTIFICATION,metaDataMap.get(EatonNotifyServiceFactoryConfig.PUBLISH_NOTIFICATION_GROUP));
            final boolean liveCopyPagesForPublish = countryPagePublishTimePeriod(writeService, pageManager,
                        session,dateFormat,milSecCurrentDateDiff,metaDataMap,path);
                if(liveCopyPagesForPublish == Boolean.TRUE){
                    metaDataMap.put(CommonConstants.SEND_EMAIL,CommonConstants.SEND_EMAIL_FOR_PUBLISH);
                }else {
                    metaDataMap.put(CommonConstants.SEND_EMAIL,StringUtils.EMPTY);
                }
        }catch (Exception e){
            throw new WorkflowException("Unable to process further workflow from Exception Process::: "+e.getMessage());
        }
    }
    private boolean countryPagePublishTimePeriod(final ResourceResolver writeService, final PageManager pageManager,
                                                 final Session session, final SimpleDateFormat dateFormat, final long milSecCurrentDateDiff,
                                                 final MetaDataMap metaDataMap, final String targetPath) throws WorkflowException {
            boolean liveCopyPageTimePeriod = Boolean.FALSE;
            try {
                if (null != writeService.getResource(targetPath)) {
                    final Page page = pageManager.getPage(targetPath);
                    final ValueMap properties = page.getProperties();
                    final Date lastModifiedDate = properties.get(CommonConstants.CQ_LAST_MODIFIED, Date.class);
                    final Date rollOutDate = properties.get(CommonConstants.CQ_LAST_ROLLED_OUT, Date.class);
                    final Date parseRollOutDate = dateFormat.parse(dateFormat.format(rollOutDate));
                    final Date parseLastModifiedDate = dateFormat.parse(dateFormat.format(lastModifiedDate));
                    long endTimeOut = 0;
                    long initialTimeOut = 0;
                    if(metaDataMap.containsKey(CommonConstants.PARTICIPANT_TIME_OUT) &&  metaDataMap.containsKey(CommonConstants.TIME_STARTED)){
                        final Date absoluteDate = metaDataMap.get(CommonConstants.PARTICIPANT_TIME_OUT, Date.class);
                        final Date parseAbsoluteDate = dateFormat.parse(dateFormat.format(absoluteDate));
                        final Date workflowStaredDate = metaDataMap.get(CommonConstants.TIME_STARTED, Date.class);
                        final Date parseWorkflowStaredDate = dateFormat.parse(dateFormat.format(workflowStaredDate));
                        endTimeOut = parseAbsoluteDate.getTime() - parseWorkflowStaredDate.getTime();
                        initialTimeOut =milSecCurrentDateDiff - parseWorkflowStaredDate.getTime();
                        if(endTimeOut >= 3600000){
                            endTimeOut = endTimeOut/3600000;
                        }else {
                            endTimeOut = 0;
                        }
                        if(initialTimeOut >= 3600000){
                            initialTimeOut = initialTimeOut/3600000;
                        }else {
                            initialTimeOut = 0;
                        }
                    }
                    if (parseLastModifiedDate.toString().equals(parseRollOutDate.toString()) && initialTimeOut >= endTimeOut) {
                           replicator.replicate(session, ReplicationActionType.ACTIVATE, targetPath);
                        liveCopyPageTimePeriod = Boolean.TRUE;
                    }
                }
        } catch (ReplicationException | ParseException e) {
            throw new WorkflowException("Unable to process further workflow from ReplicationException Process::: " + e.getMessage());
        }
        return liveCopyPageTimePeriod;
    }
}
