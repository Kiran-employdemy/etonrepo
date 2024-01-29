<%@page session="false" %>
<%@page contentType="text/html" pageEncoding="utf-8"
        import="com.eaton.platform.core.models.brandingselectortool.BrandColorSelectionConfigModel,
        com.eaton.platform.core.util.BrandColorSelectionConfigUtil" %>
<%@include file="/libs/foundation/global.jsp" %>
<%
    BrandColorSelectionConfigModel brandColorSelectionConfigModel = BrandColorSelectionConfigUtil.getBrandColorSelectionConfigDetails(resource);
%>
<div>
    <ul style="float: left; margin: 0px;">
        <h2 style="margin:0;padding-left:0;">Brand Color Selection Details</h2>
        <h3>Branding Color Impact guidelines document link :</h3>
        <c:set var="colorImpactGuidelinesLink" value="<%=brandColorSelectionConfigModel.getColorImpactGuidelinesLink()%>" />
        <a href="${colorImpactGuidelinesLink}" target="_blank">${colorImpactGuidelinesLink}</a>
        <p></p>
        <c:set var="primaryBrandingColor" value="<%=brandColorSelectionConfigModel.getPrimaryBrandingColor()%>" />
        <li>
            <div class="li-bullet"><strong>Primary Branding Color(color will replace all Eaton Blue,includingbuttons,excluding text links): </strong>${primaryBrandingColor}</div>
        </li>
        <c:set var="accentColorOne" value="<%=brandColorSelectionConfigModel.getAccentColorOne()%>" />
        <li>
            <div class="li-bullet"><strong>Accent Color #1(color should be able to stand alone on light background) : </strong>${accentColorOne}</div>
        </li>
        <c:set var="accentColorTwo" value="<%=brandColorSelectionConfigModel.getAccentColorTwo()%>" />
        <li>
            <div class="li-bullet"><strong>Accent Color #2(color should be able to stand alone on light background) : </strong>${accentColorTwo}</div>
        </li>
        <c:set var="linkColor" value="<%=brandColorSelectionConfigModel.getLinkColor()%>" />
        <li>
            <div class="li-bullet"><strong>Link Color(color will apply to text links) : </strong>${linkColor}</div>
        </li>
        <c:set var="siteName" value="<%=brandColorSelectionConfigModel.getSiteName()%>" />
        <li>
            <div class="li-bullet"><strong>Site name(for SEO purposes) : </strong>${siteName}</div>
        </li>
        <c:set var="twitterSite" value="<%=brandColorSelectionConfigModel.getTwitterSite()%>" />
        <li>
            <div class="li-bullet"><strong>Twitter handle for social integration : </strong>${twitterSite}</div>
        </li>
        <br/>
        <br/>
        <button onclick="dialog.show()">Edit Brand Color Selection Configuration</button>
    </ul>
</div>