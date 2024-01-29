package com.eaton.platform.core.services.notification;

/**
 * The Interface NotificationService.
 *
 * @author Jaroslav Rassadin
 */
public interface NotificationService {

	/**
	 * Send notification to AEM inbox.
	 *
	 * @param title
	 *            the title
	 * @param description
	 *            the description
	 * @param contentPath
	 *            the content path
	 * @param userId
	 *            the user id
	 */
	void sendNotification(String title, String description, String contentPath, String userId);
}
