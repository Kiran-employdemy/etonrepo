package com.eaton.platform.core.injectors.impl;


import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.designer.Design;
import com.eaton.platform.core.injectors.annotations.CurrentPage;
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

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

@Component(service = {Injector.class,StaticInjectAnnotationProcessorFactory.class},immediate = true,
        property = {
                Constants.SERVICE_RANKING+"=5002"
        })
public class CurrentPageInjectorImpl extends EatonBaseInjector implements Injector, StaticInjectAnnotationProcessorFactory {
    public static final String CURRENT_PAGE_INJECTOR = "current-page-injector";
    private List<Object> dataTypes = Arrays.asList(String.class,  String[].class,Integer.class,Integer[].class, Boolean.class, Boolean[].class);

    @Override
    public String getName() {
        return CURRENT_PAGE_INJECTOR;
    }

    @Override
    public Object getValue(Object adaptable, String name, Type declaredType, AnnotatedElement element, DisposalCallbackRegistry callbackRegistry) {

        // sanity check
        if (element.getAnnotation(CurrentPage.class) == null) {
            return null;
        }

        // sanity check
        if (!(adaptable instanceof Resource || adaptable instanceof SlingHttpServletRequest)) {
            return null;
        }

        return getProperty(adaptable,name, declaredType);
    }
    protected Object getProperty(Object adaptable, String name, Type type) {
        Resource currentPageResource = getCurrentPageResource(adaptable);
        if (type instanceof Class) {
            if (type.equals(Page.class)){
                return getCurrentPage(adaptable);
            }else if (type.equals(Resource.class)){
                return currentPageResource;
            }else if (type.equals(Design.class)){
                return getCurrentDesign(adaptable);
            }else if(type.equals(InheritanceValueMap.class)){
                return new HierarchyNodeInheritanceValueMap(getResource(adaptable));
            }
        }

        if (currentPageResource != null) {
            return getInstance(currentPageResource, name, type);
        }

        return null;
    }

    @Override
    public InjectAnnotationProcessor2 createAnnotationProcessor(AnnotatedElement element) {
        final CurrentPage annotation = element.getAnnotation(CurrentPage.class);
        return null != annotation ? new CurrentPageAnnotationProcessor(annotation) : null;
    }

    private static class CurrentPageAnnotationProcessor extends AbstractInjectAnnotationProcessor2{
        private final CurrentPage annotation;

        public CurrentPageAnnotationProcessor(CurrentPage annotation) {
            this.annotation = annotation;
        }
        @Override
        public InjectionStrategy getInjectionStrategy() {
            return annotation.injectionStrategy();
        }

    }
}
