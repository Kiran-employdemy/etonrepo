package com.eaton.platform.core.bean.search.find.replace.response;

import java.util.List;
import java.util.Objects;

/**
 * The Class BaseResultBean.
 *
 * @author Jaroslav Rassadin
 */
public class BaseResultBean {

	private ModificationOperationsResult operations;

	private List<ResultItem> results;

	/**
	 * Gets the operations.
	 *
	 * @return the operations
	 */
	public ModificationOperationsResult getOperations() {
		return this.operations;
	}

	/**
	 * Sets the operations.
	 *
	 * @param operations
	 *            the operations to set
	 */
	public void setOperations(final ModificationOperationsResult operations) {
		this.operations = operations;
	}

	/**
	 * Gets the results.
	 *
	 * @return the results
	 */
	public List<ResultItem> getResults() {
		return this.results;
	}

	/**
	 * Sets the results.
	 *
	 * @param results
	 *            the results to set
	 */
	public void setResults(final List<ResultItem> results) {
		this.results = results;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.operations, this.results);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final BaseResultBean other = (BaseResultBean) obj;
		return Objects.equals(this.operations, other.operations) && Objects.equals(this.results, other.results);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "BaseResultBean [operations=" + this.operations + ", results=" + this.results + "]";
	}

}
