/*******************************************************************************
 * ADOBE CONFIDENTIAL
 * ___________________
 * Copyright 2016 Adobe Systems Incorporated
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and are protected by all applicable intellectual property
 * laws, including trade secret and copyright laws.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 ******************************************************************************/
(function () {
    window.fd = window.fd || {};

    window.fd.openFormForEditing = function (editable) {
        var htmlElement = $("[data-form-page-path]", editable.dom).addBack("[data-form-page-path]"),
            formPath = htmlElement.attr("data-form-page-path"),
            url = Granite.HTTP.externalize("/editor.html" + formPath + ".html");
        window.open(url);
    };

    window.fd.formExists = function (editable) {
        return $("[data-form-page-path]", editable.dom).addBack("[data-form-page-path]").length > 0;
    };
}());
