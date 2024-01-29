package com.eaton.platform.core.models;

import com.eaton.platform.core.bean.ProductSupportBean;
import com.eaton.platform.core.util.CommonUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import java.util.Iterator;

import org.apache.sling.models.annotations.Model;

import static com.eaton.platform.core.constants.CommonConstants.SUPPORT_INFO_COUNTRY;
import static com.eaton.platform.core.constants.CommonConstants.SUPPORT_INFO_TEXT;
import static com.eaton.platform.core.constants.CommonConstants.SUPPORT_INFO_LABEL;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SupportInfoModel {

    private Resource resource;

    public SupportInfoModel(Resource resource) {
        this.resource = resource;
    }

    /**

     * Creates The support information for based on the given country. Support information
     * is a grouping of information that comes either from PDH or from the authored values
     * on the PIM node.
     * A PIM node is one of the nodes underneath "/var/commerce/products/eaton"
     *
     * @param pdhRecordResource a resource underneath the "/var/commerce/pdh" path.
     * @param overridePDHData Whether or the PIM node authored values should override the PDF values.
     * @param country The country to be compared against when creating the support information.
     * @return The support information that is relevant to the given country.
     */
    public ProductSupportBean forCountry(Resource pdhRecordResource, boolean overridePDHData, String country) {
        ProductSupportBean supportInfo = null;

        if (null != resource && overridePDHData) {
            Iterator<Resource> items = resource.listChildren();
            boolean foundExact = false;
			supportInfo = new ProductSupportBean();

			while (items.hasNext()) {
				Resource item = items.next();
				ValueMap properties = item.getValueMap();
				
				String[] supportCountries = CommonUtil.getAuthoredCountriesSupportInfo(properties);
				String supportInfoText = properties.get(SUPPORT_INFO_TEXT, EMPTY);
				
				if(supportCountries.length == 1 && supportCountries[0].equals(StringUtils.EMPTY)) {
					    supportInfo.setSupportLabel(properties.get(SUPPORT_INFO_LABEL, EMPTY));
					    supportInfo.setSupportInformation(supportInfoText);
				} else {
						if (CommonUtil.countryMatches(country, supportCountries) && !foundExact &&
							CommonUtil.exactCountryMatch(country, supportCountries)) {
								foundExact = true;
								supportInfo.setSupportInformation(supportInfoText);
								supportInfo.setSupportLabel(properties.get(SUPPORT_INFO_LABEL, EMPTY));
								supportInfo.setCountry(properties.get(SUPPORT_INFO_COUNTRY, EMPTY));
						}
			    }
			 }
         }
		return supportInfo;
      }
}
   
