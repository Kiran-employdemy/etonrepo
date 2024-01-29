package com.eaton.platform.core.services;

import org.apache.sling.api.resource.ResourceResolver;
import com.google.gson.JsonObject;

import java.io.ByteArrayInputStream;

public interface PimImporterService {

    public JsonObject createPIMData(final ByteArrayInputStream fileInputStream, final String basePath,
                                    final String replicateFlag,
                                    final ResourceResolver resolver);
}
