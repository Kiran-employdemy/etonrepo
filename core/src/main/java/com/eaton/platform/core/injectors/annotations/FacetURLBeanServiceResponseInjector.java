package com.eaton.platform.core.injectors.annotations;

import com.eaton.platform.core.injectors.impl.FacetURLBeanServiceResponseInjectorImpl;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Sling Models Injector annotation for FacetURLBeanServiceResponseInjector Properties.
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@InjectAnnotation
@Source(FacetURLBeanServiceResponseInjectorImpl.FACET_URL_BEAN_SERVICE_INJECTOR_SOURCE)
public @interface FacetURLBeanServiceResponseInjector {
    InjectionStrategy injectionStrategy() default InjectionStrategy.DEFAULT;
}
