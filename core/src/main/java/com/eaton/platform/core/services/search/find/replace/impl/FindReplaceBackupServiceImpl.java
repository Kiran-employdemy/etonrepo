package com.eaton.platform.core.services.search.find.replace.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

import com.eaton.platform.core.bean.packaging.PackageBean;
import com.eaton.platform.core.bean.packaging.PackagePathFilter;
import com.eaton.platform.core.bean.packaging.PackageRequestBean;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.services.packaging.EatonPackagingService;
import com.eaton.platform.core.services.packaging.PackageCreationException;
import com.eaton.platform.core.services.search.find.replace.FindReplaceBackupService;
import com.eaton.platform.core.services.search.find.replace.FindReplaceBackupServiceConfig;
import com.eaton.platform.core.services.search.find.replace.exception.FindReplaceBackupException;

/**
 * The Class FindReplaceBackupServiceImpl.
 *
 * Creates backups for content modified by "find and replace" functionality.
 *
 * @author Jaroslav Rassadin
 */
@Component(
		service = FindReplaceBackupService.class,
		configurationPolicy = ConfigurationPolicy.REQUIRE,
		property = {
				AEMConstants.SERVICE_VENDOR_EATON,
				AEMConstants.SERVICE_DESCRIPTION + "Find Replace Backup Service",
				AEMConstants.PROCESS_LABEL + "FindReplaceBackupServiceImpl" })
@Designate(
		ocd = FindReplaceBackupServiceConfig.class)
public class FindReplaceBackupServiceImpl implements FindReplaceBackupService {

	private static final String EXCLUDE_ALL_PATTERN = "/.*";

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH-mm-ss");

	@Reference
	private EatonPackagingService packagingService;

	private String group;

	private String namePrefix;

	private int maxNodes;

	private Set<String> pathPatterns;

	/**
	 * Activate the service.
	 *
	 * @param config
	 *            the config
	 */
	@Activate
	@Modified
	protected void activate(final FindReplaceBackupServiceConfig config) {
		this.group = config.group();
		this.namePrefix = config.namePrefix();
		this.maxNodes = config.maxNodes();
		// create java regex from pattern string
		this.pathPatterns = Arrays.stream(config.pathPatterns()).map(p -> p.replace("*", ".*")).collect(Collectors.toSet());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PackageBean createBackup(final String name, final String description, final List<String> paths) {
		this.checkPaths(paths, this.pathPatterns);
		this.checkSize(paths, this.maxNodes);

		try {
			final PackageRequestBean packageRequest = new PackageRequestBean();

			packageRequest.setDescription(description);
			packageRequest.setFilters(this.buildFilters(paths));
			packageRequest.setGroup(this.group);
			packageRequest.setName(this.buildPackageName(this.namePrefix, name));

			return this.packagingService.create(packageRequest);

		} catch (final PackageCreationException pcex) {
			throw new FindReplaceBackupException("An error has occurred while creating backup.", pcex);
		}
	}

	private void checkPaths(final List<String> paths, final Set<String> pathPatterns) throws FindReplaceBackupException {
		if (CollectionUtils.isEmpty(paths)) {
			throw new FindReplaceBackupException("Paths can not be empty.");
		}
		if (CollectionUtils.isNotEmpty(pathPatterns)) {
			paths.stream().forEach((final String path) -> {

				if (pathPatterns.stream().noneMatch(path::matches)) {
					throw new FindReplaceBackupException(String.format("Path %s does not match defined patterns: %s.", path, pathPatterns));
				}
			});
		}
	}

	private void checkSize(final List<String> paths, final int maxNodes) throws FindReplaceBackupException {
		if (paths.size() > maxNodes) {
			throw new FindReplaceBackupException(String.format("Number of nodes to be backed up (%s) exceeds maximum number (%s).", paths.size(),
					this.maxNodes));
		}
	}

	private String buildPackageName(final String namePrefix, final String name) {
		return namePrefix + "_" + name + "_" + LocalDateTime.now().format(DATE_TIME_FORMATTER) + "_" + UUID.randomUUID();
	}

	private Set<PackagePathFilter> buildFilters(final List<String> paths) {
		// only modified nodes should be included into package but not child nodes
		return paths.stream().filter(StringUtils::isNotEmpty).map(p -> new PackagePathFilter(p,
				Set.of(new ImmutablePair<>(p + EXCLUDE_ALL_PATTERN, Boolean.FALSE)))).collect(Collectors.toSet());
	}

}
