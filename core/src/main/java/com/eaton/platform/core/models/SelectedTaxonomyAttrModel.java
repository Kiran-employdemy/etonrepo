package com.eaton.platform.core.models;

import com.eaton.platform.core.services.CloudConfigService;
import java.util.Arrays;
import org.apache.commons.collections.CollectionUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.commons.lang.StringUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Optional;
import java.util.Objects;
import com.eaton.platform.core.models.EndecaConfigModel.AttributeMapping;
import javax.inject.Inject;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

/**
 * A model which represents a selected taxonomy attribute. This should be a resource that either contains a property
 * called "taxonomyAttributeValue" or a property called "name". If either of these properties exist it will be used to
 * create a list of attributes that can be used in Endeca requests. These attributes can be retrieved through the
 * getAttributeNames() method. The "taxonomyAttributeValue" property will be used in preference to the "name" property.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SelectedTaxonomyAttrModel {
    @OSGiService
    private CloudConfigService cloudConfigService;

    @Inject
    private Resource resource;

    @Inject
    private String taxonomyAttributeValue;

    @Inject
    private String name;

    /**
     * @return A list of attribute names for use with Endeca that corresponds to the selected attribute. This could simply
     * be the 'taxonomyAttributeValue' or it could be a list of values based upon the provided Endeca Cloud Config Attribute Mappings.
     */
    public Set<String> getAttributeNames(Optional<EndecaConfigModel> endecaConfigModel) {
        final String attributeName = taxonomyAttributeValue != null ? taxonomyAttributeValue : name;
        final Set<String> comboAttributes = getComboAttributes(endecaConfigModel, attributeName);
        final Set<String> taxonomyAttributes = getTaxonomyAttributes(attributeName);

        return CollectionUtils.isNotEmpty(comboAttributes) ? comboAttributes : taxonomyAttributes;
    }

    /**
     * @return A list of attribute names for use with Endeca that corresponds to the selected attribute. This could simply
     * be the 'taxonomyAttributeValue' or it could be a list of values based upon the Endeca Cloud Config Attribute Mappings
     * that are configured for the current path.
     */
    public Set<String> getAttributeNames() {
        final Optional<EndecaConfigModel> endecaConfigModel = cloudConfigService != null
                ? cloudConfigService.getEndecaCloudConfig(resource) : Optional.empty();

        return this.getAttributeNames(endecaConfigModel);
    }

    /**
     * @param endecaConfigModel Any combo attributes configured on the Endeca config will be used to generate the combo attribute list.
     * @param taxonomyAttributeValue The name of the taxonomy attribute to compare to the "AEM Attribute Name" configured on the Endeca config.
     * @return The list of Endeca Request Attributes configured in the Endeca config that match the provided taxonomy attribute.
     */
    private static Set<String> getComboAttributes(Optional<EndecaConfigModel> endecaConfigModel, String taxonomyAttributeValue) {
        return (endecaConfigModel.isPresent() ? endecaConfigModel.get().getAttributeMappings() : new ArrayList<AttributeMapping>()).stream()
                .filter(mapping -> mapping.getAemAttributeName().equals(taxonomyAttributeValue))
                .map(mapping -> mapping.getEndecaRequestAttributeName())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /**
     * @param taxonomyAttribute The taxonomy attribute to create a list for.
     * @return A list containing the taxonomy attribute if the taxonomy attribute is not empty otherwise an empty list is returned.
     */
    private static Set<String> getTaxonomyAttributes(String taxonomyAttribute) {
        return StringUtils.isNotEmpty(taxonomyAttribute) ? new HashSet(Arrays.asList(taxonomyAttribute)) : new HashSet<>();
    }
}
