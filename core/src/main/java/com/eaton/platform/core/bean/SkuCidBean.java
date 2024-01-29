package com.eaton.platform.core.bean;

import java.io.Serializable;
import java.util.List;

public class SkuCidBean implements Serializable {

	private static final long serialVersionUID = 1L;
    private SkuCidDocBean skuCidDocBean;//SonarQube private or transient issue
    private String cidNumber;
	
	public String getCidNumber() {
		return cidNumber;
	}
	public void setCidNumber(String cidNumber) {
		this.cidNumber = cidNumber;
	}
	public SkuCidDocBean getSkuCidDocBean() {
		return skuCidDocBean;
	}
	public void setSkuCidDocBean(SkuCidDocBean skuCidDocBean) {
		this.skuCidDocBean = skuCidDocBean;
	}

	


	
}
