package com.eaton.platform.core.services.search.find.replace;

import javax.jcr.RepositoryException;

/**
 * The Interface RepositoryFunction.
 *
 * Function that throws {@link RepositoryException}. To be used in lambdas.
 *
 * @author Jaroslav Rassadin
 * @param <T>
 *            the generic type
 * @param <R>
 *            the generic type
 */
@FunctionalInterface
public interface RepositoryFunction<T, R> {

	/**
	 * Applies this function to the given argument.
	 *
	 * @param t
	 *            the function argument
	 * @return the function result
	 * @throws RepositoryException
	 *             the repository exception
	 */
	R apply(T t) throws RepositoryException;
}
