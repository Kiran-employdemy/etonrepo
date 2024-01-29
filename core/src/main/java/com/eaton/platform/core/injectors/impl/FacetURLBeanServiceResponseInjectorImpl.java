package com.eaton.platform.core.injectors.impl;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.injectors.annotations.FacetURLBeanServiceResponseInjector;
import com.eaton.platform.core.models.SiteConfigModel;
import com.eaton.platform.core.models.eatonsiteconfig.EatonSiteConfigModel;
import com.eaton.platform.core.services.FacetURLBeanService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.apache.sling.models.spi.injectorspecific.AbstractInjectAnnotationProcessor2;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor2;
import org.apache.sling.models.spi.injectorspecific.StaticInjectAnnotationProcessorFactory;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

@Component(service = {Injector.class, StaticInjectAnnotationProcessorFactory.class},immediate = true,
property = {
         Constants.SERVICE_RANKING+"=5001"
})
public class FacetURLBeanServiceResponseInjectorImpl implements Injector, StaticInjectAnnotationProcessorFactory {


    public static final String FACET_URL_BEAN_SERVICE_INJECTOR_SOURCE = "facet-url-bean-service-injector";
    @Reference
    private FacetURLBeanService facetURLBeanService;
    @Override
    public String getName() {
        return FACET_URL_BEAN_SERVICE_INJECTOR_SOURCE;
    }

    @Override
    public Object getValue(final Object adaptable,final String name, final Type declaredType,final AnnotatedElement element,final DisposalCallbackRegistry callbackRegistry) {

        if (null == element.getAnnotation(FacetURLBeanServiceResponseInjector.class) || !(adaptable instanceof SlingHttpServletRequest)) {
            return null;
        }

        if (adaptable instanceof SlingHttpServletRequest) {
            SlingHttpServletRequest slingHttpServletRequest = ((SlingHttpServletRequest) adaptable);
            final EatonSiteConfigModel eatonSiteConfigModel = slingHttpServletRequest.adaptTo(EatonSiteConfigModel.class);
            if (null != slingHttpServletRequest && null != eatonSiteConfigModel && null != slingHttpServletRequest.getRequestPathInfo().getSelectors()){
                final SiteConfigModel siteConfig = eatonSiteConfigModel.getSiteConfig();
                if (null != siteConfig){
                    Resource contentResource =slingHttpServletRequest.getResource();
                    String contentResourcePath = contentResource.getPath();
                    String pageType = contentResource.getValueMap().get(CommonConstants.PAGE_TYPE, StringUtils.EMPTY);
                    return facetURLBeanService.getFacetURLBeanResponse(slingHttpServletRequest.getRequestPathInfo().getSelectors(), siteConfig.getPageSize(), pageType, contentResourcePath);
                }

            }
        }

        return null;
    }

    @Override
    public InjectAnnotationProcessor2 createAnnotationProcessor(final AnnotatedElement element) {
        final FacetURLBeanServiceResponseInjector annotation = element.getAnnotation(FacetURLBeanServiceResponseInjector.class);

        return null != annotation ? new FacetURLBeanServiceResponseInjectorProcessor(annotation): null;
    }

    private static class FacetURLBeanServiceResponseInjectorProcessor extends AbstractInjectAnnotationProcessor2 {
        private final FacetURLBeanServiceResponseInjector annotation;

        public FacetURLBeanServiceResponseInjectorProcessor(final FacetURLBeanServiceResponseInjector annotation) {
            this.annotation = annotation;
        }
        @Override
        public InjectionStrategy getInjectionStrategy() {
            return annotation.injectionStrategy();
        }
    }
}
