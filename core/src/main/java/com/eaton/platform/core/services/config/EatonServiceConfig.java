package com.eaton.platform.core.services.config;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "EatonConfigServiceImpl")
public @interface EatonServiceConfig {

    @AttributeDefinition( name = "Country Selector Folder Path" )
    String countryselector_folder_path() default StringUtils.EMPTY;

    @AttributeDefinition(name = "PriceList Folder Path")
    String priceList_folder_path() default StringUtils.EMPTY;

    @AttributeDefinition( name = "Excel file Path" )
    String excel_file_path() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Excel archieve file Path")
    String excel_file_archive_path() default StringUtils.EMPTY;

    @AttributeDefinition( name = "JSON archieve file path" )
    String json_file_archive_path() default StringUtils.EMPTY;

    @AttributeDefinition(name = "JSON file Path")
    String json_file_path() default StringUtils.EMPTY;

    @AttributeDefinition( name = "root Element" )
    String json_root_element() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Json File Name")
    String json_file_name() default "skupricelist";

    @AttributeDefinition( name = "sku page Path" )
    String skupagename() default StringUtils.EMPTY;

    @AttributeDefinition(name = "LOV Icon List path",description = "Icon LOVs List Path")
    String [] lov_icon_list_path() default {};

    @AttributeDefinition( name = "Link List View Option", description="This defines text and value of view as dropdown option" )
    String [] linklist_dropdown_option() default {};

    @AttributeDefinition(name = "Dynamic Dropdown list path",description = "Dymanic Dropdown List Path")
    String [] dynamic_dropdown_config() default {};

    @AttributeDefinition(name = "IconList Symbols",description = "IconList Symbols")
    String [] icon_list_symbol() default {};

    @AttributeDefinition(name = "Number Of Days To Publish",description = "Number Of Days To Publish")
    String number_of_days_publish() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Middle Tab list path",description = "Middle Tab list path")
    String [] middle_tab_config() default {};

    @AttributeDefinition(name = "iconlist.proofpoint.symbols")
    String [] iconlist_proofpoint_symbols() default {};

    @AttributeDefinition(name = "resource.listing.drawings.file.extensions.list", description = "Enter the list separated by comma(,)")
    String resource_listing_drawings_file_extensions_list() default StringUtils.EMPTY;
}