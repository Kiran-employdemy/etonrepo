/* eslint-disable no-unused-vars, no-undef*/

/* Recaptcha callback executed on load - Can be found in eloqua.html, defined before recaptcha scripts */

const recaptchaSel = '.g-recaptcha';
const recaptchaErrorMessageSel = '#recaptcha-err-msg-form';
const recaptchaResponseSel = '#g-recaptcha-response';
const eloquaSubmitButtonSel = '.elq-form input[type="submit"]';

/* callback func for g-recaptcha attribute: data-callback */
const recaptchaSuccess = () => {

  $(recaptchaErrorMessageSel).is(':visible') && $(recaptchaErrorMessageSel).hide();
  $(eloquaSubmitButtonSel).prop('disabled', false);
  $(eloquaSubmitButtonSel).css('cursor', 'pointer');
};
/* callback func for g-recaptcha attributes: data-expired-callback, data-error-callback */
const recaptchaPreventSubmission = () => {

  $(eloquaSubmitButtonSel).prop('disabled', true);
  $(eloquaSubmitButtonSel).css('cursor', 'not-allowed');
};

/* called on form submit */
const recaptchaSubmit = () => {

  if ($(recaptchaResponseSel).val().length === 0) {
    $(recaptchaErrorMessageSel).is(':hidden') && $(recaptchaErrorMessageSel).show();
    recaptchaPreventSubmission();
    return false;
  } else {
    return true;
  }
};

if (dataLayer.form === 'yes') {

  $(function() {

    if ($(recaptchaSel).length > 0) {
      $(eloquaSubmitButtonSel).on('click', recaptchaSubmit);
    }
  });
}

$(document).ready(function() {

    // alert('Length 12 : ' + formValues.length);
	/* eslint-disable no-undef*/
  if (typeof formValues !== 'undefined') {
    for (let i = 0; i < formValues.length; i++) {
            // alert('form : ' + formValues[i]);
      if (typeof formValues[i] !== 'undefined') {


        let disclaimerId = 'eatondisclaimer_form' + formValues[i];
        let hiddendisclaimerId = 'tag_Optin_form' + formValues[i];

        $('#' + disclaimerId).change(function() {
          $('.' + hiddendisclaimerId).val(this.checked ? 1 : 0);
        });
      }
    }
  }
  function rescaleCaptcha() {
    const screenWidth = $('.g-recaptcha').parent().width();
    let S = 0;
    let bScale = 0;
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
  $(window).resize(function() { rescaleCaptcha();});
});

const loadFieldsWithProgressiveProfile = () => {
  let configArray;
  if (typeof formValues !== 'undefined') {
    for (let i = 0; i < formValues.length; i++) {
      try {
        if (eval('config')) {
                    // alert("loadFieldsWithPrepopulation inside "+' '+formValues[i]);
          configArray = eval('config');
          lookup = [];

          for (i = 0;i < configArray.numFields;i++) {
            lookup[i] = i;
          }

          for (i = 0;i < configArray.numFields;i++) {
            li = lookup[i];
                        // if (finalRevealed.indexOf(li + '') >= 0) continue;
            pField = document.querySelector('#form' + configArray.formId + ' #epp' + li);
            pField.style.display = 'none';
          }
                    // alert("ALL unloaded");
          revealed = [];

          if (configArray.mode === 'list') {
            let li = null;
            let i = null;
            let lookup = [];

            for (i = 0;i < configArray.numFields;i++) {
              lookup[i] = i;
            }

            if (configArray.randomize) {
                            // alert('randomize on');
              let x = null;
              let t = null;
              for (i = 0;i < configArray.numFields;i++) {
                x = Math.floor(Math.random() * configArray.numFields);
                t = lookup[i];
                                // alert('lookup[i] ' +lookup[i]);
                lookup[i] = lookup[x];
                                // alert('lookup[x]' + lookup[x]);
                lookup[x] = t;
              }
            }

                        // alert('configArray.numFields '+configArray.numFields+' configArray.numToReveal '+configArray.numToReveal+' revealed.length '+revealed.length);

            for (i = 0; i < configArray.numFields; i++) {
                            // alert('configArray fields '+i);
              li = lookup[i];
                            // alert('configArray fields '+li);
              if (revealed.length === configArray.numToReveal) {break;}
              if (revealed.indexOf(li + '') >= 0) {continue;}
              pField = document.querySelector('#form' + configArray.formId + ' #epp' + li);

                            // alert('1');
              let fieldVerified = 'false';
              if ((pField.hasChildNodes) && (pField.childNodes[0] !== 'undefined')) {
                if ((pField.childNodes[0].hasChildNodes) && (pField.childNodes[0].childNodes[1])) {
                  let selectField = pField.childNodes[0].childNodes[1];
                  if ((selectField !== 'undefined') && ((selectField.type === 'select-one') || (selectField.type === 'select-multiple'))) {
                                        // alert('2 : '+li);
                                        // alert('a : '+selectField+' 1 : '+selectField.selectedIndex+' 2 : '+selectField.options[selectField.selectedIndex]+' 3 :');
                    if (((selectField.selectedIndex !== 'undefined') && (selectField.selectedIndex !== -1)) && (selectField.options[selectField.selectedIndex] !== 'undefined')) {
                                            // alert('a : '+selectField+' 1 : '+selectField.selectedIndex+' 2 : '+selectField.options[selectField.selectedIndex]+' 3 : '+selectField.options[selectField.selectedIndex].index);
                      let selectFieldValue = selectField.options[selectField.selectedIndex].index;
                                            // alert('selectFieldValue '+selectFieldValue);
                      if (selectFieldValue !== 0) {
                                                // alert('Hiding Select Fields +#epp' + li);
                        pField.style.display = 'none';
                        fieldVerified = 'true';
                      } else {
                                                // alert('Showing Select Fields +#epp' + li);
                        showField(pField, li);
                        fieldVerified = 'true';
                      }
                    } else {
                                            // alert('Showing Select Fields in Second loop+#epp' + li);
                      showField(pField, li);
                      fieldVerified = 'true';
                    }
                  }
                }
              }

              if (fieldVerified === 'false') {
                if (!fieldHasValue(pField))
                                {
                                    // alert('Showing Fields +#epp' + li);
                  showField(pField, li);
                } else {
                                    // alert('Hiding Fields +#epp' + li);
                  pField.style.display = 'none';
                }
              }
            }
          } else {
            let group;
            for (let i = 0; i < configArray.numStages; i++) {
              group = document.querySelector('#form' + configArray.formId + ' #pps' + i);
              if (!groupHasPreviousValues(group) || (i === (configArray.numStages - 1))) {
                            // alert('Showing Group +#epp' + i);
                showGroup(group, i);
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

const formRuleBuilder = () => {
  let ruleData = document.getElementById('eloquaRuleData');
  if (ruleData && typeof ruleData.dataset.eloquaRules !== 'undefined') {
    let eloquaDom = document.getElementById('eloquaRuleData');
    if (eloquaDom) {
      let rulesData = document.getElementById('eloquaRuleData').dataset.eloquaRules;
      let rulesJSON = JSON.parse(rulesData);

      for (let count = 0; count < rulesJSON.length; count++) {
        let formRuleField = rulesJSON[count].conditionField;
        let formRuleFieldElement = '[name="' + formRuleField + '"]';
        if (formRuleFieldElement) {
          let formRuleConditionType = rulesJSON[count].conditionType;
          formOperations(rulesJSON[count]);
          if (formRuleConditionType !== 'ON_LOAD') {
            let formRuleFieldSel = document.querySelector(formRuleFieldElement);
            // For text fields:
            if (formRuleFieldSel.type === 'text') {
              formRuleFieldSel.addEventListener('input', () => formOperations(rulesJSON[count]));
            } else {
              // For other field types:
              formRuleFieldSel.addEventListener('change', () => formOperations(rulesJSON[count]));
            }
          }
        }
      }
    }
  }
};


const elqFieldClassSelNew = 'form .layout > div.row';
const elqFieldClassSelOld = '.form-design-field';

let formOperations = (value) => {
  let formRuleActionTypeArray = value.actions;
  for ( let count = 0; count < formRuleActionTypeArray.length; count++) {
  // for (let actionItem of formRuleActionTypeArray) {
    let formRuleActionType = formRuleActionTypeArray[count].formRuleActionType;
    let conditionText = value.conditionText;
    let formRuleConditionType = value.conditionType;
    let formRuleField = value.conditionField;
    let formRuleFieldElement = '[name="' + formRuleField + '"]';
    let formRuleTargetFieldArray = value.actions[0];
    let formRuleTargetField = formRuleActionTypeArray[count].formRuleTargetField;
    let formRuleTargetFieldElement = '[name="' + formRuleTargetField + '"]';
    let formRuleTargetElement;
    let ruleFieldElementValue;
    let checkCondition;
    let formRuleParentTarget;
    let elqFieldClassSel = $(formRuleTargetFieldElement).parents().is(elqFieldClassSelNew) ? elqFieldClassSelNew : elqFieldClassSelOld;

    if ($(formRuleFieldElement).is('input[type=text]')) {
      formRuleTargetElement = '[name="' + formRuleField + '"]';
      ruleFieldElementValue = $(formRuleTargetElement).val();
    } else if ($(formRuleFieldElement).is('select')) {
      formRuleTargetElement = '[name~="' + formRuleField + '"] option:selected';
      ruleFieldElementValue = $(formRuleTargetElement).text();
    } else if ($(formRuleFieldElement).is('input[type=checkbox]')) {
      formRuleTargetElement = '[name="' + formRuleField + '"]:checked';
      ruleFieldElementValue = $(formRuleTargetElement).val();
    } else if ($(formRuleFieldElement).is('textarea')) {
      formRuleTargetElement = '[name="' + formRuleField + '"]';
      ruleFieldElementValue = $(formRuleTargetElement).val();
    }

    switch (formRuleActionType) {
        case 'Hide' :
          checkCondition = eloquaFormOperator(conditionText,ruleFieldElementValue,formRuleTargetFieldElement,formRuleConditionType);
          if (checkCondition) {
            $(formRuleTargetFieldElement).parents(elqFieldClassSel).hide();
          } else {
            $(formRuleTargetFieldElement).parents(elqFieldClassSel).show();
          }
          break;

        case 'Show' :
          checkCondition = eloquaFormOperator(conditionText,ruleFieldElementValue,formRuleTargetFieldElement,formRuleConditionType);
          if (checkCondition) {
            $(formRuleTargetFieldElement).parents(elqFieldClassSel).show();
          } else {
            $(formRuleTargetFieldElement).parents(elqFieldClassSel).hide();
          }
          break;

        case 'showRequired' : {

          /*
          Eloqua uses LiveValidation lib to validate field validations set in Eloqua.

          To make a field "Show and Required" in Eloqua, all Field Validations need to be removed (Form > Field > Field Validation)
          Due to Eloqua's server side validation, form submission could fail if validations are not removed in Eloqua

          When form is initialized, Eloqua creates global LiveValidation objects
          for fields with Eloqua validations. For fields without validation, Eloqua creates a field element variable
          Each object's variable name is in the field label element's "for" attribute
          */

          const elqMultiFieldClass = 'list-order'; // should NOT be a selector
          const elqLabelClassSel = '.elq-label';

          const requiredLabelHtml = '<span class="elq-required" style="color:red">*</span>';
          const errorMsgRequired = 'This field is required';
          const errorMsgNoUrl = 'Value must not contain any URLs';
          const errorMsgNoHtml = 'Value must not contain any HTML';


          let targetFieldLabelObj = $(formRuleTargetFieldElement).parents().hasClass(elqMultiFieldClass)
                                     ? $(formRuleTargetFieldElement).parents(elqFieldClassSel).find(elqLabelClassSel)
                                     : $(formRuleTargetFieldElement).labels();

          let targetFieldLabelExistingText = targetFieldLabelObj.text();
          let targetFieldLvId = targetFieldLabelObj.attr('for');
          let targetFieldLvObj = window[targetFieldLvId];

          checkCondition = eloquaFormOperator(conditionText,ruleFieldElementValue,formRuleTargetFieldElement,formRuleConditionType);

          if (checkCondition) {

            targetFieldLvObj.add(Validate.Presence, {failureMessage: errorMsgRequired});
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

          checkCondition = eloquaFormOperator(conditionText,ruleFieldElementValue,formRuleTargetFieldElement,formRuleConditionType);

          if (checkCondition) {
            $(formRuleTargetFieldElement).attr('disabled','disabled');
          } else {
            $(formRuleTargetFieldElement).removeAttr('disabled');
          }
          break;
        case 'Enable':
          checkCondition = eloquaFormOperator(conditionText,ruleFieldElementValue,formRuleTargetFieldElement,formRuleConditionType);
          if (checkCondition) {
            $(formRuleTargetFieldElement).removeAttr('disabled');
          } else {
            $(formRuleTargetFieldElement).attr('disabled', 'disabled');
          }
          break;

        case 'clearValueOf':
          checkCondition = eloquaFormOperator(conditionText,ruleFieldElementValue,formRuleTargetFieldElement,formRuleConditionType);
          if (checkCondition) {
            if ($(formRuleTargetFieldElement).is('select')) {
              formRuleParentTarget = formRuleTargetFieldElement + ' option:first';
              $(formRuleParentTarget).prop('selected',true);
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
          checkCondition = eloquaFormOperator(conditionText,ruleFieldElementValue,formRuleTargetFieldElement,formRuleConditionType);
          if (checkCondition) {
            $(formRuleTargetFieldElement).focus();
          }
          break;
        case 'hideOnLoad' :

          $(formRuleTargetFieldElement).parents(elqFieldClassSel).hide();
          break;

    }

  }
};

function eloquaFormOperator(condition,action,element,operator) {

  let conditionText = condition ? condition.toUpperCase() : condition;
  let ruleFieldElementValue = action ? action.toUpperCase() : action;
  let formRuleTargetFieldElement = element;
  let formRuleConditionType = operator;
  let formRuleConditionTypeValue = false;
  let checkString;
  let checkLength;
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
        checkString = ruleFieldElementValue.substring(0,conditionText.length);
        if (conditionText === checkString) {
          formRuleConditionTypeValue = true;
        }
        break;
      case 'ENDS_WITH':
        checkLength = ruleFieldElementValue.length - conditionText.length;
        checkString = ruleFieldElementValue.substring(checkLength,ruleFieldElementValue.length);
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
  }
  return formRuleConditionTypeValue;

}

/* eslint-enable no-undef, no-unused-vars, new-cap*/

window.addEventListener('DOMContentLoaded', (event) => {
  formRuleBuilder();
});
