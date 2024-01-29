<%--
     filesizefield - render.jsp
     ---
     Renders a text field containing the size of the current asset using bytes notations (MB, GB, etc) rather than just a long int. (Used primarily for AEM Assets)
     ---
     By: Aaron Baecker (aaron@freedomdam.com)
--%><%
%><%@include file="/libs/granite/ui/global.jsp" %><%
%><%@page session="false"
          import="java.util.Formatter,
                  com.adobe.granite.ui.components.AttrBuilder,
                  com.adobe.granite.ui.components.Config,
                  com.adobe.granite.ui.components.Field,
                  com.adobe.granite.ui.components.Tag" %><%--###
TextField
=========

.. granite:servercomponent:: /libs/granite/ui/components/foundation/form/filenamefield
   :supertype: /libs/granite/ui/components/foundation/form/field
   
   A filesize field component.

   It extends :granite:servercomponent:`Field </libs/granite/ui/components/foundation/form/field>` component.

   It has the following content structure:

   .. gnd:gnd::

      [granite:FormTextField] > granite:commonAttrs
      
      /**
       * The name that identifies the field when submitting the form.
       */
      - name (String)
      
      /**
       * The value of the field.
       */
      - value (StringEL)
      
      /**
       * Indicates if the field is in disabled state.
       */
      - disabled (Boolean)
###--%><%

    Config cfg = cmp.getConfig();
    ValueMap vm = (ValueMap) request.getAttribute(Field.class.getName());
    Field field = new Field(cfg);
    
    Tag tag = cmp.consumeTag();
    AttrBuilder attrs = tag.getAttrs();
    cmp.populateCommonAttrs(attrs);

    // Start of attrs compatibility; please use cmp.populateCommonAttrs(attrs).
    attrs.add("id", cfg.get("id", String.class));
    attrs.addClass(cfg.get("class", String.class));
    attrs.addRel(cfg.get("rel", String.class));
    attrs.add("title", i18n.getVar(cfg.get("title", String.class)));
    if(vm.get("value", String.class) != null && !vm.get("value", String.class).equals("")) {
        String valStr = vm.get("value", String.class);
        Double valDouble = new Double(valStr);
		String[] sizeNotations = {"bytes", "KB", "MB", "GB", "TB"};
		int counter = 0;
		
		while(valDouble > 1024 ) {
			valDouble /= 1024;
			counter++;
		}//end while
		
		attrs.add("value", new Formatter().format("%.2f", valDouble) + " " + sizeNotations[counter]);
    }//if
    else {
        attrs.add("value", "N/A");
    }//else
    attrs.addOthers(cfg.getProperties(), "id", "class", "rel", "title", "type", "name", "disabled", "fieldLabel", "fieldDescription", "renderReadOnly", "ignoreData");
    // End of attrs compatibility.
    
    attrs.add("type", "filename");
    attrs.add("name", cfg.get("name", String.class));
    attrs.addDisabled(cfg.get("disabled", false));

    if (cfg.get("required", false)) {
        attrs.add("aria-required", true);
    }

    attrs.addClass("coral-Textfield");
    
%><input <%= attrs.build() %> />