package com.eaton.platform.core.services.search.find.replace.text.impl;

import org.apache.sling.api.resource.Resource;

import com.eaton.platform.core.bean.search.find.replace.request.TextModificationRequestBean;

/**
 * The Class ModificationInfoHolder.
 *
 * @author Jaroslav Rassadin
 */
class ModificationInfoHolder {

	private final TextModificationRequestBean modificationRequest;

	private Resource containingResource;

	private final Resource resource;

	/**
	 * Instantiates a new modification info holder.
	 *
	 * @param modificationRequest
	 *            the modification request
	 * @param resource
	 *            the resource
	 */
	public ModificationInfoHolder(final TextModificationRequestBean modificationRequest, final Resource resource) {
		this.modificationRequest = modificationRequest;
		this.resource = resource;
	}

	/**
	 * Instantiates a new modification info holder.
	 *
	 * @param modificationRequest
	 *            the modification request
	 * @param containingResource
	 *            the containing resource
	 * @param resource
	 *            the resource
	 */
	public ModificationInfoHolder(final TextModificationRequestBean modificationRequest, final Resource containingResource, final Resource resource) {
		this.modificationRequest = modificationRequest;
		this.containingResource = containingResource;
		this.resource = resource;

	}

	/**
	 * Gets the modification request.
	 *
	 * @return the modificationRequest
	 */
	public TextModificationRequestBean getModificationRequest() {
		return this.modificationRequest;
	}

	/**
	 * Gets the containing resource.
	 *
	 * @return the containingResource
	 */
	public Resource getContainingResource() {
		return this.containingResource;
	}

	/**
	 * Gets the resource.
	 *
	 * @return the resource
	 */
	public Resource getResource() {
		return this.resource;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "ModificationInfoHolder [modificationRequest=" + this.modificationRequest + ", containingResource=" + this.containingResource + ", resource="
				+ this.resource
				+ "]";
	}

}
