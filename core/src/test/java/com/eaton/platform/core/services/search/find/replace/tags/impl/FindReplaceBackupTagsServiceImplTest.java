package com.eaton.platform.core.services.search.find.replace.tags.impl;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eaton.platform.core.bean.packaging.PackageBean;
import com.eaton.platform.core.bean.search.find.replace.request.ContentType;
import com.eaton.platform.core.bean.search.find.replace.request.Mode;
import com.eaton.platform.core.bean.search.find.replace.request.TagsModificationRequestBean;
import com.eaton.platform.core.bean.search.find.replace.response.BackupResult;
import com.eaton.platform.core.constants.FindAndReplaceConstants;
import com.eaton.platform.core.services.search.find.replace.FindReplaceBackupService;

/**
 * The Class FindReplaceBackupTagsServiceImplTest.
 *
 * @author Jaroslav Rassadin
 */
@ExtendWith(
		value = { MockitoExtension.class })
class FindReplaceBackupTagsServiceImplTest {

	private static final String PACKAGE_DESCRIPTION = "Backup for tags add operation.\nAuthor: eatonUser.\nSearch root path: /content/dam/eaton/secure/channel-marketing/digipacks.\n"
			+ "Tags to find:\naccountType: eaton-secure:accounttype/agent, eaton-secure:accounttype/consultant, eaton-secure:accounttype/contractor, eaton-secure:accounttype/distributor,"
			+ " eaton-secure:accounttype/fleet, ...\nNew tags:\ncountries: eaton:country/north-america/us, eaton:country/north-america/ca;";

	private static final String MODIFICATION_PATH = "/content/dam/eaton/secure/channel-marketing/digipacks/pack/jcr:content/metadata";

	private static final String PACKAGE_PATH = "/etc/packages/com.eaton.find.replace.backup/package.zip";

	@Captor
	private ArgumentCaptor<String> stringCaptor;

	@Captor
	private ArgumentCaptor<List<String>> listCaptor;

	@Mock
	private FindReplaceBackupService backupService;

	@InjectMocks
	private final FindReplaceBackupTagsServiceImpl classUnderTest = new FindReplaceBackupTagsServiceImpl();

	/**
	 * Test that package is created correctly.
	 */
	@Test
	@DisplayName("Package is created correctly")
	void testPackageCreatedCorrectly() {
		// set up
		when(this.backupService.createBackup(anyString(), anyString(), anyList())).thenReturn(this.getPackageBean());

		// exercise
		final BackupResult actualResult = this.classUnderTest.createBackup(this.getTagsModificationRequestBean());

		// verify
		verify(this.backupService).createBackup(this.stringCaptor.capture(), this.stringCaptor.capture(), this.listCaptor.capture());

		final String name = this.stringCaptor.getAllValues().get(0);
		final String description = this.stringCaptor.getAllValues().get(1);
		final List<String> paths = this.listCaptor.getValue();

		assertEquals(this.getExpectedBackupResult(), actualResult, "Should be equal results");
		assertEquals(FindAndReplaceConstants.TAGS_PACKAGE_NAME, name, "Should be equal name");
		assertEquals(PACKAGE_DESCRIPTION, description, "Should be equal descriptions");
		assertThat("Should be equal paths", paths, contains(MODIFICATION_PATH));
	}

	private TagsModificationRequestBean getTagsModificationRequestBean() {
		final TagsModificationRequestBean modificationRequestBean = new TagsModificationRequestBean();

		modificationRequestBean.setContentType(ContentType.ASSET);
		modificationRequestBean.setPath("/content/dam/eaton/secure/channel-marketing/digipacks");
		modificationRequestBean.setTagsToFind(Map.of("accountType", Arrays.asList("eaton-secure:accounttype/agent", "eaton-secure:accounttype/consultant",
				"eaton-secure:accounttype/contractor", "eaton-secure:accounttype/distributor", "eaton-secure:accounttype/fleet",
				"eaton-secure:accounttype/government")));
		modificationRequestBean.setTagsForModification(Map.of("countries", Arrays.asList("eaton:country/north-america/us",
				"eaton:country/north-america/ca")));

		modificationRequestBean.setMode(Mode.ADD);
		modificationRequestBean.setBackup(true);
		modificationRequestBean.setModificationPaths(Collections.singletonList(MODIFICATION_PATH));
		modificationRequestBean.setReplicate(true);
		modificationRequestBean.setRootSearch(false);

		modificationRequestBean.setUserId("eatonUser");

		return modificationRequestBean;
	}

	private PackageBean getPackageBean() {
		return PackageBean.builder().withPath(PACKAGE_PATH).build();
	}

	private BackupResult getExpectedBackupResult() {
		final BackupResult backup = new BackupResult();

		backup.setPackagePath(PACKAGE_PATH);

		return backup;
	}
}
