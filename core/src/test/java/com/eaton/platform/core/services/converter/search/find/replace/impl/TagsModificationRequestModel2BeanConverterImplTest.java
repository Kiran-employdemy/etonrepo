package com.eaton.platform.core.services.converter.search.find.replace.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.eaton.platform.core.bean.search.find.replace.request.ContentType;
import com.eaton.platform.core.bean.search.find.replace.request.Mode;
import com.eaton.platform.core.bean.search.find.replace.request.TagsModificationRequestBean;
import com.eaton.platform.core.bean.search.find.replace.request.TagsSearchRequestBean;
import com.eaton.platform.core.models.search.find.replace.TagsModificationRequestModel;

/**
 * The Class TagsModificationRequestModel2BeanConverterImplTest.
 *
 * @author Jaroslav Rassadin
 */
class TagsModificationRequestModel2BeanConverterImplTest {

	private final TagsModificationRequestModel2BeanConverterImpl classUnderTest = new TagsModificationRequestModel2BeanConverterImpl();

	/**
	 * Test that convertation is correct.
	 */
	@Test
	@DisplayName("Convertation is correct")
	void testConvertationIsCorrect() {
		// set up
		this.classUnderTest.setTagsSearchRequestModel2BeanConverter(new TagsSearchRequestModel2BeanConverterImpl());

		// exercise
		final TagsSearchRequestBean actual = this.classUnderTest.convert(this.getTagsModificationRequestModel());

		// verify
		assertEquals(this.getTagsModificationRequestBean(), actual, "Objects should be equal");
	}

	private TagsModificationRequestModel getTagsModificationRequestModel() {
		final TagsModificationRequestModel modificationRequestModel = new TagsModificationRequestModel();

		modificationRequestModel.setContentType("asset");
		modificationRequestModel.setPath("/content/eaton/de/de-de/catalog");
		modificationRequestModel.setTagsToFindRaw(Arrays.asList("accountType|eaton-secure:accounttype/fleet", "accountType|eaton-secure:accounttype/agent"));
		modificationRequestModel.setTagsForModificationRaw(Arrays.asList("accountType|eaton-secure:accounttype/consultant"));

		modificationRequestModel.setMode("add");
		modificationRequestModel.setBackup(true);
		modificationRequestModel.setModificationPaths(Collections.singletonList("/content/eaton/de/de-de/catalog/emobility/test_replace"));
		modificationRequestModel.setReplicate(true);
		modificationRequestModel.setRootSearch(false);

		modificationRequestModel.setUserId("eatonUser");

		return modificationRequestModel;
	}

	private TagsModificationRequestBean getTagsModificationRequestBean() {
		final TagsModificationRequestBean modificationRequestBean = new TagsModificationRequestBean();

		modificationRequestBean.setContentType(ContentType.ASSET);
		modificationRequestBean.setPath("/content/eaton/de/de-de/catalog");
		modificationRequestBean.setTagsToFind(Map.of("accountType", Arrays.asList("eaton-secure:accounttype/fleet", "eaton-secure:accounttype/agent")));
		modificationRequestBean.setTagsForModification(Map.of("accountType", Arrays.asList("eaton-secure:accounttype/consultant")));

		modificationRequestBean.setMode(Mode.ADD);
		modificationRequestBean.setBackup(true);
		modificationRequestBean.setModificationPaths(Collections.singletonList("/content/eaton/de/de-de/catalog/emobility/test_replace"));
		modificationRequestBean.setReplicate(true);
		modificationRequestBean.setRootSearch(false);

		modificationRequestBean.setUserId("eatonUser");

		return modificationRequestBean;
	}
}
