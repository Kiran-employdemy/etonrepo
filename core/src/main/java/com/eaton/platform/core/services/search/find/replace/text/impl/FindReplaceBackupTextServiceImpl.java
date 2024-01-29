package com.eaton.platform.core.services.search.find.replace.text.impl;

import java.util.Map;

import org.apache.commons.lang.text.StrSubstitutor;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.eaton.platform.core.bean.packaging.PackageBean;
import com.eaton.platform.core.bean.search.find.replace.request.TextModificationRequestBean;
import com.eaton.platform.core.bean.search.find.replace.response.BackupResult;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.FindAndReplaceConstants;
import com.eaton.platform.core.services.search.find.replace.FindReplaceBackupService;
import com.eaton.platform.core.services.search.find.replace.text.FindReplaceBackupTextService;

/**
 * The Class FindReplaceBackupTextServiceImpl.
 *
 * Creates backups for content modified by "find and replace" functionality for text.
 *
 * @author Jaroslav Rassadin
 */
@Component(
		service = FindReplaceBackupTextService.class,
		property = {
				AEMConstants.SERVICE_VENDOR_EATON,
				AEMConstants.SERVICE_DESCRIPTION + "Find Replace Backup Text Service",
				AEMConstants.PROCESS_LABEL + "FindReplaceBackupTextServiceImpl" })
public class FindReplaceBackupTextServiceImpl implements FindReplaceBackupTextService {

	private static final String DESCRIPTION_TEMPLATE = "Backup for text modification operation.\nAuthor: ${author}.\nSearch root path: ${path}.\nSearch text: ${searchText}.\n"
			+ "Replace text: ${replaceText}.\nCase insensitive: ${caseSensitive}, whole words only: ${wholeWords}.";

	@Reference
	private FindReplaceBackupService backupService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BackupResult createBackup(final TextModificationRequestBean modificationRequest) {
		final PackageBean packageBean = this.backupService.createBackup(FindAndReplaceConstants.TEXT_PACKAGE_NAME, this.buildDescription(modificationRequest),
				modificationRequest.getModificationPaths());

		final BackupResult result = new BackupResult();
		result.setPackagePath(packageBean.getPath());

		return result;
	}

	private String buildDescription(final TextModificationRequestBean modificationRequest) {

		return StrSubstitutor.replace(DESCRIPTION_TEMPLATE, Map.of("author",
				modificationRequest.getUserId(), "path", modificationRequest.getPath(), "searchText", modificationRequest.getSearchText(),
				"replaceText", modificationRequest.getReplaceText(), "caseSensitive", !modificationRequest.isCaseSensitive(),
				"wholeWords", modificationRequest.isWholeWords()));
	}

}
