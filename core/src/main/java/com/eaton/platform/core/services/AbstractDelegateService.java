package com.eaton.platform.core.services;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaton.platform.core.exception.EatonSystemException;

/**
 * The Class AbstractDelegateService.
 *
 * @author Jaroslav Rassadin
 * @param <T>
 *            the generic type
 * @param <V>
 *            the value type
 */
public abstract class AbstractDelegateService<T extends DelegableService<V>, V> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDelegateService.class);

	/**
	 * Gets the services.
	 *
	 * @return the services
	 */
	protected abstract List<T> getServices();

	/**
	 * Gets the service.
	 *
	 * @param value
	 *            the value
	 * @return the service
	 */
	protected T getService(final V value) {

		if (value == null) {
			throw new EatonSystemException(StringUtils.EMPTY, "Value can not be null.");
		}
		final Optional<T> optional = this.getServices().stream().filter(s -> s.canProcess(value)).findFirst();

		if (!optional.isPresent()) {
			throw new EatonSystemException(StringUtils.EMPTY, String.format("Can not get ws service for value %s.", value));
		} else {
			LOGGER.debug("Service {} found for value {}", optional.get().getClass(), value);
		}
		return optional.get();
	}

	/**
	 * Bind service.
	 *
	 * @param service
	 *            the service
	 */
	protected void bindService(final T service) {

		synchronized (this.getServices()) {
			this.getServices().add(service);
		}
	}

	/**
	 * Unbind service.
	 *
	 * @param service
	 *            the service
	 */
	protected void unbindService(final T service) {

		synchronized (this.getServices()) {
			this.getServices().remove(service);
		}
	}
}
