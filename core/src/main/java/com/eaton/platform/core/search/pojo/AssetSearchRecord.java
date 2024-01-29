package com.eaton.platform.core.search.pojo;

import com.eaton.platform.core.search.api.DefaultImageUrlFactory;
import com.eaton.platform.core.search.api.AssetSearchResultMappingContext;
import com.eaton.platform.core.search.api.TranslationMappingContext;
import com.eaton.platform.core.search.api.VendorAssetSearchRecord;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import com.eaton.platform.core.constants.CommonConstants;
import java.util.Locale;
import java.util.Objects;

/**
 * POJO for displaying the fields for each search result document.
 */
public class AssetSearchRecord {
    private String image;
    private Boolean downloadEnabled;
    private String publishDate;
    private String description;
    private String language;
    private String trackDownload;
    private Boolean secure;
    private String title;
    private String url;
    private StatusBadge badge;
    private String dcFormat;
    private String fileSize;
    private String id;
    private String contentType;
    private String fileType;
    private String fileTypeAndSize;
    private String epochPublishDate;
    private String status;
    private String fileName;
    /**
     * Factory method taking as arguments:
     * @param vendorDocument the vendor implementation for a document
     * @param mappingContext to map the search result
     * @param <T> type of the vendor implementation
     * @param <C> type of the mapping context
     * @return the completely filled in SearchResult
     */
    public static <T extends VendorAssetSearchRecord, C extends AssetSearchResultMappingContext<? extends TranslationMappingContext>> AssetSearchRecord of(T vendorDocument, C mappingContext) {
        AssetSearchRecord searchResult = new AssetSearchRecord();
        searchResult.url = vendorDocument.getUrl();
        searchResult.id = CommonUtil.generateHashValue(searchResult.url);
        searchResult.fileType = mappingContext.determineFileType(vendorDocument.getDcFormat());
        DefaultImageUrlFactory defaultImageUrlFactory = mappingContext.getDefaultImageUrlCreation();
        String imageUrl = vendorDocument.getImage();
        searchResult.image = determineImageUrl(searchResult, defaultImageUrlFactory, imageUrl);
        searchResult.publishDate = mappingContext.getFormattedPublishDate(vendorDocument.getEpochDate());
        searchResult.description = vendorDocument.getDescription();
        searchResult.language = mappingContext.locateLanguageTagAndReturnLocalizedTitle(vendorDocument.getLanguage());
        searchResult.trackDownload = vendorDocument.getTrackDownload();
        searchResult.secure = vendorDocument.isSecure();
        searchResult.title = vendorDocument.getTitle();
        searchResult.dcFormat = vendorDocument.getDcFormat();
        searchResult.fileSize = vendorDocument.getFileSize();
        searchResult.contentType = vendorDocument.getContentType();
        searchResult.fileTypeAndSize = getFileTypeAndSize(vendorDocument, mappingContext);
        searchResult.status = vendorDocument.getStatus();
        searchResult.epochPublishDate = determineEpochPublishDate(vendorDocument);
        searchResult.badge = determineBadge(vendorDocument, mappingContext);
        searchResult.downloadEnabled = mappingContext.isBulkDownloadEnabled() && ! vendorDocument.getFileType().contains("html");
        searchResult.fileName = extractFileName(vendorDocument.getUrl());
        return searchResult;
    }

    private static <T extends VendorAssetSearchRecord> String determineEpochPublishDate(T vendorDocument) {
        Long epochDate = vendorDocument.getEpochDate();
        if (epochDate == null) {
            return StringUtils.EMPTY;
        }
        return epochDate.toString();
    }

    private static String determineImageUrl(AssetSearchRecord searchResult, DefaultImageUrlFactory defaultImageUrlFactory, String imageUrl) {
        boolean needsDefaultImageUrl = defaultImageUrlFactory.needsDefaultImageUrl(imageUrl);
        if (needsDefaultImageUrl){
            return defaultImageUrlFactory.createImageUrl(searchResult.fileType.substring(searchResult.fileType.indexOf('/') + 1));
        }
        return imageUrl;
    }

    private static <C extends AssetSearchResultMappingContext<? extends TranslationMappingContext>> StatusBadge determineBadge(VendorAssetSearchRecord vendorDocument, C mappingContext) {
        TranslationMappingContext translationContext = mappingContext.getTranslationContext();
        if (mappingContext.isNew(vendorDocument.getEpochDate())) {
            if (mappingContext.bornOnDayExists(vendorDocument.getUrl())) {
                return StatusBadge.statusUpdated(translationContext);
            }
            return StatusBadge.statusNew(translationContext);
        }
        return null;
    }

    private static <C extends AssetSearchResultMappingContext<? extends TranslationMappingContext>> String getFileTypeAndSize(VendorAssetSearchRecord vendorDocument, C mappingContext) {
        String fileType = mappingContext.determineFileType(vendorDocument.getDcFormat());
        String humanReadableFileSize = mappingContext.convertToHumanReadableFileSize(vendorDocument.getFileSize());
        return fileType.substring(fileType.indexOf('/') + 1).toUpperCase(Locale.getDefault()) + " " + humanReadableFileSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AssetSearchRecord that = (AssetSearchRecord) o;
        return Objects.equals(image, that.image) && Objects.equals(downloadEnabled, that.downloadEnabled)
                && Objects.equals(publishDate, that.publishDate) && Objects.equals(description, that.description)
                && Objects.equals(language, that.language)
                && Objects.equals(trackDownload, that.trackDownload) && Objects.equals(secure, that.secure)
                && Objects.equals(title, that.title) && Objects.equals(url, that.url) && Objects.equals(badge, that.badge)
                && Objects.equals(dcFormat, that.dcFormat) && Objects.equals(fileSize, that.fileSize) && Objects.equals(id, that.id)
                && Objects.equals(contentType, that.contentType) && Objects.equals(fileType, that.fileType)
                && Objects.equals(fileTypeAndSize, that.fileTypeAndSize) && Objects.equals(epochPublishDate, that.epochPublishDate)
                && Objects.equals(status, that.status) && Objects.equals(fileName, that.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(image, downloadEnabled, publishDate, description, language, trackDownload, secure
                , title, url, badge, dcFormat, fileSize, id, contentType, fileType, fileTypeAndSize, epochPublishDate, status, fileName);
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "image='" + image + '\'' +
                ", downloadEnabled=" + downloadEnabled +
                ", publishDate='" + publishDate + '\'' +
                ", description='" + description + '\'' +
                ", language='" + language + '\'' +
                ", trackDownload='" + trackDownload + '\'' +
                ", secure=" + secure +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", badges=" + badge +
                ", dcFormat='" + dcFormat + '\'' +
                ", fileSize='" + fileSize + '\'' +
                ", id='" + id + '\'' +
                ", contentType='" + contentType + '\'' +
                ", fileType='" + fileType + '\'' +
                ", fileTypeAndSize='" + fileTypeAndSize + '\'' +
                ", epochPublishDate='" + epochPublishDate + '\'' +
                ", status='" + status + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }
    private static String extractFileName(String fileNameStr) {
        return fileNameStr.substring(fileNameStr.lastIndexOf(CommonConstants.SLASH_STRING) + 1);
    }
}
