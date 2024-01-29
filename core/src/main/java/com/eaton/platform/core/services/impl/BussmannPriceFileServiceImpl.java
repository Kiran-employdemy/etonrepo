package com.eaton.platform.core.services.impl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;

import com.eaton.platform.core.services.BussmannPriceFileService;
import com.eaton.platform.core.services.config.BussmannPriceFileServiceConfig;

@Component(service = BussmannPriceFileService.class,immediate = true)
@Designate(ocd=BussmannPriceFileServiceConfig.class)
public class BussmannPriceFileServiceImpl implements BussmannPriceFileService {

	private String bussmannUSFile;
	private String bussmannEDIUSFile;
	private String bussmannCANFile;
	private String bussmannEDICANFile;


	@Activate
	@Modified
	protected final void activate(final BussmannPriceFileServiceConfig config) {
		if (config != null) {
			bussmannUSFile = config.bussmannUSfileURL();
			bussmannEDIUSFile = config.bussmannEDIUSfileURL();
			bussmannCANFile = config.bussmannCANfileURL();
			bussmannEDICANFile = config.bussmannEDICANfileURL();
			
		}
	}

	@Override
	public String getBussmannUSfileURL() {
		return bussmannUSFile;
	}

	@Override
	public String getBussmannEDIUSfileURL() {
		return bussmannEDIUSFile;
	}

	@Override
	public String getBussmannCANfileURL() {
		return bussmannCANFile;
	}

	@Override
	public String getBussmannEDICANfileURL() {
		return bussmannEDICANFile;
	}



}
