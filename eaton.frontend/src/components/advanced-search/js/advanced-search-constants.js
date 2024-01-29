/* eslint-disable no-undef */
// noinspection JSConstantReassignment

if (typeof require !== 'undefined') {
  const globalConstants = require('../../../global/js/etn-new-global-constants');
  viewTypes = globalConstants.viewTypes;
  htmlTags = globalConstants.htmlTags;
  literals = globalConstants.literals;
}

const elementIdOf = {
  secureAttributes: 'eaton-secure_attributes',
  advancedSearchBox: 'adv-site-search-box',
  listButton: viewTypes.list + literals.BUTTON_SHORT,
  gridButton: viewTypes.grid + literals.BUTTON_SHORT,
  mustacheTemplate: {
    forGrid: 'mustache-advanced-search-result',
    forList: 'mustache-advanced-search-list-result',
    forFilter: 'mustache-advanced-search-filter',
    forListPartial: 'mustache-list-result-partial'
  },
  finalResult: 'final-result',
  searchResultsFilter: 'search-results__filters',
  noResult: 'noresult',
  sortByDropdownButton: 'dSortFacets',
  dateError: 'date-error',
  selectedFilterContainer: 'selectedFilter',
  trackDownloadDialog: 'track-exceed',
  mobileFilterButton: 'secure-filter',
  selectedFilesDrawerBox: 'selected-files-drawer-box'
};
const elementClasses = {
  active: 'active',
  hide: 'hide',
  loaderActive: 'loader-active',
  secureIcon: 'icon icon-secure-lock secure__eaton',
  disabledButton: 'disabledbutton',
  more: 'more',
  hideClearFilters: 'filterNone',
  selectedSortOption: 'faceted-navigation-header__sort-options--selected',
  facetsOpen: 'facets-open',
  show: 'show',
  checked: 'checked'
};
const querySelectorFor = {
  bulkDownloadActivated: '.bde-activated',
  advancedSearchContainer: '.eaton-advanced-search',
  advancedSearchResults: '.advanced-search__results',
  clearFilters: '.faceted-navigation-header__action-link--clear-filters',
  image: htmlTags.image,
  filterDisplay: '.advanced-search__filter',
  filterInnerSpan: '.faceted-navigation__list .inner',
  loadMore: '.load__more',
  allSearchResultLinks: '.advanced-search__title a, .list-view a',
  allCheckedToDownload: 'input[name="toDownload"]:checked',
  allToDownload: 'input[name="toDownload"]',
  filterGroups: '.faceted-navigation__filter-container ul',
  listItem: htmlTags.listItem,
  filterViewMore: '.faceted-navigation__view-more-values',
  filterViewLess: '.faceted-navigation__view-less-values',
  gridResults: '.advanced-search__result',
  listResults: '.list-view',
  searchIcon: '.adv-icon-search',
  facetFilters: '.filter-selected',
  selectedSortOption: '.' + elementClasses.selectedSortOption,
  selectedFilterButtons: '#selectedFilter button',
  startDate: '.start-date',
  endDate: '.end-date',
  advancedSearchLoader: '.adv-search__loader',
  sortOptions: '.select-sorting',
  sortByDropdownLabel: '.faceted-navigation-header__sort-link',
  applyDate: '.apply-date',
  resetDate: '.reset-date',
  dateInput: '.faceted-navigation__list__date-input',
  mobileCollapsable: '.close-accordion-mobile button.faceted-navigation__header.button--reset',
  okayTrack: '.okay-track',
  mobileFilterCloseIcon: '.icon-close-filter',
  more: '.' + elementClasses.more,
  autoSuggestionItems: '.li-div',
  autoSuggestionInput: '.faceted-navigation__facet-group .auto-search',
  advancedSearchResultsBox: '.adv-results-container',
  noResultContainer: '.search-results__no-result',
  menuContainer: '.faceted-navigation-header',
  filterContainer: '.faceted-navigation__filters',
  filterContainerWithoutDates: '.faceted-navigation__filter-container',
  noResultsAfterTextSearch: '.no-results-after-text-search'
};
const searchPrefixes = {
  SEARCH_TERM: '.searchTerm$',
  SORT_BY: '.SortBy$',
  FACET: '.Facets$',
  START_DATE: '.startDate$',
  END_DATE: '.endDate$',
  LOAD_MORE: '.loadMoreOffset$'
};
const customEvents = {
  VIEW_CHANGED: new CustomEvent('viewChange'),
  RESPONSE_RENDERED: new CustomEvent('responseRendered'),
  VIEW_TYPE_CLICKED: new CustomEvent('viewTypeSelectionClicked'),
  CLEAR_FILTERS_CLICKED: new CustomEvent('clearFiltersClicked'),
  RESET_DATES_CLICKED: new CustomEvent('resetDatesClicked'),
  SEARCH_EVENT: new CustomEvent('searchEvent'),

  filterSelected: (filter, toRemove) => { let customEvent = new CustomEvent('filterSelected'); customEvent.filter = filter; customEvent.toRemove = toRemove; return customEvent;},
  filterDeselected: (facetId) => { let customEvent = new CustomEvent('filterDeselected'); customEvent.facetId = facetId; return customEvent;},
  newTabEventWithArgument: (argument) => {
    return new CustomEvent('triggerNewTabEvent', argument);
  },
  applyDateClicked: (startDate, endDate) => { let customEvent = new CustomEvent('applyDateClicked'); customEvent.startDate = startDate; customEvent.endDate = endDate; return customEvent;},
  sortOptionSelected: (sortOption) => { let customEvent = new CustomEvent('sortOptionSelected'); customEvent.sortOption = sortOption; return customEvent;}
};
const sessionStorageKeys = {
  selectedFiles: 'selectedFiles'
};

if (typeof module !== 'undefined' && typeof module.exports !== 'undefined') {
  module.exports = {sessionStorageKeys, customEvents, searchPrefixes, querySelectorFor, elementClasses, elementIdOf};
}
