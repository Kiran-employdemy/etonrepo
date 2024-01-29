package com.eaton.platform.core.workflows;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

@Component(service = com.adobe.granite.workflow.exec.WorkflowProcess.class,immediate = true,
		property = {
				AEMConstants.SERVICE_DESCRIPTION + "Setting the Default date as publishing Date",
				AEMConstants.SERVICE_VENDOR_EATON,
		})
public class EatonDefaultPublishingDate implements WorkflowProcess{
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(EatonDefaultPublishingDate.class);
	
	private static final  String DATE_FORMAT_PUBLISH = "MMMM d, yyyy HH:mm:ss.SSSXXX";
	@Reference
	protected AdminService adminService;
	
	Resource resource = null;
	@Override
	public void execute(WorkItem workItem, WorkflowSession arg1, MetaDataMap arg2)
			throws WorkflowException {
		try (ResourceResolver adminResourceResolver = adminService.getWriteService()) {
			final WorkflowData workflowData = workItem.getWorkflowData();
			// Get the path to the JCR resource from the payload
			final String payLoadPath = workflowData.getPayload().toString();
			resource = adminResourceResolver.getResource(payLoadPath+ "/jcr:content");
			Node node1 = resource.adaptTo(Node.class);
			DateFormat dateFormat= new SimpleDateFormat(DATE_FORMAT_PUBLISH);
			Calendar cur = GregorianCalendar.getInstance();
			PageManager pageManager= resource.getResourceResolver().adaptTo(PageManager.class);
			Page currentPage = pageManager.getContainingPage(resource);
			//EAT-299 Publication issue date fix for manual date change
			if(null == currentPage.getProperties().get(CommonConstants.PUBLICATION_DATE)){
				node1.setProperty(CommonConstants.PUBLICATION_DATE, dateFormat.format(cur.getTime()));
			}
			node1.getSession().save();
		} catch (ValueFormatException e) {
			LOG.error(e.getMessage());
		} catch (VersionException e) {
			LOG.error(e.getMessage());
		} catch (LockException e) {
			LOG.error(e.getMessage());
		} catch (ConstraintViolationException e) {
			LOG.error(e.getMessage());
		} catch (RepositoryException e) {
			LOG.error(e.getMessage());
		} 
	}

}
