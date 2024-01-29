if(window.location.href.indexOf('/content/dam/formsanddocuments') != -1){
    $(document).on("foundation-contentloaded", function(e) {
         $('input[name="searchtext"]').bind("keypress", function (e) {
            if (e.keyCode == 13 && $(this).attr("assettype") == 'schema') {
                var inputField = $(this);
                var searchText = inputField.val();
                var type = inputField.attr("assettype");
                var searchUrl = "/libs/" + inputField.attr("searchUrl");
                $.ajax({
                    url: Granite.HTTP.externalize(searchUrl + "?type=" + type + "&text=" + encodeURIComponent(searchText) + "&page=" + encodeURIComponent(0)),
                    success: function(data){
                        var ihtml = '';
                        var searchResultArray = data.result;
                        if(type == "schema" && data.result[0].eloquaresponse == "noExistingEloquaSchemaJson"
                          && confirm("There is no existing schema with form id "+searchText +". If you want to create this schema now, press Ok else press Cancel?")){
                             createEloquaFormSchema(inputField);
                        } else {
                            for (var i=0; i < searchResultArray.length; i++) {
                                ihtml = ihtml + '<li class="result" title=' + searchResultArray[i].path + '><div class="listing"><coral-icon icon="form" size="L"></coral-icon><div>' + searchResultArray[i].title + '</div></li>';
                            }
                            inputField.closest(".search-pane").find("ul").html(ihtml);
                        }
                        }
                    });
                 e.preventDefault();
            }

        });
    });
    function createEloquaFormSchema(inputField){
        var searchText = inputField.val();
         var type = inputField.attr("assettype");
        var createNewEloquaSchema = true;
        var searchUrl = "/libs/" + inputField.attr("searchUrl");
        $.ajax({
            url: Granite.HTTP.externalize(searchUrl + "?type=" + type + "&text=" + encodeURIComponent(searchText) + "&page=" + encodeURIComponent(0)) +"&createNewEloquaSchema=" +createNewEloquaSchema,
            success: function(data){
            	var ihtml = '';	
            	var searchResultArray = data.result;
            if($.isEmptyObject(data.result[0])){
              alert(searchText+" is not valid eloqua form. Please ensure to search the correct eloqa form id.");
              }
                for (var i=0; i < searchResultArray.length; i++) {
                    ihtml = ihtml + '<li class="result" title=' + searchResultArray[i].path + '><div class="listing"><coral-icon icon="form" size="L"></coral-icon><div>' + searchResultArray[i].title + '</div></li>';
            	}
                inputField.closest(".search-pane").find("ul").html(ihtml);
        	}
        });
    }
}




