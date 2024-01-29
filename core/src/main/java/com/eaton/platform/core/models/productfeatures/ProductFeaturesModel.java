package com.eaton.platform.core.models.productfeatures;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import javax.inject.Inject;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.ProductFamilyDetails;
import com.eaton.platform.core.bean.ProductFeaturesBean;
import com.eaton.platform.core.services.ProductFamilyDetailService;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.sling.api.SlingHttpServletRequest;

import org.apache.sling.models.annotations.Source;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ProductFeaturesModel {
	
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(ProductFeaturesModel.class);
	ProductFamilyDetails productFamilyDetails;
	private ProductFeaturesBean productFeaturesBean = new ProductFeaturesBean();
	public static final String CORE_FEATURES = "Core Features";
	public static final String ALT_TEXT = "alttext";
	public static final String COLOR_CODE = "#FFFFFF";
	
	 @Inject
	 private Page currentPage;
	 	   
	 @Inject @Source("sling-object")
	 private SlingHttpServletRequest slingRequest;
		
	 @Inject
	 protected ProductFamilyDetailService productFamilyDetailService;

	/**
	 * Inits the.
	 */
	@PostConstruct
	protected void init() {
		LOG.debug("ProductFeaturesModel :: Init() :: Started");	
		
		productFamilyDetails = productFamilyDetailService.getProductFamilyDetailsBean(currentPage);
		if( productFamilyDetails != null){
		String title =  productFamilyDetails.getCoreFeatureTitle();
		if(title!= null){
			productFeaturesBean.setTitle(title);
		}else{
		productFeaturesBean.setTitle(CommonUtil.getI18NFromResourceBundle(slingRequest,currentPage,CORE_FEATURES));
		}
		if(productFamilyDetails.getCoreFeatureImage()!=null){
		productFeaturesBean.setImgSrc(productFamilyDetails.getCoreFeatureImage());
		}else{
			productFeaturesBean.setImgSrc("");
		}
		if(productFamilyDetails.getCoreFeatureDescription()!=null){
			productFeaturesBean.setText(productFamilyDetails.getCoreFeatureDescription());
		}else{
			productFeaturesBean.setText("");
		}
		productFeaturesBean.setImgAlt(ALT_TEXT);
		productFeaturesBean.setBackgroundColor(COLOR_CODE);
	}
		LOG.debug("ProductFeaturesModel :: Init() :: Exit");
	}

	public ProductFeaturesBean getProductFeaturesBean() {
		return productFeaturesBean;
	}
	
}
