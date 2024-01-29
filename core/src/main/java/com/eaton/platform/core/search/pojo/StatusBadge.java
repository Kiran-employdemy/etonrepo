package com.eaton.platform.core.search.pojo;

import com.eaton.platform.core.search.api.TranslationMappingContext;

import java.util.Objects;

/**
 * POJO for displaying the badge new or updated
 */
public class StatusBadge {
    public static final String NAME_NEW = "new";
    public static final String STATUS_NEW_KEY = "statusNew";
    public static final String STATUS_UPDATED_KEY = "statusUpdated";
    public static final String NAME_UPDATED = "updated";
    private String name;
    private String label;

    static <T extends TranslationMappingContext> StatusBadge statusNew(T translationMappingContext) {
        StatusBadge statusBadge = new StatusBadge();
        statusBadge.name = NAME_NEW;
        statusBadge.label = translationMappingContext.retrieveFromI18n(STATUS_NEW_KEY);
        return statusBadge;
    }
    static StatusBadge statusUpdated(TranslationMappingContext translationMappingContext) {
        StatusBadge statusBadge = new StatusBadge();
        statusBadge.name = NAME_UPDATED;
        statusBadge.label = translationMappingContext.retrieveFromI18n(STATUS_UPDATED_KEY);
        return statusBadge;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StatusBadge that = (StatusBadge) o;
        return Objects.equals(name, that.name) && Objects.equals(label, that.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, label);
    }
}
