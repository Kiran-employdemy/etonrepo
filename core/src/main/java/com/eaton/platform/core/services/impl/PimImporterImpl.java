package com.eaton.platform.core.services.impl;

import com.adobe.cq.commerce.api.CommerceConstants;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.PimImporterConstants;
import com.eaton.platform.core.services.PimImporterService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.sling.api.resource.*;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;


@Component(service = PimImporterService.class, immediate = true,
        property = {
                AEMConstants.SERVICE_VENDOR_EATON,
                AEMConstants.SERVICE_DESCRIPTION + "PimImporterImpl",
                AEMConstants.PROCESS_LABEL + "PimImporterImpl"
        })
public class PimImporterImpl implements PimImporterService{


    private static final Logger LOGGER = LoggerFactory.getLogger(PimImporterImpl.class);
    @Override
    public JsonObject createPIMData(final ByteArrayInputStream assetInputStream,final String basePath,
                                    final String replicateFlag,
                                    final ResourceResolver resolver){
        JsonObject responseJson = null;
        LOGGER.info("createPIMData entering!!!");
        try (Workbook workbook = WorkbookFactory.create(assetInputStream)){
            final JsonObject sheetsJsonObject = new JsonObject();
            if (null == workbook) {
                responseJson = createErrorResponse(PimImporterConstants.PLEASE_PROVIDE_VALID_EXCEL_SHEET);
            } else {
              for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                    final Sheet sheet = workbook.getSheetAt(sheetIndex);
                    final Iterator<Row> sheetIterator = sheet.iterator();
                    final JsonArray sheetArray = createJsonData(sheetIterator);
                    sheetsJsonObject.add(workbook.getSheetName(sheetIndex), sheetArray);
                    responseJson = createPimNodeWithData(sheetArray, resolver, basePath);
                }
                if (resolver.hasChanges()) {
                    resolver.commit();
                }
            }
        } catch (IOException e) {
            LOGGER.error("IOException "+e.getLocalizedMessage());
            responseJson = createErrorResponse(PimImporterConstants.SOMETHING_WENT_WRONG_MESSAGE);
        } catch (InvalidFormatException ife) {
            LOGGER.error("InvalidFormatException "+ife.getLocalizedMessage());
            responseJson = createErrorResponse(PimImporterConstants.SOMETHING_WENT_WRONG_MESSAGE);
        } catch (Exception je) {
            LOGGER.error("Exception "+je.getLocalizedMessage());
            responseJson = createErrorResponse(PimImporterConstants.SOMETHING_WENT_WRONG_MESSAGE);
        }
        LOGGER.info("createPIMData exiting!!!");
        return responseJson;
    }

    private JsonArray createJsonData(final Iterator<Row> sheetIterator) throws Exception {
        final JsonArray sheetArray = new JsonArray();
        final ArrayList<String> columnNames = new ArrayList<String>();
        while (sheetIterator.hasNext()) {
            final Row currentRow = sheetIterator.next();
            JsonObject pimJsonObject = new JsonObject();
            if ((currentRow.getRowNum() != 0) && (currentRow.getRowNum() != 1) && (currentRow.getRowNum() != 2)) {
                for (int columnIndex = 0; columnIndex < columnNames.size(); columnIndex++) {
                    pimJsonObject = validateCellType(pimJsonObject, columnNames, currentRow, columnIndex);
                }
                sheetArray.add(pimJsonObject);
            } else if(currentRow.getRowNum() == 2){
                for (int cellIndex = 0; cellIndex < currentRow.getPhysicalNumberOfCells(); cellIndex++) {
                    if (null != currentRow.getCell(cellIndex)) {
                        columnNames.add(currentRow.getCell(cellIndex).getStringCellValue());
                    }
                }
            }
        }
        return sheetArray;
    }

    private JsonObject createPimNodeWithData(final JsonArray pimDataJsonArray, final ResourceResolver resolver,
                                             final String basePath)
            throws Exception, PersistenceException {
        Map<String, Object> properties = null;
        Map<String, Object> propertiesMultifield = null;
        String action = StringUtils.EMPTY;
        JsonObject responseJson = new JsonObject();
        final JsonArray productSuccessArray = new JsonArray();
        final JsonArray productFailureArray = new JsonArray();
        for (int pimDataIndex=0; pimDataIndex < pimDataJsonArray.size(); pimDataIndex++) {
            JsonArray resourcePathArray = new JsonArray();
            final JsonObject failureData = new JsonObject();
            final JsonObject pimDataJson = pimDataJsonArray.get(pimDataIndex).getAsJsonObject();
            final Boolean mandatoryFieldCheck = mandatoryFieldCheck(pimDataJson);
            if (mandatoryFieldCheck) {
                final String extensionID =
                        validateExtensionId(pimDataJson.get(PimImporterConstants.EXTENSION_ID).getAsString());
                if (PimImporterConstants.EXTENSION_ID_NOT_PROVIDED.equals(extensionID)) {
                    responseJson = captureRowLevelError(failureData, responseJson, productFailureArray, pimDataIndex,
                            PimImporterConstants.EXTENSION_ID_NOT_PROVIDED);
                } else {
                    action = pimDataJson.get(PimImporterConstants.ACTION).getAsString();
                    final StringBuilder resourcePath = new StringBuilder();
                    resourcePath.append(basePath).append(PimImporterConstants.SLASH).append(extensionID);
                    properties = createPimData(pimDataJson, resolver);
                    deleteResource(resourcePath.toString(), resolver, action);
                    Resource resource = null;
                    if (PimImporterConstants.ADD.equalsIgnoreCase(action)) {
                        properties.put(JcrConstants.JCR_PRIMARYTYPE,JcrConstants.NT_UNSTRUCTURED);
                        properties.put(CommerceConstants.PN_COMMERCE_TYPE, PimImporterConstants.PRODUCT);
                        properties.put(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY,
                                PimImporterConstants.RESOURCE_TYPE_PATH);
                        properties.put(PimImporterConstants.IS_RICH, PimImporterConstants.TRUE);
                        resource = ResourceUtil.getOrCreateResource(resolver, resourcePath.toString(),
                                properties, null, true);
                    }
                    if (PimImporterConstants.UPDATE.equalsIgnoreCase(action)) {
                        resource = resolver.getResource(resourcePath.toString());
                    }
                    if (null != resource) {
                        resourcePathArray.add(resource.getPath());
                        propertiesMultifield = structureMultiFieldData(pimDataJson);
                        resourcePathArray = createMultiFieldNode(properties,propertiesMultifield, resource, resolver,
                                action, resourcePathArray);
                        productSuccessArray.add(resourcePathArray);
                        responseJson.add(PimImporterConstants.SUCCESS, productSuccessArray);
                    } else {
                        responseJson = captureRowLevelError(failureData, responseJson, productFailureArray,
                                pimDataIndex, PimImporterConstants.UPDATE_ERROR_EXCEL_SHEET);
                    }
                }
            } else {
                action = pimDataJson.get(PimImporterConstants.ACTION).getAsString();
                final String errorMessage = validateHeader(pimDataJson, action);
                responseJson = captureRowLevelError(failureData, responseJson, productFailureArray, pimDataIndex,
                        errorMessage);
            }
        }
        return responseJson;
    }

    private static Map<String, Object> createPimData(final JsonObject pimDataJson,
                                                     final ResourceResolver resolver) throws Exception {
        Map<String, Object> properties = new HashMap<String, Object>();
        Set<Map.Entry<String, JsonElement>> entries = pimDataJson.entrySet();
        for(Map.Entry<String, JsonElement> entry: entries) {
            final String propertyName = entry.getKey();
            Object propertyValue = pimDataJson.get(propertyName);
            if (!propertyName.contains(PimImporterConstants.UNDERSCORE)) {
                if (PimImporterConstants.TAGS.equals(propertyName)) {
                    final String[] tagValue = structureTagsValue(propertyValue.toString());
                    properties = retriveTagId(tagValue, resolver, properties);
                } else if (PimImporterConstants.EXTENSION_ID.equals(propertyName)) {
                    properties.put(JcrConstants.JCR_TITLE, propertyValue);
                } else if (propertyValue instanceof JsonObject) {
                    propertyValue = createPimData((JsonObject) propertyValue, resolver);
                    properties.put(propertyName, propertyValue);
                } else {
                    properties.put(propertyName, propertyValue);
                }
            }
        }
        return properties;
    }

    private static String[] structureTagsValue(final String tagValue) {
        return tagValue.split(PimImporterConstants.SEMICOLON);
    }

    private Map<String, Object> structureMultiFieldData(final JsonObject pimDataJson) throws Exception {
    	 Set<Map.Entry<String, JsonElement>> entries = pimDataJson.entrySet();
         
        //final Iterator<String> keys = pimDataJson.keys();
        final Map<String, Object> properties = new HashMap<String, Object>();
        Map<String, Object> propertiesParent = null;
        for(Map.Entry<String, JsonElement> entry: entries) {
        //while(keys.hasNext()) {
            final String key = entry.getKey();
            if (key.contains(PimImporterConstants.UNDERSCORE)) {
                final String[] propertyName = key.split(PimImporterConstants.UNDERSCORE);
                Map<String, Object> propertiesChild = new HashMap<String, Object>();
                if (propertyName.length > 1) {
                    if (properties.containsKey(propertyName[0])) {
                        propertiesParent = (Map<String, Object>) properties.get(propertyName[0]);
                        if (propertiesParent.containsKey(propertyName[2])) {
                            propertiesChild = (Map<String, Object>) propertiesParent.get(propertyName[2]);
                            propertiesChild.put(propertyName[1], pimDataJson.get(key));
                            propertiesParent.put(propertyName[2], propertiesChild);
                            properties.put(propertyName[0], propertiesParent);
                        } else {
                            propertiesChild.put(JcrConstants.JCR_PRIMARYTYPE,JcrConstants.NT_UNSTRUCTURED);
                            propertiesChild.put(propertyName[1], pimDataJson.get(key));
                            propertiesParent.put(propertyName[2], propertiesChild);
                            properties.put(propertyName[0], propertiesParent);
                        }
                    } else {
                        propertiesChild.put(JcrConstants.JCR_PRIMARYTYPE,JcrConstants.NT_UNSTRUCTURED);
                        propertiesParent = new HashMap<String, Object>();
                        propertiesChild.put(propertyName[1], pimDataJson.get(key));
                        propertiesParent.put(propertyName[2], propertiesChild);
                        properties.put(propertyName[0], propertiesParent);
                    }
                }
            }
        }
        return properties;
    }

    private JsonArray createMultiFieldNode(final Map<String, Object> properties,
                                           final Map<String, Object> propertiesMultifield, final Resource resource,
                                           final ResourceResolver resourceResolver, final String action,
                                           final JsonArray resourcePathArray) throws PersistenceException {
        propertiesMultifield.forEach((propertyKey,propertyValue)->{
            final String nodePath = resource.getPath().concat(PimImporterConstants.SLASH).concat(propertyKey);
            final Map<String, Object> propertiesParent = (Map<String, Object>) propertyValue;
            propertiesParent.forEach((nodePropertyKey,nodePropertyValue)->{
                final String absolutePath = nodePath.concat(PimImporterConstants.SLASH).concat(nodePropertyKey);
                final Map<String, Object> childProperties = (Map<String, Object>) nodePropertyValue;
                try {
                    final Resource resourceChild = ResourceUtil.getOrCreateResource(resourceResolver,
                            absolutePath,
                            childProperties, null, true);
                    if (null !=resourceChild) {
                        resourcePathArray.add(resourceChild.getPath());
                        if (PimImporterConstants.UPDATE.equalsIgnoreCase(action)) {
                            final ModifiableValueMap modifiableValueMap =
                                    resourceChild.adaptTo(ModifiableValueMap.class);
                            updatePimResourceMap(modifiableValueMap, childProperties);
                        }
                    }
                } catch (PersistenceException e) {
                    LOGGER.error("PersistenceException "+e.getLocalizedMessage());
                }
            });
        });

        if (action.equalsIgnoreCase(PimImporterConstants.UPDATE)) {
            final ModifiableValueMap modifiableValueMap = resource.adaptTo(ModifiableValueMap.class);
            updatePimResourceMap(modifiableValueMap, properties);
        }
        return resourcePathArray;
    }

    private void updatePimResourceMap(final Map<String, Object> modifiableValueMap,
                                      final Map<String, Object> propertiesMap) throws PersistenceException {
        propertiesMap.forEach((propertyName, propertyValue) -> {
            if ( modifiableValueMap.containsKey(propertyName)) {
                final Object actualValue = modifiableValueMap.get(propertyName);
                if (null != propertyValue && !actualValue.equals(propertyValue)) {
                    modifiableValueMap.put(propertyName, propertyValue);
                }
            } else if (null != propertyValue) {
                modifiableValueMap.put(propertyName, propertyValue);
            }
        });
    }

    private String validateExtensionId(String extensionID) {
        if (StringUtils.isNotBlank(extensionID)) {
            if (StringUtils.isNumeric(extensionID)) {
                extensionID =  PimImporterConstants.UNDERSCORE.concat(extensionID);
            } else {
                extensionID = extensionID.replaceAll(PimImporterConstants.SPACE,
                        PimImporterConstants.UNDERSCORE).toLowerCase(Locale.ENGLISH);
            }
        } else {
            extensionID = PimImporterConstants.EXTENSION_ID_NOT_PROVIDED;
        }
        return extensionID;
    }

    private JsonObject createErrorResponse(final String errorMessage) {
        final JsonObject errorJson = new JsonObject();
        try {
            errorJson.add(PimImporterConstants.STATUS, new Gson().toJsonTree(PimImporterConstants.FAIL));
            errorJson.add(PimImporterConstants.ERROR_DESC, new Gson().toJsonTree(errorMessage));
        } catch(Exception exc) {
            LOGGER.error("Error constructing JSON response");
        }
        return errorJson;
    }

    private void deleteResource(final String resourcePath, final ResourceResolver resourceResolver,
                                final String action) throws PersistenceException {
        final Resource pimResource = resourceResolver.getResource(resourcePath);
        if (null != pimResource && action.equalsIgnoreCase(PimImporterConstants.ADD)) {
            resourceResolver.delete(pimResource);
        }
    }

    private String validateHeader(final JsonObject pimDataJson, final String action) {
        final StringBuilder errorMessge = new StringBuilder();
        if (!pimDataJson.has(PimImporterConstants.EXTENSION_ID)) {
            errorMessge.append(PimImporterConstants.EXTENSION_ID_MISSING_IN_UPLOADED_EXCEL_SHEET);
        }
        if (PimImporterConstants.ADD.equalsIgnoreCase(action)) {
            if (!pimDataJson.has(PimImporterConstants.IDENTIFIER)) {
                errorMessge.append(PimImporterConstants.IDENTIFIER_MISSING_IN_UPLOADED_EXCEL_SHEET);
            }
            if (!pimDataJson.has(PimImporterConstants.PRODUCT_NAME)) {
                errorMessge.append(PimImporterConstants.PRODUCT_NAME_MISSING_IN_UPLOADED_EXCEL_SHEET);
            }
            if (!pimDataJson.has(PimImporterConstants.TAGS)) {
                errorMessge.append(PimImporterConstants.TAG_NAME_MISSING_IN_UPLOADED_EXCEL_SHEET);
            }
        }
        return errorMessge.toString();
    }

    private JsonObject validateCellType(final JsonObject sheetJsonObject, final ArrayList<String> columnNames,
                                        final Row currentRow, final int columnIndex) throws Exception {
        if (currentRow.getCell(columnIndex) != null) {
            switch (currentRow.getCell(columnIndex).getCellTypeEnum()) {
                case STRING:
                    sheetJsonObject.add(columnNames.get(columnIndex),
                    		new Gson().toJsonTree(currentRow.getCell(columnIndex).getStringCellValue()));
                    break;
                case NUMERIC:
                    sheetJsonObject.add(columnNames.get(columnIndex),
                    		new Gson().toJsonTree(Math.round(currentRow.getCell(columnIndex).getNumericCellValue())));
                    break;
                case BOOLEAN:
                    sheetJsonObject.add(columnNames.get(columnIndex),
                    		new Gson().toJsonTree(String.valueOf(currentRow.getCell(columnIndex).getBooleanCellValue())));
                    break;
                default:
                    LOGGER.info("Cell type is blank");
                    break;
            }
        } else {
            sheetJsonObject.add(columnNames.get(columnIndex), new Gson().toJsonTree(StringUtils.EMPTY));
        }
        return sheetJsonObject;
    }

    private static Map<String, Object> retriveTagId(final String[] tagValue, final ResourceResolver resourceResolver,
                              final Map<String, Object> properties) {
        final TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
        final List<String> tagArray = new ArrayList<String>();
        for (final String tagName : tagValue) {
            final Tag tag = tagManager.resolveByTitle(tagName);
            if (null != tag) {
                tagArray.add(tag.getTagID());
            }
        }
        if (!tagArray.isEmpty()) {
            properties.put(PimImporterConstants.TAGS, tagArray.toArray(new String[0]));
        }
        return properties;
    }

    private JsonObject captureRowLevelError(final JsonObject failureData, final JsonObject responseJson,
                                            final JsonArray productFailureArray, final int  pimDataIndex,
                                            final String errorMsg) throws Exception {
        failureData.add(PimImporterConstants.ROW, new Gson().toJsonTree(String.valueOf(pimDataIndex)));
        failureData.add(PimImporterConstants.ERR_MSG, new Gson().toJsonTree(errorMsg));
        productFailureArray.add(failureData);
        responseJson.add(PimImporterConstants.FAILURE, productFailureArray);
        return responseJson;
    }

    private Boolean mandatoryFieldCheck(final JsonObject pimDataJson) throws Exception {
        Boolean mandatoryFieldCheck = Boolean.TRUE;
        if (pimDataJson.has(PimImporterConstants.ACTION)) {
            final String action = pimDataJson.get(PimImporterConstants.ACTION).getAsString();
            if (PimImporterConstants.ADD.equalsIgnoreCase(action)) {
                mandatoryFieldCheck = ((pimDataJson.has(PimImporterConstants.EXTENSION_ID))
                        && (pimDataJson.has(PimImporterConstants.IDENTIFIER))
                        && (pimDataJson.has(PimImporterConstants.PRODUCT_NAME))
                        && (pimDataJson.has(PimImporterConstants.TAGS)));
            } else if (PimImporterConstants.UPDATE.equalsIgnoreCase(action)) {
                mandatoryFieldCheck = pimDataJson.has(PimImporterConstants.EXTENSION_ID);
            } else {
                mandatoryFieldCheck = Boolean.FALSE;
                LOGGER.error("Invalid action value provided");
            }
        } else {
            mandatoryFieldCheck = Boolean.FALSE;
            LOGGER.error("Action value not provided");
        }
        return mandatoryFieldCheck;
    }
}
