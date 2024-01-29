package com.eaton.platform.core.models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Session;

import com.eaton.platform.core.services.SiteMapGenerationService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.social.srp.internal.AbstractSchemaMapper;
import com.day.cq.commons.Externalizer;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.sitemapProductFamily.SitemapBean;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.JcrQueryConstants;
import com.eaton.platform.core.constants.SiteMapConstants;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.core.util.JcrQueryUtils;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SitemapProductFamilyModel {
	
	/** The primary nav list. */
	private List<SitemapBean> primaryNavList;
	String pageType;
	
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(SitemapProductFamilyModel.class);
	private static final String CATALOG_PAGE_NAME ="catalog";
	/**
	 * Gets the primary nav list.
	 *
	 * @return sitemap List
	 */
	public List<SitemapBean> getPrimaryNavList() {
		return primaryNavList;
	}
	
	@Inject @Source("sling-object")
    private SlingHttpServletRequest slingRequest;
	
	@Inject
	@Source("sling-object")
	private ResourceResolver resourceResolver;
	
	@Inject
	@ScriptVariable
	protected Page currentPage;

	@OSGiService
	private transient SiteMapGenerationService siteMapGenerationService;
	
	
	@PostConstruct
	protected void init() {
		
		try{
			LOG.debug("SitemapProductFamilyModel :: init() :: Started");
			primaryNavList = new ArrayList<>();

			if(currentPage !=null && slingRequest != null) {
				final Page currentSitePage = currentPage.getAbsoluteParent(CommonConstants.CONTENT_PAGE_DEPTH);
				final String lastCrawledDate = slingRequest.getParameter(SiteMapConstants.REQUEST_PARAM_DATE);

				if (currentSitePage != null) {	
					final Iterator<Page> primaryNavChildren = currentSitePage.listChildren();
					
					while (primaryNavChildren.hasNext()) {
						final Page primaryNav = primaryNavChildren.next();
						
						if (primaryNav != null && !isHidden(primaryNav)) {
							final Iterator<Page> secondaryNavChildren = primaryNav.listChildren();
							
							while (secondaryNavChildren.hasNext()) {
								final Page secondaryNav = secondaryNavChildren.next();
								if (secondaryNav != null && !isHidden(secondaryNav)) {
									final Iterator<Page> ternaryNavChildren = secondaryNav.listChildren();
									
									while (ternaryNavChildren.hasNext()) {
										Page ternaryNav = ternaryNavChildren.next();
										
								      if(ternaryNav != null && !isHidden(ternaryNav) && ternaryNav.getPath().endsWith(CATALOG_PAGE_NAME)){
											final Iterator<Page> fourthNavChildren = ternaryNav.listChildren();
											
											while (fourthNavChildren.hasNext()) {
												Page fourthNav = fourthNavChildren.next();
												if (fourthNav != null && !isHidden(fourthNav)) {
													final Iterator<Page> fifthNavChildren = fourthNav.listChildren();
													
													while (fifthNavChildren.hasNext()) {
														Page fifthNav = fifthNavChildren.next();
														if (fifthNav !=null && !isHidden(fifthNav)) {
															if (StringUtils.isNotEmpty(lastCrawledDate)) {
																final DateFormat sdf = new SimpleDateFormat(SiteMapConstants.DF_YEAR_MONTH_DAY_SLASH);
																final Date crawledDate = sdf.parse(lastCrawledDate);
																final boolean modifiedDateFlag = isProductFamilyDateModified(fifthNav, crawledDate);
																if (modifiedDateFlag) {
																	primaryNavList.add(setBean(fifthNav));
																} else {
																	final boolean damAssetModified = damAssetModificationCheck(fifthNav, crawledDate);
																	if (damAssetModified) {
																		primaryNavList.add(setBean(fifthNav));
																	}
																}
															} else {
																primaryNavList.add(setBean(fifthNav));
															}
														}
													}
												   
												}
											}
										  
								      	}
									}
								  
								}
							}
						  
						}
					}
				  
				}
			}
			}catch (Exception exception) {
				    	LOG.error("Exception occured while getting the SitemapProductFamilyModel service", exception);
		  }
						LOG.debug("SitemapProductFamilyModel :: init() :: Exited");	
		}
		
	
	
	/**
	 * Sets the bean.
	 *
	 * @param nav the nav
	 * @return the sitemap bean
	 */
	public SitemapBean setBean(Page nav) {
		LOG.debug("SitemapHelper :: setBean() :: Start");
		SitemapBean navBean = new SitemapBean();
		if(nav != null) {
		navBean.setLinkTitle(nav.getTitle());
		navBean.setLinkPath(nav.getPath());
		LOG.debug("SitemapHelper :: setBean() :: Exit");
		}
		return navBean;
	}
	
	
	private final boolean isProductFamilyDateModified(final Page page,final Date crawledDate) {
		boolean modifiedDateFlag = Boolean.FALSE;
		final Resource productFamilyResource = page.getContentResource();
		try {
			final Set<String> instanceRunmode = CommonUtil.getRunModes();
			if (null != instanceRunmode) {
				if (instanceRunmode.contains(Externalizer.PUBLISH)) {
					modifiedDateFlag = isResourceModified(productFamilyResource, JcrConstants.JCR_CREATED,crawledDate);
				} else {
					modifiedDateFlag = isResourceModified(productFamilyResource,AbstractSchemaMapper.CQ_LAST_MODIFIED, crawledDate);
				}
			}

			if (!modifiedDateFlag) {
				final Resource pimResource = getPageResource(productFamilyResource, CommonConstants.PAGE_PIM_PATH);
				modifiedDateFlag = isResourceModified(pimResource,JcrConstants.JCR_LASTMODIFIED, crawledDate);
				if (null != pimResource && !modifiedDateFlag) {
					final Resource pdhResource = getPageResource(pimResource,SiteMapConstants.PROPERTY_NAME_PDH_RECORD_PATH);
					modifiedDateFlag = isResourceModified(pdhResource,JcrConstants.JCR_CREATED, crawledDate);
				}

			}
		} catch (Exception exception) {
			LOG.error("Exception occured while getting set of runmodes", exception);

		}
		return modifiedDateFlag;
	}
	
	
	private final boolean isResourceModified(final Resource resource, final String jcrProperty, final Date crawledDate) {
		boolean modifiedFlag = false;
		if (null != resource) {
			final ValueMap valueMap = resource.getValueMap();
			if (valueMap.containsKey(jcrProperty)) {
				final GregorianCalendar modifiedDate = (GregorianCalendar) valueMap.get(jcrProperty);
				final Date resourceModifiedDate = modifiedDate.getTime();
				if (crawledDate.before(resourceModifiedDate)) {
					modifiedFlag = true;
				}
			}
		}
		return modifiedFlag;
	}

	private boolean isHidden(final Page page) {
		LOG.debug("isHidden : START");
		boolean internalExcludePropertyValue = page.getProperties().get(siteMapGenerationService.getInternalExcludeProperty(), false);
		LOG.debug("Page ({}) property {}: {}", page.getPath(), siteMapGenerationService.getInternalExcludeProperty(), internalExcludePropertyValue);

		boolean isLanguageMasterPage = page.getPath().contains(CommonConstants.LANGUAGE_MASTERS_NODE_NAME);
		if (isLanguageMasterPage) {
			LOG.debug("Page ({}) is a language master page", page.getPath());
		}

		if (internalExcludePropertyValue || isLanguageMasterPage) {
			LOG.debug("Page ({}) and all of its subpages have been hidden from the product family html sitemap.", page.getPath());
		}
		
		LOG.debug("isHidden : END");
		return internalExcludePropertyValue || isLanguageMasterPage;
	}
	
	
	private boolean damAssetModificationCheck(final Page page, final Date crawledDate){
		boolean damAssetModified = false;
		
		if(page != null) {
		final ValueMap pageProperties = page.getProperties();
		final String[] propertyValue = pageProperties.get(CommonConstants.PRIMARY_SUB_CATEGORY_TAG, new String[0]);
		final DateFormat sdf = new SimpleDateFormat(SiteMapConstants.DF_YEAR_MONTH_DAY_HYPHENATED);
		final String lastCrawledDate = sdf.format(crawledDate);
		final List<Hit> damAssetsForProductFamily = getModifiedDAMAssets(propertyValue, resourceResolver, lastCrawledDate);
		if(CollectionUtils.isNotEmpty(damAssetsForProductFamily)) {
			damAssetModified = true;
		}
		}
		return damAssetModified;
	}
	
	
	private final List<Hit> getModifiedDAMAssets(final String[] pageTeaserTagArray,
			final ResourceResolver resourceResolver,
			final String crawledDate) {
			List<Hit> hitList = null;
			if(null != pageTeaserTagArray && pageTeaserTagArray.length >0){
				final Session session = resourceResolver.adaptTo(Session.class);
				final QueryBuilder queryBuilder = resourceResolver.adaptTo(QueryBuilder.class);
				final Map<String, String> queryParam = JcrQueryConstants.QUERY_PARAMS;
				queryParam.put(JcrQueryConstants.DATERANGE_LOWER_BOUND, crawledDate);
				for (int tagIndex = 0; tagIndex < pageTeaserTagArray.length; tagIndex++) {
					queryParam.put((tagIndex + 1) + JcrQueryConstants.UNDERSCORE_PROPERTY, SiteMapConstants.METADATA_CQ_TAGS);
					queryParam.put((tagIndex + 1) + JcrQueryConstants.PROPERTY_VALUE, pageTeaserTagArray[tagIndex]);
				}
				hitList = JcrQueryUtils.excuteGenericQuery(session, queryBuilder, JcrQueryConstants.QUERY_PARAMS);
			}
			return hitList;
	}
	
	
	private final Resource getPageResource(final Resource productFamilyResource, final String propertyConstant) {
		Resource resource = null;
		final ValueMap valueMap = productFamilyResource.getValueMap();
		if (valueMap.containsKey(propertyConstant)) {
			final String pagePath = valueMap.get(propertyConstant, StringUtils.EMPTY);
			resource = resourceResolver.getResource(pagePath);
		}
		return resource;
	}
}