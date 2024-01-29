package com.eaton.platform.integration.akamai.enums;

public enum NetStorageAction {
    UPLOAD("upload"),
    DELETE("delete"),
    DOWNLOAD("download"),
    UNKNOWN("");
    private final String action;
    NetStorageAction(String action){
        this.action = action;
    }

    public static NetStorageAction getAction(String act){
        for(NetStorageAction curAction : NetStorageAction.values()){
            if(curAction.action.equals(act)){
                return curAction;
            }
        }
        return UNKNOWN;
    }

    public String getAction() {
        return action;
    }

    public String getActionValue(String version){
        return String.format("version=%s&action=%s",version,action);
    }
}
