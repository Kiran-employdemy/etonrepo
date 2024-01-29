package com.eaton.platform.core.bean;

public class AttrNmBean implements Comparable {

	private String id;
	private String label;
	private String seq;
	private String displayFlag;
	private String language;
	private String country;
	private String cdata;
	private String cid;
	private String repfl;

	public String getRepfl() {
		return repfl;
	}
	public void setRepfl(String repfl) {
		this.repfl = repfl;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getSeq() {
		return seq;
	}
	public void setSeq(String seq) {
		this.seq = seq;
	}
	public String getDisplayFlag() {
		return displayFlag;
	}
	public void setDisplayFlag(String displayFlag) {
		this.displayFlag = displayFlag;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getCdata() {
		return cdata;
	}
	public void setCdata(String cdata) {
		this.cdata = cdata;
	}
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}

	public boolean toBeDisplayed() {
		return displayFlag == null || "Y".equals(displayFlag);
	}

	 @Override
	 public int compareTo(Object object) {
		 int sequence1 = 0;
		 int sequence2 = 0;

		 AttrNmBean attrNmBean = (AttrNmBean)object;

		 if(attrNmBean.getSeq()!=null){
			 sequence1 = Integer.parseInt(attrNmBean.getSeq());
		 }

		 if(this.seq!=null){
			 sequence2 = Integer.parseInt(this.seq);
		 }
		 return sequence2-sequence1;
	 }

}
