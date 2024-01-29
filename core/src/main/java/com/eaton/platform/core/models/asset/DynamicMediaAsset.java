package com.eaton.platform.core.models.asset;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.day.cq.dam.api.s7dam.constants.S7damConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.enums.dm.AssetType;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

import static com.day.cq.dam.api.DamConstants.DC_FORMAT;
import static com.day.cq.dam.scene7.api.constants.Scene7Constants.*;
import static com.day.cq.dam.scene7.api.constants.Scene7Constants.PN_S7_FILE;

import java.util.Optional;

@Model(adaptables = Asset.class,defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class DynamicMediaAsset {

    private static final Logger LOG = LoggerFactory.getLogger(DynamicMediaAsset.class);
    @Self
    private Asset asset;

    private String dmAssetPathSuffix;

    @PostConstruct
    void initModel(){
        LOG.debug("DynamicMediaAsset :: initModel :: Start");
        LOG.debug("DynamicMediaAsset :: initModel :: Asset Path: {}",asset.getPath());
        buildDmAssetPathSuffix();
        LOG.debug("DynamicMediaAsset :: initModel :: End");
    }

    protected void buildDmAssetPathSuffix(){
        String scene7Domain = asset.getMetadataValue(PN_S7_DOMAIN);
        LOG.debug("DynamicMediaAsset :: buildDmAssetPathSuffix:: scene7Domain: {}",scene7Domain);
        if (scene7Domain != null && asset.getMetadataValue(PN_S7_FILE_STATUS).equals(PV_S7_PUBLISH_COMPLETE)) {
            String scene7Type = asset.getMetadataValue(PN_S7_TYPE);
            String scene7Folder = asset.getMetadataValue(PN_S7_FOLDER);
            String scene7File = asset.getMetadataValue(PN_S7_FILE);
            String format = Optional.ofNullable(asset.getMetadataValue(DC_FORMAT)).orElse(StringUtils.EMPTY);
            String type = format;
            LOG.debug("DynamicMediaAsset :: buildDmAssetPathSuffix :: scene7Type: {} - scene7Folder: {} - scene7File: {} - format: {}",scene7Type,scene7Folder,scene7File,format);
            if (format.contains(S7damConstants.IMAGE)) {
                LOG.debug("DynamicMediaAsset :: buildDmAssetPathSuffix :: Image Case: {}",format);
                type = S7damConstants.IMAGE;
                // AnimatedGif and PDF use the s7ContentServer and needs the folder path and file extension
                if (StringUtils.isNotBlank(scene7Type) && scene7Type.equalsIgnoreCase(CommonConstants.ANIMATEDGIF)) {
                    type = CommonConstants.ANIMATEDGIF;
                }
            }
            LOG.debug("AssetType Enum: {}",type);
            AssetType assetType = AssetType.findAssetTypeByFormat(type);
            if(assetType != AssetType.UNKNOWN) {
                this.dmAssetPathSuffix = assetType.stitchPath(scene7Domain, asset.getName(), scene7File, scene7Folder);
            }
        }
    }

    
    private String getRendition(String renditionWanted){
        LOG.debug("DynamicMediaAsset :: getRendition :: Attempting to get rendition");
        Rendition rendition = asset.getRendition(renditionWanted);
        if (null != rendition) {
            LOG.debug("DynamicMediaAsset :: getRendition :: Rendition Path: {}",rendition.getPath());
            return rendition.getPath();
        }
        return asset.getPath();
    }
    

    public String getSCImageOrDefault(String imageProfile,String renditionWanted){
        LOG.debug("SmartCropImageModel :: getSCImageOrDefault :: Image Profile: {} - default rendition: {}",imageProfile,renditionWanted);
        if(StringUtils.isBlank(dmAssetPathSuffix)){
            // meaning smart-crop image doesn't exist
           return getRendition(renditionWanted);
        }
        return new StringBuilder().append(dmAssetPathSuffix).append(imageProfile).toString();
    }

}
