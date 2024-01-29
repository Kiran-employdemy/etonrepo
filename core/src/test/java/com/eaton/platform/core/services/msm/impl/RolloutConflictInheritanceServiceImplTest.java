package com.eaton.platform.core.services.msm.impl;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.jackrabbit.commons.cnd.ParseException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.util.MockRepositoryUtils;
import com.eaton.platform.core.util.NonClosableResourceResolverWrapper;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

/**
 * The Class RolloutConflictInheritanceServiceImplTest.
 *
 * @author Jaroslav Rassadin
 */
@ExtendWith(
		value = { AemContextExtension.class, MockitoExtension.class })
class RolloutConflictInheritanceServiceImplTest {

	private final static String BLUEPRINT_PAGE_PATH = "/content/eaton/language-masters/de-de/catalog/emobility/page_msm_moved";

	private final static String PARENT_PAGE_PATH = "/content/eaton/de/de-de/catalog/emobility";

	private final static String PAGE_PAGE_PATH = "/content/eaton/de/de-de/catalog/emobility/solid-state-flashers";

	private final static String MSM_MOVED_PAGE_PATH = "/content/eaton/de/de-de/catalog/emobility/solid-state-flashers_msm_moved";

	private final static String MSM_MOVED_COMPONENTS_BLUEPRINT_PAGE_PATH = "/content/eaton/language-masters/de-de/catalog/emobility/battery-isolators";

	private final static String MSM_MOVED_COMPONENTS_BLUEPRINT_RESOURCE_PATH = MSM_MOVED_COMPONENTS_BLUEPRINT_PAGE_PATH + "/jcr:content/root/header";

	private final static String MSM_MOVED_COMPONENTS_PAGE_PATH = "/content/eaton/de/de-de/catalog/emobility/battery-isolators";

	private final static String MSM_MOVED_RESOURCE_PATH = MSM_MOVED_COMPONENTS_PAGE_PATH + "/jcr:content/root/header_msm_moved";

	private final AemContext context = new AemContext(ResourceResolverType.JCR_OAK);

	@Mock
	private AdminService adminService;

	@Mock
	private LiveRelationshipManager liveRelationshipManager;

	@Mock
	private LiveRelationship liveRelationship;

	@Mock
	private Replicator replicator;

	@InjectMocks
	private final RolloutConflictInheritanceServiceImpl classUnderTest = new RolloutConflictInheritanceServiceImpl();

	/**
	 * Sets up test data.
	 *
	 * @throws WCMException
	 *             the WCM exception
	 */
	@BeforeEach
	public void setUp() throws WCMException {
		// need to register additional node types so that properties like jcr:mixinTypes are available with ResourceResolverType.JCR_OAK
		MockRepositoryUtils.registerNodeTypes(this.context.resourceResolver().adaptTo(Session.class));

		when(this.liveRelationshipManager.getLiveRelationship(any(Resource.class), anyBoolean())).thenReturn(this.liveRelationship);
	}

	/**
	 * Test that inheritance enabling check for page works correctly.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	@DisplayName("Inheritance check for page")
	void testCanEnableInheritanceForPage() throws IOException {
		// set up
		this.setUpForPage();
		when(this.adminService.getReadService()).thenReturn(new NonClosableResourceResolverWrapper(this.context.resourceResolver()));

		// exercise
		final boolean actualResult = this.classUnderTest.canEnableInheritanceForPage(MSM_MOVED_PAGE_PATH);

		// verify
		assertTrue("Method returned true", actualResult);
	}

	/**
	 * Test that inheritance enabling check for resource works correctly.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ParseException
	 * @throws RepositoryException
	 */
	@Test
	@DisplayName("Inheritance check for resource")
	void testCanEnableInheritanceForResorce() throws IOException, RepositoryException, ParseException {
		// set up
		this.setUpForResource();
		when(this.adminService.getReadService()).thenReturn(new NonClosableResourceResolverWrapper(this.context.resourceResolver()));

		// exercise
		final boolean actualResult = this.classUnderTest.canEnableInheritanceForResource(MSM_MOVED_RESOURCE_PATH);

		// verify
		assertTrue("Method returned true", actualResult);
	}

	/**
	 * Test enable inheritance for page.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	@DisplayName("Inheritance enabling for page")
	void testEnableInheritanceForPage() throws IOException {
		// set up
		this.setUpForPage();
		when(this.adminService.getWriteService()).thenReturn(new NonClosableResourceResolverWrapper(this.context.resourceResolver()));

		// exercise
		final ConflictResolutionResult actualResult = this.classUnderTest.enableInheritanceForPage(MSM_MOVED_PAGE_PATH, true);

		// verify
		assertEquals(ConflictResolutionStatus.FIXED, actualResult.getStatus(),
				String.format("Conflict resolution result is %s", ConflictResolutionStatus.FIXED));
	}

	/**
	 * Test enable inheritance for resource.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ParseException
	 * @throws RepositoryException
	 */
	@Test
	@DisplayName("Inheritance enabling for resource")
	void testEnableInheritanceForResorce() throws IOException, RepositoryException, ParseException {
		// set up
		this.setUpForResource();
		when(this.adminService.getWriteService()).thenReturn(new NonClosableResourceResolverWrapper(this.context.resourceResolver()));

		// exercise
		final ConflictResolutionResult actualResult = this.classUnderTest.enableInheritanceForResource(MSM_MOVED_RESOURCE_PATH, true);

		// verify
		assertEquals(ConflictResolutionStatus.FIXED, actualResult.getStatus(),
				String.format("Conflict resolution result is %s", ConflictResolutionStatus.FIXED));
	}

	private void setUpForPage() {
		this.context.load().json("/com/eaton/platform/core/services/msm/impl/page_blueprint.json", BLUEPRINT_PAGE_PATH);
		this.context.load().json("/com/eaton/platform/core/services/msm/impl/page_parent.json", PARENT_PAGE_PATH);
		this.context.load().json("/com/eaton/platform/core/services/msm/impl/page_msm_moved.json", MSM_MOVED_PAGE_PATH);
		this.context.load().json("/com/eaton/platform/core/services/msm/impl/page_with_live_relationship.json", PAGE_PAGE_PATH);

		when(this.liveRelationship.getSourcePath()).thenReturn(BLUEPRINT_PAGE_PATH);
	}

	private void setUpForResource() throws RepositoryException, ParseException, IOException {
		this.context.load().json("/com/eaton/platform/core/services/msm/impl/page_blueprint_with_msm_moved_components.json",
				MSM_MOVED_COMPONENTS_BLUEPRINT_PAGE_PATH);
		this.context.load().json("/com/eaton/platform/core/services/msm/impl/page_with_msm_moved_components.json", MSM_MOVED_COMPONENTS_PAGE_PATH);

		when(this.liveRelationship.getSourcePath()).thenReturn(MSM_MOVED_COMPONENTS_BLUEPRINT_RESOURCE_PATH);
	}
}
