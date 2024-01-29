
package com.eaton.platform.integration.auth.models;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "nsUniqueId",
    "dn",
    "cn",
    "c",
    "eatonaddressline1",
    "eatonaddressline2",
    "postaladdress",
    "eatonappname",
    "eatonappsiteid",
    "eatonappsitemaster",
    "eatonappsitestatus",
    "eatondunsc",
    "eatondunscaseid",
    "eatondunscn",
    "eatondunsdomesticid",
    "eatondunsfacsimiletelephonenumber",
    "eatondunsglobalid",
    "eatondunsl",
    "eatonstatus",
    "telephonenumber",
    "st",
    "l",
    "postalcode",
    "eatondunsparentid",
    "eatondunspostalcode",
    "eatondunsst",
    "eatondunsstreet",
    "eatondunstelephonenumber",
    "physicaldeliveryofficename",
    "eatonparentsuppliercompanyid",
    "eatondunsid",
    "eatoncustsiteid",
    "eatonsuppliercompanyid"
})
public class Eatoncustsite {

    @JsonProperty("nsUniqueId")
    private String nsUniqueId;
    @JsonProperty("dn")
    private String dn;
    @JsonProperty("cn")
    private String cn;
    @JsonProperty("c")
    private String c;
    @JsonProperty("eatonaddressline1")
    private Object eatonaddressline1;
    @JsonProperty("eatonaddressline2")
    private Object eatonaddressline2;
    @JsonProperty("postaladdress")
    private Object postaladdress;
    @JsonProperty("eatonappname")
    private String eatonappname;
    @JsonProperty("eatonappsiteid")
    private String eatonappsiteid;
    @JsonProperty("eatonappsitemaster")
    private String eatonappsitemaster;
    @JsonProperty("eatonappsitestatus")
    private String eatonappsitestatus;
    @JsonProperty("eatondunsc")
    private Object eatondunsc;
    @JsonProperty("eatondunscaseid")
    private Object eatondunscaseid;
    @JsonProperty("eatondunscn")
    private Object eatondunscn;
    @JsonProperty("eatondunsdomesticid")
    private Object eatondunsdomesticid;
    @JsonProperty("eatondunsfacsimiletelephonenumber")
    private Object eatondunsfacsimiletelephonenumber;
    @JsonProperty("eatondunsglobalid")
    private Object eatondunsglobalid;
    @JsonProperty("eatondunsl")
    private Object eatondunsl;
    @JsonProperty("eatonstatus")
    private Object eatonstatus;
    @JsonProperty("telephonenumber")
    private Object telephonenumber;
    @JsonProperty("st")
    private String st;
    @JsonProperty("l")
    private String l;
    @JsonProperty("postalcode")
    private String postalcode;
    @JsonProperty("eatondunsparentid")
    private Object eatondunsparentid;
    @JsonProperty("eatondunspostalcode")
    private Object eatondunspostalcode;
    @JsonProperty("eatondunsst")
    private Object eatondunsst;
    @JsonProperty("eatondunsstreet")
    private Object eatondunsstreet;
    @JsonProperty("eatondunstelephonenumber")
    private Object eatondunstelephonenumber;
    @JsonProperty("physicaldeliveryofficename")
    private Object physicaldeliveryofficename;
    @JsonProperty("eatonparentsuppliercompanyid")
    private Object eatonparentsuppliercompanyid;
    @JsonProperty("eatondunsid")
    private Object eatondunsid;
    @JsonProperty("eatoncustsiteid")
    private String eatoncustsiteid;
    @JsonProperty("eatonsuppliercompanyid")
    private Object eatonsuppliercompanyid;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("nsUniqueId")
    public String getNsUniqueId() {
        return nsUniqueId;
    }

    @JsonProperty("nsUniqueId")
    public void setNsUniqueId(String nsUniqueId) {
        this.nsUniqueId = nsUniqueId;
    }

    @JsonProperty("dn")
    public String getDn() {
        return dn;
    }

    @JsonProperty("dn")
    public void setDn(String dn) {
        this.dn = dn;
    }

    @JsonProperty("cn")
    public String getCn() {
        return cn;
    }

    @JsonProperty("cn")
    public void setCn(String cn) {
        this.cn = cn;
    }

    @JsonProperty("c")
    public String getC() {
        return c;
    }

    @JsonProperty("c")
    public void setC(String c) {
        this.c = c;
    }

    @JsonProperty("eatonaddressline1")
    public Object getEatonaddressline1() {
        return eatonaddressline1;
    }

    @JsonProperty("eatonaddressline1")
    public void setEatonaddressline1(Object eatonaddressline1) {
        this.eatonaddressline1 = eatonaddressline1;
    }

    @JsonProperty("eatonaddressline2")
    public Object getEatonaddressline2() {
        return eatonaddressline2;
    }

    @JsonProperty("eatonaddressline2")
    public void setEatonaddressline2(Object eatonaddressline2) {
        this.eatonaddressline2 = eatonaddressline2;
    }

    @JsonProperty("postaladdress")
    public Object getPostaladdress() {
        return postaladdress;
    }

    @JsonProperty("postaladdress")
    public void setPostaladdress(Object postaladdress) {
        this.postaladdress = postaladdress;
    }

    @JsonProperty("eatonappname")
    public String getEatonappname() {
        return eatonappname;
    }

    @JsonProperty("eatonappname")
    public void setEatonappname(String eatonappname) {
        this.eatonappname = eatonappname;
    }

    @JsonProperty("eatonappsiteid")
    public String getEatonappsiteid() {
        return eatonappsiteid;
    }

    @JsonProperty("eatonappsiteid")
    public void setEatonappsiteid(String eatonappsiteid) {
        this.eatonappsiteid = eatonappsiteid;
    }

    @JsonProperty("eatonappsitemaster")
    public String getEatonappsitemaster() {
        return eatonappsitemaster;
    }

    @JsonProperty("eatonappsitemaster")
    public void setEatonappsitemaster(String eatonappsitemaster) {
        this.eatonappsitemaster = eatonappsitemaster;
    }

    @JsonProperty("eatonappsitestatus")
    public String getEatonappsitestatus() {
        return eatonappsitestatus;
    }

    @JsonProperty("eatonappsitestatus")
    public void setEatonappsitestatus(String eatonappsitestatus) {
        this.eatonappsitestatus = eatonappsitestatus;
    }

    @JsonProperty("eatondunsc")
    public Object getEatondunsc() {
        return eatondunsc;
    }

    @JsonProperty("eatondunsc")
    public void setEatondunsc(Object eatondunsc) {
        this.eatondunsc = eatondunsc;
    }

    @JsonProperty("eatondunscaseid")
    public Object getEatondunscaseid() {
        return eatondunscaseid;
    }

    @JsonProperty("eatondunscaseid")
    public void setEatondunscaseid(Object eatondunscaseid) {
        this.eatondunscaseid = eatondunscaseid;
    }

    @JsonProperty("eatondunscn")
    public Object getEatondunscn() {
        return eatondunscn;
    }

    @JsonProperty("eatondunscn")
    public void setEatondunscn(Object eatondunscn) {
        this.eatondunscn = eatondunscn;
    }

    @JsonProperty("eatondunsdomesticid")
    public Object getEatondunsdomesticid() {
        return eatondunsdomesticid;
    }

    @JsonProperty("eatondunsdomesticid")
    public void setEatondunsdomesticid(Object eatondunsdomesticid) {
        this.eatondunsdomesticid = eatondunsdomesticid;
    }

    @JsonProperty("eatondunsfacsimiletelephonenumber")
    public Object getEatondunsfacsimiletelephonenumber() {
        return eatondunsfacsimiletelephonenumber;
    }

    @JsonProperty("eatondunsfacsimiletelephonenumber")
    public void setEatondunsfacsimiletelephonenumber(Object eatondunsfacsimiletelephonenumber) {
        this.eatondunsfacsimiletelephonenumber = eatondunsfacsimiletelephonenumber;
    }

    @JsonProperty("eatondunsglobalid")
    public Object getEatondunsglobalid() {
        return eatondunsglobalid;
    }

    @JsonProperty("eatondunsglobalid")
    public void setEatondunsglobalid(Object eatondunsglobalid) {
        this.eatondunsglobalid = eatondunsglobalid;
    }

    @JsonProperty("eatondunsl")
    public Object getEatondunsl() {
        return eatondunsl;
    }

    @JsonProperty("eatondunsl")
    public void setEatondunsl(Object eatondunsl) {
        this.eatondunsl = eatondunsl;
    }

    @JsonProperty("eatonstatus")
    public Object getEatonstatus() {
        return eatonstatus;
    }

    @JsonProperty("eatonstatus")
    public void setEatonstatus(Object eatonstatus) {
        this.eatonstatus = eatonstatus;
    }

    @JsonProperty("telephonenumber")
    public Object getTelephonenumber() {
        return telephonenumber;
    }

    @JsonProperty("telephonenumber")
    public void setTelephonenumber(Object telephonenumber) {
        this.telephonenumber = telephonenumber;
    }

    @JsonProperty("st")
    public String getSt() {
        return st;
    }

    @JsonProperty("st")
    public void setSt(String st) {
        this.st = st;
    }

    @JsonProperty("l")
    public String getL() {
        return l;
    }

    @JsonProperty("l")
    public void setL(String l) {
        this.l = l;
    }

    @JsonProperty("postalcode")
    public String getPostalcode() {
        return postalcode;
    }

    @JsonProperty("postalcode")
    public void setPostalcode(String postalcode) {
        this.postalcode = postalcode;
    }

    @JsonProperty("eatondunsparentid")
    public Object getEatondunsparentid() {
        return eatondunsparentid;
    }

    @JsonProperty("eatondunsparentid")
    public void setEatondunsparentid(Object eatondunsparentid) {
        this.eatondunsparentid = eatondunsparentid;
    }

    @JsonProperty("eatondunspostalcode")
    public Object getEatondunspostalcode() {
        return eatondunspostalcode;
    }

    @JsonProperty("eatondunspostalcode")
    public void setEatondunspostalcode(Object eatondunspostalcode) {
        this.eatondunspostalcode = eatondunspostalcode;
    }

    @JsonProperty("eatondunsst")
    public Object getEatondunsst() {
        return eatondunsst;
    }

    @JsonProperty("eatondunsst")
    public void setEatondunsst(Object eatondunsst) {
        this.eatondunsst = eatondunsst;
    }

    @JsonProperty("eatondunsstreet")
    public Object getEatondunsstreet() {
        return eatondunsstreet;
    }

    @JsonProperty("eatondunsstreet")
    public void setEatondunsstreet(Object eatondunsstreet) {
        this.eatondunsstreet = eatondunsstreet;
    }

    @JsonProperty("eatondunstelephonenumber")
    public Object getEatondunstelephonenumber() {
        return eatondunstelephonenumber;
    }

    @JsonProperty("eatondunstelephonenumber")
    public void setEatondunstelephonenumber(Object eatondunstelephonenumber) {
        this.eatondunstelephonenumber = eatondunstelephonenumber;
    }

    @JsonProperty("physicaldeliveryofficename")
    public Object getPhysicaldeliveryofficename() {
        return physicaldeliveryofficename;
    }

    @JsonProperty("physicaldeliveryofficename")
    public void setPhysicaldeliveryofficename(Object physicaldeliveryofficename) {
        this.physicaldeliveryofficename = physicaldeliveryofficename;
    }

    @JsonProperty("eatonparentsuppliercompanyid")
    public Object getEatonparentsuppliercompanyid() {
        return eatonparentsuppliercompanyid;
    }

    @JsonProperty("eatonparentsuppliercompanyid")
    public void setEatonparentsuppliercompanyid(Object eatonparentsuppliercompanyid) {
        this.eatonparentsuppliercompanyid = eatonparentsuppliercompanyid;
    }

    @JsonProperty("eatondunsid")
    public Object getEatondunsid() {
        return eatondunsid;
    }

    @JsonProperty("eatondunsid")
    public void setEatondunsid(Object eatondunsid) {
        this.eatondunsid = eatondunsid;
    }

    @JsonProperty("eatoncustsiteid")
    public String getEatoncustsiteid() {
        return eatoncustsiteid;
    }

    @JsonProperty("eatoncustsiteid")
    public void setEatoncustsiteid(String eatoncustsiteid) {
        this.eatoncustsiteid = eatoncustsiteid;
    }

    @JsonProperty("eatonsuppliercompanyid")
    public Object getEatonsuppliercompanyid() {
        return eatonsuppliercompanyid;
    }

    @JsonProperty("eatonsuppliercompanyid")
    public void setEatonsuppliercompanyid(Object eatonsuppliercompanyid) {
        this.eatonsuppliercompanyid = eatonsuppliercompanyid;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
