package com.eaton.platform.core.servlets;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.services.TxnmyAttributeDropDownService;
import com.eaton.platform.integration.pim.util.PIMUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.Servlet;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import static org.apache.sling.query.SlingQuery.$;

@Component(service = Servlet.class,
        immediate = true,
        property = {
                ServletConstants.SLING_SERVLET_METHODS_GET,
                ServletConstants.SLING_SERVLET_RESOURCE_TYPES + "/eaton/content/txnmyAttributeDropDownForm",
        })
public class TxnmyAttributeDropDownFormServlet extends SlingSafeMethodsServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(TxnmyAttributeDropDownFormServlet.class);

    @Reference
    private TxnmyAttributeDropDownService txnmyAttributeDropDownService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        LOG.info("******** TxnmyAttributeDropDownFormServlet execution begin ***********");
        final ResourceResolver resourceResolver = request.getResourceResolver();
        String pagePathFromRequest = PIMUtil.getPagePathFromRequest(request);
        final Resource pageResource = resourceResolver.resolve(pagePathFromRequest);
        final Optional<Resource> resourceOptional = $(pageResource).find(CommonConstants.GUIDE_CONTAINER).asList().stream().findFirst();
        if (resourceOptional.isPresent()) {
            final Resource resourceOfTxnmyAttributeDropDown = resourceOptional.get();
            final ValueMap valueMapOfResourceOfTxnmyAttributeDropDown = resourceOfTxnmyAttributeDropDown.getValueMap();
            if (valueMapOfResourceOfTxnmyAttributeDropDown.containsKey(CommonConstants.REDIRECT)) {
                final String productFamilyPagePath = valueMapOfResourceOfTxnmyAttributeDropDown.get(CommonConstants.REDIRECT).toString();
                if (StringUtils.isNotEmpty(productFamilyPagePath)) {
                    final Page PfpPage = request.getResourceResolver().resolve(productFamilyPagePath).adaptTo(Page.class);
                    if (null != PfpPage) {
                        final HashMap<String, String> txnmyAttributeDropDownData = txnmyAttributeDropDownService.constructDropdownOptions(resourceResolver, PfpPage);
                        final List<Resource> dropdownList = PIMUtil.prepareDropDownList(resourceResolver, txnmyAttributeDropDownData);
                        if (LOG.isInfoEnabled()) {
                            LOG.debug("TxnmyAttributeDropDownServlet DropdownList Size : " + dropdownList.size());
                        }
                        // Create a DataSource that is used to populate the drop-down control
                        final DataSource dataSource = new SimpleDataSource(dropdownList.iterator());
                        request.setAttribute(DataSource.class.getName(), dataSource);
                    }
                }
            }
        }
    }
}
