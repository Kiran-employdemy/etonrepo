/* eslint-disable no-undef */
/* eslint-disable no-global-assign */
// noinspection JSConstantReassignment
//-----------------------------------
// Advanced Search Component
//-----------------------------------
let searchResult;
let uniqueSearchResultList = [];

const requirePresent = () => typeof require !== 'undefined';

if (requirePresent()) {
  const globalConstants = require('../../../global/js/etn-new-global-constants');
  const constants = require('./advanced-search-constants');
  literals = globalConstants.literals;
  $ = require(literals.JQUERY);
  eventListeners = globalConstants.eventListeners;
  elementAttributes = globalConstants.elementAttributes;
  booleans = globalConstants.booleans;
  viewTypes = globalConstants.viewTypes;
  htmlTags = globalConstants.htmlTags;
  sessionStorageKeys = constants.sessionStorageKeys;
  customEvents = constants.customEvents;
  querySelectorFor = constants.querySelectorFor;
  elementClasses = constants.elementClasses;
  elementIdOf = constants.elementIdOf;
}

const externalCallWrapperPresent = () => typeof externalCallWrapper !== literals.UNDEFINED && typeof externalCallWrapper !== literals.UNDEFINED;

class AdvancedSearch {

  constructor() {
    sessionStorage.removeItem(sessionStorageKeys.selectedFiles);
    this.bulkDownloadContainer = document.querySelector(querySelectorFor.bulkDownloadActivated);
    this.advancedSearchContainer = document.querySelector(querySelectorFor.advancedSearchContainer);
    this.resultsContainer = document.querySelector(querySelectorFor.advancedSearchResults);
    this.advancedSearchDataset = this.advancedSearchContainer.dataset;
    this.viewType = this.advancedSearchDataset.attributeViewType ? this.advancedSearchDataset.attributeViewType : null;
    this.searchBoxInput = document.getElementById(elementIdOf.advancedSearchBox);
    this.noResult = document.getElementById(elementIdOf.noResult);
    this.gridTemplate = document.getElementById(elementIdOf.mustacheTemplate.forGrid);
    this.listTemplate = document.getElementById(elementIdOf.mustacheTemplate.forList);
    this.listItemTemplate = document.getElementById(elementIdOf.mustacheTemplate.forListPartial);
    this.entireSearchList = null;
    this.initializeComponents();
  }

  initializeComponents() {
    if (requirePresent()) {
      this.viewTypeComponent = this.initializeViewTypeComponent();
      this.urlBuilder = this.initializeUrlBuilder();
      this.menuComponent = this.initializeMenuComponent();
      this.filterComponent = this.initializeFilterComponent();
      this.searchComponent = this.initializeSearchComponent();
    } else {
      this.viewTypeComponent = new ViewTypeComponent(this.viewType, this.advancedSearchContainer);
      this.urlBuilder = new AdvancedSearchUrlBuilder(this.advancedSearchDataset.resource, this.advancedSearchContainer.dataset.authenticated === literals.TRUE);
      this.menuComponent = new MenuComponent(this.advancedSearchContainer);
      this.filterComponent = new FilterComponent(this.advancedSearchContainer, this.menuComponent.removeFilterFromSelectedFilters, this.menuComponent.addSelectedFilter);
      this.searchComponent = new SearchComponent(this.advancedSearchContainer, this.searchBoxInput);
    }
  }

  initializeViewTypeComponent() {
    const {createViewTypeComponent} = require('./view-type-component');
    return createViewTypeComponent(this.viewType, this.advancedSearchContainer);
  }
  initializeUrlBuilder() {
    const {createUrlBuilder} = require('./advanced-search-url-builder');
    return createUrlBuilder(this.advancedSearchDataset.resource, this.advancedSearchContainer.dataset.authenticated === literals.TRUE);
  }
  initializeFilterComponent() {
    const {createFilterComponent} = require('./filter-component');
    return createFilterComponent(this.advancedSearchContainer, this.menuComponent.removeFilterFromSelectedFilters, this.menuComponent.addSelectedFilter);
  }
  initializeMenuComponent() {
    const {createMenuComponent} = require('./menu-component');
    return createMenuComponent(this.advancedSearchContainer);
  }
  initializeSearchComponent() {
    const {createSearchComponent} = require('./search-component');
    return createSearchComponent(this.advancedSearchContainer, this.searchBoxInput);
  }

  init() {
    this.urlBuilder.withSortBy(this.advancedSearchDataset.defaultSortOption);
    this.initializeForView().then(() => {
      this.filterComponent.renderFilters(searchResult);
      this.addEventListener();
      this.initializeRightClickEventsToTrack();
      uniqueSearchResultList = searchResult.siteSearchResults;
      this.initializeBulkDownloadClickListener();
    });
  }


  async initializeForView() {
    this.viewTypeComponent.initializeViewTypeButton();
    this.getResult().then(() => {
      this.renderResultUsingMustacheTemplate();
    });
  }

  /* eslint-disable no-undef */
  async getResult() {
    let urlToCall = this.urlBuilder.constructUrl();
    if (!externalCallWrapperPresent() && requirePresent()) {
      const {externalCallWrapper} = require('../../../global/js/external-call-wrapper');
      externalCallWrapper.makeCall(urlToCall).then((data) => {this.handleData(data);});
    } else {
      externalCallWrapper.makeCall(urlToCall).then((data) => {this.handleData(data);});
    }
  }

  handleData(data) {
    return new Promise(() => {
      searchResult = typeof data === 'string' ? JSON.parse(data) : data;
      this.addI18nToResponseObject();
      this.hideOrUnhideNoResult();
      this.showOrHideLoadMore(searchResult);
      let loader = document.querySelector(querySelectorFor.advancedSearchLoader);
      loader.classList.remove(elementClasses.loaderActive);
    });
  }

  hideOrUnhideNoResult() {
    let advResultsContainer = document.querySelector(querySelectorFor.advancedSearchResultsBox);
    let noResultsContainer = document.querySelector(querySelectorFor.noResultContainer);
    let noResultsAFterTextSearch = noResultsContainer.querySelector(querySelectorFor.noResultsAfterTextSearch);
    if (searchResult.totalCount === 0) {
      this.filterComponent.hideFilters();
      advResultsContainer.classList.add(elementClasses.hide);
      this.resultsContainer.classList.add(elementClasses.hide);
      noResultsContainer.classList.remove(elementClasses.hide);
      if (this.searchBoxInput.value !== '') {
        this.noResult.innerHTML = this.searchBoxInput.value;
        noResultsAFterTextSearch.classList.remove(elementClasses.hide);
      } else {
        noResultsAFterTextSearch.classList.add(elementClasses.hide);
      }
    } else {
      this.filterComponent.showFilters();
      advResultsContainer.classList.remove(elementClasses.hide);
      this.resultsContainer.classList.remove(elementClasses.hide);
      noResultsContainer.classList.add(elementClasses.hide);
    }
  }

  addI18nToResponseObject() {
    searchResult.i18n = {
      date: this.advancedSearchContainer.dataset.dateLabel,
      size: this.advancedSearchContainer.dataset.sizeLabel,
      language: this.advancedSearchContainer.dataset.languageLabel,
      tooltiptext: this.advancedSearchContainer.dataset.bulkdownloadTooltipText
    };
  }

  renderResultUsingMustacheTemplate() {
    let resultsToWorkWith = this.entireSearchList ? this.entireSearchList : searchResult;
    if (this.resultsContainer) {
      if (this.isViewTypeGridView()) {
        this.resultsContainer.innerHTML = this.renderForGrid(resultsToWorkWith);
      } else {
        if (this.entireSearchList) {
          resultsToWorkWith.i18n = searchResult.i18n;
        }
        this.resultsContainer.innerHTML = this.renderForList(resultsToWorkWith);
      }
    }
    let finalResult = document.getElementById(elementIdOf.finalResult);
    if (finalResult) {
      finalResult.innerHTML = searchResult.totalCount;
      this.triggerEventOnBulkDownloadContainerIfPresent(customEvents.RESPONSE_RENDERED);
    }
  }

  renderForList(resultsToWorkWith) {
    return window.Mustache.render(this.listTemplate.innerHTML, resultsToWorkWith, {
      listItems: this.listItemTemplate.innerHTML
    });
  }

  renderOnlPartial(resultsToWorkWith) {
    return window.Mustache.render(this.listItemTemplate.innerHTML, resultsToWorkWith);
  }

  // Handles LoadMore Hide/Show
  showOrHideLoadMore(searchResult) {
    let loadMoreButton = document.querySelector(querySelectorFor.loadMore);
    if (searchResult.loadMoreOffset) {
      this.urlBuilder.withLoadMore(searchResult.loadMoreOffset);
      loadMoreButton.classList.remove(elementClasses.hide);
    } else if (searchResult.siteSearchResults && searchResult.siteSearchResults.length > 0 && !searchResult.loadMoreOffset) {
      this.urlBuilder.withLoadMore('');
      loadMoreButton.classList.add(elementClasses.hide);
    } else {
      loadMoreButton.classList.add(elementClasses.hide);
    }
  }

  initializeRightClickEventsToTrack() {
    let self = this;
    document.querySelectorAll(querySelectorFor.allSearchResultLinks)
      .forEach(link => link.addEventListener(eventListeners.MOUSE_DOWN, (event) => {
        let trackDownload = event.target.dataset.trackDownload;
        if (booleans.isTrue(trackDownload)) {
          let hrefAttribute = event.target.getAttribute(elementAttributes.reference);
          let evt = customEvents.newTabEventWithArgument({link: hrefAttribute});
          evt.assetLink = hrefAttribute;
          event.stopPropagation();
          if (event.button === 0) {
            self.triggerEventOnBulkDownloadContainerIfPresent(evt);
            self.showTrackDownloadWarning();
          }
        }
      }));
  }

  async ajaxViewDisplay() {
    this.viewType = this.advancedSearchDataset.attributeViewType;
    this.viewTypeComponent.initializeViewTypeButton();
    this.entireSearchList = null;
    this.getResult().then(() => {
      this.appendSearchResultWithPreviousSelection();
      this.renderResultUsingMustacheTemplate();
      uniqueSearchResultList = [];
      uniqueSearchResultList = searchResult.siteSearchResults;
      this.filterComponent.renderFilters(searchResult);
      this.initializeRightClickEventsToTrack();
      this.initializeBulkDownloadClickListener();
    });

  }

  storePreviousSelection() {
    let allSelectedFiles = [];
    let allCheckedToDownload = document.querySelectorAll(querySelectorFor.allCheckedToDownload);
    if (allCheckedToDownload.length === 0 && this.selectedFilesNotEmpty()) {
      return;
    }
    allCheckedToDownload.forEach(input => {
      if (!input.classList.contains(elementClasses.disabledButton)) {
        uniqueSearchResultList.forEach(searchResult => {
          // check id and if matched pushed it to selected queue.
          if (input.id === searchResult.id) {
            allSelectedFiles.push(searchResult);
          }
        });
      }
    });
    sessionStorage.setItem(sessionStorageKeys.selectedFiles, JSON.stringify(allSelectedFiles));
  }

  selectedFilesNotEmpty() {
    let selectedFilesFromStorage = sessionStorage.getItem(sessionStorageKeys.selectedFiles);
    return selectedFilesFromStorage && JSON.parse(selectedFilesFromStorage).length > 0;
  }

  appendSearchResultWithPreviousSelection() {
    if (searchResult.totalCount === 0) {
      return;
    }
    let siteSearchResults = searchResult.siteSearchResults;
    let selectedFilesString = sessionStorage.getItem(sessionStorageKeys.selectedFiles);
    if (selectedFilesString !== null) {
      let selectedFiles = JSON.parse(selectedFilesString);
      selectedFiles.forEach(selectedFile => {
        if (!siteSearchResults.find(searchResult => {
          return searchResult.id === selectedFile.id;
        })) {
          searchResult.siteSearchResults.unshift(selectedFile);
        }
      });
    }

  }

  populateCheckboxes() {
    let selectedFilesString = sessionStorage.getItem(sessionStorageKeys.selectedFiles);
    if (selectedFilesString !== null) {
      let selectdFiles = JSON.parse(selectedFilesString);
      selectdFiles.forEach(file => {
        let fileInList = document.getElementById(file.id);
        if (fileInList && !fileInList.checked) {
          fileInList.click();
        }
      });
    }
  }

  searchBarResult() {
    let sortby = this.menuComponent.getSelectedSortBy();
    let startDate = this.filterComponent.getStartDate();
    let endDate = this.filterComponent.getEndDate();
    let searchterm = this.searchComponent.getSearchValue();
    // Character "." is replaced with "@" so that leading dot issue is handel in backend.
    if (searchterm[0] === '.') {
      searchterm = '@' + searchterm.slice(1);
    }
    this.urlBuilder.withSortBy(sortby).withStartDate(startDate).withEndDate(endDate).withSearchTerm(searchterm);
    this.callSearchAPIAndUpdateView();
  }

  resultsFound() {
    let resultList = document.querySelectorAll(this.isViewTypeGridView() ? querySelectorFor.gridResults : querySelectorFor.listResults);
    return resultList.length > 0;
  }

  /**
   * Funtion
   */
  moveSelectedFilesToTopOfList() {
    let checkedToDownload = document.querySelectorAll(querySelectorFor.allCheckedToDownload);
    let whereToInsert = this.isViewTypeGridView() ? this.resultsContainer : this.resultsContainer.querySelector(htmlTags.tableBody);
    checkedToDownload.forEach((inputToDownload) => {
      let parentNodeToInsert = this.isViewTypeGridView() ? inputToDownload.parentNode
        : inputToDownload.parentNode.parentNode;
      whereToInsert.insertBefore(parentNodeToInsert, whereToInsert.firstChild);
    });
    this.unhideOrHideClearFilters();
  }

  isViewTypeGridView() {
    return this.viewTypeComponent.viewType === viewTypes.grid;
  }

  callSearchAPIAndUpdateView() {
    this.urlBuilder.withLoadMore('');
    this.storePreviousSelection();
    this.ajaxViewDisplay().then(() => {
      this.populateCheckboxes();
      this.moveSelectedFilesToTopOfList();
    });
  }

// Click event to handel the functionality

  addEventListener() {
    let self = this;

    this.advancedSearchContainer.addEventListener('sortOptionSelected', (event) => {
      self.urlBuilder.withStartDate(self.filterComponent.getStartDate()).withEndDate(self.filterComponent.getEndDate()).withSortBy(event.sortOption);
      self.callSearchAPIAndUpdateView();
    });

    this.advancedSearchContainer.addEventListener('viewTypeSelectionClicked', () => {
      self.advancedSearchContainer.dataset.attributeViewType = self.viewTypeComponent.viewType;
      self.triggerEventOnBulkDownloadContainerIfPresent(customEvents.VIEW_CHANGED);
      self.appendSearchResultWithPreviousSelection();
      self.renderResultUsingMustacheTemplate();
      self.populateCheckboxes();
      self.moveSelectedFilesToTopOfList();
      self.initializeBulkDownloadClickListener();
    });

    this.advancedSearchContainer.addEventListener('filterSelected', (event) => {
      self.urlBuilder.withSortBy(self.menuComponent.getSelectedSortBy()).withStartDate(self.filterComponent.getStartDate()).withEndDate(self.filterComponent.getEndDate());
      let filter = event.filter;
      if (event.toRemove !== '') {
        self.urlBuilder.removeFacet(event.toRemove);
      }
      self.urlBuilder.addFacet(filter.value);
      self.callSearchAPIAndUpdateView();
      self.menuComponent.unhideClearFilters();
      if (!self.resultsFound()) {
        self.noResult.innerHTML = filter.dataset.title;
      }
    });

    this.advancedSearchContainer.addEventListener('filterDeselected', (event) => {
      let facetId = event.facetId;
      self.urlBuilder.removeFacet(facetId).withSortBy(self.menuComponent.getSelectedSortBy()).withStartDate(self.filterComponent.getStartDate()).withEndDate(self.filterComponent.getEndDate());
      self.callSearchAPIAndUpdateView();
      self.unhideOrHideClearFilters();
    });

    this.advancedSearchContainer.addEventListener('clearFiltersClicked', () => {
      self.searchBoxInput.value = '';
      self.filterComponent.clearDateFields();
      self.filterComponent.hideDateError();
      self.urlBuilder.resetExceptSortBy();
      self.callSearchAPIAndUpdateView();
    });

    this.advancedSearchContainer.addEventListener('applyDateClicked', (event) => {
      let startDate = event.startDate;
      let endDate = event.endDate;
      let sortby = self.menuComponent.getSelectedSortBy();
      self.urlBuilder.withSortBy(sortby).withStartDate(startDate).withEndDate(endDate);
      self.menuComponent.unhideClearFilters();
      self.callSearchAPIAndUpdateView();
    });

    this.advancedSearchContainer.addEventListener('resetDatesClicked', () => {
      self.urlBuilder.withSortBy(self.menuComponent.getSelectedSortBy()).withStartDate('').withEndDate('');
      self.callSearchAPIAndUpdateView();
      self.unhideOrHideClearFilters();
    });

    this.advancedSearchContainer.addEventListener('searchEvent', () => {
      self.searchBarResult();
      if (!self.resultsFound()) {
        self.noResult.innerHTML = self.searchComponent.getSearchValue();
      }
      self.menuComponent.unhideClearFilters();
    });

    document.querySelector(querySelectorFor.loadMore).onclick = (event) => {
      self.entireSearchList = {siteSearchResults: []};
      uniqueSearchResultList.forEach(uniqueSearchResult => { self.entireSearchList.siteSearchResults.push(uniqueSearchResult); });
      self.getResult().then(() => {
        self.loadMoreCallBack(self);
        uniqueSearchResultList = [];
        uniqueSearchResultList = [...new Set(self.entireSearchList.siteSearchResults)];
        self.triggerEventOnBulkDownloadContainerIfPresent(customEvents.RESPONSE_RENDERED);
        if (searchResult.siteSearchResults && searchResult.siteSearchResults.length > 0 && !searchResult.loadMoreOffset) {
          event.target.classList.add(elementClasses.hide);
        }
        self.moveSelectedFilesToTopOfList();
        self.initializeBulkDownloadClickListener();
      });
    };
  }

  unhideOrHideClearFilters() {
    let count = this.menuComponent.selectedFilterCount();
    if (count === 0 && this.searchBoxInput.value === '' && (this.filterComponent.getStartDate() === '' && this.filterComponent.getEndDate() === '')) {
      this.menuComponent.hideClearFilters();
    } else {
      this.menuComponent.unhideClearFilters();
    }
  }

  loadMoreCallBack(advancedSearch) {
    searchResult.siteSearchResults.forEach(result => {
      advancedSearch.entireSearchList.siteSearchResults.push(result);
    });
    if (advancedSearch.isViewTypeGridView() || !advancedSearch.viewType) {
      advancedSearch.resultsContainer.insertAdjacentHTML('beforeend', advancedSearch.renderForGrid(searchResult));
    } else {
      advancedSearch.resultsContainer.querySelector(htmlTags.tableBody).insertAdjacentHTML('beforeend', advancedSearch.renderOnlPartial(searchResult));
    }
  }

  renderForGrid(resultsToWorkWith) {
    return window.Mustache.render(this.gridTemplate.innerHTML, resultsToWorkWith);
  }

  triggerEventOnBulkDownloadContainerIfPresent(event) {
    if (this.bulkDownloadContainer) {
      this.bulkDownloadContainer.dispatchEvent(event);
    }
  }

  // To show the list result using the mustache.js
  showTrackDownloadWarning() {
    let trackDownloadWarningDialog = document.getElementById(elementIdOf.trackDownloadDialog);
    trackDownloadWarningDialog.classList.remove(elementClasses.hide);
    trackDownloadWarningDialog.querySelector(querySelectorFor.okayTrack).onclick = () => {
      trackDownloadWarningDialog.classList.add(elementClasses.hide);
    };
  }
  initializeBulkDownloadClickListener() {
    let self = this;
    document.querySelectorAll(querySelectorFor.allToDownload).forEach(toDownload => {
      toDownload.onclick = (event) => {
        let clickedElement = event.target;
        let checkedToDownload = document.querySelectorAll(querySelectorFor.allCheckedToDownload);
        let tempCount = checkedToDownload.length;
        let actualLimitSet = parseInt(document.getElementById(elementIdOf.selectedFilesDrawerBox).dataset.downloadFileCountLimit);
        // For size limit, check this condition: "checkSize > maxZipLimit" instaed of 'actualLimitSet < tempCount' in folloing if.
        if (actualLimitSet < tempCount) {
          return false;
        }
        if (clickedElement.checked) {
          self.addFileWithIdToSelectedFiles(clickedElement.id);
        } else if (!clickedElement.checked) {
          self.removeFileWithIdFromSelectedFiles(clickedElement.id);
        }
        addRemovefileForBDE();
      };
    });
  }

  addFileWithIdToSelectedFiles(id) {
    let allSelectedFiles = JSON.parse(sessionStorage.getItem(sessionStorageKeys.selectedFiles)) || [];
    uniqueSearchResultList.forEach(searchResult => {
      // check id and if matched pushed it to selected queue.
      if (id === searchResult.id && (allSelectedFiles.length === 0 || !allSelectedFiles.find(file => file.id === searchResult.id))) {
        allSelectedFiles.push(searchResult);
      }
    });
    sessionStorage.setItem(sessionStorageKeys.selectedFiles, JSON.stringify(allSelectedFiles));
  }
  removeFileWithIdFromSelectedFiles(id) {
    let allSelectedFiles = JSON.parse(sessionStorage.getItem(sessionStorageKeys.selectedFiles));
    let newSelectedFiles = [];
    allSelectedFiles.forEach(selectedFile => {
      // check id and if matched pushed it to selected queue.
      if (id !== selectedFile.id) {
        newSelectedFiles.push(selectedFile);
      }
    });
    sessionStorage.setItem(sessionStorageKeys.selectedFiles, JSON.stringify(newSelectedFiles));
  }
}

/* Custom js for Dashboard document js */
document.addEventListener('DOMContentLoaded', () => {
  const advanceSearchContainer = document.getElementById('advance__search-result-doc');
  if (advanceSearchContainer) {
    new AdvancedSearch().init();
  }
  let dateInput = $(querySelectorFor.dateInput);
  if (typeof dateInput.datepicker !== 'undefined') {
    dateInput.datepicker({
      dateFormat: 'dd-mm-yy',
      maxDate: 0,
      changeYear: true
    });
  }
});
/* Custom js for Dashboard document js*/


if (typeof module !== 'undefined' && typeof module.exports !== 'undefined') {
  module.exports = {AdvancedSearch};
}
