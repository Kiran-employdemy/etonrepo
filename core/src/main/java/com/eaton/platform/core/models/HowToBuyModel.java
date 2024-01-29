package com.eaton.platform.core.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.eaton.platform.core.services.AdminService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.slf4j.LoggerFactory;


import org.slf4j.Logger;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.HowToBuyBean;
import com.eaton.platform.core.bean.ProductFamilyDetails;
import com.eaton.platform.core.bean.ProductFamilyPIMDetails;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.EndecaRequestService;
import com.eaton.platform.core.services.ProductFamilyDetailService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsBean;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsResponseBean;
import com.eaton.platform.integration.endeca.services.EndecaService;

/**
 * The HowToBuyModel.
 */
@Model(adaptables = { Resource.class, SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HowToBuyModel {
	private static  final Logger LOGGER =  LoggerFactory.getLogger(HowToBuyModel.class);
	private List<HowToBuyBean> howToBuyList = new ArrayList<>();
	private String pageType;
	private String headLineValue;
	private String externalMappedCurrentPagePath;

	@Inject
	@Source("sling-object")
	private SlingHttpServletRequest slingRequest;

	@Inject
	private Page currentPage;

	@Inject
	protected EndecaRequestService endecaRequestService;

	@Inject
	protected ProductFamilyDetailService productFamilyDetailService;

	@Inject
	protected EndecaService endecaService;

	@Inject
	protected AdminService adminService;

	@PostConstruct
	public void setComponentValues() {
		LOGGER.debug("HowToBuyModel :: setComponentValues() :: Started");
		ValueMap properties = currentPage.getProperties();
		try(ResourceResolver adminResourceResolver = adminService.getReadService()){

			if (properties != null && properties.get(CommonConstants.PAGE_TYPE) != null) {
				pageType = properties.get(CommonConstants.PAGE_TYPE).toString();
			}

			headLineValue = CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage,
					CommonConstants.HOW_TO_BUY_HEADLINE, CommonConstants.HOW_TO_BUY_HEADLINE_DEFAULT_VALUE);

			if(pageType == null) {
				return;
			} else {
				if (pageType.equals(CommonConstants.PAGE_TYPE_PRODUCT_FAMILY_PAGE)) {

					ProductFamilyDetails productFamilyDetails = productFamilyDetailService.getProductFamilyDetailsBean(currentPage);
					if (productFamilyDetails != null && productFamilyDetails.getHowToBuyList() != null) {
						howToBuyList = CommonUtil.filterSkuHowToBuyOptions(productFamilyDetails.getHowToBuyList(), false);
					}
					String mappedPagePath = adminResourceResolver.map(currentPage.getPath());
					if (StringUtils.isNotEmpty(mappedPagePath)) {
						mappedPagePath = CommonUtil.removeSiteContentRootPathPrefix(mappedPagePath);
						externalMappedCurrentPagePath = mappedPagePath;
					}
				} else if (pageType.equals(CommonConstants.PAGE_TYPE_PRODUCT_SKU_PAGE)) {
					final String[] selectors = slingRequest.getRequestPathInfo().getSelectors();
					final EndecaServiceRequestBean endecaRequestBean = endecaRequestService.getEndecaRequestBean(currentPage,
							selectors, StringUtils.EMPTY);
					final SKUDetailsResponseBean skuDetails = endecaService.getSKUDetails(endecaRequestBean);
					final SKUDetailsBean skuDetailsBean = skuDetails.getSkuResponse().getSkuDetails();
					ProductFamilyPIMDetails productFamilyPIMDetails = productFamilyDetailService.getProductFamilyPIMDetailsBean(skuDetailsBean,
							currentPage);

					if (productFamilyPIMDetails != null && selectors.length > 0) {
						howToBuyList = CommonUtil.replaceTokenForHowToBuyOptions(productFamilyPIMDetails.getHowToBuyList(), pageType, selectors[0]);
						if (productFamilyPIMDetails.getProductFamilyAEMPath() != null) {
							externalMappedCurrentPagePath = CommonUtil.removeSiteContentRootPathPrefix(productFamilyPIMDetails.getProductFamilyAEMPath());
						}
					}

					if (Objects.nonNull(skuDetailsBean) && CommonUtil.isSkuDiscontinued(skuDetailsBean.getStatus())) {
						headLineValue = CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage,
								CommonConstants.DISCONTINUED_PRODUCT_HOW_TO_BUY_HEADLINE,
								CommonConstants.DISCONTINUED_PRODUCT_HOW_TO_BUY_HEADLINE_DEFAULT_VALUE);
					}
				}
			}
			howToBuyList = addClearfixCondition(howToBuyList, 3);
		}

		LOGGER.debug("HowToBuyModel :: setComponentValues() :: END");
	}

	public List<HowToBuyBean> addClearfixCondition(List<HowToBuyBean> howToBuyList, int numColumns) {
		//how-to-buy grid has 3 columns - for even spacing, add a clearfix to the HTL every third item
		if(howToBuyList != null && !howToBuyList.isEmpty()){
			for(int i=1; i<howToBuyList.size(); i++) {
				if(i%numColumns == 0) {
					howToBuyList.get(i).setHasClearfix(true);
				}
			}
		}
		return howToBuyList;
	}

	public String getHeadlineValue() {
		return headLineValue;
	}

	public List<HowToBuyBean> getHowToBuyList() {
		return howToBuyList;
	}

	public String getExternalMappedCurrentPagePath() {
		return externalMappedCurrentPagePath;
	}

	public void setExternalMappedCurrentPagePath(String externalMappedCurrentPagePath) {
		this.externalMappedCurrentPagePath = externalMappedCurrentPagePath;
	}
}
