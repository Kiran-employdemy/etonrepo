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

// Eloqua pre-population script

$(document).ready(function () {
  // eslint-disable-next-line no-undef
  if (dataLayer.form === 'yes') {
    populateUrlParameterFields();
    populateHiddenTags();
  }
});

/**
 * Populate elements based on element's type
 */
var populateElements = function populateElements(formId, formElements, formElementsType) {

  var positiveCheckedValues = ['on', 'true', 'y', 'yes', 'checked', '1'];

  if (typeof formElements === 'undefined' || Object.keys(formElements).length === 0) {
    console.error(formElementsType, 'selector found no elements');
  } else {

    for (var k = 0; k < formElements.length; k++) {
      var elementObj = formElements[k];
      var elementName = elementObj.name;
      var elementType = elementObj.type;
      var logElement = elementType.concat(' ', formElementsType, ' ', elementName, ' on form #', formId);
      var newElementValue = null;

      try {

        // eslint-disable-next-line no-undef, new-cap
        newElementValue = elementType === 'hidden' ? eval(elementName + '_' + formId) : GetElqContentPersonalizationValue(elementName);
      } catch (err) {
        console.error('%s did not pre-populate. Html name (%s) for element is incorrect.', logElement, elementName);
      }

      switch (elementType) {
        case 'radio': // falls through to checkbox case's operations
        case 'checkbox':
          {
            var isChecked = positiveCheckedValues.includes(newElementValue.toLowerCase());
            if (isChecked) {
              elementObj.checked = true;
            } else {
              console.warn(logElement, 'did not pre-populate: Value not found in list of values to check (positiveCheckedValues)');
            }
            break;
          }
        case 'text': // falls through to select-one case's operations
        case 'select-one':
          {
            elementObj.value = newElementValue;
            break;
          }
        case 'select-multiple':
          {
            var newElementValueArray = newElementValue.split('::');
            if (typeof newElementValueArray === 'undefined') {
              console.warn(logElement, 'did not pre-populate: Value array not defined');
            } else {
              elementObj.value = newElementValueArray;
            }
            break;
          }
        case 'hidden':
          {
            elementObj.value = newElementValue;
            break;
          }
        default:
          {
            console.warn('Pre-population for ', elementType, '-type elements is not configured');
          }
      }
    }
  }
};

/**
 * Populate page url tag
 */
var populatePageUrlTag = function populatePageUrlTag(formId) {

  var pageTagName = 'tag_page';
  var isPopulatePageTag = eval(pageTagName + '_' + formId);
  var pageTagValue = isPopulatePageTag ? location.href : '';
  $('#form' + formId + ' [name="' + pageTagName + '"]').val(pageTagValue);
};

/**
 * Populate hidden tags
 */
var populateHiddenTags = function populateHiddenTags() {

  // eslint-disable-next-line no-undef
  if (typeof formValues === 'undefined' || formValues.length === 0) {
    console.error('Unable to start pre-population of hidden tags. formValues list is not found.');
  } else {

    // eslint-disable-next-line no-undef
    for (var i = 0; i < formValues.length; i++) {

      // eslint-disable-next-line no-undef
      var formId = formValues[i];
      var hiddenTags = $('#form' + formId + ' [type="hidden"][name^="tag_"]').not('[name^="tag_Optin"]').not('[name="tag_primaryProductTaxonomy"]').not('[name="tag_page"]').not('[name="tag_PercolateID"]');

      populateElements(formId, hiddenTags, 'tag');
      populatePageUrlTag(formId); // html name: tag_page
      populatePercolateContentIdFromDataLayer(formId); // html name: tag_PercolateID
      setPrimaryProductTaxonomy(formId); // html name: tag_primaryProductTaxonomy
    }
  }
};

/**
 * Populate form fields
 * (called by SetElqContent if user exists in Eloqua contact object model)
 */
var populateFormFields = function populateFormFields() {

  // eslint-disable-next-line no-undef
  if (typeof formValues === 'undefined' || formValues.length === 0) {
    console.error('Unable to start pre-population of hidden tags. formValues list is not found.');
  } else {

    // eslint-disable-next-line no-undef
    for (var i = 0; i < formValues.length; i++) {

      // eslint-disable-next-line no-undef
      var formId = formValues[i];
      var isEnabled = eval('prepopulationEnabled_' + formId);

      if (typeof isEnabled === 'undefined') {
        console.error('Pre-population not complete: Cannot find prepopulationEnabled variable for eloqua form.');
      } else if (isEnabled === 'false') {
        console.warn('Pre-population not complete: Author disabled pre-population for form #', formId);
      } else {
        var formFields = $('#form' + formId + ' [name^="C_"]');
        populateElements(formId, formFields, 'field');
      }
    }
  }
};

/**
 * Identify visitor to Eloqua data lookup service,
 * do pre-population, then load progressive profile fields
 * SetElqContent is called by Eloqua
 */
var FirstLookup = true;
// eslint-disable-next-line no-unused-vars
var SetElqContent = function SetElqContent() {
  if (FirstLookup) {
    // eslint-disable-next-line no-undef
    window._elqQ = window._elqQ || [];
    // eslint-disable-next-line no-undef, new-cap
    window._elqQ.push(['elqDataLookup', escape(LookupIdPrimary), '<' + PrimaryUniqueField + '>' + GetElqContentPersonalizationValue(VisitorUniqueField) + '</' + PrimaryUniqueField + '>']);
    FirstLookup = false;
  } else {
    populateFormFields();
    // eslint-disable-next-line no-undef
    loadFieldsWithProgressiveProfile();
  }
};

// START: Primary Product Taxonomy Pre-population
var pptLevelOneName = 'level_1_product';
var pptLevelOneSel = ' [name="' + pptLevelOneName + '"]';
var pptLevelTwoName = 'level_2_product';
var pptLevelTwoSel = ' [name="' + pptLevelTwoName + '"]';
var pptTaxonomyEndNodeName = 'taxonomy_end_node';
var pptTaxonomyEndNodeSel = ' [name="' + pptTaxonomyEndNodeName + '"]';
var pptOptionSel = ' option';
var pptUrlParamName = 'primaryProductTaxonomy';
var pptTagName = 'tag_' + pptUrlParamName;
var pptTagSel = ' [name="' + pptTagName + '"]';

var setPrimaryProductTaxonomy = function setPrimaryProductTaxonomy(formId) {
  var urlParams = new URLSearchParams(window.location.search);

  if (urlParams.has(pptUrlParamName)) {

    try {
      // Get primary product taxonomy value from url
      var pptVal = urlParams.get(pptUrlParamName);

      if (pptVal === null) {
        throw pptTagName + ' value not found in url';
      }

      if (typeof $('#form' + formId + pptTagSel) === 'undefined') {
        throw pptTagName + ' field not found on form';
      }

      // Set primary product taxonomy value to tag field
      $('#form' + formId + pptTagSel).val(pptVal);

      var pptArray = pptVal.split('/');

      var pptLevelOneVal = pptArray[0];
      populatePrimaryProductTaxonomy(formId, pptLevelOneSel, pptLevelOneVal, pptLevelOneName);

      if (pptArray.length > 1) {

        var pptLevelTwoVal = pptArray[1];
        populatePrimaryProductTaxonomy(formId, pptLevelTwoSel, pptLevelTwoVal, pptLevelTwoName);

        if (pptArray.length > 2) {

          var pptTaxonomyEndNodeVal = pptArray[pptArray.length - 1];
          populatePrimaryProductTaxonomy(formId, pptTaxonomyEndNodeSel, pptTaxonomyEndNodeVal, pptTaxonomyEndNodeName);
        }
      }
    } catch (err) {
      console.error(err);
    }
  }
};

/*
Check for primary product taxonomy related field and populate it if exists
*/
var populatePrimaryProductTaxonomy = function populatePrimaryProductTaxonomy(formId, fieldSel, fieldVal, fieldName) {

  try {

    if (typeof fieldVal === 'undefined') {
      throw fieldName + ' not found in primaryProductTaxonomy url parameter';
    }
    if (typeof $('#form' + formId + fieldSel) === 'undefined') {
      throw fieldName + ' field not found in form';
    }

    var valueExists = $('#form' + formId + fieldSel + pptOptionSel).filter(function () {
      return this.value === fieldVal;
    }).length > 0;

    valueExists && $('#form' + formId + fieldSel).val(fieldVal);
  } catch (err) {
    console.error(err);
  }
};

// END: Primary Product Taxonomy Pre-population


// START: URL parameters to form fields

var populateUrlParameterFields = function populateUrlParameterFields() {

  var urlParams = new URLSearchParams(window.location.search);
  urlParams.forEach(function (value, key) {
    var fieldSel = $('form input[name="' + key + '"]');
    if (typeof fieldSel !== 'undefined') {
      fieldSel.val(value);
    }
  });
};

// END: URL parameters to form fields

var populatePercolateContentIdFromDataLayer = function populatePercolateContentIdFromDataLayer(formId) {

  // eslint-disable-next-line no-undef
  if (typeof dataLayer.percolateContentId !== 'undefined') {
    // eslint-disable-next-line no-undef
    $('#form' + formId + ' input[name="tag_PercolateID"]').val(dataLayer.percolateContentId);
  }
};