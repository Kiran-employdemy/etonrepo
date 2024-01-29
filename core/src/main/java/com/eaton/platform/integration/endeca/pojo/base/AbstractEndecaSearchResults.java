package com.eaton.platform.integration.endeca.pojo.base;

import java.util.Set;

public abstract class AbstractEndecaSearchResults<T> {
    protected Long totalCount;
    public Long getTotalCount() {
        return totalCount;
    }

    protected abstract Set<T> getDocument();
}
