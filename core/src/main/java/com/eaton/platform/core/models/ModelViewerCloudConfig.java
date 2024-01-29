package com.eaton.platform.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ModelViewerCloudConfig {
    private static final Logger LOG = LoggerFactory.getLogger(ModelViewerCloudConfig.class);
    public static final String CONFIG_NAME = "model-viewer-cloud-config";

    @Inject
    private Resource resource;

    @Inject
    private String[] modelViewers;


    public List<ModelViewer> getModelViewers() {
        return modelViewers != null ? Arrays.asList(modelViewers).stream()
                .map(comboAttribute -> new ModelViewer(comboAttribute))
                .collect(Collectors.toList())
                : new ArrayList<>();
    }

    public class ModelViewer {
        private static final String FIELD_TITLE = "title";
        private static final String FIELD_NAME = "name";
        private static final String FIELD_PART_SOLUTIONS_URL = "partSolutionsUrl";
        private static final String FIELD_PART_SOLUTIONS_CATALOG = "partSolutionsCatalog";
        private static final String FIELD_RESOURCE_GROUP = "resourceGroup";
        private static final String FIELD_TITLE_DEFAULT = "Unnamed Model Viewer";

        private String title;
        private String name;
        private String partSolutionsUrl;
        private String partSolutionsCatalog;
        private String resourceGroup;

        public ModelViewer(final String json) {
            try {
                JSONObject attributeJson = new JSONObject(json);
                title = attributeJson.has(FIELD_TITLE)
                        ? attributeJson.getString(FIELD_TITLE) : FIELD_TITLE_DEFAULT;
                name = attributeJson.has(FIELD_NAME)
                        ? attributeJson.getString(FIELD_NAME) : "";
                partSolutionsUrl = attributeJson.has(FIELD_PART_SOLUTIONS_URL)
                        ? attributeJson.getString(FIELD_PART_SOLUTIONS_URL) : "";
                partSolutionsCatalog = attributeJson.has(FIELD_PART_SOLUTIONS_CATALOG)
                        ? attributeJson.getString(FIELD_PART_SOLUTIONS_CATALOG) : "";
                resourceGroup = attributeJson.has(FIELD_RESOURCE_GROUP)
                        ? attributeJson.getString(FIELD_RESOURCE_GROUP) : "";
            } catch (JSONException e) {
                LOG.error("Error parsing model viewer json at: " + resource.getPath(), e);
            }
        }

        public String getTitle() {
            return title;
        }

        public String getName() {
            return name;
        }

        public String getPartSolutionsUrl() {
            return partSolutionsUrl;
        }

        public String getPartSolutionsCatalog() {
            return partSolutionsCatalog;
        }

        public String getResourceGroup() {
            return resourceGroup;
        }
    }
}
