package com.eaton.platform.core.injectors.impl;

import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.designer.Design;
import com.day.cq.wcm.api.designer.Designer;
import com.eaton.platform.core.constants.CommonConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class EatonBaseInjector {
    private List<Object> dataTypes = Arrays.asList(String.class,  String[].class,Integer.class,Integer[].class, Boolean.class, Boolean[].class);
    private static final Logger LOG = LoggerFactory.getLogger(EatonBaseInjector.class);
    public ValueMap getPageProperties(Object adaptable){
        Page resourcePage = getCurrentPage(adaptable);
        if (null != resourcePage){
            return resourcePage.getProperties();
        }
        return  null;
    }
    public Page getCurrentPage(Object adaptable) {
        PageManager pageManager = getPageManager(adaptable);
        Resource resource = getResource(adaptable);
        if (null != pageManager && null != resource){
            // There is a problem here where sometimes resource.getPath() starts with /conf/eaton instead of /content
            // In this case we need to get the actual page instead of the /conf/eaton version of the page.
            // This seems to be causing a number of problems and errors in the log.
            if (StringUtils.startsWith(resource.getPath(), CommonConstants.STARTS_WITH_CONTENT)) {
                return pageManager.getContainingPage(resource);
            } else {
                SlingHttpServletRequest request = (SlingHttpServletRequest) adaptable;
                if (null != request){
                    return getPageByRequestURI(request.getPathInfo(),request,pageManager);
                }
            }
        }
        return null;
    }

    private Page getPageByRequestURI(String pathInfo, SlingHttpServletRequest request, PageManager pageManager){
        try {
			if (StringUtils.isNotEmpty(pathInfo) && null != pageManager){
				int index = pathInfo.indexOf(".");
				if (index >= 0) {
			    String pagePth = pathInfo.substring(0, index);
			    if (StringUtils.isNotEmpty(pagePth)){
			        return pageManager.getPage(pagePth);
			    }
				}
			}
		} catch (StringIndexOutOfBoundsException exception) {
			LOG.error("Exception occured in getPageByRequestURI method", exception);
		}
        return  null;
    }
    public PageManager getPageManager(Object adaptable) {
        ResourceResolver resolver = getResourceResolver(adaptable);

        if (null != resolver) {
            return resolver.adaptTo(PageManager.class);
        }

        return null;
    }
    public ResourceResolver getResourceResolver(Object adaptable) {
        if (adaptable instanceof SlingHttpServletRequest) {
            return ((SlingHttpServletRequest) adaptable).getResourceResolver();
        }
        if (adaptable instanceof Resource) {
            return ((Resource) adaptable).getResourceResolver();
        }

        return null;
    }
    public Resource getResource(Object adaptable) {
        if (adaptable instanceof SlingHttpServletRequest) {
            return ((SlingHttpServletRequest) adaptable).getResource();
        }
        if (adaptable instanceof Resource) {
            return (Resource) adaptable;
        }
        return null;
    }

    public TagManager getTagManager(Object adaptable){
        ResourceResolver resourceResolver = getResourceResolver(adaptable);
        return null != resourceResolver ? resourceResolver.adaptTo(TagManager.class):null;
    }
    public Resource getCurrentPageResource(Object adaptable){
        final Page currentPage = getCurrentPage(adaptable);
        if (currentPage != null) {
            return currentPage.getContentResource();
        } else {
            return null;
        }
    }
    public Design getCurrentDesign(Object adaptable) {
        Page currentPage = getCurrentPage(adaptable);
        Designer designer = getDesigner(adaptable);

        if (null != currentPage && null != designer) {
            return designer.getDesign(currentPage);
        }

        return null;
    }
    public Designer getDesigner(Object adaptable) {
        ResourceResolver resolver = getResourceResolver(adaptable);
        if (null != resolver) {
            return resolver.adaptTo(Designer.class);
        }
        return null;
    }
    public Object getInstance(Resource resource, String name, Type type) {
        ValueMap valueMap = resource.getValueMap();
        if (type instanceof Class) {
            if (dataTypes.contains(type)){
                return valueMap.get(name, (Class) type);
            }else {
                return resource.adaptTo((Class) type);
            }
        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            if (parameterizedType.getActualTypeArguments().length == 1) {
                Class collectionType = (Class) parameterizedType.getRawType();
                if (collectionType.equals(Collection.class) || collectionType.equals(List.class)) {
                    Class itemType = (Class) parameterizedType.getActualTypeArguments()[0];
                    Object valuesArray = valueMap.get(name, Array.newInstance(itemType, 0).getClass());
                    if (valuesArray != null) {
                        return Arrays.asList((Object[]) valuesArray);
                    }
                }
            }
        }
        return null;
    }

}
