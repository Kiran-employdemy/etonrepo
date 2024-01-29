package com.eaton.platform.core.bean;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

/**
 * The Class FacetedHeaderNavigationResultBean.
 */
public class FacetedHeaderNavigationResultBean implements Serializable {

	private static final String SERIAL_VERSION_ID = "serialVersionUID";
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1088936349819670537L;
	
	/** The total count. */
	private int totalCount;
	
	/** The product count. */
	private int productCount;
	
	/** The news count. */
	private int newsCount;
	
	/** The resource count. */
	private int resourceCount;
	
	/** The results. */
	private String results;
	
	/** The sort. */
	private String sort;
	
	/** The relevance. */
	private String relevance;
	
	/** The assending sort. */
	private String assendingSort;
	
	/** The descending sort. */
	private String descendingSort;
	
	/** The clear filters. */
	private String clearFilters;
	
	/** The narrow results. */
	private String narrowResults;
	
	/** The view more. */
	private String viewMore;

	private String viewLess;
	
	/** The new keyword. */
	private String newKeyword;
	
	/** The best seller. */
	private String bestSeller;
	
	/** The sort by url. */
	private String sortByUrl;
	
	/** The remove filters. */
	private String removeFilters;
	
	/** The apply. */
	private String apply;
	
	/** The go to. */
	private String goTo;
	
	/** The filters. */
	private String filters;
	
	/** The sort list. */
	private transient List<SortByOptionValueBean> sortList;//SonarQube private or transient issue
		
	/**
	 * Gets the sort list.
	 *
	 * @return the sort list
	 */
	public List<SortByOptionValueBean> getSortList() {
		return sortList;
	}
	
	/**
	 * Sets the sort list.
	 *
	 * @param sortList the new sort list
	 */
	public void setSortList(List<SortByOptionValueBean> sortList) {
		this.sortList = sortList;
	}
	
	/**
	 * Gets the new keyword.
	 *
	 * @return the newKeyword
	 */
	public String getNewKeyword() {
		return newKeyword;
	}
	
	/**
	 * Sets the new keyword.
	 *
	 * @param newKeyword the newKeyword to set
	 */
	public void setNewKeyword(String newKeyword) {
		this.newKeyword = newKeyword;
	}
	
	/**
	 * Gets the best seller.
	 *
	 * @return the bestSeller
	 */
	public String getBestSeller() {
		return bestSeller;
	}
	
	/**
	 * Sets the best seller.
	 *
	 * @param bestSeller the bestSeller to set
	 */
	public void setBestSeller(String bestSeller) {
		this.bestSeller = bestSeller;
	}
	
	/**
	 * Gets the total count.
	 *
	 * @return the totalCount
	 */
	public int getTotalCount() {
		return totalCount;
	}
	
	/**
	 * Sets the total count.
	 *
	 * @param totalCount the totalCount to set
	 */
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	
	/**
	 * Gets the results.
	 *
	 * @return the results
	 */
	public String getResults() {
		return results;
	}
	
	/**
	 * Sets the results.
	 *
	 * @param results the results to set
	 */
	public void setResults(String results) {
		this.results = results;
	}
	
	/**
	 * Gets the sort.
	 *
	 * @return the sort
	 */
	public String getSort() {
		return sort;
	}
	
	/**
	 * Sets the sort.
	 *
	 * @param sort the sort to set
	 */
	public void setSort(String sort) {
		this.sort = sort;
	}
	
	/**
	 * Gets the relevance.
	 *
	 * @return the relevance
	 */
	public String getRelevance() {
		return relevance;
	}
	
	/**
	 * Sets the relevance.
	 *
	 * @param relevance the relevance to set
	 */
	public void setRelevance(String relevance) {
		this.relevance = relevance;
	}
	
	/**
	 * Gets the assending sort.
	 *
	 * @return the assendingSort
	 */
	public String getAssendingSort() {
		return assendingSort;
	}
	
	/**
	 * Sets the assending sort.
	 *
	 * @param assendingSort the assendingSort to set
	 */
	public void setAssendingSort(String assendingSort) {
		this.assendingSort = assendingSort;
	}
	
	/**
	 * Gets the descending sort.
	 *
	 * @return the descendingSort
	 */
	public String getDescendingSort() {
		return descendingSort;
	}
	
	/**
	 * Sets the descending sort.
	 *
	 * @param descendingSort the descendingSort to set
	 */
	public void setDescendingSort(String descendingSort) {
		this.descendingSort = descendingSort;
	}
	
	/**
	 * Gets the clear filters.
	 *
	 * @return the clearFilters
	 */
	public String getClearFilters() {
		return clearFilters;
	}
	
	/**
	 * Sets the clear filters.
	 *
	 * @param clearFilters the clearFilters to set
	 */
	public void setClearFilters(String clearFilters) {
		this.clearFilters = clearFilters;
	}
	
	/**
	 * Gets the narrow results.
	 *
	 * @return the narrowResults
	 */
	public String getNarrowResults() {
		return narrowResults;
	}
	
	/**
	 * Sets the narrow results.
	 *
	 * @param narrowResults the narrowResults to set
	 */
	public void setNarrowResults(String narrowResults) {
		this.narrowResults = narrowResults;
	}
	
	/**
	 * Gets the view more.
	 *
	 * @return the viewMore
	 */
	public String getViewMore() {
		return viewMore;
	}
	
	/**
	 * Sets the view more.
	 *
	 * @param viewMore the viewMore to set
	 */
	public void setViewMore(String viewMore) {
		this.viewMore = viewMore;
	}
	
	/**
	 * Gets the sort by url.
	 *
	 * @return the sort by url
	 */
	public String getSortByUrl() {
		return sortByUrl;
	}
	
	/**
	 * Sets the sort by url.
	 *
	 * @param sortByUrl the new sort by url
	 */
	public void setSortByUrl(String sortByUrl) {
		this.sortByUrl = sortByUrl;
	}
	
	/**
	 * Gets the removes the filters.
	 *
	 * @return the removes the filters
	 */
	public String getRemoveFilters() {
		return removeFilters;
	}
	
	/**
	 * Sets the removes the filters.
	 *
	 * @param removeFilters the new removes the filters
	 */
	public void setRemoveFilters(String removeFilters) {
		this.removeFilters = removeFilters;
	}
	
	/**
	 * Gets the apply.
	 *
	 * @return the apply
	 */
	public String getApply() {
		return apply;
	}
	
	/**
	 * Sets the apply.
	 *
	 * @param apply the new apply
	 */
	public void setApply(String apply) {
		this.apply = apply;
	}
	
	/**
	 * Gets the product count.
	 *
	 * @return the product count
	 */
	public int getProductCount() {
		return productCount;
	}
	
	/**
	 * Sets the product count.
	 *
	 * @param productCount the new product count
	 */
	public void setProductCount(int productCount) {
		this.productCount = productCount;
	}
	
	/**
	 * Gets the news count.
	 *
	 * @return the news count
	 */
	public int getNewsCount() {
		return newsCount;
	}
	
	/**
	 * Sets the news count.
	 *
	 * @param newsCount the new news count
	 */
	public void setNewsCount(int newsCount) {
		this.newsCount = newsCount;
	}
	
	/**
	 * Gets the resource count.
	 *
	 * @return the resource count
	 */
	public int getResourceCount() {
		return resourceCount;
	}
	
	/**
	 * Sets the resource count.
	 *
	 * @param resourceCount the new resource count
	 */
	public void setResourceCount(int resourceCount) {
		this.resourceCount = resourceCount;
	}
	
	/**
	 * Gets the go to.
	 *
	 * @return the go to
	 */
	public String getGoTo() {
		return goTo;
	}
	
	/**
	 * Sets the go to.
	 *
	 * @param goTo the new go to
	 */
	public void setGoTo(String goTo) {
		this.goTo = goTo;
	}
	
	/**
	 * Gets the filters.
	 *
	 * @return the filters
	 */
	public String getFilters() {
		return filters;
	}
	
	/**
	 * Sets the filters.
	 *
	 * @param filters the new filters
	 */
	public void setFilters(String filters) {
		this.filters = filters;
	}

	public String getViewLess() {
		return viewLess;
	}

	public void setViewLess(String viewLess) {
		this.viewLess = viewLess;
	}

	/* (non-Javadoc)
 	 * @see java.lang.Object#toString()
 	 */
 	@Override 
	 public String toString() {
    	 StringBuilder result = new StringBuilder();
    	    String newLine = System.getProperty("line.separator");

    	    result.append(newLine);

    	    //determine fields declared in this class only (no fields of superclass)
    	    Field[] fields = this.getClass().getDeclaredFields();

    	    //print field names paired with their values
    	    for (Field field : fields) {
    	    	if(!field.getName().equalsIgnoreCase(SERIAL_VERSION_ID)){
    	    		 result.append("  ");
    	    	      try {
    	    	        result.append(field.getName());
    	    	        result.append(": ");
    	    	        //requires access to private field:
    	    	        result.append(field.get(this));
    	    	      }
    	    	      catch (IllegalAccessException ex) {
    	    	    	  return "Error in toString of ProductFamilyDetails"+ex.getMessage();
    	    	      }
    	    	      result.append(newLine);
    	    	    }
    	    	}

    	    return result.toString();
     }
	
}
