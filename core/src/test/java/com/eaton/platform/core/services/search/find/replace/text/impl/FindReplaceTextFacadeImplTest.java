package com.eaton.platform.core.services.search.find.replace.text.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import com.eaton.platform.core.bean.search.find.replace.request.TextModificationRequestBean;
import com.eaton.platform.core.bean.search.find.replace.request.TextSearchRequestBean;
import com.eaton.platform.core.bean.search.find.replace.response.BackupResult;
import com.eaton.platform.core.bean.search.find.replace.response.ModificationOperationsResult;
import com.eaton.platform.core.bean.search.find.replace.response.ReplicationResult;
import com.eaton.platform.core.bean.search.find.replace.response.ResultItem;
import com.eaton.platform.core.bean.search.find.replace.response.TextModificationResultBean;
import com.eaton.platform.core.bean.search.find.replace.response.TextSearchResultBean;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.search.find.replace.FindReplaceNotificationService;
import com.eaton.platform.core.services.search.find.replace.FindReplaceReplicationService;
import com.eaton.platform.core.services.search.find.replace.text.FindReplaceBackupTextService;
import com.eaton.platform.core.services.search.find.replace.text.FindReplaceTextDataService;

/**
 * The Class FindReplaceTextFacadeImplTest.
 *
 * @author Jaroslav Rassadin
 */
@ExtendWith(
		value = { MockitoExtension.class })
class FindReplaceTextFacadeImplTest {

	private final static String SEARCH_PATH = "/content/eaton";

	private final static String PAGE_PATH = SEARCH_PATH + "/page1";

	private final static String PAGE_CONTENT_PATH = PAGE_PATH + "/" + JcrConstants.JCR_CONTENT;

	private final static String COMPONENT_PATH = PAGE_CONTENT_PATH + "/comp1";

	private final static String PAGE_TITLE = "page1";

	private final static String COMPONENT_TITLE = "comp1";

	private static final String PACKAGE_PATH = "/etc/packages/com.eaton.find.replace.backup/package.zip";

	private final static List<String> MODIFICATION_PATHS = Arrays.asList(PAGE_CONTENT_PATH, COMPONENT_PATH);

	@Mock
	private FindReplaceTextDataService textDataService;

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
	private FindReplaceBackupTextService backupService;

	@Mock
	private TextSearchResultBean searchResultBean;

	@InjectMocks
	private final FindReplaceTextFacadeImpl classUnderTest = new FindReplaceTextFacadeImpl();

	/**
	 * Test that text search executed correctly.
	 */
	@Test
	@DisplayName("Text search executed correctly")
	void testTextSearchExecutedCorrectly() {
		// set up
		this.initFindMock();

		final TextSearchRequestBean searchRequest = this.getTextSearchRequestBean();

		// exercise
		final TextSearchResultBean searchResult = this.classUnderTest.find(searchRequest);

		// verify
		verify(this.textDataService, times(2)).find(searchRequest);
		assertEquals(searchResult, this.getSearchResultBean(), "Should be equal results");
	}

	/**
	 * Test that text replacement executed correctly.
	 */
	@Test
	@DisplayName("Text replacement executed correctly")
	void testTagsReplacementExecutedCorrectly() {
		// set up
		this.initReplaceMock();
		this.initBackupMock();

		final TextModificationRequestBean modificationRequest = this.getTextModificationRequestBean();

		// exercise
		final TextModificationResultBean modificationResult = this.classUnderTest.replace(modificationRequest);

		// verify
		verify(this.textDataService, times(2)).replace(modificationRequest);
		verify(this.backupService, times(1)).createBackup(modificationRequest);

		assertEquals(modificationResult, this.getModificationResultBean(), "Should be equal results");
	}

	/**
	 * Test that text replacement executed correctly without modification paths.
	 */
	@Test
	@DisplayName("Text replacement executed correctly without modification paths")
	void testTagsReplacementExecutedCorrectlyWithoutModificationPaths() {
		// set up
		this.initFindMock();
		this.initReplaceMock();
		this.initBackupMock();

		final TextModificationRequestBean modificationRequest = this.getTextModificationRequestBean();
		modificationRequest.setModificationPaths(null);

		// exercise
		final TextModificationResultBean modificationResult = this.classUnderTest.replace(modificationRequest);

		// verify
		verify(this.textDataService, times(2)).replace(modificationRequest);
		verify(this.backupService, times(1)).createBackup(modificationRequest);

		assertEquals(modificationResult, this.getModificationResultBean(), "Should be equal results");
	}

	private void initFindMock() {
		when(this.textDataService.find(any(TextSearchRequestBean.class)))
				.thenAnswer(i -> {

					final TextSearchRequestBean searchRequest = (TextSearchRequestBean) i.getArguments()[0];

					if (ContentType.PAGE.equals(searchRequest.getContentType())) {
						return new TextSearchResultBean(new ArrayList<>(Arrays.asList(this.getExpectedPageResultItems())));
					}
					if (ContentType.COMPONENT.equals(searchRequest.getContentType())) {
						return new TextSearchResultBean(new ArrayList<>(Arrays.asList(this.getExpectedComponentesultItems())));
					}
					return null;

				});
	}

	private void initReplaceMock() {
		when(this.adminService.getReadService()).thenReturn(this.resourceResolver);
		when(this.resourceResolver.adaptTo(PageManager.class)).thenReturn(this.pageManager);
		when(this.textDataService.replace(any(TextModificationRequestBean.class)))
				.thenAnswer(i -> {

					final TextModificationRequestBean modificationRequest = (TextModificationRequestBean) i.getArguments()[0];

					if (ContentType.PAGE.equals(modificationRequest.getContentType())) {
						return new TextModificationResultBean(new ArrayList<>(Arrays.asList(this.getExpectedPageResultItems())));
					}
					if (ContentType.COMPONENT.equals(modificationRequest.getContentType())) {
						return new TextModificationResultBean(new ArrayList<>(Arrays.asList(this.getExpectedComponentesultItems())));
					}
					return null;

				});
	}

	private void initBackupMock() {
		when(this.backupService.createBackup(any())).thenReturn(new BackupResult(PACKAGE_PATH));
	}

	private TextSearchRequestBean getTextSearchRequestBean() {
		final TextSearchRequestBean searchRequest = new TextSearchRequestBean();

		searchRequest.setCaseSensitive(true);
		searchRequest.setPath("/content/eaton/de/de-de/catalog");
		searchRequest.setReplaceText("Eaton1");
		searchRequest.setSearchText("Eaton");
		searchRequest.setWholeWords(true);

		return searchRequest;
	}

	private TextModificationRequestBean getTextModificationRequestBean() {
		final TextModificationRequestBean modificationRequest = new TextModificationRequestBean();

		modificationRequest.setCaseSensitive(true);
		modificationRequest.setPath("/content/eaton/de/de-de/catalog");
		modificationRequest.setReplaceText("Eaton1");
		modificationRequest.setSearchText("Eaton");
		modificationRequest.setWholeWords(true);

		modificationRequest.setBackup(true);
		modificationRequest.setModificationPaths(MODIFICATION_PATHS);
		modificationRequest.setReplicate(true);
		modificationRequest.setRootSearch(false);

		return modificationRequest;
	}

	private TextSearchResultBean getSearchResultBean() {
		return new TextSearchResultBean(this.getExpectedResultItems());
	}

	private TextModificationResultBean getModificationResultBean() {
		return new TextModificationResultBean(this.getExpectedResultItems(), this.geExpectedtModificationOperations());
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