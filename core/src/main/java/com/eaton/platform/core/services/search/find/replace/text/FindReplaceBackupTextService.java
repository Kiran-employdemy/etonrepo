package com.eaton.platform.core.services.search.find.replace.text;

import com.eaton.platform.core.bean.search.find.replace.request.TextModificationRequestBean;
import com.eaton.platform.core.bean.search.find.replace.response.BackupResult;

/**
 * The Interface FindReplaceBackupTextService.
 *
 * Creates backups for content modified by "find and replace" functionality for text.
 *
 * @author Jaroslav Rassadin
 */
public interface FindReplaceBackupTextService {

	/**
	 * Creates the backup.
	 *
	 * @param modificationRequest
	 *            the modification request
	 * @return the backup result
	 */
	BackupResult createBackup(TextModificationRequestBean modificationRequest);
}
