<%@page session="false" import="javax.jcr.*,
        com.day.cq.wcm.api.Page,
        com.day.cq.wcm.api.PageManager,
        org.apache.sling.api.resource.Resource,
        java.io.IOException,
        java.util.ArrayList,
        java.util.List,
        java.util.StringTokenizer,
        java.util.Date,
        java.text.SimpleDateFormat,
        java.util.Locale,
        java.util.Calendar,
        javax.servlet.jsp.JspWriter,
        org.apache.sling.jcr.api.SlingRepository,
        javax.jcr.Property,
        javax.jcr.query.Query,
        javax.jcr.query.QueryManager,
        javax.jcr.query.QueryResult,
        javax.jcr.Node,
        javax.jcr.NodeIterator,
        javax.jcr.ValueFactory,
        javax.jcr.Value,
        org.apache.sling.api.resource.ResourceResolver,
        org.apache.sling.api.resource.ResourceResolverFactory,        
        javax.jcr.Session" %><%
%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %><%
%><%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %><%
%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%
%><%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %><%
%><%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %><%
%>
<%@include file="/libs/foundation/global.jsp"%>
<sling:defineObjects />
<%@page import="com.day.jcr.vault.packaging.PackageManager"%>


 <%

      ResourceResolver adminResourceResolver = null;
      String aceeditorcode = "";
      String pageTitle = "";
      String currPageName = "";
      String propertyName = "body";
      String templateName = "rawhtmlconfigpage";
      String value = "";
      String valueType = "String";
      String operationSelected ="";
      List<Node> nodeList = new ArrayList<Node>();
      final SlingRepository repos = sling.getService(SlingRepository.class);
      Session sess = repos.loginAdministrative(null);
      ResourceResolverFactory resolverFactory = sling.getService(ResourceResolverFactory.class); 
      adminResourceResolver = resolverFactory.getAdministrativeResourceResolver(null);



          //Fetching the required parameters.
          aceeditorcode = request.getParameter("aceeditorval");
          pageTitle = request.getParameter("pagetitle");
          currPageName = request.getParameter("pagename");
          value = aceeditorcode;

                          try {

                              //Fetching the required node for setting property.
                              nodeList = executeQuery(adminResourceResolver, out, propertyName, templateName, pageTitle);

                              //Set the property for the HTML Fragment fetched.
                              setPropertyValueToNodes( nodeList, value, valueType, out, propertyName, sess );

                              } catch(Exception exception){

                                      out.write("Exception occurred while perform HTML Fragment save operation "+exception); 
                             }  finally {
                
                                    adminResourceResolver.close();
                                    sess.logout();  
                                    sess=null;
                                    nodeList = null;
                              }




     %>





    <%!
        // A function which executes the query to return the required list of Nodes.
        List<Node> executeQuery(ResourceResolver resourceResolver, JspWriter out, String propertyName, String templateName, String pageName) throws Exception {

                List<Node> output = new ArrayList<Node>();
        
                // fetches Session 
                Session session = resourceResolver.adaptTo(Session.class);
        
        
                try {
        
        
                    // Create the required query string here.
                    final String queryString = " SELECT * FROM [cq:PageContent] WHERE ISDESCENDANTNODE([/etc]) AND [body] IS NOT NULL AND [cq:template]='/apps/eaton/templates/rawhtmlconfigpage' AND [jcr:title]='"+pageName+"'";

        
                    // Fetching the QueryManager from the session.
                    final QueryManager qm = session.getWorkspace().getQueryManager();
        
                    // Create the required Query here using QueryManager.
                    final Query query = qm.createQuery(queryString, Query.JCR_SQL2);
        
                    // Executing the query here.
                    QueryResult results = query.execute();
        
        
                    if (results != null) {
        
                        // Iterate the results and adding the nodes to the list.
                        NodeIterator nodeIter = results.getNodes();
        
                        while(nodeIter.hasNext()) {
        
                            Node node = nodeIter.nextNode();
        
                            if (node != null) {
        
                                   // Add node to list here.
                                   output.add(node);
        
                            } 
                        }
                    }
        
                }
        
                    catch(Exception exception){  

                       out.write("Exception occurred while executing query "+exception); 
                }  

                //Return list of Nodes here.
                return output;
      }


    %>




    <%!   
        // A function to set the value of property for a node. 
         void setPropertyValueToNodes(List<Node> nodeList, String value, String valueType, JspWriter out, String propertyName, Session session) throws Exception {

        
                ValueFactory vFactory=nodeList.get(0).getSession().getValueFactory();
                Value newValue = null;
        
        
                 if(valueType.equalsIgnoreCase("STRING"))  {
                     newValue=vFactory.createValue(value);
                 } 
        

                  //Check for empty result set.
                if(nodeList.size() == 0) {
                    
                    // Do nothing here.
                }
                else {
                    
                    
                    for (Node node : nodeList) {
                        
                        /*Looping over nodes to delete the property */
                        Property property = node.setProperty(propertyName, newValue);
                        property.setValue(newValue);	
                        property.getSession().save();
                        node.save();
                        
                        
                    }
                    
                    session.save();
                    
                }

     }


     %>
