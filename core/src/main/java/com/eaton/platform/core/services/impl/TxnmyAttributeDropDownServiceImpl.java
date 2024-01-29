package com.eaton.platform.core.services.impl;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.EndecaConfigModel;
import com.eaton.platform.core.models.PIMResourceSlingModel;
import com.eaton.platform.core.models.PdhConfigModel;
import com.eaton.platform.core.services.CloudConfigService;
import com.eaton.platform.core.services.TxnmyAttributeDropDownService;
import com.eaton.platform.integration.pim.util.PIMUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.HashMap;
import java.util.Optional;


@Component(service = TxnmyAttributeDropDownService.class, immediate = true)
public class TxnmyAttributeDropDownServiceImpl implements TxnmyAttributeDropDownService {

    @Reference
    private CloudConfigService cloudConfigService;

    /**
     * This method will construct taxonomy attributes dropdown options from single pim page path or multi pim page paths of page properties.
     * @param resourceResolver
     * @param currentPage
     * @return unsortedMap
     */
    @Override
    public HashMap<String, String> constructDropdownOptions(final ResourceResolver resourceResolver, final Page currentPage) {
            final ValueMap pageProperties = currentPage.getProperties();
            String[] pimPagePaths = null;
            if (pageProperties.containsKey(CommonConstants.PAGE_PIM_PATHS)) {
                pimPagePaths = pageProperties.get(CommonConstants.PAGE_PIM_PATHS, ArrayUtils.EMPTY_STRING_ARRAY);
            }
            final String pimPagePath = PIMUtil.getPIMPagePath(currentPage);
            final HashMap<String, String> unsortedMap;
            final java.util.Optional<EndecaConfigModel> endecaConfigModel = cloudConfigService != null
                ? cloudConfigService.getEndecaCloudConfig(currentPage.getContentResource()) : java.util.Optional.empty();

            //If the single "PIM Page Path" field is authored then the page will behave as a normal product family page regardless of what is in the multivalued "Product Selector PIMs" field.
            // If the single "PIM Page Path" field is empty and the multivalued "Product Selector PIMs" field is authored then it will behave like a product selector
            if (StringUtils.isNotEmpty(pimPagePath)) {
                unsortedMap = getAttributesFromPimResource(resourceResolver, pimPagePath, endecaConfigModel);
            } else if (pimPagePaths != null) {
                unsortedMap = new HashMap<>();
                for (String pimPath : pimPagePaths) {
                    unsortedMap.putAll(getAttributesFromPimResource(resourceResolver, pimPath, endecaConfigModel));
                }
                //Update the taxonomy attributes labels provided by the PDH config,instead of Endeca provided config.
                if(null != cloudConfigService){
					final Optional<PdhConfigModel> pdhConfigModel = cloudConfigService.getPdhCloudConfig(currentPage.getContentResource());
					if (pdhConfigModel.isPresent()) {
						pdhConfigModel.get().getComboAttributes().forEach(comboAttribute -> {
							if (unsortedMap.containsValue(comboAttribute.getComboFieldName())) {
								unsortedMap.put(comboAttribute.getField1I18nKey(), comboAttribute.getField1Name());
								unsortedMap.put(comboAttribute.getField2I18nKey(), comboAttribute.getField2Name());
							}
						});
					}
				}
            } else {
                unsortedMap = new HashMap<>();
            }

            return unsortedMap;
        }

    /**
     * This method will resolve pimpath and get the taxonomy attribute configured to that pim and put it to filterMap.
     * @param resourceResolver
     * @param pimPath
     * @return filterMap
     * */
    public HashMap<String, String> getAttributesFromPimResource(final ResourceResolver resourceResolver, final String pimPath, final Optional<EndecaConfigModel> endecaConfigModel) {
        HashMap<String, String> taxonomyAttributesLookup = new HashMap<>();
        if (StringUtils.isNotEmpty(pimPath)) {
            final String englishPIMPagePath = PIMUtil.getEnglishPIMPagePath(pimPath);
            if (StringUtils.isNotEmpty(englishPIMPagePath)) {
                final Resource pimResource = resourceResolver.getResource(englishPIMPagePath);
                if (null != pimResource) {
                    final PIMResourceSlingModel pimModel = pimResource.adaptTo(PIMResourceSlingModel.class);
					if(null != pimModel)
					{
                      taxonomyAttributesLookup.putAll(pimModel.getTaxonomyAttributesLookup(endecaConfigModel));
					}
                }
            }
        }
        return taxonomyAttributesLookup;
    }
}
