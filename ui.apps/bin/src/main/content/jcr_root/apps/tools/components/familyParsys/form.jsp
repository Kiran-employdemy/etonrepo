<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %><%
%><%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %><%
%><cq:defineObjects />
<%@include file="/libs/foundation/global.jsp"%>
<cq:includeClientLib js="granite.csrf.standalone"/>

<%@page session="false" %>

<html>

<head>
    <title>Adding parsys</title>
    <meta http-equiv="Content-Type" content="text/html; utf-8" />
    <script src="/libs/cq/ui/resources/cq-ui.js" type="text/javascript"></script>
    <script type="text/javascript">
    function submitformfamily(){

       document.familyparsysform.submit();
    }  


    </script>

</head>

            <form name="familyparsysform" method="post">
                <h1>Family inherited Parsys</h1>
                </br>
                Path under which family pages are present : <input type="text" id="familyParsys" name="familyParsys" size="50"/>
    			<select id="dropdownOption" name="dropdownOption">
  <option value="Remove">Remove</option>
  <option value="Add">Add</option>
  <option value="Remove old parsys">Remove old parsys</option>

</select>
                </br></br>
                <input type="button" value="submit" onclick="javascript:submitformfamily()" />

            </form>

</html>