package com.eaton.platform.core.services.search.find.replace.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.tagging.TagConstants;
import com.eaton.platform.core.bean.search.find.replace.FindReplacePropertyBean;
import com.eaton.platform.core.bean.search.find.replace.FindReplaceResourceTypeBean;
import com.eaton.platform.core.bean.search.find.replace.FindReplaceTagBean;
import com.eaton.platform.core.bean.search.find.replace.request.ContentType;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.services.search.find.replace.FindReplaceConfigService;
import com.eaton.platform.core.services.search.find.replace.FindReplaceConfigServiceConfig;

/**
 * The Class FindReplaceConfigServiceImpl.
 *
 * Provides configurations for find and replaces functionality for text and tags.
 *
 * @author Jaroslav Rassadin
 */
@Component(
		service = FindReplaceConfigService.class,
		configurationPolicy = ConfigurationPolicy.REQUIRE,
		property = {
				AEMConstants.SERVICE_VENDOR_EATON,
				AEMConstants.SERVICE_DESCRIPTION + "Find Replace Config Service",
				AEMConstants.PROCESS_LABEL + "FindReplaceConfigServiceImpl" })
@Designate(
		ocd = FindReplaceConfigServiceConfig.class)
public class FindReplaceConfigServiceImpl implements FindReplaceConfigService {

	private static final Logger LOGGER = LoggerFactory.getLogger(FindReplaceConfigServiceImpl.class);

	private static final int TAG_ARRAY_LENGTH = 4;

	private static final int TAG_WITH_ROOT_PATH_ARRAY_LENGTH = 5;

	private static final int OWN_PROPERTY_INDEX = 2;

	private static final int TAG_ROOT_PATH_INDEX = 4;

	private static final int RESOURCE_TYPE_ARRAY_LENGTH = 2;

	private static final int RESOURCE_TYPE_WITH_SUBTYPE_ARRAY_LENGTH = 3;

	private static final int TAG_PROPERTIES_ARRAY_LENGTH = 3;

	private static final String MULTUPLE_FLAG = "m";

	private static final String OWN_FLAG = "o";

	private static final int MULTUPLE_FLAG_TAG_INDEX = 3;

	private static final int MULTUPLE_FLAG_PROPERTY_INDEX = 2;

	private static final int RESOURCE_TYPE_WITH_SUBTYPE_INDEX = 2;

	private List<FindReplaceTagBean> tags = Collections.emptyList();

	private Map<ContentType, Map<String, FindReplacePropertyBean>> tagPropertiesMap = Collections.emptyMap();

	/**
	 * Activate the service.
	 *
	 * @param config
	 *            the config
	 */
	@Activate
	@Modified
	protected void activate(final FindReplaceConfigServiceConfig config) {
		this.tags = Collections.unmodifiableList(this.initTags(config.tags()));
		this.tagPropertiesMap = Collections.unmodifiableMap(this.initTagProperties(config.assetsTagProperties(), config.componentsTagProperties(), config
				.pagesTagProperties(), this.initResourceTypes(config.resourceTypes())));
	}

	private List<FindReplaceTagBean> initTags(final String[] tagsArray) {
		final List<FindReplaceTagBean> tagsList = new ArrayList<>();

		if (ArrayUtils.isNotEmpty(tagsArray)) {

			for (final String tagStr : tagsArray) {
				final String[] tagParts = tagStr.split("\\|");

				if (tagParts.length == TAG_ARRAY_LENGTH) {
					tagsList.add(new FindReplaceTagBean(tagParts[0], this.isMutiple(tagParts[MULTUPLE_FLAG_TAG_INDEX]),
							this.isOwn(tagParts[OWN_PROPERTY_INDEX]), TagConstants.TAG_ROOT_PATH, tagParts[1]));

				} else if (tagParts.length == TAG_WITH_ROOT_PATH_ARRAY_LENGTH) {
					tagsList.add(new FindReplaceTagBean(tagParts[0], this.isMutiple(tagParts[MULTUPLE_FLAG_TAG_INDEX]),
							this.isOwn(tagParts[OWN_PROPERTY_INDEX]), this.getRootPath(tagParts[TAG_ROOT_PATH_INDEX]),
							StringEscapeUtils.unescapeJava(tagParts[1])));

				} else {
					LOGGER.error("Tag string has wrong format: {}.", tagStr);
				}
			}
		}
		return tagsList;
	}

	private Map<String, Set<FindReplaceResourceTypeBean>> initResourceTypes(final String[] resourceTypesArray) {
		final Map<String, Set<FindReplaceResourceTypeBean>> typesMap = new HashMap<>();

		if (ArrayUtils.isNotEmpty(resourceTypesArray)) {

			for (final String resourceTypeStr : resourceTypesArray) {
				final String[] resourceTypeParts = resourceTypeStr.split("\\|");
				FindReplaceResourceTypeBean resourceType = null;

				// build bean
				if (resourceTypeParts.length == RESOURCE_TYPE_ARRAY_LENGTH) {
					resourceType = new FindReplaceResourceTypeBean(resourceTypeParts[1]);

				} else if (resourceTypeParts.length == RESOURCE_TYPE_WITH_SUBTYPE_ARRAY_LENGTH) {
					resourceType = new FindReplaceResourceTypeBean(resourceTypeParts[1], resourceTypeParts[RESOURCE_TYPE_WITH_SUBTYPE_INDEX]);

				} else {
					LOGGER.error("Resource type string has wrong format: {}.", resourceTypeStr);
					continue;
				}
				// put bean into map
				if (typesMap.containsKey(resourceTypeParts[0])) {
					typesMap.get(resourceTypeParts[0]).add(resourceType);

				} else {
					final Set<FindReplaceResourceTypeBean> resourceTypes = new HashSet<>();
					resourceTypes.add(resourceType);
					typesMap.put(resourceTypeParts[0], resourceTypes);
				}
			}
		}
		return typesMap;
	}

	private Map<ContentType, Map<String, FindReplacePropertyBean>> initTagProperties(final String[] assetsTagPropertiesArray,
			final String[] componentsTagPropertiesArray, final String[] pagesTagsPropertiesArray,
			final Map<String, Set<FindReplaceResourceTypeBean>> resourceTypesMap) {

		final Map<ContentType, Map<String, FindReplacePropertyBean>> propertiesMap = new EnumMap<>(ContentType.class);

		this.parseProperties(ContentType.ASSET, assetsTagPropertiesArray, propertiesMap, resourceTypesMap);
		this.parseProperties(ContentType.COMPONENT, componentsTagPropertiesArray, propertiesMap, resourceTypesMap);
		this.parseProperties(ContentType.PAGE, pagesTagsPropertiesArray, propertiesMap, resourceTypesMap);

		return propertiesMap;
	}

	private void parseProperties(final ContentType contentType, final String[] tagPropertiesArray,
			final Map<ContentType, Map<String, FindReplacePropertyBean>> propertiesMap, final Map<String, Set<FindReplaceResourceTypeBean>> resourceTypesMap) {

		if (ArrayUtils.isNotEmpty(tagPropertiesArray)) {

			final Map<String, FindReplacePropertyBean> id2PropertyMap = new HashedMap<>();

			for (final String propertyStr : tagPropertiesArray) {
				final String[] propertiesParts = propertyStr.split("\\|");

				if (propertiesParts.length != TAG_PROPERTIES_ARRAY_LENGTH) {
					LOGGER.error("Tag property string has wrong format: {}.", propertyStr);
					continue;
				}
				id2PropertyMap.put(propertiesParts[0], new FindReplacePropertyBean(this.isMutiple(propertiesParts[MULTUPLE_FLAG_PROPERTY_INDEX]),
						propertiesParts[1], resourceTypesMap.containsKey(propertiesParts[0]) ? resourceTypesMap.get(propertiesParts[0])
								: Collections.emptySet()));
			}
			propertiesMap.put(contentType, Collections.unmodifiableMap(id2PropertyMap));
		}
	}

	private String getRootPath(final String strProp) {
		return StringUtils.isNotEmpty(strProp) ? strProp : TagConstants.TAG_ROOT_PATH;
	}

	private boolean isMutiple(final String strProp) {
		return MULTUPLE_FLAG.equals(strProp);
	}

	private boolean isOwn(final String strProp) {
		return OWN_FLAG.equals(strProp);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<FindReplaceTagBean> getTags() {
		return this.tags;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, FindReplacePropertyBean> getTagPropertiesMap(final ContentType contentType) {
		return this.tagPropertiesMap.get(contentType);
	}

}
