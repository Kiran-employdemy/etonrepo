package com.eaton.platform.core.services.search.find.replace;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;

import com.day.cq.replication.ReplicationStatus;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.bean.search.find.replace.request.ContentType;
import com.eaton.platform.core.bean.search.find.replace.response.ReplicationResult;
import com.eaton.platform.core.bean.search.find.replace.response.ResultItem;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.util.CommonUtil;

public abstract class AbstractFindReplaceFacade {

	/**
	 * Returns replication service
	 *
	 * @return FindReplaceReplicationService instance
	 */
	protected abstract FindReplaceReplicationService getReplicationService();
	
	/**
	 * Returns notification service
	 *
	 * @return FindReplaceNotificationService instance
	 */
	protected abstract FindReplaceNotificationService getNotificationService();
	
	/**
	 * Returns admin service
	 *
	 * @return AdminService instance
	 */
	protected abstract AdminService getAdminService();
	
	/**
	 * Replicates modified content or sends notification if there were other changes since last replication.
	 * @param replaceResult
	 */
	protected ReplicationResult replicateOrNotify(final List<ResultItem> replaceResult, String userId) {				
		final Set<String> pathsToReplicate = new HashSet<>();
		final Map<String, String> pagesToNotifyAbout = new HashMap<>();
		
		try (ResourceResolver resourceResolver = getAdminService().getReadService()) {
			final PageManager pageManager = CommonUtil.adapt(resourceResolver, PageManager.class);
			
			for(ResultItem item : replaceResult) {
				Calendar lastModified = null;
				Calendar lastReplicated = null;
				String replicationAction = null;
				
				Page page = null;
				
				if (ContentType.PAGE == item.getContentType() || ContentType.COMPONENT == item.getContentType()) {
					page = pageManager.getContainingPage(item.getPath());
				}
				//assets are activated by separate workflow
				
				if (page != null && !page.getPath().startsWith(CommonConstants.CONTENT_ROOT_FOLDER + CommonConstants.LANGUAGE_MASTERS_NODE_NAME)) {
					lastModified = page.getLastModified();
					if (page.getProperties().get(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATED) != null 
							&& page.getProperties().get(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATION_ACTION) != null) {
						lastReplicated = (Calendar) page.getProperties().get(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATED);
						replicationAction = (String) page.getProperties().get(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATION_ACTION);
					}
					
					//compare dates and decide if put item to replication set or notification set
					if (lastReplicated != null && "Activate".equalsIgnoreCase(replicationAction) && lastReplicated.compareTo(lastModified) >= 0 ) {
						pathsToReplicate.add(page.getPath());
					} else {
						pagesToNotifyAbout.put(page.getPath(), page.getName());
					}	
				}
			}
		}
			
		getReplicationService().replicate(new ArrayList<>(pathsToReplicate));
		String reportPath = getNotificationService().notifyUser(pagesToNotifyAbout, userId);
		
		if (!pagesToNotifyAbout.isEmpty() && StringUtils.isNotBlank(reportPath)) {
			return new ReplicationResult(reportPath);
		} else {
			return new ReplicationResult();
		}
	}
}
