package com.eaton.platform.core.injectors.impl;

import com.eaton.platform.core.injectors.annotations.Selectors;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.apache.sling.models.spi.injectorspecific.*;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Component(service = {Injector.class,StaticInjectAnnotationProcessorFactory.class},immediate = true,
        property = {
                Constants.SERVICE_RANKING+"=5004"
        }
)
public class SelectorInjector implements Injector, StaticInjectAnnotationProcessorFactory {

    public static final String SELECTORS_INJECTOR = "selectors-injector";

    @Override
    public String getName() {
        return SELECTORS_INJECTOR;
    }

    @Override
    public Object getValue( final Object adaptable, final String name, final Type declaredType,
                            final AnnotatedElement element, final DisposalCallbackRegistry callbackRegistry) {
        if (null == element.getAnnotation(Selectors.class) || !(adaptable instanceof SlingHttpServletRequest)){
            return null;
        }
        SlingHttpServletRequest request = (SlingHttpServletRequest) adaptable;
        if (null != request){
            int index = element.getAnnotation(Selectors.class).index();
            String[] selectors = request.getRequestPathInfo().getSelectors();
            if (null != selectors && selectors.length > index){
                if (declaredType instanceof Class){
                    return getValueOfNonParameterized(declaredType, index, selectors);
                }else if (declaredType instanceof ParameterizedType) {
                    return getValueOfParameterized((ParameterizedType) declaredType, selectors);
                }
                return null;
            }
        }
        return null;
    }

    private Object getValueOfParameterized(ParameterizedType declaredType, String[] selectors) {
        ParameterizedType parameterizedType = declaredType;
        if (parameterizedType.getActualTypeArguments().length == 1) {
            Class collectionType = (Class)parameterizedType.getRawType();
            if (collectionType.equals(Collection.class) || collectionType.equals(List.class)) {
                Object valuesArray = selectors;
                if (valuesArray != null) {
                    return Arrays.asList((Object[]) valuesArray);
                }
            }
        }
        return null;
    }

    private Object getValueOfNonParameterized(Type declaredType, int index, String[] selectors) {
        if ((index == 0 && declaredType == String.class) || (declaredType == String.class && selectors.length >= index)){
            return CommonUtil.decodeURLString(selectors[index]);
        }
        return selectors;
    }


    @Override
    public InjectAnnotationProcessor2 createAnnotationProcessor(AnnotatedElement element) {
        Selectors annotation = element.getAnnotation(Selectors.class);
        return null != annotation ? new SelectorsAnnotationProcessor(annotation):null;
    }

    private static class SelectorsAnnotationProcessor extends AbstractInjectAnnotationProcessor2{
        private final Selectors annotation;

        public InjectionStrategy getInjectionStrategy() {
            return this.annotation.injectionStrategy();
        }
        public SelectorsAnnotationProcessor(Selectors annotation) {this.annotation = annotation;}

        public int getIndex(){
            return this.annotation.index();
        }
    }
}
