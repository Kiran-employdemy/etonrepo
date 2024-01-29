/*
 * Eaton
 * Copyright (C) 2020 Eaton. All Rights Reserved
 */

package com.eaton.platform.integration.myeaton.bean;

import com.eaton.platform.integration.myeaton.util.MyEatonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/** Response object that comes back from user registration service */
public class UserRegistrationResponseBean {
    private static final Logger LOG = LoggerFactory.getLogger(UserRegistrationResponseBean.class);

    private String remedyTicketID;
    private boolean existingAccount;
    private String createdAccount;
    private String createdAccountType;
    private JsonObject requestJson;

    public void setRemedyTicketID(String remedyTicketID) {
        this.remedyTicketID = remedyTicketID;
    }

    public String getRemedyTicketID() {
        return this.remedyTicketID;
    }

    public boolean isRemedyTicketIDSet() {
        return this.remedyTicketID != null && !this.remedyTicketID.isEmpty();
    }

    public void setExistingAccount(boolean existingAccount) {
        this.existingAccount = existingAccount;
    }

    public void setCreatedAccount(String createdAccount) {
        this.createdAccount = createdAccount;
    }

    public boolean isCreatedAccountSet() {
        return this.createdAccount != null && !this.createdAccount.isEmpty();
    }

    public void setCreatedAccountType(String createdAccountType) {
        this.createdAccountType = createdAccountType;
    }

    public JsonObject getRequestJson() {
        return requestJson;
    }

    public void setRequestJson(JsonObject requestJson) {
        this.requestJson = requestJson;
    }

    public String[] getRequestSectors() {
        JsonArray sectors = this.requestJson.getAsJsonArray("selectedSectors");

        return new Gson().fromJson(sectors, String[].class);
    }

    public String getRequestAccountType() {
        JsonPrimitive accountType = this.requestJson.getAsJsonPrimitive("selectedAccountType");

        return accountType.getAsString();
    }

    public boolean isSelectedApplicationProjectCentral() {
        JsonPrimitive selectedApplicationProjectCentral = this.requestJson.getAsJsonPrimitive("selectedApplicationProjectCentral");

        return selectedApplicationProjectCentral != null
            && selectedApplicationProjectCentral.getAsBoolean();
    }

    public boolean isAccountCreatedSuccessfully() {
        LOG.debug("Entered into UserRegistrationResponseBean.isAccountCreatedSuccessfully()");

        if (getRequestSectors() != null) {
            String selectedSectors = Arrays.toString(getRequestSectors());
            LOG.debug("Eaton Sectors selected on the Registration Form:");
            LOG.debug(selectedSectors);
        }

        boolean success = false;

        if ("SUPPLIER".equals(this.getRequestAccountType())) {
            success = isCreatedAccountSet();

            LOG.debug("Submission is for SUPPLIER - createdShellAccount:" + isCreatedAccountSet());
        }
        else if(MyEatonUtil.valueExcludedFromArray("ELECTRICAL", getRequestSectors())
            || isSelectedApplicationProjectCentral()) {

            success = isCreatedAccountSet() && isRemedyTicketIDSet();

            LOG.debug("Submission is for ONLY INDUSTRIAL or PROJECT CENTER - createdShellAccount:" + isCreatedAccountSet());
            LOG.debug("Submission is for ONLY INDUSTRIAL or PROJECT CENTER - createdRemedyTicket:" + isRemedyTicketIDSet());
            LOG.debug("Submission is for ONLY INDUSTRIAL or PROJECT CENTER - finalStatus:" + success);
        }
        else {
            // Most cases, check for a remedy ticket
            success = isRemedyTicketIDSet();

            LOG.debug("Submission is NOT SUPPLIER | NOT ONLY INDUSTRIAL| NOT PROJECT CENTER - createdRemedyTicket:" + isRemedyTicketIDSet());
        }

        LOG.debug("Final status to return from isAccountCreatedSuccessfully(): " + success);

        return success;
    }

    public String toString() {
        return String.format("Remedy Ticket ID: %s -- Exisiting Account? %s -- Created Account: %s"
                + "Created Account Type: %s\n --- Request Json --- \n\n%s", this.remedyTicketID,
            this.existingAccount, this.createdAccount, this.createdAccountType, this.requestJson);
    }
}
