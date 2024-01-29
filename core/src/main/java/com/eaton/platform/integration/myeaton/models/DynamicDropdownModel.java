/*
 * Eaton
 * Copyright (C) 2020 Eaton. All Rights Reserved
 */

package com.eaton.platform.integration.myeaton.models;

import com.adobe.acs.commons.models.injectors.annotation.AemObject;
import com.adobe.aemds.guide.common.GuideDropDownList;
import com.eaton.platform.integration.myeaton.bean.DynamicOptionsBean;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dynamic Dropdown Model
 */
@Model(
    adaptables = SlingHttpServletRequest.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class DynamicDropdownModel extends GuideDropDownList {
    private static final Logger LOG = LoggerFactory.getLogger(DynamicDropdownModel.class);
    private static final long serialVersionUID = -145307204690358806L;

    @AemObject
    private transient Resource resource;

    private transient List<DynamicOptionsBean> dynamicOptions;

    @PostConstruct
    private void init() {
        dynamicOptions = new ArrayList<>();

        Resource dynamicItems = resource.getChild("dynamicItems");

        if (dynamicItems == null) {
            LOG.warn("dynamicItems was not set on: {}", resource.getPath());
            return;
        }

        for (Resource dynamicItem : dynamicItems.getChildren()) {
            ValueMap properties = dynamicItem.getValueMap();

            String dynamicOptionKey = properties.get("name", "");

            DynamicOptionsBean dynamicOption = new DynamicOptionsBean();

            dynamicOption.setName(dynamicOptionKey);

            List<String> mappingsList = new ArrayList<>();

            Resource mappingRes = dynamicItem.getChild("mapping");

            if (mappingRes == null) {
                LOG.warn("dynamicItems was not set on: {}", dynamicItem.getPath());
                continue;
            }

            for (Resource mapping : mappingRes.getChildren()) {
                ValueMap mappingProps = mapping.getValueMap();
                String mappingValue = mappingProps.get("name", "");
                mappingsList.add(mappingValue);
            }
            dynamicOption.setMapping(mappingsList);
            dynamicOptions.add(dynamicOption);
        }
    }

    public String getDynamicOptions() {
        Gson gson = new Gson();
        return gson.toJson(dynamicOptions);
    }
}
