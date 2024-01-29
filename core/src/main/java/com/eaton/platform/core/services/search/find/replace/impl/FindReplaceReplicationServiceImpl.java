package com.eaton.platform.core.services.search.find.replace.impl;

import java.util.List;

import javax.jcr.Session;

import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.Replicator;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.search.find.replace.FindReplaceReplicationService;
import com.eaton.platform.core.util.CommonUtil;

/**
 * The Class FindReplaceReplicationServiceImpl.
 *
 * Replicates content modified by "find and replace" functionality.
 *
 * @author Jaroslav Rassadin
 */
@Component(
		service = FindReplaceReplicationService.class,
		property = {
				AEMConstants.SERVICE_VENDOR_EATON,
				AEMConstants.SERVICE_DESCRIPTION + "Find Replace Replicate Service",
				AEMConstants.PROCESS_LABEL + "FindReplaceReplicationServiceImpl" })
public class FindReplaceReplicationServiceImpl implements FindReplaceReplicationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(FindReplaceReplicationServiceImpl.class);
	
	@Reference
	private AdminService adminService;
	
	@Reference
    private Replicator replicator;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void replicate(final List<String> paths) {
		try (ResourceResolver resourceResolver = this.adminService.getWriteService()) {
			final Session session = CommonUtil.adapt(resourceResolver, Session.class);
			
			for(String path : paths) {
				replicator.replicate(session, ReplicationActionType.ACTIVATE, path);
				LOGGER.debug("Activating path {}", path);
			}
		} catch (Exception e) {
			LOGGER.error("Exception occured while replicating modified pages by find and replace tool", e);
		}
	}

}
