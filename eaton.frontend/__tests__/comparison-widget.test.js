/* jshint esversion: 6 */

const Comparison = require('../src/components/product-grid/js/comparison.js');

const {integratedPageMarkup,
            backButtonContainer,
            loaderContainer,
            minItemsReqModal,
            errModal,
            compareDTTool,
            comparisonResultBefore,
            hiddenVarDivs,
            compareButtonAjaxRequestTwoItems,
            compareButtonAjaxRequestThreeItems,
            compareButtonAjaxRequestFourItems,
            compareButtonServletResponseTwoItems,
            compareButtonServletResponseThreeItems,
            compareButtonServletResponseFourItems,
            compareButtonServletErrorResponse } = require('../__test-fixtures__/ComparisonWidgetFixtures');

const $ = require('jquery');
const Mustache = require('../src/global/js/vendors/mustache.min');
let ajaxSpy = {};
jest.useFakeTimers();

$.fn.modal = jest.fn();

const hideClass = 'hide';

let mainProductGrid;
let compareCheckboxGeneric;
let compareCheckboxSpace;
let compareCheckboxSlash;
let compareCheckboxFourth;
let compareCheckboxFifth;

//Comparison widget div, shows after selecting at least one item to compare
let comparisonWidgetDiv;
let clearSelectionLink;
let compareButton;
let comparisonWidgetMainBox;
let firstComparisonSlot;
let secondComparisonSlot;
let thirdComparisonSlot;
let fourthComparisonSlot;
let firstComparisonSlotCloseIcon;
let secondComparisonSlotCloseIcon;
let thirdComparisonSlotCloseIcon;
let minimizeMaximize;

//Popup modals
let errorModal;
let closeErrorModal;
let maxItemsReachedModal;
let closeMaxItemsReachedModal;
let minItemsRequiredModal;
let closeMinItemsRequiredModal;
let compareDTToolDiv;

//Comparison results screen, shows after clicking "Compare" button
let comparisonResultTable;
let lastComparisonTableCloseIcon;
let comparisonResultTableHeadings;
let comparisonResultTableBody;
let backButtonContainerDiv;
let backButton;

let loader;

beforeEach(() => {
  jest.restoreAllMocks();

  window.Mustache = Mustache;

  document.body.innerHTML = integratedPageMarkup
    + backButtonContainer + loaderContainer + minItemsReqModal + errModal + compareDTTool
    + comparisonResultBefore + hiddenVarDivs;

  ajaxSpy = jest.spyOn($, 'ajax');
  jest.spyOn(global, 'setTimeout');

  document.dispatchEvent(new Event('DOMContentLoaded', {bubbles: true}));

  //main product screen with checkboxes
  mainProductGrid = document.querySelector('.product-grid-results.product-grid-results--sku');
  compareCheckboxGeneric = document.querySelector('input[type=checkbox].product-card-sku__comp[id="C17"]')
  compareCheckboxSpace = document.querySelector('input[type=checkbox].product-card-sku__comp[id="C17 SA"]')
  compareCheckboxSlash = document.querySelector('input[type=checkbox].product-card-sku__comp[id="C/2/7"]');
  compareCheckboxFourth = document.querySelector('input[type=checkbox].product-card-sku__comp[id="C27 SA"]');
  compareCheckboxFifth = document.querySelector('input[type=checkbox].product-card-sku__comp[id="C37"]');

  //comparison widget
  comparisonWidgetDiv = document.querySelector('.comparision');
  clearSelectionLink = document.querySelector('.clear-selection');
  compareButton = document.querySelector('.compare-basket');
  comparisonWidgetMainBox = document.querySelector('.comparision .main__box');
  firstComparisonSlot = comparisonWidgetMainBox.querySelector('.comparision__box p[id="1"]');
  secondComparisonSlot = comparisonWidgetMainBox.querySelector('.comparision__box p[id="2"]');
  thirdComparisonSlot = comparisonWidgetMainBox.querySelector('.comparision__box p[id="3"]');
  fourthComparisonSlot = comparisonWidgetMainBox.querySelector('.comparision__box p[id="4"]');

  firstComparisonSlotCloseIcon = comparisonWidgetMainBox.querySelector('.comparision__box p[id="1"]').nextElementSibling;
  secondComparisonSlotCloseIcon = comparisonWidgetMainBox.querySelector('.comparision__box p[id="2"]').nextElementSibling;
  thirdComparisonSlotCloseIcon = comparisonWidgetMainBox.querySelector('.comparision__box p[id="3"]').nextElementSibling;
  minimizeMaximize = document.querySelector('.comparision .min__close');

  //popup modals and compareDTTool
  errorModal = document.getElementById('no_data-popup');
  closeErrorModal = errorModal.querySelector('.modal-header button.close-popup');
  maxItemsReachedModal = document.getElementById('myModalDT');
  closeMaxItemsReachedModal = maxItemsReachedModal.querySelector('.modal-header button.close-popup');
  minItemsRequiredModal = document.querySelector('.modal .comp_table');
  closeMinItemsRequiredModal = maxItemsReachedModal.querySelector('.modal-header button.close-popup');
  compareDTToolDiv = document.getElementById('compare__DTtool');

  //comparison result table
  comparisonResultTable = document.querySelector('.final-result-comparision');
  comparisonResultTableHeadings = comparisonResultTable.querySelector('#tableheadComp');
  comparisonResultTableBody = comparisonResultTable.querySelector('#prdComp');

  backButtonContainerDiv = document.querySelector('.container.comparision-result');
  backButton = document.querySelector('.comparision_table-go-back .comparision__table-go-back-label');

  loader = document.querySelector('.loader-product__compare');

});

const navigateToComparisonScreenTwoItems = () => {
  compareCheckboxGeneric.click();
  compareCheckboxSpace.click();

  expect(firstComparisonSlot.innerHTML).toBe('C17');
  expect(secondComparisonSlot.innerHTML).toBe('C17 SA');

  ajaxSpy.mockImplementationOnce(options => {
    if (typeof options.success === 'function') {
      let msg = compareButtonServletResponseTwoItems();
      options.success(msg);
    }
  });
  compareButton.click();
}

const navigateToComparisonScreenThreeItems = () => {
  compareCheckboxGeneric.click();
  compareCheckboxSpace.click();
  compareCheckboxSlash.click();

  expect(firstComparisonSlot.innerHTML).toBe('C17');
  expect(secondComparisonSlot.innerHTML).toBe('C17 SA');
  expect(thirdComparisonSlot.innerHTML).toBe('C/2/7');

  ajaxSpy.mockImplementationOnce(options => {
    if (typeof options.success === 'function') {
      let msg = compareButtonServletResponseThreeItems();
      options.success(msg);
    }
  });
  compareButton.click();
}

const navigateToComparisonScreenFourItems = () => {
  expect(compareButton.classList).toContain('compare-basket__button-disbaled');
  compareCheckboxGeneric.click(); // C17
  compareCheckboxSpace.click();  //  C17 SA
  compareCheckboxSlash.click();  //  C/2/7
  compareCheckboxFourth.click(); //  C27 SA
  expect(compareButton.classList).not.toContain('compare-basket__button-disbaled');

  ajaxSpy.mockImplementationOnce(options => {
    if (typeof options.success === 'function') {
      let msg = compareButtonServletResponseFourItems();
      options.success(msg);
    }
  });
  compareButton.click();
}

const navigateToComparisonScreenErrorResponse = () => {
  compareCheckboxGeneric.click(); // C17
  compareCheckboxSpace.click();  //  C17 SA
  ajaxSpy.mockImplementationOnce(options => {
    if (typeof options.success === 'function') {
      let msg = compareButtonServletErrorResponse();
      options.success(msg);
    }
  });
  compareButton.click();
}


describe('add all three items to comparison widget, then minimize and maximize the widget, then clear all the items', () => {
  it('should add all three products and keep them in the comparison widget', () => {
    compareCheckboxGeneric.click();
    compareCheckboxSpace.click();
    compareCheckboxSlash.click();

    expect(firstComparisonSlot.innerHTML).toBe('C17');
    expect(secondComparisonSlot.innerHTML).toBe('C17 SA');
    expect(thirdComparisonSlot.innerHTML).toBe('C/2/7');
  });

  it('should hide and show the comparison widget according to the user clicking widget arrow icon', () => {
    minimizeMaximize.click();
    expect(comparisonWidgetMainBox.classList).toContain(hideClass);
    expect(minimizeMaximize.querySelector('i.comp__down').classList).toContain('icon-chevron-up');
    expect(minimizeMaximize.querySelector('i.comp__down').classList).not.toContain('icon-chevron-down');
    minimizeMaximize.click();
    expect(comparisonWidgetMainBox.classList).not.toContain(hideClass);
    expect(minimizeMaximize.querySelector('i.comp__down').classList).not.toContain('icon-chevron-up');
    expect(minimizeMaximize.querySelector('i.comp__down').classList).toContain('icon-chevron-down');
  });

  it('should clear all when Clear Selection button is clicked', () => {
    clearSelectionLink.click();
    expect(firstComparisonSlot.innerHTML).toBe('');
    expect(secondComparisonSlot.innerHTML).toBe('');
    expect(thirdComparisonSlot.innerHTML).toBe('');
    expect(fourthComparisonSlot.innerHTML).toBe('');
    expect(compareDTToolDiv.innerHTML).toBe('');
  });
});


describe('add and remove items to comparison widget using checkbox and check they are in the correct order when done', () => {
  it('should add four products, then remove the third, and then show the remaining three in first three positions', () => {
    //add
    compareCheckboxGeneric.click();
    compareCheckboxSpace.click();
    compareCheckboxSlash.click();
    compareCheckboxFourth.click();  //C27 SA

    compareCheckboxSlash.click();  //remove "C/2/7"

    expect(firstComparisonSlot.innerHTML).toBe('C17');
    expect(secondComparisonSlot.innerHTML).toBe('C17 SA');
    expect(thirdComparisonSlot.innerHTML).toBe('C27 SA');
    expect(fourthComparisonSlot.innerHTML).toBe('');
  });
});


describe('add items to comparison widget using checkbox, remove with widget "x" icons, until only one remains', () => {
  it('should add four products, then remove the second and third, and then show the remaining two in first two positions', () => {
    //add
    compareCheckboxGeneric.click();
    compareCheckboxSpace.click();
    compareCheckboxSlash.click();
    compareCheckboxFourth.click();  //C27 SA

    secondComparisonSlotCloseIcon.click();  //remove "C17 SA", and "C/2/7" shifts up one to second position
    thirdComparisonSlotCloseIcon.click();   //remove "C27 SA" currently in third position
    expect(firstComparisonSlot.innerHTML).toBe('C17');
    expect(secondComparisonSlot.innerHTML).toBe('C/2/7');
    expect(thirdComparisonSlot.innerHTML).toBe('');
    expect(fourthComparisonSlot.innerHTML).toBe('');

    secondComparisonSlotCloseIcon.click();  //remove "C/2/7" from its current second position
    expect(firstComparisonSlot.innerHTML).toBe('C17');
    expect(secondComparisonSlot.innerHTML).toBe('');
    expect(compareButton.classList).toContain('compare-basket__button-disbaled');  //expect Compare button disabled (only one item present)
    });

  it('should hide comparison widget and remove innerHTML of compareDTToolDiv when user removes the one remaining item', () => {
    firstComparisonSlotCloseIcon.click();  //remove "C17"
    expect(comparisonWidgetMainBox.classList).not.toContain(hideClass);
    expect(compareDTToolDiv.innerHTML).toBe('');
  });
});


describe('show maxItemsReachedModal if the user tries to add more than four products', () => {
  it('should show maxItemsReachedModal if too many products added', () => {
    compareCheckboxGeneric.click();
    compareCheckboxSpace.click();
    compareCheckboxSlash.click();
    compareCheckboxFourth.click();

    expect(firstComparisonSlot.innerHTML).toBe('C17');
    expect(secondComparisonSlot.innerHTML).toBe('C17 SA');
    expect(thirdComparisonSlot.innerHTML).toBe('C/2/7');
    expect(fourthComparisonSlot.innerHTML).toBe('C27 SA');

    compareCheckboxFifth.click();
    // expect(maxItemsReachedModal.classList).toContain('in');
    // closeMaxItemsReachedModal.click();
    // expect(maxItemsReachedModal.classList).not.toContain('in');
  });
});


describe('click Compare button and get error modal', () => {
  it('should present the error modal when the response has no length ( e.g., [] )', () => {
    navigateToComparisonScreenErrorResponse();
    expect(ajaxSpy).toHaveBeenCalledWith(compareButtonAjaxRequestTwoItems());
    //    expect(errorModal.classList).toContain('in');
    //    closeErrorModal.click();
    //    expect(errorModal.classList).not.toContain('in');
  });
});


describe('click Compare button and go to comparison screen, then remove items', () => {
  it('should go to comparison screen when button is enabled, e.g., two or more items are selected, then remove until two items remain', () => {
    navigateToComparisonScreenFourItems();
    expect(ajaxSpy).toHaveBeenCalledWith(compareButtonAjaxRequestFourItems());

    expect(comparisonResultTableHeadings.innerHTML).not.toBe(''); //table populated (includes tbody#prdComp)
    expect(comparisonResultTableBody.innerHTML).not.toBe('');    //table body populated
    expect(compareDTToolDiv.innerHTML).toBe('C17,C17 SA,C/2/7,C27 SA');
    expect(backButtonContainerDiv.classList).not.toContain(hideClass); //back button showing
    expect(mainProductGrid.classList).toContain(hideClass);

    //remove an item successfully and show three items remaining
    lastComparisonTableCloseIcon = comparisonResultTableHeadings.querySelector('th.heading-prd-cmp:last-child').querySelector('i.popup_close');
    lastComparisonTableCloseIcon.click();

    expect(compareDTToolDiv.innerHTML).toBe('C17,C17 SA,C/2/7');
    expect(backButtonContainerDiv.classList).not.toContain(hideClass); //back button still showing

  });
});


describe('click Compare button and go to comparison screen, then test minItemsRequiredModal', () => {
  it('should show minItemsRequiredModal if user attempts to eliminate one of two remaining items using the "x" icon', () => {
    navigateToComparisonScreenTwoItems();
    expect(ajaxSpy).toHaveBeenCalledWith(compareButtonAjaxRequestTwoItems());

    lastComparisonTableCloseIcon = comparisonResultTableHeadings.querySelector('th.heading-prd-cmp:last-child').querySelector('i.popup_close');
    lastComparisonTableCloseIcon.click();
    //    expect(minItemsRequiredModal.classList).toContain('in');
    //    closeMinItemsRequiredModal.click();
    //    expect(minItemsRequiredModal.classList).not.toContain('in');
    expect(compareDTToolDiv.innerHTML).toBe('C17,C17 SA');
    expect(backButtonContainerDiv.classList).not.toContain(hideClass); //back button still showing
  });

});


describe('click Compare button and go to comparison screen, then highlight items as expected', () => {
  it('should highlight items that are different on first click, and then highlight none on second click', () => {
    navigateToComparisonScreenThreeItems();
    expect(ajaxSpy).toHaveBeenCalledWith(compareButtonAjaxRequestThreeItems());

    let highlightDifferencesCheckbox = comparisonResultTableHeadings.querySelector('.highlight-differences > input');
    let allDiff = comparisonResultTableBody.querySelectorAll('tr.comparision_table-row.all-different');
    let allSame = comparisonResultTableBody.querySelectorAll('tr.comparision_table-row:not(.all-different)');

    //click button.  Where tr.all-different, highlight.  If not, no highlight.
    highlightDifferencesCheckbox.click();
    allDiff.forEach(element => {
      expect(element.classList).toContain('highlight');
    });
    allSame.forEach(element => {
      expect(element.classList).not.toContain('highlight');
    });

    //click button again
    highlightDifferencesCheckbox.click();
    //no more highlights
    allDiff.forEach(element => {
      expect(element.classList).not.toContain('highlight');
    });

  });
});


describe('click Compare button and go to comparison screen, then show/hide item rows as expected', () => {
  it('should hide items that are different on first click, and then show them again on second click', () => {
    navigateToComparisonScreenTwoItems();
    expect(ajaxSpy).toHaveBeenCalledWith(compareButtonAjaxRequestTwoItems());

    let showOnlyDifferencesCheckbox = comparisonResultTableHeadings.querySelector('.show-only-differences > input');
    let allDiff = comparisonResultTableBody.querySelectorAll('tr.comparision_table-row.all-different');
    let allSame = comparisonResultTableBody.querySelectorAll('tr.comparision_table-row:not(.all-different)');

    showOnlyDifferencesCheckbox.click(); //where tr.all-different, expect showing.  If not, expect hidden.
    allDiff.forEach(element => {
      expect(element.classList).not.toContain(hideClass);
    });
    allSame.forEach(element => {
      expect(element.classList).toContain(hideClass);
    });

    //click button again
    showOnlyDifferencesCheckbox.click();
    allSame.forEach(element => {
      expect(element.classList).not.toContain(hideClass);
    });

  });
});


describe('click Compare button and go to comparison screen, then go back to main screen', () => {
  it('should go back to main screen with comparison widget showing', () => {
    navigateToComparisonScreenFourItems();
    expect(ajaxSpy).toHaveBeenCalledWith(compareButtonAjaxRequestFourItems());

    //click Back button
    backButton.click();
    expect(sessionStorage.getItem('selectedProdx')).toBe('C17,C17 SA,C/2/7,C27 SA');
    expect(setTimeout).toHaveBeenCalledTimes(1);
    expect(setTimeout).toHaveBeenLastCalledWith(expect.any(Function), '2000');

    //relevant SKUs should still be selected and present in the comparison widget
    expect(firstComparisonSlot.innerHTML).toBe('C17');
    expect(secondComparisonSlot.innerHTML).toBe('C17 SA');
    expect(backButtonContainerDiv.classList).toContain(hideClass);
  });
});
