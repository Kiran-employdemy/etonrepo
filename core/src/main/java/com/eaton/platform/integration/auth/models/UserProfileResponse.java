
package com.eaton.platform.integration.auth.models;

import com.fasterxml.jackson.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "eatonEpicIdentifier",
    "nsUniqueId",
    "dn",
    "roles",
    "mail",
    "eatonpersontype",
    "eatonpersonsubtype",
    "eatonbusinessunit",
    "cn",
    "givenname",
    "sn",
    "c",
    "preferredlanguage",
    "eatonc3ID",
    "eatondrcid",
    "eatonlmsid",
    "eatonsiebelid",
    "nsrole",
    "nsroledn",
    "uid",
    "eatonproductcategory",
    "eatonproductcategoryc3",
    "eatonproductcategorydrc",
    "eatonproductcategorypk",
    "eatonstatus",
    "mobile",
    "eatonportalstatus",
    "telephonenumber",
    "eatoniccvistauid",
    "eatonadminapps",
    "st",
    "l",
    "street",
    "postalcode",
    "eatoncustsite",
    "eatonsuppliercompany",
    "preferredlocale",
    "createtimestamp",
    "eatonlastportallogin",
    "eatonuserapp",
    "objectclass",
    "eatoncustsiteid",
    "eatonsuppliercompanyid"
})
public class UserProfileResponse {

    @JsonProperty("eatonEpicIdentifier")
    private Object eatonEpicIdentifier;
    @JsonProperty("nsUniqueId")
    private String nsUniqueId;
    @JsonProperty("dn")
    private String dn;
    @JsonProperty("roles")
    private List<String> roles;
    @JsonProperty("mail")
    private String mail;
    @JsonProperty("eatonpersontype")
    private String eatonpersontype;
    @JsonProperty("eatonpersonsubtype")
    private Object eatonpersonsubtype;
    @JsonProperty("eatonbusinessunit")
    private List<String> eatonbusinessunit = null;
    @JsonProperty("cn")
    private String cn;
    @JsonProperty("givenname")
    private String givenname;
    @JsonProperty("sn")
    private String sn;
    @JsonProperty("c")
    private String c;
    @JsonProperty("preferredlanguage")
    private String preferredlanguage;
    @JsonProperty("eatonc3ID")
    private Object eatonc3ID;
    @JsonProperty("eatondrcid")
    private String eatondrcid;
    @JsonProperty("eatonlmsid")
    private Object eatonlmsid;
    @JsonProperty("eatonsiebelid")
    private Object eatonsiebelid;
    @JsonProperty("nsrole")
    private List<String> nsrole = null;
    @JsonProperty("nsroledn")
    private List<String> nsroledn = null;
    @JsonProperty("userOktaGroups")
    private List<String> userOktaGroups = new ArrayList<>();
    @JsonProperty("userOktaAppLinks")
    private List<String> userOktaAppLinks = new ArrayList<>();
    @JsonProperty("uid")
    private String uid;
    @JsonProperty("eatonproductcategory")
    private List<String> eatonproductcategory;
    @JsonProperty("eatonproductcategoryc3")
    private Object eatonproductcategoryc3;
    @JsonProperty("eatonproductcategorydrc")
    private Object eatonproductcategorydrc;
    @JsonProperty("eatonproductcategorypk")
    private Object eatonproductcategorypk;
    @JsonProperty("eatonstatus")
    private String eatonstatus;
    @JsonProperty("mobile")
    private Object mobile;
    @JsonProperty("eatonportalstatus")
    private String eatonportalstatus;
    @JsonProperty("telephonenumber")
    private String telephonenumber;
    @JsonProperty("eatoniccvistauid")
    private String eatoniccvistauid;
    @JsonProperty("eatonadminapps")
    private Object eatonadminapps;
    @JsonProperty("st")
    private String st;
    @JsonProperty("l")
    private String l;
    @JsonProperty("street")
    private String street;
    @JsonProperty("postalcode")
    private String postalcode;
    @JsonProperty("eatoncustsite")
    private Eatoncustsite eatoncustsite;
    @JsonProperty("eatonsuppliercompany")
    private Object eatonsuppliercompany;
    @JsonProperty("preferredlocale")
    private String preferredlocale;
    @JsonProperty("createtimestamp")
    private String createtimestamp;
    @JsonProperty("eatonlastportallogin")
    private Object eatonlastportallogin;
    @JsonProperty("eatonuserapp")
    private Object eatonuserapp;
    @JsonProperty("objectclass")
    private List<String> objectclass = null;
    @JsonProperty("eatoncustsiteid")
    private String eatoncustsiteid;
    @JsonProperty("eatonsuppliercompanyid")
    private Object eatonsuppliercompanyid;
    @JsonProperty("partnerProgramAndTierLevels")
    private List<String> partnerProgramAndTierLevels;
    @JsonProperty("eatoneulaacceptdate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyMMddHHmmssZ")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date eatonEulaAcceptDate;
    @JsonProperty("eatoneshopeulaacceptdate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyMMddHHmmssZ")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date eatonEshopEulaAcceptDate;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();

    @JsonProperty("eatonEpicIdentifier")
    public Object getEatonEpicIdentifier() {
        return eatonEpicIdentifier;
    }

    @JsonProperty("eatonEpicIdentifier")
    public void setEatonEpicIdentifier(Object eatonEpicIdentifier) {
        this.eatonEpicIdentifier = eatonEpicIdentifier;
    }

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

    @JsonProperty("roles")
    public List<String> getRoles() {
        return roles;
    }

    @JsonProperty("roles")
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    @JsonProperty("mail")
    public String getMail() {
        return mail;
    }

    @JsonProperty("mail")
    public void setMail(String mail) {
        this.mail = mail;
    }

    @JsonProperty("eatonpersontype")
    public String getEatonpersontype() {
        return eatonpersontype;
    }

    @JsonProperty("eatonpersontype")
    public void setEatonpersontype(String eatonpersontype) {
        this.eatonpersontype = eatonpersontype;
    }

    @JsonProperty("eatonpersonsubtype")
    public Object getEatonpersonsubtype() {
        return eatonpersonsubtype;
    }

    @JsonProperty("eatonpersonsubtype")
    public void setEatonpersonsubtype(Object eatonpersonsubtype) {
        this.eatonpersonsubtype = eatonpersonsubtype;
    }

    @JsonProperty("eatonbusinessunit")
    public List<String> getEatonbusinessunit() {
        return eatonbusinessunit;
    }

    @JsonProperty("eatonbusinessunit")
    public void setEatonbusinessunit(List<String> eatonbusinessunit) {
        this.eatonbusinessunit = eatonbusinessunit;
    }

    @JsonProperty("cn")
    public String getCn() {
        return cn;
    }

    @JsonProperty("cn")
    public void setCn(String cn) {
        this.cn = cn;
    }

    @JsonProperty("givenname")
    public String getGivenname() {
        return givenname;
    }

    @JsonProperty("givenname")
    public void setGivenname(String givenname) {
        this.givenname = givenname;
    }

    @JsonProperty("sn")
    public String getSn() {
        return sn;
    }

    @JsonProperty("sn")
    public void setSn(String sn) {
        this.sn = sn;
    }

    @JsonProperty("c")
    public String getC() {
        return c;
    }

    @JsonProperty("c")
    public void setC(String c) {
        this.c = c;
    }

    @JsonProperty("preferredlanguage")
    public String getPreferredlanguage() {
        return preferredlanguage;
    }

    @JsonProperty("preferredlanguage")
    public void setPreferredlanguage(String preferredlanguage) {
        this.preferredlanguage = preferredlanguage;
    }

    @JsonProperty("eatonc3ID")
    public Object getEatonc3ID() {
        return eatonc3ID;
    }

    @JsonProperty("eatonc3ID")
    public void setEatonc3ID(Object eatonc3ID) {
        this.eatonc3ID = eatonc3ID;
    }

    @JsonProperty("eatondrcid")
    public String getEatondrcid() {
        return eatondrcid;
    }

    @JsonProperty("eatondrcid")
    public void setEatondrcid(String eatondrcid) {
        this.eatondrcid = eatondrcid;
    }

    @JsonProperty("eatonlmsid")
    public Object getEatonlmsid() {
        return eatonlmsid;
    }

    @JsonProperty("eatonlmsid")
    public void setEatonlmsid(Object eatonlmsid) {
        this.eatonlmsid = eatonlmsid;
    }

    @JsonProperty("eatonsiebelid")
    public Object getEatonsiebelid() {
        return eatonsiebelid;
    }

    @JsonProperty("eatonsiebelid")
    public void setEatonsiebelid(Object eatonsiebelid) {
        this.eatonsiebelid = eatonsiebelid;
    }

    @JsonProperty("nsrole")
    public List<String> getNsrole() {
        return nsrole;
    }

    @JsonProperty("nsrole")
    public void setNsrole(List<String> nsrole) {
        this.nsrole = nsrole;
    }

    @JsonProperty("nsroledn")
    public List<String> getNsroledn() {
        return nsroledn;
    }

    @JsonProperty("nsroledn")
    public void setNsroledn(List<String> nsroledn) {
        this.nsroledn = nsroledn;
    }

    @JsonProperty("uid")
    public String getUid() {
        return uid;
    }

    @JsonProperty("uid")
    public void setUid(String uid) {
        this.uid = uid;
    }

    @JsonProperty("eatonproductcategory")
    public List<String> getEatonproductcategory() {
        return eatonproductcategory;
    }

    @JsonProperty("eatonproductcategory")
    public void setEatonproductcategory(List<String> eatonproductcategory) {
        this.eatonproductcategory = eatonproductcategory;
    }

    @JsonProperty("eatonproductcategoryc3")
    public Object getEatonproductcategoryc3() {
        return eatonproductcategoryc3;
    }

    @JsonProperty("eatonproductcategoryc3")
    public void setEatonproductcategoryc3(Object eatonproductcategoryc3) {
        this.eatonproductcategoryc3 = eatonproductcategoryc3;
    }

    @JsonProperty("eatonproductcategorydrc")
    public Object getEatonproductcategorydrc() {
        return eatonproductcategorydrc;
    }

    @JsonProperty("eatonproductcategorydrc")
    public void setEatonproductcategorydrc(Object eatonproductcategorydrc) {
        this.eatonproductcategorydrc = eatonproductcategorydrc;
    }

    @JsonProperty("eatonproductcategorypk")
    public Object getEatonproductcategorypk() {
        return eatonproductcategorypk;
    }

    @JsonProperty("eatonproductcategorypk")
    public void setEatonproductcategorypk(Object eatonproductcategorypk) {
        this.eatonproductcategorypk = eatonproductcategorypk;
    }

    @JsonProperty("eatonstatus")
    public String getEatonstatus() {
        return eatonstatus;
    }

    @JsonProperty("eatonstatus")
    public void setEatonstatus(String eatonstatus) {
        this.eatonstatus = eatonstatus;
    }

    @JsonProperty("mobile")
    public Object getMobile() {
        return mobile;
    }

    @JsonProperty("mobile")
    public void setMobile(Object mobile) {
        this.mobile = mobile;
    }

    @JsonProperty("eatonportalstatus")
    public String getEatonportalstatus() {
        return eatonportalstatus;
    }

    @JsonProperty("eatonportalstatus")
    public void setEatonportalstatus(String eatonportalstatus) {
        this.eatonportalstatus = eatonportalstatus;
    }

    @JsonProperty("telephonenumber")
    public String getTelephonenumber() {
        return telephonenumber;
    }

    @JsonProperty("telephonenumber")
    public void setTelephonenumber(String telephonenumber) {
        this.telephonenumber = telephonenumber;
    }

    @JsonProperty("eatoniccvistauid")
    public String getEatoniccvistauid() {
        return eatoniccvistauid;
    }

    @JsonProperty("eatoniccvistauid")
    public void setEatoniccvistauid(String eatoniccvistauid) {
        this.eatoniccvistauid = eatoniccvistauid;
    }

    @JsonProperty("eatonadminapps")
    public Object getEatonadminapps() {
        return eatonadminapps;
    }

    @JsonProperty("eatonadminapps")
    public void setEatonadminapps(Object eatonadminapps) {
        this.eatonadminapps = eatonadminapps;
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

    @JsonProperty("street")
    public String getStreet() {
        return street;
    }

    @JsonProperty("street")
    public void setStreet(String street) {
        this.street = street;
    }

    @JsonProperty("postalcode")
    public String getPostalcode() {
        return postalcode;
    }

    @JsonProperty("postalcode")
    public void setPostalcode(String postalcode) {
        this.postalcode = postalcode;
    }

    @JsonProperty("eatoncustsite")
    public Eatoncustsite getEatoncustsite() {
        return eatoncustsite;
    }

    @JsonProperty("eatoncustsite")
    public void setEatoncustsite(Eatoncustsite eatoncustsite) {
        this.eatoncustsite = eatoncustsite;
    }

    @JsonProperty("eatonsuppliercompany")
    public Object getEatonsuppliercompany() {
        return eatonsuppliercompany;
    }

    @JsonProperty("eatonsuppliercompany")
    public void setEatonsuppliercompany(Object eatonsuppliercompany) {
        this.eatonsuppliercompany = eatonsuppliercompany;
    }

    @JsonProperty("preferredlocale")
    public String getPreferredlocale() {
        return preferredlocale;
    }

    @JsonProperty("preferredlocale")
    public void setPreferredlocale(String preferredlocale) {
        this.preferredlocale = preferredlocale;
    }

    @JsonProperty("createtimestamp")
    public String getCreatetimestamp() {
        return createtimestamp;
    }

    @JsonProperty("createtimestamp")
    public void setCreatetimestamp(String createtimestamp) {
        this.createtimestamp = createtimestamp;
    }

    @JsonProperty("eatonlastportallogin")
    public Object getEatonlastportallogin() {
        return eatonlastportallogin;
    }

    @JsonProperty("eatonlastportallogin")
    public void setEatonlastportallogin(Object eatonlastportallogin) {
        this.eatonlastportallogin = eatonlastportallogin;
    }

    @JsonProperty("eatonuserapp")
    public Object getEatonuserapp() {
        return eatonuserapp;
    }

    @JsonProperty("eatonuserapp")
    public void setEatonuserapp(Object eatonuserapp) {
        this.eatonuserapp = eatonuserapp;
    }

    @JsonProperty("objectclass")
    public List<String> getObjectclass() {
        return objectclass;
    }

    @JsonProperty("objectclass")
    public void setObjectclass(List<String> objectclass) {
        this.objectclass = objectclass;
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

    public List<String> getPartnerProgramAndTierLevels() {
        return partnerProgramAndTierLevels;
    }

    public Date getEatonEulaAcceptDate() {
        return eatonEulaAcceptDate;
    }

    public void setEatonEulaAcceptDate(Date eatonEulaAcceptDate) {
        this.eatonEulaAcceptDate = eatonEulaAcceptDate;
    }

    public Date getEatonEshopEulaAcceptDate() {
        return eatonEshopEulaAcceptDate;
    }

    public void setEatonEshopEulaAcceptDate(Date eatonEshopEulaAcceptDate) {
        this.eatonEshopEulaAcceptDate = eatonEshopEulaAcceptDate;
    }

    public List<String> getUserOktaGroups() {
        return userOktaGroups;
    }

    public List<String> getUserOktaAppLinks() {
        return userOktaAppLinks;
    }
}
