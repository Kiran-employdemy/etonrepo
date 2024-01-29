package com.eaton.platform.core.services.search.find.replace.tags.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.day.cq.commons.jcr.JcrConstants;
import com.eaton.platform.core.bean.search.find.replace.FindReplacePropertyBean;
import com.eaton.platform.core.bean.search.find.replace.request.ContentType;
import com.eaton.platform.core.bean.search.find.replace.request.Mode;
import com.eaton.platform.core.bean.search.find.replace.request.TagsModificationRequestBean;
import com.eaton.platform.core.bean.search.find.replace.request.TagsSearchRequestBean;
import com.eaton.platform.core.bean.search.find.replace.response.ResultItem;
import com.eaton.platform.core.bean.search.find.replace.response.TagsModificationResultBean;
import com.eaton.platform.core.bean.search.find.replace.response.TagsSearchResultBean;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.search.find.replace.FindReplaceConfigService;
import com.eaton.platform.core.util.NonClosableResourceResolverWrapper;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

/**
 * The Class FindReplaceTagsDataPageServiceImplTest.
 *
 * @author Jaroslav Rassadin
 */
@ExtendWith(
		value = { AemContextExtension.class, MockitoExtension.class })
class FindReplaceTagsDataPageServiceImplTest {

	private final static String PAGE_SEARCH_PATH = "/content/eaton/de/de-de/catalog/emobility";

	private final static String PAGE_PATH = PAGE_SEARCH_PATH + "/page";

	private final static String PAGE_CONTENT_PATH = PAGE_PATH + "/" + JcrConstants.JCR_CONTENT;

	private final static String PAGE_TITLE = "Some title";

	private final static String ACCOUNT_TYPE_AGENT_TAG = "eaton-secure:accounttype/agent";

	private final static String ACCOUNT_TYPE_DISTRIBUTOR_TAG = "eaton-secure:accounttype/distributor";

	private final AemContext context = new AemContext(ResourceResolverType.JCR_OAK);

	@Mock
	private AdminService adminService;

	@Mock
	private FindReplaceConfigService configService;

	@InjectMocks
	private final FindReplaceTagsDataPageServiceImpl classUnderTest = new FindReplaceTagsDataPageServiceImpl();

	/**
	 * Sets up test data.
	 */
	@BeforeEach
	public void setUp() {
		this.context.create().resource(PAGE_SEARCH_PATH);
		this.context.load().json("/com/eaton/platform/core/services/search/find/replace/tags/impl/pageAndComponentWithTags.json", PAGE_PATH);

		this.classUnderTest.setTagPropertiesMap(Map.of("accountType", new FindReplacePropertyBean("accountType")));
	}

	/**
	 * Test that tags addition executed correctly.
	 */
	@Test
	@DisplayName("Tags addition executed correctly")
	void testTagsAdditionExecutedCorrectly() {
		// set up
		when(this.adminService.getWriteService()).thenReturn(new NonClosableResourceResolverWrapper(this.context.resourceResolver()));

		// exercise
		final TagsModificationResultBean result = this.classUnderTest.add(this.getTagsModificationRequestBean(Mode.ADD));

		// verify
		assertEquals(this.getExpectedResultItem(), result.getResults().get(0), "Should be equal paths");
		assertTrue(Arrays.asList(this.context.resourceResolver().getResource(PAGE_PATH + "/" + JcrConstants.JCR_CONTENT).getValueMap().get("accountType",
				String[].class)).contains(ACCOUNT_TYPE_DISTRIBUTOR_TAG), "Should contain a new tag");
	}

	/**
	 * Test that tags deletion executed correctly.
	 */
	@Test
	@DisplayName("Tags deletion executed correctly")
	void testTagsDeletionExecutedCorrectly() {
		// set up
		final TagsModificationRequestBean modificationRequestBean = this.getTagsModificationRequestBean(Mode.DELETE);
		modificationRequestBean.setTagsForModification(Map.of("accountType", Arrays.asList(ACCOUNT_TYPE_AGENT_TAG)));

		when(this.adminService.getWriteService()).thenReturn(new NonClosableResourceResolverWrapper(this.context.resourceResolver()));

		// exercise
		final TagsModificationResultBean result = this.classUnderTest.delete(modificationRequestBean);

		// verify
		assertEquals(this.getExpectedResultItem(), result.getResults().get(0), "Should be equal paths");
		assertTrue(!Arrays.asList(this.context.resourceResolver().getResource(PAGE_PATH + "/" + JcrConstants.JCR_CONTENT).getValueMap().get("accountType",
				String[].class)).contains(ACCOUNT_TYPE_AGENT_TAG), "Should not contain a deleted tag");
	}

	/**
	 * Test that tags search executed correctly.
	 */
	@Test
	@DisplayName("Tags search executed correctly")
	void testTagsSearchExecutedCorrectly() {
		// set up
		when(this.adminService.getReadService()).thenReturn(new NonClosableResourceResolverWrapper(this.context.resourceResolver()));

		// exercise
		final TagsSearchResultBean result = this.classUnderTest.find(this.getTagsSearchRequestBean());

		// verify
		assertEquals(this.getExpectedResultItem(), result.getResults().get(0), "Should be equal paths");
	}

	/**
	 * Test that tags replacement executed correctly.
	 */
	@Test
	@DisplayName("Tags replacement executed correctly")
	void testTagsReplacementExecutedCorrectly() {
		// set up
		final TagsModificationRequestBean modificationRequestBean = this.getTagsModificationRequestBean(Mode.REPLACE);

		when(this.adminService.getWriteService()).thenReturn(new NonClosableResourceResolverWrapper(this.context.resourceResolver()));

		// exercise
		final TagsModificationResultBean result = this.classUnderTest.replace(modificationRequestBean);

		final List<String> tags = Arrays.asList(this.context.resourceResolver().getResource(PAGE_PATH + "/" + JcrConstants.JCR_CONTENT)
				.getValueMap().get("accountType", String[].class));

		// verify
		assertEquals(this.getExpectedResultItem(), result.getResults().get(0), "Should be equal paths");
		assertFalse(tags.contains(ACCOUNT_TYPE_AGENT_TAG), "Should not contain a replaced tag");
		assertTrue(tags.contains(ACCOUNT_TYPE_DISTRIBUTOR_TAG), "Should contain a new tag");
	}

	private TagsSearchRequestBean getTagsSearchRequestBean() {
		final TagsSearchRequestBean searchRequestBean = new TagsSearchRequestBean();

		searchRequestBean.setContentType(ContentType.PAGE);
		searchRequestBean.setPath(PAGE_SEARCH_PATH);
		searchRequestBean.setTagsToFind(Map.of("accountType", Arrays.asList("eaton-secure:accounttype/fleet", ACCOUNT_TYPE_AGENT_TAG)));

		return searchRequestBean;
	}

	private TagsModificationRequestBean getTagsModificationRequestBean(final Mode mode) {
		final TagsModificationRequestBean modificationRequestBean = new TagsModificationRequestBean();

		modificationRequestBean.setContentType(ContentType.PAGE);
		modificationRequestBean.setPath(PAGE_SEARCH_PATH);
		modificationRequestBean.setTagsToFind(Map.of("accountType", Arrays.asList(ACCOUNT_TYPE_AGENT_TAG)));

		modificationRequestBean.setMode(mode);
		modificationRequestBean.setBackup(true);
		modificationRequestBean.setModificationPaths(Collections.singletonList(PAGE_CONTENT_PATH));
		modificationRequestBean.setReplicate(true);
		modificationRequestBean.setRootSearch(false);
		modificationRequestBean.setTagsForModification(Map.of("accountType", Arrays.asList(ACCOUNT_TYPE_DISTRIBUTOR_TAG)));

		return modificationRequestBean;
	}

	private ResultItem getExpectedResultItem() {
		return ResultItem.builder().withContentType(ContentType.PAGE).withTopContainerPath(PAGE_PATH)
				.withTopContainerTitle(PAGE_TITLE).withContainerPath(PAGE_PATH).withContainerTitle(PAGE_TITLE)
				.withPath(PAGE_CONTENT_PATH).withTitle(PAGE_TITLE).build();
	}
}
