package com.eaton.platform.core.models.search.find.replace;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * The Class TagsSearchRequestModelTest.
 *
 * @author Jaroslav Rassadin
 */
class TagsSearchRequestModelTest {

	private final TagsSearchRequestModel classUnderTest = new TagsSearchRequestModel();

	/**
	 * Test that URL encoded tag value is correctly returned.
	 */
	@Test
	@DisplayName("URL encoded tag value is correctly returned")
	void testUrlEncodedTagValueIsCorrectlyReturned() {
		// set up
		this.classUnderTest.setTagsToFindRaw(List.of(
				"tags|eaton%3Aproduct-taxonomy%2Fbackup-power%2C-ups%2C-surge-%26-it-power-distribution%2Fbackup-power-(ups)%2Featon-5s"));

		// exercise
		final Map<String, List<String>> actualTagsToFind = this.classUnderTest.getTagsToFind();

		// verify
		assertEquals(Map.of("tags", List.of("eaton:product-taxonomy/backup-power,-ups,-surge-&-it-power-distribution/backup-power-(ups)/eaton-5s")),
				actualTagsToFind, "Should be equal tag maps");
	}
}
