package com.eaton.platform.core.models;

/**
 * This class is for Brightcove Player Bean.
 * @author TCS
 *
 */
public class BCPlayerBean  {
	/** playerId. */
	private String playerId;
	/** playerName. */
	private String playerName; 
	
	/**
	 * Gets the player id.
	 *
	 * @return the playerId
	 */
	public String getPlayerId() {
		return playerId;
	}

	/**
	 * Sets the player id.
	 *
	 * @param playerId the playerId to set
	 */
	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	/**
	 * Gets the player name.
	 *
	 * @return the playerName
	 */
	public String getPlayerName() {
		return playerName;
	}

	/**
	 * Sets the player name.
	 *
	 * @param playerName the playerName to set
	 */
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	
}
