<%--
  ADOBE CONFIDENTIAL
  __________________
  Copyright 2016 Adobe Systems Incorporated
  All Rights Reserved.
  NOTICE:  All information contained herein is, and remains
  the property of Adobe Systems Incorporated and its suppliers,
  if any.  The intellectual and technical concepts contained
  herein are proprietary to Adobe Systems Incorporated and its
  suppliers and are protected by trade secret or copyright law.
  Dissemination of this information or reproduction of this material
  is strictly forbidden unless prior written permission is obtained
  from Adobe Systems Incorporated.
____________________
--%>

<%@page session="false"%>

<%@page import="java.lang.Iterable,
          org.apache.sling.api.resource.Resource,
          org.apache.sling.api.resource.ValueMap" %>

<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/libs/fmdita/components/common/init.jsp" %>
<%@ page import="com.adobe.fmdita.common.UrlUtil"%>
<%@ page import="java.net.MalformedURLException" %>

<body>
  <%-- Include Template Header --%>

              <cq:include path="root/header" resourceType="eaton/components/structure/header"/>
  <main class="clearfix">



    <%-- Empty data-columns property required by Salvattore --%>   
    <div class="toccontainer" data-columns>
      <%!
        StringBuilder html = new StringBuilder();
        private void renderTOC(Iterable<Resource> topChildren, final String parentKey) {
          Iterable<Resource> subChildren = null;
          int colorCounter = 1;

          int childCount = 0;

          for (Resource topChild: topChildren) {
            ValueMap props = topChild.getValueMap();
            String topLink = props.get("link", "");
            String topTitle = props.get("title", topLink);
            String topScope = props.get("scope", "").toLowerCase();
            final String childKey = String.format("%s%d", parentKey.isEmpty() ? "" :  parentKey + ".", childCount++);

            if ("".equals(topLink))
              continue;
            try {
              topLink = UrlUtil.addUrlParam(topLink, "toc", childKey);
            } catch (MalformedURLException ignored) {}
            
            if (props.get("toc", "").toLowerCase().equals("no")) {
              renderTOC(topChild.getChildren(), childKey);
              continue;
            }
            
            String tocItemColor = "tocitemcolor" + colorCounter++ % 10; // Bound to the css classname tocitemcolor
            subChildren = topChild.getChildren();
            
            html.append("<div class='tocitem " + tocItemColor + "'>");
            html.append("<div class='mainentry'>");
            if (topScope.equals("external"))
              html.append("<a target='_blank' href='" + topLink + "'>" + topTitle + "</a></div>");
            else
              html.append("<a href='" + topLink + "'>" + topTitle + "</a></div>");

            int subChildCount = 0;
            for (Resource subChild : subChildren) {
              String subLink = subChild.getValueMap().get("link", "");
              String subTitle = subChild.getValueMap().get("title", subLink);
              String subScope = subChild.getValueMap().get("scope", "").toLowerCase();
              final String subchildKey = String.format("%s.%d", childKey, subChildCount++);

              if (subLink == null || subLink.equals(""))
                continue;
              try {
                subLink = UrlUtil.addUrlParam(subLink, "toc", subchildKey);
              } catch (MalformedURLException ignored) {}
              
              html.append("<div class='subentry'>");
              if (topScope.equals("external"))
                html.append("<a target='_blank' href='" +  subLink + "'>" + subTitle + "</a></div>");
              else
                html.append("<a href='" + subLink + "'>" + subTitle + "</a></div>");
            }

            html.append("</div>");
          }
        }
      %>

      <%
        html.setLength(0);
        String tocPath = "";
        Resource tocRoot = null;
        if (!basePath.isEmpty()) {
            tocPath = FilenameUtils.separatorsToUnix(FilenameUtils.concat(basePath, "jcr:content/toc"));
          tocRoot = resolver.getResource(tocPath);
          renderTOC(tocRoot.getChildren(), "");
        }
      %>

      <%= html.toString() %>

      </div>
  </main>
   <cq:include path="root/contentnode" resourceType="wcm/foundation/components/responsivegrid"/>
<cq:include path="root/breadcrumb" resourceType="eaton/components/structure/breadcrumb"/>
<cq:include path="root/footer" resourceType="eaton/components/structure/footer"/>

  <%-- Include Client Libraries --%>
<cq:includeClientLib categories="fmdita.template.default.common"/>
  <cq:includeClientLib categories="fmdita.template.default.landingpage"/>
  <cq:includeClientLib categories="etn-jquery"/>
  <cq:includeClientLib categories="etn-all"/>
  <link href='https://fonts.googleapis.com/css?family=Merriweather' rel='stylesheet' type='text/css'>
  <link href='https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400' rel='stylesheet' type='text/css'>

</body>