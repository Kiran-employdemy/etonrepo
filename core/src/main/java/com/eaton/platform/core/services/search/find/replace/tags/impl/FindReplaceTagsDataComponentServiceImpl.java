package com.eaton.platform.core.services.search.find.replace.tags.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.jcr.RepositoryException;
import javax.jcr.ValueFactory;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.lang3.StringUtils;
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
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.bean.search.find.replace.FindReplacePropertyBean;
import com.eaton.platform.core.bean.search.find.replace.FindReplaceResourceTypeBean;
import com.eaton.platform.core.bean.search.find.replace.request.ContentType;
import com.eaton.platform.core.bean.search.find.replace.request.TagsModificationRequestBean;
import com.eaton.platform.core.bean.search.find.replace.request.TagsSearchRequestBean;
import com.eaton.platform.core.bean.search.find.replace.response.ResultItem;
import com.eaton.platform.core.bean.search.find.replace.response.TagsModificationResultBean;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.FindAndReplaceConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.search.find.replace.FindReplaceComponentService;
import com.eaton.platform.core.services.search.find.replace.FindReplaceConfigService;
import com.eaton.platform.core.services.search.find.replace.FindReplaceConfigServiceConfig;
import com.eaton.platform.core.services.search.find.replace.RepositoryFunction;
import com.eaton.platform.core.services.search.find.replace.exception.FindReplaceException;
import com.eaton.platform.core.services.search.find.replace.exception.FindReplaceModificationException;
import com.eaton.platform.core.services.search.find.replace.tags.FindReplaceTagsDataTypeService;

/**
 * The Class FindReplaceTagsDataComponentServiceImpl.
 *
 * Service that executes "find and replace" functionality for tags for components.
 *
 * @author Jaroslav Rassadin
 */
@Component(
		service = FindReplaceTagsDataTypeService.class,
		property = {
				AEMConstants.SERVICE_VENDOR_EATON,
				AEMConstants.SERVICE_DESCRIPTION + "Find Replace Text Data Component Service",
				AEMConstants.PROCESS_LABEL + "FindReplaceTagsDataComponentServiceImpl" })
public class FindReplaceTagsDataComponentServiceImpl extends AbstractFindReplaceTagsDataTypeService implements FindReplaceTagsDataTypeService {

	private static final Logger LOGGER = LoggerFactory.getLogger(FindReplaceTagsDataComponentServiceImpl.class);

	private static final ContentType CONTENT_TYPE = ContentType.COMPONENT;

	@Reference
	private AdminService adminService;

	@Reference
	private FindReplaceConfigService configService;

	@Reference
	private FindReplaceComponentService componentService;

	private final RepositoryFunction<ModificationInfoHolder, Boolean> addProcessor = i -> this.add(i.getResource(), i.getModificationRequest()
			.getTagsForModification(), i.getValueFactory());

	private final RepositoryFunction<ModificationInfoHolder, Boolean> deleteProcessor = i -> this.delete(i.getResource(), i.getModificationRequest()
			.getTagsForModification(), i.getValueFactory());

	private final RepositoryFunction<ModificationInfoHolder, Boolean> replaceProcessor = i -> this.replace(i.getResource(), i.getModificationRequest()
			.getTagsToFind(), i.getModificationRequest().getTagsForModification(), i.getValueFactory());

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
		return this.processResources(modificationRequest, this.addProcessor, "An error has occurred while adding tags to components.");
	}

	private boolean add(final Resource resource, final Map<String, List<String>> tagsForModification, final ValueFactory valueFactory)
			throws RepositoryException {
		boolean result = false;

		for (final Map.Entry<String, List<String>> entry : tagsForModification.entrySet()) {
			final String propertyName = this.mapProperty(entry.getKey());
			final String ownPropertyName = this.stripPathFromProperty(propertyName);

			final Resource resourceToModify = this.getResourceForModification(resource, propertyName);
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
						valueFactory));
			}
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TagsModificationResultBean delete(final TagsModificationRequestBean modificationRequest) {
		return this.processResources(modificationRequest, this.deleteProcessor, "An error has occurred while deleting tags in components.");
	}

	private boolean delete(final Resource resource, final Map<String, List<String>> tagsForModification, final ValueFactory valueFactory)
			throws RepositoryException {
		boolean result = false;

		for (final Map.Entry<String, List<String>> entry : tagsForModification.entrySet()) {
			final String propertyName = this.mapProperty(entry.getKey());
			final String ownPropertyName = this.stripPathFromProperty(propertyName);

			final Resource resourceToModify = this.getResourceForModification(resource, propertyName);
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
						valueFactory));
			}
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TagsModificationResultBean replace(final TagsModificationRequestBean modificationRequest) {
		return this.processResources(modificationRequest, this.replaceProcessor, "An error has occurred while replacing tags in components.");
	}

	private boolean replace(final Resource resource, final Map<String, List<String>> tagsToBeReplaced, final Map<String, List<String>> tagsForModification,
			final ValueFactory valueFactory) throws RepositoryException {
		boolean result = false;

		for (final Map.Entry<String, List<String>> entry : tagsForModification.entrySet()) {
			final String propertyName = this.mapProperty(entry.getKey());
			final String ownPropertyName = this.stripPathFromProperty(propertyName);

			final Resource resourceToModify = this.getResourceForModification(resource, propertyName);
			final ValueMap resourceToModifyValueMap = resourceToModify.getValueMap();

			final Set<String> existingTags = this.getExistingTags(resourceToModifyValueMap, propertyName);

			final Set<String> updatedTags = new TreeSet<>(existingTags);
			updatedTags.removeAll(tagsToBeReplaced.get(entry.getKey()));
			updatedTags.addAll(entry.getValue());

			if (existingTags.equals(updatedTags)) {
				LOGGER.debug("Property already contains all provided tags. Skip.");

			} else {
				LOGGER.debug("Updated list of tags: {}.", updatedTags);
				result |= true;

				this.saveProperty(resourceToModify, new PropertyInfoHolder(this.isMultipleProperty(entry.getKey()), ownPropertyName, updatedTags,
						valueFactory));
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
		final Page containingPage = pageManager.getContainingPage(resource);
		final Resource container = this.getContainingComponent(resource);
		final String containerTitle = this.componentService.getTypeTitle(container);

		return ResultItem.builder().withContentType(ContentType.COMPONENT).withTopContainerPath(containingPage.getPath())
				.withTopContainerTitle(containingPage.getTitle()).withContainerPath(container.getPath()).withContainerTitle(containerTitle)
				.withPath(resource.getPath()).withTitle(containerTitle).build();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ResultItem buildModificationResultItem(final Resource container, final Resource resource, final PageManager pageManager) {
		final Page containingPage = pageManager.getContainingPage(resource);
		final String containerTitle = this.componentService.getTypeTitle(container);

		return ResultItem.builder().withContentType(ContentType.COMPONENT).withTopContainerPath(containingPage.getPath())
				.withTopContainerTitle(containingPage.getTitle()).withContainerPath(container.getPath()).withContainerTitle(containerTitle)
				.withPath(resource.getPath()).withTitle(containerTitle).build();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getSearchQuery(final TagsSearchRequestBean searchRequest) {
		final StringBuilder query = new StringBuilder();
		final StringBuilder tagsQuery = new StringBuilder();

		final Set<Map.Entry<String, List<String>>> entrySet = searchRequest.getTagsToFind().entrySet();
		int entrySetIndex = 0;

		boolean useSubQuery = false;

		for (final Map.Entry<String, List<String>> entry : entrySet) {
			final List<String> tagValueList = entry.getValue();
			int tagValueListIndex = 0;

			final String propertyName = this.mapProperty(entry.getKey());
			final boolean ownProperty = this.isOwnProperty(propertyName);

			useSubQuery |= !ownProperty;

			for (final String tagValue : tagValueList) {

				if (ownProperty) {
					this.addTagCondition(tagsQuery, FindAndReplaceConstants.DEFAULT_QUERY_TAG, Map.of(FindAndReplaceConstants.ALIAS_PARAM,
							FindAndReplaceConstants.DEFAULT_ALIAS, FindAndReplaceConstants.PROP_NAME_PARAM,
							propertyName, FindAndReplaceConstants.SEARCH_STRING, tagValue));

				} else {
					this.addTagCondition(tagsQuery, FindAndReplaceConstants.DEFAULT_QUERY_TAG, Map.of(FindAndReplaceConstants.ALIAS_PARAM,
							FindAndReplaceConstants.DEFAULT_ALIAS, FindAndReplaceConstants.PROP_NAME_PARAM,
							propertyName, FindAndReplaceConstants.SEARCH_STRING, tagValue));

					tagsQuery.append(FindAndReplaceConstants.AND_CONDITION);

					this.addTagCondition(tagsQuery, FindAndReplaceConstants.DEFAULT_QUERY_TAG, Map.of(FindAndReplaceConstants.ALIAS_PARAM,
							FindAndReplaceConstants.CHILD_ALIAS, FindAndReplaceConstants.PROP_NAME_PARAM,
							this.stripPathFromProperty(propertyName), FindAndReplaceConstants.SEARCH_STRING, tagValue));
				}
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
		if (useSubQuery) {
			query.append(StrSubstitutor.replace(FindAndReplaceConstants.JOIN_QUERY, Map.of("nodeType", JcrConstants.NT_UNSTRUCTURED, "pathToSearch",
					searchRequest
							.getPath(), "propertiesQuery", tagsQuery.toString())));

			this.addResourceTypeCondition(query, FindAndReplaceConstants.DEFAULT_ALIAS, searchRequest.getTagsToFind(), this.getTagPropertiesMap());
		} else {
			query.append(StrSubstitutor.replace(FindAndReplaceConstants.QUERY, Map.of("nodeType", JcrConstants.NT_UNSTRUCTURED, "pathToSearch", searchRequest
					.getPath(), "propertiesQuery", tagsQuery.toString())));

			this.addResourceTypeCondition(query, FindAndReplaceConstants.DEFAULT_ALIAS, searchRequest.getTagsToFind(), this.getTagPropertiesMap());
		}
		return query.toString();
	}

	private void addResourceTypeCondition(final StringBuilder query, final String alias, final Map<String, List<String>> tagsToFind,
			final Map<String, FindReplacePropertyBean> propertiesMap) {
		if (MapUtils.isEmpty(propertiesMap)) {
			return;
		}
		final Set<Map.Entry<String, List<String>>> tagsToFindSet = tagsToFind.entrySet();
		int tagsToFindIndex = 0;
		final StringBuilder resourceTypesBuilder = new StringBuilder();

		for (final Map.Entry<String, List<String>> tagToFind : tagsToFindSet) {
			final FindReplacePropertyBean propery = propertiesMap.get(tagToFind.getKey());

			if ((propery == null) || CollectionUtils.isEmpty(propery.getResourceTypes())) {
				continue;
			}
			this.joinResourceTypes(resourceTypesBuilder, propery.getResourceTypes());

			if (tagsToFindIndex < (tagsToFindSet.size() - 1)) {
				resourceTypesBuilder.append(",");
			}
			tagsToFindIndex++;
		}
		if (resourceTypesBuilder.length() > 0) {
			query.append(FindAndReplaceConstants.AND_CONDITION);
			query.append("(");
			query.append(StrSubstitutor.replace(FindAndReplaceConstants.RESOURCE_TYPE_CONDITION, Map.of(FindAndReplaceConstants.ALIAS_PARAM, alias,
					"resourceTypes",
					resourceTypesBuilder.toString())));
			query.append(")");
		}
	}

	private void joinResourceTypes(final StringBuilder resourceTypesBuilder, final Set<FindReplaceResourceTypeBean> resourceTypes) {
		if (CollectionUtils.isEmpty(resourceTypes)) {
			return;
		}
		int resourceTypesIndex = 0;

		for (final FindReplaceResourceTypeBean resourceType : resourceTypes) {
			resourceTypesBuilder.append("'").append(resourceType.getName()).append("'");

			if (resourceTypesIndex < (resourceTypes.size() - 1)) {
				resourceTypesBuilder.append(",");
			}
			resourceTypesIndex++;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean canFind(final Map<String, FindReplacePropertyBean> tagPropertiesMap, final TagsSearchRequestBean searchRequest) {

		for (final String propertyId : searchRequest.getTagsToFind().keySet()) {
			final FindReplacePropertyBean property = tagPropertiesMap.get(propertyId);

			if ((property == null) || CollectionUtils.isEmpty(property.getResourceTypes())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void canModify(final ModificationInfoHolder infoHolder, final Map<String, FindReplacePropertyBean> tagPropertiesMap)
			throws FindReplaceModificationException, RepositoryException {

		super.canModify(infoHolder, tagPropertiesMap);

		final Resource component = infoHolder.getContainingResource();

		if (component == null) {
			throw new FindReplaceModificationException(String.format("Can not find containing component for resource %s.",
					infoHolder.getResource() != null ? infoHolder.getResource().getPath() : StringUtils.EMPTY));
		}
		final boolean isChildResource = !infoHolder.getResource().getPath().equals(component.getPath());

		String subPath = null;

		if (isChildResource) {
			subPath = infoHolder.getResource().getPath().replace(component.getPath() + "/", StringUtils.EMPTY);
		}
		for (final String propertyId : infoHolder.getModificationRequest().getTagsForModification().keySet()) {
			final FindReplacePropertyBean property = tagPropertiesMap.get(propertyId);

			if ((property == null) || !this.supportsResourceType(component.getResourceType(), subPath, property.getResourceTypes())) {
				throw new FindReplaceModificationException(String.format("Can not modify selected property in resource %s.",
						infoHolder.getResource().getPath()));
			}
			if (!isChildResource && !this.isOwnProperty(property.getName())) {
				throw new FindReplaceModificationException(String.format("Can not modify selected property that belongs to child resource in resource %s.",
						infoHolder.getResource().getPath()));
			}
		}
	}

	private Resource getContainingComponent(final Resource resource) {
		if (resource == null) {
			throw new FindReplaceException("Resource is null.");
		}
		if (!JcrConstants.NT_UNSTRUCTURED.equals(resource.getResourceType())) {
			return resource;
		}
		return this.getContainingComponent(resource.getParent());
	}

	private boolean supportsResourceType(final String resourceType, final String subPath, final Set<FindReplaceResourceTypeBean> resourceTypes) {
		if (CollectionUtils.isEmpty(resourceTypes)) {
			return false;
		}
		return resourceTypes.stream().filter((r) -> {

			if (!r.getName().equals(resourceType)) {
				return false;
			}
			if ((subPath != null) && ((r.getSubType() == null) || !subPath.startsWith(r.getSubType()))) {
				return false;

			}
			return true;

		}).findFirst().isPresent();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getSubpathPrefix() {
		return StringUtils.EMPTY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Resource getContainingResource(final Resource resource, final ResourceResolver resourceResolver, final PageManager pageManager) {
		return this.getContainingComponent(resource);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isSupportedPath(final String path) {
		return (path != null) && !path.endsWith(JcrConstants.JCR_CONTENT);
	}

}
