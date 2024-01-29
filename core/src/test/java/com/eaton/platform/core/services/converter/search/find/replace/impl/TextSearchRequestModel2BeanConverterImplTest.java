package com.eaton.platform.core.services.converter.search.find.replace.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.eaton.platform.core.bean.search.find.replace.request.TextSearchRequestBean;
import com.eaton.platform.core.models.search.find.replace.TextSearchRequestModel;

/**
 * The Class TextSearchRequestModel2BeanConverterImplTest.
 *
 * @author Jaroslav Rassadin
 */
class TextSearchRequestModel2BeanConverterImplTest {

	private final TextSearchRequestModel2BeanConverterImpl classUnderTest = new TextSearchRequestModel2BeanConverterImpl();

	/**
	 * Test that convertation is correct.
	 */
	@Test
	@DisplayName("Convertation is csorrect")
	void testConvertationIsCorrect() {
		// set up and exercise
		final TextSearchRequestBean actual = this.classUnderTest.convert(this.getTextSearchRequestModel());

		// verify
		assertEquals(this.getTextSearchRequestBean(), actual, "Objects should be equal");
	}

	private TextSearchRequestModel getTextSearchRequestModel() {
		final TextSearchRequestModel searchRequestModel = new TextSearchRequestModel();

		searchRequestModel.setCaseSensitive(true);
		searchRequestModel.setPath("/content/eaton/de/de-de/catalog");
		searchRequestModel.setReplaceText("Eaton1");
		searchRequestModel.setSearchText("Eaton");
		searchRequestModel.setWholeWords(true);

		return searchRequestModel;
	}

	private TextSearchRequestBean getTextSearchRequestBean() {
		final TextSearchRequestBean searchRequestBean = new TextSearchRequestBean();

		searchRequestBean.setCaseSensitive(true);
		searchRequestBean.setPath("/content/eaton/de/de-de/catalog");
		searchRequestBean.setReplaceText("Eaton1");
		searchRequestBean.setSearchText("Eaton");
		searchRequestBean.setWholeWords(true);

		return searchRequestBean;
	}
}
