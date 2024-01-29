<%@page session="false" %>
<%@page contentType="text/html"
            pageEncoding="utf-8"
            import="javax.jcr.Node,
                    java.util.Iterator,
                    com.day.cq.wcm.webservicesupport.Configuration,
                    com.day.cq.wcm.webservicesupport.Service,
                    org.apache.commons.lang.StringEscapeUtils,
                    org.apache.sling.api.resource.Resource"%>
                    

<%@include file="/libs/foundation/global.jsp"%>



<%
    final String id = currentPage.getName();
    final String title = xssAPI.encodeForHTML(properties.get("jcr:title", id)); 
    final String description = xssAPI.encodeForHTML(properties.get("jcr:description", ""));
    final String path = resource.getPath();
    final String resourceType = resource.getResourceType();
    final String dialogPath = resourceType + "/dialog";
%>

<body>

     <script type="text/javascript">
        var dialog = CQ.WCM.getDialog("<%= dialogPath %>");
       dialog.loadContent(CQ.WCM.getPagePath() + "/jcr:content");
       //dialog.on('afteredit',function test(){alert('Test Reload');});
        //dialog.on('beforesubmit', $CQ.SocialAuth.osgi.updateOAuthProviderConfig);
    </script>
    
    <h1><%=title %></h1>
    
    <p><%=description %></p>

    <cq:include script="content.jsp" />
    
    <%@include file="/libs/cq/cloudserviceconfigs/components/configpage/opendialog.jsp"%>
    
    <p>&nbsp;</p>
</body>