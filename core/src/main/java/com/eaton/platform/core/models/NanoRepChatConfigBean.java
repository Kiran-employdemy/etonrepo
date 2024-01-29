package com.eaton.platform.core.models;


import java.util.List;

public class NanoRepChatConfigBean {

	private List<NanoRepChatBean> listChatBean;
	
	private int noOfChatViews;

	public List<NanoRepChatBean> getListChatBean() {
		return listChatBean;
	}

	public void setListChatBean(List<NanoRepChatBean> chatList) {
		this.listChatBean = chatList;
	}
	
	public int getNoOfChatViews() {
		return noOfChatViews;
	}

	public void setNoOfChatViews(int noOfChatViews) {
		this.noOfChatViews = noOfChatViews;
	}
	
}