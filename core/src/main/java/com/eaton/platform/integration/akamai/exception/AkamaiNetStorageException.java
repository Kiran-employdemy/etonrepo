package com.eaton.platform.integration.akamai.exception;

public class AkamaiNetStorageException extends Exception{
    public AkamaiNetStorageException(String msg){
        super(msg);
    }
    public AkamaiNetStorageException(Throwable throwable){
        super(throwable);
    }
}
