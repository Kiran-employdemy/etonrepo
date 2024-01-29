/**
 * 
 * @author Anushree HM
 * On 16th December 2020
 * EAT- 3955 & 3954
 * 
 * */
package com.eaton.platform.core.listeners;

import java.util.Calendar;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;

/**
 * 
 * The listener will validate assets of type 
 * document for mandatory fields 
 * like Title and asset publication date 
 * Also, validates the Title for other asset types as well
 *
 *
 * */
@Component(
        service = ResourceChangeListener.class,
        property = {
                ResourceChangeListener.PATHS + "=" + "/content/dam/eaton/",
                ResourceChangeListener.CHANGES + "=" + "CHANGED"
        }
)
public class ValidateAssetUploadListener implements ResourceChangeListener {
 
	/**
     * Constant for Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(ValidateAssetUploadListener.class);
 
	/**
     * Admin Service.
     */
    @Reference
    private transient AdminService adminService;
    
    private static final Pattern onlyAlphanumericCharactersInTitleRegex = Pattern.compile("[^a-zA-Z0-9]");
    
    /**
     * on change method.
     * The method handles the default values of pages for title, asset modification date
     */
    @Override
    public void onChange(java.util.List<ResourceChange> list) {
		LOG.debug("ResourceChangeListener :: Into the onChange method()");
		String resourcePath = null;
		try {	        
			Iterator<ResourceChange> resourceChangeIterator = list.iterator();
			try (ResourceResolver writeResourceResolver = adminService.getWriteService()){
				while (resourceChangeIterator.hasNext()) {
					ResourceChange resourceChange = resourceChangeIterator.next();
					resourcePath = resourceChange.getPath();
					if (resourcePath.contains(CommonConstants.JCR_CONTENT_METADATA)) {
						Resource writeAssetResource = writeResourceResolver.getResource(resourcePath);
						addDefaulTitleAndPublicationDate(resourcePath, writeAssetResource);
						writeAssetResource.getResourceResolver().commit();
					}
				}
			}
		} catch (UnsupportedOperationException exception) {
			LOG.error("Exception occured while writing into JCR properties", exception.getMessage());
		} catch (PersistenceException ex) {
			LOG.error("Unable to save data into JCR properties", ex.getMessage());
		}
        LOG.debug("ResourceChangeListener :: Exit from onChange method()"); 
    }
    
	private static String getAssetName(String resourcePath) {
		LOG.debug("ResourceChangeListener :: Into the getAssetName method()");
		String resourceFileName = null;
		String[] pathContents = resourcePath.split(CommonConstants.SLASH_STRING);
        if(pathContents != null){
            int pathContentsLength = pathContents.length;
            resourceFileName = pathContents[pathContentsLength-CommonConstants.HOME_LEVEL];
            if(resourceFileName.contains(CommonConstants.PERIOD)) {
            	int extensionIndex = resourceFileName.indexOf(CommonConstants.PERIOD);
            	resourceFileName = resourceFileName.substring(0, extensionIndex); 
            	resourceFileName = onlyAlphanumericCharactersInTitleRegex.matcher(resourceFileName).replaceAll(CommonConstants.SPACE_STRING);
            }	       
	     }
	    LOG.debug("ResourceChangeListener :: Exit from getAsset method()");    
		return resourceFileName;
	}

	private static void addDefaulTitleAndPublicationDate(String resourcePath, Resource writeAssetResource) {
		LOG.debug("ResourceChangeListener :: Into addDefaulTitleAndPublicationDate()");
		ModifiableValueMap propertiesMap = writeAssetResource.adaptTo(ModifiableValueMap.class);
		String assetFormat = propertiesMap.get(CommonConstants.DC_FORMAT, String.class);
		String assetFileName = getAssetName(resourcePath);
		addDefaultsForDocumentAssets(propertiesMap, assetFormat, assetFileName);
		LOG.debug("ResourceChangeListener :: Exit from addDefaulTitleAndPublicationDate()");
	}

	private static void addDefaultsForDocumentAssets(ModifiableValueMap propertiesMap,
			String assetFormat, String assetFileName) {
		LOG.debug("ResourceChangeListener :: Into the addDefaultsForDocumentAssets method()");
		if (assetFormat.contains(CommonConstants.APPLICATION)) {
			LOG.debug("ResourceChangeListener :: Validating mandatory fields for assets of document type");
			if (!(propertiesMap.containsKey(CommonConstants.DC_TITLE))) {
				propertiesMap.put(CommonConstants.DC_TITLE, assetFileName); 
			} 						 
			if (!(propertiesMap.containsKey(CommonConstants.ASSET_PUBLICATION_DATE))) {
				propertiesMap.put(CommonConstants.ASSET_PUBLICATION_DATE, Calendar.getInstance());             			
			}
		}
		LOG.debug("ResourceChangeListener :: Exit from addDefaultsForDocumentAssets method()");
	}
 
}
