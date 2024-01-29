package com.eaton.platform.core.services.search.find.replace.text.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eaton.platform.core.bean.search.find.replace.request.ContentType;
import com.eaton.platform.core.bean.search.find.replace.request.TextModificationRequestBean;
import com.eaton.platform.core.bean.search.find.replace.request.TextSearchRequestBean;
import com.eaton.platform.core.bean.search.find.replace.response.ResultItem;
import com.eaton.platform.core.bean.search.find.replace.response.TextModificationResultBean;
import com.eaton.platform.core.bean.search.find.replace.response.TextSearchResultBean;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.search.find.replace.FindReplaceComponentService;
import com.eaton.platform.core.util.NonClosableResourceResolverWrapper;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

/**
 * The Class FindReplaceTextDataComponentServiceImplTest.
 *
 * @author Jaroslav Rassadin
 */
@ExtendWith(
		value = { AemContextExtension.class, MockitoExtension.class })
class FindReplaceTextDataComponentServiceImplTest {

	private final static String PAGE_SEARCH_PATH = "/content/eaton/de/de-de/catalog/emobility";

	private final static String PAGE_TITLE = "Some title";

	private final static String PAGE_PATH = PAGE_SEARCH_PATH + "/page";

	private final static String COMPONENT_PATH = PAGE_PATH + "/jcr:content/root/responsivegrid/how_to_buy";

	private final static String REPLACE_TEXT = "Lorem1";

	private final static String HOW_TO_BUY_COMPONENT_TITLE = "How to Buy";

	private final AemContext context = new AemContext(ResourceResolverType.JCR_OAK);

	@Mock
	private AdminService adminService;

	@Mock
	private FindReplaceComponentService componentService;

	@InjectMocks
	private final FindReplaceTextDataComponentServiceImpl classUnderTest = new FindReplaceTextDataComponentServiceImpl();

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
		when(this.componentService.getTypeTitle(any(Resource.class))).thenReturn(HOW_TO_BUY_COMPONENT_TITLE);

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
		searchRequest.setSearchText("Ipsum");

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
		when(this.componentService.getTypeTitle(any(Resource.class))).thenReturn(HOW_TO_BUY_COMPONENT_TITLE);

		// exercise
		final TextModificationResultBean result = this.classUnderTest.replace(this.getTextModificationRequestBean());

		// verify
		assertEquals(this.getExpectedResultItem(), result.getResults().get(0), "Should be equal paths");
		assertTrue(this.context.resourceResolver().getResource(COMPONENT_PATH).getValueMap().get("text", String.class).contains(REPLACE_TEXT),
				"Should contain replaced text");
	}

	private TextSearchRequestBean getTextSearchRequestBean() {
		final TextSearchRequestBean searchRequestBean = new TextSearchRequestBean();

		searchRequestBean.setContentType(ContentType.COMPONENT);
		searchRequestBean.setCaseSensitive(true);
		searchRequestBean.setPath(PAGE_SEARCH_PATH);
		searchRequestBean.setReplaceText(REPLACE_TEXT);
		searchRequestBean.setSearchText("Lorem");
		searchRequestBean.setWholeWords(false);

		return searchRequestBean;
	}

	private TextModificationRequestBean getTextModificationRequestBean() {
		final TextModificationRequestBean modificationRequestBean = new TextModificationRequestBean();

		modificationRequestBean.setContentType(ContentType.COMPONENT);
		modificationRequestBean.setCaseSensitive(true);
		modificationRequestBean.setPath(PAGE_SEARCH_PATH);
		modificationRequestBean.setReplaceText(REPLACE_TEXT);
		modificationRequestBean.setSearchText("Lorem");
		modificationRequestBean.setWholeWords(false);

		modificationRequestBean.setBackup(true);
		modificationRequestBean.setModificationPaths(Collections.singletonList(COMPONENT_PATH));
		modificationRequestBean.setReplicate(true);
		modificationRequestBean.setRootSearch(false);

		return modificationRequestBean;
	}

	private ResultItem getExpectedResultItem() {
		return ResultItem.builder().withContentType(ContentType.COMPONENT).withTopContainerPath(PAGE_PATH).withTopContainerTitle(PAGE_TITLE)
				.withContainerPath(COMPONENT_PATH).withContainerTitle(HOW_TO_BUY_COMPONENT_TITLE).withPath(COMPONENT_PATH).withTitle(HOW_TO_BUY_COMPONENT_TITLE)
				.build();
	}
}
