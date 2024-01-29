package com.eaton.platform.core.models;

import javax.inject.Inject;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.bean.BreadcrumbBean;
import com.eaton.platform.core.bean.ProductFamilyDetails;
import com.eaton.platform.core.bean.ProductFamilyPIMDetails;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.injectors.annotations.ProductFamilyDetailsInjector;
import com.eaton.platform.core.injectors.injectoremums.ProductFamilyDetailsInjectorProperties;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class},defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class BreadcrumbModel extends EatonBaseSlingModel {

    private List<BreadcrumbBean> breadcrumbList;
	private int homePageLevel;
	private static final Logger LOG = LoggerFactory.getLogger(BreadcrumbModel.class);
	private int grandParentIndex;
	
	@SlingObject
	private PageManager pageManager;

	@ProductFamilyDetailsInjector(type = ProductFamilyDetailsInjectorProperties.PRODUCT_FAMILY_DETAIL_OBJECT)
	private ProductFamilyDetails pfDetails;

	@ProductFamilyDetailsInjector(type = ProductFamilyDetailsInjectorProperties.PRODUCT_FAMILY_PIM_DETAIL_OBJECT)
	private ProductFamilyPIMDetails pfskuDetails;

	@Inject
	private AdminService adminService;

	@ScriptVariable(name="currentPage")
	private Page currentPageResource;

	
	@PostConstruct
	public void init() {
		LOG.debug("BreadcrumbModel :: init() :: Started");
		 breadcrumbList = new ArrayList<>();
		 Page homePage = CommonUtil.getHomePage(currentPageResource);
		if(null != adminService) {
			if (Objects.nonNull(homePage)) {
				homePageLevel = homePage.getDepth();
			}
			try (ResourceResolver adminServiceReadService = adminService.getReadService()) {
				if (pageType != null && pageType.equals(CommonConstants.PAGE_TYPE_PRODUCT_FAMILY_PAGE)) {
					setProductFamilyObject(adminServiceReadService);
				} else if (pageType != null && pageType.equals(CommonConstants.PAGE_TYPE_PRODUCT_SKU_PAGE)) {
					setSkuObject(adminServiceReadService);
				} else {
					setBreadcrumbList(currentPageResource);
					Collections.reverse(breadcrumbList);
				}
			}catch (Exception exception) {
				LOG.error("Exception occured while getting the reader service", exception);
			}
			grandParentIndex = (breadcrumbList.size()) - 2;
			BreadcrumbBean currentBreadcrumbBean = getCurrentPageBean(currentPageResource);
			breadcrumbList.add(currentBreadcrumbBean);
		}
		 LOG.debug("BreadcrumbModel :: init() :: END");
	}
	
	 
	 
	 
	 public Page setBreadcrumbList(Page page) {
			
			Page upPage = page.getParent();
			
			if(upPage != null && upPage.getDepth() != homePageLevel) {
             if(upPage.isHideInNav()){
				upPage = setBreadcrumbList(upPage);
			}else 
			{
				BreadcrumbBean breadcrumbBean = getPopulatedBean(upPage);
				breadcrumbList.add(breadcrumbBean);
				upPage = setBreadcrumbList(upPage);
			}
			}
			
		
			return upPage;
		}
	 
	 
	 public BreadcrumbBean getPopulatedBean(Page beanPage)
	 {
		 BreadcrumbBean breadcrumbBean = new BreadcrumbBean();
		 breadcrumbBean.setPath(CommonUtil.dotHtmlLink(beanPage.getPath()));
		 breadcrumbBean.setTitle(getEatonPageTitle(beanPage));
		 return breadcrumbBean;
	 }
	 
	 public BreadcrumbBean getCurrentPageBean(Page currentPageResource)
	 {
		 String catalogNumber = null;
		 if(!StringUtils.equals(pageType, CommonConstants.PAGE_TYPE_PRODUCT_SKU_PAGE))
		 {
		 
		 BreadcrumbBean breadcrumbBean = new BreadcrumbBean();
		
		 breadcrumbBean.setTitle(getEatonPageTitle(currentPageResource));
		return breadcrumbBean;
		 }
		 else
		 {
			 
			 BreadcrumbBean breadcrumbBean = new BreadcrumbBean();
             final String[] selectors = slingRequest.getRequestPathInfo().getSelectors();
			 if(selectors.length > 0){
				
				catalogNumber = CommonUtil.decodeSearchTermString(selectors[0]);

				 breadcrumbBean.setTitle(catalogNumber);
				 return breadcrumbBean;
		 }else {
				 breadcrumbBean.setTitle(getEatonPageTitle(currentPageResource));
				 return breadcrumbBean;
			 }
	 }
	 }
	 
	 public String getEatonPageTitle(Page titlePage)
	 {
		 String returnTitle;
		 if(titlePage.getNavigationTitle() != null)
		 {
			 returnTitle = titlePage.getNavigationTitle();
		 }else if(titlePage.getPageTitle() != null)
		 {
			 returnTitle = titlePage.getPageTitle();
		 }else if(titlePage.getTitle() != null)
		 {
			 returnTitle = titlePage.getTitle();
		 }else
		 {
			 returnTitle = titlePage.getName();
		 }
		 
		 return returnTitle;
	 }
	

	public List<BreadcrumbBean> getBreadcrumbList() {
		return breadcrumbList;
	}
	 public int getGrandParentIndex() {
			return grandParentIndex;
		}

	 public void setProductFamilyObject(ResourceResolver  adminServiceReadService){
		 if (pfDetails != null && pfDetails.getPrimarySubCategory() != null) {
			 final Optional<Page> subCategoryPageOptional = Optional.ofNullable(adminServiceReadService.resolve(pfDetails.getPrimarySubCategory()).adaptTo(Page.class));
			 if (subCategoryPageOptional.isPresent()) {
				 setBreadcrumbList(subCategoryPageOptional.get());
				 Collections.reverse(breadcrumbList);
				 BreadcrumbBean subCatBreadcrumbBean = getPopulatedBean(subCategoryPageOptional.get());
				 breadcrumbList.add(subCatBreadcrumbBean);
			 }
		 } else {
			 LOG.debug(" Product family object is null");
		 }
	 }

	 public void setSkuObject(ResourceResolver adminServiceReadService){
		 if (pfskuDetails != null) {
			 if (pfskuDetails.getPrimarySubCategory() != null) {
				 final Optional<Page> primarySubCategoryOptional = Optional.ofNullable(adminServiceReadService
						 .resolve(pfskuDetails.getPrimarySubCategory()).adaptTo(Page.class));
				 if (primarySubCategoryOptional.isPresent()) {
					 setBreadcrumbList(primarySubCategoryOptional.get());
					 Collections.reverse(breadcrumbList);
					 BreadcrumbBean subCatBreadcrumbBean = getPopulatedBean(primarySubCategoryOptional.get());
					 breadcrumbList.add(subCatBreadcrumbBean);
				 }
			 }
			 if (pfskuDetails.getProductFamilyAEMPath() != null) {
				 final Optional<Page> productFamilyAEMPathOptional = Optional.ofNullable(adminServiceReadService
						 .resolve(pfskuDetails.getProductFamilyAEMPath()).adaptTo(Page.class));
				 if (productFamilyAEMPathOptional.isPresent()) {
					 BreadcrumbBean familyBreadcrumbBean = getPopulatedBean(productFamilyAEMPathOptional.get());
					 breadcrumbList.add(familyBreadcrumbBean);
				 }
			 }
		 } else {
			 LOG.debug(" SKU object is null");
		 }
	 }

}
