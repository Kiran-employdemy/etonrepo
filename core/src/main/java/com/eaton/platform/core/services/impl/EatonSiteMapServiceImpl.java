package com.eaton.platform.core.services.impl;

import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.SiteMapConstants;
import com.eaton.platform.core.models.PIMResourceSlingModel;
import com.eaton.platform.core.models.SiteConfigModel;
import com.eaton.platform.core.models.eatonsiteconfig.EatonSiteConfigModel;
import com.eaton.platform.core.services.EatonSiteMapService;
import com.eaton.platform.core.services.EndecaRequestService;
import com.eaton.platform.core.services.config.EatonSiteMapServiceConfig;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.familymodule.FamilyModuleBean;
import com.eaton.platform.integration.endeca.bean.familymodule.SKUListResponseBean;
import com.eaton.platform.integration.endeca.bean.subcategory.FamilyListResponseBean;
import com.eaton.platform.integration.endeca.bean.subcategory.ProductFamilyBean;
import com.eaton.platform.integration.endeca.services.EndecaService;
import java.util.Optional;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.query.Query;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

/**
 * The Class ConfigServiceImpl.
 */
@Designate(ocd = EatonSiteMapServiceConfig.class)
@Component(service = EatonSiteMapService.class, immediate = true,
        property = {
                AEMConstants.SERVICE_VENDOR_EATON,
                AEMConstants.SERVICE_DESCRIPTION + "EatonSiteMapServiceImpl",
                AEMConstants.PROCESS_LABEL + "EatonSiteMapServiceImpl"
        })
public class EatonSiteMapServiceImpl implements EatonSiteMapService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EatonConfigServiceImpl.class);
    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd");


    @Reference
    private Externalizer externalizer;

    @Reference
    private EndecaService endecaService;

    @Reference
    private EndecaRequestService endecaRequestService;



    private String[] changefreqProperties;

    private String[] priorityProperties;

    private String externalizerDomain;

    private String pageType;

    private String resourceType;

    private String descendantPagePath;


    @Activate
    @Modified
    protected void activate(final EatonSiteMapServiceConfig config) {
        this.externalizerDomain = config.externalizer_domain();
        this.changefreqProperties = config.changefreq_properties();
        this.priorityProperties = config.priority_properties();
        this.pageType = config.pageType();
        this.resourceType = config.resourceType();
        this.descendantPagePath = config.descendantPagePath();
    }


    @Override
    public void getSubCategorySiteMap(final ResourceResolver resourceResolver,
                                      final XMLStreamWriter stream) {
        try {
            final StringBuilder queryStringBuilder = new StringBuilder();
            queryStringBuilder.append(SiteMapConstants.QUERY_STRING_SELECT)
                    .append(pageType)
                    .append(SiteMapConstants.QUERY_STRING_AND)
                    .append(resourceType)
                    .append(SiteMapConstants.QUERY_STRING_DESCENDANT_NODE)
                    .append(descendantPagePath)
                    .append(SiteMapConstants.QUERY_STRING_COMPLETE);
            final Iterator results = resourceResolver.findResources(queryStringBuilder.toString(),
                    Query.JCR_SQL2);
            while (results.hasNext()) {
                final Resource resource = (Resource) results.next();
                final Page page = resource.getParent().adaptTo(Page.class);
                if (null != page && (!isHidden(page))) {
                    write(resourceResolver, stream, page, SiteMapConstants.SUB_CATEGORY, SiteMapConstants.XML_EXT,
                            Boolean.FALSE);
                }
            }
            stream.writeEndElement();
            stream.writeEndDocument();
        } catch (XMLStreamException xmlExc) {
            LOGGER.error(SiteMapConstants.XML_STREAM_EXCEPTION, xmlExc);
        }
    }

    @Override
    public void getProductFamilySiteMap(final ResourceResolver resourceResolver,
                                        final XMLStreamWriter stream,
                                        final String resourcePath,
                                        final SlingHttpServletRequest request) {
        try {
            final Page page = resourceResolver.resolve(resourcePath).getParent().adaptTo(Page.class);
            String[] selectors = request.getRequestPathInfo().getSelectors();
            final EndecaServiceRequestBean endecaRequestBean =
                    endecaRequestService.getEndecaRequestBean( page, selectors,
                    StringUtils.EMPTY);
            final FamilyListResponseBean productFamilyList = endecaService.getProductFamilyList(endecaRequestBean);
            final List<ProductFamilyBean> productFamilyBeanList =
                    productFamilyList.getResponse().getProductFamilyBean();
            if (CollectionUtils.isNotEmpty(productFamilyBeanList)) {
                writeProductFamilySiteMap(productFamilyBeanList, resourceResolver, stream);
                stream.writeEndElement();
                stream.writeEndDocument();
            }
        } catch (XMLStreamException xmlExc) {
            LOGGER.error(SiteMapConstants.XML_STREAM_EXCEPTION_WHILE_WRITING_THE_PRODUCT_FAMILY_SITE_MAP, xmlExc);
        }
    }

    private String getPagePath(final ProductFamilyBean productFamilyBean) {
        String pagePath = StringUtils.EMPTY;
        final String pageURL = productFamilyBean.getUrl();
        try {
            final URL url = new URL(pageURL);
            pagePath = url.getPath();
            if (pagePath.contains(CommonConstants.HTML_EXTN)) {
                pagePath = pagePath.replaceAll(CommonConstants.HTML_EXTN, StringUtils.EMPTY);
            }
        } catch (MalformedURLException urlExc) {
            LOGGER.error(SiteMapConstants.MALFORMED_URLEXCEPTION_EXPCEPTION_WHILE_RETREIVING_THE_PAGE_PATH, urlExc);
        }
        return pagePath;
    }

    private void write(final ResourceResolver resourceResolver, final XMLStreamWriter stream,
                       final Page productFamilyPage, final String selector, final String extension,
                       final Boolean isSkuPage)  {
        try {
            stream.writeStartElement(SiteMapConstants.NS, SiteMapConstants.URL);
            final String loc = externalizer.externalLink(resourceResolver, externalizerDomain,
                    productFamilyPage.getPath());
            final StringBuilder pathName = new StringBuilder();
            pathName.append(loc).append(selector).append(extension);
            writeElement(stream, SiteMapConstants.LOC, pathName.toString());
            final Calendar lastModified = productFamilyPage.getLastModified();
            if (null != lastModified) {
                writeElement(stream, SiteMapConstants.LASTMOD, DATE_FORMAT.format(lastModified));
            }
            if (!isSkuPage) {
                final ValueMap valueMap = productFamilyPage.getProperties();
                writeFirstPropertyValue(stream, SiteMapConstants.CHANGEFREQ, changefreqProperties,
                        valueMap);
                writeFirstPropertyValue(stream, SiteMapConstants.PRIORITY, priorityProperties,
                        valueMap);
                stream.writeEndElement();
            }
        } catch (XMLStreamException xmlExc) {
           LOGGER.error(SiteMapConstants.XML_STREAM_EXCEPTION_WHILE_WRITING_THE_PRODUCT_FAMILY_SITE_MAP, xmlExc);
        }

    }

    @Override
    public void getProductSkuSiteMap(final ResourceResolver resourceResolver, final XMLStreamWriter stream,
                                     final Optional<EatonSiteConfigModel> eatonSiteConfigModelOptional,
                                     final String resourcePath, final SlingHttpServletRequest request) {
        try {
            final Page page = resourceResolver.resolve(resourcePath).getParent().adaptTo(Page.class);
            final String inventoryId = getInventoryId(page, resourceResolver);
            String[] selectors = request.getRequestPathInfo().getSelectors();
            final EndecaServiceRequestBean endecaServiceRequestBean =
                    endecaRequestService.getEndecaRequestBean(page, selectors, inventoryId);
            final SKUListResponseBean productTypeListResponseBean = endecaService.getSKUList(endecaServiceRequestBean);
            final List<FamilyModuleBean> familyModuleList =
                    productTypeListResponseBean.getFamilyModuleResponse().getFamilyModule();
            final SiteConfigModel siteConfig = getSiteConfig(eatonSiteConfigModelOptional);
            if (null != siteConfig) {
                final String pagePath = siteConfig.getSkuPageURL();
                if (CollectionUtils.isNotEmpty(familyModuleList)) {
                    final ValueMap valueMap = page.getProperties();
                    writeProductSkuSiteMap(familyModuleList, resourceResolver, stream, pagePath, valueMap);
                    stream.writeEndElement();
                    stream.writeEndDocument();
                }
            }
        } catch (XMLStreamException xmlExc) {
            LOGGER.error(SiteMapConstants.XML_STREAM_EXCEPTION_WHILE_WRITING_THE_PRODUCT_FAMILY_SITE_MAP, xmlExc);
        }
    }


    private void writeElement(final XMLStreamWriter stream, final String elementName, final String text)
            throws XMLStreamException {
        stream.writeStartElement(SiteMapConstants.NS, elementName);
        stream.writeCharacters(text);
        stream.writeEndElement();
    }



    private void writeFirstPropertyValue(final XMLStreamWriter stream, final String elementName,
                                         final String[] propertyNames, final ValueMap properties)
            throws XMLStreamException {
        for (final String property : propertyNames) {
            String propertyValue = properties.get(property, String.class);
            if (propertyValue == null) {
                propertyValue = properties.get(property, String.class);
                if (SiteMapConstants.PRIORITY.equalsIgnoreCase(property) && propertyValue == null) {
                    propertyValue = SiteMapConstants.PRIORITY_VALUE;
                    writeElement(stream, elementName, propertyValue);
                } else {
                    if (SiteMapConstants.CHANGE_FREQUENCY.equalsIgnoreCase(property) && propertyValue == null) {
                        propertyValue = SiteMapConstants.DAILY;
                        writeElement(stream, elementName, propertyValue);
                    }
                }
            } else {
                writeElement(stream, elementName, propertyValue);
                break;
            }
        }
    }

    private String getInventoryId(final Page page, final ResourceResolver resourceResolver) {
        String inventoryId = StringUtils.EMPTY;
        final ValueMap currentPageProperties = page.getProperties();
        final Resource pimResource = getPimResource(currentPageProperties, resourceResolver);
        if (null != pimResource) {
            final PIMResourceSlingModel pimResourceSlingModel =
                    pimResource.adaptTo(PIMResourceSlingModel.class);
            if (null != pimResourceSlingModel) {
                final String pdhRecordPath = pimResourceSlingModel.getPdhRecordPath();
                final Resource pdhRecordResource = resourceResolver.getResource(pdhRecordPath);
                if (pdhRecordResource != null) {
                    final Resource inventoryIdResource = pdhRecordResource.getParent();
                    inventoryId = inventoryIdResource.getName();
                }
            }
        }
        return inventoryId;
    }

    private boolean isHidden(final Page page) {
        return page.getProperties().get(NameConstants.PN_HIDE_IN_NAV, false);
    }

    private Resource getPimResource(final ValueMap currentPageProperties, final ResourceResolver resourceResolver) {
        Resource pimResource = null;
        if (currentPageProperties.get(CommonConstants.PAGE_PIM_PATH) != null) {
            final String pimPagePath = currentPageProperties.get(SiteMapConstants.PIM_PAGE_PATH).toString();
            if (StringUtils.isNotBlank(pimPagePath)) {
                pimResource = resourceResolver.getResource(pimPagePath);
            }
        }
        return pimResource;
    }

    private SiteConfigModel getSiteConfig(final Optional<EatonSiteConfigModel> eatonSiteConfigModelOptional) {
        SiteConfigModel siteConfig = null;
        if (eatonSiteConfigModelOptional.isPresent()) {
            final EatonSiteConfigModel eatonSiteConfigModel = eatonSiteConfigModelOptional.get();
             siteConfig = eatonSiteConfigModel.getSiteConfig();
        }
        return siteConfig;
    }

    private void writeProductSkuSiteMap(final List<FamilyModuleBean> familyModuleList,
                                        final ResourceResolver resourceResolver,
                                        final XMLStreamWriter stream,
                                        final String pagePath, final ValueMap valueMap) {
        familyModuleList.forEach(familyModuleBean -> {
            try {
                final Optional<Page> pageOptional = Optional.ofNullable(resourceResolver.resolve(pagePath)
                        .adaptTo(Page.class));
                if (pageOptional.isPresent()) {
                    final Page skuPage = pageOptional.get();
                    write(resourceResolver, stream, skuPage,
                            SiteMapConstants.DOT.concat(familyModuleBean.getCatalogNumber()), SiteMapConstants.HTML_EXT,
                            Boolean.TRUE);
                    writeFirstPropertyValue(stream, SiteMapConstants.CHANGEFREQ, changefreqProperties,
                            valueMap);
                    writeFirstPropertyValue(stream, SiteMapConstants.PRIORITY, priorityProperties,
                            valueMap);
                }
            } catch (XMLStreamException e) {
                LOGGER.error(SiteMapConstants.ERROR_WHILE_CREATING_SKU_SITE_MAP, e);
            }
        });
    }

    private void writeProductFamilySiteMap(final List<ProductFamilyBean> productFamilyBeanList,
                                           final ResourceResolver resourceResolver,
                                           final XMLStreamWriter stream) {
        productFamilyBeanList.forEach(productFamilyBean -> {
            final String pagePath = getPagePath(productFamilyBean);
            final Optional<Page> pageOptional = Optional.ofNullable(resourceResolver.resolve(pagePath)
                    .adaptTo(Page.class));
            if (pageOptional.isPresent()) {
                final Page productFamilyPage = pageOptional.get();
                final String inventoryId = getInventoryId(productFamilyPage, resourceResolver);
                if (StringUtils.isNotBlank(inventoryId)) {
                    write(resourceResolver, stream, productFamilyPage, SiteMapConstants.SKUMAP,
                            SiteMapConstants.XML_EXT, Boolean.FALSE);
                }
            }
        });
    }

}
