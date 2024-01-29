<%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" %><%
%>
<%@page import="java.util.List,java.util.ArrayList,java.util.Iterator,com.eaton.platform.core.util.ReferenceUtil" %>

<section class="mega-menu dark-theme">
    <div class="container-fluid">
        <div class="row">


<%
    String css=null;
	String overlayPath=null;
	List<String> megaMenuReferenceList = ReferenceUtil.getMegaMenuReference(resourceResolver,currentPage);
	if(megaMenuReferenceList != null) {
		Iterator Iterator = megaMenuReferenceList.iterator();
        while (Iterator.hasNext()) {
			String path = Iterator.next().toString();
			String[] parts = path.split(",");
			css=parts[0];
			overlayPath=parts[1];



%>


			<div class="mega-menu__content" data-target='mega-menu-<%=css%>'>
           		<sling:include path='<%=overlayPath%>' />

            </div>
<%
        }
    }
%>
        </div>
    </div>
</section>
