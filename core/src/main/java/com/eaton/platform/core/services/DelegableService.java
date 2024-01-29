package com.eaton.platform.core.services;

/**
 * The Interface DelegableService.
 *
 * @author Jaroslav Rassadin
 * @param <V>
 *            the value type
 */
public interface DelegableService<V> {

	/**
	 * Checks if provided value can be processed.
	 *
	 * @param value
	 *            the value
	 * @return true, if provided value can be processed.
	 */
	boolean canProcess(V value);
}
