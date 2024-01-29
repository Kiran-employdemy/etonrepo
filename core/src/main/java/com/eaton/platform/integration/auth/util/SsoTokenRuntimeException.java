package com.eaton.platform.integration.auth.util;

public class SsoTokenRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public static final String MESSAGE_TEMPLATE = "%s. Token is invalid [%s]";
    public static final String MESSAGE_TEMPLATE_ONLY_TOKEN = "Token is invalid [%s]";

	
    public SsoTokenRuntimeException(String tokenText) {
    	super(String.format(MESSAGE_TEMPLATE_ONLY_TOKEN, tokenText));
    }

    public SsoTokenRuntimeException(String message, String tokenText) {
    	super(String.format(MESSAGE_TEMPLATE, message, tokenText));
    }
    
    public SsoTokenRuntimeException(String message, String tokenText, Throwable cause) {
        super(String.format(MESSAGE_TEMPLATE, message, tokenText), cause);
    }
    
    public SsoTokenRuntimeException(String tokenText, Throwable cause) {
        super(String.format(MESSAGE_TEMPLATE_ONLY_TOKEN, tokenText), cause);
    }
    
    public SsoTokenRuntimeException(Throwable cause) {
        super(cause);
    }
}