package com.eaton.platform.core.servlets.msm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eaton.platform.core.services.msm.RolloutConflictInheritanceService;
import com.eaton.platform.core.services.msm.impl.ConflictResolutionResult;
import com.eaton.platform.core.services.msm.impl.ConflictResolutionStatus;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

/**
 * The Class RolloutConflictInheritanceServletTest.
 *
 * @author Jaroslav Rassadin
 */
@ExtendWith(
		value = { AemContextExtension.class, MockitoExtension.class })
class RolloutConflictInheritanceServletTest {

	private final static String MSM_MOVED_PAGE_PATH = "/content/eaton/de/de-de/catalog/emobility/page_msm_moved";

	private final static String NEW_PAGE_PATH = "/content/eaton/de/de-de/catalog/emobility/page";

	private final static String MSM_MOVED_RESOURCE_PATH = "/content/eaton/de/de-de/catalog/emobility/page_msm_moved/jcr:content/header_msm_moved";

	private final static String NEW_RESOURCE_PATH = "/content/eaton/de/de-de/catalog/emobility/page/jcr:content/header_msm_moved";

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private final AemContext context = new AemContext(ResourceResolverType.JCR_MOCK);

	@Mock
	private RolloutConflictInheritanceService inheritanceService;

	@InjectMocks
	private final RolloutConflictInheritanceServlet classUnderTest = new RolloutConflictInheritanceServlet();

	/**
	 * Test enable inheritance for page is called.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	@DisplayName("Enable inheritance for page is called")
	void testEnableInheritanceForPageIsCalled() throws IOException {
		// set up
		final MockSlingHttpServletRequest request = this.context.request();

		final Resource resource = this.context.create().page(MSM_MOVED_PAGE_PATH).adaptTo(Resource.class);
		request.setResource(resource);

		final MockSlingHttpServletResponse response = this.context.response();

		when(this.inheritanceService.enableInheritanceForPage(MSM_MOVED_PAGE_PATH, true))
				.thenReturn(new ConflictResolutionResult(MSM_MOVED_PAGE_PATH, NEW_PAGE_PATH, ConflictResolutionStatus.FIXED));

		// exercise
		this.classUnderTest.doPost(request, response);

		// verify
		verify(this.inheritanceService, times(1)).enableInheritanceForPage(MSM_MOVED_PAGE_PATH, true);
		assertEquals(ConflictResolutionStatus.FIXED.toString(),
				OBJECT_MAPPER.readerFor(Map.class).<Map<String, String>> readValue(response.getOutputAsString()).get("status"),
				String.format("Conflict resolution result is %s", ConflictResolutionStatus.FIXED));
	}

	/**
	 * Test enable inheritance for resource is called.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	@DisplayName("Enable inheritance for resource is called")
	void testEnableInheritanceForResourceIsCalled() throws IOException {
		// set up
		final MockSlingHttpServletRequest request = this.context.request();

		final Resource resource = this.context.create().resource(MSM_MOVED_RESOURCE_PATH);
		request.setResource(resource);

		final MockSlingHttpServletResponse response = this.context.response();

		when(this.inheritanceService.enableInheritanceForResource(MSM_MOVED_RESOURCE_PATH, true))
				.thenReturn(new ConflictResolutionResult(MSM_MOVED_RESOURCE_PATH, NEW_RESOURCE_PATH, ConflictResolutionStatus.FIXED));

		// exercise
		this.classUnderTest.doPost(request, response);

		// verify
		verify(this.inheritanceService, times(1)).enableInheritanceForResource(MSM_MOVED_RESOURCE_PATH, true);
		assertEquals(ConflictResolutionStatus.FIXED.toString(),
				OBJECT_MAPPER.readerFor(Map.class).<Map<String, String>> readValue(response.getOutputAsString()).get("status"),
				String.format("Conflict resolution result is %s", ConflictResolutionStatus.FIXED));
	}

	/**
	 * Test not valid input is handled.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	@DisplayName("Not valid input is handled")
	void testNotValidInputIsHandled() throws IOException {
		// set up
		final MockSlingHttpServletRequest request = this.context.request();

		final Resource resource = this.context.create().resource(MSM_MOVED_RESOURCE_PATH);
		request.setResource(resource);

		final MockSlingHttpServletResponse response = this.context.response();

		when(this.inheritanceService.enableInheritanceForResource(MSM_MOVED_RESOURCE_PATH, true))
				.thenReturn(new ConflictResolutionResult(MSM_MOVED_RESOURCE_PATH, NEW_RESOURCE_PATH, ConflictResolutionStatus.NOT_POSSIBLE));

		// exercise
		this.classUnderTest.doPost(request, response);

		// verify
		assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus(),
				String.format("Response status is %s", HttpServletResponse.SC_BAD_REQUEST));
		assertEquals(ConflictResolutionStatus.NOT_POSSIBLE.toString(),
				OBJECT_MAPPER.readerFor(Map.class).<Map<String, String>> readValue(response.getOutputAsString()).get("status"),
				String.format("Conflict resolution result is %s", ConflictResolutionStatus.NOT_POSSIBLE));
	}

	/**
	 * Test exception is handled.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	@DisplayName("Exception is handled")
	void testExceptionIsHandled() throws IOException {
		// set up
		final MockSlingHttpServletRequest request = this.context.request();

		final Resource resource = this.context.create().resource(MSM_MOVED_RESOURCE_PATH);
		request.setResource(resource);

		final MockSlingHttpServletResponse response = this.context.response();

		when(this.inheritanceService.enableInheritanceForResource(MSM_MOVED_RESOURCE_PATH, true)).thenThrow(new RuntimeException());

		// exercise
		this.classUnderTest.doPost(request, response);

		// verify
		assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response.getStatus(),
				String.format("Response status is %s", HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
		assertNotNull(OBJECT_MAPPER.readerFor(Map.class).<Map<String, String>> readValue(response.getOutputAsString()).get("msg"), "Error message is not null");
	}

}
