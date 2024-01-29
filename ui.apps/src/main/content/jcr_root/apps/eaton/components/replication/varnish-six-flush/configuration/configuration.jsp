<%@ page import="com.adobe.acs.commons.replication.dispatcher.DispatcherFlusher" %>
<%--
  ~ #%L
  ~ ACS AEM Commons Bundle
  ~ %%
  ~ Copyright (C) 2013 Adobe
  ~ %%
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~      http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  ~ #L%
  --%>
<%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" contentType="text/html" pageEncoding="utf-8"
        import="com.day.cq.replication.Agent,
        org.apache.commons.lang.StringUtils,
        com.day.cq.replication.Agent,
        java.util.Map,
        org.apache.sling.commons.osgi.PropertiesUtil,
        com.day.cq.replication.AgentManager,
        com.adobe.acs.commons.replication.dispatcher.DispatcherFlushFilter"%><%

    /* Services */
    final DispatcherFlusher dispatcherFlusher = sling.getService(DispatcherFlusher.class);

    /* Agents */
    final Agent[] flushAgents = dispatcherFlusher.getAgents(DispatcherFlushFilter.HIERARCHICAL);
    boolean hasAgents = false;

    /* Flush Action */
    final String actionType = properties.get("replicationActionType", "");
    boolean hasActionType = StringUtils.isNotBlank(actionType);

    /* Flush Paths */
    final String[] paths = properties.get("paths", new String[]{});
    boolean hasPaths = paths.length > 0;

    /* My code */
    final AgentManager agentManager = sling.getService(AgentManager.class);
    final Map<String, Agent> agents = agentManager.getAgents();
    for(Map.Entry<String, Agent> entryAgent : agents.entrySet()) {
        Agent agentData = entryAgent.getValue();
         if(agentData.isEnabled()) {
         final ValueMap propertyConfig = agentData.getConfiguration().getProperties();
         String domainName = PropertiesUtil.toString(propertyConfig.get("varnishSixDomain"), StringUtils.EMPTY);
            if (StringUtils.isNotBlank(domainName)) {
                hasAgents = true;
                break;
            }
         }
    }
%>

<% if(hasActionType && hasPaths && hasAgents) { %>
<form id="varnishsixconfig" method="POST">
    <input class="button" type="button"  value="Flush Paths on Varnish Six"/>
</form>
<% } %>

<h3>Flush Action</h3>
<ul>
    <% if(StringUtils.equals("ACTIVATE", actionType)) { %>
    <li>Invalidate Cache</li>
    <% } else if(StringUtils.equals("DELETE", actionType)) { %>
    <li>Delete Cache</li>
    <% } else { %>
    <li class="not-set">Flush method not set</li>
    <% } %>
</ul>

<h3>Paths to Flush</h3>
<ul>
    <% if(!hasPaths) { %><li class="not-set">Varnish flush paths not set</li><% } %>
    <% for(final String path : paths) { %>
    <li><%= path %></li>
    <% } %>
</ul>

<h3>Active Varnish Six Flush Agents (Includes only Varnish Agents)</h3>
<ul>
    <% if(!hasAgents) { %><li class="not-set"><a href="<%= slingRequest.getContextPath() %>/miscadmin#/etc/replication/agents.author" target="_blank">No active
    Varnish Six Flush replication agents</a></li><% } %>
    <% for(Map.Entry<String, Agent> entry : agents.entrySet()) {
           Agent valueAgent = entry.getValue();
           if(valueAgent.isEnabled()) {
           final ValueMap property = valueAgent.getConfiguration().getProperties();
           String domain = PropertiesUtil.toString(property.get("varnishSixDomain"), StringUtils.EMPTY);
           if (StringUtils.isNotBlank(domain)) {%>
    <li><a href="<%= resourceResolver.map(valueAgent.getConfiguration().getConfigPath()) %>.log.html" target="_target"><%= valueAgent.getConfiguration().getName() %></a></li>
    <%}
    }
    } %>
</ul>

<script>
    
    $("#varnishsixconfig").click(function(e)
    {
        $.ajax({
            url: '/eaton/clearVarnishSixCache',
            type: 'POST',
            data:  {
                "path":'${resource.path}',
                "redirectPath" : window.location.href
            },
            success: function(data, textStatus, jqXHR)
            {
                 if (data.hasOwnProperty("redirectPath")) {
                     window.location.href = data.redirectPath;
                 } else {
                     CQ.Ext.Msg.alert('Status',"An error occurred during clearing varnish six cache. Response Code: "+jqXHR.status);
                 }
            },
            error: function(jqXHR, textStatus, errorThrown)
            {
                 CQ.Ext.Msg.alert('Status',"An error occurred during clearing varnish six cache. Response Code: "+jqXHR.status);
            }
        });
        e.preventDefault(); //Prevent Default action.
    });
</script>