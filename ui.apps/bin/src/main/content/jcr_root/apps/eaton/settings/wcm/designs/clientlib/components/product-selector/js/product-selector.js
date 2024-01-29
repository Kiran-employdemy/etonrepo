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

var _typeof = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; };

function _toConsumableArray(arr) { if (Array.isArray(arr)) { for (var i = 0, arr2 = Array(arr.length); i < arr.length; i++) { arr2[i] = arr[i]; } return arr2; } else { return Array.from(arr); } }

var App = window.App || {};
var resultText = '';

App.productSelector = function (guideBridge) {

  document.addEventListener('DOMContentLoaded', function () {

    [].concat(_toConsumableArray(document.querySelectorAll('.cards-container'))).forEach(function (container) {
      watchCardHeights(container);
      var count = 0;
      var images = container.querySelectorAll('.card img');

      [].concat(_toConsumableArray(images)).forEach(function (img) {
        return img.addEventListener('load', function () {
          count++;

          if (count >= images.length) {
            watchCardHeights(container);
          }
        });
      });
    });
  });

  function watchCardHeights(container) {
    setCardHeight(container);
    window.addEventListener('resize', App.global.utils.throttled(1000, function () {
      return setCardHeight(container);
    }));
    var panel = container.closest('.panel-collapse');
    if (panel) {
      App.global.utils.watchForClass(panel, 'in').then(function () {
        return setCardHeight(container);
      });
    }
  }

  function setCardHeight(container) {
    if (window.innerWidth >= 992) {
      var maxHeight = 0;

      // Reset the height to be whatever the inner card content makes it.
      [].concat(_toConsumableArray(container.querySelectorAll('.card'))).forEach(function (card) {
        return card.style.height = '';
      });

      // With the height reset, find the tallest card.
      [].concat(_toConsumableArray(container.querySelectorAll('.card'))).forEach(function (card) {
        if (card.clientHeight > maxHeight) {
          maxHeight = card.clientHeight;
        }
      });

      // If a max height was found, set all of the cards to be that max height.
      if (maxHeight > 0) {
        [].concat(_toConsumableArray(container.querySelectorAll('.card'))).forEach(function (card) {
          return card.style.height = maxHeight + 'px';
        });
      }
    }
  }

  /**
   * Updates all of the dropdown lists in the adaptive form based upon the currently
   * selected values.
   * @function eatonUpdateProductForm
   */

  window.eatonUpdateProductForm = function (hideCard) {
    if (!hideCard) {
      // To display cards with no taxonomy attribute
      displayCardsWithoutTaxAttributes();
    }
    getProductSelectors().forEach(function (component) {
      return updateDropdown(component.name, getAppliedValuesForDropdown(component.name), getSelectedValues());
    });

    var valuesForGuidedSelection = getAppliedValuesForGuidedSelection();
    requestFacetList(valuesForGuidedSelection, function (newFacetList) {
      updateOptions(newFacetList, valuesForGuidedSelection);
    });
  };

  /**
   * Initialized the values of the dropdown lists in the adaptive form.
   * @function eatonInitializeProductForm
   */
  window.eatonInitializeProductForm = function () {
    // To display cards with no taxonomy attribute
    displayCardsWithoutTaxAttributes();
    if (!window.eatonProductFormInitialized) {
      window.eatonProductFormInitialized = true;
      requestFacetList([], function (facets) {
        return updateOptions(facets, []);
      });
    }
  };

  /**
    * Resetting the values in the adaptive form.
    * @function eatonResetProductForm
    */
  window.eatonResetProductForm = function () {
    var cardwrapper = document.querySelectorAll('.card__header__radio__wrapper');
    var noPreference = document.querySelector('.card__header__radio__content');
    if (noPreference) {
      noPreference.classList.remove('selected');
    }
    if (cardwrapper) {
      cardwrapper.forEach(function (item) {
        item.classList.remove('selected');
      });
    }
    getProductSelectors().forEach(function (component) {
      return updateDropdown(component.name, [], []);
    });
    requestFacetList([], function (facets) {
      return updateOptions(facets, []);
    });

    dropdownList.forEach(function (dropDown) {
      dropDown.visible = true;
    });
  };

  /**
   * @returns The currently selected values in the adaptive form.
   */
  function getSelectedValues() {
    return getSelectedDropdowns().map(function (select) {
      return select.value;
    }).filter(function (value) {
      return value !== '';
    });
  }

  function displayCardsWithoutTaxAttributes() {
    Array.prototype.forEach.call(document.querySelectorAll('.guidedproductselection .guideDropDownList'), function (elem) {
      elem.parentNode.classList.add('hidden');
      if (elem.querySelector('.card__header__radio__btn') === null || elem.querySelector('.card__header__radio__btn-inner') && elem.querySelector('.card__header__radio__btn-inner').dataset.facetid === '') {
        elem.parentNode.classList.remove('hidden');
      }
    });
  }

  /**
   * @returns The currently selected dropdowns in the adaptive form.
   */
  function getSelectedDropdowns() {
    return getProductSelectors().map(function (component) {
      return document.getElementById(component.templateId).querySelector('select');
    }).filter(function (select) {
      return select !== null;
    });
  }

  /**
   * @param dropdownName The dropdown to skip in the applied values list.
   * @returns All of the selected dropdown values other than the value for the provided drop down.
   */
  function getAppliedValuesForDropdown(dropdownName) {
    return getAppliedDropdownValues(dropdownName).concat(getAppliedValuesForGuidedSelection());
  }

  /**
   * @param dropdownName The guided selection to skip in the applied values list.
   * @returns All of the selected values other than the value for the provided drop down.
   */
  function getAppliedValuesForGuidedSelection(guidedSelectionName) {
    return getAppliedGuidedSelectorValues().concat(getAppliedDropdownValues(null));
  }

  /**
   * @param dropdownName The dropdown to skip in the applied dropdown list.
   * @returns All of the selected dropdowns other than the provided drop down.
   */
  function getAppliedDropdowns(dropdownName) {
    return getProductSelectors().filter(function (component) {
      return component.name !== dropdownName;
    }).map(function (component) {
      return document.getElementById(component.templateId).querySelector('select');
    }).filter(function (select) {
      return select !== null;
    });
  }

  /**
   * @param dropdownName The dropdown to skip in the applied dropdown list.
   * @returns All of the selected dropdown values other than the provided drop down.
   */
  function getAppliedDropdownValues(dropdownName) {
    return getAppliedDropdowns().map(function (select) {
      return select.value;
    }).filter(function (value) {
      return value !== '';
    });
  }

  /**
   * @param dropdownName The dropdown to skip in the applied dropdown list.
   * @returns All of the selected dropdowns other than the provided drop down.
   */
  function getAppliedGuidedSelectorValues(dropdownName) {
    if (document.getElementById('selectedFacets')) {
      return document.getElementById('selectedFacets').value.split(',').filter(function (val) {
        return val !== '';
      });
    }
    return [];
  }

  /**
   * @returns All of the product dropdown list components in the adaptive form.
   */
  function getProductSelectors() {
    return getGuideComponents('eaton/components/form/productselectordropdown');
  }

  /**
   * @param resourceType The resource type to use when search for the components in the adaptive form.
   * @returns All of the components in the adaptive form with the given resource type.
   */
  function getGuideComponents(resourceType) {
    return getComponentsFrom(guideBridge.resolveNode('guide[0]').jsonModel.rootPanel).filter(function (component) {
      return component['sling:resourceType'] === resourceType;
    });
  }

  /**
   * @param component The component to begin searching from.
   * Recursively finds all of the adaptive form components starting from the provided component.
   */
  function getComponentsFrom(component) {
    var components = [component];

    if (component.items) {
      Object.keys(component.items).map(function (key) {
        return component.items[key];
      }).forEach(function (childComponent) {
        components = components.concat(getComponentsFrom(childComponent));
      });
    }

    return components;
  }

  /**
   * @param activeFacets The active facets to use in the request for the applicable facets.
   * @callback success A call back to perform if the request was successful.
   *   @param facets The new list of facets based upon the request that was made.
   */
  function requestFacetList(activeFacets, _success) {
    var aemFormContainer = document.querySelector('.aemformcontainer');
    if (aemFormContainer && activeFacets) {
      var facetSelector = aemFormContainer.dataset.facetSelector;
      var formServletUrl = aemFormContainer.dataset.productUrl;
      if (facetSelector && formServletUrl) {
        var url = activeFacets.length > 0 ? formServletUrl.replace('.' + facetSelector, '.' + [facetSelector].concat(_toConsumableArray(activeFacets)).join('$')) : formServletUrl.replace('.' + facetSelector, '');

        $.ajax({
          url: url,
          headers: {
            'Content-Type': 'application/json'
          },
          success: function success(facets) {
            return _success((typeof facets === 'undefined' ? 'undefined' : _typeof(facets)) === 'object' ? facets : JSON.parse(facets));
          },
          type: 'GET'
        });
      }
    }
  }

  /**
   * @param facets The list of current facets available.
   * @param activeFacets The facets that are active and should remain selected.
   */

  function updateOptions(facets, activeFacets) {
    var isExceedLimit = facets.isExceedLimit;
    var resultCount = facets.totalCount;
    var newResultText = '';
    if (resultText && resultText.indexOf('%XX%') !== -1) {
      newResultText = resultText.replace('%XX%', resultCount);
      $('.skipResult').children('.iconButton-label').each(function () {
        $(this).text(newResultText);
      });
    }
    if (isExceedLimit) {
      $('.skipResult').hide();
    } else {
      $('.skipResult').show();
    }

    if (facets.dropdownName && facets.hideOptions && activeFacets.length) {
      if (dropdownList) {
        dropdownList.forEach(function (dropDown) {
          if (facets.dropdownName === dropDown.name) {
            dropDown.visible = false;
          }
        });
      }
    } else {

      if (dropdownList) {
        dropdownList.forEach(function (dropDown) {
          if (facets.dropdownName === dropDown.name) {
            dropDown.visible = true;
          }
        });
      }

      facets.formFacets.forEach(function (filter) {
        var dropdownSelector = document.querySelector('.productselectordropdown .guideDropDownList.' + filter.name);
        var guidedSelectors = document.querySelectorAll('.guidedproductselection .guideDropDownList.' + filter.name);

        if (dropdownSelector) {
          updateDropdownOptions(dropdownSelector, filter, activeFacets);
        }

        if (guidedSelectors.length > 0) {
          guidedSelectors.forEach(function (guidedSelector) {
            updateGuidedOptions(guidedSelector, filter, activeFacets);
          });
        }
      });
    }
    // To display no preference radio button
    Array.prototype.forEach.call(document.querySelectorAll('.guidedproductselection .guideDropDownList .nopreference'), function (elem) {
      return elem.parentNode.parentNode.classList.remove('hidden');
    });
  }

  /**
   * Updates a <select> element with <option>'s based on the provided filters and active facets.
   * @param dropdownSelector The '.guideDropdownList' element of a product selector dropdown component.
   * @param filter An object with a list of values that represent the options of the select element with
                   each option having a 'value' field and a 'label' field.
   * @param activeFacets A list of active values.
   */
  function updateDropdownOptions(dropdownSelector, filter, activeFacets) {
    var selector = dropdownSelector.querySelector('select');
    var placeholder = selector.querySelector('#emptyValue');
    // This is neccessary because NodeList.forEach is not available in IE and
    // babel transpiles [...] syntax using the 'from' method which does not have IE support.
    Array.prototype.forEach.call(selector.querySelectorAll('option'), function (option) {
      return option.parentNode.removeChild(option);
    });
    selector.appendChild(placeholder ? placeholder : createOptionElement('', '', false));
    filter.values.forEach(function (option) {
      var isActive = activeFacets.filter(function (activeFacet) {
        return activeFacet === option.value;
      }).length > 0;
      selector.appendChild(createOptionElement(option.value, option.label, isActive));
    });
  }

  /**
   * @param dropdownSelector The '.guideDropdownList' element of a guided product selector component.
   * @param filter An object with a list of values that represent the options of the select element with
                   each option having a 'value' field and a 'label' field.
   */
  function updateGuidedOptions(guidedSelector, filter) {
    var radioBtn = guidedSelector.querySelector('.card__header__radio__btn-inner');
    if (radioBtn) {
      var radioConfig = radioBtn.dataset;
      var showGuidedSelector = filter.values.map(function (facet) {
        return facet.value;
      }).includes(radioConfig.facetid);

      if (showGuidedSelector) {
        guidedSelector.parentNode.classList.remove('hidden');
      } else {
        guidedSelector.parentNode.classList.add('hidden');
      }
    }
  }

  /**
   * @param value The value of the option element.
   * @param label The label of the option element.
   * @param isActive Whether the option element should be active.
   * @returns An option element created as specified.
   */
  function createOptionElement(value, label, isActive) {
    var optionElement = document.createElement('option');
    optionElement.value = value;
    optionElement.innerText = label;
    optionElement.selected = isActive;
    return optionElement;
  }

  /**
   * @param dropdownName The dropdown name to update.
   * @param appliedFacets The facets that should be applied when updating this dropdowns list of options.
   * @param selectedFacets The selected facets to maintain after the options are updated.
   */
  function updateDropdown(dropdownName, appliedFacets, selectedFacets, guideDropDownList) {
    requestFacetList(appliedFacets, function (facets) {
      var updatedFacets = {
        formFacets: facets.formFacets.filter(function (facet) {
          return facet.name === dropdownName;
        }),
        hideOptions: facets.formFacets.filter(function (facet) {
          return facet.name === dropdownName;
        }).length === 0,
        dropdownName: dropdownName,
        isExceedLimit: facets.isExceedLimit,
        totalCount: facets.totalCount
      };

      updateOptions(updatedFacets, selectedFacets, guideDropDownList);
    });
  }
}(guideBridge); // eslint-disable-line no-undef

$(document).ready(function () {
  $('.ctabutton').removeClass('container');
  resultText = $('.skipResult').children('.iconButton-label').first().text();
  var cardwrapper = document.querySelectorAll('.card__header__radio__wrapper');

  var _loop = function _loop(cardwrapperIndex, len) {
    var item = cardwrapper[cardwrapperIndex];
    var selectedFacets = document.getElementById('selectedFacets');

    window.eatonInitializeProductForm();
    item.addEventListener('click', function () {
      var values = selectedFacets.value.split(',').filter(function (facetId) {
        return facetId !== '';
      });
      var radioConfig = item.querySelector('.card__header__radio__btn-inner').dataset;
      var hideCard = true;
      if (item.classList.contains('selected')) {
        // Remove the selection
        item.classList.remove('selected');
        values.remove(radioConfig.facetid);
      } else {
        // Unselect the guided selectors that are for the same facet group.
        [].concat(_toConsumableArray(document.querySelectorAll('.guidedproductselection .guideDropDownList.' + radioConfig.parentgroupid))).forEach(function (guidedSelector) {
          values.remove(guidedSelector.querySelector('.card__header__radio__btn-inner').dataset.facetid);
          guidedSelector.querySelector('.card__header__radio__wrapper').classList.remove('selected');
        });

        // Add the chosen guided selection
        item.classList.add('selected');
        // clear no preference selection
        clearNoPreferenceSelection(item);
        values.push(radioConfig.facetid);
      }

      selectedFacets.value = values.join(',');
      window.eatonUpdateProductForm(hideCard);
    });
  };

  for (var cardwrapperIndex = 0, len = cardwrapper.length; cardwrapperIndex < len; cardwrapperIndex++) {
    _loop(cardwrapperIndex, len);
  }
  triggerNoPreferenceCall();
});

var globalPanelList = [];
function getPanelList(panel) {
  if (panel.children.filter(function (panel) {
    return panel.className === 'guidePanel';
  }).length > 0) {
    var childPanels = panel.children.filter(function (panel) {
      return panel.className === 'guidePanel';
    });
    childPanels.forEach(function (childPanel) {
      globalPanelList.push(childPanel);
      if (childPanel.children.length > 0) {
        getPanelList(childPanel);
      }
    });
  }
}

function triggerNoPreferenceCall() {
  [].concat(_toConsumableArray(document.querySelectorAll('.card__header__radio__content'))).forEach(function (noPreferenceItem) {
    noPreferenceItem.addEventListener('click', function () {
      if (!noPreferenceItem.classList.contains('selected')) {
        var cardWrapperPanel = $(noPreferenceItem).parentsUntil('.panel .active').find('.card__header__radio__wrapper');
        noPreferenceItem.classList.add('selected');
        $.each(cardWrapperPanel, function (i, element) {
          element.classList.remove('selected');
        });
        var _selectedFacets = document.getElementById('selectedFacets');
        _selectedFacets.value = '';
        window.eatonUpdateProductForm(true);
      } else {
        noPreferenceItem.classList.remove('selected');
      }
    });
  });
}

function clearNoPreferenceSelection(item) {
  var noPreferenceWrapper = $(item).parentsUntil('.panel .active').find('.card__header__radio__content');
  $.each(noPreferenceWrapper, function (i, element) {
    element.classList.remove('selected');
  });
}

var dropdownList = [];
/* eslint-disable no-unused-vars, no-undef*/
guideBridge.on('bridgeInitializeComplete', function (event, payload) {
  /* eslint-disable no-unused-vars, no-undef*/
  var rootPanel = guideBridge.resolveNode('rootPanel');
  var firstLevelPanel = rootPanel.children.filter(function (panel) {
    return panel.className === 'guidePanel';
  });
  firstLevelPanel.forEach(function (panel) {
    if (panel.children.length > 0) {
      getPanelList(panel);
    }
  });
  if (globalPanelList.length > 0) {
    globalPanelList.forEach(function (panelObj) {
      var dropdownFieldList = panelObj.children.filter(function (guideField) {
        return guideField.className === 'guideDropDownList';
      });
      if (dropdownFieldList.length > 0) {
        dropdownFieldList.forEach(function (field) {
          dropdownList.push(field);
        });
      }
    });
  }

  /* eslint-disable no-unused-vars, no-undef*/
  guideBridge.on('elementValueChanged', function (event, payload) {
    var component = payload.target; // Field whose value has changed
    var guideDropDownList = component.parent.children.filter(function (panel) {
      return panel.className === 'guideDropDownList';
    });
    window.eatonUpdateProductForm();
  });
});