package com.eaton.platform.core.services.search.find.replace.tags.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFactory;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.value.ValueFactoryImpl;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.bean.search.find.replace.FindReplacePropertyBean;
import com.eaton.platform.core.bean.search.find.replace.request.ContentType;
import com.eaton.platform.core.bean.search.find.replace.request.Mode;
import com.eaton.platform.core.bean.search.find.replace.request.TagsModificationRequestBean;
import com.eaton.platform.core.bean.search.find.replace.request.TagsSearchRequestBean;
import com.eaton.platform.core.bean.search.find.replace.response.ResultItem;
import com.eaton.platform.core.bean.search.find.replace.response.TagsModificationResultBean;
import com.eaton.platform.core.bean.search.find.replace.response.TagsSearchResultBean;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.search.find.replace.RepositoryFunction;
import com.eaton.platform.core.services.search.find.replace.exception.FindReplaceModificationException;
import com.eaton.platform.core.services.search.find.replace.tags.FindReplaceTagsDataTypeService;
import com.eaton.platform.core.util.CommonUtil;

/**
 * The Class AbstractFindReplaceTagsDataTypeService.
 *
 * @author Jaroslav Rassadin
 */
abstract class AbstractFindReplaceTagsDataTypeService implements FindReplaceTagsDataTypeService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractFindReplaceTagsDataTypeService.class);

	private Map<String, FindReplacePropertyBean> tagPropertiesMap;

	/**
	 * Gets the admin service.
	 *
	 * @return the admin service
	 */
	protected abstract AdminService getAdminService();

	/**
	 * Builds the search result item.
	 *
	 * @param resource
	 *            the resource
	 * @param pageManager
	 *            the page manager
	 * @return the result item
	 */
	protected abstract ResultItem buildSearchResultItem(final Resource resource, PageManager pageManager);

	/**
	 * Builds the modification result item.
	 *
	 * @param container
	 *            the container
	 * @param resource
	 *            the resource
	 * @param pageManager
	 *            the page manager
	 * @return the result item
	 */
	protected abstract ResultItem buildModificationResultItem(final Resource container, final Resource resource, PageManager pageManager);

	/**
	 * Gets the search query.
	 *
	 * @param searchRequest
	 *            the search request
	 * @return the search query
	 */
	protected abstract String getSearchQuery(final TagsSearchRequestBean searchRequest);

	/**
	 * Gets the subpath prefix. E.g. jcr:content for page.
	 *
	 * @return the subpath prefix
	 */
	protected abstract String getSubpathPrefix();

	/**
	 * Gets the containing resource.
	 *
	 * @param resource
	 *            the resource
	 * @param resourceResolver
	 *            the resource resolver
	 * @param pageManager
	 *            the page manager
	 * @return the containing resource
	 */
	protected abstract Resource getContainingResource(final Resource resource, final ResourceResolver resourceResolver, PageManager pageManager);

	/**
	 * Checks if is supported path.
	 *
	 * @param path
	 *            the path
	 * @return true, if is supported path
	 */
	protected abstract boolean isSupportedPath(String path);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TagsSearchResultBean find(final TagsSearchRequestBean searchRequest) {
		final TagsSearchResultBean result = new TagsSearchResultBean();

		try (ResourceResolver resourceResolver = this.getAdminService().getReadService()) {
			final PageManager pageManager = CommonUtil.adapt(resourceResolver, PageManager.class);

			final List<ResultItem> resultItems = new ArrayList<>();
			result.setResults(resultItems);

			if (this.canFind(this.getTagPropertiesMap(), searchRequest)) {
				final String query = this.getSearchQuery(searchRequest);
				LOGGER.debug("Generated query: {}", query);

				final Iterator<Resource> iterator = resourceResolver.findResources(query, "JCR-SQL2");
				iterator.forEachRemaining(r -> resultItems.add(this.buildSearchResultItem(r, pageManager)));
			}
		}
		return result;
	}

	/**
	 * Convert string collection to value array.
	 *
	 * @param valuesList
	 *            the values list
	 * @param valueFactory
	 *            the value factory
	 * @return the value[]
	 */
	protected Value[] convertStringCollectionToValueArray(final Collection<String> valuesList, final ValueFactory valueFactory) {
		return valuesList.stream().map(valueFactory::createValue).toArray(Value[]::new);
	}

	/**
	 * Gets the resource for modification.
	 *
	 * @param resource
	 *            the resource
	 * @param propertyName
	 *            the property name
	 * @return the resource to modify
	 */
	protected Resource getResourceForModification(final Resource resource, final String propertyName) {
		final StringBuilder path = new StringBuilder();

		if (StringUtils.isNotEmpty(this.getSubpathPrefix())) {
			path.append(this.getSubpathPrefix());
		}
		final String childResoucePath = this.getPathFromProperty(propertyName);

		if (StringUtils.isNotEmpty(childResoucePath)) {
			path.append("/").append(childResoucePath);
		}
		if (path.length() > 0) {
			return resource.getChild(path.toString());
		}
		return resource;
	}

	/**
	 * Gets the modification data.
	 *
	 * @param resourceResolver
	 *            the resource resolver
	 * @param pageManager
	 *            the page manager
	 * @param modificationRequest
	 *            the modification request
	 * @return the modification data
	 */
	protected List<ModificationInfoHolder> getModificationData(final ResourceResolver resourceResolver, final PageManager pageManager,
			final TagsModificationRequestBean modificationRequest) {
		final ValueFactory valueFactory = ValueFactoryImpl.getInstance();
		final List<ModificationInfoHolder> modificationDataList = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(modificationRequest.getModificationPaths())) {

			for (final String path : modificationRequest.getModificationPaths()) {

				if (!this.isSupportedPath(path)) {
					continue;
				}
				final Resource resource = resourceResolver.getResource(path);

				if (resource != null) {
					final Resource container = this.getContainingResource(resource, resourceResolver, pageManager);

					if ((container != null) && modificationRequest.getContentType().getPrimaryType().equals(container.getValueMap().get(
							JcrConstants.JCR_PRIMARYTYPE, String.class))) {

						modificationDataList.add(new ModificationInfoHolder(modificationRequest, container, resource, valueFactory));
					}
				}
			}
		} else {
			for (final ResultItem resultItem : this.find(modificationRequest).getResults()) {
				modificationDataList.add(new ModificationInfoHolder(modificationRequest, resourceResolver.getResource(resultItem.getContainerPath()),
						resourceResolver.getResource(resultItem.getPath()), valueFactory));
			}
		}
		return modificationDataList;
	}

	/**
	 * Process resources.
	 *
	 * @param modificationRequest
	 *            the modification request
	 * @param processor
	 *            the processor
	 * @param errorMsg
	 *            the error message
	 * @return the tags modification result bean
	 */
	protected TagsModificationResultBean processResources(final TagsModificationRequestBean modificationRequest,
			final RepositoryFunction<ModificationInfoHolder, Boolean> processor, final String errorMsg) {
		final TagsModificationResultBean result = new TagsModificationResultBean();

		final List<ResultItem> resultItems = new ArrayList<>();
		result.setResults(resultItems);

		try (ResourceResolver resourceResolver = this.getAdminService().getWriteService()) {
			final PageManager pageManager = CommonUtil.adapt(resourceResolver, PageManager.class);
			final List<ModificationInfoHolder> modificationDataList = this.getModificationData(resourceResolver, pageManager, modificationRequest);

			for (final ModificationInfoHolder infoHolder : modificationDataList) {
				LOGGER.debug("Processing resource {}.", infoHolder.getResource());

				this.canModify(infoHolder, this.getTagPropertiesMap());

				if (Boolean.TRUE.equals(processor.apply(infoHolder))) {
					resultItems.add(this.buildModificationResultItem(infoHolder.getContainingResource(), infoHolder.getResource(), pageManager));
				}
			}
			resourceResolver.commit();

		} catch (final PersistenceException | RepositoryException ex) {
			throw new FindReplaceModificationException(errorMsg, ex);
		}
		return result;
	}

	/**
	 * Checks if search with provided parameters is allowed for current content type.
	 *
	 * @param tagPropertiesMap
	 *            the tag properties map
	 * @param searchRequest
	 *            the search request
	 * @return true, if search with provided parameters is allowed
	 */
	protected boolean canFind(final Map<String, FindReplacePropertyBean> tagPropertiesMap, final TagsSearchRequestBean searchRequest) {

		for (final String propertyId : searchRequest.getTagsToFind().keySet()) {

			// check that property is allowed for current content type
			if (!tagPropertiesMap.containsKey(propertyId)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if resource can be modified. Throws {@linkplain FindReplaceModificationException} if not.
	 *
	 * @param infoHolder
	 *            the info holder
	 * @param tagPropertiesMap
	 *            the tag properties map
	 * @throws FindReplaceModificationException
	 *             the find replace modification exception
	 * @throws RepositoryException
	 *             the repository exception
	 */
	protected void canModify(final ModificationInfoHolder infoHolder, final Map<String, FindReplacePropertyBean> tagPropertiesMap)
			throws FindReplaceModificationException, RepositoryException {

		for (final Entry<String, List<String>> propertyEntry : infoHolder.getModificationRequest().getTagsForModification().entrySet()) {

			// check that property is allowed for current content type
			if (!tagPropertiesMap.containsKey(propertyEntry.getKey())) {
				throw new FindReplaceModificationException(String.format("Can not modify selected property in resource %s.", infoHolder.getResource()
						.getPath()));
			}
			// check that multiple values are not attempted to be saved into single valued property
			final String propertyName = this.mapProperty(propertyEntry.getKey());
			final String ownPropertyName = this.stripPathFromProperty(propertyName);

			final Resource resourceToModify = infoHolder.getResource();

			final Node node = CommonUtil.adapt(resourceToModify, Node.class);

			if (node.hasProperty(ownPropertyName)) {
				final Property property = node.getProperty(ownPropertyName);

				if (!property.isMultiple()) {
					this.canModifySingleValuedProperty(resourceToModify, infoHolder.getModificationRequest().getMode(), propertyEntry.getValue());
				}
			}
		}
	}

	private void canModifySingleValuedProperty(final Resource resource, final Mode mode, final List<String> tagValues) {
		int tagsNumber = 0;

		if (CollectionUtils.isNotEmpty(tagValues)) {
			tagsNumber = tagValues.size();
		}
		switch (mode) {

		case ADD:
			if (tagsNumber > 0) {
				throw new FindReplaceModificationException(String.format("Can not add values to single valued property in resource %s.",
						resource.getPath()));
			}
			break;

		case DELETE:
			if (tagsNumber > 1) {
				throw new FindReplaceModificationException(String.format("Can not delete multiple values from single valued property in resource %s.",
						resource.getPath()));
			}
			break;

		case REPLACE:
			if (tagsNumber > 1) {
				throw new FindReplaceModificationException(String.format("Can not assign multiple values to single valued property in resource %s.",
						resource.getPath()));
			}
			break;

		default:
			// do nothing
			break;
		}
	}

	/**
	 * Map property name to value specific to particular {@link ContentType}.
	 *
	 * @param propertyName
	 *            the property name
	 * @return the string
	 */
	protected String mapProperty(final String propertyName) {
		if (StringUtils.isEmpty(propertyName) || MapUtils.isEmpty(this.getTagPropertiesMap())) {
			return propertyName;
		}
		final FindReplacePropertyBean property = this.getTagPropertiesMap().get(propertyName);

		if (property != null) {
			return property.getName();
		}
		return propertyName;
	}

	/**
	 * Checks if is multiple property.
	 *
	 * @param propertyName
	 *            the property name
	 * @return true, if is multiple property
	 */
	protected boolean isMultipleProperty(final String propertyName) {
		if (StringUtils.isEmpty(propertyName) || MapUtils.isEmpty(this.getTagPropertiesMap())) {
			return false;
		}
		final FindReplacePropertyBean property = this.getTagPropertiesMap().get(propertyName);

		if (property != null) {
			return property.isMultiple();
		}
		return false;
	}

	/**
	 * Gets the existing tags.
	 *
	 * @param metadataMap
	 *            the metadata map
	 * @param propertyName
	 *            the property name
	 * @return the existing tags
	 */
	protected Set<String> getExistingTags(final ValueMap metadataMap, final String propertyName) {
		final String[] tagsArray = metadataMap.get(propertyName, String[].class);

		if (ArrayUtils.isNotEmpty(tagsArray)) {
			return new TreeSet<>(Arrays.asList(tagsArray));
		}
		return Collections.emptySet();
	}

	/**
	 * Checks if is own property.
	 *
	 * @param propertyName
	 *            the property name
	 * @return true, if is own property
	 */
	protected boolean isOwnProperty(final String propertyName) {
		return !propertyName.contains("/");
	}

	/**
	 * Gets the path from property.
	 *
	 * @param propertyName
	 *            the property name
	 * @return the path from property
	 */
	protected String getPathFromProperty(final String propertyName) {
		if (this.isOwnProperty(propertyName) || propertyName.contains("*")) {
			return null;
		}
		return propertyName.substring(0, propertyName.lastIndexOf("/"));
	}

	/**
	 * Strip path from property.
	 *
	 * @param propertyName
	 *            the property name
	 * @return the string
	 */
	protected String stripPathFromProperty(final String propertyName) {
		if (this.isOwnProperty(propertyName)) {
			return propertyName;
		}
		return propertyName.substring(propertyName.lastIndexOf("/") + 1);
	}

	/**
	 * Save property.
	 *
	 * @param resource
	 *            the resource
	 * @param propertyInfoHolder
	 *            the property info holder
	 * @throws RepositoryException
	 *             the repository exception
	 */
	protected void saveProperty(final Resource resource, final PropertyInfoHolder propertyInfoHolder) throws RepositoryException {

		final Node node = CommonUtil.adapt(resource, Node.class);

		// property exists
		if (node.hasProperty(propertyInfoHolder.getName())) {
			final Property property = node.getProperty(propertyInfoHolder.getName());

			if (property.isMultiple()) {
				this.saveMultiValuedProperty(node, propertyInfoHolder);

			} else {
				this.saveSingleValuedProperty(node, propertyInfoHolder);
			}
		}
		// property does not exist
		else {
			if (propertyInfoHolder.isMultiple()) {
				this.saveMultiValuedProperty(node, propertyInfoHolder);

			} else {
				this.saveSingleValuedProperty(node, propertyInfoHolder);
			}
		}
	}

	private void saveMultiValuedProperty(final Node node, final PropertyInfoHolder propertyInfoHolder) throws RepositoryException {

		node.setProperty(propertyInfoHolder.getName(), this.convertStringCollectionToValueArray(propertyInfoHolder.getUpdatedTags(), propertyInfoHolder
				.getValueFactory()));
	}

	private void saveSingleValuedProperty(final Node node, final PropertyInfoHolder propertyInfoHolder) throws RepositoryException {

		final Iterator<String> iterator = propertyInfoHolder.getUpdatedTags().iterator();

		if (iterator.hasNext()) {
			node.setProperty(propertyInfoHolder.getName(), propertyInfoHolder.getValueFactory().createValue(iterator.next()));

		} else {
			node.setProperty(propertyInfoHolder.getName(), propertyInfoHolder.getValueFactory().createValue(StringUtils.EMPTY));
		}
	}

	/**
	 * Adds the tag condition.
	 *
	 * @param tagsQuery
	 *            the tags query
	 * @param template
	 *            the template
	 * @param properties
	 *            the properties
	 */
	protected void addTagCondition(final StringBuilder tagsQuery, final String template, final Map<String, String> properties) {
		tagsQuery.append("(");
		tagsQuery.append(StrSubstitutor.replace(template, properties));
		tagsQuery.append(")");
	}

	/**
	 * Gets the tag properties map.
	 *
	 * @return the tagPropertiesMap
	 */
	public Map<String, FindReplacePropertyBean> getTagPropertiesMap() {
		return this.tagPropertiesMap;
	}

	/**
	 * Sets the tag properties map.
	 *
	 * @param tagPropertiesMap
	 *            the tagPropertiesMap to set
	 */
	public void setTagPropertiesMap(final Map<String, FindReplacePropertyBean> tagPropertiesMap) {
		this.tagPropertiesMap = tagPropertiesMap;
	}

}
