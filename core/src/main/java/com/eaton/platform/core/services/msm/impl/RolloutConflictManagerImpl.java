package com.eaton.platform.core.services.msm.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RangeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.acs.commons.email.EmailService;
import com.adobe.acs.commons.email.EmailServiceConstants;
import com.day.cq.commons.Externalizer;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.msm.RolloutConflictException;
import com.eaton.platform.core.services.msm.RolloutConflictManager;
import com.eaton.platform.core.services.msm.RolloutConflictReportGenerator;
import com.eaton.platform.core.services.msm.RolloutConflictSearchSearvice;
import com.eaton.platform.core.services.notification.NotificationService;
import com.eaton.platform.core.util.CommonUtil;

/**
 * The Class RolloutConflictManagerImpl.
 *
 * @author Jaroslav Rassadin
 */
@Component(
		service = RolloutConflictManager.class,
		configurationPolicy = ConfigurationPolicy.REQUIRE,
		property = {
				AEMConstants.SERVICE_VENDOR_EATON,
				AEMConstants.SERVICE_DESCRIPTION + "Rollout Conflict Manager",
				AEMConstants.PROCESS_LABEL + "RolloutConflictManagerImpl" })
@Designate(
		ocd = RolloutConflictManagerConfig.class)
public class RolloutConflictManagerImpl implements RolloutConflictManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(RolloutConflictManagerImpl.class);

	private static final String NOTIFICATION_BODY_TEMPLATE_PATH = "com/eaton/platform/core/services/msm/impl/rolloutConflictBody.vm";

	@Reference
	private AdminService adminService;

	@Reference
	private LiveRelationshipManager liveRelationshipManager;

	@Reference
	private EmailService emailService;

	@Reference(
			target = "(type=csv)")
	private RolloutConflictReportGenerator rolloutConflictReportGenerator;

	@Reference
	private NotificationService notificationService;

	@Reference
	private RolloutConflictSearchSearvice rolloutConflictSearchSearvice;

	private boolean sendNotification;

	private boolean sendEmailNotification;

	private String emailNotificationTemplatePath;

	private String emailReportTemplatePath;

	private List<String> reportRecipients;

	private String reportRecipientGroup;

	private String reportRootPath;

	private boolean detailedReport;

	private String docLink;

	/**
	 * Activate the service.
	 *
	 * @param config
	 *            the config
	 */
	@Activate
	@Modified
	protected void activate(final RolloutConflictManagerConfig config) {
		this.sendNotification = config.sendNotification();
		this.sendEmailNotification = config.sendEmailNotification();
		this.emailNotificationTemplatePath = config.emailNotificationTemplatePath();
		this.emailReportTemplatePath = config.emailReportTemplatePath();
		this.reportRootPath = config.reportRootPath();
		this.detailedReport = config.detailedReport();
		this.reportRecipients = Arrays.asList(config.reportRecipients());
		this.reportRecipientGroup = config.reportRecipientGroup();
		this.docLink = config.docLink();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void checkAndNotify(final String blueprintPath, final boolean deep, final String userId) {

		try (ResourceResolver resourceResolver = this.adminService.getReadService()) {
			this.checkAndNotify(blueprintPath, this.getLivecopyPaths(resourceResolver.getResource(blueprintPath)), deep, userId);

		} catch (final Exception ex) {
			LOGGER.error("An error has occurred while processing rollout conflict.", ex);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void checkAndNotify(final String blueprintPath, final List<String> liveCopyPaths, final boolean deep, final String userId) {
		// does not work with read service - userManager.getAuthorizable()
		try (ResourceResolver resourceResolver = this.adminService.getWriteService()) {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Check rollout conflicts for blueprint {}, live copies {}, deep {} .", blueprintPath, String.join(",", liveCopyPaths), deep);
			}
			final RolloutConflictSearchResult result = this.rolloutConflictSearchSearvice.findConflictsForRollout(blueprintPath, liveCopyPaths, deep);

			if (MapUtils.isNotEmpty(result.getItems())) {
				final String title = this.getNotificationTitle(blueprintPath, resourceResolver);
				final String body = this.getNotificationBody(result, this.docLink, resourceResolver);

				if (this.sendNotification) {
					this.sendNotification(title, body, blueprintPath, userId);
				}
				if (this.sendEmailNotification) {
					final List<String> emailAdresses = this.getUserEmails(userId, resourceResolver);

					if (CollectionUtils.isNotEmpty(emailAdresses)) {
						this.sendEmailNotification(title, body, emailAdresses, this.emailNotificationTemplatePath);

					} else {
						LOGGER.warn("Email address is not defined for user {}. Skip email notification.", userId);
					}
				}
			}
		} catch (final Exception ex) {
			LOGGER.error("An error has occurred while processing rollout conflict.", ex);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reportConflicts() {
		try (ResourceResolver resourceResolver = this.adminService.getWriteService()) {
			LOGGER.debug("Reporting rollout conflicts for path {}.", this.reportRootPath);

			final List<String> emailAdresses = this.getReportRecipientsAddresses(this.reportRecipients, this.reportRecipientGroup, resourceResolver);

			if (CollectionUtils.isNotEmpty(emailAdresses)) {
				final RolloutConflictSearchResult result = this.rolloutConflictSearchSearvice.findConflictsInContent(this.reportRootPath);

				final RolloutConflictReport exportReport = this.rolloutConflictReportGenerator.generateReport(result, this.detailedReport);

				this.sendEmailReport(exportReport, this.docLink, emailAdresses, resourceResolver);

			} else {
				LOGGER.warn("Report recipients addresses are not defined. Skip report generation.");
			}
		} catch (final Exception ex) {
			LOGGER.error("An error has occurred while creating rollout conflict report.", ex);
		}
	}

	private List<String> getLivecopyPaths(final Resource resource) throws WCMException {
		final List<String> liveCopyPaths = new ArrayList<>();
		final RangeIterator rangeIterator = this.liveRelationshipManager.getLiveRelationships(resource, "", null);

		while (rangeIterator.hasNext()) {
			final LiveRelationship liveCopy = (LiveRelationship) rangeIterator.next();
			liveCopyPaths.add(liveCopy.getTargetPath());
		}
		return liveCopyPaths;
	}

	private void sendNotification(final String title, final String description, final String contentPath, final String userId) {
		this.notificationService.sendNotification(title, description, contentPath, userId);
	}

	private void sendEmailNotification(final String title, final String description, final List<String> emailAdresses,
			final String emailNotificationTemplatePath) {
		final Map<String, String> emailParams = new HashMap<>();

		emailParams.put(EmailServiceConstants.SUBJECT, title);
		emailParams.put("body", description);

		this.emailService.sendEmail(emailNotificationTemplatePath, emailParams, emailAdresses.toArray(new String[emailAdresses.size()]));
	}

	private List<String> getReportRecipientsAddresses(final List<String> reportRecipients, final String reportRecipientGroup,
			final ResourceResolver resourceResolver) throws RepositoryException {
		return CollectionUtils.isNotEmpty(reportRecipients) ? reportRecipients : this.getReportEmails(reportRecipientGroup, resourceResolver);
	}

	private void sendEmailReport(final RolloutConflictReport exportReport, final String docLink, final List<String> emailAdresses,
			final ResourceResolver resourceResolver) throws RepositoryException {
		final Map<String, String> emailParams = new HashMap<>();

		final String date = ZonedDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
		emailParams.put(EmailServiceConstants.SUBJECT, "Rollout conflict report " + date);
		emailParams.put("date", date);
		emailParams.put("docLink", docLink);

		try (InputStream attachmentStream = this.getAttachmentStream(exportReport, resourceResolver)) {

			if (attachmentStream == null) {
				throw new RolloutConflictException(String.format("Can not get conflict report file %s.", exportReport.getReportPath()));
			}
			this.emailService.sendEmail(this.emailReportTemplatePath, emailParams,
					Collections.singletonMap(exportReport.getName(), new ByteArrayDataSource(attachmentStream, "text/csv")),
					emailAdresses.toArray(new String[emailAdresses.size()]));

		} catch (final IOException ioex) {
			throw new RolloutConflictException("Can not send rollout conflict report.", ioex);
		}
	}

	private String getNotificationTitle(final String blueprintPath, final ResourceResolver resourceResolver) {
		final PageManager pageManager = CommonUtil.adapt(resourceResolver, PageManager.class);
		final Page blueprintPage = pageManager.getPage(blueprintPath);

		return String.format("Rollout conflict for blueprint page %s", blueprintPage != null ? blueprintPage.getTitle() : blueprintPath);
	}

	private String getNotificationBody(final RolloutConflictSearchResult result, final String docLink, final ResourceResolver resourceResolver) {
		final Externalizer externalizer = CommonUtil.adapt(resourceResolver, Externalizer.class);

		final VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		velocityEngine.init();

		final Template template = velocityEngine.getTemplate(NOTIFICATION_BODY_TEMPLATE_PATH);

		final VelocityContext context = new VelocityContext();
		context.put("RolloutConflictUtils", RolloutConflictUtils.class);
		context.put("resourceResolver", resourceResolver);
		context.put("externalizer", externalizer);
		context.put("result", result);
		context.put("docLink", docLink);

		final StringWriter writer = new StringWriter();
		template.merge(context, writer);

		return writer.toString();
	}

	private List<String> getUserEmails(final String userId, final ResourceResolver resourceResolver) throws RepositoryException {
		final List<String> emails = new ArrayList<>();

		final UserManager userManager = CommonUtil.adapt(resourceResolver, UserManager.class);

		this.getEmailsFromAuthorizable(userManager.getAuthorizable(userId), emails);

		return emails;
	}

	private List<String> getReportEmails(final String reportRecipientGroup, final ResourceResolver resourceResolver) throws RepositoryException {
		final List<String> emails = new ArrayList<>();

		final UserManager userManager = CommonUtil.adapt(resourceResolver, UserManager.class);

		final Group group = (Group) userManager.getAuthorizable(reportRecipientGroup);

		if (group != null) {
			final Iterator<Authorizable> iterator = group.getMembers();

			while (iterator.hasNext()) {
				this.getEmailsFromAuthorizable(iterator.next(), emails);
			}
		}
		return emails;
	}

	private void getEmailsFromAuthorizable(final Authorizable authorizable, final List<String> emails) throws RepositoryException {
		if ((authorizable instanceof User) && authorizable.hasProperty(CommonConstants.EMAIL)) {
			final Value[] userEmails = authorizable.getProperty(CommonConstants.EMAIL);

			for (final Value email : userEmails) {
				emails.add(email.getString());
			}
		}
	}

	private InputStream getAttachmentStream(final RolloutConflictReport exportReport, final ResourceResolver resourceResolver) throws RepositoryException {
		final Resource attachementResource = resourceResolver.getResource(exportReport.getReportPath());

		if (attachementResource != null) {
			final Node attachementNode = attachementResource.adaptTo(Node.class);

			if (attachementNode != null) {
				return attachementNode.getNode(JcrConstants.JCR_CONTENT).getProperty(CommonConstants.JCR_DATA).getBinary().getStream();
			}
		}
		return null;
	}

	/**
	 * Sets the send notification.
	 *
	 * @param sendNotification
	 *            the sendNotification to set
	 */
	public void setSendNotification(final boolean sendNotification) {
		this.sendNotification = sendNotification;
	}

	/**
	 * Sets the send email notification.
	 *
	 * @param sendEmailNotification
	 *            the sendEmailNotification to set
	 */
	public void setSendEmailNotification(final boolean sendEmailNotification) {
		this.sendEmailNotification = sendEmailNotification;
	}

	/**
	 * Sets the email notification template path.
	 *
	 * @param emailNotificationTemplatePath
	 *            the emailNotificationTemplatePath to set
	 */
	public void setEmailNotificationTemplatePath(final String emailNotificationTemplatePath) {
		this.emailNotificationTemplatePath = emailNotificationTemplatePath;
	}

	/**
	 * Sets the email report template path.
	 *
	 * @param emailReportTemplatePath
	 *            the emailReportTemplatePath to set
	 */
	public void setEmailReportTemplatePath(final String emailReportTemplatePath) {
		this.emailReportTemplatePath = emailReportTemplatePath;
	}

	/**
	 * Sets the report recipients.
	 *
	 * @param reportRecipients
	 *            the reportRecipients to set
	 */
	public void setReportRecipients(final List<String> reportRecipients) {
		this.reportRecipients = reportRecipients;
	}

	/**
	 * Sets the report recipient group.
	 *
	 * @param reportRecipientGroup
	 *            the reportRecipientGroup to set
	 */
	public void setReportRecipientGroup(final String reportRecipientGroup) {
		this.reportRecipientGroup = reportRecipientGroup;
	}

	/**
	 * Sets the report root path.
	 *
	 * @param reportRootPath
	 *            the reportRootPath to set
	 */
	public void setReportRootPath(final String reportRootPath) {
		this.reportRootPath = reportRootPath;
	}

	/**
	 * Sets the detailed report.
	 *
	 * @param detailedReport
	 *            the detailedReport to set
	 */
	public void setDetailedReport(final boolean detailedReport) {
		this.detailedReport = detailedReport;
	}

	/**
	 * Sets the dock link.
	 *
	 * @param dockLink
	 *            the dockLink to set
	 */
	public void setDockLink(final String dockLink) {
		this.docLink = dockLink;
	}

}
