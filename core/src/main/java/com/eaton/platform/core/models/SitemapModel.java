package com.eaton.platform.core.models;

import com.adobe.cq.social.srp.internal.AbstractSchemaMapper;
import com.day.cq.commons.Externalizer;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.bean.sitemap.Navigation;
import com.eaton.platform.core.bean.sitemap.SitemapBean;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.SiteMapConstants;
import com.eaton.platform.core.services.sitemap.HtmlSitemapService;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.RepositoryException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * HTML Sitemap model
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SitemapModel {


    private static final Logger LOG = LoggerFactory
            .getLogger(SitemapModel.class);
    public static final int START_NAVIGATION_LEVEL = 1;

    private List<Navigation> primaryNavList;

    @Inject
    @Via("resource")
    private String sitemapRootPath;

    @Inject
    @ScriptVariable
    @SlingObject
    protected PageManager pageManager;

    @Inject
    @Via("resource")
    private int sitemapLinkLevels;

    private List<SitemapBean> modifiedNavList;

    @Inject
    @ScriptVariable
    protected Page currentPage;

    @Inject
    @Source("sling-object")
    private ResourceResolver resourceResolver;

    @Inject
    @Source("sling-object")
    private SlingHttpServletRequest slingRequest;

    @OSGiService
    private HtmlSitemapService htmlSitemapService;

    @PostConstruct
    protected void init() {

        LOG.debug("SitemapModel :: init() :: Started");
        primaryNavList = new ArrayList<>();

        if (currentPage != null) {
            boolean allowSecureContent = CommonConstants.SITEMAP_FULL_PAGE.equals(currentPage.getName());
            Page currentSitePage = getCurrentSitePage();
            if (null != currentSitePage) {
                try {
                    final String lastCrawledDate = slingRequest
                            .getParameter(SiteMapConstants.REQUEST_PARAM_DATE);
                    if (StringUtils.isNotEmpty(lastCrawledDate) && allowSecureContent) {
                        modifiedNavList = getModifiedPages(lastCrawledDate);
                    } else {
                        primaryNavList = htmlSitemapService.getNavigationList(currentSitePage,
                                START_NAVIGATION_LEVEL,
                                sitemapLinkLevels,
                                allowSecureContent);
                    }
                } catch (RepositoryException e) {
                    LOG.error("Exception occurred while getting pages", e);
                }
            }
        }
        LOG.debug("SitemapModel :: setComponentValues() :: Exited");
    }

    private List<SitemapBean> getModifiedPages(String lastCrawledDate) throws RepositoryException {
        modifiedNavList = new ArrayList<>();
        String modifiedPageDateProperty = null;
        final String countryCode = CommonUtil
                .getCountryFromPagePath(currentPage)
                .toLowerCase();
        final String languageCode = CommonUtil
                .getUpdatedLocaleFromPagePath(currentPage)
                .replace("_", "-")
                .toLowerCase();
        final String queryPath = CommonUtil
                .getSiteRootPrefixByPagePath(currentPage
                        .getPath())
                + countryCode + CommonConstants.SLASH_STRING + languageCode + CommonConstants.SLASH_STRING;
        final Set<String> instanceRunmode = CommonUtil
                .getRunModes();

        if (null != instanceRunmode) {
            if (instanceRunmode.contains(Externalizer.PUBLISH)) {
                modifiedPageDateProperty = JcrConstants.JCR_CREATED;
            } else {
                modifiedPageDateProperty = AbstractSchemaMapper.CQ_LAST_MODIFIED;
            }
        }
        final String formattedDate = getFormattedDate(lastCrawledDate);
        return htmlSitemapService.getModifiedPages(modifiedPageDateProperty, formattedDate, queryPath,
                true, resourceResolver);
    }

    private String getFormattedDate(String lastCrawledDate) {
        final DateFormat sdf = new SimpleDateFormat(
                SiteMapConstants.DF_YEAR_MONTH_DAY_SLASH);
        Date crawledDate;
        String formattedDate = StringUtils.EMPTY;
        try {
            crawledDate = sdf.parse(lastCrawledDate);
            formattedDate = CommonUtil
                    .formatDate(
                            crawledDate,
                            SiteMapConstants.DF_YEAR_MONTH_DAY_TIME_HYPHENATED);
        } catch (ParseException e) {
            LOG.error("Parse date exception", e);
        }

        return formattedDate;
    }


    private Page getCurrentSitePage() {
        Page currentSitePage;
        if (StringUtils.isNoneBlank(sitemapRootPath)) {
            currentSitePage = pageManager.getPage(sitemapRootPath);
        } else {
            currentSitePage = currentPage
                    .getAbsoluteParent(CommonConstants.HOME_LEVEL);
        }
        return currentSitePage;
    }


    public List<SitemapBean> getModifiedNavList() {
        return modifiedNavList;
    }

    public void setModifiedNavList(List<SitemapBean> modifiedNavList) {
        this.modifiedNavList = modifiedNavList;
    }

    /**
     * Gets the sitemap root path.
     *
     * @return the sitemap root path
     */
    public String getSitemapRootPath() {
        return sitemapRootPath;
    }

    /**
     * Gets the sitemap link levels.
     *
     * @return the sitemap link levels
     */
    public int getSitemapLinkLevels() {
        return sitemapLinkLevels;
    }

    /**
     * Gets the primary nav list.
     *
     * @return sitemap List
     */
    public List<Navigation> getPrimaryNavList() {
        return primaryNavList;
    }
}
