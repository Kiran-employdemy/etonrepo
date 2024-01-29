package com.eaton.platform.integration.endeca.pojo.asset;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.search.api.VendorAssetSearchRecord;
import com.eaton.platform.integration.endeca.pojo.base.BaseEndecaDocument;

import static com.eaton.platform.integration.endeca.pojo.base.EndecaFieldEnum.*;

/**
 * Endeca's implementation of VendorAssetSearchRecord
 */
public class EndecaAssetDocument extends BaseEndecaDocument implements VendorAssetSearchRecord {

    public String getUrl() {
        return getValueFor(URL);
    }

    public String getImage() {
        return getValueFor(IMAGE);
    }

    public Long getEpochDate() {
        String epochString = getValueFor(EPOCHPUBDATE);
        if (epochString.isEmpty()) {
            return null;
        }
        return Long.parseLong(epochString);
    }

    public String getDescription() {
        return getValueFor(DESCRIPTION);
    }


    public String getLanguage() {
        return getValueFor(LANGUAGE);
    }

    public String getTrackDownload() {
        return getValueFor(TRACKDOWNLOAD);
    }

    public Boolean isSecure() {
        String secureValue = getValueFor(SECURE);
        if (CommonConstants.YES.equals(secureValue)){
            return Boolean.TRUE;
        }
        return Boolean.parseBoolean(secureValue);
    }

    public String getTitle() {
        return getValueFor(TITLE);
    }

    public String getDcFormat() {
        return getValueFor(DC_FORMAT);
    }

    public String getFileSize() {
        return getValueFor(FILE_SIZE);
    }

    public String getContentType() {
        return getValueFor(CONTENT_TYPE);
    }

    public String getFileType() {
        return getValueFor(FILE_TYPE);
    }

    public String getStatus() {
        return getValueFor(STATUS);
    }

}
