package com.eaton.platform.core.services.msm.impl;

import java.util.Arrays;

/**
 * The Class RolloutConflictTestHelper.
 *
 * @author Jaroslav Rassadin
 */
final class RolloutConflictTestHelper {

	private RolloutConflictTestHelper() {

	}

	/**
	 * Builds the search result.
	 *
	 * @return the rollout conflict search result
	 */
	public static RolloutConflictSearchResult buildSearchResult() {
		final RolloutConflictSearchResult result = new RolloutConflictSearchResult(true, "/content/eaton/language-masters/de-de/catalog/emobility");

		final RolloutConflictSearchResultItem item1 = new RolloutConflictSearchResultItem();

		item1.setBlueprintPath("/content/eaton/language-masters/de-de/catalog/emobility");
		item1.setLiveCopyMsmMovedPath("/content/eaton/de/de-de/catalog/emobility/battery-isolators_msm_moved");
		item1.setLiveCopyPath("/content/eaton/de/de-de/catalog/emobility/battery-isolators");

		result.addItem("/content/eaton/de/de-de/catalog/emobility/battery-isolators", item1);

		final RolloutConflictSearchResultItem item2 = new RolloutConflictSearchResultItem();

		item2.setBlueprintPath("/content/eaton/language-masters/de-de/catalog/emobility");
		item2.setLiveCopyPath("/content/eaton/de/de-de/catalog/emobility/battery-protectors");
		item2.setMsmMovedComponentPaths(Arrays.asList(" /content/eaton/de/de-de/catalog/emobility/battery-protectors/jcr:content/root/header_msm_moved",
				"/content/eaton/de/de-de/catalog/emobility/battery-protectors/jcr:content/root/header_msm_moved_1",
				"/content/eaton/de/de-de/catalog/emobility/battery-protectors/jcr:content/root/header_msm_moved_2"));

		result.addItem("/content/eaton/de/de-de/catalog/emobility/battery-protectors", item2);

		return result;
	}
}
