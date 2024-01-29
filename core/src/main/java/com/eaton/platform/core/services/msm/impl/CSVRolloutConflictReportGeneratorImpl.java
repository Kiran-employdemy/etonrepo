package com.eaton.platform.core.services.msm.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import javax.jcr.Session;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.Externalizer;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.exception.EatonSystemException;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.file.AbstractReportGenerator;
import com.eaton.platform.core.services.msm.RolloutConflictException;
import com.eaton.platform.core.services.msm.RolloutConflictReportGenerator;
import com.eaton.platform.core.util.CommonUtil;

/**
 * The Class CSVRolloutConflictReportGeneratorImpl.
 *
 * @author Jaroslav Rassadin
 */
@Component(
		service = RolloutConflictReportGenerator.class,
		configurationPolicy = ConfigurationPolicy.REQUIRE,
		property = {
				AEMConstants.SERVICE_VENDOR_EATON,
				AEMConstants.SERVICE_DESCRIPTION + "CSV Rollout Conflict Report Generator",
				AEMConstants.PROCESS_LABEL + "RolloutConflictReportGenerator",
				"type=" + "csv" })
public class CSVRolloutConflictReportGeneratorImpl extends AbstractReportGenerator implements RolloutConflictReportGenerator {

	/**
	 * The Enum Headers.
	 */
	private enum Headers {

		/** The path. */
		PATH("Path"),

		/** The conflicting page. */
		CONFLICTING_PAGE("Conflicting page"),

		/** The conflicting components. */
		CONFLICTING_COMPONENTS("Conflicting components");

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

	private static final Logger LOGGER = LoggerFactory.getLogger(CSVRolloutConflictReportGeneratorImpl.class);

	private static final String YES_VALUE = "yes";

	private static final String NO_VALUE = "no";

	/** The Constant REPORTS_PATH. */
	protected static final String REPORTS_PATH = "/tmp/eaton/msm/conflict/reports";

	/** The Constant REPORTS_NAME_PREFIX. */
	protected static final String REPORTS_NAME_PREFIX = "msm_conflict_report_";

	/** The Constant CSV_MIME_TYPE. */
	protected static final String CSV_MIME_TYPE = "text/csv";

	@Reference
	private AdminService adminService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RolloutConflictReport generateReport(final RolloutConflictSearchResult searchResult, final boolean detailedReport) {
		try (ResourceResolver resourceResolver = this.adminService.getWriteService()) {
			final Session session = CommonUtil.adapt(resourceResolver, Session.class);

			final String name = REPORTS_NAME_PREFIX + ZonedDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + ".csv";

			final String reportPath = this.createReportResource(REPORTS_PATH, name, this.buildReportContent(searchResult, detailedReport, resourceResolver),
					CSV_MIME_TYPE, session);

			final RolloutConflictReport exportReport = new RolloutConflictReport(CSV_MIME_TYPE, name, reportPath);
			LOGGER.debug("Generated report: {}", exportReport);

			return exportReport;

		} catch (final Exception ex) {
			throw new EatonSystemException(StringUtils.EMPTY, "An error has occurred while generating rollout conflict report.", ex);
		}
	}

	/**
	 * Builds the report content.
	 *
	 * @param searchResult
	 *            the search result
	 * @param detailedReport
	 *            the detailed report
	 * @param resourceResolver
	 *            the resource resolver
	 * @return the string
	 */
	protected String buildReportContent(final RolloutConflictSearchResult searchResult, final boolean detailedReport, final ResourceResolver resourceResolver) {
		final Externalizer externalizer = CommonUtil.adapt(resourceResolver, Externalizer.class);

		final StringWriter stringWriter = new StringWriter();
		final CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader(Arrays.stream(Headers.values()).map(Headers::getValue).toArray(String[]::new))
				.withRecordSeparator(System.lineSeparator());

		try (final CSVPrinter printer = new CSVPrinter(stringWriter, csvFormat)) {

			if (detailedReport) {
				this.writeDetailedRecord(searchResult, printer, externalizer, resourceResolver);

			} else {
				this.writeRecord(searchResult, printer, externalizer, resourceResolver);
			}
		} catch (final IOException ioex) {
			throw new RolloutConflictException("Can build report content.", ioex);
		}
		return stringWriter.toString();
	}

	private void writeRecord(final RolloutConflictSearchResult searchResult, final CSVPrinter printer, final Externalizer externalizer,
			final ResourceResolver resourceResolver) {
		searchResult.getItems().forEach((final String liveCopyPath, final RolloutConflictSearchResultItem searchItem) -> {

			try {
				printer.printRecord(
						RolloutConflictUtils.buildLink(searchItem.getLiveCopyPath(), externalizer, resourceResolver),
						StringUtils.isNotEmpty(searchItem.getLiveCopyMsmMovedPath()) ? YES_VALUE : NO_VALUE,
						CollectionUtils.isNotEmpty(searchItem.getMsmMovedComponentPaths()) ? YES_VALUE : NO_VALUE);

			} catch (final IOException ioex) {
				throw new RolloutConflictException(String.format("Can write record: %s.", searchItem), ioex);
			}
		});
	}

	private void writeDetailedRecord(final RolloutConflictSearchResult searchResult, final CSVPrinter printer, final Externalizer externalizer,
			final ResourceResolver resourceResolver) {
		searchResult.getItems().forEach((final String liveCopyPath, final RolloutConflictSearchResultItem searchItem) -> {

			try {
				printer.printRecord(
						RolloutConflictUtils.buildLink(searchItem.getLiveCopyPath(), externalizer, resourceResolver),
						RolloutConflictUtils.buildLink(searchItem.getLiveCopyMsmMovedPath(), externalizer, resourceResolver),
						RolloutConflictUtils.buildComponentPaths(searchItem.getMsmMovedComponentPaths()));

			} catch (final IOException ioex) {
				throw new RolloutConflictException(String.format("Can write record: %s.", searchItem), ioex);
			}
		});
	}
}
