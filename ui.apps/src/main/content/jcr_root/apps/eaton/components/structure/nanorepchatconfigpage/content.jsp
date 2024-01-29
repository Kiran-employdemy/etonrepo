<%@page session="false" %>
<%@page contentType="text/html" pageEncoding="utf-8"
        import="com.eaton.platform.core.util.NanoRepChatConfigUtil,
    			java.util.ArrayList,
				java.util.List,
				com.eaton.platform.core.models.NanoRepChatConfigBean"
				 %>

<%@include file="/libs/foundation/global.jsp" %>

<%
     NanoRepChatConfigBean chatBean = NanoRepChatConfigUtil.populateChatList(resource);


%>

	<ul style="float: left; margin: 0px;">
        <h2 style="margin:0;padding-left:0;">NanoRep Configurations</h2>
            <p></p>

        <c:set var="noOfChatViews" value="<%=chatBean.getNoOfChatViews()%>" />
        <c:set var="chatConfigViews" value="<%=chatBean.getListChatBean()%>" scope="request"/>


        <c:if test="${noOfChatViews gt 0}">

            <c:forEach var="j" begin="0" end="${noOfChatViews-1}" step="1" varStatus="status1">

                <c:set var="chatIndex" value="${j}" scope="request" />
                <c:set var="businessdivision" value="<%=chatBean.getListChatBean().get((Integer)request.getAttribute("chatIndex")).getBusinessdivision()%>" />
				<c:set var="entrypointid" value="<%=chatBean.getListChatBean().get((Integer)request.getAttribute("chatIndex")).getKbNumber()%>" />
				<c:set var="aemtags" value="<%=chatBean.getListChatBean().get((Integer)request.getAttribute("chatIndex")).getAemtags()%>" />
				<c:set var="errmsgs" value="<%=chatBean.getListChatBean().get((Integer)request.getAttribute("chatIndex")).getErrmsgs()%>" />
				
				<li><div class="li-bullet"><h3>NanoRep View for     : ${businessdivision}</h3></div></li>
                <li><div class="li-bullet"><strong>Business Division 	 : </strong>${businessdivision}</div></li>
                <li><div class="li-bullet"><strong>Knowledge Base Number : </strong>${entrypointid}</div></li>
                <li><div class="li-bullet"><strong>AEM Tags 			 : </strong>${aemtags}</div></li>
                <li><div class="li-bullet"><strong>Error Messages 		 : </strong>${errmsgs}</div></li>
                <br/>
                <br/>


            </c:forEach>
        </c:if>
        <li></li>



<div>


        <br/>
        <br/>
           <button onclick="dialog.show()">Edit NanoRep Configuration</button>

  
</div>
