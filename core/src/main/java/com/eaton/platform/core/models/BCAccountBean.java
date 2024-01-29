package com.eaton.platform.core.models;

import java.util.List;

/**
 * This class is for Brightcove Account Bean.
 * @author TCS
 *
 */
public class BCAccountBean  {
	
	/** The acc number. */
	private String accNumber;
	
	/** The acc name. */
	private String accName;
	
	/** The player details. */
	private List<BCPlayerBean> playerDetails;
	
	/** The num of players. */
	private int numOfPlayers;

	/**
	 * Gets the acc number.
	 *
	 * @return the accNumber
	 */
	public String getAccNumber() {
		return accNumber;
	}

	/**
	 * Sets the acc number.
	 *
	 * @param accNumber the accNumber to set
	 */
	public void setAccNumber(String accNumber) {
		this.accNumber = accNumber;
	}

	/**
	 * Gets the acc name.
	 *
	 * @return the accName
	 */
	public String getAccName() {
		return accName;
	}

	/**
	 * Sets the acc name.
	 *
	 * @param accName the accName to set
	 */
	public void setAccName(String accName) {
		this.accName = accName;
	}

	/**
	 * Gets the player details.
	 *
	 * @return the playerDetails
	 */
	public List<BCPlayerBean> getPlayerDetails() {
		return playerDetails;
	}

	/**
	 * Sets the player details.
	 *
	 * @param playerDetails the playerDetails to set
	 */
	public void setPlayerDetails(List<BCPlayerBean> playerDetails) {
		this.playerDetails = playerDetails;
	}

	/**
	 * Gets the num of players.
	 *
	 * @return the numOfPlayers
	 */
	public int getNumOfPlayers() {
		return numOfPlayers;
	}

	/**
	 * Sets the num of players.
	 *
	 * @param numOfPlayers the numOfPlayers to set
	 */
	public void setNumOfPlayers(int numOfPlayers) {
		this.numOfPlayers = numOfPlayers;
	}

	
}
