package com.eaton.platform.core.bean;

import java.util.List;
import java.util.Map;

public class AttrGrpBean {

private String attrGrpId;
private String attrGrpLabel;
private String attrGrpsubType;
private Byte attrGrpSeq;
//private List<Map<String, AtrNmElementBean>> attrNmIdMapItemsBeanList;
//List<AtrNmElementBean> attrNmBeanList;
public String getAttrGrpId() {
	return attrGrpId;
}
public void setAttrGrpId(String attrGrpId) {
	this.attrGrpId = attrGrpId;
}
public String getAttrGrpLabel() {
	return attrGrpLabel;
}
public void setAttrGrpLabel(String attrGrpLabel) {
	this.attrGrpLabel = attrGrpLabel;
}
public String getAttrGrpsubType() {
	return attrGrpsubType;
}
public void setAttrGrpsubType(String attrGrpsubType) {
	this.attrGrpsubType = attrGrpsubType;
}


public Byte getAttrGrpSeq() {
	return attrGrpSeq;
}
public void setAttrGrpSeq(Byte attrGrpSeq) {
	this.attrGrpSeq = attrGrpSeq;
}


}
