package com.eaton.platform.core.services.impl;

import com.adobe.cq.commerce.common.ValueMapDecorator;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.EloquaFormOverlayService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The Class ConfigServiceImpl.
 */
@Component(service = EloquaFormOverlayService.class,
        immediate = true,
        property = {
                AEMConstants.SERVICE_VENDOR_EATON,
                AEMConstants.SERVICE_DESCRIPTION + "EloquaFormOverlayImpl",
                AEMConstants.PROCESS_LABEL + "EloquaFormOverlayImpl"
        })
public class EloquaFormOverlayImpl implements EloquaFormOverlayService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EatonConfigServiceImpl.class);


    /** The Constant DISCLAIMER_CHECK_BOX_1. */
    private static final String DISCLAIMER_CHECK_BOX_1 = "<input type=\"checkbox\" class=\"disclaimer-checkbox\" "
            + "name=\"eatondisclaimer\" value=\"eatondisclaimer\" id=\"eatondisclaimer_form";

    /** The Constant DISCLAIMER_CHECK_BOX_2. */
    private static final String DISCLAIMER_CHECK_BOX_2 = "\">";
    /** The Constant INVALID_FORM_ID_HTML_CONTENT_PART_1. */
    private static final String DISCLAIMER_HTML_CONTENT_PART_1 = "<div id=\"disclaimer-msg\" class=\"disclaimer-msg\">";
    /** The Constant DIV_TAG. */
    private static final String DIV_TAG = "</div>";
    private static final String TYPE = "type";
    private static final String HTML = "html";
    private static final String DISCLAIMER_FIELD_PREFIX = "tag_Optin";
    private static final String NAME = "name";
    private static final String SELECT = "select";
    private static final String INPUT = "input";


    @Activate
    protected final void activate() throws Exception {
        LOGGER.info("Service Activated");
    }

    @Override
    public String generateDisclaimerDom(final String extraHTML, final String formId, final String disclaimerMsg) {
        String generatedDisclaimerString = extraHTML.concat(DISCLAIMER_CHECK_BOX_1).concat(formId)
                .concat(DISCLAIMER_CHECK_BOX_2)
                .concat(DISCLAIMER_HTML_CONTENT_PART_1)
                .concat(disclaimerMsg).concat(DIV_TAG);
        return generatedDisclaimerString;
    }

    @Override
    public List<Element> getFilteredElement(String fieldType, Document htmlDoc) {
        final List<Element> collect = htmlDoc.getAllElements().stream()
                .filter(element -> element.attr(TYPE).equals(fieldType) ? true : false)
                .collect(Collectors.toList());
        return collect;
    }

    @Override
    public List<Resource> extractFormFields(String eloquaFormResponse, ResourceResolver resourceResolver,
                                            String fieldType) {
        final JSONParser parser = new JSONParser();
        List<Resource> elementResourceList = new ArrayList<Resource>();
        try {
            final JSONObject eloquoFormJsonObject = (JSONObject) parser.parse(eloquaFormResponse);
            final String htmlExtract = eloquoFormJsonObject.containsKey(HTML)
                    ? eloquoFormJsonObject.get(HTML).toString() : StringUtils.EMPTY;
            if (StringUtils.isNotEmpty(htmlExtract)) {
                final Document htmlDocument = Jsoup.parse(htmlExtract);
                List<Element> collect = Collections.emptyList();
                if (fieldType.equals("all")) {
                    collect = htmlDocument.getAllElements().stream().collect(Collectors.toList());
                } else  {
                    collect = getFilteredElement(fieldType,
                            htmlDocument);
                }
                elementResourceList = construcDropDownList(resourceResolver, elementResourceList, collect);
            }
        } catch (ParseException e) {
            LOGGER.error("Exception while parsing response from Eloqua", e);
        }
        return elementResourceList;
    }

    private List<Resource> construcDropDownList(final ResourceResolver resourceResolver,
                                                final List<Resource> elementResourceList, final List<Element> collect) {
        collect.forEach(element -> {
            final String tagName = element.tagName();
            if ((INPUT.equals(tagName) || SELECT.equals(tagName)) || "textarea".equals(tagName)) {
                final String fieldName = element.attr(NAME);
                final ValueMapDecorator valueMapDecorator = new ValueMapDecorator(new HashMap<String, Object>());
                valueMapDecorator.put(CommonConstants.VALUE, fieldName);
                valueMapDecorator.put(CommonConstants.TEXT, fieldName);
                elementResourceList.add(new ValueMapResource(resourceResolver, new ResourceMetadata(),
                        "nt:unstructured", valueMapDecorator));
            }
        });

        return elementResourceList;
    }


}
