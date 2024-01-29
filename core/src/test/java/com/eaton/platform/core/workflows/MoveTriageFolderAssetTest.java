package com.eaton.platform.core.workflows;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.commons.JcrUtils;
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
import com.day.cq.dam.api.Asset;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.services.AdminService;


import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith({AemContextExtension.class,MockitoExtension.class})
public class MoveTriageFolderAssetTest {
    
    AemContext aemContext=new AemContext(ResourceResolverType.JCR_MOCK);
    MoveTriageFolderAsset mainClass=new MoveTriageFolderAsset();
    
    @Mock
    AdminService adminService;

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
    ResourceResolver adminResourceResolver;

    @Mock
    Workflow workflow;

    @Mock
    PageManager pageManager;

    @Mock
    TagManager tagManager;

    String payloadPath="/content/dam/eaton/triage/amer-legacy-canada.html.zip";

    @InjectMocks
    private MoveTriageFolderAsset moveTriageFolderAsset;

    @BeforeEach
    void setup() throws RepositoryException{
        MockitoAnnotations.initMocks(this);
        // ResourceResolver adminResourceResolver=aemContext.resourceResolver();   
    }
    


    @Test
    void testExecute() throws WorkflowException, RepositoryException {
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("xmp:eaton-product-taxonomy","eaton:product-taxonomy/backup-power,-ups,-surge-&-it-power-distribution/backup-power-(ups)/eaton-ex");
        ResourceResolver rr=aemContext.resourceResolver();
        Asset asset=aemContext.create().asset("/content/dam/eaton/triage/amer-legacy-canada.html.zip", 300, 200, "image/jpeg",map);
        //aemContext.load(true);
       
        Tag tag=aemContext.create().tag("eaton:product-taxonomy/backup-power,-ups,-surge-&-it-power-distribution/backup-power-(ups)/eaton-ex");
        when(session.nodeExists(payloadPath)).thenReturn(true);

        Node assetNode=rr.getResource(asset.getPath()).adaptTo(Node.class);
        when(session.getNode(anyString())).thenReturn(assetNode);
        when(workItem.getWorkflowData()).thenReturn(workflowData);
        when(workflowData.getPayload()).thenReturn(payloadPath);

        when(adminService.getWriteService()).thenReturn(adminResourceResolver);
        when(adminResourceResolver.adaptTo(Session.class)).thenReturn(session);
        when(adminResourceResolver.adaptTo(UserManager.class)).thenReturn(userManager);
        when(adminResourceResolver.adaptTo(TagManager.class)).thenReturn(tagManager);
        when(adminResourceResolver.getResource(assetNode.getPath())).thenReturn(rr.getResource(asset.getPath()));

        when(workItem.getWorkflow()).thenReturn(workflow);
        when(workflow.getInitiator()).thenReturn("initiatorUserId");
        when(userManager.getAuthorizable(workItem.getWorkflow().getInitiator())).thenReturn(authorizable);
        when(authorizable.getID()).thenReturn("userId");
        when(userManager.getAuthorizable(authorizable.getID())).thenReturn(authorizable);
        when( JcrUtils.getOrCreateByPath(null , null, null, session, true)).thenReturn(assetNode);
        when(adminResourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        
        
        moveTriageFolderAsset.execute(workItem,workflowSession,metaDataMap);
        verify(session).save();
    }

    @Test
    void testExecute1() throws WorkflowException, RepositoryException {
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("xmp:eaton-services-taxonomy","eaton:services/service-type/consulting-services");
        ResourceResolver rr=aemContext.resourceResolver();
        Asset asset=aemContext.create().asset("/content/dam/eaton/triage/amer-legacy-canada.html.zip", 300, 200, "image/jpeg",map);
        //aemContext.load(true);
       
        Tag tag=aemContext.create().tag("eaton:services/service-type/consulting-services");
        when(session.nodeExists(payloadPath)).thenReturn(true);

        Node assetNode=rr.getResource(asset.getPath()).adaptTo(Node.class);
        when(session.getNode(anyString())).thenReturn(assetNode);
        when(workItem.getWorkflowData()).thenReturn(workflowData);
        when(workflowData.getPayload()).thenReturn(payloadPath);

        when(adminService.getWriteService()).thenReturn(adminResourceResolver);
        when(adminResourceResolver.adaptTo(Session.class)).thenReturn(session);
        when(adminResourceResolver.adaptTo(UserManager.class)).thenReturn(userManager);
        when(adminResourceResolver.adaptTo(TagManager.class)).thenReturn(tagManager);
        when(adminResourceResolver.getResource(assetNode.getPath())).thenReturn(rr.getResource(asset.getPath()));

        when(workItem.getWorkflow()).thenReturn(workflow);
        when(workflow.getInitiator()).thenReturn("initiatorUserId");
        when(userManager.getAuthorizable(workItem.getWorkflow().getInitiator())).thenReturn(authorizable);
        when(authorizable.getID()).thenReturn("userId");
        when(userManager.getAuthorizable(authorizable.getID())).thenReturn(authorizable);
        when( JcrUtils.getOrCreateByPath(null , null, null, session, true)).thenReturn(assetNode);
        when(adminResourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        
        moveTriageFolderAsset.execute(workItem,workflowSession,metaDataMap);
        verify(session).save();

    }

    @Test
    void testExecute2() throws WorkflowException, RepositoryException {
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("xmp:eaton-support-taxonomy","eaton:support-taxonomy/business-resources/consultants-engineers/design-guides-cwc");
        ResourceResolver rr=aemContext.resourceResolver();
        Asset asset=aemContext.create().asset("/content/dam/eaton/triage/amer-legacy-canada.html.zip", 300, 200, "image/jpeg",map);
        //aemContext.load(true);
       
        Tag tag=aemContext.create().tag("eaton:support-taxonomy/business-resources/consultants-engineers/design-guides-cwc");
        when(session.nodeExists(payloadPath)).thenReturn(true);

        Node assetNode=rr.getResource(asset.getPath()).adaptTo(Node.class);
        when(session.getNode(anyString())).thenReturn(assetNode);
        when(workItem.getWorkflowData()).thenReturn(workflowData);
        when(workflowData.getPayload()).thenReturn(payloadPath);

        when(adminService.getWriteService()).thenReturn(adminResourceResolver);
        when(adminResourceResolver.adaptTo(Session.class)).thenReturn(session);
        when(adminResourceResolver.adaptTo(UserManager.class)).thenReturn(userManager);
        when(adminResourceResolver.adaptTo(TagManager.class)).thenReturn(tagManager);
        when(adminResourceResolver.getResource(assetNode.getPath())).thenReturn(rr.getResource(asset.getPath()));

        when(workItem.getWorkflow()).thenReturn(workflow);
        when(workflow.getInitiator()).thenReturn("initiatorUserId");
        when(userManager.getAuthorizable(workItem.getWorkflow().getInitiator())).thenReturn(authorizable);
        when(authorizable.getID()).thenReturn("userId");
        when(userManager.getAuthorizable(authorizable.getID())).thenReturn(authorizable);
        when( JcrUtils.getOrCreateByPath(null , null, null, session, true)).thenReturn(assetNode);
        when(adminResourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        
        moveTriageFolderAsset.execute(workItem,workflowSession,metaDataMap);
        verify(session).save();

    }

    @Test
    void testExecute3() throws WorkflowException, RepositoryException {
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("xmp:eaton-company-taxonomy","eaton:company-taxonomy/selling-to-eaton/supplier-excellence/aerospace-facilities/euclid");
        ResourceResolver rr=aemContext.resourceResolver();
        Asset asset=aemContext.create().asset("/content/dam/eaton/triage/amer-legacy-canada.html.zip", 300, 200, "image/jpeg",map);
        //aemContext.load(true);
       
        Tag tag=aemContext.create().tag("eaton:company-taxonomy/selling-to-eaton/supplier-excellence/aerospace-facilities/euclid");
        when(session.nodeExists(payloadPath)).thenReturn(true);

        Node assetNode=rr.getResource(asset.getPath()).adaptTo(Node.class);
        when(session.getNode(anyString())).thenReturn(assetNode);
        when(workItem.getWorkflowData()).thenReturn(workflowData);
        when(workflowData.getPayload()).thenReturn(payloadPath);

        when(adminService.getWriteService()).thenReturn(adminResourceResolver);
        when(adminResourceResolver.adaptTo(Session.class)).thenReturn(session);
        when(adminResourceResolver.adaptTo(UserManager.class)).thenReturn(userManager);
        when(adminResourceResolver.adaptTo(TagManager.class)).thenReturn(tagManager);
        when(adminResourceResolver.getResource(assetNode.getPath())).thenReturn(rr.getResource(asset.getPath()));

        when(workItem.getWorkflow()).thenReturn(workflow);
        when(workflow.getInitiator()).thenReturn("initiatorUserId");
        when(userManager.getAuthorizable(workItem.getWorkflow().getInitiator())).thenReturn(authorizable);
        when(authorizable.getID()).thenReturn("userId");
        when(userManager.getAuthorizable(authorizable.getID())).thenReturn(authorizable);
        when( JcrUtils.getOrCreateByPath(null , null, null, session, true)).thenReturn(assetNode);
        when(adminResourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        
        moveTriageFolderAsset.execute(workItem,workflowSession,metaDataMap);
        verify(session).save();
    }




}
