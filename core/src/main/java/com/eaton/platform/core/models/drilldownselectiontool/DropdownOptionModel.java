package com.eaton.platform.core.models.drilldownselectiontool;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.constants.CommonConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class DropdownOptionModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(DropdownOptionModel.class);

    private String tagPath;

    private String title;

    public static DropdownOptionModel of(Resource resource, Page currentPage) {
        LOGGER.debug("DropdownOptionModel: START");
        DropdownOptionModel dropdownOptionModel = new DropdownOptionModel();
        dropdownOptionModel.tagPath = resource.getPath();
        LOGGER.debug("dropdownOptionModel.tagPath: {}", dropdownOptionModel.tagPath);
        dropdownOptionModel.title = dropdownOptionModel.getTranslatedTitle(resource, currentPage);
        LOGGER.debug("dropdownOptionModel.title: {}", dropdownOptionModel.title);
        LOGGER.debug("DropdownOptionModel: END");
        return dropdownOptionModel;
    }

    /**
     * Get the translated title of a dropdown option
     * @param resource resource to get the translated title from
     * @return translated title of the resource
     */
    private String getTranslatedTitle(Resource resource, Page currentPage) {

        LOGGER.debug("getTranslatedTitle: START");
        ValueMap tagResourceValueMap = resource.getValueMap();
        String currentPageLanguage = Objects.requireNonNull(currentPage).getLanguage().toString();
        String currentPageLanguageLowercase = StringUtils.lowerCase(Objects.requireNonNull(currentPageLanguage));
        String translatedTitlePropertyName = StringUtils.join(JcrConstants.JCR_TITLE, CommonConstants.PERIOD, currentPageLanguageLowercase);
        String translatedTitleValue = (String) Objects.requireNonNull(tagResourceValueMap).get(translatedTitlePropertyName);
        if (StringUtils.isBlank(translatedTitleValue)) {
            translatedTitleValue = (String) Objects.requireNonNull(tagResourceValueMap).get(JcrConstants.JCR_TITLE);
        }
        LOGGER.debug("translatedTitleValue: {}", translatedTitleValue);
        LOGGER.debug("getTranslatedTitle: END");
        return translatedTitleValue;

    }

    public String getTagPath() {
        return tagPath;
    }

    public void setTagPath(String tagPath) {
        this.tagPath = tagPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
