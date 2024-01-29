;(function ($, ns, channel, window, undefined) {
    "use strict";

    var PIM_PROPERTIES_ACTIVATOR_SELECTOR = ".pim-properties-activator";

    /**
     * Opens the page properties
     */
    ns.actions.openPimPage = function (el) {
        var propertiesActivator = el || document.querySelector(PIM_PROPERTIES_ACTIVATOR_SELECTOR);

        if (!propertiesActivator) {
            return;
        }

        var path = propertiesActivator.dataset.path;

        if (!path) {
            return;
        }

        window.open(Granite.HTTP.externalize(path));
    };


    channel.on('click', PIM_PROPERTIES_ACTIVATOR_SELECTOR, function() {
        ns.actions.openPimPage(this);
    });

}(jQuery, Granite.author, jQuery(document), this));
