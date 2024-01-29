package com.eaton.platform.core.services.search.find.replace.tags.impl;

import java.util.Collections;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.eaton.platform.core.bean.search.find.replace.request.ContentType;
import com.eaton.platform.core.bean.search.find.replace.request.TagsModificationRequestBean;
import com.eaton.platform.core.bean.search.find.replace.request.TagsSearchRequestBean;
import com.eaton.platform.core.bean.search.find.replace.response.BackupResult;
import com.eaton.platform.core.bean.search.find.replace.response.ModificationOperationsResult;
import com.eaton.platform.core.bean.search.find.replace.response.ReplicationResult;
import com.eaton.platform.core.bean.search.find.replace.response.ResultItem;
import com.eaton.platform.core.bean.search.find.replace.response.TagsModificationResultBean;
import com.eaton.platform.core.bean.search.find.replace.response.TagsSearchResultBean;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.search.find.replace.AbstractFindReplaceFacade;
import com.eaton.platform.core.services.search.find.replace.FindReplaceNotificationService;
import com.eaton.platform.core.services.search.find.replace.FindReplaceReplicationService;
import com.eaton.platform.core.services.search.find.replace.tags.FindReplaceBackupTagsService;
import com.eaton.platform.core.services.search.find.replace.tags.FindReplaceTagsDataService;
import com.eaton.platform.core.services.search.find.replace.tags.FindReplaceTagsFacade;

/**
 * The Class FindReplaceTagsFacadeImpl.
 *
 * Facade for "find and replace" functionality for tags.
 *
 * @author Jaroslav Rassadin
 */
@Component(
		service = FindReplaceTagsFacade.class,
		property = {
				AEMConstants.SERVICE_VENDOR_EATON,
				AEMConstants.SERVICE_DESCRIPTION + "Find Replace Tags Facade",
				AEMConstants.PROCESS_LABEL + "FindReplaceTagsFacadeImpl" })
public class FindReplaceTagsFacadeImpl extends AbstractFindReplaceFacade implements FindReplaceTagsFacade {

	@Reference
	private FindReplaceTagsDataService tagsDataService;

	@Reference

	private FindReplaceBackupTagsService backupService;

	@Reference
	private FindReplaceReplicationService replicationService;
	
	@Reference
	private FindReplaceNotificationService notificationService;

	@Reference
	private AdminService adminService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TagsModificationResultBean add(final TagsModificationRequestBean modificationRequest) {
		final ModificationOperationsResult operationsResult = new ModificationOperationsResult();
		this.doPreModificationOperations(modificationRequest, operationsResult);

		final TagsModificationResultBean modificationResult = this.tagsDataService.add(modificationRequest);

		modificationResult.setOperations(operationsResult);

		if (ContentType.PAGE == modificationRequest.getContentType()) {
			modificationRequest.setContentType(ContentType.COMPONENT);
			modificationResult.getResults().addAll(this.tagsDataService.add(modificationRequest).getResults());
		}
		Collections.sort(modificationResult.getResults());

		this.doPostModificationOperations(modificationRequest, modificationResult);

		return modificationResult;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TagsModificationResultBean delete(final TagsModificationRequestBean modificationRequest) {
		final ModificationOperationsResult operationsResult = new ModificationOperationsResult();
		this.doPreModificationOperations(modificationRequest, operationsResult);

		final TagsModificationResultBean modificationResult = this.tagsDataService.delete(modificationRequest);

		modificationResult.setOperations(operationsResult);

		if (ContentType.PAGE == modificationRequest.getContentType()) {
			modificationRequest.setContentType(ContentType.COMPONENT);
			modificationResult.getResults().addAll(this.tagsDataService.delete(modificationRequest).getResults());
		}
		Collections.sort(modificationResult.getResults());

		this.doPostModificationOperations(modificationRequest, modificationResult);

		return modificationResult;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TagsSearchResultBean find(final TagsSearchRequestBean searchRequest) {
		final TagsSearchResultBean searchResult = this.tagsDataService.find(searchRequest);

		if (ContentType.PAGE == searchRequest.getContentType()) {
			searchRequest.setContentType(ContentType.COMPONENT);
			searchResult.getResults().addAll(this.tagsDataService.find(searchRequest).getResults());
		}
		Collections.sort(searchResult.getResults());

		return searchResult;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TagsModificationResultBean replace(final TagsModificationRequestBean modificationRequest) {
		final ModificationOperationsResult operationsResult = new ModificationOperationsResult();
		this.doPreModificationOperations(modificationRequest, operationsResult);

		final TagsModificationResultBean modificationResult = this.tagsDataService.replace(modificationRequest);

		modificationResult.setOperations(operationsResult);

		if (ContentType.PAGE == modificationRequest.getContentType()) {
			modificationRequest.setContentType(ContentType.COMPONENT);
			modificationResult.getResults().addAll(this.tagsDataService.replace(modificationRequest).getResults());
		}
		Collections.sort(modificationResult.getResults());

		this.doPostModificationOperations(modificationRequest, modificationResult);

		return modificationResult;
	}

	private void checkModificationPaths(final TagsModificationRequestBean modificationRequest) {
		if (CollectionUtils.isEmpty(modificationRequest.getModificationPaths())) {
			// it is necessary to use copy of request in find() because is can reset content type
			modificationRequest.setModificationPaths(this.find(new TagsModificationRequestBean(modificationRequest)).getResults().stream().map(
					ResultItem::getPath).collect(Collectors
							.toList()));
		}
	}

	private void doPreModificationOperations(final TagsModificationRequestBean modificationRequest, final ModificationOperationsResult operationsResult) {
		this.checkModificationPaths(modificationRequest);

		if (modificationRequest.isBackup()) {
			final BackupResult backup = this.backupService.createBackup(modificationRequest);
			operationsResult.setBackup(backup);
		}
	}

	private void doPostModificationOperations(final TagsModificationRequestBean modificationRequest, final TagsModificationResultBean modificationResult) {
		if (modificationRequest.isReplicate()) {
			final ReplicationResult replication = this.replicateOrNotify(modificationResult.getResults(), modificationRequest.getUserId());
			modificationResult.getOperations().setReplication(replication);
		}
	}

	@Override
	protected FindReplaceReplicationService getReplicationService() {
		return this.replicationService;
	}

	@Override
	protected FindReplaceNotificationService getNotificationService() {
		return this.notificationService;
	}
	
	@Override
	protected AdminService getAdminService() {
		return this.adminService;
	}

}
