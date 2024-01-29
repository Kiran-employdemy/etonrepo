package com.eaton.platform.core.models.secure;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.bean.LinkListTypeBean;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.core.util.SecureUtil;
import com.eaton.platform.integration.auth.constants.SecureConstants;
import com.eaton.platform.integration.auth.models.AuthenticationToken;
import com.eaton.platform.integration.auth.services.AuthorizationService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.Via;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * <html> Description: This Sling Model used for Secure Menu class.
 * </html>
 *
 * @author ICF
 * @version 1.0
 * @since 2021
 */
@Model(adaptables ={Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SecureMenuModel  {

	private static final Logger LOGGER = LoggerFactory.getLogger(SecureMenuModel.class);

	/** The Secure child page links list. */
	private List<LinkListTypeBean> childPageList;

	/** The parent page. */
	@Inject  @Via("resource")
	private String parentPage;

	@Inject
	private SlingHttpServletRequest slingRequest;

	/** The resource resolver. */
	@Inject
	@Source("sling-object")
	private ResourceResolver resourceResolver;

	@Inject
	private AuthorizationService authorizationService;


	/**
	 * Inits the.
	 */
	@PostConstruct
	protected void init() {
		LOGGER.debug("Secure Menu :: Init() :: Started {}",this.parentPage );
		AuthenticationToken authenticationToken = null;
		if(null != authorizationService){
			authenticationToken = authorizationService.getTokenFromSlingRequest(slingRequest);
		}
		this.childPageList = new ArrayList<>();
		if (null != this.parentPage &&  null != authenticationToken) {
			populateParentModel(this.parentPage, authenticationToken);
		}
		LOGGER.debug("Secure Menu :: Init() :: Exit");
	}

	/**
	 * Populate parent model.
	 *
	 * @param parentPagePath Parent page path
	 * @param authenticationToken of logged-in user
	 * the parent page path
	 * @return the list
	 */
	public List<LinkListTypeBean> populateParentModel( String parentPagePath, AuthenticationToken authenticationToken) {
		LOGGER.debug("Secure Menu :: populateParentModel() :: Started {}",parentPagePath );
		PageManager pageManager = this.resourceResolver.adaptTo(PageManager.class);
		Page parentPageModel = null;
		if (pageManager != null) {
			parentPageModel = pageManager.getPage(parentPagePath);
		}

		if (null != parentPageModel) {
			Iterator<Page> childPages = parentPageModel.listChildren();
			while (childPages.hasNext()) {
				Page childPage = childPages.next();
				setFields(childPage, authenticationToken);
			}
		}
		LOGGER.debug("Secure Menu :: populateParentModel() :: Exit {}",childPageList.size() );
		return childPageList;
	}


	/**
	 * Sets the fields.
	 * @param page page
	 */
	private void setFields(Page page, AuthenticationToken authenticationToken) {
		LOGGER.debug("Secure Menu :: setFields() :: Started {}",page.getPath() );
		LinkListTypeBean listTypeBean = new LinkListTypeBean();
		if (!page.isHideInNav() && SecureUtil.isSecureResource(page.getContentResource())
		     && authorizationService.isAuthorized(authenticationToken, page.getContentResource())) {
			listTypeBean.setLinkTitle(CommonUtil.getLinkTitle(null, page.getPath(), this.resourceResolver));
			listTypeBean.setNavName(SecureConstants.MEGA_MENU_PRIFIX +page.getName());
			childPageList.add(listTypeBean);
		}
		LOGGER.debug("Secure Menu :: setFields() :: Exit {}",childPageList.size());
	}

	/**
	 * Gets the list type array.
	 * @return the list type array
	 */
	public List<LinkListTypeBean> getChildPageList() {
		return this.childPageList;
	}

}
