package com.eaton.platform.integration.endeca.pojo.base;

import java.util.HashSet;
import java.util.Set;

/**
 * Internal Endeca implementation detail: representation for an Endeca Document
 */
public class BaseEndecaDocument {
    protected Set<EndecaField> content = new HashSet<>();
    protected String getValueFor(EndecaFieldEnum endecaFieldEnum) {
        EndecaField endecaField = getEndecaField(endecaFieldEnum);
        if (endecaField == null) {
            return null;
        }
        return endecaField.getValue();
    }

    public EndecaField getEndecaField(EndecaFieldEnum endecaFieldEnum) {
        return content.stream().filter(endecaField -> endecaField.isOfType(endecaFieldEnum)).findFirst().orElse(null);
    }
}
