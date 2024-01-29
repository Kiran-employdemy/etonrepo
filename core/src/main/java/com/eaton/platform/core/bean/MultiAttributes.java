package com.eaton.platform.core.bean;

import java.util.List;

public class MultiAttributes extends Attribute {

    private final List<Attribute> attributes;


    public MultiAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

}
