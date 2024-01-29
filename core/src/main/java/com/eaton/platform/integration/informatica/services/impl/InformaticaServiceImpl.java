package com.eaton.platform.integration.informatica.services.impl;

import com.adobe.acs.commons.email.EmailService;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.exception.EatonApplicationException;
import com.eaton.platform.core.exception.EatonSystemException;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.integration.informatica.bean.InformaticaConfigServiceBean;
import com.eaton.platform.integration.informatica.bean.InformaticaResponse;
import com.eaton.platform.integration.informatica.constants.InformaticaConstants;
import com.eaton.platform.integration.informatica.helper.InformaticaGlobalAttrHelper;
import com.eaton.platform.integration.informatica.helper.InformaticaProductFamilyHelper;
import com.eaton.platform.integration.informatica.helper.InformaticaTaxAttrHelper;
import com.eaton.platform.integration.informatica.services.InformaticaService;
import com.eaton.platform.integration.informatica.util.InformaticaUtil;
import org.apache.commons.io.FilenameUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <html> Description: This class contains core business logic to interact with
 * Informatica XML file by passing respective configuration bean request.
 * </html>
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 *
 */

@Component(service = InformaticaService.class, immediate = true,
		property = {
				AEMConstants.SERVICE_VENDOR_EATON,
				AEMConstants.SERVICE_DESCRIPTION + "InformaticaServiceImpl",
				AEMConstants.PROCESS_LABEL + "InformaticaServiceImpl"
		})
public class InformaticaServiceImpl implements InformaticaService {
	private static final Logger LOG = LoggerFactory.getLogger(InformaticaServiceImpl.class);
	private static final String IMPORT_DATA = "Import Data";
	private static final String MOVE_FILE = "Move file to Target Folder ";
	private static final String MOVE_PROCESSED_FILE = "Move file to Archival/Failed Folder ";
	private static final String FILE_PROCESS_STATUS = "All files have been processed";
	private static final String EMAIL_MESSAGE_1 = "message";
	private static final String EMAIL_MESSAGE_2 = "message1";

	// Email Service Reference
	@Reference
	protected EmailService emailService;

	// AdminService Reference
	@Reference
	protected AdminService adminService;

	/**
	 * This method contains core business logic to interact with Informatica
	 * file i.e. Global Attributes.xml by passing Global configuration bean
	 * request
	 *
	 */

	@Override
	public String processInformaticaGlobalAttrData(InformaticaConfigServiceBean informaticaConfigServiceBean, String userID)
			throws IOException {

		LOG.info(IMPORT_DATA, InformaticaConstants.IMPORT_STARTED);
		String emailSentStatus = null;
		InformaticaGlobalAttrHelper informaticaGlobalAttr = new InformaticaGlobalAttrHelper();
		InformaticaResponse response = new InformaticaResponse();
		Map<String, String> emailParams = null;
		String fileSourcepath = null;
		String globalResultStatus = "Status : ";
		String archivedfilePath = informaticaConfigServiceBean.getAttributeArchiveFilePath();
		String failedfilePath = informaticaConfigServiceBean.getAttributeFailedFilePath();
		List<String> mailRecipients = informaticaConfigServiceBean.getAttributeEmailIds();

		try {
			response = informaticaGlobalAttr.processGlobalAttrData(informaticaConfigServiceBean, adminService);
			fileSourcepath = InformaticaUtil.getInputFilePath(informaticaConfigServiceBean.getAttributeInputFilePath(),
					informaticaConfigServiceBean.getGlobalAttFileName());

			emailParams = getEmailParams(informaticaConfigServiceBean.getGlobalAttFileName(), response, fileSourcepath);
			emailParams = InformaticaUtil.getEmailParams(emailParams,userID);

			// move file to Target Folder
			moveProcessedFile(response, fileSourcepath, archivedfilePath, failedfilePath, mailRecipients,userID);

			if (!response.isProcessFlag() && response.getStatusFile() != null){
				// moving failed status file to failed folder
				moveStatusFile(response.getStatusFilePath(), failedfilePath,response);
			}
			if (response.getErrMsg() != null && !response.isProcessFlag()) {
				globalResultStatus = globalResultStatus + informaticaConfigServiceBean.getGlobalAttFileName() + " " + response.getErrMsg();
			} else if (response.getErrMsg() != null && response.isProcessFlag()){
				globalResultStatus = globalResultStatus +  informaticaConfigServiceBean.getGlobalAttFileName() + " " + InformaticaConstants.SUCCESS_MSG + "<br>";
				globalResultStatus = globalResultStatus + informaticaConfigServiceBean.getGlobalAttFileName() + " " + response.getErrMsg() ;
			}else {
				globalResultStatus = globalResultStatus +  informaticaConfigServiceBean.getGlobalAttFileName() + " " + InformaticaConstants.SUCCESS_MSG ;
			}
			// send Email notification to recipients to inform status.
			emailSentStatus =   sendEmailNotification(mailRecipients, emailParams, response.getStatusFile());

		} catch (EatonApplicationException | EatonSystemException e) {
			LOG.error(InformaticaConstants.EXCEPTION_OCCURED, e);

		} catch (Exception e) {
			LOG.error(InformaticaConstants.EXCEPTION_OCCURED, e.getMessage(), e);
		} finally {
			if (response.isProcessFlag() && response.getStatusFile() != null) {
				Path filePath = Paths.get(response.getStatusFile().getPath());
				Files.delete(filePath);
				if (LOG.isInfoEnabled()) {
					LOG.info(InformaticaConstants.SUCCESS_CODE + InformaticaConstants.SUCCESS_MSG);
				}
			}
			LOG.info(FILE_PROCESS_STATUS);
		}
		if(emailSentStatus != null) {
			return (globalResultStatus + "<br>" + emailSentStatus);
		} else {
			return globalResultStatus;
		}
	}

	/**
	 * This method contains core business logic to interact with Informatica
	 * file i.e.TaxonomyAttribute .xml by passing PDH configuration bean request
	 *
	 * @throws EatonApplicationException
	 * @throws IOException
	 */

	@Override
	public String processInformaticaTaxAttrData(InformaticaConfigServiceBean informaticaConfigServiceBean, String userID)
			throws EatonApplicationException, IOException {
		LOG.info(IMPORT_DATA, InformaticaConstants.IMPORT_STARTED);
		String emailSentStatus = null;
		String taxAttrResultStatus = "Status : ";
		InformaticaTaxAttrHelper informaticaTaxAttr = new InformaticaTaxAttrHelper();
		InformaticaResponse response = new InformaticaResponse();
		Map<String, String> emailParams = null;
		String fileSourcepath = null;
		String archivedfilePath = informaticaConfigServiceBean.getAttributeArchiveFilePath();
		String failedfilePath = informaticaConfigServiceBean.getAttributeFailedFilePath();
		List<String> mailRecipients = informaticaConfigServiceBean.getAttributeEmailIds();

		try {

			response = informaticaTaxAttr.processTaxAttrData(informaticaConfigServiceBean, adminService);
			fileSourcepath = InformaticaUtil.getInputFilePath(informaticaConfigServiceBean.getAttributeInputFilePath(),
					informaticaConfigServiceBean.getTaxonomyAttFileName());

			emailParams = getEmailParams(informaticaConfigServiceBean.getTaxonomyAttFileName(), response,
					fileSourcepath);
			emailParams = InformaticaUtil.getEmailParams(emailParams,userID);

			// move file to Target Folder
			moveProcessedFile(response, fileSourcepath, archivedfilePath, failedfilePath, mailRecipients,userID);
			if (!response.isProcessFlag() && response.getStatusFile() != null) {
				// moving failed status file to failed folder
				moveStatusFile(response.getStatusFilePath(), failedfilePath, response);
			}

			if (response.getErrMsg() != null && !response.isProcessFlag()) {
				taxAttrResultStatus = taxAttrResultStatus + informaticaConfigServiceBean.getTaxonomyAttFileName() + " " + response.getErrMsg();
			} else if (response.getErrMsg() != null && response.isProcessFlag()){
				taxAttrResultStatus = taxAttrResultStatus + informaticaConfigServiceBean.getTaxonomyAttFileName() + InformaticaConstants.SUCCESS_MSG + "<br>";
				taxAttrResultStatus = taxAttrResultStatus + informaticaConfigServiceBean.getTaxonomyAttFileName() + " " + response.getErrMsg();
			} else {
				taxAttrResultStatus = taxAttrResultStatus + informaticaConfigServiceBean.getTaxonomyAttFileName() + " "+ InformaticaConstants.SUCCESS_MSG ;
			}
			// send Email notification to recipients to inform status.
			emailSentStatus  = sendEmailNotification(mailRecipients, emailParams, response.getStatusFile());

		} catch (EatonApplicationException | EatonSystemException e) {
			LOG.error(InformaticaConstants.EXCEPTION_OCCURED, e);

		} catch (Exception e) {
			LOG.error(InformaticaConstants.EXCEPTION_OCCURED, e.getMessage(), e);
		} finally {

			if (response.isProcessFlag() && response.getStatusFile() != null) {
				Path filePath = Paths.get(response.getStatusFile().getPath());
				Files.delete(filePath);
				if (LOG.isInfoEnabled()) {
					LOG.info(InformaticaConstants.SUCCESS_CODE + InformaticaConstants.SUCCESS_MSG);
				}
			}

			LOG.info(FILE_PROCESS_STATUS);
		}
		if(emailSentStatus != null) {
			return (taxAttrResultStatus + "<br>" + emailSentStatus);
		} else {
			return taxAttrResultStatus;
		}

	}

	/**
	 * This method contains core business logic to interact with Informatica
	 * file i.e.Product Family.xml by passing PDH configuration bean request
	 *
	 * @throws EatonApplicationException
	 */
	@Override
	public String processInformaticaProductFamilyData(InformaticaConfigServiceBean informaticaConfigServiceBean, String userID)
			throws IOException {

		LOG.info(IMPORT_DATA, InformaticaConstants.IMPORT_STARTED);
		String  emailSentStatus = null;
		InformaticaProductFamilyHelper informaticaPDHHelper = new InformaticaProductFamilyHelper();
		InformaticaResponse response = new InformaticaResponse();
		Map<String, String> emailParams = null;
		String fileName = null;
		String fileSourcepath = null;
		String productFamilyStatus = "ProductFamilyStatus : ";
		List<String> langList = informaticaConfigServiceBean.getProductfamilyLanguages();
		String archivedfilePath = informaticaConfigServiceBean.getProductfamilyArchiveFilePath();
		String failedfilePath = informaticaConfigServiceBean.getProductfamilyFailedFilePath();
		List<String> mailRecipients = informaticaConfigServiceBean.getProductfamilyEmailIds();
		try {

			if (langList != null && !langList.isEmpty()) {
				for (String lang : langList) {

					response = informaticaPDHHelper.processProductFamilyData(informaticaConfigServiceBean, adminService,
							lang);

					fileName = InformaticaUtil
							.escapeXMLAppendLang(informaticaConfigServiceBean.getProductfamilyFileName(), lang);
					fileSourcepath = InformaticaUtil
							.getInputFilePath(informaticaConfigServiceBean.getProductfamilyInputFilePath(), fileName);

					emailParams = getEmailParams(fileName, response, fileSourcepath);
					emailParams = InformaticaUtil.getEmailParams(emailParams,userID);

					// move file to target folder
					moveProcessedFile(response, fileSourcepath, archivedfilePath, failedfilePath, mailRecipients,userID);

					if (!response.isProcessFlag() && response.getStatusFile() != null) {
						// moving failed status file to failed folder
						moveStatusFile(response.getStatusFilePath(), failedfilePath, response);
					}
					if (response.getErrMsg() != null && !response.isProcessFlag()) {
						productFamilyStatus = productFamilyStatus.concat(fileName).concat(" ").concat(response.getErrMsg()).concat("<br>") ;
					} else if (response.getErrMsg() != null && response.isProcessFlag()){
						productFamilyStatus = productFamilyStatus.concat("<br>").concat(fileName).concat(" ").concat(InformaticaConstants.SUCCESS_MSG).concat("<br>");
						productFamilyStatus = productFamilyStatus.concat(" ").concat(response.getErrMsg());

					} else {
						productFamilyStatus = productFamilyStatus.concat("<br>").concat(fileName).concat(" ").concat(InformaticaConstants.SUCCESS_MSG).concat("<br>");
					}
					// sent email notification
					emailSentStatus  = sendEmailNotification(mailRecipients, emailParams, response.getStatusFile());
				}
			} else {
				LOG.info(InformaticaConstants.NO_LANGUAGE_ENTERED);
				response.setErrMsg(InformaticaConstants.NO_LANGUAGE_ENTERED);
				productFamilyStatus = response.getErrMsg();
				emailParams = getEmailParamsForFile(fileName, response);
				emailParams = InformaticaUtil.getEmailParams(emailParams,userID);
				productFamilyStatus = sendNotificationWithoutFile(mailRecipients, emailParams);
			}
		} catch (EatonSystemException e) {
			LOG.error(InformaticaConstants.EXCEPTION_OCCURED, e.getMessage());

		} catch (Exception e) {
			LOG.error(InformaticaConstants.EXCEPTION_OCCURED, e.getMessage(), e);

		} finally {
			if (response.isProcessFlag() && response.getStatusFile() != null) {
				Path filePath = Paths.get(response.getStatusFile().getPath());
				Files.delete(filePath);
				if (LOG.isInfoEnabled()) {
					LOG.info(InformaticaConstants.SUCCESS_CODE + InformaticaConstants.SUCCESS_MSG);
				}
			}
			LOG.info(FILE_PROCESS_STATUS);

		}

		if(emailSentStatus != null) {
			return (productFamilyStatus + "<br>" + emailSentStatus);
		} else {
			return productFamilyStatus;
		}
	}

	/**
	 * This method is to archive failed status file
	 *
	 * @param statusFilePath
	 * @param failedFilePath
	 */
	private void moveStatusFile(String statusFilePath, String failedFilePath, InformaticaResponse response) {

		LOG.info(MOVE_PROCESSED_FILE + " moveStatusFile Started");

		String newName = statusFilePath.substring(statusFilePath.lastIndexOf(File.separator) + 1) ;
		failedFilePath = failedFilePath + File.separator + newName;
		Path newDir = Paths.get(failedFilePath);
		Path oldName = Paths.get(statusFilePath);
		try {
			//copy file to Destination folder
			Files.copy(oldName, newDir, StandardCopyOption.REPLACE_EXISTING);
			//Deleting file from original destination
			response.setStatusFile(new File(newDir.toString()));
			fileDeletion(oldName);

		} catch (IOException e) {
			LOG.error("Exception occurred while archiving status file", e);

		}
		LOG.info(MOVE_PROCESSED_FILE + InformaticaConstants.PROCESS_ENDED);
	}

	/**
	 * @param userID
	 * @param response
	 * @param fileSourcepath
	 * @param archivedfilePath
	 * @param failedfilePath
	 * @param mailRecipients
	 * @return
	 * @throws IOException
	 * @throws EatonApplicationException
	 */
	private void moveProcessedFile(InformaticaResponse response, String fileSourcepath, String archivedfilePath,
								   String failedfilePath, List<String> mailRecipients,String userID) {
		try {
			if ((response.getStatusFile() == null)
					&& (response.getErrMsg().contains(InformaticaConstants.PATH_ERROR_CODE)
					|| response.getErrMsg().contains(InformaticaConstants.FILE_NOT_FOUND_ERROR_CODE)
					|| response.getErrMsg().contains(InformaticaConstants.NO_XML_ERROR_CODE))) {
				// move the Input file and status file to Archived or Failed
				LOG.info("Either Input path or XML File does not exists.");

			} else {
				// move the Input file and status file to Archived or Failed
				// folder as per response object's Process Flag variable
				moveToTargetFolder(fileSourcepath, archivedfilePath, failedfilePath, response.isProcessFlag());
			}
		} catch (EatonApplicationException e) {
			String fileName = fileSourcepath.substring(fileSourcepath.lastIndexOf(File.separator) + 1);
			LOG.info(InformaticaConstants.ARCHIVAL_PATH_ERROR_CODE.concat(InformaticaConstants.HYPHEN_STRING)
					.concat(fileName), InformaticaConstants.ARCHIVAL_PATH_ERROR_MSG, e);
			String message = "Error Code - ".concat(InformaticaConstants.ARCHIVAL_PATH_ERROR_CODE).concat("\n")
					.concat(InformaticaConstants.ARCHIVAL_PATH_ERROR_MSG);
			if(response.getErrMsg() == null) {
				response.setErrMsg(message);
			} else {
				response.setErrMsg(response.getErrMsg()+ "<br>" + message);
				Map<String, String> emailParams = new HashMap<>();
				emailParams = InformaticaUtil.getEmailParams(emailParams,  userID);
				emailParams.put(InformaticaConstants.SUBJECT, InformaticaConstants.EXCEPTION_OCCURED + fileName);
				emailParams.put(EMAIL_MESSAGE_1, message);
				emailParams.put(EMAIL_MESSAGE_2, fileName);
				sendEmailNotification(mailRecipients, emailParams, null);
			}
		}
	}

	/**
	 * This method is to get Email Parameters
	 *
	 * @param fileName
	 * @param response
	 * @param fileSourcePath
	 * @return
	 */
	private Map<String, String> getEmailParams(String fileName, InformaticaResponse response, String fileSourcePath) {
		Map<String, String> emailParams = null;
		if (response.getStatusFilePath() == null && (response.getStatusFile() == null)) {

			if (response.getErrMsg().contains(InformaticaConstants.FILE_NOT_FOUND_ERROR_CODE)
					|| response.getErrMsg().contains(InformaticaConstants.NO_XML_ERROR_CODE)) {
				emailParams = getEmailParamsFileNotFound(fileName);
			} else if (response.getErrMsg().contains(InformaticaConstants.PATH_ERROR_CODE)) {
				emailParams = getEmailParamsInvalidInputPath(fileSourcePath);
			} else if (response.getErrMsg().contains(InformaticaConstants. INVALID_FILE_FORMAT_ERROR_CODE)
					|| response.getErrMsg().contains(InformaticaConstants.PARSING_ISSUE_ERROR_CODE)
					|| response.getErrMsg().contains(InformaticaConstants.ARCHIVAL_PATH_ERROR_CODE)) {
				emailParams = getEmailParamsForFile(fileName, response);
			} else {
				emailParams = new HashMap<>();
				emailParams.put(InformaticaConstants.SUBJECT, InformaticaConstants.EXCEPTION_OCCURED + fileName);
				emailParams.put(EMAIL_MESSAGE_1, response.getErrMsg());
				emailParams.put(EMAIL_MESSAGE_2, fileName != null ? fileName : " ");
			}

		} else {
			emailParams = getEmailParamsProcessedFile(fileName);
		}
		return emailParams;
	}

	private Map<String, String> getEmailParamsInvalidInputPath(
			String fileSourcePath) {
		// Set the dynamic variables of email template
		Map<String, String> emailParams = new HashMap<>();

		// Customize the message
		emailParams.put(InformaticaConstants.SUBJECT, "Input path does not exists");
		emailParams.put(EMAIL_MESSAGE_1,
				" Input path does not exists. Please check path given below.");
		emailParams.put(EMAIL_MESSAGE_2, fileSourcePath);
		return emailParams;
	}

	@Activate
	protected void activate() {
		LOG.info("[*** AEM ConfigurationService]: activating Informatica Business Logic service");
	}

	/**
	 * This method decides the target folder Archive/ Failed on the basis of
	 * processFlag
	 *
	 * @param sourcePath
	 * @param archivedDesDir
	 * @param failedDesDir
	 * @param isProcessFlag
	 * @throws IOException
	 * @throws EatonApplicationException
	 */
	private void moveToTargetFolder(String sourcePath, String archivedDesDir, String failedDesDir,
									boolean isProcessFlag) throws EatonApplicationException {
		if (LOG.isDebugEnabled()) {
			LOG.debug(MOVE_FILE + "moveToTargetFolder Started");
		}
		// move the Input file and status file to Archived or Failed
		// folder as per result variable value Flag
		if (isProcessFlag) {
			LOG.info("Moving Input file to Archived Path as processFlag is true");
			moveFileToDesPath(sourcePath, archivedDesDir);

		} else {
			LOG.info("Moving Input file to Failed Path as processFlag is false");
			moveFileToDesPath(sourcePath, failedDesDir);
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug(MOVE_FILE + InformaticaConstants.PROCESS_ENDED);
		}
	}

	/**
	 * This method is to move Input file to Destination (Archive/Failed) folder
	 *
	 * @param sourcePath
	 * @param desDir
	 * @throws EatonApplicationException
	 */
	private void moveFileToDesPath(String sourcePath, String desDir) throws EatonApplicationException {

		LOG.info(MOVE_PROCESSED_FILE + " moveFileToDesPath Started");

		String extension = InformaticaConstants.DOT + FilenameUtils.getExtension(sourcePath);
		String timestamp = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
		String newName = sourcePath.substring(sourcePath.lastIndexOf(File.separator) + 1) + InformaticaConstants.UNDERSCORE + timestamp + extension;
		desDir = desDir + File.separator + newName;
		Path newDir = Paths.get(desDir);
		Path oldName = Paths.get(sourcePath);
		try {
			//copy file to Destination folder
			Files.copy(oldName, newDir, StandardCopyOption.REPLACE_EXISTING);
			//Deleting file from original destination
			fileDeletion(oldName);

		} catch (IOException e) {
			throw new EatonApplicationException(InformaticaConstants.ARCHIVAL_PATH_ERROR_CODE,
					InformaticaConstants.ARCHIVAL_PATH_ERROR_MSG, e);
		}
		LOG.info(MOVE_PROCESSED_FILE + InformaticaConstants.PROCESS_ENDED);
	}


	/**
	 * This method is to return email parameters
	 *
	 * @return Map of email parameters
	 */
	private Map<String, String> getEmailParamsProcessedFile(String fileName) {

		// Set the dynamic variables of email template

		Map<String, String> emailParams = new HashMap<>();
		emailParams.put(InformaticaConstants.SUBJECT, fileName + InformaticaConstants.MAIL_SUBJECT);
		emailParams.put(EMAIL_MESSAGE_1, "Informatica import Status for" + fileName);
		emailParams.put(EMAIL_MESSAGE_2, "Please Check attached Status file.");

		return emailParams;
	}

	/**
	 * This method is to return email parameters
	 *
	 * @return Map of email parameters
	 */
	private Map<String, String> getEmailParamsForFile(String fileName, InformaticaResponse response) {

		// Set the dynamic variables of email template
		Map<String, String> emailParams = new HashMap<>();
		emailParams.put(InformaticaConstants.SUBJECT,  "Invalid Format of File - " +fileName );
		emailParams.put(EMAIL_MESSAGE_1, response.getErrMsg());
		emailParams.put(EMAIL_MESSAGE_2, fileName != null ? fileName : " ");
		return emailParams;
	}

	/**
	 * This method is to return email parameters
	 *
	 * @param fileName
	 * @return Map of email parameters
	 */
	private Map<String, String> getEmailParamsFileNotFound(String fileName) {
		Map<String, String> emailParams = new HashMap<>();
		// Customize the message
		emailParams.put(InformaticaConstants.SUBJECT, fileName + " Not Exists");
		emailParams.put(EMAIL_MESSAGE_1,
				"XML File with below name does not exists at Input Path.");
		emailParams.put(EMAIL_MESSAGE_2, fileName);

		return emailParams;
	}

	/**
	 * This method is to sentEmailNotification on the basis of statusFile
	 *
	 * @param mailRecipients
	 * @param emailParams
	 * @param statusFile
	 */
	private String sendEmailNotification(List<String> mailRecipients, Map<String, String> emailParams, File statusFile ) {
		String returnResult = null;
		try {
			if (statusFile == null) {
				// send Email notification to recipients to inform either path
				// or
				// file doesn't exists.
				returnResult = sendNotificationWithoutFile(mailRecipients, emailParams);
			} else {
				// send Email notification to recipients with status file
				returnResult = sendNotificationWithFile(mailRecipients, statusFile, emailParams);
			}
		} catch (Exception e) {
			LOG.error(InformaticaConstants.EXCEPTION_OCCURED, e.getMessage(), e);
		}
		return returnResult;
	}

	/**
	 * This method is to send Notification to recipients when Input Path or
	 * Input file does not exists.
	 *
	 * @param mailRecipients
	 *            - List of Recipients
	 * @param emailParams
	 */
	private String sendNotificationWithoutFile(List<String> mailRecipients, Map<String, String> emailParams) {
		String status = null;
		// Array of email recipients
		String[] recipients = mailRecipients.toArray(new String[mailRecipients.size()]);

		// emailService.sendEmail(..) returns a list of all the recipients that
		// could not be sent the email
		// An empty list indicates 100% success
		List<String> participantList = emailService.sendEmail(InformaticaConstants.EMAIL_TEMPLATE_PATH, emailParams,
				recipients);

		if (participantList.isEmpty()) {
			LOG.info("Email sent successfully to the recipients");
		} else {
			LOG.info(InformaticaConstants.EMAIL_NOTIFICATION_ERROR_CODE + InformaticaConstants.HYPHEN_STRING + InformaticaConstants.EMAIL_NOTIFICATION_ERROR_MSG);
			status =  InformaticaConstants.EMAIL_NOTIFICATION_ERROR_CODE + InformaticaConstants.HYPHEN_STRING + InformaticaConstants.EMAIL_NOTIFICATION_ERROR_MSG;
			LOG.info("Email not sent successfully to the below recipients");
			for (String participant : participantList) {
				LOG.info(participant);
			}
		}

		return status;
	}

	/**
	 * This method is to send email notifications to users .
	 *
	 * @param mailRecipients
	 * @param statusFile
	 * @param emailParams
	 */
	private String sendNotificationWithFile(List<String> mailRecipients, File statusFile,
											Map<String, String> emailParams) {
		String status = null;
		// Array of email recipients
		String[] recipients = mailRecipients.toArray(new String[mailRecipients.size()]);

		// creating Map for attachment file
		Map<String, DataSource> attachments = new HashMap<>();
		FileDataSource fds = new FileDataSource(statusFile);
		attachments.put(statusFile.getName(), fds);

		// emailService.sendEmail(..) returns a list of all the recipients that
		// could not be sent the email
		// An empty list indicates 100% success
		List<String> participantList = emailService.sendEmail(InformaticaConstants.EMAIL_TEMPLATE_PATH, emailParams,
				attachments, recipients);

		if (participantList.isEmpty()) {
			LOG.info("Email sent successfully to the recipients");
		} else {
			LOG.info(InformaticaConstants.EMAIL_NOTIFICATION_ERROR_CODE + InformaticaConstants.HYPHEN_STRING + InformaticaConstants.EMAIL_NOTIFICATION_ERROR_MSG);
			status =  InformaticaConstants.EMAIL_NOTIFICATION_ERROR_CODE + InformaticaConstants.HYPHEN_STRING + InformaticaConstants.EMAIL_NOTIFICATION_ERROR_MSG;
			for (String participant : participantList) {
				LOG.info(participant);
			}
		}
		return status;
	}

	private void fileDeletion(Path filePath) throws IOException {
		if(filePath.toFile().exists()) {
			Files.delete(filePath);
			LOG.info("File Copied to Destination Folder");
		}
	}
}
