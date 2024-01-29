package com.eaton.platform.core.models.error;

import java.io.Serializable;
import java.util.List;

/**
 * The Class GenericErrorsModel.
 *
 * @author Jaroslav Rassadin
 */
public class GenericErrorsModel implements Serializable {

	private static final long serialVersionUID = 1785193230586226921L;

	private List<GenericErrorModel> errors;

	/**
	 * Instantiates a new generic errors model.
	 */
	public GenericErrorsModel() {
	}

	/**
	 * Instantiates a new generic errors model.
	 *
	 * @param errors
	 *            the errors
	 */
	public GenericErrorsModel(final List<GenericErrorModel> errors) {
		this.errors = errors;
	}

	/**
	 * Gets the errors.
	 *
	 * @return the errors
	 */
	public List<GenericErrorModel> getErrors() {
		return this.errors;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "GenericErrorsModel [errors=" + this.errors + "]";
	}

}
