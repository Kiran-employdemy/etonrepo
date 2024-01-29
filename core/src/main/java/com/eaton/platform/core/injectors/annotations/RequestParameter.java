package com.eaton.platform.core.injectors.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotation;

@Documented
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@InjectAnnotation
@Source("request-parameter")

/**
 * The Interface RequestParameter.
 *
 * Annotation to be used on fields let Sling Models inject a request parameter as string or list of strings.
 *
 * @author Jaroslav Rassadin
 */
public @interface RequestParameter {

	/**
	 * Specifies the name of the request attribute. If empty or not set, then the name is derived from the method or field.
	 *
	 * @return the name
	 */
	String name() default "";

	/**
	 * if set to REQUIRED injection is mandatory, if set to OPTIONAL injection is optional, in case of DEFAULT the standard annotations
	 * ({@link org.apache.sling.models.annotations.Optional}, {@link org.apache.sling.models.annotations.Required}) are used. If even those are not available
	 * the default injection strategy defined on the {@link org.apache.sling.models.annotations.Model} applies. Default value = DEFAULT.
	 *
	 * @return the injection strategy
	 */
	InjectionStrategy injectionStrategy() default InjectionStrategy.DEFAULT;
}
