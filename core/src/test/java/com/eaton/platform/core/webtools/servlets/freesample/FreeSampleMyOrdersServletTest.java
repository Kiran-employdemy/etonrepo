package com.eaton.platform.core.webtools.servlets.freesample;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.servlet.ServletException;

import com.eaton.platform.core.models.secure.SecureXFModel;
import com.eaton.platform.core.services.CountryLangCodeConfigService;
import com.eaton.platform.integration.auth.models.UserProfile;
import com.eaton.platform.integration.auth.models.impl.UserProfileImpl;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextBuilder;
import io.wcm.testing.mock.aem.junit5.AemContextCallback;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.servlethelpers.MockSlingHttpServletRequest;
import org.apache.sling.servlethelpers.MockSlingHttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.eaton.platform.integration.apigee.services.ApigeeService;
import com.eaton.platform.integration.auth.models.AuthenticationToken;
import com.eaton.platform.integration.auth.services.AuthorizationService;




@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class FreeSampleMyOrdersServletTest {

	private static final String ENDPOINT = "/order-center/order-management/v1/orders";

	@Mock
	ApigeeService apigeeService;

	@Mock
	AuthorizationService authorizationService;

	@Mock
	AuthenticationToken authenticationToken;

	@Mock
	ResourceResolver resourceResolver;

	@Mock
	private PrintWriter writer;

	@Rule
	public final AemContext aemContext = new AemContext();

	@InjectMocks
	FreeSampleMyOrdersServlet freeSampleMyOrdersServlet = new FreeSampleMyOrdersServlet();

	MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(resourceResolver);

	MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();

	private SlingHttpServletRequest slingHttpServletRequest;

	@BeforeEach
	void setup(){
		aemContext.registerService(AuthorizationService.class,authorizationService);
		slingHttpServletRequest = aemContext.request();
	}

	@Test
	public void testResponseWithNOEndpoint() throws ServletException, IOException, JSONException {

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("orderStatus", "submitted");
		request.setParameterMap(parameters);
		freeSampleMyOrdersServlet.doGet(request, response);
		Assertions.assertTrue(StringUtils.isBlank(response.getOutputAsString()));

	}

	@Test
	public void testResponseWithOrderIDParam() throws ServletException, IOException, JSONException {
		String orderId = "o154030145";
		InputStream orderRespStream = this.getClass().getResourceAsStream("MyFreeSampleOrder.json");
		String expectedApigeeResponse = IOUtils.toString(orderRespStream, StandardCharsets.UTF_8);
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("orderId", "o154030145");
		parameters.put("orderStatus", "submitted");
		parameters.put("endpoint", "/order-center/order-management/v1/orders");
		parameters.put("orderType", "Sample");

		parameters.put("offset", "0");
		parameters.put("limit", "10");

		request.setParameterMap(parameters);

		when(apigeeService.getFreeSampleOrders(ENDPOINT, FreeSampleMyOrderFixtures.createFixtureWithOrderID()))
				.thenReturn(expectedApigeeResponse);
		freeSampleMyOrdersServlet.doGet(request, response);
		String respStr = response.getOutputAsString();
		Assertions.assertTrue(StringUtils.isNotBlank(respStr));
		JSONObject responseJSON = new JSONObject(respStr);
		Assertions.assertTrue(responseJSON.has("orders"));
		JSONObject firstOrder = new JSONObject(responseJSON.getJSONArray("orders").getString(0));
		Assertions.assertEquals(orderId, firstOrder.get("orderId"));

	}

	@Test
	public void testResponseWithEmailIDParam() throws ServletException, IOException, JSONException {
		String emailId = "kurtis@beh.com";
		InputStream orderRespStream = this.getClass().getResourceAsStream("MyFreeSampleOrder.json");
		String expectedApigeeResponse = IOUtils.toString(orderRespStream, StandardCharsets.UTF_8);
		Map<String, Object> parameters = new HashMap<>();
		UserProfile userProfile = new UserProfileImpl();
		userProfile.setEmail("kurtis@beh.com");
		when(authorizationService.isAuthenticated(request)).thenReturn(Boolean.TRUE);
		when(authorizationService.getTokenFromSlingRequest(request)).thenReturn(authenticationToken);
		when(authorizationService.getTokenFromSlingRequest(request).getUserProfile()).thenReturn(userProfile);
		aemContext.request().setAttribute("authenticationToken", authenticationToken);
		parameters.put("orderStatus", "submitted");
		parameters.put("endpoint", "/order-center/order-management/v1/orders");
		parameters.put("orderType", "Sample");
		parameters.put("email",authenticationToken.getUserProfile().getEmail());
		parameters.put("offset", "0");
		parameters.put("limit", "10");

		request.setParameterMap(parameters);

		when(apigeeService.getFreeSampleOrders(ENDPOINT, FreeSampleMyOrderFixtures.createFixtureWithEmailID()))
				.thenReturn(expectedApigeeResponse);
		freeSampleMyOrdersServlet.doGet(request, response);
		String respStr = response.getOutputAsString();
		Assertions.assertTrue(StringUtils.isNotBlank(respStr));
		JSONObject responseJSON = new JSONObject(respStr);
		Assertions.assertTrue(responseJSON.has("orders"));
		JSONObject firstOrder = new JSONObject(responseJSON.getJSONArray("orders").getString(0));
		Assertions.assertEquals(emailId, firstOrder.get("userName"));

	}

}
