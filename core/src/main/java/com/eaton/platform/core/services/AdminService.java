package com.eaton.platform.core.services;

import org.apache.sling.api.resource.ResourceResolver;

/**
 * The Interface AdminService.
 */
public interface AdminService {
	
    /**
     * Gets the read service.
     *
     * @return the read service
     */
    public ResourceResolver getReadService();

    /**
     * Gets the write service.
     *
     * @return the write service
     */
    public ResourceResolver getWriteService();

}
