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

<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/libs/fmdita/components/common/init.jsp" %>

<%
  String cls = "";

	Boolean showBreadCrumbs = properties.get("showBreadCrumbs", (Boolean)null);
	if (showBreadCrumbs != null && showBreadCrumbs == false)
  {
      cls += "nobreadcrumbs";
  }

  String tocPath = props.get("tocPath", "");
  if (!basePath.isEmpty()) {
    tocPath = FilenameUtils.separatorsToUnix(FilenameUtils.concat(basePath, tocPath));
  }
%>


<body class="<%= cls %>">
  <!-- Include Template Header -->
    <cq:include path="root/header" resourceType="eaton/components/structure/header"/>
  <main class="clearfix">

    <!-- TOC Component -->
      <cq:include path="<%= tocPath %>" resourceType="fmdita/components/siteoutput/default/tableofcontents"/> 

    <section id="section">
      <!-- Topic Header -->
      <cq:include script="topicheader.jsp"/>
      <div class="par parsys">
        <div class="topic section">
          <!-- Topic Content -->
            <cq:include path="root/contentnode" resourceType="wcm/foundation/components/responsivegrid"/>
          <!-- Topic Footer -->
          <cq:include script="topicfooter.jsp"/>
        </div>
      </div>
    </section>
<cq:include path="root/breadcrumb" resourceType="eaton/components/structure/breadcrumb"/>
  </main>
  <!-- Topic Footer -->
<cq:include path="root/footer" resourceType="eaton/components/structure/footer"/>


  <!-- Include Client Libraries -->

  <cq:includeClientLib categories="fmdita.template.default.common"/>
  <cq:includeClientLib categories="fmdita.template.default.topicpage"/>
  <cq:includeClientLib categories="fmdita.template.default.breadcrumbs"/>
    <cq:includeClientLib categories="etn-jquery"/>
  <cq:includeClientLib categories="etn-all"/>
    <cq:includeClientLib categories="eaton-ccms-footer"/>

  <link href='https://fonts.googleapis.com/css?family=Merriweather' rel='stylesheet' type='text/css'>
  <link href='https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,700' rel='stylesheet' type='text/css'>

</body>