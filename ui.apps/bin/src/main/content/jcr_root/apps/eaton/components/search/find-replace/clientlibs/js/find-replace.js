/**
 * find-replace.js
 * --------------------------------------
 *
 * Contains logic for text, tags find & replace tool.
 *
 * --------------------------------------
 * Author: Gurneet Pal Singh, Jaroslav Rassadin
 */
(function (document, $) {
    "use strict";

    let nestedTagFields = null;

    let tagHelpItems = null;

    $(document).on("foundation-contentloaded", function (e) {
        if (e.target == document) {
            initTextForm();
            initTagsForm();
        }
    });

    function initTextForm() {
        const formId = "#findReplaceTextForm";
        const $form = $(formId);

        // preview handler
        $form.find("#btnPreview").on("click", function (e) {
            e.preventDefault();
            const formData = getTextFormData($form);

            if (!validateTextForm(formData)) {
                return;
            }
            showCoralWait($form);

            $.ajax({
                url: "/etc/reports/find-and-replace/_jcr_content.text.json?specificPath=" + encodeURIComponent(formData.path)
                    + "&searchText=" + formData.searchText
                    + "&wholeWords=" + formData.wholeWordsOnly
                    + "&caseSensitive=" + formData.caseSensitive,
                // Type of Request
                type: "GET",
                // Function to call when request is ok
                success: function (data) {
                    handlePreviewSuccess(data, formId, $form);
                },
                // Error handling
                error: function (error) {
                    handleError(error, $form);
                }
            });
        });

        // replace handler
        $form.find("#btnReplace").on("click", function (e) {
            e.preventDefault();
            const formData = getTextFormData($form);

            if (!validateTextForm(formData)) {
                return;
            }
            const selectedPaths = getSelectedPaths(formId);
            const rootSearch = selectedPaths.length == 0 ? true : false;

            const dialog = showReplaceConfirmationDialog("Are you sure you want to replace '" + formData.searchText + "' with '" + formData.replaceText + "' for the selected page(s)?");

            dialog.on("click", "#ok-button", function (ev) {
                dialog.open = false;
                const createBackup = getCheckboxValue($(dialog).find("#createBackup"));
                const replicateModified = getCheckboxValue($(dialog).find("#replicateModified"));

                showCoralWait($form);

                let url = "/etc/reports/find-and-replace/_jcr_content.text.json?specificPath=" + encodeURIComponent(formData.path)
                    + "&searchText=" + formData.searchText
                    + "&replaceText=" + formData.replaceText
                    + "&wholeWords=" + formData.wholeWordsOnly
                    + "&caseSensitive=" + formData.caseSensitive
                    + "&rootSearch=" + rootSearch
                    + "&replicate=" + replicateModified
                    + "&backup=" + createBackup;

                if (!rootSearch) {
                    url += "&modificationPaths=" + encodeURIComponent(selectedPaths.join(","));
                }
                $.ajax({
                    url: url,
                    // Type of Request
                    type: "POST",
                    success: function (data) {
                        handleModificationSuccess(data, $form);
                    },
                    // Error handling
                    error: function (error) {
                        handleError(error, $form);
                    }
                });
            });
            dialog.on("click", "#cancel-button", function (ev) {
                dialog.open = false;
            });
        });
        // form change listener
        $form.find("#destPath, #whole-words-only, #case-sensitive").on("change", function (e) {
            clearResultsTable($form);
        });
    }

    function initTagsForm() {
        const formId = "#findReplaceTagsForm";
        const $form = $(formId);

        const $newTagsMulti = $form.find("#newTagsMulti");

        // preview handler
        $form.find("#btnPreview").on("click", function (e) {
            handlePreviewBtnForTagsClick(e, formId, $form);
        });
        // replace handler
        $form.find("#btnReplace").on("click", function (e) {
            handleModificationBtnForTagsClick(e, formId, $form);
        });
        // form change listener
        $form.find("#destPath, #findTagsMulti").on("change", function (e) {
            clearResultsTable($form);
        });
        // mode field listener
        $form.find("#mode").on("change", function (e) {
            const field = $(e.target).adaptTo("foundation-field");
            const deleteMode = "delete" == field.getValue();

            if (deleteMode) {
                $newTagsMulti.find("label.coral-Form-fieldlabel").text("Tags to delete");

            } else {
                $newTagsMulti.find("label.coral-Form-fieldlabel").text("New tags");
            }
        });
        // tag field listener    
        $(document).on("foundation-contentloaded", formId + " coral-multifield.find-replace-tags-multifield", function (e) {
            $(e.target).find("[name='tagsField']").on("change", tagsFieldChangeHandler);
        });

        $form.find("[name='tagsField']").on("change", tagsFieldChangeHandler);
    }

    function handlePreviewBtnForTagsClick(e, formId, $form) {
        e.preventDefault();
        const formData = getTagsFormData($form);

        if (!validateTagsForm(formData, "preview")) {
            return;
        }
        showCoralWait($form);

        $.ajax({
            url: "/etc/reports/find-and-replace/_jcr_content.tags.json?specificPath=" + encodeURIComponent(formData.path)
                + convertTagsToRequestParam("findTags", formData.findTags),
            // Type of Request
            type: "GET",
            // Function to call when request is ok
            success: function (data) {
                handlePreviewSuccess(data, formId, $form);
            },
            // Error handling
            error: function (error) {
                handleError(error, $form);
            }
        });
    }

    function handleModificationBtnForTagsClick(e, formId, $form) {
        e.preventDefault();
        const formData = getTagsFormData($form);

        if (!validateTagsForm(formData, "replace")) {
            return;
        }
        const selectedPaths = getSelectedPaths(formId);
        const rootSearch = selectedPaths.length == 0 ? true : false;

        var dialog = showReplaceConfirmationDialog("Are you sure you want to perform update?");

        dialog.on("click", "#ok-button", function (ev) {
            dialog.open = false;
            const createBackup = getCheckboxValue($(dialog).find("#createBackup"));
            const replicateModified = getCheckboxValue($(dialog).find("#replicateModified"));

            showCoralWait($form);

            let url = "/etc/reports/find-and-replace/_jcr_content.tags.json?specificPath=" + encodeURIComponent(formData.path)
                + "&mode=" + formData.mode
                + "&rootSearch=" + rootSearch
                + convertTagsToRequestParam("findTags", formData.findTags)
                + convertTagsToRequestParam("newTags", formData.newTags)
                + "&replicate=" + replicateModified
                + "&backup=" + createBackup;

            if (!rootSearch) {
                url += "&modificationPaths=" + encodeURIComponent(selectedPaths.join(","));
            }
            $.ajax({
                url: url,
                // Type of Request
                type: "POST",
                success: function (data) {
                    handleModificationSuccess(data, $form);
                },
                // Error handling
                error: function (error) {
                    handleError(error, $form);
                }
            });
        });
        dialog.on("click", "#cancel-button", function (ev) {
            dialog.open = false;
        });
    }

    $(document).on("foundation-contentloaded", ".find-replace-tags-multifield", function (e) {
        e.stopPropagation()

        var $tagFields = $(e.target).find("coral-select[name='tagsField']");

        $tagFields.last().attr("id", generateId($(e.target).attr("id") + "-tagsField-"));

        disableTagsFieldOptions($tagFields, $tagFields.last());
    });

    $(document).on("mousedown", "button[coral-multifield-remove]", function (e) {
        var $target = $(e.target);
        enableTagsFieldOptions($target.parents(".find-replace-tags-multifield"),
            $target.parents("coral-multifield-item").find("coral-select[name='tagsField']").val());
    });

    function tagsFieldChangeHandler(e) {
        // enable tag picker
        const $tagsField = $(e.target);
        const $tags = $tagsField.parents("coral-multifield-item").find("[name='tags']");
        const $selectedTagField = $tagsField.find("coral-select-item:selected");
        const tagMultiSelection = $selectedTagField.data("multiple");

        var tags = $tags.adaptTo("foundation-field");
        tags.clear();
        tagMultiSelection ? $tags.attr("multiple", "multiple") : $tags.removeAttr("multiple");
        setPathRoot($tags, $selectedTagField.data("path"), tagMultiSelection);
        tags.setDisabled(false);
        $tags.attr("aria-disabled", "false");
        $tags.removeClass("is-disabled");

        // toggle available value in other tag fields
        toggleTagsFieldsOptions($tagsField.parents("coral-multifield").find("coral-select[name='tagsField']"));

        enableTagsFieldHelp($tagsField);
    }

    function toggleTagsFieldsOptions($tagFields) {
        $tagFields.find("coral-select-item").removeAttr("disabled");

        $tagFields.each(function (index, element) {
            disableTagsFieldOptions($tagFields, $(element))
        });
    }

    function disableTagsFieldOptions($tagFields, $currentTagfield) {
        $tagFields.each(function (tagFieldIndex, tagFieldElement) {
            var $tagField = $(tagFieldElement);

            if (!($currentTagfield.attr("id")) || ($tagField.attr("id") != $currentTagfield.attr("id"))) {
                $currentTagfield.find("coral-select-item[value='" + $tagField.val() + "']").attr("disabled", "disabled");
            }
        });
    }

    function enableTagsFieldOptions($tagFields, optionValue) {
        $tagFields.each(function (tagFieldIndex, tagFieldElement) {
            $(tagFieldElement).find("coral-select-item[value='" + optionValue + "']").removeAttr("disabled");
        });
    }

    function enableTagsFieldHelp($tagsField) {
        if (!tagHelpItems) {
            tagHelpItems = JSON.parse($tagsField.parent().find(".find-replace-form-select-help-data").val());
        }
        const tagHelpItem = tagHelpItems.filter(i => i.tagId == $tagsField.val())[0];

        const $help = $tagsField.parent().find(".find-replace-form-select-help");
        populateTagsFieldHelpHeader($help.find("coral-popover-header"), tagHelpItem);
        populateTagsFieldHelp($tagsField.parent().find("coral-popover-content div.find-replace-form-select-help-types"), tagHelpItem);

        const id = $tagsField.attr("id");

        const $button = $tagsField.parent().find(".find-replace-form-select-help-btn");
        const buttonId = id + "-btn";
        $button.attr("id", buttonId);
        $button.removeAttr("disabled");

        $help.attr("target", "#" + buttonId);
    }
    function populateTagsFieldHelpHeader($header, tagHelpItem) {
        $header.empty();
        let text = tagHelpItem.multiple ? "Multi valued" : "Single valued";

        if (!tagHelpItem.ownProperty) {
            text += ", nested in multifield";
        }
        $header.text(text);
    }

    function getContentTypeTitle(id) {
        switch (id) {
            case "ASSET":
                return "Assets";
            case "PAGE":
                return "Pages";
            case "COMPONENT":
                return "Components";
        }
        return id;
    }

    function populateTagsFieldHelp($helpContent, helpItem) {
        $helpContent.empty();

        for (var key in helpItem.types) {

            if (Object.hasOwnProperty.call(helpItem.types, key)) {
                const contentTypeTitle = getContentTypeTitle(key);
                $helpContent.append($(`<p>${contentTypeTitle}</p>`));

                if (helpItem.types[key].length) {
                    const $listElement = $("<ul></ul>");

                    for (const title of helpItem.types[key]) {
                        $listElement.append(`<li>${title}</li>`)
                    }
                    $helpContent.append($listElement);
                }
            }
        }
    }

    function getTextFormData($form) {
        return {
            path: $form.find("#destPath").val(),
            searchText: $form.find("#findText").val(),
            replaceText: $form.find("#replaceText").val(),
            wholeWordsOnly: getCheckboxValue($form.find("#whole-words-only")),
            caseSensitive: !getCheckboxValue($form.find("#case-insensitive"))
        };
    }

    function getTagsFormData($form) {
        return {
            path: $form.find("#destPath").val(),
            findTags: getTags($form.find("#findTagsMulti")),
            newTags: getTags($form.find("#newTagsMulti")),
            mode: $form.find("#mode").val()
        };
    }

    function getTags($multiField) {
        var tags = {};

        $multiField.find("coral-multifield-item").each(function (index, element) {
            var $element = $(element);

            var $tags = $element.find("[name='tags']");
            var $tagsField = $element.find("[name='tagsField']");

            var field = $tagsField.val();
            var values = getTagsValue($tags);

            if (tags[field] != null) {

                for (var tagValue of values) {

                    if (tags[field].indexOf(tagValue) == -1) {
                        tags[field].push(tagValue);
                    }
                }
            } else {
                tags[field] = values;
            }
        });
        return tags;
    }

    function getTagsValue($tagPicker) {
        var values = [];
        $tagPicker.find("coral-tag").each(function (index, element) {
            values.push($(element).val());
        });
        return values;
    }

    function getCheckboxValue($checkbox) {
        if (!$checkbox.length) {
            return false;
        }
        return $checkbox[0].checked;
    }

    function getSelectedPaths(formId) {
        var paths = [];
        var resultsTable = document.querySelector(formId + " .results-table");

        if (resultsTable.selectedItems.length != 0) {
            for (var item of resultsTable.selectedItems) {
                paths.push(item.attributes["data-nodes"].nodeValue);
            }
        }
        return paths;
    }

    function validateTextForm(data) {
        if (data.path == "" && data.searchText == "") {
            showAlert("Please choose a path and provide a text to search for", "warning", false);
            return false;

        } else if (data.path == "") {
            showAlert("Please choose a path", "warning", false);
            return false;

        } else if (data.searchText == "") {
            showAlert("Please provide a text to search for", "warning", false);
            return false;
        }
        var isPathValid = validatePath(data.path);

        if (!isPathValid) {
            return false;
        }
        return true;
    }

    function validateTagsForm(data, operation) {
        if (data.path == "") {
            showAlert("Please choose a path", "warning", false);
            return false;
        }
        var isPathValid = validatePath(data.path);

        if (!isPathValid) {
            return false;
        }
        if (data.findTagsField == "") {
            showAlert("Please choose a tag field", "warning", false);
            return false;
        }
        if (data.mode != "delete" && data.newTagsField == "") {
            showAlert("Please choose a tag field", "warning", false);
            return false;
        }
        if (data.mode == "") {
            showAlert("Please choose a mode", "warning", false);
            return false;
        }
        if (!validateFindTags(operation, data)) {
            return false;
        }
        if (!validateNewTags(operation, data)) {
            return false;
        }
        return true;
    }

    function validateFindTags(operation, data) {
        if (!validateTagValues(data.findTags)) {
            showAlert("Please choose tags to find", "warning", false);
            return false;
        }
        if (operation == "replace") {

            if (!validateNestedTagFields(data.findTags, data.newTags)) {
                showAlert("Please include nested tags in both find and new/delete sections", "warning", false);
                return false;
            }
        }
        return true;
    }

    function validateNewTags(operation, data) {
        if (operation == "replace" && data.mode != "delete" && !validateTagValues(data.newTags)) {
            showAlert("Please choose new tags", "warning", false);
            return false;
        }
        if (operation == "replace" && data.mode == "delete" && !validateTagValues(data.newTags)) {
            showAlert("Please choose tags to delete", "warning", false);
            return false;
        }
        if (operation == "replace") {

            if (data.mode == "replace" && !validateTagsForReplaceMode(data)) {
                showAlert("Please choose same tags fields", "warning", false);
                return false;

            } else if (!validateNestedTagFields(data.newTags, data.findTags)) {
                showAlert("Please include nested tags in both find and new/delete sections", "warning", false);
                return false;
            }
        }
        return true;
    }

    function validateTagValues(tags) {
        if (tags == null) {
            return false;
        }
        for (var key in tags) {
            if (Object.hasOwnProperty.call(tags, key) && tags[key] && tags[key].length != 0) {
                return true;
            }
        }
        return false;
    }

    function validateTagsForReplaceMode(data) {

        for (var key in data.newTags) {

            if (Object.hasOwnProperty.call(data.newTags, key)) {

                if (!Object.hasOwnProperty.call(data.findTags, key)) {
                    return false;
                }
            }
        }
        return true;
    }

    function validateNestedTagFields(currentSet, otherSet) {

        for (var key in currentSet) {

            if (Object.hasOwnProperty.call(currentSet, key)) {

                if (getNestedTagFields().includes(key) && !Object.hasOwnProperty.call(otherSet, key)) {
                    return false;
                }
            }
        }
        return true;
    }

    function validatePath(path) {
        let isPathValid = true;
        let pathsArray;
        if (!path.startsWith("/content/")) {
            isPathValid = false;
            showAlert("Path should start with \"/content/\"", "warning", false);

        } else if (path.startsWith("/content/dam/")) {
            pathsArray = path.split("/content/dam/")[1]?.split("/");
            if (pathsArray.length == 1 || (pathsArray.length == 2 && pathsArray[1] == "")) {
                isPathValid = false;
                showAlert("The query is too generic. Please use a more specific path restriction", "warning", false);
            }
        } else if (path.startsWith("/content/")) {
            pathsArray = path.split("/content/")[1]?.split("/");
            if (pathsArray.length == 1 || (pathsArray.length == 2 && pathsArray[1] == "")) {
                isPathValid = false;
                showAlert("The query is too generic. Please use a more specific path restriction", "warning", false);
            }
        }
        return isPathValid;
    }

    function showAlert(message, variant, allowCancel) {
        $("#findReplaceDialog")?.remove();
        let footerHtml = "<button id='ok-button' is='coral-button' variant='primary' coral-close=''>Ok</button>";
        if (allowCancel) {
            footerHtml = "<button id='ok-button' is='coral-button' variant='primary' coral-close=''>Ok</button>"
                + "<button id='cancel-button' is='coral-button' coral-close=''>Cancel</button>";
        }
        const dialog = new Coral.Dialog().set({
            id: "findReplaceDialog",
            header: {
                innerHTML: variant === "error" ? "Error" : "Info"
            },
            content: {
                innerHTML: message
            },
            footer: {
                innerHTML: footerHtml
            },
            backdrop: "static",
            variant: variant
        });
        document.body.appendChild(dialog);
        dialog.show();
    }

    function showCoralWait($form) {
        var wait = new Coral.Wait().set({
            size: "M",
            centered: true
        });
        $form.find(".results-table thead").empty();
        $form.find(".results-table tbody").empty();
        $form.find(".results-table tbody").append(wait);
    }

    function removeCoralWait($form) {
        $form.find(".results-table tbody coral-wait").remove();
    }


    function handlePreviewSuccess(data, formId, $form) {
        removeCoralWait($form);

        if (!data.results.length > 0) {
            showAlert("No Occurrences Found", "info", false);
            return;
        }
        const componentsPresent = componentsPresentInResults(data.results);
        buildResultsTableHeader(document.querySelector(formId + " .results-table thead"), componentsPresent);
        buildResultsTableContent(data.results, document.querySelector(formId + " .results-table tbody"), componentsPresent);

        // TODO check and delete this function if not necessary
        //observeGraniteUIClassLoadOnTable(formId);
    }

    function buildResultsTableHeader(container, componentsPresent) {
        let headerContent = "<tr is='coral-table-row'>"
            + "<th is='coral-table-headercell'><coral-checkbox coral-table-select></coral-checkbox></th>"
            + "<th is='coral-table-headercell'>Title</th><th is='coral-table-headercell'>Path</th>"

        if (componentsPresent) {
            headerContent += "<th is='coral-table-headercell'>Component title</th><th is='coral-table-headercell'>Component path</th>";
        }
        headerContent += "</tr>";
        container.innerHTML = headerContent;
    }

    function buildResultsTableContent(results, container, componentsPresent) {
        for (let result of results) {
            if (!result.path) {
                continue;
            }
            if (result.contentType == "COMPONENT") {
                addComponentRow(container, result);

            } else {
                componentsPresent ? addRow(container, result, 2) : addRow(container, result, 0);
            }
        }
    }

    function addRow(container, result, emptyCells) {
        const row = document.createElement("tr");
        row.setAttribute("is", "coral-table-row");
        row.setAttribute("data-nodes", result.path);

        const checkbox = document.createElement("coral-checkbox");
        checkbox.setAttribute("coral-table-rowselect", "");
        addHtmlCell(row, checkbox);

        addTextCell(row, result.topContainerTitle);
        addTextCell(row, result.topContainerPath);

        if (emptyCells) {

            for (let i = 0; i < emptyCells; i++) {
                addTextCell(row, " ");
            }
        }
        container.appendChild(row);
    }

    function addComponentRow(container, result) {
        const row = document.createElement("tr");
        row.setAttribute("is", "coral-table-row");
        row.setAttribute("data-nodes", result.path);

        const checkbox = document.createElement("coral-checkbox");
        checkbox.setAttribute("coral-table-rowselect", "");
        addHtmlCell(row, checkbox);

        addTextCell(row, result.topContainerTitle);
        addTextCell(row, result.topContainerPath);
        addTextCell(row, result.title);
        addTextCell(row, result.path.replace(result.topContainerPath + "/jcr:content", ""));

        container.appendChild(row);
    }

    function addHtmlCell(container, child) {
        const cell = document.createElement("td");
        cell.setAttribute("is", "coral-table-cell");
        cell.appendChild(child);
        container.appendChild(cell);
    }

    function addTextCell(container, text) {
        const cell = document.createElement("td");
        cell.setAttribute("is", "coral-table-cell");
        cell.innerText = text;
        container.appendChild(cell);
    }

    function observeGraniteUIClassLoadOnTable(formId) {
        var findReplaceFormBody = document.querySelector(formId + " .results-table tbody");
        var observer = new MutationObserver(function (mutations) {
            for (var mutation of mutations) {
                if (mutation.type === "attributes" && mutation.attributeName === "class") {
                    var maxWidth = getMaxWidth(formId + " .results-table tbody tr td:nth-child(2)");
                    if (maxWidth != 0) {
                        $(formId + " .results-table thead tr th:nth-child(2)").width(maxWidth);
                    } else {
                        $(formId + " .results-table thead tr th:nth-child(2)").width(80);
                        $(formId + " .results-table tbody tr td:nth-child(2)").width(80);
                    }
                    break;
                }
            }
        });
        observer.observe(findReplaceFormBody, {
            attributes: true,
            childList: true,
            subtree: true
        });
    }

    function handleModificationSuccess(data, $form) {
        removeCoralWait($form);

        const container = document.createElement("p");

        const msg = document.createElement("p");
        msg.appendChild(document.createTextNode(data && data.results.length ? "Update executed" : "No Occurrences Found"));
        container.appendChild(msg);

        if (data.operations && data.operations.backup && data.operations.backup.packagePath) {
            const backupInfo = document.createElement("p");
            const backupLink = document.createElement("a");
            const backupLinkText = "Link to the backup";
            backupLink.appendChild(document.createTextNode(backupLinkText));
            backupLink.title = backupLinkText;
            backupLink.classList.add("coral-Link");
            backupLink.target = "_blank";
            backupLink.href = "/crx/packmgr/index.jsp#" + data.operations.backup.packagePath;

            backupInfo.appendChild(backupLink);
            container.appendChild(backupInfo);
        }
        if (data.operations && data.operations.replication && data.operations.replication.reportPath) {
            const replicationInfo = document.createElement("p");
            const replicationLink = document.createElement("a");
            const replicationLinkText = "Action required: find & replace - pages excluded from re-publishing";
            replicationLink.appendChild(document.createTextNode(replicationLinkText));
            replicationLink.title = replicationLinkText;
            replicationLink.classList.add("coral-Link");
            replicationLink.target = "_blank";
            replicationLink.href = data.operations.replication.reportPath;

            replicationInfo.appendChild(replicationLink);
            container.appendChild(replicationInfo);
        }
        showAlert(container.innerHTML, "success", false);
    }

    function handleError(error, $form) {
        let errorMessage = "";

        if (error.responseJSON && error.responseJSON.errors && error.responseJSON.errors.length) {
            errorMessage = buildErrorMessage(error.responseJSON.errors);

        } else {
            errorMessage = "An error has occurred";
        }
        removeCoralWait($form);
        showAlert(errorMessage, "error", false);
        console.log(`Error: ${errorMessage}`);
    }

    function buildErrorMessage(errors) {
        let message = "";

        for (const error of errors) {
            if (error.type == "validation") {
                message += error.property + ": " + error.description + "\n";

            } else {
                message += error.description + "\n";
            }
        }
        return message;
    }

    function clearResultsTable($form) {
        $form.find(".multi-select-box").empty();
        $form.find(".results-table thead").empty();
        $form.find(".results-table tbody").empty();
    }

    function getMaxWidth(element) {
        var max = 0;
        $(element).each(function () {
            var width = parseInt($(this).width());
            if (width > max) {
                max = width;
            }
        });
        return max;
    }


    function setPathRoot($element, path, tagMultiSelection) {
        if (tagMultiSelection) {
            $element.attr("pickersrc", $element.attr("pickersrc").replace(/\?root=.*/, "?root=" + path + "&selectionCount=multiple"));
        } else {
            $element.attr("pickersrc", $element.attr("pickersrc").replace(/\?root=.*/, "?root=" + path));
        }
        var $overlay = $element.find("coral-overlay");
        $overlay.attr("data-foundation-picker-buttonlist-src", $overlay.attr("data-foundation-picker-buttonlist-src").replace(/root=.+\{/, "root=" + path + "{"));
    }

    function showReplaceConfirmationDialog(text) {
        var template = document.getElementById("confirmationDialogTpl");

        var copy = template.content.cloneNode(true);
        copy.querySelector("#confirmationDialogText").appendChild(document.createTextNode(text));

        showAlert(copy.querySelector("#confirmationDialogWrapper").innerHTML, "info", true);

        return document.querySelector("#findReplaceDialog");
    }

    function convertTagsToRequestParam(paramName, tags) {
        var result = "&" + paramName + "=";
        var propNames = getTagPropertyNames(tags);
        var propNamesIndex = 0;

        for (var propName of propNames) {

            for (var i = 0; i < tags[propName].length; i++) {
                var tagValue = tags[propName][i];
                // tag value might contain commas, for that reason value is twice URL encoded
                result += propName + "|" + encodeURIComponent(encodeURIComponent(tagValue));

                if (i < tags[propName].length - 1) {
                    result += ",";
                }
            }
            if (propNamesIndex < propNames.length - 1) {
                result += ",";
                propNamesIndex++;
            }
        }
        return result;
    }

    function getTagPropertyNames(tags) {
        var propNames = [];

        for (var key in tags) {
            if (Object.hasOwnProperty.call(tags, key) && tags[key] && tags[key].length != 0) {
                propNames.push(key);
            }
        }
        return propNames;
    }

    function generateId(prefix) {
        while (true) {
            const id = prefix + Math.floor((Math.random() * 10000));

            if (!document.getElementById(id)) {
                return id;
            }
        }
    }

    function componentsPresentInResults(results) {
        for (const result of results) {
            if (result.contentType == "COMPONENT") {
                return true;
            }
        }
        return false;
    }

    function getNestedTagFields() {
        if (!nestedTagFields) {
            nestedTagFields = [];
            $("#findReplaceTagsForm #newTagsMulti coral-select[name='tagsField']").first()
                .find("coral-select-item[data-own='false']").each(function () { nestedTagFields.push($(this).val()) });
        }
        return nestedTagFields;
    }

})(document, Granite.$);