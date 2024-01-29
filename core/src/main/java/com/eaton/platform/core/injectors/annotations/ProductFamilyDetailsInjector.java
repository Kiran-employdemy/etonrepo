package com.eaton.platform.core.injectors.annotations;

import com.eaton.platform.core.injectors.impl.ProductFamilyDetailsInjectorImpl;
import com.eaton.platform.core.injectors.injectoremums.ProductFamilyDetailsInjectorProperties;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Sling Models Injector annotation for EatonPageInjector.
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@InjectAnnotation
@Source(ProductFamilyDetailsInjectorImpl.PRODUCT_FAMILY_INJECTOR)
public @interface ProductFamilyDetailsInjector {
    InjectionStrategy injectionStrategy() default InjectionStrategy.DEFAULT;
    ProductFamilyDetailsInjectorProperties type() default ProductFamilyDetailsInjectorProperties
            .PRODUCT_FAMILY_DETAIL_OBJECT;
}
