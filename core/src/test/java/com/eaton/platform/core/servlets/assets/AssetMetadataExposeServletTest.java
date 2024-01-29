package com.eaton.platform.core.servlets.assets;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
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

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.AssetMetadataExposeService;
import com.eaton.platform.core.services.CountryLangCodeConfigService;
import com.eaton.platform.core.util.NonClosableResourceResolverWrapper;
import com.eaton.platform.integration.auth.constants.SecureConstants;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith({ MockitoExtension.class, AemContextExtension.class })
class AssetMetadataExposeServletTest {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	public final AemContext aemContext = new AemContext(ResourceResolverType.RESOURCERESOLVER_MOCK);

	@InjectMocks
	private final AssetMetadataExposeServlet assetMetadataExposeServlet = new AssetMetadataExposeServlet();

	@Mock
	private AdminService adminService;

	@Mock
	private SlingHttpServletRequest slingHttpServletRequest;

	@Mock
	private SlingHttpServletResponse slingHttpServletResponse;

	@Mock
	private AssetMetadataExposeService assetMetadataExposeService;

	@Mock
	private CountryLangCodeConfigService countryLangCodeConfigService;

	@Mock
	private PrintWriter printWriter;

	/**
	 * Sets up test data.
	 */
	@BeforeEach
	public void setUp() {
		when(this.adminService.getReadService()).thenReturn(new NonClosableResourceResolverWrapper(this.aemContext.resourceResolver()));
	}

	@Test
	@DisplayName("UUID is not present for asset")
	void testUUIDIsNotPresentForAsset() throws IOException {
		// set up
		this.aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("assetWithoutUuid.json")),
				"/content/dam/eaton/products/backup-power-ups-surge-it-power-distribution/black.jpg");
		final Resource resource = this.aemContext.resourceResolver()
				.getResource("/content/dam/eaton/products/backup-power-ups-surge-it-power-distribution/black.jpg");
		when(this.slingHttpServletRequest.getResource()).thenReturn(resource);
		when(this.slingHttpServletRequest.getResourceResolver()).thenReturn(this.aemContext.resourceResolver());
		when(this.slingHttpServletResponse.getWriter()).thenReturn(this.printWriter);
		when(this.assetMetadataExposeService.getIncludeAssetProperties()).thenReturn(new String[] { "dc:format", "dam:size", "thumbnail", "application-id" });

		// exercise
		this.assetMetadataExposeServlet.doGet(this.slingHttpServletRequest, this.slingHttpServletResponse);

		// verify
		final String jsonString = IOUtils
				.toString(new InputStreamReader(Objects.requireNonNull(this.getClass().getResourceAsStream("custommetadataWithoutUUID.json"))));
		verify(this.printWriter).write(jsonString);
	}

	@Test
	@DisplayName("UUID is present for asset")
	void testUUIDIsPresentForAsset() throws IOException {
		// set up
		this.aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("assetWithMetadata.json")),
				"/content/dam/eaton/products/backup-power-ups-surge-it-power-distribution/black.jpg");
		final Resource resource = this.aemContext.resourceResolver()
				.getResource("/content/dam/eaton/products/backup-power-ups-surge-it-power-distribution/black.jpg");
		when(this.slingHttpServletRequest.getResource()).thenReturn(resource);
		when(this.slingHttpServletRequest.getResourceResolver()).thenReturn(this.aemContext.resourceResolver());
		when(this.slingHttpServletResponse.getWriter()).thenReturn(this.printWriter);
		when(this.assetMetadataExposeService.getIncludeAssetProperties())
				.thenReturn(new String[] { "dc:format", "dam:size", "thumbnail", "uuid", "application-id" });

		// exercise
		this.assetMetadataExposeServlet.doGet(this.slingHttpServletRequest, this.slingHttpServletResponse);

		// verify
		final String jsonString = IOUtils.toString(new InputStreamReader(Objects.requireNonNull(this.getClass().getResourceAsStream("custommetadata.json"))));
		verify(this.printWriter).write(jsonString);
	}

	@Test
	@SuppressWarnings("unchecked")
	@DisplayName("Partner programm and tier level incuded in metadata")
	void testPartnerProgrammAndTierLevelIncudedInMetadata() throws IOException {
		// set up
		final String resourcePath = "/content/dam/eaton/secure/channel-marketing/digipacks/pexeva.zip";

		this.aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("partnerProgrammTypeTags.json")),
				"/content/cq:tags/eaton-secure");
		this.aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("assetWithPartnerProgrammAndTierLevel.json")), resourcePath);

		final Resource resource = this.aemContext.resourceResolver().getResource(resourcePath);

		final MockSlingHttpServletRequest request = this.aemContext.request();
		request.setResource(resource);

		final MockSlingHttpServletResponse response = this.aemContext.response();

		when(this.assetMetadataExposeService.getIncludeAssetProperties()).thenReturn(new String[] { CommonConstants.CQ_TAGS });
		when(this.assetMetadataExposeService.getIncludeSecureAssetProperties())
				.thenReturn(new String[] { SecureConstants.XMP_EATON_PROP_PARTNER_PROGRAMME_AND_TIER_LEVEL_TYPE_TAG });

		// exercise
		this.assetMetadataExposeServlet.doGet(request, response);

		// verify
		final Map<String, Object> metadataMap = OBJECT_MAPPER.readerFor(Map.class).<Map<String, Object>> readValue(response.getOutputAsString());

		final List<String> expectedTags = Arrays.asList("eaton-secure:partner-programme-type/it-channel-program/premium",
				"eaton-secure:partner-programme-type/it-channel-program/authorized", "eaton-secure:partner-programme-type/it-channel-program/registered");
		final List<String> expectedPartnerProgramAndTierLevel = Arrays.asList("it-channel-program_registered", "it-channel-program_authorized",
				"it-channel-program_premium");

		assertTrue(CollectionUtils.isEqualCollection(((List<String>) metadataMap.get(CommonConstants.CQ_TAGS)), expectedTags), "Should be equal collection");
		assertTrue(CollectionUtils.isEqualCollection(((List<String>) metadataMap.get(CommonConstants.SECURE_PROP_NAME_PARTNER_PROGRAMME_AND_TIER_LEVEL_TYPE)),
				expectedPartnerProgramAndTierLevel), "Should be equal collection");
	}

	@Test
	@DisplayName("Exclude country list incuded in metadata")
	void testExcludeCountryListIncudedInMetadata() throws IOException {
		// set up
		final String resourcePath = "/content/dam/eaton/secure/channel-marketing/digipacks/pexeva.zip";

		this.aemContext.load().json(Objects.requireNonNull(this.getClass().getResourceAsStream("assetWithPartnerProgrammAndTierLevel.json")), resourcePath);

		final Resource resource = this.aemContext.resourceResolver().getResource(resourcePath);

		final MockSlingHttpServletRequest request = this.aemContext.request();
		request.setResource(resource);

		final MockSlingHttpServletResponse response = this.aemContext.response();

		when(this.countryLangCodeConfigService.getExcludeCountryList()).thenReturn(new String[] { "IR", "SY", "CU", "SD", "KP" });

		// exercise
		this.assetMetadataExposeServlet.activate();
		this.assetMetadataExposeServlet.doGet(request, response);

		// verify
		final Map<String, Object> metadataMap = OBJECT_MAPPER.readerFor(Map.class).<Map<String, Object>> readValue(response.getOutputAsString());

		assertEquals("Should be equal county codes string", "IR||SY||CU||SD||KP", metadataMap.get(CommonConstants.EXCLUDE_COUNTRY_LIST));
	}

}
