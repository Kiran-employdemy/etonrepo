/* eslint-disable no-undef */
/* eslint-disable no-global-assign */
// noinspection JSConstantReassignment
// -----------------------------------
// Tag Dropdown Tool Component
//-----------------------------------

const requirePresent = () => typeof require !== 'undefined';

if (requirePresent()) {
  const globalConstants = require('../../../global/js/etn-new-global-constants');
  const constants = require('./drilldown-selection-tool-constants');
  literals = globalConstants.literals;
  $ = require(literals.JQUERY);
  eventListeners = globalConstants.eventListeners;
  drilldownSelectionToolClasses = constants.drilldownSelectionToolClasses;
  drilldownSelectionToolQuerySelectors = constants.drilldownSelectionToolQuerySelectors;
  drilldownSelectionToolMustacheElements = constants.drilldownSelectionToolMustacheElements;
  drilldownSelectionToolServletSuffix = constants.drilldownSelectionToolServletSuffix;
  drilldownSelectionToolAttributes = constants.drilldownSelectionToolAttributes;
}

class DrilldownSelectionTool {

  constructor() {
    // eslint-disable-next-line no-undef
    this.component = document.querySelector(drilldownSelectionToolQuerySelectors.component);

    this.dropdownList = this.component.querySelector(drilldownSelectionToolQuerySelectors.dropdownList);
    this.resourcePath = this.dropdownList.getAttribute(drilldownSelectionToolAttributes.resourcePath);

    this.dropdownSelects = this.component.querySelectorAll(drilldownSelectionToolQuerySelectors.dropdownSelects);
    this.dropdownOptionsServletUrl = this.resourcePath + drilldownSelectionToolServletSuffix.dropdown;
    this.resultsServletUrl = this.resourcePath + drilldownSelectionToolServletSuffix.results;

    this.resultsTitle = this.component.querySelector(drilldownSelectionToolQuerySelectors.resultsTitle);
    this.resultsListCount = this.component.querySelector(drilldownSelectionToolQuerySelectors.resultsCount);
    this.resultsList = this.component.querySelector(drilldownSelectionToolQuerySelectors.resultsList);

    this.resultsFooter = this.component.querySelector(drilldownSelectionToolQuerySelectors.resultsFooter);
    this.pageSize = this.resultsFooter.getAttribute(drilldownSelectionToolAttributes.pageSize);

    this.loadMoreWrapperDiv = this.component.querySelector(drilldownSelectionToolQuerySelectors.loadMoreWrapperDiv);
    this.loadMoreButton = this.component.querySelector(drilldownSelectionToolQuerySelectors.loadMoreButton);
    this.loadSpinner = document.querySelector(drilldownSelectionToolQuerySelectors.loadSpinner);

    this.noResultsFoundMsg = this.component.querySelector(drilldownSelectionToolQuerySelectors.noResultsFoundMsg);
    this.errorMsg = this.component.querySelector(drilldownSelectionToolQuerySelectors.errorMsg);

    this.mustacheNextDropdown = document.getElementById(drilldownSelectionToolMustacheElements.mustacheTemplate.nextDropdown);
    this.mustachePageResults = document.getElementById(drilldownSelectionToolMustacheElements.mustacheTemplate.pageResults);

    this.addEventListeners();

    this.loadNextDropdown(0, this.getAllSelectedOptions());
  }

  addEventListeners() {
    this.attachDropdownChangeListenerToAllDropdownsExceptLast();
    this.attachDropdownChangeListenerToLastDropdown();
    this.loadMore();
  }

  // Mustache
  renderNextDropdown(nextDropdownData) {
    return window.Mustache.render(this.mustacheNextDropdown.innerHTML, nextDropdownData);
  }
  renderPageResults(pageResultsData) {
    return window.Mustache.render(this.mustachePageResults.innerHTML, pageResultsData);
  }

  attachDropdownChangeListenerToAllDropdownsExceptLast() {
    let self = this;

    const dropdowns = Array.from(this.dropdownSelects);
    dropdowns.slice(0, dropdowns.length - 1).forEach(dropdown => {
      dropdown.addEventListener('change', (event) => {
        event.preventDefault();
        self.loadMoreWrapperDiv.classList.add(drilldownSelectionToolClasses.hide);
        self.loadSpinner.classList.remove(drilldownSelectionToolClasses.hide);
        self.resultsTitle.classList.add(drilldownSelectionToolClasses.hide);
        self.resultsList.innerHTML = '';
        let nextDropdownIndex = parseInt(event.target.dataset.index) + 1;
        self.resetDropdownsBelowCurrentDropdown(nextDropdownIndex+1);
        let selectedDropdownOptionTags = self.getAllSelectedOptions();
        self.loadNextDropdown(nextDropdownIndex, selectedDropdownOptionTags);
      });
    });

  }

  attachDropdownChangeListenerToLastDropdown() {
    let self = this;
    const lastDropdown = this.dropdownSelects[this.dropdownSelects.length - 1];
    lastDropdown.addEventListener('change', (event) => {
      event.preventDefault();
      const selectedDropdownOptionTags = self.getAllSelectedOptions();
      this.resultsTitle.classList.add(drilldownSelectionToolClasses.hide);
      this.resultsList.innerHTML = '';
      self.getPageResults(selectedDropdownOptionTags, 0);
    });
  }

  resetDropdownsBelowCurrentDropdown(nextDropdownIndex) {
    const dropdowns = Array.from(this.dropdownSelects);
    dropdowns.slice(nextDropdownIndex).forEach(dropdown => {
      dropdown.hidden = true;
      dropdown.previousElementSibling.hidden = true;
      while (dropdown.options.length > 1) {
        dropdown.remove(dropdown.options.length - 1);
      }
    });
  }

  getAllSelectedOptions() {
    let selectedDropdownOptionTags = [];

    this.dropdownSelects.forEach(function (dropdown) {
      const selectedOption = dropdown.options[dropdown.selectedIndex];
      if (selectedOption && selectedOption.dataset.tagPath) {
        selectedDropdownOptionTags.push(selectedOption.dataset.tagPath);
      }
    });

    return selectedDropdownOptionTags.join('|');
  }

  loadNextDropdown(nextDropdownIndex, selectedDropdownOptionTags) {
    let self = this;

    this.errorMsg.classList.add(drilldownSelectionToolClasses.hide);
    this.resultsTitle.classList.add(drilldownSelectionToolClasses.hide);
    this.resultsList.innerHTML = '';
    this.noResultsFoundMsg.classList.add(drilldownSelectionToolClasses.hide);
    this.loadSpinner.classList.remove(drilldownSelectionToolClasses.hide);

    $.ajax({
      type: 'GET',
      async: true,
      url: self.dropdownOptionsServletUrl,
      data: {
        selectedDropdownOptionTags: selectedDropdownOptionTags,
        nextDropdownIndex: nextDropdownIndex
      },
      success: function (response) {
        if (response) {
          let alphabetizedResponse = self.alphabetizeDropdownOptions(response);
          let nextDropdownSelect = document.querySelector(`[name="dropdown-${ nextDropdownIndex }"]`);
          nextDropdownSelect.innerHTML += self.renderNextDropdown(alphabetizedResponse).replace('\n', '').replace(' ', '');
          nextDropdownSelect.previousElementSibling.hidden = false;
          nextDropdownSelect.hidden = false;
        } else {
          self.noResultsFoundMsg.classList.remove(drilldownSelectionToolClasses.hide);
        }
        self.loadSpinner.classList.add(drilldownSelectionToolClasses.hide);
      },
      error: function (jqXHR, textStatus, errorThrown) {
        self.loadSpinner.classList.add(drilldownSelectionToolClasses.hide);
        self.errorMsg.innerHTML = jqXHR.status + ' ' + errorThrown;
        self.errorMsg.classList.remove(drilldownSelectionToolClasses.hide);
      }
    });
  }

  alphabetizeDropdownOptions(response) {
    return response.sort((a, b) => {
      if (a.title < b.title) {
        return -1;
      } else if (a.title > b.title) {
        return 1;
      } else {
        return 0;
      }
    });
  }

  loadMore() {
    let self = this;
    this.loadMoreButton.addEventListener(eventListeners.CLICK, (event) => {
      event.preventDefault();
      let selectedDropdownOptionTags = self.getAllSelectedOptions();
      const currentResultsCnt = self.resultsList.querySelectorAll(drilldownSelectionToolQuerySelectors.resultsItems).length;
      self.getPageResults(selectedDropdownOptionTags, currentResultsCnt);
    });
  }

  getPageResults(selectedDropdownOptionTags, startIndex) {
    let self = this;

    this.errorMsg.classList.add(drilldownSelectionToolClasses.hide);
    this.loadSpinner.classList.remove(drilldownSelectionToolClasses.hide);
    this.noResultsFoundMsg.classList.add(drilldownSelectionToolClasses.hide);
    this.loadMoreWrapperDiv.classList.add(drilldownSelectionToolClasses.hide);

    $.ajax({
      type: 'GET',
      async: true,
      url: self.resultsServletUrl,
      data: {
        selectedDropdownOptionTags: selectedDropdownOptionTags,
        startIndex: startIndex,
        pageSize: self.pageSize
      },
      success: function (response) {
        self.pageResultsSuccess(response);
      },
      error: function (error) {
        self.loadSpinner.classList.add(drilldownSelectionToolClasses.hide);
        self.noResultsFoundMsg.classList.remove(drilldownSelectionToolClasses.hide);
      }
    });
  }

  pageResultsSuccess(response) {
    if (response) {
      this.resultsListCount.innerHTML = response.total;
      this.loadSpinner.classList.add(drilldownSelectionToolClasses.hide);
      this.resultsTitle.classList.remove(drilldownSelectionToolClasses.hide);
      this.resultsList.innerHTML += this.renderPageResults(response);
      this.splitResultsIntoRows();

      const hasMoreResults = this.resultsList.querySelectorAll(drilldownSelectionToolQuerySelectors.resultsItems).length < response.total;
      if (hasMoreResults) {
        this.loadMoreWrapperDiv.classList.remove(drilldownSelectionToolClasses.hide);
      } else {
        this.loadMoreWrapperDiv.classList.add(drilldownSelectionToolClasses.hide);
      }

    } else {
      this.loadSpinner.classList.add(drilldownSelectionToolClasses.hide);
      this.noResultsFoundMsg.classList.remove(drilldownSelectionToolClasses.hide);
    }
  }

  splitResultsIntoRows() {

    const resultsItems = Array.from(this.component.querySelectorAll(drilldownSelectionToolQuerySelectors.resultsItems));
    this.resultsList.innerHTML = '';

    for (let i = 0; i < resultsItems.length; i += 4) {
      const rowDiv = document.createElement('div');
      rowDiv.classList.add('row');

      const itemsInRow = resultsItems.slice(i, i + 4);
      itemsInRow.forEach(item => {
        rowDiv.appendChild(item.cloneNode(true));
      });

      this.resultsList.appendChild(rowDiv);
    }
  }

}



document.addEventListener('DOMContentLoaded', function() {
  // eslint-disable-next-line no-unused-vars
  const drilldownSelectionComponent = document.querySelector(drilldownSelectionToolQuerySelectors.component);
  if (drilldownSelectionComponent) {
    new DrilldownSelectionTool();
  }
});

if (typeof module !== 'undefined' && typeof module.exports !== 'undefined') {
  // eslint-disable-next-line no-global-assign
  $ = require('jquery');
  module.exports = DrilldownSelectionTool;
}
