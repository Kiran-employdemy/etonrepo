package com.eaton.platform.core.search.api;

/**
 * To be implemented for Asset Search Vendor specific mapping, these are the fields needed by our system to be able to display the asset search results
 * we have now in the website.
 */
public interface VendorAssetSearchRecord extends VendorSearchRecord {

    String getUrl();

    String getImage();

    String getDescription();

    String getLanguage();

    String getTrackDownload();

    Boolean isSecure();

    String getTitle();

    String getDcFormat();

    String getFileSize();

    String getContentType();

    String getStatus();

    Long getEpochDate();

    String getFileType();
}
