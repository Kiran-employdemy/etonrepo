package com.eaton.platform.core.workflows;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
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
 *         The below process is for updating the metadata for publishing ditamap to generated PDF.
 */
@Component(service = WorkflowProcess.class, property = { "process.label= Eaton CCMS - Update PDF Metadata" })
public class CCMSPDFMetadataUpdate implements WorkflowProcess {


    protected final Logger logger = LoggerFactory.getLogger(CCMSPDFMetadataUpdate.class);

    private static final String[] ASSET_PN_LIST = new String[] { "dc:title", "author", "dc:description", "xmp:eaton-product-taxonomy", "xmp:eaton-services-taxonomy",
            "xmp:eaton-support-taxonomy", "xmp:eaton-company-taxonomy", "xmp:eaton-myeaton-taxonomy", "xmp:eaton-topic","xmp:eaton-content-type",
            "xmp:eaton-b-line-submittal-builder", "xmp:eaton-language", "xmp:eaton-eaton-brand", "xmp:eaton-country", "xmp:eaton-news",
            "xmp:eaton-topic", "xmp:eaton-search-tabs", "xmp:eaton-persona", "xmp:eaton-customer-journey-stage", "xmp:eaton-audience", "xmp:eaton-itar-compliance",
            "xmp:eaton-segment", "xmp:eaton-image-rights", "xmp:eaton-photo-orientation", "xmp:eaton-rights-restricted-detail", "prism:expirationDate",
            "assetPublicationDate", "bornOnDate", "default", "xmp:eaton-product-attributes", "xmp:eaton-business-unit-function-division",
            "xmp:eaton-enovia-number", "eaton-article-catalog-number", "xmp:eaton-percolate-asset-id", "xmp:eaton-sales-enablement", "eaton-copyright-status",
            "eaton-copyright-notice", "xmp:eaton-metadata-review", "secureAsset", "productCategories", "accountType", "applicationAccess",
            "countries", "xmp:eaton-partner-program-type", "xmp:eaton-tier-level", "dc:subject"};

    @Reference
    private ResourceResolverFactory resolverFactory;

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

            if (!DitaConstants.OUTPUT_PDF.equalsIgnoreCase(outputName)) {
                return;
            }

            final Node pdfAssetResource = session.getNode(outputPath);
            final Node ditaMapResource = session.getNode(pdfAssetResource.getNode(JcrConstants.JCR_CONTENT).getProperty("sourcePath")
                .getString());


            for (final String propertyName : ASSET_PN_LIST) {
                updatePDFMetaData(ditaMapResource, pdfAssetResource, propertyName);
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
    /**
     * @param ditaAsset ditaAsset
     * @param pdfAssetNode pdfAssetNode
     * @param propertyName propertyName
     * @throws RepositoryException
     */
    private void updatePDFMetaData(final Node ditaAsset, final Node pdfAssetNode, final String propertyName)
        throws RepositoryException {

        if (!ditaAsset.getNode(DitaConstants.JCR_CONTENT_METADATA).hasProperty(propertyName)) {
            return;
        }



        if (ditaAsset.getNode(DitaConstants.JCR_CONTENT_METADATA).getProperty(propertyName).isMultiple()) {
            final Value[] values = ditaAsset.getNode(DitaConstants.JCR_CONTENT_METADATA).getProperty(propertyName).getValues();
            pdfAssetNode.getNode(DitaConstants.JCR_CONTENT_METADATA).setProperty(propertyName, (Value)null);
            pdfAssetNode.getNode(DitaConstants.JCR_CONTENT_METADATA).setProperty(propertyName, values);
        } else {
            final Value value = ditaAsset.getNode(DitaConstants.JCR_CONTENT_METADATA).getProperty(propertyName).getValue();
            pdfAssetNode.getNode(DitaConstants.JCR_CONTENT_METADATA).setProperty(propertyName, (Value)null);
            pdfAssetNode.getNode(DitaConstants.JCR_CONTENT_METADATA).setProperty(propertyName, value);
        }

    }

}
