package com.eaton.platform.core.services.secure;

import java.util.Map;

public interface AgentReportsMapperService {

	/**
	 * @param key - mapping code
	 * 
	 * @return Mapped division display key value pair.
	 */
	Map<String, String> getDivisionByMappingCode(String mappingCode);
}
