<%--

  ADOBE CONFIDENTIAL
  __________________

   Copyright 2015 Adobe Systems Incorporated
   All Rights Reserved.

  NOTICE:  All information contained herein is, and remains
  the property of Adobe Systems Incorporated and its suppliers,
  if any.  The intellectual and technical concepts contained
  herein are proprietary to Adobe Systems Incorporated and its
  suppliers and are protected by trade secret or copyright law.
  Dissemination of this information or reproduction of this material
  is strictly forbidden unless prior written permission is obtained
  from Adobe Systems Incorporated.

--%><%
%><%@include file="/libs/granite/ui/global.jsp" %><%
%><%@ page session="false" contentType="text/html" pageEncoding="utf-8"
           import="org.apache.sling.api.resource.Resource,
                 org.apache.commons.lang.StringUtils,
                 org.apache.commons.lang.BooleanUtils,
                 org.apache.sling.api.resource.ResourceResolver,
                 org.apache.sling.commons.json.JSONArray,
                 org.apache.sling.commons.json.JSONObject,
                 org.apache.commons.io.IOUtils,
                 com.adobe.granite.ui.components.Config,
                 java.util.ArrayList,
                 java.util.List,
                 java.util.Arrays,
                 java.io.InputStream,
                 java.util.HashMap,
                 java.util.Map,
                 java.util.Iterator" %>
<%@ page import="com.sun.org.apache.xpath.internal.SourceTree" %>
<%
%><%
    Config cfg = new Config(resource);
    String title = cfg.get("text", String.class);
    String basePredicateName = cfg.get("predicateName", "property");
    String[] options = cfg.get("optionPaths", String[].class);
    String jsonpath = cfg.get("jsonpath", String.class);
    Boolean singleSelect = cfg.get("singleSelect", false);
    String metaPropName = cfg.get("name", "");
    long predicateIndex = cfg.get("listOrder", 5000L);
    int propertyDepth = cfg.get("propertyDepth", 0);
    String operation = cfg.get("operation", String.class);
    String indexGroup = predicateIndex + "_group";
    String predicateName = indexGroup + "." + basePredicateName;
	String predicateNameTwo = indexGroup + ".2_" + basePredicateName;
    String orName =  indexGroup + ".p.or";
    Boolean selectAllMode = cfg.get("selectAllMode", false);
    String selectAllOptionsClass = selectAllMode ? "selectall-options" : "";
    boolean foldableOpen = cfg.get("open", true);
    String selected = foldableOpen?"selected":"";

    String breadcrumbs = i18n.get(title);

%><ui:includeClientLib categories="dam.admin.searchpanel.checkedoptionspredicate" />
<coral-accordion variant="large">
    <coral-accordion-item "<%=selected%>"
    data-metaType="checkboxgroup"
    data-type="options"><%
    if (selectAllMode) {
%><coral-accordion-item-label>
    <coral-checkbox class="selectall-checkbox" coral-interactive></coral-checkbox>
    <span><%= xssAPI.encodeForHTML(i18n.getVar(title)) %></span>
</coral-accordion-item-label><%
} else {
%><coral-accordion-item-label><%= xssAPI.encodeForHTML(i18n.getVar(title)) %></coral-accordion-item-label><%
    }
%><coral-accordion-item-content class="checkedoptions-predicate <%=selectAllOptionsClass%> coral-Form coral-Form--vertical" id="<%= xssAPI.encodeForHTMLAttr(resource.getPath()) %>" data-singleSelect = "<%= singleSelect %>">
    <input type="hidden" id="predicateName" name="<%= xssAPI.encodeForHTMLAttr(predicateName) %>" value="<%= xssAPI.encodeForHTMLAttr(metaPropName) %>">
    <input type="hidden" name="<%=orName%>" value="true"><%
    Resource jsonResource = resourceResolver.getResource(jsonpath);
    if (jsonResource != null) {
        InputStream is = jsonResource.adaptTo(InputStream.class);
        String jsonText = IOUtils.toString(is, "UTF-8");
        JSONObject obj = new JSONObject(jsonText);
        JSONArray opt = obj.getJSONArray("options");
        int predicateValueCount = 1;
        for (int i = 0 ; i < opt.length(); i++) {
            obj = opt.getJSONObject(i);
            String childTitle = obj.getString("text");
            String optionValuesString = obj.getString("value");
            if (optionValuesString == null) {
                continue;
            }
            List<String> optionValues = Arrays.asList(optionValuesString.split(","));

            String childName = predicateName + "."  + predicateValueCount + "_value_visible";


            if (!"".equals(childTitle)) {
%><div class="coral-Form-fieldwrapper coral-Form-fieldwrapper--singleline">
    <coral-checkbox
            class="search-predicate-checkedoptionstype-option-visible"
            name="<%= xssAPI.encodeForHTMLAttr(childName) %>"
            data-json-path="<%= xssAPI.encodeForHTMLAttr(jsonpath) %>"
    ><%= xssAPI.encodeForHTML(i18n.getVar(childTitle)) %>
    </coral-checkbox><%
    for (String mimeType : optionValues) {
        String propName = predicateName + "."  + predicateValueCount + "_value";
        if (singleSelect) {
            propName = predicateName + ".1_value";
        }
        predicateValueCount++;%>
    <input type="hidden" name="<%=xssAPI.encodeForHTMLAttr(propName)%>" value="<%=xssAPI.encodeForHTMLAttr(mimeType)%>" class="search-predicate-checkedoptionstype-option" disabled><%
        }
    }
%></div><%

    }

} else if (options != null || resource.getChild("items")!=null) {
    int predicateValueCount = 1;
    int optionValueCount = 1;
    if (options==null || options.length==0){
        //read options from items subtree if options path array is null or empty
        options = new String []{resource.getPath()+"/items"};
    }
    for (String option : options) {
        if(option.equals(".")) {
            continue;
        }
		ArrayList<String> allOptions = new ArrayList<String>();
		Boolean nullValuePresent = false;
        Resource optionRes = resourceResolver.getResource(option);
        if (optionRes != null) {
            allOptions = getChildrenValues(optionRes);
            nullValuePresent = checkForNulls(allOptions);
            Iterator<Resource> children = optionRes.listChildren();
            while(children.hasNext()) {
                Resource childRes = children.next();
                ArrayList<String> mList = getMimeTypes(childRes);
                Config childCfg = new Config(childRes);
                String childTitle = childCfg.get("jcr:title",  childCfg.get("text",String.class));
                if (childTitle == null) {
                    continue;
                }

                String childName = predicateName + "."  + predicateValueCount + "_value_visible";

                // Custom code added/updated by Freedom Marketing ------------------------------------------
                // Derived from libs/dam/gui/coral/components/admin/customsearch/searchpredicates/optionspredicate/
                // Allows checkboxes to be checked on default.
                // Import added: org.apache.commons.lang.BooleanUtils
                // Coral-checkbox html updated with 'checked=""' attribute if option is checked on default
                // Hidden input html updated with 'disabled' attribute removed if option is checked on default.
                Boolean isChecked = childCfg.get("checked",  childCfg.get("text",Boolean.class));
                String checked = "";
                String mimeTypeValue = "";
                if (BooleanUtils.isTrue(isChecked)) {
                    checked = "checked=\"\"";
                    mimeTypeValue = childCfg.get("value",  childCfg.get("text",String.class));
                }

                if (!"".equals(childTitle)) {
%><div class="coral-Form-fieldwrapper coral-Form-fieldwrapper--singleline">
    <coral-checkbox
            <%=xssAPI.encodeForHTMLAttr(checked)%>
            class="search-predicate-checkedoptionstype-option-visible"
            name="<%= xssAPI.encodeForHTMLAttr(childName) %>"
            data-option-path="<%= xssAPI.encodeForHTMLAttr(childRes.getPath()) %>"
    ><%= xssAPI.encodeForHTML(i18n.getVar(childTitle)) %>
    </coral-checkbox><%
    for (String mimeType : mList) {
          //Option can have multiple values
          //Example: jcr:title="No" value="null,no,Deactivate,Delete,Internal Poll,Reverse,Test"
        if (!mimeType.contains(",")) {
            String disabled = "disabled";
            if(mimeType.equals(mimeTypeValue)) {
                disabled = "";
            }
            String propName = predicateName + "."  + optionValueCount +"_value";
%><input type="hidden" name="<%=xssAPI.encodeForHTMLAttr(propName)%>" value="<%=xssAPI.encodeForHTMLAttr(mimeType)%>" class="search-predicate-checkedoptionstype-option" <%=xssAPI.encodeForHTMLAttr(disabled)%>><%
optionValueCount++;
} 

         else {
            /*If null is included in search, create an unequals operation for that input field and a not operation to check for if it doesn't exist
            Example query:
            15_group.property: jcr:content/dam:portalReplicationAction
            15_group.p.or: true
            15_group.property.1_value_visible: on
            15_group.property.operation: unequals
            15_group.property.1_value: Activate
            15_group.property.2_value:  Yes
            15_group.property.3_value: Maybe
            15_group.property.and: true */
        if (nullValuePresent && mimeType.contains("null")) {
            String disabled = "disabled";
			ArrayList<String> nonNullOptions = new ArrayList<String>();

            for(String optionValue : allOptions) {
                //operation: unequals entry
                if (optionValue.contains("null")) {
                    %><input type="hidden" name="<%=xssAPI.encodeForHTMLAttr(predicateName + ".operation")%>" value="<%=xssAPI.encodeForHTMLAttr("unequals")%>" class="search-predicate-checkedoptionstype-option" <%=xssAPI.encodeForHTMLAttr(disabled)%>><%
                } 
                //all other option will be iteratred over to add to the unequals query
                else {
                    //non-null option could still have multiple values
                    if(optionValue.contains(",")){
                        String[] separateValues = optionValue.split(",");
                        List<String> separateValuesList = Arrays.asList(separateValues);
						nonNullOptions.addAll(separateValuesList);
                    } else {
						  nonNullOptions.add(optionValue);
                    }
                }
            }
            //add all unequals values
            for(String nonNullOption : nonNullOptions) {
                String propName = predicateName + "."  + optionValueCount + "_value";
                %><input type="hidden" name="<%=xssAPI.encodeForHTMLAttr(propName)%>" value="<%=xssAPI.encodeForHTMLAttr(nonNullOption)%>" class="search-predicate-checkedoptionstype-option" <%=xssAPI.encodeForHTMLAttr(disabled)%>><%
                    optionValueCount++;
            }
            //And operator needed to "and" all non null options
            %><input type="hidden" name="<%=xssAPI.encodeForHTMLAttr(predicateName + ".and")%>" value="<%=xssAPI.encodeForHTMLAttr("true")%>" class="search-predicate-checkedoptionstype-option" <%=xssAPI.encodeForHTMLAttr(disabled)%>><%

            //Include assets that do not have the property
            %><input type="hidden" name="<%=xssAPI.encodeForHTMLAttr(predicateNameTwo)%>" value="<%=xssAPI.encodeForHTMLAttr(metaPropName)%>" class="search-predicate-checkedoptionstype-option" <%=xssAPI.encodeForHTMLAttr(disabled)%>>
        		<input type="hidden" name="<%=xssAPI.encodeForHTMLAttr(predicateNameTwo + ".operation")%>" value="<%=xssAPI.encodeForHTMLAttr("not")%>" class="search-predicate-checkedoptionstype-option" <%=xssAPI.encodeForHTMLAttr(disabled)%>><%
        } else {
   		 	String[] mimeTypeValues = mimeType.split(",");
            for (String mtValue : mimeTypeValues) {
                String disabled = "disabled";
                String propName = "";
                if(mtValue.equals(mimeTypeValue)) {
                    disabled = "";
                }
                else {
                    propName = predicateName + "."  + optionValueCount + "_value";
                    %><input type="hidden" name="<%=xssAPI.encodeForHTMLAttr(propName)%>" value="<%=xssAPI.encodeForHTMLAttr(mtValue)%>" class="search-predicate-checkedoptionstype-option" <%=xssAPI.encodeForHTMLAttr(disabled)%>><%
					optionValueCount++;
                }
            }
       	 }
       }
        predicateValueCount++;
    } 
%></div><%
                    }
                }
            }
        }
    }
    else {
        breadcrumbs = "";
    }
%><input type="hidden" name="<%= xssAPI.encodeForHTMLAttr(predicateName) %>.breadcrumbs"
         value="<%= xssAPI.encodeForHTMLAttr(breadcrumbs) %>">
</coral-accordion-item-content>
    </coral-accordion-item>
</coral-accordion>
<%!
    public ArrayList<String> getMimeTypes(Resource res) {
        ArrayList<String> mlist = new ArrayList<String>();
        ResourceResolver resolver = res.getResourceResolver();

        Config resCfg = new Config(res);
        String mimeType = resCfg.get("value", "");
        if (!"".equals(mimeType)) {
            mlist.add(mimeType);
        }

        Iterator<Resource> childRes = resolver.listChildren(res);
        while (childRes.hasNext()) {
            Resource child = childRes.next();
            mlist.addAll(getMimeTypes(child));

        }
        return mlist;
    }
%>
<%!
    public ArrayList<String> getChildrenValues(Resource optionRes) {
        Iterator<Resource> children = optionRes.listChildren();
        ArrayList<String> childrenValues = new ArrayList<String>();
		while(children.hasNext()) {
            Resource childRes = children.next();
			Config childCfg = new Config(childRes);
            String value = childCfg.get("value",  childCfg.get("text",String.class));
            childrenValues.add(value);
        }
		return childrenValues;
    }
%>
<%!
    public Boolean checkForNulls(ArrayList<String> values) {
		Boolean nullPresent = false;
   	 	for(String value : values) {
        	if (value.contains("null")) {
            	nullPresent = true;
       	 	}
         }
		return nullPresent;
	}
%>