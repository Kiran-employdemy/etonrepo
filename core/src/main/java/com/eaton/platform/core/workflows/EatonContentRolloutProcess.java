package com.eaton.platform.core.workflows;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.wcm.api.Page;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Node;
import com.day.cq.wcm.msm.api.RolloutManager;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RangeIterator;
import java.util.Objects;


@Component(service = WorkflowProcess.class,immediate = true,
        property = {
                AEMConstants.SERVICE_DESCRIPTION + "Eaton Rollout Workflow Service",
                AEMConstants.SERVICE_VENDOR_EATON,
        })
public class EatonContentRolloutProcess implements WorkflowProcess {

    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(EatonContentRolloutProcess.class);

    @Reference
    private RolloutManager rolloutManager;

    @Reference
    private AdminService adminService;

    @Reference
    private LiveRelationshipManager liveRelationshipManager;


    @Override
    public void execute(WorkItem item, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
        try(ResourceResolver writeService = adminService.getWriteService()) {
                String currentUserID = StringUtils.EMPTY;
    UserManager userManager = writeService.adaptTo(UserManager.class);
    Authorizable authorizable;
        authorizable = userManager.getAuthorizable(item.getWorkflow().getInitiator());
        if (null != userManager.getAuthorizable(authorizable.getID())) {
            currentUserID = authorizable.getID();
        }
            final WorkflowData workflowData = item.getWorkflowData();
            final PageManager pageManager = writeService.adaptTo(PageManager.class);
            final String path = workflowData.getPayload().toString();
            final boolean rollOutPageFlag = rolloutPage(path, writeService, pageManager,currentUserID);
            if(!rollOutPageFlag){
                throw new WorkflowException("Unable to process further workflow from RolloutWorkflow Process : RolloutManager IS NULL");
            }
        }catch (RepositoryException e) {
            LOG.error("Repo login exception",e);
        }
    }
     
    private boolean rolloutPage(String pagePath, ResourceResolver writeService, PageManager pageManager, String currentUserID) throws WorkflowException, RepositoryException {
        boolean rollOutPageFlag = Boolean.FALSE;
        try {
            if (Objects.nonNull(rolloutManager) && Objects.nonNull(pageManager)) {
                final Page currentPage = pageManager.getPage(pagePath);
                final RolloutManager.RolloutParams rolloutParams = new RolloutManager.RolloutParams();
                rolloutParams.isDeep = false;
                rolloutParams.master = currentPage;
                rolloutParams.reset = false;
                rolloutParams.trigger = RolloutManager.Trigger.ROLLOUT;
                rolloutManager.rollout(rolloutParams);
                rollOutPageFlag = Boolean.TRUE;
                final Session session = writeService.adaptTo(Session.class);
                Node jcrNode = session.getNode(pagePath +CommonConstants.SLASH_STRING+ JcrConstants.JCR_CONTENT);
                if (jcrNode!=null) {
                    jcrNode.setProperty("cq:lastRolledoutBy", currentUserID);
                    jcrNode.setProperty("cq:lastModifiedBy",currentUserID);
                    session.save();
                }
                final Resource resource = writeService.getResource(pagePath);
                final RangeIterator liveRelationships = liveRelationshipManager.getLiveRelationships(resource, null, RolloutManager.Trigger.ROLLOUT);
                while (liveRelationships.hasNext()) {
                	
                    final LiveRelationship liveCopyPath = (LiveRelationship) liveRelationships.next();
                    final String targetPath = liveCopyPath.getTargetPath();
                    rolloutPage(targetPath, writeService, pageManager,currentUserID);
                }
            }else {
                LOG.debug("RolloutWorkflow Process : RolloutManager IS NULL");
            }
            } catch (WCMException e) {
                throw new WorkflowException("Unable to process further workflow from RolloutWorkflow Process : RolloutManager IS NULL");
            }
        return rollOutPageFlag;
    }


}


