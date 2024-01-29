package com.eaton.platform.core.bean;

import java.io.Serializable;
import java.util.List;

public class SkuCardListBean implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private List<SkuCardBean>skuCardList;

	public List<SkuCardBean> getSkuCardList() {
		return skuCardList;
	}

	public void setSkuCardList(List<SkuCardBean> skuCardList) {
		this.skuCardList = skuCardList;
	}

	
}
