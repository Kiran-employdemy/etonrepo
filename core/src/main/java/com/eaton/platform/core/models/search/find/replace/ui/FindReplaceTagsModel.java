package com.eaton.platform.core.models.search.find.replace.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

import com.day.cq.tagging.TagConstants;
import com.eaton.platform.core.bean.search.find.replace.FindReplacePropertyBean;
import com.eaton.platform.core.bean.search.find.replace.FindReplaceResourceTypeBean;
import com.eaton.platform.core.bean.search.find.replace.FindReplaceTagBean;
import com.eaton.platform.core.bean.search.find.replace.request.ContentType;
import com.eaton.platform.core.services.search.find.replace.FindReplaceComponentService;
import com.eaton.platform.core.services.search.find.replace.FindReplaceConfigService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class FindReplaceTagsModel.
 *
 * @author Jaroslav Rassadin
 */
@Model(
		adaptables = SlingHttpServletRequest.class,
		defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FindReplaceTagsModel {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@OSGiService
	private FindReplaceConfigService configService;

	@OSGiService
	private FindReplaceComponentService componentService;

	private Map<String, String> modes;

	private List<FindReplaceSelectItemModel> modeItems;

	private List<FindReplaceTagBean> tags;

	private List<FindReplaceSelectItemModel> tagItems;

	private List<FindReplaceTagsHelpModel> tagHelpItems;

	/**
	 * Gets the modes.
	 *
	 * @return the modes
	 */
	public Map<String, String> getModes() {
		if (this.modes == null) {
			this.modes = new LinkedHashMap<>();
			this.modes.put("add", "Add");
			this.modes.put("delete", "Delete");
			this.modes.put("replace", "Replace");
		}
		return this.modes;
	}

	/**
	 * Gets the select items modes.
	 *
	 * @return the mode select items
	 */
	public List<FindReplaceSelectItemModel> getModeSelectItems() {
		if (this.modeItems == null) {

			if (MapUtils.isNotEmpty(this.getModes())) {
				this.modeItems = this.getModes().entrySet().stream().map(e -> new FindReplaceSelectItemModel(Collections.emptyMap(), e.getValue(), e.getKey()))
						.collect(Collectors.toList());

			} else {
				this.modeItems = Collections.emptyList();
			}
		}
		return this.modeItems;
	}

	/**
	 * Gets the tags.
	 *
	 * @return the tags
	 */
	public List<FindReplaceTagBean> getTags() {
		if (this.tags == null) {
			this.tags = this.configService.getTags();
		}
		return this.tags;
	}

	/**
	 * Gets the select items for tag fields.
	 *
	 * @return the tag select items
	 */
	public List<FindReplaceSelectItemModel> getTagSelectItems() {
		if (this.tagItems == null) {
			final List<FindReplaceSelectItemModel> items;

			if (CollectionUtils.isNotEmpty(this.getTags())) {
				items = this.getTags().stream().map(t -> new FindReplaceSelectItemModel(Map.of("data-multiple", Boolean.toString(t.isMultiple()), "data-own",
						Boolean.toString(t.isOwnProperty()), "data-path", t.getPath()), t.getTitle(), t.getId()))
						.sorted((t1, t2) -> StringUtils.compareIgnoreCase(t1.getTitle(), t2.getTitle()))
						.collect(Collectors.toList());
			} else {
				items = Collections.emptyList();
			}
			this.tagItems = items;
		}
		return this.tagItems;
	}

	/**
	 * Gets the tag help items.
	 *
	 * @return the tagHelpItems
	 */
	public List<FindReplaceTagsHelpModel> getTagHelpItems() {
		if (this.tagHelpItems == null) {
			final List<FindReplaceTagsHelpModel> helpItems = new ArrayList<>();

			if (CollectionUtils.isNotEmpty(this.getTags())) {

				for (final FindReplaceTagBean tag : this.getTags()) {
					final Map<String, List<String>> typesMap = new HashMap<>();
					final FindReplaceTagsHelpModel help = new FindReplaceTagsHelpModel(tag.isMultiple(), tag.isOwnProperty(), tag.getId(), typesMap);

					helpItems.add(help);

					if (this.configService.getTagPropertiesMap(ContentType.ASSET).get(tag.getId()) != null) {
						typesMap.put("ASSET", Collections.emptyList());
					}
					if (this.configService.getTagPropertiesMap(ContentType.PAGE).get(tag.getId()) != null) {
						typesMap.put("PAGE", Collections.emptyList());
					}
					this.getComponentTagHelpItems(tag, typesMap);
				}
				this.tagHelpItems = helpItems;
			}
		}
		return this.tagHelpItems;
	}

	/**
	 * Gets the tag help items as JSON string.
	 *
	 * @return the tag help items json
	 * @throws JsonProcessingException
	 *             the json processing exception
	 */
	public String getTagHelpItemsJson() throws JsonProcessingException {
		return OBJECT_MAPPER.writer().writeValueAsString(this.getTagHelpItems());
	}

	private void getComponentTagHelpItems(final FindReplaceTagBean tag, final Map<String, List<String>> typesMap) {
		final FindReplacePropertyBean property = this.configService.getTagPropertiesMap(ContentType.COMPONENT).get(tag.getId());

		if ((property != null) && CollectionUtils.isNotEmpty(property.getResourceTypes())) {
			typesMap.put("COMPONENT", property.getResourceTypes().stream().map((final FindReplaceResourceTypeBean resourceType) -> {

				final String title = this.componentService.getTypeTitleByResourceType(resourceType.getName());
				return StringUtils.isNotEmpty(title) ? title : resourceType.getName();

			}).filter(Objects::nonNull).sorted().collect(Collectors.toList()));
		}
	}

	/**
	 * Gets the default tags path.
	 *
	 * @return the default tags path
	 */
	public String getDefaultTagsPath() {
		return TagConstants.TAG_ROOT_PATH;
	}

}
