<%@include file="/libs/granite/ui/global.jsp" %><%
%><%@page session="false"
          import="java.util.HashMap,
                  java.util.Iterator,
                  java.util.List,
                  java.util.ArrayList,
                  org.apache.sling.api.resource.ResourceResolver,
                  org.apache.sling.api.SlingHttpServletRequest,
                  org.apache.sling.api.wrappers.ValueMapDecorator,
                  com.adobe.granite.ui.components.AttrBuilder,
                  com.adobe.granite.ui.components.ComponentHelper,
                  com.adobe.granite.ui.components.ComponentHelper.Options,
                  com.adobe.granite.ui.components.Config,
                  com.adobe.granite.ui.components.Field,
                  com.adobe.granite.ui.components.FormData,
                  com.adobe.granite.ui.components.FormData.NameNotFoundMode,
                  com.adobe.granite.ui.components.Tag,
                  com.adobe.granite.ui.components.Value" %><%--###
Example::

      + myinput
        - name = "myTabularMultiField"
        - sling:resourceType = "/apps/authoring/touchui/widgets/tabular-multifield"
        + field
          - sling:resourceType = "granite/ui/components/foundation/container"
          + items
              + pathfield
                 - sling:resourceType = "granite/ui/components/coral/foundation/form/pathfield"
                 - name = "./one"
              + textfield
                 - sling:resourceType = "granite/ui/components/foundation/form/textfield"
                 - name = "./two"

###--%>
<%
Config cfg = cmp.getConfig();
ValueMap vm = (ValueMap) request.getAttribute(Field.class.getName());

Tag tag = cmp.consumeTag();
AttrBuilder attrs = tag.getAttrs();

attrs.add("id", cfg.get("id", String.class));
attrs.addRel(cfg.get("rel", String.class));
attrs.addClass(cfg.get("class", String.class));
attrs.add("title", i18n.getVar(cfg.get("title", String.class)));
String name = cfg.get("name", String.class);
attrs.add("name", name);
attrs.addClass("tabular-Multifield");
attrs.add("data-init", "tabularmultifield");

attrs.addOthers(cfg.getProperties(), "id", "rel", "name", "class", "title", "fieldLabel", "fieldDescription", "renderReadOnly");

Resource field = cfg.getChild("field");

List<TabInfo> tabsInfo = getTabsInfo(slingRequest, resourceResolver, name);
%><div <%= attrs.build() %>><%
    if (cfg.get("deleteHint", true)) {
     for (TabInfo tabInfo : tabsInfo) {
            %><input type="hidden" name="./<%= name != null ? name + "/" : "" + tabInfo.getNodeName() %>@Delete"><%
     }
 }
    %>
    <div class="coral-TabPanel" data-init="tabs">
        <nav class="coral-TabPanel-navigation">
            <%for (TabInfo tabInfo : tabsInfo) {%>
            <a class="coral-TabPanel-tab product-grid-facet-group-tab" href="#"  data-toggle="tab">
                <span><%=tabInfo.getName()%><span>
                    <i class="close-tab-button js-tabular-Multifield-remove coral-Icon coral-Icon--close coral-Icon--sizeXS"></i>
                    </span></span>
            </a>
            <%}%>
            <a class="coral-TabPanel-tab js-tabular-Multifield-add" href="#" data-target="#field-add" data-toggle="tab">
                <i class="coral-Icon coral-Icon--add coral-Icon--sizeXS"></i>
            </a>
        </nav>
        <div class="coral-TabPanel-content">
            <%for (TabInfo tabInfo : tabsInfo) {%>
            <section class="coral-TabPanel-pane" role="tabpanel">
                <% include(field, tabInfo.getValues(), cmp, slingRequest); %>
            </section>
            <%}%>
            <section class="dummy-section coral-TabPanel-pane" style="display:none">
            </section>
        </div>
    </div>
    <script class="js-tabular-Multifield-input-template" type="text/html"><% include(field, cmp, slingRequest); %></script>
</div><%!

private void include(Resource field, ComponentHelper cmp, SlingHttpServletRequest request) throws Exception {
    FormData.push(request, new ValueMapDecorator(new HashMap<String, Object>()), NameNotFoundMode.IGNORE_FRESHNESS);
    // include the field with no value set at all

    ValueMap existingVM = (ValueMap) request.getAttribute(Value.FORM_VALUESS_ATTRIBUTE);
    String existingPath = (String) request.getAttribute(Value.CONTENTPATH_ATTRIBUTE);
    request.removeAttribute(Value.FORM_VALUESS_ATTRIBUTE);
    request.removeAttribute(Value.CONTENTPATH_ATTRIBUTE);
    cmp.include(field, new Options().rootField(false));
    request.setAttribute(Value.FORM_VALUESS_ATTRIBUTE, existingVM);
    request.setAttribute(Value.CONTENTPATH_ATTRIBUTE, existingPath);
}

private void include(Resource field, ValueMap map, ComponentHelper cmp, SlingHttpServletRequest request) throws Exception {
    FormData formData = FormData.from(request);
    NameNotFoundMode nameMode = NameNotFoundMode.IGNORE_FRESHNESS;
    if (formData != null) {
        nameMode = formData.getMode();
    }
    FormData.push(request, map, nameMode);
    ValueMap existing = (ValueMap) request.getAttribute(Value.FORM_VALUESS_ATTRIBUTE);
    request.setAttribute(Value.FORM_VALUESS_ATTRIBUTE, map);
    cmp.include(field, new Options().rootField(false));
    request.setAttribute(Value.FORM_VALUESS_ATTRIBUTE, existing);
}
private List<TabInfo> getTabsInfo(SlingHttpServletRequest request, ResourceResolver resolver, String name) throws Exception {
    List<TabInfo> tabsInfo = new ArrayList<TabInfo>();
    String contentPath = (String) request.getAttribute(Value.CONTENTPATH_ATTRIBUTE);
    if (name != null) {
        contentPath = contentPath + "/" + name;
    }
    Resource contentRes = resolver.getResource(contentPath);
    if (contentRes != null) {
        for (Iterator<Resource> it = contentRes.listChildren(); it.hasNext(); ) {
            Resource r = it.next();
            String rName = r.getName();
            if (rName.startsWith("tab")) {
                String tabIndex = rName.replace("tab", "");
                String tabName = "Tab " + tabIndex;
                ValueMap vm = r.adaptTo(ValueMap.class);
                TabInfo tabInfo = new TabInfo(tabName, rName, vm);
                tabsInfo.add(tabInfo);
            }
        }
    }
    return tabsInfo;
}

class TabInfo {
    private String name;
    private String nodeName;
    private ValueMap values;
    TabInfo(String name, String nodeName, ValueMap values) {
        this.name = name;
        this.nodeName = nodeName;
        this.values = values;
    }
    String getName() {
        return name;
    }
    String getNodeName() {
        return nodeName;
    }
    ValueMap getValues() {
        return values;
    }
}
%>
