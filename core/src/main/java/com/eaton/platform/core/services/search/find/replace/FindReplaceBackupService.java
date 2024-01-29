package com.eaton.platform.core.services.search.find.replace;

import java.util.List;

import com.eaton.platform.core.bean.packaging.PackageBean;

/**
 * The Interface FindReplaceBackupService.
 *
 * Creates backups for content modified by "find and replace" functionality.
 *
 * @author Jaroslav Rassadin
 */
public interface FindReplaceBackupService {

	/**
	 * Creates the backup.
	 *
	 * @param name
	 *            the name
	 * @param description
	 *            the description
	 * @param paths
	 *            the paths
	 * @return the package bean
	 */
	PackageBean createBackup(String name, String description, List<String> paths);
}
