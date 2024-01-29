package com.eaton.platform.core.models.search.find.replace.ui;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eaton.platform.core.bean.search.find.replace.FindReplacePropertyBean;
import com.eaton.platform.core.bean.search.find.replace.FindReplaceResourceTypeBean;
import com.eaton.platform.core.bean.search.find.replace.FindReplaceTagBean;
import com.eaton.platform.core.bean.search.find.replace.request.ContentType;
import com.eaton.platform.core.services.search.find.replace.FindReplaceComponentService;
import com.eaton.platform.core.services.search.find.replace.FindReplaceConfigService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class FindReplaceTagsModelTest.
 *
 * @author Jaroslav Rassadin
 */
@ExtendWith(
		value = { MockitoExtension.class })
class FindReplaceTagsModelTest {

	private final static String ACCOUNT_TYPE_PROPERTY_NAME = "accountType";

	@Mock
	private FindReplaceConfigService configService;

	@Mock
	private FindReplaceComponentService componentService;

	@InjectMocks
	private final FindReplaceTagsModel classUnderTest = new FindReplaceTagsModel();

	/**
	 * Test that mode select items are returned correctly.
	 */
	@Test
	@DisplayName("Mode select items are returned correctly")
	void testModeSelectItemsAreReturnedCorrectly() {
		// exercise
		final List<FindReplaceSelectItemModel> actualList = this.classUnderTest.getModeSelectItems();

		// verify
		assertThat("Should be equal mode lists", actualList, contains(
				new FindReplaceSelectItemModel(Collections.emptyMap(), "Add", "add"),
				new FindReplaceSelectItemModel(Collections.emptyMap(), "Delete", "delete"),
				new FindReplaceSelectItemModel(Collections.emptyMap(), "Replace", "replace")));
	}

	/**
	 * Test that tag select items are returned correctly.
	 */
	@Test
	@DisplayName("Tag select items are returned correctly")
	void testTagSelectItemsAreReturnedCorrectly() {
		// set up
		when(this.configService.getTags()).thenReturn(Arrays.asList(
				new FindReplaceTagBean(ACCOUNT_TYPE_PROPERTY_NAME, true, true, "/content/cq:tags/eaton-secure/accounttype", "Account Type"),
				new FindReplaceTagBean("productCategories", true, true, "/content/cq:tags/eaton-secure/product-category", "Product Categories"),
				new FindReplaceTagBean("facet", false, false, "/content/cq:tags", "Whitelist facets (nested multifield)")));

		// exercise
		final List<FindReplaceSelectItemModel> actualList = this.classUnderTest.getTagSelectItems();

		// verify
		assertThat("Should be equal tags lists", actualList, contains(
				new FindReplaceSelectItemModel(Map.of("data-multiple", "true", "data-own", "true", "data-path", "/content/cq:tags/eaton-secure/accounttype"),
						"Account Type", ACCOUNT_TYPE_PROPERTY_NAME),
				new FindReplaceSelectItemModel(Map.of("data-multiple", "true", "data-own", "true", "data-path",
						"/content/cq:tags/eaton-secure/product-category"),
						"Product Categories", "productCategories"),
				new FindReplaceSelectItemModel(Map.of("data-multiple", "false", "data-own", "false", "data-path", "/content/cq:tags"),
						"Whitelist facets (nested multifield)", "facet")));
	}

	/**
	 * Test that tag help items are returned correctly.
	 */
	@Test
	@DisplayName("Tag help items are returned correctly")
	void testTagHelpItemsAreReturnedCorrectly() {
		// set up
		this.initMocksForTagHelp();

		// exercise
		final List<FindReplaceTagsHelpModel> actualList = this.classUnderTest.getTagHelpItems();

		Map.of("ASSET", Collections.emptyList(), "PAGE", Collections.emptyList(), "COMPONENT", List.of("Advanced Search", "Section Navigation",
				"Vertical Link List"));

		// verify
		assertThat("Should be equal tag help items list", actualList, contains(new FindReplaceTagsHelpModel(true, true, ACCOUNT_TYPE_PROPERTY_NAME, Map.of(
				"ASSET", Collections.emptyList(), "PAGE", Collections.emptyList(), "COMPONENT", List.of("Advanced Search", "Section Navigation",
						"Vertical Link List")))));
	}

	/**
	 * Test that tag help items JSON are returned correctly.
	 *
	 * @throws JsonProcessingException
	 */
	@Test
	@DisplayName("Tag help items JSON are returned correctly")
	void testTagHelpItemsJsonAreReturnedCorrectly() throws JsonProcessingException {
		// set up
		this.initMocksForTagHelp();

		// exercise
		final List<FindReplaceTagsHelpModel> actualList = new ObjectMapper().readerFor(new TypeReference<List<FindReplaceTagsHelpModel>>() {
		}).readValue(this.classUnderTest.getTagHelpItemsJson());

		// verify
		assertThat("Should be equal tag help items list", actualList, contains(new FindReplaceTagsHelpModel(true, true, ACCOUNT_TYPE_PROPERTY_NAME, Map.of(
				"ASSET", Collections.emptyList(), "PAGE", Collections.emptyList(), "COMPONENT", List.of("Advanced Search", "Section Navigation",
						"Vertical Link List")))));
	}

	private void initMocksForTagHelp() {
		when(this.configService.getTags()).thenReturn(Arrays.asList(
				new FindReplaceTagBean(ACCOUNT_TYPE_PROPERTY_NAME, true, true, "/content/cq:tags/eaton-secure/accounttype", "Account Type")));

		when(this.configService.getTagPropertiesMap(ContentType.ASSET)).thenReturn(Map.of(ACCOUNT_TYPE_PROPERTY_NAME,
				new FindReplacePropertyBean(true, ACCOUNT_TYPE_PROPERTY_NAME)));
		when(this.configService.getTagPropertiesMap(ContentType.PAGE)).thenReturn(Map.of(ACCOUNT_TYPE_PROPERTY_NAME,
				new FindReplacePropertyBean(true, ACCOUNT_TYPE_PROPERTY_NAME)));
		when(this.configService.getTagPropertiesMap(ContentType.COMPONENT)).thenReturn(Map.of(ACCOUNT_TYPE_PROPERTY_NAME,
				new FindReplacePropertyBean(true, ACCOUNT_TYPE_PROPERTY_NAME, Set.of(
						new FindReplaceResourceTypeBean("eaton/components/secure/advanced-search"),
						new FindReplaceResourceTypeBean("eaton/components/general/section-navigation"),
						new FindReplaceResourceTypeBean("eaton/components/general/vertical-link-list")))));

		when(this.componentService.getTypeTitleByResourceType("eaton/components/secure/advanced-search")).thenReturn("Advanced Search");
		when(this.componentService.getTypeTitleByResourceType("eaton/components/general/section-navigation")).thenReturn("Section Navigation");
		when(this.componentService.getTypeTitleByResourceType("eaton/components/general/vertical-link-list")).thenReturn("Vertical Link List");
	}

}
