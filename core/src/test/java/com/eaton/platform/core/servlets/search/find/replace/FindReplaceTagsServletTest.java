package com.eaton.platform.core.servlets.search.find.replace;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.security.AccessControlException;
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
import com.day.cq.tagging.InvalidTagFormatException;
import com.day.cq.tagging.TagManager;
import com.eaton.platform.core.bean.search.find.replace.request.ContentType;
import com.eaton.platform.core.bean.search.find.replace.request.Mode;
import com.eaton.platform.core.bean.search.find.replace.request.TagsModificationRequestBean;
import com.eaton.platform.core.bean.search.find.replace.request.TagsSearchRequestBean;
import com.eaton.platform.core.bean.search.find.replace.response.ResultItem;
import com.eaton.platform.core.bean.search.find.replace.response.TagsModificationResultBean;
import com.eaton.platform.core.bean.search.find.replace.response.TagsSearchResultBean;
import com.eaton.platform.core.constants.FindAndReplaceConstants;
import com.eaton.platform.core.injectors.impl.RequestParameterInjectorImpl;
import com.eaton.platform.core.models.error.GenericErrorsModel;
import com.eaton.platform.core.models.error.ValidationErrorModel;
import com.eaton.platform.core.models.search.find.replace.TagsModificationRequestModel;
import com.eaton.platform.core.models.search.find.replace.TagsSearchRequestModel;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.converter.search.find.replace.TagsModificationRequestModel2BeanConverter;
import com.eaton.platform.core.services.converter.search.find.replace.TagsSearchRequestModel2BeanConverter;
import com.eaton.platform.core.services.search.find.replace.tags.FindReplaceTagsFacade;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.core.util.NonClosableResourceResolverWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

/**
 * The Class FindReplaceTagsServletTest.
 *
 * @author Jaroslav Rassadin
 */
@ExtendWith(
		value = { AemContextExtension.class, MockitoExtension.class })
class FindReplaceTagsServletTest {

	private static final String PAGE_PATH = "/content/eaton/de/de-de/catalog/emobility/test_replace";

	private static final String PAGE_CONTENT_PATH = PAGE_PATH + "/" + JcrConstants.JCR_CONTENT;

	private static final String PAGE_TITLE = "Tagged page";

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private final AemContext context = new AemContext(ResourceResolverType.RESOURCERESOLVER_MOCK);

	@Mock
	private transient AdminService adminService;

	@Mock
	private FindReplaceTagsFacade tagsFacade;

	@Mock
	private TagsSearchRequestModel2BeanConverter tagsSearchRequestModel2BeanConverter;

	@Mock
	private TagsModificationRequestModel2BeanConverter tagsModificationRequestModel2BeanConverter;

	@Mock
	private TagsSearchRequestBean searchRequestBean;

	@Mock
	private TagsModificationRequestBean modificationRequestBean;

	@InjectMocks
	private final FindReplaceTagsServlet classUnderTest = new FindReplaceTagsServlet();

	/**
	 * Sets up test data.
	 *
	 * @throws InvalidTagFormatException
	 * @throws AccessControlException
	 */
	@BeforeEach
	public void setUp() throws AccessControlException, InvalidTagFormatException {
		this.context.registerInjectActivateService(new RequestParameterInjectorImpl());
		when(this.adminService.getReadService()).thenReturn(new NonClosableResourceResolverWrapper(this.context.resourceResolver()));

		final TagManager tagManager = CommonUtil.adapt(this.context.resourceResolver(), TagManager.class);

		tagManager.createTag("eaton-secure:accounttype/agent", "Agent", "Agent");
		tagManager.createTag("eaton-secure:accounttype/distributor", "Distributor", "Distributor");
		tagManager.createTag("eaton-secure:accounttype/fleet", "Fleet", "Fleet");
	}

	/**
	 * Test that resources with tags are found.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	@DisplayName("Resources with tags are found")
	void testResourcesWithTagsAreFound() throws IOException {
		// set up
		final TagsSearchResultBean result = this.getTagsSearchResultBean();

		final MockSlingHttpServletRequest request = this.context.request();
		request.setParameterMap(Map.of("specificPath", "/content/eaton/de/de-de/catalog", "findTags",
				"accountType|eaton-secure:accounttype/agent,accountType|eaton-secure:accounttype/fleet"));

		final MockSlingHttpServletResponse response = this.context.response();

		when(this.tagsSearchRequestModel2BeanConverter.convert(any(TagsSearchRequestModel.class))).thenReturn(this.searchRequestBean);
		when(this.tagsFacade.find(this.searchRequestBean)).thenReturn(result);

		// exercise
		this.classUnderTest.doGet(request, response);

		// verify
		assertEquals(result, OBJECT_MAPPER.readerFor(TagsSearchResultBean.class).readValue(response.getOutputAsString()), "Should be equal search results");
	}

	/**
	 * Test that validation fails for tags search with empty path.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	@DisplayName("Validation fails for tags search with empty path")
	void testValidationFailsForTagsSearchWithEmptyPath() throws IOException {
		// set up
		final MockSlingHttpServletRequest request = this.context.request();
		request.setParameterMap(Map.of("findTags", "accountType|eaton-secure:accounttype/agent,accountType|eaton-secure:accounttype/fleet"));

		final MockSlingHttpServletResponse response = this.context.response();

		// exercise
		this.classUnderTest.doGet(request, response);

		final GenericErrorsModel errorsModel = OBJECT_MAPPER.readerFor(GenericErrorsModel.class).readValue(response.getOutputAsString());

		// verify
		assertEquals(new ValidationErrorModel("Path", FindAndReplaceConstants.VALIDATION_NOT_EMPTY), errorsModel.getErrors().get(0),
				"Should be equal validation errors");
	}

	/**
	 * Test that validation fails for tags search with incorrect path.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	@DisplayName("Validation fails for tags search with incorrect path")
	void testValidationFailsForTagsSearchWithIncorrectPath() throws IOException {
		// set up
		final MockSlingHttpServletRequest request = this.context.request();
		request.setParameterMap(Map.of("specificPath", "/var/commerce/products", "findTags",
				"accountType|eaton-secure:accounttype/agent,accountType|eaton-secure:accounttype/fleet"));

		final MockSlingHttpServletResponse response = this.context.response();

		// exercise
		this.classUnderTest.doGet(request, response);

		final GenericErrorsModel errorsModel = OBJECT_MAPPER.readerFor(GenericErrorsModel.class).readValue(response.getOutputAsString());

		// verify
		assertEquals(new ValidationErrorModel("Path", FindAndReplaceConstants.VALIDATION_PATH_STARTS_WITH_CONTENT), errorsModel.getErrors().get(0),
				"Should be equal validation errors");
	}

	/**
	 * Test that validation fails for tags search with empty tag values.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	@DisplayName("Validation fails for tags search with empty tag values")
	void testValidationFailsForTagsSearchWithEmptyTagValues() throws IOException {
		// set up
		final MockSlingHttpServletRequest request = this.context.request();
		request.setParameterMap(Map.of("specificPath", "/content/eaton/de/de-de/catalog"));

		final MockSlingHttpServletResponse response = this.context.response();

		// exercise
		this.classUnderTest.doGet(request, response);

		final GenericErrorsModel errorsModel = OBJECT_MAPPER.readerFor(GenericErrorsModel.class).readValue(response.getOutputAsString());

		// verify
		assertEquals(new ValidationErrorModel("Tags to find", FindAndReplaceConstants.VALIDATION_TAGS_NOT_CORRECT), errorsModel.getErrors().get(0),
				"Should be equal validation errors");
	}

	/**
	 * Test that validation fails for tags search with incorrect tag values.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	@DisplayName("Validation fails for tags search with incorrect tag values")
	void testValidationFailsForTagsSearchWithIncorrectTagValues() throws IOException {
		// set up
		final MockSlingHttpServletRequest request = this.context.request();
		request.setParameterMap(Map.of("specificPath", "/content/eaton/de/de-de/catalog", "findTags",
				"accountType|eaton-secure:accounttype/agent,accountType|eaton-secure:accounttype/incorrect"));

		final MockSlingHttpServletResponse response = this.context.response();

		// exercise
		this.classUnderTest.doGet(request, response);

		final GenericErrorsModel errorsModel = OBJECT_MAPPER.readerFor(GenericErrorsModel.class).readValue(response.getOutputAsString());

		// verify
		assertEquals(new ValidationErrorModel("Tags to find", FindAndReplaceConstants.VALIDATION_TAGS_NOT_CORRECT), errorsModel.getErrors().get(0),
				"Should be equal validation errors");
	}

	/**
	 * Test that resources with tags are modified.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	@DisplayName("Resources with tags are modified")
	void testResourcesWithTagsAreModified() throws IOException {
		// set up
		final TagsModificationResultBean result = this.getTagsModificationResultBean();

		final MockSlingHttpServletRequest request = this.context.request();
		request.setParameterMap(Map.of("specificPath", "/content/eaton/de/de-de/catalog", "findTags",
				"accountType|eaton-secure:accounttype/agent,accountType|eaton-secure:accounttype/distributor", "newTags",
				"accountType|eaton-secure:accounttype/fleet", "mode", "add"));

		final MockSlingHttpServletResponse response = this.context.response();

		when(this.tagsModificationRequestModel2BeanConverter.convert(any(TagsModificationRequestModel.class))).thenReturn(this.modificationRequestBean);
		when(this.modificationRequestBean.getMode()).thenReturn(Mode.ADD);
		when(this.tagsFacade.add(this.modificationRequestBean)).thenReturn(result);

		// exercise
		this.classUnderTest.doPost(request, response);

		// verify
		assertEquals(result, OBJECT_MAPPER.readerFor(TagsModificationResultBean.class).readValue(response.getOutputAsString()),
				"Should be equal modification results");
	}

	/**
	 * Test that validation fails for tags modification with incorrect path.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	@DisplayName("Validation fails for tags modification with incorrect path")
	void testValidationFailsForTagsModificationWithIncorrectPath() throws IOException {
		// set up
		final MockSlingHttpServletRequest request = this.context.request();
		request.setParameterMap(Map.of("specificPath", "/var/commerce/products", "findTags",
				"accountType|eaton-secure:accounttype/agent,accountType|eaton-secure:accounttype/fleet",
				"newTags", "accountType|eaton-secure:accounttype/distributor"));

		final MockSlingHttpServletResponse response = this.context.response();

		// exercise
		this.classUnderTest.doGet(request, response);

		final GenericErrorsModel errorsModel = OBJECT_MAPPER.readerFor(GenericErrorsModel.class).readValue(response.getOutputAsString());

		// verify
		assertEquals(new ValidationErrorModel("Path", FindAndReplaceConstants.VALIDATION_PATH_STARTS_WITH_CONTENT), errorsModel.getErrors().get(0),
				"Should be equal validation errors");
	}

	/**
	 * Test that validation fails for tags modification with empty tags for modification.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	@DisplayName("Validation fails for tags modification with empty tags for modification")
	void testValidationFailsForTagsModificationWithEmptyTagsForModification() throws IOException {
		// set up
		final MockSlingHttpServletRequest request = this.context.request();
		request.setParameterMap(Map.of("specificPath", "/content/eaton/de/de-de/catalog", "findTags",
				"accountType|eaton-secure:accounttype/agent,accountType|eaton-secure:accounttype/distributor", "mode", "add"));

		final MockSlingHttpServletResponse response = this.context.response();

		// exercise
		this.classUnderTest.doPost(request, response);

		final GenericErrorsModel errorsModel = OBJECT_MAPPER.readerFor(GenericErrorsModel.class).readValue(response.getOutputAsString());

		// verify
		assertEquals(new ValidationErrorModel("Tags for modification", FindAndReplaceConstants.VALIDATION_TAGS_NOT_CORRECT), errorsModel.getErrors().get(0),
				"Should be equal validation errors");
	}

	/**
	 * Test that validation fails for tags modification with incorrect tags for modification.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	@DisplayName("Validation fails for tags modification with incorrect tags for modification")
	void testValidationFailsForTagsModificationWithIncorrectTagsForModification() throws IOException {
		// set up
		final MockSlingHttpServletRequest request = this.context.request();
		request.setParameterMap(Map.of("specificPath", "/content/eaton/de/de-de/catalog", "findTags",
				"accountType|eaton-secure:accounttype/agent,accountType|eaton-secure:accounttype/distributor",
				"newTags", "accountType|eaton-secure:accounttype/incorrect", "mode", "add"));

		final MockSlingHttpServletResponse response = this.context.response();

		// exercise
		this.classUnderTest.doPost(request, response);

		final GenericErrorsModel errorsModel = OBJECT_MAPPER.readerFor(GenericErrorsModel.class).readValue(response.getOutputAsString());

		// verify
		assertEquals(new ValidationErrorModel("Tags for modification", FindAndReplaceConstants.VALIDATION_TAGS_NOT_CORRECT), errorsModel.getErrors().get(0),
				"Should be equal validation errors");
	}

	private TagsSearchResultBean getTagsSearchResultBean() {
		final TagsSearchResultBean result = new TagsSearchResultBean();

		result.setResults(Collections.singletonList(this.getExpectedResultItem()));

		return result;
	}

	private TagsModificationResultBean getTagsModificationResultBean() {
		final TagsModificationResultBean result = new TagsModificationResultBean();

		result.setResults(Collections.singletonList(this.getExpectedResultItem()));

		return result;
	}

	private ResultItem getExpectedResultItem() {
		return ResultItem.builder().withContentType(ContentType.PAGE).withTopContainerPath(PAGE_PATH)
				.withTopContainerTitle(PAGE_TITLE).withContainerPath(PAGE_PATH).withContainerTitle(PAGE_TITLE)
				.withPath(PAGE_CONTENT_PATH).withTitle(PAGE_TITLE).build();
	}

}
