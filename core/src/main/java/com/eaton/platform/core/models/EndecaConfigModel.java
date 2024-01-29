package com.eaton.platform.core.models;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.sling.models.annotations.Model;
import javax.inject.Inject;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class EndecaConfigModel {
    private static final Logger LOG = LoggerFactory.getLogger(EndecaConfigModel.class);
    public static final String CONFIG_NAME = "endeca-configurations";

    @Inject
    private Resource resource;

    @Inject
    private String[] sortableAttributes;

    @Inject
    private String[] attributeMappings;

    @Inject
    private String[] additionalTaxonomyAttributes;

    public List<String> getSortableAttributes() {
        return sortableAttributes != null ? Arrays.asList(sortableAttributes) : new ArrayList<>();
    }

    public List<AttributeMapping> getAttributeMappings() {
         return attributeMappings != null ? Arrays.asList(attributeMappings).stream()
                 .map(attributeMapping -> new AttributeMapping(attributeMapping))
                 .collect(Collectors.toList())
                 : new ArrayList<>();
    }

    public List<AdditionalTaxonomyAttribute> getAdditionalTaxonomyAttributes() {
        return additionalTaxonomyAttributes != null ? Arrays.asList(additionalTaxonomyAttributes).stream()
                .map(additionalTaxonomyAttribute -> new AdditionalTaxonomyAttribute(additionalTaxonomyAttribute))
                .collect(Collectors.toList())
                : new ArrayList<>();
    }

    /**
     * @param endecaResponseAttrName The attribute name as provided in the Endeca response.
     * @return The AEM name for the same attribute. These mappings are configured in the attribute mapping list. If none is
     * found then an empty optional is returned.
     */
    public Optional<String> getAemAttributeName(String endecaResponseAttrName) {
        Optional<AttributeMapping> matchedAttrMapping = getAttributeMappings().stream()
                .filter(attributeMapping -> StringUtils.equals(attributeMapping.getEndecaResponseAttributeName(), endecaResponseAttrName))
                .findFirst();

        if (matchedAttrMapping.isPresent()) {
            return Optional.ofNullable(matchedAttrMapping.get().getAemAttributeName());
        } else {
            return Optional.empty();
        }
    }

    public Optional<String> getAdditionalTaxonomyAttributeName(String taxonomyAttrName) {
        Optional<AdditionalTaxonomyAttribute> matchedAttrMapping = getAdditionalTaxonomyAttributes().stream()
                .filter(taxonomyAttributeMapping -> StringUtils.equals(taxonomyAttributeMapping.getTaxonomyAttributeName(), taxonomyAttrName))
                .findFirst();

        if (matchedAttrMapping.isPresent()) {
            return Optional.ofNullable(matchedAttrMapping.get().getTaxonomyAttributeI18nKey());
        } else {
            return Optional.empty();
        }
    }

    public class AttributeMapping {
        private String aemAttributeName;
        private String endecaRequestAttributeName;
        private String endecaResponseAttributeName;

        public AttributeMapping(String json) {
            try {
                JsonObject mappingJson = new JsonParser().parse(json).getAsJsonObject();
                aemAttributeName = mappingJson.get("aemAttributeName").getAsString();
                endecaRequestAttributeName = mappingJson.get("endecaRequestAttributeName").getAsString();
                endecaResponseAttributeName = mappingJson.get("endecaResponseAttributeName").getAsString();
            } catch (Exception e) {
                LOG.error("Error parsing attribute mapping json at: " + resource.getPath(), e);
            }
        }

        public String getAemAttributeName() {
            return aemAttributeName;
        }

        public String getEndecaRequestAttributeName() {
            return endecaRequestAttributeName;
        }

        public String getEndecaResponseAttributeName() {
            return endecaResponseAttributeName;
        }
    }

    public class AdditionalTaxonomyAttribute {
        private String taxonomyAttributeName;
        private String taxonomyAttributeI18nKey;

        public AdditionalTaxonomyAttribute(String json) {
            try {
            	JsonObject mappingJson = new JsonParser().parse(json).getAsJsonObject();
                taxonomyAttributeName = mappingJson.get("taxonomyAttributeName").getAsString();
                taxonomyAttributeI18nKey = mappingJson.get("taxonomyAttributeI18nKey").getAsString();
            } catch (Exception e) {
                LOG.error("Error parsing attribute mapping json at: " + resource.getPath(), e);
            }
        }
        public String getTaxonomyAttributeName() {
            return taxonomyAttributeName;
        }

        public String getTaxonomyAttributeI18nKey() {
            return taxonomyAttributeI18nKey;
        }
    }
}