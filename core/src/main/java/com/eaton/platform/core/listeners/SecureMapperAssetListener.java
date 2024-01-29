package com.eaton.platform.core.listeners;

import static com.eaton.platform.core.constants.CommonConstants.RUNMODE_PUBLISH;

import java.util.Iterator;
import java.util.Set;

import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaton.platform.core.services.secure.SecureMapperService;
import com.eaton.platform.integration.auth.constants.SecureConstants;

/**
 * By Satya
 */
@Component(
        service = ResourceChangeListener.class,
        property = {
                ResourceChangeListener.PATHS + "=" + SecureConstants.ROLLMAPPER_JSON,
                ResourceChangeListener.CHANGES + "=" + "CHANGED"
        }
)
public class SecureMapperAssetListener implements ResourceChangeListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecureMapperAssetListener.class);

    @Reference
    private SlingSettingsService slingSettingsService;

    @Reference
    private SecureMapperService secureMapperService;

    @Override
    public void onChange(java.util.List<ResourceChange> list) {
        LOGGER.debug("onChange Start :: SecureMapperAssetListener");
        Set<String> runModes = slingSettingsService.getRunModes();
        boolean isRoleMapperUpdate = false;
        try {
            LOGGER.debug("Run Mode is Publish ::: {}", runModes.contains(RUNMODE_PUBLISH));
            if (runModes.contains(RUNMODE_PUBLISH)) {
                Iterator<ResourceChange> resourceChangeIterator = list.iterator();
                while (resourceChangeIterator.hasNext()) {
                    ResourceChange resourceChange = resourceChangeIterator.next();
                    LOGGER.debug(" :::::::::: Role Mapper JSON change event identified :::::::: {}", resourceChange.getPath());
					if (resourceChange.getPath().contains(SecureConstants.ROLLMAPPER_JSON)
							&& secureMapperService != null) {
						isRoleMapperUpdate = true;
						LOGGER.debug("isRoleMapperUpdate: {}",isRoleMapperUpdate);
                    }
                }
                
				if (isRoleMapperUpdate) {
					LOGGER.debug("secureMapperService Trigger : START ");
					secureMapperService.loadUpdatedSecureMapperFile();
					LOGGER.debug("secureMapperService Trigger : END ");
				}
                
            }
        } catch (UnsupportedOperationException exception) {
            LOGGER.error("SecureMapperAssetListener: OnChange Event, Exception", exception);
        }
        LOGGER.debug("onChange END :: SecureMapperAssetListener");
    }
}
