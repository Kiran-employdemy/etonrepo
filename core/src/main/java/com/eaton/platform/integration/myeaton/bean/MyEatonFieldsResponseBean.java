/*
 * Eaton
 * Copyright (C) 2020 Eaton. All Rights Reserved
 */

package com.eaton.platform.integration.myeaton.bean;

import com.eaton.platform.integration.myeaton.bean.fields.ValidCountriesBean;
import com.eaton.platform.integration.myeaton.bean.fields.ValidStatesBean;
import com.eaton.platform.integration.myeaton.bean.fields.ValidSupplierAccreditationsBean;
import com.eaton.platform.integration.myeaton.bean.fields.ValidSupplierCommoditiesBean;
import com.google.gson.annotations.Expose;

/** Retrieves valid field options for use with My Eaton form/services
 * Going to expose specific fields for now, to save on some response space. */
public class MyEatonFieldsResponseBean {
    private String[] validRequestTypes;
    private String[] validAccountTypes;
    private String[] validSectors;
    private String[] validBusinessUnits;
    private String[] validC3ProductCategories;
    private String[] validOcProductCategories;
    private String[] validCpsAgentRepClassifications;
    private String[] validBidmanVistaRepClassifications;
    private String[] validBidmanVistaVerticalPositions;
    private String[] validCpqProductCategories;
    private String[] validBidmanVotwCustomerTypes;
    private String[] validWccRoles;
    private String[] validSupplierRegions;
    private String[] validSupplierRoles;

    @Expose
    private ValidSupplierAccreditationsBean[] validSupplierAccreditations;
    @Expose
    private ValidSupplierCommoditiesBean[] validSupplierCommodities;

    private String[] validOrderCenterSalesOrgs;
    private String[] validEatonProductUsedCooperPowerSeries;
    private String[] validEatonProductUsedEnergyAutomationSolutions;

    @Expose
    private ValidCountriesBean[] validCountries;
    @Expose
    private ValidStatesBean[] validStates;

    private String[] validOcPermissions;

    public String[] getValidRequestTypes() {
        return validRequestTypes;
    }

    public void setValidRequestTypes(String[] validRequestTypes) {
        this.validRequestTypes = validRequestTypes;
    }

    public String[] getValidAccountTypes() {
        return validAccountTypes;
    }

    public void setValidAccountTypes(String[] validAccountTypes) {
        this.validAccountTypes = validAccountTypes;
    }

    public String[] getValidSectors() {
        return validSectors;
    }

    public void setValidSectors(String[] validSectors) {
        this.validSectors = validSectors;
    }

    public String[] getValidBusinessUnits() {
        return validBusinessUnits;
    }

    public void setValidBusinessUnits(String[] validBusinessUnits) {
        this.validBusinessUnits = validBusinessUnits;
    }

    public String[] getValidC3ProductCategories() {
        return validC3ProductCategories;
    }

    public void setValidC3ProductCategories(String[] validC3ProductCategories) {
        this.validC3ProductCategories = validC3ProductCategories;
    }

    public String[] getValidOcProductCategories() {
        return validOcProductCategories;
    }

    public void setValidOcProductCategories(String[] validOcProductCategories) {
        this.validOcProductCategories = validOcProductCategories;
    }

    public String[] getValidCpsAgentRepClassifications() {
        return validCpsAgentRepClassifications;
    }

    public void setValidCpsAgentRepClassifications(String[] validCpsAgentRepClassifications) {
        this.validCpsAgentRepClassifications = validCpsAgentRepClassifications;
    }

    public String[] getValidBidmanVistaRepClassifications() {
        return validBidmanVistaRepClassifications;
    }

    public void setValidBidmanVistaRepClassifications(String[] validBidmanVistaRepClassifications) {
        this.validBidmanVistaRepClassifications = validBidmanVistaRepClassifications;
    }

    public String[] getValidBidmanVistaVerticalPositions() {
        return validBidmanVistaVerticalPositions;
    }

    public void setValidBidmanVistaVerticalPositions(String[] validBidmanVistaVerticalPositions) {
        this.validBidmanVistaVerticalPositions = validBidmanVistaVerticalPositions;
    }

    public String[] getValidCpqProductCategories() {
        return validCpqProductCategories;
    }

    public void setValidCpqProductCategories(String[] validCpqProductCategories) {
        this.validCpqProductCategories = validCpqProductCategories;
    }

    public String[] getValidBidmanVotwCustomerTypes() {
        return validBidmanVotwCustomerTypes;
    }

    public void setValidBidmanVotwCustomerTypes(String[] validBidmanVotwCustomerTypes) {
        this.validBidmanVotwCustomerTypes = validBidmanVotwCustomerTypes;
    }

    public String[] getValidWccRoles() {
        return validWccRoles;
    }

    public void setValidWccRoles(String[] validWccRoles) {
        this.validWccRoles = validWccRoles;
    }

    public String[] getValidSupplierRegions() {
        return validSupplierRegions;
    }

    public void setValidSupplierRegions(String[] validSupplierRegions) {
        this.validSupplierRegions = validSupplierRegions;
    }

    public String[] getValidSupplierRoles() {
        return validSupplierRoles;
    }

    public void setValidSupplierRoles(String[] validSupplierRoles) {
        this.validSupplierRoles = validSupplierRoles;
    }

    public ValidSupplierAccreditationsBean[] getValidSupplierAccreditations() {
        return validSupplierAccreditations;
    }

    public void setValidSupplierAccreditations(
        ValidSupplierAccreditationsBean[] validSupplierAccreditations) {
        this.validSupplierAccreditations = validSupplierAccreditations;
    }

    public ValidSupplierCommoditiesBean[] getValidSupplierCommodities() {
        return validSupplierCommodities;
    }

    public void setValidSupplierCommodities(
        ValidSupplierCommoditiesBean[] validSupplierCommodities) {
        this.validSupplierCommodities = validSupplierCommodities;
    }

    public String[] getValidOrderCenterSalesOrgs() {
        return validOrderCenterSalesOrgs;
    }

    public void setValidOrderCenterSalesOrgs(String[] validOrderCenterSalesOrgs) {
        this.validOrderCenterSalesOrgs = validOrderCenterSalesOrgs;
    }

    public String[] getValidEatonProductUsedCooperPowerSeries() {
        return validEatonProductUsedCooperPowerSeries;
    }

    public void setValidEatonProductUsedCooperPowerSeries(
        String[] validEatonProductUsedCooperPowerSeries) {
        this.validEatonProductUsedCooperPowerSeries = validEatonProductUsedCooperPowerSeries;
    }

    public String[] getValidEatonProductUsedEnergyAutomationSolutions() {
        return validEatonProductUsedEnergyAutomationSolutions;
    }

    public void setValidEatonProductUsedEnergyAutomationSolutions(
        String[] validEatonProductUsedEnergyAutomationSolutions) {
        this.validEatonProductUsedEnergyAutomationSolutions = validEatonProductUsedEnergyAutomationSolutions;
    }

    public ValidCountriesBean[] getValidCountries() {
        return validCountries;
    }

    public void setValidCountries(
        ValidCountriesBean[] validCountries) {
        this.validCountries = validCountries;
    }

    public ValidStatesBean[] getValidStates() {
        return validStates;
    }

    public void setValidStates(
        ValidStatesBean[] validStates) {
        this.validStates = validStates;
    }

    public String[] getValidOcPermissions() {
        return validOcPermissions;
    }

    public void setValidOcPermissions(String[] validOcPermissions) {
        this.validOcPermissions = validOcPermissions;
    }
}
