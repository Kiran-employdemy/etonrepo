package com.eaton.platform.core.workflows;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.Workflow;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;
import com.eaton.platform.core.util.WorkflowUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Assertions.*;

import java.util.Iterator;
import java.util.Set;

class RepublishIfPublishedTest {

    @Mock
    private Logger logger;

    @Mock
    private Replicator replicator;

    @Mock
    private Session session;

    @Mock
    private Node root;

    @Mock
    private Node payloadNode;

    @Mock
    private Node assetNode;

    @Mock
    private Node jcrNode;

    @Mock
    private Property replicationActionProperty;

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private UserManager userManager;

    @Mock
    private Authorizable authorizable;

    @InjectMocks
    private RepublishIfPublished republishIfPublished;

    @Mock
    WorkItem workItem;

    @Mock
    MetaDataMap metaDataMap;

    @Mock
    Workflow workflow;

    @Mock
    private WorkflowSession workflowSession;

    @Mock
    private Set keyset;

    @Mock
    private Object payload;

    @Mock
    WorkflowData workflowData;

    @Mock
    Iterator<String> iterator;

    @Mock
    NodeType nodeType;

    @Mock
    Node parent;

    private final String LAST_REPLICATION_ACTION_PROPERTY = "jcr:content/cq:lastReplicationAction";

    @BeforeEach
    void setUp() throws RepositoryException {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testExecuteRepublishIfPublished() throws RepositoryException, ReplicationException {

         // Mocking behavior for session and nodes\
         
         when(workflowSession.adaptTo(Session.class)).thenReturn(session);
         when(session.getRootNode()).thenReturn(root);
         when(root.getNode("  ")).thenReturn(root);
         when(workItem.getWorkflowData()).thenReturn(workflowData);
         when(workflowData.getPayload()).thenReturn("  ");
        when(WorkflowUtils.getPayloadNode(root, workItem)).thenReturn(payloadNode);

        when(payloadNode.getPath()).thenReturn("/content/asset"); 
        when(payloadNode.getPrimaryNodeType()).thenReturn(nodeType).thenReturn(nodeType);
        when(payloadNode.getParent()).thenReturn(payloadNode).thenReturn(null);
        when(payloadNode.getDepth()).thenReturn(1).thenReturn(0);
        when(nodeType.isNodeType("dam:Asset")).thenReturn(true).thenReturn(true);
    
        assetNode=WorkflowUtils.getAssetNode(payloadNode, logger); 
        
         
         when(assetNode.hasProperty(LAST_REPLICATION_ACTION_PROPERTY)).thenReturn(true);
         when(assetNode.getProperty(LAST_REPLICATION_ACTION_PROPERTY)).thenReturn(replicationActionProperty);
         when(replicationActionProperty.getString()).thenReturn("activate");
         when(assetNode.getNode(JcrConstants.JCR_CONTENT)).thenReturn(jcrNode);
 
         // Mocking behavior for resource resolver
         when(workflowSession.adaptTo(ResourceResolver.class)).thenReturn(resourceResolver);
         when(resourceResolver.adaptTo(UserManager.class)).thenReturn(userManager);
 
         // Mocking behavior for user manager
         
         when(workItem.getWorkflow()).thenReturn(workflow);
         when(workItem.getWorkflow().getInitiator()).thenReturn("initiatorUserId");
         when(userManager.getAuthorizable("initiatorUserId")).thenReturn(authorizable);
         when(authorizable.getID()).thenReturn("initiatorUserId");

         when(workItem.getWorkflowData()).thenReturn(workflowData);
         when(workflowData.getPayload()).thenReturn(
            "  ");
         when(workflowData.getMetaDataMap()).thenReturn(metaDataMap);
         when(workItem.getMetaDataMap()).thenReturn(metaDataMap);
         when(metaDataMap.keySet()).thenReturn(keyset);
         when(keyset.iterator()).thenReturn(iterator);
         when(assetNode.getPath()).thenReturn("   ");

        // Call the method to be tested
        republishIfPublished.execute(workItem, workflowSession, metaDataMap);

        // Verify that the session.save method is called
        verify(session).save();
        verify(replicator).replicate(session, ReplicationActionType.ACTIVATE, assetNode.getPath());
        
    }

    // Add more test cases to cover different scenarios as needed
}