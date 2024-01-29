package com.eaton.platform.core.services;

import com.eaton.platform.core.bean.ConfigServiceBean;

/**
 * The Interface EatonConfigService.
 */
public interface EatonConfigService {
	
    /** The Constant LOV_ICON_LIST_PATH_ARRAY. */
    public static final String LOV_ICON_LIST_PATH_ARRAY = "lov.icon.list.path";
	
	/** The Constant DROPDOWN_OPTION_SELECT. */
    public static final String DROPDOWN_OPTION_SELECT = "linklist.dropdown.option";

    
    /** The Constant NUMBER_OF_DAYS_TO_PUBLISH. */
    public static final String NUMBER_OF_DAYS_TO_PUBLISH = "number.of.days.publish";
    
    /** The Constant MIDDLE_TAB_PATH_ARRAY. */
    public static final String MIDDLE_TAB_PATH_ARRAY = "middle.tab.config";

    /** The Constant ICONLIST_PROOFPOINT_SYMBOLS. */
    public static final String ICONLIST_PROOFPOINT_SYMBOLS = "iconlist.proofpoint.symbols";
	
	/** The Constant DYNAMIC_DROPDOWN_PATH_ARRAY. */
	public static final String DYNAMIC_DROPDOWN_PATH_ARRAY = "dynamic.dropdown.config";
	
	public static final String ICON_LIST_SYMBOLS = "icon.list.symbol";
	
	/** The Constant priceList_FOLDER_PATH. */
    public static final String PRICELIST_FOLDER_PATH = "priceList.folder.path";
    
    /** The Constant priceList_FOLDER_PATH. */
    public static final String SKU_PAGE_NAME = "skupagename";
    
    /** The Constant EXCEL_FILE_PATH */
    public static final String EXCEL_FILE_PATH = "excel.file.path";
    
    /** The Constant EXCEL_FILE_ARCHIVE_PATH. */
    public static final String EXCEL_FILE_ARCHIVE_PATH = "excel.file.archive.path";
    
    /** The Constant JSON_FILE_ARCHIVE_PATH. */
    public static final String JSON_FILE_ARCHIVE_PATH = "json.file.archive.path";
    
    /** The Constant JSON_FILE_PATH. */
    public static final String JSON_FILE_PATH = "json.file.path";
    
    /** The Constant ROOTELEMENT. */
    public static final String ROOTELEMENT = "json.root.element";
    
    /** Generated json file name in price list generator */
     
    public static final String JSONFILE_NAME = "json.file.name";


    /** Resource Listing Drawing File Extensions */

    public static final String RESOURCE_LISTING_DRAWINGS_FILE_EXTENSIONS = "resource.listing.drawings.file.extensions.list";
    
    /**
     * Gets the config service bean.
     *
     * @return the config service bean
     */
    public ConfigServiceBean getConfigServiceBean();
}
