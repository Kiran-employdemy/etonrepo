/**
 * table.js
 * --------------------------------------
 * This script will find and replace the language-country code with their full name.
 *
 * How to use:
 *
 * - Ensure the page includes the multilingual table component `apps/eaton/components/language/table`.
 * --------------------------------------
 * Author: Soo Woo (soo@freedomdam.com)
 */
(function(document, $) {
    "use strict";

    $(document).on("foundation-contentloaded", function() {

        /** Multilingual Table Language Column **/
        $.getJSON("/eaton/content/multilingual.json", function(data) {
            $("#multilingual-table .language").each(function () {
                var text = $(this).text();
                if (data[text]) {
                    $(this).text(data[text]);
                }
            });
        });

    });
})(document,Granite.$);