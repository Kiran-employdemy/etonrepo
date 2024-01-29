package com.eaton.platform.core.services.msm;

import com.eaton.platform.core.services.msm.impl.RolloutConflictReport;
import com.eaton.platform.core.services.msm.impl.RolloutConflictSearchResult;

/**
 * The Interface RolloutConflictReportGenerator.
 *
 * @author Jaroslav Rassadin
 */
public interface RolloutConflictReportGenerator {

	/**
	 * Generate report.
	 *
	 * @param searchResult
	 *            the search result
	 * @param detailedReport
	 *            the detailed report
	 * @return the rollout conflict export
	 */
	RolloutConflictReport generateReport(RolloutConflictSearchResult searchResult, boolean detailedReport);
}
