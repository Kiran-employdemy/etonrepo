/*
 * ADOBE CONFIDENTIAL
 *
 * Copyright 2018 Adobe Systems Incorporated
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
(function (document, $) {

	"use strict";

    var createTagAutoPopulate;

    $(document).on("foundation-contentloaded", function(e){

        if (!createTagAutoPopulate) {
            createTagAutoPopulate = new CreateTagAutoPopulate();
            createTagAutoPopulate.initialize();
        }

    });

    var CreateTagAutoPopulate = new Class({

        _ILLEGAL_FILENAME_CHARS: "\"\.#%/\\:*?\[\]|\n\t\r ",
        _ILLEGAL_FILENAME_REGEX: /[\"\.#%/\\:*?\[\]|\n\t\r ]|[\x7f-\uffff]/g,

        initialize: function () {
            var self = this;
            self.stopAutoPopulate = false;
            var $document =  $(document);
            var $tagTitleInput = $document.find("#tagtitle");
            var $tagNameInput = $document.find("#newTagName").find("input.js-coral-Autocomplete-textfield");
            $tagTitleInput.on('input', function(event){
                if (!self.stopAutoPopulate) {
                    //validate the Title input, add tooltip if necessary and populate the valid value in Name field
                    self._validateAndAddTooltip($tagNameInput,this.value);
                }
            });

            $tagNameInput.on('keydown', function (event) {
                self.prevValueOnKeyUp = this.value;
            }).on('keyup', function(event){
                //Check for `tab` key press
                var code = event.keyCode || event.which;
                if(code == '9') {
                    return;
                }

                //console.log("Testing value :" + this.value.toLowerCase().replace(self._ILLEGAL_FILENAME_REGEX, "-"));

                if (!self.stopAutoPopulate && (this.value != self.prevValueOnKeyUp)) {
                    // If Name field updated, then stop auto-populate
                    self.stopAutoPopulate = true;
                    // Remove the tooltips and error-icons if any as there is no contract between TiTle and Name for auto-filling
					//$tagNameInput.val(this.value.toLowerCase().replace(self._ILLEGAL_FILENAME_REGEX, "-"));
                    self._removeErrorIconTooltip($tagNameInput);
                }
				else if(self.stopAutoPopulate){

					//$tagNameInput.val(this.value.toLowerCase().replace(self._ILLEGAL_FILENAME_REGEX, "-"));
				}
            });

        },

         _removeErrorIconTooltip: function ($tagNameInput) {
            $tagNameInput.parent().find('#tag-name-textfield-fielderror').remove();
            $tagNameInput.parent().find('#tag-name-textfield-fielderror-tooltip').remove();
         },

        _validateAndAddTooltip: function ($tagNameInput, enteredText) {
            var self = this;
            // Remove the stale tooltips if any
            self._removeErrorIconTooltip($tagNameInput);

            // Do validation and add icon and tooltip if required
            var match = enteredText.match(self._ILLEGAL_FILENAME_REGEX);
            if (match && match.length > 0) {
                var errorIcon = new Coral.Icon().set({
                    id: "tag-name-textfield-fielderror",
                    icon: "infoCircle",
                    size: "S",
                    className: "coral-Form-fieldinfo error-info-icon"
                });
                $tagNameInput.parent()[0].appendChild(errorIcon);

                var errorTooltip = new Coral.Tooltip().set({
                    content: {
                        innerHTML: Granite.I18n.get("The name must not contain invalid chars like {0} , so replaced by {1}", [self._ILLEGAL_FILENAME_CHARS.toString().replace(/[,]/g, " "), "-"])
                    },
                    variant: 'inspect',
                    target: '#tag-name-textfield-fielderror',
                    placement: 'left',
                    id: "tag-name-textfield-fielderror-tooltip"
                });
                $tagNameInput.parent()[0].appendChild(errorTooltip);
                //auto-populate Name field using Title field by replacing restricted chars not valid for JCR complaint Names
                $tagNameInput.val(enteredText.toLowerCase().replace(self._ILLEGAL_FILENAME_REGEX, "-"));
            } else {
                //auto-populate Name field using Title field
                $tagNameInput.val(enteredText.toLowerCase());
            }
        }

    });

 	var NON_VALID_CHARS = "%#/\\:*?\"[]|\n\t\r. ";

    $(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
        selector: "[data-foundation-validation~='foundation.jcr.autoname'],[data-validation~='foundation.jcr.autoname']",
        validate: function(el) {
            //console.log($(el).find("input.js-coral-Autocomplete-textfield").val());
            var v = $(el).find("input.js-coral-Autocomplete-textfield").val();

            for (var i = 0, ln = v.length; i < ln; i++) {
                if (NON_VALID_CHARS.indexOf(v[i]) >= 0 || v.charCodeAt(i) > 127) {
                    //console.log("In fail");
                    return Granite.I18n.get("Invalid character '{0}'. It must be a valid JCR name.", [ v[i] ]);
                }
            }
        }
    });


})(document, Granite.$);
