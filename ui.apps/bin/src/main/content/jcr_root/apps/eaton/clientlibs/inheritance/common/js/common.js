/**
 * common.js
 * --------------------------------------
 *
 * Contains common functionality for enabling inheritance between conflicting pages\components (_msm_moved) and blueprints.
 *
 * --------------------------------------
 * Author: Jaroslav Rassadin
 */
(function ($, ns, $document, window) {
    "use strict";

    var namespace = initNamespace(window, ["Eaton", "author", "msm", "ih"]);

    namespace.enableInheritanceHandler = function (url, successCallback) {
        showModalWait();
        var ui = $(window).adaptTo("foundation-ui");

        $.ajax({
            url: url,
            type: "POST",
            success: function (data) {
                handleSuccess(ui, data, successCallback);
            },
            error: function (error) {
                handleError(ui, error);
            }
        });
    };

    function handleSuccess(ui, data, successCallback) {
        hideModalWait();

        var messageContainer = $(document.createElement("div"));
        $(document.createElement("p"))
            .text(Granite.I18n.get("Inheritance enabled."))
            .appendTo(messageContainer);

        ui.prompt(Granite.I18n.get("Success"), messageContainer.html(), "success", [{
            text: Granite.I18n.get("Ok"),
            primary: true,
            handler: function () {
                successCallback(data);
            }
        }]);
    }

    function handleError(ui, error) {
        hideModalWait();

        var errorMessage = "";

        if (error.responseJSON && error.responseJSON.msg) {
            errorMessage = error.responseJSON.msg;
        } else {
            errorMessage = "An error has occurred while enabling inheritance.";
        }
        console.log(`Error: ${errorMessage}`);

        var messageContainer = $(document.createElement("div"));
        $(document.createElement("p"))
            .text(Granite.I18n.get(errorMessage))
            .appendTo(messageContainer);

        ui.prompt(Granite.I18n.get("Error"), messageContainer.html(), "error", [{
            text: Granite.I18n.get("Ok"),
            primary: true
        }]);
    }

    function showModalWait() {
        var modal = document.getElementById("eaton-msm-ih-modal-wait");

        if (!modal) {
            modal = document.createElement("div");
            modal.id = "eaton-msm-ih-modal-wait";
            modal.style.cssText = `display: none;
            position: fixed;
            z-index: 10010;
            left: 0; top: 0;
            width: 100%;
            height: 100%;
            overflow: auto;
            background-color: rgba(0,0,0,0.3);
            transition: opacity 0.35s;`;

            var wait = new Coral.Wait().set({
                size: "L",
                centered: true
            });
            modal.appendChild(wait);
            document.body.appendChild(modal);
        }
        modal.style.display = "block";
    }

    function hideModalWait() {
        var modal = document.getElementById("eaton-msm-ih-modal-wait");

        if (modal) {
            modal.style.display = "none";
        }
    }

    function initNamespace(windowObj, elements) {
        var parent = windowObj;
        var child = null;

        for (const element of elements) {
            if (parent[element] == null) {
                child = {};
                parent[element] = child;
            }
            parent = parent[element];
        }
        return child;
    }

}(jQuery, Granite.author, jQuery(document), this));
