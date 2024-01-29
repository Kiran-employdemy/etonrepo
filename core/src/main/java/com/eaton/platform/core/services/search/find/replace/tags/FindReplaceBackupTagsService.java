package com.eaton.platform.core.services.search.find.replace.tags;

import com.eaton.platform.core.bean.search.find.replace.request.TagsModificationRequestBean;
import com.eaton.platform.core.bean.search.find.replace.response.BackupResult;

/**
 * The Interface FindReplaceBackupTagsService.
 *
 * Creates backups for content modified by "find and replace" functionality for tags.
 *
 * @author Jaroslav Rassadin
 */
public interface FindReplaceBackupTagsService {

	/**
	 * Creates the backup.
	 *
	 * @param modificationRequest
	 *            the modification request
	 * @return the backup result
	 */
	BackupResult createBackup(TagsModificationRequestBean modificationRequest);
}
