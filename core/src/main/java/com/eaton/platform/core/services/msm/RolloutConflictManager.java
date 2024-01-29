package com.eaton.platform.core.services.msm;

import java.util.List;

/**
 * The Interface RolloutConflictManager.
 *
 * @author Jaroslav Rassadin
 */
public interface RolloutConflictManager {

	/**
	 * Check whether conflicts exist for certain blueprint page and notify author.
	 *
	 * @param blueprintPath
	 *            the blueprint path
	 * @param deep
	 *            the deep
	 * @param userId
	 *            the user id
	 */
	void checkAndNotify(String blueprintPath, boolean deep, String userId);

	/**
	 * Check whether conflicts exist for certain blueprint page and notify author.
	 *
	 * @param blueprintPath
	 *            the blueprint path
	 * @param liveCopyPaths
	 *            the live copy paths
	 * @param deep
	 *            the deep
	 * @param userId
	 *            the user id
	 */
	void checkAndNotify(String blueprintPath, List<String> liveCopyPaths, boolean deep, String userId);

	/**
	 * Report conflicts for configured repository root.
	 */
	void reportConflicts();
}
