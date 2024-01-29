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

/* eslint-disable no-undef */
// noinspection JSConstantReassignment
var drilldownSelectionToolClasses = {
  hide: 'hide'
};
var drilldownSelectionToolAttributes = {
  resourcePath: 'data-resource-path',
  pageSize: 'data-page-size',
  dropdownTagPath: 'data-dropdown-tag-path'
};
var drilldownSelectionToolServletSuffix = {
  dropdown: '.dropdown.nocache.json',
  results: '.results.nocache.json'
};
var drilldownSelectionToolQuerySelectors = {
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
  errorMsg: '.results-footer__error'
};
var drilldownSelectionToolMustacheElements = {
  mustacheTemplate: {
    nextDropdown: 'drilldown-selection-tool-next-dropdown-mustache',
    pageResults: 'drilldown-selection-tool-page-results-mustache'
  }
};
if (typeof module !== 'undefined' && typeof module.exports !== 'undefined') {
  module.exports = { drilldownSelectionToolClasses: drilldownSelectionToolClasses, drilldownSelectionToolQuerySelectors: drilldownSelectionToolQuerySelectors, drilldownSelectionToolMustacheElements: drilldownSelectionToolMustacheElements, drilldownSelectionToolAttributes: drilldownSelectionToolAttributes, drilldownSelectionToolServletSuffix: drilldownSelectionToolServletSuffix };
}