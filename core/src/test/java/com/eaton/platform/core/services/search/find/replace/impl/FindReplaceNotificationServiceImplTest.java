package com.eaton.platform.core.services.search.find.replace.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.value.StringValue;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adobe.acs.commons.email.EmailService;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.notification.NotificationService;

import io.wcm.testing.mock.aem.junit5.AemContext;

@ExtendWith(value = { MockitoExtension.class })
class FindReplaceNotificationServiceImplTest {

	private final AemContext context = new AemContext(ResourceResolverType.JCR_MOCK);
	
	@Mock
	private AdminService adminService;
	
	@Mock
	private NotificationService notificationService;
	
	@Mock
	private EmailService emailService;
	
	@Mock
	private UserManager userManager;
	
	@Mock
	private User authorizable;
	
	@InjectMocks
	private final FindReplaceNotificationServiceImpl classUnderTest = new FindReplaceNotificationServiceImpl();
	
	@Test
	@DisplayName("Notify method call")
	void testNotification() throws Exception {
		Map<String,String> items = new HashMap<>();
		items.put("/content/eaton/test", "Test Page");	
		
		ResourceResolver resourceResolver = Mockito.spy(context.resourceResolver());
		Mockito.when(adminService.getWriteService()).thenReturn(resourceResolver);
		Mockito.when(resourceResolver.adaptTo(Session.class)).thenReturn(context.resourceResolver().adaptTo(Session.class));
		Mockito.when(resourceResolver.adaptTo(UserManager.class)).thenReturn(userManager);
		Mockito.when(userManager.getAuthorizable("admin")).thenReturn(authorizable);
		Mockito.when(authorizable.hasProperty(CommonConstants.EMAIL)).thenReturn(true);
		Mockito.when(authorizable.getProperty(CommonConstants.EMAIL)).thenReturn(new StringValue[]{new StringValue("fakeemailaddress")});
		
		classUnderTest.notifyUser(items, "admin");
	}
	
	@Test
	@DisplayName("Report content is generated correctly")
	void testReportContentGeneratedCorrectly() throws IOException {
		final String expectedContent = IOUtils.toString(
				this.getClass().getClassLoader().getResourceAsStream("com/eaton/platform/core/services/search/find/replace/impl/findandreplace_conflict_report.csv"),
				StandardCharsets.UTF_8);
		
		Map<String,String> items = new HashMap<>();
		items.put("/content/eaton/test", "Test Page");
		
		// exercise
		final String actualContent = this.classUnderTest.buildReportContent(items);

		// verify
		assertEquals(expectedContent, actualContent, "Should be equal string");
	}
	
	@Test
	@DisplayName("Conflict report resource is created correctly")
	void testReportResourceCreated() throws IOException, RepositoryException {
		// set up
		final Session session = this.context.resourceResolver().adaptTo(Session.class);

		final String resourceName = FindReplaceNotificationServiceImpl.REPORTS_NAME_PREFIX + "2024-01-03.csv";
		final String expectedResourcePath = FindReplaceNotificationServiceImpl.REPORTS_PATH + "/" + resourceName;
		final String expectedContent = IOUtils.toString(
				this.getClass().getClassLoader().getResourceAsStream("com/eaton/platform/core/services/search/find/replace/impl/findandreplace_conflict_report.csv"),
				StandardCharsets.UTF_8);

		// exercise
		final String actualResourcePath = this.classUnderTest.createReportResource(FindReplaceNotificationServiceImpl.REPORTS_PATH, resourceName,
				expectedContent, FindReplaceNotificationServiceImpl.CSV_MIME_TYPE, session);

		// verify
		assertEquals(expectedResourcePath, actualResourcePath, "Should be equal path");
		assertNotNull("Resource should exist", this.context.resourceResolver().getResource(expectedResourcePath));
	}
}
