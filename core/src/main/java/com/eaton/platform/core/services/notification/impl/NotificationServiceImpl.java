package com.eaton.platform.core.services.notification.impl;

import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.taskmanagement.Task;
import com.adobe.granite.taskmanagement.TaskManager;
import com.adobe.granite.taskmanagement.TaskManagerFactory;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.notification.NotificationService;
import com.eaton.platform.core.util.CommonUtil;

/**
 * The Class NotificationServiceImpl.
 *
 * @author Jaroslav Rassadin
 */
@Component(
		service = NotificationService.class,
		property = {
				AEMConstants.SERVICE_VENDOR_EATON,
				AEMConstants.SERVICE_DESCRIPTION + "Notification Service",
				AEMConstants.PROCESS_LABEL + "NotificationServiceImpl" })
public class NotificationServiceImpl implements NotificationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceImpl.class);

	@Reference
	private AdminService adminService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendNotification(final String title, final String description, final String contentPath, final String userId) {

		try (ResourceResolver resourceResolver = this.adminService.getReadService()) {
			final TaskManager taskManager = CommonUtil.adapt(resourceResolver, TaskManager.class);
			final TaskManagerFactory taskManagerFactory = taskManager.getTaskManagerFactory();

			final Task newTask = taskManagerFactory.newTask("Notification");

			newTask.setName(title);
			newTask.setDescription(description);
			newTask.setContentPath(contentPath);
			newTask.setCurrentAssignee(userId);

			taskManager.createTask(newTask);

		} catch (final Exception ex) {
			LOGGER.error("An error has occurred while sendin notification to AEM inbox.", ex);
		}
	}

}
