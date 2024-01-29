package com.eaton.platform.core.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;

import com.day.cq.commons.RangeIterator;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.bean.SectionNavigationBean;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import com.eaton.platform.core.constants.CommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <html> Description: This Sling Model used to inject dialog values. </html>
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SectionNavigationModel {
	/** The logger */
	private static final Logger logger = LoggerFactory.getLogger(SectionNavigationModel.class);
	
	/** The section nav parent page path. */
	@Inject
	private String sectionNavParentPagePath;

	/** The tags. */
	@Inject
	private String[] tags;

	/** The tags type. */
	@Inject
	private String tagsType;

	/** The count navigation. */
	@Inject
	private int countNavigation;
	
	/** The child page links list. */
	private List<SectionNavigationBean> childPageLinksList;
	
    /** The resource resolver. */
    @Inject @Source("sling-object")
    private ResourceResolver resourceResolver;
    
    /** The resource page. */
    @Inject
    private Page resourcePage;
	
	/**
	 * Inits the.
	 */
    @PostConstruct
    protected void init() {
    	childPageLinksList = new ArrayList<>();
		if (null != tags) {
			populateTagsModel();
		} else {
    		populateParentModel(childPageLinksList, getSectionNavParentPagePath());
    	}
    }
	
	/**
	 * Populate parent model.
	 *
	 * @param childPageLinksList the child page and tags links list
	 * @param parentPagePath the parent page path
	 * @return the list
	 */
    public List<SectionNavigationBean> populateParentModel(List<SectionNavigationBean> childPageLinksList, String parentPagePath) {
    	PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
    	Page parentPage =null;
    	if(null!=pageManager)//SonarQube Null Pointer Issue fix
    		parentPage = pageManager.getPage(parentPagePath);
    	if (null != parentPage) {
    		Iterator<Page> childPages = parentPage.listChildren();
    		while (childPages.hasNext()) {
    			Page childPage = childPages.next();
    			setFields(childPageLinksList, childPage);
    		}
    	}
    	return childPageLinksList;
    }
	
	/**
	 * Populate tags model.
	 */
	public void populateTagsModel() {
		if (null != resourceResolver) {
			TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
			RangeIterator<Resource> tagsIterator = null;
			if(null != tagManager) {
                boolean oneMatchIsEnough = StringUtils.equals(CommonConstants.ANY, tagsType);
                tagsIterator = tagManager.find(CommonUtil.getHomePagePath(resourcePage), tags, oneMatchIsEnough);
                if(null != tagsIterator) {
                    while (tagsIterator.hasNext()) {
						getTaggedPage(tagsIterator);
                    }
                }
            }
		}
	}

	/**
	 * Get a tagged page's path and check if the tagged page is below the parent page path (any levels below, not only child page)
	 * @param tagsIterator
	 */
	private void getTaggedPage(RangeIterator<Resource> tagsIterator) {
		final Resource tagResource =  tagsIterator.next();
		final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
		Page tagPage = null;
		if (null != pageManager) {
			tagPage = pageManager.getPage(StringUtils.removeEnd(tagResource.getPath(), JcrConstants.JCR_CONTENT));
		}
		try {
			if (null != tagPage && CommonUtil.startsWithAnySiteContentRootPath(tagResource.getPath()) && tagPage.getPath().contains(getSectionNavParentPagePath())) {
				setFields(childPageLinksList, tagPage);
			}
		} catch (NullPointerException e) {
			logger.error("In getTaggedPage, contains method is throwing null pointer exception because getSectionNavParentPagePath is returning null: " + String.valueOf(e));
		}
	}

	/**
	 * Sets the fields.
	 *
	 * @param array the array
	 * @param page the page
	 */
	private void setFields(List<SectionNavigationBean> array, Page page) {
		SectionNavigationBean listTypeBean = new SectionNavigationBean();
		if (!page.isHideInNav() && array.size() < countNavigation) {
			listTypeBean.setLinkTitle(CommonUtil.getLinkTitle(null, page.getPath(), resourceResolver));
			listTypeBean.setPageLink(page.getPath());
			array.add(listTypeBean);
		}
	}
	
	/**
	 * Gets the section nav parent page path.
	 *
	 * @return the section nav parent page path
	 */
	public String getSectionNavParentPagePath() {
		String parentPage = StringUtils.EMPTY;
		if(null == sectionNavParentPagePath && null != resourcePage) {
			parentPage = resourcePage.getPath();
		} else {
			parentPage = sectionNavParentPagePath;
		}
		
		return parentPage;
	}

	/**
	 * Gets the child page links list.
	 *
	 * @return the child page links list
	 */
	public List<SectionNavigationBean> getChildPageLinksList() {
		return childPageLinksList;
	}
}
