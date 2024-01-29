package com.eaton.platform.core.workflows;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * The Class EatonManualCleanWorkflowProcess.
 */

@Component(service = com.adobe.granite.workflow.exec.WorkflowProcess.class,immediate = true,
		property = {
				AEMConstants.SERVICE_DESCRIPTION + "Enable CloudService Configuration Inheritance",
				AEMConstants.SERVICE_VENDOR_EATON,
		})
public class EatonManualCleanWorkflowProcess implements WorkflowProcess {

	/** The Constant log. */
	private static final Logger log = LoggerFactory.getLogger(EatonManualCleanWorkflowProcess.class);

	/** The Constant CLOUD_SERVICE_CONFIG. */
	private static final String CLOUD_SERVICE_CONFIG = "cq:cloudserviceconfigs";

	/** The admin service. */
	@Reference
	protected AdminService adminService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.adobe.granite.workflow.exec.WorkflowProcess#execute(com.adobe.granite
	 * .workflow.exec.WorkItem, com.adobe.granite.workflow.WorkflowSession,
	 * com.adobe.granite.workflow.metadata.MetaDataMap)
	 */
	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession,
			MetaDataMap args) throws WorkflowException {
		log.debug("Here in execute method of EatonManualCleanWorkflowProcess");
		Query query = null;
		SearchResult results = null;
		QueryBuilder queryBuilder = null;
		Session session = null;
		// invoked
		final WorkflowData workflowData = workItem.getWorkflowData();
		// Get the path to the JCR resource from the payload
		final String payLoadPath = workflowData.getPayload().toString();
		try (ResourceResolver adminResourceResolver = adminService.getWriteService()){
			updateCloudServiceConfigProperty(payLoadPath, adminResourceResolver);
			if(null != adminResourceResolver) {
				Resource pageResource = adminResourceResolver.getResource(payLoadPath);
	
				if (pageResource != null && pageResource.hasChildren()) {
					if (adminResourceResolver != null) {
						// Creating QueryBuilder
						queryBuilder = adminResourceResolver.adaptTo(QueryBuilder.class);
						// fetches Session
						session = workflowSession.adaptTo(Session.class);
					}
					Map<String, String> map = createQueryString(payLoadPath);
	
					if (queryBuilder != null && session != null) {
						query = queryBuilder.createQuery(PredicateGroup.create(map), session);
						results = query.getResult();
					}
	
					if (results != null) {
						log.info("Total no of results: " + results.getTotalMatches());
						String path;
						// iterating over the results
						for (Hit hit : results.getHits()) {
							path = hit.getPath();
							updateCloudServiceConfigProperty(path, adminResourceResolver);
						}
					}
				}
			}
		} catch (RepositoryException e) {
			log.info("Exception Occured", e);
		}
		log.debug("Here in execute method ends of EatonManualCleanWorkflowProcess");
	}

	/**
	 * Creates the query string.
	 * 
	 * @param payLoadPath
	 *            the pay load path
	 * @return the map
	 */
	private Map<String, String> createQueryString(final String payLoadPath) {
		// create query description as hash map
		Map<String, String> map = new HashMap<>();

		// create query description as hash map to get all pages for whom
		// cq:cloudserviceconfigs property is set to [].
		map.put("path", payLoadPath);
		map.put("type", "cq:Page");
		map.put("property", "jcr:content/" + CLOUD_SERVICE_CONFIG);
		map.put("property.value", "[]");
		map.put("p.limit", "-1");
		return map;
	}

	/**
	 * Update cloud service config property.
	 * 
	 * @param pagePath
	 *            the page path
	 * @param adminResourceResolver
	 *            the admin resource resolver
	 * @throws RepositoryException
	 *             the repository exception
	 */
	private void updateCloudServiceConfigProperty(String pagePath,
			ResourceResolver adminResourceResolver) throws RepositoryException {
		ValueMap valueMap = null;
		String[] returnVal = null;
		Resource pagePathResource = null;

		if (adminResourceResolver != null) {
			Session resourceResolverSession = adminResourceResolver.adaptTo(Session.class);
			pagePathResource = adminResourceResolver.getResource(pagePath + "/jcr:content");
			if (pagePathResource != null)
				valueMap = pagePathResource.getValueMap();

			if (null != valueMap) {
				returnVal = (String[]) valueMap.get(CLOUD_SERVICE_CONFIG);

				if (returnVal != null
						&& (returnVal.length == 1 || !Arrays.asList(returnVal).isEmpty())) {
					Node pageNode = pagePathResource.adaptTo(Node.class);
					if (pageNode != null) {
						pageNode.setProperty(CLOUD_SERVICE_CONFIG, (String[]) null);
						log.info("Page to update cq:cloudserviceconfigs property: ", pagePath);
						log.info("CLOUD_SERVICE_CONFIG value for above page: ", returnVal[0]);
					}
				}
			}
			resourceResolverSession.save();
		}
	}
}
