/* jshint esversion: 6 */

const DrilldownSelectionTool = require('../src/components/drilldown-selection-tool/js/drilldown-selection-tool');

const {integratedPageMarkup,
            drilldownOneNoResultsAjaxRequest,
            drilldownOneNextDropdownAjaxRequest,
            drilldownTwoPageResultsAjaxRequest,
            servletResponseDrilldownOne,
            servletResponseDrilldownNoResults,
            loadMoreAjaxRequest,
            servletResponsePageResultsOne,
            servletResponseLoadMore} = require('../__test-fixtures__/DrilldownSelectionToolFixtures');

let drilldownSelectionTool;

const $ = require('jquery');
const Mustache = require('../src/global/js/vendors/mustache.min');
const hideClass = 'hide';

let ajaxSpy = {};
let loadNextDropdownSpy;
let getPageResultsSpy;

//two dropdowns in this scenario with options returning different results
let dropdownOne;
let dropdownTwo;
let dropdownOneSelectOptionNoResults;
let dropdownOneSelectOption;
let dropdownTwoSelectedOption;

//results display
let noResultsFoundMsg;
let resultsList;
let resultsTitle;
let resultsCount;

let loadMoreWrapperDiv;
let loadMoreButton;
let loadSpinner;

beforeEach(() => {
  jest.restoreAllMocks();

  window.Mustache = Mustache;

  document.body.innerHTML = integratedPageMarkup;

  drilldownSelectionTool = new DrilldownSelectionTool();

  ajaxSpy = jest.spyOn($, 'ajax');
  loadNextDropdownSpy = jest.spyOn(drilldownSelectionTool, 'loadNextDropdown');
  getPageResultsSpy = jest.spyOn(drilldownSelectionTool, 'getPageResults');

  document.dispatchEvent(new Event('DOMContentLoaded', {bubbles: true}));

  //main product screen with checkboxes
  dropdownOne = document.querySelector('.dropdown-list__dropdown select#dropdown-0');
  dropdownTwo = document.querySelector('.dropdown-list__dropdown select#dropdown-1');
  dropdownOneSelectOptionNoResults = document.querySelector('.dropdown-list__dropdown select#dropdown-0 option[data-tag-title="Business resources"]');
  dropdownOneSelectOption = document.querySelector('.dropdown-list__dropdown select#dropdown-0 option[data-tag-title="Category"]');
  dropdownTwoSelectedOption = document.querySelector('.dropdown-list__dropdown select#dropdown-1 option[data-tag-title="Wiring"]')

  noResultsFoundMsg = document.querySelector('.results-footer__message');
  resultsList = document.querySelector('.results-list');
  resultsTitle = document.querySelector('.results-title');
  resultsCount = document.querySelector('.results-title .count');

  loadMoreWrapperDiv = document.querySelector('.results-footer__load-more');
  loadMoreButton = document.querySelector('.results-footer__load-more button');
  loadSpinner = document.querySelector('.results-footer__load-spinner');
});

describe('user chooses an option for which there are no results to show', () => {
  it('should show noResultsFoundMsg div if no page results returned from servlet', () => {
    ajaxSpy.mockImplementation(options => {
      if (typeof options.success === 'function') {
        let response = servletResponseDrilldownNoResults();
        options.success(response);
      }
    });

    dropdownOne.selectedIndex = 1;  //select "Business Resources"
    dropdownOne.dispatchEvent(new Event('change'));

    expect(loadNextDropdownSpy).toHaveBeenCalledWith(1, '/content/cq:tags/eaton/support-taxonomy/business-resources');
    expect(ajaxSpy).toHaveBeenCalledWith(drilldownOneNoResultsAjaxRequest());
    expect(noResultsFoundMsg.classList).not.toContain(hideClass);  //expect no results message to show
  });
});

describe('user chooses options from each dropdown and then results are returned', () => {

  it('should show next dropdown, then results section after the second and final dropdown selection', () => {
    ajaxSpy.mockImplementationOnce(options => {
      if (typeof options.success === 'function') {
        let response = servletResponseDrilldownOne();
        options.success(response);
      }
    });

    dropdownOne.selectedIndex = 2;  //select "Category"
    dropdownOne.dispatchEvent(new Event('change'));

    expect(loadNextDropdownSpy).toHaveBeenCalledWith(1, '/content/cq:tags/eaton/support-taxonomy/category');
    expect(ajaxSpy).toHaveBeenCalledWith(drilldownOneNextDropdownAjaxRequest());

    expect(dropdownTwo.innerHTML).toBe('<option disabled="" selected="">Please select</option><option data-option-label="option-" data-tag-path="/content/cq:tags/eaton/product-taxonomy/clutches-brakes" data-title="Clutches and brakes">Clutches and brakes</option><option data-option-label="option-" data-tag-path="/content/cq:tags/eaton/product-taxonomy/electrical-circuit-protection" data-title="Electrical circuit protection">Electrical circuit protection</option>');

    //Second Dropdown Change
    ajaxSpy.mockImplementation(options => {
      if (typeof options.success === 'function') {
        let response = servletResponsePageResultsOne();
        options.success(response);
      }
    });
    dropdownTwo.selectedIndex = 1;  //choose anything, results will be simulated
    dropdownTwo.dispatchEvent(new Event('change'));
    let resultsItem = document.querySelectorAll('.results-list .results-item');

    expect(getPageResultsSpy).toHaveBeenCalledWith('/content/cq:tags/eaton/support-taxonomy/category|/content/cq:tags/eaton/product-taxonomy/clutches-brakes', 0);
    expect(ajaxSpy).toHaveBeenCalledWith(drilldownTwoPageResultsAjaxRequest());

    expect(resultsList.innerHTML).not.toBe('');
    expect(resultsTitle.classList).not.toContain(hideClass);
    expect(resultsCount.innerHTML).toBe('5');
    expect(loadMoreWrapperDiv.classList).not.toContain(hideClass);
    expect(resultsItem.length).toBe(4);  //expect 4 of 5 results to be showing

    //Load More Button
    ajaxSpy.mockImplementation(options => {
      if (typeof options.success === 'function') {
        let response = servletResponseLoadMore();
        options.success(response);
      }
    });
    loadMoreButton.click();  //why would this execute twice?
    resultsItem = document.querySelectorAll('.results-list .results-item');

    expect(ajaxSpy).toHaveBeenCalledWith(loadMoreAjaxRequest());
  });
});



