(function ($, $document) {
    "use strict";
$document.on("foundation-contentloaded", function() {
    setTimeout(function() {
                   $(".eaton-facet-value-tab-name"). each(
                       function() {
                           $(this).attr("readonly", "readonly");
                       });
                }, 250);
});
})(jQuery, jQuery(document));