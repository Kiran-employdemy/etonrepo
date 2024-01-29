package com.eaton.platform.core.bean.search.find.replace.request;

import java.util.Objects;

/**
 * The Class TextSearchRequestBean.
 *
 * @author Jaroslav Rassadin
 */
public class TextSearchRequestBean {

	private boolean caseSensitive;

	private ContentType contentType;

	private String path;

	private String replaceText;

	private String searchText;

	private boolean wholeWords;

	/**
	 * Instantiates a new text search request bean.
	 */
	public TextSearchRequestBean() {

	}

	/**
	 * Instantiates a new text search request bean.
	 *
	 * @param source
	 *            the source
	 */
	public TextSearchRequestBean(final TextSearchRequestBean source) {
		this.caseSensitive = source.isCaseSensitive();
		this.contentType = source.getContentType();
		this.path = source.getPath();
		this.replaceText = source.getReplaceText();
		this.searchText = source.getSearchText();
		this.wholeWords = source.isWholeWords();
	}

	/**
	 * Checks if is case sensitive.
	 *
	 * @return the caseSensitive
	 */
	public boolean isCaseSensitive() {
		return this.caseSensitive;
	}

	/**
	 * Sets the case sensitive.
	 *
	 * @param caseSensitive
	 *            the caseSensitive to set
	 */
	public void setCaseSensitive(final boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	/**
	 * Gets the content type.
	 *
	 * @return the contentType
	 */
	public ContentType getContentType() {
		return this.contentType;
	}

	/**
	 * Sets the content type.
	 *
	 * @param contentType
	 *            the contentType to set
	 */
	public void setContentType(final ContentType contentType) {
		this.contentType = contentType;
	}

	/**
	 * Gets the path.
	 *
	 * @return the path
	 */
	public String getPath() {
		return this.path;
	}

	/**
	 * Sets the path.
	 *
	 * @param path
	 *            the path to set
	 */
	public void setPath(final String path) {
		this.path = path;
	}

	/**
	 * Gets the replace text.
	 *
	 * @return the replaceText
	 */
	public String getReplaceText() {
		return this.replaceText;
	}

	/**
	 * Sets the replace text.
	 *
	 * @param replaceText
	 *            the replaceText to set
	 */
	public void setReplaceText(final String replaceText) {
		this.replaceText = replaceText;
	}

	/**
	 * Gets the search text.
	 *
	 * @return the searchText
	 */
	public String getSearchText() {
		return this.searchText;
	}

	/**
	 * Sets the search text.
	 *
	 * @param searchText
	 *            the searchText to set
	 */
	public void setSearchText(final String searchText) {
		this.searchText = searchText;
	}

	/**
	 * Checks if is whole words.
	 *
	 * @return the wholeWords
	 */
	public boolean isWholeWords() {
		return this.wholeWords;
	}

	/**
	 * Sets the whole words.
	 *
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
	public int hashCode() {
		return Objects.hash(this.caseSensitive, this.contentType, this.path, this.replaceText, this.searchText, this.wholeWords);
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
		final TextSearchRequestBean other = (TextSearchRequestBean) obj;
		return (this.caseSensitive == other.caseSensitive) && (this.contentType == other.contentType) && Objects.equals(this.path, other.path)
				&& Objects.equals(this.replaceText, other.replaceText) && Objects.equals(this.searchText, other.searchText)
				&& (this.wholeWords == other.wholeWords);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "TextSearchRequestBean [caseSensitive=" + this.caseSensitive + ", contentType=" + this.contentType + ", path=" + this.path + ", replaceText="
				+ this.replaceText
				+ ", searchText=" + this.searchText + ", wholeWords=" + this.wholeWords + "]";
	}

}
