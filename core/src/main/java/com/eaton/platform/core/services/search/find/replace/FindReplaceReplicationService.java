package com.eaton.platform.core.services.search.find.replace;

import java.util.List;

/**
 * The Interface FindReplaceReplicationService.
 *
 * Replicates content modified by "find and replace" functionality.
 *
 * @author Jaroslav Rassadin
 */
public interface FindReplaceReplicationService {

	/**
	 * Replicate.
	 *
	 * @param paths
	 *            the paths
	 */
	void replicate(List<String> paths);
}
