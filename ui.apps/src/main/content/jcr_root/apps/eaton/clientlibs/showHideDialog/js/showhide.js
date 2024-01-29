/**
 * showhide.js
 * --------------------------------------
 * This will allow input components to be dynamically shown / hidden based
 * on the value of a dropdown field. For example, if I have a dropdown in a workflow dialog that allows me to select
 * between expiring or renewing an asset, I would want a datepicker field to be shown when the
 * user selects "renew", and to hide that same datepicker when the user selects "expire". This
 * clientlibrary will allow for this functionality.
 *
 * How to use:
 *
 * - add the class cq-dialog-dropdown-showhide to the dropdown/select element
 * - add the data attribute cq-dialog-dropdown-showhide-target to the dropdown/select element, value should be the
 *   selector, usually a specific class name, to find all possible target elements that can be shown/hidden.
 * - add the target class to each target component that can be shown/hidden (NOTE: it is highly recommended that
 *   each target component be a container containing all the fields you wish to show or hide)
 * - add the attribute showhidetargetvalue to each target component, the value should equal the value of the select
 *   option that will unhide this element.
 * --------------------------------------
 * Author: Jay LeCavalier (jay@freedomdam.com)
 */
(function(document, $) {
    "use strict";

    // when dialog gets injected
    $(document).on("foundation-contentloaded", function(e) {
        // if there is already an inital value make sure the according target element becomes visible
        showHide($(".cq-dialog-dropdown-showhide", e.target));
    });

    $(document).on("selected", ".cq-dialog-dropdown-showhide", function(e) {
        showHide($(this));
    });

    function showHide(el){

        el.each(function(i, element) {

            var target,
                value;
            var selection = $(element).data("select");

            if (selection) {
                target = $(element).data("cqDialogDropdownShowhideTarget");
                value = selection.getValue();
            }

            if (target) {
                $(target).each(function() {
                    var targetvalue = $(this).data("showhidetargetvalue");
                    if (targetvalue) {
                        if (targetvalue !== value) {
                            $(this).hide();
                        } else {
                            $(this).show();
                        }
                    }
                });
            }
        })
    }
})(document,Granite.$);
