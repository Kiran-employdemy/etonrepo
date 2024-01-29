package com.eaton.platform.core.models;

import com.adobe.xfa.Bool;
import com.day.cq.wcm.api.Page;

import java.util.Iterator;
import java.util.List;
import com.eaton.platform.core.bean.ProductGridSelectors;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.core.services.EatonSiteConfigService;
import com.eaton.platform.integration.endeca.bean.familymodule.SKUListResponseBean;
import com.eaton.platform.integration.endeca.services.EndecaService;
import com.eaton.platform.core.services.EndecaRequestService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import com.eaton.platform.integration.endeca.bean.familymodule.FamilyModuleBean;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import com.eaton.platform.core.services.EatonConfigService;
import java.util.Optional;
import com.eaton.platform.core.services.FacetURLBeanService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.slf4j.Logger;
import java.util.stream.Collectors;
import java.util.ArrayList;
import org.slf4j.LoggerFactory;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import com.eaton.platform.core.bean.FacetURLBeanServiceResponse;

@Model(adaptables = { Resource.class, SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class RecommendedProductsModel {
	private static final Logger LOG = LoggerFactory.getLogger(RecommendedProductsModel.class);
	private static final String SEARCH_TYPE_STANDARD = "standard";

	@OSGiService
	private EndecaRequestService endecaRequestService;

	@OSGiService
	private EatonSiteConfigService eatonSiteConfigService;

	@OSGiService
	private EndecaService endecaService;

	@OSGiService
	private EatonConfigService configService;

	@OSGiService
	private FacetURLBeanService facetURLBeanService;

	@Inject
	private Resource resource;

	@Inject
	private Page currentPage;

	@Inject
	private SlingHttpServletRequest slingRequest;

	@Inject @Via("resource")
	@Default(values = SEARCH_TYPE_STANDARD)
	private String searchType;

	private String baseSKUPath;
	private List<FamilyModuleBean> familyModuleBeanList;
	private SiteResourceSlingModel siteResourceSlingModel;
	private String specificationLabel;
	private String resourceLabel;
	private boolean exceedLimit = false;
	private boolean noResults = false;

	@PostConstruct
	public void init() {
		if (configService != null && endecaService != null && eatonSiteConfigService != null && endecaRequestService != null) {
			final String skuPageName = configService.getConfigServiceBean().getSkupagename();
			final Optional<SiteResourceSlingModel> siteConfig = eatonSiteConfigService.getSiteConfig(currentPage);

			if (siteConfig.isPresent()) {
				siteResourceSlingModel = siteConfig.get();
			} else {
				LOG.error("Site config was not authored: " + currentPage.getPath());
			}

			final String pageType = currentPage.getProperties().get(CommonConstants.PAGE_TYPE).toString();
			FacetURLBeanServiceResponse facetURLBeanResponse = null;
			if(null != facetURLBeanService && null != siteResourceSlingModel) {
				facetURLBeanResponse = facetURLBeanService.getFacetURLBeanResponse(slingRequest.getRequestPathInfo().getSelectors(), siteResourceSlingModel.getPageSize(), pageType, resource.getPath());
			}

			final ProductGridSelectors productGridSelectors;			
			if (null != facetURLBeanResponse) {
				productGridSelectors = facetURLBeanResponse.getProductGridSelectors();
			} else {
				productGridSelectors = null;
			}

			final List<String> activeFacets = (null != productGridSelectors && productGridSelectors.getFacets() != null)
					? productGridSelectors.getFacets().stream().map(facet -> facet.getFacetID()).collect(Collectors.toList())
					: new ArrayList<>();

			final EndecaServiceRequestBean endecaServiceRequest;
			final SKUListResponseBean skuListResponseBean;
			int pageSize = siteConfig.isPresent() ? siteConfig.get().getPageSize() : 0;
			if (SEARCH_TYPE_STANDARD.equals(searchType)) {
				endecaServiceRequest = endecaRequestService.getProductFamilyEndecaRequestBean(currentPage, activeFacets, null, null, "");
				skuListResponseBean = endecaService.getSKUList(endecaServiceRequest, resource);
			} else {
				endecaServiceRequest = endecaRequestService.getAltProductFamilyEndecaRequestBean(currentPage, activeFacets, null, null, "", pageSize);
				skuListResponseBean = endecaService.getSKUList(endecaServiceRequest, resource);
				if (null != skuListResponseBean.getFamilyModuleResponse().getFamilyModule()) {
					for(Iterator<FamilyModuleBean> itr = skuListResponseBean.getFamilyModuleResponse().getFamilyModule().iterator(); itr.hasNext();){
						FamilyModuleBean sku = itr.next();
						if(!Boolean.valueOf(sku.getAltSku())){
							itr.remove();
						}
					}
				}
			}

			if( null != skuListResponseBean.getFamilyModuleResponse().getFamilyModule()) {
				skuListResponseBean.getFamilyModuleResponse().getFamilyModule()
						.forEach(sku -> {
							if (StringUtils.isNotBlank(sku.getCatalogNumber())) {
								sku.setEncodedCatalogNumber(CommonUtil.encodeURLString(sku.getCatalogNumber()));
							}
						});
			}
			if (skuListResponseBean.getFamilyModuleResponse().getFamilyModule() != null) {
				exceedLimit = skuListResponseBean.getFamilyModuleResponse().getFamilyModule().size() > pageSize;
				noResults = skuListResponseBean.getFamilyModuleResponse().getFamilyModule().size() == 0;
				familyModuleBeanList = skuListResponseBean.getFamilyModuleResponse().getFamilyModule();
			} else {
				exceedLimit = false;
				noResults = true;
			}
			baseSKUPath = CommonUtil.getSKUPagePath(currentPage, skuPageName);
			specificationLabel = CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, CommonConstants.I18N_SPECIFICATION_TAB, "Specification");
			resourceLabel = CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, CommonConstants.I18N_RESOURCE_TAB, "Resource");

		} else {
			if (configService == null) LOG.error("RecommendedProductsModel: configService was null");
			if (endecaService == null) LOG.error("RecommendedProductsModel: endecaService was null");
			if (endecaRequestService == null) LOG.error("RecommendedProductsModel: endecaRequestService was null");
			if (eatonSiteConfigService == null) LOG.error("RecommendedProductsModel: eatonSiteConfigService was null");
		}
	}

	public String getBaseSKUPath() {
		return baseSKUPath;
    }

	public List<FamilyModuleBean> getFamilyModuleBeanList() {
		return familyModuleBeanList;
	}

	public SiteResourceSlingModel getSiteResourceSlingModel() {
		return siteResourceSlingModel;
	}

	public String getSpecificationLabel() {
		return specificationLabel;
	}

	public String getResourceLabel() {
		return resourceLabel;
	}

	public boolean isExceedLimit() {
		return exceedLimit;
	}

	public boolean isNoResults() {
		return noResults;
	}
}
