/* eslint-disable no-undef */
/* eslint-disable no-global-assign */
// noinspection JSConstantReassignment
//-----------------------------------
// Module Products Tab v2 Component
//-----------------------------------
if (typeof require !== 'undefined') {
  const globalConstants = require('../../../global/js/etn-new-global-constants');
  suffixes = globalConstants.suffixes;
}

const productTabsConstants = {
  querySelectorFor: {
    mainContainer: '.product-tabs-v2',
    allTabLabels: '.eaton-product-tabs__buttons label',
    allLinksWithCurrentPath: () => { return `a[href*="${ window.location.pathname.substring(0, window.location.pathname.indexOf('.')) }"]`; },
    allActiveFiltersDivsForCurrentPath: () => { return `div[data-value*="${ encodeURIComponent(window.location.pathname.substring(0, window.location.pathname.indexOf('.'))) }"]`; },
    allActiveFiltersButtonsForCurrentPath: () => { return `button[data-filter-value*="${ window.location.pathname.substring(0, window.location.pathname.indexOf('.')) }"]`; },
    activeFiltersContainer: '.faceted-navigation__active-filters',
    allTabPanels: '.eaton-product-tabs__tab-panel',
    labelForTabToSelect: (tabToSelect) => {
      return `label[for=${ tabToSelect }]`;
    },
    panelForTab: (tabId) => {
      return `.eaton-product-tabs__tab-panel[data-tab-name="${ tabId }"]`;
    }
  },
  customEvents: {
    ACTIVE_FILTERS_RENDERED: 'active-filters-rendered'
  },
  sessionStorageKeys: {
    VERTICAL_SCROLL_POSITION: 'verticalScrollPosition'
  }
};

const addHashToUrlsInLinksFor = tabId => {
  let tabPanel = document.querySelector(productTabsConstants.querySelectorFor.panelForTab(tabId));
  if (tabPanel) {
    tabPanel.querySelectorAll(productTabsConstants.querySelectorFor.allLinksWithCurrentPath()).forEach((link) => {
      if (link.href.indexOf(tabId) === -1) {
        link.href = link.href.replace('(#.*)', '') + '#' + tabId;
      }
    });
    tabPanel.querySelectorAll(productTabsConstants.querySelectorFor.allActiveFiltersDivsForCurrentPath()).forEach((div) => {
      if (div.dataset.value.indexOf(tabId) === -1) {
        div.dataset.value = div.dataset.value.replace('(#.*)', '') + '#' + tabId;
      }
    });
    tabPanel.querySelectorAll(productTabsConstants.querySelectorFor.allActiveFiltersButtonsForCurrentPath()).forEach((button) => {
      if (button.dataset.filterValue.indexOf(tabId) === -1) {
        button.dataset.filterValue = button.dataset.filterValue.replace('(#.*)', '') + '#' + tabId;
      }
    });
  }
};

document.addEventListener('DOMContentLoaded', () => {
  if (document.querySelector(productTabsConstants.querySelectorFor.mainContainer)) {
    let tabToSelect = window.location.hash.substring(1);
    let activeFilterContainer = document.querySelector(productTabsConstants.querySelectorFor.activeFiltersContainer);
    if (activeFilterContainer && tabToSelect) {
      activeFilterContainer.addEventListener(productTabsConstants.customEvents.ACTIVE_FILTERS_RENDERED, (event) => {
        addHashToUrlsInLinksFor(tabToSelect);
      });
    }
  }
});

window.addEventListener('load', () => {
  if (document.querySelector(productTabsConstants.querySelectorFor.mainContainer)) {
    window.onscroll = () => {
      sessionStorage.setItem(productTabsConstants.sessionStorageKeys.VERTICAL_SCROLL_POSITION, window.scrollY);
    };

    const allTabs = document.querySelectorAll(productTabsConstants.querySelectorFor.allTabLabels);
    allTabs.forEach((tabLabel) => {
      let tabId = tabLabel.getAttribute('for');

      // All tabs initially hidden.  If content is authored within a tab's corresponding panel (.aemGrid), show the tab.
      let tabPanel = document.querySelector(productTabsConstants.querySelectorFor.panelForTab(tabId));
      let tabPanelParsys = tabPanel.querySelector('.aem-Grid');
      if (typeof tabPanelParsys !== 'undefined' && tabPanelParsys !== null) {
        if (tabPanelParsys.children.length > 0) {
          tabLabel.classList.remove('hide');
        }
      }

      tabLabel.onclick = () => {
        for (const tab of allTabs) {
          tab.classList.remove('tab-active');
        }
        tabLabel.classList.add('tab-active');
        window.location.hash = tabId;
        let scrollDiv = document.querySelector('.eaton-product-tabs').offsetTop - 150;
        addHashToUrlsInLinksFor(tabId);
        window.scrollTo({ top: scrollDiv, behavior: 'smooth'});
      };
    });
    if (window.location.hash === null || window.location.hash === '') {
      sessionStorage.setItem(productTabsConstants.sessionStorageKeys.VERTICAL_SCROLL_POSITION, 0);
    }
    let tabToSelect = window.location.hash.substring(1);
    if (tabToSelect) {
      addHashToUrlsInLinksFor(tabToSelect);
      let tabToSelectElement = document.querySelector(productTabsConstants.querySelectorFor.labelForTabToSelect(tabToSelect));
      tabToSelectElement.click();
    }
    window.setTimeout(() => {
      window.scroll({
        top: Number(sessionStorage.getItem(productTabsConstants.sessionStorageKeys.VERTICAL_SCROLL_POSITION)),
        behavior: 'instant'
      }
      );
    }, 500);
  }
});

