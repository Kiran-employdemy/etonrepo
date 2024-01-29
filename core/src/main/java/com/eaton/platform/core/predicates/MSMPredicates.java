package com.eaton.platform.core.predicates;

import com.day.cq.search.result.Hit;
import com.day.cq.wcm.msm.api.MSMNameConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.util.function.Predicate;

public class MSMPredicates {

    private static final Logger log = LoggerFactory.getLogger(MSMPredicates.class);

    public static final Predicate<Hit> liveSyncPredicate = hit -> {
        boolean isLiveSync = Boolean.FALSE;
        try {
            isLiveSync = !(hit.getPath().contains(MSMNameConstants.NT_LIVE_SYNC_CONFIG));
        } catch (RepositoryException e) {
            log.error(e.getLocalizedMessage());
        }
        return isLiveSync;
    };
}
