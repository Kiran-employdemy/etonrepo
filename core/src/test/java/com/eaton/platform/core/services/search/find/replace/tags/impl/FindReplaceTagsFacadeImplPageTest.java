package com.eaton.platform.core.services.search.find.replace.tags.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.bean.search.find.replace.request.ContentType;
import com.eaton.platform.core.bean.search.find.replace.request.Mode;
import com.eaton.platform.core.bean.search.find.replace.request.TagsModificationRequestBean;
import com.eaton.platform.core.bean.search.find.replace.request.TagsSearchRequestBean;
import com.eaton.platform.core.bean.search.find.replace.response.BackupResult;
import com.eaton.platform.core.bean.search.find.replace.response.ModificationOperationsResult;
import com.eaton.platform.core.bean.search.find.replace.response.ReplicationResult;
import com.eaton.platform.core.bean.search.find.replace.response.ResultItem;
import com.eaton.platform.core.bean.search.find.replace.response.TagsModificationResultBean;
import com.eaton.platform.core.bean.search.find.replace.response.TagsSearchResultBean;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.search.find.replace.FindReplaceNotificationService;
import com.eaton.platform.core.services.search.find.replace.FindReplaceReplicationService;
import com.eaton.platform.core.services.search.find.replace.tags.FindReplaceBackupTagsService;
import com.eaton.platform.core.services.search.find.replace.tags.FindReplaceTagsDataService;

/**
 * The Class FindReplaceTagsFacadeImplPageTest.
 *
 * @author Jaroslav Rassadin
 */
@ExtendWith(
		value = { MockitoExtension.class })
class FindReplaceTagsFacadeImplPageTest {

	private final static String SEARCH_PATH = "/content/eaton";

	private final static String PAGE_PATH = SEARCH_PATH + "/page1";

	private final static String PAGE_CONTENT_PATH = PAGE_PATH + "/" + JcrConstants.JCR_CONTENT;

	private final static String COMPONENT_PATH = PAGE_CONTENT_PATH + "/comp1";

	private final static String PAGE_TITLE = "page1";

	private final static String COMPONENT_TITLE = "comp1";

	private static final String PACKAGE_PATH = "/etc/packages/com.eaton.find.replace.backup/package.zip";

	private final static List<String> MODIFICATION_PATHS = Arrays.asList(PAGE_CONTENT_PATH, COMPONENT_PATH);

	@Mock
	private FindReplaceTagsDataService tagsDataService;

	@Mock
	private FindReplaceBackupTagsService backupService;

	@Mock
	private FindReplaceReplicationService replicationService;
	
	@Mock
	private FindReplaceNotificationService notificationService;

	@Mock
	private AdminService adminService;

	@Mock
	private PageManager pageManager;

	@Mock
	private ResourceResolver resourceResolver;

	@Mock
	private TagsSearchResultBean searchResultBean;

	@Mock
	private TagsModificationResultBean modificationResultBean;

	@InjectMocks
	private final FindReplaceTagsFacadeImpl classUnderTest = new FindReplaceTagsFacadeImpl();

	/**
	 * Test that tags addition executed correctly.
	 */
	@Test
	@DisplayName("Tags addition executed correctly")
	void testTagsAdditionExecutedCorrectly() {
		// set up
		this.initBackupMock();
		this.initAddMock();

		final TagsModificationRequestBean modificationRequest = this.getTagsModificationRequestBean(Mode.ADD);

		// exercise
		final TagsModificationResultBean modificationResult = this.classUnderTest.add(modificationRequest);

		// verify
		verify(this.tagsDataService, times(2)).add(modificationRequest);
		verify(this.backupService, times(1)).createBackup(modificationRequest);

		assertEquals(modificationResult, this.getModificationResultBean(), "Should be equal results");
	}

	/**
	 * Test that tags addition executed correctly without modification paths.
	 */
	@Test
	@DisplayName("Tags addition executed correctly without modification paths")
	void testTagsAdditionExecutedCorrectlyWithoutModificationPaths() {
		// set up
		this.initBackupMock();
		this.initFindMock();
		this.initAddMock();

		final TagsModificationRequestBean modificationRequest = this.getTagsModificationRequestBean(Mode.ADD);
		modificationRequest.setModificationPaths(null);

		// exercise
		final TagsModificationResultBean modificationResult = this.classUnderTest.add(modificationRequest);

		// verify
		verify(this.tagsDataService, times(2)).add(modificationRequest);
		verify(this.backupService, times(1)).createBackup(modificationRequest);

		assertEquals(modificationResult, this.getModificationResultBean(), "Should be equal results");
	}

	/**
	 * Test that tags deletion executed correctly without modification paths.
	 */
	@Test
	@DisplayName("Tags deletion executed correctly without modification paths")
	void testTagsDeletionExecutedCorrectlyWithoutModificationPaths() {
		// set up
		this.initBackupMock();
		this.initFindMock();
		this.initDeleteMock();

		final TagsModificationRequestBean modificationRequest = this.getTagsModificationRequestBean(Mode.DELETE);
		modificationRequest.setModificationPaths(null);

		// exercise
		final TagsModificationResultBean modificationResult = this.classUnderTest.delete(modificationRequest);

		// verify
		verify(this.tagsDataService, times(2)).delete(modificationRequest);
		verify(this.backupService, times(1)).createBackup(modificationRequest);

		assertEquals(modificationResult, this.getModificationResultBean(), "Should be equal results");
	}

	/**
	 * Test that tags deletion executed correctly.
	 */
	@Test
	@DisplayName("Tags deletion executed correctly")
	void testTagsDeletionExecutedCorrectly() {
		// set up
		this.initBackupMock();
		this.initDeleteMock();

		final TagsModificationRequestBean modificationRequest = this.getTagsModificationRequestBean(Mode.DELETE);

		// exercise
		final TagsModificationResultBean modificationResult = this.classUnderTest.delete(modificationRequest);

		// verify
		verify(this.tagsDataService, times(2)).delete(modificationRequest);
		verify(this.backupService, times(1)).createBackup(modificationRequest);

		assertEquals(modificationResult, this.getModificationResultBean(), "Should be equal results");
	}

	/**
	 * Test that tags search executed correctly.
	 */
	@Test
	@DisplayName("Tags search executed correctly")
	void testTagsSearchExecutedCorrectly() {
		// set up
		this.initFindMock();

		final TagsSearchRequestBean searchRequest = this.getTagsSearchRequestBean();

		// exercise
		final TagsSearchResultBean searchResult = this.classUnderTest.find(searchRequest);

		// verify
		verify(this.tagsDataService, times(2)).find(searchRequest);
		assertEquals(searchResult, this.getSearchResultBean(), "Should be equal results");
	}

	/**
	 * Test that tags replacement executed correctly.
	 */
	@Test
	@DisplayName("Tags replacement executed correctly")
	void testTagsReplacementExecutedCorrectly() {
		// set up
		this.initBackupMock();
		this.initReplaceMock();

		final TagsModificationRequestBean modificationRequest = this.getTagsModificationRequestBean(Mode.REPLACE);

		// exercise
		final TagsModificationResultBean modificationResult = this.classUnderTest.replace(modificationRequest);

		// verify
		verify(this.tagsDataService, times(2)).replace(modificationRequest);
		verify(this.backupService, times(1)).createBackup(modificationRequest);

		assertEquals(modificationResult, this.getModificationResultBean(), "Should be equal results");
	}

	/**
	 * Test that tags replacement executed correctly without modification paths.
	 */
	@Test
	@DisplayName("Tags replacement executed correctly without modification paths")
	void testTagsReplacementExecutedCorrectlyWithoutModificationPaths() {
		// set up
		this.initBackupMock();
		this.initFindMock();
		this.initReplaceMock();

		final TagsModificationRequestBean modificationRequest = this.getTagsModificationRequestBean(Mode.REPLACE);
		modificationRequest.setModificationPaths(null);

		// exercise
		final TagsModificationResultBean modificationResult = this.classUnderTest.replace(modificationRequest);

		// verify
		verify(this.tagsDataService, times(2)).replace(modificationRequest);
		verify(this.backupService, times(1)).createBackup(modificationRequest);

		assertEquals(modificationResult, this.getModificationResultBean(), "Should be equal results");
	}

	private void initFindMock() {
		when(this.tagsDataService.find(any(TagsSearchRequestBean.class)))
				.thenAnswer(i -> {

					final TagsSearchRequestBean requestBean = (TagsSearchRequestBean) i.getArguments()[0];

					if (ContentType.PAGE.equals(requestBean.getContentType())) {
						return new TagsSearchResultBean(new ArrayList<>(Arrays.asList(this.getExpectedPageResultItems())));
					}
					if (ContentType.COMPONENT.equals(requestBean.getContentType())) {
						return new TagsSearchResultBean(new ArrayList<>(Arrays.asList(this.getExpectedComponentesultItems())));
					}
					return null;

				});
	}

	private void initAddMock() {
		when(this.adminService.getReadService()).thenReturn(this.resourceResolver);
		when(this.resourceResolver.adaptTo(PageManager.class)).thenReturn(this.pageManager);
		when(this.tagsDataService.add(any(TagsModificationRequestBean.class)))
				.thenAnswer(i -> {

					final TagsModificationRequestBean modificationRequest = (TagsModificationRequestBean) i.getArguments()[0];

					if (ContentType.PAGE.equals(modificationRequest.getContentType())) {
						return new TagsModificationResultBean(new ArrayList<>(Arrays.asList(this.getExpectedPageResultItems())));
					}
					if (ContentType.COMPONENT.equals(modificationRequest.getContentType())) {
						return new TagsModificationResultBean(new ArrayList<>(Arrays.asList(this.getExpectedComponentesultItems())));
					}
					return null;

				});
	}

	private void initDeleteMock() {
		when(this.adminService.getReadService()).thenReturn(this.resourceResolver);
		when(this.resourceResolver.adaptTo(PageManager.class)).thenReturn(this.pageManager);
		when(this.tagsDataService.delete(any(TagsModificationRequestBean.class)))
				.thenAnswer(i -> {

					final TagsModificationRequestBean modificationRequest = (TagsModificationRequestBean) i.getArguments()[0];

					if (ContentType.PAGE.equals(modificationRequest.getContentType())) {
						return new TagsModificationResultBean(new ArrayList<>(Arrays.asList(this.getExpectedPageResultItems())));
					}
					if (ContentType.COMPONENT.equals(modificationRequest.getContentType())) {
						return new TagsModificationResultBean(new ArrayList<>(Arrays.asList(this.getExpectedComponentesultItems())));
					}
					return null;

				});
	}

	private void initReplaceMock() {
		when(this.adminService.getReadService()).thenReturn(this.resourceResolver);
		when(this.resourceResolver.adaptTo(PageManager.class)).thenReturn(this.pageManager);
		when(this.tagsDataService.replace(any(TagsModificationRequestBean.class)))
				.thenAnswer(i -> {

					final TagsModificationRequestBean modificationRequest = (TagsModificationRequestBean) i.getArguments()[0];

					if (ContentType.PAGE.equals(modificationRequest.getContentType())) {
						return new TagsModificationResultBean(new ArrayList<>(Arrays.asList(this.getExpectedPageResultItems())));
					}
					if (ContentType.COMPONENT.equals(modificationRequest.getContentType())) {
						return new TagsModificationResultBean(new ArrayList<>(Arrays.asList(this.getExpectedComponentesultItems())));
					}
					return null;

				});
	}

	private void initBackupMock() {
		when(this.backupService.createBackup(any())).thenReturn(new BackupResult(PACKAGE_PATH));
	}

	private TagsSearchRequestBean getTagsSearchRequestBean() {
		final TagsSearchRequestBean searchRequestBean = new TagsSearchRequestBean();

		searchRequestBean.setContentType(ContentType.PAGE);
		searchRequestBean.setPath(SEARCH_PATH);
		searchRequestBean.setTagsToFind(Map.of("accountType", Arrays.asList("eaton-secure:accounttype/fleet", "eaton-secure:accounttype/agent")));

		return searchRequestBean;
	}

	private TagsModificationRequestBean getTagsModificationRequestBean(final Mode mode) {
		final TagsModificationRequestBean modificationRequestBean = new TagsModificationRequestBean();

		modificationRequestBean.setContentType(ContentType.PAGE);
		modificationRequestBean.setPath(SEARCH_PATH);
		modificationRequestBean.setTagsToFind(Map.of("accountType", Arrays.asList("eaton-secure:accounttype/fleet", "eaton-secure:accounttype/agent")));

		modificationRequestBean.setMode(mode);
		modificationRequestBean.setBackup(true);
		modificationRequestBean.setModificationPaths(MODIFICATION_PATHS);
		modificationRequestBean.setReplicate(true);
		modificationRequestBean.setRootSearch(false);

		return modificationRequestBean;
	}

	private TagsSearchResultBean getSearchResultBean() {
		return new TagsSearchResultBean(this.getExpectedResultItems());
	}

	private TagsModificationResultBean getModificationResultBean() {
		return new TagsModificationResultBean(this.getExpectedResultItems(), this.geExpectedtModificationOperations());
	}

	private List<ResultItem> getExpectedResultItems() {
		return Arrays.asList(this.getExpectedPageResultItems(), this.getExpectedComponentesultItems());
	}

	private ResultItem getExpectedPageResultItems() {

		return ResultItem.builder().withContentType(ContentType.PAGE).withTopContainerPath(PAGE_PATH)
				.withTopContainerTitle(PAGE_TITLE).withContainerPath(PAGE_PATH).withContainerTitle(PAGE_TITLE)
				.withPath(PAGE_CONTENT_PATH).withTitle(PAGE_TITLE).build();
	}

	private ResultItem getExpectedComponentesultItems() {

		return ResultItem.builder().withContentType(ContentType.COMPONENT).withTopContainerPath(PAGE_PATH)
				.withTopContainerTitle(PAGE_TITLE).withContainerPath(COMPONENT_PATH).withContainerTitle(COMPONENT_TITLE)
				.withPath(COMPONENT_PATH).withTitle(COMPONENT_TITLE).build();
	}

	private ModificationOperationsResult geExpectedtModificationOperations() {
		final ModificationOperationsResult operations = new ModificationOperationsResult();

		operations.setBackup(new BackupResult(PACKAGE_PATH));
		operations.setReplication(new ReplicationResult());

		return operations;
	}
}