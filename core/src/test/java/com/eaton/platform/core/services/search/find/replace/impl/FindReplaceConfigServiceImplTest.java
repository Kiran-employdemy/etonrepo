package com.eaton.platform.core.services.search.find.replace.impl;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.annotation.Annotation;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.day.cq.tagging.TagConstants;
import com.eaton.platform.core.bean.search.find.replace.FindReplacePropertyBean;
import com.eaton.platform.core.bean.search.find.replace.FindReplaceResourceTypeBean;
import com.eaton.platform.core.bean.search.find.replace.FindReplaceTagBean;
import com.eaton.platform.core.bean.search.find.replace.request.ContentType;
import com.eaton.platform.core.exception.EatonApplicationException;
import com.eaton.platform.core.services.search.find.replace.FindReplaceConfigServiceConfig;

/**
 * The Class FindReplaceConfigServiceImplTest.
 *
 * @author Jaroslav Rassadin
 */
class FindReplaceConfigServiceImplTest {

	private final FindReplaceConfigServiceImpl classUnderTest = new FindReplaceConfigServiceImpl();

	/**
	 * Test that service is activated correctly.
	 *
	 */
	@Test
	@DisplayName("Service is activated correctly")
	void testServiceActivatedCorrectly() {
		// set up and exercise
		this.classUnderTest.activate(new FindReplaceConfigServiceConfig() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return FindReplaceConfigServiceConfig.class;
			}

			@Override
			public String[] tags() {
				return new String[] { "accountType|Account Type|o|m|/content/cq:tags/eaton-secure/accounttype",
						"productCategories|Product Categories|o|m|/content/cq:tags/eaton-secure/product-category",
						"facet|Whitelist facets (nested multifield)|n|s" };
			}

			@Override
			public String[] assetsTagProperties() {
				return new String[] { "partnerProgramAndTierLevel|xmp:eaton-partner-program-and-tier-level|m" };
			}

			@Override
			public String[] componentsTagProperties() {
				return new String[] { "facet|whitelistFacets/*/facet|s|", "tags|tags|m|" };
			}

			@Override
			public String[] pagesTagProperties() {
				return new String[] { "accountType|accountType|m" };
			}

			@Override
			public String[] resourceTypes() {
				return new String[] { "facet|eaton/components/secure/advanced-search",
						"tags|eaton/components/general/section-navigation",
						"tags|eaton/components/general/vertical-link-list" };
			}
		});

		// verify
		assertThat("Should be equal tags lists", this.classUnderTest.getTags(), contains(
				new FindReplaceTagBean("accountType", true, true, "/content/cq:tags/eaton-secure/accounttype", "Account Type"),
				new FindReplaceTagBean("productCategories", true, true, "/content/cq:tags/eaton-secure/product-category", "Product Categories"),
				new FindReplaceTagBean("facet", false, false, TagConstants.TAG_ROOT_PATH, "Whitelist facets (nested multifield)")));

		assertEquals(new FindReplacePropertyBean(true, "xmp:eaton-partner-program-and-tier-level"), this.classUnderTest.getTagPropertiesMap(ContentType.ASSET)
				.get("partnerProgramAndTierLevel"), "Should be equal property names");

		assertEquals(new FindReplacePropertyBean(false, "whitelistFacets/*/facet", Set.of(new FindReplaceResourceTypeBean(
				"eaton/components/secure/advanced-search"))), this.classUnderTest
						.getTagPropertiesMap(ContentType.COMPONENT).get("facet"),
				"Should be equal properties");

		assertEquals(new FindReplacePropertyBean(true, "accountType"), this.classUnderTest.getTagPropertiesMap(ContentType.PAGE).get("accountType"),
				"Should be equal property names");
	}

	/**
	 * Test that default tags path is used when path in config is empty.
	 *
	 * @throws EatonApplicationException
	 *             the eaton application exception
	 */
	@Test
	@DisplayName("Default tags path is used when path in config is empty")
	void testDefaultTagsPathIsUsed() throws EatonApplicationException {
		// set up and exercise
		this.classUnderTest.activate(new FindReplaceConfigServiceConfig() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return FindReplaceConfigServiceConfig.class;
			}

			@Override
			public String[] tags() {
				return new String[] { "accountType|Account Type|o|m" };
			}

			@Override
			public String[] assetsTagProperties() {
				return null;
			}

			@Override
			public String[] componentsTagProperties() {
				return null;
			}

			@Override
			public String[] pagesTagProperties() {
				return null;
			}

			@Override
			public String[] resourceTypes() {
				return null;
			}
		});

		// verify
		assertThat("Should be equal tags lists", this.classUnderTest.getTags(), contains(new FindReplaceTagBean("accountType", true, true,
				TagConstants.TAG_ROOT_PATH, "Account Type")));

	}

}
