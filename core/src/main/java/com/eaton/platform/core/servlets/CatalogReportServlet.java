package com.eaton.platform.core.servlets;


import com.eaton.platform.core.constants.CatalogReportConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.services.CatalogReportService;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component(service = Servlet.class,
        immediate = true,
        property = {
                ServletConstants.SLING_SERVLET_METHODS_GET,
                ServletConstants.SLING_SERVLET_RESOURCE_TYPES + "eaton/components/product/reports",
                ServletConstants.SLING_SERVLET_SELECTORS + "report",
                ServletConstants.SLING_SERVLET_EXTENSION_XLS
        })
public class CatalogReportServlet extends SlingAllMethodsServlet {


    @Reference
    private CatalogReportService catalogReportService;

    @Override
    protected final void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType(CatalogReportConstants.XLS_CONTENT_TYPE);
        final ResourceResolver resourceResolver = request.getResourceResolver();
        final String basePath = request.getParameter(CatalogReportConstants.PATH);
        final String selectedProperties = request.getParameter(CatalogReportConstants.PROPERTIES);
        final List<String> selectedPropertyList = getPropertList(selectedProperties);
        final Resource basePathresource = resourceResolver.getResource(basePath);
        final LinkedHashMap<String, ValueMap> translatedPathMap = fetchTranslatedList(resourceResolver);
        final ByteArrayOutputStream bos = catalogReportService.generateCatalogReport(basePathresource, translatedPathMap, selectedPropertyList);
        final byte[] encodeBytes = Base64.getEncoder().encode(bos.toByteArray());
        final ServletOutputStream servletOutputStream = response.getOutputStream();
        servletOutputStream.write(encodeBytes);
        bos.close();
        servletOutputStream.flush();
        servletOutputStream.close();

    }

    public static List<String> getPropertList(final String selectedProperties)
    {
        List<String> result = new ArrayList<>();
        if (StringUtils.isNotBlank(selectedProperties)) {
            String[] commaSeparatedArr = selectedProperties.split("\\s*,\\s*");
            result = Arrays.stream(commaSeparatedArr).collect(Collectors.toList());
        }
        return result;
    }

    private LinkedHashMap<String, ValueMap> fetchTranslatedList(final ResourceResolver resourceResolver) {
        final LinkedHashMap<String, ValueMap> translatedList = new LinkedHashMap();
        final Resource resource = resourceResolver.getResource(CatalogReportConstants.GLOBAL_LINK_SUBMISSION_PATH);
        if (null != resource && resource.hasChildren()) {
            final Iterator<Resource> resourceIterator = resource.listChildren();
            while (resourceIterator.hasNext()) {
                final Resource translationNode = resourceIterator.next();
                if (null != translationNode) {
                    final ValueMap valueMap = translationNode.getValueMap();
                    if (null != valueMap && valueMap.containsKey(CatalogReportConstants.CQ_STATUS)
                            && valueMap.containsKey(CatalogReportConstants.CQ_SOURCE_NODE_PATH)) {
                        final String cqStatus = valueMap.get(CatalogReportConstants.CQ_STATUS, StringUtils.EMPTY);
                        if (StringUtils.isNotBlank(cqStatus) && cqStatus.equals(CatalogReportConstants.IMPORTED)) {
                            final Resource targetNode = resourceResolver.resolve(translationNode.getPath().concat("/targets"));
                            if (null != targetNode) {
                                final Iterator<Resource> targetIterator = targetNode.listChildren();
                                while (targetIterator.hasNext()) {
                                    final Resource languageNode = targetIterator.next();
                                    if (null != languageNode) {
                                        final ValueMap languageMap = languageNode.getValueMap();
                                        if (null != languageMap && languageMap.containsKey(CatalogReportConstants.CQ_TARGET_NODE_PATH)) {
                                            translatedList.put(languageMap.get(CatalogReportConstants.CQ_TARGET_NODE_PATH, StringUtils.EMPTY), valueMap);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return translatedList;
    }

}
