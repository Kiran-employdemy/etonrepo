package com.eaton.platform.integration.endeca.services.impl;

import com.eaton.platform.core.search.api.LoadMoreOffsetProvider;
import com.eaton.platform.integration.endeca.constants.EndecaConstants;
import com.google.common.base.Strings;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.Arrays;
import java.util.Optional;

public class EndecaAdvancedSearchLoadMoreOffsetProvider implements LoadMoreOffsetProvider {
    private final SlingHttpServletRequest slingHttpServletRequest;

    public EndecaAdvancedSearchLoadMoreOffsetProvider(SlingHttpServletRequest slingHttpServletRequest) {
        this.slingHttpServletRequest = slingHttpServletRequest;
    }

    @Override
    public Long getLoadMoreOffset() {
        Optional<String> loadMoreOffsetSelectorOptional = Arrays.stream(slingHttpServletRequest.getRequestPathInfo().getSelectors())
                .filter(selector -> selector.contains(EndecaConstants.LOAD_MORE_OFFSET)).findFirst();
        if (loadMoreOffsetSelectorOptional.isEmpty()) {
            return 0L;
        }
        String loadMoreOffsetSelector = loadMoreOffsetSelectorOptional.get();
        String afterDollar = loadMoreOffsetSelector.substring(loadMoreOffsetSelector.indexOf(EndecaConstants.DOLLAR_SYMBOL) + 1);
        if (Strings.isNullOrEmpty(afterDollar)){
            return 0L;
        }
        return Long.parseLong(afterDollar);
    }
}
