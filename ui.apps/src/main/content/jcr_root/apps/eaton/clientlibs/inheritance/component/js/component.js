/**
 * component.js
 * --------------------------------------
 *
 * Contains functionality for enabling inheritance between conflicting components (_msm_moved) and parent components in blueprint.
 *
 * --------------------------------------
 * Author: Jaroslav Rassadin
 */
(function ($, ns, $document, window) {
    "use strict";

    var EditorFrame = ns.EditorFrame;

    var ACTION_ICON = "coral-Icon--link";
    var ACTION_TITLE = "Enable inheritance";
    var ACTION_NAME = "ENABLE_INHERITANCE";

    var MSM_MOVED_POSTFIX = "_msm_moved";

    var action = new ns.ui.ToolbarAction({
        name: ACTION_NAME,
        text: Granite.I18n.get(ACTION_TITLE),
        icon: ACTION_ICON,
        order: "before MSM_ROLLOUT",
        execute: function () {
            // just ensure we don't close the toolbar
            return false;
        },
        handler: function (editable, param, target) {
            var conflictingPath = editable.path.replace(/_msm_moved(_\\d)?$/, "");
            var conflictingEditable = Granite.author.editables.find(conflictingPath);

            var modalHeader = Granite.I18n.get("Enable inheritance");
            var modalMessage = Granite.I18n.get("Do you really want to enable inheritance for this component?");

            var $message = $(document.createElement("div"));
            $(document.createElement("p")).text(modalMessage).appendTo($message);

            if (conflictingEditable.length) {
                $(document.createElement("p")).text(Granite.I18n.get("Component at path {0} will be replaced with current.", [conflictingEditable[0].path])).appendTo($message);
            }
            var promptConfig = {
                title: modalHeader,
                message: $message.html(),
                type: ns.ui.helpers.PROMPT_TYPES["INFO"],
                actions: [{
                    id: "Cancel",
                    text: Granite.I18n.get("Cancel"),
                    primary: false
                }, {
                    id: "Enable",
                    text: Granite.I18n.get("Enable"),
                    primary: true
                }],
                callback: function (actionId) {
                    if ("Enable" === actionId) {
                        Eaton.author.msm.ih.enableInheritanceHandler(editable.path + ".eaton.msm.enable.json", function (data) {
                            document.getElementById("ContentFrame").contentWindow.location.reload();
                        });
                    }
                }
            };
            Granite.author.ui.helpers.prompt(promptConfig);
        },
        condition: function (editable) {
            return editable && editable.path && editable.path.endsWith(MSM_MOVED_POSTFIX) && !ns.MsmAuthoringHelper.isLiveCopy(editable);
        },
        isNonMulti: true
    });

    $document.on("cq-layer-activated", function (event) {
        if (event.layer === "Edit") {
            EditorFrame.editableToolbar.registerAction(ACTION_NAME, action);
        }
    });

}(jQuery, Granite.author, jQuery(document), this));
