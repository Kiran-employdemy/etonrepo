<%@ page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@include file="/libs/foundation/global.jsp" %>
<%
String[] scripts = properties.get("scripts", String[].class);
String[] styles = properties.get("css", String[].class);
String pageTitle = currentPage.getTitle();

%>

<form id="rawhtmlconfig"  method="POST" action="${resource.path}.form.html" enctype="multipart/form-data" >
    
    <div id="htmlfragment">

        <div id="tabs">
            <ul>
                <li><a href="#tabs-1">Preview</a></li>
                <li><a href="#tabs-2">Source / Syntax Validation</a></li>
            </ul>
            <div id="tabs-1"  style="height:350px;width:90%; border:solid 2px white;overflow:scroll;overflow-x:scroll;overflow-y:scroll;">
                <%
                    if(scripts!=null){
                    for(String script : scripts){
                        out.write("<script src=\"" + script + "\"></script>");
                    }
                 }

                    if(styles!=null){
                        for(String style : styles){
                            out.write("<link rel=\"stylesheet\" href=\"" + style + "\" type=\"text/css\" >");
                        }
                    }
                    
                    out.write("\r\n");
                    out.write(properties.get("body",""));
                    

                    %>
                
            </div>
            <div id="tabs-2">
                <pre id="htmlSource">
                <%
                out.write("\r\n");
                out.write(StringEscapeUtils.escapeHtml(properties.get("body","")));
                %>
                </pre>
                
              <div id="hiddeninputs">
                    <input type="hidden" name="aceeditorval" style="display: none;">
                    <input type="hidden" name="pagetitle" value="<%=pageTitle%>" style="display: none;">
                    <input type="hidden" name="pagename"  value="${currentPage.name}" style="display: none;">
                    <div>
                        <input type="button" value="Save" name="save" id="save"  onclick="javascript:submitform()" style="height:30px; width:100px" />
                    </div>    
                    
                </div>
                
            </div>
        </div>
        
    </div>

    
</form>


<style type="text/css" media="screen">
    #htmlSource { 
        margin: 0;
        height: 350px;
        width: 90%
    }
   .ui-tabs .ui-tabs-nav li a{width:100%; outline:none;}
</style>

<script>

    var $input = $('input[name="aceeditorval"]');
    var $pagetitle = $('input[name="pagetitle"]');
    var $pagename = $('input[name="pagename"]');
    
    
    
    $(function() {


        $( "#tabs" ).tabs();
        var editor = ace.edit("htmlSource");
        editor.setTheme("ace/theme/dreamweaver");
        editor.getSession().setMode("ace/mode/html");

        editor.setShowPrintMargin(false);

        editor.setOptions({
            autoScrollEditorIntoView: true
        });
        
        
        //Initialize hidden variable with content of ace editor.
        $input.val(editor.getSession().getValue());
        
        
        //Handler to update the content of the editor.
        editor.getSession().on("change", function () {
            
            $input.val(editor.getSession().getValue());
            
            $input.prev('input[type=hidden]').val($input.val());
            
        });
        
        
        
        
    });
    
    

    // Submitting the form.
    function submitform(){
        
        var str = $input.val();
        
        $input.val(str.trim());


        $("#rawhtmlconfig").submit();
    }


    
    //Callback handler for form submit event
    $("#rawhtmlconfig").submit(function(e)
                                    {


                                        var formObj = $(this);
                                        var formURL = formObj.attr("action");
                                        var formData = new FormData(this);


                                        $.ajax({
                                            url: formURL,
                                            type: 'POST',
                                            data:  formData,
                                            mimeType:"multipart/form-data",
                                            contentType: false,
                                            cache: false,
                                            processData:false,
                                            success: function(data, textStatus, jqXHR)
                                            {
                                                CQ.Ext.Msg.minWidth = 400;
      											CQ.Ext.Msg.minProgressWidth = 300;
   											    CQ.Ext.Msg.progress('Raw HTML','','');
                                                CQ.Ext.Msg.updateProgress(1,'  Saved ','Reloading page...');
                                                
                                                window.location.reload(true);
                                            },
                                            error: function(jqXHR, textStatus, errorThrown) 
                                            {
                                                 CQ.Ext.Msg.alert('Status',"An error occurred during save operation. Response Code: "+jqXHR.status);
                                            }          
                                        });
                                        e.preventDefault(); //Prevent Default action. 
                                        e.unbind();
                                    }); 
    

</script>