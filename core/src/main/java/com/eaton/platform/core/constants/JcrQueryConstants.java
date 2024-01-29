package com.eaton.platform.core.constants;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.DamConstants;

import java.util.HashMap;
import java.util.Map;

public class JcrQueryConstants {
    public static final String PROP_TYPE = "type";
    public static final String ZERO_PREFIX_PROPERTY = "0_property";
    public static final String ZERO_PREFIX_PROPERTY_VALUE = "0_property.value";
    public static final String P_HITS = "p.hits";
    public static final String FULL = "full";
    public static final String P_LIMIT = "p.limit";
    public static final String INFINITY_VALUE = "-1";
    public static final String LIKE = "like";
    public static final String ZERO_PREFIX_PROPERTY_OPERATION = "0_property.operation";
    public static final String UNDERSCORE_PROPERTY = "_property";
    public static final String PROPERTY_VALUE = "_property.value";
    public static final String DATERANGE_PROPERTY = "daterange.property";
    public static final String DATERANGE_LOWER_BOUND = "daterange.lowerBound";
    public static final String DATERANGE_LOWER_OPERATION = "daterange.lowerOperation";
    public static final String GREATER_THAN_OR_EQUAL_TO = ">=";
    public static final String PROP_PATH = "path";
    public static final String FULL_TEXT = "fulltext";
    public static final String FULLTEXT_REL_PATH = "fulltext.relPath";
    public static final Map<String, String> QUERY_PARAMS;
    static {
        QUERY_PARAMS = new HashMap<>();
        QUERY_PARAMS.put(JcrQueryConstants.PROP_TYPE, DamConstants.NT_DAM_ASSET);
        QUERY_PARAMS.put(JcrQueryConstants.PROP_PATH, CommonConstants.CONTENT_DAM_EATON);
        QUERY_PARAMS.put(JcrQueryConstants.P_HITS, JcrQueryConstants.FULL);
        QUERY_PARAMS.put(JcrQueryConstants.P_LIMIT, JcrQueryConstants.INFINITY_VALUE);
        QUERY_PARAMS.put(JcrQueryConstants.ZERO_PREFIX_PROPERTY, SiteMapConstants.METADATA_CQ_TAGS);
        QUERY_PARAMS.put(JcrQueryConstants.ZERO_PREFIX_PROPERTY_OPERATION, JcrQueryConstants.LIKE);
        QUERY_PARAMS.put(JcrQueryConstants.DATERANGE_PROPERTY,
                JcrConstants.JCR_CONTENT.concat(CommonConstants.SLASH_STRING).concat(JcrConstants.JCR_LASTMODIFIED));
        QUERY_PARAMS.put(JcrQueryConstants.DATERANGE_LOWER_OPERATION, JcrQueryConstants.GREATER_THAN_OR_EQUAL_TO);
    }
}
