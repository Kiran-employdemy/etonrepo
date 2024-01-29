package com.eaton.platform.integration.informatica.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.QueryManager;

import com.day.cq.dam.api.Asset;
import org.apache.commons.io.FilenameUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaton.platform.core.exception.EatonApplicationException;
import com.eaton.platform.core.exception.EatonSystemException;
import com.eaton.platform.integration.informatica.constants.InformaticaConstants;

public class InformaticaUtil {
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(InformaticaUtil.class);
	/** The Constant SPACE_STRING. */
	private static final String SPACE_STRING = " ";
	/** The Constant HYPHEN_STRING. */
	private static final String HYPHEN_STRING = "-";
	/** The Constant UNDERSCORE_STRING. */
	private static final String UNDERSCORE = "_";
	/** The ConstantXML STRING. */
	private static final String FINAL_IDENTIFIER = ".xml";
	/** The Constant DOT STRING. */
	private static final String DOT = ".";
	private static final String LINE_SEPARATOR = "line.separator";
	
	/** The Constant SPECIALCHARS_STRING. */
	private static final String SPECIALCHARS_STRING = "[ /]";
	
	private InformaticaUtil() {
		LOGGER.debug("Inside InformaticaUtil constructor");
	}

	/**
	 * This method is to lowercase the string and replace space with hyphen
	 * 
	 * @param nodeName
	 * @return
	 */
	public static String getLowercaseEscapeSpace(String nodeName) {
		String output = nodeName;
		if (output != null) {
			output = output.toLowerCase().replaceAll(SPECIALCHARS_STRING, HYPHEN_STRING).trim();
		}
		return output;
	}

	/**
	 * Escape XML.
	 * 
	 * @param htmlString
	 *            the html string
	 * @return the string
	 */
	public static String escapeXMLAppendLang(String xmlString, String lang) {
		LOGGER.debug("Entered into escapeXML method");
		String output = xmlString;
		if (output != null) {
			output = output.replaceAll(FINAL_IDENTIFIER, "").trim();
			output = output.concat(UNDERSCORE).concat(lang).concat(FINAL_IDENTIFIER);
		}
		LOGGER.debug("Exited from escapeXML method");
		return output;
	}

	/**
	 * This method is to read XSD files from DAM
	 * 
	 * @param adminResourceResolver
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static InputStream getXSDInputStreamfromDAM(
			ResourceResolver adminResourceResolver, String filePath) throws EatonSystemException {
		LOGGER.debug("Entered into getXSDInputStreamfromDAM method");
		Resource resource = adminResourceResolver.getResource(filePath);
		if (resource != null) {
			Asset asset = resource.adaptTo(Asset.class);
			return asset.getOriginal().getStream();
		} else {
			LOGGER.error("Error in getXSDInputStreamfromDAM method, DAM resource not found");
		}
		LOGGER.debug("Exited from getXSDInputStreamfromDAM method");
		return null;
	}

	/**
	 * This method is to rename Input file as _TimeStamp .
	 * 
	 * @param sourceFile
	 * @param filepath
	 * @return
	 * @throws IOException
	 */
	public static Path renameInputFile(String filePath) {

		String extension = DOT + FilenameUtils.getExtension(filePath);
		String timestamp = new SimpleDateFormat("MM-dd-yy").format(new Date());
		String newName = filePath + InformaticaConstants.UNDERSCORE + timestamp + extension;
		Path newPath = null;

		
        
		try {
			Path oldName = Paths.get(filePath);		
			newPath = Files.move(oldName, oldName.resolveSibling(newName), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			LOGGER.error("Issue while Renaming the file with timestamp", e.getMessage(), e);

		}
		return newPath;
	}

	/**
	 * This method is to return email parameters
	 * 
	 * @return Map of email parameters
	 */
	public static Map<String, String> getEmailParams(Map<String, String> emailParams, String userID) {

			//set dynamic parameters
		emailParams.put("senderName", InformaticaConstants.SENDER_NAME );
		emailParams.put("recipientName", InformaticaConstants.RECIPIENT_NAME);
		emailParams.put("importerName", "Import initiated by user: " + userID);

		return emailParams;

	}

	/**
	 * This method is to create Status.txt file for each XML
	 * 
	 * @param successItemIDs
	 * @param failedItemIDs
	 * @return
	 * @throws IOException
	 */
	public static File updateStatus(List<String> successItemIDs,
			List<String> failedItemIDs, String statusFileName) {
		File statusFile = null;
		FileOutputStream outFile = null;

		String timestamp = new SimpleDateFormat("MM-dd-yy").format(new Date());	
		statusFileName = statusFileName+ InformaticaConstants.UNDERSCORE + timestamp + ".txt";
		try {
			LOGGER.info("updateStatus method started: ");
			statusFile = new File(statusFileName);
			LOGGER.info(InformaticaConstants.STATUS_FILE_PATH, statusFile.getCanonicalPath());
			if (statusFile.exists()) {
				statusFile.delete();
			}
			statusFile.createNewFile();
			outFile = new FileOutputStream(statusFile);
			if (successItemIDs.isEmpty() && failedItemIDs.isEmpty()) {
				outFile.write(InformaticaConstants.NO_DATA_TO_CHANGE.getBytes());

			} else { // Writing Succeeded entries to Status.txt file
				outFile.write(("Succeeded Entries:" + System.getProperty(LINE_SEPARATOR)).getBytes());
				if (!successItemIDs.isEmpty()) {
					for (String entry : successItemIDs) {
						outFile.write(entry.getBytes());
						outFile.write(System.getProperty(LINE_SEPARATOR).getBytes());
					}
				} else {
					outFile.write("Sorry!! No Succeeded Entries".getBytes());
					outFile.write(System.getProperty(LINE_SEPARATOR).getBytes());
				}

				outFile.write(("Succeeded Entries Ended" + System.getProperty(LINE_SEPARATOR)).getBytes());
				outFile.write(System.getProperty(LINE_SEPARATOR).getBytes());

				// Writing Failed entries to Status.txt file
				outFile.write(("Failed Entries:" + System.getProperty(LINE_SEPARATOR)).getBytes());

				if (!failedItemIDs.isEmpty()) {
					for (String entry : failedItemIDs) {
						outFile.write(entry.getBytes());
						outFile.write(System.getProperty(LINE_SEPARATOR).getBytes());
					}
				} else {
					outFile.write("Hurray!! No Failed Entries".getBytes());
					outFile.write(System.getProperty(LINE_SEPARATOR).getBytes());

				}

				outFile.write(("Failed Entries Ended" + System.getProperty(LINE_SEPARATOR)).getBytes());
			}

			outFile.flush();
		} catch (IOException e) {
			LOGGER.error(InformaticaConstants.ERR_CODE_IO, InformaticaConstants.ERR_MSG_STATUS_FILE, e);
		} finally {
			try {
				if (outFile != null)
					outFile.close();
			} catch (IOException ioe) {
				LOGGER.error("Error in closing the Stream", ioe);
			}

		}
		LOGGER.info("updateStatus method Ended ");
		return statusFile;

	}

	/**
	 * This method is to check duplicates entries in String array.
	 * 
	 * @param tradeNameList
	 * @return
	 */
	public static boolean isAllStringSame(String[] tradeNameList) {
		boolean flag = true;
		for (int i = 0; i < tradeNameList.length; i++) {
			for (int j = i + 1; j < tradeNameList.length; j++) {
				if (!tradeNameList[i].equals(tradeNameList[j])) {
					flag = false;
					break;

				}
			}
		}

		return flag;
	}

	/**
	 * This method is to create Node in JCR
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
	public static Node createNodeStructure(
			ResourceResolver adminResourceResolver, String nodeName,
			Node nodeParentNode, String nodePath)
			throws EatonApplicationException {

		Node pdhNode = null;

		try {
			// Determine if Node to be created already exists or not
			if (ResourceUtil.isNonExistingResource(adminResourceResolver.resolve(nodePath)))
				pdhNode = nodeParentNode.addNode(nodeName, InformaticaConstants.OSGI_NODE_TYPE);
			else {
				nodeParentNode.getNode(nodeName).remove();
				pdhNode = nodeParentNode.addNode(nodeName, InformaticaConstants.OSGI_NODE_TYPE);
			}
		} catch (RepositoryException e) {
			throw new EatonApplicationException(InformaticaConstants.IMPORT_FILE_ERROR_CODE, e.getMessage(), e);
		}
		LOGGER.info(" Node {} Created Succesfully.", nodePath);
		return pdhNode;

	}

	/**
	 * This method Creates, Exceutes Query bases on params provided and returns
	 * whether inventory Item ID node present in JCR or not.
	 * 
	 * @param adminResourceResolver
	 * @param pdhnodeName
	 * @return
	 * @throws RepositoryException 
	 */
	public static boolean isExistingInventoryIDNode(
			ResourceResolver adminResourceResolver, String pdhnodeName,
			String searchPath) throws RepositoryException {
		javax.jcr.query.QueryResult results = null;
		try {
			Session adminSession = adminResourceResolver.adaptTo(Session.class);
			QueryManager queryManager = adminSession.getWorkspace().getQueryManager();
			String sqlStatement = "SELECT * FROM [sling:OsgiConfig] AS S WHERE S.[name] = '"
					+ pdhnodeName + "' AND ISDESCENDANTNODE(S,'" + searchPath + "')";
			javax.jcr.query.Query query = queryManager
					.createQuery(sqlStatement, "JCR-SQL2");
			
			LOGGER.debug("JCR-SQL2 query being execute:", query);
			results = query.execute();
		} catch (InvalidQueryException e) {
			LOGGER.error("Invalid Query Exception in isExistingInventoryIDNode method", e);
		} catch (RepositoryException e) {
			LOGGER.error("Repository Exception - ", e);
		}
		if (results != null && results.getNodes().getSize() > 0) {
			LOGGER.info("Inventory Item ID {} already present", pdhnodeName);
			return true;
		}
		return false;
	}

	/**
	 * This method return input path with separator
	 * 
	 * @param inputPath
	 * @param filename
	 * @return
	 */
	public static String getInputFilePath(String inputPath, String filename) {
		String fileSourcepath;
		if (inputPath.endsWith(File.separator))
			fileSourcepath = inputPath + filename;
		else
			fileSourcepath = inputPath + File.separator + filename;
		return fileSourcepath;

	}

	/**
	 * This method returns list of all possible paths using None with given
	 * brand/subbrand / tradename values.
	 * 
	 * @param brand
	 * @param subBrand
	 * @param tradeName
	 * @return
	 */
	public static List<String> getPossiblePaths(String brand, String subBrand,
			String tradeName) {
		List<String> paths = new ArrayList<>();
		paths.add("none/none/none/");
		paths.add("none/" + subBrand + "/none/");
		paths.add("none/" + subBrand + "/" + tradeName + "/");
		paths.add("none/none/" + tradeName + "/");
		paths.add(brand + "/none/none/");
		paths.add(brand + "/" + subBrand + "/none/");
		paths.add(brand + "/none" + "/" + tradeName + "/");
		return paths;
	}

}
