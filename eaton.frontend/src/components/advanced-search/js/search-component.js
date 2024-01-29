/* eslint-disable no-undef */
// noinspection JSConstantReassignment

if (typeof require !== 'undefined') {
  const globalConstants = require('../../../global/js/etn-new-global-constants');
  keyCodes = globalConstants.keyCodes;
}
class SearchComponent {
  constructor(clickHandler, searchBoxInput) {
    this.clickHandler = clickHandler;
    this.searchBoxInput = searchBoxInput;
    this.registerSearchEventListeners();
  }

  registerSearchEventListeners() {
    let self = this;
    this.searchBoxInput.addEventListener(eventListeners.KEY_UP, function (event) {
      if (event.code === keyCodes.enter) {
        event.preventDefault();
        self.clickHandler.dispatchEvent(customEvents.SEARCH_EVENT);
      }
    });

    document.querySelector(querySelectorFor.searchIcon).onclick = (event) => {
      event.preventDefault();
      self.clickHandler.dispatchEvent(customEvents.SEARCH_EVENT);
    };
  }

  getSearchValue() {
    return this.searchBoxInput.value;
  }
}

const createSearchComponent = (clickHandler, searchInboxInput) => {
  return new SearchComponent(clickHandler, searchInboxInput);
};

if (typeof module !== 'undefined' && typeof module.exports !== 'undefined') {
  module.exports = {createSearchComponent};
}
