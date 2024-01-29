<%@page session="false" %>
<%@page contentType="text/html" pageEncoding="utf-8"
        import="com.eaton.platform.core.util.ChatConfigUtil,
    			java.util.ArrayList,
				java.util.List,
				com.eaton.platform.core.models.ChatConfigBean,
				com.eaton.platform.core.models.BCPlayerBean" %>

<%@include file="/libs/foundation/global.jsp" %>

<%
     ChatConfigBean chatBean = ChatConfigUtil.populateChatList(resource);


%>

	<ul style="float: left; margin: 0px;">
        <h2 style="margin:0;padding-left:0;">Chat Configurations</h2>
            <p></p>

        <c:set var="noOfChatViews" value="<%=chatBean.getNoOfChatViews()%>" />
        <c:set var="chatConfigViews" value="<%=chatBean.getListChatBean()%>" scope="request"/>


        <c:if test="${noOfChatViews gt 0}">

            <c:forEach var="j" begin="0" end="${noOfChatViews-1}" step="1" varStatus="status1">

                <c:set var="chatIndex" value="${j}" scope="request" />
                <c:set var="businessdivision" value="<%=chatBean.getListChatBean().get((Integer)request.getAttribute("chatIndex")).getBusinessdivision()%>" />
				<c:set var="urltochat" value="<%=chatBean.getListChatBean().get((Integer)request.getAttribute("chatIndex")).getUrltochat()%>" />
				<c:set var="urltoagent" value="<%=chatBean.getListChatBean().get((Integer)request.getAttribute("chatIndex")).getUrltoagent()%>" />
				<c:set var="onlinecontent" value="<%=chatBean.getListChatBean().get((Integer)request.getAttribute("chatIndex")).getOnlinecontent()%>" />
				<c:set var="offlinecontent" value="<%=chatBean.getListChatBean().get((Integer)request.getAttribute("chatIndex")).getOfflinecontent()%>" />
				<c:set var="entrypointid" value="<%=chatBean.getListChatBean().get((Integer)request.getAttribute("chatIndex")).getEntrypointid()%>" />
				<c:set var="aemtags" value="<%=chatBean.getListChatBean().get((Integer)request.getAttribute("chatIndex")).getAemtags()%>" />
				<c:set var="errmsgs" value="<%=chatBean.getListChatBean().get((Integer)request.getAttribute("chatIndex")).getErrMsgs()%>" />
				
				<li><div class="li-bullet"><h3>Chat View for : ${businessdivision}</h3></div></li>
                <li><div class="li-bullet"><strong>Business Division 	: </strong>${businessdivision}</div></li>
                <li><div class="li-bullet"><strong>URL to Chat 			: </strong>${urltochat}</div></li>
                <li><div class="li-bullet"><strong>URL to Agent 		: </strong>${urltoagent}</div></li>
                <li><div class="li-bullet"><strong>Online Content 		: </strong>${onlinecontent}</div></li>
                <li><div class="li-bullet"><strong>Offline Content 		: </strong>${offlinecontent}</div></li>
                <li><div class="li-bullet"><strong>Entry Point ID 		: </strong>${entrypointid}</div></li>
                <li><div class="li-bullet"><strong>AEM Tags 			: </strong>${aemtags}</div></li>
                <li><div class="li-bullet"><strong>Error Messages 		: </strong>${errmsgs}</div></li>
                <br/>
                <br/>


            </c:forEach>
        </c:if>
        <li></li>



<div>


        <br/>
        <br/>
           <button onclick="dialog.show()">Edit Chat Configuration</button>

  
</div>
