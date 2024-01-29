package com.eaton.platform.core.services.msm.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jcr.RepositoryException;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.eaton.platform.core.services.msm.RolloutConflictSearchSearvice;
import com.eaton.platform.core.util.CommonUtil;

/**
 * The Class RolloutConflictSearchSearviceImpl.
 *
 * @author Jaroslav Rassadin
 */
@Component(
		service = RolloutConflictSearchSearvice.class,
		configurationPolicy = ConfigurationPolicy.REQUIRE,
		property = {
				AEMConstants.SERVICE_VENDOR_EATON,
				AEMConstants.SERVICE_DESCRIPTION + "Rollout Conflict Search Service",
				AEMConstants.PROCESS_LABEL + "RolloutConflictSearchSearviceImpl" })
public class RolloutConflictSearchSearviceImpl implements RolloutConflictSearchSearvice {

	private static final Logger LOGGER = LoggerFactory.getLogger(RolloutConflictSearchSearviceImpl.class);

	@Reference
	private AdminService adminService;

	@Reference
	private LiveRelationshipManager liveRelationshipManager;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RolloutConflictSearchResult findConflictsForRollout(final String blueprintPath, final List<String> liveCopyPaths, final boolean deep) {
		final RolloutConflictSearchResult result = new RolloutConflictSearchResult(deep, blueprintPath);

		try (ResourceResolver resourceResolver = this.adminService.getReadService()) {
			final PageManager pageManager = CommonUtil.adapt(resourceResolver, PageManager.class);

			for (final String liveCopyPath : liveCopyPaths) {
				final Page liveCopyPage = pageManager.getPage(liveCopyPath);

				if (liveCopyPage == null) {
					LOGGER.warn("Can not get page for path {}.", liveCopyPath);
					continue;
				}
				this.processPage(liveCopyPage, deep, result);
			}

		} catch (final Exception ex) {
			LOGGER.error("An error has occurred while searching for rollout conflicts.", ex);
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RolloutConflictSearchResult findConflictsInContent(final String searchRoot) {
		final RolloutConflictSearchResult result = new RolloutConflictSearchResult(true, searchRoot);

		try (ResourceResolver resourceResolver = this.adminService.getReadService()) {
			final PageManager pageManager = CommonUtil.adapt(resourceResolver, PageManager.class);
			final Page rootPage = pageManager.getPage(searchRoot);

			if (rootPage == null) {
				throw new RolloutConflictException(String.format("Can not get page for root search path %s.", searchRoot));
			}
			this.processPage(rootPage, true, result);

		} catch (final Exception ex) {
			LOGGER.error("An error has occurred while searching for rollout conflicts.", ex);
		}
		return result;
	}

	protected void processPage(final Page page, final boolean deep, final RolloutConflictSearchResult result) throws RepositoryException, WCMException {
		RolloutConflictSearchResultItem searchResultItem = null;

		// check page itself
		final Page msmMovedPage = page.getPageManager().getPage(page.getPath() + CommonConstants.MSM_MOVED_POSTFIX);

		if (msmMovedPage != null) {
			searchResultItem = this.createSearchResultItem(page);
			searchResultItem.setLiveCopyMsmMovedPath(msmMovedPage.getPath());
		}
		// check components
		final List<String> msmMovedComponentPaths = new ArrayList<>();
		this.processResource(page.getContentResource(), msmMovedComponentPaths);

		if (CollectionUtils.isNotEmpty(msmMovedComponentPaths)) {

			if (searchResultItem == null) {
				searchResultItem = this.createSearchResultItem(page);
			}
			searchResultItem.setMsmMovedComponentPaths(msmMovedComponentPaths);
		}
		if ((searchResultItem != null) && StringUtils.isNotEmpty(searchResultItem.getBlueprintPath())) {
			result.addItem(searchResultItem.getLiveCopyPath(), searchResultItem);
		}
		// process child pages
		if (deep) {
			final Iterator<Page> childIterator = page.listChildren();

			while (childIterator.hasNext()) {
				this.processPage(childIterator.next(), deep, result);
			}
		}
	}

	private RolloutConflictSearchResultItem createSearchResultItem(final Page liveCopyPage) throws WCMException {
		final RolloutConflictSearchResultItem searchResultItem = new RolloutConflictSearchResultItem();

		searchResultItem.setLiveCopyPath(liveCopyPage.getPath());

		final LiveRelationship liveRelationship = this.liveRelationshipManager.getLiveRelationship(liveCopyPage.getContentResource(), false);

		if (liveRelationship != null) {
			final String bluePrintPath = liveRelationship.getSourcePath();
			searchResultItem.setBlueprintPath(bluePrintPath.substring(0, bluePrintPath.indexOf(JcrConstants.JCR_CONTENT) - 1));
		}
		return searchResultItem;
	}

	private void processResource(final Resource resource, final List<String> msmMovedComponentPaths) throws RepositoryException {
		if (resource == null) {
			return;
		}
		// resource name should contain _msm_moved postfix and does not have cq:LiveRelationship mixin
		// also resource with name without _msm_moved postfix and cq:LiveRelationship mixin should be present at same level
		if (this.checkResource(resource) && this.checkLiveSyncResourceExists(resource)) {
			msmMovedComponentPaths.add(resource.getPath());
		}
		// process child resources
		final Iterator<Resource> childIterator = resource.listChildren();

		while (childIterator.hasNext()) {
			this.processResource(childIterator.next(), msmMovedComponentPaths);
		}
	}

	private boolean checkResource(final Resource resource) throws RepositoryException {
		return resource.getName().matches(".+_msm_moved(_\\d)?") && !RolloutConflictUtils.hasLiveRelationship(resource);
	}

	private boolean checkLiveSyncResourceExists(final Resource msmMovedResource) throws RepositoryException {
		final Resource parent = msmMovedResource.getParent();

		if (parent == null) {
			throw new RolloutConflictException(String.format("Can not get parent for resource %s.", msmMovedResource.getPath()));
		}
		final String name = msmMovedResource.getName().substring(0, msmMovedResource.getName().indexOf(CommonConstants.MSM_MOVED_POSTFIX));
		final Iterator<Resource> children = parent.listChildren();

		while (children.hasNext()) {
			final Resource child = children.next();

			if (child.getName().equals(name) && RolloutConflictUtils.hasLiveRelationship(child)) {
				return true;
			}
		}
		return false;
	}

}
