package com.eaton.platform.core.util;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.wrappers.ResourceResolverWrapper;

import io.wcm.testing.mock.aem.junit5.AemContext;

/**
 * The Class NonClosableResourceResolverWrapper.
 *
 * This wrapper allows to inject {@link ResourceResolver} created by {@link AemContext} into methods with try-with-resources blocks. {@link AemContext} will
 * close {@link ResourceResolver} after test execution.
 *
 * @author Jaroslav Rassadin
 */
public class NonClosableResourceResolverWrapper extends ResourceResolverWrapper {

	/**
	 * Instantiates a new non closable resource resolver wrapper.
	 *
	 * @param resolver
	 *            the resolver
	 */
	public NonClosableResourceResolverWrapper(final ResourceResolver resolver) {
		super(resolver);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() {
		// do nothing
	}
}
