//-----------------------------------
// Eaton: Global Forms
//-----------------------------------
'use strict';

let App = window.App || {};
App.form = (function(Choices) {

  const $componentEl = $('.eaton-form');
  const selectorDropdowns = '.eaton-form select:not([multiple])';
  const $selectEl = $(selectorDropdowns);

  const init = () => {
    initDropdown();
  };


    /**
     * Initialize Choices Dropdown
     */
  const initDropdown = () => {

    // Init Choices Library Only if the current ".eaton-form" has <select> elements
    if ($selectEl.length > 0) {
      new Choices(selectorDropdowns, {
        placeholder: false,
        itemSelectText: '',
        searchEnabled: false
      });
    }
  };


    /**
     * If containing DOM element is found, Initialize and Expose public methods
     */
  if ($componentEl.length > 0) {
    init();
  }

}(window.Choices));
