package com.eaton.platform.core.workflows;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.Workflow;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;

public class EatonPublishWorkflowProcessTest {

    @Mock
    private Replicator replicator;

    @Mock
    private WorkflowSession workflowSession;

    @Mock
    private Session session;


    @Mock
    private WorkItem workItem;

    @Mock
    private WorkflowData workflowData;

    @Mock
    private MetaDataMap metaDataMap;

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private UserManager userManager;

    @InjectMocks
    private EatonPublishWorkflowProcess workflowProcess;

    @Mock
    Node node;

    @Mock
    Authorizable authorizable;


    @Mock
    private Workflow workflow;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testExecute() throws WorkflowException, PathNotFoundException, RepositoryException, ReplicationException, LoginException {
        Workflow workflow=mock(Workflow.class);
        WorkItem workItem=mock(WorkItem.class);

         // Mocking necessary objects and behavior
        when(workItem.getWorkflowData()).thenReturn(workflowData);
        when(workflowSession.adaptTo(ResourceResolver.class)).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(UserManager.class)).thenReturn(userManager);
        when(workflowSession.adaptTo(Session.class)).thenReturn(session);
        when(workflowData.getPayload()).thenReturn("/content/sample/page");
        when(workItem.getWorkflow()).thenReturn(workflow);
        when(workflow.getInitiator()).thenReturn("eatonwriteuser");
        when(authorizable.getID()).thenReturn("eatonwriteuser");
        when(userManager.getAuthorizable("eatonwriteuser")).thenReturn(authorizable);
        when(session.getNode("/content/sample/page/jcr:content")).thenReturn(node);

        // Executing the method under test
        workflowProcess.execute(workItem, workflowSession, metaDataMap);

        // Verifying that the replicate method was called with the expected arguments
        verify(replicator).replicate(session, ReplicationActionType.ACTIVATE, "/content/sample/page");
        assertEquals(workflow.getInitiator(),"eatonwriteuser");
        assertEquals(workflowData.getPayload(),"/content/sample/page");

  }
}
