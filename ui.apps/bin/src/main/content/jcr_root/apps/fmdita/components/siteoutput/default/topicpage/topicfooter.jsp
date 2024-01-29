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

<%--
  Footer script
--%>

<%@ page import="java.util.HashMap,
                java.util.ArrayList,
                java.util.Map,
                com.day.cq.wcm.foundation.Paragraph,
                com.day.cq.wcm.foundation.ParagraphSystem"%>

<%@include file="/libs/foundation/global.jsp"%>

<%
  ArrayList<HashMap<String, Object>> footNotes = (ArrayList<HashMap<String, Object>>)request.getAttribute("anonfootnotes");
  HashMap<String, Object> idFootNotes = (HashMap<String, Object>)request.getAttribute("idedfootnotes");
    
  if (!(footNotes == null && idFootNotes == null))
  {
%>

  <div class="footnotes" id="footnotes">
    <!-- Render footnotes -->
    <%
      if (footNotes != null) {
        for (HashMap footNote: footNotes) {
          if (footNote != null) {
            Resource node = slingRequest.getResourceResolver().getResource((String)footNote.get("path"));
            String callout = (String)footNote.get("callout");
            String uidSrc = (String)footNote.get("uidsrc");
            String uidTarget = (String)footNote.get("uidtarget");
      %>
              <div class="fn">
                <a name="<%= uidTarget %>" href="#<%= uidSrc %>">
                  <sup><%= callout %></sup>
                </a>
                <%
                  ParagraphSystem parSys = ParagraphSystem.create(node, slingRequest);
                  for (Paragraph par: parSys.paragraphs()){
                    %><sling:include resource="<%= par %>"/><%
                  }
                %>
              </div>
      <%
          }
        }
      }

      if (idFootNotes != null) {
        for (Map.Entry<String, Object> entry: idFootNotes.entrySet()) {
          HashMap<String, Object> footNote = (HashMap<String, Object>)entry.getValue();
          Resource node = slingRequest.getResourceResolver().getResource((String)footNote.get("path"));
          String callout = (String)footNote.get("callout");
          String uidSrc = (String)footNote.get("uidsrc");
          String uidTarget = (String)footNote.get("uidtarget");
          Boolean show = (Boolean)footNote.get("show");

          if (show) {
      %>
              <div class="fn">
                <sup><%= callout %></sup>
                <%
                  ParagraphSystem parSys = ParagraphSystem.create(node, slingRequest);
                  for (Paragraph par: parSys.paragraphs()){
                    %><sling:include resource="<%= par %>"/><%
                  }
                %>
              </div>
      <%
          }
        }
      }
      %>
  </div>

<%
  }
%>