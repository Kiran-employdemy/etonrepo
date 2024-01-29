package com.eaton.platform.core.services.msm.impl;

import java.util.Iterator;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import com.day.cq.wcm.msm.api.MSMNameConstants;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.exception.EatonSystemException;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.msm.RolloutConflictException;
import com.eaton.platform.core.services.msm.RolloutConflictInheritanceService;
import com.eaton.platform.core.util.CommonUtil;

/**
 * The Class RolloutConflictInheritanceServiceImpl.
 *
 * Contains logic for inheritance enabling functionality for components and pages.
 *
 * @author Jaroslav Rassadin
 */
@Component(
		service = RolloutConflictInheritanceService.class,
		configurationPolicy = ConfigurationPolicy.REQUIRE,
		property = {
				AEMConstants.SERVICE_VENDOR_EATON,
				AEMConstants.SERVICE_DESCRIPTION + "Rollout Conflict Inheritance Service",
				AEMConstants.PROCESS_LABEL + "RolloutConflictInheritanceServiceImpl" })
public class RolloutConflictInheritanceServiceImpl implements RolloutConflictInheritanceService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RolloutConflictInheritanceServiceImpl.class);

	private static final String MSM_MOVED_REGEX = "_msm_moved(_\\d)?";

	private static final String MSM_MOVED_END_REGEX = MSM_MOVED_REGEX + "$";

	private static final String MSM_MOVED_FULL_REGEX = ".+" + MSM_MOVED_REGEX;

	@Reference
	private AdminService adminService;

	@Reference
	private LiveRelationshipManager liveRelationshipManager;

	@Reference
	private Replicator replicator;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canEnableInheritanceForPage(final String path) {
		LOGGER.debug("Checking inheritance enabling possibility for page {}.", path);

		try (ResourceResolver resourceResolver = this.adminService.getReadService()) {
			final PageManager pageManager = CommonUtil.adapt(resourceResolver, PageManager.class);
			final Page page = pageManager.getPage(path);

			final ConflictResolutionStatus status = this.canEnableInheritanceForPageInternal(page, resourceResolver);
			final boolean result = ConflictResolutionStatus.POSSIBLE.equals(status);

			LOGGER.debug("Inheritance enabling possibile: {}, reason: {}.", result, status.getDescription());

			return result;

		} catch (final Exception ex) {
			LOGGER.error(String.format("An error has occurred while checking inheritance enabling possibility for page %s.", path), ex);
		}
		return false;
	}

	private ConflictResolutionStatus canEnableInheritanceForPageInternal(final Page page, final ResourceResolver resourceResolver)
			throws RepositoryException, WCMException {
		if ((page == null) || (page.getContentResource() == null) || !page.getName().matches(MSM_MOVED_FULL_REGEX)) {
			return ConflictResolutionStatus.NOT_MSM_MOVED_RESOURCE;
		}
		if (RolloutConflictUtils.hasLiveRelationship(page.getContentResource())) {
			return ConflictResolutionStatus.HAS_LIVE_RELATIONSHIP;
		}
		if (page.listChildren().hasNext()) {
			return ConflictResolutionStatus.HAS_CHILDREN;
		}
		final LiveRelationship liveRelationship = this.liveRelationshipManager.getLiveRelationship(page.getContentResource(), false);

		if (liveRelationship != null) {
			final String sourcePath = liveRelationship.getSourcePath().replaceFirst(MSM_MOVED_REGEX + "/jcr:content$", "") + "/" + JcrConstants.JCR_CONTENT;
			return resourceResolver.getResource(sourcePath) != null ? ConflictResolutionStatus.POSSIBLE : ConflictResolutionStatus.NO_BLUEPRINT_RESOURCE;
		}
		return ConflictResolutionStatus.NOT_POSSIBLE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canEnableInheritanceForResource(final String path) {
		LOGGER.debug("Checking inheritance enabling possibility for resource {}.", path);

		try (ResourceResolver resourceResolver = this.adminService.getReadService()) {
			final Resource resource = resourceResolver.getResource(path);

			final ConflictResolutionStatus status = this.canEnableInheritanceForResourceInternal(resource, resourceResolver);
			final boolean result = ConflictResolutionStatus.POSSIBLE.equals(status);

			LOGGER.debug("Inheritance enabling possibile: {}, reason: {}.", result, status.getDescription());

			return result;

		} catch (final Exception ex) {
			LOGGER.error(String.format("An error has occurred while checking inheritance possibility for resource %s.", path), ex);
		}
		return false;
	}

	private ConflictResolutionStatus canEnableInheritanceForResourceInternal(final Resource resource, final ResourceResolver resourceResolver)
			throws WCMException, RepositoryException {
		if ((resource == null) || !resource.getName().matches(MSM_MOVED_FULL_REGEX)) {
			return ConflictResolutionStatus.NOT_MSM_MOVED_RESOURCE;
		}
		if (RolloutConflictUtils.hasLiveRelationship(resource)) {
			return ConflictResolutionStatus.HAS_LIVE_RELATIONSHIP;
		}
		final LiveRelationship liveRelationship = this.liveRelationshipManager.getLiveRelationship(resource, false);

		if (liveRelationship != null) {
			final String sourcePath = liveRelationship.getSourcePath().replaceFirst(MSM_MOVED_END_REGEX, "");
			return resourceResolver.getResource(sourcePath) != null ? ConflictResolutionStatus.POSSIBLE : ConflictResolutionStatus.NO_BLUEPRINT_RESOURCE;
		}
		return ConflictResolutionStatus.NOT_POSSIBLE;
	}

	private ConflictResolutionStatus canEnableInheritanceForNonConflictingResourceInternal(final Resource resource, final ResourceResolver resourceResolver)
			throws WCMException, RepositoryException {
		if (RolloutConflictUtils.hasLiveRelationship(resource)) {
			return ConflictResolutionStatus.HAS_LIVE_RELATIONSHIP;
		}
		final LiveRelationship liveRelationship = this.liveRelationshipManager.getLiveRelationship(resource, false);

		if (liveRelationship != null) {
			final String sourcePath = liveRelationship.getSourcePath().replaceFirst(MSM_MOVED_REGEX + "/jcr:content", "/" + JcrConstants.JCR_CONTENT);
			return resourceResolver.getResource(sourcePath) != null ? ConflictResolutionStatus.POSSIBLE : ConflictResolutionStatus.NO_BLUEPRINT_RESOURCE;
		}
		return ConflictResolutionStatus.NOT_POSSIBLE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConflictResolutionResult enableInheritanceForPage(final String path, final boolean replicate) {
		LOGGER.debug("Going to enable inheritance for page {}.", path);

		try (ResourceResolver resourceResolver = this.adminService.getWriteService()) {
			final PageManager pageManager = CommonUtil.adapt(resourceResolver, PageManager.class);
			final Page page = pageManager.getPage(path);

			LOGGER.debug("Checking inheritance enabling possibility for page {}.", path);
			final ConflictResolutionStatus status = this.canEnableInheritanceForPageInternal(page, resourceResolver);

			if (!ConflictResolutionStatus.POSSIBLE.equals(status)) {
				LOGGER.debug("Inheritance enabling for page not possibile. Reason: {}.", status.getDescription());
				return new ConflictResolutionResult(path, null, status);
			}
			final Session session = CommonUtil.adapt(resourceResolver, Session.class);

			final Resource pageContentResource = page.getContentResource();

			final String name = page.getName().replaceFirst(MSM_MOVED_END_REGEX, "");
			final Page parentPage = this.getParentPage(page);

			final String newPath = parentPage.getPath() + "/" + name;
			final String newContentResourcePath = newPath + "/" + JcrConstants.JCR_CONTENT;

			// if page with target name already exists, only jcr:content node and it's subnodes will be copied but not the page node
			// if page with target name does not exist, _msm_moved page will be renamed
			if (parentPage.hasChild(name)) {
				this.replacePage(page, pageContentResource, newPath, newContentResourcePath, pageManager, session);

			} else {
				this.renamePage(page, parentPage, name, newPath, session);
			}
			// add live relationship to components
			this.addLiveRelationship(resourceResolver.getResource(newContentResourcePath), resourceResolver, true);

			session.save();

			// replicate changes to publish
			if (replicate) {
				this.replicator.replicate(session, ReplicationActionType.DELETE, path);
				LOGGER.debug("Path {} deactivated.", path);

				this.replicator.replicate(session, ReplicationActionType.ACTIVATE, newPath);
				LOGGER.debug("Path {} activated.", newPath);
			}
			final ConflictResolutionResult result = new ConflictResolutionResult(path, newPath, ConflictResolutionStatus.FIXED);

			LOGGER.debug("Conflict fixed: {}, reason: {}.", ConflictResolutionStatus.FIXED.equals(result.getStatus()), result.getMsg());

			return result;

		} catch (final Exception ex) {
			LOGGER.error(String.format("An error has occurred while enabling inheritance for page %s.", path), ex);

			return new ConflictResolutionResult(path, path, ConflictResolutionStatus.ERROR);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConflictResolutionResult enableInheritanceForResource(final String path, final boolean replicate) {
		LOGGER.debug("Going to enable inheritance for resource {}.", path);

		try (ResourceResolver resourceResolver = this.adminService.getWriteService()) {
			final Resource resource = resourceResolver.getResource(path);

			if (resource == null) {
				return new ConflictResolutionResult(path, null, ConflictResolutionStatus.NOT_MSM_MOVED_RESOURCE);
			}
			LOGGER.debug("Checking inheritance enabling possibility for resource {}.", path);
			final ConflictResolutionStatus status = this.canEnableInheritanceForResourceInternal(resource, resourceResolver);

			if (!ConflictResolutionStatus.POSSIBLE.equals(status)) {
				LOGGER.debug("Inheritance enabling for resource not possibile. Reason: {}.", status.getDescription());
				return new ConflictResolutionResult(path, null, status);
			}
			final Session session = CommonUtil.adapt(resourceResolver, Session.class);

			final Node node = CommonUtil.adapt(resource, Node.class);

			// add live relationship mixin
			node.addMixin(MSMNameConstants.NT_LIVE_RELATIONSHIP);

			final String name = resource.getName().replaceFirst(MSM_MOVED_END_REGEX, "");

			final Resource parentResource = this.getParentResource(resource);

			final String newPath = parentResource.getPath() + "/" + name;

			this.replaceResource(node, resource, parentResource, name, newPath, resourceResolver, session);

			session.save();

			// replicate changes to publish
			if (replicate) {
				this.replicator.replicate(session, ReplicationActionType.DELETE, path);
				LOGGER.debug("Path {} deactivated.", path);

				this.replicator.replicate(session, ReplicationActionType.ACTIVATE, newPath);
				LOGGER.debug("Path {} activated.", newPath);
			}
			final ConflictResolutionResult result = new ConflictResolutionResult(path, newPath, ConflictResolutionStatus.FIXED);

			LOGGER.debug("Conflict fixed: {}, reason: {}.", ConflictResolutionStatus.FIXED.equals(result.getStatus()), result.getMsg());

			return result;

		} catch (final Exception ex) {
			LOGGER.error(String.format("An error has occurred while enabling inheritance for resource %s.", path), ex);

			return new ConflictResolutionResult(path, path, ConflictResolutionStatus.ERROR);
		}
	}

	private Page getNextPage(final Page currentPage, final Page parentPage, final String pageToBeDeleted) {
		final Iterator<Page> pageIterator = parentPage.listChildren();

		while (pageIterator.hasNext()) {
			final Page page = pageIterator.next();

			if (page.getName().equals(currentPage.getName()) && pageIterator.hasNext()) {
				final Page nextPage = pageIterator.next();

				if (!nextPage.getName().equals(pageToBeDeleted)) {
					return nextPage;
				}
			}
		}
		return null;
	}

	private Node getNextNode(final Node currentNode, final Node parentNode, final String resourceToBeDeleted) throws RepositoryException {
		final NodeIterator nodeIterator = parentNode.getNodes();

		while (nodeIterator.hasNext()) {
			final Node node = (Node) nodeIterator.next();

			if (node.getName().equals(currentNode.getName()) && nodeIterator.hasNext()) {
				final Node nextNode = (Node) nodeIterator.next();

				if (!nextNode.getName().equals(resourceToBeDeleted)) {
					return nextNode;
				}
			}
		}
		return null;
	}

	private void addLiveRelationship(final Resource resource, final ResourceResolver resourceResolver, final boolean deep)
			throws RepositoryException, WCMException {
		if (!ConflictResolutionStatus.POSSIBLE.equals(this.canEnableInheritanceForNonConflictingResourceInternal(resource, resourceResolver))) {
			return;
		}
		// add live relationship mixin
		final Node node = CommonUtil.adapt(resource, Node.class);
		node.addMixin(MSMNameConstants.NT_LIVE_RELATIONSHIP);

		if (deep) {

			for (final Resource child : resource.getChildren()) {
				this.addLiveRelationship(child, resourceResolver, deep);
			}
		}
	}

	private void replacePage(final Page page, final Resource pageContentResource, final String newPath, final String newContentResourcePath,
			final PageManager pageManager, final Session session) throws RepositoryException {
		// replace page content
		this.replacePageContent(pageContentResource, newPath, newContentResourcePath, pageManager, session);

		// delete MSM moved page
		CommonUtil.adapt(page, Node.class).remove();
	}

	private void replacePageContent(final Resource pageContentResource, final String newPath, final String newContentResourcePath,
			final PageManager pageManager, final Session session) throws RepositoryException {
		final Page targetPage = pageManager.getPage(newPath);
		String targetPageContentResourcePath = null;
		String tmpPath = null;

		if (targetPage.getContentResource() != null) {
			targetPageContentResourcePath = targetPage.getContentResource().getPath();
			tmpPath = targetPageContentResourcePath + "_tmp";

			// it is necessary to persist changes before calling Workspace.copy()
			session.move(targetPageContentResourcePath, tmpPath);
			session.save();
		}
		try {
			final Workspace workspace = session.getWorkspace();
			workspace.copy(pageContentResource.getPath(), newContentResourcePath);

		} catch (final Exception ex) {
			// revert changes
			if (targetPageContentResourcePath != null) {
				session.move(tmpPath, targetPageContentResourcePath);
				session.save();
			}
			throw new RolloutConflictException(
					String.format("An error has occurred while copying resource from %s to %s.", pageContentResource.getPath(), newPath), ex);
		}
		// delete temporary resource
		if (tmpPath != null) {
			final Node tmpNode = session.getNode(tmpPath);

			if (tmpNode != null) {
				tmpNode.remove();
			}
		}

	}

	private void renamePage(final Page page, final Page parentPage, final String name, final String newPath, final Session session) throws RepositoryException {
		// necessary to put renamed page at right position
		final Page nextPage = this.getNextPage(page, parentPage, name);

		// rename and position MSM moved page
		session.move(page.getPath(), newPath);

		if (nextPage != null) {
			CommonUtil.adapt(parentPage, Node.class).orderBefore(name, nextPage.getName());
		}
	}

	private Page getParentPage(final Page page) {
		final Page parentPage = page.getParent();

		if (parentPage == null) {
			throw new EatonSystemException(StringUtils.EMPTY, String.format("Can not get parent page for page %s.", page.getPath()));
		}
		return parentPage;
	}

	private Resource getParentResource(final Resource resource) {
		final Resource parentResource = resource.getParent();

		if (parentResource == null) {
			throw new EatonSystemException(StringUtils.EMPTY, String.format("Can not get parent for resource %s.", resource.getPath()));
		}
		return parentResource;
	}

	private void replaceResource(final Node node, final Resource resource, final Resource parentResource, final String name, final String newPath,
			final ResourceResolver resourceResolver, final Session session) throws RepositoryException, PersistenceException {
		final Node parentNode = node.getParent();
		// necessary to put renamed node at right position
		final Node nextNode = this.getNextNode(node, parentNode, name);

		final Resource conflictingResource = parentResource.getChild(name);

		// delete conflicting node if present
		if (conflictingResource != null) {
			resourceResolver.delete(conflictingResource);
		}
		// copy and position MSM moved resource
		session.move(resource.getPath(), newPath);

		if (nextNode != null) {
			parentNode.orderBefore(name, nextNode.getName());
		}
	}

}
