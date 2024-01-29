package com.eaton.platform.core.services.converter.search.find.replace.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.eaton.platform.core.bean.search.find.replace.request.TextModificationRequestBean;
import com.eaton.platform.core.bean.search.find.replace.request.TextSearchRequestBean;
import com.eaton.platform.core.models.search.find.replace.TextModificationRequestModel;

/**
 * The Class TextModificationRequestModel2BeanConverterImplTest.
 *
 * @author Jaroslav Rassadin
 */
class TextModificationRequestModel2BeanConverterImplTest {

	private final TextModificationRequestModel2BeanConverterImpl classUnderTest = new TextModificationRequestModel2BeanConverterImpl();

	/**
	 * Test that convertation is correct.
	 */
	@Test
	@DisplayName("Convertation is correct")
	void testConvertationIsCorrect() {
		// set up
		this.classUnderTest.setTextSearchRequestModel2BeanConverter(new TextSearchRequestModel2BeanConverterImpl());

		// exercise
		final TextSearchRequestBean actual = this.classUnderTest.convert(this.getTextModificationRequestModel());

		// verify
		assertEquals(this.getTextModificationRequestBean(), actual, "Objects should be equal");
	}

	private TextModificationRequestModel getTextModificationRequestModel() {
		final TextModificationRequestModel modificationRequestModel = new TextModificationRequestModel();

		modificationRequestModel.setCaseSensitive(true);
		modificationRequestModel.setPath("/content/eaton/de/de-de/catalog");
		modificationRequestModel.setReplaceText("Eaton1");
		modificationRequestModel.setSearchText("Eaton");
		modificationRequestModel.setWholeWords(true);

		modificationRequestModel.setBackup(true);
		modificationRequestModel.setModificationPaths(Collections.singletonList("/content/eaton/de/de-de/catalog/emobility/test_replace"));
		modificationRequestModel.setReplicate(true);
		modificationRequestModel.setRootSearch(false);

		modificationRequestModel.setUserId("eatonUser");

		return modificationRequestModel;
	}

	private TextModificationRequestBean getTextModificationRequestBean() {
		final TextModificationRequestBean modificationRequestBean = new TextModificationRequestBean();

		modificationRequestBean.setCaseSensitive(true);
		modificationRequestBean.setPath("/content/eaton/de/de-de/catalog");
		modificationRequestBean.setReplaceText("Eaton1");
		modificationRequestBean.setSearchText("Eaton");
		modificationRequestBean.setWholeWords(true);

		modificationRequestBean.setBackup(true);
		modificationRequestBean.setModificationPaths(Collections.singletonList("/content/eaton/de/de-de/catalog/emobility/test_replace"));
		modificationRequestBean.setReplicate(true);
		modificationRequestBean.setRootSearch(false);

		modificationRequestBean.setUserId("eatonUser");

		return modificationRequestBean;
	}
}
