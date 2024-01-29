package com.eaton.platform.core.models.downloadable;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.SiteConfigModel;
import com.eaton.platform.core.models.eatonsiteconfig.EatonSiteConfigModel;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.sling.api.SlingHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.eaton.platform.core.constants.CommonConstants.*;

/**
 * Abstraction to indicate that a model can be used in a BulkDownloadable context
 * Default implementation of {@link BulkDownloadable}
 */
public abstract class AbstractBulkDownloadableImpl implements BulkDownloadable {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractBulkDownloadableImpl.class);

    private SiteConfigModel siteConfigModel;
    protected String bulkDownloadToolTipText;

    protected String fileNamePrefix;
    protected String bulkDownloadText;
    protected String downloadLimitExceededText;

    protected SiteConfigModel getSiteConfigModel() {
        if (siteConfigModel == null) {
            SlingHttpServletRequest slingHttpServletRequest = getSlingHttpServletRequest();
            if (slingHttpServletRequest == null) {
                LOGGER.error("SetSiteConfigModel - SlingHttpServletRequest received, not allowed here, throwing an IllegalStateException");
                throw new IllegalStateException("SlingHttpServletRequest is null, not allowed, check your code.");
            }
            EatonSiteConfigModel eatonSiteConfigModel = slingHttpServletRequest.adaptTo(EatonSiteConfigModel.class);
            if (eatonSiteConfigModel == null) {
                LOGGER.error("SetSiteConfigModel - SlingHttpServletRequest not adaptable to EatonSiteConfigModel, throwing an IllegalStateException");
                throw new IllegalStateException("SlingHttpServletRequest is not adaptable to EatonSiteConfigModel, should not happen, please investigate.");
            }
            siteConfigModel = eatonSiteConfigModel.getSiteConfig();
        }
        return this.siteConfigModel;
    }

    protected abstract SlingHttpServletRequest getSlingHttpServletRequest();

    protected abstract Page getCurrentPage();

    @Override
    public long getMaxAllowedDownloadSize() {
        return getSiteConfigModel().getBulkDownloadPackageSize();
    }

    @Override
    public long getDownloadCacheDuration() {
        return getSiteConfigModel().getBulkDownloadCacheDuration();
    }

    @Override
    public String getFileNamePrefix() {
        if (fileNamePrefix == null) {
            String company = getSiteConfigModel().getCompanyName();
            String downloadName = CommonUtil.getI18NFromResourceBundle(getSlingHttpServletRequest(), getCurrentPage()
                    , DOWNLOAD_KEY, DOWNLOAD_DEFAULT_VALUE);
            fileNamePrefix = String.format("%s_%s_", company, downloadName);
        }
        return fileNamePrefix;
    }

    @Override
    public String getBulkDownloadToolTipText() {
        if (bulkDownloadToolTipText == null) {
            bulkDownloadToolTipText = CommonUtil.getI18NFromResourceBundle(getSlingHttpServletRequest(),getCurrentPage()
                    , CommonConstants.BULK_DOWNLOAD_TOOL_TIP_KEY, CommonConstants.BULK_DOWNLOAD_TOOL_TIP_DEFAULT_VALUE);
        }
        return bulkDownloadToolTipText;
    }

    @Override
    public String getBulkDownloadText() {
        if (bulkDownloadText == null) {
            bulkDownloadText = CommonUtil.getI18NFromResourceBundle(getSlingHttpServletRequest(),getCurrentPage()
                    , CommonConstants.BULK_DOWNLOAD_KEY, CommonConstants.BULK_DOWNLOAD_DEFAULT_VALUE);
        }
        return bulkDownloadText;
    }

    @Override
    public String getDownloadLimitExceededText() {
        if (downloadLimitExceededText == null) {
            downloadLimitExceededText = CommonUtil.getI18NFromResourceBundle(getSlingHttpServletRequest(), getCurrentPage()
                    , DOWNLOAD_LIMIT_EXCEEDED_KEY, DOWNLOAD_LIMIT_EXCEEDED_DEFAULT_VALUE);
        }
        return downloadLimitExceededText;
    }
}
