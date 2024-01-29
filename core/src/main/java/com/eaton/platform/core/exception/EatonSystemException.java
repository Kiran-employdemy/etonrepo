package com.eaton.platform.core.exception;

/**
 * The Class EatonSystemException.
 */
public class EatonSystemException extends RuntimeException {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2424688552118995085L;
   
    /**
     * Instantiates a new eaton system exception.
     *
     * @param anException the an exception
     */
    public EatonSystemException(final Throwable anException) {
        super(anException);
    }
    
    /**
     * Instantiates a new eaton system exception.
     *
     * @param anErrorCode the an error code
     * @param theMessage the the message
     */
    public EatonSystemException(final String anErrorCode, final String theMessage) {
    	super("Error Code - ".concat(anErrorCode).concat("\n").concat(theMessage));
    }
    
    /**
     * Instantiates a new eaton system exception.
     *
     * @param theMessage the the message
     * @param anException the an exception
     */
    public EatonSystemException(final String theMessage, final Throwable anException) {
        super("Error Message - ".concat(theMessage), anException);
    }
    
    /**
     * Instantiates a new eaton system exception.
     *
     * @param anErrorCode the an error code
     * @param theMessage the the message
     * @param anException the an exception
     */
    public EatonSystemException(final String anErrorCode, final String theMessage, final Throwable anException) {
    	super("Error Code - ".concat(anErrorCode).concat("\n").concat(theMessage), anException);
    }
}
