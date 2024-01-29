(function(window, document, $, Granite) {
    "use strict";
    $(window).adaptTo("foundation-registry").register("foundation.collection.action.action", {
        name: "cq.wcm.delete",
        selector:".cq-siteadmin-admin-actions-delete-activator",
        handler: function(name, el, config, collection, selections) {
            if (selections.length > 0) {
                var paths = selections.map(function(v) {
                        return $(v).data("foundationCollectionItemId");
                    });
                  checkReferenceForResource("deletePage", paths, selections, collection);
            }
        }
    });
})(window, document, Granite.$, Granite);
