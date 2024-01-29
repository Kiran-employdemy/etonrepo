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

/**
* @deprecated Since cq-commerce-content-1.4.264
*/
(function(window, document, Granite, $) {
    "use strict";

    $(document).on("foundation-contentloaded", function(e) {
        var activeRelations = $(".parent-relationships").data("applicableRels") || "";
        activeRelations = activeRelations.trim();
        activeRelations = activeRelations.length ? activeRelations.split(/\s+/) : [];

        var context = e.target || document;
        var importProductsButton = $(".cq-commerce-products-import-activator", context);
        if ($.inArray("cq-commerce-products-import-activator", activeRelations) >= 0) {
            importProductsButton.show();
        } else {
            importProductsButton.hide();
        }
    });

})(window, document, Granite, Granite.$);
