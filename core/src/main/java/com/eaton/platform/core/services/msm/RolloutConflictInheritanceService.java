package com.eaton.platform.core.services.msm;

import com.eaton.platform.core.services.msm.impl.ConflictResolutionResult;

/**
 * The Interface RolloutConflictInheritanceService.
 *
 * Contains logic for inheritance enabling functionality for components and pages.
 *
 * @author Jaroslav Rassadin
 */
public interface RolloutConflictInheritanceService {

	/**
	 * Can enable inheritance for page.
	 *
	 * @param path
	 *            the path
	 * @return true, if possible
	 */
	boolean canEnableInheritanceForPage(String path);

	/**
	 * Can enable inheritance for resource.
	 *
	 * @param path
	 *            the path
	 * @return true, if possible
	 */
	boolean canEnableInheritanceForResource(String path);

	/**
	 * Enable inheritance for page.
	 *
	 * @param path
	 *            the path
	 * @param replicate
	 *            the replicate
	 * @return the conflict resolution result
	 */
	ConflictResolutionResult enableInheritanceForPage(String path, boolean replicate);

	/**
	 * Enable inheritance for resource.
	 *
	 * @param path
	 *            the path
	 * @param replicate
	 *            the replicate
	 * @return the conflict resolution result
	 */
	ConflictResolutionResult enableInheritanceForResource(String path, boolean replicate);
}
