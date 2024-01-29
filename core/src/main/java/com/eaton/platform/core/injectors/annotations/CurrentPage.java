
package com.eaton.platform.core.injectors.annotations;

import com.eaton.platform.core.injectors.impl.CurrentPageInjectorImpl;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Sling Models Injector annotation for Current Page Properties.
 * @document https://eatonprojects.atlassian.net/wiki/spaces/B2/pages/88965177/CurrentPage
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@InjectAnnotation
@Source(CurrentPageInjectorImpl.CURRENT_PAGE_INJECTOR)
public @interface CurrentPage {
    InjectionStrategy injectionStrategy() default InjectionStrategy.DEFAULT;

}
