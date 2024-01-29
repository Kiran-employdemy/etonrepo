package com.eaton.platform.integration.auth.util;

public class MissingParameterRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public static final String MESSAGE_TEMPLATE = "Parameter [%s] is required";
	
    public MissingParameterRuntimeException(String parameterName) {
    	super(String.format(MESSAGE_TEMPLATE, parameterName));
    }
    
    public MissingParameterRuntimeException(String parameterName, Throwable cause) {
        super(String.format(MESSAGE_TEMPLATE, parameterName), cause);
    }
    
    public MissingParameterRuntimeException(Throwable cause) {
        super(cause);
    }
}