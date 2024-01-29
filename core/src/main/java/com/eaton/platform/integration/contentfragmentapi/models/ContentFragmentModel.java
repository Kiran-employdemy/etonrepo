package com.eaton.platform.integration.contentfragmentapi.models;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * 
 * Generic Content Fragment model, expose content fragment 
 * as key, value pairs of strings,  recursively resolves all the referenced content fragments. 
 *  
 * @author E0527858
 *
 */
@JsonPropertyOrder(alphabetic = true )
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ContentFragmentModel {
	
	private static final String CFM_PROP_PREFIX = "cfm_";
	private static final Logger logger = LoggerFactory.getLogger(ContentFragmentModel.class);
	private static final List<String> CF_PROPERTIES = Arrays.asList("submenu_items","eyebrow_menu_items","main_menu_items");
	
	@Inject
	@Self
	private Resource resource;
	private Optional<ContentFragment> contentFragment;
	
	
	
	@PostConstruct
	public void init() {
		contentFragment = Optional.ofNullable(resource.adaptTo(ContentFragment.class));
	}

	@JsonAnyGetter
	public Map<String, Object> getContent() {
		Map<String, Object> cfMap = new HashMap<>();
		if (contentFragment.isPresent()) {
			ContentFragment cf = contentFragment.get();
			cf.getElements().forEachRemaining(ce -> addToMap(cfMap, ce));
		}else {
			logger.error("ContentFragmentModel: Could not adapt to a valid content fragment");
		}
		return cfMap;
	}

	private void addToMap(Map<String, Object> cfMap, ContentElement ce) {
		if (CF_PROPERTIES.contains(ce.getName()) || ce.getName().startsWith(CFM_PROP_PREFIX)) {
			if (ce.getValue().getDataType().isMultiValue()) {
				cfMap.put(ce.getName(), getMultiValueContent(ce.getValue().getValue()));
			}else {
				cfMap.put(ce.getName(), resolveContentFragment(ce.getContent()));
			}
		} else {
			cfMap.put(ce.getName(), ce.getContent());
		}
	}
	
	private ContentFragmentModel resolveContentFragment(String contentPath) {
		return resource.getResourceResolver().resolve(contentPath).adaptTo(ContentFragmentModel.class);
	}

	private List<ContentFragmentModel> getMultiValueContent(Object fragmentData) {
		List<ContentFragmentModel> cfList = null;
		if (fragmentData != null) {
			cfList = Arrays.asList((String[]) fragmentData)
							.stream()
							.map(refContentPath -> resource.getResourceResolver().resolve(refContentPath))
							.filter(Objects::nonNull)
							.map(refContentResource -> refContentResource.adaptTo(ContentFragmentModel.class))
							.collect(Collectors.toList());
		}
		return cfList;
	}
}