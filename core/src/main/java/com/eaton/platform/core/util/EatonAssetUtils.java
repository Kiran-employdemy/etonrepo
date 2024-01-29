package com.eaton.platform.core.util;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.dam.api.Rendition;
import com.day.cq.dam.commons.util.PrefixRenditionPicker;
import com.eaton.platform.core.constants.CommonConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;

/**
 * EatonAssetUtils.java
 * About : Having utility methods to extract asset metadata and renditions
 * Author: Satya
 */
public class EatonAssetUtils {


    /**
     Utility method to get the thumbnail images.
     * @param asset Current Asset
     * @param thumbnail Ex : ASSET_140_100_THUMBNAIL, ASSET_319_319_THUMBNAIL
     * @param renditionId Ex : .319, .1240
     * @param prefixType Thumbnail Prefix Type. Ex : thumb or web
     * @return Thumbnail Path
     */
    public static String getThumbnailImage(Asset asset, String thumbnail, String renditionId, String prefixType){
        PrefixRenditionPicker picker = new PrefixRenditionPicker(prefixType + renditionId, true);
        Rendition rendition = picker.getRendition(asset);
        if(rendition != null && !rendition.getPath().contains(DamConstants.ORIGINAL_FILE)){
            return asset.getPath() + thumbnail;
        }
        return StringUtils.EMPTY;
    }

    /**
     * Returns tracking flag (yes/no) of an asset.
     * @param resource current Resource
     * @return 'yes' or 'no'
     */
    public static String trackThisAsset(Resource resource){
        if(!ResourceUtil.isNonExistingResource(resource)) {
            String trackDownload = resource.getValueMap().get(CommonConstants.ASSET_TRACK_DOWNLOAD, String.class);
            if (trackDownload != null && trackDownload.equalsIgnoreCase(CommonConstants.TRUE)) {
                return CommonConstants.YES;
            } else {
                return CommonConstants.NO;
            }
        }
        return CommonConstants.NO;
    }
}
