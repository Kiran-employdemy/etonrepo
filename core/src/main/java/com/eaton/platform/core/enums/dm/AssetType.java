package com.eaton.platform.core.enums.dm;

import com.day.cq.dam.api.s7dam.constants.S7damConstants;
import com.eaton.platform.core.constants.CommonConstants;
import org.apache.commons.lang3.StringUtils;

import static com.eaton.platform.core.constants.CommonConstants.FILE_TYPE_APPLICATION_PDF;

public enum AssetType {
    IMAGE(S7damConstants.IMAGE,false),
    ANIMATEDGIF(CommonConstants.ANIMATEDGIF,true),
    PDF(FILE_TYPE_APPLICATION_PDF,true),
    AUDIO(CommonConstants.AUDIO,true),
    VOICE(CommonConstants.VIDEO,true),
    UNKNOWN(StringUtils.EMPTY,false);
    private final String format;
    private final String middle;
    private final boolean isContentServer;
    AssetType(String format,boolean isContentServer){
        this.format = format;
        this.middle = isContentServer ? CommonConstants.IS_CONTENT + CommonConstants.SLASH_STRING :
                CommonConstants.IS_IMAGE + CommonConstants.SLASH_STRING;
        this.isContentServer = isContentServer;
    }

    public String getFormat() {
        return format;
    }

    public static AssetType findAssetTypeByFormat(String inFormat){
        for(AssetType type : AssetType.values()){
            if(type.format.equals(inFormat)){
                return type;
            }
        }
        return UNKNOWN;
    }

    public String stitchPath(String domain,String fileName,String scene7File, String scene7Folder){
        StringBuilder builder = new StringBuilder();
        builder.append(domain).append(middle);
        if(!isContentServer){
            // image case
            return builder.append(scene7File).toString();
        }
        if(this == AssetType.VOICE || this == AssetType.AUDIO){
            // audio && video case
            return builder.append(scene7File).toString();
        }
        return builder.append(scene7Folder).append(fileName).toString();
    }
}
