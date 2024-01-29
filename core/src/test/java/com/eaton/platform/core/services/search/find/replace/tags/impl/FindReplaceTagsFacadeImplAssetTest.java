package com.eaton.platform.core.services.search.find.replace.tags.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.day.cq.dam.api.DamConstants;
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
 * The Class FindReplaceTagsFacadeImplAssetTest.
 *
 * @author Jaroslav Rassadin
 */
@ExtendWith(
		value = { MockitoExtension.class })
class FindReplaceTagsFacadeImplAssetTest {

	private final static String SEARCH_PATH = "/content/dam/eaton";

	private final static String ASSET_1_PATH = SEARCH_PATH + "/asset1";

	private final static String ASSET_1_METADATA_PATH = ASSET_1_PATH + "/" + JcrConstants.JCR_CONTENT + "/" + DamConstants.METADATA_FOLDER;

	private final static String ASSET_2_PATH = SEARCH_PATH + "/asset2";

	private final static String ASSET_2_METADATA_PATH = ASSET_2_PATH + "/" + JcrConstants.JCR_CONTENT + "/" + DamConstants.METADATA_FOLDER;

	private final static String ASSET_1_TITLE = "asset1";

	private final static String ASSET_2_TITLE = "asset2";

	private static final String PACKAGE_PATH = "/etc/packages/com.eaton.find.replace.backup/package.zip";

	private final static List<String> MODIFICATION_PATHS = Arrays.asList(ASSET_1_METADATA_PATH, ASSET_2_METADATA_PATH);

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

		when(this.adminService.getReadService()).thenReturn(this.resourceResolver);
		when(this.resourceResolver.adaptTo(PageManager.class)).thenReturn(this.pageManager);
		when(this.tagsDataService.add(any(TagsModificationRequestBean.class))).thenReturn(this.getModificationResultBean());

		final TagsModificationRequestBean modificationRequest = this.getTagsModificationRequestBean();

		// exercise
		final TagsModificationResultBean modificationResult = this.classUnderTest.add(modificationRequest);

		// verify
		verify(this.tagsDataService, times(1)).add(modificationRequest);
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

		when(this.adminService.getReadService()).thenReturn(this.resourceResolver);
		when(this.resourceResolver.adaptTo(PageManager.class)).thenReturn(this.pageManager);
		when(this.tagsDataService.delete(any(TagsModificationRequestBean.class))).thenReturn(this.getModificationResultBean());

		final TagsModificationRequestBean modificationRequest = this.getTagsModificationRequestBean();

		// exercise
		final TagsModificationResultBean modificationResult = this.classUnderTest.delete(modificationRequest);

		// verify
		verify(this.tagsDataService, times(1)).delete(modificationRequest);
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
		when(this.tagsDataService.find(any(TagsSearchRequestBean.class))).thenReturn(this.getSearchResultBean());

		final TagsSearchRequestBean searchRequest = this.getTagsSearchRequestBean();

		// exercise
		final TagsSearchResultBean searchResult = this.classUnderTest.find(searchRequest);

		// verify
		verify(this.tagsDataService, times(1)).find(searchRequest);
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

		when(this.adminService.getReadService()).thenReturn(this.resourceResolver);
		when(this.resourceResolver.adaptTo(PageManager.class)).thenReturn(this.pageManager);
		when(this.tagsDataService.replace(any(TagsModificationRequestBean.class))).thenReturn(this.getModificationResultBean());

		final TagsModificationRequestBean modificationRequest = this.getTagsModificationRequestBean();

		// exercise
		final TagsModificationResultBean modificationResult = this.classUnderTest.replace(modificationRequest);

		// verify
		verify(this.tagsDataService, times(1)).replace(modificationRequest);
		verify(this.backupService, times(1)).createBackup(modificationRequest);

		assertEquals(modificationResult, this.getModificationResultBean(), "Should be equal results");
	}

	private void initBackupMock() {
		when(this.backupService.createBackup(any())).thenReturn(new BackupResult(PACKAGE_PATH));
	}

	private TagsSearchRequestBean getTagsSearchRequestBean() {
		final TagsSearchRequestBean searchRequestBean = new TagsSearchRequestBean();

		searchRequestBean.setContentType(ContentType.ASSET);
		searchRequestBean.setPath(SEARCH_PATH);
		searchRequestBean.setTagsToFind(Map.of("accountType", Arrays.asList("eaton-secure:accounttype/fleet", "eaton-secure:accounttype/agent")));

		return searchRequestBean;
	}

	private TagsModificationRequestBean getTagsModificationRequestBean() {
		final TagsModificationRequestBean modificationRequestBean = new TagsModificationRequestBean();

		modificationRequestBean.setContentType(ContentType.ASSET);
		modificationRequestBean.setPath(SEARCH_PATH);
		modificationRequestBean.setTagsToFind(Map.of("accountType", Arrays.asList("eaton-secure:accounttype/fleet", "eaton-secure:accounttype/agent")));

		modificationRequestBean.setMode(Mode.ADD);
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
		return Arrays.asList(
				ResultItem.builder().withContentType(ContentType.ASSET).withTopContainerPath(ASSET_1_PATH)
						.withTopContainerTitle(ASSET_1_TITLE).withContainerPath(ASSET_1_PATH).withContainerTitle(ASSET_1_TITLE)
						.withPath(ASSET_1_METADATA_PATH).withTitle(ASSET_1_TITLE).build(),

				ResultItem.builder().withContentType(ContentType.ASSET).withTopContainerPath(ASSET_2_PATH)
						.withTopContainerTitle(ASSET_2_TITLE).withContainerPath(ASSET_2_PATH).withContainerTitle(ASSET_2_TITLE)
						.withPath(ASSET_2_METADATA_PATH).withTitle(ASSET_2_TITLE).build());
	}

	private ModificationOperationsResult geExpectedtModificationOperations() {
		final ModificationOperationsResult operations = new ModificationOperationsResult();

		operations.setBackup(new BackupResult(PACKAGE_PATH));
		operations.setReplication(new ReplicationResult());

		return operations;
	}
}