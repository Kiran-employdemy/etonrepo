package com.eaton.platform.core.services.impl;

import java.util.HashMap;
import java.util.Map;

public class EmailParamFixtures {
    public static final String NO_REPLY_PARAM_KEY = "noReply";
    public static final String BODY_PARAM_KEY = "emailBody";
    public static final String TEAM_PARAM_KEY = "teamEaton";
    public static final String START_PARAM_KEY = "emailStart";
    public static final String DEAR_PARAM_KEY = "dearCustomer";
    public static final String SUBJECT_PARAM_KEY = "emailSubject";
    public static final String BEST_PARAM_KEY = "bestRegards";
    public static final String NO_REPLY = "no reply";
    public static final String DEAR_CUSTOMER = "Dear customer";
    public static final String HERE_ARE_THE_LINKS = "Here are the links";
    public static final String BEST_REGARDS = "Best regards";
    public static final String TEAM_EATON = "Team eaton";
    public static final String EATON_DATASHEETS = "Eaton datasheets";
    public static Map<String, String> forDataSheet(String body){
        HashMap<String, String> emailServiceParameters = new HashMap<>();
        emailServiceParameters.put(NO_REPLY_PARAM_KEY, NO_REPLY);
        emailServiceParameters.put(BODY_PARAM_KEY, body);
        emailServiceParameters.put(TEAM_PARAM_KEY, TEAM_EATON);
        emailServiceParameters.put(START_PARAM_KEY, HERE_ARE_THE_LINKS);
        emailServiceParameters.put(DEAR_PARAM_KEY, DEAR_CUSTOMER);
        emailServiceParameters.put(SUBJECT_PARAM_KEY, EATON_DATASHEETS);
        emailServiceParameters.put(BEST_PARAM_KEY, BEST_REGARDS);
        return emailServiceParameters;
    }
}
