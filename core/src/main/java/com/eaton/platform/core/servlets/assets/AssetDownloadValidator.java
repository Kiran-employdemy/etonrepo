package com.eaton.platform.core.servlets.assets;

import com.day.cq.dam.api.Asset;
import com.eaton.platform.core.constants.AssetDownloadConstants;
import com.eaton.platform.core.constants.DownloadOption;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.List;

public final class AssetDownloadValidator {
    //TODO: get all the validation error messages from component dialog or localize them.
    private AssetDownloadValidator() {
        super();
    }

    public static void validateParameters(final String fileName, final List<String> assetPaths, final List<String> emailToRecipients, final String option) {
        if (StringUtils.isEmpty(option)) {
            throw new IllegalArgumentException("Invalid request. Option is required.");
        }

        if (option.equals(DownloadOption.DOWNLOAD.getDownloadOption())) {
            validateDirectDownloadRequest(assetPaths);
        } else if(option.equals(DownloadOption.EMAIL.getDownloadOption())) {
            validateEmailDownloadLinkRequest(assetPaths, emailToRecipients);
        }
    }

    public static void validateDirectDownloadRequest(final List<String> assetPaths) {
        if(assetPaths == null || assetPaths.size() < 1) {
            throw new IllegalArgumentException("Invalid request. Minimum one valid asset path is required.");
        }
    }

    public static void validateEmailDownloadLinkRequest(final List<String> assetPaths, final List<String> emailToRecipients) {
        validateDirectDownloadRequest(assetPaths);

        if(emailToRecipients == null || emailToRecipients.size() < 1 ) {
            throw new IllegalArgumentException("Invalid request. Minimum one EmailTo address is required.");
        }
    }

    public static void validateMinimum(final List<Asset> assets) {
        if(null == assets || assets.isEmpty() || assets.size() < 1) {
            throw new IllegalArgumentException("Invalid number of assets. Minimum one valid asset is required.");
        }
    }

    public static void validateMaxDownloadPackageSizeLimit(final List<Asset> assets, final long maxPackageSizeLimit ) {
        long totalSize = 0L;
        for (final Asset asset : assets) {
            if (null != asset) {
                totalSize += asset.getOriginal().getSize();
                if(totalSize > maxPackageSizeLimit) {
                    throw new IllegalArgumentException("Max size exceeded.");
                }
            }
        }
    }
}
