<%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" contentType="text/html" pageEncoding="utf-8"
          import="javax.jcr.*,
            javax.jcr.query.InvalidQueryException,
            javax.jcr.query.Query,
            javax.jcr.query.QueryResult,
            javax.jcr.RepositoryException,
            java.util.ArrayList,
            java.util.HashMap,
            java.util.List, 
            org.apache.sling.api.resource.Resource,
            javax.jcr.Property,
            javax.jcr.Node,                        
            org.apache.sling.api.resource.ResourceResolver,
            org.apache.sling.api.resource.ResourceResolverFactory,
			org.apache.sling.api.scripting.SlingScriptHelper,
            javax.jcr.Session,

			com.eaton.platform.core.services.AdminService,
			java.util.Date"%>
<html>

<head>
    <title>Result page</title>
    <meta http-equiv="Content-Type" content="text/html; utf-8" />
    <script src="/libs/cq/ui/resources/cq-ui.js" type="text/javascript"></script>
</head>
<body>

<%


	List<Node> generatedNodeList = new ArrayList<Node>();
	List<Node> successNodeList = new ArrayList<Node>();
	List<Node> rootFailedNodeList = new ArrayList<Node>();
	List<Node> parsysFailedNodeList = new ArrayList<Node>();
	List<Node> successRemoveNodeList = new ArrayList<Node>();
	

 String searchPath = null;
String stmt = null;
	Node promoNode = null;
	int count = 0 ;
	int listSize = 0;
	int removeCount = 0 ;

    try{    
        searchPath = request.getParameter("familyParsys");
		String dropdownOption = request.getParameter("dropdownOption");
		
        %><br><div style="margin-bottom:25px;">                         
<p style="float:left;width:auto"> Fetching the family pages under the path :: </p> <h4> <%= searchPath %> </h4> 

    </div>
 <%
/*        com.eaton.platform.core.services.AdminService adminService = sling.getService(com.eaton.platform.core.services.AdminService.class);
		resourceResolver = adminService.getWriteService(); */
		Session adminSession = resourceResolver.adaptTo(Session.class);
		generatedNodeList = getList(resourceResolver,searchPath);

        listSize = generatedNodeList.size();
        %> <div style="margin-bottom:25px;clear:both">                         
<p style="float:left;width:auto"> Number of family pages fetched :: </p> <h4> <%= listSize %> </h4>
    </div> <%

        	for(Node pageJCRNode : generatedNodeList){ 

                Boolean flag = pageJCRNode.hasNode("root");

                if(flag){
                   	Node rootNode = pageJCRNode.getNode("root");
					Boolean innerFlag = rootNode.hasNode("famiyinheritedparsys");
					if(rootNode!=null){
						
					Node responsiveGrid = rootNode.getNode("responsivegrid");
					if(responsiveGrid != null){
						Node productTabNode = responsiveGrid.getNode("product_tabs");
						
						if(productTabNode != null){
							Node productNode = productTabNode.getNode("content-tab-1");
							
						if(productNode != null){
							Boolean inheritanceExists = productNode.hasNode("family_inherited_pars");
							
							if(!inheritanceExists && dropdownOption.equals("Add")){
								Node familynode = productNode.addNode("family_inherited_pars", "nt:unstructured");
		                        familynode.setProperty("sling:resourceType", "eaton/components/product/family-inherited-parsys");
		                        Node parsysNode = familynode.addNode("familyInherited","nt:unstructured");
		                        parsysNode.setProperty("sling:resourceType","wcm/foundation/components/parsys");
								adminSession.save();
		                        successNodeList.add(rootNode);
								count++;
							}else if(inheritanceExists && dropdownOption.equals("Add")){
								parsysFailedNodeList.add(rootNode);
							}else if(inheritanceExists && dropdownOption.equals("Remove")){
								Node deleteNode = productNode.getNode("family_inherited_pars");
								deleteNode.remove();
								adminSession.save();
								successRemoveNodeList.add(rootNode);
								removeCount++;
		                    } 
						}
						}
					}
					}
                   
					if(innerFlag && dropdownOption.equals("Remove old parsys")){
						Node deleteNode = rootNode.getNode("famiyinheritedparsys");
						deleteNode.remove();
						adminSession.save();
						successRemoveNodeList.add(rootNode);
						removeCount++;
                    } 
                }
            else if(!flag){
            	rootFailedNodeList.add(pageJCRNode);
            }
	        }

       if(dropdownOption.equals("Add")){ %><div style="margin-bottom:25px;clear:both">                         
<p style="float:left;width:auto"> Number of family pages to which parsys added :: </p> <h4> <%=count %> </h4> 
    </div>

<div style="margin-bottom:25px;clear:both">                         
<p style="float:left;width:auto"> Number of family pages to which parsys NOT added :: </p> <h4> <%= listSize - count %> </h4> 
    </div>
<%


  //successNodeList iterations
    if(successNodeList.size() > 0){
    	%><p style="clear:both"> Family pages to which parsys added :: </p><%
    	int nodeCount = 1;
    for(Node tempList : successNodeList){
    %><div>                         
     <h4 style="padding-left: 60px;"> <%= nodeCount++ %> <%= tempList.getParent().getParent().getPath() %> </h4>  
        </div><%}  %> <br> <%
    }

  //rootFailedNodeList iterations
    if(rootFailedNodeList.size() > 0){
    	%><p style="clear:both"> Page does not contain root node :: </p><%
    	int nodeCount = 1;
    for(Node tempList : rootFailedNodeList){
    %><div>                         
     <h4 style="padding-left: 60px;"> <%= nodeCount++ %> <%= tempList.getParent().getParent().getPath() %> </h4>  
        </div><%} %> <br> <%
    }

            //parsysFailedNodeList iterations
    if(parsysFailedNodeList.size() > 0){
    	%><p style="clear:both"> Page already contains familyinheritedparsys node :: </p><%
    	int nodeCount = 1;
    for(Node tempList : parsysFailedNodeList){
    %><div>                         
     <h4 style="padding-left: 60px;"> <%= nodeCount++ %> <%= tempList.getParent().getParent().getPath() %> </h4>  
        </div><%} %> <br> <%
    }



	   }
	 if(dropdownOption.equals("Remove")){
	%><div style="margin-bottom:25px;clear:both">                         
<p style="float:left;width:auto"> Number of family pages to which parsys removed :: </p> <h4> <%=removeCount %> </h4> 
    </div><%
			if(successRemoveNodeList.size() > 0){
    	%><p style="clear:both"> Family pages to which parsys removed :: </p><%
    	int nodeCount = 1;
    for(Node tempList : successRemoveNodeList){
    %><div>                         
     <h4 style="padding-left: 60px;"> <%= nodeCount++ %> <%= tempList.getParent().getParent().getPath() %> </h4>  
        </div><%}  %> <br> <%
    }
	 }  
	   }
     catch(Exception e) {
        log.info("Exception occured ------ " + e.toString());
    } 
    %>

<%!
    
    List<Node> getList(ResourceResolver resourceResolver, String searchPath)
    {          

	
    	// Query statement
   	String stmt = "select * from [nt:base] as s where [cq:template] = '/conf/eaton/settings/wcm/templates/product-family-page' and  ISDESCENDANTNODE(s,'" + searchPath + "')";

        Session session = resourceResolver.adaptTo(Session.class);
    
        List<Node> newNodeList = null;

    
        try {
            Query query = session.getWorkspace().getQueryManager().createQuery(stmt, Query.JCR_SQL2);
            QueryResult results = query.execute();

                if (results != null) {
                    NodeIterator it = results.getNodes();
                    newNodeList = new ArrayList<Node>();
    
                    while (it.hasNext()) {

                    	newNodeList.add(it.nextNode());    
                    }
    


    
                }
        } catch (InvalidQueryException e) {
        } catch (RepositoryException e) {
        }

    return newNodeList;
    }

%>



</body>
</html>
