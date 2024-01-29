package com.eaton.platform.core.servlets.search.find.replace;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaton.platform.core.bean.search.find.replace.request.TextModificationRequestBean;
import com.eaton.platform.core.bean.search.find.replace.request.TextSearchRequestBean;
import com.eaton.platform.core.bean.search.find.replace.response.TextModificationResultBean;
import com.eaton.platform.core.bean.search.find.replace.response.TextSearchResultBean;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.FindAndReplaceConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.models.error.ValidationErrorModel;
import com.eaton.platform.core.models.search.find.replace.TextModificationRequestModel;
import com.eaton.platform.core.models.search.find.replace.TextSearchRequestModel;
import com.eaton.platform.core.services.converter.search.find.replace.TextModificationRequestModel2BeanConverter;
import com.eaton.platform.core.services.converter.search.find.replace.TextSearchRequestModel2BeanConverter;
import com.eaton.platform.core.services.search.find.replace.exception.FindReplaceException;
import com.eaton.platform.core.services.search.find.replace.text.FindReplaceTextFacade;
import com.eaton.platform.core.servlets.ServletHelper;
import com.eaton.platform.core.servlets.exception.ServletValidationException;
import com.eaton.platform.core.util.CommonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class FindReplaceTextServlet.
 *
 * Endpoint for "find and replace" administrative functionality. Searches and replaces text properties.
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
				ServletConstants.SLING_SERVLET_SELECTORS + "text",
				ServletConstants.SLING_SERVLET_EXTENSION_JSON })
public class FindReplaceTextServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = -3189065334619443581L;

	private static final Logger LOGGER = LoggerFactory.getLogger(FindReplaceTextServlet.class);

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private static final String MODIFICATION_ERROR_MESSAGE = "An error has occurred while modifying text.";

	@Reference
	private transient FindReplaceTextFacade textFacade;

	@Reference
	private transient TextSearchRequestModel2BeanConverter textSearchRequestModel2BeanConverter;

	@Reference
	private transient TextModificationRequestModel2BeanConverter textModificationRequestModel2BeanConverter;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws IOException {
		try {
			ServletHelper.setNoCacheHeaders(response);
			ServletHelper.setJsonContentType(response);

			// get data from request
			final TextSearchRequestModel searchModel = CommonUtil.adapt(request, TextSearchRequestModel.class);

			// validate
			if (!this.validateForPreview(searchModel, response)) {
				return;
			}
			final TextSearchRequestBean searchRequestBean = this.textSearchRequestModel2BeanConverter.convert(searchModel);
			LOGGER.debug("Received input: {}", searchRequestBean);

			// search for resources
			final TextSearchResultBean result = this.textFacade.find(searchRequestBean);
			LOGGER.debug("Search results: {}", result);

			// write response
			response.getWriter().write(OBJECT_MAPPER.writerFor(TextSearchResultBean.class).writeValueAsString(result));

		} catch (final Exception ex) {
			final String msg = "An error has occurred while searching for text.";
			LOGGER.error(msg, ex);

			ServletHelper.sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, msg);

		} finally {
			response.flushBuffer();
		}
	}

	private boolean validateForPreview(final TextSearchRequestModel model, final SlingHttpServletResponse response) throws IOException {
		return ServletHelper.validate(() -> {
			if (StringUtils.isEmpty(model.getPath())) {
				throw new ServletValidationException(response, new ValidationErrorModel("Path", FindAndReplaceConstants.VALIDATION_NOT_EMPTY));
			}
			if (!model.getPath().startsWith(CommonConstants.STARTS_WITH_CONTENT_WITH_SLASH)) {
				throw new ServletValidationException(response, new ValidationErrorModel("Path", FindAndReplaceConstants.VALIDATION_PATH_STARTS_WITH_CONTENT));
			}
			if (StringUtils.isEmpty(model.getSearchText())) {
				throw new ServletValidationException(response, new ValidationErrorModel("Text to find", FindAndReplaceConstants.VALIDATION_NOT_EMPTY));
			}
			return true;
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws IOException {
		try {
			ServletHelper.setNoCacheHeaders(response);
			ServletHelper.setJsonContentType(response);

			// get data from request
			final TextModificationRequestModel modificationModel = CommonUtil.adapt(request, TextModificationRequestModel.class);

			// validate
			if (!this.validateForModification(modificationModel, response)) {
				return;
			}
			final TextModificationRequestBean modificationRequestBean = this.textModificationRequestModel2BeanConverter.convert(modificationModel);
			LOGGER.debug("Received input: {}", modificationRequestBean);

			// execute modification
			final TextModificationResultBean result = this.textFacade.replace(modificationRequestBean);

			LOGGER.debug("Modification results: {}", result);

			// write response
			response.getWriter().write(OBJECT_MAPPER.writerFor(TextModificationResultBean.class).writeValueAsString(result));

		} catch (final FindReplaceException me) {
			LOGGER.error(MODIFICATION_ERROR_MESSAGE, me);

			ServletHelper.sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, me.getMessage());

		} catch (final Exception ex) {
			final String msg = "An error has occurred while modifying tags.";
			LOGGER.error(msg, ex);

			ServletHelper.sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, msg);

		} finally {
			response.flushBuffer();
		}
	}

	private boolean validateForModification(final TextModificationRequestModel model, final SlingHttpServletResponse response) throws IOException {
		return ServletHelper.validate(() -> {
			if (StringUtils.isEmpty(model.getPath())) {
				throw new ServletValidationException(response, new ValidationErrorModel("Path", FindAndReplaceConstants.VALIDATION_NOT_EMPTY));
			}
			if (!model.getPath().startsWith(CommonConstants.STARTS_WITH_CONTENT_WITH_SLASH)) {
				throw new ServletValidationException(response, new ValidationErrorModel("Path", FindAndReplaceConstants.VALIDATION_PATH_STARTS_WITH_CONTENT));
			}
			if (StringUtils.isEmpty(model.getSearchText())) {
				throw new ServletValidationException(response, new ValidationErrorModel("Text to find", FindAndReplaceConstants.VALIDATION_NOT_EMPTY));
			}
			if (StringUtils.isEmpty(model.getReplaceText())) {
				throw new ServletValidationException(response, new ValidationErrorModel("Text to replace", FindAndReplaceConstants.VALIDATION_NOT_EMPTY));
			}
			return true;
		});
	}

}
