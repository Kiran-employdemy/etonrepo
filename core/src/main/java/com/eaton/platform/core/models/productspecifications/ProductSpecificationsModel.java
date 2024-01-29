package com.eaton.platform.core.models.productspecifications;

import com.eaton.platform.core.bean.AttributeListDetail;
import com.eaton.platform.core.bean.attributetable.AttributeTable;
import com.eaton.platform.core.search.pojo.SkuSearchResponse;
import com.eaton.platform.core.search.service.SkuSearchService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;


@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ProductSpecificationsModel {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductSpecificationsModel.class);

    @Inject
    private SkuSearchService skuSearchService;
    @Self
    private SlingHttpServletRequest request;

    private SkuSearchResponse searchResponse;

    @PostConstruct
    public void init() {
        try {
            searchResponse = Objects.requireNonNull(skuSearchService).search(request);
        } catch (IOException e) {
            LOGGER.error("An IOException occurred in SkuSearchService");
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public List<AttributeListDetail> getAttributeGroupList() {
        if (searchResponse == null) {
            return new ArrayList<>();
        }
        return searchResponse.getAttributeGroups();
    }

    public Set<AttributeTable> getAttributeTableList() {
        if (searchResponse == null) {
            return new LinkedHashSet<>();
        }
        return searchResponse.getAttributeTableList();
    }

}
