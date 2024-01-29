package com.eaton.platform.core.services.search.find.replace.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.components.ComponentManager;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.search.find.replace.FindReplaceComponentService;
import com.eaton.platform.core.util.CommonUtil;

/**
 * The Class FindReplaceComponentServiceImpl.
 *
 * Provides data regarding component types.
 *
 * @author Jaroslav Rassadin
 */
@Component(
		service = FindReplaceComponentService.class,
		property = {
				AEMConstants.SERVICE_VENDOR_EATON,
				AEMConstants.SERVICE_DESCRIPTION + "Find Replace Component Service",
				AEMConstants.PROCESS_LABEL + "FindReplaceComponentServiceImpl" })
public class FindReplaceComponentServiceImpl implements FindReplaceComponentService {

	private final Map<String, String> componentsTitles = new HashMap<>();

	@Reference
	private AdminService adminService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTypeTitle(final Resource componentInstance) {
		if (componentInstance == null) {
			return null;
		}
		if (this.componentsTitles.containsKey(componentInstance.getResourceType())) {
			return this.componentsTitles.get(componentInstance.getResourceType());
		}
		return this.getComponentTitle(componentInstance);
	}

	private synchronized String getComponentTitle(final Resource resource) {
		String title = this.componentsTitles.get(resource.getResourceType());

		if (title != null) {
			return title;
		}
		final ComponentManager componentManager = CommonUtil.adapt(resource.getResourceResolver(), ComponentManager.class);
		final com.day.cq.wcm.api.components.Component component = componentManager.getComponentOfResource(resource);

		if (component != null) {
			title = component.getProperties().get(JcrConstants.JCR_TITLE, StringUtils.EMPTY);
			this.componentsTitles.put(resource.getResourceType(), title);
		}
		return title;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTypeTitleByResourceType(final String resourceType) {
		if (StringUtils.isEmpty(resourceType)) {
			return null;
		}
		if (this.componentsTitles.containsKey(resourceType)) {
			return this.componentsTitles.get(resourceType);
		}
		return this.getComponentTitleByResourceType(resourceType);
	}

	private synchronized String getComponentTitleByResourceType(final String resourceType) {
		String title = this.componentsTitles.get(resourceType);

		if (title != null) {
			return title;
		}
		try (ResourceResolver resourceResolver = this.adminService.getReadService()) {

			final ComponentManager componentManager = CommonUtil.adapt(resourceResolver, ComponentManager.class);
			final com.day.cq.wcm.api.components.Component component = componentManager.getComponent(resourceType);

			if (component != null) {
				title = component.getProperties().get(JcrConstants.JCR_TITLE, StringUtils.EMPTY);
				this.componentsTitles.put(resourceType, title);
			}
		}
		return title;
	}

}
