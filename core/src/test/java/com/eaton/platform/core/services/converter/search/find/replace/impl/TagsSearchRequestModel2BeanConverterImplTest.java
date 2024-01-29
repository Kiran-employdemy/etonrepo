package com.eaton.platform.core.services.converter.search.find.replace.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.eaton.platform.core.bean.search.find.replace.request.ContentType;
import com.eaton.platform.core.bean.search.find.replace.request.TagsSearchRequestBean;
import com.eaton.platform.core.models.search.find.replace.TagsSearchRequestModel;

/**
 * The Class TagsSearchRequestModel2BeanConverterImplTest.
 *
 * @author Jaroslav Rassadin
 */
class TagsSearchRequestModel2BeanConverterImplTest {

	private final TagsSearchRequestModel2BeanConverterImpl classUnderTest = new TagsSearchRequestModel2BeanConverterImpl();

	/**
	 * Test that convertation is correct.
	 */
	@Test
	@DisplayName("Convertation is csorrect")
	void testConvertationIsCorrect() {
		// set up and exercise
		final TagsSearchRequestBean actual = this.classUnderTest.convert(this.getTagsSearchRequestModel());

		// verify
		assertEquals(this.getTagsSearchRequestBean(), actual, "Objects should be equal");
	}

	private TagsSearchRequestModel getTagsSearchRequestModel() {
		final TagsSearchRequestModel searchRequestModel = new TagsSearchRequestModel();

		searchRequestModel.setContentType("asset");
		searchRequestModel.setPath("/content/eaton/de/de-de/catalog/emobility/test_replace");
		searchRequestModel.setTagsToFindRaw(Arrays.asList("accountType|eaton-secure:accounttype/fleet", "accountType|eaton-secure:accounttype/agent"));

		return searchRequestModel;
	}

	private TagsSearchRequestBean getTagsSearchRequestBean() {
		final TagsSearchRequestBean searchRequestBean = new TagsSearchRequestBean();

		searchRequestBean.setContentType(ContentType.ASSET);
		searchRequestBean.setPath("/content/eaton/de/de-de/catalog/emobility/test_replace");
		searchRequestBean.setTagsToFind(Map.of("accountType", Arrays.asList("eaton-secure:accounttype/fleet", "eaton-secure:accounttype/agent")));

		return searchRequestBean;
	}
}
