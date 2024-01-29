package com.eaton.platform.core.models.sitemap;


import com.day.cq.commons.Externalizer;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.RepositoryException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;
import java.util.Arrays;


/**
 * Asset SiteMap Generator.
 */
@Model(adaptables = {SlingHttpServletRequest.class,Resource.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AssetSiteMapModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(AssetSiteMapModel.class);

    @Inject
    private SlingHttpServletRequest request;


    @Inject
    private Page currentPage;


    private String hostDomain;

    private Map<String,String> assetLinks;

    @Inject @Via("resource") @Default(values="/content/dam/eaton")
    private String damPath;

    private transient QueryBuilder queryBuilder;

    private static final String EATON_LANGUAGE_NAME_SPACE ="eaton:country/";

    /**
     * init()
     */
    @PostConstruct
    protected void init() {
        try {
            LOGGER.debug("AssetSiteMapModel:: init() start");
            ResourceResolver resourceResolver = request.getResourceResolver();
            String mapTagString[] = null;
            if(currentPage!=null) {
                ValueMap mapTag = currentPage.getProperties();
                if(mapTag!=null) {
                    mapTagString = mapTag.get(CommonConstants.PRIMARY_SUB_CATEGORY_TAG, String[].class);
                }
            }
            assetLinks = new HashMap<>();
            String range = request.getParameter(CommonConstants.PARAM_RANGE);
            Externalizer externalizer = resourceResolver.adaptTo(Externalizer.class);
            if(externalizer != null) {
                this.hostDomain = externalizer.publishLink(resourceResolver, StringUtils.EMPTY);
                // Truncate slash from the url
                this.hostDomain = this.hostDomain.substring(0,this.hostDomain.length()-1);
            }
            List<Hit> assetsFound = CommonUtil.findAssetsByLocale(resourceResolver,damPath,getLocaleTag(resourceResolver),range,queryBuilder);
            for(Hit hit : assetsFound) {
                Resource resource = hit.getResource();
                if(resource !=null && resource.getChild("jcr:content/metadata") != null) {
                    Resource metaDataResource  = resource.getChild("jcr:content/metadata");
                    if(metaDataResource!=null) {
                        ValueMap resourceValueMap = metaDataResource.getValueMap();
                        String title = resourceValueMap.get(DamConstants.DC_TITLE, String.class);
                        if(resourceValueMap!=null) {
                                String[] assetTag = resourceValueMap.get("cq:tags", String[].class);
                                Boolean isEqual = checkEquality(mapTagString,assetTag);
                                if(assetTag!=null && isEqual) {
                                    assetLinks.put(title, resource.getPath());
                                }
                            }
                    }
                }
            }
            LOGGER.debug(":::::::: Assets Found = {}",assetsFound.size());
        }catch (RepositoryException e){
            LOGGER.error("::::: doGet() : Asset SiteMapGenerator : Repository Exception ::::::",e);
        }
    }

    public static boolean checkEquality(String[] s1, String[] s2)
    {
        if (s1 == null || s2 == null) {
            return false;
        }
        Arrays.sort(s1);
        Arrays.sort(s2);

        int i = 0;
        int j = 0;
        int n = s1.length;
        int m = s2.length;
        for (i = 0; i < n; i++) {
            for (j = 0; j < m; j++)
                if (s1[i].equals(s2[j]))
                    break;

            if (j == m)
                return false;
        }
        return true;
    }

    public Map<String, String> getAssetLinks() {
        return assetLinks;
    }

    /**
     * @param resourceResolver
     * @return Locale Tag based on current page. If 'locale' param exist in the url, it takes the precedence.
     */
    private String getLocaleTag(ResourceResolver resourceResolver){
        queryBuilder = resourceResolver.adaptTo(QueryBuilder.class);
        String locale = request.getParameter(CommonConstants.PARAM_LOCALE);
        LOGGER.debug(":::::: getLocaleTag : Locale :::::{}", locale);
        if(locale != null){
            return (EATON_LANGUAGE_NAME_SPACE + locale);
        }
        Locale localeObj = CommonUtil.getLocaleFromPagePath(currentPage);
        return CommonUtil.findTagByName(resourceResolver,localeObj.getCountry());
    }
}
