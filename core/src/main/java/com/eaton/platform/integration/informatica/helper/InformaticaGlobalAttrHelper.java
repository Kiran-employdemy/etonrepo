package com.eaton.platform.integration.informatica.helper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
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
import com.eaton.platform.integration.informatica.models.globalattributes.GLOBALATTRIBUTEITM;
import com.eaton.platform.integration.informatica.models.globalattributes.GLOBALATTRIBUTEITM.ROW;
import com.eaton.platform.integration.informatica.util.InformaticaUtil;

/**
 * This class defines business logic to read Global Attributes from XML file and
 * then import them to AEM.
 * 
 * @author TCS
 * 
 */

public class InformaticaGlobalAttrHelper {
	private static final Logger LOG = LoggerFactory.getLogger(InformaticaGlobalAttrHelper.class);

	private static final String DOES_NOT_EXISTS = "does not exists.";

	/**
	 * This method is main method to all inner methods to process XML file
	 * content
	 * 
	 * @param informaticaConfigServiceBean
	 * @param adminService
	 * @return
	 * @throws EatonApplicationException
	 * @throws IOException
	 */
	public InformaticaResponse processGlobalAttrData(InformaticaConfigServiceBean informaticaConfigServiceBean,
			AdminService adminService) throws EatonApplicationException {

		LOG.debug(InformaticaConstants.IMPORT_STARTED);
		// start import timing
		long startTime = System.nanoTime();
		if (LOG.isInfoEnabled()) {
			LOG.info("Starting Global Attribute data process with system time: " + startTime);
		}
		// Method vars for managing import
		InformaticaResponse response = new InformaticaResponse();
		List<String> successItemIDs = new ArrayList<>();
		List<String> failedItemIDs = new ArrayList<>();
		File statusFile = null;
		GLOBALATTRIBUTEITM globalttrObject = null;
		

		try {

			// Retrieved XML file that need to process
			File globalAttrXML = getEntryXMLFile(informaticaConfigServiceBean);

			// Create GlobalAttribute Object
			globalttrObject = parseGlobalAttrXML(globalAttrXML, adminService);

			// List of Global Attribute records
			List<ROW> rowsList = globalttrObject.getROW();

			if (rowsList != null && !rowsList.isEmpty()) {

				processEntryData(adminService, response, successItemIDs, failedItemIDs, rowsList);

			} else {
				LOG.info(InformaticaConstants.PARSING_ISSUE_ERROR_CODE, InformaticaConstants.PARSING_ISSUE_ERROR_MSG);

			}

			// create status.txt file
			statusFile = InformaticaUtil.updateStatus(successItemIDs, failedItemIDs,InformaticaConstants.STATUS_FILE_GLOBAL);
			if (statusFile != null) {
				response.setStatusFile(statusFile);
				response.setStatusFilePath(statusFile.getCanonicalPath());
			}

			// Log import duration
			long endTime = System.nanoTime();
			long durationMilli = endTime - startTime;
			double durationSeconds = (double) durationMilli / 1000000000.0;
			if (LOG.isInfoEnabled()) {
				LOG.info("Ending Global Attribute data process with system time: " + endTime);
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
	 * This method starts processing of Data coming in XML
	 * 
	 * @param adminService
	 * @param response
	 * @param successItemIDs
	 * @param failedItemIDs
	 * @param rowsList
	 * @throws RepositoryException
	 */
	private void processEntryData(AdminService adminService, InformaticaResponse response, List<String> successItemIDs,
			List<String> failedItemIDs, List<ROW> rowsList) {

		try (ResourceResolver adminResourceResolver = adminService.getWriteService()){
			// Method vars
			int updateItemsCount = 0;
			// ResourceResolver object to get Write Service
			// First Checking for No incremental Data
			if (rowsList.size() == 1 && rowsList.get(0).getATTRNAME().equals("-9999")) {
                LOG.info(InformaticaConstants.NO_DATA_TO_CHANGE);
            } else {

                // Iterate For each Global Attribute record
                for (ROW rowglobalAttr : rowsList) {
                    // Import the Global Attribute data
                    updateItemsCount = importEntryDataIntoAEM(rowglobalAttr, successItemIDs, failedItemIDs,
                            adminResourceResolver, response);
                }
            }
			LOG.info(InformaticaConstants.TOTAL_ENTRIES_ADDEDTO_AEM, updateItemsCount);
		}
	}

	/**
	 * This Method is to create Nodes in JCR for respective data from XMl
	 * 
	 * @param rowglobalAttr
	 * @param successItemIDs
	 * @param failedItemIDs
	 * @param updateItemsCount
	 * @param adminResourceResolver2
	 * @param isprocessFlag
	 * @return
	 */
	private int importEntryDataIntoAEM(ROW rowglobalAttr, List<String> successItemIDs, List<String> failedItemIDs,
			ResourceResolver adminResourceResolver, InformaticaResponse response) {
		ROW relatedRow = rowglobalAttr;
		int count = response.getUpdateItemsCount();
		try {

			if (relatedRow != null) {

				LOG.info("Iteration of record Started");
				LOG.info("Global Attribute Name for itreated ROW IS: " + relatedRow.getATTRNAME());

				// Logic to create Node structure for each record of
				// Global_Attribute.xml
				Resource globalAttrFolderRes = adminResourceResolver
						.getResource(InformaticaConstants.GLOBAL_ATTRIBUTE_AEM_LOCATION);
				if (globalAttrFolderRes != null && !ResourceUtil.isNonExistingResource(globalAttrFolderRes)) {
					Node globalAttRootNode = globalAttrFolderRes.adaptTo(Node.class);

					// Determine path to check Node to be created already exists
					// or not
					String globalAttrNodePath = InformaticaConstants.GLOBAL_ATTRIBUTE_AEM_LOCATION
							+ InformaticaUtil.getLowercaseEscapeSpace(relatedRow.getATTRNAME());

					// Creating Global Attribute Node
					Node globalAttrNode = InformaticaUtil.createNodeStructure(adminResourceResolver,
							InformaticaUtil.getLowercaseEscapeSpace(relatedRow.getATTRNAME()), globalAttRootNode,
							globalAttrNodePath);

					// Setting Global Attribute Node Properties
					setGlobalodeProperties(globalAttrNode, relatedRow);

					LOG.info("Iteration of record Ended");

					successItemIDs.add(relatedRow.getATTRNAME().concat(InformaticaConstants.HYPHEN_STRING)
							.concat(InformaticaConstants.SUCCESS_CODE));
					count++;
					response.setUpdateItemsCount(count);

				} else {
					LOG.info("Folder structure {} for Global Atribute Data does not exists",
							InformaticaConstants.GLOBAL_ATTRIBUTE_AEM_LOCATION);
					String failedEntry = relatedRow.getATTRNAME().concat(InformaticaConstants.HYPHEN_STRING)
							.concat(InformaticaConstants.TECHNICAL_ISSUE_ERROR_CODE);
					failedItemIDs.add(failedEntry);
					response.setProcessFlag(false);
					response.setErrMsg(InformaticaConstants.TECHNICAL_ISSUE_ERROR_CODE.concat("_")
							.concat(InformaticaConstants.TECHNICAL_ISSUE_ERROR_MSG));
				}

			}

		} catch (EatonApplicationException | EatonSystemException e) {
			LOG.error(InformaticaConstants.IMPORT_FILE_ERROR_CODE.concat("_").concat(relatedRow.getATTRNAME())
					.concat(InformaticaConstants.IMPORT_FILE_ERROR_MSG), e.getMessage(), e);
			String failedEntry = relatedRow.getATTRNAME().concat(InformaticaConstants.HYPHEN_STRING)
					.concat(InformaticaConstants.IMPORT_FILE_ERROR_CODE);
			failedItemIDs.add(failedEntry);
			response.setProcessFlag(false);
			response.setErrMsg(InformaticaConstants.IMPORT_FILE_ERROR_CODE + InformaticaConstants.UNDERSCORE
					+ InformaticaConstants.IMPORT_FILE_ERROR_MSG);

		}

		catch (Exception e) {
			LOG.error(InformaticaConstants.IMPORT_FILE_ERROR_CODE.concat("_").concat(relatedRow.getATTRNAME())
					.concat(InformaticaConstants.IMPORT_FILE_ERROR_MSG), e.getMessage(), e);
			String failedEntry = relatedRow.getATTRNAME().concat(InformaticaConstants.HYPHEN_STRING)
					.concat(InformaticaConstants.IMPORT_FILE_ERROR_CODE);
			failedItemIDs.add(failedEntry);
			response.setProcessFlag(false);
			response.setErrMsg(InformaticaConstants.IMPORT_FILE_ERROR_CODE + InformaticaConstants.UNDERSCORE
					+ InformaticaConstants.IMPORT_FILE_ERROR_MSG);

		}

		return count;
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
	private void setGlobalodeProperties(Node globalAttrNode, ROW relatedRow) throws EatonApplicationException {

		try {
			if (globalAttrNode != null) {
				globalAttrNode.setProperty("attributeName", relatedRow.getATTRNAME());
				globalAttrNode.setProperty("attributeDisplayName", relatedRow.getATTRDISPLAYNAME());
				globalAttrNode.setProperty("lastUpdateDate", relatedRow.getLASTUPDATEDATE());
				globalAttrNode.setProperty("sequence", relatedRow.getSEQUENCE());
				globalAttrNode.getSession().save();
			}
		} catch (RepositoryException e) {
			throw new EatonApplicationException(InformaticaConstants.IMPORT_FILE_ERROR_CODE, e.getMessage(), e);

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
	private File getEntryXMLFile(InformaticaConfigServiceBean informaticaConfigServiceBean) {
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

				File globalfile = new File(filepath + informaticaConfigServiceBean.getGlobalAttFileName());

				// Validate if required input Global Attribute file exists for
				// processing
				if (globalfile.exists()) {
					LOG.info("File {} Found at shared location ", informaticaConfigServiceBean.getGlobalAttFileName());
					LOG.debug(" Existed getEntryXMLFile() method");
					return globalfile;
				}

				else {
					// No Requested Input file found
					if (LOG.isInfoEnabled()) {
						LOG.info(
								InformaticaConstants.FILE_NOT_FOUND_ERROR_CODE
										.concat(InformaticaConstants.HYPHEN_STRING) + "{}",
								informaticaConfigServiceBean.getGlobalAttFileName());
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
			if (LOG.isInfoEnabled()) {
				LOG.info("Input FilePath {}" + DOES_NOT_EXISTS, filepath);
			}
			throw new EatonSystemException(InformaticaConstants.PATH_ERROR_CODE, InformaticaConstants.PATH_ERROR_MSG);
		}

	}

	/**
	 * This method is to parse Global Attribute XML
	 * 
	 * @param globalAttrXML
	 * @return
	 * @throws EatonApplicationException
	 * @throws EatonSystemException
	 */
	private GLOBALATTRIBUTEITM parseGlobalAttrXML(File globalAttrXML, AdminService adminService)
			throws EatonApplicationException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("parseGlobalAttrXML method" + InformaticaConstants.IMPORT_STARTED);
		}
		// New GlobalAttribute object which will be returned
		GLOBALATTRIBUTEITM globalAttribute = new GLOBALATTRIBUTEITM();
		InputStream xsdInputStream = null;

		try (ResourceResolver adminResourceResolver = adminService.getReadService()){
			// New jaxbContext for parsing XML

			JAXBContext jaxbContext;
			jaxbContext = JAXBContext.newInstance(GLOBALATTRIBUTEITM.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			// tmp Global Attribute Object for adding to overall load object
			GLOBALATTRIBUTEITM tmpglobalAttr = null;

			String filePath = InformaticaConstants.INFORMATICA_XSD_LOCATION
					.concat(InformaticaConstants.GLOBAL_ATTR_XSD_FILE);

			// Getting XSD file from DAM
			xsdInputStream = InformaticaUtil.getXSDInputStreamfromDAM(adminResourceResolver, filePath);

			// create an instance of SchemaFactory to validate input XML against
			// XSD
			SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = sf.newSchema(new StreamSource(xsdInputStream));

			jaxbUnmarshaller.setSchema(schema);
			jaxbUnmarshaller.setEventHandler(new XMLValidator());

			// unmarshalling the current file to return the content tree
			tmpglobalAttr = (GLOBALATTRIBUTEITM) jaxbUnmarshaller.unmarshal(globalAttrXML);

			if (tmpglobalAttr != null) {
				globalAttribute.getROW().addAll(tmpglobalAttr.getROW());

			}

		} catch (UnmarshalException  e) {
			LOG.error(InformaticaConstants.INVALID_FILE_FORMAT_ERROR_CODE,
					InformaticaConstants.INVALID_FILE_FORMAT_ERROR_MSG);
			throw new EatonApplicationException(InformaticaConstants.INVALID_FILE_FORMAT_ERROR_CODE
					.concat(InformaticaConstants.HYPHEN_STRING).concat(globalAttrXML.getName()),
					InformaticaConstants.INVALID_FILE_FORMAT_ERROR_MSG, e);

		} catch ( SAXException | JAXBException e) {
			LOG.error("JAXBException occured ", e.getMessage(), e);
			throw new EatonApplicationException(InformaticaConstants.PARSING_ISSUE_ERROR_CODE
					.concat(InformaticaConstants.HYPHEN_STRING).concat(globalAttrXML.getName()),
					InformaticaConstants.PARSING_ISSUE_ERROR_MSG, e);

		}
		LOG.info("XML file {} successfully Validated and parsed", globalAttrXML.getName());
		if (LOG.isDebugEnabled()) {
			LOG.debug("parseProductFamilyXML method" + InformaticaConstants.PROCESS_ENDED);
		}
		// Return the item unmarshalled to Global Attribute object
		return globalAttribute;

	}

}
