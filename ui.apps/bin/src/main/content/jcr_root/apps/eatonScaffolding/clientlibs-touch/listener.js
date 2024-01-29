	/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * ___________________
 *
 *  Copyright 2013 Adobe Systems Incorporated
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 **************************************************************************/
/* ==========================================================================================
 * jQuery-based listeners (Touch-optimized UI) testing 3 write
 * ==========================================================================================
 */
(function($, $document) {
    "use strict";

    $(document).ready(function() {
        var pagePath = window.location.pathname;
        var duplicate = pagePath.includes("duplicate");
        var itemsToLoad;
		
		  $("input[name='./spinImages']").change(function(){
			var imgCheckPath = $("input[name='./spinImages']").val();
			if( imgCheckPath.includes("https://eaton.sirv.com") === false && imgCheckPath !== ''){
				var message = "URL must Begins with: https://eaton.sirv.com";
                $(window).adaptTo("foundation-ui").notify('', message, 'notice');
                $('#shell-propertiespage-doneactivator').attr('disabled','disabled');
            } else {
				$('#shell-propertiespage-doneactivator').attr('disabled',false);
            }
			
		})

		function globalcheck() {
            var path=$("input[name='./primaryCTAURL']").val();
            var selector="/content/eaton";
             if($("input[name='./primaryCTAMakeGlobal']").is(":checked") && !(path.startsWith(selector))){
                  document.getElementsByName("./primaryCTAURL")[0].value = ""
                  var message = "URL must be an Eaton web page when Make this global checkbox is selected";
                  $(window).adaptTo("foundation-ui").notify('', message, 'notice');
                  $("[type='submit']").prop("disabled",true);
                  $("[name='./primaryCTAURL'] .coral3-Textfield").addClass('is-invalid');
            } else {
				$("[type='submit']").prop("disabled",false);
				$("[name='./primaryCTAURL'] .coral3-Textfield").removeClass('is-invalid');
				}
            }

		$("[name='./primaryCTAURL']").change(function(e) {
			globalcheck();
		})

		$("[name='./primaryCTAMakeGlobal']").click(function(e){
			globalcheck();
		})

		$(".coral-Form-field #makeglobal").click(function(){
			var leng = $(".coral-Form-field #makeglobal").length;
			for(var i=0;i<=leng;i++){
				var howbuylink ='./howToBuy/item'+i+'/./howtoBuyLink';
				var a ='./howToBuy/item'+i+'/./additionalCTAMakeGlobal';
				var b = './howToBuy/item'+i+'/./countries';
				if($("[name='"+a+"']").is(":checked")){
				  $("[name='"+b+"']").prop( "disabled", true );
				  linkCheck();
				  break;
				} else {
				  $("[name='"+b+"']").prop( "disabled", false );
				  $("[type='submit']").prop("disabled",false);
				  $("[name='"+ howbuylink +"']").removeClass('is-invalid');
				}
			}
		 })

		$('.coral-Form-fieldwrapper #countrysel').click( function(){
			 var leng = $(".coral-Form-fieldwrapper #countrysel").length;
			 for(var i=0;i<leng;i++){
				var b = './howToBuy/item'+i+'/./countries';
				var a='./howToBuy/item'+i+'/./additionalCTAMakeGlobal';
				if($("[name='"+b+"'] option:selected").text() != ""){
					$("[name='"+a+"']").prop("disabled",true);
				}else  {
					$("[name='"+a+"']").prop("disabled",false);
				}
			 }
		})

		 $("#linking").change(function(){
			linkCheck();
        })

		function linkCheck(){
            var selector="/content/eaton";
			var len = $(".coral-Form-fieldwrapper #linking").length;
            for(var i=0; i<len; i++){
				var link = './howToBuy/item'+i+'/./howtoBuyLink';
				var linkval = $("[name='"+link+"']").val();
                var global = './howToBuy/item'+i+'/./additionalCTAMakeGlobal';
                if($("input[name='"+global+"']").is(":checked") && (!(linkval.startsWith(selector))) ){
				  var message = "URL must be an Eaton web page when Make this global checkbox is selected";
                  $(window).adaptTo("foundation-ui").notify('', message, 'notice');
                  $("[type='submit']").prop("disabled",true);
                  $("[name='"+ link +"']").addClass('is-invalid');
				  break;
                }else {
				  $("[type='submit']").prop("disabled",false);
				  $("[name='"+ link +"']").removeClass('is-invalid');
                }
        	}
        }


        $('.foundation-layout-inline2-item').attr('disabled',false);
        $('body').attr('id', 'cq-commerce-products-createproduct');

        $.fn.PDHField = function(){
            var prodId = $("[name='./pdhRecordPath']").val();
            var action = 'onLoad';
			if(prodId !== ''  ) {
				$("input[name='./spinImages']"). attr('disabled','disabled');
				$("input[name='./spinImages']").next('.coral-InputGroup-button').children().attr('disabled','disabled');
			} else {
				$("input[name='./spinImages']").prop('disabled', false);
				$("input[name='./spinImages']").next('.coral-InputGroup-button').children().prop('disabled', false);
			}
            if (prodId) {
                $.ajax({
                    type: 'GET',
                    url: '/eaton/productfamilypagedata',
                    async: false,
                    data: {
                        prodId: prodId,
                        action: action
                    },
                    success: function (msg) {
                        var productName = $("input[name='./productName']");
                        if (msg.pdhTitle != null && msg.pdhTitle.length != 0) {
                            productName.removeAttr('aria-required');
                        }
                        $("input[name='./jcr:title']").val(msg.extnId);
                        checkExtensionIdValidity();
                        if(!productName.val()) {
                            productName.val(msg.pdhTitle);
                        }
                        if (!duplicate) {
                            $("input[name='./pdhProdName']").val(msg.pdhTitle);
                            $("textarea[name='./pdhProdDesc']").val(msg.pdhMarDesc);
                            $("input[name='./pdhPrimaryImg']").val(msg.primaryImgName);
                            $("textarea[name='./pdhCoreFeatures']").val(msg.coreFeatures);
                            $("textarea[name='./pdhSupportInfo']").val(msg.supportInfo);
                            $("input[name='./inventoryID']").val(msg.inventoryID);
							$("input[name='./spinImage']").val(msg.spinImage);
                        } else {
                            clearPDHData();
                        }
                        $("#cq-commerce-products-createproduct form").attr('action', msg.action);
                    },
                    error: function (msg) {
                        alert("PDH Node doesnot exists");
                    }
                });
            }
        }


        $("[name='./pdhRecordPath']").change(function(e) {
			console.log("on pdh record path browser change :::");
            var prodId = $("[name='./pdhRecordPath']").val();
            var action = 'pathBrowserChange';

            if (prodId.length == 0) {
                $("input[name='./pdhProdName'], input[name='./jcr:title'], textarea[name='./pdhProdDesc'], input[name='./pdhPrimaryImg'], input[name='./identifier'], textarea[name='./pdhCoreFeatures'], textarea[name='./pdhSupportInfo'] ").val('');
            } else {

                    $.ajax({
                        type: 'GET',
                        url: '/eaton/productfamilypagedata',
                        async: false,
                        data: {
                            prodId: prodId,
                            action: action
                        },
                        success: function(msg) {
                            $("input[name='./jcr:title']").val(msg.extnId);
                            checkExtensionIdValidity();
                            $("#cq-commerce-products-createproduct form").attr('action',msg.action);
                            $("input[name='./pdhProdName']").val(msg.pdhTitle);
                            $("textarea[name='./pdhProdDesc']").val(msg.pdhMarDesc);
                            $("input[name='./pdhPrimaryImg']").val(msg.primaryImgName);
                            $("textarea[name='./pdhCoreFeatures']").val(msg.coreFeatures);
                            $("textarea[name='./pdhSupportInfo']").val(msg.supportInfo);
                            $("input[name='./inventoryID']").val(msg.inventoryID);
							$("input[name='./spinImage']").val(msg.spinImage);
                        }
                    });
                }

           return true;
		 });

        function clearPDHData() {
            var extensionID = $("input[name='./jcr:title']").val();
            $("input[name='./jcr:title']").removeAttr('aria-required');
            $("input[name='./jcr:title']").removeAttr('data-validation');
            $("input[name='./jcr:title']").val("")
            $(".granite-title").text('Create Product');
            $("#shell-propertiespage-saveactivator").text('Create');
            $("input[name='./pdhProdName']").val("");
            $("textarea[name='./pdhProdDesc']").val("");
            $("input[name='./pdhPrimaryImg']").val("");
            $("textarea[name='./pdhCoreFeatures']").val("");
            $("textarea[name='./pdhSupportInfo']").val("");
            document.getElementsByName("./pdhRecordPath")[0].value = ""
        }


        setTimeout(function () {
            if (duplicate) {
                clearPDHData();
            }
        }, 10);

        $('button[type="submit"]').click(function (e) {
            if ($('.lazyload').length > 0) {
                itemsToLoad = $('.lazyload').length;
                e.preventDefault();
                $('.lazyload').trigger('appear');
                var count=0;
                $(document).on("foundation-contentloaded", function(e) {
                    count++;
                    if ($('.lazyload').length == 0 && itemsToLoad == count) {
                        $('button[type="submit"]').click();
                    }
                });
            } else {
                ui.wait();
                var pagePath = window.location.pathname;
                var duplicate = pagePath.includes("duplicate");
                if (!duplicate) {
                    var buttonAction = $(this).text();
                    var newPDHPath = $("[name='./pdhRecordPath']").val();
                    var existingPDHPath = $("[name='./pdhRecordPath']").attr('value');
                    var path = $("#cq-commerce-products-createproduct form").attr('action');
                    var url = decodeURIComponent($(location).attr('href'));

                    if (!existingPDHPath) {
                        var oldID = $.trim($(".granite-title").text());
                    }
                    if (newPDHPath.length == 0) {
                        var newID = $("input[name='./jcr:title']").val();
                    }


                $.ajax({
                    type: 'GET',
                    url: '/eaton/productfamilypagedata',
                    async: false,
                    data: {
                        prodId: newPDHPath,
                        existingPDHPath: existingPDHPath,
                        oldID: oldID,
                        newID: newID,
                        path: path,
                        action: buttonAction,
                        url: url
                    },
                    success: function(msg) {if(!($("input[name='./jcr:title']").val())) {
                            $("input[name='./jcr:title']").val(msg.extnId);
                            checkExtensionIdValidity();
                            ui.clearWait();
                        }

                    if(msg.nodeRenameSuccess == "true"){
                    	alert("PDH Path (Extension ID) changed.  You must reload the PIM node to continue.  Please click OK, then click Save, and then reopen the PIM node.");
                    }
                   $("#cq-commerce-products-createproduct form").attr('action',msg.action);

                    return true;},
                    error: function(msg){
                        ui.clearWait();
                        alert("PIM Node already exists, please click Cancel/Close and edit existing PIM node to update content");
                        $('button[type="submit"]').attr('disabled','true');
                    }
                });
                $('button[type="submit"]').off().click();
                }
            }
        });

        window.addEventListener('DOMContentLoaded', (event) => {
             var isproductPropertiesPage = $('.cq-commerce-products-properties').length > 0;
             if(isproductPropertiesPage){
                 setSaveAndCloseActivatorHref();
                 $('.lazyload').lazyload({
                     trigger: 'appear'
                 });
                 $.fn.PDHField();
                 setTaxonomyAttributeListeners();
             }
        });

    });

    if(window.location.pathname.includes("duplicate")) {
        $(document).on("foundation-contentloaded", function (e) {
            var createProductWizard = $(".cq-commerce-products-properties", e.target);
            if (createProductWizard.length) {
                createProductWizard.on("submit.cq-commerce-products-properties", function (e) {
                    e.preventDefault();

                    e.stopImmediatePropagation(); // FIX JIRA CQ-42231414
                    if (window.location.pathname.includes("duplicate")) {
                        CreateDuplicateProduct();
                    }
                });
            }
        });
    }

    function CreateDuplicateProduct() {
        var extensionID = $("input[name='./jcr:title']").val();
        if (extensionID === "") {
            var date = new Date().YYYYMMDDHHMMSS();
			var newExtensionId = "aem".concat(date);
            $("input[name='./jcr:title']").val(newExtensionId);
            checkExtensionIdValidity();
        }
        var actionPath = $("#cq-commerce-products-createproduct form").attr('action');
        actionPath = actionPath.substring(0, actionPath.lastIndexOf('/')) + "/*";
        $("#cq-commerce-products-createproduct form").append('<input name="./cq:commerceType" value="product" type="hidden"><input name="./sling:resourceType" value="commerce/components/product" type="hidden"><input name="./jcr:mixinTypes" value="cq:Taggable" type="hidden"><input name="./jcr:primaryType" value="nt:unstructured" type="hidden"><input name="./jcr:mixinTypes@TypeHint" value="String[]" type="hidden"><input type="hidden" name="_charset_" value="utf-8">');
        var filteredFormData = $("#cq-commerce-products-createproduct form").serializeArray().filter(function (element) {
            return element.name.indexOf("${") == -1;
        });

        // Sometimes there will be more than one sling:resourceTypes in the serialized array and we need to make sure there is only one listed.
        var resourceType = filteredFormData.find(function(prop) {
            return prop.name === './sling:resourceType';
        });
        filteredFormData = filteredFormData.filter(function(prop) {
            return prop.name !== './sling:resourceType';
        });
        filteredFormData.push(resourceType);
        Granite.$.ajax({
            type: "POST",
            url: actionPath,
            contentType: "application/x-www-form-urlencoded",
            data: $.param(filteredFormData),
            success: function (data, status, request) {
                var response = _g.shared.HTTP.buildPostResponseFromHTML(request.responseText),
                    productPath = response.headers.Path,
                    responseStatus = response.headers["Status"];
                // send an event to create the product assets
                $(document).trigger("cq-commerce-product-assets-create", {productPath: productPath});
                if (status === 'success') {
                    var redirectTo = $('#shell-propertiespage-closeactivator').attr("href");
                    if (redirectTo) {
                        //when the product assets have been created and marker request is sent, go to the redirect page
                        $(document).ajaxStop(function () {
                            document.location.href = Granite.HTTP.externalize(redirectTo);
                        });
                    }
                } else {
                    _onError(response.headers["Message"]);
                }
            },
            error: function (xhr, textStatus, errorThrown) {
                var response = _g.shared.HTTP.buildPostResponseFromHTML(xhr.responseText);
                _onError(response.headers["Message"]);
            }
        });
    }

    Object.defineProperty(Date.prototype, 'YYYYMMDDHHMMSS', {
    value: function() {
        function pad2(n) {  // always returns a string
            return (n < 10 ? '0' : '') + n;
        }
        return this.getFullYear() +
               pad2(this.getMonth() + 1) +
               pad2(this.getDate()) +
               pad2(this.getHours()) +
               pad2(this.getMinutes()) +
               pad2(this.getSeconds());
    }
	});

  function checkExtensionIdValidity() {
    // Coral UI 2's validation requires this check to be delayed.
    setTimeout(function() {
      $(".eaton-scaffold-aem-tab-extn-id").checkValidity()
    }, 1000);
  }

  function setSaveAndCloseActivatorHref() {
       var redirectTo = "/libs/commerce/gui/content/products.html/var/commerce/products/eaton";
       if(location.search.includes("context=pimpage")){
   		$("#shell-propertiespage-closeactivator").attr("href",Granite.HTTP.externalize(redirectTo));
   		$("#shell-propertiespage-doneactivator").attr("data-granite-form-saveactivator-href",Granite.HTTP.externalize(redirectTo));
       }
  }

  function setTaxonomyAttributeListeners(){
       const taxonomyMultiField = $("coral-multifield[data-granite-coral-multifield-name='./taxonomyAttributes']");

       //Event delegation binding listener to the element that represents the entire Taxonomy Attribute multifield
       taxonomyMultiField.on("click change", "coral-select", function(event){
        const selectedField = $(this).closest("coral-multifield-item");
        const siblingFields = selectedField.siblings("coral-multifield-item");

        //Change event handler
        if (event.type === "change" && !(siblingFields.length === 0)){
            const previousValue = selectedField.find("coral-selectlist-item.is-selected").attr("value");
            if((typeof previousValue !== "undefined")){
                siblingFields.each(function(){
                    $(this).find(`coral-selectlist-item[value=${previousValue}]`).show();
                });
            }

        //Click event handler
        } else if (!(siblingFields.length === 0)) {
            siblingFields.each(function(){
                const value = $(this).find("coral-selectlist-item.is-selected").attr("value");
                if(typeof value == "undefined"){
                    return true;
                }
                selectedField.find(`coral-selectlist-item[value=${value}]`).hide();
            });
        }
        return;
       });
  }

})($, $(document));
