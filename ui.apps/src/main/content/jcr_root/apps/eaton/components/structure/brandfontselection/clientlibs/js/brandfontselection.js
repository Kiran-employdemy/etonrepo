$(document).ready(function () {
    $('.primary-brand-font-select').closest('.x-form-item').wrapAll('<div class="brand-font-selection-padding">');
    $('.primary-brand-font-medium-select').closest('.x-form-item').wrapAll('<div class="brand-font-selection-padding">');
    $('.primary-brand-font-bold-select').closest('.x-form-item').wrapAll('<div class="brand-font-selection-padding">');
    $('.body-font-select').closest('.x-form-item').wrapAll('<div class="brand-font-selection-padding">');
    $('.brand-font-selection-padding').css('padding', '15px');
    var primaryBrandFontElem = $('input[name="./primaryBrandFont"]');
    var primaryBrandFontTextElem = primaryBrandFontElem.closest('.x-panel-body').find('.x-form-field-wrap');
    var primaryBrandFontFallbackFontElem = $('input[name="./primaryBrandFontFallbackFont"]');
    var primaryBrandFontFallbackFontTextElem = primaryBrandFontFallbackFontElem.closest('.x-panel-body').find('.x-form-field-wrap');
    var primaryBrandFontMediumElem = $('input[name="./primaryBrandFontMedium"]');
    var primaryBrandFontMediumFallbackFontElem = $('input[name="./primaryBrandFontMediumFallbackFont"]');
    var primaryBrandFontBoldElem = $('input[name="./primaryBrandFontBold"]');
    var usePrimaryBrandFontCheckboxOneElem = $('input[name="./usePrimaryBrandFontCheckboxOne"]');
    var usePrimaryBrandFontCheckboxTwoElem = $('input[name="./usePrimaryBrandFontCheckboxTwo"]');
    var primaryBrandFontBoldFallbackFontElem = $('input[name="./primaryBrandFontBoldFallbackFont"]');
    $('.brand-font-selection-dialog').click(function () {
        dialog.show();
        var primaryBrandFontMediumTextElem = primaryBrandFontMediumElem.closest('.x-panel-body').find('.x-form-field-wrap');
        var primaryBrandFontMediumFallbackFontTextElem = primaryBrandFontMediumFallbackFontElem.closest('.x-panel-body').find('.x-form-field-wrap');
        var primaryBrandFontBoldTextElem = primaryBrandFontBoldElem.closest('.x-panel-body').find('.x-form-field-wrap');
        var primaryBrandFontBoldFallbackFontTextElem = primaryBrandFontBoldFallbackFontElem.closest('.x-panel-body').find('.x-form-field-wrap');
        primaryBrandFontTextElem.addClass('brand-font');
        primaryBrandFontFallbackFontTextElem.addClass('brand-font-fallback');
        primaryBrandFontMediumTextElem.addClass('brand-font-medium');
        primaryBrandFontMediumFallbackFontTextElem.addClass('brand-font-medium-fallback');
        primaryBrandFontBoldTextElem.addClass('brand-font-bold');
        primaryBrandFontBoldFallbackFontTextElem.addClass('brand-font-bold-fallback');
        if (usePrimaryBrandFontCheckboxOneElem.prop('checked')) {
            applyPrimaryBrandFontValues(primaryBrandFontMediumElem, primaryBrandFontMediumFallbackFontElem);
        }
        if (usePrimaryBrandFontCheckboxTwoElem.prop('checked')) {
            applyPrimaryBrandFontValues(primaryBrandFontBoldElem, primaryBrandFontBoldFallbackFontElem);
        }

        $(usePrimaryBrandFontCheckboxOneElem).click(function () {
            if ($(this).prop('checked'))
                applyPrimaryBrandFontValues(primaryBrandFontMediumElem, primaryBrandFontMediumFallbackFontElem);
            else {
                enableFields(primaryBrandFontMediumElem, primaryBrandFontMediumFallbackFontElem);
                var primaryBrandFontMediumComboListName = primaryBrandFontMediumElem.closest('.x-panel-body').find('.x-form-field.x-form-hidden').attr('name').replace('./', '');
                var primaryBrandFontMediumComboListElem = $('[class*="' + primaryBrandFontMediumComboListName + '-0"]');
                var primaryBrandFontMediumFallbackFontComboListName = primaryBrandFontMediumFallbackFontElem.closest('.x-panel-body').find('.x-form-field.x-form-hidden').attr('name').replace('./', '');
                var primaryBrandFontMediumFallbackFontComboListElem = $('[class*="' + primaryBrandFontMediumFallbackFontComboListName + '-0"]');
                selectDropDownOption(primaryBrandFontMediumElem, primaryBrandFontMediumComboListElem);
                selectDropDownOption(primaryBrandFontMediumFallbackFontElem, primaryBrandFontMediumFallbackFontComboListElem);
            }
        });

        $(usePrimaryBrandFontCheckboxTwoElem).click(function () {
            if ($(this).prop('checked'))
                applyPrimaryBrandFontValues(primaryBrandFontBoldElem, primaryBrandFontBoldFallbackFontElem);
            else {
                enableFields(primaryBrandFontBoldElem, primaryBrandFontBoldFallbackFontElem);
                var primaryBrandFontBoldComboListName = primaryBrandFontBoldElem.closest('.x-panel-body').find('.x-form-field.x-form-hidden').attr('name').replace('./', '');
                var primaryBrandFontBoldComboListElem = $('[class*="' + primaryBrandFontBoldComboListName + '-0"]');
                var primaryBrandFontBoldFallbackFontComboListName = primaryBrandFontBoldFallbackFontElem.closest('.x-panel-body').find('.x-form-field.x-form-hidden').attr('name').replace('./', '');
                var primaryBrandFontBoldFallbackFontComboListElem = $('[class*="' + primaryBrandFontBoldFallbackFontComboListName + '-0"]');
                selectDropDownOption(primaryBrandFontBoldElem, primaryBrandFontBoldComboListElem);
                selectDropDownOption(primaryBrandFontBoldFallbackFontElem, primaryBrandFontBoldFallbackFontComboListElem);
            }
        });
    });


    function applyPrimaryBrandFontValues($fontElement, $fallbackFontElem) {
        $fontElement.val(primaryBrandFontElem.val());
        $fontElement.closest('.x-panel-body').find('.x-form-field-wrap input').val(primaryBrandFontTextElem.find('input').val());
        $fallbackFontElem.val(primaryBrandFontFallbackFontElem.val());
        $fallbackFontElem.closest('.x-panel-body').find('.x-form-field-wrap input').val(primaryBrandFontFallbackFontTextElem.find('input').val());
        disableFields($fontElement, $fallbackFontElem);
    }

    function disableFields($fontElement, $fallbackFontElem) {
        $fontElement.closest('.x-panel-body').find('.x-form-item.x-hide-label').css({'cursor': 'not-allowed', 'opacity': '0.5'});
        $fontElement.closest('.x-panel-body').find('.x-form-element').css('pointer-events', 'none');
        $fallbackFontElem.closest('.x-panel-body').find('.x-form-item.x-hide-label').css({'cursor': 'not-allowed', 'opacity': '0.5'});
        $fallbackFontElem.closest('.x-panel-body').find('.x-form-element').css('pointer-events', 'none');
    }

    function enableFields($fontElement, $fallbackFontElem) {
        $fontElement.closest('.x-panel-body').find('.x-form-item.x-hide-label').css({'cursor': '', 'opacity': ''});
        $fontElement.closest('.x-panel-body').find('.x-form-element').css('pointer-events', '');
        $fallbackFontElem.closest('.x-panel-body').find('.x-form-item.x-hide-label').css({'cursor': '', 'opacity': ''});
        $fallbackFontElem.closest('.x-panel-body').find('.x-form-element').css('pointer-events', '');
    }

    function selectDropDownOption($fontElement, $targetElem) {
        var dropdownVal = $fontElement.closest('.x-panel-body').find('.x-form-field-wrap input').val();
        $targetElem.find('.x-combo-list-inner').find('div').each(function () {
            if ($(this).text() === dropdownVal) {
                $(this).addClass('x-combo-selected');
                var optionTop = $(this).offset().top;
                var selectContainerElem = $(this).parent('.x-combo-list-inner');
                var selectTop = selectContainerElem.offset().top;
                selectContainerElem.scrollTop(selectContainerElem.scrollTop() + (optionTop - selectTop));
            } else {
                $(this).removeClass('x-combo-selected');
            }
        });
    }

    $(document).on('mouseover', '.x-combo-list-item', function () {
        $(this).parent('.x-combo-list-inner').find('.x-combo-list-item').removeClass('x-combo-selected');
        $(this).addClass('x-combo-selected');
    });

    $(document).on('click', '.brand-font, .brand-font-fallback, .brand-font-medium, .brand-font-medium-fallback, .brand-font-bold, .brand-font-bold-fallback', function (event) {
        event.stopPropagation();
        var $thisElem = $(this);
        var comboListName = $thisElem.closest('.x-panel-body').find('.x-form-field.x-form-hidden').attr('name').replace('./', '');
        var comboListElem = $('[class*="' + comboListName + '-0"]');
        selectDropDownOption($thisElem, comboListElem);
    });

    $(document).on('click', '.x-combo-list', function () {
        if ($('[class*="primaryBrandFont-0"]') || $('[class*="primaryBrandFontFallbackFont-0"]')) {
            if (usePrimaryBrandFontCheckboxOneElem.prop('checked')) {
                applyPrimaryBrandFontValues(primaryBrandFontMediumElem, primaryBrandFontMediumFallbackFontElem);
            }
            if (usePrimaryBrandFontCheckboxTwoElem.prop('checked')) {
                applyPrimaryBrandFontValues(primaryBrandFontBoldElem, primaryBrandFontBoldFallbackFontElem);
            }
        }
    });
});
