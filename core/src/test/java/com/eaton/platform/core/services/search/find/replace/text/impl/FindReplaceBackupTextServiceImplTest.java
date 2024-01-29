package com.eaton.platform.core.services.search.find.replace.text.impl;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eaton.platform.core.bean.packaging.PackageBean;
import com.eaton.platform.core.bean.search.find.replace.request.TextModificationRequestBean;
import com.eaton.platform.core.bean.search.find.replace.response.BackupResult;
import com.eaton.platform.core.constants.FindAndReplaceConstants;
import com.eaton.platform.core.services.search.find.replace.FindReplaceBackupService;

/**
 * The Class FindReplaceBackupTextServiceImplTest.
 *
 * @author Jaroslav Rassadin
 */
@ExtendWith(
		value = { MockitoExtension.class })
class FindReplaceBackupTextServiceImplTest {

	private static final String PACKAGE_DESCRIPTION = "Backup for text modification operation.\nAuthor: eatonUser.\nSearch root path: /content/eaton/de/de-de/catalog.\n"
			+ "Search text: Eaton.\nReplace text: Eaton1.\nCase insensitive: false, whole words only: true.";

	private static final String MODIFICATION_PATH = "/content/eaton/de/de-de/catalog/emobility/page";

	private static final String PACKAGE_PATH = "/etc/packages/com.eaton.find.replace.backup/package.zip";

	@Captor
	private ArgumentCaptor<String> stringCaptor;

	@Captor
	private ArgumentCaptor<List<String>> listCaptor;

	@Mock
	private FindReplaceBackupService backupService;

	@InjectMocks
	private final FindReplaceBackupTextServiceImpl classUnderTest = new FindReplaceBackupTextServiceImpl();

	/**
	 * Test that package is created correctly.
	 */
	@Test
	@DisplayName("Package is created correctly")
	void testPackageCreatedCorrectly() {
		// set up
		when(this.backupService.createBackup(anyString(), anyString(), anyList())).thenReturn(this.getPackageBean());

		// exercise
		final BackupResult actualResult = this.classUnderTest.createBackup(this.getTextModificationRequestBean());

		// verify
		verify(this.backupService).createBackup(this.stringCaptor.capture(), this.stringCaptor.capture(), this.listCaptor.capture());

		final String name = this.stringCaptor.getAllValues().get(0);
		final String description = this.stringCaptor.getAllValues().get(1);
		final List<String> paths = this.listCaptor.getValue();

		assertEquals(this.getExpectedBackupResult(), actualResult, "Should be equal results");
		assertEquals(FindAndReplaceConstants.TEXT_PACKAGE_NAME, name, "Should be equal name");
		assertEquals(PACKAGE_DESCRIPTION, description, "Should be equal descriptions");
		assertThat("Should be equal paths", paths, contains(MODIFICATION_PATH));
	}

	private TextModificationRequestBean getTextModificationRequestBean() {
		final TextModificationRequestBean modificationRequestBean = new TextModificationRequestBean();

		modificationRequestBean.setCaseSensitive(true);
		modificationRequestBean.setPath("/content/eaton/de/de-de/catalog");
		modificationRequestBean.setReplaceText("Eaton1");
		modificationRequestBean.setSearchText("Eaton");
		modificationRequestBean.setWholeWords(true);

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
