package com.eaton.platform.core.services.msm.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.value.StringValue;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adobe.acs.commons.email.EmailService;
import com.adobe.acs.commons.email.EmailServiceConstants;
import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.msm.RolloutConflictReportGenerator;
import com.eaton.platform.core.services.msm.RolloutConflictSearchSearvice;
import com.eaton.platform.core.services.notification.NotificationService;
import com.eaton.platform.core.util.NonClosableResourceResolverWrapper;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

/**
 * The Class RolloutConflictManagerImplTest.
 *
 * @author Jaroslav Rassadin
 */
@ExtendWith(
		value = { AemContextExtension.class, MockitoExtension.class })
class RolloutConflictManagerImplTest {

	private final AemContext context = new AemContext(ResourceResolverType.JCR_MOCK);

	@Mock
	private AdminService adminService;

	@Mock
	private EmailService emailService;

	@Mock
	private LiveRelationshipManager liveRelationshipManager;

	@Mock
	private NotificationService notificationService;

	@Mock
	private RolloutConflictSearchSearvice rolloutConflictSearchSearvice;

	@Mock
	private RolloutConflictReportGenerator rolloutConflictReportGenerator;

	@Mock
	private Externalizer externalizer;

	@Mock
	private UserManager userManager;

	@Mock
	private User adminUser;

	@InjectMocks
	private final RolloutConflictManagerImpl classUnderTest = new RolloutConflictManagerImpl();

	/**
	 * Sets up test data.
	 */
	@BeforeEach
	public void setUp() {
		this.context.load().json("/com/eaton/platform/core/services/msm/impl/page_blueprint_root.json",
				"/content/eaton/language-masters/de-de/catalog/emobility");

		when(this.adminService.getWriteService()).thenReturn(new NonClosableResourceResolverWrapper(this.context.resourceResolver()));
	}

	/**
	 * Test that notification is sent for rollout conflict.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	@DisplayName("Notification is sent for rollout conflict")
	void testNotificationIsSentForRolloutConflict() throws IOException {
		// set up
		this.classUnderTest.setSendNotification(true);
		this.classUnderTest.setDockLink("https://confluence-prod.tcc.etn.com/display/EC/Rollout+conflicts");

		final String expectedDescription = IOUtils.toString(
				this.getClass().getClassLoader().getResourceAsStream("com/eaton/platform/core/services/msm/impl/rolloutConflictBody.txt"),
				StandardCharsets.UTF_8);

		this.context.registerAdapter(ResourceResolver.class, Externalizer.class, this.externalizer);
		when(this.externalizer.externalLink(any(ResourceResolver.class), anyString(), anyString()))
				.thenAnswer(i -> "http://localhost:4502" + i.getArguments()[2]);

		when(this.rolloutConflictSearchSearvice.findConflictsForRollout(anyString(), anyList(), anyBoolean()))
				.thenReturn(RolloutConflictTestHelper.buildSearchResult());

		// exercise
		this.classUnderTest.checkAndNotify("/content/eaton/language-masters/de-de/catalog/emobility", Arrays.asList(
				new String[] { "/content/eaton/de/de-de/catalog/emobility/battery-isolators", "/content/eaton/de/de-de/catalog/emobility/battery-protectors" }),
				true, "admin");

		// verify
		verify(this.notificationService, times(1)).sendNotification("Rollout conflict for blueprint page eMobility", expectedDescription,
				"/content/eaton/language-masters/de-de/catalog/emobility", "admin");
	}

	/**
	 * Test that email notification is sent for rollout conflict.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws RepositoryException
	 *             the repository exception
	 */
	@Test
	@DisplayName("Email notification is sent for rollout conflict")
	void testEmailNotificationIsSentForRolloutConflict() throws IOException, RepositoryException {
		// set up
		final String emailNotificationTemplatePath = "/etc/notification/email/txt/com.eaton.generic.email/rolloutConflictNotification.txt";
		final String emailAddress = "admin@localhost.com";

		final String expectedDescription = IOUtils.toString(
				this.getClass().getClassLoader().getResourceAsStream("com/eaton/platform/core/services/msm/impl/rolloutConflictBody.txt"),
				StandardCharsets.UTF_8);

		final Map<String, String> emailParams = new HashMap<>();

		emailParams.put(EmailServiceConstants.SUBJECT, "Rollout conflict for blueprint page eMobility");
		emailParams.put("body", expectedDescription);

		this.context.registerAdapter(ResourceResolver.class, Externalizer.class, this.externalizer);
		when(this.externalizer.externalLink(any(ResourceResolver.class), anyString(), anyString()))
				.thenAnswer(i -> "http://localhost:4502" + i.getArguments()[2]);

		this.context.registerAdapter(ResourceResolver.class, UserManager.class, this.userManager);
		when(this.userManager.getAuthorizable(anyString())).thenReturn(this.adminUser);
		when(this.adminUser.hasProperty(CommonConstants.EMAIL)).thenReturn(true);
		when(this.adminUser.getProperty(CommonConstants.EMAIL)).thenReturn(new Value[] { new StringValue(emailAddress) });

		when(this.rolloutConflictSearchSearvice.findConflictsForRollout(anyString(), anyList(), anyBoolean()))
				.thenReturn(RolloutConflictTestHelper.buildSearchResult());

		this.classUnderTest.setSendEmailNotification(true);
		this.classUnderTest.setEmailNotificationTemplatePath(emailNotificationTemplatePath);
		this.classUnderTest.setDockLink("https://confluence-prod.tcc.etn.com/display/EC/Rollout+conflicts");

		// exercise
		this.classUnderTest.checkAndNotify("/content/eaton/language-masters/de-de/catalog/emobility", Arrays.asList(
				new String[] { "/content/eaton/de/de-de/catalog/emobility/battery-isolators", "/content/eaton/de/de-de/catalog/emobility/battery-protectors" }),
				true, "admin");

		// verify
		verify(this.emailService, times(1)).sendEmail(emailNotificationTemplatePath, emailParams, new String[] { emailAddress });
	}

	/**
	 * Test that conflict report is sent.
	 *
	 * @throws IOException
	 */
	@Test
	@DisplayName("Test conflict report is sent")
	void testConflictReportIsSent() throws IOException {
		// set up
		final String emailReportTemplatePath = "/etc/notification/email/txt/com.eaton.generic.email/rolloutConflictReport.txt";
		final String docLink = "https://confluence-prod.tcc.etn.com/display/EC/Rollout+conflicts";
		final String emailAddress = "admin@localhost.com";
		final String reportName = "msm_conflict_report.csv";
		final String reportPath = "/tmp/reports/" + reportName;

		final Map<String, String> emailParams = new HashMap<>();

		final String date = ZonedDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
		emailParams.put(EmailServiceConstants.SUBJECT, "Rollout conflict report " + date);
		emailParams.put("date", date);
		emailParams.put("docLink", docLink);

		final RolloutConflictReport report = new RolloutConflictReport("text/csv", reportName, reportPath);

		when(this.rolloutConflictSearchSearvice.findConflictsInContent(anyString())).thenReturn(RolloutConflictTestHelper.buildSearchResult());
		when(this.rolloutConflictReportGenerator.generateReport(any(RolloutConflictSearchResult.class), anyBoolean())).thenReturn(report);

		this.classUnderTest.setReportRootPath("/content/eaton");
		this.classUnderTest.setEmailReportTemplatePath(emailReportTemplatePath);
		this.classUnderTest.setDockLink(docLink);
		this.classUnderTest.setReportRecipients(Arrays.asList(new String[] { emailAddress }));

		this.context.load().binaryFile("/com/eaton/platform/core/services/msm/impl/simple_msm_conflict_report.csv", reportPath);

		// exercise
		this.classUnderTest.reportConflicts();

		// verify
		verify(this.emailService, times(1)).sendEmail(eq(emailReportTemplatePath), eq(emailParams), anyMap(), eq(emailAddress));
	}
}
