package com.eaton.platform.core.services;

import java.util.Map;

public interface EatonEmailService {

    String sendEmail(String emailIdList,Map<String, String> emailParams,String templatePath) ;

    String getFromAddress();

    String getSenderPreName();
}
