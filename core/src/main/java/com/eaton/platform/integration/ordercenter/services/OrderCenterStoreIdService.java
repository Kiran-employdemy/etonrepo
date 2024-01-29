package com.eaton.platform.integration.ordercenter.services;

import com.google.gson.JsonElement;

public interface OrderCenterStoreIdService
{
    JsonElement getStoreIds();

    String getOrderCenterTagPath();

    boolean isEnabled();
}
