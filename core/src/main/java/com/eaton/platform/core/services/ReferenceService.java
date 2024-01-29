package com.eaton.platform.core.services;

import com.adobe.granite.references.ReferenceList;
import com.day.cq.search.result.Hit;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public interface ReferenceService {

    public List<Hit> getReferenceList(final String path, final ResourceResolver resourceResolver,
                                      final Map<String, String> queryParam);

    public ReferenceList getReferenceListForPageAndAssets(final String path, final ResourceResolver resourceResolver);

    public List<String> getFilteredReferenceList(final List<Hit> referenceList, Predicate<Hit> predicate);

    public void updatePathReferences(final String currentPath, final String destinationPath);

    public List<String> getDeletePageReference(final String path, final ResourceResolver resourceResolver);
}
