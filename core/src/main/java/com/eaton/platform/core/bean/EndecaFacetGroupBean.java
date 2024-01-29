package com.eaton.platform.core.bean;

import java.util.List;

public class EndecaFacetGroupBean {
	
	String FacetGroup;
	List<String> Facets;
	
	public String getFacetGroup() {
		return FacetGroup;
	}
	public void setFacetGroup(String facetGroup) {
		FacetGroup = facetGroup;
	}
	public List<String> getFacets() {
		return Facets;
	}
	public void setFacets(List<String> facets) {
		Facets = facets;
	}
	

}
