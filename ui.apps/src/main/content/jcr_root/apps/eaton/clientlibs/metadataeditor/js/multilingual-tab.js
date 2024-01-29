/**
 * multilingual-tab.js
 * --------------------------------------
 * This script operates on the multilingual tab of the metadata editor. It will pre-fill the multilingual inputs,
 * dynamically set the language table link, and create hidden inputs for saving translations.
 *
 * How to use:
 *
 * - Ensure the metadata schema has a Multilingual tab and it has Language, Title and Description fields.
 * --------------------------------------
 * Author: Soo Woo (soo@freedomdam.com)
 */
(function(document, $) {
    "use strict";

    $(document).on("foundation-contentloaded", function() {

        /** Bulk Editor **/
        if ($(".aem-assets-metadataform-bulk").length > 0) {
            $(".hide-in-bulk-editor").hide();
            return;
        }

        /** Multilingual Tab Language **/
        $(".multilingual-language").on("change", function () {
            resetTextFields();
            var language = $(".multilingual-language").val();
            if (language) {
                $.getJSON(getSelectedAsset() + "/jcr:content/metadata.json", function(data) {
                    $(".multilingual-title").val(data["dc:title-" +  language]);
                    $(".multilingual-title").trigger("input");
                    $(".multilingual-description").val(data["dc:description-" +  language]);
                    $(".multilingual-description").trigger("input");
                });
            }
        });

        /** Multilingual Tab Title Input **/
        $(".multilingual-title").on("input", function() {
            var language = $(".multilingual-language").val();
            var val = $(this).val();
            if (language) {
                var mtlTitlePropName = "./jcr:content/metadata/dc:title-" + language;
                if ($(".multilingual-title input[name='" + mtlTitlePropName + "']").length > 0) {
                    $(".multilingual-title input[name='" + mtlTitlePropName + "']").attr("value", val);
                } else {
                    $(".multilingual-title").append("<input type='hidden' name='" + mtlTitlePropName + "' value='" + val + "'/>");
                    $(".multilingual-title").append("<input type='hidden' name='" + mtlTitlePropName + "@Delete' value=''/>");
                }
            }
        });

        /** Multilingual Tab Description Input **/
        $(".multilingual-description").on("input", function() {
            var language = $(".multilingual-language").val();
            var val = $(this).val();
            if (language) {
                var mtlDescriptionPropName = "./jcr:content/metadata/dc:description-" + language;
                if ($(".multilingual-description input[name='" + mtlDescriptionPropName + "']").length > 0) {
                    $(".multilingual-description input[name='" + mtlDescriptionPropName + "']").attr("value", val);
                } else {
                    $(".multilingual-description").append("<input type='hidden' name='" + mtlDescriptionPropName + "' value='" + val + "'/>");
                    $(".multilingual-description").append("<input type='hidden' name='" + mtlDescriptionPropName + "@Delete' value=''/>");
                }
            }
        });

        init();

    });

    function init() {
        resetTextFields();
        setLink();
    }

    function getSelectedAsset() {
        return $(".foundation-collection-item").attr("data-foundation-collection-item-id");
    }

    function setLink() {
        $(".multilingual-click-to-open-language-table").attr("href", "/mnt/overlay/eaton/components/language/detail.html" + getSelectedAsset());
    }

    function resetTextFields() {
        $(".multilingual-title, .multilingual-description").val("");
    }
})(document,Granite.$);