package com.eaton.platform.core.services.sitemap;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.bean.sitemap.Navigation;
import com.eaton.platform.core.bean.sitemap.SitemapBean;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.SiteMapGenerationService;
import com.eaton.platform.integration.auth.constants.SecureConstants;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Helper service for HTML sitemap generation(used by Endeca)
 */
@Component(service = HtmlSitemapService.class)
public class HtmlSitemapService {

    private static final Logger LOG = LoggerFactory
            .getLogger(HtmlSitemapService.class);

    @Reference
    private SiteMapGenerationService siteMapGenerationService;

    /**
     * Get hierarchy of the pages based on the provided depth(level)
     *
     * @param currentSitePage    page to start iteration from
     * @param currentLevel       current level of pages iteration
     * @param sitemapLinkLevels  last level of iteration
     * @param allowSecureContent allow secure content to be added to the object
     * @return List of navigation items
     */
    public List<Navigation> getNavigationList(Page currentSitePage, int currentLevel,
                                              int sitemapLinkLevels, boolean allowSecureContent) {
        final List<Navigation> nextNavigationLevel = new ArrayList<>();

        final Iterator<Page> primaryNavChildren = currentSitePage
                .listChildren();
        while (primaryNavChildren.hasNext()
                && currentLevel <= sitemapLinkLevels) {
            final Page page = primaryNavChildren.next();

            if (!isHidden(page)) {
                List<Navigation> secondaryNavList = null;
                if (currentLevel < sitemapLinkLevels) {
                    secondaryNavList = getNavigationList(page, currentLevel + 1, sitemapLinkLevels, allowSecureContent);
                }
                Navigation navigation = new Navigation();
                if (allowSecureContent || !isSecurePage(page)) {
                    navigation.setNav(setBean(page));
                }
                navigation.setInnerNavList(secondaryNavList);

                nextNavigationLevel.add(navigation);

            }
        }
        return nextNavigationLevel;
    }

    /**
     * Get last modified pages info
     *
     * @param modifiedPageDateProperty the type of property to search by
     * @param formattedDate            date to start the modified pages search from
     * @param queryPath                search pages path
     * @param allowSecureContent       allow secure content to be added to the object
     * @param resourceResolver         Resource Resolver Object
     * @return List of modified pages information
     */
    public List<SitemapBean> getModifiedPages(String modifiedPageDateProperty,
                                              String formattedDate,
                                              String queryPath,
                                              boolean allowSecureContent,
                                              ResourceResolver resourceResolver) {
        final Session adminSession = resourceResolver
                .adaptTo(Session.class);
        List<SitemapBean> modifiedNavList = new ArrayList();
        try {

            if (adminSession != null) {
                QueryManager queryManager = adminSession
                        .getWorkspace().getQueryManager();
                StringBuilder sqlStatement = new StringBuilder("SELECT S.* FROM [cq:Page] AS S ");
                sqlStatement.append("INNER JOIN [cq:PageContent] AS jcrcontent ON ISCHILDNODE(jcrcontent,S) WHERE jcrcontent.[");
                sqlStatement.append(modifiedPageDateProperty);
                sqlStatement.append("] >= '");
                sqlStatement.append(formattedDate);
                sqlStatement.append("' AND ISDESCENDANTNODE(S,'");
                sqlStatement.append(queryPath);
                sqlStatement.append("')");
                Query query = queryManager
                        .createQuery(sqlStatement.toString(), "JCR-SQL2");
                QueryResult result = query.execute();
                NodeIterator nodeIter = result.getNodes();
                nodeIter.getSize();
                PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
                while (nodeIter.hasNext()) {
                    Node temp = (Node) nodeIter.next();
                    Page page = pageManager.getPage(temp.getPath());
                    if (allowSecureContent || !isSecurePage(page)) {
                        modifiedNavList.add(setBean(page));
                    }
                }
            }
        } catch (InvalidQueryException e) {
            LOG.error("Invalid query", e);
        } catch (RepositoryException e) {
            LOG.error("Repository exception during getting modified pages", e);
        }
        return modifiedNavList;
    }

    /**
     * Sets the bean.
     *
     * @param nav the nav
     * @return the sitemap bean
     */


    private SitemapBean setBean(Page nav) {
        LOG.debug("SitemapModel :: setBean() :: Start");
        SitemapBean navBean = new SitemapBean();
        if (nav != null) {
            navBean.setLinkTitle(nav.getTitle());
            navBean.setLinkPath(nav.getPath());
            LOG.debug("SitemapModel :: setBean() :: Exit");
        }
        return navBean;
    }

    private boolean isSecurePage(Page page) {
        if (null != page.getContentResource() && null != page.getContentResource().getValueMap()
                && !CommonConstants.SITEMAP_FULL_PAGE.equalsIgnoreCase(page.getName())) {
            return page.getContentResource().getValueMap().get(SecureConstants.SECUREPAGE, false);
        }
        return false;
    }

    private boolean isHidden(final Page page) {
        LOG.debug("isHidden : START");

        if (CommonConstants.SITEMAP_FULL_PAGE.equals(page.getName())) {
            return true;
        }
        boolean internalExcludePropertyValue = page.getProperties().get(siteMapGenerationService.getInternalExcludeProperty(), false);
        LOG.debug("Page ({}) property {}: {}", page.getPath(), siteMapGenerationService.getInternalExcludeProperty(), internalExcludePropertyValue);
        if (internalExcludePropertyValue) {
            LOG.debug("Page ({}) and all of its subpages have been hidden from the html sitemap.", page.getPath());
        }
        LOG.debug("isHidden : END");
        return internalExcludePropertyValue;
    }
}
