package com.eaton.platform.core.services.impl;

import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * <html> Description: This class is used to get the user session.
 * We have to create the user  name with "readService"  & "writeService"  </html> 
 * @author TCS
 * @version 1.0
 * @since 2017
 *
 */

@Component(service = AdminService.class,immediate = true,
property = {
		AEMConstants.SERVICE_VENDOR_EATON,
		AEMConstants.SERVICE_DESCRIPTION + "Admin Service",
		AEMConstants.PROCESS_LABEL + "AdminServiceImpl"
})
public class AdminServiceImpl implements AdminService {

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(AdminServiceImpl.class);

    /** The resolver factory. */
    @Reference(policy = ReferencePolicy.STATIC)
    protected ResourceResolverFactory resolverFactory;


    /**
     * Called when the Scheduler is activated/updated.
     *
     * @throws Exception Exception
     */
    @Activate
    protected final void activate() throws Exception {
    	LOG.debug("AdminServiceImpl :: activate()");
    }
    
    /**
     * Deactivate.
     */
    @Deactivate
    protected void deactivate() {
    	LOG.debug("AdminServiceImpl :: deactivate()");
    }


    /* (non-Javadoc)
     * @see com.eaton.platform.core.services.AdminService#getReadService()
     */
    @Override
	public ResourceResolver getReadService() {
    	LOG.debug("AdminServiceImpl :: getReadService() :: Start");
		Map<String, Object> readParam = new HashMap<>();
		readParam.put(ResourceResolverFactory.SUBSERVICE, CommonConstants.RESOURCE_RESOLVER_READ_SERVICE);
		ResourceResolver adminReadResourceResolver = null;
		try {
			adminReadResourceResolver = resolverFactory.getServiceResourceResolver(readParam);
		} catch (LoginException exception) {
			LOG.error("Exception occured while getting the reader service", exception.getMessage());
		}
		LOG.debug("AdminServiceImpl :: getReadService() :: Exit");
		return adminReadResourceResolver;
	}

	/* (non-Javadoc)
	 * @see com.eaton.platform.core.services.AdminService#getWriteService()
	 */
	@Override
	public ResourceResolver getWriteService() {
		LOG.debug("AdminServiceImpl :: getWriteService() :: Start");
		Map<String, Object> writeParam = new HashMap<>();
		writeParam.put(ResourceResolverFactory.SUBSERVICE, CommonConstants.RESOURCE_RESOLVER_WRITE_SERVICE);
		ResourceResolver adminWriteResourceResolver = null;
		try {
			adminWriteResourceResolver = resolverFactory.getServiceResourceResolver(writeParam);
		} catch (LoginException exception) {
			LOG.error("Exception occured while getting the write service", exception.getMessage());
		}
		LOG.debug("AdminServiceImpl :: getWriteService() :: Exit");
		return adminWriteResourceResolver;
	}

}
