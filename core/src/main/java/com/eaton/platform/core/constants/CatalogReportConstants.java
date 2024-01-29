package com.eaton.platform.core.constants;

import java.util.ArrayList;

public class CatalogReportConstants {
    public static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String CQ_STATUS = "cqStatus";
    public static final String IMPORTED = "IMPORTED";
    public static final String CQ_SOURCE_NODE_PATH = "cqSourceNodePath";
    public static final String CQ_TARGET_NODE_PATH = "cqTargetNodePath";
    public static final String CQ_INITIATOR = "cqInitiator";
    public static final String TRANSLATED_BY = "Translated By";
    public static final String TRANSLATION_SOURCE_PATH = "Translation Source Path";
    public static final String TRANSLATION_TARGET_PATH = "Translation Target Path";
    public static final String ROOT = "root";
    public static final String DATE = "Date: ";
    public static final String GLOBAL_LINK_SUBMISSION_PATH = "/libs/globallink-adaptor/submissions/";
    public static final String CATEGORY = "Category";
    public static final String SUB_CATEGORY = "Sub Category";
    public static final String TRANSLATION_STATUS = "Translation Status";
    public static final String XLS_CONTENT_TYPE = "application/xls";
    public static final String PATH = "path";
    public static final String PROPERTIES = "properties";
    public static final ArrayList<String> HEADER_NAMES;
    static {
        HEADER_NAMES = new ArrayList<>();
        HEADER_NAMES.add(CATEGORY);
        HEADER_NAMES.add(SUB_CATEGORY);
        HEADER_NAMES.add(TRANSLATION_STATUS);
    }

}
