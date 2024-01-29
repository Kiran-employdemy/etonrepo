package com.eaton.platform.core.services.search.find.replace.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.components.Component;
import com.day.cq.wcm.api.components.ComponentManager;
import com.eaton.platform.core.services.AdminService;

/**
 * The Class FindReplaceComponentServiceImplTest.
 *
 * @author Jaroslav Rassadin
 */
@ExtendWith(
		value = { MockitoExtension.class })
class FindReplaceComponentServiceImplTest {

	private final static String ADVANCED_SEARCH_COMPONENT_TITLE = "Advanced Search";

	private final static String ADVANCED_SEARCH_RESOURCE_TYPE = "eaton/components/secure/advanced-search";

	@Mock
	private Resource componentInstance;

	@Mock
	private AdminService adminService;

	@Mock
	private ResourceResolver resourceResolver;

	@Mock
	private ComponentManager componentManager;

	@Mock
	private Component component;

	@InjectMocks
	private final FindReplaceComponentServiceImpl classUnderTest = new FindReplaceComponentServiceImpl();

	/**
	 * Sets up test data.
	 */
	@BeforeEach
	public void setUp() {

		when(this.resourceResolver.adaptTo(ComponentManager.class)).thenReturn(this.componentManager);
		when(this.component.getProperties()).thenReturn(new ValueMapDecorator(Map.of(JcrConstants.JCR_TITLE, ADVANCED_SEARCH_COMPONENT_TITLE)));
	}

	/**
	 * Test that component type title is returned correctly.
	 */
	@Test
	@DisplayName("Component type title is returned correctly")
	void testComponentTypeTitleReturnedCorrectly() {
		// set up
		when(this.componentInstance.getResourceResolver()).thenReturn(this.resourceResolver);
		when(this.componentManager.getComponentOfResource(this.componentInstance)).thenReturn(this.component);

		// exercise
		final String actual = this.classUnderTest.getTypeTitle(this.componentInstance);

		// verify
		assertEquals(ADVANCED_SEARCH_COMPONENT_TITLE, actual, "Should be equal title");
	}

	/**
	 * Test that component type title by resource type returned correctly.
	 */
	@Test
	@DisplayName("Component type title by resource type is returned correctly")
	void testComponentTypeTitleByResourceTypeReturnedCorrectly() {
		// set up
		when(this.adminService.getReadService()).thenReturn(this.resourceResolver);
		when(this.componentManager.getComponent(ADVANCED_SEARCH_RESOURCE_TYPE)).thenReturn(this.component);

		// exercise
		final String actual = this.classUnderTest.getTypeTitleByResourceType(ADVANCED_SEARCH_RESOURCE_TYPE);

		// verify
		assertEquals(ADVANCED_SEARCH_COMPONENT_TITLE, actual, "Should be equal title");
	}

}
