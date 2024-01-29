package com.eaton.platform.core.services.packaging;

import com.eaton.platform.core.bean.packaging.PackageBean;
import com.eaton.platform.core.bean.packaging.PackageRequestBean;

/**
 * The Interface EatonPackagingService.
 *
 * Service for creation of JCR packages.
 *
 * @author Jaroslav Rassadin
 */
public interface EatonPackagingService {

	/**
	 * Creates package.
	 *
	 * @param packageRequest
	 *            the package request
	 * @return the package bean
	 * @throws PackageCreationException
	 *             the package creation exception
	 */
	PackageBean create(PackageRequestBean packageRequest) throws PackageCreationException;
}
