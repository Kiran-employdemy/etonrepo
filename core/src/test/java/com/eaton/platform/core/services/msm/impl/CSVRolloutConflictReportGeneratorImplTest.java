package com.eaton.platform.core.services.msm.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.io.IOUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.day.cq.commons.Externalizer;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.util.NonClosableResourceResolverWrapper;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

/**
 * The Class CSVRolloutConflictReportGeneratorImplTest.
 *
 * @author Jaroslav Rassadin
 */
@ExtendWith(
		value = { AemContextExtension.class, MockitoExtension.class })
class CSVRolloutConflictReportGeneratorImplTest {

	private final AemContext context = new AemContext(ResourceResolverType.JCR_MOCK);

	@Mock
	private AdminService adminService;

	@Mock
	private ResourceResolver resourceResolver;

	@Mock
	private Externalizer externalizer;

	@InjectMocks
	private final CSVRolloutConflictReportGeneratorImpl classUnderTest = new CSVRolloutConflictReportGeneratorImpl();

	/**
	 * Test that conflict report generated correctly.
	 */
	@Test
	@DisplayName("Conflict report is generated correctly")
	void testConflictReportGeneratedCorrectly() {
		// set up
		when(this.adminService.getWriteService()).thenReturn(new NonClosableResourceResolverWrapper(this.context.resourceResolver()));
		this.context.registerAdapter(ResourceResolver.class, Externalizer.class, this.externalizer);
		when(this.externalizer.externalLink(any(ResourceResolver.class), anyString(), anyString()))
				.thenAnswer(i -> "http://localhost:4502" + i.getArguments()[2]);

		final RolloutConflictSearchResult result = RolloutConflictTestHelper.buildSearchResult();

		final String name = CSVRolloutConflictReportGeneratorImpl.REPORTS_NAME_PREFIX + ZonedDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + ".csv";
		final RolloutConflictReport expectedReport = new RolloutConflictReport(CSVRolloutConflictReportGeneratorImpl.CSV_MIME_TYPE, name,
				CSVRolloutConflictReportGeneratorImpl.REPORTS_PATH + "/" + name);

		// exercise
		final RolloutConflictReport actualReport = this.classUnderTest.generateReport(result, false);

		// verify
		assertEquals(expectedReport, actualReport, "Should be equal report");
	}

	/**
	 * Test that simple conflict report content is generated correctly.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	@DisplayName("Simple conflict report content is generated correctly")
	void testSimpleConflictReportContentGeneratedCorrectly() throws IOException {
		// set up
		when(this.resourceResolver.adaptTo(Externalizer.class)).thenReturn(this.externalizer);
		when(this.externalizer.externalLink(any(ResourceResolver.class), anyString(), anyString()))
				.thenAnswer(i -> "http://localhost:4502" + i.getArguments()[2]);

		final String expectedContent = IOUtils.toString(
				this.getClass().getClassLoader().getResourceAsStream("com/eaton/platform/core/services/msm/impl/simple_msm_conflict_report.csv"),
				StandardCharsets.UTF_8);

		final RolloutConflictSearchResult result = RolloutConflictTestHelper.buildSearchResult();

		// exercise
		final String actualContent = this.classUnderTest.buildReportContent(result, false, this.resourceResolver);

		// verify
		assertEquals(expectedContent, actualContent, "Should be equal string");
	}

	/**
	 * Test that conflict report resource is created correctly.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws RepositoryException
	 *             the repository exception
	 */
	@Test
	@DisplayName("Conflict report resource is created correctly")
	void testConflictReportResourceCreatedCorrectly() throws IOException, RepositoryException {
		// set up
		final Session session = this.context.resourceResolver().adaptTo(Session.class);

		final String resourceName = CSVRolloutConflictReportGeneratorImpl.REPORTS_NAME_PREFIX + "2023-08-16.csv";
		final String expectedResourcePath = CSVRolloutConflictReportGeneratorImpl.REPORTS_PATH + "/" + resourceName;
		final String expectedContent = IOUtils.toString(
				this.getClass().getClassLoader().getResourceAsStream("com/eaton/platform/core/services/msm/impl/simple_msm_conflict_report.csv"),
				StandardCharsets.UTF_8);

		// exercise
		final String actualResourcePath = this.classUnderTest.createReportResource(CSVRolloutConflictReportGeneratorImpl.REPORTS_PATH, resourceName,
				expectedContent, CSVRolloutConflictReportGeneratorImpl.CSV_MIME_TYPE, session);

		// verify
		assertEquals(expectedResourcePath, actualResourcePath, "Should be equal path");
		assertNotNull("Resource should exist", this.context.resourceResolver().getResource(expectedResourcePath));
	}

}
