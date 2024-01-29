/**
 * Extension to the standard dropdown/select component. It enabled hidding/unhidding of other components based on the
 * selection made in the dropdown/select.
 *
 * How to use:
 *
 * - add the class cq-dialog-dropdown-showhide to the dropdown/select element
 * - add the data attribute cq-dialog-dropdown-showhide-target to the dropdown/select element, value should be the
 *   selector, usually a specific class name, to find all possible target elements that can be shown/hidden.
 * - add the target class to each target component that can be shown/hidden
 * - add the class hidden to each target component to make them initially hidden
 * - add the attribute showhidetargetvalue to each target component, the value should equal the value of the select
 *   option that will unhide this element.
 */
(function (document, $) {
    "use strict";

    // when dialog gets injected
    $(document).on("foundation-contentloaded", function (e) {
        // if there is already an inital value make sure the according target element becomes visible
        Coral.commons.ready(function () {
            showHideHandler($(".cq-dialog-dropdown-showhide", e.target));
        });
    });

    $(document).on("selected", ".cq-dialog-dropdown-showhide", function (e) {
        showHideHandler($(this));
    });

    function showHideHandler(el) {
        el.each(function (i, element) {
            if ($(element).is("coral-select")) {
                // handle Coral3 base drop-down
                Coral.commons.ready(element, function (component) {
                    showHide(component, element);
                    component.on("change", function () {
                        showHide(component, element);
                    });
                });
            } else {
                // handle Coral2 based drop-down
                var component = $(element).data("select");
                if (component) {
                    showHide(component, element);
                }
            }
        })
    }

    function showHide(component, element) {
        // get the selector to find the target elements. its stored as data-.. attribute
        var target = $(element).data("cqDialogDropdownShowhideTarget");
        var $target = $(target);

        if (target) {
            var value;
            if (typeof component.value !== "undefined") {
                value = component.value;
            } else if (typeof component.getValue === "function") {
                value = component.getValue();
            }


            var values = $target.attr("data-showhidetargetvalue").split(",");
            var targetElement = $('*[data-showhidetargetvalue]');
            var $targetElement = $(targetElement);

            $.each(values, function(index, targetVal) {
                // make sure all unselected target elements are hidden.
                // unhide the target element that contains the selected value as data-showhidetargetvalue attribute
                if (targetVal === value) {
                    setVisibilityAndHandleFieldValidation($(targetElement), targetVal);}
            });
        }
    }

    /**
     * Shows or hides an element based on parameter "show" and toggles validations if needed. If element
     * is being shown, all VISIBLE fields inside it whose validation is false would be changed to set the validation
     * to true. If element is being hidden, all fields inside it whose validation is true would be changed to
     * set validation to false.
     *
     * @param {jQuery} $element Element to show or hide.
     * @param {Boolean} show <code>true</code> to show the element.
     */
    function setVisibilityAndHandleFieldValidation($element, show) {
        if (show) {
            $element.removeClass("hide");
            $element.find("input[aria-required=false], coral-multifield[aria-required=false], foundation-autocomplete[aria-required=false]")
                .filter(":not(.hide>input)").filter(":not(input.hide)")
                .filter(":not(foundation-autocomplete[aria-required=false] input)")
                .filter(":not(.hide>coral-multifield)").filter(":not(input.coral-multifield)").each(function(index, field) {
                toggleValidation($(field));
            });
        } else {
            $element.addClass("hide");
            $element.find("input[aria-required=true], coral-multifield[aria-required=true], foundation-autocomplete[required]")
                .filter(":not(foundation-autocomplete[required] input)")
                .each(function(index, field) {
                    toggleValidation($(field));
                });
        }
    }

    /**
     * If the form element is not shown we have to disable the required validation for that field.
     *
     * @param {jQuery} $field To disable / enable required validation.
     */
    function toggleValidation($field) {
        var required = $field.prop("required");
        var ariaRequired = $field.attr('aria-required');
        var notRequired = ariaRequired === 'true';
        if ($field.is("foundation-autocomplete") && required !== 'undefined') {
            if (required === true) {
                $field[0].required = false;
                $field.attr('aria-required', false);
            } else if (required === false) {
                $field[0].required = true;
                $field.removeAttr('aria-required');
            }
        } else if (typeof ariaRequired !== 'undefined') {
            $field.attr('aria-required', String(!notRequired));
        }

        var api = $field.adaptTo("foundation-validation");
        if (api) {
            if (notRequired) {
                api.checkValidity();
            }
            api.updateUI();
        }
    }
})(document, Granite.$);