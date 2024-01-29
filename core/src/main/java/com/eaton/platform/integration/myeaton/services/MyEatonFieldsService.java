/*
 * Eaton
 * Copyright (C) 2020 Eaton. All Rights Reserved
 */

package com.eaton.platform.integration.myeaton.services;

import com.eaton.platform.integration.myeaton.bean.MyEatonFieldsResponseBean;

/** Service to retrieve valid fields for My Eaton */
public interface MyEatonFieldsService {
    MyEatonFieldsResponseBean getFields();
}
