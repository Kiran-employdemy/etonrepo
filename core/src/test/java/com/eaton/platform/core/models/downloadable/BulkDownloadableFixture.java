package com.eaton.platform.core.models.downloadable;

import com.day.cq.wcm.api.Page;
import org.apache.sling.api.SlingHttpServletRequest;

import static com.eaton.platform.core.util.ResourceBundleFixtures.*;

public class BulkDownloadableFixture extends AbstractBulkDownloadableImpl {

    static final String FILE_NAME_PREFIX = "prefix";
    private SlingHttpServletRequest slingHttpServletRequest;

    private Page currentPage;

    public BulkDownloadableFixture() {
    }

    public BulkDownloadableFixture(SlingHttpServletRequest slingHttpServletRequest, Page currentPage) {
        this.slingHttpServletRequest = slingHttpServletRequest;
        this.currentPage = currentPage;
        this.bulkDownloadToolTipText = BULK_DOWNLOAD_TOOL_TIP;
        this.fileNamePrefix = FILE_NAME_PREFIX;
        this.bulkDownloadText = BULK_DOWNLOAD_VALUE;
        this.downloadLimitExceededText = DOWNLOAD_EXCEEDED_TEXT;
    }


    @Override
    protected SlingHttpServletRequest getSlingHttpServletRequest() {
        return slingHttpServletRequest;
    }

    @Override
    protected Page getCurrentPage() {
        return currentPage;
    }
}
