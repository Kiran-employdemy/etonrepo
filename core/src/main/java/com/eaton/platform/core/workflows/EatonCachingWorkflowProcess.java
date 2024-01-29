package com.eaton.platform.core.workflows;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This workflow is used to publish the PIM and PDH nodes
 * corresponding to the linked family page.
 */
@Component(service = WorkflowProcess.class,immediate = true,
		property = {
				AEMConstants.SERVICE_DESCRIPTION + "Eaton PIM Publication Workflow",
				AEMConstants.SERVICE_VENDOR_EATON,
		})
public class EatonCachingWorkflowProcess implements WorkflowProcess {
	
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(EatonCachingWorkflowProcess.class);
	
	/** The Constant PDH_RECORD_PATH. */
	private static final String PDH_RECORD_PATH = "pdhRecordPath";
	
	/** The replicator. */
	@Reference
	public Replicator replicator;

	/** The admin service. */
	@Reference
	protected AdminService adminService;

	/* (non-Javadoc)
	 * @see com.adobe.granite.workflow.exec.WorkflowProcess#execute(com.adobe.granite.workflow.exec.WorkItem, com.adobe.granite.workflow.WorkflowSession, com.adobe.granite.workflow.metadata.MetaDataMap)
	 */
	public void execute(WorkItem item, WorkflowSession workflowSession,
			MetaDataMap args) throws WorkflowException {
		try (ResourceResolver adminResourceResolver = adminService.getWriteService()){
			List<String> nodeToPublish = new ArrayList<>();
			Resource familyPagePathResource = null;
			Resource pimPagePathResource = null;
			String pdhRecordPath = null;
			Session session = workflowSession.adaptTo(Session.class);
			final String payLoadPath = item.getWorkflowData().getPayload().toString();
			String pimPagePath = null;
			familyPagePathResource = adminResourceResolver.getResource(payLoadPath	+ "/jcr:content");
			if(null != familyPagePathResource) {
                pimPagePath = familyPagePathResource.getValueMap().get(CommonConstants.PAGE_PIM_PATH).toString();
                String pageType = familyPagePathResource.getValueMap().get(CommonConstants.PAGE_TYPE).toString();
                pimPagePathResource = adminResourceResolver.getResource(pimPagePath);
                if(null != pimPagePathResource) {
                    pdhRecordPath = pimPagePathResource.getValueMap().get(PDH_RECORD_PATH).toString();
                    try {
                        if (StringUtils.equals(CommonConstants.PAGE_TYPE_PRODUCT_FAMILY_PAGE, pageType)) {
                            replicatePimPdhNodes(workflowSession, nodeToPublish, payLoadPath, pdhRecordPath, session, pimPagePath);
                        } else {
                            LOG.debug("Not a family page OR Family page path not equal to mentioned in the PIM node");
                            replicator.replicate(workflowSession.adaptTo(Session.class), ReplicationActionType.ACTIVATE, payLoadPath);

                        }
                    } catch (Exception e) {
                        LOG.error("ERROR" + e.getMessage());
                    }
                }
            }
		}
	}

	/**
	 * Replicate pim pdh nodes.
	 *
	 * @param workflowSession the workflow session
	 * @param nodeToPublish the node to publish
	 * @param aemFamilyPagePath the aem family page path
	 * @param pdhRecordPath the pdh record path
	 * @param session the session
	 * @param pimPagePath the pim page path
	 * @throws RepositoryException the repository exception
	 * @throws ReplicationException the replication exception
	 */
	private void replicatePimPdhNodes(WorkflowSession workflowSession,
			List<String> nodeToPublish, String aemFamilyPagePath,
			String pdhRecordPath, Session session, String pimPagePath)
			throws RepositoryException,	ReplicationException {
		QueryManager queryManager = null;
		QueryResult result = null;
		Query query = null;
		NodeIterator nodes = null;
		Node node = null;
		
		// the node paths that needs to be published are kept in a list
		nodeToPublish.add(aemFamilyPagePath);
		nodeToPublish.add(pimPagePath);
		nodeToPublish.add(pdhRecordPath);

		// get all the child nodes of PDH record to be published
		queryManager = session.getWorkspace().getQueryManager();
		if(null != queryManager) {
			query = queryManager.createQuery(
					"SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE(["
							+ pdhRecordPath + "])", Query.JCR_SQL2);
			if(null != query) {
				result = query.execute();
				if(null != result) {
					nodes = result.getNodes();
				}
			}
		}
		if(null != nodes) {
			while (nodes.hasNext()) {
				node = nodes.nextNode();
				nodeToPublish.add(node.getPath());
			}
		}
		Iterator<String> paths = nodeToPublish.iterator();

		// publish all the paths
		while (paths.hasNext()) {
			String path = paths.next();
			LOG.debug("pdhRecordPathNode ", path);
			replicator.replicate(workflowSession.adaptTo(Session.class), ReplicationActionType.ACTIVATE, path);
		}
	}
}
