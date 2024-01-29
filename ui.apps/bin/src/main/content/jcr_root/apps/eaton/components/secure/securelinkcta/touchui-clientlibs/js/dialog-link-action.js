(function (document, $) {
    $(document).on('dialog-ready', function() {
        var secureLinkDialog = document.querySelector('form.cq-dialog .secure-link-dialog');
        $($(".secure-link-properties-tab .coral-Form-fieldwrapper")[0]).hide();
        var props = secureLinkDialog.getElementsByClassName("secure-link-properties-tab")[0];
        $($(".secure-link-path-config .coral3-Textfield")[0]).focusout(function() {
            fetchProperties(secureLinkDialog,props);
        });
        fetchProperties(secureLinkDialog,props);
    });

    function checkForInternalUrl(url) {
        if(url.length>0) {
            if (url.startsWith('/content/') || url.indexOf('content/') >= 0 || url.indexOf('content/dam/') >= 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    function makeReadOnly(properties){
        for(var i=0;i<properties.getElementsByClassName("coral3-Textfield").length;i++){
            properties.getElementsByClassName("coral3-Textfield")[i].disabled = 'true';
            properties.getElementsByClassName("coral3-Button")[i].disabled = 'true';
        }
    }
    function removeReadOnly(properties){
        $(".secure-link-isexternal").parent().show();
        for(var i=0;i<properties.getElementsByClassName("coral3-Textfield").length;i++){
            properties.getElementsByClassName("coral3-Textfield")[i].removeAttribute("disabled");
            properties.getElementsByClassName("coral3-Button")[i].removeAttribute("disabled");
        }
        $(".secure-link-properties-tab coral-taglist").html("");
    }

    function fetchProperties(secureLinkDialog, props){
        if(secureLinkDialog.getElementsByClassName("coral3-Tag").length > 0){
            var confVal = secureLinkDialog.getElementsByClassName("coral3-Tag")[0].innerText.trim();
            if(confVal){
                if(checkForInternalUrl(confVal)){
                    callToGetProps(confVal);
                    makeReadOnly(props);
                    $(".secure-link-isexternal").parent().hide();
                }
            }else{
                removeReadOnly(props);
            }
        }else{
            removeReadOnly(props);
        }
    }

    function iterateResponseData(tagHtmlOpen,secureProperty,tagHtmlClose){
        finalHtml = "";
        for(var i=0;i<secureProperty.length;i++){
            finalHtml += tagHtmlOpen + secureProperty[i] + tagHtmlClose;
        }
        return finalHtml;
    }

    function callToGetProps(confUrl){
        var url = "/eaton/authoring/secure/attribute-tags";
        if(confUrl.includes("content/dam/")){
            url += ".asset.json";
        }else{
            url += ".page.json";
        }
        $.ajax({
            url: url,
            data: {
                configuredUrl: confUrl
            },
            headers: {
                'Content-Type': 'application/json'
            },
            type: 'GET',
            success: function(data) {
                var tagHtmlOpen = '<coral-tag class="coral3-Tag coral3-Tag--multiline coral3-Tag--large" role="option" aria-selected="false"><coral-tag-label>';
                var tagHtmlClose = '</coral-tag-label></coral-tag>';
                if (data) {
                    for(var i=0;i<$(".secure-link-properties-tab coral-taglist").length;i++){
                        var secureConfs = [data.accountType, data.businessGroup, data.productCategories, data.applicationAccess, data.countries, data.partnerProgrammeType, data.tierlevel ];
                        if(secureConfs[i].length > 0){
                            var appendHtml = iterateResponseData(tagHtmlOpen,secureConfs[i],tagHtmlClose);
                            $($(".secure-link-properties-tab coral-taglist")[i]).html(appendHtml);
                        }
                    }
                }
            },
            error: function(data){
                console.log("ERROR : Couldnt get expected response. Response : "+data);
            }
        });
    }
})(document, Granite.$);
