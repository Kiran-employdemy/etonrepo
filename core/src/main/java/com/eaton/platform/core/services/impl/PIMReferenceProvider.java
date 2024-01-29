package com.eaton.platform.core.services.impl;

import java.util.ArrayList;
import java.util.List;

import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.PageManagerFactory;
import com.day.cq.wcm.api.reference.ReferenceProvider;
import com.eaton.platform.integration.pim.util.PIMUtil;

/**
 * PIM Reference Provider Reference provider that searches for PIM node path
 * referenced inside any given page resource
 */
@Component(service = ReferenceProvider.class, immediate = true, enabled = true,
		property = {
				AEMConstants.SERVICE_VENDOR_EATON,
				AEMConstants.SERVICE_DESCRIPTION + "Provides PIM reference",
				AEMConstants.PROCESS_LABEL + "PIMReferenceProvider"
		})
public class PIMReferenceProvider implements ReferenceProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger(PIMReferenceProvider.class);
	public static final String REFERENCE_TYPE = "product";
	@Reference
	private PageManagerFactory pageManagerFactory;
	/**
	 * Finds PIM references for a given resource.
	 */
	@Override
	public List<com.day.cq.wcm.api.reference.Reference> findReferences(Resource resource) {
		LOGGER.debug("PIMReferenceProvider :: findReferences() :: Started");
		List<com.day.cq.wcm.api.reference.Reference> referenceList = new ArrayList<>();
		Resource pimResource = null;
		String primaryImagePath = StringUtils.EMPTY;
		/**
		 * Logic for retrieving referenced PIM resource and creation of reference object
		 * for the same, and adding each reference object to the pimReferenceList.
		 */
		ResourceResolver resourceResolver = resource.getResourceResolver();
		PageManager pageManager = pageManagerFactory.getPageManager(resourceResolver);
		if (pageManager != null) {
			Page contextPage = pageManager.getContainingPage(resource);
			if (contextPage != null) {
				final String pimPagePath = PIMUtil.getPIMPagePath(contextPage);
				if (StringUtils.isNotBlank(pimPagePath)) {
					LOGGER.debug("PIM Page Path :: {}", pimPagePath);
					pimResource = resourceResolver.getResource(pimPagePath);
					if(pimResource != null){
						addPIMReference(referenceList, pimResource, pimPagePath);
						LOGGER.debug("PIM Reference Added to Reference List");
						addPrimaryImageReference(referenceList, pimResource, primaryImagePath, resourceResolver);
						LOGGER.debug("Primary Image Reference Added to Reference List");
					}
				}
			}
		}
		LOGGER.debug("PIMReferenceProvider :: findReferences() :: Ended");
		return referenceList;
	}

	private void addPIMReference(List<com.day.cq.wcm.api.reference.Reference> referenceList, Resource pimResource,
								  final String pimPagePath) {
		String pimNodeTitle;
		pimNodeTitle = pimResource.getValueMap().get("productName", String.class);
		if(StringUtils.isNotBlank(pimNodeTitle)) {
			LOGGER.debug("Adding PIM Node :: {}", pimNodeTitle);
			referenceList.add(getReference(pimNodeTitle, pimResource));
		}else{
			LOGGER.debug("Adding PIM Path :: {}", pimPagePath);
			referenceList.add(getReference(pimPagePath, pimResource));
		}
	}

	private void addPrimaryImageReference(List<com.day.cq.wcm.api.reference.Reference> referenceList,
										  Resource pimResource, String primaryImagePath, ResourceResolver resourceResolver) {
		LOGGER.debug("PIMReferenceProvider :: addPrimaryImageReference() :: Started");
		Resource primaryImageResource;
		ValueMap imageResourceMap = pimResource.getValueMap();
		if(imageResourceMap.containsKey(CommonConstants.PIM_PRIMARY_IMAGE)) {
			primaryImagePath = imageResourceMap.get(CommonConstants.PIM_PRIMARY_IMAGE).toString();
		}else if(imageResourceMap.containsKey(CommonConstants.PIM_PDH_PRIMARY_IMAGE)) {
			primaryImagePath = imageResourceMap.get(CommonConstants.PIM_PDH_PRIMARY_IMAGE).toString();
		}
		if(null != primaryImagePath) {
			primaryImageResource = resourceResolver.getResource(primaryImagePath);
			Resource primaryImgContentRes = resourceResolver.getResource(primaryImagePath + CommonConstants.SLASH_STRING + CommonConstants.JCR_CONTENT_STR);
			if(null != primaryImgContentRes) {
				if(primaryImgContentRes.getValueMap().containsKey(CommonConstants.CQ_NAME)) {
					primaryImagePath = primaryImgContentRes.getValueMap().get(CommonConstants.CQ_NAME).toString();
				}
				referenceList.add(getReference(primaryImagePath, primaryImageResource));
			}
		}
		LOGGER.debug("PIMReferenceProvider :: addPrimaryImageReference() :: Ended");
	}

	private com.day.cq.wcm.api.reference.Reference getReference(String pimPagePath,Resource pimResource) {
		return  new com.day.cq.wcm.api.reference.Reference(getType(), pimPagePath, pimResource, getLastModifiedTimeOfResource(pimResource));
	}

	private static long getLastModifiedTimeOfResource(Resource pimResource) {
		Long lastModified = pimResource.getResourceMetadata().getModificationTime();
		return lastModified != 0 ? lastModified : -1;
	}

	private static String getType() {
		return REFERENCE_TYPE;
	}

}
