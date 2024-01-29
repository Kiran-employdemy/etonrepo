/*
 * Eaton
 * Copyright (C) 2020 Eaton. All Rights Reserved
 */

/* global jQuery:false, guideBridge:false */
/**
 * @private
 */
const distinct = (value, index, self) => self.indexOf(value) === index;

/* eslint-disable no-unused-vars */
/**
 * Eaton Get Country Cookie
 * @name eatonGetCountryCookie Eaton - Get Country From Cookie
 * @return {string} country ID
 * This function will not work properly until countries codes are aligned
 * between cookie name and the my eaton service
 */
function eatonGetCountryCookie() {
  const countryID = document.cookie.replace(/(?:(?:^|.*;\s*)etn_country_selector_country\s*\=\s*([^;]*).*$)|^.*$/, '$1');

  // Need to return value that will be acceptable to AF
  const countryWords = countryID.toLowerCase().split('-');
  for (let i = 0; i < countryWords.length; i++) {
    countryWords[i] = countryWords[i].charAt(0).toUpperCase() + countryWords[i].substring(1);
  }
  return countryWords.join(' ');
}

/**
 * Eaton Is Multiple Checked
 * @name eatonIsMultipleChecked Eaton - Return whether multiple checkboxes are checked
 * @param {string} formElementSom
 * @return {boolean} Whether or not multiple items were selected
 */
function eatonIsMultipleChecked(formElementSom) {
  const formElement = guideBridge.resolveNode(formElementSom);
  return formElement.value.indexOf(',') > -1;
}

/**
 * Eaton Build Dynamic Options
 * @name eatonBuildDynamicOptions Eaton - Return Array of options based on output of target field
 * @param {string} updatedElementSom Som Expression of field to update values
 * @param {string} targetElementSom Target field's Som Expression
 */
function eatonBuildDynamicOptions(updatedElementSom, targetElementSom) {
  const updatedElement = guideBridge.resolveNode(updatedElementSom);
  const targetElement = guideBridge.resolveNode(targetElementSom);
  const $updatedElement = $('.' + updatedElement.name);

  if (!updatedElement.hasOwnProperty('originalItems')) {
    updatedElement.originalItems = updatedElement.items;
  }

  if (!targetElement.value) {
    updatedElement.items = updatedElement.originalItems;
  } else {
    const dynamicItems = $updatedElement.prev().data('dynamicItems');

    let itemsArray = [];

    for (let i = 0; i < dynamicItems.length; i++) {
      const dynamicItem = dynamicItems[i];

      if (targetElement.value.indexOf(dynamicItem.name) > -1) {
        itemsArray = itemsArray.concat(dynamicItem.mapping);
      }
    }

    updatedElement.items = itemsArray.filter(distinct);
  }
  updatedElement.value = '';
}

/**
 * Eaton Get Valid Fields
 * @name eatonGetValidFields Eaton - Gets the valid fields for use on the form
 * @returns {boolean} true empty or if account is available
 */
function eatonGetValidFields() {
  jQuery.ajax({
    method: 'GET',
    dataType: 'json',
    url: '/eaton/my-eaton/fields'
  }).done(function(result) {
    guideBridge.eatonFields = result;
    jQuery('.etn-user-registration').trigger('fieldsSet.etnUserReg');
  });
}

/**
 * Eaton Set Options for Field
 * @name eatonOptionsForField Eaton - Sets specific options from fields service for particular field
 * @param {string} fieldSom SomExpression of field to update
 * @param {string} parameterName Name of parameter from fields object to use
 * @param {string} keyName Name of key parameter to use when building options
 * @param {string} valueName Name of value parameter to use when building options
 * @param {string} [sortKey] Optionally sort based on a parameter of the objects
 * @param {string} [keyValueSeparator]  Optionally combine the key and value together when displaying, using value as a separator
 */
function eatonOptionsForField(fieldSom, parameterName,
  keyName, valueName, sortKey, keyValueSeparator) {

  let field = guideBridge.resolveNode(fieldSom);
  let prefillValue = field.value ? field.value : '';

  $( document ).ready(function() {
    if (guideBridge.eatonFields !== null && guideBridge.eatonFields !== undefined) {
      jQuery('.etn-user-registration', function() {
        field.items = calculateFieldOptions(parameterName, keyName, valueName, sortKey, keyValueSeparator);
        field.value = prefillValue;
      });
    }
    else {
      jQuery('.etn-user-registration').on('fieldsSet.etnUserReg', function() {
        field.items = calculateFieldOptions(parameterName, keyName, valueName, sortKey, keyValueSeparator);
        field.value = prefillValue;
      });
    }
  });
}

function calculateFieldOptions(parameterName, keyName, valueName, sortKey, keyValueSeparator) {
  let options = guideBridge.eatonFields[parameterName];

  if (sortKey !== 'null') {
    options.sort(propComparator(sortKey));
  }

  options = options.map(function (option) {
    let optionString = option[keyName] + '=';

    if (keyValueSeparator !== 'null') {
      optionString += option[keyName] + keyValueSeparator;
    }

    optionString += option[valueName];

    return optionString;
  });

  return options;
}

/**
 * Eaton Trigger Field Set
 * @name eatonTriggerFieldSet Eaton - Trigger Field Set
 */
function eatonTriggerFieldSet() {
  jQuery('.etn-user-registration').trigger('fieldsSet.etnUserReg');
}

/**
 * Eaton Get States for Country
 * @name eatonGetStatesForCountry Eaton - Gets options for states/provinces for a particular country
 * @param {string} countrySom SomExpression of country field pull key country
 * @param {string} stateSom SomExpression of state/province field to update
 */
function eatonGetStatesForCountry(countrySom, stateSom) {
  const countryField = guideBridge.resolveNode(countrySom);
  let stateField = guideBridge.resolveNode(stateSom);

  const countryVal = countryField.value;
  // Get the country based on the countryCode set
  const country = guideBridge.eatonFields.validCountries
    .filter(countryItem => countryItem.countryCode === countryVal)[0];

  const countryId = country.countryId;

  // Filter States with Country ID
  let options = guideBridge.eatonFields.validStates
    .filter(stateItem => stateItem.countryId === countryId);

  options = options
    .sort(propComparator('stateName'))
    .map(function(countryItem) {
      return countryItem.stateCode + '=' + countryItem.stateName;
    });

  // Set items
  stateField.items = options;
}

/**
 * Eaton Collect Repeatable Panel
 * @name eatonCollectRepeatablePanel Eaton - Add a repeatable panel to a collection for handling data on submit
 * @param {string} repeatablePanelSom SOM Expression of Repeatable Panel to collect
 */
function eatonCollectRepeatablePanel(repeatablePanelSom) {
  // Define our array if it's not already defined
  guideBridge.etnRepeatablePanels = guideBridge.etnRepeatablePanels || [];

  guideBridge.etnRepeatablePanels.push(repeatablePanelSom);
}

/**
 * Eaton Format Repeatable Panel Data
 * @name eatonFormatRepeatablePanels Eaton - Format Repeatable Panels for submission
 * @param {string} dataFieldSom SOM Expression for hidden field where data will live
 * Formats all of the data for repeatable panels into a single hidden field,
 * this is a bit of a hack because it seems like the current implementation of
 * AF Data Models doesn't really support repeatable panels .·´¯`(>▂<)´¯`·.
 */
function eatonFormatRepeatablePanels(dataFieldSom) {
  const dataField = guideBridge.resolveNode(dataFieldSom);
  const result = {};

  for (let i = 0; i < guideBridge.etnRepeatablePanels.length; i++) {
    const repeatablePanel = guideBridge.resolveNode(guideBridge.etnRepeatablePanels[i]);
    const refName = getProperBindRef(repeatablePanel);

    if (refName && repeatablePanel.instanceManager.instances.length) {
      result[refName] = [];
      for (let j = 0; j < repeatablePanel.instanceManager.instances.length; j++) {
        const panel = repeatablePanel.instanceManager.instances[j];

        if (refName) {
          result[refName].push(iterateFields(panel));
        }
      }
    }

    dataField.value = JSON.stringify(result);
  }
}

/**
 * Eaton Set Value Based On Field
 * @name eatonSetValueBasedOnField Eaton - Set the value of a field based on another field
 * @param {string} watchFieldSom SOM Expression of field to watch for changes
 * @param {string} setFieldSom Expression of field to update
 * @param {string} watchValue Value to check against, will update if matches, can be
 *                 comma/new-line separated for multi value fields
 * @param {string} setValue Value to set on the field
 */
function eatonSetValueBasedOnField(watchFieldSom, setFieldSom,
  watchValue, setValue) {
  const setField = guideBridge.resolveNode(setFieldSom);

  guideBridge.on('elementValueChanged', function(e, payload) {
    if (payload.target.somExpression === watchFieldSom) {
      const value = guideBridge.resolveNode(watchFieldSom).value;
      const matched = isValueMatched(watchValue, value);

      if (matched) {
        setField.value = setValue;
      } else {
        setField.value = null;
      }
    }
  });
}

/* eslint-enable no-unused-vars */


/** @private
 * Sorts array of objects alphabetically based on property
 * @param {string} propName Name of property to sort on
 * @returns {function(*, *): number}
 */
function propComparator(propName) {
  return function(a, b) {
    let nameA = a[propName];
    let nameB = b[propName];

    if (nameA.typeOf === 'string') {
      nameA = a[propName].toUpperCase();
    }
    if (nameA.typeOf === 'string') {
      nameB = b[propName].toUpperCase();
    }

    if (nameA < nameB) {
      return -1;
    }
    if (nameA > nameB) {
      return 1;
    }
    return 0;
  };
}

/** @private
 * Iterates over all children fields
 * @param {object} field field to traverse
 * @param {number} [maxDepth=20] Maximum depth to traverse
 * @param {number} [depth=0] Current depth of call
 * @param {object} [result={}] Object to store everything in
 * @returns {object}
 */
function iterateFields(field, maxDepth, depth, result ) {
  result = result || {};
  maxDepth = maxDepth || 20;
  depth = depth + 1 || 0;

  if (field.children && depth <= maxDepth) {
    for (let i = 0; i < field.children.length; i++) {
      const subField = field.children[i];
      if (subField.className !== 'guideInstanceManager'
          && subField.className !== 'guideButton') {
        const refName = getProperBindRef(subField);
        if (refName && !result[refName]) {
          result[refName] = subField.value;
        }

        result = iterateFields(subField, maxDepth, depth, result);
      }
    }
  }

  return result;
}

/** @private
 * Get proper bind reference from field
 * @param {object} field
 */
function getProperBindRef(field) {
  if (field.bindRef) {
    return field.bindRef.substr(field.bindRef.lastIndexOf('/') + 1);
  }

  return null;
}

/** @private
 * @param {string} checkedValue
 * @param {string} actualValue
 * @return {boolean}
 */
function isValueMatched(checkedValue, actualValue) {
  if (!actualValue) {
    return false;
  }

  if (actualValue.indexOf('\n') < 0 && actualValue.indexOf(',') < 0) {
    return checkedValue === actualValue;
  }

  let splitChar = ',';
  if (actualValue.indexOf('\n') > -1) {
    splitChar = '\n';
  }

  const values = actualValue.split(splitChar);
  for (let i = 0; i < values.length; i++) {
    if (values[i] === checkedValue) {
      return true;
    }
  }

  return false;
}
