package com.eaton.platform.core.util;

import com.eaton.platform.core.bean.AttrNmBean;
import com.eaton.platform.core.bean.ImageGroupBean;
import com.eaton.platform.core.bean.attributetable.AttributeTable;
import com.eaton.platform.core.bean.attributetable.AttributeTableRow;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.ATRGRPTYPE;
import com.eaton.platform.core.models.ATRGRPTYPE.ATRGRP;
import com.eaton.platform.core.models.ATRGRPTYPE.ATRGRP.ATRMULTIROW;
import com.eaton.platform.core.models.ATRGRPTYPE.ATRGRP.ATRNM;
import com.eaton.platform.core.services.AdminService;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.*;

/**
 * Utility class used to parse sku data (attributes, images, documents)
 */
public class SkuImageParser {

	private static final Logger LOG = LoggerFactory.getLogger(SkuImageParser.class);

	private static final String ATTR_RUNTIME_GRAPH = "Runtime_Graph";
	private static final String DEBUG_MSG_ATTRIBUTE_GROUP_LIST_EMPTY = "attribute group list is empty";
	private static final String BULLETED_LIST_PREFIX = "<ul><li>";
	private static final String BULLETED_LIST_SUFFIX = "</li></ul>";
	private static final String BULLETED_LIST_PIPE_REPLACEMENT = "</li><li>";

	/**
	 * Get map of pdh image data found in passed xmlString
	 * @param xmlString xml string containing pdh image data
	 * @param adminService admin service for getting the xsd file
	 * @return map of pdh image data
	 */
	public Map<String,ImageGroupBean> getPDHImageList(String xmlString, AdminService adminService) {

		LOG.debug("getPDHImageList : START");

		Map<String,ImageGroupBean> pdhImageGroupList = new HashMap<>();

		List<ATRGRP> attributeGroupList = getAttributeGroupList(xmlString, adminService);

		if (!attributeGroupList.isEmpty()) {
			for (ATRGRP item : attributeGroupList) {
				if (item != null && item.getATRNM() != null && !item.getATRNM().isEmpty()) {
					ImageGroupBean imageGroupBean = getImageGroupBean(item);
					pdhImageGroupList.put(item.getId(), imageGroupBean);
				}
			}
		} else {
			LOG.debug(DEBUG_MSG_ATTRIBUTE_GROUP_LIST_EMPTY);
		}

		LOG.debug("getPDHImageList : END");
		return pdhImageGroupList;
	}

	/**
	 * Get map of document data found in passed xmlString
	 * @param xmlString xml string containing document data
	 * @param adminService admin service for getting the xsd file
	 * @return map of document data
	 */
	public Map<String, List<AttrNmBean>> getDocumentMap(String xmlString, AdminService adminService) {

		LOG.debug("getDocumentMap : START");

		Map<String, List<AttrNmBean>> documentMap = new HashMap<>();

		List<ATRGRP> listOfAttributeGroups = getAttributeGroupList(xmlString, adminService);

		if (!listOfAttributeGroups.isEmpty()) {

			for (ATRGRP attributeGroup : listOfAttributeGroups) {

				if (StringUtils.isNotBlank(attributeGroup.getId())
						&& attributeGroup.getATRNM() != null
						&& !attributeGroup.getATRNM().isEmpty() ) {

					documentMap = addDocumentsToDocumentMap(documentMap, attributeGroup);

				}

			}

		} else {
			LOG.debug(DEBUG_MSG_ATTRIBUTE_GROUP_LIST_EMPTY);
		}

		LOG.debug("getDocumentMap : END");
		return documentMap;
	}

	/**
	 * Add documents from the attribute group to the document map
	 * @param documentMap map of documents from attribute group data
	 * @param attributeGroup attribute group with document data
	 * @return updated document map
	 */
	private static Map<String, List<AttrNmBean>> addDocumentsToDocumentMap(Map<String, List<AttrNmBean>> documentMap, ATRGRP attributeGroup) {
		LOG.debug("addDocumentsToDocumentMap : START");

		List<AttrNmBean> listOfAttributeNameBeans = new ArrayList<>();
		for (ATRNM attributeNameData : attributeGroup.getATRNM()) {
			AttrNmBean attributeNameBean = getAttributeNameBean(attributeNameData);
			listOfAttributeNameBeans.add(attributeNameBean);
		}

		if (documentMap.containsKey(attributeGroup.getId())) {
			List<AttrNmBean> listOfExistingAttributeNameBeans = documentMap.get(attributeGroup.getId());
			Collections.sort(listOfExistingAttributeNameBeans);

			if (listOfExistingAttributeNameBeans.addAll(listOfAttributeNameBeans)) {
				documentMap.put(attributeGroup.getId(), listOfExistingAttributeNameBeans);
			}
		} else {
			documentMap.put(attributeGroup.getId(), listOfAttributeNameBeans);
		}

		LOG.debug("addDocumentsToDocumentMap : END");
		return documentMap;
	}

	/**
	 * Get a map of attribute group data found in passed xmlString
	 * @param xmlString xml string containing attribute groups
	 * @param adminService admin service for getting the xsd file
	 * @param includeMultiRowAttributesWithMultipleValues when true, multi-row attributes with multiple vales in each row are included in the returned attribute map
	 * @return map of attribute group data
	 */
	public Map<String,AttrNmBean> getAttributeMapWithMultiRowMultiValue(String xmlString, AdminService adminService, boolean includeMultiRowAttributesWithMultipleValues) {

		LOG.debug("getAttributeMapWithMultiRowMultiValue : START");

		Map<String,AttrNmBean> attributeMap = new HashMap<>();

		List<ATRGRP> attributeGroupList = getAttributeGroupList(xmlString, adminService);

		if (!attributeGroupList.isEmpty()) {
			for (ATRGRP item : attributeGroupList) {
				if (item != null) {
					if (item.getATRNM() != null && !item.getATRNM().isEmpty()) {
						attributeMap = addSingleRowAttributeNameToAttributeMap(attributeMap, item);
                    }

					if (item.getATRMULTIROW() != null && !item.getATRMULTIROW().isEmpty()) {
						attributeMap = addMultiRowAttributeNameToAttributeMap(attributeMap, item, includeMultiRowAttributesWithMultipleValues);
					}
				}
			}
		} else {
			LOG.debug(DEBUG_MSG_ATTRIBUTE_GROUP_LIST_EMPTY);
		}

		LOG.debug("getAttributeMapWithMultiRowMultiValue : END");
		return attributeMap;
	}

	/**
	 * Add a single-row attribute to the attribute map
	 * @param attributeMap map of attribute group data
	 * @param attributeGroup a single-row attribute name
	 * @return map of attribute group data with single-row attribute added to the map
	 */
	private static Map<String,AttrNmBean> addSingleRowAttributeNameToAttributeMap(Map<String,AttrNmBean> attributeMap, ATRGRP attributeGroup) {
		LOG.debug("addSingleRowAttributeNameToAttributeMap : START");

		List<ATRNM> listOfAttributeNames = attributeGroup.getATRNM();

		for (ATRNM attributeNameData : listOfAttributeNames) {

			AttrNmBean attributeBean = getAttributeNameBean(attributeNameData);

			if (attributeBean != null
					&& StringUtils.isNotBlank(attributeBean.getCdata())
					&& StringUtils.isNotBlank(attributeNameData.getId())
					&& !attributeMap.containsKey(attributeNameData.getId()) ) {

				if (attributeBean.getCdata().contains(CommonConstants.PIPE)) {

					LOG.debug("Attribute's cdata contains at least 1 pipe value ({}). Replacing pipe values with bulleted list.", attributeBean.getCdata());
					String cdataFormattedAsBulletedList = StringUtils.join(BULLETED_LIST_PREFIX, attributeBean.getCdata().replace(CommonConstants.PIPE, BULLETED_LIST_PIPE_REPLACEMENT), BULLETED_LIST_SUFFIX);
					LOG.debug("Replaced pipe values with bulleted list. End result: {}", cdataFormattedAsBulletedList);
					attributeBean.setCdata(cdataFormattedAsBulletedList);
					LOG.debug("Added cdata with bullet list to attribute bean.");

				}

				attributeMap.put(attributeNameData.getId(), attributeBean);
            }

        }

		LOG.debug("addSingleRowAttributeNameToAttributeMap : END");
		return attributeMap;
	}

	/**
	 * Add a multi-row attribute in the attribute map
	 * @param attributeMap map of attribute group data
	 * @param item a multi-row attribute
	 * @param includeMultiRowAttributesWithMultipleValues whether to include multirow attributes with more than 1 value
	 * @return map of attribute group data with multi-row attribute added to the map
	 */
	private static Map<String, AttrNmBean> addMultiRowAttributeNameToAttributeMap(Map<String,AttrNmBean> attributeMap, ATRGRP item, boolean includeMultiRowAttributesWithMultipleValues) {
		LOG.debug("addMultiRowAttributeNameToAttributeMap : START");

		List<ATRMULTIROW> listOfMultiRowAttributes = item.getATRMULTIROW();

		for (ATRMULTIROW multiRowAttribute : listOfMultiRowAttributes) {

			List<ATRMULTIROW.ATRNM> attributeNamesInMultiRowAttribute = multiRowAttribute.getATRNM();

			if ( attributeNamesInMultiRowAttribute != null
					&& ( includeMultiRowAttributesWithMultipleValues || attributeNamesInMultiRowAttribute.size() <= 1 ) ) {

				for (ATRMULTIROW.ATRNM multiRowAttributeNameData : attributeNamesInMultiRowAttribute) {
					AttrNmBean multiRowAttributeNameBean = getMultiRowAttributeNameBean(multiRowAttributeNameData);
					if (multiRowAttributeNameBean != null
							&& StringUtils.isNotBlank(multiRowAttributeNameBean.getCdata())
							&& StringUtils.isNotBlank(multiRowAttributeNameBean.getId())
					) {
						attributeMap = updateExistingMultiRowAttributeNameInAttributeMap(attributeMap, multiRowAttributeNameBean);
					}
				}

			}

        }

		LOG.debug("addMultiRowAttributeNameToAttributeMap : END");
		return attributeMap;
	}

	/**
	 * Update the existing multi-row attribute in the attribute map
	 * @param attributeMap attribute map containing all attributes
	 * @param multiRowAttributeNameBean multi-row attribute to add to attribute map
	 * @return updated attribute map
	 */
	private static Map<String, AttrNmBean> updateExistingMultiRowAttributeNameInAttributeMap(Map<String,AttrNmBean> attributeMap, AttrNmBean multiRowAttributeNameBean) {
		LOG.debug("updateExistingMultiRowAttributeNameInAttributeMap : START");

		if (!attributeMap.containsKey(multiRowAttributeNameBean.getId())) {

			attributeMap.put(multiRowAttributeNameBean.getId(), multiRowAttributeNameBean);

		} else {

			AttrNmBean existingMultiRowAttributeNameEntry = attributeMap.get(multiRowAttributeNameBean.getId());
			String existingMultiRowAttributeNameValue = existingMultiRowAttributeNameEntry.getCdata();
			LOG.debug("Multi-row attribute name entry value: {}", existingMultiRowAttributeNameValue);

			if (StringUtils.isNotBlank(existingMultiRowAttributeNameValue)) {
				existingMultiRowAttributeNameValue = existingMultiRowAttributeNameValue + " <br/> " + multiRowAttributeNameBean.getCdata();
			}

			existingMultiRowAttributeNameEntry.setCdata(existingMultiRowAttributeNameValue);
			attributeMap.replace(multiRowAttributeNameBean.getId(), existingMultiRowAttributeNameEntry);

		}

		LOG.debug("updateExistingMultiRowAttributeNameInAttributeMap : END");
		return attributeMap;
	}

	/**
	 * Get all AttributeTable objects for multi-row, multi-value attribute groups
	 * @param xmlString xml string containing attribute groups
	 * @param adminService admin service for getting the xsd file
	 * @return list of AttributeTable objects
	 */
	public List<AttributeTable> getAttributeTablesForMultiRowMultiValueAttributes(String xmlString, AdminService adminService) {

		LOG.debug("getAttributeTablesForMultiRowMultiValueAttributes : START");
		LOG.debug("xmlString: {}", xmlString);

		List<AttributeTable> attributeTableList = new ArrayList<>();

		List<ATRGRP> attributeGroupList = getAttributeGroupList(xmlString, adminService);

		if (!attributeGroupList.isEmpty()) {
			for (ATRGRP attributeGroup : attributeGroupList) {
				List<ATRMULTIROW> multiRowAttributes = attributeGroup.getATRMULTIROW();
				if (multiRowAttributes != null && !multiRowAttributes.isEmpty()) {

					AttributeTable attributeTable = new AttributeTable();

					attributeTable.setId(attributeGroup.getId());
					attributeTable.setHeadline(attributeGroup.getLabel());

					attributeTable = addAllRowsToMultiRowMultiValueTable(attributeTable, multiRowAttributes);
					if (attributeTable.getRows() != null && !attributeTable.getRows().isEmpty()) {
						attributeTableList.add(attributeTable);
					}
				}
			}
		} else {
			LOG.debug(DEBUG_MSG_ATTRIBUTE_GROUP_LIST_EMPTY);
		}

		LOG.debug("getAttributeTablesForMultiRowMultiValueAttributes : END");
		return attributeTableList;

	}

	/**
	 * Add all rows to multi-row, multi-value attribute table
	 * @param attributeTable table for rows to be added to
	 * @param multiRowAttributes multi-row attributes to add to table
	 * @return updated attribute table with multi-row attribute added
	 */
	private static AttributeTable addAllRowsToMultiRowMultiValueTable(AttributeTable attributeTable, List<ATRMULTIROW> multiRowAttributes) {
		LOG.debug("addAllRowsToMultiRowMultiValueTable : START");

		List<AttributeTableRow> rows = new ArrayList<>();

		for (ATRMULTIROW multiRowAttribute : multiRowAttributes) {

			List<ATRMULTIROW.ATRNM> listOfMultiRowAttributeNames = multiRowAttribute.getATRNM();
			if (listOfMultiRowAttributeNames.size() > 1) {

				AttributeTableRow attributeTableRow = getSingleRowForMultiRowMultiValueTable(multiRowAttribute, listOfMultiRowAttributeNames);
				rows.add(attributeTableRow);

			}

		}

		if (!rows.isEmpty()) {
			Set<String> headers = getAttributeTableHeaders(rows);
			attributeTable.setHeaders(headers);
			attributeTable.setRows(removeAttributeTableRowCellsUnderIncompleteColumns(headers, rows));
		}

		LOG.debug("addAllRowsToMultiRowMultiValueTable : END");
		return attributeTable;
	}

	/**
	 * Get a single row data for a table that has a multi-row attribute group with multiple values in that row
	 * @param atrMultiRow parsed multi-row attribute data
	 * @param atrNmList list of attribute values in the multi-row attribute
	 * @return AttributeTableRow object
	 */
	private static AttributeTableRow getSingleRowForMultiRowMultiValueTable(ATRMULTIROW atrMultiRow, List<ATRMULTIROW.ATRNM> atrNmList) {
		LOG.debug("getSingleRowForMultiRowMultiValueTable : START");

		AttributeTableRow attributeTableRow = new AttributeTableRow();
		if (atrMultiRow.getROWID() != null) {
			LOG.debug("row id set: {}", atrMultiRow.getROWID());
			attributeTableRow.setId(atrMultiRow.getROWID());
		}

		LinkedHashMap<String, String> cells = new LinkedHashMap<>();

		for (ATRMULTIROW.ATRNM attributeRowData : atrNmList) {
			if (StringUtils.isNotBlank(attributeRowData.getLabel()) && StringUtils.isNotBlank(attributeRowData.getValue())) {
				cells.put(attributeRowData.getLabel(), attributeRowData.getValue());
			}
		}

		if (!cells.isEmpty()) {
			attributeTableRow.setCells(cells);
		}

		LOG.debug("getSingleRowForMultiRowMultiValueTable : END");
		return attributeTableRow;
	}

	/**
	 * Get attribute table header values that exist in all Attribute Table Rows
	 * @param rows row data for attribute table
	 * @return a set of common headers for attribute table's headers
	 */
	private static Set<String> getAttributeTableHeaders(List<AttributeTableRow> rows) {
		LOG.debug("getAttributeTableHeaders : Start");

		Set<String> commonHeaders = new LinkedHashSet<>();

		commonHeaders.addAll(rows.get(0).getCells().keySet());
		LOG.debug("First row's header values: {}", commonHeaders);

		for (int i = 1; i < rows.size(); i++) {
			Set<String> currentHeaders = rows.get(i).getCells().keySet();
			LOG.debug("Row {}'s header values: {}", i, currentHeaders);
			commonHeaders.retainAll(currentHeaders);
			LOG.debug("Current common header values: {}", commonHeaders);
		}

		LOG.debug("Final common header values: {}", commonHeaders);
		LOG.debug("getAttributeTableHeaders : End");
		return commonHeaders;
	}

	/**
	 * Remove attribute table row cells that are a part of an incomplete column (1 or more of cells in the column don't contain data)
	 * @param commonHeaders a set of table headers that exist in every row
	 * @return row data with only cells where 1 or more of the other cells in the same column don't contain a value
	 */
	private static List<AttributeTableRow> removeAttributeTableRowCellsUnderIncompleteColumns(Set<String> commonHeaders, List<AttributeTableRow> rows) {
		LOG.debug("removeAttributeTableRowCellsUnderIncompleteColumns : Start");

		for (int rowCnt = 0; rowCnt < rows.size(); rowCnt++) {

			AttributeTableRow currentRow = rows.get(rowCnt);

			Set<String> currentRowHeaders = currentRow.getCells().keySet();
			Set<String> currentDifferentKeys = SetUtils.difference(currentRowHeaders, commonHeaders);
			LOG.debug("current different keys: {}", currentDifferentKeys);

			Map<String, String> currentRowCells = currentRow.getCells();
			LOG.debug("Current row cells: {}", currentRowCells);
			for (String currentDifferentKey : currentDifferentKeys) {
				currentRowCells.remove(currentDifferentKey);
				LOG.debug("Remove a table row cell that is a part of an incomplete column. The table row cell's header is {}", currentDifferentKey);
			}
			currentRow.setCells(currentRowCells);

			rows.set(rowCnt, currentRow);

		}

		LOG.debug("removeAttributeTableRowCellsUnderIncompleteColumns : End");
		return rows;
	}

	/**
	 * Get list of attribute groups that exist in passed xmlString
	 * @param xmlString xml string containing attribute groups
	 * @param adminService admin service for getting the xsd file
	 * @return list of attributes groups
	 */
	public List<ATRGRP> getAttributeGroupList(String xmlString, AdminService adminService) {

		LOG.debug("getAttributeGroupList : START");
		LOG.debug("xmlString: {}", xmlString);

		List<ATRGRP> attributeGroupList = new ArrayList<>();

		if (StringUtils.isNotBlank(xmlString)) {
			xmlString = CommonUtil.convertXMLString(xmlString);
			LOG.debug("xmlString: {}", xmlString);

            InputStream xsdInputStream = CommonUtil.getFileFromDAM(adminService, CommonConstants.DAM_XSD_FILEPATH, CommonConstants.IMG_XSD_FILENAME);
            if (xsdInputStream != null) {
                LOG.debug("xsd stream: {}", xsdInputStream);
                ATRGRPTYPE atrGrpType = CommonUtil.getUnmarshaledClass(xmlString, ATRGRPTYPE.class, xsdInputStream);
                if (null != atrGrpType) {
                    LOG.debug("Attribute group list found.");
                    attributeGroupList = atrGrpType.getATRGRP();
                    LOG.debug("Attribute group list value: {}", attributeGroupList);
                    LOG.debug("getAttributeGroupList : END");
                    return attributeGroupList;
                } else {
                    LOG.error("Attribute group list not found.");
                }
            } else {
                LOG.error("XSD file not found in dam.");
            }

        }

		LOG.debug("getAttributeGroupList : END");
		return attributeGroupList;
	}

	/**
	 * Get image group bean from the attribute group
	 * @param item attribute group data
	 * @return image group bean
	 */
	private static ImageGroupBean getImageGroupBean(ATRGRP item){

		LOG.debug("getImageGroupBean : START");

		ImageGroupBean imageGroupBean = new ImageGroupBean();

		if (StringUtils.isNotBlank(item.getId())) {
			imageGroupBean.setId(item.getId());
		}
		if (StringUtils.isNotBlank(item.getLabel())) {
			imageGroupBean.setId(item.getLabel());
		}
		if (item.getSEQ() != null) {
			imageGroupBean.setId(item.getSEQ().toString());
		}

		Map<String,AttrNmBean> imageRenditionMap = new HashMap<>();

		if(item.getATRNM() != null && !item.getATRNM().isEmpty()) {
			List<ATRNM> atrNmList = item.getATRNM();
			for (ATRNM atrnm : atrNmList) {
				AttrNmBean attrNmBean = getAttributeNameBean(atrnm);
				if (StringUtils.isNotBlank(atrnm.getId())) {
					imageRenditionMap.put(atrnm.getId(), attrNmBean);
				}
			}
		}
		imageGroupBean.setImageRenditionMap(imageRenditionMap);

		LOG.debug("getImageGroupBean : END");
		return imageGroupBean;
	}

	/**
	 * Get a new attribute name bean with the passed attribute data
	 * @param atrNm attribute data
	 * @return attribute name bean
	 */
	private static AttrNmBean getAttributeNameBean(ATRNM atrNm){

		LOG.debug("getAttributeNameBean : START");

		AttrNmBean attrNmBean = new AttrNmBean();

		if (StringUtils.isNotBlank(atrNm.getId())) {
			attrNmBean.setId(atrNm.getId());
		}
		if (StringUtils.isNotBlank(atrNm.getLabel())) {
			attrNmBean.setLabel(atrNm.getLabel());
		}
		if (atrNm.getSEQ() != null) {
			attrNmBean.setSeq(atrNm.getSEQ().toString());
		}
		if (StringUtils.isNotBlank(atrNm.getDisplayFlag())) {
			attrNmBean.setDisplayFlag(atrNm.getDisplayFlag());
		}
		if (StringUtils.isNotBlank(atrNm.getLanguage())) {
			attrNmBean.setLanguage(atrNm.getLanguage());
		}
		if (StringUtils.isNotBlank(atrNm.getCountry())) {
			attrNmBean.setCountry(atrNm.getCountry());
		}
		if (StringUtils.isNotBlank(atrNm.getValue())) {
			attrNmBean.setCdata(atrNm.getValue());
		}
		if (atrNm.getCID() != null && StringUtils.isNotBlank(Long.toString(atrNm.getCID()))) {
			attrNmBean.setCid(Long.toString(atrNm.getCID()));
		}
		if (StringUtils.isBlank(attrNmBean.getCdata()) && StringUtils.isNotBlank(attrNmBean.getCid()) && StringUtils.isNotBlank(atrNm.getLabel())) {
			attrNmBean.setCdata(atrNm.getLabel());
		}
		if (atrNm.getREPFL() != null) {
			attrNmBean.setRepfl(atrNm.getREPFL());
		}

		LOG.debug("getAttributeNameBean : END");
		return attrNmBean;
	}

	/**
	 * Get a multi-row attribute name with the passed multi-row attribute data
	 * @param atrNm multi-row attribute data
	 * @return multi-row attribute bean
	 */
	private static AttrNmBean getMultiRowAttributeNameBean(ATRMULTIROW.ATRNM atrNm){

		LOG.debug("getMultiRowAttributeNameBean : START");

		AttrNmBean attrNmBean = new AttrNmBean();

		if (StringUtils.isNotBlank(atrNm.getId())) {
			attrNmBean.setId(atrNm.getId());
		}
		if (StringUtils.isNotBlank(atrNm.getLabel())) {
			attrNmBean.setLabel(atrNm.getLabel());
		}
		if (atrNm.getSEQ() != null) {
			attrNmBean.setSeq(atrNm.getSEQ().toString());
		}

		if (atrNm.getValue() != null) {
			if (!(atrNm.getValue().isEmpty())) {
				attrNmBean.setCdata(atrNm.getValue());
			} else {
				if (atrNm.getValue().isEmpty()
						&& StringUtils.isNotBlank(atrNm.getId())
						&& atrNm.getId().contentEquals(ATTR_RUNTIME_GRAPH)
						&& StringUtils.isNotBlank(atrNm.getLabel()) ) {

					attrNmBean.setCdata(atrNm.getLabel());
				}
			}
		}

		LOG.debug("getMultiRowAttributeNameBean : END");
		return attrNmBean;
	}
}
