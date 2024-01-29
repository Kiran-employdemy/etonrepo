package com.eaton.platform.core.services.msm.impl;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * The Interface RolloutConflictManagerConfig.
 *
 * @author Jaroslav Rassadin
 */
@ObjectClassDefinition(
		name = "Rollout Conflict Manager Config")
public @interface RolloutConflictManagerConfig {

	/**
	 * Send notification.
	 *
	 * @return true, if a notification should be sent to user
	 */
	@AttributeDefinition(
			name = "Send notification",
			description = "If checked, a notification via AEM inbox will be sent to the user, who did rollout, in case conflicts are detected.")
	boolean sendNotification() default false;

	/**
	 * Send email notification.
	 *
	 * @return true, if an email notification should be sent to user
	 */
	@AttributeDefinition(
			name = "Send email notification",
			description = "If checked, an email notification via AEM inbox will be sent to the user, who did rollout, in case conflicts are detected.")
	boolean sendEmailNotification() default false;

	/**
	 * Email notification template path.
	 *
	 * @return the string
	 */
	@AttributeDefinition(
			name = "Notification tempalte path",
			description = "Email notification tempalte path.")
	String emailNotificationTemplatePath() default "/etc/notification/email/txt/com.eaton.generic.email/rolloutConflictNotification.txt";

	/**
	 * Email report template path.
	 *
	 * @return the string
	 */
	@AttributeDefinition(
			name = "Report Tempalte path",
			description = "Email report tempalte path.")
	String emailReportTemplatePath() default "/etc/notification/email/txt/com.eaton.generic.email/rolloutConflictReport.txt";

	/**
	 * Report root path.
	 *
	 * @return the string
	 */
	@AttributeDefinition(
			name = "Report root path",
			description = "Report root path.")
	String reportRootPath() default "/content/eaton";

	/**
	 * Detailed report.
	 *
	 * @return true, if detailed report should be sent
	 */
	@AttributeDefinition(
			name = "Detailed report",
			description = "Detailed report.")
	boolean detailedReport() default false;

	/**
	 * Report recipients.
	 *
	 * @return the string[]
	 */
	@AttributeDefinition(
			name = "Report recipients",
			description = "List of report recipients' emails. Takes precedence over reportRecipientGroup.")
	String[] reportRecipients() default {};

	/**
	 * Report recipient group.
	 *
	 * @return the string
	 */
	@AttributeDefinition(
			name = "Report recipient group",
			description = "Report recipient group name in AEM.")
	String reportRecipientGroup() default "";

	/**
	 * Documentation link.
	 *
	 * @return the string
	 */
	@AttributeDefinition(
			name = "Documentation link",
			description = "Link to documentation.")
	String docLink() default "";

}
