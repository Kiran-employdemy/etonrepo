<%--
     filesizefield - readonly.jsp
     ---
     Renders the readonly (non-edit mode) view of a filesizefield.
     ---
     By: Aaron Baecker (aaron@freedomdam.com)
--%><%
%><%@include file="/libs/granite/ui/global.jsp" %><%
%><%@page session="false"
          import="java.util.HashMap,
                  java.util.Formatter,
                  org.apache.sling.api.wrappers.ValueMapDecorator,
                  com.adobe.granite.ui.components.AttrBuilder,
                  com.adobe.granite.ui.components.Config,
                  com.adobe.granite.ui.components.Tag,
                  com.adobe.granite.ui.components.Field" %><%

    Config cfg = cmp.getConfig();
	
	Tag tag = cmp.consumeTag();
    AttrBuilder attrs = tag.getAttrs();
                  
    String fieldLabel = cfg.get("fieldLabel", String.class);
    String value = cmp.getValue().val(cmp.getExpressionHelper().getString(cfg.get("value", "")));
    
    if(!value.equals("")) {
        Double valDouble = new Double(value);
		String[] sizeNotations = {"bytes", "KB", "MB", "GB", "TB"};
		int counter = 0;
		
		while(valDouble > 1024 ) {
			valDouble /= 1024;
			counter++;
		}//end while
		
		value = new Formatter().format("%.2f", valDouble) + " " + sizeNotations[counter];
    }//if                                                        

    if (cmp.getOptions().rootField()) {
        attrs.addClass("coral-Form-fieldwrapper");
        
        %><span <%= attrs.build() %>><%
	        if (fieldLabel != null) {
	            %><label class="coral-Form-fieldlabel"><%= outVar(xssAPI, i18n, fieldLabel) %></label><%
	        }
	        %><span class="coral-Form-field foundation-layout-util-breakword"><%= xssAPI.encodeForHTML(value) %></span
        ></span><%
    } else {
        %><span <%= attrs.build() %>><%= xssAPI.encodeForHTML(value) %></span><%
    }
%>