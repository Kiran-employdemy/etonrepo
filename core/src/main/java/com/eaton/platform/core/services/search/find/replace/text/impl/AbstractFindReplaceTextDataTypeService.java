package com.eaton.platform.core.services.search.find.replace.text.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jcr.Property;
import javax.jcr.RepositoryException;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.bean.search.find.replace.request.TextModificationRequestBean;
import com.eaton.platform.core.bean.search.find.replace.request.TextSearchRequestBean;
import com.eaton.platform.core.bean.search.find.replace.response.ResultItem;
import com.eaton.platform.core.bean.search.find.replace.response.TextModificationResultBean;
import com.eaton.platform.core.bean.search.find.replace.response.TextSearchResultBean;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.search.find.replace.RepositoryFunction;
import com.eaton.platform.core.services.search.find.replace.exception.FindReplaceModificationException;
import com.eaton.platform.core.services.search.find.replace.text.FindReplaceTextDataTypeService;
import com.eaton.platform.core.util.CommonUtil;

/**
 * The Class AbstractFindReplaceTextDataTypeService.
 *
 * @author Jaroslav Rassadin
 */
abstract class AbstractFindReplaceTextDataTypeService implements FindReplaceTextDataTypeService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractFindReplaceTextDataTypeService.class);

	/** The Constant PROPERTIES_NAMES. */
	protected static final String[] PROPERTIES_NAMES = { JcrConstants.JCR_TITLE, JcrConstants.JCR_DESCRIPTION, "navTitle", "jcr:text", "text", "transText",
			"topicLinkDesc" };

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
	protected abstract String getSearchQuery(final TextSearchRequestBean searchRequest);

	/**
	 * Checks for matching values.
	 *
	 * @param resource
	 *            the resource
	 * @param pattern
	 *            the pattern
	 * @return true, if has matching values
	 */
	protected abstract boolean hasMatchingValues(final Resource resource, final Pattern pattern);

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
	public TextSearchResultBean find(final TextSearchRequestBean searchRequest) {
		final TextSearchResultBean result = new TextSearchResultBean();

		try (ResourceResolver resourceResolver = this.getAdminService().getReadService()) {
			final PageManager pageManager = CommonUtil.adapt(resourceResolver, PageManager.class);

			final List<ResultItem> resultItems = new ArrayList<>();
			result.setResults(resultItems);

			final String query = this.getSearchQuery(searchRequest);
			LOGGER.debug("Generated query: {}", query);

			final Iterator<Resource> iterator = resourceResolver.findResources(query, "JCR-SQL2");

			if (searchRequest.isWholeWords() && searchRequest.isCaseSensitive()) {
				// full text search is case insensitive, need to refine results
				final Pattern pattern = this.getCompiledPattern(true, true, searchRequest.getSearchText());

				iterator.forEachRemaining((final Resource r) -> {

					if (this.hasMatchingValues(r, pattern)) {
						resultItems.add(this.buildSearchResultItem(r, pageManager));
					}
				});

			} else {
				iterator.forEachRemaining(r -> resultItems.add(this.buildSearchResultItem(r, pageManager)));
			}
		}
		return result;
	}

	/**
	 * Gets the resources for modification.
	 *
	 * @param resourceResolver
	 *            the resource resolver
	 * @param pageManager
	 *            the page manager
	 * @param modificationRequest
	 *            the modification request
	 * @return the resources for modification
	 */
	protected List<ModificationInfoHolder> getModificationData(final ResourceResolver resourceResolver, final PageManager pageManager,
			final TextModificationRequestBean modificationRequest) {
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

						modificationDataList.add(new ModificationInfoHolder(modificationRequest, container, resource));
					}
				}
			}
		} else {
			for (final ResultItem resultItem : this.find(modificationRequest).getResults()) {
				modificationDataList.add(new ModificationInfoHolder(modificationRequest, resourceResolver.getResource(resultItem.getContainerPath()),
						resourceResolver.getResource(resultItem.getPath())));
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
	 * @return the text modification result bean
	 */
	protected TextModificationResultBean processResources(final TextModificationRequestBean modificationRequest,
			final RepositoryFunction<ModificationInfoHolder, Boolean> processor, final String errorMsg) {
		final TextModificationResultBean result = new TextModificationResultBean();

		final List<ResultItem> resultItems = new ArrayList<>();
		result.setResults(resultItems);

		try (ResourceResolver resourceResolver = this.getAdminService().getWriteService()) {
			final PageManager pageManager = CommonUtil.adapt(resourceResolver, PageManager.class);
			final List<ModificationInfoHolder> modificationDataList = this.getModificationData(resourceResolver, pageManager, modificationRequest);

			for (final ModificationInfoHolder infoHolder : modificationDataList) {
				LOGGER.debug("Processing resource {}.", infoHolder.getResource());

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
	 * Gets the compiled pattern.
	 *
	 * @param wholeWordsOnly
	 *            the whole words only
	 * @param caseSensitive
	 *            the case sensitive
	 * @param searchString
	 *            the search string
	 * @return the compiled pattern
	 */
	protected Pattern getCompiledPattern(final boolean wholeWordsOnly, final boolean caseSensitive, final String searchString) {
		final String regex = wholeWordsOnly ? ("\\b" + searchString + "\\b") : (".*" + searchString + ".*");
		return caseSensitive ? Pattern.compile(regex) : Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
	}

	/**
	 * Adds the case sensitive pattern prefix.
	 *
	 * @param caseSensitive
	 *            the case sensitive
	 * @return the string
	 */
	protected String addCaseSensitivePatternPrefix(final boolean caseSensitive) {
		return caseSensitive ? "" : "(?i)";
	}

	/**
	 * Prepare search text.
	 *
	 * @param searchText
	 *            the search text
	 * @param caseSensitive
	 *            the case sensitive
	 * @return the string
	 */
	protected String prepareSearchText(final String searchText, final boolean caseSensitive) {
		String escapedText = searchText;

		if (!caseSensitive) {
			escapedText = escapedText.toLowerCase(Locale.ROOT);
		}
		return escapedText.replace("'", "''").replace("\\\\", "\\\\\\\\").replace("%", "\\\\%").replace("_", "\\\\_").replace(" ", "\\ ");

	}

	/**
	 * Update property.
	 *
	 * @param infoHolder
	 *            the info holder
	 * @param valueMap
	 *            the value map
	 * @return true, if successful
	 * @throws RepositoryException
	 *             the repository exception
	 */
	protected boolean updateProperty(final PropertyModificationInfoHolder infoHolder, final ValueMap valueMap) throws RepositoryException {

		final Property property = valueMap.get(infoHolder.getPropertyName(), Property.class);

		if (property != null) {

			if (property.isMultiple()) {
				return this.updateMultiValueProperty(infoHolder, property, valueMap);

			} else {
				return this.updateSingleValueProperty(infoHolder, property);
			}
		}
		return false;
	}

	/**
	 * Update multi value property.
	 *
	 * @param infoHolder
	 *            the info holder
	 * @param property
	 *            the property
	 * @param valueMap
	 *            the value map
	 * @return true, if successful
	 * @throws RepositoryException
	 *             the repository exception
	 */
	protected boolean updateMultiValueProperty(final PropertyModificationInfoHolder infoHolder, final Property property, final ValueMap valueMap)
			throws RepositoryException {

		final String[] values = valueMap.get(property.getName(), String[].class);
		boolean updated = false;

		for (int i = 0; i < values.length; ++i) {
			final String value = values[i];
			final Matcher matcher = infoHolder.getPattern().matcher(value);

			if (matcher.find()) {
				LOGGER.debug("Going to replace {} with {} in {}.", infoHolder.getSearchText(), infoHolder.getReplaceText(), value);
				values[i] = value.replaceAll(this.addCaseSensitivePatternPrefix(infoHolder.isCaseSensitive()) + infoHolder.getSearchText(), infoHolder
						.getReplaceText());

				updated = true;
			}
		}
		if (updated) {
			property.setValue(values);

			return true;
		}
		return false;
	}

	/**
	 * Update single value property.
	 *
	 * @param infoHolder
	 *            the info holder
	 * @param property
	 *            the property
	 * @return true, if successful
	 * @throws RepositoryException
	 *             the repository exception
	 */
	protected boolean updateSingleValueProperty(final PropertyModificationInfoHolder infoHolder, final Property property) throws RepositoryException {
		final String propetyValue = property.getString();

		final Matcher matcher = infoHolder.getPattern().matcher(propetyValue);

		if (matcher.find()) {
			LOGGER.debug("Going to replace {} with {} in {}.", infoHolder.getSearchText(), infoHolder.getReplaceText(), propetyValue);
			property.setValue(propetyValue.replaceAll(this.addCaseSensitivePatternPrefix(infoHolder.isCaseSensitive()) + infoHolder.getSearchText(), infoHolder
					.getReplaceText()));

			return true;
		}
		return false;
	}

}
