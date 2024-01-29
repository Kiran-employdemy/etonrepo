package com.eaton.platform.core.services.search.find.replace.tags.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.jcr.RepositoryException;

import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.bean.search.find.replace.request.ContentType;
import com.eaton.platform.core.bean.search.find.replace.request.TagsModificationRequestBean;
import com.eaton.platform.core.bean.search.find.replace.request.TagsSearchRequestBean;
import com.eaton.platform.core.bean.search.find.replace.response.ResultItem;
import com.eaton.platform.core.bean.search.find.replace.response.TagsModificationResultBean;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.FindAndReplaceConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.search.find.replace.FindReplaceConfigService;
import com.eaton.platform.core.services.search.find.replace.FindReplaceConfigServiceConfig;
import com.eaton.platform.core.services.search.find.replace.RepositoryFunction;
import com.eaton.platform.core.services.search.find.replace.tags.FindReplaceTagsDataTypeService;

/**
 * The Class FindReplaceTagsDataAssetServiceImpl.
 *
 * Service that executes "find and replace" functionality for tags for assets.
 *
 * @author Jaroslav Rassadin
 */
@Component(
		service = FindReplaceTagsDataTypeService.class,
		property = {
				AEMConstants.SERVICE_VENDOR_EATON,
				AEMConstants.SERVICE_DESCRIPTION + "Find Replace Tags Data Asset Service",
				AEMConstants.PROCESS_LABEL + "FindReplaceTagsDataAssetServiceImpl" })
public class FindReplaceTagsDataAssetServiceImpl extends AbstractFindReplaceTagsDataTypeService implements FindReplaceTagsDataTypeService {

	private static final Logger LOGGER = LoggerFactory.getLogger(FindReplaceTagsDataAssetServiceImpl.class);

	private static final ContentType CONTENT_TYPE = ContentType.ASSET;

	@Reference
	private AdminService adminService;

	@Reference
	private FindReplaceConfigService configService;

	private final RepositoryFunction<ModificationInfoHolder, Boolean> addProcessor = i -> this.add(i);

	private final RepositoryFunction<ModificationInfoHolder, Boolean> deleteProcessor = i -> this.delete(i);

	private final RepositoryFunction<ModificationInfoHolder, Boolean> replaceProcessor = i -> this.replace(i);

	/**
	 * Activate the service.
	 *
	 * @param config
	 *            the config
	 */
	@Activate
	@Modified
	protected void activate(final FindReplaceConfigServiceConfig config) {
		this.setTagPropertiesMap(this.configService.getTagPropertiesMap(this.getContentType()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TagsModificationResultBean add(final TagsModificationRequestBean modificationRequest) {
		return this.processResources(modificationRequest, this.addProcessor, "An error has occurred while adding tags to assets.");
	}

	private boolean add(final ModificationInfoHolder infoHolder) throws RepositoryException {
		boolean result = false;

		for (final Map.Entry<String, List<String>> entry : infoHolder.getModificationRequest().getTagsForModification().entrySet()) {
			final String propertyName = this.mapProperty(entry.getKey());
			final String ownPropertyName = this.stripPathFromProperty(propertyName);

			final Resource resourceToModify = infoHolder.getResource();
			final ValueMap resourceToModifyValueMap = resourceToModify.getValueMap();

			final Set<String> existingTags = this.getExistingTags(resourceToModifyValueMap, propertyName);

			final Set<String> updatedTags = new TreeSet<>(existingTags);
			updatedTags.addAll(entry.getValue());

			if (existingTags.equals(updatedTags)) {
				LOGGER.debug("Property already contains all provided tags. Skip.");

			} else {
				LOGGER.debug("Tags to be added: {}.", entry.getValue());
				result |= true;

				this.saveProperty(resourceToModify, new PropertyInfoHolder(this.isMultipleProperty(entry.getKey()), ownPropertyName, updatedTags,
						infoHolder.getValueFactory()));
			}
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TagsModificationResultBean delete(final TagsModificationRequestBean modificationRequest) {
		return this.processResources(modificationRequest, this.deleteProcessor, "An error has occurred while deleting tags in assets.");
	}

	private boolean delete(final ModificationInfoHolder infoHolder) throws RepositoryException {
		boolean result = false;

		for (final Map.Entry<String, List<String>> entry : infoHolder.getModificationRequest().getTagsForModification().entrySet()) {
			final String propertyName = this.mapProperty(entry.getKey());
			final String ownPropertyName = this.stripPathFromProperty(propertyName);

			final Resource resourceToModify = infoHolder.getResource();
			final ValueMap resourceToModifyValueMap = resourceToModify.getValueMap();

			final Set<String> existingTags = this.getExistingTags(resourceToModifyValueMap, propertyName);

			final Set<String> updatedTags = new TreeSet<>(existingTags);
			updatedTags.removeAll(entry.getValue());

			if (existingTags.equals(updatedTags)) {
				LOGGER.debug("Property does not contain provided tags. Skip.");

			} else {
				LOGGER.debug("Tags to be deleted: {}.", entry.getValue());
				result |= true;

				this.saveProperty(resourceToModify, new PropertyInfoHolder(this.isMultipleProperty(entry.getKey()), ownPropertyName, updatedTags,
						infoHolder.getValueFactory()));
			}
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TagsModificationResultBean replace(final TagsModificationRequestBean modificationRequest) {
		return this.processResources(modificationRequest, this.replaceProcessor, "An error has occurred while replacing tags in assets.");
	}

	private boolean replace(final ModificationInfoHolder infoHolder) throws RepositoryException {
		boolean result = false;

		for (final Map.Entry<String, List<String>> entry : infoHolder.getModificationRequest().getTagsForModification().entrySet()) {
			final String propertyName = this.mapProperty(entry.getKey());
			final String ownPropertyName = this.stripPathFromProperty(propertyName);

			final Resource resourceToModify = infoHolder.getResource();
			final ValueMap resourceToModifyValueMap = resourceToModify.getValueMap();

			final Set<String> existingTags = this.getExistingTags(resourceToModifyValueMap, propertyName);

			final Set<String> updatedTags = new TreeSet<>(existingTags);
			updatedTags.removeAll(infoHolder.getModificationRequest().getTagsToFind().get(entry.getKey()));
			updatedTags.addAll(entry.getValue());

			if (existingTags.equals(updatedTags)) {
				LOGGER.debug("Property already contains all provided tags. Skip.");

			} else {
				LOGGER.debug("Updated list of tags: {}.", updatedTags);
				result |= true;

				this.saveProperty(resourceToModify, new PropertyInfoHolder(this.isMultipleProperty(entry.getKey()), ownPropertyName, updatedTags,
						infoHolder.getValueFactory()));
			}
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canProcess(final ContentType value) {
		return CONTENT_TYPE.equals(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ContentType getContentType() {
		return CONTENT_TYPE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AdminService getAdminService() {
		return this.adminService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ResultItem buildSearchResultItem(final Resource resource, final PageManager pageManager) {
		final Resource metadata = resource.getChild(JcrConstants.JCR_CONTENT + "/" + DamConstants.METADATA_FOLDER);
		final String title = metadata.getValueMap().get(DamConstants.DC_TITLE, String.class);

		return ResultItem.builder().withContentType(ContentType.ASSET).withTopContainerPath(resource.getPath()).withTopContainerTitle(title)
				.withContainerPath(resource.getPath()).withContainerTitle(title).withPath(metadata.getPath()).withTitle(title).build();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ResultItem buildModificationResultItem(final Resource container, final Resource resource, final PageManager pageManager) {
		final String title = resource.getValueMap().get(DamConstants.DC_TITLE, String.class);

		return ResultItem.builder().withContentType(ContentType.ASSET).withTopContainerPath(container.getPath()).withTopContainerTitle(title)
				.withContainerPath(container.getPath()).withContainerTitle(title).withPath(resource.getPath()).withTitle(title).build();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getSearchQuery(final TagsSearchRequestBean searchRequest) {
		final StringBuilder tagsQuery = new StringBuilder();

		final Set<Map.Entry<String, List<String>>> entrySet = searchRequest.getTagsToFind().entrySet();
		int entrySetIndex = 0;

		for (final Map.Entry<String, List<String>> entry : entrySet) {
			final List<String> tagValueList = entry.getValue();
			int tagValueListIndex = 0;

			final String propertyName = this.mapProperty(entry.getKey());

			for (final String tagValue : tagValueList) {
				tagsQuery.append("(");
				tagsQuery.append(StrSubstitutor.replace(FindAndReplaceConstants.ASSET_QUERY_TAG, Map.of(FindAndReplaceConstants.PROP_NAME_PARAM, propertyName,
						FindAndReplaceConstants.SEARCH_STRING, tagValue)));
				tagsQuery.append(")");

				if (tagValueListIndex < (tagValueList.size() - 1)) {
					tagsQuery.append(FindAndReplaceConstants.AND_CONDITION);
				}
				tagValueListIndex++;
			}
			if (entrySetIndex < (entrySet.size() - 1)) {
				tagsQuery.append(FindAndReplaceConstants.AND_CONDITION);
			}
			entrySetIndex++;
		}
		return StrSubstitutor.replace(FindAndReplaceConstants.QUERY, Map.of("nodeType", DamConstants.NT_DAM_ASSET, "pathToSearch", searchRequest.getPath(),
				"propertiesQuery", tagsQuery.toString()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getSubpathPrefix() {
		return JcrConstants.JCR_CONTENT + "/" + DamConstants.METADATA_FOLDER;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Resource getContainingResource(final Resource resource, final ResourceResolver resourceResolver, final PageManager pageManager) {
		Resource currentResource = resource;

		do {
			if (DamConstants.NT_DAM_ASSET.equals(currentResource.getValueMap().get(JcrConstants.JCR_PRIMARYTYPE, String.class))) {
				return currentResource;
			}
			currentResource = currentResource.getParent();

		} while (currentResource != null);

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isSupportedPath(final String path) {
		return (path != null) && path.endsWith(DamConstants.METADATA_FOLDER);
	}

}
