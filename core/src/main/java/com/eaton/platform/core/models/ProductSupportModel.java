package com.eaton.platform.core.models;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.ProductFamilyDetails;
import com.eaton.platform.core.bean.ProductFamilyPIMDetails;
import com.eaton.platform.core.bean.ProductSupportBean;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.ProductFamilyDetailService;
import com.eaton.platform.core.services.EndecaRequestService;
import com.eaton.platform.integration.endeca.services.EndecaService;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.commons.lang.StringUtils;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsBean;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsResponseBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.sling.api.SlingHttpServletRequest;



@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ProductSupportModel {

	/** The Constant SUPPORT_INFO_HEADLINE_I18N_KEY. */
	private static final String SUPPORT_INFO_HEADLINE_I18N_KEY = "supportInfoHeadline";

	/** The Constant SUPPORT_INFO_DESC_I18N_KEY. */
	private static final String SUPPORT_INFO_DESC_I18N_KEY = "supportInfoDesc";


	private String supportInfoLabel;
	private String supportInfoText;
	private String supportInfoHeadline;
	private String supportInfoDesc;
	private boolean showComponent;

	@Inject @Source("sling-object")
	private SlingHttpServletRequest slingRequest;

	@Inject
	protected Page currentPage;

	@Inject
	protected ProductFamilyDetailService productFamilyDetailService;

	@Inject
	protected EndecaRequestService endecaRequestService;

	@Inject
	protected EndecaService endecaService;

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductSupportModel.class);

	/**
	 * Inits the.
	 */
	@PostConstruct
	protected void init() {
		LOGGER.debug("ProductSupportModel :: init() :: Started");
		String pageType = "";
		ProductFamilyDetails productFamilyDetails;
		ProductFamilyPIMDetails productFamilyPIMDetails;

		if (currentPage.getProperties().get(CommonConstants.PAGE_TYPE) != null) {
			pageType = currentPage.getProperties().get(CommonConstants.PAGE_TYPE).toString();
		}

		ProductSupportBean productSupportBean = new ProductSupportBean();
		if (!pageType.isEmpty() && pageType.equals(CommonConstants.PAGE_TYPE_PRODUCT_FAMILY_PAGE)) {
			productFamilyDetails = productFamilyDetailService.getProductFamilyDetailsBean(currentPage);
			if(null != productFamilyDetails){
				productSupportBean = productFamilyDetails.getProductSupportBean();
			}

			LOGGER.debug("ProductSupportModel :: int() :: Getting the ProductSupportBean for Product Family page ");

		} else if (!pageType.isEmpty() && pageType.equals(CommonConstants.PAGE_TYPE_PRODUCT_SKU_PAGE)) {
			final String[] selectors = slingRequest.getRequestPathInfo().getSelectors();
			final EndecaServiceRequestBean endecaRequestBean = endecaRequestService
					.getEndecaRequestBean(currentPage, selectors, StringUtils.EMPTY);
			final SKUDetailsResponseBean skuDetails = endecaService.getSKUDetails(endecaRequestBean);
			final SKUDetailsBean skuDetailsBean = skuDetails.getSkuResponse().getSkuDetails();
			productFamilyPIMDetails = productFamilyDetailService.getProductFamilyPIMDetailsBean(skuDetailsBean, currentPage);
			if (null != productFamilyPIMDetails) {
				productSupportBean = productFamilyPIMDetails.getProductSupportBean();
			}

			LOGGER.debug("ProductSupportModel :: int() :: Getting the ProductSupportBean for SKU page");
		}

		// get the Support Info Label and text from PIM node or PDH node(if not configured in PIM node)
		if(null != productSupportBean) {
			supportInfoLabel = StringUtils.trim(productSupportBean.getSupportLabel());
			supportInfoText = StringUtils.trim(productSupportBean.getSupportInformation());
		}
		// get I18N values for Support Info Headline and Description
		supportInfoHeadline = CommonUtil.getI18NFromResourceBundle(slingRequest,currentPage, SUPPORT_INFO_HEADLINE_I18N_KEY);
		supportInfoDesc = CommonUtil.getI18NFromResourceBundle(slingRequest,currentPage, SUPPORT_INFO_DESC_I18N_KEY);
		showComponent = StringUtils.isNotBlank(supportInfoText);

		LOGGER.debug(" ProductSupportModel :: int() :: Exit");
	}

	/**
	 * @return the supportInfoLabel
	 */
	public String getSupportInfoLabel() {
		return supportInfoLabel;
	}

	/**
	 * @return the supportInfoText
	 */
	public String getSupportInfoText() {
		return supportInfoText;
	}

	/**
	 * @return the supportInfoHeadline
	 */
	public String getSupportInfoHeadline() {
		return supportInfoHeadline;
	}

	/**
	 * @return the supportInfoDesc
	 */
	public String getSupportInfoDesc() {
		return supportInfoDesc;
	}

	public boolean isShowComponent() {
		return showComponent;
	}

}
