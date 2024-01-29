package com.eaton.platform.core.util;

import java.util.*;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.InvalidQueryException;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.eaton.platform.core.constants.JcrQueryConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.constants.CommonConstants;

/**
 * The Class ReferenceUtil.
 */
public class ReferenceUtil {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(ReferenceUtil.class);

	/** The Constant LIST_TYPE. */
	private static final String LIST_TYPE = "listType";

	/** The constant For Query **/
	private static final String SLING_RESOURCETYPE_PATH = "eaton/components/layout/mega-menu";
	private static final String MEGAMENUTITLEPATH = "megaMenuTitlePath";
	private static final String JCR_CONTENT_HIDEINNAV = "@jcr:content/hideInNav";

	/**
	 * Gets the mega menu reference.
	 *
	 * @param resourceResolver
	 *            the resource resolver
	 * @param currentPage
	 *            the current page
	 * @return the mega menu reference
	 */
	public static List<String> getMegaMenuReference(ResourceResolver resourceResolver, Page currentPage) {
		LOGGER.debug("ReferenceUtil :: getMegaMenuReference() :: Start");
		List<String> megaMenuReferenceList = new ArrayList<>();
		String homePagePath = CommonUtil.getHomePagePath(currentPage);
		if (StringUtils.isNotBlank(homePagePath)) {
			
			String primaryNavPath = homePagePath.concat(CommonConstants.PRIMARY_NAV_PATH);
			String temp=null;
			Resource primaryNavRes=null;
			String mega_menu = homePagePath.concat("/jcr:content/root/header");
			Resource mega_menu_resource = resourceResolver.getResource(mega_menu);
			 if(mega_menu_resource.getValueMap().get("reference")!=null){
				 temp= mega_menu_resource.getValueMap().get("reference").toString();
				 primaryNavRes= resourceResolver.getResource(temp);
			}
		
			
			if (primaryNavRes != null) {// SonarQube Null Pointer Issue fix
				// convert to primary nav sling model -starts
				ValueMap primaryNavResVM = primaryNavRes.getValueMap();
				String primaryNavListType = CommonUtil.getStringProperty(primaryNavResVM, LIST_TYPE);
				if (StringUtils.equals(CommonConstants.MANUAL_LIST, primaryNavListType)) {
					getReferenceListFromManualList(resourceResolver, megaMenuReferenceList, primaryNavPath);
				} else {
					getReferenceFromChildNodes(resourceResolver, currentPage, megaMenuReferenceList);
				}
			}
		}
		LOGGER.debug("ReferenceUtil :: getMegaMenuReference() :: Exit");
		return megaMenuReferenceList;
	}

	/**
	 * Gets the reference from child nodes.
	 *
	 * @param resourceResolver
	 *            the resource resolver
	 * @param currentPage
	 *            the current page
	 * @param megaMenuReferenceList
	 *            the mega menu reference list
	 * @return the reference from child nodes
	 */
	private static void getReferenceFromChildNodes(ResourceResolver resourceResolver, Page currentPage,
			List<String> megaMenuReferenceList) {
		LOGGER.debug("ReferenceUtil :: getReferenceFromChildNodes() :: Start");
		Page homePage = CommonUtil.getHomePage(currentPage);
		if (homePage != null) {
			ValueMap homepageVM = homePage.getProperties();
			String languageOverlayPath = CommonUtil.getStringProperty(homepageVM, "overlay-path");
			if (StringUtils.isNotBlank(languageOverlayPath)) {
				Iterator<Page> primaryNavPagesList = homePage.listChildren();
				while (primaryNavPagesList.hasNext()) {
					Page primaryNavPage = primaryNavPagesList.next();
					ValueMap primaryPageProp = primaryNavPage.getProperties();
					String hideInNav = CommonUtil.getStringProperty(primaryPageProp, "hideInNav");
					if (StringUtils.isBlank(hideInNav)) {
						try {
							final List<Hit> megaMenuRefListResult = getQueryResult(languageOverlayPath,
									primaryNavPage.getPath(), resourceResolver);
							String megaMenuRefPagePath = StringUtils.EMPTY;
							if (null != megaMenuRefListResult && megaMenuRefListResult.size() != 0) {
								final Iterator<Hit> megaMenuRefIterator = megaMenuRefListResult.iterator();
								while (megaMenuRefIterator.hasNext()) {
									final Hit next = megaMenuRefIterator.next();
									String megaMenuPagePath = next.getPath();
									if (megaMenuPagePath.contains(CommonConstants.JCR_CONTENT_STR)) {
										megaMenuRefPagePath = megaMenuPagePath.replace(CommonConstants.JCR_CONTENT_STR, "_jcr_content");
									}
									megaMenuReferenceList
											.add(primaryNavPage.getName().concat(",").concat(megaMenuRefPagePath));
								}
							}
						} catch (InvalidQueryException e) {
							LOGGER.error("Query is not valid for this scenario.", e);
						} catch (RepositoryException e) {
							LOGGER.error("Repository Exception - ", e);
						}
					}
				}
			}
		}
		LOGGER.debug("ReferenceUtil :: getReferenceFromChildNodes() :: Exit");
	}

	private static List<Hit> getQueryResult(String languageOverlayPath, String primaryNavPage, ResourceResolver resourceResolver) {
		Map<String, String> queryParam = getReferenceQueryParamsMap();
		queryParam.put(JcrQueryConstants.PROP_PATH, languageOverlayPath);
		queryParam.put(CommonConstants.PROP_NAME,CommonConstants.SLING_RESOURCETYPE);
		queryParam.put(CommonConstants.PROP_VALUE,SLING_RESOURCETYPE_PATH);
		queryParam.put(CommonConstants.SECOND_PROP_NAME,MEGAMENUTITLEPATH);
		queryParam.put(CommonConstants.SECOND_PROP_VALUE,primaryNavPage);
		queryParam.put(CommonConstants.THIRD_PROP_NAME,JCR_CONTENT_HIDEINNAV);
		queryParam.put(CommonConstants.THIRD_PROP_OPERATION,CommonConstants.NOT);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(StringUtils.join(queryParam));
		}

		final Session session = resourceResolver.adaptTo(Session.class);
		final QueryBuilder queryBuilder = resourceResolver.adaptTo(QueryBuilder.class);
		final List<Hit>  megaMenuRefListResult = JcrQueryUtils.excuteGenericQuery(session, queryBuilder, queryParam);
		return megaMenuRefListResult;
	}

	private static final Map<String, String> getReferenceQueryParamsMap() {
		final Map<String, String> queryParam = new HashMap<>();
		queryParam.put(JcrQueryConstants.PROP_TYPE, JcrConstants.NT_BASE);
		queryParam.put(JcrQueryConstants.P_LIMIT, JcrQueryConstants.INFINITY_VALUE);
		return queryParam;
	}

	/**
	 * Gets the reference list from manual list.
	 *
	 * @param resourceResolver
	 *            the resource resolver
	 * @param megaMenuReferenceList
	 *            the mega menu reference list
	 * @param primaryNavRes
	 *            the primary nav res
	 * @return the reference list from manual list
	 */
	private static void getReferenceListFromManualList(ResourceResolver resourceResolver,
			List<String> megaMenuReferenceList, String primaryNavPath) {
		LOGGER.debug("ReferenceUtil :: getReferenceListFromManualList() :: Start");
		Resource manualLinksRes = resourceResolver.getResource(primaryNavPath + "/manualLinks");
		if (manualLinksRes != null) {
			Iterator<Resource> childManualLiksResList = manualLinksRes.listChildren();

				while (childManualLiksResList.hasNext()) {
					Resource manualLinksResource = childManualLiksResList.next();
					ValueMap manualLinksResourceVM = manualLinksResource.getValueMap();

					String primaryNavPagePath = CommonUtil.getStringProperty(manualLinksResourceVM, "path");
					String overlayPagePath = CommonUtil.getStringProperty(manualLinksResourceVM, "overlayPath");

					Resource navigationRes = resourceResolver.getResource(primaryNavPagePath);
					if (primaryNavPagePath != StringUtils.EMPTY && overlayPagePath != StringUtils.EMPTY) {
						Resource overlayPathRes = resourceResolver
								.getResource(overlayPagePath.concat("/jcr:content/menuoverlay"));

						String css = StringUtils.EMPTY;
						String ref = StringUtils.EMPTY;
						if (navigationRes != null) {
							css = navigationRes.getName();
						}
						if (overlayPathRes != null) {
							ref = overlayPathRes.getPath();
						}
						megaMenuReferenceList.add(css.concat(",").concat(ref));
					}
				}
			
		}
		LOGGER.debug("ReferenceUtil :: getReferenceListFromManualList() :: Exit");
	}

	/**
	 * Gets the full page drawer reference.
	 *
	 * @param resourceResolver
	 *            the resource resolver
	 * @param currentPage
	 *            the current page
	 * @return the full page drawer reference
	 */
	public static String getFullPageDrawerReference(ResourceResolver resourceResolver, Page currentPage) {
		LOGGER.debug("ReferenceUtil :: getFullPageDrawerReference() :: Start");
		String countrySelectorRefPath = StringUtils.EMPTY;
		String fullPageDrawerResPath = getReference(resourceResolver, currentPage,
				"/jcr:content/root/header/full-page-drawer");
		Resource fullPageDrawerResource = resourceResolver.getResource(fullPageDrawerResPath);
		if (fullPageDrawerResource != null) {
			ValueMap fullPageDrawerResourceVM = fullPageDrawerResource.getValueMap();
			countrySelectorRefPath = CommonUtil.getStringProperty(fullPageDrawerResourceVM, "reference");
		}
		LOGGER.debug("ReferenceUtil :: getFullPageDrawerReference() :: Exit");
		return countrySelectorRefPath;
	}

	/**
	 * Gets the reference.
	 *
	 * @param resourceResolver
	 *            the resource resolver
	 * @param currentPage
	 *            the current page
	 * @param nodePath
	 *            the node path
	 * @return the reference
	 */
	public static String getReference(ResourceResolver resourceResolver, Page currentPage, String nodePath) {
		LOGGER.debug("ReferenceUtil :: getReference() :: Start");
		String reference = StringUtils.EMPTY;
		String homePagePath = CommonUtil.getHomePagePath(currentPage);
		if (StringUtils.isNotBlank(homePagePath)) {
			String resourcePath = homePagePath.concat(nodePath);
			Resource fullPageDrawerResource = resourceResolver.getResource(resourcePath);
			if (fullPageDrawerResource != null) {
				reference = resourcePath;
			}
		}
		LOGGER.debug("ReferenceUtil :: getReference() :: Exit");
		return reference;
	}
}
