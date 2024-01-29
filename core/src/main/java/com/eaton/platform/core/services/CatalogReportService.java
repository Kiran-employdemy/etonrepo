package com.eaton.platform.core.services;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import java.io.ByteArrayOutputStream;
import java.util.LinkedHashMap;
import java.util.List;

public interface CatalogReportService {

    ByteArrayOutputStream generateCatalogReport(final Resource basePathresource, final LinkedHashMap<String, ValueMap> translatedPathMap, final List<String> selectedPropertyList);
}
