package com.eaton.platform.core.models.primarynav;

import java.util.*;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.eaton.platform.core.util.SecureUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.bean.LinkListTypeBean;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.util.CommonUtil;

/**
 * <html> Description: This Sling Model used in PrimaryNavigationHelper class.
 * </html>
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class PrimaryNavContentList extends PrimaryNavigationModel {

	/** The manual links list. */
	private List<PrimaryNavManualLinksModel> manualLinksList;

	/** The child page links list. */
	private List<LinkListTypeBean> childPageList;

	/** The parent page. */
	@Inject
	private String parentPage;

	/** The manual links. */
	@Inject
	private Resource manualLinks;

	/** The count. */
	@Inject
	private int count;

	/** The resource resolver. */
	@Inject
	@Source("sling-object")
	private ResourceResolver resourceResolver;

	 private static final String MEGA_MENU_PATH = "mega-menu-";

	/**
	 * Inits the.
	 */
	@PostConstruct
	protected void init() {
		final String listType = null != super.getListType() ? super.getListType() : StringUtils.EMPTY;
		switch (listType) {
		case CommonConstants.CHILD_PAGES: {
			childPageList = new ArrayList<>();
			if (null != getParentPagePath()) {
				populateParentModel(this.childPageList, getParentPagePath());

			}
			break;
		}
		case CommonConstants.MANUAL_LIST: {
			this.manualLinksList = new ArrayList<>();
			if (null != this.manualLinks) {
				populateManualLinkModel(this.manualLinksList, this.manualLinks);
			}
			break;
		}
		default: {

			break;
		}
		}
	}

	/**
	 * Populate parent model.
	 *
	 * @param childPageList
	 *            the child page list
	 * @param parentPagePath
	 *            the parent page path
	 * @return the list
	 */
	public List<LinkListTypeBean> populateParentModel(List<LinkListTypeBean> childPageList, String parentPagePath) {

		PageManager pageManager = this.resourceResolver.adaptTo(PageManager.class);
		Page parentPageModel = null;
		if (pageManager != null) {
			parentPageModel = pageManager.getPage(parentPagePath);
		}

		if (null != parentPageModel) {
			Iterator<Page> childPages = parentPageModel.listChildren();
			while (childPages.hasNext()) {
				Page childPage = childPages.next();
				setFields(childPageList, childPage);
			}
		}

		return childPageList;
	}

	/**
	 * Populate manual link model.
	 *
	 * @param manualLinksList
	 *            the manual links list
	 * @param resource
	 *            the resource
	 * @return the list
	 */

	public List<PrimaryNavManualLinksModel> populateManualLinkModel(List<PrimaryNavManualLinksModel> manualLinksList,
			Resource resource) {
		if (null != resource) {
			Iterator<Resource> linkResources = resource.listChildren();
			while (linkResources.hasNext()) {
				PrimaryNavManualLinksModel manualLink = linkResources.next().adaptTo(PrimaryNavManualLinksModel.class);
				if (null != manualLink) {
					//EAT-4774 -  Exclude secure Page/Asset from primary navigation list
					Resource linkPathResource = CommonUtil.getResourceFromLinkPath(manualLink.getPageLink(), resourceResolver);
					if (!SecureUtil.isSecureResource(linkPathResource)) {
						manualLinksList.add(manualLink);
					}
				}
			}
		}
		return manualLinksList;
	}

	/**
	 * Sets the fields.
	 *
	 * @param array
	 *            the array
	 * @param page
	 *            the page
	 */
	private void setFields(List<LinkListTypeBean> array, Page page) {
		LinkListTypeBean listTypeBean = new LinkListTypeBean();
		//EAT-4774 -  Exclude secure Pages from primary navigation list
		if (!page.isHideInNav() && !SecureUtil.isSecureResource(page.getContentResource()) && array.size() < this.count) {
			listTypeBean.setLinkTitle(CommonUtil.getLinkTitle(null, page.getPath(), this.resourceResolver));
			listTypeBean.setPageLink(page.getPath());
			listTypeBean.setTemplateName(
					page.getProperties().get(CommonConstants.TEMPLATE_PROP_KEY, StringUtils.EMPTY));
			if (page.getProperties().containsKey(CommonConstants.PUBLICATION_DATE)) {
				GregorianCalendar gc = page.getProperties().get(CommonConstants.PUBLICATION_DATE,
						GregorianCalendar.class);
				if (gc != null) {
					listTypeBean.setPublicationDate(gc.getTimeInMillis());
				}
			}
			Calendar lastModified = page.getLastModified();
			if(Objects.nonNull(lastModified)) {
				listTypeBean.setLastModifiedDate(Long.toString(lastModified.getTimeInMillis()));
			}

			GregorianCalendar gc = page.getProperties().get(CommonConstants.JCR_CREATED, GregorianCalendar.class);
			if (gc != null) {
				listTypeBean.setCreatedDate(gc.getTimeInMillis());
			}
			listTypeBean.setNavName(MEGA_MENU_PATH+page.getName());
			listTypeBean.setNavTitle(page.getTitle());
			array.add(listTypeBean);
		}
	}

	/**
	 * Gets the parentPage.
	 *
	 * @return the parentPage
	 */
	public String getParentPagePath() {

		return this.parentPage;
	}

	/**
	 * Gets the list type array.
	 *
	 * @return the list type array
	 */
	public List<?> getListTypeArray() {
		List<?> viewList = new ArrayList<>();
		if (StringUtils.equals(CommonConstants.CHILD_PAGES, super.getListType())) {
			viewList = childPageList;
		} else if (StringUtils.equals(CommonConstants.MANUAL_LIST, super.getListType())) {
			viewList = manualLinksList;
		}
		return viewList;
	}
}
