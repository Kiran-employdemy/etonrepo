package com.eaton.platform.core.servlets.search.find.replace;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.day.cq.commons.jcr.JcrConstants;
import com.eaton.platform.core.bean.search.find.replace.request.ContentType;
import com.eaton.platform.core.bean.search.find.replace.request.TextModificationRequestBean;
import com.eaton.platform.core.bean.search.find.replace.request.TextSearchRequestBean;
import com.eaton.platform.core.bean.search.find.replace.response.ResultItem;
import com.eaton.platform.core.bean.search.find.replace.response.TextModificationResultBean;
import com.eaton.platform.core.bean.search.find.replace.response.TextSearchResultBean;
import com.eaton.platform.core.constants.FindAndReplaceConstants;
import com.eaton.platform.core.injectors.impl.RequestParameterInjectorImpl;
import com.eaton.platform.core.models.error.GenericErrorsModel;
import com.eaton.platform.core.models.error.ValidationErrorModel;
import com.eaton.platform.core.models.search.find.replace.TextModificationRequestModel;
import com.eaton.platform.core.models.search.find.replace.TextSearchRequestModel;
import com.eaton.platform.core.services.converter.search.find.replace.TextModificationRequestModel2BeanConverter;
import com.eaton.platform.core.services.converter.search.find.replace.TextSearchRequestModel2BeanConverter;
import com.eaton.platform.core.services.search.find.replace.text.FindReplaceTextFacade;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

/**
 * The Class FindReplaceTextServletTest.
 *
 * @author Jaroslav Rassadin
 */
@ExtendWith(
		value = { AemContextExtension.class, MockitoExtension.class })
class FindReplaceTextServletTest {

	private static final String PAGE_PATH = "/content/eaton/de/de-de/catalog/emobility/test_replace";

	private static final String PAGE_CONTENT_PATH = PAGE_PATH + "/" + JcrConstants.JCR_CONTENT;

	private static final String PAGE_TITLE = "Eaton page";

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private final AemContext context = new AemContext(ResourceResolverType.RESOURCERESOLVER_MOCK);

	@Mock
	private FindReplaceTextFacade textFacade;

	@Mock
	private TextSearchRequestModel2BeanConverter textSearchRequestModel2BeanConverter;

	@Mock
	private TextModificationRequestModel2BeanConverter textModificationRequestModel2BeanConverter;

	@Mock
	private TextSearchRequestBean searchRequestBean;

	@Mock
	private TextModificationRequestBean modificationRequestBean;

	@InjectMocks
	private final FindReplaceTextServlet classUnderTest = new FindReplaceTextServlet();

	/**
	 * Sets up test data.
	 */
	@BeforeEach
	public void setUp() {
		this.context.registerInjectActivateService(new RequestParameterInjectorImpl());
	}

	/**
	 * Test that resources with text are found.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	@DisplayName("Resources with text are found")
	void testResourcesWithTextAreFound() throws IOException {
		// set up
		final TextSearchResultBean result = this.getTextSearchResultBean();

		final MockSlingHttpServletRequest request = this.context.request();
		request.setParameterMap(Map.of("specificPath", "/content/eaton/de/de-de/catalog", "searchText", "Eaton"));

		final MockSlingHttpServletResponse response = this.context.response();

		when(this.textSearchRequestModel2BeanConverter.convert(any(TextSearchRequestModel.class))).thenReturn(this.searchRequestBean);
		when(this.textFacade.find(this.searchRequestBean)).thenReturn(result);

		// exercise
		this.classUnderTest.doGet(request, response);

		// verify
		assertEquals(result, OBJECT_MAPPER.readerFor(TextSearchResultBean.class).readValue(response.getOutputAsString()), "Should be equal search results");
	}

	/**
	 * Test that validation fails for text search with incorrect path.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	@DisplayName("Validation fails for text search with incorrect path")
	void testValidationFailsForTextSearchWithIncorrectPath() throws IOException {
		// set up
		final MockSlingHttpServletRequest request = this.context.request();
		request.setParameterMap(Map.of("specificPath", "/var/commerce/products", "searchText", "Eaton"));

		final MockSlingHttpServletResponse response = this.context.response();

		// exercise
		this.classUnderTest.doGet(request, response);

		final GenericErrorsModel errorsModel = OBJECT_MAPPER.readerFor(GenericErrorsModel.class).readValue(response.getOutputAsString());

		// verify
		assertEquals(new ValidationErrorModel("Path", FindAndReplaceConstants.VALIDATION_PATH_STARTS_WITH_CONTENT), errorsModel.getErrors().get(0),
				"Should be equal validation errors");
	}

	/**
	 * Test that validation fails for text search with empty text to find.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	@DisplayName("Validation fails for text search with empty text to find")
	void testValidationFailsForTextSearchWithEmptyTextToFind() throws IOException {
		// set up
		final MockSlingHttpServletRequest request = this.context.request();
		request.setParameterMap(Map.of("specificPath", "/content/eaton/de/de-de/catalog"));

		final MockSlingHttpServletResponse response = this.context.response();

		// exercise
		this.classUnderTest.doGet(request, response);

		final GenericErrorsModel errorsModel = OBJECT_MAPPER.readerFor(GenericErrorsModel.class).readValue(response.getOutputAsString());

		// verify
		assertEquals(new ValidationErrorModel("Text to find", FindAndReplaceConstants.VALIDATION_NOT_EMPTY), errorsModel.getErrors().get(0),
				"Should be equal validation errors");
	}

	/**
	 * Test that resources with text are modified.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	@DisplayName("Resources with text are modified")
	void testResourcesWithTextAreModified() throws IOException {
		// set up
		final TextModificationResultBean result = this.getTextModificationResultBean();

		final MockSlingHttpServletRequest request = this.context.request();
		request.setParameterMap(Map.of("specificPath", "/content/eaton/de/de-de/catalog", "searchText", "Eaton", "replaceText", "Eaton1"));

		final MockSlingHttpServletResponse response = this.context.response();

		when(this.textModificationRequestModel2BeanConverter.convert(any(TextModificationRequestModel.class))).thenReturn(this.modificationRequestBean);
		when(this.textFacade.replace(this.modificationRequestBean)).thenReturn(result);

		// exercise
		this.classUnderTest.doPost(request, response);

		// verify
		assertEquals(result, OBJECT_MAPPER.readerFor(TextModificationResultBean.class).readValue(response.getOutputAsString()),
				"Should be equal modification results");
	}

	/**
	 * Test that validation fails for text modification with incorrect path.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	@DisplayName("Validation fails for text modification with incorrect path")
	void testValidationFailsForTextModificationWithIncorrectPath() throws IOException {
		// set up
		final MockSlingHttpServletRequest request = this.context.request();
		request.setParameterMap(Map.of("specificPath", "/var/commerce/products", "searchText", "Eaton", "replaceText", "Eaton1"));

		final MockSlingHttpServletResponse response = this.context.response();

		// exercise
		this.classUnderTest.doGet(request, response);

		final GenericErrorsModel errorsModel = OBJECT_MAPPER.readerFor(GenericErrorsModel.class).readValue(response.getOutputAsString());

		// verify
		assertEquals(new ValidationErrorModel("Path", FindAndReplaceConstants.VALIDATION_PATH_STARTS_WITH_CONTENT), errorsModel.getErrors().get(0),
				"Should be equal validation errors");
	}

	/**
	 * Test that validation fails for text modification with empty text to replace.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	@DisplayName("Validation fails for text modification with empty text to replace")
	void testValidationFailsForTextModificationWithEmptyTextToReplace() throws IOException {
		// set up
		final MockSlingHttpServletRequest request = this.context.request();
		request.setParameterMap(Map.of("specificPath", "/content/eaton/de/de-de/catalog", "searchText", "Eaton"));

		final MockSlingHttpServletResponse response = this.context.response();

		// exercise
		this.classUnderTest.doPost(request, response);

		final GenericErrorsModel errorsModel = OBJECT_MAPPER.readerFor(GenericErrorsModel.class).readValue(response.getOutputAsString());

		// verify
		assertEquals(new ValidationErrorModel("Text to replace", FindAndReplaceConstants.VALIDATION_NOT_EMPTY), errorsModel.getErrors().get(0),
				"Should be equal validation errors");
	}

	private TextSearchResultBean getTextSearchResultBean() {
		final TextSearchResultBean result = new TextSearchResultBean();

		result.setResults(Collections.singletonList(this.getExpectedResultItem()));

		return result;
	}

	private TextModificationResultBean getTextModificationResultBean() {
		final TextModificationResultBean result = new TextModificationResultBean();

		result.setResults(Collections.singletonList(this.getExpectedResultItem()));

		return result;
	}

	private ResultItem getExpectedResultItem() {
		return ResultItem.builder().withContentType(ContentType.PAGE).withTopContainerPath(PAGE_PATH)
				.withTopContainerTitle(PAGE_TITLE).withContainerPath(PAGE_PATH).withContainerTitle(PAGE_TITLE)
				.withPath(PAGE_CONTENT_PATH).withTitle(PAGE_TITLE).build();
	}
}
