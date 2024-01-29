package com.eaton.platform.core.exception;

/**
 * The Class EatonApplicationException.
 * 
 * @author sradhanand.achugatla
 */
public class EatonApplicationException extends Exception {
	
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1285333466482054738L;
    
    /**
     * Instantiates a new eaton application exception.
     *
     * @param theMessage the the message
     */
    public EatonApplicationException(final String theMessage) {
        super(theMessage);
    }
    
    /**
     * Instantiates a new eaton application exception.
     *
     * @param anErrorCode the an error code
     * @param theMessage the the message
     */
    public EatonApplicationException(final String anErrorCode, final String theMessage) {
        super("Error Code - ".concat(anErrorCode).concat("\n").concat(theMessage));
    }
    
    /**
     * Instantiates a new eaton application exception.
     *
     * @param theMessage the the message
     * @param anException the an exception
     */
    public EatonApplicationException(final String theMessage, final Throwable anException) {
        super("Error Message - ".concat(theMessage), anException);
    }
    
    /**
     * Instantiates a new eaton application exception.
     *
     * @param anErrorCode the an error code
     * @param theMessage the the message
     * @param anException the an exception
     */
    public EatonApplicationException(final String anErrorCode, final String theMessage, final Throwable anException) {
    	super("Error Code - ".concat(anErrorCode).concat("\n").concat(theMessage), anException);
    }
}
