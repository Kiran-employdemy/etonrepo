package com.eaton.platform.core.services.converter;

/**
 * The Interface Converter.
 *
 * Basic interface for converters. Should be extended not used directly.
 *
 * @author Jaroslav Rassadin
 *
 * @param <S>
 *            the source generic type
 * @param <T>
 *            the target generic type
 */
public interface Converter<S, T> {

	/**
	 * Convert source to target.
	 *
	 * @param source
	 *            the source
	 * @return the target
	 */
	T convert(S source);

	/**
	 * Convert. Data will be put into provided target instance.
	 *
	 * @param source
	 *            the source
	 * @param target
	 *            the target
	 * @return the t
	 */
	T convert(S source, T target);
}
