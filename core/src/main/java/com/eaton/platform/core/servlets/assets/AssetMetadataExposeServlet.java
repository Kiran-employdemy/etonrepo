package com.eaton.platform.core.servlets.assets;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.dam.commons.util.PrefixRenditionPicker;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.AssetMetadataExposeService;
import com.eaton.platform.core.services.CountryLangCodeConfigService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.auth.constants.SecureConstants;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;

/**
 * The Class AssetMetadataExposeServlet.
 *
 * Endpoint for exposing asset metadata. Is supposed to be used by search engines.
 */
@Component(
		service = Servlet.class,
		immediate = true,
		property = { ServletConstants.SLING_SERVLET_METHODS_GET, ServletConstants.SLING_SERVLET_RESOURCE_TYPES + "dam/Asset",
				ServletConstants.SLING_SERVLET_SELECTORS + "custommetadata" })
public class AssetMetadataExposeServlet extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = -4090386502502010905L;

	private static final Logger LOGGER = LoggerFactory.getLogger(AssetMetadataExposeServlet.class);

	public static final String ASSET_1280_1280_THUMBNAIL = ".thumb.1280.1280.png";

	@Reference
	private transient AssetMetadataExposeService assetMetadataExposeService;

	@Reference
	private transient CountryLangCodeConfigService countryLangCodeConfigService;

	@Reference
	private transient AdminService adminService;

	private String excludeCountryListString;

	/**
	 * Activate the service.
	 *
	 * @param config
	 *            the config
	 */
	@Activate
	@Modified
	protected void activate() {
		final String[] excludeCountryList = this.countryLangCodeConfigService.getExcludeCountryList();

		if (ArrayUtils.isNotEmpty(excludeCountryList)) {
			this.excludeCountryListString = String.join("||", excludeCountryList);
		}
	}

	@Override
	protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws IOException {
		try (ResourceResolver resourceResolver = this.adminService.getReadService()) {
			final TagManager tagManager = CommonUtil.adapt(resourceResolver, TagManager.class);

			response.setContentType(CommonConstants.APPLICATION_JSON);
			response.setCharacterEncoding(CommonConstants.UTF_8);

			final JsonObject jsonOutPutOfAssetProperties = new JsonObject();

			final String[] includeAssetProperties = this.assetMetadataExposeService.getIncludeAssetProperties();
			final String[] includeSecureAssetProperties = this.assetMetadataExposeService.getIncludeSecureAssetProperties();

			final Resource asset = request.getResource();
			final ValueMap valueMapOfAssetsProp = asset.getValueMap();
			final ValueMap metadataMap = asset.getChild(CommonConstants.JCR_CONTENT_METADATA).getValueMap();

			this.processGeneralProperties(metadataMap, includeAssetProperties, jsonOutPutOfAssetProperties, tagManager);
			this.processSecureProperties(metadataMap, includeSecureAssetProperties, jsonOutPutOfAssetProperties, tagManager);

			this.addThumbnailImage(asset, request, jsonOutPutOfAssetProperties);

			if (valueMapOfAssetsProp.get(CommonConstants.JCR_UUID) != null) {
				jsonOutPutOfAssetProperties.addProperty(CommonConstants.UUID, valueMapOfAssetsProp.get(CommonConstants.JCR_UUID, String.class));
			}
			if ((this.excludeCountryListString != null)
					&& CommonConstants.TRUE.equals(metadataMap.get(CommonConstants.EXCLUDE_COUNTRY_PROPERTY, CommonConstants.FALSE))) {
				jsonOutPutOfAssetProperties.addProperty(CommonConstants.EXCLUDE_COUNTRY_LIST, this.excludeCountryListString);
			}
			this.getAssetPropertiesAsJsonOutput(CommonUtil.getApplicationId(asset.getPath()), CommonConstants.APPLICATION_ID_META_TAG_NAME, metadataMap,
					jsonOutPutOfAssetProperties, tagManager);

			response.getWriter().write(jsonOutPutOfAssetProperties.toString());
		}
	}

	private void processGeneralProperties(final ValueMap metadataMap, final String[] includeAssetProperties, final JsonObject jsonOutPutOfAssetProperties,
			final TagManager tagManager) {
		if (ArrayUtils.isNotEmpty(includeAssetProperties)) {

			for (final String assetProperties : includeAssetProperties) {
				final Object objectOfAssetProperty = metadataMap.get(assetProperties);

				if (null != objectOfAssetProperty) {
					this.getAssetPropertiesAsJsonOutput(objectOfAssetProperty, assetProperties, metadataMap, jsonOutPutOfAssetProperties, tagManager);
				}
			}
		} else {
			metadataMap.forEach((key, value) -> this.getAssetPropertiesAsJsonOutput(value, key, metadataMap, jsonOutPutOfAssetProperties, tagManager));
		}
	}

	private void processSecureProperties(final ValueMap metadataMap, final String[] includeSecureAssetProperties, final JsonObject jsonOutPutOfAssetProperties,
			final TagManager tagManager) {
		if (ArrayUtils.isNotEmpty(includeSecureAssetProperties)
				&& SecureConstants.YES.equals(metadataMap.get(SecureConstants.METADATA_SECURE_ASSET, SecureConstants.NO))) {
			this.getSecureAssetPropertiesAsJsonOutput(includeSecureAssetProperties, metadataMap, jsonOutPutOfAssetProperties, tagManager);
		}
	}

	/**
	 * Get JSON output.
	 *
	 * @param objectOfAssetProperty
	 * @param assetProperties
	 * @Param valueMapOfAssets
	 * @Param jsonOutPutOfAssetProperties
	 */
	private void getAssetPropertiesAsJsonOutput(Object objectOfAssetProperty, final String assetProperties, final ValueMap metadataMap,
			final JsonObject jsonOutPutOfAssetProperties, final TagManager tagManager) {
		try {
			if (objectOfAssetProperty instanceof String[]) {
				JsonArray jsonArray;

				if (assetProperties.equals(CommonConstants.CQ_TAGS)) {
					jsonArray = this.getResolvedTagForAssetMetaData(assetProperties, metadataMap, false, tagManager);

				} else {
					jsonArray = this.convertJsonArray(assetProperties, metadataMap);
				}
				jsonOutPutOfAssetProperties.add(assetProperties, jsonArray);
			} else {

				if (objectOfAssetProperty.toString().contains("java.util.GregorianCalendar")) {

					if (assetProperties.equalsIgnoreCase(CommonConstants.DAM_ASSET_PUBLICATION_DATE)) {
						objectOfAssetProperty = CommonUtil.getEpocTimeFromGregorianForAssets(objectOfAssetProperty);
					} else {
						objectOfAssetProperty = CommonUtil.getStringDateFromGregorianForAssets(objectOfAssetProperty);
					}
				}
				jsonOutPutOfAssetProperties.add(assetProperties, new Gson().toJsonTree(objectOfAssetProperty.toString()));
			}
		} catch (final Exception e) {
			LOGGER.error("JSON Exception while adding asset metadata.", e);
		}
	}

	/**
	 * It will return json array of resolved tags.
	 *
	 * @param key
	 * @param metadataMap
	 */
	private JsonArray getResolvedTagForAssetMetaData(final String key, final ValueMap metadataMap, final boolean outputOnlyTagName,
			final TagManager tagManager) {
		final JsonArray jsonArrayWithTags = new JsonArray();

		for (final String tagValue : metadataMap.get(key, new String[0])) {
			if (null != tagValue) {
				this.addResolvedTag(jsonArrayWithTags, outputOnlyTagName, tagManager, tagValue);
			}
		}
		return jsonArrayWithTags;
	}

	private JsonArray addPartnerProgrammeTag(final String key, final ValueMap metadataMap, final TagManager tagManager) {
		final JsonArray jsonArrayWithTags = new JsonArray();

		for (final String tagValue : metadataMap.get(key, new String[0])) {
			if (tagValue != null) {

				final Tag tag = tagManager.resolve(tagValue);

				if (tag != null) {
					final String[] parts = tag.getTagID().split(CommonConstants.SLASH_STRING);

					if (parts.length > 2) {
						jsonArrayWithTags.add(parts[1] + CommonConstants.UNDERSCORE + parts[2]);

					} else {
						jsonArrayWithTags.add(parts[1]);
					}
				}
			}
		}
		return jsonArrayWithTags;
	}

	private void addResolvedTag(final JsonArray jsonArrayWithTags, final boolean outputOnlyTagName, final TagManager tagManager, final String tagValue) {
		final Tag tag = tagManager.resolve(tagValue);
		if (null != tag) {
			jsonArrayWithTags.add(outputOnlyTagName ? tag.getName() : tag.getTagID());
		}
	}

	private JsonArray convertJsonArray(final String key, final ValueMap valueMap) {
		final JsonArray jsonArray = new JsonArray();
		for (final String propValue : valueMap.get(key, new String[0])) {
			jsonArray.add(propValue);
		}
		return jsonArray;
	}

	/**
	 * Adds 319x319 rendition as thumbnail image.
	 *
	 * @param resource
	 * @param request
	 * @param jsonOutPutOfAssetProperties
	 */
	private void addThumbnailImage(final Resource resource, final SlingHttpServletRequest request, final JsonObject jsonOutPutOfAssetProperties) {
		final Asset asset = request.getResourceResolver().resolve(resource.getPath()).adaptTo(Asset.class);

		if (asset != null) {
			final PrefixRenditionPicker picker = new PrefixRenditionPicker(DamConstants.PREFIX_ASSET_WEB + CommonConstants.DAM_1280_RENDITION, true);

			if (picker.getRendition(asset) != null) {
				final String assetPath = picker.getRendition(asset).getPath().contains(DamConstants.ORIGINAL_FILE) ? picker.getRendition(asset).getPath()
						: (asset.getPath() + ASSET_1280_1280_THUMBNAIL);
				jsonOutPutOfAssetProperties.add(CommonConstants.DAM_THUMBNAIL_KEY, new Gson().toJsonTree(assetPath));
			}
		}
	}

	/**
	 * It will prepare json array for secure attributes Get JSON output.
	 *
	 * @param includeSecureAssetProperties
	 * @Param valueMapOfAssets
	 * @Param jsonOutPutOfAssetProperties
	 */
	private void getSecureAssetPropertiesAsJsonOutput(final String[] includeSecureAssetProperties, final ValueMap metadataMap,
			final JsonObject jsonOutPutOfAssetProperties, final TagManager tagManager) {
		try {
			for (final String secureAssetProperty : includeSecureAssetProperties) {
				final Object propertyValue = metadataMap.get(secureAssetProperty);

				if (propertyValue instanceof String[]) {
					final String mappedPropertyName = getXMPMappingProperty(secureAssetProperty);
					final JsonArray jsonArray;

					if (SecureConstants.PROP_PARTNER_PROGRAMME_AND_TIER_LEVEL_TYPE_TAG.equals(mappedPropertyName)) {
						jsonArray = this.addPartnerProgrammeTag(secureAssetProperty, metadataMap, tagManager);

					} else {
						jsonArray = this.getResolvedTagForAssetMetaData(secureAssetProperty, metadataMap, true, tagManager);
					}
					jsonOutPutOfAssetProperties.add(mappedPropertyName, jsonArray);

				} else if (secureAssetProperty.equals(SecureConstants.METADATA_SECURE_ASSET)) {
					jsonOutPutOfAssetProperties.add(secureAssetProperty, new Gson().toJsonTree(SecureConstants.TRUE));
					final String isSecureAllAuthUsers = (metadataMap.get(SecureConstants.PROP_ACCOUNT_TYPE_TAG) == null)
							&& (metadataMap.get(SecureConstants.PROP_PRODUCT_CATEGORIES_TAG) == null)
							&& (metadataMap.get(SecureConstants.PROP_APPLICATION_ACCESS_TAG) == null)
							&& (metadataMap.get(SecureConstants.XMP_EATON_PROP_PARTNER_PROGRAMME_AND_TIER_LEVEL_TYPE_TAG) == null) ? SecureConstants.TRUE
									: SecureConstants.FALSE;
					jsonOutPutOfAssetProperties.add(SecureConstants.SECURE_GLOBAL_ATTRIB, new Gson().toJsonTree(isSecureAllAuthUsers));
				}
			}
		} catch (final Exception e) {
			LOGGER.error("JSON Exception while adding asset secure metadata", e);
		}
	}

	private static String getXMPMappingProperty(String xmpMappingProperty) {
		if (xmpMappingProperty.equals(SecureConstants.XMP_EATON_PROP_PARTNER_PROGRAMME_AND_TIER_LEVEL_TYPE_TAG)) {
			xmpMappingProperty = SecureConstants.PROP_PARTNER_PROGRAMME_AND_TIER_LEVEL_TYPE_TAG;
		}
		return xmpMappingProperty;
	}

}
