package com.eaton.platform.core.models.submittalbuilder;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.constants.AssetDownloadConstants;
import com.eaton.platform.core.models.EndecaFacetTag;
import com.eaton.platform.core.models.eatonsiteconfig.EatonSiteConfigModel;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.EndecaRequestService;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.constants.EndecaConstants;
import com.eaton.platform.integration.endeca.services.EndecaService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SubmittalBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(SubmittalBuilder.class);

    private static final String EMPTY_JSON = "{}";
    private static final String EMPTY_JSON_ARRAY = "[]";

    @OSGiService
    private EndecaRequestService endecaRequestService;

    @OSGiService
    private EndecaService endecaService;

    @OSGiService
    private AdminService adminService;

    @Inject
    @Via("resource")
    private SubmittalScope submittalScope;

    @Inject
    @Via("resource")
    private SubmittalAttributes submittalAttributes;

    @Inject
    private Resource resource;

    @Inject
    private SlingHttpServletRequest slingRequest;

    private JsonArray submittalFilters;
    private JsonArray submittalResults;
    private int totalCount;

    @PostConstruct
    protected void init() throws Exception {
        try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
            final TagManager tagManager = adminResourceResolver.adaptTo(TagManager.class);
            final PageManager pageManager = adminResourceResolver.adaptTo(PageManager.class);
            final Page page = pageManager.getContainingPage(resource);

            String scopeConfig = "[]";
            String attributesConfig = "[]";
            String defaultSortOrder = "[{ \"name\":\"\", \"order\":\"ASC\"}]"; // This is the only way that Endeca lets us not order on anything.
            String defaultActiveFacets = "[]";

            if(null != submittalScope) {
                scopeConfig = new ObjectMapper().writeValueAsString(
                        submittalScope.getFamilies().stream()
                                .map(family -> family.getId())
                                .toArray());
            }
            if (null != submittalAttributes) {
                attributesConfig = new ObjectMapper().writeValueAsString(
                        submittalAttributes.getAttributes().stream()
                                .map(attribute -> attribute.getId())
                                .map(tagId -> tagManager.resolve(tagId).adaptTo(EndecaFacetTag.class).getFacetId())
                                .toArray());
            }


            if (submittalScope != null && submittalAttributes != null) {
                final int pageSize = slingRequest.adaptTo(EatonSiteConfigModel.class).getSiteConfig().getPageSize();

                final EndecaServiceRequestBean endecaRequestBean =
                        endecaRequestService.getSubmitBuilderEndecaRequestBean(page, adminResourceResolver,
                                scopeConfig, attributesConfig, defaultSortOrder, defaultActiveFacets, pageSize, 0);

                final JsonObject submittalResponseJson = endecaService.getSubmittalResponse(endecaRequestBean);
                if (submittalResponseJson.has("filters") && submittalResponseJson.has("results")) {
                    submittalFilters = submittalResponseJson.get("filters").getAsJsonArray();
                    submittalResults = submittalResponseJson.get("results").getAsJsonArray();
                    totalCount = submittalResponseJson.get(EndecaConstants.TOTAL_COUNT_STRING).getAsInt();
                }

            }
        } catch (JsonGenerationException e) {
            LOG.error("Could not convert submittal filters into json.", e);
        } catch (JsonMappingException e) {
            LOG.error("Could not convert submittal filters into json.", e);
        } catch (IOException e) {
            LOG.error("Could not convert submittal filters into json.", e);
        }
    }

    public String getSubmittalScopeJson() {
        String submittalScopeJson = EMPTY_JSON;
        try {
            if (submittalScope != null) {
                submittalScopeJson = new ObjectMapper().writeValueAsString(submittalScope);
            }
        } catch (java.io.IOException e) {
            LOG.error("Could not convert submittal filters into json.", e);
        }
        return submittalScopeJson;
    }

    public String getSubmittalAttributesJson() {
        String submittalAttributesJson = EMPTY_JSON;
        try {
            if (submittalAttributes != null) {
                submittalAttributesJson = new ObjectMapper().writeValueAsString(submittalAttributes);
            }
        } catch (java.io.IOException e) {
            LOG.error("Could not convert submittal attributes into json.", e);
        }
        return submittalAttributesJson;
    }

    public String getResultJson() {
        if (submittalResults != null) {
            return submittalResults.toString();
        } else {
            return EMPTY_JSON_ARRAY;
        }
    }

    public String getFilterJson() {
        if (submittalFilters != null) {
            return submittalFilters.toString();
        } else {
            return EMPTY_JSON_ARRAY;
        }
    }

    public int getTotalCount() {
        return totalCount;
    }

    public String getResultServletUrl() {
        return resource.getPath().concat(AssetDownloadConstants.SUBMITTAL_BUILDER_RESULTS_SELECTOR);
    }

    public String getDownloadServletUrl() {
        return resource.getPath().concat(AssetDownloadConstants.SUBMITTAL_BUILDER_DOWNLOAD_SELECTOR);
   }

    public String getEmailServletUrl() {
        return resource.getPath().concat(AssetDownloadConstants.SUBMITTAL_BUILDER_EMAIL_SELECTOR);
    }
}