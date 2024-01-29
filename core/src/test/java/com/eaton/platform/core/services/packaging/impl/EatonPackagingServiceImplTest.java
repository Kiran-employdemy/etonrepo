package com.eaton.platform.core.services.packaging.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eaton.platform.core.bean.packaging.PackageBean;
import com.eaton.platform.core.bean.packaging.PackagePathFilter;
import com.eaton.platform.core.bean.packaging.PackageRequestBean;
import com.eaton.platform.core.util.NonClosableResourceResolverWrapper;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

/**
 * The Class EatonPackagingServiceImplTest.
 *
 * @author Jaroslav Rassadin
 */
@ExtendWith(
		value = { AemContextExtension.class, MockitoExtension.class })
class EatonPackagingServiceImplTest {

	private static final String PACKAGE_DESCRIPTION = "Tags modification";

	private static final String PACKAGE_GROUP = "com.eaton.find.replace.backup";

	private static final String PACKAGE_NAME = "find-replace-backup-tags-add";

	private static final String PACKAGE_ID = "com.eaton.find.replace.backup:find-replace-backup-tags-add";

	private static final String PACKAGE_PATH = "/etc/packages/com.eaton.find.replace.backup/find-replace-backup-tags-add.zip";

	private final AemContext context = new AemContext(ResourceResolverType.JCR_OAK);

	@Mock
	private ResourceResolverFactory resourceResolverFactory;

	@InjectMocks
	private final EatonPackagingServiceImpl classUnderTest = new EatonPackagingServiceImpl();

	/**
	 * Test that package is created successfully.
	 *
	 * @throws LoginException
	 */
	@Test
	@DisplayName("Package created successfully")
	void testPackageCreatedSuccessfully() throws LoginException {
		// set up
		when(this.resourceResolverFactory.getServiceResourceResolver(anyMap())).thenReturn(new NonClosableResourceResolverWrapper(this.context
				.resourceResolver()));

		// exercise
		final PackageBean actualPackageBean = this.classUnderTest.create(this.getPackageRequest());

		// verify
		assertEquals(PackageBean.builder()
				.withDescription(PACKAGE_DESCRIPTION)
				.withGroup(PACKAGE_GROUP)
				.withId(PACKAGE_ID)
				.withName(PACKAGE_NAME)
				.withPath(PACKAGE_PATH)
				.withVersion(StringUtils.EMPTY)
				.build(), actualPackageBean, "Should be equal packages");
	}

	private PackageRequestBean getPackageRequest() {
		final PackageRequestBean request = new PackageRequestBean();

		request.setDescription(PACKAGE_DESCRIPTION);
		request.setGroup(PACKAGE_GROUP);
		request.setName(PACKAGE_NAME);
		request.setFilters(Set.of(new PackagePathFilter("/content/eaton/us/en-us/catalog/valves/coils-sicv-eaton/jcr:content"),
				new PackagePathFilter("/content/eaton/us/en-us/catalog/valves/custom-manifolds-sicv/jcr:content")));

		return request;
	}
}
