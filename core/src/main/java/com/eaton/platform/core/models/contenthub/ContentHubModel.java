package com.eaton.platform.core.models.contenthub;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.injectors.annotations.EatonSiteConfigInjector;
import com.eaton.platform.core.models.SiteConfigModel;
import com.eaton.platform.core.models.SiteResourceSlingModel;
import com.eaton.platform.core.models.eatonsiteconfig.EatonSiteConfigModel;
import com.eaton.platform.core.services.EndecaRequestService;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.contenthub.ContentHubListResponseBean;
import com.eaton.platform.integration.endeca.services.EndecaService;
import com.google.gson.Gson;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.ContentHubBean;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.CloudConfigService;
import com.eaton.platform.core.services.ContentHubService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.endeca.bean.FacetGroupBean;
import com.eaton.platform.integration.endeca.bean.FacetsBean;
/**
 * <html> Description: This Sling Model used in Content Hub Helper class. </html>
 *
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ContentHubModel {

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(ContentHubModel.class);

	/** The tags. */
	@Inject
	@Via("resource")
	private String tags[];

	/** Default Image*/
	private String defaultImagePath = "";


	/** The maxLimitToDisplay. */
	@Inject
	@Via("resource")
	@Default (intValues=6)
	private int initialNumberOfResults;

	/** Show filters flag */
	@Inject
	@Via("resource")
	private  String showFilters;
	
	/** Show publication date */
	@Inject
	@Via("resource")
	private  String enablePublicationDate;
	
	/**
     * The sort.
     */
    @Inject
    @Via("resource")
    private String sortBy;

	/** The desktop trans. */
	@Inject
	@Via("resource")
	private String desktopTrans;

	/** The mobile trans. */
	@Inject
	@Via("resource")
	private String mobileTrans;

	/** The resource page. */
	@Inject
	@Via("resource")
	private Page resourcePage;

	@Self
	@Via("resource")
	private Resource resource;

	@OSGiService
	private ContentHubService contentHubService;

	@OSGiService
	private EndecaRequestService endecaRequestService;

	@OSGiService
	private EndecaService endecaService;

	/** The Content Hub List response bean. */
	ContentHubListResponseBean contentHubListResponseBean;

	EndecaServiceRequestBean endecaServiceRequestBean;

	FacetsBean allReturnedFacetsBean;

	/** The child page links list. */
	private List<ContentHubBean> childPageAndTagsLinksList;

	@OSGiService
	private AdminService adminService;

	@OSGiService
	private CloudConfigService cloudConfigService;

	private List<FacetGroupBean> filters;

	@Inject
	private SlingHttpServletRequest slingRequest;
	
	@EatonSiteConfigInjector
	private Optional<SiteResourceSlingModel> siteResourceSlingModel;
    
    private boolean isUnitedStatesDateFormat = false;

	@PostConstruct
	protected void init() {
		LOG.debug("Content Hub Model Init Started");
		if (siteResourceSlingModel.isPresent()) {
			isUnitedStatesDateFormat = Boolean.parseBoolean(siteResourceSlingModel.get().getUnitedStatesDateFormat());
		}
		if (adminService != null) {
			try (ResourceResolver adminReadResourceResolver = adminService.getReadService()) {
				if (null != cloudConfigService) {
					cloudConfigService.getEndecaCloudConfig(resource);
				}
				final Locale locale = CommonUtil.getLocaleFromPagePath(resourcePage);
				if( tags.length!=0){
					if(null != slingRequest) {
						final EatonSiteConfigModel eatonSiteConfigModel = slingRequest.adaptTo(EatonSiteConfigModel.class);
						if (null != eatonSiteConfigModel) {
							final SiteConfigModel siteConfiguration = eatonSiteConfigModel.getSiteConfig();
							defaultImagePath = siteConfiguration.getContenthubDefaultIcon();
						}
					}
					childPageAndTagsLinksList = new ArrayList<>();
					if (tags.length!=0) {
						contentHubService.populateTagsModel(childPageAndTagsLinksList, tags, resourcePage,resourcePage.getContentResource().getResourceResolver(),locale,showFilters,defaultImagePath,enablePublicationDate,sortBy,isUnitedStatesDateFormat);
					}
					if(null != showFilters && CommonConstants.TRUE.equalsIgnoreCase(showFilters)){
						List<FacetGroupBean> cleanAEMFilters = contentHubService.getCleanAEMFilters(childPageAndTagsLinksList,tags,locale,resourcePage.getContentResource().getResourceResolver());
						setFilters(cleanAEMFilters);
					}
				}
			}catch (Exception e)
			{
				LOG.error("Error in ContentHubModel class ");
			}

		} else {
			LOG.error("Could not retrieve an admin service in ContentHubModel.init");
		}
		LOG.debug("Content Hub Model Init Ended");
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
		return new Gson().toJson(childPageAndTagsLinksList);
	}

	public int getTotalCount() {
		return childPageAndTagsLinksList.size();
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
