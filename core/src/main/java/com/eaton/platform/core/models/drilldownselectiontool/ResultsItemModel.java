package com.eaton.platform.core.models.drilldownselectiontool;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.SiteResourceSlingModel;
import com.eaton.platform.core.services.EatonSiteConfigService;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import java.util.Objects;
import java.util.Optional;

public class ResultsItemModel {

    private String path;

    private String title;

    private String description;

    private String imagePath;


    public static ResultsItemModel of(ResourceResolver resourceResolver, Resource resource, EatonSiteConfigService eatonSiteConfigService, Page currentPage) {

        String pagePath = StringUtils.removeEnd(resource.getPath(), StringUtils.join(CommonConstants.SLASH_STRING, CommonConstants.JCR_CONTENT_STR));

        ResultsItemModel resultsItem = new ResultsItemModel();
        resultsItem.path = CommonUtil.dotHtmlLink(pagePath);

        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        Page page = Objects.requireNonNull(pageManager).getPage(pagePath);
        ValueMap properties = page.getProperties();
        resultsItem.title = (String) properties.get(JcrConstants.JCR_TITLE);
        String teaserImagePathFromProperties = (String) properties.get(CommonConstants.TEASER_IMAGE_PATH);
        resultsItem.imagePath = StringUtils.isNotBlank(teaserImagePathFromProperties) ? teaserImagePathFromProperties : resultsItem.getFallbackImagePath(eatonSiteConfigService, currentPage);
        String productGridDescription = (String) properties.get(CommonConstants.PRODUCT_GRID_DESCRIPTION);
        resultsItem.description = StringUtils.isNotBlank(productGridDescription) ? productGridDescription : StringUtils.EMPTY;

        return resultsItem;
    }
    
    private String getFallbackImagePath(EatonSiteConfigService eatonSiteConfigService, Page currentPage) {
        Optional<SiteResourceSlingModel> siteConfig = Objects.requireNonNull(eatonSiteConfigService).getSiteConfig(Objects.requireNonNull(currentPage));
        return siteConfig.isPresent() ? siteConfig.get().getSkuFallBackImage() : StringUtils.EMPTY;
    }

    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getDescription() {
        return description;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
