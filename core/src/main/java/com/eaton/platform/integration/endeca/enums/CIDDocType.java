package com.eaton.platform.integration.endeca.enums;

import com.eaton.platform.core.bean.AttributeValue;

import java.util.Arrays;

public enum CIDDocType {
    DOC_LINKS("link"), IMAGE("image"), DOCUMENT("document"), OTHER("others"), MULTIPLE("multiple");

    private final String type;

    CIDDocType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setCorrectValueOnAttributeValue(AttributeValue attributeValue, String docCdataValue) {
        if (this == DOC_LINKS) {
            attributeValue.setLinkURL(docCdataValue);
        }
        if (this == IMAGE) {
            attributeValue.setImageURL(docCdataValue);
        }
        if (Arrays.asList(OTHER, DOCUMENT).contains(this)) {
            attributeValue.setDocumentURL(docCdataValue);
        }
    }
}
