package com.eaton.platform.core.models.productgrid;

import com.eaton.platform.core.util.CommonUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.SiteConfigModel;
import com.eaton.platform.core.models.eatonsiteconfig.EatonSiteConfigModel;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.integration.endeca.bean.subcategory.ProductFamilyBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DigitalAttributeOperator {

    private static final Logger LOG = LoggerFactory.getLogger(DigitalAttributeOperator.class);
    private SlingHttpServletRequest slingHttpServletRequest;
    private AdminService adminService;

    public DigitalAttributeOperator(SlingHttpServletRequest slingHttpServletRequest, AdminService adminService){
        this.slingHttpServletRequest = slingHttpServletRequest;
        this.adminService = adminService;
    }
    public void addDigitalAttributes(ProductFamilyBean productFamilyBean) {
        LOG.debug("DigitalAttributeOperator : This is Entry into addDigitalAttributes() method");
        addNewBadge(productFamilyBean);
    }
    public static int getDaysLimit(SlingHttpServletRequest slingHttpServletRequest,ResourceResolver adminResourceResolver){
        SiteConfigModel siteConfiguration;
        final EatonSiteConfigModel eatonSiteConfigModel = slingHttpServletRequest.adaptTo(EatonSiteConfigModel.class);
        if(null != eatonSiteConfigModel) {
           siteConfiguration = eatonSiteConfigModel.getSiteConfig();
        } else {
            Page currentPage = CommonUtil.getCurrentPagefromRequestReferer(slingHttpServletRequest);
            siteConfiguration = CommonUtil.getSiteConfigFromPage(currentPage,adminResourceResolver);
        }
        return (null != siteConfiguration) ? siteConfiguration.getNoOfDays() : ProductGridModel.DEFAULT_STATUS_BADGE_TIME;
    }
    public void addNewBadge(ProductFamilyBean productFamilyBean){
        LOG.debug("Inside addNew Badge digital");
        try(ResourceResolver resourceResolver = this.adminService.getReadService()){
            LOG.debug("addNewBadge resource resolver {} ",resourceResolver);
            String formattedDate = StringUtils.EMPTY;
            if(StringUtils.isNotBlank(productFamilyBean.getPublishDate())){
                SimpleDateFormat publicationDateFormat = new SimpleDateFormat(CommonConstants.DATE_FORMAT_PUBLISH);
                SimpleDateFormat formatter = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT_PUBLISH);
                Date parsedDate = publicationDateFormat.parse(productFamilyBean.getPublishDate());
                formattedDate = formatter.format(parsedDate);
                LOG.debug("addNewBadge {} ",formattedDate);
            }
            int dayLimit = getDaysLimit(this.slingHttpServletRequest,resourceResolver);
            LOG.debug("addNewBadge daylimit {} ",dayLimit);
            if (StringUtils.isNotBlank(formattedDate)) {
                long noOfDays = CommonUtil.getDaysDifference(formattedDate);
                boolean isVisible = noOfDays >= 0 && noOfDays <= dayLimit;
                productFamilyBean.setNewBadgeVisible(isVisible);
            } else {
                LOG.debug("DigitalAttributeOperator : populateNewProductBadge: publicationDate is null for Family Page");
            }
        } catch (ParseException e) {
            LOG.error("ParseException :: {}",e.getMessage(),e);
       }catch (Exception e){
            LOG.error("DigitalAttributeOperator : addDigitalAttributes :  Admin service catch block: exception", e);
        }
    }
}

