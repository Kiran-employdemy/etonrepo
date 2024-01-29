package com.eaton.platform.core.constants;

public final class SiteMapConstants {

    public static final String NS = "http://www.sitemaps.org/schemas/sitemap/0.9";
    public static final String URL = "url";
    public static final String LOC = "loc";
    public static final String XML_EXT = ".xml";
    public static final String MALFORMED_URLEXCEPTION_EXPCEPTION_WHILE_RETREIVING_THE_PAGE_PATH =
            "MalformedURLException expception while retreiving the page path";
    public static final String XML_STREAM_EXCEPTION_WHILE_WRITING_THE_PRODUCT_FAMILY_SITE_MAP =
            "XML Stream  Exception while writing the product family site map";
    public static final String IO_EXCEPTION_EXCEPTION_WHILE_WRITING_THE_PRODUCT_FAMILY_SITE_MAP =
            "IO Exception  Exception while writing the product family site map";
    public static final String URLSET = "urlset";
    public static final String DOT = ".";
    public static final String LASTMOD = "lastmod";
    public static final String CHANGEFREQ = "changefreq";
    public static final String PRIORITY = "priority";
    public static final String SUB_CATEGORY = ".sub_category";
    public static final String SKUMAP = ".skumap";
    public static final String EATON_COMPONENTS_PRODUCT_PRODUCT_GRID = "eaton/components/product/product-grid";
    public static final String EATON_COMPONENTS_DIGITAL_PRODUCT_GRID = "eaton_ecommerce/components/core/digital-catalog-product-grid/v1/digital-catalog-product-grid";
    public static final String EATON_COMPONENTS_DIGITAL_PRODUCT_GRID_V2 = "eaton_ecommerce/components/core/digital-catalog-product-grid/v2/digital-catalog-product-grid";
    public static final String RETURN_FACETS_FOR = "ReturnFacetsFor";
    public static final String FACETS = "Facets";
    public static final String SORT_BY = "SortBy";
    public static final String SEARCH = "search";
    public static final String UNDERSCORE = "_";
    public static final String ASCENDING = "asc";
    public static final String PIM_PAGE_PATH = "pimPagePath";
    public static final String DOC_VERSION = "1.0";
    public static final String SKUCARD_FILTER = "SKUCardParameters";
    public static final String COUNTRY = "Country";
    public static final String PRODUCT_TYPE = "Product_Type";
    public static final String RECORD_START = "0";
    public static final String TOTAL_RECORD = "1000";
    public static final String XML_STREAM_EXCEPTION = "XML Stream  Exception";
    public static final String IO_EXCEPTION = "IO Exception";
    public static final String SUB_CATEGORY_QUERY = "SELECT * FROM [cq:PageContent] WHERE [page-type] = 'sub-category' "
                            + "and [sling:resourceType] = 'eaton/components/structure/eaton-edit-template-page' "
                            + "and [primary-sub-category-tag] IS NOT NULL and ISDESCENDANTNODE('/content/eaton')";
    public static final String CHANGE_FREQUENCY = "changefrequency";
    public static final String DAILY = "daily";
    public static final String PRIORITY_VALUE = "0.5";
    public static final String UTF_8 = "UTF-8";
    public static final String APPLICATION_XML = "application/xml";
    public static final String MASTER = "master";
    public static final String SUBCATEGORY = "sub_category";
    public static final String SKU_MAP = "skumap";
    public static final String HTML_EXT = ".html";
    public static final String QUERY_STRING_SELECT = "SELECT * FROM [cq:PageContent] WHERE [page-type] ='";
    public static final String QUERY_STRING_AND = "'and [sling:resourceType] = '";
    public static final String QUERY_STRING_DESCENDANT_NODE = "'and [primary-sub-category-tag] IS NOT NULL and "
            + "ISDESCENDANTNODE('";
    public static final String QUERY_STRING_COMPLETE = "')";
    public static final String ERROR_WHILE_CREATING_SKU_SITE_MAP = "Error while creating sku site map";
    public static final String PROPERTY_NAME_PDH_RECORD_PATH = "pdhRecordPath";
    public static final String REQUEST_PARAM_DATE = "date";
    public static final String METADATA_CQ_TAGS = "jcr:content/metadata/cq:tags";
    public static final String DF_YEAR_MONTH_DAY_HYPHENATED = "yyyy-MM-dd";
    public static final String DF_YEAR_MONTH_DAY_SLASH = "yyyy/MM/dd";
	public static final String DF_YEAR_MONTH_DAY_TIME_HYPHENATED = "yyyy-MM-dd'T'HH:mm:ss.sss";
}
