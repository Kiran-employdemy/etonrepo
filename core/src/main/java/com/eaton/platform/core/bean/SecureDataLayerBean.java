package com.eaton.platform.core.bean;

import java.io.Serializable;
import java.util.Objects;

/**
 * POJO representation of the secure data layer
 */
public class SecureDataLayerBean implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -2156149953659297832L;

    private  String visitorPartnerprogramAndTier;

    private  String visitorApprovedCountries;

    private  String visitorAccountType;

    private  String visitorProductAuth;

    private  String visitorApplicationAuth;

    private  String visitorCountryAuth;

    /** The login id. */
    private String loginId;

    /** The login state. */
    private String loginState;

    /**
     *
     * @return visitorPartnerprogram
     */
    public  String getVisitorPartnerprogramAndTier() {
        return visitorPartnerprogramAndTier;
    }

    /**
     *
     * @param visitorPartnerprogramAndTier
     */
    public  void setVisitorPartnerProgramTypeAndTier(String visitorPartnerprogramAndTier) {
        this.visitorPartnerprogramAndTier = visitorPartnerprogramAndTier;
    }

    /**
     *
     * @return visitorApprovedCountries
     */
    public  String getVisitorApprovedCountries() {
        return visitorApprovedCountries;
    }

    /**
     *
     * @param visitorApprovedCountries
     */
    public  void setVisitorApprovedCountries(String visitorApprovedCountries) {
        this.visitorApprovedCountries = visitorApprovedCountries;
    }

    /**
     *
     * @return visitorAccountType
     */
    public  String getVisitorAccountType() {
        return visitorAccountType;
    }

    /**
     *
     * @param visitorAccountType
     */
    public  void setVisitorAccountType(String visitorAccountType) {
        this.visitorAccountType = visitorAccountType;
    }

    /**
     *
     * @return visitorProductAuth
     */
    public  String getVisitorProductAuth() {
        return visitorProductAuth;
    }

    /**
     *
     * @param visitorProductAuth
     */
    public  void setVisitorProductAuth(String visitorProductAuth) {
        this.visitorProductAuth = visitorProductAuth;
    }

    /**
     *
     * @return visitorApplicationAuth
     */
    public  String getVisitorApplicationAuth() {
        return visitorApplicationAuth;
    }

    /**
     *
     * @param visitorApplicationAuth
     */
    public  void setVisitorApplicationAuth(String visitorApplicationAuth) {
        this.visitorApplicationAuth = visitorApplicationAuth;
    }

    /**
     *
     * @return visitorCountryAuth
     */
    public  String getVisitorCountryAuth() {
        return visitorCountryAuth;
    }

    /**
     *
     * @param visitorCountryAuth
     */
    public  void setVisitorCountryAuth(String visitorCountryAuth) {
        this.visitorCountryAuth = visitorCountryAuth;
    }

    /**
     *
     * @return loginId
     */
    public String getLoginId() {
        return loginId;
    }

    /**
     *
     * @param loginId
     */
    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    /**
     *
     * @return loginState
     */
    public String getLoginState() {
        return loginState;
    }

    /**
     *
     * @param loginState
     */
    public void setLoginState(String loginState) {
        this.loginState = loginState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SecureDataLayerBean that = (SecureDataLayerBean) o;
        return Objects.equals(visitorPartnerprogramAndTier, that.visitorPartnerprogramAndTier)
                && Objects.equals(visitorApprovedCountries, that.visitorApprovedCountries)
                && Objects.equals(visitorAccountType, that.visitorAccountType)
                && Objects.equals(visitorProductAuth, that.visitorProductAuth)
                && Objects.equals(visitorApplicationAuth, that.visitorApplicationAuth)
                && Objects.equals(visitorCountryAuth, that.visitorCountryAuth)
                && Objects.equals(loginId, that.loginId)
                && Objects.equals(loginState, that.loginState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(visitorPartnerprogramAndTier
                , visitorApprovedCountries
                , visitorAccountType
                , visitorProductAuth
                , visitorApplicationAuth
                , visitorCountryAuth
                , loginId
                , loginState);
    }
}
