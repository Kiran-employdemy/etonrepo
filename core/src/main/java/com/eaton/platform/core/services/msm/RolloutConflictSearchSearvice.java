package com.eaton.platform.core.services.msm;

import java.util.List;

import com.eaton.platform.core.services.msm.impl.RolloutConflictSearchResult;

/**
 * The Interface RolloutConflictSearchSearvice.
 *
 * @author Jaroslav Rassadin
 */
public interface RolloutConflictSearchSearvice {

	/**
	 * Find conflicts for rollout.
	 *
	 * @param blueprintPath
	 *            the blueprint path
	 * @param liveCopyPaths
	 *            the live copy paths
	 * @param deep
	 *            the deep
	 * @return the rollout conflict search result
	 */
	RolloutConflictSearchResult findConflictsForRollout(final String blueprintPath, final List<String> liveCopyPaths, final boolean deep);

	/**
	 * Find conflicts in content.
	 *
	 * @param searchRoot
	 *            the search root
	 * @return the rollout conflict search result
	 */
	RolloutConflictSearchResult findConflictsInContent(final String searchRoot);
}
