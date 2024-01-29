package com.eaton.platform.integration.informatica.helper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.jcr.AccessDeniedException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;
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

import com.eaton.platform.core.exception.EatonApplicationException;
import com.eaton.platform.core.exception.EatonSystemException;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.integration.informatica.bean.InformaticaConfigServiceBean;
import com.eaton.platform.integration.informatica.bean.InformaticaResponse;
import com.eaton.platform.integration.informatica.constants.InformaticaConstants;
import com.eaton.platform.integration.informatica.models.productfamily.PRODUCTFAMILY;
import com.eaton.platform.integration.informatica.models.productfamily.PRODUCTFAMILY.ROW;
import com.eaton.platform.integration.informatica.models.productfamily.PRODUCTFAMILY.ROW.ITM;
import com.eaton.platform.integration.informatica.models.productfamily.PRODUCTFAMILY.ROW.ITM.ITMATRBT.ATRGRPTYPE;
import com.eaton.platform.integration.informatica.models.productfamily.PRODUCTFAMILY.ROW.ITM.ITMATRBT.ATRGRPTYPE.ATRGRP;
import com.eaton.platform.integration.informatica.models.productfamily.PRODUCTFAMILY.ROW.ITM.ITMATRBT.ATRGRPTYPE.ATRGRP.ATRMULTIROW;
import com.eaton.platform.integration.informatica.models.productfamily.PRODUCTFAMILY.ROW.ITM.ITMATRBT.ATRGRPTYPE.ATRGRP.ATRNM;
import com.eaton.platform.integration.informatica.util.InformaticaUtil;

/**
 * This class defines business logic to read Product Family Attributes from XML
 * file and then import them to AEM.
 * 
 * @author TCS
 * 
 */
public class InformaticaProductFamilyHelper {
	private static final Logger LOG = LoggerFactory.getLogger(InformaticaProductFamilyHelper.class);
	private static final String PROCESS_DATA = "processData";
	private static final String DOES_NOT_EXISTS = "does not exists.";
	private static final String LABEL_CONSTANT = "LABEL";

	/**
	 * This method is main method to call inner methods to process XML file
	 * content and return the final result
	 * 
	 * @param informaticaConfigServiceBean
	 * @param adminService
	 * @param lang
	 * @return
	 * @throws EatonApplicationException
	 * @throws IOException
	 */
	public InformaticaResponse processProductFamilyData(InformaticaConfigServiceBean informaticaConfigServiceBean,
			AdminService adminService, String lang) {

		LOG.debug(InformaticaConstants.IMPORT_STARTED);

		// start import timing
		long startTime = System.nanoTime();
		if (LOG.isInfoEnabled()) {
			LOG.info(PROCESS_DATA, "Starting product Family data process with startTime: " + startTime);
		}
		InformaticaResponse response = new InformaticaResponse();

		PRODUCTFAMILY productFamilyObject = null;
		List<String> successItemIDs = new ArrayList<>();
		List<String> failedItemIDs = new ArrayList<>();
		File statusFile = null;

		try {

			// Retrieved XML file that need to process
			File prodAttrXML = getEntryXMLFile(informaticaConfigServiceBean, lang);

			// Create SelectedTag Object
			productFamilyObject = parseProductFamilyXML(prodAttrXML, adminService);

			// List of Product Family records
			List<ROW> rowsList = productFamilyObject.getROW();
			if (rowsList != null && !rowsList.isEmpty()) {

				// Process the Product Family data
				processEntryData(adminService, response, successItemIDs, failedItemIDs, rowsList, lang);

			} else {
				LOG.info(InformaticaConstants.PARSING_ISSUE_ERROR_CODE, InformaticaConstants.PARSING_ISSUE_ERROR_MSG);
			}

			// create status.txt file
			statusFile = InformaticaUtil.updateStatus(successItemIDs, failedItemIDs, InformaticaConstants.STATUS_FILE_PDH);
			if (statusFile != null) {
				response.setStatusFile(statusFile);
				response.setStatusFilePath(statusFile.getCanonicalPath());
			}
			// Log import duration
			long endTime = System.nanoTime();
			long durationMilli = endTime - startTime;
			double durationSeconds = (double) durationMilli / 1000000000.0;
			if (LOG.isInfoEnabled()) {
				LOG.info("Ending Product Family Attribute data process with system time: " + endTime);
				LOG.info("The import took " + durationSeconds + " seconds.");
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
	 * This method is to process all the entries imported in productFamily XML
	 * 
	 * @param adminService
	 * @param response
	 * @param successItemIDs
	 * @param failedItemIDs
	 * @param rowsList
	 * @param lang
	 * @throws RepositoryException
	 */
	private void processEntryData(AdminService adminService, InformaticaResponse response, List<String> successItemIDs,
			List<String> failedItemIDs, List<ROW> rowsList, String lang) throws RepositoryException {
		// Method vars
		int updateItemsCount = 0;
		// ResourceResolver object to get Write Service
		try (ResourceResolver adminResourceResolver = adminService.getWriteService()){
			// First Checking for No incremental Data
			if (rowsList.size() == 1 && rowsList.get(0).getINVENTORYITEMID() == -9999) {
                LOG.info(InformaticaConstants.NO_DATA_TO_CHANGE);
            } else {
                Resource pdhRootFolderRes = adminResourceResolver
                        .getResource(InformaticaConstants.PRODUCT_FAMILY_AEM_LOCATION);
                if (pdhRootFolderRes != null && !ResourceUtil.isNonExistingResource(pdhRootFolderRes)) {
                    // Iterate For each Product Family record
                    for (ROW rowPDHItem : rowsList) {
                        updateItemsCount = importEntryDataIntoAEM(rowPDHItem, lang, successItemIDs, failedItemIDs,
                                adminService, response);
                    }
                } else {
                    LOG.info("Folder structure {} for Product Family Data does not exists",
                            InformaticaConstants.PRODUCT_FAMILY_AEM_LOCATION);
                    String failedEntry = Long.toString(rowsList.get(0).getINVENTORYITEMID())
                            .concat(InformaticaConstants.HYPHEN_STRING)
                            .concat(InformaticaConstants.TECHNICAL_ISSUE_ERROR_CODE);
                    failedItemIDs.add(failedEntry);
                    response.setProcessFlag(false);
                    response.setErrMsg(InformaticaConstants.TECHNICAL_ISSUE_ERROR_CODE.concat("_")
                            .concat(InformaticaConstants.TECHNICAL_ISSUE_ERROR_MSG));
                }
            }
			LOG.info(InformaticaConstants.TOTAL_ENTRIES_ADDEDTO_AEM, updateItemsCount);
		}
	}

	/**
	 * This method is to read Input XML file from Shared location on server.
	 * 
	 * @param informaticaConfigServiceBean
	 * @return
	 * @throws EatonApplicationException
	 * @throws IOException
	 */
	private File getEntryXMLFile(InformaticaConfigServiceBean informaticaConfigServiceBean, String lang)
			throws EatonApplicationException {
		LOG.debug(" Entered into getEntryXMLFile() method");

		String filepath;
		String fileName = InformaticaUtil.escapeXMLAppendLang(informaticaConfigServiceBean.getProductfamilyFileName(),
				lang);

		if (informaticaConfigServiceBean.getProductfamilyInputFilePath().endsWith(InformaticaConstants.BACKWARD_SLASH))
			filepath = informaticaConfigServiceBean.getProductfamilyInputFilePath();
		else
			filepath = informaticaConfigServiceBean.getProductfamilyInputFilePath() + File.separator;

		File files = new File(filepath);

		// Validate the current file path is accepted
		if (files.exists()) {

			// Validate at least one XML file exists for processing
			if (files.listFiles().length != 0) {

				File pdhfile = new File(filepath.concat(fileName));

				// Validate if required input product family file exists for
				// processing
				if (pdhfile.exists()) {
					LOG.info("File {} Found at shared location ", fileName);
					LOG.debug(" Existed getEntryXMLFile() method");
					return pdhfile;
				}

				else {

					// No Requested Input file found
					if (LOG.isInfoEnabled()) {
						LOG.info(InformaticaConstants.FILE_NOT_FOUND_ERROR_CODE
								.concat(InformaticaConstants.HYPHEN_STRING) + "{}", fileName);
					}
					throw new EatonSystemException(InformaticaConstants.FILE_NOT_FOUND_ERROR_CODE,
							InformaticaConstants.FILE_NOT_FOUND_ERROR_MSG);

				}

			} else {

				// No XML files found to process
				LOG.info(InformaticaConstants.NO_XML_ERROR_CODE, InformaticaConstants.NO_XML_ERROR_MSG);

				throw new EatonSystemException(InformaticaConstants.NO_XML_ERROR_CODE,
						InformaticaConstants.NO_XML_ERROR_MSG);

			}

		}

		else {
			if (LOG.isErrorEnabled()) {
				LOG.error("Input FilePath {}" + DOES_NOT_EXISTS, filepath);
			}
			throw new EatonApplicationException(InformaticaConstants.PATH_ERROR_CODE,
					InformaticaConstants.PATH_ERROR_MSG);
		}

	}

	/**
	 * This method is to create Node of any Type on the basis of given type for
	 * Product Family XML
	 * 
	 * @param adminResourceResolver
	 *            - ResourceResolver Object to resolve resource
	 * @param pdhnodeName
	 *            - Node Name that need to create
	 * @param pdhParentNode
	 *            - Parent Node under which node need to create
	 * @return - Return created Node
	 * @throws EatonApplicationException
	 */
	private Node createRootNodeStructure(ResourceResolver adminResourceResolver, String pdhnodeName, Node pdhParentNode,
			String nodeType) throws EatonApplicationException {

		Node pdhNode = null;
		String pdhNodePath;
		try {
			pdhNodePath = pdhParentNode.getPath() + InformaticaConstants.BACKWARD_SLASH + pdhnodeName;
			// Determine if Node to be created already exists or not
			if (ResourceUtil.isNonExistingResource(adminResourceResolver.resolve(pdhNodePath)))
				pdhNode = pdhParentNode.addNode(pdhnodeName, nodeType);
			else
				pdhNode = pdhParentNode.getNode(pdhnodeName);

		} catch (RepositoryException e) {

			throw new EatonApplicationException(InformaticaConstants.IMPORT_FILE_ERROR_CODE, e.getMessage(), e);
		}
		LOG.info(" Node {} Created Succesfully.", pdhNodePath);
		return pdhNode;

	}

	/**
	 * This method is to create Node of nt:unstructured type for Product Family
	 * XML
	 * 
	 * @param adminResourceResolver
	 *            - ResourceResolver Object to resolve resource
	 * @param pdhnodeName
	 *            - Node Name that need to create
	 * @param pdhParentNode
	 *            - Parent Node under which node need to create
	 * @param pdhNodePath
	 *            - Path where Node to create
	 * @return - Return created Node
	 * @throws EatonApplicationException
	 */
	private Node createNodeStructure(ResourceResolver adminResourceResolver, String pdhnodeName, Node pdhParentNode,
			String pdhNodePath) throws EatonApplicationException {

		Node pdhNode = null;

		try {

			// Determine if Node to be created already exists or not
			if (ResourceUtil.isNonExistingResource(adminResourceResolver.resolve(pdhNodePath)))
				pdhNode = pdhParentNode.addNode(pdhnodeName, InformaticaConstants.OSGI_NODE_TYPE);
			else
				pdhNode = pdhParentNode.getNode(pdhnodeName);

		} catch (RepositoryException e) {
			throw new EatonApplicationException(InformaticaConstants.IMPORT_FILE_ERROR_CODE, e.getMessage(), e);
		}
		LOG.info(" Node {} Created Succesfully.", pdhNodePath);
		return pdhNode;

	}

	/**
	 * This method is to create Inventory Item ID node for Product Family XML
	 * 
	 * @param adminResourceResolver
	 *            - ResourceResolver Object to resolve resource
	 * @param pdhParentNode
	 *            - Parent Node under which node need to create
	 * @param pdhNodePath
	 *            - Path where Node to create
	 * @return - Return created Node
	 * @throws EatonApplicationException
	 */
	private Node createInventoryNodeStructure(ResourceResolver adminResourceResolver, Node pdhParentNode,
			String pdhNodePath, Node rootNode) throws EatonApplicationException {

		Node pdhNode = null;

		try {
			// Parse the path to get the Inventory ID NodeName
			int lastSlash = pdhNodePath.lastIndexOf(InformaticaConstants.BACKWARD_SLASH);
			String inventoryIDNodeName = pdhNodePath.substring(lastSlash + 1);

			if (!ResourceUtil.isNonExistingResource(adminResourceResolver.resolve(pdhNodePath))) {
				pdhNode = pdhParentNode.getNode(inventoryIDNodeName);
   				deleteInventoryChildNodes(pdhNode);
			} else {
				// If it is first time load then check if child Nodes exists
				// under rootNode i.e. /var/commerce/pdh/product-family/
				// then check for other condition otherwise directly add
				// Inventory Item ID Nodes
				if (rootNode.hasNodes()) {
					pdhNode = checkInventoryNode(adminResourceResolver, rootNode, pdhParentNode, inventoryIDNodeName);
				} else {
					pdhNode = pdhParentNode.addNode(inventoryIDNodeName, InformaticaConstants.OSGI_NODE_TYPE);
				}
			}

		} catch (RepositoryException e) {
			throw new EatonApplicationException(InformaticaConstants.IMPORT_FILE_ERROR_CODE, e.getMessage(), e);
		} catch (Exception e) {
			throw new EatonApplicationException(InformaticaConstants.IMPORT_FILE_ERROR_CODE, e.getMessage(), e);
		}
		LOG.info(" Node {} Created/Updated Succesfully.", pdhNodePath);
		return pdhNode;

	}

	/**
	 * This method is to delete existing Inventory Node
	 * 
	 * @param pdhNode
	 * @throws RepositoryException
	 * 
	 */
	private void deleteInventoryChildNodes(Node pdhNode) throws RepositoryException, VersionException, LockException,
			ConstraintViolationException, AccessDeniedException {
		NodeIterator nodeIterator = pdhNode.getNodes();
		while (nodeIterator.hasNext()) {
			Node nodeTodelete = nodeIterator.nextNode();		
			PropertyIterator proprtyIterator = nodeTodelete.getProperties(InformaticaConstants.JCR_TITLE);         
			if (!proprtyIterator.hasNext()) {
				// deleting all the existing Nodes
				nodeTodelete.remove();
			} 
		}
	}

	/**
	 * This method is to checkInventoryNode at all possible paths in JCR
	 * 
	 * @param adminResourceResolver
	 * @param pdhParentNode
	 * @param rootNode
	 * @param inventoryIDNodeName
	 * @return Inventory Node
	 * @throws RepositoryException
	 * @throws EatonApplicationException
	 */
	private Node checkInventoryNode(ResourceResolver adminResourceResolver, Node rootNode, Node pdhParentNode,
			String inventoryIDNodeName) throws RepositoryException, EatonApplicationException {

		Node pdhNode = null;

		// Check for inventory-id node using AEM search
		if (!InformaticaUtil.isExistingInventoryIDNode(adminResourceResolver, inventoryIDNodeName,
				rootNode.getPath())) {
			pdhNode = pdhParentNode.addNode(inventoryIDNodeName, InformaticaConstants.OSGI_NODE_TYPE);
		} else {
			LOG.info("Inventory Item ID {} already present", inventoryIDNodeName);
			throw new EatonApplicationException("Inventory Item ID" + inventoryIDNodeName + "already present in AEM");

		}

		return pdhNode;
	}

	/**
	 * This method is to set Inventory ID Node properties
	 * 
	 * @param pdhinventoryIDNode
	 *            - Inventory ID Node
	 * @param pdhrow
	 *            - Row Object to get properties Values
	 * @throws EatonApplicationException
	 */
	private void seInventorytNodeProperties(Node pdhinventoryIDNode, ROW pdhrow) throws EatonApplicationException {

		try {
			if (pdhinventoryIDNode != null) {
				pdhinventoryIDNode.setProperty("LAST_UPDATE_DATE", pdhrow.getLASTUPDATEDATE());
				pdhinventoryIDNode.setProperty("STATUS", pdhrow.getSTATUS());
				pdhinventoryIDNode.setProperty("INSTANCE_NAME", pdhrow.getInstancename());
				pdhinventoryIDNode.setProperty("name",Long.toString(pdhrow.getINVENTORYITEMID()));
				ITM itm = pdhrow.getITM();
				if (itm != null) {

					pdhinventoryIDNode.setProperty("DESCRIPTION", itm.getDESCRIPTION());
					pdhinventoryIDNode.setProperty("UOMCODE", itm.getUOMCODE());
					pdhinventoryIDNode.setProperty("ITEMSTATUS", itm.getITEMSTATUS());
					pdhinventoryIDNode.setProperty(InformaticaConstants.JCR_TITLE, itm.getDESCRIPTION());

				}
			}
		} catch (RepositoryException e) {
			throw new EatonApplicationException(InformaticaConstants.IMPORT_FILE_ERROR_CODE, e.getMessage(), e);

		}

	}

	/**
	 * This method is to set GrpNode properties
	 * 
	 * @param pdhgrpNode
	 *            - Group Node
	 * @param atrgrp
	 *            - ATRGRP Object to get properties Values
	 * @throws EatonApplicationException
	 */
	private void setGrpNodeProperties(Node pdhgrpNode, ATRGRP atrgrp) throws EatonApplicationException {

		try {
			if (pdhgrpNode != null) {
				// Setting Group Node Properties
				pdhgrpNode.setProperty(LABEL_CONSTANT, atrgrp.getLabel());
				pdhgrpNode.setProperty("SUBTYPE", atrgrp.getSUBTYPE());

			}
		} catch (RepositoryException e) {
			throw new EatonApplicationException(InformaticaConstants.IMPORT_FILE_ERROR_CODE, e.getMessage(), e);

		}

	}

	/**
	 * This method is to set ATRNMNode properties
	 * 
	 * @param pdhatrnmNode
	 *            -ATRNMNode node
	 * @param atrnm
	 *            - ATRNM Object to get properties Values
	 * @throws EatonApplicationException
	 */
	private void setATRNMNodeProperties(Node pdhatrnmNode, ATRNM atrnm) throws EatonApplicationException {

		try {
			if (pdhatrnmNode != null) {
				pdhatrnmNode.setProperty(LABEL_CONSTANT, atrnm.getLabel());
				pdhatrnmNode.setProperty("ValueCQDATA", atrnm.getValue());
				pdhatrnmNode.setProperty("COUNTRY", atrnm.getCountry());
				pdhatrnmNode.setProperty("DISPLAY_FLAG", atrnm.getDisplayFlag());
				pdhatrnmNode.setProperty("LANGUAGE", atrnm.getLanguage());
				pdhatrnmNode.setProperty("UOM", atrnm.getUOM());
				pdhatrnmNode.setProperty("Value", atrnm.getValueAttribute());
				if (atrnm.getCID() != null)
					pdhatrnmNode.setProperty("CID", atrnm.getCID());
				if (atrnm.getREPFL() != null){
					pdhatrnmNode.setProperty("REPFL", atrnm.getREPFL());
				}
			}
		} catch (RepositoryException e) {
			throw new EatonApplicationException(InformaticaConstants.IMPORT_FILE_ERROR_CODE, e.getMessage(), e);

		}

	}

	/**
	 * This method is to set ATRMultiNMNode properties
	 * 
	 * @param pdhatrmultiNMNode
	 *            - ATRMultiNMNode
	 * @param atrnmMultirow
	 *            -ATRMULTIROW.ATRNM Object to get properties Values
	 * @throws EatonApplicationException
	 */
	private void setATRMultiNMNodeProperties(Node pdhatrmultiNMNode, ATRMULTIROW.ATRNM atrnmMultirow)
			throws EatonApplicationException {

		try {
			if (pdhatrmultiNMNode != null) {
				pdhatrmultiNMNode.setProperty(LABEL_CONSTANT, atrnmMultirow.getLabel());
				pdhatrmultiNMNode.setProperty("ValueCQDATA", atrnmMultirow.getValue());
				pdhatrmultiNMNode.setProperty("Value", atrnmMultirow.getValueAttribute());
				//pdhatrmultiNMNode.setProperty("Sequence", atrnmMultirow.getSEQ());

			}
		} catch (RepositoryException e) {
			throw new EatonApplicationException(InformaticaConstants.IMPORT_FILE_ERROR_CODE, e.getMessage(), e);

		}

	}

	/**
	 * This method creates Inventory Item ID node.
	 * 
	 * @param adminResourceResolver
	 * @param pdhrow
	 * @return
	 * @throws EatonApplicationException
	 * @throws ConstraintViolationException
	 * @throws LockException
	 * @throws VersionException
	 * @throws AccessDeniedException
	 * @throws RepositoryException
	 */
	Node createInventoryItemNode(ResourceResolver adminResourceResolver, ROW pdhrow, Node rootNode, String lang)
			throws EatonApplicationException, RepositoryException {

		String pdhTradeNameNodePath;
		String pdhSubBrandNodePath;
		String pdhBrandNodePath;
		Node pdhTradeNameNode = null;
		Node pdhSubBrandNode = null;
		Node pdhBrandNode = null;
		Node pdhinventoryIDNode = null;
		Node pdhLangNode = null;

		try {

			// creating lang Node
			String langNodePath = InformaticaConstants.PRODUCT_FAMILY_AEM_LOCATION + lang.toLowerCase()
					+ InformaticaConstants.BACKWARD_SLASH;
			pdhLangNode = createRootNodeStructure(adminResourceResolver, lang.toLowerCase(), rootNode,
					InformaticaConstants.UNSTRUCTURED_FOLDER_TYPE);

			// creating Brand Node
			String[] brandList = InformaticaUtil.getLowercaseEscapeSpace(pdhrow.getPRODUCTBRAND())
					.split(InformaticaConstants.COMMA_STRING);
			if (InformaticaUtil.isAllStringSame(brandList) && !brandList[0].equalsIgnoreCase("none")) {
				pdhBrandNodePath = langNodePath + brandList[0];
				pdhBrandNode = createRootNodeStructure(adminResourceResolver, brandList[0], pdhLangNode,
						InformaticaConstants.OSGI_NODE_TYPE);
			} else {
				pdhBrandNodePath = langNodePath + InformaticaConstants.NONE_NODE;
				pdhBrandNode = createRootNodeStructure(adminResourceResolver, InformaticaConstants.NONE_NODE,
						pdhLangNode, InformaticaConstants.OSGI_NODE_TYPE);
			}

			// creating SubBrand Node
			String[] subBrandList = InformaticaUtil.getLowercaseEscapeSpace(pdhrow.getPRODUCTSUBBRAND())
					.split(InformaticaConstants.COMMA_STRING);
			if (InformaticaUtil.isAllStringSame(subBrandList) && !subBrandList[0].equalsIgnoreCase("none")) {
				pdhSubBrandNodePath = pdhBrandNodePath + InformaticaConstants.BACKWARD_SLASH + subBrandList[0];
				pdhSubBrandNode = createNodeStructure(adminResourceResolver, subBrandList[0], pdhBrandNode,
						pdhSubBrandNodePath);
			} else {
				pdhSubBrandNodePath = pdhBrandNodePath + InformaticaConstants.BACKWARD_SLASH
						+ InformaticaConstants.NONE_NODE;
				pdhSubBrandNode = createNodeStructure(adminResourceResolver, InformaticaConstants.NONE_NODE,
						pdhBrandNode, pdhSubBrandNodePath);
			}

			// creating Trade Name Node
			String[] tradeNameList = InformaticaUtil.getLowercaseEscapeSpace(pdhrow.getPRODUCTTRADENAME())
					.split(InformaticaConstants.COMMA_STRING);
			if (InformaticaUtil.isAllStringSame(tradeNameList) && !tradeNameList[0].equalsIgnoreCase("none")) {
				pdhTradeNameNodePath = pdhSubBrandNodePath + InformaticaConstants.BACKWARD_SLASH + tradeNameList[0];
				pdhTradeNameNode = createNodeStructure(adminResourceResolver, tradeNameList[0], pdhSubBrandNode,
						pdhTradeNameNodePath);
			} else {
				pdhTradeNameNodePath = pdhSubBrandNodePath + InformaticaConstants.BACKWARD_SLASH
						+ InformaticaConstants.NONE_NODE;
				pdhTradeNameNode = createNodeStructure(adminResourceResolver, InformaticaConstants.NONE_NODE,
						pdhSubBrandNode, pdhTradeNameNodePath);
			}

			// Creating InventoryID Node
			String pdhinventoryIDNodePath = pdhTradeNameNodePath + InformaticaConstants.BACKWARD_SLASH
					+ pdhrow.getINVENTORYITEMID();
			pdhinventoryIDNode = createInventoryNodeStructure(adminResourceResolver, pdhTradeNameNode,
					pdhinventoryIDNodePath, pdhLangNode);

		} catch (Exception e) {
			throw new EatonApplicationException(InformaticaConstants.IMPORT_FILE_ERROR_CODE, e.getMessage(), e);
		} finally {
			if (pdhTradeNameNode != null && pdhTradeNameNode.isNode() && !pdhTradeNameNode.hasNodes())
				pdhTradeNameNode.remove();
			if (pdhSubBrandNode != null && pdhSubBrandNode.isNode() && !pdhSubBrandNode.hasNodes())
				pdhSubBrandNode.remove();
			if (pdhBrandNode != null && pdhBrandNode.isNode() && !pdhBrandNode.hasNodes())
				pdhBrandNode.remove();
			if (pdhLangNode != null && pdhLangNode.isNode() && !pdhLangNode.hasNodes())
				pdhLangNode.remove();
		}

		return pdhinventoryIDNode;

	}

	/**
	 * This Method is to create Nodes in JCR for respective data from XMl
	 * 
	 * @param rowPDHItem
	 * @param lang
	 * @param successItemIDs
	 * @param failedItemIDs
	 * @param adminService
	 * @param response
	 * @return
	 */
	private int importEntryDataIntoAEM(ROW rowPDHItem, String lang, List<String> successItemIDs,
			List<String> failedItemIDs, AdminService adminService, InformaticaResponse response) {

		ROW pdhrow = rowPDHItem;
		int count = response.getUpdateItemsCount();
		try (ResourceResolver adminResourceResolver = adminService.getWriteService()) {
				LOG.info("Iteration of record Started");
				LOG.info("Inventory ITEM Id for itreated ROW IS: " + pdhrow.getINVENTORYITEMID());

				// get resource resolver object from admin service

				Node pdhRootNode;
				Resource pdhRootFolderRes = adminResourceResolver
						.getResource(InformaticaConstants.PRODUCT_FAMILY_AEM_LOCATION);
				pdhRootNode = pdhRootFolderRes.adaptTo(Node.class);
				Session session = pdhRootNode.getSession();

				// Method to have Inventory Item ID node
				Node pdhinventoryIDNode = createInventoryItemNode(adminResourceResolver, pdhrow, pdhRootNode, lang);

				if (pdhinventoryIDNode != null) {

					String pdhinventoryIDNodePath = pdhinventoryIDNode.getPath();
					// Setting Inventory ID Node Properties
					seInventorytNodeProperties(pdhinventoryIDNode, pdhrow);

					// Creating Extension ID Node
					String[] extensionIdList = pdhrow.getEXTENSIONID().split(InformaticaConstants.COMMA_STRING);

					// Creating Group Type Nodes
					ITM.ITMATRBT itmatrbt = pdhrow.getITM().getITMATRBT();
					if (itmatrbt != null) {

						List<ATRGRPTYPE> atrgrptypeList = itmatrbt.getATRGRPTYPE();
						if (atrgrptypeList != null && !atrgrptypeList.isEmpty()) {
							for (ATRGRPTYPE atrgrptype : atrgrptypeList) {
	
								String pdhgrpTypeNodePath = pdhinventoryIDNodePath + InformaticaConstants.BACKWARD_SLASH
										+ InformaticaUtil.getLowercaseEscapeSpace(atrgrptype.getId());
								String pdhgrpTypeNodeName = InformaticaUtil.getLowercaseEscapeSpace(atrgrptype.getId());
								Node pdhgrpTypeNode = createNodeStructure(adminResourceResolver, pdhgrpTypeNodeName,
										pdhinventoryIDNode, pdhgrpTypeNodePath);
								// Setting Grp Type Node Properties
								pdhgrpTypeNode.setProperty(LABEL_CONSTANT, atrgrptype.getLabel());

								// Creating Group Nodes
								List<ATRGRP> atrgrpList = atrgrptype.getATRGRP();
								if (atrgrpList != null && !atrgrpList.isEmpty()) {
									for (ATRGRP atrgrp : atrgrpList) {
										if(!atrgrp.getId().equals("XXPDH_PRD_FM_AG")){
											LOG.info("In If case "+atrgrp.getId());
											String pdhgrpNodePath = pdhgrpTypeNodePath + InformaticaConstants.BACKWARD_SLASH
													+ InformaticaUtil.getLowercaseEscapeSpace(atrgrp.getId());
											String pdhgrpNodeName = InformaticaUtil.getLowercaseEscapeSpace(atrgrp.getId());
											Node pdhgrpNode = createNodeStructure(adminResourceResolver, pdhgrpNodeName,
													pdhgrpTypeNode, pdhgrpNodePath);
											// Setting Group Node Properties
											setGrpNodeProperties(pdhgrpNode, atrgrp);
	
											// Creating ATRNM Node
											List<ATRNM> atrnmList = atrgrp.getATRNM();
											if (atrnmList != null && !atrnmList.isEmpty()) {
												for (ATRNM atrnm : atrnmList) {
													String pdhatrnmNodePath = pdhgrpNodePath
															+ InformaticaConstants.BACKWARD_SLASH
															+ InformaticaUtil.getLowercaseEscapeSpace(atrnm.getId());
													String pdhatrnmNodeName = InformaticaUtil
															.getLowercaseEscapeSpace(atrnm.getId());
													Node pdhatrnmNode = createNodeStructure(adminResourceResolver,
															pdhatrnmNodeName, pdhgrpNode, pdhatrnmNodePath);
	
													// Setting ATRNM Node Properties
													setATRNMNodeProperties(pdhatrnmNode, atrnm);
	
												}
											}

											// Creating ATRMULTIROW (Extension
											// ID)Node
											List<ATRMULTIROW> atrmultirowList = atrgrp.getATRMULTIROW();
											if (atrmultirowList != null && !atrmultirowList.isEmpty()) {							
											
												for (ATRMULTIROW atrmultirow : atrmultirowList) {
													String pdhatrmultiNodePath = pdhgrpNodePath
															+ InformaticaConstants.BACKWARD_SLASH + atrmultirow.getROWID();
													String pdhatrmultiNodeName = Long.toString(atrmultirow.getROWID());
													
													Node pdhatrmultiNode = createNodeStructure(adminResourceResolver,
															pdhatrmultiNodeName, pdhgrpNode, pdhatrmultiNodePath);
													if(atrmultirow.getSEQ() != null){
														pdhatrmultiNode.setProperty("Sequence", atrmultirow.getSEQ());
													}
													// Creating ATRNM MULTIROW Node
													List<ITM.ITMATRBT.ATRGRPTYPE.ATRGRP.ATRMULTIROW.ATRNM> atrnmMultirowList = atrmultirow
															.getATRNM();
													if (atrnmMultirowList != null && !atrnmMultirowList.isEmpty()) {
														for (ATRMULTIROW.ATRNM atrnmMultirow : atrnmMultirowList) {
	
															String pdhatrmultiNMNodePath = pdhatrmultiNodePath
																	+ InformaticaConstants.BACKWARD_SLASH + InformaticaUtil
																			.getLowercaseEscapeSpace(atrnmMultirow.getId());
															String pdhatrmultiNMNodeName = InformaticaUtil
																	.getLowercaseEscapeSpace(atrnmMultirow.getId());
															Node pdhatrmultiNMNode = createNodeStructure(
																	adminResourceResolver, pdhatrmultiNMNodeName,
																	pdhatrmultiNode, pdhatrmultiNMNodePath);
															// Setting ATRNM
															// MULTIROW Properties
															setATRMultiNMNodeProperties(pdhatrmultiNMNode, atrnmMultirow);
														}
													}
												}
											}
										} else
										{	
											// Creating ATRMULTIROW (Extension
											// ID)Node
											List<ATRMULTIROW> atrmultirowList = atrgrp.getATRMULTIROW();
											if (atrmultirowList != null && !atrmultirowList.isEmpty()) {
												// Match Extension ID
												checkExistingExtensionIDs(pdhinventoryIDNode, extensionIdList);
												for (ATRMULTIROW atrmultirow : atrmultirowList) {
													String pdhatrmultiNodePath = pdhinventoryIDNodePath
															+ InformaticaConstants.BACKWARD_SLASH + atrmultirow.getROWID();
													String pdhatrmultiNodeName = Long.toString(atrmultirow.getROWID());
													LOG.info("Extension Id "+atrmultirow.getROWID()+" "+pdhatrmultiNodePath);
													Node pdhatrmultiNode = createNodeStructure(adminResourceResolver,
															pdhatrmultiNodeName, pdhinventoryIDNode, pdhatrmultiNodePath);
													if(atrmultirow.getSEQ() != null){
														pdhatrmultiNode.setProperty("Sequence", atrmultirow.getSEQ());
													}
													// Creating ATRNM MULTIROW Node
													List<ITM.ITMATRBT.ATRGRPTYPE.ATRGRP.ATRMULTIROW.ATRNM> atrnmMultirowList = atrmultirow
															.getATRNM();
													Property productName = null;
													if (atrnmMultirowList != null && !atrnmMultirowList.isEmpty()) {
														for (ATRMULTIROW.ATRNM atrnmMultirow : atrnmMultirowList) {
	
															String pdhatrmultiNMNodePath = pdhatrmultiNodePath
																	+ InformaticaConstants.BACKWARD_SLASH + InformaticaUtil
																			.getLowercaseEscapeSpace(atrnmMultirow.getId());
															String pdhatrmultiNMNodeName = InformaticaUtil
																	.getLowercaseEscapeSpace(atrnmMultirow.getId());
															Node pdhatrmultiNMNode = createNodeStructure(
																	adminResourceResolver, pdhatrmultiNMNodeName,
																	pdhatrmultiNode, pdhatrmultiNMNodePath);
															// Setting ATRNM
															// MULTIROW Properties
															setATRMultiNMNodeProperties(pdhatrmultiNMNode, atrnmMultirow);
															if(atrnmMultirow.getId().equals("Product_Name")){
																productName = pdhatrmultiNMNode.getProperty("ValueCQDATA");
															}
														}
														if(productName!=null){
															LOG.info("In Second Else case "+productName);
															pdhatrmultiNode.setProperty(InformaticaConstants.JCR_TITLE, productName.getValue().toString());
														}
													}
												}
											}
										}	
									}
								}
							}
						}
					}
				}

				session.save();
				// Added Inventory Item ID to successItemlist
				LOG.info("Iteration of record Ended");
				successItemIDs.add(Long.toString(pdhrow.getINVENTORYITEMID())
						.concat(InformaticaConstants.HYPHEN_STRING).concat(InformaticaConstants.SUCCESS_CODE));
				count++;
				response.setUpdateItemsCount(count);

			

		} catch (EatonApplicationException | EatonSystemException e) {
			
			String failedEntry ;
			if( e.getMessage().contains(InformaticaConstants.EXTENSION_ID_ERROR_CODE)){
				 failedEntry = Long.toString(pdhrow.getINVENTORYITEMID())
						.concat(InformaticaConstants.HYPHEN_STRING).concat(InformaticaConstants.EXTENSION_ID_ERROR_CODE);			
				response.setErrMsg(InformaticaConstants.EXTENSION_ID_ERROR_CODE + InformaticaConstants.UNDERSCORE
						+ InformaticaConstants.EXTENSION_ID_ERROR_MSG);
			} else{
				LOG.error(InformaticaConstants.IMPORT_FILE_ERROR_CODE.concat("_")
						.concat(Long.toString(pdhrow.getINVENTORYITEMID()))
						.concat(InformaticaConstants.IMPORT_FILE_ERROR_MSG), e.getMessage(), e);
			 failedEntry = Long.toString(pdhrow.getINVENTORYITEMID())
					.concat(InformaticaConstants.HYPHEN_STRING).concat(InformaticaConstants.IMPORT_FILE_ERROR_CODE);			
			response.setErrMsg(InformaticaConstants.IMPORT_FILE_ERROR_CODE + InformaticaConstants.UNDERSCORE
					+ InformaticaConstants.IMPORT_FILE_ERROR_MSG);
			}
			failedItemIDs.add(failedEntry);
			response.setProcessFlag(false);

		}

		catch (Exception e) {
			LOG.error(InformaticaConstants.IMPORT_FILE_ERROR_CODE.concat("_")
					.concat(Long.toString(pdhrow.getINVENTORYITEMID()))
					.concat(InformaticaConstants.IMPORT_FILE_ERROR_MSG), e.getMessage(), e);
			String failedEntry = Long.toString(pdhrow.getINVENTORYITEMID())
					.concat(InformaticaConstants.HYPHEN_STRING).concat(e.getMessage());
			failedItemIDs.add(failedEntry);
			response.setProcessFlag(false);
			response.setErrMsg(e.getMessage());
		}
		return count;

	}

	/**
	 * This method is to match existing extensionIDs with new extensionIDs
	 *
	 * @param extensionIDList
	 * @param pdhinventoryIDNode
	 * @throws Exception
	 * @throws RepositoryException
	 * @throws EatonApplicationException
	 */
	
	private void checkExistingExtensionIDs(Node pdhinventoryIDNode, String[] extensionIDList )
			throws RepositoryException, EatonApplicationException {
		String inventoryNode= pdhinventoryIDNode.getName();
		try {
			
			if ( pdhinventoryIDNode.hasNodes()) {
				boolean flag;
				List<String> extensionNodes = new ArrayList<>();				
				NodeIterator nodesIterator = pdhinventoryIDNode.getNodes();				
				while (nodesIterator.hasNext()) {
					Node nodetoAdd = nodesIterator.nextNode();					
					PropertyIterator proprtyIterator = nodetoAdd.getProperties(InformaticaConstants.JCR_TITLE); 
					if (proprtyIterator.hasNext() ) {		          
					        extensionNodes.add(nodetoAdd.getName());
					}
				}
				flag = Arrays.asList(extensionIDList).containsAll(extensionNodes);

				if (!flag) {
				    pdhinventoryIDNode.remove();
					throw new RepositoryException(
							InformaticaConstants.EXTENSION_ID_ERROR_CODE + InformaticaConstants.EXTENSION_ID_ERROR_MSG);
				}
			}
		} catch (RepositoryException e) {
			LOG.error(InformaticaConstants.EXTENSION_ID_ERROR_CODE.concat("_")
					.concat(inventoryNode).concat(InformaticaConstants.EXTENSION_ID_ERROR_MSG), e.getMessage(), e);		
			throw new EatonApplicationException("Exception occured ", e.getMessage());

		}
	}

	/**
	 * This method is to parse Product Family Attribute XML
	 * 
	 * @param prodAttrXML
	 *            - Product Family XML file to Unmarshall
	 * @param adminService
	 *            - Adminservice Object to resolve resource(XSD) from DAM
	 * @return Unmarshalled SelectedTag Object
	 * @throws EatonApplicationException
	 * @throws EatonSystemException
	 */
	private PRODUCTFAMILY parseProductFamilyXML(File prodAttrXML, AdminService adminService)
			throws EatonApplicationException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("parseProductFamilyXML method" + InformaticaConstants.IMPORT_STARTED);
		}
		// New productFamilyObject which will be returned
		PRODUCTFAMILY productFamily = new PRODUCTFAMILY();
		InputStream xsdInputStream = null;
		try (ResourceResolver adminResourceResolver = adminService.getReadService()){
			// New jaxbContext for parsing XML
			JAXBContext jaxbContext;
			jaxbContext = JAXBContext.newInstance(PRODUCTFAMILY.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			// tmp product family for adding to overall load object
			PRODUCTFAMILY tmpproductFamily = null;

			// get resource resolver object from admin service
			String filePath = InformaticaConstants.INFORMATICA_XSD_LOCATION
					.concat(InformaticaConstants.PRODUCT_FAMILY_XSD_FILE);

			// Getting Product Family XSD file from DAM
			xsdInputStream = InformaticaUtil.getXSDInputStreamfromDAM(adminResourceResolver, filePath);

			// create an instance of SchemaFactory to validate input XML
			// against XSD
			SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = sf.newSchema(new StreamSource(xsdInputStream));
			jaxbUnmarshaller.setSchema(schema);
			jaxbUnmarshaller.setEventHandler(new XMLValidator());

			// unmarshalling the current file to return the content tree
			tmpproductFamily = (PRODUCTFAMILY) jaxbUnmarshaller.unmarshal(prodAttrXML);

			if (tmpproductFamily != null) {
				productFamily.getROW().addAll(tmpproductFamily.getROW());
			}

		} catch (UnmarshalException e) {

			LOG.error(InformaticaConstants.INVALID_FILE_FORMAT_ERROR_CODE,
					InformaticaConstants.INVALID_FILE_FORMAT_ERROR_MSG);
			throw new EatonApplicationException(InformaticaConstants.INVALID_FILE_FORMAT_ERROR_CODE
					.concat(InformaticaConstants.HYPHEN_STRING).concat(prodAttrXML.getName()),
					InformaticaConstants.INVALID_FILE_FORMAT_ERROR_MSG, e);

		} catch (SAXException | JAXBException e) {
			     LOG.error("JAXBException occured ", e.getMessage(), e);					
			throw new EatonApplicationException(InformaticaConstants.PARSING_ISSUE_ERROR_CODE
					.concat(InformaticaConstants.HYPHEN_STRING).concat(prodAttrXML.getName()),
					InformaticaConstants.PARSING_ISSUE_ERROR_MSG, e);
		}

		LOG.info("XML file {} successfully Validated and parsed", prodAttrXML.getName());
		if (LOG.isDebugEnabled()) {
			LOG.debug("parseProductFamilyXML method" + InformaticaConstants.PROCESS_ENDED);
		}
		// Return the item unmarshalled to item object
		return productFamily;
	}

	
}
