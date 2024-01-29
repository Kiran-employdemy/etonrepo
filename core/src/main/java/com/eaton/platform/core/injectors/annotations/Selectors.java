package com.eaton.platform.core.injectors.annotations;

import com.eaton.platform.core.injectors.impl.SelectorInjector;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD,ElementType.FIELD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@InjectAnnotation
@Source(SelectorInjector.SELECTORS_INJECTOR)
public @interface Selectors {
    InjectionStrategy injectionStrategy() default InjectionStrategy.DEFAULT;
    int index() default 0;
}
