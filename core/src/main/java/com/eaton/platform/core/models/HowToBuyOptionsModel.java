package com.eaton.platform.core.models;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.HowToBuyBean;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.eloqua.util.EloquaUtil;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static com.eaton.platform.core.constants.CommonConstants.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HowToBuyOptionsModel {
    private static final Logger LOG = LoggerFactory.getLogger(HowToBuyOptionsModel.class);

    @Self
    private Resource resource;
    @Inject
    protected AdminService adminService;

    /**
     * TODO This needs refactored to not require the siteCloudConfigRes be provided externally.
     * Provides the list of how to buy options for the given country. A how to buy opton is a
     * grouping of information that comes either from PDH or from the authored values on the
     * PIM node. A PIM node is one of the nodes underneath "/var/commerce/products/eaton"
     *
     * @param country A string representation of the country to create the list for. Different
     *                countries will have different lists of topic links based on how the PIM
     *                node was authored.
     * @param siteCloudConfigRes
     * @return The list of how to buy options relevant to the given country based on the given resource.
     */
    public List<HowToBuyBean> forCountry(String country, String[] siteCloudConfigRes,Page currentPage, ResourceResolver adminServiceReadService, String primaryProductTaxonomy) {
        List<HowToBuyBean> howToBuyBeans = new ArrayList<>();
        if (resource == null) {
            return howToBuyBeans;
        }
        resource.getChildren().forEach(howToBuyResource -> {
            ValueMap properties = howToBuyResource.getValueMap();
            String[] authoredCountries = CommonUtil.getAuthoredCountries(properties);
            String makeCTAGlobal =  properties.get("additionalCTAMakeGlobal",String.class);
            LOG.debug("makeCTAGlobal property check {}",makeCTAGlobal);
            if (CommonUtil.countryMatches(country, authoredCountries)) {
                HowToBuyBean howToBuy = new HowToBuyBean();
                String htbTitle = properties.get(HOW_TO_BUY_TITLE, EMPTY);
                boolean isValueSetFromSiteConfig = setTitleAndIcons(siteCloudConfigRes, htbTitle, howToBuy);

                if (!isValueSetFromSiteConfig) {
                    howToBuy.setTitle(properties.get(HOW_TO_BUY_TITLE, EMPTY));
                    howToBuy.setIcon(properties.get(HOW_TO_BUY_ICON, EMPTY));
                    howToBuy.setDropdownIcon(properties.get(HOW_TO_BUY_DROPDOWN_ICON, EMPTY));
                }

                howToBuy.setDscription(properties.get(HOW_TO_BUY_DESCRIPTION, EMPTY));
                String linkURL=properties.get(HOW_TO_BUY_LINK,String.class);
                try {
                    if(adminServiceReadService!=null && makeCTAGlobal != null) {
                        String currentLink=properties.get(HOW_TO_BUY_LINK,String.class);
                        LOG.debug("currentLink property check {}",currentLink);
                        String updateCTAURLString=CommonUtil.updateCTAURLString(currentLink, currentPage, adminServiceReadService);
                        LOG.debug("updateCTAURLString property check {}",updateCTAURLString);
                        howToBuy.setLink(updateCTAURLString);
                    }
                    else{
                        howToBuy.setLink(linkURL);
                    }
                } catch(Exception e) {
                    LOG.error("Error in HowToBuyOptionsModel::Forcounty method {}" , e.getMessage());
                }

                linkURL=howToBuy.getLink();
                if(linkURL!=null && primaryProductTaxonomy!=null) {
                    linkURL = EloquaUtil.hasEloquaForm(CommonUtil.escapeHTML(linkURL), adminService) ? EloquaUtil.appendPrimaryProductTaxonomy(linkURL, primaryProductTaxonomy) : linkURL;
                    howToBuy.setLink(linkURL);
                }

                howToBuy.setOpenInNewWindow(! properties.get(HOW_TO_BUY_NEW_WINDOW, EMPTY).equals(EMPTY)
                        ? TARGET_BLANK : TARGET_SELF);
                howToBuy.setSkuOnly(properties.get("skuOnly", false));
                howToBuy.setSuffixEnabled(properties.get("isSuffixEnabled", false));
                howToBuy.setModalEnabled(properties.get("isModalEnabled", false));
                howToBuy.setSourceTrackingEnabled(properties.get(ENABLE_SOURCE_TRACKING, false));
                howToBuyBeans.add(howToBuy);
            }
        });

        return howToBuyBeans;
    }

    /**
     * TODO This needs refactored to be unnecessary. This class should not require that the siteCloudConfigRes be provided to it.
     * Sets the htb title and icons.
     *
     * @param howTobuySiteConfig the site cloud config res
     * @param htbTitle           the htb title
     * @param howToBuy           Method to retrieve icon and image icon value for corresponding
     *                           title.
     * @return true, if successful
     */
    public static boolean setTitleAndIcons(String[] howTobuySiteConfig, String htbTitle, HowToBuyBean howToBuy) {
        boolean isValueSetFromSiteConfig = false;
        if (null != howTobuySiteConfig) {
            for (int i = 0; i < howTobuySiteConfig.length; i++) {
                JSONParser parser = new JSONParser();

                try {
                    JSONObject jsonObject = (JSONObject) parser.parse(howTobuySiteConfig[i]);
                    if (null != jsonObject.get(VALUE).toString() && (!jsonObject.get(VALUE).toString().isEmpty()) && (htbTitle.equalsIgnoreCase(jsonObject.get(VALUE).toString()))) {
                        howToBuy.setTitle(jsonObject.get(TEXT).toString());
                        if (null != jsonObject.get(HOW_TO_BUY_ICON).toString() && (!jsonObject.get(HOW_TO_BUY_ICON).toString().isEmpty())) {
                            howToBuy.setIcon(jsonObject.get(HOW_TO_BUY_ICON).toString());
                        }
                        if (null != jsonObject.get(HOW_TO_BUY_DROPDOWN_ICON).toString() && (!jsonObject.get(HOW_TO_BUY_DROPDOWN_ICON).toString().isEmpty())) {
                            howToBuy.setDropdownIcon(jsonObject.get(HOW_TO_BUY_DROPDOWN_ICON).toString());
                        }

                        isValueSetFromSiteConfig = true;
                        break;
                    }
                } catch (ParseException e) {
                    LOG.error("Error in setTitleAndIcons method {}" , e.getMessage());
                }
            }
        }
        LOG.debug("Exit from setTitleAndIcons method");

        return isValueSetFromSiteConfig;
    }
}