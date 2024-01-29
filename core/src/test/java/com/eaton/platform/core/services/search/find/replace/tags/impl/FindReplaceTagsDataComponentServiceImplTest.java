package com.eaton.platform.core.services.search.find.replace.tags.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eaton.platform.core.bean.search.find.replace.FindReplacePropertyBean;
import com.eaton.platform.core.bean.search.find.replace.FindReplaceResourceTypeBean;
import com.eaton.platform.core.bean.search.find.replace.request.ContentType;
import com.eaton.platform.core.bean.search.find.replace.request.Mode;
import com.eaton.platform.core.bean.search.find.replace.request.TagsModificationRequestBean;
import com.eaton.platform.core.bean.search.find.replace.request.TagsSearchRequestBean;
import com.eaton.platform.core.bean.search.find.replace.response.ResultItem;
import com.eaton.platform.core.bean.search.find.replace.response.TagsModificationResultBean;
import com.eaton.platform.core.bean.search.find.replace.response.TagsSearchResultBean;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.search.find.replace.FindReplaceComponentService;
import com.eaton.platform.core.services.search.find.replace.FindReplaceConfigService;
import com.eaton.platform.core.util.NonClosableResourceResolverWrapper;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

/**
 * The Class FindReplaceTagsDataComponentServiceImplTest.
 *
 * @author Jaroslav Rassadin
 */
@ExtendWith(
		value = { AemContextExtension.class, MockitoExtension.class })
class FindReplaceTagsDataComponentServiceImplTest {

	private final static String PAGE_SEARCH_PATH = "/content/eaton/de/de-de/catalog/emobility";

	private final static String PAGE_PATH = PAGE_SEARCH_PATH + "/page";

	private final static String PAGE_TITLE = "Some title";

	private final static String PRODUCT_GRID_COMPONENT_PATH = PAGE_PATH + "/jcr:content/root/responsivegrid/product_tabs/content-tab-2/product_grid";

	private final static String ADVANCED_SEARCH_COMPONENT_PATH = PAGE_PATH + "/jcr:content/root/responsivegrid/product_tabs/content-tab-3/advanced_search";

	private final static String ADVANCED_SEARCH_COMPONENT_SUB_PATH = ADVANCED_SEARCH_COMPONENT_PATH + "/whitelistFacets/item0";

	private final static String COUNTRY_DE_TAG = "eaton:country/europe/de";

	private final static String COUNTRY_US_TAG = "eaton:country/north-america/us";

	private final static String COUNTRY_CA_TAG = "eaton:country/north-america/ca";

	private final static String TAGS_PROPERTY_NAME = "tags";

	private final static String PRODUCT_GRID_RESOURCE_TYPE = "eaton/components/product/product-grid";

	private final static String PRODUCT_GRID_COMPONENT_TITLE = "Product Grid";

	private final static String ADVANCED_SEARCH_COMPONENT_TITLE = "Advanced Search";

	private final static String ADVANCED_SEARCH_RESOURCE_TYPE = "eaton/components/secure/advanced-search";

	private final static String DOWNLOAD_LINK_LIST_RESOURCE_TYPE = "eaton/components/general/download-link-list";

	private final AemContext context = new AemContext(ResourceResolverType.JCR_OAK);

	@Mock
	private AdminService adminService;

	@Mock
	private FindReplaceConfigService configService;

	@Mock
	private FindReplaceComponentService componentService;

	@InjectMocks
	private final FindReplaceTagsDataComponentServiceImpl classUnderTest = new FindReplaceTagsDataComponentServiceImpl();

	/**
	 * Sets up test data.
	 */
	@BeforeEach
	public void setUp() {
		this.context.create().resource(PAGE_SEARCH_PATH);
		this.context.load().json("/com/eaton/platform/core/services/search/find/replace/tags/impl/pageAndComponentWithTags.json", PAGE_PATH);

		this.classUnderTest.setTagPropertiesMap(Map.of("facet", new FindReplacePropertyBean(true, "whitelistFacets/*/facet",
				Set.of(new FindReplaceResourceTypeBean(ADVANCED_SEARCH_RESOURCE_TYPE))), TAGS_PROPERTY_NAME, new FindReplacePropertyBean(true,
						TAGS_PROPERTY_NAME, Set.of(new FindReplaceResourceTypeBean(PRODUCT_GRID_RESOURCE_TYPE), new FindReplaceResourceTypeBean(
								DOWNLOAD_LINK_LIST_RESOURCE_TYPE)))));

		when(this.componentService.getTypeTitle(any(Resource.class))).thenAnswer(i -> {

			final Resource resource = (Resource) i.getArguments()[0];

			if (ADVANCED_SEARCH_RESOURCE_TYPE.equals(resource.getResourceType())) {
				return ADVANCED_SEARCH_COMPONENT_TITLE;
			}
			if (PRODUCT_GRID_RESOURCE_TYPE.equals(resource.getResourceType())) {
				return PRODUCT_GRID_COMPONENT_TITLE;
			}
			return null;
		});
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
		assertEquals(this.getExpectedResultItem(), result.getResults().get(0), "Should be equal modification results");
		assertTrue(Arrays.asList(this.context.resourceResolver().getResource(PRODUCT_GRID_COMPONENT_PATH).getValueMap().get(TAGS_PROPERTY_NAME, String[].class))
				.contains(COUNTRY_DE_TAG), "Should contain a new tag");
	}

	/**
	 * Test that tags deletion executed correctly.
	 */
	@Test
	@DisplayName("Tags deletion executed correctly")
	void testTagsDeletionExecutedCorrectly() {
		// set up
		final TagsModificationRequestBean modificationRequestBean = this.getTagsModificationRequestBean(Mode.DELETE);
		modificationRequestBean.setTagsForModification(Map.of(TAGS_PROPERTY_NAME, Arrays.asList(COUNTRY_CA_TAG)));

		when(this.adminService.getWriteService()).thenReturn(new NonClosableResourceResolverWrapper(this.context.resourceResolver()));

		// exercise
		final TagsModificationResultBean result = this.classUnderTest.delete(modificationRequestBean);

		// verify
		assertEquals(this.getExpectedResultItem(), result.getResults().get(0), "Should be equal modification results");
		assertTrue(!Arrays.asList(this.context.resourceResolver().getResource(PRODUCT_GRID_COMPONENT_PATH).getValueMap().get(TAGS_PROPERTY_NAME,
				String[].class)).contains(COUNTRY_CA_TAG), "Should not contain a deleted tag");
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
		assertEquals(this.getExpectedResultItem(), result.getResults().get(0), "Should be equal search results");
	}

	/**
	 * Test that tags search executed correctly for nested property (multifield).
	 */
	@Test
	@DisplayName("Tags search executed correctly for nested property")
	void testTagsSearchExecutedCorrectlyForNestedProperty() {
		// set up
		final TagsSearchRequestBean searchRequestBean = this.getTagsSearchRequestBean();
		searchRequestBean.setTagsToFind(Map.of("facet", Arrays.asList(COUNTRY_US_TAG)));

		when(this.adminService.getReadService()).thenReturn(new NonClosableResourceResolverWrapper(this.context.resourceResolver()));

		// exercise
		final TagsSearchResultBean result = this.classUnderTest.find(searchRequestBean);

		// verify
		assertEquals(ResultItem.builder().withContentType(ContentType.COMPONENT).withTopContainerPath(PAGE_PATH).withTopContainerTitle(PAGE_TITLE)
				.withContainerPath(ADVANCED_SEARCH_COMPONENT_PATH).withContainerTitle(ADVANCED_SEARCH_COMPONENT_TITLE)
				.withPath(ADVANCED_SEARCH_COMPONENT_SUB_PATH).withTitle(ADVANCED_SEARCH_COMPONENT_TITLE).build(), result.getResults().get(0),
				"Should be equal search results");
	}

	private TagsSearchRequestBean getTagsSearchRequestBean() {
		final TagsSearchRequestBean searchRequestBean = new TagsSearchRequestBean();

		searchRequestBean.setContentType(ContentType.COMPONENT);
		searchRequestBean.setPath(PAGE_SEARCH_PATH);
		searchRequestBean.setTagsToFind(Map.of(TAGS_PROPERTY_NAME, Arrays.asList(COUNTRY_US_TAG)));

		return searchRequestBean;
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

		final List<String> tags = Arrays.asList(this.context.resourceResolver().getResource(PRODUCT_GRID_COMPONENT_PATH).getValueMap().get(TAGS_PROPERTY_NAME,
				String[].class));

		// verify
		assertEquals(this.getExpectedResultItem(), result.getResults().get(0), "Should be modification results");
		assertFalse(tags.contains(COUNTRY_US_TAG), "Should not contain a replaced tag");
		assertTrue(tags.contains(COUNTRY_DE_TAG), "Should contain a new tag");
	}

	private TagsModificationRequestBean getTagsModificationRequestBean(final Mode mode) {
		final TagsModificationRequestBean modificationRequestBean = new TagsModificationRequestBean();

		modificationRequestBean.setContentType(ContentType.COMPONENT);
		modificationRequestBean.setPath(PAGE_SEARCH_PATH);
		modificationRequestBean.setTagsToFind(Map.of(TAGS_PROPERTY_NAME, Arrays.asList(COUNTRY_US_TAG)));

		modificationRequestBean.setMode(mode);
		modificationRequestBean.setBackup(true);
		modificationRequestBean.setModificationPaths(Collections.singletonList(PRODUCT_GRID_COMPONENT_PATH));
		modificationRequestBean.setReplicate(true);
		modificationRequestBean.setRootSearch(false);
		modificationRequestBean.setTagsForModification(Map.of(TAGS_PROPERTY_NAME, Arrays.asList(COUNTRY_DE_TAG)));

		return modificationRequestBean;
	}

	private ResultItem getExpectedResultItem() {
		return ResultItem.builder().withContentType(ContentType.COMPONENT).withTopContainerPath(PAGE_PATH).withTopContainerTitle(PAGE_TITLE)
				.withContainerPath(PRODUCT_GRID_COMPONENT_PATH).withContainerTitle(PRODUCT_GRID_COMPONENT_TITLE).withPath(PRODUCT_GRID_COMPONENT_PATH)
				.withTitle(PRODUCT_GRID_COMPONENT_TITLE).build();
	}
}
