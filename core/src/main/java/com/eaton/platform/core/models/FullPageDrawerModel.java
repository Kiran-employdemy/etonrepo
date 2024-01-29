package com.eaton.platform.core.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.day.cq.commons.Externalizer;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.integration.auth.services.AuthenticationServiceConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.*;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import com.eaton.platform.integration.auth.models.AuthenticationToken;
import com.eaton.platform.integration.auth.services.AuthorizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.util.CommonUtil;

/**
 * This class is used to inject the dialog properties.
 * @author TCS
 *
 */
@Model(adaptables = { Resource.class,
		SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FullPageDrawerModel {

	private static final Logger LOG = LoggerFactory.getLogger(FullPageDrawerModel.class);

	/** The links. */
	private List<FullPageDrawerLinkListModel> links;

	/** The link list. */
	@Inject
	@ChildResource
	private Resource linkList;

	@Inject
	@ScriptVariable
	private PageManager pageManager;

	/** The Externalizer */
	@Inject
	private Externalizer externalizer;

	@Inject
	private AuthorizationService authorizationService;

	/** The admin service. */
	@Inject
	protected AdminService adminService;

	@Inject
	private Page currentPage;

	@Inject
	@Source("sling-object")
	private SlingHttpServletRequest slingRequest;

	/** The Authentication Configuration service. */
	@Inject
	protected AuthenticationServiceConfiguration authenticationServiceConfiguration;
	
    @Inject @Via("resource") @Default(values = "/content/eaton/us/en-us/secure/profile/my-profile")
	private String userProfilelink;

	@Inject @Via("resource") @Default(booleanValues = false)
	private boolean enableShoppingCart;

	private String countryTitle;
	private String languageTitle;
	private String countryPagePath;
	private Boolean logedInState = Boolean.FALSE;
	private String userName="";
	private String orderCenterSSOLink = "False";
	private String bidManagerSSOLink = "False";
	/**
	 * Inits the.
	 */
	@PostConstruct
	protected void init() {
		LOG.debug("FullPageDrawerModel :: init() :: Start");
		links = new ArrayList<>();

		if (adminService !=null ) {
			try (ResourceResolver readServiceResourceResolver = adminService.getReadService()) {
				if(authorizationService.getTokenFromSlingRequest(slingRequest) != null) {
					userProfilelink = CommonUtil.dotHtmlLink(userProfilelink, readServiceResourceResolver);
					AuthenticationToken authenticationToken = authorizationService.getTokenFromSlingRequest(slingRequest);
					userName = authenticationToken.getUserProfile().getEmail().split("@")[0].toLowerCase();
					if(!userName.isEmpty()){
						logedInState = Boolean.TRUE;
						String applicationTag = "";
						Iterable<String> applicationAccess = authenticationToken.getUserProfile().getApplicationAccessTags();
						for (Iterator<String> applicationTags  = applicationAccess.iterator(); applicationTags.hasNext(); ) {
							applicationTag = applicationTags.next().toLowerCase();
							if (applicationTag.contains(authenticationServiceConfiguration.getOrderCenterAppRole())) {
								orderCenterSSOLink = authenticationServiceConfiguration.getOrderCenterSSOLink();
							} else if (applicationTag.contains(authenticationServiceConfiguration.getBidManagerAppRole())) {
								bidManagerSSOLink = authenticationServiceConfiguration.getBidManagerSSOLink();
							}
						}

					}
				}
				final String domain = CommonUtil.getExternalizerDomainNameBySiteRootPath(currentPage.getPath());
				countryPagePath = externalizer.externalLink(readServiceResourceResolver,domain, CommonConstants.COUNTRY) + CommonConstants.HTML_EXTN;
				final String homePagePath = CommonUtil.getHomePagePath(currentPage);
				if (StringUtils.isNotBlank(homePagePath)) {
					final Page homePage = pageManager.getPage(homePagePath);
					if ((homePage.getNavigationTitle() != null) && (!homePage.getNavigationTitle().isEmpty())) {
						languageTitle = homePage.getNavigationTitle();
					} else {
						languageTitle = homePage.getTitle();
					}

					if ((homePage.getParent() != null) && (homePage.getParent().getNavigationTitle() != null)
							&& (!homePage.getParent().getNavigationTitle().isEmpty())) {
						countryTitle = homePage.getParent().getNavigationTitle();
					} else {
						countryTitle = homePage.getParent().getTitle();
					}

				}
				if (linkList != null) {
					populateModel(links, linkList);
				}
				LOG.debug("FullPageDrawerModel :: init() :: Exit");
			} catch (Exception e) {
				LOG.error("FullPageDrawerModel: exception {}", e.getMessage());
			}
		}
	}

	/**
	 * Populate model.
	 *
	 * @param array    the array
	 * @param resource the resource
	 * @return the list
	 */
	public List<FullPageDrawerLinkListModel> populateModel(List<FullPageDrawerLinkListModel> array, Resource resource) {
		if (resource != null) {
			Iterator<Resource> linkResources = resource.listChildren();
			while (linkResources.hasNext()) {
				FullPageDrawerLinkListModel link = linkResources.next().adaptTo(FullPageDrawerLinkListModel.class);
				if (link != null) {
					array.add(link);
				}
			}
		}
		return array;
	}

	/**
	 * Gets the links.
	 *
	 * @return the links
	 */
	public List<FullPageDrawerLinkListModel> getLinks() {
		return links;
	}

	/**
	 * Gets the country title.
	 *
	 * @return the country title
	 */
	public String getCountryTitle() {
		return countryTitle;
	}

	/**
	 * Gets the language title.
	 *
	 * @return the language title
	 */
	public String getLanguageTitle() {
		return languageTitle;
	}
	/**
	 * Gets the country page path.
	 *
	 * @return the page path
	 */
	public String getCountryPagePath() {
		return countryPagePath;
	}

	/**
	 * Gets the LogedInstate.
	 *
	 * @return the loggedInstate
	 */
	public Boolean getLoggedInstate() {
		return logedInState;
	}

	/**
	 * Gets the Username.
	 *
	 * @return Gets the Username
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Gets Order Center SSO Link.
	 *
	 * @return Order Center SSO Link
	 */
	public String getOrderCenterSSOLink() {
		return orderCenterSSOLink;
	}

	/**
	 * Gets Bid Manager SSO link.
	 *
	 * @return Bid Manager SSO link
	 */
	public String getBidManagerSSOLink() {
		return bidManagerSSOLink;
	}

	
	/**
	 *  Gets User Profile Link .
	 *
	 * @return userProfilelink .
	 */
	public String getUserProfilelink() {
		return userProfilelink;
	}

	public boolean getEnableShoppingCart() {
		return enableShoppingCart;
	}
}
