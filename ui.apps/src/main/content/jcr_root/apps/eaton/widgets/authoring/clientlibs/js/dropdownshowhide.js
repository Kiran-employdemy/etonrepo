/**
 * Extension to the standard dropdown/select component. It enabled hidding/unhidding of multiple dialog fields based on the
 * selection made in the dropdown/select or on checkbox check. Additionally supports hiding of tabs if property set on tab
 * or all dialog fields on tab are hidden
 *
 * How to use:
 *
 * - add the class cq-dialog-dropdown-showhide to the dropdown/select element
 * - add the data attribute cq-dialog-dropdown-showhide-target to the dropdown/select element, value should be the
 *   selector, usually a specific class name, to find all possible target elements that can be shown/hidden.
 * - add the target class to each target component that can be shown/hidden
 * - add the class hidden to each target component to make them initially hidden
 * - add the attribute showhidetargetvalue to each target component, the value should equal the value of the select
 *   option that will unhide this element.
 */
(function(document, $) {
    "use strict";

    // when dialog gets injected
    $(document).on("foundation-contentloaded", function(e) {
       if(document.getElementById("sidepanel-guide-properties")){
          if(document.getElementsByName("./name")[0]){
             if(document.getElementsByName("./name")[0].length <= 1){
                 alert(Granite.I18n.get('Thank you path missing in Adaptive Form container or inappropriate taxonomies in PIM page path'));
             }
          }
       }
        // if there is already an inital value make sure the according target element becomes visible
        showHideHandler($(".cq-dialog-dropdown-showhide, .cq-dialog-checkbox-showhide", e.target));
    });

    $(document).on("selected", ".cq-dialog-dropdown-showhide", function(e) {
        showHideHandler($(this));
    });

    $(document).on("change", ".cq-dialog-checkbox-showhide", function(e) {
        showHideHandler($(this));
    });

    $(document).on("change", ".ttil-list-hide-show", function(e) {
        showHideHandler($(this));
    });

    function showHideHandler(el) {
        el.each(function (i, element) {
            if($(element).is("coral-select")) {
                // handle Coral3 base drop-down
                Coral.commons.ready(element, function (component) {
                    showHide(component, element);
                    component.on("change", function () {
                        showHide(component, element);
                    });
                });
            } else {
                // handle Coral2 based drop-down
                var component = $(element).data("select");
                if (component) {
                    showHide(component, element);
                }
            }
        })
    }

   function showHide(component, element) {
        var target = $(element).data("cqDialogDropdownShowhideTarget");
           if (target) {
               var value;
               if (component.value) {
                   value = component.value;
               } else {
                   value = component.getValue();
               }
               //hide target fields and tabs (except first tab)
               $(target).each(function() {
                   var $tabsContent = $(this).closest('.coral-TabPanel-pane'),
                       $tabsContents = $tabsContent.parent().find('.coral-TabPanel-pane'),
                       tabIndex = $tabsContents.index($tabsContent[0]),
                       $tabs = $(this).closest('.coral-TabPanel').find('.coral-TabPanel-navigation > .coral-TabPanel-tab');
                   if (tabIndex > 0) {
                       $tabs.eq(tabIndex).addClass('hide');
                   }
                   $(this).addClass('hide');
                   $(this).removeClass('newhide');
                   $(this).parent().children('label').addClass('hide');
                   $(this).parent().children('ul').addClass('hide');
                   $(this).parent().children('[data-init="quicktip"]').addClass('hide');
                   $(this).parent().children('coral-icon').addClass('hide');
                   $(this).parent().children('coral-tooltip').addClass('hide');
                   if(($(this).attr('data-init')== "pathbrowser") && $(this).hasClass("hide") && $(this).children().children('input').attr('aria-required')){
						$(this).parent().children().children().children('input').removeAttr('aria-required');
                       $(this).parent().children().children().children('input').attr("data-required", "added");
                   }
                   if ($(this).parent().children('ul').attr('data-init') === "taglist" && $(this).parent().children('ul').attr('aria-required')) {
                       $(this).parent().children('ul').removeAttr('aria-required');
                       $(this).parent().children('ul').attr("data-required", "added");
                   }
                   if($(this).parent().children('input').attr('aria-required')) {
						$(this).parent().children('input').removeAttr('aria-required');
                        $(this).parent().children('input').attr("data-required", "added");

                   }
                    if($(this).parent().children('textarea').attr('aria-required')) {
						$(this).parent().children('textarea').removeAttr('aria-required');
                        $(this).parent().children('textarea').attr("data-required", "added");

                   }
                   if($(this).attr('type') == 'checkbox') {
					  $(this).parent().parent().addClass('hide');
                   }
                   if ($(this).is("foundation-autocomplete") && $(this).hasClass("hide")) {
                      $(this).removeAttr('required');
                   }
               });


               //unhide proper fields
               $(target).each(function() {
                   var $this = $(this),
                       showHideTarget = $this.data('showhidetargetvalue') || $this.find('[data-showhidetargetvalue]').data('showhidetargetvalue'),
                       showOnChecked = $this.data('showonchecked'),
                       checked = $(element).prop('checked');
                   if (showOnChecked === undefined) {
                       showOnChecked = $this.find('[data-showonchecked]').data('showonchecked');
                   }
                   var checkboxCondition = checked === undefined || (checked && showOnChecked) || (checked && showOnChecked === undefined) || (!checked && showOnChecked == false);

				   var showHideTargetArray = showHideTarget ? showHideTarget.split(',') : [];
				   for (var i = 0; i < showHideTargetArray.length; i++) {
                       if(showHideTargetArray[i] == value){
                           if (showHideTarget !== undefined  && checkboxCondition) {
                               var $tabsContent = $(this).closest('.coral-TabPanel-pane'),
                                   $tabsContents = $tabsContent.parent().find('.coral-TabPanel-pane'),
                                   tabIndex = $tabsContents.index($tabsContent[0]),
                                   $tabs = $(this).closest('.coral-TabPanel').find('.coral-TabPanel-navigation > .coral-TabPanel-tab');
                               $tabs.eq(tabIndex).removeClass('hide');
                               if($(this).parent().children('input').attr('data-required')){
                                    $(this).parent().children('input').attr("aria-required","true");
                               }
                               if($(this).parent().children('textarea').attr('data-required')){
                                    $(this).parent().children('textarea').attr("aria-required","true");
                               }
                               if ($(this).parent().children('ul').attr('data-init') === "taglist" && $(this).parent().children('ul').attr('data-required')) {
                                   $(this).parent().children('ul').attr("aria-required", "true");
                               }
                               $(this).removeClass('hide');
                               $(this).addClass('newhide');
                               $(this).parent().children('label').removeClass('hide');
                               $(this).parent().children('ul').removeClass('hide');
                               $(this).parent().children('[data-init="quicktip"]').removeClass('hide');
                               $(this).parent().children('coral-icon').removeClass('hide');
                               $(this).parent().children('coral-tooltip').removeClass('hide');
                               //$(this).parent().children('input').attr("aria-required","true");
                               if( !$(this).hasClass("hide") && ($(this).attr('data-init')== "pathbrowser") && !$(this).hasClass("js-cq-TagsPickerField") && $(this).parent().children().children().children('input').attr('data-required')){
                                $(this).parent().children().children().children('input').attr("aria-required","true");
                               }
                               if($(this).attr('type') == 'checkbox') {
                                  $(this).parent().parent().removeClass('hide');
                               }
                               if ($(this).is("foundation-autocomplete") && !$(this).hasClass("hide")) {
                                     $(this).attr('required', "true");
                                     $(this).attr("aria-required", "true");
                                }
                                var featureval = $('#feature-drop').val();
                                var manualval = $('#manual-drop').val();

                               
                                $('.no-feature-list').parent().children('label').addClass('hide');
                                $('.no-feature-list').removeClass('newhide');  
                                $('.feature-list').parent().children('label').addClass('hide');
                                $('.feature-list').removeClass('newhide');

                                if(featureval === 'feature' && manualval === 'manual-list' ){

                                    $('.no-feature-list').removeClass('newhide');
                                    $('.feature-list').addClass('newhide');
                                    $('.no-feature-list').parent().children('label').addClass('hide');
                                    $('.feature-list').parent().children('label').removeClass('hide');

                                }
                                if(featureval === 'no-feature' && manualval === 'manual-list'){

                                    $('.no-feature-list').addClass('newhide');
                                    $('.feature-list').removeClass('newhide');
                                    $('.feature-list').parent().children('label').addClass('hide');
                                    $('.no-feature-list').parent().children('label').removeClass('hide');
                                    
                                    
                                }
                               
                           }

                       }
                   }

               });
           }
   }

})(document,Granite.$);


(function($, $document) {

    var CORAL_RTE = ".coral-RichText",

        fieldErrorEl = $("<span class='coral-Form-fielderror coral-Icon coral-Icon--alert coral-Icon--sizeS'" +
            "data-init='quicktip' data-quicktip-type='error' />");

    foundationReg = $(window).adaptTo("foundation-registry");
    $(document).on("foundation-contentloaded", function(e) {

        $(CORAL_RTE).after("<input type=text style='display:none'/>");

        $(CORAL_RTE).on("input", function() {
            var $invisibleText = $(this).nextAll("input:text").val($(this).text().trim());

            $invisibleText.checkValidity();

        })
    });

    foundationReg.register("foundation.validation.validator", {
        selector: ".richtext-container > input:text",
        validate: function(el) {

            var $form = $(el).closest("form.foundation-form");
            var $hidden = $form.find("input[type=hidden].coral-Textfield");

            isRequired = $hidden.attr("required") === true ||
                $hidden.attr("aria-required") === "true";

            var $check =  $hidden.closest("coral-Form-field coral-Textfield");
            if (isRequired && _.isEmpty($hidden.val())) {

               return $(el).message("validation.required") || "Please fill out this field";
            }
            return null;
        },
        show: function(el, message, ctx) {

            this.clear(el);
            fieldErrorEl.clone()
                .attr("data-quicktip-content", message)
                .insertAfter($(el));
            $(el).attr("aria-invalid", "true").toggleClass("is-invalid", true);
        },
        clear: function (el) {
             var $hid = $(el).removeAttr("aria-invalid").removeClass("is-invalid").nextAll(".coral-Form-fielderror");
          $hid.tooltip("hide").remove();

        }

    });
}(jQuery));