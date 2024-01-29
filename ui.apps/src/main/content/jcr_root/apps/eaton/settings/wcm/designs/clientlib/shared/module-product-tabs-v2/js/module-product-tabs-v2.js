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
/* eslint-disable no-global-assign */
// noinspection JSConstantReassignment
//-----------------------------------
// Module Products Tab v2 Component
//-----------------------------------
if (typeof require !== 'undefined') {
  var globalConstants = require('../../../global/js/etn-new-global-constants');
  suffixes = globalConstants.suffixes;
}

var productTabsConstants = {
  querySelectorFor: {
    mainContainer: '.product-tabs-v2',
    allTabLabels: '.eaton-product-tabs__buttons label',
    allLinksWithCurrentPath: function allLinksWithCurrentPath() {
      return 'a[href*="' + window.location.pathname.substring(0, window.location.pathname.indexOf('.')) + '"]';
    },
    allActiveFiltersDivsForCurrentPath: function allActiveFiltersDivsForCurrentPath() {
      return 'div[data-value*="' + encodeURIComponent(window.location.pathname.substring(0, window.location.pathname.indexOf('.'))) + '"]';
    },
    allActiveFiltersButtonsForCurrentPath: function allActiveFiltersButtonsForCurrentPath() {
      return 'button[data-filter-value*="' + window.location.pathname.substring(0, window.location.pathname.indexOf('.')) + '"]';
    },
    activeFiltersContainer: '.faceted-navigation__active-filters',
    allTabPanels: '.eaton-product-tabs__tab-panel',
    labelForTabToSelect: function labelForTabToSelect(tabToSelect) {
      return 'label[for=' + tabToSelect + ']';
    },
    panelForTab: function panelForTab(tabId) {
      return '.eaton-product-tabs__tab-panel[data-tab-name="' + tabId + '"]';
    }
  },
  customEvents: {
    ACTIVE_FILTERS_RENDERED: 'active-filters-rendered'
  },
  sessionStorageKeys: {
    VERTICAL_SCROLL_POSITION: 'verticalScrollPosition'
  }
};

var addHashToUrlsInLinksFor = function addHashToUrlsInLinksFor(tabId) {
  var tabPanel = document.querySelector(productTabsConstants.querySelectorFor.panelForTab(tabId));
  if (tabPanel) {
    tabPanel.querySelectorAll(productTabsConstants.querySelectorFor.allLinksWithCurrentPath()).forEach(function (link) {
      if (link.href.indexOf(tabId) === -1) {
        link.href = link.href.replace('(#.*)', '') + '#' + tabId;
      }
    });
    tabPanel.querySelectorAll(productTabsConstants.querySelectorFor.allActiveFiltersDivsForCurrentPath()).forEach(function (div) {
      if (div.dataset.value.indexOf(tabId) === -1) {
        div.dataset.value = div.dataset.value.replace('(#.*)', '') + '#' + tabId;
      }
    });
    tabPanel.querySelectorAll(productTabsConstants.querySelectorFor.allActiveFiltersButtonsForCurrentPath()).forEach(function (button) {
      if (button.dataset.filterValue.indexOf(tabId) === -1) {
        button.dataset.filterValue = button.dataset.filterValue.replace('(#.*)', '') + '#' + tabId;
      }
    });
  }
};

document.addEventListener('DOMContentLoaded', function () {
  if (document.querySelector(productTabsConstants.querySelectorFor.mainContainer)) {
    var tabToSelect = window.location.hash.substring(1);
    var activeFilterContainer = document.querySelector(productTabsConstants.querySelectorFor.activeFiltersContainer);
    if (activeFilterContainer && tabToSelect) {
      activeFilterContainer.addEventListener(productTabsConstants.customEvents.ACTIVE_FILTERS_RENDERED, function (event) {
        addHashToUrlsInLinksFor(tabToSelect);
      });
    }
  }
});

window.addEventListener('load', function () {
  if (document.querySelector(productTabsConstants.querySelectorFor.mainContainer)) {
    window.onscroll = function () {
      sessionStorage.setItem(productTabsConstants.sessionStorageKeys.VERTICAL_SCROLL_POSITION, window.scrollY);
    };

    var allTabs = document.querySelectorAll(productTabsConstants.querySelectorFor.allTabLabels);
    allTabs.forEach(function (tabLabel) {
      var tabId = tabLabel.getAttribute('for');

      // All tabs initially hidden.  If content is authored within a tab's corresponding panel (.aemGrid), show the tab.
      var tabPanel = document.querySelector(productTabsConstants.querySelectorFor.panelForTab(tabId));
      var tabPanelParsys = tabPanel.querySelector('.aem-Grid');
      if (typeof tabPanelParsys !== 'undefined' && tabPanelParsys !== null) {
        if (tabPanelParsys.children.length > 0) {
          tabLabel.classList.remove('hide');
        }
      }

      tabLabel.onclick = function () {
        var _iteratorNormalCompletion = true;
        var _didIteratorError = false;
        var _iteratorError = undefined;

        try {
          for (var _iterator = allTabs[Symbol.iterator](), _step; !(_iteratorNormalCompletion = (_step = _iterator.next()).done); _iteratorNormalCompletion = true) {
            var tab = _step.value;

            tab.classList.remove('tab-active');
          }
        } catch (err) {
          _didIteratorError = true;
          _iteratorError = err;
        } finally {
          try {
            if (!_iteratorNormalCompletion && _iterator.return) {
              _iterator.return();
            }
          } finally {
            if (_didIteratorError) {
              throw _iteratorError;
            }
          }
        }

        tabLabel.classList.add('tab-active');
        window.location.hash = tabId;
        var scrollDiv = document.querySelector('.eaton-product-tabs').offsetTop - 150;
        addHashToUrlsInLinksFor(tabId);
        window.scrollTo({ top: scrollDiv, behavior: 'smooth' });
      };
    });
    if (window.location.hash === null || window.location.hash === '') {
      sessionStorage.setItem(productTabsConstants.sessionStorageKeys.VERTICAL_SCROLL_POSITION, 0);
    }
    var tabToSelect = window.location.hash.substring(1);
    if (tabToSelect) {
      addHashToUrlsInLinksFor(tabToSelect);
      var tabToSelectElement = document.querySelector(productTabsConstants.querySelectorFor.labelForTabToSelect(tabToSelect));
      tabToSelectElement.click();
    }
    window.setTimeout(function () {
      window.scroll({
        top: Number(sessionStorage.getItem(productTabsConstants.sessionStorageKeys.VERTICAL_SCROLL_POSITION)),
        behavior: 'instant'
      });
    }, 500);
  }
});