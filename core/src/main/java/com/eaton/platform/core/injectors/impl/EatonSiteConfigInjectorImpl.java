package com.eaton.platform.core.injectors.impl;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.injectors.annotations.EatonSiteConfigInjector;
import com.eaton.platform.core.services.EatonSiteConfigService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.apache.sling.models.spi.injectorspecific.AbstractInjectAnnotationProcessor2;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor2;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessorFactory2;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

@Component(service = Injector.class,immediate = true,
property = {
         Constants.SERVICE_RANKING+"=4004"
})
public class EatonSiteConfigInjectorImpl implements Injector, InjectAnnotationProcessorFactory2 {

    public static final String EATON_SITE_CONFIG_INJECT = "eaton_site_config_injector";

    @Reference
    EatonSiteConfigService eatonSiteConfigService;

    @Override
    public String getName() {
        return EATON_SITE_CONFIG_INJECT;
    }

    @Override
    public Object getValue(final Object adaptable, final String name, final Type declaredType,
                           final AnnotatedElement element, final DisposalCallbackRegistry callbackRegistry) {
        final EatonSiteConfigInjector eatonPageInjector = element.getAnnotation(EatonSiteConfigInjector.class);
        Object returnObject = null;
        if (null != eatonPageInjector) {
            final SlingHttpServletRequest request = (SlingHttpServletRequest) adaptable;
            final PageManager pageManager = request.getResourceResolver().adaptTo(PageManager.class);
            final Page containingPage = pageManager.getContainingPage(request.getResource());
            if (null != eatonSiteConfigService) {
                returnObject = eatonSiteConfigService.getSiteConfig(containingPage);
            }
        }
        return returnObject;
    }

    @Override
    public InjectAnnotationProcessor2 createAnnotationProcessor(final Object o, final AnnotatedElement annotatedElement) {
        EatonSiteConfigInjector annotation = annotatedElement.getAnnotation(EatonSiteConfigInjector.class);
        if (null != annotation){
            return new EatonSiteConfigAnnotationProcessor(annotation);
        }
        return null;
    }

    private static class EatonSiteConfigAnnotationProcessor extends AbstractInjectAnnotationProcessor2 {
        private final EatonSiteConfigInjector annotation;
        EatonSiteConfigAnnotationProcessor(final EatonSiteConfigInjector annotation) {
            this.annotation = annotation;
        }
    }
}
