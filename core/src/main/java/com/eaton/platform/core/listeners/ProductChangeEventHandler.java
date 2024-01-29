package com.eaton.platform.core.listeners;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import static com.eaton.platform.core.constants.CommonConstants.RUNMODE_AUTHOR;

import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.adobe.granite.references.ReferenceAggregator;
import com.eaton.platform.core.constants.PimImporterConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.integration.informatica.constants.InformaticaConstants;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.NameConstants;
import com.eaton.platform.core.constants.AEMConstants;
import com.day.cq.replication.Replicator;
import com.eaton.platform.core.constants.CommonConstants;

import javax.jcr.Session;

import static com.eaton.platform.integration.endeca.constants.EndecaConstants.RUNMODE_ENDECA;

/**
 * The handler is used to listen to any product resource changes and to update
 * last
 * modified (cq:lastModified) property of all the referenced pages
 */
@Component(service = ResourceChangeListener.class, property = {
        AEMConstants.SERVICE_VENDOR_EATON,
        AEMConstants.SERVICE_DESCRIPTION + "Listens to Product resource modified  Event.",
        ResourceChangeListener.PATHS + "=" + "/var/commerce/products/eaton",
        ResourceChangeListener.CHANGES + "=" + "CHANGED"
})
public class ProductChangeEventHandler implements ResourceChangeListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductChangeEventHandler.class);

    @Reference
    private ReferenceAggregator referenceAggregator;

    /**
     * Admin Service.
     */
    @Reference
    private transient AdminService adminService;

    @Reference
    private SlingSettingsService slingSettingsService;

    @Reference
    private Replicator replicator;

    @Override
    public void onChange(List<ResourceChange> list) {

        LOGGER.debug("onChange Start :: Changes to product node should update the page properties ");
        Set<String> runModes = slingSettingsService.getRunModes();
        if (runModes.contains(RUNMODE_AUTHOR) || runModes.contains(RUNMODE_ENDECA)) {
            handleProductChange(list);
        }

    }

    private void handleProductChange(List<ResourceChange> list) {
        Iterator<ResourceChange> resourceChangeIterator = list.iterator();
        while (resourceChangeIterator.hasNext()) {
            ResourceChange resourceChange = resourceChangeIterator.next();
            LOGGER.debug(" :::::::::: Product change event identified :::::::: {}", resourceChange.getPath());
            ResourceResolver resourceResolver = null;
            try {
                resourceResolver = adminService.getWriteService();
                Resource changedResource = resourceResolver.getResource(resourceChange.getPath());
                Resource productResource = getProductResource(changedResource);

                if (productResource == null) {
                    return;
                }

                List<com.adobe.granite.references.Reference> refList = referenceAggregator
                        .createReferenceList(productResource);
                List<String> uniqueReferenceList = new ArrayList<String>();
                for (int i = 0; i < refList.size(); i++) {
                    Resource target = refList.get(i).getTarget();
                    String resourcePath = target.getPath() + InformaticaConstants.BACKWARD_SLASH +JcrConstants.JCR_CONTENT;
                    Resource resourceWithJCRContent = resourceResolver.getResource(resourcePath);

                    if (uniqueReferenceList.contains(resourcePath)) {
                        continue;
                    }
                    if (resourceWithJCRContent != null) {
                        ModifiableValueMap properties = resourceWithJCRContent.adaptTo(ModifiableValueMap.class);
                            properties.put(NameConstants.PN_PAGE_LAST_MOD, Calendar.getInstance());
                            uniqueReferenceList.add((resourcePath));
                            Session session = resourceResolver.adaptTo(Session.class);
                            if (resourceChange.getUserId() != null && resourceChange.getUserId().equalsIgnoreCase("replication-service")) {
                                //activate the referenced pages with the help of replication api
                                replicateContent(session, target);
                            }
                            resourceWithJCRContent.getResourceResolver().commit();
                    }
                }
                resourceResolver.commit();

            } catch (PersistenceException e) {
                LOGGER.error("Persistence Exception e{}", e.getMessage());

            } finally {

                if (resourceResolver != null && resourceResolver.isLive()) {

                    resourceResolver.close();

                }
            }
        }
        LOGGER.debug("onChange END :: Changes to product node should update the page properties");
    }

    private static Resource getProductResource(Resource resource) {
        ValueMap properties = resource.getValueMap();
        String resourceType = properties.get(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY, String.class);
        if (resourceType == null || !resourceType.equals(PimImporterConstants.RESOURCE_TYPE_PATH)) {
            Resource parentResource = resource.getParent();

            if (parentResource == null) {
                return null;
            }

            return getProductResource(parentResource);
        }
        return resource;
    }

    private void replicateContent(Session session, Resource target) {
        ResourceResolver resourceResolver = null;
        try{
            resourceResolver = adminService.getWriteService();
            if(!target.getPath().contains(CommonConstants.LANGUAGE_MASTERS_NODE_NAME)) {

                String targetResourcePath = target.getPath() + InformaticaConstants.BACKWARD_SLASH + JcrConstants.JCR_CONTENT;
                Resource targetResourceWithJCRContent = resourceResolver.getResource(targetResourcePath);
                if (targetResourceWithJCRContent != null) {
                    ModifiableValueMap properties = targetResourceWithJCRContent.adaptTo(ModifiableValueMap.class);
                    if (properties.get(NameConstants.PN_PAGE_LAST_REPLICATION_ACTION) != null && (!properties.get(NameConstants.PN_PAGE_LAST_REPLICATION_ACTION).equals(CommonConstants.DEACTIVATE))) {

                        replicator.replicate(session, ReplicationActionType.ACTIVATE, target.getPath());
                    }
                }
            }
        }
        catch (ReplicationException e){
            LOGGER.error(e.getMessage(), e);
        }
    }
}