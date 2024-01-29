package com.eaton.platform.core.models.contenthubassets;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.ContentHubAssetsBean;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.SiteConfigModel;
import com.eaton.platform.core.models.eatonsiteconfig.EatonSiteConfigModel;
import com.eaton.platform.core.services.*;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.endeca.bean.FacetGroupBean;
import com.google.gson.Gson;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * <html> Description: This Sling Model used in Content Hub Assets View. </html>
 *
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ContentHubAssetsModel {

	private static final Logger LOG = LoggerFactory.getLogger(ContentHubAssetsModel.class);

	@Inject
	@Via("resource")
	private String[] eyebrowTag;

	@Inject
	@Via("resource")
	private String[] tags;
	
	@Inject
	@Via("resource")
	private String[] taxonomy;

	private String defaultImagePath = "";

	/** The maxLimitToDisplay. */
	@Inject
	@Via("resource")
	@Default (intValues=6)
	private int initialNumberOfResults;

	@Inject
	@Via("resource")
	private  String showFilters;
	
	@Inject
	@Via("resource")
	private  String showFiltersTaxonomy;

	@Inject
	@Via("resource")
	private  String enablePublicationDate;

    @Inject
    @Via("resource")
    private String sortBy;
    
    @Inject
    @Via("resource")
    private String[] countries;
    
    @Inject
    @Via("resource")
    private String[] languages;

	@Inject
	@Via("resource")
	private String desktopTrans;

	@Inject
	@Via("resource")
	private String mobileTrans;

	@Inject
	@Via("resource")
	private Page resourcePage;

	@Self
	@Via("resource")
	private Resource resource;

	@Inject
	private ContentHubAssetsService contentHubAssetsService;

	/** The child page links list. */
	private List<ContentHubAssetsBean> childAssetAndTagsLinksList;

	@Inject
	private AdminService adminService;

	@Inject
	private CloudConfigService cloudConfigService;

	private List<FacetGroupBean> filters;

	@Inject
	private SlingHttpServletRequest slingRequest;
	
	int totalCount;

	@PostConstruct
	protected void init() {
		LOG.debug("Content Hub Model Init Started");
		if (adminService != null) {
			try (final ResourceResolver readServiceResourceResolver = adminService.getReadService()) {
				final Locale locale = CommonUtil.getLocaleFromPagePath(resourcePage);
				if(null !=  tags){
					if(null != slingRequest) {
						final EatonSiteConfigModel eatonSiteConfigModel = slingRequest.adaptTo(EatonSiteConfigModel.class);
						if (null != eatonSiteConfigModel) {
							final SiteConfigModel siteConfiguration = eatonSiteConfigModel.getSiteConfig();
							if(siteConfiguration!=null && siteConfiguration.getContenthubDefaultIcon()!=null) {
							defaultImagePath = siteConfiguration.getContenthubDefaultIcon();
							}
						}
					}
					childAssetAndTagsLinksList = new ArrayList<>();
					Map<String, String> params = new HashMap<>();
					params.put(CommonConstants.CONTENT_HUB_SHOW_FILTERS, showFilters);
					params.put(CommonConstants.CONTENT_HUB_SHOW_FILTERS_TAXONOMY, showFiltersTaxonomy);
					params.put(CommonConstants.CONTENT_HUB_DEFAULT_IMAGE_PATH,defaultImagePath);
					params.put(CommonConstants.CONTENT_HUB_ENABLE_PUBLICATION_DATE,enablePublicationDate);
					params.put(CommonConstants.CONTENT_HUB_SORT_BY,sortBy);
					if(null != contentHubAssetsService) {
						contentHubAssetsService.populateTagsModel(childAssetAndTagsLinksList, tags, resourcePage.getContentResource().getResourceResolver(), locale, eyebrowTag, params,countries,languages,taxonomy);
						if(CommonConstants.TRUE.equalsIgnoreCase(showFiltersTaxonomy) || CommonConstants.TRUE.equalsIgnoreCase(showFilters)) {
							List<FacetGroupBean> cleanAEMFilters = contentHubAssetsService.getCleanAEMFilters(childAssetAndTagsLinksList,tags, locale, resourcePage.getContentResource().getResourceResolver(),taxonomy,params);
							setFilters(cleanAEMFilters);
						}
					}
					totalCount=0;
					if(childAssetAndTagsLinksList!=null && !childAssetAndTagsLinksList.isEmpty())
						totalCount=childAssetAndTagsLinksList.size();
				}
			}catch (Exception e) {
				LOG.error("Error in ContentHubAssetsModel class ");
			}

		} else {
			LOG.error("Could not retrieve an admin service in ContentHubAssetsModel.init");
		}
		LOG.debug("Content Hub Assets Model Init Ended");
	}

	/**
	 * Gets the taxonomy.
	 *
	 * @return the taxonomy
	 */
	public String[] getTaxonomy() {
		return taxonomy;
	}
	
	/**
	 * Gets the tags.
	 *
	 * @return the tags
	 */
	public String[] getTags() {
		return tags;
	}

	/**
	 * Gets the max limit to display.
	 *
	 * @return the max limit to display
	 */
	public int getInitialNumberOfResults() {
		return initialNumberOfResults;
	}
	/**
	 * Gets the desktop trans.
	 *
	 * @return the desktop trans
	 */
	public String getDesktopTrans() {
		return desktopTrans;
	}

	/**
	 * Gets the mobile trans.
	 *
	 * @return the mobile trans
	 */
	public String getMobileTrans() {
		return mobileTrans;
	}


	public String getResultJson() {
		return new Gson().toJson(childAssetAndTagsLinksList);
	}

	public int getTotalCount() {
		return totalCount;
	}

	public String getDefaultImagePath() {
		return defaultImagePath;
	}

	public List<FacetGroupBean> getFilters() {
		return filters;
	}

	public void setFilters(List<FacetGroupBean> filters) {
		this.filters = filters;
	}

	public String getShowFilters() {

		return (CommonConstants.TRUE.equalsIgnoreCase(showFilters)) ? showFilters : CommonConstants.FALSE;
	}

	public String getShowFiltersTaxonomy() {
		return (CommonConstants.TRUE.equalsIgnoreCase(showFiltersTaxonomy)) ? showFiltersTaxonomy : CommonConstants.FALSE;
	}

	public void setShowFiltersTaxonomy(String showFiltersTaxonomy) {
		this.showFiltersTaxonomy = showFiltersTaxonomy;
	}

	public void setShowFilters(String showFilters) {
		this.showFilters = showFilters;
	}
	
	public String getEnablePublicationDate() {
		return enablePublicationDate;
	}

	public void setEnablePublicationDate(String enablePublicationDate) {
		this.enablePublicationDate = enablePublicationDate;
	}
	
	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}
}
