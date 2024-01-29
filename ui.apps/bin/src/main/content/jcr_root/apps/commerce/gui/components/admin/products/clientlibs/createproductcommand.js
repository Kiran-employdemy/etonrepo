/*
 * ADOBE CONFIDENTIAL
 *
 * Copyright 2012 Adobe Systems Incorporated
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 */
(function(window, document, Granite, $) {
    "use strict";

    var ui = $(window).adaptTo("foundation-ui");

    function updateButton(activator, context, activeRelations) {
        var button = $("." + activator, context || document);
        if ($.inArray(activator, activeRelations) >= 0) {
            button.removeClass("is-disabled");
            button.attr("href", button.attr("href") || button.data("href"));
        } else {
            button.addClass("is-disabled");
            button.data("href", button.data("href") || button.attr("href"));
            button.attr("href", null);
        }
    }

    function submit(wizard, message) {

        var spinner = ui.waitTicker(message, "");

        // exclude param names like ./${imageRelPath}/${imagePropName}
        var filteredFormData = wizard.serializeArray().filter( function(element) {
            return element.name.indexOf("${") == -1;
        });

        Granite.$.ajax({
            type: wizard.prop("method"),
            url: wizard.prop("action") + "/*",
            contentType: wizard.prop("enctype"),
            data: $.param(filteredFormData),
            success: function(data, status, request) {
                spinner.clear();
                var response = _g.shared.HTTP.buildPostResponseFromHTML(request.responseText),
                    productPath = response.headers.Path,
                    responseStatus = response.headers["Status"];

                // send an event to create the product assets
                $(document).trigger("cq-commerce-product-assets-create", {productPath: productPath});

                if (_g.shared.HTTP.isOkStatus(responseStatus)) {
                      var redirectTo = $('*[data-foundation-wizard-control-action="cancel"]').attr("href");
                      if (redirectTo) {
                        //when the product assets have been created and marker request is sent, go to the redirect page
                        $(document).ajaxStop(function() {
                                document.location.href = Granite.HTTP.externalize(redirectTo);
                        });
                      }
                } else {
                    _onError(response.headers["Message"]);
                }
            },
            error: function(xhr, textStatus, errorThrown) {
                spinner.clear();
                var response = _g.shared.HTTP.buildPostResponseFromHTML(xhr.responseText);
                _onError(response.headers["Message"]);
            }
        });
    }

    function _onError(message) {
        if (!message || typeof message !== "string") {
            message = Granite.I18n.get("Failed to create item.");
        }
        ui.alert(Granite.I18n.get("Error"), message, "error");
    }

    $(document).on("foundation-contentloaded", function(e) {
        var createProductWizard = $("#cq-commerce-products-createproduct-eaton .form", e.target);
        if (createProductWizard.length) {
            createProductWizard.on("submit.cq-commerce-products-createproduct-eaton", function(e) {
                e.preventDefault();

                e.stopImmediatePropagation(); // FIX JIRA CQ-42231414
                submit(createProductWizard, Granite.I18n.get("Creating Product..."));
            });
        }

        var createVariantWizard = $("#cq-commerce-products-createproduct-eaton", e.target);
        if (createVariantWizard.length) {
            createVariantWizard.on("submit.cq-commerce-products-createproduct-eaton", function(e) {
                e.preventDefault();

                e.stopImmediatePropagation(); // FIX JIRA CQ-42231414

                // handle first prefilled variant image
                $("div.product-image").each(function () {
                    if ($(this).data("productImageReferencePath")) {
                        $(this).find("input[name='./image/fileReference']").prop("name", "");
                        $(this).find("input[name='./assets/asset/fileReference']").prop("name", "");
                        if (!$(this).data("productImageOperation")) {
                            $(this).data("productImageOperation", "add");
                        }
                    }
                });
                submit(createVariantWizard, Granite.I18n.get("Creating Variation..."));
            });
        }

    });

})(window, document, Granite, Granite.$);