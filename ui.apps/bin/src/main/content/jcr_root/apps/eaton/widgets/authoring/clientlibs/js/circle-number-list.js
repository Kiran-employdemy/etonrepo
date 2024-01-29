(function ($, $document) {
    "use strict";
    $.validator.register("foundation.validation.validator", {
        selector: "coral-multifield",
        validate: function (el) {

            var totalPanels = el["0"].items.getAll().length;

            var max;

            if ($(el).data("maximum-item")) {
                max = $(el).data("maximum-item");
                if (totalPanels > max) {
                    return "Maximum numbers of items allowed are: " + max;
                }
            }
        }
    });
})($, $(document));
