package com.eaton.platform.integration.sendgrid.exception;

public class FailureSendGridException extends RuntimeException{

    public FailureSendGridException(String message) {
        super(message);
    }

    public FailureSendGridException(String message, Throwable cause) {
        super(message, cause);
    }

    public FailureSendGridException(Throwable cause) {
        super(cause);
    }
}
