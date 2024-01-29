package com.eaton.platform.core.services.search.find.replace;

import java.util.Map;

/**
 * The Interface FindReplaceNotificationService.
 *
 * Notifies AEM user about pages modified by "find and replace" that can't be replicated.
 *
 * @author Daniel Gruszka
 */
public interface FindReplaceNotificationService {

	/**
	 * Send notifcations.
	 * 
	 * @param items
	 * @param userId
	 * @return reportPath
	 */
	String notifyUser(Map<String, String> items, String userId);
}
