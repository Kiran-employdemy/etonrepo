package com.eaton.platform.core.workflows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;
import com.eaton.platform.core.constants.DitaConstants;
import com.eaton.platform.core.util.CCMSUtils;

/**
 *
 * @author vsa
 *         The below process is for updating the metadata for publishing dita files to generated AEM pages.
 */
@Component(service = WorkflowProcess.class, property = { "process.label= Eaton CCMS - Update Site Page Metadata" })
public class CCMSSitePageMetadataUpdate implements WorkflowProcess {

    private static final String TIER_LEVEL = "tierLevel";

	private static final String PARTNER_PROGRAMME_TYPE = "partnerProgrammeType";

	protected final Logger logger = LoggerFactory.getLogger(CCMSSitePageMetadataUpdate.class);

    private static final String[] ASSET_PN_LIST = new String[] { "xmp:eaton-percolate-asset-id", "secureAsset", "productCategories",
            "accountType", "applicationAccess", "countries", "excludeCountries", "xmp:eaton-partner-program-type",
            "xmp:eaton-tier-level", "trackDownload" };
    private static final String[] ASSET_PN_TAG_LIST = new String[] { "xmp:eaton-language", "xmp:eaton-country", "xmp:eaton-content-type",
        "xmp:eaton-eaton-brand", "xmp:eaton-search-tabs", "xmp:eaton-topic", "xmp:eaton-persona", "xmp:eaton-audience", "xmp:eaton-segment",
        "xmp:eaton-customer-journey-stage", "xmp:eaton-product-taxonomy", "xmp:eaton-services-taxonomy",
        "xmp:eaton-support-taxonomy", "xmp:eaton-company-taxonomy", "xmp:eaton-myeaton-taxonomy",
        "xmp:eaton-business-unit-function-division", "xmp:eaton-product-attributes" };

    private static final String PV_XML_DOCUMENTATION = "xml-documentation";
    private static final String PN_PAGE_TYPE = "page-type";

    @Reference
    private ResourceResolverFactory resolverFactory;
    @Reference
    private QueryBuilder queryBuilder;

    @Override
    public void execute(final WorkItem item, final WorkflowSession wfsession, final MetaDataMap args)
        throws WorkflowException {

        final MetaDataMap meta = item.getWorkflowData().getMetaDataMap();
        logger.debug("Workflow metadata::: {}", meta);

        ResourceResolver resolver = null;
        Session session = null;

        try {

            resolver = CCMSUtils.getResourceResolver(resolverFactory, DitaConstants.FMDITA_SERVICEUSER);
            if (null == resolver) {
                logger.error("CCMSPDFMetadataUpdate resolver is empty.");
                return;
            }

            session = resolver.adaptTo(Session.class);

            if (null == meta) {
                logger.error("CCMSPDFMetadataUpdate metadata is empty.");
                return;
            }

            final String ditaOTGenerationSuccess = meta.get(DitaConstants.IS_SUCCESS, String.class);
            final String outputPath = meta.get(DitaConstants.GENERATED_PATH, String.class);
            final String outputName = meta.get(DitaConstants.OUTPUT_NAME, String.class);

            if (StringUtils.isBlank(ditaOTGenerationSuccess) || ditaOTGenerationSuccess.equals("false") || StringUtils.isBlank(
                outputPath)) {
                logger.error("Dita Output generation failure ");
                return;
            }

            if (!DitaConstants.AEM_SITE.equalsIgnoreCase(outputName)) {
                return;
            }

            final String sourcePath = outputPath.substring(0, outputPath.indexOf(DitaConstants.HTML_EXTENSION));
            final Map<String, Object> predicateMap = getPredicateMap(sourcePath);
            final Query query = queryBuilder.createQuery(PredicateGroup.create(predicateMap), session);
            final SearchResult searchResult = query.getResult();

            iteratePage(resolver, session, searchResult);

            Resource ditaMapPageContentResource = resolver.getResource(sourcePath + DitaConstants.FORWARD_SLASH + JcrConstants.JCR_CONTENT);
            if(null != ditaMapPageContentResource && null != ditaMapPageContentResource.adaptTo(Node.class)) {
            	ditaMapPageContentResource.adaptTo(Node.class).setProperty(PN_PAGE_TYPE, PV_XML_DOCUMENTATION);
            }

            if (resolver.hasChanges()) {
                resolver.commit();
            }

        } catch (Exception e) {
            logger.error("Error in UpdateCCMSPDFMetadata {}", e);
        } finally {

            if (null != resolver && resolver.isLive()) {
                resolver.close();
            }
        }

    }

    private void iteratePage(ResourceResolver resolver, Session session, final SearchResult searchResult) throws RepositoryException {
        final Iterator<Resource> resources = searchResult.getResources();
        while (resources.hasNext()) {
            final Resource resource = resources.next();
            Resource contentResource = resolver.getResource(resource.getPath().concat(DitaConstants.FORWARD_SLASH).concat(
                JcrConstants.JCR_CONTENT));
            if (null != contentResource) {
                Node contentResourceNode = contentResource.adaptTo(Node.class);
                ValueMap properties = contentResource.getValueMap();
                if (properties.containsKey(DitaConstants.PN_SOURCE_PATH)) {

                    final Node ditaResource = session.getNode(properties.get(DitaConstants.PN_SOURCE_PATH, String.class));

                    for (final String propertyName : ASSET_PN_LIST) {
                        updatePageProperties(ditaResource, contentResourceNode, propertyName);
                    }

                    List<String> cqTags = new ArrayList<>();

                    for (final String propertyName : ASSET_PN_TAG_LIST) {
                        collectTaglist(ditaResource, cqTags, propertyName);
                    }

                    if (!cqTags.isEmpty()) {
                        String[] cqTagsArray = new String[cqTags.size()];
                        cqTagsArray = cqTags.toArray(new String[0]);
                        contentResourceNode.setProperty(DitaConstants.CQ_TAGS, (Value) null);
                        contentResourceNode.setProperty(DitaConstants.CQ_TAGS, cqTagsArray);
                    }
                    contentResourceNode.setProperty(PN_PAGE_TYPE, PV_XML_DOCUMENTATION);
                }
            }
        }
    }

    /**
     * This is for collecting list of Tags.
     *
     * @param ditaMapResource ditaMapResource
     * @param cqTags cqTags
     * @param propertyName propertyName
     * @throws RepositoryException
     */
    private void collectTaglist(final Node ditaMapResource, List<String> cqTags, String propertyName) throws RepositoryException {

        if (!ditaMapResource.getNode(DitaConstants.JCR_CONTENT_METADATA).hasProperty(propertyName)) {
            return;
        }

        if (ditaMapResource.getNode(DitaConstants.JCR_CONTENT_METADATA).getProperty(propertyName).isMultiple()) {
            final Value[] values = ditaMapResource.getNode(DitaConstants.JCR_CONTENT_METADATA).getProperty(propertyName).getValues();
            for (Value value : values) {
                cqTags.add(value.getString());
            }
        } else {
            final Value value = ditaMapResource.getNode(DitaConstants.JCR_CONTENT_METADATA).getProperty(propertyName).getValue();
            cqTags.add(value.getString());
        }

    }

    /**
     * Update the page properties using asset metadata.
     *
     * @param ditaAsset ditaAsset
     * @param pageNode pageNode
     * @param propertyName propertyName
     * @throws RepositoryException
     */
    private void updatePageProperties(final Node ditaAsset, final Node pageNode, final String propertyName)
        throws RepositoryException {

        if (null == pageNode || null == ditaAsset) {
            return;
        }

        if (!ditaAsset.getNode(DitaConstants.JCR_CONTENT_METADATA).hasProperty(propertyName)) {
            return;
        }

        if (ditaAsset.getNode(DitaConstants.JCR_CONTENT_METADATA).getProperty(propertyName).isMultiple()) {
            final Value[] values = ditaAsset.getNode(DitaConstants.JCR_CONTENT_METADATA).getProperty(propertyName).getValues();
            if(StringUtils.equals("xmp:eaton-partner-program-type", propertyName)) {
            	updateMultiValueProperties(pageNode,PARTNER_PROGRAMME_TYPE, values);
            }
            else if(StringUtils.equals("xmp:eaton-tier-level", propertyName)) {
            	updateMultiValueProperties(pageNode,TIER_LEVEL, values);
            }
            else {
            	updateMultiValueProperties(pageNode,propertyName, values);
            }
        } else {
            final Value value = ditaAsset.getNode(DitaConstants.JCR_CONTENT_METADATA).getProperty(propertyName).getValue();
            if(StringUtils.equals("xmp:eaton-partner-program-type", propertyName)) {
            	updateSingleValueProperties(pageNode,PARTNER_PROGRAMME_TYPE, value);
            }
            else if(StringUtils.equals("xmp:eaton-tier-level", propertyName)) {
            	updateSingleValueProperties(pageNode,TIER_LEVEL, value);
            }
            else {
            	updateSingleValueProperties(pageNode,propertyName, value);
            }
        }

    }

    /**
     * Update single value properties.
     *
     * @param pageNode the page node
     * @param propertyName the property name
     * @param value the value
     * @throws RepositoryException the repository exception
     */
    private void updateSingleValueProperties(Node pageNode,String propertyName,Value value) throws RepositoryException {
    	pageNode.setProperty(propertyName, (Value) null);
        pageNode.setProperty(propertyName, value);
    }

    /**
     * Update multi value properties.
     *
     * @param pageNode the page node
     * @param propertyName the property name
     * @param values the values
     * @throws RepositoryException the repository exception
     */
    private void updateMultiValueProperties(Node pageNode,String propertyName,Value[] values) throws RepositoryException {
    	pageNode.setProperty(propertyName, (Value) null);
        pageNode.setProperty(propertyName, values);
    }

    /**
     * Private method to return the Search predicate map
     *
     * @param outputPath
     *            pathOfOutputPage
     * @return predicateMap
     */
    private Map<String, Object> getPredicateMap(final String outputPath) {

        final Map<String, Object> predicateMap = new HashMap<>();
        predicateMap.put("p.limit", "-1");
        predicateMap.put("path", outputPath);
        predicateMap.put("type", "cq:Page");

        return predicateMap;
    }

}
