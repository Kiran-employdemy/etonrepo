package com.eaton.platform.core.services.search.find.replace.text.impl;

import java.util.Collections;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.eaton.platform.core.bean.search.find.replace.request.ContentType;
import com.eaton.platform.core.bean.search.find.replace.request.TextModificationRequestBean;
import com.eaton.platform.core.bean.search.find.replace.request.TextSearchRequestBean;
import com.eaton.platform.core.bean.search.find.replace.response.BackupResult;
import com.eaton.platform.core.bean.search.find.replace.response.ModificationOperationsResult;
import com.eaton.platform.core.bean.search.find.replace.response.ReplicationResult;
import com.eaton.platform.core.bean.search.find.replace.response.ResultItem;
import com.eaton.platform.core.bean.search.find.replace.response.TextModificationResultBean;
import com.eaton.platform.core.bean.search.find.replace.response.TextSearchResultBean;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.search.find.replace.AbstractFindReplaceFacade;
import com.eaton.platform.core.services.search.find.replace.FindReplaceNotificationService;
import com.eaton.platform.core.services.search.find.replace.FindReplaceReplicationService;
import com.eaton.platform.core.services.search.find.replace.text.FindReplaceBackupTextService;
import com.eaton.platform.core.services.search.find.replace.text.FindReplaceTextDataService;
import com.eaton.platform.core.services.search.find.replace.text.FindReplaceTextFacade;

/**
 * The Class FindReplaceTextFacadeImpl.
 *
 * Facade for "find and replace" functionality for text.
 *
 * @author Jaroslav Rassadin
 */
@Component(
		service = FindReplaceTextFacade.class,
		property = {
				AEMConstants.SERVICE_VENDOR_EATON,
				AEMConstants.SERVICE_DESCRIPTION + "Find Replace Text Facade",
				AEMConstants.PROCESS_LABEL + "FindReplaceTextFacadeImpl" })
public class FindReplaceTextFacadeImpl extends AbstractFindReplaceFacade implements FindReplaceTextFacade {

	@Reference
	private FindReplaceTextDataService textDataService;

	@Reference
	private FindReplaceBackupTextService backupService;

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
	public TextSearchResultBean find(final TextSearchRequestBean searchRequest) {
		searchRequest.setContentType(ContentType.PAGE);
		final TextSearchResultBean searchResult = this.textDataService.find(searchRequest);

		searchRequest.setContentType(ContentType.COMPONENT);
		searchResult.getResults().addAll(this.textDataService.find(searchRequest).getResults());

		Collections.sort(searchResult.getResults());

		return searchResult;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TextModificationResultBean replace(final TextModificationRequestBean modificationRequest) {
		final ModificationOperationsResult operationsResult = new ModificationOperationsResult();
		this.doPreModificationOperations(modificationRequest, operationsResult);

		modificationRequest.setContentType(ContentType.PAGE);
		final TextModificationResultBean modificationResult = this.textDataService.replace(modificationRequest);

		modificationResult.setOperations(operationsResult);

		modificationRequest.setContentType(ContentType.COMPONENT);
		modificationResult.getResults().addAll(this.textDataService.replace(modificationRequest).getResults());

		Collections.sort(modificationResult.getResults());

		this.doPostModificationOperations(modificationRequest, modificationResult);

		return modificationResult;
	}

	private void checkModificationPaths(final TextModificationRequestBean modificationRequest) {
		if (CollectionUtils.isEmpty(modificationRequest.getModificationPaths())) {
			// it is necessary to use copy of request in find() because is can reset content type
			modificationRequest.setModificationPaths(this.find(new TextModificationRequestBean(modificationRequest))
					.getResults().stream().map(ResultItem::getPath).collect(Collectors.toList()));
		}
	}

	private void doPreModificationOperations(final TextModificationRequestBean modificationRequest, final ModificationOperationsResult operationsResult) {
		this.checkModificationPaths(modificationRequest);

		if (modificationRequest.isBackup()) {
			final BackupResult backup = this.backupService.createBackup(modificationRequest);
			operationsResult.setBackup(backup);
		}
	}

	private void doPostModificationOperations(final TextModificationRequestBean modificationRequest, final TextModificationResultBean modificationResult) {
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
