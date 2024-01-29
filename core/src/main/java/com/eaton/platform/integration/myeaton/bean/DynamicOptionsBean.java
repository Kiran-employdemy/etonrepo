/*
 * Eaton
 * Copyright (C) 2020 Eaton. All Rights Reserved
 */

package com.eaton.platform.integration.myeaton.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Bean for dynamic options, to be used in creating json
 * */
public class DynamicOptionsBean implements Serializable {

    private static final long serialVersionUID = -2970344776284660895L;
    private String name;
    private List<String> mapping;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getMapping() {
        return mapping;
    }

    public void setMapping(List<String> mapping) {
        this.mapping = mapping;
    }
}
