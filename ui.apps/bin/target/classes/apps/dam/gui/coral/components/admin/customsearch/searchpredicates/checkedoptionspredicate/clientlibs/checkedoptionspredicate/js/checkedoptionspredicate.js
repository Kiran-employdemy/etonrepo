/*
 * ADOBE CONFIDENTIAL
 *
 * Copyright 2016 Adobe Systems Incorporated
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 */
(function(document, $) {
    "use strict";

    var $document = $(document);
    var ns = ".checkedoptions";

    function updateTag(tagList, conf, tag, name) {
        var exists = false;
        if (tag === null || tag.length === 0) {
            tag = new Coral.Tag();
            // used to search for it afterwards
            tag.setAttribute("name", name);
            tagList.items.add(tag);
        } else {
            exists = true;
            tag = tag.get(0);
        }

        tag.set({
            label: {
                innerHTML: "<span class='u-coral-text-capitalize u-coral-text-italic u-coral-text-secondary'>" +
                    conf["graniteOmnisearchTypeaheadSuggestionTag"] + ":</span> " + conf["graniteOmnisearchTypeaheadSuggestionValue"]
            }
        });

        if (exists) {
            Coral.commons.ready(tagList, function(el) {
                Coral.commons.nextFrame(function() {
                    // private CSS usage/s
                    var input = document.querySelector(".granite-omnisearch-typeahead-input");
                    if (input && el) {
                        $(input).css("padding-left", el.offsetWidth);
                    }
                });
            });
        }

        return tag;
    }

    $document.off("granite-omnisearch-predicate-clear" + ns).on("granite-omnisearch-predicate-clear" + ns, function(event) {
        var $form = $(event.target);
        var name = event.detail.reset ? "" : event.detail.item.getAttribute("name");
        var $items = $form.find(".checkedoptions-predicate coral-checkbox" + (event.detail.reset ? "" : "[name='" + name + "']") +
                                ", .checkedoptions-predicate coral-radio" + (event.detail.reset ? "" : "[name='" + name + "']"));

        $items.each(function(index, value) {
            // if the search is being cleared by closing the omnisearch tag
            if ($(value).get(0).checked) {
                $(value).get(0).checked = false;
                $(value).parent().find(".search-predicate-checkedoptionstype-option").prop("disabled", true);
                // now that the properties have been changed for the option, trigger the visible option change to submit new search
                $(value).get(0).trigger("change");
            }
        });
    });

    $document.off("granite-omnisearch-predicate-update" + ns).on("granite-omnisearch-predicate-update" + ns, function(event) {
        var predicate = [];
        var conf = {};
        var queryParameters = event.detail.queryParameters;
        var tagList = event.detail.tagList;
        var detailItem = event.detail.item;
        // we use the predicate id to determine if the queryParameters apply to this predicate
        if (event.detail.item) {
            var id = event.detail.item.dataset.graniteOmnisearchTypeaheadSuggestionPredicateid;
            predicate = $(document.getElementById(id));
            conf = event.detail.item.dataset;
        }

        if (predicate.length === 0 && !queryParameters) {
            return false;
        }

        if (queryParameters) {
            if (predicate.length === 0) {
                predicate = $(".checkedoptions-predicate");
            }
            processPredicate(predicate, queryParameters, tagList, detailItem, conf);
        }
    });

    function processPredicate(predicate, queryParameters, tagList, detailItem, conf){
        predicate.each(function() {
            //coral-accordion-item
            var nameField = this.querySelector("#predicateName");
            var predicateFieldName = nameField.name;
            var predicateSavedName;
            var $predicate = $(this);
            var $visibleOptions = $predicate.find(".search-predicate-checkedoptionstype-option-visible");
            var isSingleSelect = $predicate.data("singleselect");

            $visibleOptions.each(function() {
                //coral-checkbox
                var alreadyChecked = this.checked;
                if(alreadyChecked){
                    $(this).trigger('change');
                }
            });
            if (queryParameters[nameField.name] === this.querySelector("#predicateName").value) {
                predicateSavedName = nameField.name;
            } else if (queryParameters) {
                $.each(queryParameters, function(key, value) {
                    if (value === nameField.value) {
                        predicateSavedName = key;
                    }
                });
            }
            if (!predicateSavedName) {
                return;
            }

            var $breadcrumb = $(this).find("input[type=hidden][name='" + predicateFieldName + ".breadcrumbs']");
            conf["graniteOmnisearchTypeaheadSuggestionTag"] = $breadcrumb.val();
            processVisibleOptions($visibleOptions, detailItem, queryParameters, predicateSavedName, isSingleSelect, tagList, conf, predicateFieldName);

            // makes sure the coral internals are not modified
            $predicate.children("input[type=hidden]").each(function() {
                this.disabled = !($(this).val());
            });
        });
    }

    function processVisibleOptions($visibleOptions, detailItem, queryParameters, predicateSavedName, isSingleSelect, tagList, conf, predicateFieldName){
        $visibleOptions.each(function() {
            var $visibleOption = $(this);
            var name = this.getAttribute("name");
            var optionValue = getOptionValue(detailItem, queryParameters, predicateSavedName, predicateFieldName, name, $visibleOption);

            if (optionValue === $(this).find(".coral3-Checkbox-description").get(0).textContent.trim()) {
                if(isSingleSelect) {
                    $(this).parent().parent().find(".search-predicate-checkedoptionstype-option-visible").each(function() {
                        var tagname = this.getAttribute("name");
                        var tag = tagList.querySelector("coral-tag[name='" + tagname + "']");
                        $visibleOption.parent().find(".search-predicate-checkedoptionstype-option").prop("disabled", true);
                        $visibleOption.get(0).checked = false;
                        if (tag) {
                            tag.remove();
                        }
                    });
                }

                $(this).parent().find(".search-predicate-checkedoptionstype-option").prop("disabled", false);
                conf["graniteOmnisearchTypeaheadSuggestionValue"] = optionValue;
                this.checked = true;
                updateTag(tagList, conf, (isSingleSelect ? $(tagList).find("coral-tag[name='" + name + "']").first() : null), name);
            }
        });
    }

    function getOptionValue(detailItem, queryParameters, predicateSavedName, predicateFieldName, name, $visibleOption) {
        let optionValue;
        if (detailItem) {
            optionValue = queryParameters[predicateSavedName + ".value"];
        } else {
            // "16_property.1_value" >> "1_value"
            var predicateSuffix = name.replace(predicateFieldName + ".", "");
            if(queryParameters[predicateSavedName + "." + predicateSuffix]){
                optionValue = $visibleOption.find(".coral3-Checkbox-description").get(0).textContent.trim();
            }
            if (!optionValue && predicateSuffix.indexOf("_") !== -1) {

                predicateSuffix = predicateSuffix.substring(predicateSuffix.indexOf("_") + 1);
                optionValue = queryParameters[predicateSavedName + "." + predicateSuffix];
            }
        }
        return optionValue;
    }


    $document.off("change" + ns, ".search-predicate-checkedoptionstype-option-visible").on("change" + ns, ".search-predicate-checkedoptionstype-option-visible", function(event) {
        var $this = $(this);
		var $form = $this.closest(".granite-omnisearch-form");
        var tagList = document.querySelector(".granite-omnisearch-typeahead-tags");
        var name = event.target.getAttribute("name");
		var tag = tagList.querySelector("coral-tag[name='" + name + "']");
        var $predicate = $this.closest(".checkedoptions-predicate");
        var singleSelect = $predicate.data("singleselect");
       	var eventTargetChecked = event.target.checked;

        //remove all checks and tags if it is single select
        if (singleSelect) {
            $this.parent().parent().find(".search-predicate-checkedoptionstype-option-visible").each(function() {
                var $visibleOption = $(this);
                var name = this.getAttribute("name");
                var tag = tagList.querySelector("coral-tag[name='" + name + "']");
               	$visibleOption.parent().find(".search-predicate-checkedoptionstype-option").prop("disabled", true);
                $visibleOption.get(0).checked = false;
                if (tag) {
                    tag.remove();
                }
            });
        }
        // option was just selected so we need to create the matching tag
        if (eventTargetChecked) {
            $this.get(0).checked = true;
            // enable hidden inputs
            $this.parent().find(".search-predicate-checkedoptionstype-option").prop("disabled", false);
            var breadcrumbName = name.split(".")[0];
        	var remaining = name.split(".")[1];
            breadcrumbName += "." + remaining.split(".")[0];
           	var $breadcrumb = $predicate.find("input[type=hidden][name='" + breadcrumbName + ".breadcrumbs" + "']");
           	var conf = {
                graniteOmnisearchTypeaheadSuggestionTag: $breadcrumb.val(),
                graniteOmnisearchTypeaheadSuggestionValue: event.target.label.textContent
            };
            updateTag(tagList, conf, $(tag), name);
        // otherwise remove it if the change was an uncheck
        } else {
           $this.parent().find(".search-predicate-checkedoptionstype-option").prop("disabled", true);
			if (tag) {
                tag.remove();
            }
        }
        // makes sure the coral internals are not modified
        $predicate.children("input[type=hidden]").each(function() {
            this.disabled = !($(this).val());
        });
        $form.submit();

    });
})(document, Granite.$);
