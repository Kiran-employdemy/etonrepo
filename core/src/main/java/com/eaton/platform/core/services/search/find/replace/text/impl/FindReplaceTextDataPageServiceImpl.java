package com.eaton.platform.core.services.search.find.replace.text.impl;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jcr.RepositoryException;

import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.bean.search.find.replace.request.ContentType;
import com.eaton.platform.core.bean.search.find.replace.request.TextModificationRequestBean;
import com.eaton.platform.core.bean.search.find.replace.request.TextSearchRequestBean;
import com.eaton.platform.core.bean.search.find.replace.response.ResultItem;
import com.eaton.platform.core.bean.search.find.replace.response.TextModificationResultBean;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.FindAndReplaceConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.search.find.replace.RepositoryFunction;
import com.eaton.platform.core.services.search.find.replace.text.FindReplaceTextDataTypeService;
import com.eaton.platform.core.util.CommonUtil;

/**
 * The Class FindReplaceTextDataPageServiceImpl.
 *
 * Service that executes "find and replace" functionality for text for pages.
 *
 * @author Jaroslav Rassadin
 */
@Component(
		service = FindReplaceTextDataTypeService.class,
		property = {
				AEMConstants.SERVICE_VENDOR_EATON,
				AEMConstants.SERVICE_DESCRIPTION + "Find Replace Text Data Page Service",
				AEMConstants.PROCESS_LABEL + "FindReplaceTextDataPageServiceImpl" })
public class FindReplaceTextDataPageServiceImpl extends AbstractFindReplaceTextDataTypeService implements FindReplaceTextDataTypeService {

	@Reference
	private AdminService adminService;

	private final RepositoryFunction<ModificationInfoHolder, Boolean> replaceProcessor = i -> this.update(i);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TextModificationResultBean replace(final TextModificationRequestBean modificationRequest) {
		return this.processResources(modificationRequest, this.replaceProcessor, "An error has occurred while replacing text in pages.");
	}

	private boolean update(final ModificationInfoHolder infoHolder) throws RepositoryException {
		boolean result = false;

		final ValueMap valueMap = infoHolder.getResource().getValueMap();

		final Pattern pattern = this.getCompiledPattern(infoHolder.getModificationRequest().isWholeWords(), infoHolder.getModificationRequest()
				.isCaseSensitive(), infoHolder.getModificationRequest().getSearchText());

		for (int i = 0; i < PROPERTIES_NAMES.length; i++) {
			final String propertyName = PROPERTIES_NAMES[i];

			result |= this.updateProperty(new PropertyModificationInfoHolder(propertyName, pattern, infoHolder.getModificationRequest().getSearchText(),
					infoHolder.getModificationRequest().getReplaceText(), infoHolder.getModificationRequest().isCaseSensitive()), valueMap);
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canProcess(final ContentType value) {
		return ContentType.PAGE.equals(value);
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
		final Resource contentResource = resource.getChild(JcrConstants.JCR_CONTENT);
		final String title = contentResource.getValueMap().get(JcrConstants.JCR_TITLE, String.class);

		return ResultItem.builder().withContentType(ContentType.PAGE).withTopContainerPath(resource.getPath()).withTopContainerTitle(title)
				.withContainerPath(resource.getPath()).withContainerTitle(title).withPath(contentResource.getPath()).withTitle(title).build();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ResultItem buildModificationResultItem(final Resource container, final Resource resource, final PageManager pageManager) {
		final String title = resource.getValueMap().get(JcrConstants.JCR_TITLE, String.class);

		return ResultItem.builder().withContentType(ContentType.PAGE).withTopContainerPath(container.getPath()).withTopContainerTitle(title)
				.withContainerPath(container.getPath()).withContainerTitle(title).withPath(resource.getPath()).withTitle(title).build();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getSearchQuery(final TextSearchRequestBean searchRequest) {
		final StringBuilder propertiesQuery = new StringBuilder();
		final String searchText = this.prepareSearchText(searchRequest.getSearchText(), searchRequest.isCaseSensitive());
		String queryFragment = null;

		if (searchRequest.isWholeWords()) {
			queryFragment = FindAndReplaceConstants.PAGE_QUERY_WHOLE_WORD;

		} else {
			queryFragment = searchRequest.isCaseSensitive() ? FindAndReplaceConstants.PAGE_QUERY_CASE_SENSITIVE_NO_WHOLE_WORD
					: FindAndReplaceConstants.PAGE_QUERY_CASE_INSENSITIVE_NO_WHOLE_WORD;
		}
		for (int i = 0; i < PROPERTIES_NAMES.length; i++) {
			final String propertyName = PROPERTIES_NAMES[i];

			propertiesQuery.append("(").append(
					StrSubstitutor.replace(queryFragment, Map.of(FindAndReplaceConstants.PROP_NAME_PARAM, propertyName,
							FindAndReplaceConstants.SEARCH_STRING, searchText)))
					.append(")");

			if (i < (PROPERTIES_NAMES.length - 1)) {
				propertiesQuery.append(FindAndReplaceConstants.OR_CONDITION);
			}
		}
		return StrSubstitutor.replace(FindAndReplaceConstants.QUERY,
				Map.of("nodeType", NameConstants.NT_PAGE, "pathToSearch", searchRequest.getPath(), "propertiesQuery",
						propertiesQuery.toString()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean hasMatchingValues(final Resource resource, final Pattern pattern) {
		final Resource contentResource = resource.getChild(JcrConstants.JCR_CONTENT);
		final ValueMap contentResourceValueMap = contentResource.adaptTo(ValueMap.class);

		for (int i = 0; i < PROPERTIES_NAMES.length; i++) {
			final String propertyName = PROPERTIES_NAMES[i];

			final String value = contentResourceValueMap.get(propertyName, String.class);

			if (StringUtils.isNotEmpty(value)) {
				final Matcher matcher = pattern.matcher(value);

				if (matcher.find()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Resource getContainingResource(final Resource resource, final ResourceResolver resourceResolver, final PageManager pageManager) {
		return CommonUtil.adapt(pageManager.getContainingPage(resource), Resource.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isSupportedPath(final String path) {
		return (path != null) && path.endsWith(JcrConstants.JCR_CONTENT);
	}

}
