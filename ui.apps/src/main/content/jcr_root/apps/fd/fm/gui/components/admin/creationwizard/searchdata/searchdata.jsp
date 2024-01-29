
<%--
  ADOBE CONFIDENTIAL

  Copyright 2013 Adobe Systems Incorporated
  All Rights Reserved.

  NOTICE:  All information contained herein is, and remains
  the property of Adobe Systems Incorporated and its suppliers,
  if any.  The intellectual and technical concepts contained
  herein are proprietary to Adobe Systems Incorporated and its
  suppliers and may be covered by U.S. and Foreign Patents,
  patents in process, and are protected by trade secret or copyright law.
  Dissemination of this information or reproduction of this material
  is strictly forbidden unless prior written permission is obtained
  from Adobe Systems Incorporated.
--%><%
%><%@include file="/libs/granite/ui/global.jsp"%><%
%><%@page session="false"
          import="com.adobe.granite.ui.components.Config,
				  com.day.cq.search.PredicateGroup,
				  com.day.cq.search.Predicate,
				  com.day.cq.search.Query,
				  com.day.cq.search.QueryBuilder,
				  com.day.cq.search.result.SearchResult,
				  com.day.cq.search.eval.JcrPropertyPredicateEvaluator,
				  com.day.cq.search.eval.NodenamePredicateEvaluator,
                  com.day.cq.search.eval.PathPredicateEvaluator,
				  com.day.cq.search.eval.FulltextPredicateEvaluator,
				  com.adobe.granite.xss.XSSAPI,
				  javax.jcr.Session,java.util.Map,
				  java.util.HashMap,
				  com.adobe.aem.formsndocuments.util.FMConstants,
				  com.day.cq.search.result.Hit,
				  javax.jcr.Node,javax.jcr.Property,
				  org.apache.jackrabbit.util.Text,
				  org.apache.sling.commons.json.JSONObject,
				  org.apache.sling.commons.json.JSONArray,
				  java.nio.charset.Charset " %>
<%
    Session session = slingRequest.getResourceResolver().adaptTo(Session.class);
    QueryBuilder queryBuilder =  sling.getService(QueryBuilder.class);
    String text = request.getParameter("text");
    String type = request.getParameter("type");
    String formPath = request.getParameter("formPath");
	int sizePerPage = 30;
	int pageNo = Integer.parseInt(request.getParameter("page"));
	int offset = pageNo * sizePerPage;

    JSONObject resultData = new JSONObject();
    response.setContentType(FMConstants.RESPONSE_CONTENT_TYPE_JSON);
    response.setHeader(FMConstants.RESPONSE_CONTENT_TYPE_OPTIONS_KEY, FMConstants.RESPONSE_CONTENT_TYPE_OPTION_NO_SNIFF);

    PredicateGroup finalPredicates = new PredicateGroup();

    Predicate pathPredicate = new Predicate(PathPredicateEvaluator.PATH);
    finalPredicates.add(pathPredicate);

    if(text != null){
        Predicate fulltextPredicate = new Predicate(FulltextPredicateEvaluator.FULLTEXT);
        fulltextPredicate.set(FulltextPredicateEvaluator.FULLTEXT, text);
        finalPredicates.add(fulltextPredicate);
    }

    Predicate propertyPredicate = null;

	String rootpath = (String)application.getAttribute("ROOT_CONTENT_PATH");

    if(type.equals("xdp") || type.equals("dor") || type.equals(FMConstants.FORM_DATA_MODEL)){
        propertyPredicate = createPropertyPredicate(FMConstants.JCR_PRIMARY_TYPE, FMConstants.DAM_ASSET_NODETYPE,null);
        if(propertyPredicate != null)
            finalPredicates.add(propertyPredicate);
        if(type.equals("xdp")) {
            propertyPredicate = createPropertyPredicate(FMConstants.LC_XDP_FORM_PATH,"1",null);
            if(propertyPredicate != null)
                finalPredicates.add(propertyPredicate);
        } else if(type.equals("dor")){
            // In case of DoR, display all XDPs(with extension .xdp) and PDF of type PDFForm and having acroForm as true.
            PredicateGroup dorPredicates = new PredicateGroup();

            PredicateGroup xdpFormPredicates = new PredicateGroup();
            Predicate xdpPredicate = createPropertyPredicate(FMConstants.LC_XDP_FORM_PATH,"1",null);
             if(xdpPredicate != null)
                 xdpFormPredicates.add(xdpPredicate);


            PredicateGroup acroFormPredicates = new PredicateGroup();
            Predicate acroFormPropertyPredicate = createPropertyPredicate(FMConstants.PROPERTYNAME_ACROFORM,"true",null);
            if(acroFormPropertyPredicate != null)
                acroFormPredicates.add(acroFormPropertyPredicate);

            dorPredicates.add(acroFormPredicates);
            dorPredicates.add(xdpFormPredicates);

            dorPredicates.setAllRequired(false);

            finalPredicates.add(dorPredicates);
        } else if(type.equals(FMConstants.FORM_DATA_MODEL)) {
            rootpath = FMConstants.FDM_ROOT_PATH;
            propertyPredicate = createPropertyPredicate(FMConstants.JCR_CONTENT_NODE_NAME + FMConstants.DELIMITER_SLASH + FMConstants.TYPE_PROPERTY,FMConstants.FORM_DATA_MODEL,null);
        }
        if(propertyPredicate != null)
            finalPredicates.add(propertyPredicate);
        propertyPredicate = createMainassetPredicate();
        if(propertyPredicate != null)
            finalPredicates.add(propertyPredicate);
    } else if(type.equals("letter")){
        rootpath = "/content/apps/cm/correspondence/letters";
        propertyPredicate = createPropertyPredicate("lcc:className","com.adobe.icc.dbforms.obj.Letter",null);
        if(propertyPredicate != null)
            finalPredicates.add(propertyPredicate);
    } else if(type.equals("dataDictionary")){
        rootpath = "/content/apps/cm/datadictionaries";
        propertyPredicate = createPropertyPredicate("lcc:className","com.adobe.dct.transfer.DataDictionary",null);
        if(propertyPredicate != null)
            finalPredicates.add(propertyPredicate);
        propertyPredicate = createPropertyPredicate("dictionaryType","NONSYSTEM",null);
        if(propertyPredicate != null)
            finalPredicates.add(propertyPredicate);
        propertyPredicate = createPropertyPredicate("dictionarySubtype",null,JcrPropertyPredicateEvaluator.OP_NOT);
        if(propertyPredicate != null)
            finalPredicates.add(propertyPredicate);
    } else if (type.equals("schema")){
        propertyPredicate = createPropertyPredicate(FMConstants.JCR_PRIMARY_TYPE, FMConstants.DAM_ASSET_NODETYPE, null);
        if(propertyPredicate != null)
            finalPredicates.add(propertyPredicate);
        propertyPredicate = createMainassetPredicate();
        if(propertyPredicate != null)
            finalPredicates.add(propertyPredicate);
        propertyPredicate = createPropertyPredicate(FMConstants.LC_RESOURCE_PATH, "1", null);
        if(propertyPredicate != null)
            finalPredicates.add(propertyPredicate);
        PredicateGroup nodeNamePredicateGroup = new PredicateGroup();
        propertyPredicate = createNodeNamePredicate("*" + FMConstants.XSD_EXTENSION);
        nodeNamePredicateGroup.add(propertyPredicate);
        propertyPredicate = createNodeNamePredicate("*" + FMConstants.JSON_EXTENSION);
        nodeNamePredicateGroup.add(propertyPredicate);
        nodeNamePredicateGroup.setAllRequired(false);
        if(propertyPredicate != null)
            finalPredicates.add(nodeNamePredicateGroup);
    } else {
        // Type is not supported
        resultData.put("result", new JSONArray());
        response.getWriter().write(resultData.toString());
        return;
    }
    pathPredicate.set(PathPredicateEvaluator.PATH, rootpath);
    Query query = queryBuilder.createQuery(finalPredicates, session);
    query.setHitsPerPage(sizePerPage);
    query.setStart(offset);
    SearchResult result = query.getResult();
    JSONArray allMatches = new JSONArray();
	if(result.getTotalMatches()  > 0L) {
	    String dorTemplateRef = "";
	    Node formNode = session.getNode(formPath);
	    if(formNode != null && formNode.hasProperty(FMConstants.AF_ORIGINAL_DOR_TEMPLATE_REF_PROPERTY)) {
            dorTemplateRef = formNode.getProperty(FMConstants.AF_ORIGINAL_DOR_TEMPLATE_REF_PROPERTY).getString();
        }

        for (Hit hit : result.getHits()) {
            String path = hit.getPath();
            Node node = session.getNode(path);
            boolean isAcroForm = node.hasProperty(FMConstants.PROPERTYNAME_ACROFORM) &&
               "true".equals(node.getProperty(FMConstants.PROPERTYNAME_ACROFORM).getString());

            if (isAcroForm && !dorTemplateRef.equals(path)) {
                // If acroform path is not same as dorTemplateRef then don't add in the result.
                // we need to show only one acroForm using which the dorTemplate is created.
                continue;
            }
            String title = "";
            if(node.hasProperty("jcr:content/metadata/title") && node.getProperty("jcr:content/metadata/title").getString().trim().length() > 0){
                title = node.getProperty("jcr:content/metadata/title").getString();
            } else{
                title = node.getName();
            }
            JSONObject eachMatch = new JSONObject();
            eachMatch.put("path", path);
            eachMatch.put("title", title);
            allMatches.put(eachMatch);
        }
    } else if ( Boolean.parseBoolean(request.getParameter("createNewEloquaSchema")) ) {
          if(text != null && type != null && type.equals("schema") && !text.equals("")){
            com.eaton.platform.integration.eloqua.services.EloquaAemFormCustmJsonService eloquaAemFormCustmJsonService = sling.getService(com.eaton.platform.integration.eloqua.services.EloquaAemFormCustmJsonService.class);
            String custmJsonPath = eloquaAemFormCustmJsonService.createAndGetEloquaFormSchemaPath(text);
            JSONObject eachMatch = new JSONObject();
            if(custmJsonPath != null){
            eachMatch.put("path", custmJsonPath);
            eachMatch.put("title", text+".schema.json");
            }
              allMatches.put(eachMatch);
        }
    } else{
        if(text != null && type != null && type.equals("schema") && !text.equals("")){
            JSONObject eachMatch = new JSONObject();
            eachMatch.put("eloquaresponse", "noExistingEloquaSchemaJson");
            allMatches.put(eachMatch);
        }
	}
    resultData.put("result", allMatches);
    // NOCHECKMARX - Setting the correct content type and nosniff as content type options avoids XSS attacks. Also, the functions above are working with JSONObject & JSONArray.
    response.getWriter().write(resultData.toString());
%>

<%!

    Predicate createPropertyPredicate(String property , String value , String operation) {
        if(property == null)
            return null;
        Predicate predicate = new Predicate(JcrPropertyPredicateEvaluator.PROPERTY);
        predicate.set(JcrPropertyPredicateEvaluator.PROPERTY, property);
        if(value != null)
            predicate.set(JcrPropertyPredicateEvaluator.VALUE, value);
        if(operation != null)
            predicate.set(JcrPropertyPredicateEvaluator.OPERATION,operation);
        return predicate;
    }

    Predicate createNodeNamePredicate(String nodeName) {
        if(nodeName == null)
            return null;
        Predicate predicate = new Predicate(NodenamePredicateEvaluator.NODENAME);
        predicate.set(NodenamePredicateEvaluator.NODENAME, nodeName);
        return predicate;
    }

    Predicate createMainassetPredicate(){
        Predicate predicate = new Predicate("mainasset");
        predicate.set("mainasset", "mainasset");
        predicate.set("mainasset", "true");
        return predicate;
    }

%>
