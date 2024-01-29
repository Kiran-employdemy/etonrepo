package com.eaton.platform.core.services.search.find.replace.tags.impl;

import javax.jcr.ValueFactory;

import org.apache.sling.api.resource.Resource;

import com.eaton.platform.core.bean.search.find.replace.request.TagsModificationRequestBean;

/**
 * The Class ModificationInfoHolder.
 *
 * @author Jaroslav Rassadin
 */
class ModificationInfoHolder {

	private TagsModificationRequestBean modificationRequest;

	private final Resource containingResource;

	private Resource resource;

	private ValueFactory valueFactory;

	/**
	 * Instantiates a new modification info holder.
	 *
	 * @param modificationRequest
	 *            the modification request
	 * @param resource
	 *            the resource
	 * @param valueFactory
	 *            the value factory
	 */
	public ModificationInfoHolder(final TagsModificationRequestBean modificationRequest, final Resource containingResource, final Resource resource,
			final ValueFactory valueFactory) {
		this.modificationRequest = modificationRequest;
		this.containingResource = containingResource;
		this.resource = resource;
		this.valueFactory = valueFactory;
	}

	/**
	 * Gets the modification request.
	 *
	 * @return the modificationRequest
	 *
	 */
	public TagsModificationRequestBean getModificationRequest() {
		return this.modificationRequest;
	}

	/**
	 * Sets the modification request.
	 *
	 * @param modificationRequest
	 *            the modificationRequest to set
	 */
	public void setModificationRequest(final TagsModificationRequestBean modificationRequest) {
		this.modificationRequest = modificationRequest;
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
	 * Sets the resource.
	 *
	 * @param resource
	 *            the resource to set
	 */
	public void setResource(final Resource resource) {
		this.resource = resource;
	}

	/**
	 * Gets the value factory.
	 *
	 * @return the valueFactory
	 */
	public ValueFactory getValueFactory() {
		return this.valueFactory;
	}

	/**
	 * Sets the value factory.
	 *
	 * @param valueFactory
	 *            the valueFactory to set
	 */
	public void setValueFactory(final ValueFactory valueFactory) {
		this.valueFactory = valueFactory;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "ModificationInfoHolder [modificationRequest=" + this.modificationRequest + ", containingResource=" + this.containingResource + ", resource="
				+ this.resource
				+ ", valueFactory=" + this.valueFactory + "]";
	}

}
