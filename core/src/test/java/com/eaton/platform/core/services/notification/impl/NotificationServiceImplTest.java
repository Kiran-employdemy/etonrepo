package com.eaton.platform.core.services.notification.impl;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adobe.granite.taskmanagement.Task;
import com.adobe.granite.taskmanagement.TaskManager;
import com.adobe.granite.taskmanagement.TaskManagerException;
import com.adobe.granite.taskmanagement.TaskManagerFactory;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.notification.NotificationService;

/**
 * The Class NotificationServiceImplTest.
 *
 * @author Jaroslav Rassadin
 */
@ExtendWith(
		value = { MockitoExtension.class })
class NotificationServiceImplTest {

	@Mock
	private AdminService adminService;

	@Mock
	private ResourceResolver resourceResolver;

	@Mock
	private TaskManager taskManager;

	@Mock
	private TaskManagerFactory taskManagerFactory;

	@Mock
	private Task newTask;

	@InjectMocks
	private final NotificationService classUnderTest = new NotificationServiceImpl();

	/**
	 * Sets up test data.
	 */
	@BeforeEach
	public void setUp() {
		when(this.adminService.getReadService()).thenReturn(this.resourceResolver);
		when(this.resourceResolver.adaptTo(TaskManager.class)).thenReturn(this.taskManager);
		when(this.taskManager.getTaskManagerFactory()).thenReturn(this.taskManagerFactory);
	}

	/**
	 * Test that notification is created with correct parameters.
	 *
	 * @throws TaskManagerException
	 */
	@Test
	@DisplayName("Notification is created with correct parameters")
	void testNotificationIsCreatedWithCorrectParameters() throws TaskManagerException {
		// set up
		final String title = "title";
		final String description = "description";
		final String contentPath = "/content/eaton/language-masters/de-de/catalog/emobility/battery-isolators";
		final String currentAssignee = "admin";

		when(this.taskManagerFactory.newTask("Notification")).thenReturn(this.newTask);

		// exercise
		this.classUnderTest.sendNotification(title, description, contentPath, currentAssignee);

		// verify
		verify(this.newTask, times(1)).setName(title);
		verify(this.newTask, times(1)).setDescription(description);
		verify(this.newTask, times(1)).setContentPath(contentPath);
		verify(this.newTask, times(1)).setCurrentAssignee(currentAssignee);

		verify(this.taskManager, times(1)).createTask(this.newTask);
	}
}
