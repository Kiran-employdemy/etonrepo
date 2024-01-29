package com.eaton.platform.core.services.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/** Configuration for AgentReports services */

@ObjectClassDefinition(name = "BussmannPriceFileService")
public @interface BussmannPriceFileServiceConfig {


	/**
	 * BussmannUSfileURL Config
	 * 
	 * @return BussmannUSfileURL
	 */
	@AttributeDefinition(name = "Bussmann US file URL", description = "Service URL part for Bussmann Price files")
	String bussmannUSfileURL() default "https://my.eaton.com/extranet/faces/oracle/webcenter/portalapp/pages/getFile.jspx?content_id=PCT_3633526";

	/**
	 * BussmannEDI_USfileURL Config
	 * 
	 * @return BussmannEDI_USfileURL
	 */
	@AttributeDefinition(name = "Bussmann EDI_US file URL", description = "Service URL part for Bussmann Price files")
	String bussmannEDIUSfileURL() default "https://my.eaton.com/extranet/faces/oracle/webcenter/portalapp/pages/getFile.jspx?content_id=PCT_3633529";

	/**
	 * BussmannCANfileURL Config
	 * 
	 * @return BussmannCANfileURL
	 */
	@AttributeDefinition(name = "Bussmann CAN file URL", description = "Service URL part for Bussmann Price files")
	String bussmannCANfileURL() default "https://my.eaton.com/extranet/faces/oracle/webcenter/portalapp/pages/getFile.jspx?content_id=PCT_3633527";
	
	
	/**
	 * BussmannEDI_CANfileURL Config
	 * 
	 * @return BussmannEDI_CANfileURL
	 */
	@AttributeDefinition(name = "Bussmann EDI_CAN file URL", description = "Service URL part for Bussmann Price files")
	String bussmannEDICANfileURL() default "https://my.eaton.com/extranet/faces/oracle/webcenter/portalapp/pages/getFile.jspx?content_id=PCT_3633524";
	
}
