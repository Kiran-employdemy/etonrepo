<%@page session="false" %>
<%@page contentType="text/html" pageEncoding="utf-8"
        import="com.eaton.platform.core.models.brandingselectortool.BrandFontSelectionConfigModel"%>
<%@include file="/libs/foundation/global.jsp" %>
<cq:includeClientLib categories="eaton.brandfontselection"/>
<c:set var ="brandFontSelectionConfigModel" value="<%= resource.adaptTo(BrandFontSelectionConfigModel.class)%>" />
<div>
    <ul style="float: left; margin: 0px;">
        <h2 style="margin:0;padding-left:0;">Brand Font Selection Details</h2>
        <h3>Branding Font Impact guidelines document link :</h3>
        <a href="${brandFontSelectionConfigModel.fontImpactGuidelinesLink}" target="_blank">${brandFontSelectionConfigModel.fontImpactGuidelinesLink}</a>
        <p></p>
        <li>
            <div class="li-bullet"><strong>Primary Brand Font (Primary branding fonts will appear mostly in titles and headings) - </strong>${brandFontSelectionConfigModel.primaryBrandFont}</div>
            <div class="li-bullet"><strong>Fallback font: - </strong>${brandFontSelectionConfigModel.primaryBrandFontFallbackFont}</div>
        </li>
        <li>
            <div class="li-bullet"><strong>Primary Brand Font: Medium - </strong>${brandFontSelectionConfigModel.primaryBrandFontMedium}</div>
            <div class="li-bullet"><strong>Fallback font: - </strong>${brandFontSelectionConfigModel.primaryBrandFontMediumFallbackFont}</div>
        </li>
        <li>
            <div class="li-bullet"><strong>Primary Brand Font: Bold - </strong>${brandFontSelectionConfigModel.primaryBrandFontBold}</div>
            <div class="li-bullet"><strong>Fallback font: - </strong>${brandFontSelectionConfigModel.primaryBrandFontBoldFallbackFont}</div>
        </li>
        <li>
            <div class="li-bullet"><strong>Body Font    (Font will appear in body copy, links, buttons, eyebrows, etc.) - </strong>${brandFontSelectionConfigModel.bodyFont}</div>
            <div class="li-bullet"><strong>Fallback font: -  </strong>${brandFontSelectionConfigModel.bodyFontFallbackFont}</div>
        </li>
        <br/>
        <br/>
        <button class="brand-font-selection-dialog">Edit Brand Font Selection Configuration</button>
    </ul>
</div>