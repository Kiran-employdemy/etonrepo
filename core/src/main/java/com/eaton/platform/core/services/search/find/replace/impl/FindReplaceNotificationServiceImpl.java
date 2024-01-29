package com.eaton.platform.core.services.search.find.replace.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.acs.commons.email.EmailService;
import com.adobe.acs.commons.email.EmailServiceConstants;
import com.day.cq.commons.jcr.JcrConstants;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.exception.EatonSystemException;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.file.AbstractReportGenerator;
import com.eaton.platform.core.services.notification.NotificationService;
import com.eaton.platform.core.services.search.find.replace.FindReplaceNotificationService;
import com.eaton.platform.core.services.search.find.replace.exception.FindReplaceException;
import com.eaton.platform.core.util.CommonUtil;

/**
 * The Class FindReplaceNotificationServiceImpl.
 *
 * Notifies AEM user about pages modified by "find and replace" that can't be replicated.
 *
 * @author Daniel Gruszka
 */
@Component(
		service = FindReplaceNotificationService.class,
		property = {
				AEMConstants.SERVICE_VENDOR_EATON,
				AEMConstants.SERVICE_DESCRIPTION + "Find Replace Notification Service",
				AEMConstants.PROCESS_LABEL + "FindReplaceNotificationServiceImpl" })
public class FindReplaceNotificationServiceImpl extends AbstractReportGenerator implements FindReplaceNotificationService {

	/**
	 * The Enum Headers.
	 */
	private enum Headers {

		/** The page path. */
		PATH("Path"),

		/** The conflicting page. */
		PAGE_NAME("Page name");

		private final String value;

		Headers(final String value) {
			this.value = value;
		}

		/**
		 * Gets the value.
		 *
		 * @return the value
		 */
		public String getValue() {
			return this.value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return this.getValue();
		}
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FindReplaceNotificationServiceImpl.class);
	
	/** The Constant EMAIL_TEMPLATE_PATH. */
	protected static final String EMAIL_TEMPLATE_PATH = "/etc/notification/email/txt/com.eaton.generic.email/findandreplaceActivationConflictNotification.txt";
	
	/** The Constant REPORTS_PATH. */
	protected static final String REPORTS_PATH = "/tmp/eaton/findandreplace/replicaction/conflict/reports";

	/** The Constant REPORTS_NAME_PREFIX. */
	protected static final String REPORTS_NAME_PREFIX = "findandreplace_conflict_report_";

	/** The Constant CSV_MIME_TYPE. */
	protected static final String CSV_MIME_TYPE = "text/csv";
	
	/** The Constant NOTIFICATION_TITLE. */
	protected static final String NOTIFICATION_TITLE = "Action required: find & replace - pages excluded from re-publishing";
	
	/** The Constant NOTIFICATION_DESCRIPTION. */
	protected static final String NOTIFICATION_DESCRIPTION = "The following pages were not re-published due to pre-existing modifications which might still be in progress. "
			+ "Please validate the list with the business requestor.";
	
	@Reference
	private AdminService adminService;
	
	@Reference
	private NotificationService notificationService;
	
	@Reference
	private EmailService emailService;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String notifyUser(final Map<String, String> items, String userId) {
		String reportPath = null;
		if (items != null && !items.isEmpty() && userId != null) {
			//create CSV report
			reportPath = createReport(items);
			//send AEM notification
			sendNotification(NOTIFICATION_TITLE, NOTIFICATION_DESCRIPTION, reportPath, userId);
			//send e-mail to the user performing modifications
			sendEmailNotification(NOTIFICATION_TITLE, NOTIFICATION_DESCRIPTION, reportPath, userId);
		}
		return reportPath;
	}
	
	protected String createReport(final Map<String, String> items) {
		try (ResourceResolver resourceResolver = this.adminService.getWriteService()) {
			final Session session = CommonUtil.adapt(resourceResolver, Session.class);

			final String name = REPORTS_NAME_PREFIX + ZonedDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + ".csv";
			return this.createReportResource(REPORTS_PATH, name, this.buildReportContent(items), CSV_MIME_TYPE, session);
		} catch (final Exception ex) {
			throw new EatonSystemException(StringUtils.EMPTY, "An error has occurred while generating find and replace conflict report.", ex);
		}
	}

	protected String buildReportContent(final Map<String, String> items) {

		final StringWriter stringWriter = new StringWriter();
		final CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader(Arrays.stream(Headers.values()).map(Headers::getValue).toArray(String[]::new))
				.withRecordSeparator(System.lineSeparator());

		try (final CSVPrinter printer = new CSVPrinter(stringWriter, csvFormat)) {
			this.writeRecords(items, printer);
		} catch (final IOException ioex) {
			throw new FindReplaceException("Can build report content.", ioex);
		}
		return stringWriter.toString();
	}
	
	private void writeRecords(final Map<String, String> items, final CSVPrinter printer) {
		items.forEach((key, value) -> {
			try {
				printer.printRecord(key, value);

			} catch (final IOException ioex) {
				throw new FindReplaceException(String.format("Can write record with key: %s value: %s", key, value), ioex);
			}
		});
	}
	
	private void sendNotification(final String title, final String description, final String csvReportPath, final String userId) {
		this.notificationService.sendNotification(title, description, csvReportPath, userId);
	}

	private void sendEmailNotification(final String title, final String description, final String csvReportPath, final String userId) {		
		final Map<String, String> emailParams = new HashMap<>();
		emailParams.put(EmailServiceConstants.SUBJECT, title);
		emailParams.put("body", description);
		
		String reportFileName = StringUtils.substringAfterLast(csvReportPath, "/");
		if (StringUtils.isBlank(reportFileName)) {
			reportFileName = StringUtils.substringBeforeLast(REPORTS_NAME_PREFIX, "_");
		}
		
		try (ResourceResolver resourceResolver = this.adminService.getWriteService()) {
			final List<String> emailAdresses = this.getUserEmails(userId, resourceResolver);
			
			try (InputStream attachmentStream = this.getAttachmentStream(csvReportPath, resourceResolver)) {

				if (attachmentStream == null) {
					throw new FindReplaceException(String.format("Can not get conflict report file %s.", csvReportPath));
				}
				this.emailService.sendEmail(EMAIL_TEMPLATE_PATH, emailParams,
						Collections.singletonMap(reportFileName, new ByteArrayDataSource(attachmentStream, CSV_MIME_TYPE)),
						emailAdresses.toArray(new String[emailAdresses.size()]));
			}
		} catch (final Exception ex) {
			LOGGER.error("An error has occurred while sending find and replace conflict report.", ex);
		}
	}
	
	private List<String> getUserEmails(final String userId, ResourceResolver resourceResolver) throws RepositoryException {
		final List<String> emails = new ArrayList<>();

		final UserManager userManager = CommonUtil.adapt(resourceResolver, UserManager.class);
		Authorizable authorizable = userManager.getAuthorizable(userId);
		if ((authorizable instanceof User) && authorizable.hasProperty(CommonConstants.EMAIL)) {
			final Value[] userEmails = authorizable.getProperty(CommonConstants.EMAIL);

			for (final Value email : userEmails) {
				emails.add(email.getString());
			}
		}

		return emails;
	}
	
	private InputStream getAttachmentStream(String attachementPath, final ResourceResolver resourceResolver) throws RepositoryException {
		final Resource attachementResource = resourceResolver.getResource(attachementPath);

		if (attachementResource != null) {
			final Node attachementNode = attachementResource.adaptTo(Node.class);

			if (attachementNode != null) {
				return attachementNode.getNode(JcrConstants.JCR_CONTENT).getProperty(CommonConstants.JCR_DATA).getBinary().getStream();
			}
		}
		return null;
	}
}
