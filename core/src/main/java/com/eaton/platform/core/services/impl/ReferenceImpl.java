package com.eaton.platform.core.services.impl;

import com.adobe.granite.references.Reference;
import com.adobe.granite.references.ReferenceAggregator;
import com.adobe.granite.references.ReferenceList;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.wcm.commons.ReferenceSearch;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.JcrQueryConstants;
import com.eaton.platform.core.constants.PimImporterConstants;
import com.eaton.platform.core.predicates.MSMPredicates;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.ReferenceService;
import com.eaton.platform.core.util.JcrQueryUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.apache.sling.query.SlingQuery.$;

@Component(service = ReferenceService.class, immediate = true)

public class ReferenceImpl implements ReferenceService {

    @org.osgi.service.component.annotations.Reference
    private ReferenceAggregator referenceAggregator;

    @org.osgi.service.component.annotations.Reference
    protected AdminService adminService;

    private static final Logger log = LoggerFactory.getLogger(ReferenceImpl.class);
    public List<Hit> getReferenceList(final String path, final ResourceResolver resourceResolver,
                                      final Map<String, String> queryParam) {
        List<Hit> referenceList = null;
        final Session session = resourceResolver.adaptTo(Session.class);
        final QueryBuilder queryBuilder = resourceResolver.adaptTo(QueryBuilder.class);
        queryParam.put(JcrQueryConstants.FULL_TEXT, path);
        referenceList = JcrQueryUtils.excuteGenericQuery(session, queryBuilder, queryParam);
        return referenceList;
    }

    public ReferenceList getReferenceListForPageAndAssets(final String path, final ResourceResolver resourceResolver) {
        final Map<String, String> queryParam = getReferenceQueryParamsMap();
        queryParam.put(JcrQueryConstants.PROP_PATH, CommonConstants.PIM_BASE);
        final List<Hit> hitList = getReferenceList(path, resourceResolver, queryParam);
        final Set<Resource> filteredSet = getFilteredProductPathList(hitList, resourceResolver);
        final Resource currentResource = resourceResolver.getResource(path);
        final ReferenceList referenceList = referenceAggregator.createReferenceList(currentResource, new String[0]);
        for (Resource pimResource : filteredSet) {
            if (null != pimResource) {
                final Reference reference = new Reference(pimResource,currentResource, CommonConstants.PIM_CONTENT);
                referenceList.add(reference);
            }
        }
        return referenceList;
    }

    public final List<String> getFilteredReferenceList(final List<Hit> referenceList, Predicate<Hit> predicate) {

        return (Optional.ofNullable(referenceList).orElse(Collections.emptyList()))
                .stream()
                .filter(predicate::test)
                .map( hit -> {
                    try {
                        return hit.getPath();
                    } catch (RepositoryException e) {
                        log.error(e.getLocalizedMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

    }

    public final Set<Resource> getFilteredProductPathList(final List<Hit> referenceList,
                                                        final ResourceResolver resourceResolver) {
        return (Optional.ofNullable(referenceList).orElse(Collections.emptyList()))
                .stream().map( hit -> {
                    try {
                        final String pimPath = hit.getPath();
                        final Resource pimResource = resourceResolver.resolve(pimPath);
                        return getPimBaseResource(pimResource);
                    } catch (RepositoryException e) {
                        log.error(e.getLocalizedMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

    }

    final Resource getPimBaseResource(final Resource resource) {
        Resource pimBaseResource = null;
        if (null != resource) {
            if (PimImporterConstants.RESOURCE_TYPE_PATH.equals(resource.getResourceType())) {
                pimBaseResource =  resource;
            } else {
                final List<Resource> resources = $(resource).parents(PimImporterConstants.RESOURCE_TYPE_PATH).asList();
                final Optional<Resource> resourceOptional = resources.stream().findFirst();
                if (resourceOptional.isPresent()) {
                    pimBaseResource = resourceOptional.get();
                }
            }
        }
        return pimBaseResource;
    }

    public void updatePathReferences(final String currentPath, final String destinationPath) {
        try (ResourceResolver adminServiceReadService = adminService.getReadService()) {
            final List<Hit> referenceList = getReferenceList(currentPath, adminServiceReadService, getReferenceQueryParamsMap());
            final List<String> referencePathList = getFilteredReferenceList(referenceList, MSMPredicates.liveSyncPredicate);
            if (null!= referencePathList && !referencePathList.isEmpty()) {
                ReferenceSearch referenceSearch = new ReferenceSearch();
                try (ResourceResolver writeService = adminService.getWriteService()) {
                    referenceSearch.adjustReferences(writeService, currentPath, destinationPath,
                            referencePathList.toArray(new String[0]));
                }
            }
        }
    }

    public List<String> getDeletePageReference(final String path, final ResourceResolver resourceResolver) {
        List<String> filteredReferenceList = null;
        try (ResourceResolver adminServiceReadService = adminService.getReadService()) {
            if (StringUtils.isNotEmpty(path)) {
                final List<Hit> referenceList = getReferenceList(path, adminServiceReadService, getReferenceQueryParamsMap());
                filteredReferenceList = getFilteredReferenceList(referenceList, MSMPredicates.liveSyncPredicate);
            }
        }
        return filteredReferenceList;
    }

    private final Map<String, String> getReferenceQueryParamsMap() {
        final Map<String, String> queryParam = new HashMap<>();
        queryParam.put(JcrQueryConstants.PROP_TYPE, JcrConstants.NT_BASE);
        queryParam.put(JcrQueryConstants.P_LIMIT, JcrQueryConstants.INFINITY_VALUE);
        return queryParam;
    }

}
