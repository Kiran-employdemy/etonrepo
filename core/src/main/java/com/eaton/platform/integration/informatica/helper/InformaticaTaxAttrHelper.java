package com.eaton.platform.integration.informatica.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.eaton.platform.core.exception.EatonApplicationException;
import com.eaton.platform.core.exception.EatonSystemException;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.integration.informatica.bean.InformaticaConfigServiceBean;
import com.eaton.platform.integration.informatica.bean.InformaticaResponse;
import com.eaton.platform.integration.informatica.constants.InformaticaConstants;
import com.eaton.platform.integration.informatica.models.taxonomyAttributes.TAXONOMYATTRIBUTEITM;
import com.eaton.platform.integration.informatica.models.taxonomyAttributes.TAXONOMYATTRIBUTEITM.ROW;
import com.eaton.platform.integration.informatica.util.InformaticaUtil;

/**
 * This class defines business logic to readTaxonomy Attributes from XML file
 * and then import them to AEM.
 * 
 * @author TCS
 * 
 */

public class InformaticaTaxAttrHelper {

	private static final Logger LOG = LoggerFactory.getLogger(InformaticaTaxAttrHelper.class);
	private static final String DOES_NOT_EXISTS = "does not exists.";
	private static final String ATTRIBUTE_NAME = "attributeName";
	private static final String ATTRIBUTE_DISPLAY_NAME = "attributeDisplayName";
	private static final String STATUS = "status";
	private static final String LAST_UPDATE_DATE = "lastUpdateDate";
	private static final String SEQUENCE = "sequence";
	private static final String ICC = "icc";
	private static final String ATTRIBUTE_GRP_NAME = "attrGroupName";
	private static final String ATTRIBUTE_GRP_DISP_NAME = "attrGroupDispName";
	private static final String SECONDS = " seconds.";

	/**
	 * This method is main method to call other inner methods to process XML
	 * file content and return the final result
	 * 
	 * @param informaticaConfigServiceBean
	 * @param adminService
	 * @return
	 * @throws EatonApplicationException
	 */
	public InformaticaResponse processTaxAttrData(InformaticaConfigServiceBean informaticaConfigServiceBean,
			AdminService adminService) throws EatonApplicationException {

		LOG.debug(InformaticaConstants.IMPORT_STARTED);
		// start import timing
		long startTime = System.nanoTime();
		if (LOG.isInfoEnabled()) {
			LOG.info("Starting Global Attribute data process with system time: " + startTime);
		}
		// Method vars for managing import
		InformaticaResponse response = new InformaticaResponse();
		TAXONOMYATTRIBUTEITM taxattrObject = null;

		try {

			// Retrieved XML file that need to process
			File taxAttrXML = getEntryXMLFile(informaticaConfigServiceBean);
			if (LOG.isInfoEnabled()) {
				LOG.info("File reading time " + (double) ((System.nanoTime() - startTime) / 1000000000.0) + SECONDS);
			} // Create TaxonomyAttribute Object
			long UnmarshallingStartTime = System.nanoTime();
			taxattrObject = parseTaxAttrXML(taxAttrXML, adminService);
			if (LOG.isInfoEnabled()) {
				LOG.info("File Unmarshalling Time " + ((System.nanoTime() - UnmarshallingStartTime) / 1000000000.0)
						+ SECONDS);
			} // List of Items
			List<ROW> rowsList = taxattrObject.getROW();
			if (rowsList != null && !rowsList.isEmpty()) {

				// Process the Product Family data
				processEntryData(adminService, response, rowsList);

			} else {
				LOG.info(InformaticaConstants.PARSING_ISSUE_ERROR_CODE, InformaticaConstants.PARSING_ISSUE_ERROR_MSG);

			}
			// Log import duration
			long endTime = System.nanoTime();
			long durationMilli = endTime - startTime;
			double durationSeconds = (double) durationMilli / 1000000000.0;
			if (LOG.isInfoEnabled()) {
				LOG.info("Ending Taxonomy Attribute data process with system time: " + endTime);
				LOG.info("The import took " + durationSeconds + SECONDS);
			}
			LOG.debug(InformaticaConstants.PROCESS_ENDED);
		} catch (Exception e) {
			LOG.error(InformaticaConstants.EXCEPTION_OCCURED, e.getMessage(), e);
			response.setProcessFlag(false);
			response.setErrMsg(e.getMessage());

		}

		return response;
	}

	/**
	 * This method starts processing of Data coming in XML
	 * 
	 * @param adminService
	 * @param response
	 * @param rowsList
	 * @throws IOException
	 * @throws RepositoryException
	 */
	private void processEntryData(AdminService adminService, InformaticaResponse response, List<ROW> rowsList)
			throws IOException {

		// Method vars
		int updateItemsCount = 0;
		List<String> successItemIDs = new ArrayList<>();
		List<String> failedItemIDs = new ArrayList<>();
		File statusFile;
		// ResourceResolver object to get Write Service
		try(ResourceResolver adminResourceResolver = adminService.getWriteService()) {
			// First Checking for No incremental Data
			if (rowsList.size() == 1 && rowsList.get(0).getATTRNAME().equals("-9999")) {
				LOG.info(InformaticaConstants.NO_DATA_TO_CHANGE);
			} else {
				long importDataStartTime = System.nanoTime();
				if (LOG.isInfoEnabled()) {
					LOG.info("importDataStartTime " + importDataStartTime + SECONDS);
				}
				// Iterate For each Taxonomy Attribute record
				for (ROW rowtaxAttr : rowsList) {
					// Process the Taxonomy Attribute data
					updateItemsCount = importEntryDataIntoAEM(rowtaxAttr, successItemIDs, failedItemIDs,
							adminResourceResolver, response);
				}
				if (LOG.isInfoEnabled()) {
					LOG.info("Total time to Import Data " + ((System.nanoTime() - importDataStartTime) / 1000000000.0)
							+ SECONDS);
				}
			}
			LOG.info(InformaticaConstants.TOTAL_ENTRIES_ADDEDTO_AEM, updateItemsCount);
			// create status.txt file
			statusFile = InformaticaUtil.updateStatus(successItemIDs, failedItemIDs, InformaticaConstants.STATUS_FILE_TAX);
			if (statusFile != null) {
				response.setStatusFile(statusFile);
				response.setStatusFilePath(statusFile.getCanonicalPath());
			}
		}

	}

	/**
	 * This method is to read Input XML file from Shared location on server.
	 * 
	 * @param informaticaConfigServiceBean
	 * @return
	 * @throws EatonApplicationException
	 */
	private File getEntryXMLFile(InformaticaConfigServiceBean informaticaConfigServiceBean)
			throws EatonApplicationException {

		LOG.debug(" Entered into getEntryXMLFile() method");
		String filepath;

		if (informaticaConfigServiceBean.getAttributeInputFilePath().endsWith(InformaticaConstants.BACKWARD_SLASH))
			filepath = informaticaConfigServiceBean.getAttributeInputFilePath();
		else
			filepath = informaticaConfigServiceBean.getAttributeInputFilePath() + File.separator;
		File files = new File(filepath);

		// Validate the current file path is accepted
		if (files.exists()) {

			// Validate at least one XML file exists for processing
			if (files.listFiles().length != 0) {

				File taxonomyfile = new File(filepath + informaticaConfigServiceBean.getTaxonomyAttFileName());
				// Validate if required input Global Attribute file exists for
				// processing
				if (taxonomyfile.exists()) {
					LOG.info("File {} Found at shared location ",
							informaticaConfigServiceBean.getTaxonomyAttFileName());
					LOG.debug(" Existed getEntryXMLFile() method");
					return taxonomyfile;

				}

				else {
					// No Requested Input file found
					if (LOG.isInfoEnabled()) {
						LOG.info(
								InformaticaConstants.FILE_NOT_FOUND_ERROR_CODE
										.concat(InformaticaConstants.HYPHEN_STRING) + "{}",
								informaticaConfigServiceBean.getTaxonomyAttFileName());
					}
					throw new EatonApplicationException(InformaticaConstants.FILE_NOT_FOUND_ERROR_CODE,
							InformaticaConstants.FILE_NOT_FOUND_ERROR_MSG);

				}

			} else {
				// No XML files found to process
				LOG.info(InformaticaConstants.NO_XML_ERROR_CODE, InformaticaConstants.NO_XML_ERROR_MSG);
				throw new EatonApplicationException(InformaticaConstants.NO_XML_ERROR_CODE,
						InformaticaConstants.NO_XML_ERROR_MSG);
			}

		}

		else {
			if (LOG.isInfoEnabled()) {
				LOG.info("Input FilePath {}" + DOES_NOT_EXISTS, filepath);
			}
			throw new EatonApplicationException(InformaticaConstants.PATH_ERROR_CODE,
					InformaticaConstants.PATH_ERROR_MSG);
		}

	}

	/**
	 * This method is to import Taxonomy data in AEM
	 * 
	 * @param rowItem
	 * @param successItemIDs
	 * @param failedItemIDs
	 * @param adminResourceResolver
	 * @param response
	 * @return
	 */
	private int importEntryDataIntoAEM(ROW rowItem, List<String> successItemIDs, List<String> failedItemIDs,
			ResourceResolver adminResourceResolver, InformaticaResponse response) {
		long importDataStartTime = System.nanoTime();
		ROW taxAttribute = rowItem;
		long inventoryID = 0;
		int count = response.getUpdateItemsCount();
		int addedCount = 0;
		String extensionID = InformaticaUtil.getLowercaseEscapeSpace(taxAttribute.getPRODUCTFAMILYEXTID());
		String brand = InformaticaUtil.getLowercaseEscapeSpace(taxAttribute.getPRODUCTBRAND());
		String subBrand = InformaticaUtil.getLowercaseEscapeSpace(taxAttribute.getPRODUCTSUBBRAND());
		String tradeName = InformaticaUtil.getLowercaseEscapeSpace(taxAttribute.getPRODUCTTRADENAME());
		try {

			inventoryID = taxAttribute.getINVENTORYITEMID();
			LOG.info("Iteration of record Started");
			if (LOG.isInfoEnabled()) {
				LOG.info("Inventory ID & Extension ID for itreated ROW IS: " + inventoryID + "&"
						+ taxAttribute.getPRODUCTFAMILYEXTID());
			}

			Node extIDNode;
			Node taxAttrNode;
			String taxAttrNodePath;

			// Logic to create Node structure for each record of
			// Taxonomy.xml
			String extIDNodePath = InformaticaConstants.TAXONOMY_ATTRIBUTE_AEM_LOCATION + brand
					+ InformaticaConstants.BACKWARD_SLASH + subBrand + InformaticaConstants.BACKWARD_SLASH + tradeName
					+ InformaticaConstants.BACKWARD_SLASH + inventoryID + InformaticaConstants.XXPDH_PRD_FM_AG_LOCATION
					+ extensionID;
			Resource extIDResource = adminResourceResolver.getResource(extIDNodePath);
			if (extIDResource != null && !ResourceUtil.isNonExistingResource(extIDResource)) {
				extIDNode = extIDResource.adaptTo(Node.class);

				List<List<String>> taxAttrPropertiesList = getTaxAttrPropertiesList(taxAttribute);
				String[] taxAttributeList = taxAttribute.getATTRNAME().split(InformaticaConstants.PIPE_STRING);

				for (int i = 0; i < taxAttributeList.length; i++) {
					// Determine if Node to be created already exists or
					// not
					taxAttrNodePath = extIDNodePath + InformaticaConstants.BACKWARD_SLASH
							+ InformaticaUtil.getLowercaseEscapeSpace(taxAttributeList[i]);
					// Creating Taxonmoy Attribute Node
					taxAttrNode = createNodeStructure(adminResourceResolver,
							InformaticaUtil.getLowercaseEscapeSpace(taxAttributeList[i]), extIDNode, taxAttrNodePath);
					// setting properties for created Node
					Map<String, String> taxAttrProperties = getTaxAttrProperties(taxAttrPropertiesList, i);
					setTaxNodeProperties(taxAttrNode, taxAttrProperties);
				}
				successItemIDs.add(extensionID.concat(InformaticaConstants.HYPHEN_STRING)
						.concat(InformaticaConstants.SUCCESS_CODE));
				count++;
			} else {
				List<String> paths = InformaticaUtil.getPossiblePaths(brand, subBrand, tradeName);
				addedCount = createTaxNodeAtNonePath(taxAttribute, adminResourceResolver, paths, successItemIDs);

			}

			response.setUpdateItemsCount(count + addedCount);
			LOG.info("Iteration of record Ended");
			if (LOG.isInfoEnabled()) {
				LOG.info("Total time to create Node for Record "
						+ ((System.nanoTime() - importDataStartTime) / 1000000000.0) + SECONDS);
			}
		} catch (EatonApplicationException | EatonSystemException e) {
			LOG.error(InformaticaConstants.IMPORT_FILE_ERROR_CODE.concat("_").concat(taxAttribute.getATTRNAME())
					.concat(InformaticaConstants.IMPORT_FILE_ERROR_MSG), e.getMessage(), e);
			String failedEntry = extensionID.concat(InformaticaConstants.HYPHEN_STRING)
					.concat(InformaticaConstants.IMPORT_FILE_ERROR_CODE);
			failedItemIDs.add(failedEntry);
			response.setProcessFlag(false);
			response.setErrMsg(InformaticaConstants.IMPORT_FILE_ERROR_CODE + InformaticaConstants.UNDERSCORE
					+ InformaticaConstants.IMPORT_FILE_ERROR_MSG);

		}

		catch (Exception e) {
			LOG.error(InformaticaConstants.IMPORT_FILE_ERROR_CODE.concat("_").concat(taxAttribute.getATTRNAME())
					.concat(InformaticaConstants.IMPORT_FILE_ERROR_MSG), e.getMessage(), e);
			String failedEntry = extensionID.concat(InformaticaConstants.HYPHEN_STRING)
					.concat(InformaticaConstants.IMPORT_FILE_ERROR_CODE);
			failedItemIDs.add(failedEntry);
			response.setProcessFlag(false);
			response.setErrMsg(InformaticaConstants.IMPORT_FILE_ERROR_CODE + InformaticaConstants.UNDERSCORE
					+ InformaticaConstants.IMPORT_FILE_ERROR_MSG);

		}

		return count;
	}

	/**
	 * This method is get List of all attributes values
	 * 
	 * @param taxAttribute
	 * @return
	 */
	private List<List<String>> getTaxAttrPropertiesList(ROW taxAttribute) {

		List<List<String>> taxAttrPropertiesList = new ArrayList<>();
		List<String> taxAttributeNameList = Arrays
				.asList(taxAttribute.getATTRNAME().split(InformaticaConstants.PIPE_STRING));
		List<String> taxAttributeDisNameList = Arrays
				.asList(taxAttribute.getATTRDISPLAYNAME().split(InformaticaConstants.PIPE_STRING));
		List<String> taxAttributeStatusList = Arrays
				.asList(taxAttribute.getSTATUS().split(InformaticaConstants.PIPE_STRING));
		List<String> taxAttrLastUpdateDateList = Arrays
				.asList(taxAttribute.getLASTUPDATEDATE().split(InformaticaConstants.PIPE_STRING));
		List<String> taxAttributeSequenceList = Arrays
				.asList(taxAttribute.getSEQUENCE().split(InformaticaConstants.PIPE_STRING));
		List<String> taxAttributeIccList = Arrays
				.asList(taxAttribute.getICC().split(InformaticaConstants.PIPE_STRING));
		List<String> taxAttributeGrpList = Arrays
				.asList(taxAttribute.getATTRGROUPNAME().split(InformaticaConstants.PIPE_STRING));
		List<String> taxAttributeGrpDispNameList = Arrays
				.asList(taxAttribute.getATTRGROUPDISPNAME().split(InformaticaConstants.PIPE_STRING));
		taxAttrPropertiesList.add(0, taxAttributeNameList);
		taxAttrPropertiesList.add(1, taxAttributeDisNameList);
		taxAttrPropertiesList.add(2, taxAttributeStatusList);
		taxAttrPropertiesList.add(3, taxAttrLastUpdateDateList);
		taxAttrPropertiesList.add(4, taxAttributeSequenceList);
		taxAttrPropertiesList.add(5, taxAttributeIccList);
		taxAttrPropertiesList.add(6, taxAttributeGrpList);
		taxAttrPropertiesList.add(7, taxAttributeGrpDispNameList);
		return taxAttrPropertiesList;
	}

	/**
	 * This method is to setTaxNode Properties
	 * 
	 * @param taxAttrNode
	 * @param taxAttrProperties
	 * @throws EatonApplicationException
	 */
	private void setTaxNodeProperties(Node taxAttrNode, Map<String, String> taxAttrProperties)
			throws EatonApplicationException {

		try {
			if (taxAttrNode != null) {
				taxAttrNode.setProperty(ATTRIBUTE_NAME, taxAttrProperties.get(ATTRIBUTE_NAME));
				taxAttrNode.setProperty(ATTRIBUTE_DISPLAY_NAME, taxAttrProperties.get(ATTRIBUTE_DISPLAY_NAME));
				taxAttrNode.setProperty(STATUS, taxAttrProperties.get(STATUS));
				taxAttrNode.setProperty(LAST_UPDATE_DATE, taxAttrProperties.get(LAST_UPDATE_DATE));
				taxAttrNode.setProperty(SEQUENCE, taxAttrProperties.get(SEQUENCE));
				taxAttrNode.setProperty(ICC, taxAttrProperties.get(ICC));
				taxAttrNode.setProperty(ATTRIBUTE_GRP_NAME, taxAttrProperties.get(ATTRIBUTE_GRP_NAME));
				taxAttrNode.setProperty(ATTRIBUTE_GRP_DISP_NAME, taxAttrProperties.get(ATTRIBUTE_GRP_DISP_NAME));
				taxAttrNode.getSession().save();
			}
		} catch (RepositoryException e) {
			LOG.info(InformaticaConstants.IMPORT_FILE_ERROR_CODE, InformaticaConstants.IMPORT_FILE_ERROR_MSG);
			throw new EatonApplicationException(InformaticaConstants.IMPORT_FILE_ERROR_CODE, e.getMessage(), e);

		}
	}

	/**
	 * This method is to set Map for properties of Taxonomy Node
	 * 
	 * @param taxAttrPropertiesList
	 * @param i
	 * @return
	 */
	private Map<String, String> getTaxAttrProperties(List<List<String>> taxAttrPropertiesList, int i) {

		Map<String, String> taxAttrProperties = new HashMap<>();
		taxAttrProperties.put(ATTRIBUTE_NAME, taxAttrPropertiesList.get(0).get(i));
		taxAttrProperties.put(ATTRIBUTE_DISPLAY_NAME, taxAttrPropertiesList.get(1).get(i));
		taxAttrProperties.put(STATUS, taxAttrPropertiesList.get(2).get(i));
		taxAttrProperties.put(LAST_UPDATE_DATE, taxAttrPropertiesList.get(3).get(i));
		taxAttrProperties.put(SEQUENCE, taxAttrPropertiesList.get(4).get(i));
		taxAttrProperties.put(ICC, taxAttrPropertiesList.get(5).get(i));
		taxAttrProperties.put(ATTRIBUTE_GRP_NAME, taxAttrPropertiesList.get(6).get(i));
		taxAttrProperties.put(ATTRIBUTE_GRP_DISP_NAME, taxAttrPropertiesList.get(7).get(i));
		return taxAttrProperties;

	}

	/**
	 * This method is to create Taxonomy Attribute at other possible paths
	 * 
	 * @param taxAttribute
	 * @param adminResourceResolver
	 * @param paths
	 * @param successItemIDs
	 * @return
	 * @throws EatonApplicationException
	 * @throws RepositoryException
	 */
	private int createTaxNodeAtNonePath(ROW taxAttribute, ResourceResolver adminResourceResolver, List<String> paths,
			List<String> successItemIDs) throws EatonApplicationException, RepositoryException {
		Node extIDNode = null;
		String taxAttrNodePath;
		Node taxAttrNode = null;
		int updatedAttrcount = 0;
		long inventoryID = taxAttribute.getINVENTORYITEMID();
		String extensionID = InformaticaUtil.getLowercaseEscapeSpace(taxAttribute.getPRODUCTFAMILYEXTID());
		boolean isNodeFound = false;
		for (String path : paths) {
			String extensionPath = InformaticaConstants.TAXONOMY_ATTRIBUTE_AEM_LOCATION + path + inventoryID
					+ InformaticaConstants.XXPDH_PRD_FM_AG_LOCATION + extensionID;
			Resource extIDResource = adminResourceResolver.getResource(extensionPath);
			if (extIDResource != null && !ResourceUtil.isNonExistingResource(extIDResource)) {
				extIDNode = extIDResource.adaptTo(Node.class);

				List<List<String>> taxAttrPropertiesList = getTaxAttrPropertiesList(taxAttribute);
				String[] taxAttributeList = taxAttribute.getATTRNAME().split(InformaticaConstants.PIPE_STRING);

				for (int i = 0; i < taxAttributeList.length; i++) {
					// Creating Taxonmoy Attribute Node
					taxAttrNodePath = extensionPath + InformaticaConstants.BACKWARD_SLASH
							+ InformaticaUtil.getLowercaseEscapeSpace(taxAttributeList[i]);
					try{
					taxAttrNode = createNodeStructure(adminResourceResolver,
							InformaticaUtil.getLowercaseEscapeSpace(taxAttributeList[i]), extIDNode, taxAttrNodePath);
					}catch(Exception e){
						LOG.error(e.getMessage());
					}
					// setting properties for created Node
					Map<String, String> taxAttrProperties = getTaxAttrProperties(taxAttrPropertiesList, i);
					setTaxNodeProperties(taxAttrNode, taxAttrProperties);

				}
				successItemIDs.add(extensionID.concat(InformaticaConstants.HYPHEN_STRING)
						.concat(InformaticaConstants.SUCCESS_CODE));
				updatedAttrcount++;
				isNodeFound = true;
				break;

			}
		}
		if (!isNodeFound) {
			// Check for Extension ID node using AEM search, if Node exists then
			// return path of same
			String extensionIDNodePath = isExistingExtensionIDNode(adminResourceResolver, extensionID);
			boolean flag = false;
			if (extensionIDNodePath != null) {
				for (String path : extensionIDNodePath.split(InformaticaConstants.BACKWARD_SLASH)) {
					if (path.equals(Long.toString(inventoryID))) {
						Resource res = adminResourceResolver.getResource(extensionIDNodePath);
						if (res != null) {
							extIDNode = res.adaptTo(Node.class);
						}
						List<List<String>> taxAttrPropertiesList = getTaxAttrPropertiesList(taxAttribute);
						String[] taxAttributeList = taxAttribute.getATTRNAME().split(InformaticaConstants.PIPE_STRING);
						for (int i = 0; i < taxAttributeList.length; i++) {
							// Creating Taxonmoy Attribute Node
							taxAttrNodePath = extensionIDNodePath + InformaticaConstants.BACKWARD_SLASH
									+ InformaticaUtil.getLowercaseEscapeSpace(taxAttributeList[i]);
							try{
							taxAttrNode = createNodeStructure(adminResourceResolver,
									InformaticaUtil.getLowercaseEscapeSpace(taxAttributeList[i]), extIDNode,
									taxAttrNodePath);
							}catch(Exception e){
								LOG.error(e.getMessage());
							}
							// setting properties for created Node
							Map<String, String> taxAttrProperties = getTaxAttrProperties(taxAttrPropertiesList, i);
							setTaxNodeProperties(taxAttrNode, taxAttrProperties);
						}
						successItemIDs.add(extensionID.concat(InformaticaConstants.HYPHEN_STRING)
								.concat(InformaticaConstants.SUCCESS_CODE));
						updatedAttrcount++;
						flag = true;
						break;

					}
				}

			}
			if (extensionIDNodePath == null || !flag) {
				LOG.info("Extension ID {} for Given Taxonmoy Attribute does not exists", extensionID);
				throw new EatonApplicationException(InformaticaConstants.IMPORT_FILE_ERROR_CODE,
						InformaticaConstants.IMPORT_FILE_ERROR_MSG);

			}

		}
		return updatedAttrcount;
	}

	/**
	 * This method Creates, Executes Query bases on params provided and returns
	 * whether Extension ID node present in JCR or not.
	 * 
	 * @param adminResourceResolver
	 * @param pdhnodeName
	 * @return
	 * @throws RepositoryException
	 */
	public static String isExistingExtensionIDNode(ResourceResolver adminResourceResolver, String pdhnodeName)
			throws RepositoryException {
		/**
		 * Query Reference.
		 */
		Query query = null;
		String path = null;
		// Creating QueryBuilder
		QueryBuilder queryBuilder = adminResourceResolver.adaptTo(QueryBuilder.class);
		// fetches Session
		Session session = adminResourceResolver.adaptTo(Session.class);

		// create query description as hash map (simplest way, same as form
		// post)
		Map<String, String> map = new HashMap<>();

		// create query description as hash map (simplest way, same as form
		// post)
		map.put("path", InformaticaConstants.TAXONOMY_ATTRIBUTE_AEM_LOCATION);
		map.put("type", InformaticaConstants.OSGI_NODE_TYPE);
		map.put("nodename", pdhnodeName);
		SearchResult results =null;
		if (queryBuilder != null) {
			query = queryBuilder.createQuery(PredicateGroup.create(map), session);
			results = query.getResult();
		}
		
		if (results != null && results.getHits().size() == 1) {
			// iterating over the results
			for (Hit hit : results.getHits()) {
				path = hit.getPath();
				LOG.info("Extension {} present in AEM at path {}", pdhnodeName, path);
				return path;
			}

		}
		return path;
	}

	/**
	 * This method is to create Node in JCR
	 * 
	 * @param adminResourceResolver
	 *            - ResourceResolver Object to resolve resource
	 * @param nodeName
	 *            - Node Name that need to create
	 * @param nodeParentNode
	 *            - Parent Node under which node need to create
	 * @param nodePath
	 *            - Path where Node to create
	 * @return - Return created Node
	 * @throws EatonApplicationException
	 */
	public static Node createNodeStructure(ResourceResolver adminResourceResolver, String nodeName, Node nodeParentNode,
			String nodePath) throws EatonApplicationException {

		Node pdhNode = null;

		try {
			Resource pdhNodeResource = adminResourceResolver.getResource(nodePath);

			if (pdhNodeResource != null && !ResourceUtil.isNonExistingResource(pdhNodeResource)) {
				nodeParentNode.getNode(nodeName).remove();
				pdhNode = nodeParentNode.addNode(nodeName, InformaticaConstants.OSGI_NODE_TYPE);
			} else
				pdhNode = nodeParentNode.addNode(nodeName, InformaticaConstants.OSGI_NODE_TYPE);

		} catch (RepositoryException e) {
			throw new EatonApplicationException(InformaticaConstants.IMPORT_FILE_ERROR_CODE, e.getMessage(), e);
		}
		LOG.info(" Node {} Created/Updated Succesfully.", nodePath);
		return pdhNode;

	}

	/**
	 * This method is to parse Taxonomy Attribute XML
	 * 
	 * @param adminService
	 * @return
	 * @throws EatonApplicationException
	 * @throws EatonSystemException
	 * @throws SAXException
	 * @throws FileNotFoundException
	 */
	private TAXONOMYATTRIBUTEITM parseTaxAttrXML(File taxAttrXML, AdminService adminService)
			throws EatonApplicationException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("parseTaxAttrXML Method" + InformaticaConstants.IMPORT_STARTED);
		}

		// New TaxonomyObject which will be returned
		TAXONOMYATTRIBUTEITM taxAttributes = new TAXONOMYATTRIBUTEITM();
		InputStream xsdInputStream = null;
		try (ResourceResolver adminResourceResolver = adminService.getReadService()){
			// New jaxbContext for parsing XML
			JAXBContext jaxbContext;
			jaxbContext = JAXBContext.newInstance(TAXONOMYATTRIBUTEITM.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			// tmp TaxonomyObject for adding to overall load object
			TAXONOMYATTRIBUTEITM tmptaxAttr = null;

			String filePath = InformaticaConstants.INFORMATICA_XSD_LOCATION
					.concat(InformaticaConstants.TAX_ATTR_XSD_FILE);

			// Getting XSD file from DAM
			xsdInputStream = InformaticaUtil.getXSDInputStreamfromDAM(adminResourceResolver, filePath);

			// create an instance of SchemaFactory to validate input XML against
			// XSD
			SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = sf.newSchema(new StreamSource(xsdInputStream));

			jaxbUnmarshaller.setSchema(schema);
			jaxbUnmarshaller.setEventHandler(new XMLValidator());

			// unmarshalling the current file to return the content tree
			tmptaxAttr = (TAXONOMYATTRIBUTEITM) jaxbUnmarshaller.unmarshal(taxAttrXML);

			if (tmptaxAttr != null) {
				taxAttributes.getROW().addAll(tmptaxAttr.getROW());
			}
		} catch (UnmarshalException  e) {

			LOG.error(InformaticaConstants.INVALID_FILE_FORMAT_ERROR_CODE,
					InformaticaConstants.INVALID_FILE_FORMAT_ERROR_MSG, e.getMessage());
			throw new EatonApplicationException(InformaticaConstants.INVALID_FILE_FORMAT_ERROR_CODE
					.concat(InformaticaConstants.HYPHEN_STRING).concat(taxAttrXML.getName()),
					InformaticaConstants.INVALID_FILE_FORMAT_ERROR_MSG, e);

		} catch (SAXException | JAXBException e) {
			LOG.error("JAXBException occured ", e.getMessage(), e);					
			throw new EatonApplicationException(InformaticaConstants.PARSING_ISSUE_ERROR_CODE
					.concat(InformaticaConstants.HYPHEN_STRING).concat(taxAttrXML.getName()),
					InformaticaConstants.PARSING_ISSUE_ERROR_MSG, e);			

		}
		LOG.info("XML file {} successfully Validated and parsed", taxAttrXML.getName());
		if (LOG.isDebugEnabled()) {
			LOG.debug("parseProductFamilyXML method" + InformaticaConstants.PROCESS_ENDED);
		}
		// Return the item unmarshalled to Taxonmoy Attribute object
		return taxAttributes;
	}

}
