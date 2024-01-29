package com.eaton.platform.core.injectors.impl;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.injectors.annotations.ProductFamilyDetailsInjector;
import com.eaton.platform.core.injectors.injectoremums.ProductFamilyDetailsInjectorProperties;
import com.eaton.platform.core.services.EndecaRequestService;
import com.eaton.platform.core.services.ProductFamilyDetailService;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsBean;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsResponseBean;
import com.eaton.platform.integration.endeca.services.EndecaService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.apache.sling.models.spi.injectorspecific.AbstractInjectAnnotationProcessor2;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor2;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessorFactory2;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@Component(service = Injector.class,immediate = true,
property = {
         Constants.SERVICE_RANKING+"=4003"
})
public class ProductFamilyDetailsInjectorImpl implements Injector, InjectAnnotationProcessorFactory2 {

    public static final String PRODUCT_FAMILY_INJECTOR = "product_family_injector";
    private static final String CONF = "/conf";
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductFamilyDetailsInjectorImpl.class);

    @Reference
    ProductFamilyDetailService productFamilyDetailService;

    @Reference
    EndecaRequestService endecaRequestService;

    @Reference
    EndecaService endecaService;

    @Override
    public String getName() {
        return PRODUCT_FAMILY_INJECTOR;
    }

    @Override
    public Object getValue(final Object adaptable, final String name, final Type declaredType,
                           final AnnotatedElement element, final DisposalCallbackRegistry callbackRegistry) {
        final ProductFamilyDetailsInjector eatonPageInjector = element.getAnnotation(ProductFamilyDetailsInjector.class);
        Object returnObject = null;
        if (null != eatonPageInjector) {
            try {
                Optional<Page> containingPageOptional = Optional.empty();
                final ProductFamilyDetailsInjectorProperties type = eatonPageInjector.type();
                final SlingHttpServletRequest request = (SlingHttpServletRequest) adaptable;
                final PageManager pageManager = request.getResourceResolver().adaptTo(PageManager.class);
                final org.apache.sling.api.resource.Resource resource = request.getResource();
                if (null != resource && !resource.getPath().startsWith(CONF)) {
                    containingPageOptional = Optional.ofNullable(pageManager.getContainingPage(request.getResource()));
                } else {
                    final URI uri = new URI(request.getRequestURI());
                    final String path = uri.getPath();
                    containingPageOptional = Optional.ofNullable(request.getResourceResolver()
                            .resolve(path).adaptTo(Page.class));
                }
                if (containingPageOptional.isPresent()) {
                    switch (type) {
                        case PRODUCT_FAMILY_DETAIL_OBJECT:
                            returnObject = productFamilyDetailService
                                    .getProductFamilyDetailsBean(containingPageOptional.get());
                            break;
                        case PRODUCT_FAMILY_PIM_DETAIL_OBJECT:
                            final String[] selectors = request.getRequestPathInfo().getSelectors();
                            Optional<EndecaServiceRequestBean> endecaRequestBean = Optional
                                    .ofNullable(endecaRequestService.getEndecaRequestBean(containingPageOptional.get(), selectors, StringUtils.EMPTY));
                            if (endecaRequestBean.isPresent()) {
                                final SKUDetailsResponseBean skuDetailsResponseBean = endecaService
                                        .getSKUDetails(endecaRequestBean.get());
                                if (null != skuDetailsResponseBean.getSkuResponse().getSkuDetails()) {
                                    final SKUDetailsBean skuData = skuDetailsResponseBean.getSkuResponse().getSkuDetails();
                                    returnObject  = productFamilyDetailService.getProductFamilyPIMDetailsBean(skuData, containingPageOptional.get());
                                }
                            }
                            break;
                        default: break;
                    }
                }
            } catch (URISyntaxException e) {
                LOGGER.error("URISyntaxException in Product Family Details Injector", e);
            }
        }
        return returnObject;
    }

    @Override
    public InjectAnnotationProcessor2 createAnnotationProcessor(final Object o, final AnnotatedElement annotatedElement) {
        ProductFamilyDetailsInjector annotation = annotatedElement.getAnnotation(ProductFamilyDetailsInjector.class);
        if (null != annotation){
            return new ProductFamilyAnnotationProcessor(annotation);
        }
        return null;
    }

    private static class ProductFamilyAnnotationProcessor extends AbstractInjectAnnotationProcessor2 {
        private final ProductFamilyDetailsInjector annotation;
        ProductFamilyAnnotationProcessor(final ProductFamilyDetailsInjector annotation) {
            this.annotation = annotation;
        }
    }
}
