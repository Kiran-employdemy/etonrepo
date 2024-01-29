package com.eaton.platform.core.bean;

public class FacetURLBeanServiceResponse {
    private FacetURLBean facetURLBean;
    private ProductGridSelectors productGridSelectors;

    public FacetURLBean getFacetURLBean() {
        return facetURLBean;
    }

    public void setFacetURLBean(FacetURLBean facetURLBean) {
        this.facetURLBean = facetURLBean;
    }

    public ProductGridSelectors getProductGridSelectors() {
        return productGridSelectors;
    }

    public void setProductGridSelectors(ProductGridSelectors productGridSelectors) {
        this.productGridSelectors = productGridSelectors;
    }
}
