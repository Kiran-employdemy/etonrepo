// Eloqua pre-population script

$(document).ready(function() {
  // eslint-disable-next-line no-undef
  if (dataLayer.form === 'yes') {
    populateUrlParameterFields();
    populateHiddenTags();
  }
});

/**
 * Populate elements based on element's type
 */
const populateElements = (formId, formElements, formElementsType) => {

  let positiveCheckedValues = ['on', 'true', 'y', 'yes', 'checked', '1'];

  if (typeof formElements === 'undefined' || Object.keys(formElements).length === 0) {
    console.error(formElementsType, 'selector found no elements');
  } else {

    for (let k = 0; k < formElements.length; k++) {
      let elementObj = formElements[k];
      let elementName = elementObj.name;
      let elementType = elementObj.type;
      let logElement = elementType.concat(' ', formElementsType, ' ', elementName, ' on form #', formId);
      let newElementValue = null;

      try {

        // eslint-disable-next-line no-undef, new-cap
        newElementValue = elementType === 'hidden' ? eval(elementName + '_' + formId) : GetElqContentPersonalizationValue(elementName);

      } catch (err) {
        console.error('%s did not pre-populate. Html name (%s) for element is incorrect.', logElement, elementName);
      }

      switch (elementType) {
          case 'radio': // falls through to checkbox case's operations
          case 'checkbox': {
            let isChecked = positiveCheckedValues.includes(newElementValue.toLowerCase());
            if (isChecked) {
              elementObj.checked = true;
            } else {
              console.warn(logElement, 'did not pre-populate: Value not found in list of values to check (positiveCheckedValues)');
            }
            break;
          }
          case 'text': // falls through to select-one case's operations
          case 'select-one': {
            elementObj.value = newElementValue;
            break;
          }
          case 'select-multiple': {
            let newElementValueArray = newElementValue.split('::');
            if (typeof newElementValueArray === 'undefined') {
              console.warn(logElement, 'did not pre-populate: Value array not defined');
            } else {
              elementObj.value = newElementValueArray;
            }
            break;
          }
          case 'hidden': {
            elementObj.value = newElementValue;
            break;
          }
          default: {
            console.warn('Pre-population for ', elementType, '-type elements is not configured');
          }
      }

    }

  }

};

/**
 * Populate page url tag
 */
const populatePageUrlTag = (formId) => {

  const pageTagName = 'tag_page';
  let isPopulatePageTag = eval(pageTagName + '_' + formId);
  let pageTagValue = isPopulatePageTag ? location.href : '';
  $('#form' + formId + ' [name="' + pageTagName + '"]').val(pageTagValue);

};

/**
 * Populate hidden tags
 */
const populateHiddenTags = () => {

  // eslint-disable-next-line no-undef
  if (typeof formValues === 'undefined' || formValues.length === 0) {
    console.error('Unable to start pre-population of hidden tags. formValues list is not found.');
  } else {

    // eslint-disable-next-line no-undef
    for (let i = 0; i < formValues.length; i++) {

      // eslint-disable-next-line no-undef
      let formId = formValues[i];
      let hiddenTags = $('#form' + formId + ' [type="hidden"][name^="tag_"]')
                                                  .not('[name^="tag_Optin"]')
                                                  .not('[name="tag_primaryProductTaxonomy"]')
                                                  .not('[name="tag_page"]')
                                                  .not('[name="tag_PercolateID"]');

      populateElements(formId, hiddenTags, 'tag');
      populatePageUrlTag(formId);  // html name: tag_page
      populatePercolateContentIdFromDataLayer(formId);  // html name: tag_PercolateID
      setPrimaryProductTaxonomy(formId);  // html name: tag_primaryProductTaxonomy
    }

  }

};

/**
 * Populate form fields
 * (called by SetElqContent if user exists in Eloqua contact object model)
 */
const populateFormFields = () => {

  // eslint-disable-next-line no-undef
  if (typeof formValues === 'undefined' || formValues.length === 0) {
    console.error('Unable to start pre-population of hidden tags. formValues list is not found.');
  } else {

    // eslint-disable-next-line no-undef
    for (let i = 0; i < formValues.length; i++) {

      // eslint-disable-next-line no-undef
      let formId = formValues[i];
      let isEnabled = eval('prepopulationEnabled_' + formId);

      if (typeof isEnabled === 'undefined') {
        console.error('Pre-population not complete: Cannot find prepopulationEnabled variable for eloqua form.');
      } else if (isEnabled === 'false') {
        console.warn('Pre-population not complete: Author disabled pre-population for form #', formId);
      } else {
        let formFields = $('#form' + formId + ' [name^="C_"]');
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
let FirstLookup = true;
// eslint-disable-next-line no-unused-vars
const SetElqContent = () => {
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
const pptLevelOneName = 'level_1_product';
const pptLevelOneSel = ' [name="' + pptLevelOneName + '"]';
const pptLevelTwoName = 'level_2_product';
const pptLevelTwoSel = ' [name="' + pptLevelTwoName + '"]';
const pptTaxonomyEndNodeName = 'taxonomy_end_node';
const pptTaxonomyEndNodeSel = ' [name="' + pptTaxonomyEndNodeName + '"]';
const pptOptionSel = ' option';
const pptUrlParamName = 'primaryProductTaxonomy';
const pptTagName = 'tag_' + pptUrlParamName;
const pptTagSel = ' [name="' + pptTagName + '"]';

const setPrimaryProductTaxonomy = (formId) => {
  let urlParams = new URLSearchParams(window.location.search);

  if (urlParams.has(pptUrlParamName)) {

    try {
      // Get primary product taxonomy value from url
      let pptVal = urlParams.get(pptUrlParamName);

      if (pptVal === null) {
        throw pptTagName + ' value not found in url';
      }

      if (typeof $('#form' + formId + pptTagSel) === 'undefined') {
        throw pptTagName + ' field not found on form';
      }


      // Set primary product taxonomy value to tag field
      $('#form' + formId + pptTagSel).val(pptVal);

      let pptArray = pptVal.split('/');

      let pptLevelOneVal = pptArray[0];
      populatePrimaryProductTaxonomy(formId, pptLevelOneSel, pptLevelOneVal, pptLevelOneName);

      if (pptArray.length > 1) {

        let pptLevelTwoVal = pptArray[1];
        populatePrimaryProductTaxonomy(formId, pptLevelTwoSel, pptLevelTwoVal, pptLevelTwoName);

        if (pptArray.length > 2) {

          let pptTaxonomyEndNodeVal = pptArray[pptArray.length - 1];
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
const populatePrimaryProductTaxonomy = (formId, fieldSel, fieldVal, fieldName) => {

  try {

    if (typeof fieldVal === 'undefined') {
      throw fieldName + ' not found in primaryProductTaxonomy url parameter';
    }
    if (typeof $('#form' + formId + fieldSel) === 'undefined') {
      throw fieldName + ' field not found in form';
    }

    let valueExists = $('#form' + formId + fieldSel + pptOptionSel).filter(function() {
      return this.value === fieldVal;
    }).length > 0;

    valueExists && $('#form' + formId + fieldSel).val(fieldVal);

  } catch (err) {
    console.error(err);
  }

};

// END: Primary Product Taxonomy Pre-population


// START: URL parameters to form fields

const populateUrlParameterFields = () => {

  let urlParams = new URLSearchParams(window.location.search);
  urlParams.forEach(function(value, key) {
    let fieldSel = $('form input[name="' + key + '"]');
    if (typeof fieldSel !== 'undefined') {
      fieldSel.val(value);
    }
  });

};

// END: URL parameters to form fields

const populatePercolateContentIdFromDataLayer = (formId) => {

  // eslint-disable-next-line no-undef
  if (typeof dataLayer.percolateContentId !== 'undefined') {
    // eslint-disable-next-line no-undef
    $('#form' + formId + ' input[name="tag_PercolateID"]').val(dataLayer.percolateContentId);
  }

};

