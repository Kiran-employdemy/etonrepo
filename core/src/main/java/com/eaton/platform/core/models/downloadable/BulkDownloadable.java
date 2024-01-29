package com.eaton.platform.core.models.downloadable;

/**
 * Implement this interface to indicate this model is used in a bulk downloadable context
 */
public interface BulkDownloadable {
    /**
     * @return the maximum downloadable size
     */
    long getMaxAllowedDownloadSize();

    /**
     * @return the cache duration of download
     */
    long getDownloadCacheDuration();

    /**
     * @return the prefix used in the name of the downloaded file
     */
    String getFileNamePrefix();

    /**
     * @return the toolTip for the bulkDownload check box
     */
    String getBulkDownloadToolTipText();

    /**
     * @return the text for bulkDownload
     */
    String getBulkDownloadText();

    /**
     * @return the text displayed when the download limit is exceeded
     */
    String getDownloadLimitExceededText();
}
