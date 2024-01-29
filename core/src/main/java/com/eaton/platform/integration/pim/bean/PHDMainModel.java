package com.eaton.platform.integration.pim.bean;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

@Model(adaptables = Resource.class)
public class PHDMainModel {

	@Inject @Optional
	private String	DESCRIPTION;
	
	@Inject @Optional
	private String ITEMSTATUS;
	
	@Inject @Optional
	private String VALUE;

	/**
	 * @return the dESCRIPTION
	 */
	public String getDESCRIPTION() {
		return DESCRIPTION;
	}

	/**
	 * @return the iTEMSTATUS
	 */
	public String getITEMSTATUS() {
		return ITEMSTATUS;
	}

	/**
	 * @return the vALUE
	 */
	public String getVALUE() {
		return VALUE;
	}
	
}
