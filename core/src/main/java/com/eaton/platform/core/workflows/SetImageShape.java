package com.eaton.platform.core.workflows;

/**
 * SetImageShape.java
 * ------------------
 * Calculates an image's aspect ratio based on its height and width and sets the
 * image shape metadata field of the asset.
 *
 * Workflow Process Step Arguments:
 * In order for this process step to function properly, you will need to provide five comma-delimited arguments
 * when configuring the workflow. The five arguments correspond to:
 * 1. A client-specific destination asset node property where the aspect ratio will be stored (ex: jcr:content/metadata/xmp:freedom-aspect-ratio)
 * 2. A client-specific dropdown value representing a 'tall' asset (ex: freedommarketing-aspect-ratio:tall)
 * 3. A client-specific dropdown value representing a 'square' asset (ex: freedommarketing-aspect-ratio:square)
 * 4. A client-specific dropdown value representing a 'wide' asset (ex: freedommarketing-aspect-ratio:wide)
 * 5. A client-specific dropdown value representing a 'panoramic' asset (ex: freedommarketing-aspect-ratio:panoramic)
 *
 * Example argument input:
 * jcr:content/metadata/xmp:freedom-aspect-ratio,freedommarketing-aspect-ratio:tall,freedommarketing-aspect-ratio:square,freedommarketing-aspect-ratio:wide,freedommarketing-aspect-ratio:panoramic
 * ------------------
 * By: Julie Rybarczyk (julie@freedomdam.com)
 */

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.eaton.platform.core.util.WorkflowUtils;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import static com.day.cq.commons.jcr.JcrConstants.JCR_CONTENT;
import static com.day.cq.dam.api.DamConstants.TIFF_IMAGELENGTH;
import static com.day.cq.dam.api.DamConstants.TIFF_IMAGEWIDTH;

@Component(
        service = { WorkflowProcess.class },
        property = {
                Constants.SERVICE_DESCRIPTION + "=" + "Calculates the aspect ratio and sets the image shape.",
                Constants.SERVICE_VENDOR + "=" + "Freedom Marketing",
                "process.label" + "=" + "FM - Set Image Shape"
        },
        immediate = true
)
public class SetImageShape implements WorkflowProcess {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private String destinationProperty = "";
    private String tallValue = "";
    private String squareValue = "";
    private String wideValue = "";
    private String panoramicValue = "";

    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap args) throws WorkflowException {
        log.debug("---------- Executing Set Image Shape Process ----------");
        WorkflowUtils.logWorkflowMetadata(log, workItem, args);

        try {
            Session session = workflowSession.adaptTo(Session.class);
            Node root = session.getRootNode();

            // Exit if arguments are invalid.
            if(!parseArguments(args)) {
                return;
            }

            // Get the asset type node to update.
            Node payloadNode = WorkflowUtils.getPayloadNode(root, workItem);
            Node assetNode = WorkflowUtils.getAssetNode(payloadNode, log);
            log.debug("payloadNode: {}", payloadNode.getPath());

            if (assetNode == null) {
                log.error("Unable to find asset node for {}. Node will not be updated.", payloadNode.getPath());
                return;
            }

            log.debug("assetNode: {}", assetNode.getPath());

            if (!WorkflowUtils.isDestinationPropertyValid(destinationProperty, log)) {
                return;
            }

            if (assetNode.getNode(JCR_CONTENT + "/metadata").hasProperty(TIFF_IMAGELENGTH)
                    && assetNode.getNode(JCR_CONTENT + "/metadata").hasProperty(TIFF_IMAGEWIDTH)) {
                long height = assetNode.getNode(JCR_CONTENT + "/metadata").getProperty(TIFF_IMAGELENGTH).getValue().getLong();
                log.debug("height: {}", height);

                long width = assetNode.getNode(JCR_CONTENT + "/metadata").getProperty(TIFF_IMAGEWIDTH).getValue().getLong();
                log.debug("width: {}", width);

                BigDecimal aspectRatio = getAspectRatioDecimal(width, height);
                log.debug("aspectRatio: {}", aspectRatio);

                String imageShape = getImageShape(aspectRatio);
                log.debug("Image Shape: {}", imageShape);

                assetNode.getNode(WorkflowUtils.getPath(destinationProperty)).setProperty(
                        WorkflowUtils.getName(destinationProperty), imageShape);
            }
            session.save();
        } catch (RepositoryException e) {
            log.error("{}", e);
        }
    }

    /**
     * Given the aspect ratio of an image, returns the associated image shape.
     * @param aspectRatio
     * @return
     */
    private String getImageShape(BigDecimal aspectRatio) {
        /*
        < 1.00 = Tall
        1.00 = Square
        > 1.00 && < 2.00 = Wide
        >= 2.00 = Panoramic
        */
        if (aspectRatio.compareTo(new BigDecimal(1)) == -1) {
            return tallValue;
        } else if (aspectRatio.compareTo(new BigDecimal(1)) == 0) {
            return squareValue;
        } else if (aspectRatio.compareTo(new BigDecimal(2)) == -1) {
            return wideValue;
        } else {
            return panoramicValue;
        }
    }

    /**
     * Given the height and width of an image, calculates and returns the corresponding aspect ratio
     * in decimal form rounded to the nearest hundredth.
     * @param widthLong Image width
     * @param heightLong Image height
     * @return Aspect Ratio in decimal
     */
    private static BigDecimal getAspectRatioDecimal(long widthLong, long heightLong) {
        BigDecimal width = BigDecimal.valueOf(widthLong);
        BigDecimal height = BigDecimal.valueOf(heightLong);
        BigDecimal aspectRatio = width.divide(height, 2, RoundingMode.HALF_UP);
        return aspectRatio;
    }

    /**
     * Currently not in use- potential for future use.
     * Given the height and width of an image, calculates and returns the corresponding aspect ratio.
     * @param widthLong Image width
     * @param heightLong Image height
     * @return Aspect ratio
     */
    private static String getAspectRatio(long widthLong, long heightLong) {
        BigInteger width = BigInteger.valueOf(widthLong);
        BigInteger height = BigInteger.valueOf(heightLong);
        BigInteger gcd = width.gcd(height);
        return "" + width.divide(gcd) + ":" + height.divide(gcd);
    }

    private boolean parseArguments(MetaDataMap args) {
        String rawInput = "NULL";
        if (args.containsKey("PROCESS_ARGS")) {
            rawInput = args.get("PROCESS_ARGS", String.class);
        }
        String[] arguments = rawInput.split(",");
        if (arguments.length == 5) {
            destinationProperty = arguments[0];
            tallValue = arguments[1];
            squareValue = arguments[2];
            wideValue = arguments[3];
            panoramicValue = arguments[4];
            return true;
        }

        log.error("{} is not a valid configuration for {}. Please see the class file for documentation", rawInput, getClass().getName());
        return false;
    }
}