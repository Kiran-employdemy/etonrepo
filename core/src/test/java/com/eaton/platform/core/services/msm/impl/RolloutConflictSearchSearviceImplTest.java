package com.eaton.platform.core.services.msm.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;

import java.util.List;

import javax.jcr.RepositoryException;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.io.JSONWriter;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.LiveCopy;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import com.day.cq.wcm.msm.api.LiveStatus;
import com.day.cq.wcm.msm.api.RolloutConfig;
import com.day.cq.wcm.msm.api.RolloutManager.Trigger;
import com.eaton.platform.core.services.AdminService;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

/**
 * The Class RolloutConflictSearchSearviceImplTest.
 *
 * @author Jaroslav Rassadin
 */
@ExtendWith(
		value = { AemContextExtension.class, MockitoExtension.class })
class RolloutConflictSearchSearviceImplTest {

	private final AemContext context = new AemContext(ResourceResolverType.JCR_MOCK);

	@Mock
	private AdminService adminService;

	@Mock
	private LiveRelationshipManager liveRelationshipManager;

	@InjectMocks
	private final RolloutConflictSearchSearviceImpl classUnderTest = new RolloutConflictSearchSearviceImpl();

	/**
	 * Sets up test data.
	 *
	 * @throws WCMException
	 */
	@BeforeEach
	public void setUp() throws WCMException {
		when(this.liveRelationshipManager.getLiveRelationship(any(Resource.class), anyBoolean())).thenAnswer(i -> new LiveRelationship() {

			@Override
			public void write(final JSONWriter var1) throws JSONException {
			}

			@Override
			public boolean isTrigger(final Trigger var1) {
				return false;
			}

			@Override
			public String getTargetPath() {
				return null;
			}

			@Override
			public String getSyncPath() {
				return null;
			}

			@Override
			public LiveStatus getStatus() {
				return null;
			}

			@Override
			public String getSourcePath() {
				return ((Resource) i.getArguments()[0]).getPath().replace("/de/", "/language-masters/");
			}

			@Override
			public List<RolloutConfig> getRolloutConfigs(final Trigger var1) {
				return null;
			}

			@Override
			public List<RolloutConfig> getRolloutConfigs() {
				return null;
			}

			@Override
			public LiveCopy getLiveCopy() {
				return null;
			}
		});
	}

	/**
	 * Test that conflicts found with another page.
	 *
	 * @throws WCMException
	 * @throws RepositoryException
	 */
	@Test
	@DisplayName("Conflicts found with another page")
	void testConflictsFoundWithAnotherPage() throws RepositoryException, WCMException {
		// set up
		final String pagePath = "/content/eaton/de/de-de/catalog/emobility/solid-state-flashers";

		this.context.load().json("/com/eaton/platform/core/services/msm/impl/page_msm_moved.json",
				"/content/eaton/de/de-de/catalog/emobility/solid-state-flashers_msm_moved");
		this.context.load().json("/com/eaton/platform/core/services/msm/impl/page_with_live_relationship.json", pagePath);

		final Page page = this.context.pageManager().getPage(pagePath);
		final RolloutConflictSearchResult result = new RolloutConflictSearchResult();

		// exercise
		this.classUnderTest.processPage(page, false, result);

		// verify
		assertNotNull(result.getItems().get(pagePath), "Search results contain expected page");
	}

	/**
	 * Test that conflicts found in child pages.
	 *
	 * @throws WCMException
	 * @throws RepositoryException
	 */
	@Test
	@DisplayName("Conflicts found in child pages")
	void testConflictsFoundInChildPages() throws RepositoryException, WCMException {
		// set up
		final String parentPagePath = "/content/eaton/de/de-de/catalog/emobility";
		final String pagePath = "/content/eaton/de/de-de/catalog/emobility/solid-state-flashers";

		this.context.load().json("/com/eaton/platform/core/services/msm/impl/page_parent.json", parentPagePath);

		this.context.load().json("/com/eaton/platform/core/services/msm/impl/page_msm_moved.json",
				"/content/eaton/de/de-de/catalog/emobility/solid-state-flashers_msm_moved");

		this.context.load().json("/com/eaton/platform/core/services/msm/impl/page_with_live_relationship.json", pagePath);

		final Page parentPage = this.context.pageManager().getPage(parentPagePath);
		final RolloutConflictSearchResult result = new RolloutConflictSearchResult();

		// exercise
		this.classUnderTest.processPage(parentPage, true, result);

		// verify
		assertNotNull(result.getItems().get(pagePath), "Search results contain expected page");
	}

}
