package com.eaton.platform.core.search.impl;

import com.day.cq.dam.api.Asset;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.SiteResourceSlingModel;
import com.eaton.platform.core.models.secure.AdvancedSearchModel;
import com.eaton.platform.core.search.api.DefaultImageUrlFactory;
import com.eaton.platform.core.search.api.AssetSearchResultMappingContext;
import com.eaton.platform.integration.endeca.util.FileSizeToHumanReadableStringConverter;
import com.eaton.platform.integration.endeca.util.FileTypesUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

/**
 * The implementation of Search result mapping context for Advanced Search
 */
public class AdvancedSearchResultMappingContextImpl implements AssetSearchResultMappingContext<TranslationMappingContextImpl> {
    private static final String US_DATE_PATTERN = "MM/dd/yyyy";
    private static final String OTHER_DATE_PATTERN = "dd/MM/yyyy";
    private DefaultImageUrlFactory defaultImageUrlFactory;
    private SiteResourceSlingModel siteConfig;
    private FileSizeToHumanReadableStringConverter fileSizeToHumanReadableStringConverter;
    private SlingHttpServletRequest request;
    private LocalDate now;
    private ZoneId zoneId;
    private TranslationMappingContextImpl translationMappingContext;

    /**
     * factory method for setting
     * @param defaultImageUrlFactory to set
     * @return this
     */
    public AdvancedSearchResultMappingContextImpl defaultImageUrlFactory(DefaultImageUrlFactory defaultImageUrlFactory) {
        this.defaultImageUrlFactory = defaultImageUrlFactory;
        return this;
    }
    /**
     * factory method for setting
     * @param siteConfig to set
     * @return this
     */
    public AdvancedSearchResultMappingContextImpl siteConfig(SiteResourceSlingModel siteConfig) {
        this.siteConfig = siteConfig;
        return this;
    }
    /**
     * factory method for setting
     * @param fileSizeToHumanReadableStringConverter to set
     * @return this
     */
    public AdvancedSearchResultMappingContextImpl fileSizeToHumanReadableStringConverter(FileSizeToHumanReadableStringConverter fileSizeToHumanReadableStringConverter) {
        this.fileSizeToHumanReadableStringConverter = fileSizeToHumanReadableStringConverter;
        return this;
    }
    /**
     * factory method for setting
     * @param request to set
     * @return this
     */
    public AdvancedSearchResultMappingContextImpl request(SlingHttpServletRequest request) {
        this.request = request;
        return this;
    }
    /**
     * factory method for setting
     * @param now to set
     * @return this
     */
    public AdvancedSearchResultMappingContextImpl now(LocalDate now) {
        this.now = now;
        return this;
    }
    /**
     * factory method for setting
     * @param translationHelper to set
     * @return this
     */
    public AdvancedSearchResultMappingContextImpl translationHelper(TranslationMappingContextImpl translationHelper) {
        this.translationMappingContext = translationHelper;
        return this;
    }

    public AdvancedSearchResultMappingContextImpl zoneId(ZoneId zoneId) {
        this.zoneId = zoneId;
        return this;
    }

    @Override
    public String determineFileType(String type) {
        if (FileTypesUtil.fileTypeMap().containsKey(type)) {
            return FileTypesUtil.fileTypeMap().get(type);
        }
        return type;
    }

    @Override
    public DefaultImageUrlFactory getDefaultImageUrlCreation() {
        return defaultImageUrlFactory;
    }

    private String getDatePattern() {
        if (Boolean.parseBoolean(siteConfig.getUnitedStatesDateFormat())) {
            return US_DATE_PATTERN;
        }
        return OTHER_DATE_PATTERN;
    }

    @Override
    public String locateLanguageTagAndReturnLocalizedTitle(String language) {
        if (language.isEmpty()) {
            return "";
        }
        String hyphenedLanguage = language.replace(CommonConstants.UNDERSCORE, CommonConstants.HYPHEN);
        String tagId = "eaton:language/" + hyphenedLanguage;
        String languageTitle = translationMappingContext.locateTagAndRetrieveTitle(tagId.toLowerCase(Locale.ENGLISH)
                , determineDefaultTitle(hyphenedLanguage));
        if (languageTitle.contains("(")) {
            return languageTitle.substring(0, languageTitle.indexOf('(')-1);
        }
        return languageTitle;
    }

    private String determineDefaultTitle(String hyphenedLanguage) {
        if (Arrays.asList("global", "multilingual").contains(hyphenedLanguage)) {
            return translationMappingContext.retrieveFromI18n(hyphenedLanguage);
        }
        return hyphenedLanguage;
    }

    @Override
    public String convertToHumanReadableFileSize(String fileSize) {
        return fileSizeToHumanReadableStringConverter.convert(fileSize);
    }

    @Override
    public Boolean isBulkDownloadEnabled() {
        AdvancedSearchModel advancedSearchModel = request.getResource().adaptTo(AdvancedSearchModel.class);
        if (advancedSearchModel == null) {
            return false;
        }
        return advancedSearchModel.isBulkDownloadEnabled();
    }

    @Override
    public Integer getNumberOfDays() {
        return siteConfig.getNoOfDays();
    }

    @Override
    public LocalDate getNow() {
        return now;
    }

    @Override
    public boolean bornOnDayExists(String url) {
        ResourceResolver resourceResolver = request.getResourceResolver();
        Resource resource = resourceResolver.getResource(url.substring(url.indexOf("/content")));
        if (resource == null) {
            return false;
        }
        Asset asset = resource.adaptTo(Asset.class);
        String bornOnDateString = Objects.requireNonNull(asset).getMetadataValue("bornOnDate");
        return bornOnDateString != null;
    }

    @Override
    public TranslationMappingContextImpl getTranslationContext() {
        return translationMappingContext;
    }

    @Override
    public String getFormattedPublishDate(Long epochDate) {
        if (epochDate == null) {
            return StringUtils.EMPTY;
        }
        return convertEpochPublishDateToLocalDate(epochDate).format(DateTimeFormatter.ofPattern(getDatePattern()));
    }

    @Override
    public boolean isNew(Long epochDate) {
        LocalDate localDate = convertEpochPublishDateToLocalDate(epochDate);
        if (localDate == null) {
            return false;
        }
        return now.minusDays(getNumberOfDays()).isBefore(localDate);
    }

    private LocalDate convertEpochPublishDateToLocalDate(Long epochDate) {
        if (epochDate == null) {
            return null;
        }
        return LocalDate.ofInstant(Instant.ofEpochMilli(epochDate), zoneId);
    }
}
