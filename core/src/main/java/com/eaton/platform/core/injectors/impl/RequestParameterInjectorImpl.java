package com.eaton.platform.core.injectors.impl;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.apache.sling.models.spi.injectorspecific.AbstractInjectAnnotationProcessor2;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor2;
import org.apache.sling.models.spi.injectorspecific.StaticInjectAnnotationProcessorFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceRanking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.injectors.annotations.RequestParameter;

/**
 * The Class RequestParameterInjectorImpl.
 *
 * Injects request parameters into Sling model fields annotated with {@link RequestParameter}.
 *
 * @author Jaroslav Rassadin
 */
@Component(
		service = { Injector.class, StaticInjectAnnotationProcessorFactory.class },
		immediate = true)
@ServiceRanking(
		value = Integer.MIN_VALUE)
public class RequestParameterInjectorImpl implements Injector, StaticInjectAnnotationProcessorFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(RequestParameterInjectorImpl.class);

	private static final String NAME = "request-parameter";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getValue(final Object adaptable, final String name, final Type declaredType, final AnnotatedElement element,
			final DisposalCallbackRegistry callbackRegistry) {

		Object value = null;

		try {
			final RequestParameter annotation = element.getAnnotation(RequestParameter.class);
			if ((annotation == null) || !(adaptable instanceof SlingHttpServletRequest)) {
				return null;
			}
			final SlingHttpServletRequest request = (SlingHttpServletRequest) adaptable;

			final String paramName = this.getName(name, annotation);

			if (declaredType instanceof Class<?>) {
				value = this.getValue((Class<?>) declaredType, request, paramName);

			} else if (declaredType instanceof ParameterizedType) {
				final ParameterizedType parameterizedType = (ParameterizedType) declaredType;
				final Class<?> clazz = (Class<?>) parameterizedType.getRawType();
				final Class<?> paramClass = (Class<?>) parameterizedType.getActualTypeArguments()[0];

				if (clazz.isAssignableFrom(List.class) && paramClass.isAssignableFrom(String.class)) {
					value = this.getValues(paramClass, request, this.getName(name, annotation));
				}
			} else {
				// ignore and return null
			}
			// it is fine for injector to catch any exception here and return null
		} catch (final Exception ex) {
			LOGGER.error("An error has occurred while injecting request parameter.", ex);
			value = null;
		}
		return value;
	}

	private String getName(final String name, final RequestParameter annotation) {
		if (StringUtils.isNotEmpty(annotation.name())) {
			return annotation.name();
		}
		return name;
	}

	private Object getValue(final Class<?> clazz, final SlingHttpServletRequest request, final String paramName) {
		final String paramStrValue = request.getParameter(paramName);
		final Object paramValue = this.parseValue(clazz, paramStrValue);

		LOGGER.error("Parameter name: {}, value: {}", paramName, paramValue);

		return paramValue;
	}

	private List<Object> getValues(final Class<?> clazz, final SlingHttpServletRequest request, final String paramName) {
		final String[] paramValuesArr = request.getParameterValues(paramName);
		List<Object> valuesList = null;

		if (paramValuesArr != null) {

			if (paramValuesArr.length == 1) {
				valuesList = this.getValuesFromSingleParam(clazz, paramValuesArr[0]);

			} else {
				valuesList = this.getValuesFromMultiParam(clazz, paramValuesArr);
			}
		}
		LOGGER.error("Parameter name: {}, values: {}", paramName, valuesList);

		return valuesList;
	}

	private List<Object> getValuesFromSingleParam(final Class<?> clazz, final String paramValue) {
		if (paramValue.contains(CommonConstants.COMMA)) {
			return Arrays.asList(paramValue.split(",")).stream().map(v -> this.parseValue(clazz, v)).collect(Collectors.toList());

		} else {
			return Collections.singletonList(this.parseValue(clazz, paramValue));
		}
	}

	private List<Object> getValuesFromMultiParam(final Class<?> clazz, final String[] paramValuesArr) {
		return Arrays.asList(paramValuesArr).stream().map(v -> this.parseValue(clazz, v)).collect(Collectors.toList());
	}

	private Object parseValue(final Class<?> clazz, final String paramStrValue) {
		if (clazz.isAssignableFrom(String.class)) {
			return paramStrValue;

		} else if (clazz.isAssignableFrom(Boolean.class)) {
			return Boolean.valueOf(paramStrValue);

		} else {
			// add more types here if necessary
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InjectAnnotationProcessor2 createAnnotationProcessor(final AnnotatedElement element) {
		final RequestParameter annotation = element.getAnnotation(RequestParameter.class);
		if (annotation != null) {
			return new RequestParameterAnnotationProcessor(annotation);
		}
		return null;
	}

	private static class RequestParameterAnnotationProcessor extends AbstractInjectAnnotationProcessor2 {
		private final RequestParameter annotation;

		public RequestParameterAnnotationProcessor(final RequestParameter annotation) {
			this.annotation = annotation;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public InjectionStrategy getInjectionStrategy() {
			return this.annotation.injectionStrategy();
		}
	}

}
