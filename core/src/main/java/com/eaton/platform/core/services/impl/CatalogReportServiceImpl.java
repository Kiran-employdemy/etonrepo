package com.eaton.platform.core.services.impl;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.CatalogReportConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.CatalogReportService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.sling.api.resource.*;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component(service = CatalogReportService.class,immediate = true)
public class CatalogReportServiceImpl implements CatalogReportService {

    private static final Logger LOG = LoggerFactory.getLogger(CatalogReportServiceImpl.class);

    @Reference
    private AdminService adminService;

    public ByteArrayOutputStream generateCatalogReport(final Resource basePathresource, final LinkedHashMap<String, ValueMap> translatedPathMap, final List<String> selectedPropertyList) {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            if (null != basePathresource) {
                final Iterator<Resource> resourceIterator = basePathresource.listChildren();
                final XSSFWorkbook workbook = new XSSFWorkbook();
                final CellStyle style = workbook.createCellStyle();
                final Font font = workbook.createFont();
                style.setFont(font);
                XSSFSheet sheet = null;
                Cell headerCell = null;
                while (resourceIterator.hasNext()) {
                    boolean hasCategory = false;
                    final Resource rootNode = resourceIterator.next();
                    if (null != rootNode && rootNode.getResourceType().equals(NameConstants.NT_PAGE)) {
                        final Page rootPage = rootNode.adaptTo(Page.class);
                        if (null != rootPage && null != rootPage.getContentResource()) {
                            final Resource rootResource = rootPage.getContentResource();
                            final ValueMap parentMap = rootResource.getValueMap();
                            if (null!= parentMap && parentMap.containsKey(JcrConstants.JCR_TITLE)) {
                                sheet = checkDuplicateSheetName(workbook,parentMap,rootPage);
                            }
                            final Iterator<Page> categoryIterator = rootPage.listChildren();
                            int rowIndex = 1;
                            final Row headerRow = sheet.createRow(0);
                            List<String> headerList = Stream.of(CatalogReportConstants.HEADER_NAMES, selectedPropertyList)
                                   .flatMap(map -> map.stream()).collect(Collectors.toList());
                            setHeaderName(headerRow, headerList);
                            Row row;
                            while (categoryIterator.hasNext()) {
                                final Page categoryPage = categoryIterator.next();
                                if (null != categoryPage && null != categoryPage.getContentResource()) {
                                    hasCategory = true;
                                    boolean hasSubcategory = false;
                                    Resource categoryResource = categoryPage.getContentResource();
                                    final ValueMap categoryValueMap = categoryResource.getValueMap();
                                    row = sheet.createRow(rowIndex);
                                    headerCell = row.createCell(0);
                                    setCategoryName(headerCell, categoryPage, categoryValueMap);
                                    final Iterator<Page> subCateogoryIterator = categoryPage.listChildren();
                                    while (subCateogoryIterator.hasNext()) {
                                        final Page subCategoryPage = subCateogoryIterator.next();
                                        if (null != subCategoryPage && null != subCategoryPage.getContentResource()) {
                                            hasSubcategory = true;
                                            Resource subCategoryResource = subCategoryPage.getContentResource();
                                            final ValueMap subCategoryValueMap = subCategoryResource.getValueMap();
                                            headerCell = row.createCell(1);
                                            setSubcategoryName(headerCell, row, subCategoryPage, subCategoryValueMap);
                                            pathTranslationCheck(translatedPathMap, headerCell, categoryPage, row, subCategoryPage, selectedPropertyList, rootPage);
                                            rowIndex++;
                                            row = row.getSheet().createRow(rowIndex);
                                        }
                                    }
                                    if (!hasSubcategory) {
                                        pathTranslationCheck(translatedPathMap, headerCell, categoryPage, row, null, selectedPropertyList, rootPage);
                                        rowIndex++;
                                    }

                                }
                            }
                            if (!hasCategory) {
                                row = sheet.createRow(rowIndex);
                                headerCell = row.createCell(0);
                                setCategoryName(headerCell, rootPage, rootPage.getProperties());
                                pathTranslationCheck(translatedPathMap, headerCell, rootPage, row, null, selectedPropertyList, rootPage);
                                rowIndex++;
                            }
                        }
                    }
                }
                workbook.write(bos);
            }
        } catch (IOException e) {
            LOG.error("Exception while generating excel::".concat(e.getMessage()));
        }
        return bos;
    }

    private XSSFSheet checkDuplicateSheetName(XSSFWorkbook workbook, ValueMap parentMap, Page rootPage) {
        XSSFSheet sheet;
        boolean duplicateSheetCheck = false;
        if (workbook.getNumberOfSheets() != 0) {
            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                if (parentMap.get(JcrConstants.JCR_TITLE).toString().contains(workbook.getSheetName(sheetIndex))) {
                    duplicateSheetCheck = true;
                    break;
                }
            }
            if (duplicateSheetCheck) {
                sheet = workbook.createSheet(rootPage.getName());
            } else {
                sheet = workbook.createSheet(parentMap.get(JcrConstants.JCR_TITLE).toString());
            }
        } else {
            sheet = workbook.createSheet(parentMap.get(JcrConstants.JCR_TITLE).toString());
        }
        return sheet;
    }

    private void pathTranslationCheck(final LinkedHashMap<String, ValueMap> translatedPathMap, Cell headerCell,
                                      final Page categoryPage, final Row row, final Page subCategoryPage,
                                      final List<String> selectedPropertyList,
                                      final Page rootPage) {
        if (null != subCategoryPage && translatedPathMap.containsKey(subCategoryPage.getPath())) {
            setColumnProperties(translatedPathMap, row, subCategoryPage.getPath(), headerCell, selectedPropertyList);
        } else if ( translatedPathMap.containsKey(categoryPage.getPath())) {
            setColumnProperties(translatedPathMap, row, categoryPage.getPath(), headerCell, selectedPropertyList);
        } else if (translatedPathMap.containsKey(rootPage.getPath())) {
            setColumnProperties(translatedPathMap, row, rootPage.getPath(), headerCell, selectedPropertyList);
        } else if (translatedPathMap.containsKey(rootPage.getParent().getPath())) {
            setColumnProperties(translatedPathMap, row, rootPage.getParent().getPath(), headerCell, selectedPropertyList);
        } else {
            headerCell = row.createCell(2);
            headerCell.setCellValue(CommonConstants.NO);
        }
    }

    private void setSubcategoryName(final Cell headerCell, final Row row, final Page subCategoryPage, ValueMap subCategoryValueMap) {
        if (null!= subCategoryValueMap && subCategoryValueMap.containsKey(JcrConstants.JCR_TITLE)) {
            if(null != row.getCell(0) && row.getCell(0).getStringCellValue().equals(subCategoryValueMap.get(JcrConstants.JCR_TITLE))) {
                headerCell.setCellValue(StringUtils.EMPTY);
            } else {
                headerCell.setCellValue(subCategoryValueMap.get(JcrConstants.JCR_TITLE, StringUtils.EMPTY));
            }
        } else{
            headerCell.setCellValue(subCategoryPage.getName());
        }
    }

    private void setCategoryName(final Cell headerCell, final Page categoryPage, ValueMap categoryValueMap) {
        if (null!= categoryValueMap && categoryValueMap.containsKey(JcrConstants.JCR_TITLE)) {
            headerCell.setCellValue(categoryValueMap.get(JcrConstants.JCR_TITLE, StringUtils.EMPTY));
        } else {
            headerCell.setCellValue(categoryPage.getName());
        }
    }

    private void setHeaderName(final Row headerRow,
                               final List<String> headerNameList) {
        for (String headerName : headerNameList) {
            headerRow.createCell(headerNameList.indexOf(headerName)).setCellValue(headerName);
        }
        final SimpleDateFormat simpleDate = new SimpleDateFormat(CatalogReportConstants.SIMPLE_DATE_FORMAT);
        final String dateFormatted = simpleDate.format(new Date());
        headerRow.createCell(8).setCellValue(CatalogReportConstants.DATE.concat(dateFormatted));
    }

    private void setColumnProperties(final LinkedHashMap<String, ValueMap> translatedPathMap, final Row row,
                                      final String nodePath, Cell headerCell, final List<String> selectedPropertyList) {
        final ValueMap propertiesMap = translatedPathMap.get(nodePath);
        headerCell = row.createCell(2);
        headerCell.setCellValue(CommonConstants.YES);
        if (null != propertiesMap) {
            int rowIndex = 3;
            if (propertiesMap.containsKey(CatalogReportConstants.CQ_INITIATOR)
                    && selectedPropertyList.contains(CatalogReportConstants.TRANSLATED_BY)) {
                headerCell = row.createCell(rowIndex);
                headerCell.setCellValue(propertiesMap.get(CatalogReportConstants.CQ_INITIATOR, StringUtils.EMPTY));
                rowIndex ++;
            }
            if (propertiesMap.containsKey(CatalogReportConstants.CQ_SOURCE_NODE_PATH)
                    && selectedPropertyList.contains(CatalogReportConstants.TRANSLATION_SOURCE_PATH)) {
                headerCell = row.createCell(rowIndex);
                headerCell.setCellValue(propertiesMap.get(CatalogReportConstants.CQ_SOURCE_NODE_PATH, StringUtils.EMPTY));
                rowIndex ++;
            }
            if (null != nodePath && selectedPropertyList.contains(CatalogReportConstants.TRANSLATION_TARGET_PATH)) {
                headerCell = row.createCell(rowIndex);
                headerCell.setCellValue(nodePath);
                rowIndex ++;
            }
        }
    }
}
