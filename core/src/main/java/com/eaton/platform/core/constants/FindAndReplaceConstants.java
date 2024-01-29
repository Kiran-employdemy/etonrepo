package com.eaton.platform.core.constants;

/**
 * FindAndReplaceConstants.
 *
 * @author Gurneet Pal Singh
 */
public final class FindAndReplaceConstants {

	/** The Constant QUERY. */
	public static final String QUERY = "SELECT * FROM [${nodeType}] AS node WHERE (ISDESCENDANTNODE(node, '${pathToSearch}') OR node.[jcr:path] = '${pathToSearch}') AND (${propertiesQuery})";

	/** The Constant JOIN_QUERY. */
	public static final String JOIN_QUERY = "SELECT child.* FROM [nt:unstructured] AS node INNER JOIN [nt:unstructured] AS child ON ISDESCENDANTNODE(child, node)"
			+ " WHERE ISDESCENDANTNODE(node, '${pathToSearch}') AND (${propertiesQuery})";

	// Text - page query start

	/** The Constant PAGE_QUERY_WHOLE_WORD. */
	public static final String PAGE_QUERY_WHOLE_WORD = "CONTAINS(node.[jcr:content/${propName}], '${searchString}')";

	/** The Constant PAGE_QUERY_CASE_SENSITIVE_NO_WHOLE_WORD. */
	public static final String PAGE_QUERY_CASE_SENSITIVE_NO_WHOLE_WORD = "node.[jcr:content/${propName}] LIKE '%${searchString}%'";

	/** The Constant PAGE_QUERY_CASE_INSENSITIVE_NO_WHOLE_WORD. */
	public static final String PAGE_QUERY_CASE_INSENSITIVE_NO_WHOLE_WORD = "lower(node.[jcr:content/${propName}]) LIKE '%${searchString}%'";

	// Text - page query end

	// Text - component query start

	/** The Constant COMPONENT_QUERY_WHOLE_WORD. */
	public static final String COMPONENT_QUERY_WHOLE_WORD = "CONTAINS(node.[${propName}], '${searchString}')";

	/** The Constant COMPONENT_QUERY_CASE_SENSITIVE_NO_WHOLE_WORD. */
	public static final String COMPONENT_QUERY_CASE_SENSITIVE_NO_WHOLE_WORD = "node.[${propName}] LIKE '%${searchString}%'";

	/** The Constant COMPONENT_QUERY_CASE_INSENSITIVE_NO_WHOLE_WORD. */
	public static final String COMPONENT_QUERY_CASE_INSENSITIVE_NO_WHOLE_WORD = "lower(node.[${propName}]) LIKE '%${searchString}%'";

	// Text - component query end

	/** The Constant ASSET_QUERY_TAG. */
	public static final String ASSET_QUERY_TAG = "node.[jcr:content/metadata/${propName}] = '${searchString}'";

	/** The Constant PAGE_QUERY_TAG. */
	public static final String PAGE_QUERY_TAG = "node.[jcr:content/${propName}] = '${searchString}'";

	/** The Constant COMPONENT_QUERY_TAG. */
	public static final String COMPONENT_QUERY_TAG = "node.[${propName}] = '${searchString}'";

	/** The Constant DEFAULT_QUERY_TAG. */
	public static final String DEFAULT_QUERY_TAG = "${alias}.[${propName}] = '${searchString}'";

	/** The Constant AND_CONDITION. */
	public static final String AND_CONDITION = " AND ";

	/** The Constant OR_CONDITION. */
	public static final String OR_CONDITION = " OR ";

	/** The Constant SEARCH_STRING. */
	public static final String SEARCH_STRING = "searchString";

	/** The Constant PROP_NAME_PARAM. */
	public static final String PROP_NAME_PARAM = "propName";

	/** The Constant ALIAS_PARAM. */
	public static final String ALIAS_PARAM = "alias";

	/** The Constant NOT_JCR_CONTENT_CONDITION. */
	public static final String NOT_JCR_CONTENT_CONDITION = " AND (NAME(node) <> 'jcr:content')";

	/** The Constant RESOURCE_TYPE_CONDITION. */
	public static final String RESOURCE_TYPE_CONDITION = "${alias}.[sling:resourceType] IN (${resourceTypes})";

	/** The Constant TRAVERSAL_OPTION. */
	public static final String TRAVERSAL_OPTION = " OPTION(TRAVERSAL OK)";

	/** The Constant DEFAULT_ALIAS. */
	public static final String DEFAULT_ALIAS = "node";

	/** The Constant CHILD_ALIAS. */
	public static final String CHILD_ALIAS = "child";

	/** The Constant TEXT_PACKAGE_NAME. */
	public static final String TEXT_PACKAGE_NAME = "text";

	/** The Constant TAGS_PACKAGE_NAME. */
	public static final String TAGS_PACKAGE_NAME = "tags";

	// Validation

	/** The Constant VALIDATION_NOT_EMPTY. */
	public static final String VALIDATION_NOT_EMPTY = "should not be empty";

	/** The Constant VALIDATION_PATH_STARTS_WITH_CONTENT. */
	public static final String VALIDATION_PATH_STARTS_WITH_CONTENT = "path should start with \"" + CommonConstants.STARTS_WITH_CONTENT_WITH_SLASH + "\"";

	/** The Constant VALIDATION_TAGS_NOT_CORRECT. */
	public static final String VALIDATION_TAGS_NOT_CORRECT = "tags are empty or values are not correct";

	private FindAndReplaceConstants() {
	}

}
