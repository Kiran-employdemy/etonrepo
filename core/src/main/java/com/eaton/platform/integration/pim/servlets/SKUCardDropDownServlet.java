package com.eaton.platform.integration.pim.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.eaton.platform.core.constants.ServletConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.PIMResourceSlingModel;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.pim.util.PIMUtil;

import javax.servlet.Servlet;

/**
 * This servlet pre-populates the SKU Card dropdown in teaser tab of page
 * properties author - TCS.
 */
@Component(service = Servlet.class,
		immediate = true,
		property = {
				ServletConstants.SLING_SERVLET_METHODS_GET,
				ServletConstants.SLING_SERVLET_PATHS + "/eaton/content/skucardattributes",
				ServletConstants.SLING_SERVLET_EXTENSION_JSON
		})
public class SKUCardDropDownServlet extends SlingSafeMethodsServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(SKUCardDropDownServlet.class);
	private static final String GLOBAL_ATTR_NAME = "attributeName";
	private static final String ITEM = "item";
	private static final String GLOBAL_ATTR_DISPLAY_NAME = "attributeDisplayName";

	@Reference
	private transient AdminService adminService;

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
		LOG.debug("******** SKUCardDropDownServlet execution started ***********");
		LOG.debug("******** pimPagePath ***********" + request.getParameter(ITEM));

		ResourceResolver resourceResolver = request.getResourceResolver();
		String pdhRecordPath = null;
		Resource extnIdResource = null;
		String pimPagePath = request.getParameter(ITEM);

		if (pimPagePath != null) {
			// Changing language of PIM to en_us.
			pimPagePath = setPIMLanguagePath(pimPagePath);
			final Resource PIMResource = resourceResolver.getResource(pimPagePath);
			if (PIMResource != null) {
				PIMResourceSlingModel pimResourceSlingModel = PIMResource.adaptTo(PIMResourceSlingModel.class);
				pdhRecordPath = pimResourceSlingModel.getPdhRecordPath();
			}
		}

		LOG.debug("******** pdhRecordPath ***********" + pdhRecordPath);

		if (pdhRecordPath != null) {
			extnIdResource = resourceResolver.getResource(pdhRecordPath);
		}

		// Prepare an unsorted Map which contains the "attributeName" and "attributeDisplayName" as key value pair.
		HashMap<String, String> unsortedMap = new HashMap<>();

		// Get taxonomy attributes.
		if (null != extnIdResource) {
			final Iterator<Resource> txnmyAttrResourceList = extnIdResource.listChildren();

			while (txnmyAttrResourceList.hasNext()) {
				final Resource attrItem = txnmyAttrResourceList.next();

				if (null != attrItem) {
					final String displayValue = CommonUtil.getStringProperty(attrItem.getValueMap(), GLOBAL_ATTR_NAME);
					final String displayLabel = CommonUtil.getStringProperty(attrItem.getValueMap(), GLOBAL_ATTR_DISPLAY_NAME);

					if (null != displayValue && null != displayLabel && (!displayLabel.isEmpty())
							&& (!displayValue.isEmpty()) && (!displayValue.startsWith("Product_"))
							&& (!displayLabel.startsWith("Product_"))) {
						unsortedMap.put(displayLabel, displayValue);
					}
				}
			}
		}
		final List<Resource> dropdownList = PIMUtil.prepareDropDownList(resourceResolver, unsortedMap);
		LOG.debug("SKUCardDropDownServlet1 DropdownList Size : " + dropdownList.size());
		// Create a DataSource that is used to populate the drop-down control.
		final DataSource dataSource = new SimpleDataSource(dropdownList.iterator());
		request.setAttribute(DataSource.class.getName(), dataSource);
		LOG.debug("******** SKUCardDropDownServlet execution ended ***********");
	}

	private static String setPIMLanguagePath(final String pimPagePath) {
		final String pagePathLanguage = StringUtils.substringBetween(pimPagePath, CommonConstants.PIM_PATH, CommonConstants.SLASH_STRING);
		final String englishPimPagePath;

		if (StringUtils.isNotBlank(pagePathLanguage)) {
			englishPimPagePath = pimPagePath.replace(pagePathLanguage, CommonConstants.ENGLISH_LANGUAGE);
		} else {
			englishPimPagePath = pimPagePath;
		}

		return englishPimPagePath;
	}
}