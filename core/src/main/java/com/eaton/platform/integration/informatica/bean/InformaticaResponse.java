package com.eaton.platform.integration.informatica.bean;

import java.io.File;
import java.io.Serializable;

/**
 * This class is Informatica Response Bean
 * 
 * @author TCS
 * 
 */
public class InformaticaResponse implements Serializable {

	private static final long serialVersionUID = 8797832577877924230L;

	/** The file Entry ProcessFlag */
	private boolean processFlag = true;

	/** The Status file path. */
	private String statusFilePath;

	/** The Status file */
	private File statusFile;

	/** The Error Message String */
	private String errMsg;

	/** The record count String */
	int updateItemsCount = 0;

	/**
	 * @return the updateItemsCount
	 */
	public int getUpdateItemsCount() {
		return updateItemsCount;
	}

	/**
	 * @param updateItemsCount
	 *            the updateItemsCount to set
	 */
	public void setUpdateItemsCount(int updateItemsCount) {
		this.updateItemsCount = updateItemsCount;
	}

	/**
	 * @return the processFlag
	 */
	public boolean isProcessFlag() {
		return processFlag;
	}

	/**
	 * @param processFlag
	 *            the processFlag to set
	 */
	public void setProcessFlag(boolean processFlag) {

		this.processFlag = processFlag;
	}

	/**
	 * @return the statusFilePath
	 */
	public String getStatusFilePath() {
		return statusFilePath;
	}

	/**
	 * @param statusFilePath
	 *            the statusFilePath to set
	 */
	public void setStatusFilePath(String statusFilePath) {
		this.statusFilePath = statusFilePath;
	}

	/**
	 * @return the statusFile
	 */
	public File getStatusFile() {
		return statusFile;
	}

	/**
	 * @param statusFile
	 *            the statusFile to set
	 */
	public void setStatusFile(File statusFile) {
		this.statusFile = statusFile;
	}

	/**
	 * @return the errMsg
	 */
	public String getErrMsg() {
		return errMsg;
	}

	/**
	 * @param errMsg
	 *            the errMsg to set
	 */
	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

}
