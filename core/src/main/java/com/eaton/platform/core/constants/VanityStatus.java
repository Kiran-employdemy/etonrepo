package com.eaton.platform.core.constants;

/**    
 * Vanity URL Status    
 **/
public enum VanityStatus {
    NOT_ACTIVE_STATUS("Not Active"),
    ACTIVE("Active"),
    DISABLED_STATUS("Disabled"),
    DISABLED_UNPUBLISHED_STATUS("Disabled but not published"),
    ACTIVE_UNPUBLISHED_STATUS("Active but updates not published");


    private String vanityStatus;

    VanityStatus(final String vanityStatus)	{
        this.vanityStatus = vanityStatus;
    }

    public String getVanityStatus() {
        return vanityStatus;
    }


}
