package com.eaton.platform.core.workflows;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.jcr.Node;
import javax.jcr.RangeIterator;
import javax.jcr.Session;

import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.Workflow;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.adobe.livecycle.content.repository.exception.RepositoryException;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import com.day.cq.wcm.msm.api.RolloutManager;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith({AemContextExtension.class,MockitoExtension.class})
public class EatonContentRolloutProcessTest {
    AemContext aemContext=new AemContext(ResourceResolverType.JCR_MOCK);

    @InjectMocks
    EatonContentRolloutProcess ec=new EatonContentRolloutProcess();

    @Mock
    RolloutManager rolloutManager;

    @Mock
    AdminService adminService;

    @Mock
    LiveRelationshipManager liveRelationshipManager;

    @Mock
    private WorkItem workItem;

    @Mock
    private WorkflowSession workflowSession;

    @Mock
    private MetaDataMap metaDataMap;

    @Mock
    private WorkflowData workflowData;

    @Mock
    Session session;

    @Mock
    private UserManager userManager;

    @Mock
    private Authorizable authorizable;

    @Mock
    Resource resource;

    @Mock
    ResourceResolver writeService;

    @Mock
    Workflow workflow;

    @Mock
    PageManager pageManager;

    @Mock
    Page page;

    @Mock
    Node node;

    @Mock
    LiveRelationship liveCopyPath;

    @Mock
    RolloutManager.RolloutParams rolloutParams;

    @Mock
    RangeIterator liveRelationships;

    String path="/content/testPage";


    @Test
    void testExecute() throws RepositoryException, WorkflowException, javax.jcr.RepositoryException, WCMException {
        when(adminService.getWriteService()).thenReturn(writeService);
        when(writeService.adaptTo(Session.class)).thenReturn(session);
        when(writeService.adaptTo(UserManager.class)).thenReturn(userManager);
        when(workItem.getWorkflowData()).thenReturn(workflowData);
        when(workflowData.getPayload()).thenReturn(path);

        when(workItem.getWorkflow()).thenReturn(workflow);
        when(workflow.getInitiator()).thenReturn("initiatorUserId");
        //when(userManager.getAuthorizable(authorizable.getID())).thenReturn(authorizable);
        when(userManager.getAuthorizable(workItem.getWorkflow().getInitiator())).thenReturn(authorizable);
        when(authorizable.getID()).thenReturn("userId");
        when(userManager.getAuthorizable(authorizable.getID())).thenReturn(authorizable);
        when(writeService.adaptTo(PageManager.class)).thenReturn(pageManager);
        when(pageManager.getPage(path)).thenReturn(page);
        when(session.getNode(path +CommonConstants.SLASH_STRING+ JcrConstants.JCR_CONTENT)).thenReturn(node);
        when(writeService.getResource(path)).thenReturn(resource);
        when(liveRelationshipManager.getLiveRelationships(resource, null, RolloutManager.Trigger.ROLLOUT)).thenReturn(liveRelationships);
        when(liveRelationships.hasNext()).thenReturn(true).thenReturn(false);
        when(liveRelationships.next()).thenReturn(liveCopyPath);
        when(liveCopyPath.getTargetPath()).thenReturn(path);

        ec.execute(workItem,workflowSession,metaDataMap);



    }
}
