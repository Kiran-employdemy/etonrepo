package com.eaton.platform.core.workflows;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.Replicator;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.EatonConfigService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Session;
import java.util.Calendar;


@Component(service = com.adobe.granite.workflow.exec.WorkflowProcess.class,immediate = true,
		property = {
				AEMConstants.SERVICE_DESCRIPTION + "Notify publish content after specified date",
				AEMConstants.SERVICE_VENDOR_EATON,
		})
public class EatonScheduleActivationWorkflowProcess implements WorkflowProcess {
	private static final Logger log = LoggerFactory.getLogger(EatonScheduleActivationWorkflowProcess.class);

	// Replicator Service Reference
	@Reference
	private Replicator replicator;

	// EatonConfig Service Reference
	@Reference
	private EatonConfigService eatonConfigService;

	// Admin Service Reference
	@Reference
	private AdminService adminService;

	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap args) throws WorkflowException {
		log.info("Here in execute method of EatonScheduleActivationWorkflowProcess"); // ensure
																						// that
																						// the
																						// execute
																						// method
																						// is
																						// invoked
		final WorkflowData workflowData = workItem.getWorkflowData();
		final String type = workflowData.getPayloadType();

		Session session = workflowSession.adaptTo(Session.class); // get the
																	// user
																	// session
		// Check if the payload is a path in the JCR; The other (less common)
		// type is JCR_UUID
		if (!StringUtils.equals(type, "JCR_PATH")) {
			return;
		}
		// Get the path to the JCR resource from the payload
		final String path = workflowData.getPayload().toString();

		try (ResourceResolver adminResourceResolver = adminService.getWriteService()){
			String numofDaysToPublish;
			numofDaysToPublish = eatonConfigService.getConfigServiceBean().getNumofDaysToPublish(); // Get
																									// Number
																									// of
																									// Days
																									// To
																									// Publish
																									// property
																									// value

			Calendar c = Calendar.getInstance();

			c.add(Calendar.DATE, Integer.parseInt(numofDaysToPublish)); // Add
																		// the
																		// number
																		// of
																		// Days
																		// mentioned
																		// in
																		// property
																		// to
																		// current
																		// Date

			log.info("-------------------Publish Schedule metadata for key numofDaysToPublish and value {}",
					numofDaysToPublish);


			log.info("-------------------Publish Schedule metadata for key PATH and value {}", path);
			Resource pdhRootFolderRes = adminResourceResolver.getResource(path.concat("/jcr:content"));
			if (pdhRootFolderRes != null && !ResourceUtil.isNonExistingResource(pdhRootFolderRes)) {
				if (log.isInfoEnabled()) {
					log.info("-------------------Publish Schedule metadata for key NEW pdhRootFolderRes and value {}",
							pdhRootFolderRes.toString());
				}
				Node pdhRootNode = pdhRootFolderRes.adaptTo(Node.class);
				if (pdhRootNode != null) {
					pdhRootNode.setProperty("onTime", c); // Set the onTime
															// property
															// for Page for
															// Scheduled
															// Publication

					pdhRootNode.getSession().save();
				}
			} else {
				replicator.replicate(session, ReplicationActionType.ACTIVATE, path); // Publish
																						// the
																						// Pages
			}

		} catch (Exception e) {
			// If an error occurs that prevents the Workflow from
			// completing/continuing - Throw a WorkflowException
			// and the WF engine will retry the Workflow later (based on the AEM
			// Workflow Engine configuration).

			log.error("Unable to complete processing the Workflow Process step", e);

			throw new WorkflowException("Unable to complete processing the Workflow Process step", e);
		}

	}

}
