package com.eaton.platform.core.models;


import java.util.List;

public class ChatConfigBean {

	private List<ChatBean> listChatBean;
	
	private int noOfChatViews;

	public List<ChatBean> getListChatBean() {
		return listChatBean;
	}

	public void setListChatBean(List<ChatBean> chatList) {
		this.listChatBean = chatList;
	}
	
	public int getNoOfChatViews() {
		return noOfChatViews;
	}

	public void setNoOfChatViews(int noOfChatViews) {
		this.noOfChatViews = noOfChatViews;
	}
	
}