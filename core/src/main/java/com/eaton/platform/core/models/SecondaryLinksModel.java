package com.eaton.platform.core.models;

import com.eaton.platform.core.bean.SecondaryLinksBean;
import com.eaton.platform.core.util.CommonUtil;
import java.util.ArrayList;
import java.util.List;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;

import org.apache.sling.models.annotations.Model;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static com.eaton.platform.core.constants.CommonConstants.SECONDARY_LINK_TEXT;
import static com.eaton.platform.core.constants.CommonConstants.COUNTRIES_FIELD;
import static com.eaton.platform.core.constants.CommonConstants.SECONDARY_LINK_URL;
import static com.eaton.platform.core.constants.CommonConstants.SECONDARY_LINK_NEW_WINDOW;
import static com.eaton.platform.core.constants.CommonConstants.SECONDARY_LINK_SKU_ONLY;
import static com.eaton.platform.core.constants.CommonConstants.TARGET_SELF;
import static com.eaton.platform.core.constants.CommonConstants.TARGET_BLANK;


@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SecondaryLinksModel {
    private Resource resource;

    public SecondaryLinksModel(Resource resource) {
        this.resource = resource;
    }

	/**
     * Provides the list of secondary links for the given country. A secondary link is a grouping of
     * information that comes either from PDH or from the authored values on the PIM node. A PIM
     * node is one of the nodes underneath "/var/commerce/products/eaton"
     *
     * @param country A string representation of the country to create the list for. Different
     *                countries will have different lists of secondary links based on how the PIM
     *                node was authored.
     * @return The list of secondary links relevant to the given country based on the given resource.
     */
    public List<SecondaryLinksBean> forCountry(String country) {
        List<SecondaryLinksBean> links = new ArrayList<>();

        resource.getChildren().forEach(linkResource -> {
            final SecondaryLinksBean secondaryLink = new SecondaryLinksBean();
            final ValueMap properties = linkResource.getValueMap();
            final String[] authoredCountries = CommonUtil.getAuthoredCountries(properties);

            if (CommonUtil.countryMatches(country, authoredCountries)) {
                secondaryLink.setText(properties.get(SECONDARY_LINK_TEXT, EMPTY));
                secondaryLink.setPath(properties.get(SECONDARY_LINK_URL, EMPTY));
                secondaryLink.setNewWindow(!properties.get(SECONDARY_LINK_NEW_WINDOW, EMPTY).equals(EMPTY)
                        ? TARGET_BLANK : TARGET_SELF);
                secondaryLink.setSecLinkSkuOnly(!properties.get(SECONDARY_LINK_SKU_ONLY, EMPTY).equals(EMPTY) ? Boolean.TRUE : Boolean.FALSE);
                links.add(secondaryLink);
            }
        });

        return links;
    }
}
