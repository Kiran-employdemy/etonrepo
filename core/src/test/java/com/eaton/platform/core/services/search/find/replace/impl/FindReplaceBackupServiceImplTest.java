package com.eaton.platform.core.services.search.find.replace.impl;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eaton.platform.core.bean.packaging.PackagePathFilter;
import com.eaton.platform.core.bean.packaging.PackageRequestBean;
import com.eaton.platform.core.constants.FindAndReplaceConstants;
import com.eaton.platform.core.services.packaging.EatonPackagingService;
import com.eaton.platform.core.services.search.find.replace.FindReplaceBackupServiceConfig;
import com.eaton.platform.core.services.search.find.replace.exception.FindReplaceBackupException;

/**
 * The Class FindReplaceBackupServiceImplTest.
 *
 * @author Jaroslav Rassadin
 */
@ExtendWith(
		value = { MockitoExtension.class })
class FindReplaceBackupServiceImplTest {

	private static final String PACKAGE_DESCRIPTION = "Tags modification backup package";

	private static final String MODIFICATION_PATH = "/content/eaton/de/de-de/catalog/page/jcr:content";

	private static final String GROUP_NAME = "com.eaton.find.replace.backup";

	private static final String PACKAGE_NAME_PREFIX = "com.eaton.find.replace.backup";

	private static final String STATIC_PACKAGE_NAME = PACKAGE_NAME_PREFIX + "_" + FindAndReplaceConstants.TAGS_PACKAGE_NAME;

	@Captor
	private ArgumentCaptor<PackageRequestBean> packageRequestBeanCaptor;

	@Mock
	private EatonPackagingService packagingService;

	@InjectMocks
	private final FindReplaceBackupServiceImpl classUnderTest = new FindReplaceBackupServiceImpl();

	/**
	 * Sets up test data.
	 */
	@BeforeEach
	public void setUp() {
		this.classUnderTest.activate(new FindReplaceBackupServiceConfig() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return FindReplaceBackupServiceConfig.class;
			}

			@Override
			public String[] pathPatterns() {
				return new String[] { "/content/dam/*", "/content/eaton/*" };
			}

			@Override
			public String namePrefix() {
				return PACKAGE_NAME_PREFIX;
			}

			@Override
			public int maxNodes() {
				return 1;
			}

			@Override
			public String group() {
				return GROUP_NAME;
			}
		});
	}

	/**
	 * Test that package is created correctly.
	 */
	@Test
	@DisplayName("Package is created correctly")
	void testPackageCreatedCorrectly() {
		// exercise
		this.classUnderTest.createBackup(FindAndReplaceConstants.TAGS_PACKAGE_NAME, PACKAGE_DESCRIPTION, List.of(MODIFICATION_PATH));

		// verify
		verify(this.packagingService).create(this.packageRequestBeanCaptor.capture());

		final PackageRequestBean packageRequestBean = this.packageRequestBeanCaptor.getValue();

		assertEquals(PACKAGE_DESCRIPTION, packageRequestBean.getDescription(), "Should be equal descriptions");
		assertThat("Should be equal filters", packageRequestBean.getFilters(), contains(new PackagePathFilter(MODIFICATION_PATH,
				Set.of(new ImmutablePair<>(MODIFICATION_PATH + "/.*", Boolean.FALSE)))));
		assertEquals(GROUP_NAME, packageRequestBean.getGroup(), "Should be equal groups");
		assertTrue(String.format("Package name should start with %s", STATIC_PACKAGE_NAME), packageRequestBean.getName().startsWith(STATIC_PACKAGE_NAME));
	}

	/**
	 * Test that empty paths cause exception.
	 */
	@Test
	@DisplayName("Empty paths cause exception")
	void testEmptyPathsCauseException() {
		// setup
		final List<String> paths = Collections.emptyList();

		// exercise and verify
		assertThrows(FindReplaceBackupException.class, () -> {

			this.classUnderTest.createBackup(FindAndReplaceConstants.TAGS_PACKAGE_NAME, PACKAGE_DESCRIPTION, paths);
		}, "Should throw FindReplaceBackupException");
	}

	/**
	 * Test that incorrect path causes exception.
	 */
	@Test
	@DisplayName("Incorrect path causes exception")
	void testIncorrectPathCausesException() {
		// setup
		// path is not matching configured patterns
		final List<String> paths = List.of("/content/eaton");

		// exercise and verify
		assertThrows(FindReplaceBackupException.class, () -> {

			this.classUnderTest.createBackup(FindAndReplaceConstants.TAGS_PACKAGE_NAME, PACKAGE_DESCRIPTION, paths);
		}, "Should throw FindReplaceBackupException");
	}

	/**
	 * Test that nodes number to be backed up that exceed cause exception.
	 */
	@Test
	@DisplayName("Nodes number to be backed up that exceed max cause exception")
	void testNodesNumberToBeBackedUpThatExceedMaxCauseException() {
		// setup
		// number of nodes to be put into package exceeds configured maximum
		final List<String> paths = List.of("/content/eaton/de/de-de/catalog/page/jcr:content", "/content/eaton/de/de-de/catalog/page1/jcr:content");

		// exercise and verify
		assertThrows(FindReplaceBackupException.class, () -> {

			this.classUnderTest.createBackup(FindAndReplaceConstants.TAGS_PACKAGE_NAME, PACKAGE_DESCRIPTION, paths);
		}, "Should throw FindReplaceBackupException");
	}

}
