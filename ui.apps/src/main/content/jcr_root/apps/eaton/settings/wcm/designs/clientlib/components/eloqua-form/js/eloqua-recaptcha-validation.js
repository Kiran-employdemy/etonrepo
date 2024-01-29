/**
 *
 *
 *
 * - THIS IS AN AUTOGENERATED FILE. DO NOT EDIT THIS FILE DIRECTLY -
 * - Generated by Gulp (gulp-babel).
 *
 *
 *
 *
 */


'use strict';

/* eslint-disable no-unused-vars, no-undef*/

/* Recaptcha callback executed on load - Can be found in eloqua.html, defined before recaptcha scripts */

var recaptchaSel = '.g-recaptcha';
var recaptchaErrorMessageSel = '#recaptcha-err-msg-form';
var recaptchaResponseSel = '#g-recaptcha-response';
var eloquaSubmitButtonSel = '.elq-form input[type="submit"]';

/* callback func for g-recaptcha attribute: data-callback */
var recaptchaSuccess = function recaptchaSuccess() {

  $(recaptchaErrorMessageSel).is(':visible') && $(recaptchaErrorMessageSel).hide();
  $(eloquaSubmitButtonSel).prop('disabled', false);
  $(eloquaSubmitButtonSel).css('cursor', 'pointer');
};
/* callback func for g-recaptcha attributes: data-expired-callback, data-error-callback */
var recaptchaPreventSubmission = function recaptchaPreventSubmission() {

  $(eloquaSubmitButtonSel).prop('disabled', true);
  $(eloquaSubmitButtonSel).css('cursor', 'not-allowed');
};

/* called on form submit */
var recaptchaSubmit = function recaptchaSubmit() {

  if ($(recaptchaResponseSel).val().length === 0) {
    $(recaptchaErrorMessageSel).is(':hidden') && $(recaptchaErrorMessageSel).show();
    recaptchaPreventSubmission();
    return false;
  } else {
    return true;
  }
};

if (dataLayer.form === 'yes') {

  $(function () {

    if ($(recaptchaSel).length > 0) {
      $(eloquaSubmitButtonSel).on('click', recaptchaSubmit);
    }
  });
}

$(document).ready(function () {

  // alert('Length 12 : ' + formValues.length);
  /* eslint-disable no-undef*/
  if (typeof formValues !== 'undefined') {
    for (var i = 0; i < formValues.length; i++) {
      // alert('form : ' + formValues[i]);
      if (typeof formValues[i] !== 'undefined') {
        (function () {

          var disclaimerId = 'eatondisclaimer_form' + formValues[i];
          var hiddendisclaimerId = 'tag_Optin_form' + formValues[i];

          $('#' + disclaimerId).change(function () {
            $('.' + hiddendisclaimerId).val(this.checked ? 1 : 0);
          });
        })();
      }
    }
  }
  function rescaleCaptcha() {
    var screenWidth = $('.g-recaptcha').parent().width();
    var S = 0;
    var bScale = 0;
    if (screenWidth < 302) {
      S = screenWidth / 302;
    } else {
      S = 1.0;
    }
    if (screenWidth < 258) {
      bScale = screenWidth / 258;
    } else {
      bScale = 1.0;
    }
    $('.g-recaptcha').css('transform', 'scale(' + S + ')');
    $('.g-recaptcha').css('-webkit-transform', 'scale(' + S + ')');
    $('.g-recaptcha').css('transform-origin', '0 0');
    $('.g-recaptcha').css('-webkit-transform-origin', '0 0');
  }
  rescaleCaptcha();
  $(window).resize(function () {
    rescaleCaptcha();
  });
});

var loadFieldsWithProgressiveProfile = function loadFieldsWithProgressiveProfile() {
  var configArray = void 0;
  if (typeof formValues !== 'undefined') {
    for (var i = 0; i < formValues.length; i++) {
      try {
        if (eval('config')) {
          // alert("loadFieldsWithPrepopulation inside "+' '+formValues[i]);
          configArray = eval('config');
          lookup = [];

          for (i = 0; i < configArray.numFields; i++) {
            lookup[i] = i;
          }

          for (i = 0; i < configArray.numFields; i++) {
            li = lookup[i];
            // if (finalRevealed.indexOf(li + '') >= 0) continue;
            pField = document.querySelector('#form' + configArray.formId + ' #epp' + li);
            pField.style.display = 'none';
          }
          // alert("ALL unloaded");
          revealed = [];

          if (configArray.mode === 'list') {
            var _li = null;
            var _i = null;
            var _lookup = [];

            for (_i = 0; _i < configArray.numFields; _i++) {
              _lookup[_i] = _i;
            }

            if (configArray.randomize) {
              // alert('randomize on');
              var x = null;
              var t = null;
              for (_i = 0; _i < configArray.numFields; _i++) {
                x = Math.floor(Math.random() * configArray.numFields);
                t = _lookup[_i];
                // alert('lookup[i] ' +lookup[i]);
                _lookup[_i] = _lookup[x];
                // alert('lookup[x]' + lookup[x]);
                _lookup[x] = t;
              }
            }

            // alert('configArray.numFields '+configArray.numFields+' configArray.numToReveal '+configArray.numToReveal+' revealed.length '+revealed.length);

            for (_i = 0; _i < configArray.numFields; _i++) {
              // alert('configArray fields '+i);
              _li = _lookup[_i];
              // alert('configArray fields '+li);
              if (revealed.length === configArray.numToReveal) {
                break;
              }
              if (revealed.indexOf(_li + '') >= 0) {
                continue;
              }
              pField = document.querySelector('#form' + configArray.formId + ' #epp' + _li);

              // alert('1');
              var fieldVerified = 'false';
              if (pField.hasChildNodes && pField.childNodes[0] !== 'undefined') {
                if (pField.childNodes[0].hasChildNodes && pField.childNodes[0].childNodes[1]) {
                  var selectField = pField.childNodes[0].childNodes[1];
                  if (selectField !== 'undefined' && (selectField.type === 'select-one' || selectField.type === 'select-multiple')) {
                    // alert('2 : '+li);
                    // alert('a : '+selectField+' 1 : '+selectField.selectedIndex+' 2 : '+selectField.options[selectField.selectedIndex]+' 3 :');
                    if (selectField.selectedIndex !== 'undefined' && selectField.selectedIndex !== -1 && selectField.options[selectField.selectedIndex] !== 'undefined') {
                      // alert('a : '+selectField+' 1 : '+selectField.selectedIndex+' 2 : '+selectField.options[selectField.selectedIndex]+' 3 : '+selectField.options[selectField.selectedIndex].index);
                      var selectFieldValue = selectField.options[selectField.selectedIndex].index;
                      // alert('selectFieldValue '+selectFieldValue);
                      if (selectFieldValue !== 0) {
                        // alert('Hiding Select Fields +#epp' + li);
                        pField.style.display = 'none';
                        fieldVerified = 'true';
                      } else {
                        // alert('Showing Select Fields +#epp' + li);
                        showField(pField, _li);
                        fieldVerified = 'true';
                      }
                    } else {
                      // alert('Showing Select Fields in Second loop+#epp' + li);
                      showField(pField, _li);
                      fieldVerified = 'true';
                    }
                  }
                }
              }

              if (fieldVerified === 'false') {
                if (!fieldHasValue(pField)) {
                  // alert('Showing Fields +#epp' + li);
                  showField(pField, _li);
                } else {
                  // alert('Hiding Fields +#epp' + li);
                  pField.style.display = 'none';
                }
              }
            }
          } else {
            var group = void 0;
            for (var _i2 = 0; _i2 < configArray.numStages; _i2++) {
              group = document.querySelector('#form' + configArray.formId + ' #pps' + _i2);
              if (!groupHasPreviousValues(group) || _i2 === configArray.numStages - 1) {
                // alert('Showing Group +#epp' + i);
                showGroup(group, _i2);
                break;
              }
            }
          }
        }
      } catch (e) {
        console.log('exception in loadFieldsWithProgressiveProfile');
      }
    }
  }
};

var formRuleBuilder = function formRuleBuilder() {
  var ruleData = document.getElementById('eloquaRuleData');
  if (ruleData && typeof ruleData.dataset.eloquaRules !== 'undefined') {
    var eloquaDom = document.getElementById('eloquaRuleData');
    if (eloquaDom) {
      (function () {
        var rulesData = document.getElementById('eloquaRuleData').dataset.eloquaRules;
        var rulesJSON = JSON.parse(rulesData);

        var _loop = function _loop(count) {
          var formRuleField = rulesJSON[count].conditionField;
          var formRuleFieldElement = '[name="' + formRuleField + '"]';
          if (formRuleFieldElement) {
            var formRuleConditionType = rulesJSON[count].conditionType;
            formOperations(rulesJSON[count]);
            if (formRuleConditionType !== 'ON_LOAD') {
              var formRuleFieldSel = document.querySelector(formRuleFieldElement);
              // For text fields:
              if (formRuleFieldSel.type === 'text') {
                formRuleFieldSel.addEventListener('input', function () {
                  return formOperations(rulesJSON[count]);
                });
              } else {
                // For other field types:
                formRuleFieldSel.addEventListener('change', function () {
                  return formOperations(rulesJSON[count]);
                });
              }
            }
          }
        };

        for (var count = 0; count < rulesJSON.length; count++) {
          _loop(count);
        }
      })();
    }
  }
};

var elqFieldClassSelNew = 'form .layout > div.row';
var elqFieldClassSelOld = '.form-design-field';

var formOperations = function formOperations(value) {
  var formRuleActionTypeArray = value.actions;
  for (var count = 0; count < formRuleActionTypeArray.length; count++) {
    // for (let actionItem of formRuleActionTypeArray) {
    var formRuleActionType = formRuleActionTypeArray[count].formRuleActionType;
    var conditionText = value.conditionText;
    var formRuleConditionType = value.conditionType;
    var formRuleField = value.conditionField;
    var _formRuleFieldElement = '[name="' + formRuleField + '"]';
    var formRuleTargetFieldArray = value.actions[0];
    var formRuleTargetField = formRuleActionTypeArray[count].formRuleTargetField;
    var formRuleTargetFieldElement = '[name="' + formRuleTargetField + '"]';
    var formRuleTargetElement = void 0;
    var ruleFieldElementValue = void 0;
    var checkCondition = void 0;
    var formRuleParentTarget = void 0;
    var elqFieldClassSel = $(formRuleTargetFieldElement).parents().is(elqFieldClassSelNew) ? elqFieldClassSelNew : elqFieldClassSelOld;

    if ($(_formRuleFieldElement).is('input[type=text]')) {
      formRuleTargetElement = '[name="' + formRuleField + '"]';
      ruleFieldElementValue = $(formRuleTargetElement).val();
    } else if ($(_formRuleFieldElement).is('select')) {
      formRuleTargetElement = '[name~="' + formRuleField + '"] option:selected';
      ruleFieldElementValue = $(formRuleTargetElement).text();
    } else if ($(_formRuleFieldElement).is('input[type=checkbox]')) {
      formRuleTargetElement = '[name="' + formRuleField + '"]:checked';
      ruleFieldElementValue = $(formRuleTargetElement).val();
    } else if ($(_formRuleFieldElement).is('textarea')) {
      formRuleTargetElement = '[name="' + formRuleField + '"]';
      ruleFieldElementValue = $(formRuleTargetElement).val();
    }

    switch (formRuleActionType) {
      case 'Hide':
        checkCondition = eloquaFormOperator(conditionText, ruleFieldElementValue, formRuleTargetFieldElement, formRuleConditionType);
        if (checkCondition) {
          $(formRuleTargetFieldElement).parents(elqFieldClassSel).hide();
        } else {
          $(formRuleTargetFieldElement).parents(elqFieldClassSel).show();
        }
        break;

      case 'Show':
        checkCondition = eloquaFormOperator(conditionText, ruleFieldElementValue, formRuleTargetFieldElement, formRuleConditionType);
        if (checkCondition) {
          $(formRuleTargetFieldElement).parents(elqFieldClassSel).show();
        } else {
          $(formRuleTargetFieldElement).parents(elqFieldClassSel).hide();
        }
        break;

      case 'showRequired':
        {

          /*
          Eloqua uses LiveValidation lib to validate field validations set in Eloqua.
            To make a field "Show and Required" in Eloqua, all Field Validations need to be removed (Form > Field > Field Validation)
          Due to Eloqua's server side validation, form submission could fail if validations are not removed in Eloqua
            When form is initialized, Eloqua creates global LiveValidation objects
          for fields with Eloqua validations. For fields without validation, Eloqua creates a field element variable
          Each object's variable name is in the field label element's "for" attribute
          */

          var elqMultiFieldClass = 'list-order'; // should NOT be a selector
          var elqLabelClassSel = '.elq-label';

          var requiredLabelHtml = '<span class="elq-required" style="color:red">*</span>';
          var errorMsgRequired = 'This field is required';
          var errorMsgNoUrl = 'Value must not contain any URLs';
          var errorMsgNoHtml = 'Value must not contain any HTML';

          var targetFieldLabelObj = $(formRuleTargetFieldElement).parents().hasClass(elqMultiFieldClass) ? $(formRuleTargetFieldElement).parents(elqFieldClassSel).find(elqLabelClassSel) : $(formRuleTargetFieldElement).labels();

          var targetFieldLabelExistingText = targetFieldLabelObj.text();
          var targetFieldLvId = targetFieldLabelObj.attr('for');
          var targetFieldLvObj = window[targetFieldLvId];

          checkCondition = eloquaFormOperator(conditionText, ruleFieldElementValue, formRuleTargetFieldElement, formRuleConditionType);

          if (checkCondition) {

            targetFieldLvObj.add(Validate.Presence, { failureMessage: errorMsgRequired });
            targetFieldLabelObj.html(targetFieldLabelExistingText + requiredLabelHtml);
            $(formRuleTargetFieldElement).parents(elqFieldClassSel).show();
          } else {

            $(formRuleTargetFieldElement).parents(elqFieldClassSel).hide();
            targetFieldLvObj.validations = [];
            targetFieldLabelObj.html(targetFieldLabelExistingText.replace(requiredLabelHtml, ''));
            targetFieldLabelObj.text(targetFieldLabelExistingText.replace('*', ''));
          }
          break;
        }

      case 'Disable':

        checkCondition = eloquaFormOperator(conditionText, ruleFieldElementValue, formRuleTargetFieldElement, formRuleConditionType);

        if (checkCondition) {
          $(formRuleTargetFieldElement).attr('disabled', 'disabled');
        } else {
          $(formRuleTargetFieldElement).removeAttr('disabled');
        }
        break;
      case 'Enable':
        checkCondition = eloquaFormOperator(conditionText, ruleFieldElementValue, formRuleTargetFieldElement, formRuleConditionType);
        if (checkCondition) {
          $(formRuleTargetFieldElement).removeAttr('disabled');
        } else {
          $(formRuleTargetFieldElement).attr('disabled', 'disabled');
        }
        break;

      case 'clearValueOf':
        checkCondition = eloquaFormOperator(conditionText, ruleFieldElementValue, formRuleTargetFieldElement, formRuleConditionType);
        if (checkCondition) {
          if ($(formRuleTargetFieldElement).is('select')) {
            formRuleParentTarget = formRuleTargetFieldElement + ' option:first';
            $(formRuleParentTarget).prop('selected', true);
          } else if ($(formRuleTargetFieldElement).is('input[type=checkbox]')) {
            formRuleParentTarget = formRuleTargetFieldElement + ':checked';
            $(formRuleParentTarget).click();
          } else if ($(formRuleTargetFieldElement).is('input[type=text]')) {

            $(formRuleTargetFieldElement).val('');
          } else if ($(formRuleTargetFieldElement).is('textarea')) {

            $(formRuleTargetFieldElement).val('');
          }
        }

        break;
      case 'setFocus':
        checkCondition = eloquaFormOperator(conditionText, ruleFieldElementValue, formRuleTargetFieldElement, formRuleConditionType);
        if (checkCondition) {
          $(formRuleTargetFieldElement).focus();
        }
        break;
      case 'hideOnLoad':

        $(formRuleTargetFieldElement).parents(elqFieldClassSel).hide();
        break;

    }
  }
};

function eloquaFormOperator(condition, action, element, operator) {

  var conditionText = condition ? condition.toUpperCase() : condition;
  var ruleFieldElementValue = action ? action.toUpperCase() : action;
  var formRuleTargetFieldElement = element;
  var formRuleConditionType = operator;
  var formRuleConditionTypeValue = false;
  var checkString = void 0;
  var checkLength = void 0;
  switch (formRuleConditionType) {
    case 'EQUALS_TO':
      if (conditionText === ruleFieldElementValue) {
        formRuleConditionTypeValue = true;
      }
      break;
    case 'NOT_EQUALS_TO':
      if (conditionText !== ruleFieldElementValue) {
        formRuleConditionTypeValue = true;
      }
      break;
    case 'STARTS_WITH':
      checkString = ruleFieldElementValue.substring(0, conditionText.length);
      if (conditionText === checkString) {
        formRuleConditionTypeValue = true;
      }
      break;
    case 'ENDS_WITH':
      checkLength = ruleFieldElementValue.length - conditionText.length;
      checkString = ruleFieldElementValue.substring(checkLength, ruleFieldElementValue.length);
      if (conditionText === checkString) {
        formRuleConditionTypeValue = true;
      }
      break;
    case 'CONTAINS':
      if (ruleFieldElementValue.indexOf(conditionText) > -1) {
        formRuleConditionTypeValue = true;
      }
      break;
    case 'IS_EMPTY':
      if (ruleFieldElementValue === '') {
        formRuleConditionTypeValue = true;
      }
      break;
    case 'IS_NOT_EMPTY':
      if (!(ruleFieldElementValue === '')) {
        formRuleConditionTypeValue = true;
      }
      break;
    case 'ON_LOAD':
      formRuleConditionTypeValue = true;
      break;
    case 'REG_EXP':
    const countryArray = conditionText.split("|");
      if (countryArray.indexOf(ruleFieldElementValue) > -1) {
        formRuleConditionTypeValue = true;
      }
            break;
  }
  return formRuleConditionTypeValue;
}

/* eslint-enable no-undef, no-unused-vars, new-cap*/

window.addEventListener('DOMContentLoaded', function (event) {
  formRuleBuilder();
});