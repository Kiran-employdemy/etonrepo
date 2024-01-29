package com.eaton.platform.core.servlets.assets;

import com.eaton.platform.core.constants.AssetDownloadConstants;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AssetDownloadParameters {
    private final String downloadFileName;
    private final List<String> downloadAssetPaths;
    private final List<String> emailToRecipients;
    private final String downloadOption;
    private final boolean mergeAssetsToSinglePDF;
    private final String mergeAssetsFileName;

    private static final Logger LOG = LoggerFactory.getLogger(AssetDownloadParameters.class);


    public AssetDownloadParameters(final String downloadFileName, final List<String> downloadAssetPaths, final List<String> emailToRecipients, final String downloadOption, final boolean mergeAssetsToSinglePDF, final String mergeAssetsFileName) {
        this.downloadFileName = downloadFileName;
        this.downloadAssetPaths = downloadAssetPaths;
        this.emailToRecipients = emailToRecipients;
        this.downloadOption = downloadOption;
        this.mergeAssetsToSinglePDF = mergeAssetsToSinglePDF;
        this.mergeAssetsFileName = mergeAssetsFileName;
    }

    public static AssetDownloadParameters fromRequest(final SlingHttpServletRequest request, final String option)
            throws IOException, Exception {
        String fileName = "";
        final List<String> assetPaths = new ArrayList<>();
        final List<String> emailToRecipients = new ArrayList<>();
        boolean mergeAssetsToSinglePDF = Boolean.TRUE;
        String mergeAssetsFileName = AssetDownloadConstants.MERGE_ASSETS_FILE_NAME_DEFAULT_VALUE;

        try {
            JsonArray assetPathsParam = new JsonArray();
            final JsonObject dataJson = CommonUtil.getJsonRequestFromPOST(request);
            if (dataJson.has(AssetDownloadConstants.FILE_NAME)) {
                fileName = dataJson.get(AssetDownloadConstants.FILE_NAME).getAsString();
            }
            if (dataJson.has(AssetDownloadConstants.ASSET_PATHS)) {
                assetPathsParam = dataJson.get(AssetDownloadConstants.ASSET_PATHS).getAsJsonArray();
            }
            for (int i = 0; i < assetPathsParam.size(); i++) {
                final String url = assetPathsParam.get(i).getAsString();
                if (StringUtils.isNotBlank(url)) {
                    assetPaths.add(url);
                } else {
                    LOG.error("Malformed Submittal Builder URL provided by Endeca. ");
                }
            }

            if (dataJson.has(AssetDownloadConstants.EMAIL_TO_RECIPIENTS)) {
                final JsonArray emailToRecipientsParam = dataJson.get(AssetDownloadConstants.EMAIL_TO_RECIPIENTS).getAsJsonArray();
                for (int i = 0; i < emailToRecipientsParam.size(); i++) {
                    emailToRecipients.add(emailToRecipientsParam.get(i).getAsString());
                }
            }
            if (dataJson.has(AssetDownloadConstants.PARAMETER_KEY_MERGE_ASSETS_TO_SINGLE_PDF)) {
                mergeAssetsToSinglePDF = dataJson.get(AssetDownloadConstants.PARAMETER_KEY_MERGE_ASSETS_TO_SINGLE_PDF).getAsBoolean();
            }
            if (dataJson.has(AssetDownloadConstants.PARAMETER_KEY_MERGE_ASSETS_FILE_NAME)) {
                mergeAssetsFileName = dataJson.get(AssetDownloadConstants.PARAMETER_KEY_MERGE_ASSETS_FILE_NAME).getAsString();
            }
            AssetDownloadValidator.validateParameters(fileName, assetPaths, emailToRecipients, option);
        } catch (Exception e) {
            LOG.error("Error while processing request parameters", e);
            throw new Exception("Error while processing request parameters");
        }

        return new AssetDownloadParameters(fileName, assetPaths, emailToRecipients, option, mergeAssetsToSinglePDF, mergeAssetsFileName);
    }

    // TODO: get the download file name default value from the component dialog or localize this value.
    public String getDownloadFileName() {
        return Optional.ofNullable(downloadFileName).orElse(AssetDownloadConstants.DOWNLOAD_FILE_NAME_DEFAULT_VALUE);
    }

    public List<String> getDownloadAssetPaths() { return downloadAssetPaths; }

    public List<String> getEmailToRecipients() {
        return emailToRecipients;
    }

    public String getDownloadOption() { return downloadOption; }

    public boolean isMergeAssetsToSinglePDF() {
        return mergeAssetsToSinglePDF;
    }

    public String getMergeAssetsFileName() {
        return mergeAssetsFileName.endsWith(AssetDownloadConstants.PDF_EXTN_WTIH_DOT) ? mergeAssetsFileName : mergeAssetsFileName.concat(AssetDownloadConstants.PDF_EXTN_WTIH_DOT);
    }
}
