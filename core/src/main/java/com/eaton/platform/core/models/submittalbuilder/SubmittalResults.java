package com.eaton.platform.core.models.submittalbuilder;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.eaton.platform.core.models.SiteConfigModel;
import com.eaton.platform.core.models.eatonsiteconfig.EatonSiteConfigModel;
import com.eaton.platform.core.services.AssetDownloadService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SubmittalResults {
    @Inject
    @Via("resource")
    private String searchResultsTitle;

    @Inject
    @Via("resource")
    private String packageViewTitle;

    @Inject
    @Via("resource")
    private String description;

    @Inject
    @Via("resource")
    private String addAllText;

    @Inject
    @Via("resource")
    private String removeAllText;

    @Inject
    @Via("resource")
    private String cannotAddMoreFilesMessage;

    @Inject
    @Via("resource")
    @Default(values="Load More")
    private String loadMoreLabel;

    @Inject
    @Via("resource")
    private String allItemsRemovedText;

    @Inject
    @Via("resource")
    private String itemHasBeenRemovedText;

    @Inject
    @Via("resource")
    private String fileDeletionConfirmation;

    @Inject
    @Via("resource")
    private String fileDeletionConfirmationText;

    @Inject
    private SlingHttpServletRequest slingRequest;

    @OSGiService
    AssetDownloadService assetDownloadService;

    private long sizelimit;
    private int pageSize;

    @PostConstruct
    protected void init() {
        if (null != slingRequest) {
            final EatonSiteConfigModel eatonSiteConfigModel = slingRequest.adaptTo(EatonSiteConfigModel.class);
            if (null != eatonSiteConfigModel) {
                final SiteConfigModel siteConfiguration = eatonSiteConfigModel.getSiteConfig();
                pageSize = siteConfiguration.getPageSize();
                if (assetDownloadService != null){
                    sizelimit =  assetDownloadService.getMaxAllowedDownloadPackageSize();
                }
            }
        }
    }

    public String getSearchResultsTitle() {
        return searchResultsTitle;
    }

    public String getPackageViewTitle() {
        return packageViewTitle;
    }

    public String getDescription() {
        return description;
    }

    public String getAddAllText() {
        return addAllText;
    }

    public String getRemoveAllText() {
        return removeAllText;
    }

    public String getCannotAddMoreFilesMessage() {
        return cannotAddMoreFilesMessage;
    }

    public long getSizelimit() {
        return this.sizelimit;
    }

    public String getLoadMoreLabel() { return loadMoreLabel; }

    public String getAllItemsRemovedText() { return allItemsRemovedText; }

    public String getItemHasBeenRemovedText() { return itemHasBeenRemovedText; }

    public String getFileDeletionConfirmation() { return fileDeletionConfirmation; }

    public String getFileDeletionConfirmationText() { return fileDeletionConfirmationText; }

    public int getPageSize(){
        return pageSize;
    }
}