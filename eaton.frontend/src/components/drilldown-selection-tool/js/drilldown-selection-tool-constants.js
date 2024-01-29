/* eslint-disable no-undef */
// noinspection JSConstantReassignment
const drilldownSelectionToolClasses = {
  hide: 'hide'
};
const drilldownSelectionToolAttributes = {
  resourcePath: 'data-resource-path',
  pageSize: 'data-page-size',
  dropdownTagPath: 'data-dropdown-tag-path'
};
const drilldownSelectionToolServletSuffix = {
  dropdown: '.dropdown.nocache.json',
  results: '.results.nocache.json'
};
const drilldownSelectionToolQuerySelectors = {
  component: '.drilldown-selection-tool',
  dropdownList: '.dropdown-list',
  dropdownSelects: '.dropdown-list select',
  dropdownOptions: '.dropdown-list select option',
  resultsList: '.results-list',
  resultsTitle: '.results-title',
  resultsCount: '.results-title .count',
  resultsItems: '.results-item',
  resultsFooter: '.results-footer',
  loadMoreWrapperDiv: '.results-footer__load-more',
  loadMoreButton: '.results-footer__load-more button',
  loadSpinner: '.results-footer__load-spinner',
  noResultsFoundMsg: '.results-footer__message',
  errorMsg: '.results-footer__message'
};
const drilldownSelectionToolMustacheElements = {
  mustacheTemplate: {
    nextDropdown: 'drilldown-selection-tool-next-dropdown-mustache',
    pageResults: 'drilldown-selection-tool-page-results-mustache'
  }
};
if (typeof module !== 'undefined' && typeof module.exports !== 'undefined') {
  module.exports = {drilldownSelectionToolClasses, drilldownSelectionToolQuerySelectors, drilldownSelectionToolMustacheElements, drilldownSelectionToolAttributes, drilldownSelectionToolServletSuffix};
}
