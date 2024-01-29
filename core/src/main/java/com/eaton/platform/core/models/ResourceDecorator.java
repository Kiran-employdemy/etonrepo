package com.eaton.platform.core.models;

import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.crx.JcrConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.List;
import java.util.function.Predicate;
import org.apache.sling.models.annotations.Model;
import javax.inject.Inject;

@Model(adaptables = { Resource.class })
public class ResourceDecorator {
    @Inject
    Resource resource;

    public Resource getResource() {
        return resource;
    }

    /**
     * @param resource The resource to begin the search from.
     * @param predicate The predicate to use to match against ancestor resources.
     * @return The first ancestor resource of the given resource that matches the given predicate.
     */
    static public Optional<Resource> getAncestor(final Resource resource, final Predicate<Resource> predicate) {
    	return (resource != null ? predicate.test(resource) ? Optional.of(resource) : ResourceDecorator.getAncestor(resource.getParent(), predicate): Optional.empty());
    }

    /**
     * @param predicate The predicate to use to match against ancestor resources.
     * @return The first ancestor resource of the current resource that matches the given predicate.
     */
    public Optional<Resource> getAncestor(Predicate<Resource> predicate) {
        return ResourceDecorator.getAncestor(resource, predicate);
    }

    /**
     * @return The containing cq:Page of the current resource.
     */
    public Optional<Page> getContainingPage() {
        final Optional<Resource> pageResource = this.getAncestor(res -> NameConstants.NT_PAGE.equals(res.getValueMap().get(JcrConstants.JCR_PRIMARYTYPE, StringUtils.EMPTY)));

        return pageResource.isPresent()
                ? Optional.ofNullable(pageResource.get().adaptTo(Page.class))
                : Optional.empty();
    }

    /**
     * @param resourceType The resource type to recursively search for within the current resource.
     * @return All the matching resource that are found.
     */
    public List<Resource> findAllByResourceType(String resourceType) {
        return ResourceDecorator.findAllByResourceType(resource, resourceType);
    }

    /**
     * @param resourceType The resource type to recursively search for within the current resource.
     * @return The first matching resource that is found.
     */
    public Optional<Resource> findByResourceType(String resourceType) {
        return ResourceDecorator.findByResourceType(resource, resourceType);
    }

    /**
     * @param resourceTypes The resource types to recursively search for within the current resource.
     * @return All the matching resource that are found.
     */
    public List<Resource> findAllByResourceTypes(List<String> resourceTypes) {
        return ResourceDecorator.findAllByResourceTypes(resource, resourceTypes);
    }

    /**
     * @param resourceTypes The resource types to recursively search for within the current resource.
     * @return The first matching resource that is found.
     */
    public Optional<Resource> findByResourceTypes(List<String> resourceTypes) {
        return ResourceDecorator.findByResourceTypes(resource, resourceTypes);
    }

    /**
     * Recursively search the content hierarchy starting from the provided resource in search of the first resource that
     * matches the given resource type. The provided resource is not included. The search follows a depth first search approach.
     * Once a matching resource is found the search is stopped.
     * @param resource The resource to search within.
     * @param resourceType The resource type to search for.
     * @return The first matching resource or an empty optional if none exists.
     */
    private static Optional<Resource> findByResourceType(Resource resource, String resourceType) {
        return findByResourceTypes(resource, Arrays.asList(resourceType));
    }

    /**
     * Recursively search the content hierarchy starting from the provided resource in search of the first resource that matches
     * any of the given resource types. The provided resource is not included. The search follows a depth first search approach.
     * Once a matching resource is found the search is stopped.
     * @param resource The resource to search within.
     * @param resourceTypes The resource types to search for.
     * @return The first matching resource or an empty optional if none exists.
     */
    private static Optional<Resource> findByResourceTypes(Resource resource, List<String> resourceTypes) {
        for (Resource child : resource.getChildren()) {
            for (String resourceType : resourceTypes) {
                if (child.isResourceType(resourceType)) {
                    return Optional.of(child);
                } else {
                    Optional<Resource> potentialMatch = findByResourceType(child, resourceType);
                    if (potentialMatch.isPresent()) {
                        return potentialMatch;
                    }
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Recursively search the content hierarchy starting from the provided resource in search of all of the resources that
     * match the given resource type. The provided resource is not included. The search follows a depth first search approach.
     * @param resource The resource to search within.
     * @param resourceType The resource type to search for.
     * @return All of the matching resources or an empty list if none exists.
     */
    private static List<Resource> findAllByResourceType(Resource resource, String resourceType) {
        return findAllByResourceTypes(resource, Arrays.asList(resourceType));
    }

    /**
     * Recursively search the content hierarchy starting from the provided resource in search of all of the resources that match
     * any of the given resource types. The provided resource is not included. The search follows a depth first search approach.
     * @param resource The resource to search within.
     * @param resourceTypes The resource types to search for.
     * @return All of the matching resources or an empty list if none exists.
     */
    private static List<Resource> findAllByResourceTypes(Resource resource, List<String> resourceTypes) {
        final List<Resource> foundResources = new ArrayList<>();

        for (Resource child : resource.getChildren()) {
            for (String resourceType : resourceTypes) {
                if (child.isResourceType(resourceType)) {
                    foundResources.add(child);
                }
            }

            foundResources.addAll(findAllByResourceTypes(child, resourceTypes));
        }

        return foundResources;
    }
}
