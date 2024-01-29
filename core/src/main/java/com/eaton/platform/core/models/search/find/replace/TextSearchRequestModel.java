package com.eaton.platform.core.models.search.find.replace;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import com.eaton.platform.core.injectors.annotations.RequestParameter;

/**
 * The Class TextSearchRequestModel.
 *
 * @author Jaroslav Rassadin
 */
@Model(
		adaptables = SlingHttpServletRequest.class,
		defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TextSearchRequestModel {

	@RequestParameter
	private boolean caseSensitive;

	@RequestParameter(
			name = "specificPath")
	private String path;

	@RequestParameter
	private String replaceText;

	@RequestParameter(
			name = "searchText")
	private String searchText;

	@RequestParameter
	private boolean wholeWords;

	/**
	 * @return the caseSensitive
	 */
	public boolean isCaseSensitive() {
		return this.caseSensitive;
	}

	/**
	 * @param caseSensitive
	 *            the caseSensitive to set
	 */
	public void setCaseSensitive(final boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return this.path;
	}

	/**
	 * @param path
	 *            the path to set
	 */
	public void setPath(final String path) {
		this.path = path;
	}

	/**
	 * @return the replaceText
	 */
	public String getReplaceText() {
		return this.replaceText;
	}

	/**
	 * @param replaceText
	 *            the replaceText to set
	 */
	public void setReplaceText(final String replaceText) {
		this.replaceText = replaceText;
	}

	/**
	 * @return the searchText
	 */
	public String getSearchText() {
		return this.searchText;
	}

	/**
	 * @param searchText
	 *            the searchText to set
	 */
	public void setSearchText(final String searchText) {
		this.searchText = searchText;
	}

	/**
	 * @return the wholeWords
	 */
	public boolean isWholeWords() {
		return this.wholeWords;
	}

	/**
	 * @param wholeWords
	 *            the wholeWords to set
	 */
	public void setWholeWords(final boolean wholeWords) {
		this.wholeWords = wholeWords;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "TextSearchRequestModel [caseSensitive=" + this.caseSensitive + ", path=" + this.path + ", replaceText=" + this.replaceText + ", searchText="
				+ this.searchText
				+ ", wholeWords=" + this.wholeWords + "]";
	}

}
