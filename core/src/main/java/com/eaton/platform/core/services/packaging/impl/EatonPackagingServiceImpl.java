package com.eaton.platform.core.services.packaging.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.vault.fs.api.PathFilterSet;
import org.apache.jackrabbit.vault.fs.config.DefaultWorkspaceFilter;
import org.apache.jackrabbit.vault.fs.filter.DefaultPathFilter;
import org.apache.jackrabbit.vault.packaging.JcrPackage;
import org.apache.jackrabbit.vault.packaging.JcrPackageDefinition;
import org.apache.jackrabbit.vault.packaging.JcrPackageManager;
import org.apache.jackrabbit.vault.packaging.PackageException;
import org.apache.jackrabbit.vault.packaging.PackageId;
import org.apache.jackrabbit.vault.packaging.PackagingService;
import org.apache.jackrabbit.vault.util.DefaultProgressListener;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.eaton.platform.core.bean.packaging.PackageBean;
import com.eaton.platform.core.bean.packaging.PackagePathFilter;
import com.eaton.platform.core.bean.packaging.PackageRequestBean;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.services.packaging.EatonPackagingService;
import com.eaton.platform.core.services.packaging.PackageCreationException;
import com.eaton.platform.core.util.CommonUtil;

/**
 * The Class EatonPackagingServiceImpl.
 *
 * Service for creation of JCR packages.
 *
 * @author Jaroslav Rassadin
 */
@Component(
		service = EatonPackagingService.class,
		property = {
				AEMConstants.SERVICE_VENDOR_EATON,
				AEMConstants.SERVICE_DESCRIPTION + "Eaton Packaging Service",
				AEMConstants.PROCESS_LABEL + "EatonPackagingServiceImpl" })
public class EatonPackagingServiceImpl implements EatonPackagingService {

	private static final Logger LOGGER = LoggerFactory.getLogger(EatonPackagingServiceImpl.class);

	private static final String SUBSERVICE_NAME = "backupService";

	private static final Map<String, Object> AUTH_INFO = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, SUBSERVICE_NAME);

	@Reference
	private ResourceResolverFactory resourceResolverFactory;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PackageBean create(final PackageRequestBean packageRequest) throws PackageCreationException {
		this.validateRequest(packageRequest);

		LOGGER.info("Going to build packge: group \"{}\", name \"{}\", version \"{}\"", packageRequest.getGroup(), packageRequest.getName(), packageRequest
				.getVersion());
		LOGGER.debug("Received request: {}.", packageRequest);

		JcrPackage jcrPackage = null;

		try (final ResourceResolver resourceResolver = this.getResourceResolver()) {
			final Session session = CommonUtil.adapt(resourceResolver, Session.class);

			final JcrPackageManager packageManager = PackagingService.getPackageManager(session);

			jcrPackage = packageManager.create(packageRequest.getGroup(), packageRequest.getName(), packageRequest.getVersion());
			this.populatePackageDefinition(packageRequest, jcrPackage.getDefinition(), session);
			packageManager.assemble(jcrPackage, new DefaultProgressListener());

			final PackageBean packageBean = this.buildResultBean(jcrPackage);

			LOGGER.info("Package {} ({}) has been built successfully.", packageBean.getId(), packageBean.getPath());

			jcrPackage.close();

			return packageBean;

		} catch (final RepositoryException | PackageException | IOException ex) {
			throw new PackageCreationException("An error has occurred while creating package.", ex);

		} finally {

			if (jcrPackage != null) {
				jcrPackage.close();
			}
		}
	}

	private void validateRequest(final PackageRequestBean packageRequest) {
		if (StringUtils.isEmpty(packageRequest.getGroup())) {
			throw new PackageCreationException("Group name can not be empty.");
		}
		if (StringUtils.isEmpty(packageRequest.getName())) {
			throw new PackageCreationException("Package name can not be empty.");
		}
		if (CollectionUtils.isEmpty(packageRequest.getFilters()) || packageRequest.getFilters().stream().anyMatch(f -> StringUtils.isEmpty(f.getRootPath()))) {
			throw new PackageCreationException(String.format("Incorrect filters definition: %s.", packageRequest.getFilters()));
		}
	}

	private void populatePackageDefinition(final PackageRequestBean packageRequest, final JcrPackageDefinition definition, final Session session)
			throws RepositoryException {
		// set filters
		final DefaultWorkspaceFilter workspaceFilter = new DefaultWorkspaceFilter();

		for (final PackagePathFilter filter : packageRequest.getFilters()) {
			final PathFilterSet pathFilterSet = new PathFilterSet();

			pathFilterSet.setRoot(filter.getRootPath());

			if (CollectionUtils.isNotEmpty(filter.getRules())) {

				for (final Map.Entry<String, Boolean> rule : filter.getRules()) {

					if (Boolean.TRUE.equals(rule.getValue())) {
						pathFilterSet.addInclude(new DefaultPathFilter(rule.getKey()));
					} else {
						pathFilterSet.addExclude(new DefaultPathFilter(rule.getKey()));
					}
				}
			}
			workspaceFilter.add(pathFilterSet);
		}
		definition.setFilter(workspaceFilter, false);

		// set description
		if (StringUtils.isNotEmpty(packageRequest.getDescription())) {
			definition.set(JcrConstants.JCR_DESCRIPTION, packageRequest.getDescription(), false);
		}
		// save changes
		session.save();
	}

	private PackageBean buildResultBean(final JcrPackage jcrPackage) throws RepositoryException, IOException {
		final PackageId packageId = jcrPackage.getPackage().getId();

		return PackageBean.builder()
				.withDescription(this.getPackageDescription(jcrPackage))
				.withGroup(packageId.getGroup())
				.withId(packageId.toString())
				.withName(packageId.getName())
				.withPath(this.getPackagePath(jcrPackage))
				.withVersion(packageId.getVersion() != null ? packageId.getVersion().toString() : null)
				.build();
	}

	private String getPackageDescription(final JcrPackage jcrPackage) throws RepositoryException {
		if (jcrPackage.getDefinition() != null) {
			return jcrPackage.getDefinition().getDescription();
		}
		return null;
	}

	private String getPackagePath(final JcrPackage jcrPackage) throws RepositoryException {
		if (jcrPackage.getNode() != null) {
			return jcrPackage.getNode().getPath();
		}
		return null;
	}

	private ResourceResolver getResourceResolver() {
		try {
			return this.resourceResolverFactory.getServiceResourceResolver(AUTH_INFO);

		} catch (final LoginException ex) {
			throw new PackageCreationException(String.format("Unable to login to repository with subservice name '%s'.", SUBSERVICE_NAME), ex);
		}
	}

}
