<%@page session="false" %>
<%@page contentType="text/html" pageEncoding="utf-8"
        import="com.eaton.platform.core.util.BrightcoveUtil,
    			java.util.ArrayList,
				java.util.List,
				com.eaton.platform.core.models.BCAccountBean,
				com.eaton.platform.core.models.BCPlayerBean" %>

<%@include file="/libs/foundation/global.jsp" %>

<%
     BCAccountBean accountBean = BrightcoveUtil.getBCAccounts(resource);
	 String errorMsg = BrightcoveUtil.getBCErrorMessage(resource);

%>
<div>

    <ul style="float: left; margin: 0px;">
        <h2 style="margin:0;padding-left:0;">Brightcove Accounts</h2>
            <p></p>

        <c:set var="accountTitle" value="<%=accountBean.getAccName()%>" />
        <c:set var="accountNumber" value="<%=accountBean.getAccNumber()%>" />
        <c:set var="noOfPlayers" value="<%=accountBean.getNumOfPlayers()%>" />
        <li><div class="li-bullet"><h3>${accountTitle} : ${accountNumber}</h3></div></li>
        <c:set var="playerList" value="<%=accountBean.getPlayerDetails()%>" scope="request" />
        <c:if test="${noOfPlayers gt 0}">
            <c:forEach var="j" begin="0" end="${noOfPlayers-1}" step="1" varStatus="status1">
                <c:set var="playerIndex" value="${j}" scope="request" />
                <c:set var="playerName" value="<%=accountBean.getPlayerDetails().get((Integer)request.getAttribute("playerIndex")).getPlayerName()%>" />
                <c:set var="playerId" value="<%=accountBean.getPlayerDetails().get((Integer)request.getAttribute("playerIndex")).getPlayerId()%>" />
                
                <li><div class="li-bullet"><strong>Player Name : </strong>${playerName}</div></li>
                <li><div class="li-bullet"><strong>Player ID : </strong>${playerId}</div></li>
            </c:forEach>
        </c:if>
        <li></li>
        <c:set var="errorMsg" value="<%=errorMsg%>" />
        Error Message : ${errorMsg}
        <br/>
        <br/>

        
        <br/>
        <br/>
           <button onclick="dialog.show()">Edit Brightcove Configuration</button>
    
    </ul>
</div>
