<%@page contentType="text/html" pageEncoding="utf-8"
        import="java.util.Enumeration" %>
<%@page session="false"%>
<%@include file="/libs/foundation/global.jsp"%>

<div>

    <img src="/etc/designs/ace/advanced-html.png"
         height="120px" 
         width="120px" 
         alt="Raw HTML" 
         title="Raw HTML Configuration"
         style="float: left;padding-top:0px;" />    

     <ul style="height:130px"> 

        <%if(properties.get("documentation", "")!=""){%>
        <h3><a href="<%=properties.get("documentation")%>">Download documentation</a></h3>
        <%}%>
	    <button onclick="dialog.show()">Configure Raw HTML</button>

        <p />
    </ul>

   <cq:include script="form.jsp" />
</div>





    
