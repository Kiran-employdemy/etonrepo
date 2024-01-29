package com.eaton.platform.core.services.search.find.replace.text.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.day.cq.commons.jcr.JcrConstants;
import com.eaton.platform.core.bean.search.find.replace.request.ContentType;
import com.eaton.platform.core.bean.search.find.replace.request.TextModificationRequestBean;
import com.eaton.platform.core.bean.search.find.replace.request.TextSearchRequestBean;
import com.eaton.platform.core.bean.search.find.replace.response.ResultItem;
import com.eaton.platform.core.bean.search.find.replace.response.TextModificationResultBean;
import com.eaton.platform.core.bean.search.find.replace.response.TextSearchResultBean;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.util.NonClosableResourceResolverWrapper;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

/**
 * The Class FindReplaceTextDataPageServiceImplTest.
 *
 * @author Jaroslav Rassadin
 */
@ExtendWith(
		value = { AemContextExtension.class, MockitoExtension.class })
class FindReplaceTextDataPageServiceImplTest {

	private final static String PAGE_SEARCH_PATH = "/content/eaton/de/de-de/catalog/emobility";

	private final static String PAGE_TITLE = "Some title";

	private final static String PAGE_PATH = PAGE_SEARCH_PATH + "/page";

	private final static String PAGE_CONTENT_PATH = PAGE_PATH + "/" + JcrConstants.JCR_CONTENT;

	private final static String REPLACE_TEXT = "Eaton1";

	private final AemContext context = new AemContext(ResourceResolverType.JCR_OAK);

	@Mock
	private AdminService adminService;

	@InjectMocks
	private final FindReplaceTextDataPageServiceImpl classUnderTest = new FindReplaceTextDataPageServiceImpl();

	/**
	 * Sets up test data.
	 */
	@BeforeEach
	public void setUp() {
		this.context.create().resource(PAGE_SEARCH_PATH);
		this.context.load().json("/com/eaton/platform/core/services/search/find/replace/text/impl/pageWithReplacebleProperties.json", PAGE_PATH);
	}

	/**
	 * Test that text search executed correctly.
	 */
	@Test
	@DisplayName("Text search executed correctly")
	void testTextSearchExecutedCorrectly() {
		// set up
		when(this.adminService.getReadService()).thenReturn(new NonClosableResourceResolverWrapper(this.context.resourceResolver()));

		// exercise
		final TextSearchResultBean result = this.classUnderTest.find(this.getTextSearchRequestBean());

		// verify
		assertEquals(this.getExpectedResultItem(), result.getResults().get(0), "Should be equal paths");
	}

	/**
	 * Test that text not found with wrong case.
	 */
	@Test
	@DisplayName("Text not found with wrong case")
	void testTextNotFoundWithWrongCase() {
		// set up
		final TextSearchRequestBean searchRequest = this.getTextSearchRequestBean();
		searchRequest.setCaseSensitive(true);
		searchRequest.setSearchText("eaton");

		when(this.adminService.getReadService()).thenReturn(new NonClosableResourceResolverWrapper(this.context.resourceResolver()));

		// exercise
		final TextSearchResultBean result = this.classUnderTest.find(searchRequest);

		// verify
		assertTrue(result.getResults().isEmpty(), "Should not have results");
	}

	/**
	 * Test that text replacement executed correctly.
	 */
	@Test
	@DisplayName("Text replacement executed correctly")
	void testTextReplacementExecutedCorrectly() {
		// set up
		when(this.adminService.getWriteService()).thenReturn(new NonClosableResourceResolverWrapper(this.context.resourceResolver()));

		// exercise
		final TextModificationResultBean result = this.classUnderTest.replace(this.getTextModificationRequestBean());

		// verify
		assertEquals(this.getExpectedResultItem(), result.getResults().get(0), "Should be equal paths");
		assertTrue(this.context.resourceResolver().getResource(PAGE_PATH + "/" + JcrConstants.JCR_CONTENT).getValueMap().get(JcrConstants.JCR_DESCRIPTION,
				String.class).contains(REPLACE_TEXT), "Should contain replaced text");
	}

	private TextSearchRequestBean getTextSearchRequestBean() {
		final TextSearchRequestBean searchRequestBean = new TextSearchRequestBean();

		searchRequestBean.setContentType(ContentType.PAGE);
		searchRequestBean.setCaseSensitive(true);
		searchRequestBean.setPath(PAGE_SEARCH_PATH);
		searchRequestBean.setReplaceText(REPLACE_TEXT);
		searchRequestBean.setSearchText("Eaton");
		searchRequestBean.setWholeWords(false);

		return searchRequestBean;
	}

	private TextModificationRequestBean getTextModificationRequestBean() {
		final TextModificationRequestBean modificationRequestBean = new TextModificationRequestBean();

		modificationRequestBean.setContentType(ContentType.PAGE);
		modificationRequestBean.setCaseSensitive(true);
		modificationRequestBean.setPath(PAGE_SEARCH_PATH);
		modificationRequestBean.setReplaceText(REPLACE_TEXT);
		modificationRequestBean.setSearchText("Eaton");
		modificationRequestBean.setWholeWords(false);

		modificationRequestBean.setBackup(true);
		modificationRequestBean.setModificationPaths(Collections.singletonList(PAGE_CONTENT_PATH));
		modificationRequestBean.setReplicate(true);
		modificationRequestBean.setRootSearch(false);

		return modificationRequestBean;
	}

	private ResultItem getExpectedResultItem() {
		return ResultItem.builder().withContentType(ContentType.PAGE).withTopContainerPath(PAGE_PATH).withTopContainerTitle(PAGE_TITLE)
				.withContainerPath(PAGE_PATH).withContainerTitle(PAGE_TITLE).withPath(PAGE_CONTENT_PATH).withTitle(PAGE_TITLE).build();
	}

}
