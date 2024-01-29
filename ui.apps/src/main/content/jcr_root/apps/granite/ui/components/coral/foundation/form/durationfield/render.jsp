<%--
     durationfield - render.jsp
     ---
     Renders a text field containing the duration (dc:extent) of a given node in the format HH:mm:ss. (Used primarily for AEM Assets)
     ---
     By: Julie Rybarczyk (julie@freedomdam.com)
--%><%
%><%@include file="/libs/granite/ui/global.jsp" %><%
%><%@page session="false"
          import="com.adobe.granite.ui.components.AttrBuilder,
                  com.adobe.granite.ui.components.Config,
                  com.adobe.granite.ui.components.Field,
                  com.adobe.granite.ui.components.Tag,
                  java.text.SimpleDateFormat,
                  java.util.Calendar,
                  java.lang.Integer" %><%--###
TextField
=========

.. granite:servercomponent:: /libs/granite/ui/components/foundation/form/durationfield
   :supertype: /libs/granite/ui/components/foundation/form/field
   
   A duration field component.

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

    attrs.add("id", cfg.get("id", String.class));
    attrs.addClass(cfg.get("class", String.class));
    attrs.addRel(cfg.get("rel", String.class));
    attrs.add("title", i18n.getVar(cfg.get("title", String.class)));

    // Get the duration (in milliseconds) of the asset (dc:extent).
    String durationMillisFormatted = ""; // default

    // If a valid duration (number) is given, format it to HH:mm:ss.
    // Otherwise, the value is set to the default.
    String durationMillis = vm.get("value", String.class);
    if (durationMillis.matches("[-+]?\\d*\\.?\\d+")) {
        try {
            Integer durationMillisInt = new Integer(durationMillis);
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            cal.clear();
            cal.set(Calendar.MILLISECOND, durationMillisInt);
            durationMillisFormatted = format.format(cal.getTime());
        } catch (NumberFormatException nfe) {
            // Log the error. Value will be set to default below.
            log.error("NumberFormatException found when attempting to convert duration of asset from"
                    + " milliseconds to HH:mm:ss. Value is: " + durationMillis);
        }
    }

    attrs.add("value", durationMillisFormatted);

    attrs.addOthers(cfg.getProperties(), "id", "class", "rel", "title", "type", "name", "disabled", "fieldLabel", "fieldDescription", "renderReadOnly", "ignoreData");
    attrs.add("type", "duration");
    attrs.add("name", cfg.get("name", String.class));
    attrs.addDisabled(cfg.get("disabled", false));

    if (cfg.get("required", false)) {
        attrs.add("aria-required", true);
    }

    attrs.addClass("coral-Textfield");

%><input <%= attrs.build() %> />