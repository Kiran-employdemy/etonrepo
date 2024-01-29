package com.eaton.platform.core.servlets.search.find.replace;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.eaton.platform.core.bean.search.find.replace.request.TagsModificationRequestBean;
import com.eaton.platform.core.bean.search.find.replace.request.TagsSearchRequestBean;
import com.eaton.platform.core.bean.search.find.replace.response.TagsModificationResultBean;
import com.eaton.platform.core.bean.search.find.replace.response.TagsSearchResultBean;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.FindAndReplaceConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.exception.EatonApplicationException;
import com.eaton.platform.core.models.error.ValidationErrorModel;
import com.eaton.platform.core.models.search.find.replace.TagsModificationRequestModel;
import com.eaton.platform.core.models.search.find.replace.TagsSearchRequestModel;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.converter.search.find.replace.TagsModificationRequestModel2BeanConverter;
import com.eaton.platform.core.services.converter.search.find.replace.TagsSearchRequestModel2BeanConverter;
import com.eaton.platform.core.services.search.find.replace.exception.FindReplaceException;
import com.eaton.platform.core.services.search.find.replace.tags.FindReplaceTagsFacade;
import com.eaton.platform.core.servlets.ServletHelper;
import com.eaton.platform.core.servlets.exception.ServletValidationException;
import com.eaton.platform.core.util.CommonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class FindReplaceTagsServlet.
 *
 * Endpoint for "find and replace" administrative functionality. Searches and replaces tags.
 *
 * @author Gurneet Pal Singh, Jaroslav Rassadin
 */
@Component(
		service = Servlet.class,
		immediate = true,
		property = {
				ServletConstants.SLING_SERVLET_METHODS_GET,
				ServletConstants.SLING_SERVLET_METHODS_POST,
				ServletConstants.SLING_SERVLET_RESOURCE_TYPES + "eaton/components/search/find-replace",
				ServletConstants.SLING_SERVLET_SELECTORS + "tags",
				ServletConstants.SLING_SERVLET_EXTENSION_JSON })
public class FindReplaceTagsServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = -7728840033991076799L;

	private static final Logger LOGGER = LoggerFactory.getLogger(FindReplaceTagsServlet.class);

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private static final String MODIFICATION_ERROR_MESSAGE = "An error has occurred while modifying tags.";

	@Reference
	private transient AdminService adminService;

	@Reference
	private transient FindReplaceTagsFacade tagsFacade;

	@Reference
	private transient TagsSearchRequestModel2BeanConverter tagsSearchRequestModel2BeanConverter;

	@Reference
	private transient TagsModificationRequestModel2BeanConverter tagsModificationRequestModel2BeanConverter;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws IOException {
		try (ResourceResolver resourceResolver = this.adminService.getReadService()) {
			ServletHelper.setNoCacheHeaders(response);
			ServletHelper.setJsonContentType(response);

			// get data from request
			final TagsSearchRequestModel searchModel = CommonUtil.adapt(request, TagsSearchRequestModel.class);

			// validate
			if (!this.validateForPreview(searchModel, response, resourceResolver)) {
				return;
			}
			final TagsSearchRequestBean searchRequestBean = this.tagsSearchRequestModel2BeanConverter.convert(searchModel);
			LOGGER.debug("Received input: {}", searchRequestBean);

			// search for resources
			final TagsSearchResultBean result = this.tagsFacade.find(searchRequestBean);
			LOGGER.debug("Search results: {}", result);

			// write response
			response.getWriter().write(OBJECT_MAPPER.writerFor(TagsSearchResultBean.class).writeValueAsString(result));

		} catch (final Exception ex) {
			final String msg = "An error has occurred while searching for tags.";
			LOGGER.error(msg, ex);

			ServletHelper.sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, msg);

		} finally {
			response.flushBuffer();
		}
	}

	private boolean validateForPreview(final TagsSearchRequestModel searchModel, final SlingHttpServletResponse response,
			final ResourceResolver resourceResolver)
			throws IOException {
		return ServletHelper.validate(() -> {
			if (StringUtils.isEmpty(searchModel.getPath())) {
				throw new ServletValidationException(response, new ValidationErrorModel("Path", FindAndReplaceConstants.VALIDATION_NOT_EMPTY));
			}
			if (!searchModel.getPath().startsWith(CommonConstants.STARTS_WITH_CONTENT_WITH_SLASH)) {
				throw new ServletValidationException(response, new ValidationErrorModel("Path", FindAndReplaceConstants.VALIDATION_PATH_STARTS_WITH_CONTENT));
			}
			if (!this.validateTags(searchModel.getTagsToFind(), resourceResolver)) {
				throw new ServletValidationException(response, new ValidationErrorModel("Tags to find", FindAndReplaceConstants.VALIDATION_TAGS_NOT_CORRECT));
			}
			return true;
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws IOException {
		try (ResourceResolver resourceResolver = this.adminService.getReadService()) {
			ServletHelper.setNoCacheHeaders(response);
			ServletHelper.setJsonContentType(response);

			// get data from request
			final TagsModificationRequestModel modificationModel = CommonUtil.adapt(request, TagsModificationRequestModel.class);

			// validate
			if (!this.validateForModification(modificationModel, response, resourceResolver)) {
				return;
			}
			final TagsModificationRequestBean modificationRequestBean = this.tagsModificationRequestModel2BeanConverter.convert(modificationModel);
			LOGGER.debug("Received input: {}", modificationRequestBean);

			// execute modification
			TagsModificationResultBean result = null;

			switch (modificationRequestBean.getMode()) {

			case ADD:
				result = this.tagsFacade.add(modificationRequestBean);
				break;

			case DELETE:
				result = this.tagsFacade.delete(modificationRequestBean);
				break;

			case REPLACE:
				result = this.tagsFacade.replace(modificationRequestBean);
				break;

			default:
				throw new EatonApplicationException(String.format("Mode is not supported: %s.", modificationModel.getMode()));
			}
			LOGGER.debug("Modification results: {}", result);

			// write response
			response.getWriter().write(OBJECT_MAPPER.writerFor(TagsModificationResultBean.class).writeValueAsString(result));

		} catch (final FindReplaceException me) {
			LOGGER.error(MODIFICATION_ERROR_MESSAGE, me);

			ServletHelper.sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, me.getMessage());

		} catch (final Exception ex) {
			LOGGER.error(MODIFICATION_ERROR_MESSAGE, ex);

			ServletHelper.sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, MODIFICATION_ERROR_MESSAGE);

		} finally {
			response.flushBuffer();
		}
	}

	private boolean validateForModification(final TagsModificationRequestModel modificationModel, final SlingHttpServletResponse response,
			final ResourceResolver resourceResolver) throws IOException {
		return ServletHelper.validate(() -> {
			if (StringUtils.isEmpty(modificationModel.getPath())) {
				throw new ServletValidationException(response, new ValidationErrorModel("Path", FindAndReplaceConstants.VALIDATION_NOT_EMPTY));
			}
			if (!modificationModel.getPath().startsWith(CommonConstants.STARTS_WITH_CONTENT_WITH_SLASH)) {
				throw new ServletValidationException(response, new ValidationErrorModel("Path", FindAndReplaceConstants.VALIDATION_PATH_STARTS_WITH_CONTENT));
			}
			if (!this.validateTags(modificationModel.getTagsToFind(), resourceResolver)) {
				throw new ServletValidationException(response, new ValidationErrorModel("Tags to find", FindAndReplaceConstants.VALIDATION_TAGS_NOT_CORRECT));
			}
			if (!this.validateTags(modificationModel.getTagsForModification(), resourceResolver)) {
				throw new ServletValidationException(response, new ValidationErrorModel("Tags for modification",
						FindAndReplaceConstants.VALIDATION_TAGS_NOT_CORRECT));
			}
			return true;
		});
	}

	private boolean validateTags(final Map<String, List<String>> tags, final ResourceResolver resourceResolver) {
		if (MapUtils.isEmpty(tags)) {
			return false;
		}
		final TagManager tagManager = CommonUtil.adapt(resourceResolver, TagManager.class);

		for (final Map.Entry<String, List<String>> entry : tags.entrySet()) {

			for (final String tagValueStr : entry.getValue()) {
				final Tag tag = tagManager.resolve(tagValueStr);

				if (tag == null) {
					return false;
				}
			}
		}
		return true;
	}

}
