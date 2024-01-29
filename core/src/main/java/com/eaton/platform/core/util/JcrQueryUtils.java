package com.eaton.platform.core.util;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import java.util.List;
import java.util.Map;

public class JcrQueryUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(JcrQueryUtils.class);

    public static List<Hit> excuteGenericQuery(Session session, QueryBuilder builder, final Map< String , String > map){
        com.day.cq.search.Query query = builder.createQuery( PredicateGroup.create( map ) , session );
        query.setStart( 0 );
        query.setHitsPerPage( 10000 );
        final SearchResult result = query.getResult( );
        LOGGER.info("\n####### excuteGenericQuery:generated xpath="+result.getQueryStatement()+" no.of hits="+result.getTotalMatches()+" ########\n");
        return result.getHits();
    }

    /**
     *  This method executes JCR query behind the scene by taking parameter map as input query and limit the results to specific number
     *  as it's passed as 'limit'
     * @param session
     * @param builder
     * @param map
     * @param limit
     * @return
     */
    public static List<Hit> excuteGenericQuery(Session session, QueryBuilder builder, final Map< String , String > map, String limit){
        com.day.cq.search.Query query = builder.createQuery( PredicateGroup.create( map ) , session );
        query.setStart( 0 );
        query.setHitsPerPage(Long.parseLong(limit));
        final SearchResult result = query.getResult( );
        LOGGER.info("\n####### excuteGenericQuery:generated xpath="+result.getQueryStatement()+" no.of hits="+result.getTotalMatches()+" ########\n");
        return result.getHits();
    }
}
