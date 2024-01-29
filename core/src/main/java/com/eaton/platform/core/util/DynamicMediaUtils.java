package com.eaton.platform.core.util;

import com.day.cq.commons.Externalizer;
import com.day.cq.dam.api.s7dam.constants.S7damConstants;
import com.eaton.platform.core.constants.CommonConstants;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import java.util.Set;

import static com.day.cq.dam.api.DamConstants.DC_FORMAT;
import static com.day.cq.dam.scene7.api.constants.Scene7Constants.*;

/**
 * The Class DynamicMediaUtils.
 */
public class DynamicMediaUtils {
    /** The Constant LOGGER. */
    private static final Logger LOG = LoggerFactory.getLogger(DynamicMediaUtils.class);

    /**
     * Gets the asset's Dynamic Media Publish URL.
     *
     * @param metadata The asset's metadata node.
     * @param dmBaseUrl The Dynamic Media Base URL.
     * @return The asset's Dynamic Media Publish URL.
     */
    public static String getDynamicMediaPublishURL(Node metadata, String dmBaseUrl) {
        try {
            String scene7Domain = metadata.getProperty(PN_S7_DOMAIN).getString();
            final Set<String> instanceRunmode = CommonUtil.getRunModes();
            // if they have scene7/DM and the asset it published create the URL
            if (scene7Domain != null && (instanceRunmode.contains(Externalizer.PUBLISH) || metadata.getProperty(PN_S7_FILE_STATUS).getString().equals(PV_S7_PUBLISH_COMPLETE))) {
                String s7ImageServer;
                if (StringUtils.isEmpty(dmBaseUrl)) {
                    s7ImageServer = scene7Domain + CommonConstants.IS_IMAGE + CommonConstants.SLASH_STRING;
                } else {
                    s7ImageServer = dmBaseUrl + CommonConstants.IS_IMAGE + CommonConstants.SLASH_STRING;
                }
                String s7ContentServer;
                if (StringUtils.isEmpty(dmBaseUrl)) {
                    s7ContentServer = scene7Domain + CommonConstants.IS_CONTENT + CommonConstants.SLASH_STRING;
                } else {
                    s7ContentServer = dmBaseUrl + CommonConstants.IS_CONTENT + CommonConstants.SLASH_STRING;
                }
                String scene7Type = metadata.getProperty(PN_S7_TYPE).getString();
                String scene7Folder = metadata.getProperty(PN_S7_FOLDER).getString();
                String scene7File = metadata.getProperty(PN_S7_FILE).getString();
                String format = metadata.getProperty(DC_FORMAT).getString();

                // create URL for images (PNG, JPG, GIF)
                if (format.contains(S7damConstants.IMAGE)) {
                    // AnimatedGif and PDF use the s7ContentServer and needs the folder path and file extension
                    if (scene7Type.equalsIgnoreCase(CommonConstants.ANIMATEDGIF)) {
                        String [] parentPath = metadata.getParent().getParent().getPath().split(CommonConstants.SLASH_STRING);
                        return s7ContentServer + scene7Folder + parentPath[parentPath.length - 1];
                    } else {
                        // all other images use the s7ImageServer and file name
                        return s7ImageServer + scene7File;
                    }
                    // create URL for PDF that will use the s7ContentServer and needs the folder path and file extension
                } else if (scene7Type.equalsIgnoreCase(CommonConstants.PDF)) {
                    String [] parentPath = metadata.getParent().getParent().getPath().split(CommonConstants.SLASH_STRING);
                    return s7ContentServer + scene7Folder + parentPath[parentPath.length - 1];
                    // create URL for audio and video that will use the s7ContentServer and file name
                } else if (format.contains(CommonConstants.AUDIO) || format.contains(CommonConstants.VIDEO)) {
                    return s7ContentServer + scene7File;
                } else {
                    // intentionally left blank, resolves sonarlint
                }
            }
        } catch (RepositoryException ex) {
            LOG.error("Error in getting the dynamic media publish url {}", ex.getMessage());
        }
        return null;
    }
}
