package com.eaton.platform.core.services.impl;

import com.eaton.platform.core.bean.ConfigServiceBean;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.services.EatonConfigService;
import com.eaton.platform.core.services.config.EatonServiceConfig;
import com.eaton.platform.core.util.CommonUtil;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Class ConfigServiceImpl.
 */
@Designate(ocd = EatonServiceConfig.class)
@Component(service = EatonConfigService.class ,immediate = true,
        property = {
                AEMConstants.SERVICE_VENDOR_EATON,
                AEMConstants.SERVICE_DESCRIPTION + "EatonConfigServiceImpl",
                AEMConstants.PROCESS_LABEL + "EatonConfigServiceImpl"
        })
public class EatonConfigServiceImpl implements EatonConfigService {
	
	/** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EatonConfigServiceImpl.class);

	/** The service reference. */
    ConfigServiceBean configServiceBean;

    /**
     * Activate.
     *
     * @param props the props
     * @throws Exception the exception
     */
    @Activate
    @Modified
    protected final void activate(final EatonServiceConfig props) throws Exception {
        this.configServiceBean = new ConfigServiceBean();
        initializeConfigurations(props);
    }

	/**
     * Deactivate.
     */
    @Deactivate
    protected void deactivate() {
        this.configServiceBean = null;
    }

    /**
     * Initialize configurations.
     *
     * @param properties the properties
     */
    private void initializeConfigurations(final EatonServiceConfig properties) {
    	LOGGER.debug("EatonConfigServiceImpl :: initializeConfigurations() :: Start");
        //this.configServiceBean.setCountryselectorFolderPath(properties.countryselector_folder_path());
        if (null != properties.lov_icon_list_path()) {
            List<String> lovIconListPath = CommonUtil.getListFromStringArray(properties.lov_icon_list_path());
            this.configServiceBean.setLovIconListPagePathList(lovIconListPath);
        }

        if (null != properties.linklist_dropdown_option()) {
            String[] dropDownNameArray = properties.linklist_dropdown_option();
            List<String> dropDownName = CommonUtil.getListFromStringArray(dropDownNameArray);
            this.configServiceBean.setLinkListViews(dropDownName);
        }
		if(null != properties.dynamic_dropdown_config()){
        	Map<String,String> dropdownMap = new HashMap<String,String>();
        	String[] dropdownListPathArray = properties.dynamic_dropdown_config();//
        	for (String listElement: dropdownListPathArray) {
        		String temp = listElement;
        		String[] parts = temp.split("#");
        		if (null != parts && parts.length == 1){
                    String part1 = parts[0];
                    String part2 = parts[1];
                    dropdownMap.put(part1, part2);
                }
        	}
        	this.configServiceBean.setDynamicDropdownPathList(dropdownMap);
        	
        }
        if (null != properties.iconlist_proofpoint_symbols()) {
            String[] proofPointSymbolsArray = properties.iconlist_proofpoint_symbols();
            List<String> proofPointSymbols = CommonUtil.getListFromStringArray(proofPointSymbolsArray);
            this.configServiceBean.setProofPointSymbols(proofPointSymbols);;
        }
        this.configServiceBean.setNumofDaysToPublish(properties.number_of_days_publish());
        if(null != properties.middle_tab_config()){
        	String[] middleTabListPathArray =  properties.middle_tab_config();
        	 List<String> middleTabList = CommonUtil.getListFromStringArray(middleTabListPathArray);
        	this.configServiceBean.setMiddleTabListPagePathList(middleTabList);        	
        }
        if(null != properties.icon_list_symbol()){
   		 
			 String[] iconListSymbols =  properties.icon_list_symbol();
			 List<String> iconList = CommonUtil.getListFromStringArray(iconListSymbols);
			 this.configServiceBean.setIconListSymbols(iconList);
		 
	    }
        this.configServiceBean.setPriceListFolderPath(properties.priceList_folder_path());
        this.configServiceBean.setSkupagename(properties.skupagename());
        this.configServiceBean.setDamExcelUploadPath(properties.excel_file_path());
        this.configServiceBean.setDamExcelArchivePath(properties.excel_file_archive_path());;
        this.configServiceBean.setDamJsonArchivePath(properties.json_file_archive_path());
        this.configServiceBean.setDamJsonUploadPath(properties.json_file_path());
        this.configServiceBean.setJsonRootElementName(properties.json_root_element());
        this.configServiceBean.setJsonFileName(properties.json_file_name());
        this.configServiceBean.setResourceListingDrawingsFileExtensionList(Arrays.asList(properties.resource_listing_drawings_file_extensions_list().split(",")));

        LOGGER.debug("EatonConfigServiceImpl :: initializeConfigurations() :: Exit");
	}

	/* (non-Javadoc)
	 * @see com.eaton.platform.core.services.EatonConfigService#getConfigServiceBean()
	 */
	public ConfigServiceBean getConfigServiceBean() {
		return configServiceBean;
	}
    
}
