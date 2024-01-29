package com.eaton.platform.core.models;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

/**
 * <html> Description: This bean class used in TableHelper class to store content </html> .
 *
 * @author EATON
 * @version 1.0
 * @since 2019
 */

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TableModel {
	
	/** The tableData. **/
	@Inject
	private String tableData;
	
	/** The Title. **/
	@Inject
	private String title;
	
	/** The toggle inner grid. */
	@Inject @Default(values = "true")
	private String toggleInnerGrid;
	
	/** Checkbox for adding border to class**/
	@Inject @Default(values = "false")
	private String tableVerticalBorder;
	
	/** Table div class name **/
	@Inject @Default(values = "eaton-custom-table")
	private String tableDivClass;
	
	private static final String TRUE="true";
	private static final String divTableName="eaton-custom-table table-border";
	
	/**
	 * Gets the tableData.
	 *
	 * @return the title
	 */

	public String getTableData() {
		return tableData;
	}
	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	
	public String getTitle() {
		return title;
	}
	/**
	 * Gets the toggle inner grid.
	 *
	 * @return the toggle inner grid
	 */
	
	public String getToggleInnerGrid() {
		return toggleInnerGrid;
	}
	
	public String getTableVerticalBorder() {
		return tableVerticalBorder;
	}
	public void setTableVerticalBorder(String tableVerticalBorder) {
		this.tableVerticalBorder = tableVerticalBorder;
	}
	
	public String getTableDivClass() {
		if(StringUtils.equals(TRUE, tableVerticalBorder)){
			tableDivClass = divTableName;
		}
		return tableDivClass;
	}
	public void setTableDivClass(String tableDivClass) {
		this.tableDivClass = tableDivClass;
	}
		
}
