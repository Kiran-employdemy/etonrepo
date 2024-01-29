package com.eaton.platform.core.services.msm.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * The Class RolloutConflictSearchResult.
 *
 * @author Jaroslav Rassadin
 */
public class RolloutConflictSearchResult {

	private boolean deep;

	private Map<String, RolloutConflictSearchResultItem> items = new HashMap<>();

	private String root;

	/**
	 * Instantiates a new rollout conflict search result.
	 */
	public RolloutConflictSearchResult() {
	}

	/**
	 * Instantiates a new rollout conflict search result.
	 *
	 * @param deep
	 *            the deep
	 * @param root
	 *            the root
	 */
	public RolloutConflictSearchResult(final boolean deep, final String root) {
		this.deep = deep;
		this.root = root;
	}

	/**
	 * Checks if is deep.
	 *
	 * @return the deep
	 */
	public boolean isDeep() {
		return this.deep;
	}

	/**
	 * Sets the deep.
	 *
	 * @param deep
	 *            the deep to set
	 */
	public void setDeep(final boolean deep) {
		this.deep = deep;
	}

	/**
	 * Adds the item.
	 *
	 * @param pagePath
	 *            the page path
	 * @param item
	 *            the item
	 */
	public void addItem(final String pagePath, final RolloutConflictSearchResultItem item) {
		this.items.put(pagePath, item);
	}

	/**
	 * Gets the items.
	 *
	 * @return the items
	 */
	public Map<String, RolloutConflictSearchResultItem> getItems() {
		return this.items;
	}

	/**
	 * Sets the items.
	 *
	 * @param items
	 *            the items to set
	 */
	public void setItems(final Map<String, RolloutConflictSearchResultItem> items) {
		this.items = items;
	}

	/**
	 * Gets the root.
	 *
	 * @return the root
	 */
	public String getRoot() {
		return this.root;
	}

	/**
	 * Sets the root.
	 *
	 * @param root
	 *            the root to set
	 */
	public void setRoot(final String root) {
		this.root = root;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();

		for (final Map.Entry<String, RolloutConflictSearchResultItem> item : this.getItems().entrySet()) {
			result.append("Page: " + item.getKey() + ":\n\n");

			if (StringUtils.isNotEmpty(item.getValue().getLiveCopyMsmMovedPath())) {
				result.append("Conflicting page:\n");
				result.append("\t" + item.getValue().getLiveCopyMsmMovedPath());
			}
			if (CollectionUtils.isNotEmpty(item.getValue().getMsmMovedComponentPaths())) {
				result.append("Conflicting resources:\n");
				item.getValue().getMsmMovedComponentPaths().forEach(v -> result.append("\t" + v + "\n"));
			}
		}

		return result.toString();
	}
}
