/**
 * page.js
 * --------------------------------------
 *
 * Contains functionality for enabling inheritance between conflicting page (_msm_moved) and blueprint page.
 *
 * --------------------------------------
 * Author: Jaroslav Rassadin
 */
(function ($, ns, $document, window) {
    "use strict";

    // Copy of original Adobe script pattern. It contains versions for both Coral 2 and Coral 3.

    // Coral 3 version
    $(document).on("click", ".coral-TabPanel.cq-siteadmin-admin-properties-tabs > .coral-TabPanel-navigation > .coral-TabPanel-tab", function (e) {
        var $tabPanel = $(e.target).closest(".cq-siteadmin-admin-properties-tabs");
        var $actionBar = $("coral-actionbar");

        if ($tabPanel.find(".cq-siteadmin-admin-properties-inheritance:visible").length > 0) {
            $actionBar.find(".cq-siteadmin-admin-properties-actions-inheritance").removeClass("hide");
        } else {
            $actionBar.find(".cq-siteadmin-admin-properties-actions-inheritance").addClass("hide");
        }
    });

    // Coral 2 version
    $(document).on("coral-panelstack:change", ".cq-siteadmin-admin-properties-tabs", function (e) {
        var $tabPanel = $(e.target).closest(".cq-siteadmin-admin-properties-tabs");
        var $actionBar = $("coral-actionbar");

        var $target = $tabPanel.find(".cq-siteadmin-admin-properties-inheritance");
        if ($target.length > 0 && e.detail.selection.contains($target[0])) {
            $actionBar.find(".cq-siteadmin-admin-properties-actions-inheritance").removeClass("hide");
        } else {
            $actionBar.find(".cq-siteadmin-admin-properties-actions-inheritance").addClass("hide");
        }
    });

    // "Enable inheritance" button handler
    var ui = $(window).adaptTo("foundation-ui");

    $(window).adaptTo("foundation-registry").register("foundation.collection.action.action", {
        name: "cq.wcm.msm.enable",
        handler: function (name, el, config) {
            var message = $(document.createElement("div"));

            var newpath = $(this).data("newpath");
            var blueprintPath = $(this).data("blueprintpath");
            var deletepath = $(this).data("deletepath");

            var action = $(this).data("foundation-collection-action");

            $(document.createElement("p"))
                .text(Granite.I18n.get("You are going to enable inheritance between this page and {0}.", [blueprintPath]))
                .appendTo(message);

            $(document.createElement("p"))
                .text(Granite.I18n.get("This page will be renamed to {0}.", [newpath]))
                .appendTo(message);

            if (deletepath !== "") {
                $(document.createElement("p"))
                    .text(Granite.I18n.get("Current page at path {0} will be replaced with this.", [newpath]))
                    .appendTo(message);
            }
            ui.prompt(Granite.I18n.get("Enable inheritance"), message.html(), "info", [{
                text: Granite.I18n.get("Cancel")
            }, {
                text: Granite.I18n.get("Enable"),
                primary: true,
                handler: function () {
                    Eaton.author.msm.ih.enableInheritanceHandler(action.data.path + "." + action.data.uriPostfix, function (data) {
                        document.location.href = "/sites.html" + data.newPath;
                    });
                }
            }]);
        }
    });
}(jQuery, Granite.author, jQuery(document), this));
