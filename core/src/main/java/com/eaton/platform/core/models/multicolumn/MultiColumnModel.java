package com.eaton.platform.core.models.multicolumn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaton.platform.core.bean.MultiColumnAttributeBean;

/**
 * <html> Description: In post processing of this sling model,
 *  it will load the respective model inputs passing resource object</html> .
 * 
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)

public class MultiColumnModel {

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(MultiColumnModel.class);

	/** The section Title **/
	@Inject
	private String sectionTitle;

	@Inject
	private Resource tableValues;

	/** The AttributeList. */
	private List<MultiColumnAttributeBean> tableValuesList;
	
	/** The columnHeaderList. */
	private List<String>  columnHeaderList;

	/**
	 * Get the sectionTitle
	 * @return the sectionTitle
	 */
	public String getSectionTitle() {
		return sectionTitle;
	}

	/**
	 * Inits the.
	 */
	@PostConstruct
	protected void init() {
		LOG.debug("MultiColumnModel :: init() :: Started");
		if(tableValues  != null){
			List<MultiColumnListModel> multicolumnTableList = new ArrayList<MultiColumnListModel>();
			populateMultiColumnTableModel(tableValues, multicolumnTableList);
		}
		LOG.debug("MultiColumnModel :: init() :: Exit");
	}

	/**
	 * Populate MultiColumnTableModel.
	 *
	 * @param resource the resource
	 * @param multicolumnTableList 
	 * @return List
	 */
	private List<?> populateMultiColumnTableModel(Resource resource,
			List<MultiColumnListModel> multicolumnTableList) {
		LOG.info("populateMultiColumnTableModel() Started");
		if (resource != null) {
			Iterator<Resource> linkResources = resource.listChildren();  
			List<String> itemNameList = new ArrayList<String>();
			List<String> attrNameList = new ArrayList<String>();

			while (linkResources.hasNext()) {
				MultiColumnListModel multicolumnModel = linkResources.next().adaptTo(MultiColumnListModel.class);
				if (multicolumnModel != null) {
					itemNameList.add(multicolumnModel.getItemTitle().toUpperCase());	
					attrNameList.add(multicolumnModel.getAttrTitle().toUpperCase());
					multicolumnTableList.add(multicolumnModel);
				}
			}
			this.columnHeaderList = fetchUniqueList(itemNameList);			
			List<MultiColumnAttributeBean> attributeNameValueList = setAttributeValueList(multicolumnTableList,this.columnHeaderList,fetchUniqueList(attrNameList));

			this.tableValuesList = attributeNameValueList;
		}

		return this.tableValuesList;	
	}

	/**
	 * SetAttributeList
	 * @param multicolumnTableList
	 * @param columnHeaderList
	 * @param attrNameList
	 * @return
	 */
	private static List<MultiColumnAttributeBean> setAttributeValueList(List<MultiColumnListModel> multicolumnTableList,List<String> columnHeaderList, List<String> attrNameList) {

		List<MultiColumnAttributeBean> attributeNameValueList = new ArrayList<MultiColumnAttributeBean>();	

		for(int i=0;i<attrNameList.size();i++){

			MultiColumnAttributeBean multicolumnBean = new MultiColumnAttributeBean();
			String attributeName =attrNameList.get(i);
			List<String> attributeValueList = Arrays.asList(new String[columnHeaderList.size()]);
			
			for (int j = 0;j < multicolumnTableList.size(); j++){

				if (attributeName.equalsIgnoreCase(multicolumnTableList.get(j).getAttrTitle())) {
					
					String itemName = multicolumnTableList.get(j).getItemTitle();
					String attributeValue = multicolumnTableList.get(j).getAttrValue();
					int index = checkIndexAttributeName(itemName,columnHeaderList);
					if(index != -1) {   			           
                        attributeValueList.set(index, attributeValue);
					}
				}
			} 
           
			//Set MultiColumnAttributeBean
			multicolumnBean.setAttributeName(attributeName);
			multicolumnBean.setAttributeValueList(attributeValueList);

			attributeNameValueList.add(multicolumnBean);
		}

		return attributeNameValueList;
	}

	/**
	 * This method is to return index of ItemName in HeaderList
	 * @param itemName
	 * @param headerList
	 * @return
	 */
	private static int checkIndexAttributeName(String itemName,List<String> headerList) {		
		int pos = -1;

		for (int i = 0; i < headerList.size(); i++){

			if (itemName.equalsIgnoreCase(headerList.get(i))) {
				pos = i;
			}
		}
		return pos; 
	}

	/**
	 * This method is to return List of unique elements
	 * @param itemNameList
	 * @return List<String> itemNameList
	 */
	private static List<String> fetchUniqueList(List<String> itemNameList) {
		LinkedHashSet<String> set = new LinkedHashSet<String>(itemNameList);
		return new ArrayList<String>(set);
	}

	/**
	 * Get TableValuesList
	 * @return the tableValuesList
	 */
	public List<MultiColumnAttributeBean> getTableValuesList() {
		return tableValuesList;
	}

	/**
	 * Get columnHeaderList
	 * @return the columnHeaderList
	 */
	public List<String> getColumnHeaderList() {
		return columnHeaderList;
	}
}
